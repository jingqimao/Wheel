package com.chs.wheel.core;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * <p>CMapping</p>
 * Description: 控制器注解
 * 
 * @author chenhaishan
 * @date 2018-04-17 09:13
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE,ElementType.METHOD})
public @interface ControllerMapping {
	//路径
	String url() default "";
}
