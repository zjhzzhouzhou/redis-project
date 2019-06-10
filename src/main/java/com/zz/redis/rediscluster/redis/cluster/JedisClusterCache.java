package com.zz.redis.rediscluster.redis.cluster;


import com.zz.redis.rediscluster.redis.serialize.Serializer;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.util.JedisClusterCRC16;
import redis.clients.util.SafeEncoder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class JedisClusterCache extends JedisCluster implements RedisCache {


	private static final Logger LOGGER = LoggerFactory.getLogger(JedisClusterCache.class);

	@Override
	public String setex(String key, int seconds, String value) {
		return super.setex(key, seconds, value);
	}

	@Override
	public Long hdel(String key, String... field) {
		return super.hdel(key, field);
	}

	@Override
	public Long hdel(byte[] key, byte[]... field) {
		return super.hdel(key, field);
	}

	private Serializer serial;

	public JedisClusterCache(Set<HostAndPort> nodes,
                             int connectionTimeout,
                             int soTimeout,
                             int maxRedirections,
                             final GenericObjectPoolConfig poolConfig,
                             Serializer serial) {
		super(nodes,
			  connectionTimeout,
			  soTimeout,
			  maxRedirections,
			  poolConfig);
		this.serial = serial;
		initClusterInfo();
	}

	/**
	 * 传入明确的class类型
	 * @param key
	 * @param type
	 * @param <T>
	 * @return
	 */
	@Override
	public <T> T get(final String key, final Class<T> type) {
		byte[] val = get(SafeEncoder.encode(key));
		T obj = (T)toObject(val, type);
		return obj;
	}

	@Override
	public <T> T getFromCache(final String key, final Class<T>... type) {
		byte[] val = get(SafeEncoder.encode(key));
		T obj = (T)toObject(val, type);
		return obj;
	}

	// 根据多个主键批量获取值。
	public <T> List<T> mgetFromCache(final String[] keys, Class<T>... type) {
		byte[][] keysByte = new byte[keys.length][];
		int i = 0;
		for (String key : keys) {
			keysByte[i] = SafeEncoder.encode(key);
			i++;
		}
		List<byte[]> list = mget(keysByte);

		List<T> retList = new ArrayList<T>();

		list.forEach(item->retList.add((T)toObject(item, type)));
		return retList;
	}

	// 根据多个主键批量获取值。
	// 设置多个键值对  jedis.mset("name","liuling","age","23","qq","476777XXX");
	public <T> void msetInfoCache(final String[] keys, final T[] values) {
		byte[][] keyvalues = new byte[keys.length * 2][];
		int i = 0;
		int index = 0;
		for (String key : keys) {
			keyvalues[index++] = SafeEncoder.encode(key);
			keyvalues[index++] = toByteArray(values[i]);
			i++;
		}
		mset(keyvalues);
	}

	@Override
	public String set(final String key, final Object value) {
		String result = set(SafeEncoder.encode(key), toByteArray(value));
		return result;
	}

	@Override
	public Long del(final String key){
		Long result = del(SafeEncoder.encode(key));
		return result;
	}

	@Override
	public Long del(final String... keys){
		byte[][] keybs = new byte[keys.length][];
		int i = 0;
		for (String key: keys) {
			keybs[i++] = SafeEncoder.encode(key);
		}
		Long result = del(keybs);
		return result;

	}

	/**
	 * Set the string value as value of the key. The string can't be longer than 1073741824 bytes (1
	 * GB).
	 * @param key
	 * @param value
	 * @param nxxx NX|XX, NX -- Only set the key if it does not already exist. XX -- Only set the key
	 *          if it already exist.
	 * @param expx EX|PX, expire time units: EX = seconds; PX = milliseconds
	 * @param time expire time in the units of <code>expx</code>
	 * @return Status code reply
	 */
	@Override
	public String set(final String key,
					  final Object value,
					  final String nxxx,
					  final String expx,
					  final long time) {
		String result = set(SafeEncoder.encode(key), toByteArray(value), SafeEncoder.encode(nxxx), SafeEncoder.encode(expx), time);
		return result;
	}

	@Override
	public String setex(String key, Object value, int time) {
		String result = setex(SafeEncoder.encode(key), time, toByteArray(value));
		return result;
	}

	@Override
	public Long setnx(String key, String value) {
		Long result = super.setnx(key, value);
		return result;
	}

	@Override
	public Long expire(String key, int seconds) {
		return super.expire(key, seconds);
	}

	//*********************************//
	//************ private ************//
	//*********************************//
	private byte[] toByteArray(Object value) {
		Assert.notNull(value, "RedisCache Value must not be null");
		try {
			return SerializeUtil.serialize(value);
		}
		catch (Exception e) {
			LOGGER.error("SerializeUtil.serialize error", e);
			return serial.serialize(value);
		}
	}

	private Object toObject(byte[] bytes, Class... clazz) {
		if (bytes == null) {
			return null;
		}
		try {
			return SerializeUtil.unserialize(bytes, clazz);
		}
		catch (Exception e) {
			LOGGER.error("SerializeUtil.unserialize error", e);
			return serial.deserialize(bytes);
		}
	}



	// 获取集群key所在的主机节点。
	public Map<String, JedisPool> nodeMap = null;
	public TreeMap<Long, String> slotHostMap = null;

	private void initClusterInfo() {
		nodeMap = this.getClusterNodes();
		String anyHost = nodeMap.keySet().iterator().next();
		slotHostMap = getSlotHostMap(anyHost);
	}

	private static TreeMap<Long, String> getSlotHostMap(String anyHostAndPortStr) {
		TreeMap<Long, String> tree = new TreeMap<Long, String>();
		String parts[] = anyHostAndPortStr.split(":");
		HostAndPort anyHostAndPort = new HostAndPort(parts[0], Integer.parseInt(parts[1]));
		Jedis jedis = null;
		try {
			jedis = new Jedis(anyHostAndPort.getHost(), anyHostAndPort.getPort());
			List<Object> list = jedis.clusterSlots();
			for (Object object : list) {
				List<Object> list1 = (List<Object>) object;
				List<Object> master = (List<Object>) list1.get(2);
				String hostAndPort = new String((byte[]) master.get(0)) + ":" + master.get(1);
				tree.put((Long) list1.get(0), hostAndPort);
				tree.put((Long) list1.get(1), hostAndPort);
			}

		} catch (Exception e) {

		} finally {
			if (jedis != null) {
                jedis.close();
            }
		}
		return tree;
	}

	public Jedis getJedisByKey(String key) {

		//获取槽号
		int slot = JedisClusterCRC16.getSlot(SafeEncoder.encode(key));

		//获取到对应的Jedis对象
		// +1 让开始槽点落在自己的区间。
		Map.Entry<Long, String> entry = slotHostMap.lowerEntry(Long.valueOf(slot) +1);

		Jedis jedis = nodeMap.get(entry.getValue()).getResource();

		return jedis;
	}


	/**
	 * 集群模糊查询并删除。
	 * @param pattern
	 * @return
	 */
	public TreeSet<String> removeKeys(String pattern){
		LOGGER.debug("Start removing keys...");
		TreeSet<String> keys = new TreeSet<>();
		Map<String, JedisPool> clusterNodes = this.getClusterNodes();
		for(String k : clusterNodes.keySet()){
			LOGGER.debug("removing keys from: " + k);
			JedisPool jp = clusterNodes.get(k);
			Jedis connection = jp.getResource();
			try {
				Set<String> keyInSingleRedis = connection.keys(pattern);
				if(keyInSingleRedis!=null && keyInSingleRedis.size() > 0){
					Map<Integer, List<String>> map = new HashMap<>(16);
					for (String key : keyInSingleRedis) {
						//cluster模式执行多key操作的时候，这些key必须在同一个slot上，不然会报:JedisDataException: CROSSSLOT Keys in request don't hash to the same slot
						int slot = JedisClusterCRC16.getSlot(key);
						//按slot将key分组，相同slot的key一起提交
						if (map.containsKey(slot)) {
							map.get(slot).add(key);
						} else {
							List<String> list = new ArrayList<>(16);
							list.add(key);
							map.put(slot, list);
						}
					}
					for (Map.Entry<Integer, List<String>> integerListEntry : map.entrySet()) {
						connection.del(integerListEntry.getValue().toArray(new String[integerListEntry.getValue().size()]));
					}
					
					keys.addAll(keyInSingleRedis);
				}
			} catch(Exception e){
				LOGGER.error("removing keys error: {}", e);
			} finally{
				LOGGER.debug("Connection closed.");
				connection.close();//用完一定要close这个链接！！！
			}
		}
		LOGGER.debug("Keys removed!");
		return keys;
	}

	/**
	 * List尾部追加记录
	 * @param key 1
	 * @param value 2
	 * @return java.lang.Long
	 * @author meco.he
	 * @date 2019/5/21
	 */
	public Long rpush(String key, Object value) {
		return super.rpush(SafeEncoder.encode(key), toByteArray(value));
	}

	/**
	 * list删除某一元素
	 * @param key 1
	 * @param count 2
	 * @param value 3
	 * @return java.lang.Long
	 * @author meco.he
	 * @date 2019/5/21
	 */
	public Long lrem(String key, long count, Object value) {
		return super.lrem(SafeEncoder.encode(key), count, toByteArray(value));
	}

	/**
	 * 从头部删除元素，并返回该元素
	 * @param key 1
	 * @param type 2
	 * @return T
	 * @author meco.he
	 * @date 2019/5/21
	 */
	public <T> T lpop(String key, Class<T> type) {
		byte[] val = super.lpop(SafeEncoder.encode(key));
		T obj = (T)toObject(val, type);
		return obj;
	}

	/**
	 * 获取下标start-end的元素列表
	 * @param key 1
	 * @param start 2
	 * @param end 3
	 * @param type 4
	 * @return java.util.List<T>
	 * @author meco.he
	 * @date 2019/5/21
	 */
	public <T> List<T> lrange(String key, long start, long end, Class<T> type) {
		List<byte[]> list = super.lrange(SafeEncoder.encode(key), start, end);
		List<T> res = new ArrayList<>();
		for(byte[] value: list){
			T e = (T)toObject(value, type);
			res.add(e);
		}
		return res;
	}
}
