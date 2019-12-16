package com.yianit.common.netty;

import com.alipay.api.domain.Car;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class YianClientHandler extends ChannelInboundHandlerAdapter {

    private final String message;

    /**
     * Creates a client-side handler.
     */
    public YianClientHandler(String message) {
        this.message = message;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // Send the message to Server
        super.channelActive(ctx);

        System.out.println("client send message:"+message);
        ctx.writeAndFlush(message);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        // you can use the Object from Server here
        System.out.println("client receive msg:"+new String((byte[])msg));
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {

        cause.printStackTrace();
        ctx.close();
    }
}
