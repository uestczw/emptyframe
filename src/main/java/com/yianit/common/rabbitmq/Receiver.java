package com.yianit.common.rabbitmq;

import java.io.IOException;

import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.yianit.config.RabbitConfig;

import io.netty.handler.timeout.TimeoutException;

public class Receiver {
	private static final String QUEUE_NAME = "zhangw";

    public static void main(String[] args) {
    	RabbitConfig rc = new RabbitConfig();
		ConnectionFactory connectionFactory = rc.connectionFactory();
        try {
            //获取连接
            Connection connection = connectionFactory.createConnection();

            //创建频道
            Channel channel = connection.createChannel(false);
            //channel.exchangeDeclare("zhangwExchange","topic");
          //队列声明
            //channel.queueDeclare(QUEUE_NAME,false,false,false,null);

            //绑定队列到交换机转发器
            //channel.queueBind(QUEUE_NAME,"zhangwExchange","zhangw");
            //定义队列的消费者
            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
                        throws IOException {
                    String message = new String(body, "UTF-8");
                    System.out.println("接收到一个消息： " + message);
                }
            };

            channel.basicConsume(QUEUE_NAME, true, consumer);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }
}
