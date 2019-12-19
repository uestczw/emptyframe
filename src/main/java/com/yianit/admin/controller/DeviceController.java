package com.yianit.admin.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.yianit.common.AjaxJsonResponseWraper;
import com.yianit.common.netty.DeviceCache;
import com.yianit.common.util.HttpClientUtil;
import com.yianit.common.util.JedisPoolUtil;
import com.yianit.config.SpringBaseConfig;
import com.yianit.exception.LogisHandleException;
import com.yianit.exception.LogisServiceException;

import feign.Param;
import hl.king.mybatis.spring.common.controller.BaseController;
import hl.king.utils.StringUtil;
import io.netty.channel.Channel;

@RestController
@RequestMapping("/devicec")
public class DeviceController extends BaseController {
	@Autowired
	@Qualifier("jedisPoolUtil")
	private JedisPoolUtil jedisPoolUtil;
	@Autowired
	private SpringBaseConfig springBaseConfig;
	@ResponseBody
	@RequestMapping("/send")
	public Map<String, Object> send(HttpSession session, HttpServletRequest req, HttpServletResponse response,
			Model model, @Param("") String deviceId, @Param("") String msg) throws LogisHandleException {
		Map<String, Object> ret = new HashMap<String, Object>();
		if (!DeviceCache.DEVICE_CHANNEL_CACHE.containsKey(deviceId)) {
			String rd = jedisPoolUtil.get("clientcache." + deviceId);
			if (StringUtil.isEmpty(rd)) {
				ret.put("code", 1);
				ret.put("msg", "设备未连接服务器");
				return ret;
			} else {
				if(rd.equals(springBaseConfig.getIp())){
					sendMsgToDevice(deviceId,msg,ret);
					return ret;
				}
				String path = jedisPoolUtil.get("proxy." + rd);
				LOG.info("设备未连接本地，开始转发到" + path);
				if (StringUtil.isEmpty(path)) {
					ret.put("code", 3);
					ret.put("msg", "设备对应服务器已停机");
					return ret;//
				}
				try {
					Map<String, String> params = new HashMap<String, String>();
					params.put("deviceId", deviceId);
					params.put("msg", msg);
					String retStr = HttpClientUtil.sendHttpClientPost(path, params, "UTF-8");
					ret = JSON.parseObject(retStr, Map.class);
					ret.put("path", path);
					return ret;
				} catch (LogisServiceException e) {
					LOG.error("设备调用转发到" + path + "失败", e);
					ret.put("code", 4);
					ret.put("msg", "服务器转发失败");
					return ret;
				}
			}
		} else {
			LOG.info("设备连接本地，开始调用");
			sendMsgToDevice(deviceId,msg,ret);
			return ret;
		}
	}
	private void sendMsgToDevice(String deviceId,String msg,Map<String, Object> ret){
		Channel cnannel = DeviceCache.DEVICE_CHANNEL_CACHE.get(deviceId);
		if (cnannel != null && cnannel.isOpen() && cnannel.isActive()) {
			cnannel.writeAndFlush(msg);
			ret.put("code", 0);
			ret.put("msg", "成功");
		} else {
			ret.put("code", 2);
			ret.put("msg", "设备已掉线");
		}
	}

	@RequestMapping("/list")
	public Map<String, Object> list(HttpSession session, HttpServletRequest req, HttpServletResponse response,
			Model model, @Param("") String deviceId) throws LogisHandleException {
		// List<String> l = jedisPoolUtil.gets("clientcache.*");
		List<String> l = jedisPoolUtil.keys("clientcache.*");
		List<String> ls = jedisPoolUtil.gets("proxy.*");
		// for(String s:l){
		// jedisPoolUtil.removeKey(s);
		// }
		l.addAll(ls);
		Map<String, Object> ret = AjaxJsonResponseWraper.createSuccessResponseWithData(l);
		return ret;
	}
}
