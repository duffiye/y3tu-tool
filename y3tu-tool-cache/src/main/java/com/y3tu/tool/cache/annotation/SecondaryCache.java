package com.y3tu.tool.cache.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 二级缓存配置项
 *
 * @author yuhao.wang
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface SecondaryCache {
    /**
     * 缓存有效时间
     *
     * @return long
     */
    long expireTime() default 5;

    /**
     * 缓存主动在失效前强制刷新缓存的时间
     * 建议是： preloadTime = expireTime * 0.2
     *
     * @return long
     */
    long preloadTime() default 1;

    /**
     * 时间单位 {@link TimeUnit}
     *
     * @return TimeUnit
     */
    TimeUnit timeUnit() default TimeUnit.HOURS;

    /**
     * 是否强制刷新（直接执行被缓存方法），默认是false
     *
     * @return boolean
     */
    boolean forceRefresh() default false;
}
