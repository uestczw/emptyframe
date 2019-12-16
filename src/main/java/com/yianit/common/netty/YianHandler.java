package com.yianit.common.netty;

import java.io.IOException;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class YianHandler extends ChannelInboundHandlerAdapter{
	private ChannelContext context;
    public YianHandler(ChannelContext context){
    	this.context = context;
    }
	public YianHandler(){
		
	}
	/**
     * 从客户端收到新的数据时，这个方法会在收到消息时被调用
     *
     * @param ctx
     * @param msg
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception, IOException
    {
    	System.out.println("YianHandler:read msg:"+new String((byte[])msg));
        //回应客户端
        ctx.writeAndFlush("I got it");
    }
}
