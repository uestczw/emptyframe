package com.yianit.common.netty;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import com.yianit.common.util.JedisPoolUtil;

public abstract class AbsSendTask implements Runnable {
	protected RabbitTemplate rabbitTemplate;
	protected JedisPoolUtil jedisPoolUtil;
	protected ConnectionFactory connectionFactory;
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

	public ConnectionFactory getConnectionFactory() {
		return connectionFactory;
	}

	public void setConnectionFactory(ConnectionFactory connectionFactory) {
		this.connectionFactory = connectionFactory;
	}

}
