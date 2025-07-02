package com.foodietime.config;

import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class MemberLoginInterceptor implements HandlerInterceptor{
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
	        throws Exception {

	    HttpSession session = request.getSession();
	    Object loggedInMember = session.getAttribute("loggedInMember");

	    if (loggedInMember == null) {
	        // ⏺ 記住原始請求網址（包含查詢參數）
	        String uri = request.getRequestURI();
	        String query = request.getQueryString();
	        String fullUrl = uri + (query != null ? "?" + query : "");

	        session.setAttribute("redirectAfterLogin", fullUrl);

	        response.sendRedirect(request.getContextPath() + "/front/member/login");
	        return false;
	    }

	    return true;
	}

}
