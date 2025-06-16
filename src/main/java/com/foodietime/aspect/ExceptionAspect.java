package com.foodietime.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;

@Aspect
@Component
public class ExceptionAspect {
    
    private static final Logger logger = LoggerFactory.getLogger(ExceptionAspect.class);
    
    /**
     * 針對 SMG Controller 進行異常處理
     * 攔截 com.foodietime.smg.controller 包下所有方法執行
     */
    @Around("execution(* com.foodietime.smg.controller..*(..))")
    public Object handleSmgControllerExceptions(ProceedingJoinPoint joinPoint) throws Throwable {
        
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        
        try {
            logger.info("執行方法: {}.{}", className, methodName);
            
            // 執行原始方法
            Object result = joinPoint.proceed();
            
            logger.info("方法執行成功: {}.{}", className, methodName);
            return result;
            
        } catch (Exception e) {
            logger.error("方法執行異常: {}.{}, 錯誤訊息: {}", className, methodName, e.getMessage(), e);
            
            // 簡化的異常處理
            return handleSimpleException(joinPoint, e, methodName);
        }
    }
    
    // 註解：未來可擴展到全專案的異常處理
    /*
    @Around("execution(* com.foodietime..controller..*(..))")
    public Object handleAllControllerExceptions(ProceedingJoinPoint joinPoint) throws Throwable {
        
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        
        try {
            logger.info("執行方法: {}.{}", className, methodName);
            Object result = joinPoint.proceed();
            logger.info("方法執行成功: {}.{}", className, methodName);
            return result;
            
        } catch (Exception e) {
            logger.error("方法執行異常: {}.{}, 錯誤訊息: {}", className, methodName, e.getMessage(), e);
            return handleException(joinPoint, e, methodName, className);
        }
    }
    */
    
    /**
     * 簡化的異常處理 - 只針對 SMG Controller
     */
    private Object handleSimpleException(ProceedingJoinPoint joinPoint, Exception e, String methodName) {
        
        // 取得方法參數，尋找 Model 物件
        Object[] args = joinPoint.getArgs();
        Model model = null;
        RedirectAttributes redirectAttributes = null;
        
        for (Object arg : args) {
            if (arg instanceof Model) {
                model = (Model) arg;
            } else if (arg instanceof RedirectAttributes) {
                redirectAttributes = (RedirectAttributes) arg;
            }
        }
        
        // 簡化的錯誤訊息
        String errorMessage = "系統發生錯誤: " + e.getMessage();
        
        // 簡化的頁面路由 - 根據方法名稱決定
        String errorView;
        if (methodName.contains("login")) {
            errorView = "admin/smg/admin-login";
        } else if (methodName.contains("edit") || methodName.contains("update")) {
            errorView = "admin/smg/admin-users-edit";
        } else if (methodName.contains("add") || methodName.contains("create")) {
            errorView = "admin/smg/admin-users-add";
        } else {
            errorView = "admin/smg/admin-dashboard";
        }
        
        // 將錯誤訊息加入到適當的物件中
        if (model != null) {
            model.addAttribute("error", errorMessage);
        }
        
        if (redirectAttributes != null) {
            redirectAttributes.addFlashAttribute("error", errorMessage);
        }
        
        logger.info("異常處理完成，方法: {}, 返回錯誤頁面: {}", methodName, errorView);
        return errorView;
    }
    
    // 註解：複雜的異常處理邏輯（未來可啟用）
    /*
    private Object handleException(ProceedingJoinPoint joinPoint, Exception e, String methodName, String className) {
        
        Object[] args = joinPoint.getArgs();
        Model model = null;
        RedirectAttributes redirectAttributes = null;
        HttpServletRequest request = null;
        
        for (Object arg : args) {
            if (arg instanceof Model) {
                model = (Model) arg;
            } else if (arg instanceof RedirectAttributes) {
                redirectAttributes = (RedirectAttributes) arg;
            } else if (arg instanceof HttpServletRequest) {
                request = (HttpServletRequest) arg;
            }
        }
        
        String errorMessage = getErrorMessage(e);
        String errorView = getErrorView(methodName, e, className);
        
        if (model != null) {
            model.addAttribute("error", errorMessage);
            model.addAttribute("errorDetails", e.getMessage());
        }
        
        if (redirectAttributes != null) {
            redirectAttributes.addFlashAttribute("error", errorMessage);
        }
        
        logger.info("異常處理完成，類別: {}, 方法: {}, 返回錯誤頁面: {}", className, methodName, errorView);
        return errorView;
    }
    */
    
    // 註解：複雜的錯誤訊息處理（未來可啟用）
    /*
    private String getErrorMessage(Exception e) {
        if (e instanceof IllegalArgumentException) {
            return "輸入參數錯誤: " + e.getMessage();
        } else if (e instanceof NullPointerException) {
            return "系統發生空指標異常，請聯繫管理員";
        } else if (e instanceof RuntimeException) {
            return "系統執行時發生錯誤: " + e.getMessage();
        } else {
            return "系統發生未知錯誤，請稍後再試";
        }
    }
    
    private String getErrorView(String methodName, Exception e, String className) {
        
        // SMG 後台管理相關
        if (className.contains("SmgController")) {
            if (methodName.contains("login")) {
                return "admin/smg/admin-login";
            }
            if (methodName.contains("edit") || methodName.contains("update")) {
                return "admin/smg/admin-users-edit";
            }
            if (methodName.contains("add") || methodName.contains("create")) {
                return "admin/smg/admin-users-add";
            }
            if (methodName.contains("permissions")) {
                return "admin/smg/admin-users-permissions";
            }
            if (methodName.contains("dashboard")) {
                return "admin/smg/admin-dashboard";
            }
            return "admin/smg/admin-dashboard";
        }
        
        // 前台會員相關
        if (className.contains("MemberController")) {
            if (methodName.contains("login")) {
                return "front/member/login";
            }
            if (methodName.contains("register")) {
                return "front/member/register";
            }
            return "front/member/home";
        }
        
        // 商品相關
        if (className.contains("ProductController") || className.contains("ProductCategoryController")) {
            return "front/product/error";
        }
        
        // 首頁相關
        if (className.contains("IndexController")) {
            return "index";
        }
        
        // 全域預設錯誤頁面
        return "error/500";
    }
    */
    
    /**
     * 針對特定的業務異常進行處理
     * 可以根據需要擴展更多的異常處理邏輯
     */
    @Around("execution(* com.foodietime.smg.model.SmgService..*(..))")
    public Object handleSmgServiceExceptions(ProceedingJoinPoint joinPoint) throws Throwable {
        
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        
        try {
            logger.debug("執行服務方法: {}.{}", className, methodName);
            return joinPoint.proceed();
            
        } catch (Exception e) {
            logger.error("服務層異常: {}.{}, 錯誤訊息: {}", className, methodName, e.getMessage(), e);
            
            // 將服務層異常包裝後重新拋出，讓 Controller 層的切面處理
            throw new RuntimeException("服務執行失敗: " + e.getMessage(), e);
        }
    }
}
