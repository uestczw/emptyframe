package com.yianit.admin.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yianit.common.netty.SendRule;
import com.yianit.common.task.SendTask;
import com.yianit.common.threadpools.SendThreadPool;

import hl.king.mybatis.spring.common.controller.BaseController;
@RestController
public class SendMqController extends BaseController {
	@Autowired
	RabbitTemplate rabbitTemplate;
	@Autowired
	SendThreadPool sendThreadPool;
	@GetMapping("/sendTopicMessage1")
	public String sendTopicMessage1() {
		SendRule rule = new SendRule();
        rule.setType(0);
        rule.setMqExchange("topicExchange");
        rule.setMqKey("yianiot.man");
        List<SendRule> rules = new LinkedList<SendRule>();
        rules.add(rule);
        SendTask task = new SendTask(rules,"电饭锅电饭锅电饭锅电饭锅电饭锅电饭锅电饭锅电饭锅");
        for(int j=0;j<20;j++){
        new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				for(int i=0;i<100000;i++){
		        	sendThreadPool.execute(task);
		        }
			}
		}).start();
        }
//		String messageId = String.valueOf(UUID.randomUUID());
//		String messageData = "路由topicExchange，关键值yianiot.man";
//		String createTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
//		Map<String, Object> manMap = new HashMap<>();
//		manMap.put("messageId", messageId);
//		manMap.put("messageData", messageData);
//		manMap.put("createTime", createTime);
//		rabbitTemplate.convertAndSend("topicExchange", "yianiot.man", manMap);
		return "ok";
	}
}
