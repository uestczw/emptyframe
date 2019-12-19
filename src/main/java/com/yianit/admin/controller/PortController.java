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

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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
public class PortController extends BaseController{
	@Autowired
	@Qualifier("jedisPoolUtil")
	private JedisPoolUtil jedisPoolUtil;
	@Autowired
	private SpringBaseConfig springBaseConfig;
	@Resource
	private RegisterBean registerBean;
	@Autowired
	private NettyServer nettyServer;
	
	@RequestMapping("/port")
    public String port(){
    	return "html/ports.html";
    }
	
	@ResponseBody
    @RequestMapping("/port/list")
    public Map<String, Object> list(HttpSession session, HttpServletRequest req, HttpServletResponse response, Model model)
            throws LogisHandleException {
		List<String> portsl = jedisPoolUtil.keys("server.lis.port."+springBaseConfig.getIp()+".*");
		List<Map<String,Object>> data = new ArrayList<Map<String,Object>>();
		for(String s:portsl){
			String p = s.substring(s.lastIndexOf(".")+1);
			YianCoder coder = YianBenCache.getDcoer("yianrule" + p);
			String num = jedisPoolUtil.get(s);
			Map<String,Object> m = new HashMap<String,Object>();
			m.put("port", p);
			m.put("coderName", coder.getClass().getName());
			m.put("coderVer", coder.getVersion());
			num = StringUtil.isEmpty(num)?"1":num;
			m.put("channels", Integer.parseInt(num)-1);
			data.add(m);
		}
    	Map<String, Object> ret = AjaxJsonResponseWraper.createSuccessResponseWithData(data);
        return ret;
    }
	@RequestMapping("/port/add")
    public String add(){
		this.setAttribute("port", this.getParameter("port"));
    	return "html/addPort.html";
    }
	
	@RequestMapping("/port/addSubmit")
	public String add(@RequestParam("wj") MultipartFile[] files, HttpSession session, HttpServletRequest request,
			HttpServletResponse response, Model model) throws LogisServiceException {
		String port = request.getParameter("port");
		for (MultipartFile commonsFile : files) {
			if (commonsFile.isEmpty()) {
				this.setAttribute("error","请上传文件!");
				return "html/addPort.html";
			}
			LOG.debug(commonsFile.getOriginalFilename());
			ApplicationHome h = new ApplicationHome(getClass());
			File jarF = h.getSource();
			if (jarF == null) {
				jarF = h.getDir();
			}
			LOG.debug(jarF.getParentFile().toString());
			String f = "yyyyMMddHHmmss";
			SimpleDateFormat simple = new SimpleDateFormat(f);
			String oprtime = simple.format(new Date());
			String tpfname = commonsFile.getOriginalFilename();
			if(!tpfname.endsWith(".class")){
				this.setAttribute("error","请上传class文件!");
				return "html/addPort.html";
			}
			String filename = tpfname.replace(".class", "");
			String sourceDir = jarF.getParentFile().toString() + "/activefile/source/" + oprtime;
			String activefile = sourceDir + "/" + tpfname;
			String onlinePath = jarF.getParentFile().toString() + "/activefile/online/" + port;
			LOG.info(activefile);
			File file = new File(activefile);
			try {
				if (!file.getParentFile().exists()) {
					file.getParentFile().mkdirs();
				}
				commonsFile.transferTo(file);
			} catch (IllegalStateException | IOException e) {
				//throw new LogisServiceException("上传文件失败!");
				this.setAttribute("error","上传文件失败!");
				return "html/addPort.html";
			}
			try {
				String path = "com.yianit.common.netty.coder." + filename;
				NetworkClassLoader ncl = new NetworkClassLoader(activefile, YianCoder.class.getClassLoader());
				Class clazz;
				YianCoder newInstance = null;
				try {
					clazz = ncl.loadClass(path);
					newInstance = (YianCoder) clazz.newInstance();
					YianBenCache.addCoder("yianrule" + port, newInstance);
					YianFileUtil.deleteFileAll(new File(onlinePath));
					System.out.println("复制文件" + activefile + "到" + onlinePath);
					YianFileUtil.copyFile(activefile, onlinePath);
					LOG.info("注册" + port + "编解码规则[" + activefile + "]成功,版本号:" + newInstance.getVersion());
				} catch (Exception e) {
					LOG.error("注册" + port + "编解码规则[" + activefile + "]失败", e);
					//throw new LogisServiceException("注册" + port + "编解码规则[" + activefile + "]失败");
					this.setAttribute("error","注册" + port + "编解码规则[" + activefile + "]失败");
					return "html/addPort.html";
				}
				if (NettyServer.PORT_CACHE.containsKey(Integer.parseInt(port))) {
					YianMsgEncoder encoder = null;
					YianMsgDecoder decoder = null;
					for (Entry<String, ChannelContext> v : NettyServer.PORT_CACHE.get(Integer.parseInt(port))
							.entrySet()) {
						encoder = v.getValue().getYianMsgEncoder();
						decoder = v.getValue().getYianMsgDecoder();
						if (encoder != null) {
							encoder.updateCoder(newInstance);
						}
						if (decoder != null) {
							decoder.updateCoder(newInstance);
						}
					}
				} else {
					nettyServer.openServerChannel(Integer.parseInt(port));
				}
			} catch (Exception e) {
				//throw new LogisServiceException("文件解析失败!", e);
				this.setAttribute("error", "文件解析失败!");
				return "html/addPort.html";
			}
		}
		return "redirect:/port";
	}
	
	@ResponseBody
	@RequestMapping("/port/stop")
    public Map<String, Object> stop(HttpSession session, HttpServletRequest req, HttpServletResponse response, Model model)
            throws LogisHandleException {
		String port = this.getParameter("port");
		nettyServer.stopServerChannel(Integer.parseInt(port));
		ApplicationHome h = new ApplicationHome(getClass());
		File jarF = h.getSource();
		if (jarF == null) {
			jarF = h.getDir();
		}
		String onlinePath = jarF.getParentFile().toString() + "/activefile/online/" + port;
		YianFileUtil.deleteFileAll(new File(onlinePath));
		Map<String, Object> ret = AjaxJsonResponseWraper.createSuccessResponseWithData("OK");
        return ret;
    }
}
