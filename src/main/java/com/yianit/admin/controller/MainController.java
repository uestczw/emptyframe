package com.yianit.admin.controller;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ThreadPoolExecutor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.yianit.common.AjaxJsonResponseWraper;
import com.yianit.common.netty.ChannelContext;
import com.yianit.common.netty.NettyServer;
import com.yianit.common.netty.NetworkClassLoader;
import com.yianit.common.netty.YianBenCache;
import com.yianit.common.netty.coder.YianCoder;
import com.yianit.common.netty.decoder.YianMsgDecoder;
import com.yianit.common.netty.encoder.YianMsgEncoder;
import com.yianit.common.util.JedisPoolUtil;
import com.yianit.common.util.YianFileUtil;
import com.yianit.config.SpringBaseConfig;
import com.yianit.config.distribute.SendThreadConfig;
import com.yianit.exception.LogisHandleException;
import com.yianit.exception.LogisServiceException;

import hl.king.common.RegisterBean;
import hl.king.mybatis.spring.common.controller.BaseController;
import tk.mybatis.mapper.util.StringUtil;
@Controller
public class MainController extends BaseController{
	@Autowired
	@Qualifier("jedisPoolUtil")
	private JedisPoolUtil jedisPoolUtil;
	@Autowired
	private SpringBaseConfig springBaseConfig;
	@Resource
	private RegisterBean registerBean;
	@Autowired
	private NettyServer nettyServer;
	@Autowired
	@Qualifier("sendThreadService")
	ThreadPoolExecutor sendThreadService;
	@Autowired
	@Qualifier("yianConnectionFactory")
	ConnectionFactory connectionFactory;
	@RequestMapping("/")
	public String index(){
		return "redirect:/main";
	}
	@RequestMapping("/main")
    public String main(){
		List<String> proxys = jedisPoolUtil.keys("proxy.*");
		int ports = 0;
		int channels = 0;
		Map<String,String> m = new HashMap<String,String>();
		for(String key:proxys){
			String ip = key.replace("yianiot.proxy.", "");
			List<String> portsl = jedisPoolUtil.keys("server.lis.port."+ip+".*");			
			for(String v:portsl){
				String p = v.substring(v.lastIndexOf("."));
				if(!m.containsKey(p)){
					m.put(p , p);
					ports++;
				}
				String num = jedisPoolUtil.get(v);
				if(StringUtil.isNotEmpty(num)){
					channels += (Integer.parseInt(num)-1);
				}
			}
		}
		this.setAttribute("proxys", proxys.size());
		this.setAttribute("ports", ports);
		this.setAttribute("channels", channels);
		this.setAttribute("queues", SendThreadConfig.QUEUE.size());
		this.setAttribute("user", "张伟");
    	return "html/main.html";
    }
	@ResponseBody
	@RequestMapping("/thread")
	public Map<String, Object> thread(HttpSession session, HttpServletRequest req, HttpServletResponse response,
			Model model) throws LogisHandleException {
		String size = req.getParameter("size");
		int sizeInt = Integer.parseInt(size);
		((CachingConnectionFactory) connectionFactory).setConnectionLimit(sizeInt);
		((CachingConnectionFactory) connectionFactory).setConnectionCacheSize(sizeInt);
		sendThreadService.setCorePoolSize(sizeInt);
		sendThreadService.setMaximumPoolSize(sizeInt);
		Map<String, Object> ret = AjaxJsonResponseWraper.createSuccessResponseWithData("OK");
		return ret;
	}
}
