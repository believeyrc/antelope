#Antelope#
-----------
### 项目描述：
基于 Zookeeper 的分布式配置管理平台，实现对业务系统的环境变量进行统一的配置与管理。

### 项目特点：
1. 数据结构为 Key-Value，采用发布-订阅模式，服务端主动推送数据变更。
2. 采用 Spring XML 配置进行应用整合，使用方便，无代码侵入。
3. 采用 SPI 机制，可动态扩展实现机制和业务处理。

### 项目使用

在项目pom文件中加入：

	<dependency>
		<groupId>com.github.believeyrc</groupId>
		<artifactId>antelope-common</artifactId>
		<version>0.0.1-SNAPSHOT</version>
	</dependency>

在Spring配置文件中加入：

	<bean class="com.github.believeyrc.antelope.common.ZookeeperClientFactoryBean">
		<property name="connectString" value="192.168.1.100:2181"></property>
		<property name="configPrefix" value="/configPrefix" />
		<property name="client" value="zkclient"></property>
		<property name="path" >
			<list>
				<value>order/timeout</value> <!-- 订阅配置 -->
			</list>
		</property>
	</bean>

获取客户端：
	
	import com.github.believeyrc.antelope.common.ConfigClient;
	@Autowired
	private ConfigClient configClient;

使用客户端
	
	configClient.publishConfig("order/timeout", "60");	//发布配置
	configClient.getConfigData("order/timeout");		//获取配置
	configClient.subscribeConfig("order/timeout");		//订阅配置
	configClient.deleteConfig("order/timeout");		//删除配置

### 配置控制台
将antelope-admin.war部署至Tomcat

页面快照 ![](http://dl2.iteye.com/upload/attachment/0109/1257/c57c43af-5f4e-3925-9c40-daf5014c7fd7.png)


	