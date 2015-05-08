package com.github.believeyrc.antelope.common;

public interface ClientSupport extends ZookeeperClient, ConfigClient{
	
	void close();
}
