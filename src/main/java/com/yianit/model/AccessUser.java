package com.yianit.model;

import java.io.Serializable;
import java.util.Map;
import java.util.UUID;

import com.yianit.common.util.MD5Util;
import com.yianit.exception.UtilException;

/**
 * 类名称: AccessUser 类描述: 登录信息. 创建人: zhangwei 创建时间: 2015年9月9日 下午2:39:43 修改人:
 * zhangwei 修改时间: 2015年9月9日 下午2:39:43 修改备注:
 */

public class AccessUser implements Serializable {

	/**
	 * serialVersionUID:TODO(用一句话描述这个变量表示什么).
	 * 
	 * @since JDK 1.7
	 */

	private static final long serialVersionUID = -5799374490610509921L;
	private String sessionId;
	private String accessId;
	private String accessIp;
	// private I_UR_DM_YH ur;
	private boolean hasLogin = false;
	private Integer loginId;
	private String loginPassword;
	private String accessUuid;
	private Map<String, Object> menu;
	private String mail;
	private String token;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public AccessUser() {
		accessUuid = UUID.randomUUID().toString();
	}

	public AccessUser(String accessId, String accessIp, String sessionId) {
		this.accessId = accessId;
		this.accessIp = accessIp;
		accessUuid = UUID.randomUUID().toString();
		this.sessionId = sessionId;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getAccessId() {
		return accessId;
	}

	public void setAccessId(String accessId) {
		this.accessId = accessId;
	}

	public String getAccessIp() {
		return accessIp;
	}

	public void setAccessIp(String accessIp) {
		this.accessIp = accessIp;
	}

	public boolean isHasLogin() {
		return hasLogin;
	}

	public void setHasLogin(boolean hasLogin) {
		this.hasLogin = hasLogin;
	}

	public Integer getLoginId() {
		return loginId;
	}

	public void setLoginId(Integer loginId) {
		this.loginId = loginId;
	}

	public String getLoginPassword() {
		return loginPassword;
	}

	public void setLoginPassword(String loginPassword) {
		this.loginPassword = loginPassword;
	}

	/**
	 * getAccessUuid:(获取登录key). <br/>
	 * 
	 * @author zhangwei
	 * @return
	 * @since JDK 1.7
	 */

	public String getAccessUuid() {
		String cloneAccessUuid = accessUuid;
		String newAccessUuid = null;
		try {
			newAccessUuid = MD5Util.md5Encode(accessUuid, "");
		} catch (UtilException e) {

		}
		accessUuid = newAccessUuid;
		return cloneAccessUuid;
	}

	public void setAccessUuid(String accessUuid) {
		this.accessUuid = accessUuid;
	}

	/**
	 * checkAccess:(验证登录key). <br/>
	 * 
	 * @author zhangwei
	 * @param accessUuid
	 * @throws Exception
	 * @since JDK 1.7
	 */

	public void checkAccess(String accessUuid) throws Exception {
		if (!this.accessUuid.equals(MD5Util.md5Encode(accessUuid, ""))) {
			throw new Exception("");
		}
	}

	public Map<String, Object> getMenu() {
		return menu;
	}

	public void setMenu(Map<String, Object> menu) {
		this.menu = menu;
	}

	/**
	 * main:(测试). <br/>
	 * 
	 * @author zhangweiF
	 * @param args
	 * @since JDK 1.7
	 */

	public static void main(String[] args) {
		AccessUser accessUser = new AccessUser();
		for (int i = 0; i < 10; i++) {
			String uuid = accessUser.getAccessUuid();
			try {
				accessUser.checkAccess(uuid);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
