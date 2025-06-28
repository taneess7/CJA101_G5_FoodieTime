package com.foodietime.gbpromotion.model;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foodietime.smg.model.SmgVO;
import com.foodietime.store.model.StoreVO;

@Service
public class GbpromotionService {

    @Autowired
    private GbpromotionRepository gbpromotionRepository;

    public List<GbpromotionVO> findAllGbpromotion() {
        return gbpromotionRepository.findAll();
    }

    public GbpromotionVO findById(Integer gbPromoId) {
        return gbpromotionRepository.findById(gbPromoId).orElse(null);
    }

    public GbpromotionVO save(GbpromotionVO gbpromotionVO) {
        if (gbpromotionVO.getGbProdSpe() != null) {
            int sales = (int) Math.round(gbpromotionVO.getGbProdSpe() * 1.2);
            gbpromotionVO.setGbProdSales(sales);
        }
        return gbpromotionRepository.save(gbpromotionVO);
    }

    public void deleteById(Integer gbPromoId) {
    	gbpromotionRepository.deleteById(gbPromoId);
    }
    
    public List<GbpromotionVO> findAllByStore(StoreVO store) {
        return gbpromotionRepository.findByGbprod_Store(store);
    }
}
