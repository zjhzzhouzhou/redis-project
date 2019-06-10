package com.zz.redis.rediscluster.redis.serialize;

public interface Serializer {

	byte[] serialize(Object obj);
	Object deserialize(byte[] bytes);

}
