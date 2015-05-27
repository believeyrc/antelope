package com.github.believeyrc.antelope.common;

import java.util.List;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.believeyrc.antelope.common.support.NodeListener;

public class ZkclientClient extends AbstractClient {
	
	private final Logger logger = LoggerFactory.getLogger(ZkclientClient.class);
	
	private ZkClient client;
	
	private String configPrefix;
	
	private String charset;
	
	private String namespace;
	
	public ZkclientClient() {
		
	}

	@Override
	public void close() {
		client.close();
		
	}

	@Override
	public String getData(String path) {
		return client.readData(path);
	}

	@Override
	public List<String> getChildren(String path) {
		return client.getChildren(path);
	}

	@Override
	public void addListener(String path, final NodeListener listener) {
		client.subscribeDataChanges(path, new IZkDataListener() {
			
			@Override
			public void handleDataDeleted(String dataPath) throws Exception {
				listener.nodeChanged(dataPath, null);
			}
			
			@Override
			public void handleDataChange(String dataPath, Object data) throws Exception {
				listener.nodeChanged(dataPath, (String)data);
			}
		});
		
	}

	@Override
	public String getConfigPrefix() {
		StringBuilder builder = new StringBuilder();
		if (namespace != null && !"".equals(namespace)) {
			builder.append("/");
			builder.append(namespace);
		}
		builder.append(configPrefix);
		return builder.toString();
	}

	@Override
	protected boolean checkExists(String path) {
		return client.exists(path);
	}

	@Override
	protected void createPersistent(String path, String data) {
		client.createPersistent(path, data);
	}

	@Override
	protected void createEphemeral(String path, String data) {
		client.createEphemeral(path, data);
	}

	@Override
	protected void deletePath(String path) {
		client.delete(path);
	}

	@Override
	protected void setPathData(String path, String data) {
		client.writeData(path, data);
	}

	@Override
	protected String getConfigPath(String path) {
		return getConfigPrefix() + "/" + path;
	}

	@Override
	public void init(String namespace, String connectString,
			String configPrefix, String charset, int maxRetries,
			int baseSleepTime) {
		try {
			int connectionTimeout = 60000;
			this.charset = charset;
			this.configPrefix = configPrefix;
			this.namespace = namespace;
			client = new ZkClient(connectString, connectionTimeout);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		
		
	}

}
