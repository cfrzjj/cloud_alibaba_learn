package com.liu.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.RequestParameterBuilder;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.schema.ScalarType;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.ApiSelectorBuilder;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

@Slf4j
@Configuration
@EnableAutoConfiguration
@EnableOpenApi//开启swagger,当前版本为3 所以注解和2@EnableSwagger2的版本不同
//或者直接省略prefix，那么直接写为swagger.enabled，当配置中存在swagger.enabled生效，matchIfMissing = true默认生效
@ConditionalOnProperty(prefix = "swagger",name = "enabled", matchIfMissing = true)
public class SwaggerAutoConfig {

    //默认的排除路径，排除Spring Boot默认的错误处理路径和端点(在解析的url规则之上)  /*/error，由于服务通常加前缀，所以前面/*忽略前缀
    private static final List<String> DEFAULT_EXCLUDE_PATH = Arrays.asList("/error","/actuator/**","/*/error");

    //swagger会解析的url规则
    private static final String BASE_PATH = "/**";

    @Autowired
    private SwaggerProperties swaggerProperties;

    @Bean
    public Docket createRestApi() {

        // base-path处理
        if (swaggerProperties.getBasePath().isEmpty()) {
            swaggerProperties.getBasePath().add(BASE_PATH);
        }

        // exclude-path处理
        if (swaggerProperties.getExcludePath().isEmpty()) {
            swaggerProperties.getExcludePath().addAll(DEFAULT_EXCLUDE_PATH);
        }

        //需要排除的url
        List<Predicate<String>> excludePath = new ArrayList<>();
        swaggerProperties.getExcludePath().forEach(path -> excludePath.add(PathSelectors.ant(path)));

        // 版本请求头处理
        List<RequestParameter> pars = new ArrayList<>();

        RequestParameterBuilder versionPar = new RequestParameterBuilder().description("灰度路由版本信息")
                .in(ParameterType.HEADER).name("VERSION").required(false)
                .query(param -> param.model(model -> model.scalarModel(ScalarType.STRING)));

        pars.add(versionPar.build());
        ApiSelectorBuilder builder = new Docket(DocumentationType.OAS_30)
                .host(swaggerProperties.getHost())
                .apiInfo(apiInfo()).globalRequestParameters(pars)
                .select()
                .apis(RequestHandlerSelectors.basePackage(swaggerProperties.getBasePackage()));

        swaggerProperties.getBasePath().forEach(p -> builder.paths(PathSelectors.ant(p)));

        swaggerProperties.getExcludePath().forEach(p -> builder.paths(PathSelectors.ant(p).negate()));

        return builder.build().securitySchemes(Collections.singletonList(securitySchema()))
                .securityContexts(Collections.singletonList(securityContext())).pathMapping("/");

    }

    /**
     * 配置默认的全局鉴权策略的开关，通过正则表达式进行匹配；默认匹配所有URL
     * @return
     */
    private SecurityContext securityContext() {
        return SecurityContext.builder().securityReferences(defaultAuth()).build();
    }

    /**
     * 默认的全局鉴权策略
     * @return
     */
    private List<SecurityReference> defaultAuth() {
        ArrayList<AuthorizationScope> authorizationScopeList = new ArrayList<>();
        swaggerProperties.getAuthorization().getAuthorizationScopeList()
                .forEach(authorizationScope -> authorizationScopeList.add(
                        new AuthorizationScope(authorizationScope.getScope(), authorizationScope.getDescription())));
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[authorizationScopeList.size()];
        return Collections
                .singletonList(SecurityReference.builder().reference(swaggerProperties.getAuthorization().getName())
                        .scopes(authorizationScopeList.toArray(authorizationScopes)).build());
    }


    private OAuth securitySchema() {
        ArrayList<AuthorizationScope> authorizationScopeList = new ArrayList<>();
        swaggerProperties.getAuthorization().getAuthorizationScopeList()
                .forEach(authorizationScope -> authorizationScopeList.add(
                        new AuthorizationScope(authorizationScope.getScope(), authorizationScope.getDescription())));
        ArrayList<GrantType> grantTypes = new ArrayList<>();
        swaggerProperties.getAuthorization().getTokenUrlList()
                .forEach(tokenUrl -> grantTypes.add(new ResourceOwnerPasswordCredentialsGrant(tokenUrl)));
        return new OAuth(swaggerProperties.getAuthorization().getName(), authorizationScopeList, grantTypes);
    }


    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title(swaggerProperties.getTitle())
                .description(swaggerProperties.getDescription())
                .version(swaggerProperties.getVersion())
                .license(swaggerProperties.getLicense())
                .licenseUrl(swaggerProperties.getLicenseUrl())
                .termsOfServiceUrl(swaggerProperties.getTermsOfServiceUrl())
                .contact(new Contact(
                        swaggerProperties.getContact().getName(),
                        swaggerProperties.getContact().getUrl(),
                        swaggerProperties.getContact().getEmail()
                ))
                .build();
    }

}

