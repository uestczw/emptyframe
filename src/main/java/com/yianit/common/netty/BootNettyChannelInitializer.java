package com.yianit.common.netty;

import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.yianit.common.netty.coder.YianCoder;
import com.yianit.common.netty.decoder.YianMsgDecoder;
import com.yianit.common.netty.encoder.YianMsgEncoder;
import com.yianit.common.util.JedisPoolUtil;
import com.yianit.config.SpringBaseConfig;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.timeout.ReadTimeoutHandler;

@Component
public class BootNettyChannelInitializer<SocketChannel> extends ChannelInitializer<Channel> {
	private final Logger LOG = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private SpringBaseConfig springBaseConfig;
	@Autowired
	@Qualifier("jedisPoolUtil")
	private JedisPoolUtil jedisPoolUtil;
	@Autowired
	@Qualifier("yianRabbitTemplate")
	protected RabbitTemplate rabbitTemplate;
	@Autowired
	@Qualifier("yianRabbitAdmin")
	private RabbitAdmin rabbitAdmin;
	@Autowired
	protected RestTemplate restTemplate;
	@Autowired
	BootNettyChannelInboundHandlerAdapter bootNettyChannelInboundHandlerAdapter;

	@Override
	protected void initChannel(Channel ch) throws Exception {
		InetSocketAddress ipSocket = (InetSocketAddress) ch.localAddress();
		int port = ipSocket.getPort();
		if (!NettyServer.STATUS_CACHE.containsKey(port)) {
			LOG.info("正在关闭端口" + port + "服务，不接受新的连接");
			ch.close();
			return;
		}
		YianCoder coder = YianBenCache.getDcoer("yianrule" + port);
		if (coder == null) {
			LOG.error("端口" + port + "未配置编解码规则");
		}
		// RabbitTemplate amqpTemplate = new RabbitTemplate(connectionFactory);
		coder.setServerIp(springBaseConfig.getIp());
		coder.setJedisPoolUtil(jedisPoolUtil);
		coder.setRabbitTemplate(rabbitTemplate);
		coder.setRestTemplate(restTemplate);
		ch.pipeline().addLast(new ReadTimeoutHandler(60*3));
		// ChannelOutboundHandler，依照逆序执行
		YianMsgEncoder encoder = new YianMsgEncoder(coder);
		// NettyServer.PORT_ENCODER_CACHE.put(port, encoder);
		ch.pipeline().addLast("encoder", encoder);

		// 属于ChannelInboundHandler，依照顺序执行
		YianMsgDecoder decoder = new YianMsgDecoder(coder);
		// NettyServer.PORT_DECODER_CACHE.put(port, decoder);
		ch.pipeline().addLast("decoder", decoder);
		/**
		 * 自定义ChannelInboundHandlerAdapter
		 */
		ch.pipeline().addLast(bootNettyChannelInboundHandlerAdapter);
		ChannelContext channelContext = ChannelContext.build().setChannel(ch).setCoder(coder).setPort(port)
				.setYianMsgDecoder(decoder).setYianMsgEncoder(encoder);
		NettyServer.PORT_CACHE.get(port).put(ch.id().asLongText(), channelContext);
		jedisPoolUtil.incr("server.lis.port." + springBaseConfig.getIp() + "." + port);

		// 分发设置，暂时全部压入mq，但是需要设置mq队列绑定关系
		String exchange = "topicExchange";
		String queue = "yianiot.man";
		String routingKey = "yianiot.man";
		if (jedisPoolUtil.setNx("rabbit.init.exchange" + exchange, "1")) {
			// 声明topic类型的exchange
			rabbitAdmin.declareExchange(new TopicExchange(exchange, true, false));
			// 声明队列
			rabbitAdmin.declareQueue(new Queue(queue));
			// 使用BindingBuilder进行绑定
			rabbitAdmin.declareBinding(
					BindingBuilder.bind(new Queue(queue)).to(new TopicExchange(exchange)).with(routingKey));
		}

		coder.afterConnection(ch);
		LOG.info("监听到连接请求");
	}

}
