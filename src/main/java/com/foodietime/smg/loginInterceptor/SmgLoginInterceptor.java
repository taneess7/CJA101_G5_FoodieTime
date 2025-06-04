package com.foodietime.smg.loginInterceptor;

import org.springframework.web.servlet.HandlerInterceptor;

import com.foodietime.smg.model.SmgVO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class SmgLoginInterceptor implements HandlerInterceptor {
//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        HttpSession session = request.getSession();
//        SmgVO smg = (SmgVO) session.getAttribute("loggedInSmg");
//        if (smg == null) {
//            response.sendRedirect("/smg/login");
//            return false; // 終止請求
//        }
//        return true; // 通過攔截
//    }
}
