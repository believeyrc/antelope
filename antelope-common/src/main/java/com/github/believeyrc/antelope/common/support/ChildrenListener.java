package com.github.believeyrc.antelope.common.support;


import java.util.List;

public interface ChildrenListener {
	
	void childrenChanged(String path, List<String> children);

}
