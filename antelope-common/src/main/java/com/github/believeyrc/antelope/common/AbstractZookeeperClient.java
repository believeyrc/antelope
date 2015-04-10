package com.github.believeyrc.antelope.common;


import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import com.github.believeyrc.antelope.common.support.ChildrenListener;
import com.github.believeyrc.antelope.common.support.NodeListener;

public abstract class AbstractZookeeperClient<T> implements ZookeeperClient,ConfigClient, InitializingBean, DisposableBean {
	
	private String connectString;
	
	private String configPrefix;
	
	protected ConcurrentMap<String, String> configData = new ConcurrentHashMap<String, String>();
	

	public String getConfigPrefix() {
		if (configPrefix == null || "".equals(configPrefix)) {
			return "/config";
		}
		return configPrefix;
	}


	public void setConfigPrefix(String configPrefix) {
		this.configPrefix = configPrefix;
	}

	private ConcurrentMap<String, ConcurrentMap<NodeListener, T>> dataListeners = new ConcurrentHashMap<String, ConcurrentMap<NodeListener,T>>();

	private ConcurrentMap<String, NodeListener> nodeListeners = new ConcurrentHashMap<String, NodeListener>();  
	
	public String getConnectString() {
		return connectString;
	}

	
	public void setConnectString(String connectString) {
		this.connectString = connectString;
	}

	

	protected ConcurrentMap<String, ConcurrentMap<NodeListener, T>> getDataListeners() {
		return dataListeners;
	}
	
	protected T getDataListenerTarget(String path, NodeListener dataListener) {
		return dataListeners.get(path).get(dataListener);
	}


	//protected abstract T createDataListenerTarget(String path, NodeListener listener);
	
	
	
	//protected abstract String addDataListenerTarget(String path, T listenerTarget);
	
	
	/*@Override
	public String addListener(String path, NodeListener listener) {
		NodeListener nodeListener = nodeListeners.get(path);
		if (nodeListener == null) {
			nodeListener = listener;
			nodeListeners.putIfAbsent(key, value)
		}
//		T listenerTarget = listeners.get(listener);
		
		//return addDataListenerTarget(path, listenerTarget);
		return null;
	}*/

	@Override
	public List<String> addChildrenListener(String path, ChildrenListener childrenListener) {
		return null;
	}

	@Override
	public void destroy() throws Exception {
		doClose();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		doInit();
	}
	
	protected abstract void doInit();
	
	protected abstract void doClose();
}

