package com.yianit.config.rabbit;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory.CacheMode;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
@Configurable
@Component
public class RabbitConfig {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Value("${netty.server.maximum_pool_size}")
	private int maximum_pool_size = 4;
	@Value("${spring.rabbitmq.host}")
	private String host="192.168.3.200";

	@Value("${spring.rabbitmq.port}")
	private int port=5672;

	@Value("${spring.rabbitmq.username}")
	private String username="admin";

	@Value("${spring.rabbitmq.password}")
	private String password="admin";

	@Bean("yianConnectionFactory")
	public ConnectionFactory connectionFactory() {
		CachingConnectionFactory connectionFactory = new CachingConnectionFactory(host, port);
		connectionFactory.setUsername(username);
		connectionFactory.setPassword(password);
		connectionFactory.setVirtualHost("/");
		//connectionFactory.setPublisherConfirms(true);
		connectionFactory.setCacheMode(CacheMode.CONNECTION);
		connectionFactory.setChannelCacheSize(25);
		connectionFactory.setChannelCheckoutTimeout(300);
		connectionFactory.setConnectionLimit(maximum_pool_size);
		connectionFactory.setConnectionTimeout(2000);
		connectionFactory.setConnectionCacheSize(maximum_pool_size);
		//connectionFactory.setExecutor(2);
		return connectionFactory;
	}
	@Bean("yianRabbitTemplate")
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public RabbitTemplate rabbitTemplate(@Qualifier("yianConnectionFactory")ConnectionFactory connectionFactory) {
		RabbitTemplate template = new RabbitTemplate(connectionFactory);
		return template;
	}
	@Bean("yianRabbitAdmin")
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public RabbitAdmin rabbitAdmin(@Qualifier("yianConnectionFactory")ConnectionFactory connectionFactory) {
		RabbitAdmin template = new RabbitAdmin(connectionFactory);
		return template;
	}
	
	public static void main(String[] args){
		RabbitConfig rc = new RabbitConfig();
		ConnectionFactory connectionFactory = rc.connectionFactory();
		RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
		RabbitAdmin rabbitAdmin  = new RabbitAdmin(connectionFactory);
		//声明topic类型的exchange
		rabbitAdmin.declareExchange(new TopicExchange("zhangwExchange",true,false));
		//声明队列
		rabbitAdmin.declareQueue(new Queue("zhangw"));
		//使用BindingBuilder进行绑定
		rabbitAdmin.declareBinding(BindingBuilder.bind(new Queue("zhangw")).
		        to(new TopicExchange("zhangwExchange")).with("zhangw.#"));
		//rabbitTemplate.setQueue("zhangw");
		String messageId = String.valueOf(UUID.randomUUID());
		String createTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		Map<String, Object> manMap = new HashMap<>();
		manMap.put("messageId", messageId);
		manMap.put("messageData", "sadfsdfsdf");
		manMap.put("createTime", createTime);
		rabbitTemplate.convertAndSend("zhangwExchange", "zhangw", manMap);
//		Connection connection = connectionFactory.createConnection();
//		Channel channel = connection.createChannel(false);
//		String QUEUE_NAME = "zhangw";
//		try {
//			//声明交换机
//			 channel.exchangeDeclare("zhangwExchange","topic");
//	          //队列声明
//	            channel.queueDeclare(QUEUE_NAME,false,false,false,null);
//
//	            //绑定队列到交换机转发器
//	            channel.queueBind(QUEUE_NAME,"zhangwExchange","zhangw");
//			channel.basicPublish("zhangwExchange", "zhangw", null, "asdasdads".getBytes());
//			System.out.println("Send asdasdads");
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}finally{
//			try {
//				channel.close();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (TimeoutException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//	        connection.close();
//		}
	}
}
