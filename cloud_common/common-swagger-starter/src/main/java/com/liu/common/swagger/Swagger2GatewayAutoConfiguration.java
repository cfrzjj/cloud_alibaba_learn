/*
 * Copyright (c) 2018, hiwepy (https://github.com/hiwepy).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.liu.common.swagger;

import com.liu.common.swagger.filter.SwaggerHeaderFilter;
import com.liu.common.swagger.handler.SwaggerResourceHandler;
import com.liu.common.swagger.handler.SwaggerSecurityHandler;
import com.liu.common.swagger.handler.SwaggerUiHandler;
import com.liu.common.swagger.model.DocketInfo;
import com.liu.common.swagger.model.GlobalOperationParameter;
import com.liu.common.swagger.model.GlobalResponseMessage;
import com.liu.common.swagger.model.GlobalResponseMessageBody;
import com.liu.common.swagger.provider.GatewaySwaggerResourcesProvider;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.config.GatewayProperties;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.config.ResourceHandlerRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.*;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.*;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 *	 ?????????
 * http://www.spring4all.com/article/1381
 * https://my.oschina.net/go4it/blog/3035218
 * 	????????????????????????https://github.com/SpringForAll/spring-boot-starter-swagger
 */
@Configuration
@ConditionalOnProperty(prefix = Swagger2GatewayProperties.PREFIX, value = "enabled", havingValue = "true")
@EnableConfigurationProperties({ Swagger2GatewayProperties.class })
@EnableSwagger2
@Import({ BeanValidatorPluginsConfiguration.class })
public class Swagger2GatewayAutoConfiguration implements BeanFactoryAware, WebFluxConfigurer {

	private BeanFactory beanFactory;

	@Bean
	public UiConfiguration uiConfiguration(Swagger2GatewayProperties swaggerProperties) {
		return UiConfigurationBuilder.builder()
				.deepLinking(swaggerProperties.getUiConfig().getDeepLinking())
				.defaultModelExpandDepth(swaggerProperties.getUiConfig().getDefaultModelExpandDepth())
				.defaultModelRendering(swaggerProperties.getUiConfig().getDefaultModelRendering())
				.defaultModelsExpandDepth(swaggerProperties.getUiConfig().getDefaultModelsExpandDepth())
				.displayOperationId(swaggerProperties.getUiConfig().getDisplayOperationId())
				.displayRequestDuration(swaggerProperties.getUiConfig().getDisplayRequestDuration())
				.docExpansion(swaggerProperties.getUiConfig().getDocExpansion())
				.maxDisplayedTags(swaggerProperties.getUiConfig().getMaxDisplayedTags())
				.operationsSorter(swaggerProperties.getUiConfig().getOperationsSorter())
				.showExtensions(swaggerProperties.getUiConfig().getShowExtensions())
				.showCommonExtensions(swaggerProperties.getUiConfig().getShowCommonExtensions())
				.supportedSubmitMethods(UiConfiguration.Constants.DEFAULT_SUBMIT_METHODS)
				//.swaggerUiBaseUrl(swaggerUiBaseUrl)
				.tagsSorter(swaggerProperties.getUiConfig().getTagsSorter())
				.validatorUrl(swaggerProperties.getUiConfig().getValidatorUrl())
				.build();
	}

	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnBean(UiConfiguration.class)
	@ConditionalOnProperty(prefix = Swagger2GatewayProperties.PREFIX, name = "enabled", matchIfMissing = true)
	public List<Docket> createRestApi(Swagger2GatewayProperties swaggerProperties) {
		ConfigurableBeanFactory configurableBeanFactory = (ConfigurableBeanFactory) beanFactory;
		List<Docket> docketList = new LinkedList<>();

		// ????????????
		if (CollectionUtils.isEmpty(swaggerProperties.getDocket())) {
			
			ApiInfo apiInfo = new ApiInfoBuilder()
					.title(swaggerProperties.getTitle())
					.description(swaggerProperties.getDescription())
					.version(swaggerProperties.getVersion())
					.license(swaggerProperties.getLicense())
					.licenseUrl(swaggerProperties.getLicenseUrl())
					.contact(new Contact(swaggerProperties.getContact().getName(), swaggerProperties.getContact().getUrl(), swaggerProperties.getContact().getEmail()))
					.termsOfServiceUrl(swaggerProperties.getTermsOfServiceUrl())
					.build();

			Docket docketForBuilder = new Docket(DocumentationType.SWAGGER_2)
					.host(swaggerProperties.getHost())
					.apiInfo(apiInfo)
					.securityContexts(Collections.singletonList(securityContext(swaggerProperties)))
					.globalRequestParameters(buildGlobalOperationParametersFromSwagger2WebFluxProperties( swaggerProperties.getGlobalOperationParameters()));

			switch (swaggerProperties.getAuthorization().getType()) {
				case APIKEY:{
					docketForBuilder.securitySchemes(Collections.singletonList(apiKey(swaggerProperties)));
				};break;
				case BASICAUTH:{
					docketForBuilder.securitySchemes(Collections.singletonList(basicAuth(swaggerProperties)));
				};break;
				default:{
					
				};break;
			}

			// ??????????????????
			if (!swaggerProperties.isApplyDefaultResponseMessages()) {
				buildGlobalResponseMessage(swaggerProperties, docketForBuilder);
			}

			Docket docket = docketForBuilder.select()
					.apis( StringUtils.hasText(swaggerProperties.getBasePackage()) ? RequestHandlerSelectors.basePackage(swaggerProperties.getBasePackage()) : RequestHandlerSelectors.any())
					.paths(StringUtils.hasText(swaggerProperties.getBasePathPattern()) ? PathSelectors.ant(swaggerProperties.getBasePathPattern()) : PathSelectors.any())
					.build();

			/* ignoredParameterTypes **/
			Class<?>[] array = new Class[swaggerProperties.getIgnoredParameterTypes().size()];
			Class<?>[] ignoredParameterTypes = swaggerProperties.getIgnoredParameterTypes().toArray(array);
			docket.ignoredParameterTypes(ignoredParameterTypes)
					.enableUrlTemplating(swaggerProperties.isEnableUrlTemplating())
					.forCodeGeneration(swaggerProperties.isForCodeGen());

			configurableBeanFactory.registerSingleton("defaultDocket", docket);
			
			docketList.add(docket);
			
			return docketList;
			
		} 
		
		// ????????????
		for (String groupName : swaggerProperties.getDocket().keySet()) {
			
			DocketInfo docketInfo = swaggerProperties.getDocket().get(groupName);

			ApiInfo apiInfo = new ApiInfoBuilder()
					.title(docketInfo.getTitle().isEmpty() ? swaggerProperties.getTitle() : docketInfo.getTitle())
					.description(docketInfo.getDescription().isEmpty() ? swaggerProperties.getDescription() : docketInfo.getDescription())
					.version(docketInfo.getVersion().isEmpty() ? swaggerProperties.getVersion() : docketInfo.getVersion())
					.license(docketInfo.getLicense().isEmpty() ? swaggerProperties.getLicense() : docketInfo.getLicense())
					.licenseUrl(docketInfo.getLicenseUrl().isEmpty() ? swaggerProperties.getLicenseUrl() : docketInfo.getLicenseUrl())
					.contact(new Contact(
							docketInfo.getContact().getName().isEmpty() ? swaggerProperties.getContact().getName() : docketInfo.getContact().getName(),
							docketInfo.getContact().getUrl().isEmpty() ? swaggerProperties.getContact().getUrl() : docketInfo.getContact().getUrl(),
							docketInfo.getContact().getEmail().isEmpty() ? swaggerProperties.getContact().getEmail() : docketInfo.getContact().getEmail()))
					.termsOfServiceUrl(
							docketInfo.getTermsOfServiceUrl().isEmpty() ? swaggerProperties.getTermsOfServiceUrl() : docketInfo.getTermsOfServiceUrl())
					.build();

			Docket docketForBuilder = new Docket(DocumentationType.SWAGGER_2).host(swaggerProperties.getHost())
					.apiInfo(apiInfo)
					.securityContexts(Collections.singletonList(securityContext(swaggerProperties)))
					.globalRequestParameters(assemblyGlobalOperationParameters(swaggerProperties.getGlobalOperationParameters(), docketInfo.getGlobalOperationParameters()));

			switch (swaggerProperties.getAuthorization().getType()) {
				case APIKEY:{
					docketForBuilder.securitySchemes(Collections.singletonList(apiKey(swaggerProperties)));
				};break;
				case BASICAUTH:{
					docketForBuilder.securitySchemes(Collections.singletonList(basicAuth(swaggerProperties)));
				};break;
				default:{
					
				};break;
			}

			// ??????????????????
			if (!swaggerProperties.isApplyDefaultResponseMessages()) {
				buildGlobalResponseMessage(swaggerProperties, docketForBuilder);
			}

			Docket docket = docketForBuilder.groupName(groupName).select()
					.apis(RequestHandlerSelectors.basePackage(docketInfo.getBasePackage()))
					.paths(StringUtils.hasText(docketInfo.getBasePathPattern()) ? PathSelectors.ant(docketInfo.getBasePathPattern()) : PathSelectors.any())
					.build();

			/* ignoredParameterTypes **/
			Class<?>[] array = new Class[docketInfo.getIgnoredParameterTypes().size()];
			Class<?>[] ignoredParameterTypes = docketInfo.getIgnoredParameterTypes().toArray(array);
			docket.ignoredParameterTypes(ignoredParameterTypes)
					.enableUrlTemplating(docketInfo.isEnableUrlTemplating())
					.forCodeGeneration(docketInfo.isForCodeGen());

			configurableBeanFactory.registerSingleton(groupName, docket);
			
			docketList.add(docket);
		}
			
		return docketList;
	}

	/**
	 * ???????????? ApiKey ???????????????
	 *
	 * @return
	 */
	private ApiKey apiKey(Swagger2GatewayProperties swaggerProperties) {
		return new ApiKey(swaggerProperties.getAuthorization().getName(),
				swaggerProperties.getAuthorization().getKeyName(), ApiKeyVehicle.HEADER.getValue());
	}

	/**
	 * ???????????? BasicAuth ???????????????
	 *
	 * @return
	 */
	private BasicAuth basicAuth(Swagger2GatewayProperties swaggerProperties) {
		return new BasicAuth(swaggerProperties.getAuthorization().getName());
	}

	/**
	 * ????????????????????????????????????????????????????????????????????????????????????????????? ^.*$ ????????????URL ?????? securityReferences ??????????????????????????????
	 *
	 * @return
	 */
	private SecurityContext securityContext(Swagger2GatewayProperties swaggerProperties) {
		Predicate<String> predicate = PathSelectors.regex(swaggerProperties.getAuthorization().getAuthRegex());
		return SecurityContext.builder().securityReferences(defaultAuth(swaggerProperties))
				.operationSelector((ctx) -> {
					return predicate.test(ctx.requestMappingPattern());
				}).build();
	}
	
	/**
	 * ??????????????????????????????????????????????????? SecurityReference ??????reference
	 * ??????ApiKey???????????????name???????????????????????????????????????
	 *
	 * @return
	 */
	private List<SecurityReference> defaultAuth(Swagger2GatewayProperties swaggerProperties) {
		AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
		AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
		authorizationScopes[0] = authorizationScope;
		return Collections.singletonList(SecurityReference.builder()
				.reference(swaggerProperties.getAuthorization().getName()).scopes(authorizationScopes).build());
	}

	private List<RequestParameter> buildGlobalOperationParametersFromSwagger2WebFluxProperties(
			List<GlobalOperationParameter> globalRequestParameters) {
		List<RequestParameter> parameters = new ArrayList<RequestParameter>();

		if (Objects.isNull(globalRequestParameters)) {
			return parameters;
		}
		for (GlobalOperationParameter globalOperationParameter : globalRequestParameters) {
			parameters.add(new RequestParameterBuilder()
					.name(globalOperationParameter.getName())
					.description(globalOperationParameter.getDescription())
					//.modelRef(new ModelRef(globalOperationParameter.getModelRef()))
					//.parameterType(globalOperationParameter.getParameterType())
					.required(Boolean.parseBoolean(globalOperationParameter.getRequired())).build());
		}
		return parameters;
	}

	/**
	 * ??????????????????name??????????????????
	 *
	 * @param globalRequestParameters
	 * @param docketOperationParameters
	 * @return
	 */
	private List<RequestParameter> assemblyGlobalOperationParameters(List<GlobalOperationParameter> globalRequestParameters,
			List<GlobalOperationParameter> docketOperationParameters) {

		if (Objects.isNull(docketOperationParameters) || docketOperationParameters.isEmpty()) {
			return buildGlobalOperationParametersFromSwagger2WebFluxProperties(globalRequestParameters);
		}

		Set<String> docketNames = docketOperationParameters.stream().map(GlobalOperationParameter::getName)
				.collect(Collectors.toSet());

		List<GlobalOperationParameter> resultOperationParameters = new ArrayList<GlobalOperationParameter>();

		if (Objects.nonNull(globalRequestParameters)) {
			for (GlobalOperationParameter parameter : globalRequestParameters) {
				if (!docketNames.contains(parameter.getName())) {
					resultOperationParameters.add(parameter);
				}
			}
		}

		resultOperationParameters.addAll(docketOperationParameters);
		return buildGlobalOperationParametersFromSwagger2WebFluxProperties(resultOperationParameters);
	}

	/**
	 * ????????????????????????
	 *
	 * @param swaggerProperties swaggerProperties ?????? POST,GET,PUT,PATCH,DELETE,HEAD,OPTIONS,TRACE
	 * @param docketForBuilder  swagger docket builder
	 */
	private void buildGlobalResponseMessage(Swagger2GatewayProperties swaggerProperties, Docket docketForBuilder) {

		GlobalResponseMessage globalResponseMessages = swaggerProperties.getGlobalResponseMessage();

		/* POST,GET,PUT,PATCH,DELETE,HEAD,OPTIONS,TRACE ??????????????? **/
		List<Response> postResponseMessages = getResponseMessageList(globalResponseMessages.getPost());
		List<Response> getResponseMessages = getResponseMessageList(globalResponseMessages.getGet());
		List<Response> putResponseMessages = getResponseMessageList(globalResponseMessages.getPut());
		List<Response> patchResponseMessages = getResponseMessageList(globalResponseMessages.getPatch());
		List<Response> deleteResponseMessages = getResponseMessageList(globalResponseMessages.getDelete());
		List<Response> headResponseMessages = getResponseMessageList(globalResponseMessages.getHead());
		List<Response> optionsResponseMessages = getResponseMessageList(globalResponseMessages.getOptions());
		List<Response> trackResponseMessages = getResponseMessageList(globalResponseMessages.getTrace());

		docketForBuilder.useDefaultResponseMessages(swaggerProperties.isApplyDefaultResponseMessages())
				.globalResponses(HttpMethod.POST, postResponseMessages)
				.globalResponses(HttpMethod.GET, getResponseMessages)
				.globalResponses(HttpMethod.PUT, putResponseMessages)
				.globalResponses(HttpMethod.PATCH, patchResponseMessages)
				.globalResponses(HttpMethod.DELETE, deleteResponseMessages)
				.globalResponses(HttpMethod.HEAD, headResponseMessages)
				.globalResponses(HttpMethod.OPTIONS, optionsResponseMessages)
				.globalResponses(HttpMethod.TRACE, trackResponseMessages);
	}

	/**
	 * ???????????????????????????
	 * @param globalResponseMessageBodyList ??????Code??????????????????
	 * @return
	 */
	private List<Response> getResponseMessageList(
			List<GlobalResponseMessageBody> globalResponseMessageBodyList) {
		List<Response> responseMessages = new ArrayList<>();
		for (GlobalResponseMessageBody globalResponseMessageBody : globalResponseMessageBodyList) {
			ResponseBuilder responseMessageBuilder = new ResponseBuilder()
						.code(globalResponseMessageBody.getCode())
						.description(globalResponseMessageBody.getMessage());

			if (!StringUtils.isEmpty(globalResponseMessageBody.getModelRef())) {
				//responseMessageBuilder.responseModel(new ModelRef(globalResponseMessageBody.getModelRef()));
			}
			responseMessages.add(responseMessageBuilder.build());
		}

		return responseMessages;
	}
	
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		if(!registry.hasMappingForPattern("/doc.html**")) {
			registry.addResourceHandler("/doc.html**").addResourceLocations("classpath:/META-INF/resources/");
		}
		if(!registry.hasMappingForPattern("/swagger-ui.html**")) {
			registry.addResourceHandler("/swagger-ui.html**").addResourceLocations("classpath:/META-INF/resources/");
		}
		if(!registry.hasMappingForPattern("/webjars/**")) {
			registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
			//registry.addResourceHandler("/webjars/**").addResourceLocations("/webjars/").resourceChain(true).addResolver(new WebJarsResourceResolver());
		}
	}
	
	@Bean
	@Primary
	public SwaggerResourcesProvider swaggerResources(RouteLocator routeLocator,
			GatewayProperties gatewayProperties,Swagger2GatewayProperties swagger2GatewayProperties ) {
		return new GatewaySwaggerResourcesProvider(routeLocator, gatewayProperties, swagger2GatewayProperties);
	}

	@Bean
	public SwaggerResourceHandler swaggerResourceHandler(SwaggerResourcesProvider swaggerResources) {
		return new SwaggerResourceHandler(swaggerResources);
	}

	@Bean
	public SwaggerSecurityHandler swaggerSecurityHandler(
			@Autowired(required = false) SecurityConfiguration securityConfiguration) {
		return new SwaggerSecurityHandler(securityConfiguration);
	}

	@Bean
	public SwaggerUiHandler swaggerUiHandler(@Autowired(required = false) UiConfiguration uiConfiguration) {
		return new SwaggerUiHandler(uiConfiguration);
	}

	@Bean
	public RouterFunction<?> routerFunction(SwaggerResourceHandler swaggerResourceHandler,
			SwaggerSecurityHandler swaggerSecurityHandler, SwaggerUiHandler swaggerUiHandler) {
		return RouterFunctions
				.route(RequestPredicates.GET("/swagger-resources").and(RequestPredicates.accept(MediaType.ALL)),
						swaggerResourceHandler)
				.andRoute(RequestPredicates.GET("/swagger-resources/configuration/ui")
						.and(RequestPredicates.accept(MediaType.ALL)), swaggerUiHandler)
				.andRoute(RequestPredicates.GET("/swagger-resources/configuration/security")
						.and(RequestPredicates.accept(MediaType.ALL)), swaggerSecurityHandler);
	}
	
	@Bean
	public SwaggerHeaderFilter swaggerHeaderFilter() {
		return new SwaggerHeaderFilter();
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}

}