package com.yianit.common.netty;

import com.yianit.common.netty.coder.YianCoder;
import com.yianit.common.netty.decoder.YianMsgDecoder;
import com.yianit.common.netty.encoder.YianMsgEncoder;

import io.netty.channel.Channel;

public class ChannelContext {
	private Channel channel;
	private int port;
	private String deviceId;
	private YianCoder coder;
	private YianMsgEncoder yianMsgEncoder;
	private YianMsgDecoder yianMsgDecoder;
	
	public static ChannelContext build(){
		return new ChannelContext();
	}

	public Channel getChannel() {
		return channel;
	}

	public ChannelContext setChannel(Channel channel) {
		this.channel = channel;
		return this;
	}

	public int getPort() {
		return port;
	}

	public ChannelContext setPort(int port) {
		this.port = port;
		return this;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public ChannelContext setDeviceId(String deviceId) {
		this.deviceId = deviceId;
		return this;
	}

	public YianCoder getCoder() {
		return coder;
	}

	public ChannelContext setCoder(YianCoder coder) {
		this.coder = coder;
		return this;
	}

	public YianMsgEncoder getYianMsgEncoder() {
		return yianMsgEncoder;
	}

	public ChannelContext setYianMsgEncoder(YianMsgEncoder yianMsgEncoder) {
		this.yianMsgEncoder = yianMsgEncoder;
		return this;
	}

	public YianMsgDecoder getYianMsgDecoder() {
		return yianMsgDecoder;
	}

	public ChannelContext setYianMsgDecoder(YianMsgDecoder yianMsgDecoder) {
		this.yianMsgDecoder = yianMsgDecoder;
		return this;
	}

}
