package com.yianit.config.datasource;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import hl.king.common.RegisterBean;

/**
 * @author zhangw 多数据源核心配置
 */
@Configuration
public class DataSourceConfig {
	private final Logger LOG = LoggerFactory.getLogger(this.getClass());
	private final String defaultDbStr = "defaultdb";
	@Value("${spring.datasource.driver-class-name}")
	private String driverClassName;
	@Value("${spring.datasource.type}")
	private String type;
	@Value("${spring.datasource.hikari.read-only}")
	private boolean readOnly;
	@Value("${spring.datasource.hikari.connection-timeout}")
	private long connectionTimeout;
	@Value("${spring.datasource.hikari.idle-timeout}")
	private long idleTimeout;
	@Value("${spring.datasource.hikari.max-lifetime}")
	private long maxLifetime;
	@Value("${spring.datasource.hikari.connection-test-query}")
	private String connectionTestQuery;
	@Autowired
	private Environment env;
	@Autowired
	RegisterBean registerBean;
	@Value("${custom.datasource.ds1.url}")
	private String url;
	@Value("${custom.datasource.ds1.username}")
	private String username;
	@Value("${custom.datasource.ds1.password}")
	private String password;
	@Value("${custom.datasource.ds1.hikari.maximum-pool-size}")
	private int maximumPoolSize;

	/**
	 * @param db
	 * @return 根据数据源key加载对应配置
	 */
	public HikariConfig createConfig(String db) {
		HikariConfig hc = new HikariConfig();
		if (StringUtils.equals(defaultDbStr, db)) {
			hc.setJdbcUrl(env.getProperty("spring.datasource.url", String.class));
			hc.setPassword(env.getProperty("spring.datasource.password", String.class));
			hc.setUsername(env.getProperty("spring.datasource.username", String.class));
			hc.setMaximumPoolSize(env.getProperty("spring.datasource.hikari.maximum-pool-size", Integer.class));
		} else {
			hc.setJdbcUrl(env.getProperty("custom.datasource." + db + ".url", String.class));
			hc.setPassword(env.getProperty("custom.datasource." + db + ".password", String.class));
			hc.setUsername(env.getProperty("custom.datasource." + db + ".username", String.class));
			hc.setMaximumPoolSize(
					env.getProperty("custom.datasource." + db + ".hikari.maximum-pool-size", Integer.class));
		}
		hc.setConnectionTimeout(connectionTimeout);
		hc.setDriverClassName(driverClassName);
		hc.setReadOnly(readOnly);
		hc.setIdleTimeout(idleTimeout);
		hc.setMaxLifetime(maxLifetime);
		hc.setConnectionTestQuery(connectionTestQuery);
		return hc;
	}

	/**
	 * @param id
	 * @return 动态注册数据源到容器
	 */
	public DataSource regDataSource(String id) {
		Map<String, Object> params = new HashMap<String, Object>();
		try {
			registerBean.addBean(id, "com.zaxxer.hikari.HikariDataSource", params, createConfig(id));
		} catch (ClassNotFoundException e) {
			LOG.error("动态注册数据源到容器失敗", e);
			System.exit(-1);
		}
		return registerBean.getBean(id, HikariDataSource.class);
	}

	/**
	 * @return 动态注册数据源选择器
	 */
	public DataSource multipleDataSource() {
		// MultipleDataSource multipleDataSource = new MultipleDataSource();
		Map<Object, Object> targetDataSources = new HashMap<>();
		DataSource defaultdb = regDataSource(defaultDbStr);
		targetDataSources.put(defaultDbStr, defaultdb);
		String customnames = env.getProperty("custom.datasource.names", String.class);
		if (StringUtils.isNotEmpty(customnames)) {
			String[] cattr = customnames.split(",");
			for (String name : cattr) {
				if (StringUtils.isNotEmpty(name)) {
					targetDataSources.put(name, regDataSource(name));
				}
			}
		}
		// //添加数据源
		// multipleDataSource.setTargetDataSources(targetDataSources);
		// //设置默认数据源
		// multipleDataSource.setDefaultTargetDataSource(ds1);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("targetDataSources", targetDataSources);
		params.put("defaultTargetDataSource", defaultdb);
		try {
			registerBean.addBean("multipleDataSource", "com.yianit.config.datasource.MultipleDataSource", params);
		} catch (ClassNotFoundException e) {
			LOG.error("动态注册数据源选择器失敗", e);
			System.exit(-1);
		}
		return registerBean.getBean("multipleDataSource", MultipleDataSource.class);
	}

	@Bean("sqlSessionFactory")
	public SqlSessionFactory sqlSessionFactory() throws Exception {
		SqlSessionFactoryBean sqlSessionFactory = new SqlSessionFactoryBean();
		sqlSessionFactory.setDataSource(multipleDataSource());
		return sqlSessionFactory.getObject();
	}
}
