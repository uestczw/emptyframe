package com.yianit.admin.service.auth;

import java.util.List;

import com.yianit.model.Test;

import hl.king.mybatis.spring.common.service.BaseService;

public interface TestService extends BaseService<Test>{
	public List<Test> testmoresource();
	public void addTest1();
	public void addTest2();
}
