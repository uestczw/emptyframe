package com.yianit.common.threadpools;

import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import com.yianit.common.netty.AbsSendTask;
import com.yianit.common.util.JedisPoolUtil;
import com.yianit.config.distribute.SendThreadConfig;

//@Component
public class SendThreadPool {
	public SendThreadPool(RabbitTemplate amqpTemplate,JedisPoolUtil jedisPoolUtil,ThreadPoolExecutor SERVICE){
		this.rabbitTemplate = amqpTemplate;
		this.jedisPoolUtil = jedisPoolUtil;
		this.SERVICE = SERVICE;
	}
	RabbitTemplate rabbitTemplate;
	JedisPoolUtil jedisPoolUtil;
	private static final Logger LOGGER = LoggerFactory.getLogger(SendThreadPool.class);
	public static ThreadPoolExecutor SERVICE = null;

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
        	System.out.println(SendThreadConfig.QUEUE.size());
        	try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
	}
}
