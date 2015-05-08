package com.github.believeyrc.antelope.common;


import java.util.List;

import com.github.believeyrc.antelope.common.support.ChildrenListener;
import com.github.believeyrc.antelope.common.support.NodeListener;

/**
 * zookeeper客户端基础操作接口
 * @author yangrucheng
 * @since 1.0
 * @version 1.0
 *
 */
public interface ZookeeperClient {
	
	void create(String path, boolean ephemeral, String data);
	
	void delete(String path);
	
	void setData(String path, String data);
	
	String getData(String path);
	
	List<String> getChildren(String path);

	void addListener(String path, NodeListener listener);
	
	void addChildrenListener(String path, ChildrenListener childrenListener);
	
	
}
