package com.inspur.podm.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 
 * @ClassName: ServiceApplication
 * @Description: PODM的SpringBoot入口
 *
 * @author: liuchangbj
 * @date: 2018年11月12日 上午10:44:28
 */
@EnableScheduling
@EnableAsync
@EnableTransactionManagement
@SpringBootApplication(exclude = JpaRepositoriesAutoConfiguration.class)
//@EnableEncryptableProperties
@ComponentScan(basePackages = "com.inspur.podm.service")
public class ServiceApplication {
	/**
	 * 
	 * <p> SpringBoot入口 </p>
	 * 
	 * @author: liuchangbj
	 * @date: 2018年11月12日 上午10:44:36
	 * @param args
	 */
    public static void main(String[] args) {
        SpringApplication.run(ServiceApplication.class, args);
    }
}
