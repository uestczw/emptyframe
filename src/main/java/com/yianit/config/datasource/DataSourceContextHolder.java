package com.yianit.config.datasource;

/**
 * @author zhangw
 *根据线程缓存数据源key
 */
public class DataSourceContextHolder {
	private static final ThreadLocal<String> contextHolder = new InheritableThreadLocal<>();

	/**
	 * 设置数据源
	 * 
	 * @param db
	 */
	public static void setDataSource(String db) {
		contextHolder.set(db);
	}

	/**
	 * 取得当前数据源
	 * 
	 * @return
	 */
	public static String getDataSource() {
		return contextHolder.get();
	}

	/**
	 * 清除上下文数据
	 */
	public static void clear() {
		contextHolder.remove();
	}
}
