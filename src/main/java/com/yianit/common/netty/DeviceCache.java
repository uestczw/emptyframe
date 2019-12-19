package com.yianit.common.netty;

import java.net.InetSocketAddress;
import java.util.Map;

import org.jboss.netty.util.internal.ConcurrentHashMap;

import com.yianit.common.util.JedisPoolUtil;
import com.yianit.config.SpringBaseConfig;

import hl.king.common.ApplicationContextHelper;
import io.netty.channel.Channel;
import tk.mybatis.mapper.util.StringUtil;

public class DeviceCache {
    //public static Map<String,String> CHANNEL_DEVICE_CACHE = new ConcurrentHashMap<String,String>();
    public static Map<String,Channel> DEVICE_CHANNEL_CACHE = new ConcurrentHashMap<String,Channel>();
    public static void clearDevice(Channel channel){
    	JedisPoolUtil jedisPoolUtil = ApplicationContextHelper.getBean(JedisPoolUtil.class);
    	String channelId = channel.id().asLongText();
    	InetSocketAddress ipSocket = (InetSocketAddress) channel.localAddress();
		int port = ipSocket.getPort();
    	if(NettyServer.PORT_CACHE.containsKey(port)&&NettyServer.PORT_CACHE.get(port).containsKey(channelId)){
    		ChannelContext channelContext = NettyServer.PORT_CACHE.get(port).get(channelId);
    		String deviceId =channelContext.getDeviceId();
    		if(StringUtil.isNotEmpty(deviceId)&&DEVICE_CHANNEL_CACHE.containsKey(deviceId)){
    			DEVICE_CHANNEL_CACHE.remove(deviceId);
    		}
    		 NettyServer.PORT_CACHE.get(port).remove(channelId);
    		jedisPoolUtil.removeKey("clientcache."+deviceId);
    	}
    	SpringBaseConfig springBaseConfig = ApplicationContextHelper.getBean(SpringBaseConfig.class);
    	jedisPoolUtil.decr("server.lis.port." + springBaseConfig.getIp()+"."+port);
    }
}
