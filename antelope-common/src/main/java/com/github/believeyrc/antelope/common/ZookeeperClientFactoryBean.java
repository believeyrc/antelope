package com.github.believeyrc.antelope.common;

import java.util.List;

import org.springframework.beans.factory.FactoryBean;

import com.github.believeyrc.antelope.common.support.ClientConfigurationSupport;

public class ZookeeperClientFactoryBean extends ClientConfigurationSupport
			implements FactoryBean<ClientSupport> {
	
	private ClientSupport zkClient;

	@Override
	public ClientSupport getObject() throws Exception {
		return zkClient;
	}

	@Override
	public Class<? extends ClientSupport> getObjectType() {
		return ClientSupport.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	


	@Override
	protected void doClose() {
		zkClient.close();
		
	}

	@Override
	protected void initializeClient(String client, String namespace, String connectString,
			String configPrefix, String charset, int maxRetries, int baseSleepTime) {
		zkClient = createClient(client, namespace, connectString, 
				configPrefix, charset, maxRetries, baseSleepTime);
		
	}

	@Override
	protected void initializeConfig(List<String> pathList) {
		if (pathList != null && pathList.size() > 0) {
			for (String path : pathList) {
				zkClient.subscribeConfig(path);
			}
		}
	}
	
	private ClientSupport createClient(String client,String namespace, String connectString,
			String configPrefix, String charset, int maxRetries, int baseSleepTime) {
		if ("zkclient".equals(client)) {
			return null;
		} else {
			return new CuratorClient(namespace, connectString, 
					configPrefix, charset, maxRetries, baseSleepTime);
		}
	}
	

}
