package com.yianit.config;

import org.springframework.cloud.commons.util.InetUtils;
import org.springframework.cloud.commons.util.InetUtilsProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import tk.mybatis.spring.mapper.MapperScannerConfigurer;

/**
 * spring 配置
 * 
 * @author HL.King
 */
@Configuration
public class ApplicationConfig {
	@Bean
	public MapperScannerConfigurer mapperScannerConfigurer() {
		MapperScannerConfigurer config = new MapperScannerConfigurer();
		config.setBasePackage("com.yianit.*.mapper.**..;hl.king.mybatis.spring.common.mapper.**..");
		return config;
	}
	@Bean
	public InetUtils inetUtils(InetUtilsProperties inetUtilsProperties){
	    return new InetUtils(inetUtilsProperties);
	}
}
