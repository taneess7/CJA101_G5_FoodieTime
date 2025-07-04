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
	        String uri = request.getRequestURI();
	        String query = request.getQueryString();
	        String fullUrl = uri + (query != null ? "?" + query : "");

	     // 只有當 redirectAfterLogin 尚未被 RefererInterceptor 設定，才設定
	        if (session.getAttribute("redirectAfterLogin") == null &&
	            !"XMLHttpRequest".equals(request.getHeader("X-Requested-With")) && // 避免 AJAX
	            request.getMethod().equalsIgnoreCase("GET") && // 只針對 GET 頁面
	            !uri.startsWith("/favorite/") &&
	            !uri.startsWith("/front/member/login") &&
	            !uri.startsWith("/front/member/register") &&
	            !uri.startsWith("/front/member/activate") &&
	            !uri.startsWith("/front/member/verify")) {

	            session.setAttribute("redirectAfterLogin", fullUrl);
	        }


	        response.sendRedirect(request.getContextPath() + "/front/member/login");
	        return false;
	    }

	    return true;
	}


}
