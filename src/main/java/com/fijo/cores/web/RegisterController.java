package com.fijo.cores.web;

import javax.annotation.Resource;
import javax.validation.Valid;

import com.fijo.cores.model.JsonResponse;
import com.fijo.cores.model.JsonResponse;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import com.fijo.cores.admin.authority.model.SysUser;
import com.fijo.cores.admin.authority.service.ISysUserService;

@Controller
@RequestMapping(value = "/action/register")
public class RegisterController {

	@Resource(name = "sysUserService")
	private ISysUserService sysUserService;

	@RequestMapping(method = RequestMethod.GET)
	public String registerForm() {
		return "account/register";
	}

	@RequestMapping(method = { RequestMethod.POST })
    @ApiOperation(value = "注册", httpMethod = "POST", notes = "注册", response = JsonResponse.class,
            produces="application/json",consumes="application/x-www-form-urlencoded")
	public RedirectView register(@ApiParam(required=true, value="用户")@Valid SysUser sysUser,
                                 RedirectAttributes redirectAttributes) {
		sysUserService.create(sysUser);
		redirectAttributes.addFlashAttribute(FormAuthenticationFilter.DEFAULT_USERNAME_PARAM, sysUser.getLoginName());
		return new RedirectView("/login", true, false, false);
		//return "redirect:/login";
	}

	/**
	 * Ajax请求校验loginName是否唯一。
	 */
	@RequestMapping(value = "checkLoginName",method = { RequestMethod.POST })
	@ResponseBody
    @ApiOperation(value = "校验用户名唯一", httpMethod = "POST", notes = "校验用户名唯一", response = String.class,
            produces="application/json",consumes="application/x-www-form-urlencoded")
	public String checkLoginName(@ApiParam(required=true, value="用户名")@RequestParam("loginName") String loginName) {
		if (sysUserService.getByUnique(loginName) == null) {
			return "true";
		} else {
			return "false";
		}
	}
}