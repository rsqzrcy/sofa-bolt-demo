package com.alipay.mfma.config;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

/**
 * @author csizg
 */
@Service
public class ApplicationContextHelper implements ApplicationContextAware {
	
	private static ApplicationContext applicationContext;
	
	@Override
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		applicationContext = context;
	}
	
	public static <E> E getBean(Class<E> clazz) {
		return applicationContext.getBean(clazz);
	}
	
}