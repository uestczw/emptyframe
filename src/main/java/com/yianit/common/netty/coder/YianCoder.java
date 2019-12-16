package com.yianit.common.netty.coder;

import io.netty.channel.Channel;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public interface YianCoder {
	public List<String> decode(ChannelHandlerContext ctx, ByteBuf in);

	public void encode(ChannelHandlerContext ctx, String msg, ByteBuf out);

	public int getVersion();

	public void initDeviceCache(String deviceId, Channel channel);

	public void setServerIp(String serverIp);

	public String getServerIp();
}
