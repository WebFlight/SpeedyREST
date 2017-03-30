package speedyrest.services;

import java.util.List;
import java.util.Map;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;
import redis.clients.jedis.ScanParams;
import redis.clients.johm.JOhm;
import speedyrest.proxies.constants.Constants;

public class RedisCache implements Cache {
	
	private static JedisPool redisPool;
	private Jedis redis;
	
	public RedisCache(String host, String password, int port, int index) {
		if (redisPool == null) {
			if (password.length() == 0) {
				initRedisPoolWithoutPassword(host, port, index);
			}
			if (password.length() > 0) {
				initRedisPoolWithPassword(host, password, port, index);
			}			
		}
		this.redis = redisPool.getResource();
		JOhm.setPool(redisPool);
	}
	
	private void initRedisPoolWithPassword(String host, String password, int port, int index) {
		redisPool = new JedisPool(
				new JedisPoolConfig(), 
				Constants.getREDIS_HOST(),
				Constants.getREDIS_PORT().intValue(),
				Protocol.DEFAULT_TIMEOUT, 
				Constants.getREDIS_PASSWORD());			
	}
	
	private void initRedisPoolWithoutPassword(String host, int port, int index) {
			redisPool = new JedisPool(
							new JedisPoolConfig(), 
							Constants.getREDIS_HOST(),
							Constants.getREDIS_PORT().intValue(),
							Protocol.DEFAULT_TIMEOUT);
	}
	
	public void set(String key, String value) {
		redis.set(key, value);
	}
	
	public void setHashMap(String key, Map<String, String> hashMap) {
		redis.hmset(key, hashMap);
	}

	public String get(String key) {
		return redis.get(key);
	}
	
	public Map<String, String> getHashMap(String key) {
		return redis.hgetAll(key);
	}
	
	public boolean deleteByPattern(String pattern) {
		ScanParams params = new ScanParams();
		params.match(pattern);
		List<String> keys = redis.scan("0", params).getResult();
		for (String key : keys) {
		    redis.del(key);
		}
		return true;
	}

	@Override
	public void persistObject(Object object) {
		JOhm.save(object);
	}

	@Override
	public List<?> findObject(Class<?> classDefinition, String key, Object value) {
		return JOhm.find(classDefinition, key, value);
	}
	
	
}
