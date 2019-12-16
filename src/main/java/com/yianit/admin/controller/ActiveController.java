package com.yianit.admin.controller;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.yianit.common.AjaxJsonResponseWraper;
import com.yianit.common.netty.ChannelContext;
import com.yianit.common.netty.NettyServer;
import com.yianit.common.netty.NetworkClassLoader;
import com.yianit.common.netty.YianBenCache;
import com.yianit.common.netty.coder.YianCoder;
import com.yianit.common.netty.decoder.YianMsgDecoder;
import com.yianit.common.netty.encoder.YianMsgEncoder;
import com.yianit.common.threadpools.SendThreadPool;
import com.yianit.common.util.YianFileUtil;
import com.yianit.exception.LogisHandleException;
import com.yianit.exception.LogisServiceException;

import hl.king.common.RegisterBean;
import hl.king.mybatis.spring.common.controller.BaseController;
import io.netty.channel.ChannelPipeline;

@Controller("activeController")
public class ActiveController extends BaseController {
	@Resource
	private RegisterBean registerBean;
	@Autowired
	private NettyServer nettyServer;

	@RequestMapping("/admin/active/add.do")
	public String add(@RequestParam("wj") MultipartFile[] files, HttpSession session, HttpServletRequest request,
			HttpServletResponse response, Model model) throws LogisServiceException {
		String port = request.getParameter("port");
		for (MultipartFile commonsFile : files) {
			if (commonsFile.isEmpty()) {
				continue;
			}
			System.out.println(commonsFile.getOriginalFilename());
			ApplicationHome h = new ApplicationHome(getClass());
			File jarF = h.getSource();
			if (jarF == null) {
				jarF = h.getDir();
			}
			System.out.println(jarF.getParentFile().toString());
			String f = "yyyyMMddHHmmss";
			SimpleDateFormat simple = new SimpleDateFormat(f);
			String oprtime = simple.format(new Date());
			String tpfname = commonsFile.getOriginalFilename();
			String filename = tpfname.replace(".class", "");
			String packagePath = "/com/yianit/common/netty/coder/";
			//String compilerPath = jarF.getParentFile().toString() + "/activefile/compiler/" + port + "/" + oprtime;
			//String compilerPathClass = compilerPath + packagePath + filename + oprtime + ".class";
			String sourceDir = jarF.getParentFile().toString() + "/activefile/source/" + oprtime;
			String activefile = sourceDir + "/" + tpfname;
			//String activefileCopy = sourceDir + "/" + filename + oprtime + ".java";

			String onlinePath = jarF.getParentFile().toString() + "/activefile/online/" + port;
			//String onlinePathWithPage = onlinePath;
			//String onlineFile = onlinePathWithPage + filename + oprtime + ".class";
			LOG.info(activefile);
			File file = new File(activefile);
			//File cf = new File(compilerPath);
			try {
//				if (!cf.exists()) {
//					cf.mkdirs();
//				}
				if (!file.getParentFile().exists()) {
					file.getParentFile().mkdirs();
				}
				commonsFile.transferTo(file);
			} catch (IllegalStateException | IOException e) {
				throw new LogisServiceException("上传文件失败!");
			}
			try {
//				String s = YianFileUtil.readString2(activefile);
//				s = s.replace(tpfname.replace(".java", ""), tpfname.replace(".java", "") + oprtime);
//				YianFileUtil.write(activefileCopy, s);
//				JavaCompiler javac = ToolProvider.getSystemJavaCompiler();
				String path = "com.yianit.common.netty.coder." + filename;
//				int status = javac.run(null, null, null, "-d", compilerPath, activefileCopy);
//				if (status != 0) {
//					LOG.error("编解码规则[" + activefile + "]编译失败！");
//				}
				NetworkClassLoader ncl = new NetworkClassLoader(activefile, YianCoder.class.getClassLoader());
				Class clazz;
				YianCoder newInstance = null;
				try {
					clazz = ncl.loadClass(path);
					newInstance = (YianCoder) clazz.newInstance();
					YianBenCache.addCoder("yianrule" + port, newInstance);
					YianFileUtil.deleteFileAll(new File(onlinePath));
					System.out.println("复制文件"+activefile+"到"+onlinePath);
					YianFileUtil.copyFile(activefile, onlinePath);
					LOG.error("注册" + port + "编解码规则[" + activefile + "]成功,版本号:" + newInstance.getVersion());
				} catch (Exception e) {
					LOG.error("注册" + port + "编解码规则[" + activefile + "]失败", e);
				}
				if (NettyServer.PORT_CACHE.containsKey(Integer.parseInt(port))) {
//					YianMsgEncoder encoder = NettyServer.PORT_ENCODER_CACHE.get(Integer.parseInt(port));
//					YianMsgDecoder decoder = NettyServer.PORT_DECODER_CACHE.get(Integer.parseInt(port));
					YianMsgEncoder encoder = null;
					YianMsgDecoder decoder = null;
					for(Entry<String,ChannelContext> v:NettyServer.PORT_CACHE.get(Integer.parseInt(port)).entrySet()){
						encoder = v.getValue().getYianMsgEncoder();
						decoder = v.getValue().getYianMsgDecoder();
						if(encoder!=null){
							encoder.updateCoder(newInstance);
						}
						if(decoder!=null){
							decoder.updateCoder(newInstance);
						}
					}
				} else {
					nettyServer.openServerChannel(Integer.parseInt(port));
				}
			} catch (Exception e) {
				throw new LogisServiceException("文件解析失败!", e);
			}
		}
		return "admin/user.html";
	}
	@ResponseBody
	@RequestMapping("/stop")
    public Map<String, Object> stop(HttpSession session, HttpServletRequest req, HttpServletResponse response, Model model)
            throws LogisHandleException {
		nettyServer.stopServerChannel(7997);
		Map<String, Object> ret = AjaxJsonResponseWraper.createSuccessResponseWithData("ssss");
        return ret;
    }
	@ResponseBody
	@RequestMapping("/rabbit")
    public Map<String, Object> rabbit(HttpSession session, HttpServletRequest req, HttpServletResponse response, Model model)
            throws LogisHandleException {
		String s = "netty:"+NettyServer.QUEUE.size()+",rabbit:"+SendThreadPool.QUEUE.size();
		Map<String, Object> ret = AjaxJsonResponseWraper.createSuccessResponseWithData(s);
        return ret;
    }
	
	@ResponseBody
	@RequestMapping("/thread")
    public Map<String, Object> thread(HttpSession session, HttpServletRequest req, HttpServletResponse response, Model model)
            throws LogisHandleException {
		String size = req.getParameter("size");
		SendThreadPool.SERVICE.setCorePoolSize(Integer.parseInt(size));
		SendThreadPool.SERVICE.setMaximumPoolSize(Integer.parseInt(size));
		Map<String, Object> ret = AjaxJsonResponseWraper.createSuccessResponseWithData("OK");
        return ret;
    }
}
