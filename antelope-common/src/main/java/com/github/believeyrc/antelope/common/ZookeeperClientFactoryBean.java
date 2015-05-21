package com.github.believeyrc.antelope.common;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.beans.factory.FactoryBean;

import com.github.believeyrc.antelope.common.support.ClientConfigurationSupport;

public class ZookeeperClientFactoryBean extends ClientConfigurationSupport
			implements FactoryBean<ClientSupport> {
	
	private ClientSupport zkClient;

	//private Set<String,Map<String,?>> clientSet = new HashSet<E>(arg0)
	
	private static final ConcurrentMap<Class<?>, ConcurrentMap<String, Object>> 
		EXETENSION = new ConcurrentHashMap<Class<?>, ConcurrentMap<String, Object>>();
	
	private Map<String, Class<?>> clientHolder = new HashMap<String, Class<?>>();
	
	
	
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
	
	private ClientSupport createClient(String clientType,String namespace, String connectString,
			String configPrefix, String charset, int maxRetries, int baseSleepTime) {
		ClientSupport clientSupport = null;
		ServiceLoader<ClientSupport> serviceLoader = ServiceLoader.load(ClientSupport.class);
		Iterator<ClientSupport> clientIterator = serviceLoader.iterator();
		ConcurrentMap<String, Object> clientInstances = null;
		while (clientIterator.hasNext()) {
			ClientSupport client = clientIterator.next();
			clientInstances = EXETENSION.get(ClientSupport.class);
			if (clientInstances == null) {
				EXETENSION.putIfAbsent(ClientSupport.class, new ConcurrentHashMap<String, Object>());
			}
			clientInstances = EXETENSION.get(ClientSupport.class);
			clientInstances.put(client.getClass().getSimpleName(), client);
			if (client.getClass().getSimpleName().toLowerCase().startsWith(clientType.toLowerCase())) {
				client.init(namespace, connectString, configPrefix, charset, maxRetries, baseSleepTime);
				clientSupport = client;
			}
		}
		
		/*SPI spi = ClientSupport.class.getAnnotation(SPI.class);
		String defaultClient = spi.value();
		defaultClient.startsWith(clientType);
		System.out.println(defaultClient);*/
		
		return clientSupport;
		/*if ("zkclient".equals(clientType)) {
			return null;
		} else {
			return new CuratorClient(namespace, connectString, 
					configPrefix, charset, maxRetries, baseSleepTime);
		}*/
		
		
	}
/*
	@Override
	protected void initializeSPIAnnotation() {
		// TODO Auto-generated method stub
		System.out.println(applicationContext);
		Map<String, Object> spiMap = applicationContext.getBeansWithAnnotation(SPI.class);
		for (Object spi : spiMap.values()) {
			Class<? extends Object> spiImpl = spi.getClass();
			System.out.println("spi impl : " + spiImpl.getName());
		}
	}*/
	

}
