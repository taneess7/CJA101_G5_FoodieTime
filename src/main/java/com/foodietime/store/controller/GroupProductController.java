package com.foodietime.store.controller;

import com.foodietime.gbprod.model.GbprodVO;
import com.foodietime.gbprod.model.GbprodService;
import com.foodietime.gbprodcg.model.GbprodcgVO;
import com.foodietime.member.model.MemberVO;
import com.foodietime.gbprodcg.model.GbprodcgService;
import com.foodietime.store.model.StoreVO;
import com.foodietime.store.model.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
        System.out.println("會員 email: " + member.getMemEmail());
        StoreVO store = storeService.findByStorEmail(member.getMemEmail());
        System.out.println("查到的 store: " + store);
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

    @GetMapping("/group-prod-list")
    public String showGroupProductList(Model model, HttpSession session) {
        MemberVO member = (MemberVO) session.getAttribute("loggedInMember");
        if (member == null) {
            return "redirect:/front/member/login";
        }
        StoreVO store = storeService.findById(member.getMemId());
        List<GbprodVO> groupProducts = gbprodService.findByStore(store);
        model.addAttribute("groupProducts", groupProducts);
        return "front/store/group-prod-list";
    }

    @GetMapping("/group-prod-edit")
    public String showEditGroupProductForm(@RequestParam("gbProdId") Integer gbProdId, Model model) {
        GbprodVO gbprodVO = gbprodService.findById(gbProdId);
        List<GbprodcgVO> categories = gbprodcgService.findAllGbprodcg();
        model.addAttribute("gbprodVO", gbprodVO);
        model.addAttribute("categories", categories);
        return "front/store/group-prod-edit";
    }

    @PostMapping("/group-prod/edit")
    public String editGroupProduct(@Valid @ModelAttribute("gbprodVO") GbprodVO gbprodVO,
                                   BindingResult result,
                                   @RequestParam("gbProdPhotoFile") MultipartFile file,
                                   HttpSession session,
                                   Model model) {
        MemberVO member = (MemberVO) session.getAttribute("loggedInMember");
        if (member == null) {
            return "redirect:/front/member/login";
        }
        StoreVO store = storeService.findByStorEmail(member.getMemEmail());
        gbprodVO.setStore(store);
        if (result.hasErrors()) {
            model.addAttribute("categories", gbprodcgService.findAllGbprodcg());
            return "front/store/group-prod-edit";
        }
        gbprodVO.setUpdateAt(new Date());
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
        } else {
            // 若沒上傳新圖，保留原圖
            GbprodVO original = gbprodService.findById(gbprodVO.getGbProdId());
            gbprodVO.setGbProdPhoto(original.getGbProdPhoto());
        }
        gbprodService.save(gbprodVO);
        return "redirect:/store/group-prod-list";
    }

    @GetMapping("/group-prod-photo/{gbProdId}")
    @ResponseBody
    public ResponseEntity<byte[]> getGroupProdPhoto(@PathVariable Integer gbProdId) {
        GbprodVO gbprodVO = gbprodService.findById(gbProdId);
        Byte[] photoWrapper = null;
        try {
            java.lang.reflect.Field field = GbprodVO.class.getDeclaredField("gbProdPhoto");
            field.setAccessible(true);
            photoWrapper = (Byte[]) field.get(gbprodVO);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
        if (gbprodVO == null || photoWrapper == null || photoWrapper.length == 0) {
            return ResponseEntity.notFound().build();
        }
        byte[] photo = new byte[photoWrapper.length];
        for (int i = 0; i < photoWrapper.length; i++) {
            photo[i] = (photoWrapper[i] != null) ? photoWrapper[i] : 0;
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        return new ResponseEntity<>(photo, headers, HttpStatus.OK);
    }
} 