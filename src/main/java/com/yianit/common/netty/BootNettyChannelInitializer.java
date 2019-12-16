package com.yianit.common.netty;

import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yianit.common.netty.coder.YianCoder;
import com.yianit.common.netty.decoder.YianMsgDecoder;
import com.yianit.common.netty.encoder.YianMsgEncoder;
import com.yianit.config.SpringBaseConfig;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

@Component
public class BootNettyChannelInitializer<SocketChannel> extends ChannelInitializer<Channel> {
	private final Logger LOG = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private SpringBaseConfig springBaseConfig;
	@Autowired
	BootNettyChannelInboundHandlerAdapter bootNettyChannelInboundHandlerAdapter;
	@Override
	protected void initChannel(Channel ch) throws Exception {
		InetSocketAddress ipSocket = (InetSocketAddress) ch.localAddress();
		int port = ipSocket.getPort();
		if (!NettyServer.STATUS_CACHE.containsKey(port)) {
			LOG.info("正在关闭端口" + port + "服务，不接受新的连接");
			ch.close();
		}
		YianCoder coder = YianBenCache.getDcoer("yianrule" + port);
		if (coder == null) {
			LOG.error("端口" + port + "未配置编解码规则");
		}
		coder.setServerIp(springBaseConfig.getIp());
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
		ch.pipeline().addLast("server_hander", bootNettyChannelInboundHandlerAdapter);
		ChannelContext channelContext = ChannelContext.build().setChannel(ch).setCoder(coder).setPort(port)
				.setYianMsgDecoder(decoder).setYianMsgEncoder(encoder);
		NettyServer.PORT_CACHE.get(port).put(ch.id().asLongText(), channelContext);
		LOG.info("监听到连接请求");
	}

}
