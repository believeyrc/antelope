package com.github.believeyrc.antelope.common;


import java.util.List;

import com.github.believeyrc.antelope.common.support.ChildrenListener;
import com.github.believeyrc.antelope.common.support.NodeListener;

public interface ZookeeperClient {
	
	String createPersistent(String path, String data);
	
	String createEphemeral(String path);
	
	
	void delete(String path);
	
	void setData(String path, String data);
	
	String getData(String path);
	
	List<String> getChildren(String path);

	String addListener(String path, NodeListener listener);
	
	
	List<String> addChildrenListener(String path, ChildrenListener childrenListener);
}
