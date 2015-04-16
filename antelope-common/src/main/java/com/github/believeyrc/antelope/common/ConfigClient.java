package com.github.believeyrc.antelope.common;

/**
 * 配置管理接口
 * @author yangrucheng
 * @created 2015年4月15日 上午11:49:03
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
