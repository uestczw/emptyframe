package com.yianit.common.netty;

import org.springframework.amqp.core.AmqpTemplate;

import com.yianit.common.util.JedisPoolUtil;

public abstract class AbsSendTask implements Runnable {
	protected AmqpTemplate rabbitTemplate;
	protected JedisPoolUtil jedisPoolUtil;

	public AmqpTemplate getRabbitTemplate() {
		return rabbitTemplate;
	}

	public void setRabbitTemplate(AmqpTemplate rabbitTemplate) {
		this.rabbitTemplate = rabbitTemplate;
	}

	public JedisPoolUtil getJedisPoolUtil() {
		return jedisPoolUtil;
	}

	public void setJedisPoolUtil(JedisPoolUtil jedisPoolUtil) {
		this.jedisPoolUtil = jedisPoolUtil;
	}

}
