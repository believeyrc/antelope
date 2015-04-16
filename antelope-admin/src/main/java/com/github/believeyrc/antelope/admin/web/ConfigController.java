package com.github.believeyrc.antelope.admin.web;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.believeyrc.antelope.admin.vo.ConfigNodeVo;
import com.github.believeyrc.antelope.common.ConfigClient;
import com.github.believeyrc.antelope.common.ZookeeperClient;

/**
 * 配置管理
 * @author yangrucheng
 * @created 2015年4月15日 上午11:47:39
 * @since 1.0
 * @version 1.0
 *
 */
@RequestMapping(value = "/config")
@Controller
public class ConfigController {
	@Autowired
	private ConfigClient configClient;
	@Autowired
	private ZookeeperClient zookeeperClient;
	
	private Logger logger = LoggerFactory.getLogger(ConfigController.class);
	
	@RequestMapping(value = "/index")
	public String index(HttpServletRequest request, Model model) {
		String type = request.getParameter("type");
		String name = request.getParameter("name");
		List<ConfigNodeVo> configList = new ArrayList<ConfigNodeVo>();
		
		try {
			String root = configClient.getConfigPrefix();
			List<String> typeList = zookeeperClient.getChildren(root);
			if (typeList != null && typeList.size() > 0 && type == null) {
				type = typeList.get(0);
			}
			
			if (type != null && !"".equals(type)) {
				if (name != null && !"".equals(name)) {
					configList.add(getConfigNode(root, type, name));
				} else {
					List<String> nodeList = new ArrayList<String>();
					nodeList = zookeeperClient.getChildren(root + "/" + type);
					for (String node : nodeList) {
						configList.add(getConfigNode(root, type, node));
					}
				}
			}
			model.addAttribute("type", type);
			model.addAttribute("typeList", typeList);
			model.addAttribute("configList", configList);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return "config/index";
	}
	
	@RequestMapping(value = "/publish")
	public String publish(HttpServletRequest request, Model model) {
		String type = request.getParameter("configType");
		String code = request.getParameter("configName");
		String path = type + "/" + code;
		String data = request.getParameter("configValue");
		try {
			configClient.publishConfig(path, data);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		model.addAttribute("type", type);
		return "redirect:index";
	}
	
	@RequestMapping(value = "/get")
	@ResponseBody
	public ConfigNodeVo getData(HttpServletRequest request, Model model) {
		
		String type = request.getParameter("configType");
		String name = request.getParameter("configName");
		ConfigNodeVo configNode = null;
		try {
			configNode = getConfigNode(configClient.getConfigPrefix(), type, name);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return configNode;
	}
	
	@RequestMapping(value = "/delete")
	public String delete(HttpServletRequest request, Model model) {
		String type = request.getParameter("d-configType");
		String name = request.getParameter("d-configName");
		try {
			configClient.deleteConfig(type + "/" + name);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		model.addAttribute("type", type);
		return "redirect:index";
		
	}
	
	private ConfigNodeVo getConfigNode(String root, String type, 
			String name) {
		ConfigNodeVo configNodeVo = null;
		String data = zookeeperClient.getData(root + "/" + type + "/" + name);
		if (data != null) {
			configNodeVo = new ConfigNodeVo();
			configNodeVo.setConfigType(type);
			configNodeVo.setConfigName(name);
			configNodeVo.setConfigValue(data);
		}
		return configNodeVo;
	}
}
