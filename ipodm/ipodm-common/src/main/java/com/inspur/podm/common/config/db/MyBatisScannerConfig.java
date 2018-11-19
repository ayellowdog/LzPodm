package com.inspur.podm.common.config.db;

import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.inspur.podm.common.utils.PropertiesUtil;




/**
 * Created by Wanxian.He on 2017/3/22 0022.
 */
@Configuration
@AutoConfigureAfter(MyBatisConfig.class)
public class MyBatisScannerConfig {

    @Bean
    public MapperScannerConfigurer mapperScannerConfigurer(){
        MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
        mapperScannerConfigurer.setSqlSessionFactoryBeanName("sqlSessionFactory");
        mapperScannerConfigurer.setBasePackage(PropertiesUtil.getValue("mapper.scan.basePackage"));
        return mapperScannerConfigurer;
    }
}
