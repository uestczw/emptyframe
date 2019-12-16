package com.yianit.common.netty;

import java.util.HashMap;
import java.util.Map;

import com.yianit.common.netty.coder.YianCoder;

public class YianBenCache {
	private static Map<String, YianCoder> cache = new HashMap<String, YianCoder>();
	public static void addCoder(String name,YianCoder coder){
		cache.put(name,coder);
	}
	
	public static YianCoder getDcoer(String name){
		return cache.get(name);
	}
}
