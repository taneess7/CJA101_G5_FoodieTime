package com.foodietime.cart.model;

import com.foodietime.cart.dto.CartItemDTO;
import com.foodietime.product.model.ProductRepository;
import com.foodietime.product.model.ProductVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CartService {

    // ==================== 1. 常數與依賴注入 ====================
    private static final String CART_KEY_PREFIX = "cart:";
    private final RedisTemplate<String, Object> redisTemplate;
    private final HashOperations<String, String, Integer> hashOps;
    private final ProductRepository productRepo; // 仍然需要用來獲取商品詳細資訊

    @Autowired
    public CartService(RedisTemplate<String, Object> redisTemplate, ProductRepository productRepo) {
        this.redisTemplate = redisTemplate;
        this.hashOps = redisTemplate.opsForHash();
        this.productRepo = productRepo;
    }

    // ==================== 2. 主要公開方法 ====================

    /**
     * 將商品加入指定會員的購物車。
     * @param memId 會員ID
     * @param prodId 商品ID
     * @param quantity 欲增加的數量
     */
    public void addCart(Integer memId, Integer prodId, Integer quantity) {
        // ==================== 1. 商品存在性檢查 (新的 FK 約束) ====================
        // 在操作 Redis 之前，先確認這個商品在 MySQL 中是真實存在的。
        // 這一步取代了資料庫的 FK 約束檢查。
        ProductVO product = productRepo.findById(prodId)
                .orElseThrow(() -> new IllegalArgumentException("無效的商品ID：" + prodId));

        // 你甚至可以增加商品狀態的檢查
        if (!product.getProdStatus()) {
            throw new IllegalStateException("此商品目前無法購買");
        }

        // ==================== 2. 執行 Redis 操作 ====================
        String cartKey = getCartKey(memId);
        String prodIdStr = String.valueOf(prodId);

        // 使用 HINCRBY 命令，原子性地增加數量
        hashOps.increment(cartKey, prodIdStr, quantity);
    }

    /**
     * 更新購物車中特定商品的數量。
     * @param memId 會員ID
     * @param prodId 商品ID
     * @param newQuantity 新的商品數量
     */
    public void updateCartQuantity(Integer memId, Integer prodId, Integer newQuantity) {
        // 步驟 1：同樣進行商品存在性檢查
        productRepo.findById(prodId)
                .orElseThrow(() -> new IllegalArgumentException("無效的商品ID：" + prodId));

        // 步驟 2：執行更新邏輯...
        if (newQuantity <= 0) {
            deleteCartItem(memId, prodId);
        } else {
            String cartKey = getCartKey(memId);
            hashOps.put(cartKey, String.valueOf(prodId), newQuantity);
        }
    }

    /**
     * 從購物車中刪除一項商品。
     * @param memId 會員ID
     * @param prodId 商品ID
     */
    public void deleteCartItem(Integer memId, Integer prodId) {
        String cartKey = getCartKey(memId);
        hashOps.delete(cartKey, String.valueOf(prodId));
    }

    /**
     * 獲取會員的完整購物車內容。
     * @param memId 會員ID
     * @return 包含完整商品資訊的 DTO 列表
     */
    public List<CartItemDTO> getCart(Integer memId) {
        String cartKey = getCartKey(memId);
        // 從 Redis 獲取所有商品ID和對應的數量
        Map<String, Integer> cartItemsMap = hashOps.entries(cartKey);

        if (cartItemsMap.isEmpty()) {
            return new ArrayList<>();
        }

        // 根據商品ID列表，一次性從資料庫查詢所有商品詳細資訊
        List<Integer> prodIds = cartItemsMap.keySet().stream()
                .map(Integer::valueOf)
                .collect(Collectors.toList());

        List<ProductVO> products = productRepo.findAllById(prodIds);

        // 將商品資訊和購物車中的數量組合成 DTO
        return products.stream()
                .map(product -> new CartItemDTO(
                        product,
                        cartItemsMap.get(String.valueOf(product.getProdId()))
                ))
                .collect(Collectors.toList());
    }

    /**
     * 清空指定會員的購物車。
     * @param memId 會員ID
     */
    public void clearCart(Integer memId) {
        String cartKey = getCartKey(memId);
        redisTemplate.delete(cartKey);
    }

    /**
     * 獲取會員購物車中的商品項目總數 (用於購物車圖標)。
     * @param memId 會員ID
     * @return 商品項目數量
     */
    public Integer getCartItemCount(Integer memId) {
        String cartKey = getCartKey(memId);
        // HLEN 命令，高效獲取 Hash 中的 Field 數量
        return hashOps.size(cartKey).intValue();
    }


    // ==================== 3. 輔助方法 ====================

    /**
     * 根據會員ID生成對應的 Redis Key。
     * @param memId 會員ID
     * @return Redis Key
     */
    private String getCartKey(Integer memId) {
        return CART_KEY_PREFIX + memId;
    }

    /**
     * 【新增】根據商品ID列表，從會員購物車中獲取指定的商品項目。
     * 這個方法是結帳流程的關鍵，用於獲取用戶勾選的商品詳情。
     *
     * @param memId 會員ID
     * @param prodIds 商品ID列表
     * @return 包含指定商品完整資訊的 DTO 列表
     */
    public List<CartItemDTO> getCartItemsByProdIds(Integer memId, List<Integer> prodIds) {
        String cartKey = getCartKey(memId);
        if (prodIds == null || prodIds.isEmpty()) {
            return new ArrayList<>();
        }

        // 將 Integer 列表轉換為 String 列表以用於 HMGET
        List<String> prodIdStrs = prodIds.stream()
                .map(String::valueOf)
                .collect(Collectors.toList());

        // 1. 使用 HMGET 一次從 Redis 獲取多個商品的數量
        List<Integer> quantities = hashOps.multiGet(cartKey, prodIdStrs);

        // 2. 一次性從資料庫查詢所有商品的詳細資訊
        List<ProductVO> products = productRepo.findAllById(prodIds);

        // 3. 組裝成 CartItemDTO 列表
        List<CartItemDTO> result = new ArrayList<>();
        Map<Integer, ProductVO> productMap = products.stream()
                .collect(Collectors.toMap(ProductVO::getProdId, p -> p));

        for (int i = 0; i < prodIds.size(); i++) {
            Integer prodId = prodIds.get(i);
            Integer quantity = quantities.get(i);
            ProductVO product = productMap.get(prodId);

            // 確保商品存在且購物車中有此項 (quantity 不為 null)
            if (product != null && quantity != null && quantity > 0) {
                result.add(new CartItemDTO(product, quantity));
            }
        }
        return result;
    }

    /**
     * 【新增】從購物車中批量刪除多項商品。
     * 這個方法將在訂單成功建立後被呼叫。
     *
     * @param memId 會員ID
     * @param prodIds 要刪除的商品ID列表
     */
    public void deleteCartItems(Integer memId, List<Integer> prodIds) {
        if (prodIds == null || prodIds.isEmpty()) {
            return;
        }
        String cartKey = getCartKey(memId);
        // 將 prodId 轉換為 String[] 以作為 HDEL 的參數
        String[] fieldsToDelete = prodIds.stream()
                .map(String::valueOf)
                .toArray(String[]::new);
        hashOps.delete(cartKey, (Object[]) fieldsToDelete);
    }
}