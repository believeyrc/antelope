package com.github.believeyrc.antelope.common;


import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.CuratorFrameworkFactory.Builder;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jmx.export.UnableToRegisterMBeanException;

import com.github.believeyrc.antelope.common.support.ChildrenListener;
import com.github.believeyrc.antelope.common.support.NodeListener;

public class CuratorZookeeperClient extends AbstractZookeeperClient {
	
	private CuratorFramework client;
	
	//private ConcurrentMap<String, Set<NodeCache>> nodeCacheMap = new ConcurrentHashMap<String, Set<NodeCache>>();
	
	private Logger logger = LoggerFactory.getLogger(CuratorZookeeperClient.class);
	
	@Override
	public void delete(String path) {
		try {
			client.delete().forPath(path);
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	@Override
	public void setData(String path, String data) {
		try {
			client.setData().forPath(path, data.getBytes(getCharset()));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getData(String path) {
		try {
			if (client.checkExists().forPath(path) != null) {
				return new String(client.getData().forPath(path), getCharset());
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
	public void createPersistent(String path, String data) {
		try {
			client.create().creatingParentsIfNeeded()
					.withMode(CreateMode.PERSISTENT).forPath(path, data.getBytes(getCharset()));
					
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	@Override
	public void createEphemeral(String path, String data) {
		
		try {
			client.create().creatingParentsIfNeeded()
					.withMode(CreateMode.EPHEMERAL).forPath(path, data.getBytes(getCharset()));
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	@Override
	protected void doClose() {
		System.out.println("do close.....");
		client.close();
	}

	
	
	@Override
	protected void doInit() {
		System.out.println("do init ...");
		try {
			RetryPolicy retryPolicy = new ExponentialBackoffRetry(getBaseSleepTime(), getMaxRetries());
			Builder builder = CuratorFrameworkFactory.builder().retryPolicy(retryPolicy)
					.connectString(getConnectString());
			if (getNamespace() != null && !"".equals(getNamespace())) {
				builder.namespace(getNamespace());
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
	public void publishConfig(String path, String data) {
		try {
			String pubPath = getConfigPrefix() + "/" + path;
			Stat stat = client.checkExists().forPath(pubPath);
			if (stat == null) {
				createPersistent(pubPath, data);
			} else {
				setData(pubPath, data);
			}
			
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
		
		
	}

	@Override
	public void subscribeConfig(String path) {
		String subPath = getConfigPrefix() + "/" + path;
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
		try {
			Stat stat = client.checkExists().forPath(subPath);
			if (stat != null) {
				configData.put(subPath, getData(subPath));
			}
			
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
		
	}

	@Override
	public String getConfigData(String path) {
		String subPath = getConfigPrefix() + "/" + path;
		return configData.get(subPath);
	}

	@Override
	public void addListener(final String path, NodeListener listener) {
	 	final NodeCache nodeCache = new NodeCache(client, path);
	 	try {
			nodeCache.start(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	 	nodeCache.getListenable().addListener(new NodeCacheListenerImpl(listener, nodeCache, path));
	}

	



	@Override
	public void deleteConfig(String path) {
		String deletePath = getConfigPrefix() + "/" + path;
		try {
			if (client.checkExists().forPath(deletePath) != null) {
				delete(deletePath);
			}
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
		
	}
		
	@Override
	protected void initConfig() {
		List<String> path = getPath();
		if (path != null && path.size() > 0) {
			for (String pt : path) {
				subscribeConfig(pt);
			}
		}
		
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
	
	class PathChildrenCacheListenerImpl implements PathChildrenCacheListener {
		private ChildrenListener childrenListener;
		
		private PathChildrenCache pathChildrenCache;
		
		public PathChildrenCacheListenerImpl(PathChildrenCache pathChildrenCache, 
				ChildrenListener childrenListener) {
			this.pathChildrenCache = pathChildrenCache;
			this.childrenListener = childrenListener;
			
		}
		
		@Override
		public void childEvent(CuratorFramework client,
				PathChildrenCacheEvent event) throws Exception {

			//event.getData().getPath();
		//	event.getData().get
			//childrenListener.childrenChanged("", pathChildrenCache.getCurrentData());
		}
		
	}
	
}
