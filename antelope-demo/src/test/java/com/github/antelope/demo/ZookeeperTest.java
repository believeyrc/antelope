package com.github.antelope.demo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.github.believeyrc.antelope.common.ConfigClient;
import com.github.believeyrc.antelope.common.ZookeeperClient;
import com.github.believeyrc.antelope.common.support.NodeListener;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
locations = "classpath:spring/applicationContext.xml"
		)
public class ZookeeperTest {
	
	@Autowired
	private ZookeeperClient client;
	@Autowired
	private ConfigClient configService;
	
	@Test
	public void subTest() throws InterruptedException {
		
		String path = "/yrc";
		client.addListener(path, new NodeListener() {
			
			@Override
			public void nodeChanged(String path, String data) {
				String t = client.getData(path);
				
				System.out.println("listener 1 " + path + " data changed , " + t);
			}
		});
		client.addListener(path, new NodeListener() {
			
			@Override
			public void nodeChanged(String path, String data) {
				String t = client.getData(path);
				System.out.println("listener 2 " + path + " data changed , " + t);
				
			}
		});
		Thread.sleep(1000);
		client.setData(path, "hello");
		Thread.sleep(1000);
		client.setData(path, "world");
		Thread.sleep(1000);
		client.setData(path, "sfbest");
	//	Thread.sleep(1000);
	//	client.delete(path);
		Thread.sleep(50000);
	}
	
	@Test
	public void pubTest() throws InterruptedException {
		String path = "/yrc";
		String data = "hello1";
		//String r =  client.createPersistent(path, data);
		client.setData(path, data);
		Thread.sleep(200);
		client.setData(path, "32");
	//	System.out.println(r);
	}
	
	@Test
	public void configTest() throws InterruptedException {
		String path = "name2";
		//configService.delete(path);
		
		configService.publishConfig(path, "sfbest");
		//configService.deleteConfig(path);
	//	configService.subscribeConfig(path);
		System.out.println(configService.getConfigData(path));
		//configService.subscribeConfig(path);
		configService.publishConfig(path, "king");
		Thread.sleep(10);
		System.out.println(configService.getConfigData(path));
		
		configService.publishConfig(path, "fasf");
		Thread.sleep(10);
		System.out.println(configService.getConfigData(path));
		
		Thread.sleep(50000);
	}
	@Test
	public void confTest() throws InterruptedException {
		String p = "order/name";
		String p2 = "test/config";
		System.out.println(configService.getConfigData(p));
		System.out.println(configService.getConfigData(p2));
		Thread.sleep(500);
		configService.publishConfig(p, "name======");
		configService.publishConfig(p2, "config-------");
		//Thread.sleep(500);
	}

}
