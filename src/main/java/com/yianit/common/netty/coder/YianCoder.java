package com.yianit.common.netty.coder;

import java.util.List;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.client.RestTemplate;

import com.yianit.common.util.JedisPoolUtil;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

public interface YianCoder {
	public List<String> decode(ChannelHandlerContext ctx, ByteBuf in);

	public void encode(ChannelHandlerContext ctx, String msg, ByteBuf out);

	public int getVersion();

	public void initDeviceCache(String deviceId, Channel channel);

	public void setServerIp(String serverIp);

	public String getServerIp();

	public void afterConnection(Channel channel);

	public RabbitTemplate getRabbitTemplate();

	public void setRabbitTemplate(RabbitTemplate rabbitTemplate);

	public JedisPoolUtil getJedisPoolUtil();

	public void setJedisPoolUtil(JedisPoolUtil jedisPoolUtil);

	public RestTemplate getRestTemplate();

	public void setRestTemplate(RestTemplate restTemplate);
}
