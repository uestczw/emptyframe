package com.yianit.intercept;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.alibaba.fastjson.JSON;
import com.yianit.common.util.TokenUtil;
import com.yianit.model.AccessUser;

import hl.king.common.AjaxJsonResponseWraper;

/**
 * 用户登录权限拦截
 * 
 * @author HL.King
 *
 */
@Component
public class AdminAccessIntercept extends HandlerInterceptorAdapter {
	@Autowired
	@Qualifier("tokenUtil")
	private TokenUtil tokenUtil;
	private static final Logger LOG = LoggerFactory.getLogger(AdminAccessIntercept.class);

	private String	noPowerUrl	= "";
	private String	noLoginUrl	= "/login";

	public void setNoPowerUrl(String noPowerUrl) {
		this.noPowerUrl = noPowerUrl;
	}

	public void setNoLoginUrl(String noLoginUrl) {
		this.noLoginUrl = noLoginUrl;
	}

	/**
	 * 处理需要登录的请求
	 * 
	 * @param request
	 * @param response
	 */
	private void needLoginHandler(HttpServletRequest request, HttpServletResponse response, String url) {
		try {
			if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
				response.getOutputStream().write(JSON.toJSONString(AjaxJsonResponseWraper.createTimeOutResponse(null)).getBytes("UTF-8"));
			} else {
				response.sendRedirect(request.getContextPath() + url);
			}
		} catch (IOException e) {
			LOG.error("", e);
		}
	}

	// 请求处理之前进行调用
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		// 判断登录用户
		LOG.debug("Request URI:{},{}", request.getRequestURI(), handler);

		AccessUser user = tokenUtil.loadAccessUser(request, "user_normal");
		if (user == null||!user.isHasLogin()) {
			needLoginHandler(request, response, noLoginUrl);
			return false;
		}
		// 目前不验证功能按钮
		// if (request.getRequestURI().endsWith(".json")) {
		// return true;
		// }
		// 判断登录用户菜单权限
//		Map<String, Menu> menus = user.getMenuUrls();
//		if (menus == null || !menus.containsKey(request.getRequestURI()) || (menus.get(request.getRequestURI()) != null && Boolean.FALSE.equals(menus.get(request.getRequestURI()).getActivity()))) {
//			needLoginHandler(request, response, noPowerUrl);
//			return false;
//		}
		return true;
	}

	// 请求处理之后
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
		// 注入工程路径
		if (modelAndView != null && modelAndView.getView() != null) {
			modelAndView.addObject("cpx", request.getContextPath());
		}
	}
}
