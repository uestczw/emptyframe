package com.yianit.common.threadpools;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.yianit.common.netty.AbsSendTask;
import com.yianit.common.util.JedisPoolUtil;

@Component
public class SendThreadPool {
	@Autowired
	AmqpTemplate rabbitTemplate;
	@Autowired
	JedisPoolUtil jedisPoolUtil;
	private static final Logger LOGGER = LoggerFactory.getLogger(SendThreadPool.class);
	private static final int CORE_POOL_SIZE = 4;
	private static final int MAXIMUM_POOL_SIZE = 4;
	private static final long KEEP_ALIVE_TIME = 10;
	public static final BlockingQueue<Runnable> QUEUE = new LinkedBlockingQueue<Runnable>();
	public static final ThreadPoolExecutor SERVICE = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE,
			KEEP_ALIVE_TIME, TimeUnit.MINUTES, QUEUE,
			new ThreadFactoryBuilder().setDaemon(true).setNameFormat("GP-THREAD-POOL-%d").build()) {
		@Override
		protected void afterExecute(Runnable r, Throwable t) {
			super.afterExecute(r, t);
//			if (r instanceof AbsSendTask) {
//				try {
//					QUEUE.put((AbsSendTask) r);
//				} catch (InterruptedException e) {
//					LOGGER.error("SendTask put:{}", e);
//				}
//				LOGGER.debug("SendTask put:{}", r);
//			}
		}
	};

	/**
	 * 执行任务
	 * 
	 * @param task
	 */
	public void execute(AbsSendTask task) {
		task.setRabbitTemplate(rabbitTemplate);
		task.setJedisPoolUtil(jedisPoolUtil);
		SERVICE.execute(task);
		LOGGER.debug("Parking space task added:{}", task);
	}

	public static void main(String[] args) {
        for(int i=0;i<30000;i++){
        	SERVICE.execute(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					
				}
			});
        }
        while(true){
        	System.out.println(QUEUE.size());
        	try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
	}
}
