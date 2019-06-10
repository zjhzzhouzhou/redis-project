package com.zz.redis.rediscluster.redis.support;

import java.util.Map;

/** 定义cache获取的接口类
 * @param <V>cache对象的类型
*/
public interface SeniorGenericCache<V> {
	/** 根据多参数获取对象 */
	V get(final Map param);

	/** 从缓存中删除指定参数对象 */
	void del(final Map param);

	/** 更新对象 */
	void update(final Map param, V object);

    Boolean exists(final Map param);

    public Long expire(final Map param, final int seconds);

    public Long pexpire(final Map param, final long milliseconds);

}
