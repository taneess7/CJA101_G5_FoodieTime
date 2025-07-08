package com.foodietime.gbprod.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.foodietime.gbprod.model.GbprodService;
import com.foodietime.gbprod.model.GbprodVO;
import com.foodietime.gbprod.service.GbleaderService;
import com.foodietime.member.model.MemberVO;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/gb")
public class gbleaderservlet {

	 @Autowired
	 private GbleaderService gbleaderService;
	 @Autowired
	 private GbprodService gbprodService;
//	 	/**
//	     * 查詢店家的促銷價格
//	     * @param storeName 店家名稱
//	     * @param model 用來傳遞資料到前端
//	     * @return 返回顯示頁面
//	     */
//	    @GetMapping("/leaderindex")
//	    public String getStoresWithPromotionPrices(Model model) {
//	        // 呼叫 Service 層獲取指定店家的促銷價格
//	        List<GbprodVO> storesWithPromotion = gbleaderService.getStoresWithPromotionPrices();
//
//	        // 把結果添加到模型中
//	        model.addAttribute("storesWithPromotion", storesWithPromotion);
//
//	        // 返回要顯示頁面
//	        return "front/gb/gbleader/leaderindex";  
//	    }
        
	    @GetMapping("/gbproduct/image/{gbProdId}")
	    @ResponseBody
	    public ResponseEntity<byte[]> getGroupProductImage(@PathVariable Integer gbProdId) {
	        byte[] image = gbprodService.getProductPhoto(gbProdId);
	        HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(MediaType.IMAGE_JPEG); // 根據圖片格式調整
	        return new ResponseEntity<>(image, headers, HttpStatus.OK);
	    }
	    
	    @GetMapping("/leaderindex")
	    public String getStoresWithPromotionPrices(
	            HttpSession session,
	            @RequestParam(value = "keyword", required = false) String keyword,
	            Model model) {

	        // 1. 檢查是否登入
	        MemberVO member = (MemberVO) session.getAttribute("loggedInMember");
	        if (member == null) {
	            // 若未登入，導到會員登入頁
	            return "front/member/login";
	        }

	        // 2. 取得資料
	        List<GbprodVO> storesWithPromotion;
	        if (keyword != null && !keyword.trim().isEmpty()) {
	            storesWithPromotion = gbleaderService.searchProducts(keyword.trim());
	            model.addAttribute("keyword", keyword);
	        } else {
	            storesWithPromotion = gbleaderService.getStoresWithPromotionPrices();
	        }
	        model.addAttribute("storesWithPromotion", storesWithPromotion);

	        // 3. 回傳畫面
	        return "front/gb/gbleader/leaderindex";
	    }


	    @GetMapping("/leader-product/{gbProdId}")
	    public String getLeaderProductDetail(@PathVariable Integer gbProdId, Model model, HttpSession session) {
	        // 1. 檢查是否登入
	        MemberVO member = (MemberVO) session.getAttribute("loggedInMember");
	        if (member == null) {
	            return "redirect:/front/member/login";
	        }

	        try {
	            // 2. 查詢商品（找不到會拋出例外或為 null）
	            GbprodVO product = gbprodService.findById(gbProdId);
	            if (product == null) {
	                return "redirect:/gb/leaderindex";
	            }

	            // 3. 放入 model
	            model.addAttribute("product", product);

	            // 4. 抓 promotion 成團數量與起訖時間
	            Integer gbMinQty = null;
	            String promotStart = null;
	            String promotEnd = null;

	            if (product.getGbpromotionList() != null && !product.getGbpromotionList().isEmpty()) {
	                var promo = product.getGbpromotionList().get(0);
	                gbMinQty = promo.getGbMinQty();
	                if (promo.getPromotStart() != null) {
	                    promotStart = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm")
	                        .format(promo.getPromotStart());
	                }
	                if (promo.getPromotEnd() != null) {
	                    promotEnd = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm")
	                        .format(promo.getPromotEnd());
	                }
	            }

	            model.addAttribute("gbMinQty", gbMinQty);
	            model.addAttribute("promotStart", promotStart);
	            model.addAttribute("promotEnd", promotEnd);

	            return "front/gb/gbleader/leader-product";

	        } catch (Exception e) {
	            // 5. 發生任何錯誤也回到商品列表
	            return "redirect:/gb/leaderindex";
	        }
	    }

	    
	    @PostMapping("/create-case")
        public String createGroupBuyingCase(
            @RequestParam Integer gbProdId,
            @RequestParam String gbTitle,
            @RequestParam String gbDescription,
            @RequestParam Integer gbMinQty,
            @RequestParam("endTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) java.time.LocalDateTime endTime,
            HttpSession session,
            Model model
        ) {
            MemberVO member = (MemberVO) session.getAttribute("loggedInMember");
            if (member == null) {
                return "front/member/login";
            }
            gbleaderService.createGroupBuyingCase(gbProdId, gbTitle, gbDescription, gbMinQty, endTime, member);
            return "redirect:/gb/leaderindex";
        }
}
