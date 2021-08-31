package com.acrabsoft.web.service.sjt.pc.operation.web.util.hbaseHuawei;

import java.lang.annotation.*;

/**
 * hbase自定义注解，指定字段为hbase的主键
 * @Author zyx
 * @create 2020/8/13 17:37
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
public @interface HTableId {
    String value() default "";
}
