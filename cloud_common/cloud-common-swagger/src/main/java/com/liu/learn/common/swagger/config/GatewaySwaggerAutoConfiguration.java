package com.liu.learn.common.swagger.config;

/**
 * <p>
 * 网关swagger 配置类，仅在webflux 环境生效
 */
//@RequiredArgsConstructor
//@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
//@ComponentScan("com.liu.learn.common.swagger.support")
public class GatewaySwaggerAutoConfiguration {

	/*private final SwaggerResourceHandler swaggerResourceHandler;

	private final SwaggerSecurityHandler swaggerSecurityHandler;

	private final SwaggerUiHandler swaggerUiHandler;

	private final SwaggerProperties swaggerProperties;

	@Bean
	public WebFluxSwaggerConfiguration fluxSwaggerConfiguration() {
		return new WebFluxSwaggerConfiguration();
	}

	@Bean
	public RouterFunction swaggerRouterFunction() {
		// 开启swagger 匹配路由
		if (swaggerProperties.getEnabled()) {
			return RouterFunctions
					.route(RequestPredicates.GET("/swagger-resources").and(RequestPredicates.accept(MediaType.ALL)),
							swaggerResourceHandler)
					.andRoute(RequestPredicates.GET("/swagger-resources/configuration/ui")
							.and(RequestPredicates.accept(MediaType.ALL)), swaggerUiHandler)
					.andRoute(RequestPredicates.GET("/swagger-resources/configuration/security")
							.and(RequestPredicates.accept(MediaType.ALL)), swaggerSecurityHandler);
		}
		else {
			// 关闭时，返回404
			return RouterFunctions.route(
					RequestPredicates.GET("/swagger-ui/**").and(RequestPredicates.accept(MediaType.ALL)),
					serverRequest -> ServerResponse.notFound().build());
		}
	}*/

}
