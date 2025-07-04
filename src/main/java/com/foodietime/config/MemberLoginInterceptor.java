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
	    // 🔥 重新查詢資料庫，判斷是否已被停權
        MemberVO fresh = memService.getById(loggedIn.getMemId());
        if (fresh.getMemStatus().ordinal() == 2) {
            session.invalidate();
            response.sendRedirect(request.getContextPath() + "/front/member/login?error=disabled");
            return false;
        }

        return true;
	}


}
