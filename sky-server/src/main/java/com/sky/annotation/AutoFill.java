package com.sky.annotation;

import com.sky.enumeration.OperationType;
import org.aopalliance.intercept.Joinpoint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解，标识方法需要进行字符填充
 * @author
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoFill {
	// 数据库操作类型 update,insert
	OperationType value();



}
