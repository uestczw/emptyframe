package com.yianit.admin.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import hl.king.mybatis.spring.common.controller.BaseController;
@RestController
public class SendMqController extends BaseController {
	@Autowired
	RabbitTemplate rabbitTemplate;

	@GetMapping("/sendTopicMessage1")
	public String sendTopicMessage1() {
		String messageId = String.valueOf(UUID.randomUUID());
		String messageData = "路由topicExchange，关键值yianiot.man";
		String createTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		Map<String, Object> manMap = new HashMap<>();
		manMap.put("messageId", messageId);
		manMap.put("messageData", messageData);
		manMap.put("createTime", createTime);
		rabbitTemplate.convertAndSend("topicExchange", "yianiot.man", manMap);
		return "ok";
	}
}
