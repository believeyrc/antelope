package com.github.believeyrc.antelope.common.extension;

import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class AnnotationHandler implements InitializingBean, ApplicationContextAware {

	private ApplicationContext applicationContext;
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext; 
		
	} 

	@Override
	public void afterPropertiesSet() throws Exception {
		System.out.println(applicationContext);
		Map<String, Object> spiMap = applicationContext.getBeansWithAnnotation(SPI.class);
		for (Object spi : spiMap.values()) {
			Class<? extends Object> spiImpl = spi.getClass();
			System.out.println("spi impl : " + spiImpl.getName());
		}
	}

}
