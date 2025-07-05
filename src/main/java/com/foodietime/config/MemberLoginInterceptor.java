package com.foodietime.config;

import org.springframework.web.servlet.HandlerInterceptor;

import com.foodietime.member.model.MemService;
import com.foodietime.member.model.MemberVO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class MemberLoginInterceptor implements HandlerInterceptor{
	
	private final MemService memService;

    public MemberLoginInterceptor(MemService memService) {
        this.memService = memService;
    }
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        HttpSession session = request.getSession();
        MemberVO loggedIn = (MemberVO) session.getAttribute("loggedInMember");

        if (loggedIn == null) {
            String uri = request.getRequestURI();
            String query = request.getQueryString();
            String fullUrl = uri + (query != null ? "?" + query : "");

            // AJAX 請求回傳 401 JSON
            if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"error\": \"unauthorized\", \"message\": \"尚未登入\"}");
                return false;
            }

            // 非 AJAX 請求：記錄 redirect
            if (session.getAttribute("redirectAfterLogin") == null &&
                request.getMethod().equalsIgnoreCase("GET") &&
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

        // 🔥 重新查詢是否已被停權
        MemberVO fresh = memService.getById(loggedIn.getMemId());
        if (fresh.getMemStatus().ordinal() == 2) {
            session.invalidate();

            // AJAX 請求：回傳 JSON
            if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"error\": \"disabled\", \"message\": \"帳號已停權\"}");
                return false;
            }

            // 非 AJAX 請求：重導登入頁
            response.sendRedirect(request.getContextPath() + "/front/member/login?error=disabled");
            return false;
        }

        return true;
    }



}
