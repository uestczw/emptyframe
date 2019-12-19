package com.yianit.common.netty.coder;

import java.net.InetSocketAddress;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.client.RestTemplate;

import com.yianit.common.netty.ChannelContext;
import com.yianit.common.netty.DeviceCache;
import com.yianit.common.netty.NettyServer;
import com.yianit.common.util.JedisPoolUtil;

import hl.king.common.ApplicationContextHelper;
import io.netty.channel.Channel;
import tk.mybatis.mapper.util.StringUtil;

public abstract class BaseCoder implements YianCoder{
	protected RabbitTemplate rabbitTemplate;
	protected JedisPoolUtil jedisPoolUtil;
	protected RestTemplate restTemplate;
	protected String serverIp;
	private Logger LOG = LoggerFactory.getLogger(this.getClass());
	/* (non-Javadoc)
	 * @see com.yianit.common.netty.coder.YianCoder#initDeviceCache(java.lang.String, io.netty.channel.Channel)
	 * 初始化设备编号对应服务器缓存信息,设备注册时调用
	 */
	@Override
	public void initDeviceCache(String deviceId,Channel channel){
		InetSocketAddress ipSocket = (InetSocketAddress) channel.localAddress();
		int port = ipSocket.getPort();
		String channelId = channel.id().asLongText();
		if(NettyServer.PORT_CACHE.containsKey(port)&&NettyServer.PORT_CACHE.get(port).containsKey(channelId)){
			ChannelContext channelContext = NettyServer.PORT_CACHE.get(port).get(channelId);
			if(StringUtil.isEmpty(channelContext.getDeviceId())){
				if(StringUtil.isEmpty(deviceId)){
					deviceId = UUID.randomUUID().toString();
				}
				channelContext.setDeviceId(deviceId);
				DeviceCache.DEVICE_CHANNEL_CACHE.put(deviceId,channel);
				JedisPoolUtil jedisPoolUtil = ApplicationContextHelper.getBean(JedisPoolUtil.class);
				jedisPoolUtil.set("clientcache."+deviceId, this.getServerIp());
			}
		}
	}
	@Override
	public String getServerIp() {
		return serverIp;
	}
	@Override
	public void setServerIp(String serverIp) {
		this.serverIp = serverIp;
	}
	public RabbitTemplate getRabbitTemplate() {
		return rabbitTemplate;
	}
	public void setRabbitTemplate(RabbitTemplate rabbitTemplate) {
		this.rabbitTemplate = rabbitTemplate;
	}
	public JedisPoolUtil getJedisPoolUtil() {
		return jedisPoolUtil;
	}
	public void setJedisPoolUtil(JedisPoolUtil jedisPoolUtil) {
		this.jedisPoolUtil = jedisPoolUtil;
	}
	public RestTemplate getRestTemplate() {
		return restTemplate;
	}
	public void setRestTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}
	
}
