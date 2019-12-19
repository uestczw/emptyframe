package com.yianit.config;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.yianit.common.netty.NettyServer;
import com.yianit.common.threadpools.SendThreadPool;
import com.yianit.common.util.JedisPoolUtil;

@Configurable
@Component
public class SendThreadConfig {
	@Autowired
	@Qualifier("yianRabbitTemplate")
	RabbitTemplate rabbitTemplate;
	@Autowired
	JedisPoolUtil jedisPoolUtil;
	@Value("${netty.server.core_pool_size}")
	private int core_pool_size = 4;
	@Value("${netty.server.maximum_pool_size}")
	private int maximum_pool_size = 4;
	@Value("${netty.server.keep_alive_time}")
	private long keep_alive_time = 1;
	private final Logger LOG = LoggerFactory.getLogger(NettyServer.class);
	public static final BlockingQueue<Runnable> QUEUE = new LinkedBlockingQueue<Runnable>();
	@Bean(name="sendThreadService")
	public ThreadPoolExecutor createExecutor(){
		LOG.info("分发池参数:");
		LOG.info("core_pool_size:"+core_pool_size);
		LOG.info("maximum_pool_size:"+maximum_pool_size);
		LOG.info("keep_alive_time:"+keep_alive_time);
		ThreadPoolExecutor SERVICE = new ThreadPoolExecutor(core_pool_size, maximum_pool_size,
				keep_alive_time, TimeUnit.MINUTES, QUEUE,
				new ThreadFactoryBuilder().setDaemon(true).setNameFormat("SENDTASK-THREAD-POOL-%d").build()) {
			@Override
			protected void afterExecute(Runnable r, Throwable t) {
				super.afterExecute(r, t);
//				if (r instanceof AbsSendTask) {
//					try {
//						QUEUE.put((AbsSendTask) r);
//					} catch (InterruptedException e) {
//						LOGGER.error("SendTask put:{}", e);
//					}
//					LOGGER.debug("SendTask put:{}", r);
//				}
			}
		};
//		ThreadPoolExecutor SERVICE = new YianExecutor(core_pool_size, maximum_pool_size,
//				keep_alive_time, TimeUnit.MINUTES, QUEUE,
//				new ThreadFactoryBuilder().setDaemon(true).setNameFormat("SENDTASK-THREAD-POOL-%d").build()){
//			@Override
//			protected void afterExecute(Runnable r, Throwable t) {
//				super.afterExecute(r, t);
//			}
//		};
		return SERVICE;
	}
	@Bean
	public SendThreadPool createSendThreadPool(@Qualifier("sendThreadService")ThreadPoolExecutor sendThreadService){
		SendThreadPool SendThreadPool = new SendThreadPool(rabbitTemplate,jedisPoolUtil,sendThreadService);
		return SendThreadPool;
	}
	public int getMaximum_pool_size() {
		return maximum_pool_size;
	}
	public void setMaximum_pool_size(int maximum_pool_size) {
		this.maximum_pool_size = maximum_pool_size;
	}
	
}
