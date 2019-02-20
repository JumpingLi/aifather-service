package com.champion.readingstory.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: JiangPing Li
 * @date: 2018-09-05 11:21
 */
@Profile(value = {"dev","stage"})
@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Docket createRestApi() {
        List<Parameter> pars = new ArrayList<>();
        ParameterBuilder param1 = new ParameterBuilder();
        param1.name("openId")
                .description("user openId")
                .defaultValue("abc")
                .modelRef(new ModelRef("string"))
                .parameterType("header")
                .required(true).build();
        pars.add(param1.build());
//        ParameterBuilder param2 = new ParameterBuilder();
//        param2.name("sign")
//                .description("sign")
//                .defaultValue("a4f6443f007e18ea45a020aaf869be43")
//                .modelRef(new ModelRef("string"))
//                .parameterType("header")
//                .required(true).build();
//        pars.add(param2.build());
//        ParameterBuilder param3 = new ParameterBuilder();
//        param3.name("timestamp")
//                .description("时间戳毫秒")
//                .defaultValue("123")
//                .modelRef(new ModelRef("string"))
//                .parameterType("header")
//                .required(true).build();
//        pars.add(param3.build());
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .globalOperationParameters(pars)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.iflytek.readingstory.controller"))
                .paths(PathSelectors.any()).build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("AI讲故事项目 Restul APIs")
                .description("AI讲故事项目后台api接口文档")
                .version("1.0")
                .build();
    }

}
