package com.foodietime.smg.loginInterceptor;

import org.springframework.web.servlet.HandlerInterceptor;

import com.foodietime.smg.model.SmgVO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class SmgLoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 加上 no-cache 標頭，避免快取
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        HttpSession session = request.getSession();
        SmgVO smg = (session != null) ? (SmgVO) session.getAttribute("loggedInSmg") : null;
        
        String uri = request.getRequestURI();
        // 忘記密碼、重設密碼 API 允許匿名訪問
        if (
            uri.equals("/smg/forgot-password") ||
            uri.equals("/smg/reset-password") ||
            uri.equals("/smg/do-reset-password") ||
            uri.equals("/smg/reset-password-success") ||
            uri.equals("/smg/reset-password-error")
        ) {
            return true;
        }

        if (smg == null) {
            // 判斷是否為 AJAX 請求
            String ajaxHeader = request.getHeader("X-Requested-With");

            if ("XMLHttpRequest".equals(ajaxHeader)) {
                // AJAX：回傳 JSON 格式錯誤提示
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.setStatus(401); // Unauthorized
                response.getWriter().write("{\"error\":\"unauthorized\",\"message\":\"請重新登入管理員帳號\"}");
            } else {
                // 一般瀏覽器頁面導向登入畫面
                response.sendRedirect(request.getContextPath() + "/smg/login");
            }

            return false;
        }
        System.out.println("✅ SmgLoginInterceptor 放行: " + request.getRequestURI());
        return true;
        
    }
    
}
