package com.yianit.config.datasource;


import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author zhangw
 *aop检测数据源注解,根据注解设置线程对应数据源key
 */
@Component
@Aspect
@Order(-1)
public class DataSourceAspect {
	private final Logger LOG = LoggerFactory.getLogger(this.getClass());
    @Pointcut("@annotation(com.yianit.config.datasource.TargetDataSource)")
    public void pointCut(){

    }

    @Before(value ="pointCut() && @annotation(targetDataSource)", argNames = "targetDataSource")
    public void doBefore(TargetDataSource targetDataSource){
    	LOG.info("选择数据源---"+targetDataSource.value());
        DataSourceContextHolder.setDataSource(targetDataSource.value());
    }

    @After("pointCut()")
    public void doAfter(){
        DataSourceContextHolder.clear();
    }
}
