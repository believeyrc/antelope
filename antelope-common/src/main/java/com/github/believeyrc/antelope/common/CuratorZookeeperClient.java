package com.github.believeyrc.antelope.common;


import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.data.Stat;
import org.springframework.jmx.export.UnableToRegisterMBeanException;

import com.github.believeyrc.antelope.common.support.NodeListener;
import com.github.believeyrc.antelope.common.support.ZookeeperListener;

public class CuratorZookeeperClient extends AbstractZookeeperClient<ZookeeperListener> {
	
	private CuratorFramework client;
	
	private ConcurrentMap<String, Set<NodeCache>> nodeCacheMap = new ConcurrentHashMap<String, Set<NodeCache>>();
	
	@Override
	public void delete(String path) {
		try {
			client.delete().forPath(path);
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	@Override
	public String setData(String path, String data) {
		Stat stat = null;
		try {
			stat = client.setData().forPath(path, data.getBytes("utf-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String getData(String path) {
		try {
			return new String(client.getData().forPath(path), "utf-8");
		} catch (UnsupportedEncodingException e) {
			throw new UnableToRegisterMBeanException(e.getMessage(), e);
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	@Override
	public List<String> getChildren(String path) {
		// TODO Auto-generated method stub
		return null;
	}

	class CuratorDataWatcher implements CuratorWatcher {
		private volatile NodeListener listener;
		
		public CuratorDataWatcher(NodeListener listener) {
			this.listener = listener;
		}

		@Override
		public void process(WatchedEvent event) throws Exception {
			listener.nodeChanged(event.getPath(),"");
			//String r = addDataListenerTarget(event.getPath(), getDataListenerTarget(event.getPath(), listener));
			//System.out.println("r = " + r);
			//addListener(event.getPath(), listener);
			
			
		}
		
	}

	class CuratorDataListener implements NodeCacheListener {

		private volatile NodeListener dataListener;
		
		public CuratorDataListener(NodeListener dataListener) {
			this.dataListener = dataListener;
		}
		
		@Override
		public void nodeChanged() throws Exception {
			dataListener.nodeChanged("", "");
		}
		
	}
	
	

	@Override
	public String createPersistent(String path, String data) {
		try {
			return client.create().withMode(CreateMode.PERSISTENT).forPath(path, data.getBytes("utf-8"));
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	@Override
	public String createEphemeral(String path) {
		
		try {
			return client.create().withMode(CreateMode.EPHEMERAL).forPath(path);
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
			RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
			client = CuratorFrameworkFactory.builder().namespace("yrc").retryPolicy(retryPolicy)
					.connectString(getConnectString())
					.build();
			
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
			String configPrefix = getConfigPrefix();
			String p = configPrefix + "/" + path;
			//client.create().creatingParentsIfNeeded().forPath(p);
			Stat stat = client.checkExists().forPath(configPrefix);
			if (stat == null) {
				createPersistent(configPrefix, "ee");
			}
			Stat stat2 = client.checkExists().forPath(p);
			if (stat2 == null) {
				createPersistent(p, data);
			} else {
				setData(p, data);
			}
			
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
		
		
	}

	@Override
	public void subscribeConfig(String path) {
		String realPath = getConfigPrefix() + "/" + path;
		addListener(realPath, new NodeListener() {
			@Override
			public void nodeChanged(String path, String data) {
				configData.put(path, data);
			}
		});
		try {
			Stat stat = client.checkExists().forPath(realPath);
			if (stat != null) {
				configData.put(realPath, getData(realPath));
			}
			
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
		
	}

	@Override
	public String getConfigData(String path) {
		String realPath = getConfigPrefix() + "/" + path;
		return configData.get(realPath);
	}

	@Override
	public String addListener(final String path, NodeListener listener) {
	 	final NodeCache nodeCache = new NodeCache(client, path);
	 	try {
			nodeCache.start(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	 	nodeCache.getListenable().addListener(new NodeCacheListenerImpl(listener, nodeCache));
	 	
	 	
		return null;
	}

	class NodeCacheListenerImpl implements NodeCacheListener {
	
		private NodeListener nodeListener;
		
		private NodeCache nodeCache;
		
		public NodeCacheListenerImpl(NodeListener nodeListener, NodeCache nodeCache) {
			this.nodeListener = nodeListener;
			this.nodeCache = nodeCache;
		}

		@Override
		public void nodeChanged() throws Exception {
			String path = nodeCache.getCurrentData().getPath();
			String data = new String(nodeCache.getCurrentData().getData());
			nodeListener.nodeChanged(path, data);
			
		}
		
	}



	@Override
	public void deleteConfig(String path) {
		delete(getConfigPrefix() + "/" + path);
	}

}
