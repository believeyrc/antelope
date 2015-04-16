package com.github.believeyrc.antelope.common;


import java.util.List;

import com.github.believeyrc.antelope.common.support.ChildrenListener;
import com.github.believeyrc.antelope.common.support.NodeListener;

/**
 * zookeeper 操作接口
 * @author yangrucheng
 * @created 2015年4月15日 上午11:48:26
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
