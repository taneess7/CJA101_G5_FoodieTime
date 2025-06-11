package com;

import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.foodietime.smg.model.SmgService;
import com.foodietime.smg.model.SmgVO;

@SpringBootApplication
public class TestHibernate {

	public static void main(String[] args) {
		// 啟動 Spring Boot 並取得 ApplicationContext
//        ConfigurableApplicationContext context = SpringApplication.run(TestHibernateCampsiteOrder.class, args);
		SpringApplication app = new SpringApplication(TestHibernate.class);
		app.setWebApplicationType(WebApplicationType.NONE); // 🟢 禁用 Web 模式
		ConfigurableApplicationContext context = app.run(args);

		SmgService smgService = context.getBean(SmgService.class);
		List<SmgVO> smglist = smgService.findAllSmgs();
		for (SmgVO smg : smglist) {
			System.out.println(smg); // 前提是你有 override toString()
		}
	}

}