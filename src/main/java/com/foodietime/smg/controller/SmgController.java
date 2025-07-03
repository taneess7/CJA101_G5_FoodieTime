package com.foodietime.smg.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.foodietime.grouporders.model.GroupOrdersService;
import com.foodietime.grouporders.model.GroupOrdersVO;
import com.foodietime.smg.model.SmgService;
import com.foodietime.smg.model.SmgVO;
import com.foodietime.smgfc.model.SmgfcService;
import com.foodietime.smgfc.model.SmgfcVO;
import com.foodietime.smgauth.model.SmgauthService;
import com.foodietime.smgauth.model.SmgauthVO;
import com.foodietime.smgauth.model.SmgauthId;
import com.foodietime.grouppurchasereport.model.GroupPurchaseReportService;
import com.foodietime.grouppurchasereport.model.GroupPurchaseReportVO;
import com.foodietime.accrec.model.AccrecService;
import com.foodietime.accrec.model.AccrecVO;
import com.foodietime.smg.AccrecPayoutScheduler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/smg")
public class SmgController {
	@Autowired
	SmgService smgSvc;
	@Autowired
	SmgfcService smgfcService;
	@Autowired
	SmgauthService smgauthService;
	@Autowired
	GroupOrdersService groupOrdersService;
	@Autowired
	private GroupPurchaseReportService groupPurchaseReportService;
	@Autowired
	private AccrecService accrecService;
	@Autowired
	private AccrecPayoutScheduler accrecPayoutScheduler;
	@GetMapping("/login")
	public String showLoginPage() {
	    return "admin/smg/admin-login"; // 對應到你的 Thymeleaf 登入頁面
	}
	@PostMapping("/login")
    public String login(@RequestParam String account,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {

        SmgVO smg = smgSvc.login(account, password);
        
        if (smg != null) {
        	if( smgSvc.checkAccountStatus(smg)) {
            	session.setAttribute("loggedInSmg", smg); // 儲存商家資訊於 session
            	return "redirect:/smg/admin-dashboard";
            }else {
            	model.addAttribute("error", "帳號狀態無啟用");
                return "admin/smg/admin-login"; // 返回登入頁面
            }          
        } else {
            model.addAttribute("error", "帳號或密碼錯誤");
            return "admin/smg/admin-login"; // 返回登入頁面
        }
        
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/smg/admin/login";
    }
	
    @GetMapping("/admin/login")
    public String adminpagelogin() {
        return "admin/smg/admin-login";
    }
    @GetMapping("/admin-dashboard")
    public String admindashboard() {
        return "admin/smg/admin-dashboard"; 
    }
    @GetMapping("/admin-users-add")
    public String adminusersadd(Model model) {
        model.addAttribute("smg", new SmgVO()); // 傳空 VO 讓表單能綁定
        return "admin/smg/admin-users-add";
    }
    @GetMapping("/admin-users-edit/{id}")
    public String editSmg(@PathVariable("id") Integer id, Model model) {
        SmgVO smg = smgSvc.findById(id); // 這裡記得你要自己定義 findById 方法
        if (smg == null) {
            return "redirect:/smg/admin-users-permissions"; // 找不到資料時回列表頁
        }
        model.addAttribute("smg", smg); // 放入 Thymeleaf 模型
        return "admin/smg/admin-users-edit"; // 回傳畫面
    }
    @PostMapping("/update")
    @Transactional
    public String updateSmg(@Valid @ModelAttribute("smg") SmgVO smgVO,
                            BindingResult result,
                            @RequestParam(value = "permissions", required = false) List<String> permissions,
                            Model model) {
        
        System.out.println("=== 開始更新管理員資料 ===");
        System.out.println("接收到的 smgVO: " + smgVO);
        System.out.println("接收到的權限: " + permissions);
        
        if (result.hasErrors()) {
            System.out.println("表單驗證錯誤: " + result.getAllErrors());
            result.getAllErrors().forEach(error -> {
                System.out.println("錯誤詳情: " + error.getDefaultMessage());
            });
            model.addAttribute("error", "表單驗證失敗，請檢查輸入資料");
            model.addAttribute("smg", smgVO);
            return "admin/smg/admin-users-edit";
        }

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
        
        String newPassword = smgVO.getNewPassword();
        // 處理密碼更新
        if (newPassword != null && !newPassword.trim().isEmpty()) {
            System.out.println("更新密碼");
            // TODO: 這裡應該加密密碼
            existingSmg.setSmgrPassword(newPassword);
        } else {
            System.out.println("保留原密碼");
            // 保留原密碼，不做任何變更
        }
        
        // 處理權限更新 - 分離事務避免級聯衝突
        if (permissions != null) {
            System.out.println("更新權限: " + permissions);
            // 先清除現有權限
            smgauthService.deleteBySmgrId(existingSmg.getSmgrId());
            // 清空集合避免級聯衝突
            existingSmg.getSmgauths().clear();
        } else {
            System.out.println("未選擇任何權限，清除所有權限");
            smgauthService.deleteBySmgrId(existingSmg.getSmgrId());
            existingSmg.getSmgauths().clear();
        }
        
        // 先保存基本資料，不包含權限
        System.out.println("準備儲存的資料: " + existingSmg);
        SmgVO savedSmg = smgSvc.save(existingSmg);
        
        // 在新的事務中創建權限
        if (permissions != null) {
            smgauthService.createPermissions(savedSmg.getSmgrId(), permissions, smgfcService);
        }
        System.out.println("儲存成功: " + savedSmg);

        System.out.println("=== 更新完成，重導向到列表頁 ===");
        return "redirect:/smg/admin-users-permissions";
    }
    
    @PostMapping("/add")
    public String addSmg(@Valid @ModelAttribute("smg") SmgVO smgVO,
            BindingResult result,
            @RequestParam(value = "permissions", required = false) List<String> permissions,
            Model model) {
    	if (result.hasErrors()) {
            System.out.println("表單驗證錯誤: " + result.getAllErrors());
            result.getAllErrors().forEach(error -> {
                System.out.println("錯誤詳情: " + error.getDefaultMessage());
            });
            model.addAttribute("error", "表單驗證失敗，請檢查輸入資料");
            model.addAttribute("smg", smgVO);
            return "admin/smg/admin-users-add";
        }
    	// 密碼與確認密碼是否一致
        if (!smgVO.getSmgrPassword().equals(smgVO.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "error.confirmPassword", "密碼與確認密碼不一致");
            model.addAttribute("error", "密碼與確認密碼不一致");
            return "admin/smg/admin-users-add";
        }
        SmgVO existing = smgSvc.findBySmgrAccount(smgVO.getSmgrAccount());
        if (existing != null) {
            result.rejectValue("smgrAccount", "error.smgrAccount", "此帳號已被使用");
            model.addAttribute("error", "此帳號已存在");
            return "admin/smg/admin-users-add";
        }
        
        // 先儲存管理員基本資料
        SmgVO savedSmg = smgSvc.save(smgVO);
        
        // 處理權限資料
        if (permissions != null && !permissions.isEmpty()) {
            for (String permission : permissions) {
                SmgfcVO smgfc = smgfcService.findByFunctionName(permission);
                if (smgfc != null) {
                    SmgauthVO smgauth = new SmgauthVO();
                    SmgauthId id = new SmgauthId(smgfc.getSmgFuncId(), savedSmg.getSmgrId());
                    smgauth.setId(id);
                    smgauth.setSmg(savedSmg);
                    smgauth.setSmgfc(smgfc);
                    smgauthService.save(smgauth);
                }
            }
        }

        // 導向列表頁
        return "redirect:/smg/admin-users-permissions";
    	
    }   
    
    @GetMapping("/admin-users-permissions")
    public String adminuserspermissions(Model model) {
    	List<SmgVO> smgList = smgSvc.findAllSmgs(); // 從資料庫抓全部管理員
        model.addAttribute("smgList", smgList); // 加入模型供 Thymeleaf 使用
    	return "admin/smg/admin-users-permissions"; 
    }
    @GetMapping("/search")
    public String searchSmg(@RequestParam(required = false) String smgrAccount,
				             @RequestParam(required = false) String smgrName,
				             @RequestParam(required = false) String smgrEmail,				             
				             Model model) {
    	List<SmgVO> smgList = smgSvc.searchByConditions(smgrAccount, smgrName, smgrEmail);
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
   
    @GetMapping("/admin-groups-reports")
    public String adminGroupsReports(Model model) {
        List<GroupPurchaseReportVO> reports = groupPurchaseReportService.getAll();
        model.addAttribute("reports", reports);
        return "admin/smg/admin-groups-reports";
    }
    @GetMapping("/admin-groups-status")
    public String admingroupsstatus() {
    	return "admin/smg/admin-groups-status"; 
    }
    @GetMapping("/admin-groups-orders")
    public String admingroupsorders(Model model) {
        List<GroupOrdersVO> groupOrders = groupOrdersService.findAll();
        model.addAttribute("groupOrders", groupOrders);
        return "admin/smg/admin-groups-orders";
    }
    @GetMapping("/admin-groups-payments")
    public String admingroupspayments() {
    	return "admin/smg/admin-groups-payments"; 
    }
    @GetMapping("/admin-groups-refunds")
    public String admingroupsrefunds(Model model) {
        // 查詢狀態4（退款）的訂單，這些是需要處理退款的訂單
        Byte status = 4;
        List<GroupOrdersVO> groupOrders = groupOrdersService.getOrdersByStatus(status);
        model.addAttribute("groupOrders", groupOrders);
        return "admin/smg/admin-groups-refunds"; 
    }
    @GetMapping("/admin-groups-monthly")
    public String admingroupsmonthly(Model model) {
        List<AccrecVO> accrecList = accrecService.findAllAccrec();
        model.addAttribute("accrecList", accrecList);
        return "admin/smg/admin-groups-monthly"; 
    }
//    @GetMapping("/admin-orders-view")
//    public String adminordersview() {
//    	return "admin/smg/admin-orders-view";
//    }
    @GetMapping("/admin-orders-payments")
    public String adminorderspayments() {
    	return "admin/smg/admin-orders-payments"; 
    }
    @PostMapping("/admin-groups-monthly/payout-all")
    public String manualPayoutAll(Model model) {
        accrecPayoutScheduler.payoutAllPending();
        model.addAttribute("msg", "批次撥款已完成！");
        return "redirect:/smg/admin-groups-monthly";
    }
    @PostMapping("/admin-groups-monthly/payout-one/{id}")
    @ResponseBody
    public String manualPayoutOne(@PathVariable("id") Integer id) {
        accrecPayoutScheduler.payoutOne(id);
        return "OK";
    }
}
