package com.yianit.common.netty;

import com.yianit.common.netty.decoder.YianMsgDecoder;
import com.yianit.common.netty.encoder.YianMsgEncoder;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class YianTransferClient {
	private String host;
	private int port;
	private String message;

	public YianTransferClient(String host, int port, String message) {
		this.host = host;
		this.port = port;
		this.message = message;
	}

	public void send() throws InterruptedException {

		Bootstrap bootstrap = new Bootstrap();
		EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

		try {
			bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class)
					.handler(new ChannelInitializer<SocketChannel>() {
						@Override
						protected void initChannel(SocketChannel ch) throws Exception {
							ch.pipeline().addLast(new YianMsgEncoder(), new YianMsgDecoder(),
									new YianClientHandler(message));
						}
					});

			ChannelFuture future = bootstrap.connect(host, port).sync();

			future.channel().closeFuture().sync();
		} finally {
			eventLoopGroup.shutdownGracefully();
		}
	}

	public static void main(String[] args) throws Exception {
		new YianTransferClient("127.0.0.1", 7997, "你好").send();
	}
}
