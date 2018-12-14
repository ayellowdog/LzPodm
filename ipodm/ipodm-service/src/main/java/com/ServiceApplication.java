package com;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.retry.annotation.EnableRetry;
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
@SpringBootApplication
//@EnableEncryptableProperties
@ComponentScan(basePackages = {"com.intel","com.inspur"})
@EnableJpaRepositories(basePackages = {"com.inspur", "com.intel"})
@EntityScan(basePackages = {"com.inspur", "com.intel"})
@EnableRetry
@Configuration
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
