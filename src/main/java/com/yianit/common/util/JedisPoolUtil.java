package com.yianit.common.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.yianit.config.RedisConfig;

import redis.clients.jedis.GeoRadiusResponse;
import redis.clients.jedis.GeoUnit;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;
import tk.mybatis.mapper.util.StringUtil;

/**
 * 类名称: JedisPoolUtil 类描述: jedis连接 获取 释放工具. 创建人: zhangwei 创建时间: 2015年9月9日
 * 下午2:46:55 修改人: zhangwei 修改时间: 2015年9月9日 下午2:46:55 修改备注:
 */
@Component("jedisPoolUtil")
public class JedisPoolUtil {
	private final Logger LOG = Logger.getLogger(JedisPoolUtil.class);
	private String prefex = "";
	@Resource
	private JedisPool POOL;
	@Resource
	private RedisConfig redisConfig;

	/**
	 * 在多线程环境同步初始化
	 */
	private synchronized void poolInit() {
		if (POOL == null) {

		}
	}

	public String formatKey(String key) {
		if (StringUtil.isNotEmpty(redisConfig.getPrefex()) && !key.startsWith(redisConfig.getPrefex())) {
			key = redisConfig.getPrefex() + "." + key;
		}
		return key;
	}

	/**
	 * 获取一个jedis 对象
	 * 
	 * @return
	 */
	public Jedis getJedis() {

		if (POOL == null) {
			poolInit();
		}
		return POOL.getResource();
	}

	/**
	 * 归还一个连接
	 * 
	 * @param jedis
	 */
	public void returnRes(Jedis jedis) {
		jedis.close();
	}

	public boolean set(String key, String value) {
		key = formatKey(key);
		Jedis j = null;
		try {
			j = getJedis();
			j.set(key, value);
		} catch (Exception e) {
			LOG.error("保存redis数据失败", e);
			return false;
		} finally {
			returnRes(j);
		}
		return true;
	}

	public String get(String key) {
		key = formatKey(key);
		Jedis j = null;
		try {
			j = getJedis();
			return j.get(key);
		} catch (Exception e) {
			LOG.error("获取redis数据失败", e);
			return null;
		} finally {
			returnRes(j);
		}
	}

	public boolean removeKey(String key) {
		key = formatKey(key);
		Jedis j = null;
		try {
			j = getJedis();
			j.expireAt(key, 0);
			j.del(key);
			return true;
		} catch (Exception e) {
			LOG.error("获取redis数据失败", e);
			return false;
		} finally {
			returnRes(j);
		}
	}

	public List<String> gets(String key) {
		key = formatKey(key);
		Jedis j = null;
		try {
			j = getJedis();
			Set<String> sr = j.keys(key);
			if (sr == null || sr.size() == 0) {
				return new ArrayList<String>();
			}
			String[] s = new String[sr.size()];
			sr.toArray(s);
			List<String> l = j.mget(s);
			return l;
		} catch (Exception e) {
			LOG.error("获取redis数据失败", e);
			return new ArrayList<String>();
		} finally {
			returnRes(j);
		}
	}

	public List<String> gets(String[] s) {
		for (int i = 0; i < s.length; i++) {
			s[i] = formatKey(s[i]);
		}
		Jedis j = null;
		try {
			j = getJedis();
			List<String> l = j.mget(s);
			return l;
		} catch (Exception e) {
			LOG.error("获取redis数据失败", e);
			return new ArrayList<String>();
		} finally {
			returnRes(j);
		}
	}

	public List<String> keys(String key) {
		key = formatKey(key);
		Jedis j = null;
		try {
			j = getJedis();
			Set<String> sr = j.keys(key);
			List<String> l = new ArrayList<String>();
			l.addAll(sr);
			return l;
		} catch (Exception e) {
			LOG.error("获取redis数据失败", e);
			return new ArrayList<String>();
		} finally {
			returnRes(j);
		}
	}

	public String getSet(String key, String value) {
		key = formatKey(key);
		Jedis j = null;
		try {
			j = getJedis();
			return j.getSet(key, value);
		} catch (Exception e) {
			LOG.error("获取redis数据失败", e);
			return null;
		} finally {
			returnRes(j);
		}
	}

	public boolean expire(String key, int t) {
		key = formatKey(key);
		Jedis j = null;
		try {
			j = getJedis();
			j.expire(key, t);
			return true;
		} catch (Exception e) {
			LOG.error("获取redis数据失败", e);
			return false;
		} finally {
			returnRes(j);
		}
	}

	public boolean lpush(String key, String... values) {
		key = formatKey(key);
		Jedis j = null;
		try {
			j = getJedis();
			j.lpush(key, values);
			return true;
		} catch (Exception e) {
			LOG.error("lpush redis数据失败", e);
			return false;
		} finally {
			returnRes(j);
		}
	}

	public String rpop(String key) {
		key = formatKey(key);
		Jedis j = null;
		try {
			j = getJedis();
			String value = j.rpop(key);
			return value;
		} catch (Exception e) {
			LOG.error("rpop redis数据失败", e);
			return null;
		} finally {
			returnRes(j);
		}
	}

	public int len(String key) {
		key = formatKey(key);
		Jedis j = null;
		try {
			j = getJedis();
			if (!j.exists(key)) {
				return 0;
			}
			return j.llen(key).intValue();
		} catch (Exception e) {
			LOG.error("len redis数据失败", e);
			return 0;
		} finally {
			returnRes(j);
		}
	}

	public int incr(String key) {
		key = formatKey(key);
		Jedis j = null;
		try {
			j = getJedis();
			return j.incr(key).intValue();
		} catch (Exception e) {
			LOG.error("incr redis数据失败", e);
			return 0;
		} finally {
			returnRes(j);
		}
	}

	public int decr(String key) {
		key = formatKey(key);
		Jedis j = null;
		try {
			j = getJedis();
			return j.decr(key).intValue();
		} catch (Exception e) {
			LOG.error("decr redis数据失败", e);
			return 0;
		} finally {
			returnRes(j);
		}
	}

	public boolean exits(String key) {
		key = formatKey(key);
		Jedis j = null;
		try {
			j = getJedis();
			return j.exists(key);
		} catch (Exception e) {
			LOG.error("exits redis数据失败", e);
			return false;
		} finally {
			returnRes(j);
		}
	}

	public void subscribe(String channel, JedisPubSub sub) {
		Jedis j = null;
		try {
			j = getJedis();
			j.subscribe(sub, channel);
		} catch (Exception e) {
			LOG.error("subscribe redis失败", e);
		} finally {
			returnRes(j);
		}
	}

	public void publish(String channel, String message) {
		Jedis j = null;
		try {
			j = getJedis();
			j.publish(channel, message);
		} catch (Exception e) {
			LOG.error("publish redis失败", e);
		} finally {
			returnRes(j);
		}
	}

	public long geoadd(String key, double longitude, double latitude, String member) {
		key = formatKey(key);
		Jedis j = null;
		try {
			j = getJedis();
			return j.geoadd(key, longitude, latitude, member);
		} catch (Exception e) {
			LOG.error("exits redis数据失败", e);
			return 0;
		} finally {
			returnRes(j);
		}
	}

	public List<GeoRadiusResponse> georadius(String key, double longitude, double latitude, double distance) {
		key = formatKey(key);
		Jedis j = null;
		try {
			j = getJedis();
			List<GeoRadiusResponse> l = j.georadius(key, longitude, latitude, distance, GeoUnit.M);
			return l;
		} catch (Exception e) {
			LOG.error("exits redis数据失败", e);
			return null;
		} finally {
			returnRes(j);
		}
	}

	public boolean setNx(String key, String value) {
		key = formatKey(key);
		Jedis j = null;
		try {
			j = getJedis();
			long ret = j.setnx(key, value);
			return ret == 1 ? true : false;
		} catch (Exception e) {
			LOG.error("setNx redis数据失败", e);
			return false;
		} finally {
			returnRes(j);
		}
	}

	public void main(String[] args) {
		System.out.println("start");
		for (int i = 0; i < 1000; i++) {
			this.set("test_stock_" + i, "" + i);
		}
		long t = System.currentTimeMillis();
		// List<String> l = JedisPoolUtil.gets("test_stock_*");
		// for (String code : l) {
		// System.out.println(code);
		// }
		for (int i = 0; i < 1000; i++) {
			System.out.println(this.get("test_stock_" + i));
		}
		System.out.println("耗时:" + (System.currentTimeMillis() - t) + "   ");
	}
}
