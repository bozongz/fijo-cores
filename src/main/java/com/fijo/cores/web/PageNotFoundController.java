/**
 * 
 */
package com.fijo.cores.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

/**
 * @author zhangbo
 *
 * 注释mvc:default-servlet-handler, 以便可以专门用于处理404错误
 */
public class PageNotFoundController implements Controller{
	public final Log log = LogFactory.getLog(PageNotFoundController.class);
	
	@Override
	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		log.error("Error access resource: "+request.getRequestURI());
		return new ModelAndView("errors/404");
	}

}
