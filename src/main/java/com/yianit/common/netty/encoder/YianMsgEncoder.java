package com.yianit.common.netty.encoder;

import java.net.InetSocketAddress;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.yianit.common.netty.YianBenCache;
import com.yianit.common.netty.coder.YianCoder;

import hl.king.common.RegisterBean;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class YianMsgEncoder extends MessageToByteEncoder<String> {
	private Logger LOG = LoggerFactory.getLogger(this.getClass());
	private YianCoder coder;

	public YianMsgEncoder(YianCoder coder) {
		this.coder = coder;
	}

	public YianMsgEncoder() {
		super();
	}

	@Override
	protected void encode(ChannelHandlerContext ctx, String msg, ByteBuf out) throws Exception {
		// InetSocketAddress ipSocket = (InetSocketAddress)
		// ctx.channel().localAddress();
		// int port = ipSocket.getPort();
		// LOG.info("我的端口号:" + ctx.channel().localAddress().toString());
		// byte[] body = msg.getBytes(); // 将对象转换为byte
		// byte[] rd = null;
		// try {
		// if (coder != null) {
		// rd = coder.encode(ctx,msg);
		// }
		// } catch (Exception e) {
		// }
		// body = msg.getBytes();
		coder.encode(ctx, msg, out); // 消息体中包含我们要发送的数据
	}

	public void updateCoder(YianCoder coder) {
		this.coder = coder;
	}
}
