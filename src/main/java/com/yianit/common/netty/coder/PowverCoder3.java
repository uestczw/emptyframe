package com.yianit.common.netty.coder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yianit.common.util.HexUtil;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

public class PowverCoder3 extends BaseCoder {
	private byte[] cache = null;
	private final Logger LOG = LoggerFactory.getLogger(this.getClass());

	@Override
	public List<String> decode(ChannelHandlerContext ctx, ByteBuf in) {
		// LOG.info("调用充电桩解码插件,版本" + getVersion());
		List<String> outs = new ArrayList<String>();
		int alllen = in.readableBytes();
		byte[] data = new byte[alllen];
		in.readBytes(data);
//		if (data[0] == -52 && data[1] == -52) {
//			byte lenb = data[4];
//			int len = HexUtil.byteToInt(lenb);
//			if (alllen == len) {
//				if (data[5] == 0x02) {
//					byte[] deviceIdBytes = new byte[8];
//					for (int i = 0; i < 8; i++) {
//						deviceIdBytes[i] = data[14 + i];
//					}
//					// 收到设备注册报文时调用设备注册
//					this.initDeviceCache(new String(deviceIdBytes), ctx.channel());
//					String id = new String(deviceIdBytes);
//					String s = "DDDDAAAA1f031111111122334455" + id + "3132313831363234";
//					ctx.channel().writeAndFlush(s);
//
//					outs.add(HexUtil.byteToHex(data));
//				}
//				cache = null;
//			} else if (alllen > lenb) {
//				LOG.info("包头标记相同，但接收到的数据长度大于报文给定长度");
//			} else {
//				LOG.info("包头标记相同，但接收到的数据长度小于报文给定长度");
//			}
//		} else {
//			LOG.info("包头标记不相同");
//			if (cache != null) {
//
//			} else {
//				boolean hasHead = false;
//				int headIndex = 0;
//				for (int i = 0; i < data.length - 1; i++) {
//					if (data[i] == -52 && data[i + 1] == -52) {
//						if (data.length - i >= 5) {
//							byte lenb = data[i+4];
//							int len = HexUtil.byteToInt(lenb);
//							if(data.length - i == len){
//								byte[] tmpb = Arrays.copyOfRange(data, i, data.length-1);
//							}else if(data.length - i > len){
//								byte[] tmpb = Arrays.copyOfRange(data, i, i+len);
//							}else{
//								cache = Arrays.copyOfRange(data, i, data.length-1);
//							}
//						}
//						hasHead = true;
//						headIndex = i;
//						break;
//					}
//				}
//			}
//		}
		byte[] all = null;
		if(cache!=null){
			all = ArrayUtils.addAll(cache, data);
			cache = null;
		}else{
			all = data;
		}
		int start = 0;
		byte[] dist = null;
		while(start!=-1){
			dist = null;
			Object[] ret = format(all,start);
			start = (int)ret[0];
			dist = ret[1] == null?null:(byte[])ret[1];
			if(dist!=null){
				//校验格式，不对则丢弃
				//LOG.info(HexUtil.byteToHex(dist));
			}
			if (dist!=null&&dist[5] == 0x02) {
				byte[] deviceIdBytes = new byte[8];
				for (int i = 0; i < 8; i++) {
					deviceIdBytes[i] = dist[14 + i];
				}
				// 收到设备注册报文时调用设备注册
				this.initDeviceCache(new String(deviceIdBytes), ctx.channel());
				String id = new String(deviceIdBytes);
				String s = "DDDDAAAA1f031111111122334455" + id + "3132313831363234";
				ctx.channel().writeAndFlush(s);
				outs.add(HexUtil.byteToHex(dist));
			}
		}
//		new Thread(() -> {
//			try {
//				Thread.sleep(2000);
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			ctx.channel().writeAndFlush(HexUtil.byteToHex(data));
//		}).start();

		//LOG.info(HexUtil.byteToHex(data));
		// String ver = null;
		// try {
		// ver = new String(data, "GBK") + ",coderName:" +
		// this.getClass().getName() + ",coderVersion:"
		// + this.getVersion();
		// } catch (UnsupportedEncodingException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		return outs;
	}
	
	private Object[] format(byte[] data,int start){
		byte[] dist = null;
		int end = -1;
		for (int i = start; i < data.length - 1; i++) {
			if (data[i] == -52 && data[i + 1] == -52) {
				if (data.length - i >= 5) {
					byte lenb = data[i+4];
					int len = HexUtil.byteToInt(lenb);
					if(data.length - i == len){
						dist = Arrays.copyOfRange(data, i, data.length);
					}else if(data.length - i > len){
						dist = Arrays.copyOfRange(data, i, i+len);
						end = i+len;
					}else{
						cache = Arrays.copyOfRange(data, i, data.length);
					}
				}else{
					cache = Arrays.copyOfRange(data, i, data.length);
				}
			}
		}
		Object[] ret = new Object[]{end,dist};
		return ret;
	}

	@Override
	public void encode(ChannelHandlerContext ctx, String msg, ByteBuf out) {
		// TODO Auto-generated method stub
		// LOG.info("调用充电桩编码插件,版本" + getVersion());
		// String ver = msg.toString()+ ",coderName:" +
		// this.getClass().getName() + ",coderVersion:" + this.getVersion();
		byte[] bm = HexUtil.hexStr2Byte(msg);
		out.writeBytes(bm);
	}

	@Override
	public int getVersion() {
		// TODO Auto-generated method stub
		return 2;
	}

	@Override
	public void afterConnection(Channel channel) {
		// TODO Auto-generated method stub
		channel.writeAndFlush("DDDD000024011388005a00b4012c0e1000280014000111111111111111111111000a050a");
	}

}
