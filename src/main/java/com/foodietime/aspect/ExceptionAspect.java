package com.foodietime.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;

import org.aspectj.lang.reflect.MethodSignature;
import java.lang.reflect.Method;

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
    
    /**
     * 針對團購商品 Controller 進行異常處理
     * 攔截 com.foodietime.gbprod.controller 包下所有方法執行
     */
    @Around("execution(* com.foodietime.gbprod.controller..*(..))")
    public Object handleGbprodControllerExceptions(ProceedingJoinPoint joinPoint) throws Throwable {
        
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
     * 簡化的異常處理 - 針對 SMG 和 Gbprod Controller
     */
    private Object handleSimpleException(ProceedingJoinPoint joinPoint, Exception e, String methodName) {
        
        String className = joinPoint.getTarget().getClass().getSimpleName();
        
        // 簡化的錯誤訊息
        String errorMessage = "系統發生錯誤: " + e.getMessage();
        
        // 判斷是否為 REST API，如果是，則返回 ResponseEntity
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        if (method.isAnnotationPresent(ResponseBody.class) || 
            joinPoint.getTarget().getClass().isAnnotationPresent(RestController.class)) {
            
            logger.error("REST API 執行異常: {}.{}, 返回錯誤訊息: {}", className, methodName, errorMessage);
            return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
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
        
        // 根據控制器類別和方法名稱決定返回的錯誤頁面
        String errorView;
        if (className.contains("gbservlet") || className.contains("Gbprod")) {
            // 團購商品相關錯誤處理
            if (methodName.contains("detail")) {
                errorView = "front/gb/gbindex"; // 詳情頁面錯誤返回首頁
            } else if (methodName.contains("search") || methodName.contains("products") || methodName.contains("all")) {
                errorView = "front/gb/gball"; // 列表頁面錯誤返回列表頁
            } else {
                errorView = "front/gb/gbindex"; // 預設返回團購首頁
            }
        } else {
            // SMG 後台管理相關錯誤處理
            if (methodName.contains("login")) {
                errorView = "admin/smg/admin-login";
            } else if (methodName.contains("edit") || methodName.contains("update")) {
                errorView = "admin/smg/admin-users-edit";
            } else if (methodName.contains("add") || methodName.contains("create")) {
                errorView = "admin/smg/admin-users-add";
            } else {
                errorView = "admin/smg/admin-dashboard";
            }
        }
        
        // 將錯誤訊息加入到適當的物件中
        if (model != null) {
            model.addAttribute("error", errorMessage);
        }
        
        if (redirectAttributes != null) {
            redirectAttributes.addFlashAttribute("error", errorMessage);
        }
        
        logger.info("異常處理完成，類別: {}, 方法: {}, 返回錯誤頁面: {}", className, methodName, errorView);
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
