package com.yianit.admin.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSON;
import com.yianit.admin.service.auth.TestService;
import com.yianit.exception.LogisHandleException;

import hl.king.mybatis.spring.common.controller.BaseController;
@Controller("adminController")
@RequestMapping("/admin/main")
public class AdminController extends BaseController{
	@Autowired
	@Qualifier("testService")
	private TestService testService;
	@RequestMapping("/queryUser")
    public String login1(HttpSession session, HttpServletRequest req, HttpServletResponse response, Model model)
            throws LogisHandleException {
		req.setAttribute("aaaa", JSON.toJSONString(testService.testmoresource()));
    	testService.addTest1();
        return "admin/user.html";
    }
}
