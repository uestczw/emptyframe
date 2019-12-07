package com.yianit.admin.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController("userController")
public class UserController {
	private static Logger LOG = LoggerFactory.getLogger(UserController.class);
	@RequestMapping("/user")
    public String login1(@RequestParam("dddd")String name){
		LOG.info("UserController:"+name);
        return "UserController:"+name;
    }
}
