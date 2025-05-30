package com.foodietime.cart.model;

import java.util.*;

public interface CartDAO_interface {
	//==============MySQL基本語法===========
	// 新增購物車商品
    public void insert(CartVO cartVO);
    // 修改購物車商品（如需支援）
    public void update(CartVO cartVO);
    // 刪除購物車商品（依據主鍵 SHOP_ID）
    public void delete(Integer shopId);

    //==============MySQL查詢語法===========
    // 依據主鍵查詢單一購物車商品
    public CartVO findByPrimaryKey(Integer shopId);
    // 查詢所有購物車商品
    public List<CartVO> getAll();
    // 查詢某會員的所有購物車商品
    public List<CartVO> findByMemId(Integer memId);
    // 查詢某會員某商品是否已存在於購物車
    public CartVO findByMemIdAndProdId(Integer memId, Integer prodId);
}
