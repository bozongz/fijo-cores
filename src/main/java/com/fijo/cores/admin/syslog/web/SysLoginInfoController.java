package com.fijo.cores.admin.syslog.web;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.fijo.cores.admin.syslog.service.ISysLoginInfoService;
import com.wordnik.swagger.annotations.ApiOperation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Maps;
import com.fijo.cores.admin.syslog.model.SysLoginInfo;
import com.fijo.cores.admin.syslog.model.SysLoginInfoExample;
import com.fijo.cores.admin.syslog.model.SysLoginInfoExample.Criteria;
import com.fijo.cores.admin.syslog.service.ISysLoginInfoService;

@Controller
@RequestMapping("/action/admin/syslog/login")
public class SysLoginInfoController {

	public final Log log = LogFactory.getLog(getClass());

	@Resource(name = "sysLoginInfoService")
	private ISysLoginInfoService sysLoginInfoService;

	@InitBinder
	private void initBinder(ServletRequestDataBinder binder) {
		// bind empty strings as null
		binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
	}

	@RequiresPermissions("admin:syslog:login:submenu")
	@RequestMapping(value = "/syslogininfoList", method = RequestMethod.GET)
    @ApiOperation(value = "打开登陆日志表单", httpMethod = "GET", notes = "打开登陆日志表单",
            consumes="application/x-www-form-urlencoded")
	public ModelAndView syslogininfoList() throws Exception {
		ModelAndView mav = new ModelAndView();
	    mav.addObject("topNav", "sysAdmin"); //topNav: 顶部导航的key
	    mav.addObject("sideBar", "syslog"); //sideBar：左侧导航的title
	    mav.addObject("subNav", "login"); //subNav：左侧导航的二级导航
        mav.addObject("sidebar", "sysAdmin"); //sidebar: 左侧导航文件的名字
        return mav;
	}

	@RequiresPermissions("admin:syslog:login:query")
	@RequestMapping(value = "/query", method = RequestMethod.GET)
	@ResponseBody
    @ApiOperation(value = "查询登陆日志", httpMethod = "GET", response = Map.class, notes = "查询登陆日志",
            consumes="application/x-www-form-urlencoded")
	public Map<String, Object> query() throws Exception {
		Map<String, Object> result = Maps.newHashMap();
		SysLoginInfoExample ex = new SysLoginInfoExample();
		Criteria c = ex.createCriteria();
		c.andUseraccountNotEqualTo("supervisor");		
		ex.setOrderByClause("logintime desc");
		List<SysLoginInfo> list = sysLoginInfoService.selectByExample(ex);
		result.put("iTotalRecords", list.size());
		result.put("iTotalDisplayRecords", list.size());
		result.put("aaData", list);
		return result;
	}

}
