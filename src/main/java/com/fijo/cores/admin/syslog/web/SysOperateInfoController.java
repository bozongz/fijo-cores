package com.fijo.cores.admin.syslog.web;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.fijo.cores.admin.syslog.service.ISysOperateInfoService;
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
import com.fijo.cores.admin.syslog.model.SysOperateInfo;
import com.fijo.cores.admin.syslog.model.SysOperateInfoExample;
import com.fijo.cores.admin.syslog.model.SysOperateInfoExample.Criteria;
import com.fijo.cores.admin.syslog.service.ISysOperateInfoService;

@Controller
@RequestMapping("/action/admin/syslog/operate")
public class SysOperateInfoController {
	public final Log log = LogFactory.getLog(getClass());

	@Resource(name = "sysOperateInfoService")
	private ISysOperateInfoService sysOperateInfoService;

	@InitBinder
	private void initBinder(ServletRequestDataBinder binder){
		binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
	}

	@RequiresPermissions("admin:syslog:operate:submenu")
	@RequestMapping(value = "/sysOperateInfoList", method = RequestMethod.GET)
    @ApiOperation(value = "打开操作日志表单", httpMethod = "GET", notes = "打开操作日志表单",
            consumes="application/x-www-form-urlencoded")
	public ModelAndView sysOperateInfoList() throws Exception {
		ModelAndView mav = new ModelAndView();
	    mav.addObject("topNav", "sysAdmin"); //topNav: 顶部导航的key
	    mav.addObject("sideBar", "syslog"); //sideBar：左侧导航的title
	    mav.addObject("subNav", "operate"); //subNav：左侧导航的二级导航
        mav.addObject("sidebar", "sysAdmin"); //sidebar: 左侧导航文件的名字
        return mav;
	}
	
	@RequiresPermissions("admin:syslog:operate:query")	
	@RequestMapping(value = "/query", method = RequestMethod.GET)
	@ResponseBody
    @ApiOperation(value = "查询操作日志", httpMethod = "GET", response = Map.class, notes = "查询操作日志",
            consumes="application/x-www-form-urlencoded")
	public Map<String, Object> query(SysOperateInfo o) throws Exception {
		Map<String, Object> result = Maps.newHashMap();
		SysOperateInfoExample ex = new SysOperateInfoExample();
		Criteria c = ex.createCriteria();
		c.andLoginnameNotEqualTo("supervisor");		
		ex.setOrderByClause("operatetime desc");
		List<SysOperateInfo> list = sysOperateInfoService.selectByExample(ex);
		result.put("iTotalRecords", list.size());
		result.put("iTotalDisplayRecords", list.size());
		result.put("aaData", list);
		return result;
	}

}
