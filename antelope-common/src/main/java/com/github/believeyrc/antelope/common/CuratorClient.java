package com.github.believeyrc.antelope.common;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.CuratorFrameworkFactory.Builder;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jmx.export.UnableToRegisterMBeanException;

import com.github.believeyrc.antelope.common.support.NodeListener;


public class CuratorClient extends AbstractClient{
	
	private final Logger logger = LoggerFactory.getLogger(CuratorClient.class);
	
	private CuratorFramework client;
	
	private String configPrefix;
	
	private String charset;
	
	public CuratorClient(){
		
	}
	
	public CuratorClient(String namespace, String connectString,
			String configPrefix, String charset, int maxRetries, int baseSleepTime) {
		this.configPrefix = configPrefix;
		this.charset = charset;
		try {
			RetryPolicy retryPolicy = new ExponentialBackoffRetry(baseSleepTime, maxRetries);
			Builder builder = CuratorFrameworkFactory.builder().retryPolicy(retryPolicy)
					.connectString(connectString);
			if (namespace != null && !"".equals(namespace)) {
				builder.namespace(namespace);
			}
			client = builder.build();
			client.getConnectionStateListenable().addListener(new ConnectionStateListener() {
				@Override
				public void stateChanged(CuratorFramework client, ConnectionState newState) {
					System.out.println("curator " + newState);
					if (newState == ConnectionState.CONNECTED) {
						
					} else if (newState == ConnectionState.RECONNECTED) {
						
					} else if (newState == ConnectionState.LOST) {
						
					} else if (newState == ConnectionState.SUSPENDED) {
						
					}
					
				}
			});
			client.start();
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
		
	}


	@Override
	public String getData(String path) {
		try {
			if (client.checkExists().forPath(path) != null) {
				return new String(client.getData().forPath(path), charset);
			} else {
				return null;
			}
		} catch (UnsupportedEncodingException e) {
			throw new UnableToRegisterMBeanException(e.getMessage(), e);
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}


	@Override
	public List<String> getChildren(String path) {
		try {
			return client.getChildren().forPath(path);
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}


	@Override
	public void addListener(String path, NodeListener listener) {

	 	final NodeCache nodeCache = new NodeCache(client, path);
	 	try {
			nodeCache.start(true);
		} catch (Exception e) {
			try {
				nodeCache.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			throw new IllegalStateException(e.getMessage(), e);
			
		} finally {

		}
	 	nodeCache.getListenable().addListener(new NodeCacheListenerImpl(listener, nodeCache, path));
	
		
	}


	@Override
	public String getConfigPrefix() {
		return configPrefix;
	}


	@Override
	protected boolean checkExists(String path) {
		boolean exist = false;
		try {
			if (client.checkExists().forPath(path) != null) {
				exist = true;
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return exist;
	}


	@Override
	protected void createPersistent(String path, String data) {
		try {
			client.create().creatingParentsIfNeeded()
					.withMode(CreateMode.PERSISTENT).forPath(path, data.getBytes(charset));
					
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
		
	}


	@Override
	protected void createEphemeral(String path, String data) {
		try {
			client.create().creatingParentsIfNeeded()
					.withMode(CreateMode.EPHEMERAL).forPath(path, data.getBytes(charset));
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
		
	}


	@Override
	protected void deletePath(String path) {
		try {
			client.delete().forPath(path);
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
		
	}


	@Override
	protected void setPathData(String path, String data) {
		try {
			client.setData().forPath(path, data.getBytes(charset));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}


	@Override
	protected String getConfigPath(String path) {
		return configPrefix + "/" + path;
	}


class NodeCacheListenerImpl implements NodeCacheListener {
		
		private NodeListener nodeListener;
		
		private NodeCache nodeCache;
		
		private String path;
		
		public NodeCacheListenerImpl(NodeListener nodeListener, NodeCache nodeCache, String path) {
			this.nodeListener = nodeListener;
			this.nodeCache = nodeCache;
			this.path = path;
		}

		@Override
		public void nodeChanged() throws Exception {
			String data = null;
			if (nodeCache.getCurrentData() != null) {
				data = new String(nodeCache.getCurrentData().getData());
			}
			logger.info("node changed : path {} data  {}", path, data);
			nodeListener.nodeChanged(path, data);
			
		}
		
	}


	@Override
	public void close() {
		System.out.println("client close ...");
		client.close();
		
	}

	@Override
	public void init(String namespace, String connectString,
			String configPrefix, String charset, int maxRetries,
			int baseSleepTime) {
		// TODO Auto-generated method stub
		this.configPrefix = configPrefix;
		this.charset = charset;
		try {
			RetryPolicy retryPolicy = new ExponentialBackoffRetry(baseSleepTime, maxRetries);
			Builder builder = CuratorFrameworkFactory.builder().retryPolicy(retryPolicy)
					.connectString(connectString);
			if (namespace != null && !"".equals(namespace)) {
				builder.namespace(namespace);
			}
			client = builder.build();
			client.getConnectionStateListenable().addListener(new ConnectionStateListener() {
				@Override
				public void stateChanged(CuratorFramework client, ConnectionState newState) {
					System.out.println("curator " + newState);
					if (newState == ConnectionState.CONNECTED) {
						
					} else if (newState == ConnectionState.RECONNECTED) {
						
					} else if (newState == ConnectionState.LOST) {
						
					} else if (newState == ConnectionState.SUSPENDED) {
						
					}
					
				}
			});
			client.start();
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
		
	}

	
}
