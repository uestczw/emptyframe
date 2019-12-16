package com.yianit.common.netty;

public class SendRule {
	private int type;// 0:mq,1:微服务
	private String mqExchange;// mq的路由
	private String mqKey;// mq的关键值
	private String serviceName;// 微服务路径
	private String servicePath;// 微服务路径

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getMqExchange() {
		return mqExchange;
	}

	public void setMqExchange(String mqExchange) {
		this.mqExchange = mqExchange;
	}

	public String getMqKey() {
		return mqKey;
	}

	public void setMqKey(String mqKey) {
		this.mqKey = mqKey;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getServicePath() {
		return servicePath;
	}

	public void setServicePath(String servicePath) {
		this.servicePath = servicePath;
	}

}
