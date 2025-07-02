package com.foodietime.groupbuyingcases.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.foodietime.member.model.MemberVO;

import jakarta.servlet.http.HttpSession;

@RequestMapping("/gb")
@Controller
public class GroupBuyingCasesServlet {

	@GetMapping("/leader")
    public String leader(HttpSession session){
		if(session.getAttribute("loggedInMember") == null) {
			return "front/member/login";
		}
        return "front/gb/gbleader/leader";
    }
	
	
	@GetMapping("/leader-cashflow")
    public String leadercashflow(HttpSession session){
		if(session.getAttribute("loggedInMember") == null) {
			return "front/member/login";
		}
        return "front/gb/gbleader/leader-cashflow";
    }
	
	@GetMapping("/leader-notifications")
    public String leadernotifications(HttpSession session){
		if(session.getAttribute("loggedInMember") == null) {
			return "front/member/login";
		}
        return "front/gb/gbleader/leader-notifications";
    }
	

	
	@GetMapping("/leader-product")
    public String leaderproduct(HttpSession session){
		if(session.getAttribute("loggedInMember") == null) {
			return "front/member/login";
		}
        return "front/gb/gbleader/leader-product";
    }
	
	@GetMapping("/leader-searchresult")
    public String leadersearchresult(HttpSession session){
		if(session.getAttribute("loggedInMember") == null) {
			return "front/member/login";
		}
        return "front/gb/gbleader/leader-searchresult";
        
    }
	@GetMapping("/leader-gborder-detail")
    public String leadergborderdetail(){
		
		
        return "front/gb/gbleader/leader-gborder-detail";
        
    }
	
	@GetMapping("/mem-gborders")
    public String memgborders(){
		
		
        return "front/memgborders/member-gborders";
        
    }
	

}
