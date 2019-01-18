package com.fijo.cores.web;

import com.wordnik.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = {"/action/sso", //SSO跳转，Shrio不拦截
        "/action"}) //后台管理跳转，Shrio拦截校验权限
public class IndexController {

    @RequestMapping("/")
    @ApiOperation(value = "项目首页", httpMethod = "GET", consumes="application/x-www-form-urlencoded", response = ModelAndView.class)
    public ModelAndView index() {
        ModelAndView mav = new ModelAndView("index");
        return mav;
    }

}
