package com.yianit.config;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;

@Configurable
public class NettyConfig {
	@Value("${netty.server.boss}")
	private int boss;
	@Value("${netty.server.worker}")
	private int worker;
	public int getBoss() {
		return boss;
	}

	public void setBoss(int boss) {
		this.boss = boss;
	}

	public int getWorker() {
		return worker;
	}

	public void setWorker(int worker) {
		this.worker = worker;
	}
}
