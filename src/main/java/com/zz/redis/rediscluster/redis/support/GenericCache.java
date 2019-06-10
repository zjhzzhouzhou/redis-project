package com.zz.redis.rediscluster.redis.support;

import java.util.List;
import java.util.Map;

/** 定义cache获取的接口类
 * @param <PK>cache对象的key,为一个数组对象
 * @param <V>cache对象的类型
*/
public interface GenericCache<PK, V> {

	/** 根据ID获取对象 */
	V get(final PK id);

	/** 根据一组ID,获取相应对象组 */
	List<V> get(final PK[] ids);

	/** 批量方式返回Map类型*/
	Map<PK, V> getBatchMap(final PK[] ids);

	/** 从缓存中删除指定id对象 */
	void del(final PK id);

	/** 更新对象 */
	void update(final PK id, V object);


    Boolean exists(final PK id);

    public Long expire(final PK id, final int seconds);
    public Long pexpire(final PK id, final long milliseconds);
}
