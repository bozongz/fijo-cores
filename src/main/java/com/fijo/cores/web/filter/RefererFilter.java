/**
 * 版权所有 © 北京晟壁科技有限公司 2008-2016。保留一切权利!
 */
package com.fijo.cores.web.filter;

import com.fijo.cores.exceptions.Exceptions;
import com.fijo.cores.model.JsonResponse;
import com.fijo.cores.utils.Constants;
import com.fijo.cores.utils.json.JacksonUtils;
import com.google.common.collect.Sets;
import com.fijo.cores.exceptions.Exceptions;
import com.fijo.cores.model.JsonResponse;
import com.fijo.cores.utils.Constants;
import com.fijo.cores.utils.json.JacksonUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;

/**
 * 用途：验证HTTP Referer字段防御CSRF攻击
 * 作者: zhangbo
 * 时间: 2016-04-20  0:11
 * 参考：
 * https://www.ibm.com/developerworks/cn/web/1102_niugang_csrf/
 * http://blog.csdn.net/u012228718/article/details/39521677
 */
public class RefererFilter implements Filter {
    public final Log log = LogFactory.getLog(RefererFilter.class);
    private Set<String> whiteHostList = Sets.newHashSet();

    public void init(FilterConfig config) throws ServletException {
        String whiteHosts = config.getInitParameter("whiteHosts");
        if(StringUtils.isNotEmpty(whiteHosts)){
            String[] whiteHostss = whiteHosts.split(Constants.COMMA);
            for(String s:whiteHostss){
                whiteHostList.add(s.toLowerCase());
            }
        }
    }

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        log.debug(request.getRequestURI());
        // 链接来源地址，通过获取请求头 referer 得到
        String referer = request.getHeader("referer");
        try {
            if (referer != null) {
                URI referUri = new URI(referer);
                String host = referUri.getHost().toLowerCase();
                log.debug("Check host："+host);
                if (whiteHostList.size()>0 && !whiteHostList.contains(host)) {
                    //request.getRequestDispatcher("/html/errors/404.html").forward(request, response);
                    PrintWriter writer = res.getWriter();
                    JsonResponse jsonResponse = new JsonResponse(0);
                    String msg = "Forbidden: invalid host access: "+host;
                    jsonResponse.setMessage(msg);
                    log.debug(msg);
                    writer.write(JacksonUtils.writeValueAsString(jsonResponse));
                    writer.flush();
                    writer.close();
                } else {
                    chain.doFilter(request, response);
                }
            }else {
                    chain.doFilter(request, response);
            }
        } catch (URISyntaxException e) {
            Exceptions.printException(e);
        }

    }

    public void destroy() {
    }
}
