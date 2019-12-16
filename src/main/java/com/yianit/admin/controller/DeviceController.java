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
import org.springframework.web.bind.annotation.RestController;

import com.yianit.common.AjaxJsonResponseWraper;
import com.yianit.common.netty.DeviceCache;
import com.yianit.common.util.HttpClientUtil;
import com.yianit.common.util.JedisPoolUtil;
import com.yianit.exception.LogisHandleException;
import com.yianit.exception.LogisServiceException;

import feign.Param;
import hl.king.mybatis.spring.common.controller.BaseController;
import hl.king.utils.StringUtil;
import io.netty.channel.Channel;

@RestController
@RequestMapping("/device")
public class DeviceController extends BaseController {
	@Autowired
	@Qualifier("jedisPoolUtil")
	private JedisPoolUtil jedisPoolUtil;

	@RequestMapping("/send")
	public String send(HttpSession session, HttpServletRequest req, HttpServletResponse response, Model model,
			@Param("") String deviceId) throws LogisHandleException {
		Map<String, Object> ret = null;
		if (!DeviceCache.DEVICE_CHANNEL_CACHE.containsKey(deviceId)) {
			String rd = jedisPoolUtil.get("clientcache_" + deviceId);
			if (StringUtil.isEmpty(rd)) {
				return "1";// 设备未连接服务器
			} else {
				Map<String, String> params = new HashMap<String, String>();
				params.put("deviceId", deviceId);
				String path = jedisPoolUtil.get("proxy_" + rd);
				LOG.info("设备未连接本地，开始转发到" + path);
				if (StringUtil.isEmpty(path)) {
					return "3";// 设备对应服务器已停机
				}
				try {
					return HttpClientUtil.sendHttpClientPost(path, params, "UTF-8");
				} catch (LogisServiceException e) {
					LOG.error("设备调用转发到" + path + "失败", e);
					return "4";// 服务器转发失败
				}
			}
		} else {
			LOG.info("设备连接本地，开始调用");
			Channel cnannel = DeviceCache.DEVICE_CHANNEL_CACHE.get(deviceId);
			if (cnannel != null && cnannel.isOpen() && cnannel.isActive()) {
				cnannel.writeAndFlush("您被调用");
				return "0";// 成功
			} else {
				return "2";// 设备已掉线
			}
		}
	}

	@RequestMapping("/list")
	public Map<String, Object> list(HttpSession session, HttpServletRequest req, HttpServletResponse response,
			Model model, @Param("") String deviceId) throws LogisHandleException {
		// List<String> l = jedisPoolUtil.gets("clientcache_*");
		List<String> l = jedisPoolUtil.keys("clientcache_*");
		List<String> ls = jedisPoolUtil.gets("proxy_*");
		// for(String s:l){
		// jedisPoolUtil.removeKey(s);
		// }
		l.addAll(ls);
		Map<String, Object> ret = AjaxJsonResponseWraper.createSuccessResponseWithData(l);
		return ret;
	}
}
