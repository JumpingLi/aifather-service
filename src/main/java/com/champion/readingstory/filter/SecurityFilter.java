package com.champion.readingstory.filter;

import com.champion.readingstory.util.Md5Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author jpli3
 */
@Slf4j
@WebFilter(filterName = "securityFilter", urlPatterns = {"/ls/*","/story/check/bind","/story/userStory/list"})
public class SecurityFilter implements Filter {


    @Override
    public void init(FilterConfig filterConfig) {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
//        resp.setHeader("Access-Control-Allow-Origin", "*");
        String openId = req.getHeader("openId");
        String timestamp = req.getHeader("timestamp");
        String sign = req.getHeader("sign");
        if (HttpMethod.OPTIONS.name().equals(req.getMethod())) {
            resp.setStatus(HttpStatus.NO_CONTENT.value());
            chain.doFilter(request, response);
        } else if (openId == null || !openId.startsWith("odxQd0")) {
            //|| sign == null || timestamp == null || !validateSign(openId, timestamp, sign)
            log.error("===== openId is null or error");
            resp.setStatus(HttpStatus.FORBIDDEN.value());
        } else {
            request.setAttribute("sessionId", openId);
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {

    }

    private Boolean validateSign(String openId, String timestamp, String sign) {
        return Md5Util.md5Encode(Md5Util.md5Encode(openId) + timestamp).equals(sign);
    }
}
