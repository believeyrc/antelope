package com.github.believeyrc.antelope.common;


public interface ConfigClient{
	
	void publishConfig(String path, String data);
	
	void subscribeConfig(String path);
	
	String getConfigData(String path);
	
	
	void deleteConfig(String path);
	
	
	
	
}
