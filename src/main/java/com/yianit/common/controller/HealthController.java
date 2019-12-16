package com.yianit.common.controller;

import org.apache.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import hl.king.mybatis.spring.common.controller.BaseController;
@RestController
public class HealthController extends BaseController{
	@RequestMapping("/health")
	public int health(){
		LOG.info("consul健康检查");
		return HttpStatus.SC_OK;
	}
}
