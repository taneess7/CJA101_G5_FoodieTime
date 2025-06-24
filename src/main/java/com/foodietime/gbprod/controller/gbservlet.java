package com.foodietime.gbprod.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.servlet.http.HttpSession;

import com.foodietime.gbprod.dto.GroupBuyingDisplayDTO;
import com.foodietime.gbprod.dto.ProductDetailDTO;
import com.foodietime.gbprod.dto.ReviewDTO;
import com.foodietime.gbprod.dto.JoinGroupRequest;
import com.foodietime.gbprod.service.GroupBuyingDisplayService;
import com.foodietime.gbprod.service.ProductDetailService;
import com.foodietime.gbprod.service.ReviewService;
import com.foodietime.groupbuyingcases.model.GroupBuyingCasesService;
import com.foodietime.groupbuyingcases.model.GroupBuyingCasesVO;
import com.foodietime.member.model.MemberVO;
import com.foodietime.participants.model.ParticipantsService;
import com.foodietime.participants.model.ParticipantsVO;
import com.foodietime.grouporders.model.GroupOrdersService;
import com.foodietime.grouporders.model.GroupOrdersVO;
import com.foodietime.grouporders.model.GroupOrdersRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodietime.gbprod.dto.MemberInfoDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/gb")
public class gbservlet {
	
	private static final Logger logger = LoggerFactory.getLogger(gbservlet.class);
	
	@Autowired
    private ObjectMapper objectMapper;
	
	@Autowired
    private GroupBuyingDisplayService groupBuyingDisplayService;
    
    @Autowired
    private ProductDetailService productDetailService;
    
    @Autowired
    private ReviewService reviewService;
  
    @Autowired
    private GroupOrdersService groupOrdersService;
    @Autowired
    private GroupOrdersRepository groupOrdersRepository;
    @Autowired
    private GroupBuyingCasesService groupBuyingCasesService;
    @Autowired
    private ParticipantsService participantsService;
    
	
	@GetMapping("/gbindex")
	public String showLoginPage(Model model) {
		List<GroupBuyingDisplayDTO> popularProducts = groupBuyingDisplayService.getPopularProducts();
        model.addAttribute("popularProducts", popularProducts);
        
        // 獲取最新上架商品
        List<GroupBuyingDisplayDTO> latestProducts = groupBuyingDisplayService.getLatestProducts();
        model.addAttribute("latestProducts", latestProducts);
        
	    return "front/gb/gbindex"; // 對應到你的 Thymeleaf 登入頁面
	}
	
		/**
	     * 團購商品列表頁
	     * @param model Spring MVC Model
	     * @return 團購商品列表模板
	     */
	    @GetMapping("/products")
	    public String products(Model model) {
	        List<GroupBuyingDisplayDTO> allProducts = groupBuyingDisplayService.getAllActiveProducts();
	        model.addAttribute("allProducts", allProducts);
	        
	        return "front/gb/gball";
	    }
	    /**
	     * 搜尋團購商品
	     * @param keyword 搜尋關鍵字
	     * @param model Spring MVC Model
	     * @return 搜尋結果模板
	     */
	    @GetMapping("/search")
    public String search(@RequestParam("keyword") String keyword, Model model) {
        List<GroupBuyingDisplayDTO> searchResults = groupBuyingDisplayService.searchProducts(keyword);
        model.addAttribute("allProducts", searchResults);
        model.addAttribute("keyword", keyword);
        
        return "front/gb/gball";
    }
    
    /**
     * 顯示所有團購案例頁面
     * @param model Spring MVC Model
     * @return 全部團購案例模板
     */
    @GetMapping("/all")
    public String showAllGroupBuyingCases(Model model) {
        // 獲取所有活躍的團購案
        List<GroupBuyingDisplayDTO> allProducts = groupBuyingDisplayService.getAllActiveProducts();
        model.addAttribute("allProducts", allProducts);
        return "front/gb/gball";
    }
    
    /**
     * 顯示團購商品詳情頁面
     * @param gbId 團購案例ID
     * @param model Spring MVC Model
     * @return 商品詳情模板
     */
    @GetMapping("/detail/{gbId}")
    public String showProductDetail(@PathVariable Integer gbId, Model model, HttpSession session) {
        
             // 從資料庫獲取商品詳情
             ProductDetailDTO productDetail = productDetailService.getProductDetail(gbId);
            
            if (productDetail == null) {
                model.addAttribute("error", "找不到該團購商品");
                return "front/gb/gbindex"; // 返回首頁並顯示錯誤訊息
            }
            
             // 從訂單獲取詳細評價資料
            List<ReviewDTO> reviews = reviewService.getReview(gbId);
            
            // 將商品詳情資料傳遞給前端模板
            model.addAttribute("productDetail", productDetail);
            model.addAttribute("reviews",reviews);
            
            // 如果會員已登入，將會員資料序列化為 JSON 字串並加入 model
            MemberVO member = (MemberVO) session.getAttribute("loggedInMember");
            if (member != null) {
                try {
                    // 使用 DTO 來避免序列化問題
                    MemberInfoDTO memberInfoDTO = new MemberInfoDTO(member);
                    model.addAttribute("loggedInMemberInfoJson", objectMapper.writeValueAsString(memberInfoDTO));
                } catch (Exception e) {
                    // 處裡序列化錯誤，例如 LOG 紀錄
                    logger.error("序列化 MemberInfoDTO 時發生錯誤", e);
                    model.addAttribute("loggedInMemberInfoJson", "null");
                }
            } else {
                model.addAttribute("loggedInMemberInfoJson", "null");
            }
            
            return "front/gb/product-detail";
        
    }
    //抓取圖片
    @GetMapping("/group/image/{gbId}")
    @ResponseBody
    public ResponseEntity<byte[]> getGroupProductImage(@PathVariable Integer gbId) {
        byte[] image = productDetailService.getImageByGroupBuyingId(gbId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG); // 根據圖片格式調整
        return new ResponseEntity<>(image, headers, HttpStatus.OK);
    }
    
    @PostMapping("/join")
    @ResponseBody
    public ResponseEntity<?> joinGroup(@RequestBody JoinGroupRequest req, HttpSession session) {
        MemberVO member = (MemberVO) session.getAttribute("loggedInMember");
        if (member == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("請先登入");
        // 1. 取得團購
        GroupBuyingCasesVO groupCase = groupBuyingCasesService.findById(req.getGbId()).orElse(null);
        if (groupCase == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("找不到團購");
        // 2. 建立訂單
        GroupOrdersVO order = new GroupOrdersVO();
        order.setGroupBuyingCase(groupCase);
        order.setStore(groupCase.getStore());
        order.setGbprod(groupCase.getGbProd());
        order.setJoinTime(java.time.LocalDateTime.now());
        order.setAmount(req.getAmount());
        order.setQuantity(req.getQuantity());
        order.setPayMethod(req.getPayMethod());
        order.setOrderStatus((byte)0); // 未接單
        order.setPaymentStatus((byte)0); // 未付款
        order.setShippingStatus((byte)0); // 未出貨
        order.setParName(req.getParName());
        order.setParAddress(req.getParAddress());
        order.setParLongitude(java.math.BigDecimal.valueOf(req.getParLongitude()));
        order.setParLatitude(java.math.BigDecimal.valueOf(req.getParLatitude()));
        order.setParPhone(req.getParPhone());
        order.setDeliveryMethod(req.getDeliveryMethod());
        order.setComment(null);
        order.setRating((byte)0); // 尚未評分
        groupOrdersRepository.save(order);
        // 3. 新增參與者
        ParticipantsVO participant = new ParticipantsVO();
        participant.setMember(member);
        participant.setGroupBuyingCase(groupCase);
        participant.setParPhone(req.getParPhone());
        participant.setParName(req.getParName());
        participant.setParAddress(req.getParAddress());
        participant.setParLongitude(java.math.BigDecimal.valueOf(req.getParLongitude()));
        participant.setParLatitude(java.math.BigDecimal.valueOf(req.getParLatitude()));
        participant.setLeader(member.getMemId().equals(groupCase.getMember().getMemId()) ? (byte)0 : (byte)1);
        participant.setParPurchaseQuantity(req.getQuantity());
        participant.setPaymentStatus((byte)0); // 未付款
        participantsService.save(participant);
        // 4. 更新累計購買數量
        int currentQuantity = groupCase.getCumulativePurchaseQuantity() != null ? groupCase.getCumulativePurchaseQuantity() : 0;
        groupCase.setCumulativePurchaseQuantity(currentQuantity + req.getQuantity());
        groupBuyingCasesService.saveGroupBuyingCase(groupCase);
        return ResponseEntity.ok("參團成功");
    }

}