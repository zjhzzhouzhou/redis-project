package com.zz.redis.rediscluster.redis.cluster;

public interface RedisCache {
	/**
	 *  * @param nxxx NX|XX, NX -- Only set the key if it does not already exist. XX -- Only set the key
	 *          if it already exist.
	 */
	public static final String KEY_NX ="NX";
	public static final String KEY_XX ="XX";


	/** @param expx EX|PX, expire time units: EX = seconds; PX = milliseconds*/
	public static final String EXPIRE_TIME_UNIT_EX = "EX";
	public static final String EXPIRE_TIME_UNIT_PX = "PX";

	<T> T get(String key, final Class<T> type);

	<T> T getFromCache(String key, final Class<T>... type);

	String set(String key, Object value);
	
	String set(String key, Object value, String nxxx, String expx, long time);

	Long del(String key);

	/**
	 * 设置过期时间, 单位: 秒
	 * @param key
	 * @param value
	 * @param time
	 * @return
	 */
	String setex(String key, Object value, int time);
}
