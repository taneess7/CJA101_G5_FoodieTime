package com.foodietime.gbprod.controller;

import java.util.List;
import java.util.Optional;
import java.io.*;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;
import org.json.JSONObject;
import org.json.JSONException;
import java.net.URISyntaxException;
import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletResponse;

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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodietime.gbprod.dto.MemberInfoDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.foodietime.member.model.MemService;
import com.foodietime.gbprod.model.GbprodService;
import com.foodietime.gbprod.model.GbprodVO;
import com.foodietime.store.model.StoreVO;
import org.springframework.transaction.annotation.Transactional;
import com.foodietime.store.model.StoreService;

@Controller
@RequestMapping("/gb")
public class gbservlet {
	
	private static final Logger logger = LoggerFactory.getLogger(gbservlet.class);
	
	// LINE Pay 設定
	private static final String CHANNEL_ID = "1656895462";
	private static final String CHANNEL_SECRET = "fd01e635b9ea97323acbe8d5c6b2fb71";
	private static final String API_URL = "https://sandbox-api-pay.line.me/v3/payments/request";
	
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
    private GroupBuyingCasesService groupBuyingCasesService;
    @Autowired
    private ParticipantsService participantsService;
    
    @Autowired
    private MemService memService;
    
    @Autowired
    private GbprodService gbprodService;
    
    @Autowired
    private StoreService storeService;
	
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
        StoreVO store = storeService.findById(groupCase.getStore().getStorId());
        if (store == null) throw new RuntimeException("找不到店家");
        order.setStore(store);
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
        groupOrdersService.save(order);
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
        // 4. 不要在這裡更新累計購買數量，僅在付款成功時加總
        return ResponseEntity.ok("參團成功");
    }
    
    /**
     * 顯示付款頁面
     */
    @GetMapping("/payment")
    public String showPaymentPage() {
        return "front/gb/payment";
    }
    
    /**
     * 顯示付款成功頁面
     */
    @GetMapping("/payment-success")
    public String showPaymentSuccessPage() {
        return "front/gb/payment-success";
    }
    
    /**
     * 顯示付款失敗頁面
     */
    @GetMapping("/payment-failed")
    public String showPaymentFailedPage() {
        return "front/gb/payment-failed";
    }
    
    /**
     * LINE Pay 付款請求
     */
    @PostMapping("/api/linepay")
    @ResponseBody
    public ResponseEntity<?> doLinePay(@RequestBody String jsonBody, HttpServletResponse response) throws Exception {
        // 解析 JSON
        logger.info("收到的原始 JSON: {}", jsonBody);
        JSONObject body = new JSONObject(jsonBody);
        JSONObject linepayBody = body.optJSONObject("linepayBody");
        JSONObject linepayOrder = body.optJSONObject("linepayOrder");
        logger.info("LINE Pay 付款請求 - jsonBody: {}", body);
        logger.info("LINE Pay 付款請求 - linepayBody: {}", linepayBody);
        logger.info("LINE Pay 付款請求 - linepayOrder: {}", linepayOrder);
        
        // 檢查 linepayOrder 是否為 null
        if (linepayOrder == null) {
            logger.error("linepayOrder 為 null，無法處理付款請求");
            return ResponseEntity.ok(new ApiResponse<>("fail", null, "訂單資訊不完整"));
        }

        // 產生 HMAC 簽章
        String nonce = UUID.randomUUID().toString();
        String uri = "/v3/payments/request";
        String signatureRaw = CHANNEL_SECRET + uri + linepayBody.toString() + nonce;
        String signature = HmacUtil.hmacSHA256Base64(CHANNEL_SECRET, signatureRaw);

        // 發送 HTTP POST 請求到 LINE Pay
        HttpURLConnection conn = (HttpURLConnection) new URL(API_URL).openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("X-LINE-ChannelId", CHANNEL_ID);
        conn.setRequestProperty("X-LINE-Authorization", signature);
        conn.setRequestProperty("X-LINE-Authorization-Nonce", nonce);
        conn.setDoOutput(true);
        try (OutputStream os = conn.getOutputStream()) {
            os.write(linepayBody.toString().getBytes("UTF-8"));
        }
        // 取得回應
        StringBuilder responseLinePay = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            String respLine;
            while ((respLine = br.readLine()) != null) {
                responseLinePay.append(respLine);
            }
        }
        JSONObject resJson = new JSONObject(responseLinePay.toString());
        logger.info("LINE Pay 回應: {}", resJson);
        JSONObject info = resJson.optJSONObject("info");
        String returnCode = resJson.getString("returnCode");
        if (returnCode.equals("0000")) {
            // 付款網址請求成功，將訂單資料(未付款)塞入DB
            logger.info("LINE Pay 付款請求成功，建立團購訂單");
            createGroupBuyOrder(linepayOrder);
            String paymentUrl = (info != null && info.has("paymentUrl"))
                ? info.getJSONObject("paymentUrl").getString("web")
                : null;
            // 回傳 paymentUrl 給前端
            return ResponseEntity.ok(new ApiResponse<>("success", paymentUrl, "付款請求成功"));
        } else {
            // 交易失敗
            logger.error("LINE Pay 付款請求失敗: {}", returnCode);
            return ResponseEntity.ok(new ApiResponse<>("fail", null, "付款請求失敗"));
        }
    }
    
    /**
     * 確認 LINE Pay 付款狀態
     */
    @GetMapping("/api/confirmpayment/{orderId}")
    public void checkLinePayStatus(@PathVariable String orderId, HttpServletResponse responseServlet)
            throws Exception {
        final String API_URL = "https://sandbox-api-pay.line.me/v2/payments/orders/" + orderId + "/check";
        // 發送 HTTP GET 請求到 LINE Pay
        HttpURLConnection conn = (HttpURLConnection) new URL(API_URL).openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("X-LINE-ChannelId", CHANNEL_ID);
        conn.setRequestProperty("X-LINE-ChannelSecret", CHANNEL_SECRET);
        // 發送request
        int responseCode = conn.getResponseCode();
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        // 解析 JSON 字串
        JSONObject json = new JSONObject(response.toString());
        String returnCode = json.getString("returnCode");
        String returnMessage = json.getString("returnMessage");
        logger.info("LINE Pay 交易確認({}): {} || {}", orderId, returnCode, returnMessage);
        if (returnCode.equals("0000")) {
            // 交易成功，將訂單狀態改成已付款
            logger.info("LINE Pay 交易成功，更新付款狀態");
            updatePaymentStatus(orderId, (byte)1);
            responseServlet.sendRedirect("/gb/payment-success?orderId=" + orderId);
        } else {
            // 交易失敗
            logger.error("LINE Pay 交易失敗: {}", returnMessage);
            responseServlet.sendRedirect("/gb/payment-failed?error=" + encodeURIComponent(returnMessage));
        }
    }
    
    /**
     * 建立團購訂單（未付款狀態）
     */
    @Transactional
    private void createGroupBuyOrder(JSONObject linepayOrder) throws Exception {
        logger.info("建立團購訂單: {}", linepayOrder);
        
        // 檢查 linepayOrder 是否為 null
        if (linepayOrder == null) {
            throw new RuntimeException("linepayOrder 為 null");
        }
        
        // 檢查必要欄位是否存在
        if (!linepayOrder.has("orderId")) {
            throw new RuntimeException("缺少 orderId 欄位");
        }
        if (!linepayOrder.has("memberId")) {
            throw new RuntimeException("缺少 memberId 欄位");
        }
        if (!linepayOrder.has("groupBuyingCaseId")) {
            throw new RuntimeException("缺少 groupBuyingCaseId 欄位");
        }
        if (!linepayOrder.has("quantity")) {
            throw new RuntimeException("缺少 quantity 欄位");
        }
        if (!linepayOrder.has("totalAmount")) {
            throw new RuntimeException("缺少 totalAmount 欄位");
        }
        if (!linepayOrder.has("recipientName")) {
            throw new RuntimeException("缺少 recipientName 欄位");
        }
        if (!linepayOrder.has("recipientPhone")) {
            throw new RuntimeException("缺少 recipientPhone 欄位");
        }
        if (!linepayOrder.has("recipientAddress")) {
            throw new RuntimeException("缺少 recipientAddress 欄位");
        }
        
        // 解析訂單資訊
        String orderId = linepayOrder.getString("orderId");
        Integer memberId = linepayOrder.getInt("memberId");
        Integer groupBuyingCaseId = linepayOrder.getInt("groupBuyingCaseId");
        Integer quantity = linepayOrder.getInt("quantity");
        Integer totalAmount = linepayOrder.getInt("totalAmount");
        String recipientName = linepayOrder.getString("recipientName");
        String recipientPhone = linepayOrder.getString("recipientPhone");
        String recipientAddress = linepayOrder.getString("recipientAddress");
        
        logger.info("解析的訂單資訊 - orderId: {}, memberId: {}, groupBuyingCaseId: {}, quantity: {}, totalAmount: {}", 
                   orderId, memberId, groupBuyingCaseId, quantity, totalAmount);
        
        // 取得相關實體
        MemberVO member = memService.getById(memberId);
        if (member == null) throw new RuntimeException("找不到會員: " + memberId);
        GroupBuyingCasesVO groupCase = groupBuyingCasesService.findById(groupBuyingCaseId)
            .orElseThrow(() -> new RuntimeException("找不到團購案: " + groupBuyingCaseId));
        GbprodVO gbprod = gbprodService.findById(groupCase.getGbProd().getGbProdId());
        if (gbprod == null) throw new RuntimeException("找不到商品");
        StoreVO store = storeService.findById(groupCase.getStore().getStorId());
        if (store == null) throw new RuntimeException("找不到店家");
        // 1. 建立團購訂單
        GroupOrdersVO order = new GroupOrdersVO();
        order.setGroupBuyingCase(groupCase);
        order.setStore(store);
        order.setGbprod(gbprod);
        order.setJoinTime(java.time.LocalDateTime.now());
        order.setAmount(totalAmount);
        order.setQuantity(quantity);
        order.setPayMethod((byte)2); // LINE Pay
        order.setOrderStatus((byte)0); // 未接單
        order.setPaymentStatus((byte)0); // 未付款
        order.setShippingStatus((byte)0); // 未出貨
        order.setParName(recipientName);
        order.setParAddress(recipientAddress);
        order.setParLongitude(java.math.BigDecimal.valueOf(121.543211)); // 預設經度
        order.setParLatitude(java.math.BigDecimal.valueOf(25.033964)); // 預設緯度
        order.setParPhone(recipientPhone);
        order.setDeliveryMethod((byte)0); // 宅配
        order.setComment(null);
        order.setRating((byte)0); // 尚未評分
        GroupOrdersVO savedOrder = groupOrdersService.save(order);
        logger.info("團購訂單建立成功，訂單ID: {}", savedOrder.getGbOrId());
        // 2. 建立參與者
        ParticipantsVO participant = new ParticipantsVO();
        participant.setMember(member);
        participant.setGroupBuyingCase(groupCase);
        participant.setParPhone(recipientPhone);
        participant.setParName(recipientName);
        participant.setParAddress(recipientAddress);
        participant.setParLongitude(java.math.BigDecimal.valueOf(121.543211)); // 預設經度
        participant.setParLatitude(java.math.BigDecimal.valueOf(25.033964)); // 預設緯度
        participant.setLeader(member.getMemId().equals(groupCase.getMember().getMemId()) ? (byte)0 : (byte)1);
        participant.setParPurchaseQuantity(quantity);
        participant.setPaymentStatus((byte)0); // 未付款
        ParticipantsVO savedParticipant = participantsService.save(participant);
        logger.info("參與者建立成功，參與者ID: {}", savedParticipant.getParId());
        // 3. 更新累計購買數量
        int currentQuantity = groupCase.getCumulativePurchaseQuantity() != null ? groupCase.getCumulativePurchaseQuantity() : 0;
        groupCase.setCumulativePurchaseQuantity(currentQuantity + quantity);
        groupBuyingCasesService.saveGroupBuyingCase(groupCase);
        logger.info("累計購買數量更新成功: {}", groupCase.getCumulativePurchaseQuantity());
    }
    
    /**
     * 更新付款狀態
     */
    @Transactional
    private void updatePaymentStatus(String orderId, byte status) throws Exception {
        logger.info("更新訂單 {} 的付款狀態為: {}", orderId, status);
        // 查找最新的未付款訂單（這裡簡化處理）
        List<GroupOrdersVO> orders = groupOrdersService.getOrdersByPaymentStatus((byte)0);
        if (!orders.isEmpty()) {
            GroupOrdersVO order = orders.get(orders.size() - 1); // 取最新的訂單
            // 更新團購訂單付款狀態
            groupOrdersService.updateOrderField(order.getGbOrId(), "paymentStatus", status);
            logger.info("團購訂單付款狀態更新成功: {}", order.getGbOrId());
            // 更新參與者付款狀態
            List<ParticipantsVO> participants = participantsService.getParticipantsByGroupBuyingCaseId(order.getGroupBuyingCase().getGbId());
            for (ParticipantsVO participant : participants) {
                if (participant.getMember().getMemId().equals(order.getGroupBuyingCase().getMember().getMemId())) {
                    participant.setPaymentStatus(status);
                    participantsService.save(participant);
                    logger.info("參與者付款狀態更新成功: {}", participant.getParId());
                    break;
                }
            }
        } else {
            logger.warn("找不到未付款的訂單");
        }
    }
    
    /**
     * URL 編碼工具方法
     */
    private String encodeURIComponent(String value) {
        try {
            return java.net.URLEncoder.encode(value, "UTF-8");
        } catch (Exception e) {
            return value;
        }
    }
    
    /**
     * API 回應類別
     */
    public static class ApiResponse<T> {
        private String status;
        private T data;
        private String message;
        
        public ApiResponse(String status, T data, String message) {
            this.status = status;
            this.data = data;
            this.message = message;
        }
        
        // Getters and Setters
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public T getData() { return data; }
        public void setData(T data) { this.data = data; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
    
    /**
     * HMAC 工具類別
     */
    public static class HmacUtil {
        public static String hmacSHA256Base64(String secret, String data) {
            try {
                javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA256");
                javax.crypto.spec.SecretKeySpec secretKeySpec = new javax.crypto.spec.SecretKeySpec(secret.getBytes("UTF-8"), "HmacSHA256");
                mac.init(secretKeySpec);
                byte[] hash = mac.doFinal(data.getBytes("UTF-8"));
                return java.util.Base64.getEncoder().encodeToString(hash);
            } catch (Exception e) {
                throw new RuntimeException("HMAC 計算失敗", e);
            }
        }
    }

}