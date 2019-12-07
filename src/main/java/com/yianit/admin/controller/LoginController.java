package com.yianit.admin.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yianit.admin.service.auth.TestService;
import com.yianit.common.AjaxJsonResponseWraper;
import com.yianit.common.util.TokenUtil;
import com.yianit.exception.LogisHandleException;
import com.yianit.model.AccessUser;

import hl.king.mybatis.spring.common.controller.BaseController;

/**
 * 类名称: LoginController 类描述: 登录处理. 创建人: zhangwei 创建时间: 2015年9月9日 下午2:48:39 修改人:
 * zhangwei 修改时间: 2015年9月9日 下午2:48:39 修改备注:
 */

@Controller("loginController")
public class LoginController extends BaseController{
    @Autowired
	@Qualifier("tokenUtil")
	private TokenUtil tokenUtil;
    /**
     * signLogin:(ajax登录验证). <br/>
     * 
     * @author zhangwei
     * @param session
     * @param req
     * @param response
     * @param model
     * @return
     * @throws LogisHandleException
     * @since JDK 1.7
     */
    @ResponseBody
    @RequestMapping("/signLogin.json")
    public Map<String, Object> signLogin(HttpSession session, HttpServletRequest req, HttpServletResponse response,
            Model model) throws LogisHandleException {
        Map<String, Object> r = new HashMap<String, Object>();
        AccessUser accessUser = tokenUtil.initAccessUser(req);       
        accessUser.setHasLogin(true);
        accessUser.setAccessId("1");
        accessUser.setLoginId(1);
        tokenUtil.addAccessUser(req, response, "user_normal", accessUser.getLoginId().toString(), accessUser,false);
        Map<String, Object> ret = AjaxJsonResponseWraper.createSuccessResponseWithData(r);
        return ret;
    }

    /**
     * loginOut:(注销). <br/>
     * 
     * @author zhangwei
     * @param session
     * @param req
     * @param response
     * @param model
     * @return
     * @throws LogisHandleException
     * @since JDK 1.7
     */

    @RequestMapping("/loginOut.html")
    public String loginOut(HttpSession session, HttpServletRequest req, HttpServletResponse response, Model model)
            throws LogisHandleException {
    	tokenUtil.removeAccessUser(req, response, "user_normal");
        return "redirect:/login.html";
    }
    @RequestMapping("/login")
    public String login1(HttpSession session, HttpServletRequest req, HttpServletResponse response, Model model)
            throws LogisHandleException {
        return "admin/login.html";
    }
    @ResponseBody
    @RequestMapping("/loginj")
    public Map<String, Object> loginj(HttpSession session, HttpServletRequest req, HttpServletResponse response, Model model)
            throws LogisHandleException {
    	Map<String, Object> ret = AjaxJsonResponseWraper.createSuccessResponseWithData("ssss");
        return ret;
    }
}
