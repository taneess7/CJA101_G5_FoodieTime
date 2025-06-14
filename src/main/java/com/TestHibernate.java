package com;

import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.foodietime.smg.model.SmgService;
import com.foodietime.smg.model.SmgVO;

import jakarta.transaction.Transactional;

@SpringBootApplication
public class TestHibernate {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(TestHibernate.class);
        app.setWebApplicationType(WebApplicationType.NONE);
        ConfigurableApplicationContext context = app.run(args);

        SmgService smgService = context.getBean(SmgService.class);
        List<SmgVO> smglist = smgService.findAllSmgs();
        for (SmgVO smg : smglist) {
            System.out.println(smg);
        }
    }
}
