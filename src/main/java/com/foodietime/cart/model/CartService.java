package com.foodietime.cart.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartService {

    private final CartRepository repo;

    @Autowired
    public CartService(CartRepository repo) {
        this.repo = repo;
    }
//    private CartDAO_interface dao;
//    public CartService() {
//        dao = new CartDAO();
//    }

    // 新增購物車商品
    public CartVO addCart(Integer memId, Integer prodId, Integer prodN) {
        CartVO cartVO = new CartVO();
        cartVO.setMemId(memId);
        cartVO.setProdId(prodId);
        cartVO.setProdN(prodN);
        repo.save(cartVO);
        return cartVO;
    }

    // 修改購物車商品 - 修正：加入 shopId 參數
    public CartVO updateCart(Integer shopId, Integer memId, Integer prodId, Integer prodN) {
        CartVO cartVO = new CartVO();
        cartVO.setShopId(shopId);
        cartVO.setMemId(memId);
        cartVO.setProdId(prodId);
        cartVO.setProdN(prodN);
        repo.save(cartVO);
        return cartVO;
    }

    // 刪除購物車商品
    public void deleteCart(Integer shopId) {
        repo.deleteById(shopId);
    }

    // 查詢單一購物車商品
    public CartVO getOneCart(Integer shopId) {
        return repo.findById(shopId).orElse(null);
    }

    // 查詢所有購物車商品
    public List<CartVO> getAll() {
        return repo.findAll();
    }

    // 查詢某會員所有購物車商品
    public List<CartVO> getByMemId(Integer memId) {
        return repo.findByMemId(memId);
    }

    // 查詢某會員某商品是否已存在於購物車
    public CartVO getByMemIdAndProdId(Integer memId, Integer prodId) {
        return repo.findByMemIdAndProdId(memId, prodId);
    }
}
