package com.yianit.common.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration
public class TopicRabbitConfig {
    //绑定键
    public final static String man = "yianiot.man";
    public final static String woman = "yianiot.woman";
 
    @Bean
    public Queue firstQueue() {
        return new Queue(TopicRabbitConfig.man);
    }
 
//    @Bean
//    public Queue secondQueue() {
//        return new Queue(TopicRabbitConfig.woman);
//    }
 
    @Bean
    TopicExchange exchange() {
        return new TopicExchange("topicExchange");
    }
 
 
    //将firstQueue和topicExchange绑定,而且绑定的键值为topic.man
    //这样只要是消息携带的路由键是topic.man,才会分发到该队列
    @Bean
    Binding bindingExchangeMessage() {
        return BindingBuilder.bind(firstQueue()).to(exchange()).with(man);
    }
 
    //将secondQueue和topicExchange绑定,而且绑定的键值为用上通配路由键规则topic.#
    // 这样只要是消息携带的路由键是以topic.开头,都会分发到该队列
//    @Bean
//    Binding bindingExchangeMessage2() {
//        return BindingBuilder.bind(secondQueue()).to(exchange()).with("yianiot.#");
//    }
    
    //@Bean
    public RabbitTemplate createRabbitTemplate(ConnectionFactory connectionFactory){
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);
        //设置开启Mandatory,才能触发回调函数,无论消息推送结果怎么样都强制调用回调函数
        rabbitTemplate.setMandatory(true);
 
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
//                System.out.println("ConfirmCallback:     "+"相关数据："+correlationData);
//                System.out.println("ConfirmCallback:     "+"确认情况："+ack);
//                System.out.println("ConfirmCallback:     "+"原因："+cause);
            }
        });
 
        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            @Override
            public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
//                System.out.println("ReturnCallback:     "+"消息："+message);
//                System.out.println("ReturnCallback:     "+"回应码："+replyCode);
//                System.out.println("ReturnCallback:     "+"回应信息："+replyText);
//                System.out.println("ReturnCallback:     "+"交换机："+exchange);
//                System.out.println("ReturnCallback:     "+"路由键："+routingKey);
            }
        });
 
        return rabbitTemplate;
    }
 
}
