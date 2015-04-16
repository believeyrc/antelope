package com.github.believeyrc.antelope.common;


import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.github.believeyrc.antelope.common.support.ChildrenListener;
import com.github.believeyrc.antelope.common.support.ClientConfigurationSupport;

public abstract class AbstractZookeeperClient extends ClientConfigurationSupport implements ZookeeperClient,ConfigClient {
	
	protected ConcurrentMap<String, String> configData = new ConcurrentHashMap<String, String>();
	

	

	@Override
	public void addChildrenListener(String path, ChildrenListener childrenListener) {

	}









	@Override
	public void create(String path, boolean ephemeral, String data) {
		if (ephemeral) {
			createEphemeral(path, data);
		} else {
			createPersistent(path, data);
		}
	}
	
	protected abstract void createPersistent(String path, String data);
	
	protected abstract void createEphemeral(String path, String data);
	
	
	
	
	

}

