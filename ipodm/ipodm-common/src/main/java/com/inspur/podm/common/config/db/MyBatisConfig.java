package com.inspur.podm.common.config.db;

import com.alibaba.druid.pool.DruidDataSource;
import com.github.pagehelper.PageHelper;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * MyBatisConfig配置.
 * @author Wanxian.He
 *
 */
@Configuration
public class MyBatisConfig {

    @Primary
    @Bean(name = "dataSource", destroyMethod = "close")
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource dataSource() {
        DruidDataSource druidDataSource = new DruidDataSource();
        return druidDataSource;
    }

    @Bean(name = "sqlSessionFactory")
    public SqlSessionFactoryBean sqlSessionFactory(ApplicationContext applicationContext, DataSource dataSource, PageHelper pageHelper) throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        sessionFactory.setMapperLocations(applicationContext.getResources("classpath*:mapper/**/*.xml"));
        return sessionFactory;
    }

    @Bean
    public DataSourceTransactionManager transactionManager(DataSource dataSource) {
    	DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager(dataSource);
        return dataSourceTransactionManager;
    }

    /**
     * PageHelper pageHelper = new PageHelper();
     Properties props = new Properties();
     props.setProperty("reasonable", "true");
     props.setProperty("supportMethodsArguments", "true");
     props.setProperty("returnPageInfo", "check");
     props.setProperty("params", "count=countSql");
     pageHelper.setProperties(props);
     //添加插件
     sessionFactory.setPlugins(new Interceptor[]{(Interceptor) pageHelper});
     * @return
     */
    @Bean
    public PageHelper pageHelper() {
        PageHelper pageHelper = new PageHelper();
        Properties p = new Properties();
        p.setProperty("offsetAsPageNum", "true");
        p.setProperty("rowBoundsWithCount", "true");
//        p.setProperty("reasonable", "false");
//        //通过设置pageSize=0或者RowBounds.limit = 0就会查询出全部的结果。
//        p.setProperty("pageSizeZero", "true");
        pageHelper.setProperties(p);
        return pageHelper;
    }

}
