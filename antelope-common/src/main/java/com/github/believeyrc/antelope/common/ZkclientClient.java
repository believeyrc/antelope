package com.github.believeyrc.antelope.common;

import java.util.List;

import com.github.believeyrc.antelope.common.support.NodeListener;

public class ZkclientClient extends AbstractClient {
	
	public ZkclientClient() {
		
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getData(String path) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getChildren(String path) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addListener(String path, NodeListener listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getConfigPrefix() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean checkExists(String path) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void createPersistent(String path, String data) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void createEphemeral(String path, String data) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void deletePath(String path) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void setPathData(String path, String data) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String getConfigPath(String path) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void init(String namespace, String connectString,
			String configPrefix, String charset, int maxRetries,
			int baseSleepTime) {
		// TODO Auto-generated method stub
		System.out.println("zkclient started ....");
		
	}

}
