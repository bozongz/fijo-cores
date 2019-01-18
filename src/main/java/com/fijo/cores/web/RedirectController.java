package com.fijo.cores.web;

import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = "/action/redirect")
public class RedirectController {
    public transient final Log log = LogFactory.getLog(getClass());

    /**
     * 用户跳转JSP页面
     * <p>
     * 此方法不考虑权限控制
     *
     * @param pageName JSP名称(不加后缀)
     * @return 指定JSP页面
     */
    @RequestMapping("/{pageName}")
    @ApiOperation(value = "页面转发", httpMethod = "GET", notes = "application/x-www-form-urlencoded", response = String.class
            , consumes = "application/x-www-form-urlencoded")
    public String redirectView1(@ApiParam(required = true, name = "pageName", value = "页面名称") @PathVariable String pageName) {
        log.debug("redirectView1: Redirect to page: " + pageName);
        return pageName;
    }

    /**
     * 用户跳转JSP页面
     * <p>
     * 此方法不考虑权限控制
     *
     * @param folder   路径
     * @param pageName JSP名称(不加后缀)
     * @return 指定JSP页面
     * <p>
     * 例如用法：webapp/portal/index.jsp 中调用连接  ${ctx}/redirect/portal/about'
     */
    @RequestMapping("/{folder}/{pageName}")
    @ApiOperation(value = "页面转发", httpMethod = "GET", notes = "application/x-www-form-urlencoded", response = String.class
            , consumes = "application/x-www-form-urlencoded")
    public String redirectView2(@ApiParam(required = true, name = "pageName", value = "页面父层路径") @PathVariable String folder,
                                @ApiParam(required = true, name = "pageName", value = "页面名称") @PathVariable String pageName) {
        log.debug("redirectView2: Redirect to page: " + folder + "/" + pageName);
        return folder + "/" + pageName;
    }

    @RequestMapping("/view/{folder}/{pageName}")
    @ApiOperation(value = "页面转发", httpMethod = "GET", notes = "application/x-www-form-urlencoded", response = ModelAndView.class
            , consumes = "application/x-www-form-urlencoded")
    public ModelAndView redirectView3(@ApiParam(required = true, name = "pageName", value = "页面父层路径") @PathVariable String folder,
                                      @ApiParam(required = true, name = "pageName", value = "页面名称") @PathVariable String pageName) {
        log.debug("redirectView3: Redirect to page: " + folder + "/" + pageName);
        ModelAndView mav = new ModelAndView();
        mav.setViewName(folder + "/" + pageName);
        return mav;
    }
}
