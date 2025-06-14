package com.foodietime.smg.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.foodietime.smg.model.SmgService;
import com.foodietime.smg.model.SmgVO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/smg")
public class SmgController {
	@Autowired
	SmgService smgSvc;

	@PostMapping("/login")
    public String login(@RequestParam String account,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {

        SmgVO smg = smgSvc.login(account, password);

        if (smg != null) {
            session.setAttribute("loggedInSmg", smg); // 儲存商家資訊於 session
            return "admin/smg/admin-dashboard";
        } else {
            model.addAttribute("error", "帳號或密碼錯誤");
            return "admin/smg/admin-login"; // 返回登入頁面
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/smg/login";
    }
	
    @GetMapping("/admin/login")
    public String adminpagelogin() {
        return "admin/smg/admin-login";
    }
    @GetMapping("/admin-dashboard")
    public String admindashboard() {
        return "admin/smg/admin-dashboard"; 
    }
    @GetMapping("/admin-forum-reports")
    public String adminforumreports() {
    	return "admin/smg/admin-forum-reports"; 
    }
    @GetMapping("/admin-users-add")
    public String adminusersadd() {
    	return "admin/smg/admin-users-add"; 
    }
    @GetMapping("/admin-users-edit/{id}")
    public String editSmg(@PathVariable("id") Integer id, Model model) {
        SmgVO smg = smgSvc.findById(id); // 這裡記得你要自己定義 findById 方法
        if (smg == null) {
            return "redirect:/admin-users-permissions"; // 找不到資料時回列表頁
        }
        model.addAttribute("smg", smg); // 放入 Thymeleaf 模型
        return "admin/smg/admin-users-edit"; // 回傳畫面
    }
    @PostMapping("/update")
    public String updateSmg(@Valid @ModelAttribute("smg") SmgVO smgVO,
                            BindingResult result,
                            @RequestParam(required = false) String newPassword,
                            Model model) {
        
        System.out.println("=== 開始更新管理員資料 ===");
        System.out.println("接收到的 smgVO: " + smgVO);
        System.out.println("新密碼: " + (newPassword != null ? "[已提供]" : "[未提供]"));
        
        if (result.hasErrors()) {
            System.out.println("表單驗證錯誤: " + result.getAllErrors());
            result.getAllErrors().forEach(error -> {
                System.out.println("錯誤詳情: " + error.getDefaultMessage());
            });
            model.addAttribute("error", "表單驗證失敗，請檢查輸入資料");
            model.addAttribute("smg", smgVO);
            return "admin/smg/admin-users-edit";
        }

        try {
            // 取得現有的管理員資料
            SmgVO existingSmg = smgSvc.findById(smgVO.getSmgrId());
            if (existingSmg == null) {
                System.out.println("找不到 ID 為 " + smgVO.getSmgrId() + " 的管理員");
                model.addAttribute("error", "找不到指定的管理員");
                model.addAttribute("smg", smgVO);
                return "admin/smg/admin-users-edit";
            }
            
            // 更新基本資料
            existingSmg.setSmgrName(smgVO.getSmgrName());
            existingSmg.setSmgrEmail(smgVO.getSmgrEmail());
            existingSmg.setSmgrPhone(smgVO.getSmgrPhone());
            existingSmg.setSmgrStatus(smgVO.getSmgrStatus());
            
            System.out.println("更新後的資料: Name=" + existingSmg.getSmgrName() + 
                             ", Email=" + existingSmg.getSmgrEmail() + 
                             ", Phone=" + existingSmg.getSmgrPhone() + 
                             ", Status=" + existingSmg.getSmgrStatus());
            
            // 處理密碼更新
            if (newPassword != null && !newPassword.trim().isEmpty()) {
                System.out.println("更新密碼");
                // TODO: 這裡應該加密密碼
                existingSmg.setSmgrPassword(newPassword);
            } else {
                System.out.println("保留原密碼");
                // 保留原密碼，不做任何變更
            }
            
            System.out.println("準備儲存的資料: " + existingSmg);
            SmgVO savedSmg = smgSvc.save(existingSmg);
            System.out.println("儲存成功: " + savedSmg);
            
        } catch (Exception e) {
            System.out.println("儲存時發生錯誤: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "儲存失敗: " + e.getMessage());
            model.addAttribute("smg", smgVO);
            return "admin/smg/admin-users-edit";
        }

        System.out.println("=== 更新完成，重導向到列表頁 ===");
        return "redirect:/smg/admin-users-permissions";
    }
    
    
    
    
    
    @GetMapping("/admin-users-permissions")
    public String adminuserspermissions(Model model) {
    	List<SmgVO> smgList = smgSvc.findAllSmgs(); // 從資料庫抓全部管理員
        model.addAttribute("smgList", smgList); // 加入模型供 Thymeleaf 使用
    	return "admin/smg/admin-users-permissions"; 
    }
    @GetMapping("/admin-vendors-review")
    public String adminvendorsreview() {
    	return "admin/smg/admin-vendors-review"; 
    }
    @GetMapping("/admin-vendors-edit")
    public String adminvendorsedit() {
    	return "admin/smg/admin-vendors-edit"; 
    }
    @GetMapping("/admin-vendors-reports")
    public String adminvendorsreports() {
    	return "admin/smg/admin-vendors-reports"; 
    }
    @GetMapping("/admin-products-disable")
    public String adminproductsdisable() {
    	return "admin/smg/admin-products-disable"; 
    }
    @GetMapping("/admin-vendors-blacklist")
    public String adminvendorsblacklist() {
    	return "admin/smg/admin-vendors-blacklist"; 
    }
    @GetMapping("/admin-vendors-permissions")
    public String adminvendorspermissions() {
    	return "admin/smg/admin-vendors-permissions"; 
    }	
    @GetMapping("/admin-members-search")
    public String adminmemberssearch() {
    	return "admin/smg/admin-members-search"; 
    }
    @GetMapping("/admin-members-permissions")
    public String adminmemberspermissions() {
    	return "admin/smg/admin-members-permissions"; 
    }
    @GetMapping("/admin-groups-reports")
    public String admingroupsreports() {
    	return "admin/smg/admin-vendors-blacklist"; 
    }
    @GetMapping("/admin-groups-status")
    public String admingroupsstatus() {
    	return "admin/smg/admin-groups-status"; 
    }
    @GetMapping("/admin-groups-orders")
    public String admingroupsorders() {
    	return "admin/smg/admin-groups-orders"; 
    }
    @GetMapping("/admin-groups-payments")
    public String admingroupspayments() {
    	return "admin/smg/admin-groups-payments"; 
    }
    @GetMapping("/admin-groups-refunds")
    public String admingroupsrefundss() {
    	return "admin/smg/admin-groups-refunds"; 
    }
    @GetMapping("/admin-groups-monthly")
    public String admingroupsmonthly() {
    	return "admin/smg/admin-groups-monthly"; 
    }
    @GetMapping("/admin-orders-view")
    public String adminordersview() {
    	return "admin/smg/admin-orders-view"; 
    }
    @GetMapping("/admin-orders-payments")
    public String adminorderspayments() {
    	return "admin/smg/admin-orders-payments"; 
    }
    @GetMapping("/admin-service-tickets")
    public String adminserviceticketss() {
    	return "admin/smg/admin-service-tickets"; 
    }
}
