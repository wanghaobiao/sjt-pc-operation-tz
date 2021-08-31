package com.acrabsoft.web.service.sjt.pc.operation.web.util.hbaseHuawei;

import java.lang.annotation.*;

/**
 * 自定义hbase注解，指定实体对象映射的hbase表名称
 * @Author zyx
 * @create 2020/8/13 17:36
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
public @interface HTableName {
    String value() default "";
    String chs() default "";
}
