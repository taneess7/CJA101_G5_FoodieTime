package com.foodietime.groupbuyingcases.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/gb")
@Controller
public class GroupBuyingCasesServlet {

	@GetMapping("/leader")
    public String leader(){
        return "front/gb/gbleader/leader";
    }
	
	@GetMapping("/leader-address")
    public String leaderaddress(){
        return "front/gb/gbleader/leader-address";
    }
	
	@GetMapping("/leader-cashflow")
    public String leadercashflow(){
        return "front/gb/gbleader/leader-cashflow";
    }
	
//	@GetMapping("/leader-groups")
//    public String leadergroups(){
//        return "front/gb/gbleader/leader-groups";
//    }
		
	@GetMapping("/leaderindex")
	public String leaderindex(){
        return "front/gb/gbleader/leaderindex";
    }
	
	@GetMapping("/leader-notifications")
    public String leadernotifications(){
        return "front/gb/gbleader/leader-notifications";
    }
	
	@GetMapping("/leader-orders")
    public String leaderorders(){
        return "front/gb/gbleader/leader-orders";
    }
	
	@GetMapping("/leader-product")
    public String leaderproduct(){
        return "front/gb/gbleader/leader-product";
    }
	
	@GetMapping("/leader-searchresult")
    public String leadersearchresult(){
        return "front/gb/gbleader/leader-searchresult";
    }
	
	@GetMapping("/leader-gbdetail")
    public String leadergbdetail(){
        return "front/gb/gbleader/leader-gbdetail";
    }
	
	@GetMapping("/leader-groups-edit")
    public String leadergroupsedit(){
        return "front/gb/gbleader/leader-groups-edit";
    }
}
