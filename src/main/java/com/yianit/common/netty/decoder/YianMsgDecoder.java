package com.yianit.common.netty.decoder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yianit.common.netty.DeviceCache;
import com.yianit.common.netty.coder.YianCoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class YianMsgDecoder extends ByteToMessageDecoder {
	private Logger LOG = LoggerFactory.getLogger(this.getClass());
	public static final int HEAD_LENGTH = 4;
	private YianCoder coder;

	public YianMsgDecoder(YianCoder coder) {
		this.coder = coder;
	}

	public YianMsgDecoder() {
		super();
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		// int dataLength = in.readableBytes();
		// System.out.println("dataLength:" + dataLength);
		// byte[] body = new byte[dataLength]; // 传输正常
		// in.readBytes(body);
		// InetSocketAddress ipSocket = (InetSocketAddress)
		// ctx.channel().localAddress();
		// int port = ipSocket.getPort();
		// LOG.info("我的端口号:" + port);
		// coder.decode(ctx,in);
		coder.initDeviceCache(null, ctx.channel());
		out.addAll(coder.decode(ctx, in));
	}

	public void updateCoder(YianCoder coder) {
		this.coder = coder;
	}
}
