package com.foodietime.cart.model;

import java.util.List;

public class CartService {

    private CartDAO_interface dao;

    public CartService() {
        dao = new CartDAO();
    }

    // 新增購物車商品
    public CartVO addCart(Integer memId, Integer prodId , Integer prodN) {
        CartVO cartVO = new CartVO();
        cartVO.setMemId(memId);
        cartVO.setProdId(prodId);
        cartVO.setProdN(prodN);
        dao.insert(cartVO);
        return cartVO;
    }

    // 修改購物車商品 - 修正：加入 shopId 參數
    public CartVO updateCart(Integer shopId, Integer memId, Integer prodId, Integer prodN) {
        CartVO cartVO = new CartVO();
        cartVO.setShopId(shopId);  
        cartVO.setMemId(memId);
        cartVO.setProdId(prodId);
        cartVO.setProdN(prodN);
        dao.update(cartVO);
        return cartVO;
    }

    // 刪除購物車商品
    public void deleteCart(Integer shopId) {
        dao.delete(shopId);
    }

    // 查詢單一購物車商品
    public CartVO getOneCart(Integer shopId) {
        return dao.findByPrimaryKey(shopId);
    }

    // 查詢所有購物車商品
    public List<CartVO> getAll() {
        return dao.getAll();
    }

    // 查詢某會員所有購物車商品
    public List<CartVO> getByMemId(Integer memId) {
        return dao.findByMemId(memId);
    }

    // 查詢某會員某商品是否已存在於購物車
    public CartVO getByMemIdAndProdId(Integer memId, Integer prodId) {
        return dao.findByMemIdAndProdId(memId, prodId);
    }
}
