package com.yianit.common.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.yianit.common.netty.AbsSendTask;
import com.yianit.common.netty.SendRule;

public class SendTask extends AbsSendTask {
	private List<SendRule> sendRules;
	private String content;

	public SendTask(List<SendRule> sendRules, String content) {
		this.sendRules = sendRules;
		this.content = content;
	}

	@Override
	public void run() {
		//System.out.println("执行分发任务");
		for (SendRule rule : sendRules) {
			if (rule.getType() == 0) {
				try {
					String messageId = String.valueOf(UUID.randomUUID());
					String createTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
					Map<String, Object> manMap = new HashMap<>();
					manMap.put("messageId", messageId);
					manMap.put("messageData", content);
					manMap.put("createTime", createTime);
					//jedisPoolUtil.set("hhhhh", content);
					 rabbitTemplate.convertAndSend(rule.getMqExchange(), rule.getMqKey(), manMap);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
