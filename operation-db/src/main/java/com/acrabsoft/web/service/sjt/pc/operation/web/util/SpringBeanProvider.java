package com.acrabsoft.web.service.sjt.pc.operation.web.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringBeanProvider implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringBeanProvider.applicationContext = applicationContext;

    }

    /**
     * 通过 beanName 获取 Bean
     * @param beanName
     * @return
     */
    public Object getBean(String beanName) {
        return applicationContext.getBean(beanName);
    }

    /**
     * 通过class获取Bean
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T getBean(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }

    /**
     * 通过 beanName ,以及Clazz返回指定的Bean
     * @param beanName
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T getBean(String beanName, Class<T> clazz) {
        return applicationContext.getBean(beanName, clazz);
    }
}
