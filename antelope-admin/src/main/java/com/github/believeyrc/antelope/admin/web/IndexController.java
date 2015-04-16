package com.github.believeyrc.antelope.admin.web;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController {
	
	@RequestMapping(value = "/index")
	public String index(HttpServletRequest request) {
		return "index";
	}

	@RequestMapping(value = "/login")
	public String login(HttpServletRequest request) {
		return "login";
	}
}
