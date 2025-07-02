package com.foodietime.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

public class StoreLoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        HttpSession session = request.getSession();
        Object loggedInStore = session.getAttribute("loggedInStore");

        if (loggedInStore == null) {
            // 🔁 記住原始網址，登入後可導回
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
