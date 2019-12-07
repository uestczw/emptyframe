package com.yianit.common.util;

import java.util.List;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.yianit.exception.LogisServiceException;
import com.yianit.model.AccessUser;

import tk.mybatis.mapper.util.StringUtil;

@Component("tokenUtil")
public class TokenUtil {
	private static final Logger LOGGER = LoggerFactory.getLogger(TokenUtil.class);
	@Autowired
	@Qualifier("jedisPoolUtil")
	private JedisPoolUtil jedisPoolUtil;

	public String loadToken(HttpServletRequest request, String user_type) {
		Cookie[] cookies = request.getCookies();
		String value = null;
		if (cookies == null) {
			return null;
		}
		for (Cookie cookie : cookies) {
			if (StringUtil.isNotEmpty(cookie.getName())
					&& (user_type + "_zenitoo_save_token").equals(cookie.getName())) {
				value = cookie.getValue();
				if (StringUtil.isNotEmpty(value)) {
					try {
						value = EncrypAES.Decryptor("1567892", value, EncrypAES.ENC_TYPE_ENUM.AES.id);
					} catch (LogisServiceException e) {
						LOGGER.error("token解析失败", e);
						value = null;
					}
				}
			}
		}
		return value;
	}

	public String loadTokenVersion(HttpServletRequest request, String user_type) {
		Cookie[] cookies = request.getCookies();
		String value = null;
		if (cookies == null) {
			return null;
		}
		for (Cookie cookie : cookies) {
			if (StringUtil.isNotEmpty(cookie.getName())
					&& (user_type + "_zenitoo_save_token_version").equals(cookie.getName())) {
				value = cookie.getValue();
				if (StringUtil.isNotEmpty(value)) {
					try {
						value = EncrypAES.Decryptor("1567892", value, EncrypAES.ENC_TYPE_ENUM.AES.id);
					} catch (LogisServiceException e) {
						LOGGER.error("tokenVersion解析失败", e);
						value = null;
					}
				}
			}
		}
		return value;
	}

	public boolean addToken(HttpServletResponse response, String value, String version, String path, String user_type) {
		try {
			HttpClientUtil.addCookie(user_type + "_zenitoo_save_token",
					EncrypAES.Encrytor("1567892", value, EncrypAES.ENC_TYPE_ENUM.AES.id), response, path);
			HttpClientUtil.addCookie(user_type + "_zenitoo_save_token_version",
					EncrypAES.Encrytor("1567892", version, EncrypAES.ENC_TYPE_ENUM.AES.id), response, path);
		} catch (Exception e) {
			LOGGER.error("添加token失败", e);
			return false;
		}
		return true;
	}

	public boolean removeToken(HttpServletRequest request, HttpServletResponse response) {
		try {
			HttpClientUtil.delCookie(request.getCookies(), response, "zenitoo_save_token",
					"zenitoo_save_token_version");
		} catch (Exception e) {
			LOGGER.error("删除token失败", e);
			return false;
		}
		return true;
	}

	// user_type 0客户1商家2渠道3平台4销售
	public void addAccessUser(HttpServletRequest request, HttpServletResponse response, String type, String key,
			AccessUser accessUser, boolean onlyone) {
		String path = "/";
		String user_type = getUserType(type);
		try {
			List<String> tmps = jedisPoolUtil.keys("login_token_" + user_type + "_" + key + "_*");
			String token_value = null;
			if (tmps.size() > 0) {
				if (!onlyone) {
					// 登录不挤出已登录用户
					token_value = tmps.get(0).replace("login_token_", "").replace("_version", "");
				} else {
					// 登录挤出已登录用户
					token_value = tmps.get(0).replace("_version", "");
					jedisPoolUtil.removeKey(token_value);
					jedisPoolUtil.removeKey(token_value + "_version");
					token_value = user_type + "_" + key + "_" + UUID.randomUUID().toString();
				}
			} else {
				token_value = user_type + "_" + key + "_" + UUID.randomUUID().toString();
			}
			String version = System.currentTimeMillis() + "";
			jedisPoolUtil.set("login_token_" + token_value, JSON.toJSONString(accessUser));
			jedisPoolUtil.getSet("login_token_" + token_value + "_version", version);
			addToken(response, token_value, version, path, user_type);
		} catch (Exception e) {
			LOGGER.error("缓存用户登录信息失败", e);
			String token_value = user_type + "_" + key + "_" + UUID.randomUUID().toString();
			String version = System.currentTimeMillis() + "";
			addToken(response, token_value, version, path, user_type);
		}
		request.getSession().setAttribute(type, accessUser);
	}

	public void removeAccessUser(HttpServletRequest request, HttpServletResponse response, String type) {
		String user_type = getUserType(type);
		String token = loadToken(request, user_type);
		try {
			jedisPoolUtil.removeKey("login_token_" + token);
			jedisPoolUtil.removeKey("login_token_" + token + "_version");
		} catch (Exception e) {
			LOGGER.error("退出删除缓存数据失败", e);
		}
		removeToken(request, response);
	}

	public void updateAccessUser(HttpServletRequest request, String type, AccessUser accessUser) {
		String user_type = getUserType(type);
		String token = loadToken(request, user_type);
		if (StringUtil.isNotEmpty(token)) {
			try {
				jedisPoolUtil.set("login_token_" + token, JSON.toJSONString(accessUser));
			} catch (Exception e) {
				LOGGER.error("更新用户缓存失败", e);
			}
		}
	}

	public AccessUser loadAccessUser(HttpServletRequest request, String type) {
		String user_type = getUserType(type);
		AccessUser accessUser = null;
		String token = loadToken(request, user_type);
		if (StringUtil.isNotEmpty(token)) {
			try {
				String tvv = jedisPoolUtil.get("login_token_" + token + "_version");
				String version = loadTokenVersion(request, user_type);
				if (StringUtil.isNotEmpty(version) && StringUtil.isNotEmpty(tvv) && version.equals(tvv)) {
					accessUser = (request.getSession().getAttribute(type) == null)
							|| !(request.getSession().getAttribute(type) instanceof AccessUser) ? null
									: (AccessUser) request.getSession().getAttribute(type);
					if (accessUser != null) {
						return accessUser;
					}
				}
				try {
					String tv = jedisPoolUtil.get("login_token_" + token);
					if (StringUtil.isNotEmpty(tv)) {
						accessUser = JSON.parseObject(tv, AccessUser.class);
						request.getSession().setAttribute(type, accessUser);
					}
				} catch (Exception e) {
					LOGGER.error("token:" + token + "对应用户信息解析失败", e);
					accessUser = null;
				}
			} catch (Exception e) {
				LOGGER.error("缓存加载登录信息失败", e);
//				String[] keys = token.split("_");
//				String key = keys[1];
//				String cpx = request.getContextPath();
			}
		}
		return accessUser;
	}

	public AccessUser initAccessUser(HttpServletRequest request) {
		String accessId = null;
		String accessIp = request.getRemoteAddr();
		AccessUser accessUser = new AccessUser(accessId, accessIp, request.getSession().getId());
		return accessUser;
	}

	private String getUserType(String type) {
		String user_type = "1";
		return user_type;
	}
}
