package com.yianit.common.netty.coder;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public class TestCoder extends BaseCoder {
	@Override
	public List<String> decode(ChannelHandlerContext ctx, ByteBuf in) {
		// TODO Auto-generated method stub
		//LOG.info("调用充电桩解码插件,版本" + getVersion());
		int alllen = in.readableBytes();
		byte[] data = new byte[alllen];
		in.readBytes(data);
		String ver = new String(data) + ",使用编解码插件" + this.getClass().getName() + ",版本号" + this.getVersion();
		List<String> outs = new ArrayList<String>();
		outs.add(ver);
		return outs;
	}

	@Override
	public void encode(ChannelHandlerContext ctx, String msg, ByteBuf out) {
		// TODO Auto-generated method stub
		//LOG.info("调用充电桩编码插件,版本" + getVersion());
		String ver = msg.toString();
		out.writeBytes(ver.getBytes());
	}

	@Override
	public int getVersion() {
		// TODO Auto-generated method stub
		return 2;
	}

}
