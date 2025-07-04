package com.foodietime.cart.model;

import com.foodietime.member.model.MemberRepository;
import com.foodietime.member.model.MemberVO;
import com.foodietime.product.model.ProductRepository;
import com.foodietime.product.model.ProductVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CartService {
    // ============================= 注入 ===================================================================
    private final CartRepository repo;
    private final MemberRepository memberRepo;
    private final ProductRepository productRepo;
    @Autowired
    public CartService(CartRepository repo, MemberRepository memberRepo, ProductRepository productRepo) {
        this.repo = repo;
        this.memberRepo = memberRepo;
        this.productRepo = productRepo;
    }

    // ============================= 主要方法 ===================================================================


    // 新增_購物車商品，智能加入購物車（如果商品已存在則增加數量）
    public CartVO addCart(Integer memId, Integer prodId, Integer prodN) {
        MemberVO member = memberRepo.findById(memId)
                .orElseThrow(() -> new RuntimeException("會員不存在"));
        ProductVO product = productRepo.findById(prodId)
                .orElseThrow(() -> new RuntimeException("商品不存在"));

        CartVO existingCart = repo.findByMemberAndProduct(member, product);
        if (existingCart != null) {
            existingCart.setProdN(existingCart.getProdN() + prodN);
            return repo.save(existingCart);
        } else {
            CartVO newCart = new CartVO();
            newCart.setMember(member);
            newCart.setProduct(product);
            newCart.setProdN(prodN);
            return repo.save(newCart);
        }

    }

    // 修改_更新購物車數量
    public CartVO updateCart(Integer shopId, Integer memId, Integer newProdN) {
        MemberVO member = memberRepo.findById(memId)
                .orElseThrow(() -> new RuntimeException("會員不存在"));
        CartVO cart = repo.findByShopIdAndMember(shopId,member)
                .orElseThrow(() -> new RuntimeException("購物車項目不存在或無權限修改"));
        cart.setProdN(newProdN);
        return repo.save(cart);
    }

    // 刪除購物車商品
    public void deleteCart(Integer shopId) {
        if (!repo.existsById(shopId)) {
            throw new RuntimeException("購物車項目不存在");
        }
        repo.deleteById(shopId);
    }

    // 查詢單一購物車商品
    public CartVO getOneCart(Integer shopId) {
        return repo.findById(shopId)
                .orElseThrow(() -> new RuntimeException("購物車項目不存在"));
    }

    // 查詢所有購物車商品
    public List<CartVO> getAll() {
        return repo.findAll();
    }

    // 查詢某會員所有購物車商品
    public List<CartVO> getByMemId(Integer memId) {
        MemberVO member = memberRepo.findById(memId)
                .orElseThrow(() -> new RuntimeException("會員不存在"));
        return repo.findAllByMemberIdWithDetails(member);
    }
    // 查詢某會員某商品是否已存在於購物車
    public CartVO getByMemIdAndProdId(Integer memId, Integer prodId) {
        MemberVO member = memberRepo.findById(memId)
                .orElseThrow(() -> new RuntimeException("會員不存在"));
        ProductVO product = productRepo.findById(prodId)
                .orElseThrow(() -> new RuntimeException("商品不存在"));
        return repo.findByMemberAndProduct(member, product);
    }

    // 計算會員的購物車總金額
    public Integer calculateTotalAmount(Integer memId) {
        MemberVO member = memberRepo.findById(memId)
                .orElseThrow(() -> new RuntimeException("會員不存在"));
        List<CartVO> carts = repo.findByMember(member);
        return carts.stream().mapToInt( cart -> cart.getProduct().getProdPrice() * cart.getProdN()).sum();
    }

    // 計算會員購物車總商品數量
    public Integer calculateTotalQuantity(Integer memId) {
        MemberVO member = memberRepo.findById(memId)
                .orElseThrow(() -> new RuntimeException("會員不存在"));
        List<CartVO> carts = repo.findByMember(member);
        return carts.stream().mapToInt(CartVO::getProdN).sum();
    }

    // 清空會員購物車
    public void clearCart(Integer memId) {
        MemberVO member = memberRepo.findById(memId)
                .orElseThrow(() -> new RuntimeException("會員不存在"));
        List<CartVO> carts = repo.findByMember(member);
        repo.deleteAll(carts);
    }

    // 批量刪除購物車商品
    public void deleteCartItems(List<Integer> shopIds) {
        repo.deleteAllById(shopIds);
    }

    // 檢查會員購物車是否為空
    public boolean isCartEmpty(Integer memId) {
        MemberVO member = memberRepo.findById(memId)
                .orElseThrow(() -> new RuntimeException("會員不存在"));
        return repo.findByMember(member).isEmpty();
    }

    // 獲取購物車商品數量（用於購物車圖標顯示）
    public Integer getCartItemCount(Integer memId) {
        MemberVO member = memberRepo.findById(memId)
                .orElseThrow(() -> new RuntimeException("會員不存在"));
        return repo.findByMember(member).size();
    }

    // 更新商品價格（如果商品價格有變動）
    public void updateCartPrices(Integer memId) {
        MemberVO member = memberRepo.findById(memId)
                .orElseThrow(() -> new RuntimeException("會員不存在"));
        List<CartVO> carts = repo.findByMember(member);
        for (CartVO cart : carts) {
            // 重新取得最新商品資訊
            ProductVO latestProduct = productRepo.findById(cart.getProduct().getProdId())
                    .orElse(null);
            if (latestProduct != null) {
                cart.setProduct(latestProduct);
                repo.save(cart);
            }
        }
    }

    @Transactional(readOnly = true)
    public List<CartVO> getCartItemsByIds(List<Integer> shopIds) {
        if (shopIds == null || shopIds.isEmpty()) {
            return List.of(); // 返回一個空的列表
        }
        // 呼叫 Repository 中對應的新方法
        return repo.findAllByIdsWithDetails(shopIds);
    }

    /**
     * 【新增】根據會員和商品列表，批量刪除購物車中的項目。
     * @param member   執行刪除操作的會員
     * @param products 要從購物車中移除的商品列表
     */
    @Transactional
    public void deleteItemsByMemberAndProducts(MemberVO member, List<ProductVO> products) {
        if (member == null || products == null || products.isEmpty()) {
            return;
        }
        repo.deleteByMemberAndProducts(member, products);
    }

    /**
     * 根據會員實體，計算該會員購物車中的【商品項目總數】。
     * <p>
     * 注意：此方法計算的是購物車中有幾筆不同的商品記錄，而非所有商品的總數量加總。
     * 這個方法專為 Header 購物車圖標上的計數顯示而設計，效能經過最佳化。
     *
     * @param memId 欲查詢的會員實體 (從 GlobalControllerAdvice 或 Session 傳入)
     * @return Integer 購物車中的商品項目數量。如果會員為 null，則返回 0。
     */
    public Integer getTotalItemCountByMemberId(Integer  memId) {
        MemberVO member = memberRepo.findById(memId)
                .orElseThrow(() -> new RuntimeException("會員不存在"));

        // ==================== 1. 參數檢查 ====================
        // 確保傳入的會員物件不是 null，避免後續發生 NullPointerException
        if (member == null) {
            return 0;
        }
        // ==================== 2. 呼叫 Repository 執行高效計數 ====================
        return repo.countByMember(member);
    }
}
