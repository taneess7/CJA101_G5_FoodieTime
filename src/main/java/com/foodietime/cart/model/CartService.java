package com.foodietime.cart.model;

import com.foodietime.member.model.MemberVO;
import com.foodietime.product.model.ProductVO;
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
    public CartVO addCart(MemberVO member, ProductVO product, Integer prodN) {
        CartVO cartVO = new CartVO();
        cartVO.setMember(member);
        cartVO.setProduct(product);
        cartVO.setProdN(prodN);
        repo.save(cartVO);
        return cartVO;
    }

    // 修改購物車商品 - 修正：加入 shopId 參數
    public CartVO updateCart(Integer shopId, MemberVO member, ProductVO product, Integer prodN) {
        CartVO cartVO = new CartVO();
        cartVO.setShopId(shopId);
        cartVO.setMember(member);
        cartVO.setProduct(product);
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
    public List<CartVO> getByMemId(MemberVO member) {
        return repo.findByMember(member);
    }

    // 查詢某會員某商品是否已存在於購物車
    public CartVO getByMemIdAndProdId(MemberVO member, ProductVO product) {
        return repo.findByMemberAndProduct(member, product);
    }
}
