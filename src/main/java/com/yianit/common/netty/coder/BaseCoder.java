package com.yianit.common.netty.coder;

import java.lang.management.ManagementFactory;
import java.net.InetSocketAddress;
import java.util.Set;
import java.util.UUID;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yianit.common.netty.DeviceCache;
import com.yianit.common.util.IpUtil;
import com.yianit.common.util.JedisPoolUtil;

import hl.king.common.ApplicationContextHelper;
import io.netty.channel.Channel;

public abstract class BaseCoder implements YianCoder{
	private String serverIp;
	private Logger LOG = LoggerFactory.getLogger(this.getClass());
	@Override
	public void initDeviceCache(String deviceId,Channel channel){
		if(!DeviceCache.CHANNEL_DEVICE_CACHE.containsKey(channel.id().asLongText())){
			String uuid = UUID.randomUUID().toString();
			deviceId = uuid;
			DeviceCache.CHANNEL_DEVICE_CACHE.put(channel.id().asLongText(), uuid);
			DeviceCache.DEVICE_CHANNEL_CACHE.put(uuid,channel);
			JedisPoolUtil jedisPoolUtil = ApplicationContextHelper.getBean(JedisPoolUtil.class);
//			InetSocketAddress ipSocket = (InetSocketAddress) channel.localAddress();
//			int port = ipSocket.getPort();
			jedisPoolUtil.set("clientcache_"+deviceId, this.getServerIp());
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
}
