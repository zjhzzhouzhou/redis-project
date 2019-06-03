package com.zz.redis.config;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;

/**
 * 
 * @Description: redis通用工具类
 *
 * @author yl
 * @date 2018年5月9日
 */
public class ZCacheUtil {

	/**
	 * 获取被拦截方法对象
	 * 
	 * MethodSignature.getMethod() 获取的是顶层接口或者父类的方法对象 而缓存的注解在实现类的方法上
	 * 所以应该使用反射获取当前对象的方法对象
	 */
	public static Method getMethod(ProceedingJoinPoint joinPoint) {
		Signature sig = joinPoint.getSignature();
		MethodSignature msig = (MethodSignature) sig;
		Class<?>[] argTypes = msig.getParameterTypes();
//		Object[] args = joinPoint.getArgs();
//		Class<?>[] argTypes = new Class[joinPoint.getArgs().length];
//		for (int i = 0; i < args.length; i++) {
//			if (args[i] != null) {
//				argTypes[i] = args[i].getClass();
//			}
//		}
		Method method = null;
		try {
			method = joinPoint.getTarget().getClass().getMethod(joinPoint.getSignature().getName(), argTypes);
		} catch (NoSuchMethodException e1) {
			e1.printStackTrace();
		} catch (SecurityException e1) {
			e1.printStackTrace();
		}
		return method;
	}

	/**
	 * 获取缓存的key key 定义在注解上，支持SPEL表达式
	 *
	 * @return
	 */
	public static String parseKey(String key, Method method, Object[] args) {
		// 获取被拦截方法参数名列表(使用Spring支持类库)
		LocalVariableTableParameterNameDiscoverer u = new LocalVariableTableParameterNameDiscoverer();
		String[] paraNameArr = u.getParameterNames(method);

		// 使用SPEL进行key的解析
		ExpressionParser parser = new SpelExpressionParser();
		// SPEL上下文
		StandardEvaluationContext context = new StandardEvaluationContext();
		// 把方法参数放入SPEL上下文中
		for (int i = 0; i < paraNameArr.length; i++) {
			context.setVariable(paraNameArr[i], args[i]);
		}
		return parser.parseExpression(key).getValue(context, String.class);
	}

	/**
	 * 自定义key,此方法会根据类名+方法名+所有参数的值生成一个 唯一的key
	 */
	public static String keyGenerator(ProceedingJoinPoint joinPoint) {
		// 获取目标方法所在类
		String target = joinPoint.getTarget().toString();
		String className = target.split("@")[0];
		// 获取目标方法的方法名称
		String methodName = joinPoint.getSignature().getName();
		StringBuilder sb = new StringBuilder();
		sb.append(className);
		sb.append(methodName);
		Object[] objects = joinPoint.getArgs();
		for (int i = 0; i < objects.length; i++) {
			if (objects[i] != null) {
				sb.append(objects.toString());
			}
		}
		return sb.toString();
	}
}
