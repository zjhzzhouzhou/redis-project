package com.zz.redis.rediscluster.redis.cluster;


import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.objenesis.Objenesis;
import org.springframework.objenesis.ObjenesisStd;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SerializeUtil {
    protected final static Log log = LogFactory.getLog(SerializeUtil.class);

    private static Objenesis objenesis = new ObjenesisStd(true);


    private static Map<Class, Class> filterClass = new HashMap<Class, Class>() {
        {
            put(List.class, ArrayList.class);
            put(Map.class, HashMap.class);
        }
    };

    private static Map<Class, Schema> cachedSchema = new ConcurrentHashMap<Class, Schema>();

    @SuppressWarnings("unchecked")
    private static Schema getSchema(Class cls) {
        Schema schema = (Schema) cachedSchema.get(cls);
        if (schema == null) {
            schema = RuntimeSchema.createFrom(cls);
            if (schema != null) {
                cachedSchema.put(cls, schema);
            }
        }
        return schema;
    }

    /**
     * 
     * @desc 序列化对象 ，非基本类型是包装了一层CacheObject
     */
    public static <T> byte[] serialize(T object) {
        Class clazz = object.getClass();

        Object newObject = object;
        // 非基本类型，统一使用CacheObject包装一层。
        if (!isBaseDataType(clazz)) {
            CacheObject cacheObject = new CacheObject();
            cacheObject.setResult(object);
            newObject =  cacheObject;
        }

       return serializeInner(newObject);
    }

    private static <T> byte[] serializeInner(T object) {
        Class<T> clazz = (Class<T>) object.getClass();

        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        try {
            Schema<T> schema = getSchema(clazz);
            return ProtostuffIOUtil.toByteArray(object, schema, buffer);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        } finally {
            buffer.clear();
        }
    }

    /**
     * 
     * @param bytes
     * @param clazz
     * @return
     */
    private static Object unserializeInner(byte[] bytes, Class clazz) {

        try {
            Object instance = clazz.newInstance();
            Schema schema = getSchema(clazz);
            ProtostuffIOUtil.mergeFrom(bytes, instance, schema);
            return instance;
        }
        catch (Exception e) {
            log.error("unserialize obj error.", e);
        }
        return null;
    }

    /**
     * 反序列化，非基本类型是包装了一层CacheObject
     * @param bytes
     * @param clazz
     * @return
     */
    public static Object unserialize(byte[] bytes, Class... clazz) {

        Class newClazz = null;
        try {
            if (clazz != null && clazz.length > 0) {
                newClazz = clazz[0];
            }

            // 判断是否基本类型，非基本类型包装一层。
            if (newClazz == null || !isBaseDataType(newClazz)) {
                try {
                    return ((CacheObject) unserializeInner(bytes, CacheObject.class)).getResult();
                }
                catch(Exception e) {
                    //做下容错处理
                    return unserializeInner(bytes, String.class);
                }
            }

            return unserializeInner(bytes, newClazz);
        }
        catch (Exception e) {
            log.error("unserialize obj error.", e);
        }
        return null;
    }


    private static final String TYPE_NAME_PREFIX = "class ";

    /**
     * 根据type获取class名称.
     * @param type
     * @return
     */
    public static String getClassName(Type type) {
        if (type==null) {
            return "";
        }
        String className = type.toString();
        if (className.startsWith(TYPE_NAME_PREFIX)) {
            className = className.substring(TYPE_NAME_PREFIX.length());
        }
        int beginIndex = className.indexOf("<");
        if (beginIndex >= 0) {
            //        int endIndex = className.lastIndexOf(">");
            className = className.substring(0, beginIndex);
        }
        return className;
    }

    // 判断是否基本数据类型，暂时只是string类型不包装。
    public static boolean isBaseDataType(Class clazz)
    {
        return
            (
                clazz.equals(String.class)
//                    ||
//                    clazz.equals(Integer.class)||
//                    clazz.equals(Byte.class) ||
//                    clazz.equals(Long.class) ||
//                    clazz.equals(Double.class) ||
//                    clazz.equals(Float.class) ||
//                    clazz.equals(Character.class) ||
//                    clazz.equals(Short.class) ||
//                    clazz.equals(BigDecimal.class) ||
//                    clazz.equals(BigInteger.class) ||
//                    clazz.equals(Boolean.class) ||
////                    clazz.equals(Date.class) ||
//                    clazz.isPrimitive()
            );
    }

    /**
     * 获取class
     * @param type
     * @return
     * @throws ClassNotFoundException
     */
    public static Class getClass(Type type)
        throws ClassNotFoundException {
        String className = getClassName(type);
        if (className==null || className.isEmpty()) {
            return null;
        }
        return Class.forName(className);
    }

    /**
     * 将接口替换成实现类
     * @param clazz
     * @return
     */
    private static Class filterInterfaceToClass(Class clazz) {
        Class toClass = filterClass.get(clazz);
        if (toClass != null) {
            return toClass;
        }

        return clazz;
    }

    /**
     * 获取实例 如果是接口，如list，需要传入arraylist
     * @param type
     * @return
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public static Object newInstance(Type type)
        throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        Class<?> clazz = getClass(type);
        if (clazz==null) {
            return null;
        }
        clazz = filterInterfaceToClass(clazz);
        return clazz.newInstance();
    }
}
