/**
 * 版权所有 © 北京晟壁科技有限公司 2008-2016。保留一切权利!
 */
package com.fijo.cores.web.filter;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.owasp.validator.html.AntiSamy;
import org.owasp.validator.html.CleanResults;
import org.owasp.validator.html.Policy;
import org.owasp.validator.html.PolicyException;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用途：防御XSS跨站脚本攻击
 * 作者: zhangbo
 * 时间: 2016-04-19  23:10
 * 参考：
 * http://www.lai18.com/content/1956704.html
 * https://github.com/barrypitman/antisamy-servlet-filter
 */
public class AntiSamyFilter implements Filter {

    public static transient final Log log = LogFactory.getLog(AntiSamyFilter.class);

    /**
     * AntiSamy is unfortunately not immutable, but is threadsafe if we only call
     * {@link AntiSamy#scan(String taintedHTML, int scanType)}
     */
    private final AntiSamy antiSamy;

    public AntiSamyFilter() {
        try {
            InputStream is = this.getClass().getResourceAsStream("/antisamy/antisamy-ebay.xml");
            Policy policy = Policy.getInstance(is);
            antiSamy = new AntiSamy(policy);
        } catch (PolicyException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (request instanceof HttpServletRequest) {
            CleanServletRequest cleanRequest = new CleanServletRequest((HttpServletRequest) request, antiSamy);
            chain.doFilter(cleanRequest, response);
        } else {
            chain.doFilter(request, response);
        }
    }

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void destroy() {
    }

    /**
     * Wrapper for a {@link HttpServletRequest} that returns 'safe' parameter values by
     * passing the raw request parameters through the anti-samy filter. Should be private
     */
    public static class CleanServletRequest extends HttpServletRequestWrapper {

        private final AntiSamy antiSamy;

        private CleanServletRequest(HttpServletRequest request, AntiSamy antiSamy) {
            super(request);
            this.antiSamy = antiSamy;
        }

        /**
         * overriding getParameter functions in {@link ServletRequestWrapper}
         */
        @Override
        public String[] getParameterValues(String name) {
            String[] originalValues = super.getParameterValues(name);
            if (originalValues == null) {
                return null;
            }
            List<String> newValues = new ArrayList<String>(originalValues.length);
            for (String value : originalValues) {
                newValues.add(filterString(value));
            }
            return newValues.toArray(new String[newValues.size()]);
        }

        @Override
        @SuppressWarnings("unchecked")
        public Map getParameterMap() {
            Map<String, String[]> originalMap = super.getParameterMap();
            Map<String, String[]> filteredMap = new ConcurrentHashMap<String, String[]>(originalMap.size());
            for (String name : originalMap.keySet()) {
                filteredMap.put(name, getParameterValues(name));
            }
            return Collections.unmodifiableMap(filteredMap);
        }

        @Override
        public String getParameter(String name) {
            String potentiallyDirtyParameter = super.getParameter(name);
            return filterString(potentiallyDirtyParameter);
        }

        /**
         * This is only here so we can see what the original parameters were, you should delete this method!
         *
         * @return original unwrapped request
         */
        @Deprecated
        public HttpServletRequest getOriginalRequest() {
            return (HttpServletRequest) super.getRequest();
        }

        /**
         * @param potentiallyDirtyParameter string to be cleaned
         * @return a clean version of the same string
         */
        private String filterString(String potentiallyDirtyParameter) {
            if (potentiallyDirtyParameter == null) {
                return null;
            }

            try {
                CleanResults cr = antiSamy.scan(potentiallyDirtyParameter, AntiSamy.DOM);
                if (cr.getNumberOfErrors() > 0) {
                    log.warn("antisamy encountered problem with input: " + cr.getErrorMessages());
                }
                String str = StringEscapeUtils.unescapeHtml(cr.getCleanHTML());
                str = str.replaceAll((antiSamy.scan("&nbsp;",AntiSamy.DOM)).getCleanHTML(),"");
                return str;
            } catch (Exception e) {
                throw new IllegalStateException(e.getMessage(), e);
            }
        }
    }
}
