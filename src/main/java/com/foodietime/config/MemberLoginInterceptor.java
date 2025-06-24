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
            response.sendRedirect(request.getContextPath() + "/front/member/login");
            return false;
        }

        return true;
    }
}
