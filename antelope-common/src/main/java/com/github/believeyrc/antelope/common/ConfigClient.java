package com.github.believeyrc.antelope.common;

/**
 * 配置管理操作接口
 * @author yangrucheng
 * @since 1.0
 * @version 1.0
 *
 */
public interface ConfigClient{
	
	void publishConfig(String path, String data);
	
	void subscribeConfig(String path);
	
	String getConfigData(String path);
	
	void deleteConfig(String path);
	
	String getConfigPrefix();
	
	
	
	
	
	
}
