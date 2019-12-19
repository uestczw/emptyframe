package com.yianit.common.rabbitmq;

import java.io.IOException;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.rabbitmq.client.Channel;

//@Component
public class TopicManReceiver {
 
	@RabbitListener(queues = "yianiot.man")
    public void process(Message message, Channel channel) {
    	try {
			channel.basicAck(message.getMessageProperties().getDeliveryTag(), true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        System.out.println("TopicManReceiver消费者收到消息  : " + message.toString());
    }
}
