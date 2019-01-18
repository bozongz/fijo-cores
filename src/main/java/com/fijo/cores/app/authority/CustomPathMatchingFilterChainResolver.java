package com.fijo.cores.app.authority;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.web.filter.mgt.FilterChainManager;
import org.apache.shiro.web.filter.mgt.PathMatchingFilterChainResolver;

/**
 * <p>User: Zhang Kaitao
 * <p>Date: 14-2-25
 * <p>Version: 1.0
 */
public class CustomPathMatchingFilterChainResolver extends PathMatchingFilterChainResolver {
	
	private static transient final Log log = LogFactory.getLog(CustomPathMatchingFilterChainResolver.class);
	
    private CustomDefaultFilterChainManager customDefaultFilterChainManager;

    public void setCustomDefaultFilterChainManager(CustomDefaultFilterChainManager customDefaultFilterChainManager) {
        this.customDefaultFilterChainManager = customDefaultFilterChainManager;
        setFilterChainManager(customDefaultFilterChainManager);
    }

    @Override
    public FilterChain getChain(ServletRequest request, ServletResponse response, FilterChain originalChain) {
        FilterChainManager filterChainManager = getFilterChainManager();
        if (!filterChainManager.hasChains()) {
            return null;
        }
        String requestURI = getPathWithinApplication(request);
        List<String> chainNames = new ArrayList<String>();
        for (String pathPattern : filterChainManager.getChainNames()) {
        	//校验满足所有规则的URL
//            if (pathMatches(pathPattern, requestURI)) {
//                chainNames.add(pathPattern);
//            }
        	//校验第一个满足规则的URL
        	if (pathMatches(pathPattern, requestURI)) {
        		log.debug("Matched path pattern [" + pathPattern + "] for requestURI [" + requestURI + "].  " +
                        "Utilizing corresponding filter chain...");
                return filterChainManager.proxy(originalChain, pathPattern);
            }
        }

        if(chainNames.size() == 0) {
            return null;
        }

        return customDefaultFilterChainManager.proxy(originalChain, chainNames);
    }
}
