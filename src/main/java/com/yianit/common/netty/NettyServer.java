package com.yianit.common.netty;

import java.io.File;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.yianit.common.netty.coder.TestCoder;
import com.yianit.common.netty.coder.YianCoder;
import com.yianit.common.util.JedisPoolUtil;
import com.yianit.config.SpringBaseConfig;

import hl.king.common.RegisterBean;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

@Configuration
@Component
public class NettyServer {
	@Resource
	private RegisterBean registerBean;
	@Value("${netty.server.boss}")
	private int boss;
	@Value("${netty.server.worker}")
	private int worker;
	@Value("${server.port}")
	private int tomcatPort;
	@Autowired
	private SpringBaseConfig springBaseConfig;
	@Autowired
	@Qualifier("jedisPoolUtil")
	private JedisPoolUtil jedisPoolUtil;
	@Autowired
	BootNettyChannelInitializer<SocketChannel> bootNettyChannelInitializer;
	private final Logger LOG = LoggerFactory.getLogger(NettyServer.class);
	public static Map<Integer, Map<String, ChannelContext>> PORT_CACHE = new ConcurrentHashMap<Integer, Map<String, ChannelContext>>();
	public static Map<Integer, Boolean> STATUS_CACHE = new ConcurrentHashMap<Integer, Boolean>();
	// public static Map<Integer, YianMsgEncoder> PORT_ENCODER_CACHE = new
	// ConcurrentHashMap<Integer, YianMsgEncoder>();
	// public static Map<Integer, YianMsgDecoder> PORT_DECODER_CACHE = new
	// ConcurrentHashMap<Integer, YianMsgDecoder>();
	// private EventLoopGroup bossGroup = new NioEventLoopGroup(boss,new
	// DefaultThreadFactory("server1", true));
	// private EventLoopGroup workerGroup = new NioEventLoopGroup(worker,new
	// DefaultThreadFactory("server2", true));
	private static final int CORE_POOL_SIZE = 4;
	private static final int MAXIMUM_POOL_SIZE = 4;
	private static final long KEEP_ALIVE_TIME = 10;
	public static final BlockingQueue<Runnable> QUEUE = new LinkedBlockingQueue<Runnable>();
	public static final ThreadPoolExecutor SERVICE = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE,
			KEEP_ALIVE_TIME, TimeUnit.MINUTES, QUEUE,
			new ThreadFactoryBuilder().setDaemon(true).setNameFormat("GP-THREAD-POOL-%d").build()) {
		@Override
		protected void afterExecute(Runnable r, Throwable t) {
			super.afterExecute(r, t);
		}
	};
	private EventLoopGroup bossGroup = new NioEventLoopGroup(0);
	private EventLoopGroup workerGroup = new NioEventLoopGroup(0, SERVICE);
	private ServerBootstrap bootstrap = new ServerBootstrap();
	private ChannelFuture[] ChannelFutures = null;
	private int beginPort = 6000;
	private int endPort = 8000;

	// public NettyServer(int beginPort, int endPort) {
	// this.beginPort = beginPort;
	// this.endPort = endPort;
	// }

	public static void main(String[] args) {
		NettyServer server = new NettyServer();
		server.start();

		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
		}
		server.stopServerChannel(6000);

	}

	@PostConstruct
	public void init() {
		this.start();

		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
		}
		// this.stopServerChannel(6000);

	}

	public void start() {
		initRedisCache();
		System.out.println("server starting....");
		bootstrap.group(bossGroup, workerGroup);
		bootstrap.channel(NioServerSocketChannel.class);
		bootstrap.childOption(ChannelOption.SO_REUSEADDR, true);

		bootstrap.childHandler(bootNettyChannelInitializer);

		if (ChannelFutures == null) {
			ChannelFutures = new ChannelFuture[endPort - beginPort + 1];
		}
		YianCoder coder = new TestCoder();
		// 多个端口绑定
		ApplicationHome h = new ApplicationHome(getClass());
		File jarF = h.getSource();
		if (jarF == null) {
			jarF = h.getDir();
		}
		System.out.println("jarF:" + jarF);
		String onlinePath = jarF.getParentFile().toString() + "/activefile/online/";
		File online = new File(onlinePath);
		if (online.exists()) {
			String[] fs = online.list();
			String tmpf = null;
			String packagePath = "/com/yianit/common/netty/coder/";
			for (String port : fs) {
				System.out.println(port);
				tmpf = onlinePath + "/" + port;
				System.out.println("查找目录下class文件" + tmpf);
				File cfp = new File(tmpf);
				if (cfp.exists()) {
					File[] cfs = cfp.listFiles();
					if (cfs == null || cfs.length == 0 || cfs.length > 1) {
						return;
					}
					File cf = cfs[0];
					String path = "com.yianit.common.netty.coder." + cf.getName().replace(".class", "");
					System.out.println(cf.getPath());
					NetworkClassLoader ncl = new NetworkClassLoader(cf.getPath(), YianCoder.class.getClassLoader());
					Class clazz;
					YianCoder newInstance = null;
					try {
						clazz = ncl.loadClass(path);
						newInstance = (YianCoder) clazz.newInstance();
						YianBenCache.addCoder("yianrule" + port, newInstance);
						this.openServerChannel(Integer.parseInt(port));
						LOG.error("注册" + port + "编解码规则[" + path + "]成功,版本号:" + newInstance.getVersion());
					} catch (Exception e) {
						LOG.error("注册" + port + "编解码规则[" + path + "]失败", e);
					}
				} else {
					System.out.println("不存在目录" + cfp.getPath());
				}
			}
		}
		// for (int i = beginPort; i <= endPort; i++) {
		// openServerChannel(i);
		// }
		// for (int i = beginPort; i <= endPort; i++) {
		// final int port = i;
		// ChannelFuture channelFuture = bootstrap.bind(port);
		// ChannelFutures[i - beginPort] = channelFuture;
		// channelFuture.addListener(new GenericFutureListener<Future<? super
		// Void>>() {
		// @Override
		// public void operationComplete(Future<? super Void> future) throws
		// Exception {
		// if (future.isSuccess()) {
		// System.out.println("Started success,port:" + port);
		// } else {
		// System.out.println("Started Failed,port:" + port);
		// }
		// }
		// });
		// }
		// for (int i = 0; i <= endPort - beginPort; i++) {
		// final Channel channel = ChannelFutures[i].channel();
		// int index = i;
		// channel.closeFuture().addListener(new GenericFutureListener<Future<?
		// super Void>>() {
		//
		// @Override
		// public void operationComplete(Future<? super Void> future) {
		// System.out.println("channel close !");
		// channel.close();
		// ChannelFutures[index] = null;
		// }
		//
		// });
		// }
	}

	public void stopAll() {
		bossGroup.shutdownGracefully();
		workerGroup.shutdownGracefully();
		System.out.println("stoped");
	}

	// 关闭单个端口的NioServerSocketChannel
	public void stopServerChannel(int port) {
		if (STATUS_CACHE.containsKey(port)) {
			STATUS_CACHE.remove(port);
		}
		if (PORT_CACHE.containsKey(port)) {
			for (Entry<String, ChannelContext> v : PORT_CACHE.get(port).entrySet()) {
				v.getValue().getChannel().close();
			}
			PORT_CACHE.remove(port);
			LOG.info("端口" + port + "服务监听关闭成功");
		} else {
			LOG.info("端口" + port + "服务监听未找到");
		}
	}

	public void openServerChannel(int port) {
		YianCoder coder = YianBenCache.getDcoer("yianrule" + port);
		if (coder == null) {
			LOG.error("端口" + port + "未配置编解码规则");
			System.exit(-1);
		}
		LOG.info("请求打开端口" + port + "服务监听");
		if (PORT_CACHE.containsKey(port)) {
			LOG.info("端口" + port + "被占用，开始关闭服务监听");
			stopServerChannel(port);
		}
		STATUS_CACHE.put(port, true);
		Map<String, ChannelContext> tmp = new ConcurrentHashMap<String, ChannelContext>();
		PORT_CACHE.put(port, tmp);
		LOG.info("开始端口" + port + "服务监听");
		ChannelFuture channelFuture = bootstrap.bind(port);
		channelFuture.addListener(new GenericFutureListener<Future<? super Void>>() {
			@Override
			public void operationComplete(Future<? super Void> future) throws Exception {
				if (future.isSuccess()) {
					final Channel channel = channelFuture.channel();
					// ChannelContext channelContext =
					// ChannelContext.build().setChannel(channel).setCoder(coder)
					// .setPort(port);
					// channel.pipeline().addLast("encoder", new
					// YianMsgEncoder(channelContext));
					// channel.pipeline().addLast("decoder", new
					// YianMsgDecoder(channelContext));
					// channel.pipeline().addLast(new YianHandler());
					channel.closeFuture().addListener(new GenericFutureListener<Future<? super Void>>() {
						@Override
						public void operationComplete(Future<? super Void> future) {
							System.out.println("channel close !");
							channel.close();
							PORT_CACHE.remove(port);
						}
					});

					// PORT_CACHE.put(port, channelContext);
					LOG.info("端口" + port + "服务监听成功");
				} else {
					LOG.error("端口" + port + "服务监听失败");
				}
			}
		});
	}

	public void initRedisCache() {
		// String ip = IpUtil.getLocalIp();
		jedisPoolUtil.set("proxy_" + springBaseConfig.getIp(),
				"http://" + springBaseConfig.getIp() + ":" + tomcatPort + "/device/send");
	}
}
