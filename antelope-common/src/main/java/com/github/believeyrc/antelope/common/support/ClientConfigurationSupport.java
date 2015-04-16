package com.github.believeyrc.antelope.common.support;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public abstract class ClientConfigurationSupport implements InitializingBean, DisposableBean {
	
	
	private String namespace = "zk-servcer";
	
	private String connectString;
	
	private String configPrefix = "/config";
	
	private String chartset = "utf-8";
	
	private int maxRetries = 2;
	
	private int baseSleepTime = 1000;
	
	private List<String> path = new ArrayList<String>();
	
	protected ConcurrentMap<String, String> configData = new ConcurrentHashMap<String, String>();
	

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public String getConnectString() {
		return connectString;
	}

	public void setConnectString(String connectString) {
		this.connectString = connectString;
	}

	public String getConfigPrefix() {
		return configPrefix;
	}

	public void setConfigPrefix(String configPrefix) {
		this.configPrefix = configPrefix;
	}

	public String getChartset() {
		return chartset;
	}

	public void setChartset(String chartset) {
		this.chartset = chartset;
	}

	public int getMaxRetries() {
		return maxRetries;
	}

	public void setMaxRetries(int maxRetries) {
		this.maxRetries = maxRetries;
	}

	public int getBaseSleepTime() {
		return baseSleepTime;
	}

	public void setBaseSleepTime(int baseSleepTime) {
		this.baseSleepTime = baseSleepTime;
	}
	
	
	

	public List<String> getPath() {
		return path;
	}

	public void setPath(List<String> path) {
		this.path = path;
	}

	@Override
	public void destroy() throws Exception {
		doClose();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		doInit();
		initConfig();
	}
	
	protected abstract void doInit();
	
	protected abstract void initConfig();
	
	protected abstract void doClose();
	
	

}
