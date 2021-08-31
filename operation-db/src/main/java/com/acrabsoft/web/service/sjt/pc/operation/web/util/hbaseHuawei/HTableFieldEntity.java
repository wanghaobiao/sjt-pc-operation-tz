package com.acrabsoft.web.service.sjt.pc.operation.web.util.hbaseHuawei;

import java.lang.annotation.*;

/**
 * hbase自定义注解，指定字段在hbase中的family和column
 * @Author zyx
 * @create 2020/8/13 17:36
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
public @interface HTableFieldEntity {
    String family();

}
