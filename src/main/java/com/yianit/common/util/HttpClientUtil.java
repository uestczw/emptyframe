package com.yianit.common.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.ssl.SSLContexts;
import org.apache.log4j.Logger;

import com.yianit.common.Concans;
import com.yianit.exception.LogisServiceException;

/**
 * 类名称: HttpClientUtil 类描述: http请求工具类. 创建人: zhangwei 创建时间: 2015年9月9日 下午2:46:38
 * 修改人: zhangwei 修改时间: 2015年9月9日 下午2:46:38 修改备注:
 */

public class HttpClientUtil {
	public HttpClientUtil() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * sendHttpClientPost:(post访问). <br/>
	 * 
	 * @author zhangwei
	 * @param path
	 * @param params
	 * @param encode
	 * @return
	 * @since JDK 1.7
	 */
	private static final Logger LOG = Logger.getLogger(HttpClientUtil.class);

	public static String sendHttpClientPost(String path, Map<String, String> params, String encode)
			throws LogisServiceException {
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		if (null != params) {
			for (Map.Entry<String, String> entry : params.entrySet()) {
				list.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
			}
		}
		DefaultHttpClient client = null;
		try {
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, encode);
			HttpPost httpPost = new HttpPost(path);
			httpPost.setEntity(entity);
			client = new DefaultHttpClient();
			client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 60000);
			client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 60000);
			HttpResponse httpResponse = client.execute(httpPost);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				return changeInputStream(httpResponse.getEntity().getContent(), encode);
			} else {
				throw new LogisServiceException("请求失败status:" + httpResponse.getStatusLine().getStatusCode());
			}
		} catch (Exception e) {
			throw new LogisServiceException(e);
		} finally {
			if (null != client) {
				client.close();
			}
		}
		// return "";
	}

	/**
	 * changeInputStream:(处理返回结果). <br/>
	 * 
	 * @author zhangwei
	 * @param inputStream
	 * @param encode
	 * @return
	 * @since JDK 1.7
	 */

	private static String changeInputStream(InputStream inputStream, String encode) {
		// TODO Auto-generated method stub
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		int len = 0;
		byte[] date = new byte[1024];
		String result = "";
		try {
			while ((len = inputStream.read(date)) != -1) {
				outputStream.write(date, 0, len);
			}
			result = new String(outputStream.toByteArray(), encode);
			return result;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static String getRemortIP(HttpServletRequest request) {
		String ipAddress = null;
		// ipAddress = this.getRequest().getRemoteAddr();
		ipAddress = request.getHeader("x-forwarded-for");
		if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getHeader("Proxy-Client-IP");
		}
		if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getRemoteAddr();
			if (ipAddress.equals("127.0.0.1")) {
				// 根据网卡取本机配置的IP
				InetAddress inet = null;
				try {
					inet = InetAddress.getLocalHost();
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
				if (inet == null) {
					return "0.0.0.0";
				}
				ipAddress = inet.getHostAddress();
			}

		}

		// 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
		if (ipAddress != null && ipAddress.length() > 15) { // "***.***.***.***".length()
															// = 15
			if (ipAddress.indexOf(",") > 0) {
				ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
			}
		}
		return ipAddress;
	}

	public static void addCookie(String name, String value, HttpServletResponse response) {
		Cookie cookie = new Cookie(name, value);
		cookie.setMaxAge(Concans.COOKIE_OUT_TIME);
		cookie.setPath(Concans.COOKIE_DEF_PATH);
		response.addCookie(cookie);
	}
	public static void addCookie(String name, String value, HttpServletResponse response,String path) {
		Cookie cookie = new Cookie(name, value);
		cookie.setMaxAge(Concans.COOKIE_OUT_TIME);
		cookie.setPath(path);
		response.addCookie(cookie);
	}

	public static void delCookie(Cookie[] cookies, HttpServletResponse response, String... names) {
		Map<String, String> name_m = new HashMap<String, String>();
		for (String name : names) {
			name_m.put(name, name);
		}
		if (null != cookies) {
			for (Cookie cookie : cookies) {
				if (name_m.containsKey(cookie.getName())) {
					cookie.setMaxAge(0);
					cookie.setPath("/");
					response.addCookie(cookie);
				}
			}
		}
	}

	public static String sendHttpClientGet(String path, Map<String, String> params, String encode)
			throws LogisServiceException {
		if (null != params) {
			for (Map.Entry<String, String> entry : params.entrySet()) {
				if (path.indexOf("?") >= 0) {
					path += "&" + entry.getKey() + "=" + entry.getValue();
				} else {
					path += "?" + entry.getKey() + "=" + entry.getValue();
				}
			}
		}
		DefaultHttpClient client = null;
		try {
			HttpGet httpPost = new HttpGet(path);
			client = new DefaultHttpClient();
			client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 60000);
			client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 60000);
			HttpResponse httpResponse = client.execute(httpPost);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				return changeInputStream(httpResponse.getEntity().getContent(), encode);
			} else {
				throw new LogisServiceException("请求失败status:" + httpResponse.getStatusLine().getStatusCode());
			}
		} catch (Exception e) {
			throw new LogisServiceException(e);
		} finally {
			if (null != client) {
				client.close();
			}
		}
		// return "";
	}

	public static String sendHttpClientPostStream(String path, Map<String, String> params, String data, String encode)
			throws LogisServiceException {
		if (null != params) {
			for (Map.Entry<String, String> entry : params.entrySet()) {
				if (path.indexOf("?") >= 0) {
					path += "&" + entry.getKey() + "=" + entry.getValue();
				} else {
					path += "?" + entry.getKey() + "=" + entry.getValue();
				}
			}
		}
		DefaultHttpClient client = null;
		try {
			StringEntity entity = new StringEntity(data, encode);
			entity.setContentType("application/json");
			HttpPost httpPost = new HttpPost(path);
			httpPost.setEntity(entity);
			client = new DefaultHttpClient();
			client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 60000);
			client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 60000);
			HttpResponse httpResponse = client.execute(httpPost);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				return changeInputStream(httpResponse.getEntity().getContent(), encode);
			} else {
				throw new LogisServiceException("请求失败status:" + httpResponse.getStatusLine().getStatusCode());
			}
		} catch (Exception e) {
			throw new LogisServiceException(e);
		} finally {
			if (null != client) {
				client.close();
			}
		}
		// return "";
	}

	public static String sendHttpClientPostStream(String path, Map<String, String> params, String data, String encode,
			String mchId, String certPath) throws Exception {
		// 证书密码，默认为商户ID
		String key = mchId;
		// 指定读取证书格式为PKCS12
		KeyStore keyStore = KeyStore.getInstance("PKCS12");
		// 读取本机存放的PKCS12证书文件
		//ClassPathResource cp = new ClassPathResource(certPath);
		InputStream instream = new FileInputStream(new File(certPath));//cp.getInputStream();
		try {
			// 指定PKCS12的密码(商户ID)
			keyStore.load(instream, key.toCharArray());
		} finally {
			instream.close();
		}
		SSLContext sslcontext = SSLContexts.custom().loadKeyMaterial(keyStore, key.toCharArray()).build();
		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext, new String[] { "TLSv1" }, null,
				SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
		CloseableHttpClient client = HttpClients.custom().setSSLSocketFactory(sslsf).build();
		if (null != params) {
			for (Map.Entry<String, String> entry : params.entrySet()) {
				if (path.indexOf("?") >= 0) {
					path += "&" + entry.getKey() + "=" + entry.getValue();
				} else {
					path += "?" + entry.getKey() + "=" + entry.getValue();
				}
			}
		}
		try {
			StringEntity entity = new StringEntity(data, encode);
			entity.setContentType("application/json");
			HttpPost httpPost = new HttpPost(path);
			httpPost.setEntity(entity);
			client = new DefaultHttpClient();
			client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 60000);
			client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 60000);
			HttpResponse httpResponse = client.execute(httpPost);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				return changeInputStream(httpResponse.getEntity().getContent(), encode);
			} else {
				throw new LogisServiceException("请求失败status:" + httpResponse.getStatusLine().getStatusCode());
			}
		} catch (Exception e) {
			throw new LogisServiceException(e);
		} finally {
			if (null != client) {
				client.close();
			}
		}
	}

	public static void main(String[] args) throws LogisServiceException {
		Map p = new HashMap();
		// String s = HttpClientUtil.sendHttpClientPost(
		// "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=wx6ba3425a386a6c58&secret=807e2c0d9291c2198513280d10930973",
		// p, "UTF-8");
		String access_token = "8_7wZXJ7m34KOAH209hOC7vERGc841TyN13sNelAbX-HgtHLCkudsqMdCA7BGvfpTzCj72_iNeBYCd8cgAg2D_sSmlcyd61MPllMjkrEaVdGV4NFz4jupuzEpDzwMND-BurRGqGYul6A4oTP42QRBjAGACDN";
		String t = HttpClientUtil.sendHttpClientPost(
				"https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=" + access_token + "&type=jsapi", p,
				"UTF-8");
		String ticket = "HoagFKDcsGMVCIY2vOjf9hiNlJsTCyNDoDVuw3Xpoz6_MfAgrOGGFBqOJSkJbEYFpeyzUY13Qu0TpNr1sT_s8Q";
		System.out.println(t);
	}
}
