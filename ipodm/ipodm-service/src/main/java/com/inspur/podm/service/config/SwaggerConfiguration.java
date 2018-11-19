package com.inspur.podm.service.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Created by Wanxian.He on 2017/3/28 0028.
 */
@Configuration
@EnableSwagger2
@PropertySource(value = "classpath:ispim_swagger.properties", ignoreResourceNotFound = true)
public class SwaggerConfiguration {

	@Bean
	public Docket createRestApi(@Value("${server.swagger.context-path}") String contextPath,
			@Value("${swagger.base.package}") String basePackage, final ApiInfo apiInfo) {
		List<Parameter> params = new ArrayList<Parameter>();
		params.add(new ParameterBuilder().parameterType("header").name("language").defaultValue("zh_CN")
				.modelRef(new ModelRef("string")).required(false).build());
		return new Docket(DocumentationType.SWAGGER_2).pathMapping(contextPath).apiInfo(apiInfo)
				.globalOperationParameters(params).select().apis(RequestHandlerSelectors.basePackage(basePackage))
				.build();
	}

	@Bean
	public ApiInfo apiInfo(@Value("${swagger.api.info.title}") String title,
			@Value("${swagger.api.info.description}") String description,
			@Value("${swagger.api.info.contact}") String contact,
			@Value("${swagger.api.info.version}") String version) {
		return new ApiInfoBuilder().title(title).description(description).contact(contact).version(version).build();
	}

}
