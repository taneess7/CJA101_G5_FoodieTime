package com.foodietime.config;

import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class RecordRefererInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        HttpSession session = request.getSession();

        // 只有尚未登入的會員，且尚未記錄過 redirectAfterLogin
        if (session.getAttribute("loggedInMember") == null &&
            session.getAttribute("redirectAfterLogin") == null) {

            // 僅記錄 GET 請求（避免 POST / DELETE / PUT）
            if (!"GET".equalsIgnoreCase(request.getMethod())) {
                return true;
            }

            // 避免 AJAX（X-Requested-With: XMLHttpRequest）
            if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
                return true;
            }

            String uri = request.getRequestURI();

            // 排除登入、註冊、驗證、收藏 API 等頁面
            if (!uri.startsWith("/front/member/login") &&
                !uri.startsWith("/front/member/register") &&
                !uri.startsWith("/front/member/activate") &&
                !uri.startsWith("/front/member/verify") &&
                !uri.startsWith("/favorite/")) {

                String query = request.getQueryString();
                String fullUrl = uri + (query != null ? "?" + query : "");

                // 記錄原始頁面
                session.setAttribute("redirectAfterLogin", fullUrl);
            }
        }

        return true; // ✅ 繼續請求流程
    }
}
