package com.yianit.common.netty;

import java.util.Map;

import org.jboss.netty.util.internal.ConcurrentHashMap;

import com.yianit.common.util.JedisPoolUtil;

import hl.king.common.ApplicationContextHelper;
import io.netty.channel.Channel;
import tk.mybatis.mapper.util.StringUtil;

public class DeviceCache {
    public static Map<String,String> CHANNEL_DEVICE_CACHE = new ConcurrentHashMap<String,String>();
    public static Map<String,Channel> DEVICE_CHANNEL_CACHE = new ConcurrentHashMap<String,Channel>();
    public static void clearDevice(Channel channel){
    	JedisPoolUtil jedisPoolUtil = ApplicationContextHelper.getBean(JedisPoolUtil.class);
    	String channelId = channel.id().asLongText();
    	if(CHANNEL_DEVICE_CACHE.containsKey(channelId)){
    		String deviceId = CHANNEL_DEVICE_CACHE.get(channelId);
    		if(StringUtil.isNotEmpty(deviceId)&&DEVICE_CHANNEL_CACHE.containsKey(deviceId)){
    			DEVICE_CHANNEL_CACHE.remove(deviceId);
    		}
    		CHANNEL_DEVICE_CACHE.remove(channelId);
    		jedisPoolUtil.removeKey("clientcache_"+deviceId);
    	}
    }
}
