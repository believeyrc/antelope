package com.github.believeyrc.antelope.common;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.github.believeyrc.antelope.common.support.ChildrenListener;
import com.github.believeyrc.antelope.common.support.NodeListener;

public abstract class AbstractClient implements ClientSupport {
	
	protected ConcurrentMap<String, String> configData = new ConcurrentHashMap<String, String>();
	
	
	@Override
	public void create(String path, boolean ephemeral, String data) {
		if (ephemeral) {
			createEphemeral(path, data);
		} else {
			createPersistent(path, data);
		}
	}
	
	
	@Override
	public void delete(String path) {
		if (checkExists(path)) {
			deletePath(path);
		}
		
	}
	
	@Override
	public void setData(String path, String data) {
		if (checkExists(path)) {
			setPathData(path, data);
		} else {

		}
		
	}
	
	@Override
	public void publishConfig(String path, String data) {
		String pubPath = getConfigPath(path);
		if (! checkExists(pubPath)) {
			create(pubPath, false, data);
		} else {
			setData(pubPath, data);
		}
	}
	@Override
	public void subscribeConfig(String path) {
		String subPath = getConfigPath(path);
		addListener(subPath, new NodeListener() {
			@Override
			public void nodeChanged(String path, String data) {
				if (data == null || "".equals(data)) {
					configData.remove(path);
				} else {
					configData.put(path, data);
				}
				
			}
		});
		if (checkExists(subPath)) {
			configData.put(subPath, getData(subPath));
		}
	}

	@Override
	public String getConfigData(String path) {
		String subPath = getConfigPath(path);
		return configData.get(subPath);
	}
	
	
	@Override
	public void deleteConfig(String path) {
		String subPath = getConfigPath(path);
		delete(subPath);
	}





	protected abstract boolean checkExists(String path);
	
	protected abstract void createPersistent(String path, String data);
	
	protected abstract void createEphemeral(String path, String data);
	
	protected abstract void deletePath(String path);
	
	protected abstract void setPathData(String path, String data);
	
	protected abstract String getConfigPath(String path);
	
	@Override
	public void addChildrenListener(String path, ChildrenListener childrenListener) {

	}
	/*
	private String getConfigPath(String path) {
		return configPrefix + "/" + path;
	}*/
}
