package com.fijo.cores.web;

import com.wordnik.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@Controller
public class WelcomeController {

    @RequestMapping("action/welcome")
    @ApiOperation(value = "欢迎页", httpMethod = "GET", notes = "欢迎页", response = ModelAndView.class
            ,consumes="application/x-www-form-urlencoded")
    public ModelAndView welcome() {
    	ModelAndView mav = new ModelAndView("welcome");
        return mav;
    }
}
