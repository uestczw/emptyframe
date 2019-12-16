package com.yianit.common.netty;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yianit.common.task.SendTask;
import com.yianit.common.threadpools.SendThreadPool;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
@Component
@Sharable
public class BootNettyChannelInboundHandlerAdapter extends ChannelInboundHandlerAdapter{
	private final Logger LOG = LoggerFactory.getLogger(this.getClass());
	@Autowired
	SendThreadPool sendThreadPool;
    /**
     * 从客户端收到新的数据时，这个方法会在收到消息时被调用
     *
     * @param ctx
     * @param msg
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception, IOException
    {
    	//System.out.println("server:"+msg);
        //回应客户端
    	InetSocketAddress ipSocket = (InetSocketAddress) ctx.channel().localAddress();
		int port = ipSocket.getPort();
		SendRule rule = new SendRule();
        rule.setType(0);
        rule.setMqExchange("topicExchange");
        rule.setMqKey("yianiot.man");
        List<SendRule> rules = new LinkedList<SendRule>();
        rules.add(rule);
        SendTask task = new SendTask(rules,msg.toString());
        sendThreadPool.execute(task);
        ctx.writeAndFlush(msg);
    	//ctx.fireChannelRead(msg);
    }
 
    /**
     * 从客户端收到新的数据、读取完成时调用
     *
     * @param ctx
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws IOException
    {
    	//System.out.println("channelReadComplete");
    	ctx.flush();
    }
 
    /**
     * 当出现 Throwable 对象才会被调用，即当 Netty 由于 IO 错误或者处理器在处理事件时抛出的异常时
     *
     * @param ctx
     * @param cause
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws IOException
    {
    	//System.out.println("exceptionCaught");
        LOG.error("netty exceptionCaught",cause);
        ctx.close();//抛出异常，断开与客户端的连接
    }
 
    /**
     * 客户端与服务端第一次建立连接时 执行
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception, IOException
    {
        super.channelActive(ctx);
        ctx.channel().read();
        InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
        String clientIp = insocket.getAddress().getHostAddress();
        //此处不能使用ctx.close()，否则客户端始终无法与服务端建立连接
        //System.out.println("channelActive:"+clientIp+ctx.name());
    }
 
    /**
     * 客户端与服务端 断连时 执行
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception, IOException
    {
        super.channelInactive(ctx);
        DeviceCache.clearDevice(ctx.channel());
        InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
        String clientIp = insocket.getAddress().getHostAddress();
        ctx.close(); //断开连接时，必须关闭，否则造成资源浪费，并发量很大情况下可能造成宕机
        //System.out.println("channelInactive:"+clientIp);
    }
 
    /**
     * 服务端当read超时, 会调用这个方法
     *
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception, IOException
    {
        super.userEventTriggered(ctx, evt);
        InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
        String clientIp = insocket.getAddress().getHostAddress();
        ctx.close();//超时时断开连接
    	//System.out.println("userEventTriggered:"+clientIp);
    }

}
