package com.yianit.common.netty.coder;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

public class EdCoder extends BaseCoder {
	@Override
	public List<String> decode(ChannelHandlerContext ctx, ByteBuf in) {
		//LOG.info("调用充电桩解码插件,版本" + getVersion());
		//收到设备注册报文时调用设备注册
		this.initDeviceCache(null, ctx.channel());
		int alllen = in.readableBytes();
		byte[] data = new byte[alllen];
		in.readBytes(data);
		String ver = null;
		try {
			ver = new String(data,"GBK") + ",coderName:" + this.getClass().getName() + ",coderVersion:" + this.getVersion();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<String> outs = new ArrayList<String>();
		outs.add(ver);
		return outs;
	}

	@Override
	public void encode(ChannelHandlerContext ctx, String msg, ByteBuf out) {
		// TODO Auto-generated method stub
		//LOG.info("调用充电桩编码插件,版本" + getVersion());
		String ver = msg.toString()+ ",coderName:" + this.getClass().getName() + ",coderVersion:" + this.getVersion();
		try {
			out.writeBytes(ver.getBytes("GBK"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public int getVersion() {
		// TODO Auto-generated method stub
		return 12;
	}

	@Override
	public void afterConnection(Channel channel) {
		// TODO Auto-generated method stub
		
	}

}
