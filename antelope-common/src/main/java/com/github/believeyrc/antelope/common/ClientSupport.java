package com.github.believeyrc.antelope.common;

import org.springframework.stereotype.Component;

import com.github.believeyrc.antelope.common.extension.SPI;

@SPI("cuartor")
@Component
public interface ClientSupport extends ZookeeperClient, ConfigClient{
	
	void init(String namespace, String connectString,
			String configPrefix, String charset, int maxRetries, int baseSleepTime);
	
	void close();
}
