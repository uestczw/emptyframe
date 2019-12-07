package com.yianit.admin.service.auth;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yianit.common.mapper.TestMapper;
import com.yianit.config.datasource.DataSourceContextHolder;
import com.yianit.config.datasource.TargetDataSource;
import com.yianit.model.Test;

import hl.king.mybatis.spring.common.service.BaseServiceImpl;
import hl.king.mybatis.spring.common.utils.Query;

@Service("testService")
public class TestServiceImpl extends BaseServiceImpl<Test, TestMapper> implements TestService {
	@TargetDataSource("ds2")
	@Override
	public List<Test> testmoresource() {
		// TODO Auto-generated method stub
		// 手动设置数据源
		// DataSourceContextHolder.setDataSource("ds1");
		// try{
		//
		// }finally{
		// DataSourceContextHolder.clear();
		// }
		return this.list(Query.getInstance());
	}

	@Transactional
	@Override
	public void addTest1() {
		// TODO Auto-generated method stub
		DataSourceContextHolder.setDataSource("ds2");
		try {
			Test t = new Test();
			t.setAddress("3");
			t.setTel("3");
			t.setUserName("3");
			this.add(t);
			addTest2();
		} finally {
			DataSourceContextHolder.clear();
		}
	}

	@Transactional
	@Override
	public void addTest2() {
		// TODO Auto-generated method stub
		Test t = new Test();
		t.setAddress("4");
		t.setTel("4");
		t.setUserName("4");
		this.add(t);
	}

}
