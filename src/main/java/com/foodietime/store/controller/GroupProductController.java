package com.foodietime.store.controller;

import com.foodietime.gbprod.model.GbprodVO;
import com.foodietime.gbprod.model.GbprodService;
import com.foodietime.gbprodcg.model.GbprodcgVO;
import com.foodietime.member.model.MemberVO;
import com.foodietime.gbprodcg.model.GbprodcgService;
import com.foodietime.store.model.StoreVO;
import com.foodietime.store.model.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import java.io.IOException;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/store")
public class GroupProductController {

    @Autowired
    private GbprodService gbprodService;

    @Autowired
    private GbprodcgService gbprodcgService;

    @Autowired
    private StoreService storeService;

    @GetMapping("/group-prod-add")
    public String showAddGroupProductForm(Model model) {
        List<GbprodcgVO> categories = gbprodcgService.findAllGbprodcg();
        model.addAttribute("categories", categories);
        model.addAttribute("gbprodVO", new GbprodVO());
        return "front/store/group-prod-add";
    }

    @PostMapping("/group-prod/add")
    public String addGroupProduct(@Valid @ModelAttribute("gbprodVO") GbprodVO gbprodVO,
                                  BindingResult result,
                                  @RequestParam("gbProdPhotoFile") MultipartFile file,
                                  HttpSession session,
                                  Model model) {
        MemberVO member = (MemberVO) session.getAttribute("loggedInMember");
        if (member == null) {
            return "redirect:/front/member/login";
        }
        StoreVO store = storeService.findById(member.getMemId());
        gbprodVO.setStore(store);

        if (result.hasErrors()) {
            model.addAttribute("categories", gbprodcgService.findAllGbprodcg());
            return "front/store/group-prod-add";
        }
        
        gbprodVO.setUpdateAt(new Date());
        gbprodVO.setGbProdReportCount((byte) 0);

        if (!file.isEmpty()) {
            try {
                byte[] photoBytes = file.getBytes();
                Byte[] photoWrapper = new Byte[photoBytes.length];
                for (int i = 0; i < photoBytes.length; i++) {
                    photoWrapper[i] = photoBytes[i];
                }
                gbprodVO.setGbProdPhoto(photoWrapper);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        gbprodService.save(gbprodVO);
        return "redirect:/store/sc";
    }
} 