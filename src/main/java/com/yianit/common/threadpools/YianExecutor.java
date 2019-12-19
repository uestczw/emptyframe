package com.yianit.common.threadpools;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class YianExecutor extends ThreadPoolExecutor {
	public YianExecutor(int core_pool_size,int maximum_pool_size,long keep_alive_time,TimeUnit timeUnit,BlockingQueue queque,ThreadFactory threadFactory){
    	super(core_pool_size,maximum_pool_size,keep_alive_time,timeUnit,queque,threadFactory);
    }
}
