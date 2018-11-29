package com.erik.ikeashoppinglist.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
    private ApiInfo metadata() {
        return new ApiInfoBuilder()//
                .title("Api Documentation (INSECURE!!)")//
                .description(
                        "Api Documentation for this IKEA sample app.")//
                .version("1.0.0")//
                .license("MIT License").licenseUrl("http://opensource.org/licenses/MIT")//
                .contact(new Contact("ME!", "localhost", "erik_zhang11@hotmail.com"))//
                .build();
    }

    /*
    private static final ApiInfo DEFAULT_API_INFO = new ApiInfo(
            "", "Api Documentation for this IKEA sample app", "1.0",
            "urn:tos", DEFAULT_CONTACT, "Apache 2.0",
            "http://www.apache.org/licenses/LICENSE-2.0");
    */
    private static final Set<String> DEFAULT_PRODUCES_AND_CONSUMES =
            new HashSet<String>(Arrays.asList("application/json",
                    "application/xml"));

    @Bean
    public Docket api(){
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(metadata())
                .produces(DEFAULT_PRODUCES_AND_CONSUMES)
                .consumes(DEFAULT_PRODUCES_AND_CONSUMES)
                .genericModelSubstitutes(Optional.class);
    }

}