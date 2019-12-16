package com.yianit.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.accept.ContentNegotiationManagerFactoryBean;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.yianit.intercept.AdminAccessIntercept;

/**
 * WebMvc 配置
 * 
 * @author HL.King
 */
// @EnableWebMvc
//@Configuration
//@AutoConfigureAfter
public class SysWebMvcConfigurer implements WebMvcConfigurer {
	@Autowired
	@Qualifier("adminAccessIntercept")
	private AdminAccessIntercept adminAccessIntercept;
	@Bean
	public WebServerFactoryCustomizer<ConfigurableWebServerFactory> webServerFactoryCustomizer() {
		return factory -> {
			ErrorPage errorPage400 = new ErrorPage(HttpStatus.BAD_REQUEST, "/error/400");
			ErrorPage errorPage404 = new ErrorPage(HttpStatus.NOT_FOUND, "/error/404");
			ErrorPage errorPage500 = new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/error/500");
			ErrorPage errorPage401 = new ErrorPage(HttpStatus.UNAUTHORIZED, "/error/401");
			ErrorPage errorPage403 = new ErrorPage(HttpStatus.FORBIDDEN, "/error/403");
			factory.addErrorPages(errorPage400, errorPage401, errorPage403, errorPage404, errorPage500);
		};
	}

	/**
	 * 文件上传配置
	 * 
	 * @return
	 */
	@Bean
	public CommonsMultipartResolver commonsMultipartResolver() {
		CommonsMultipartResolver resolver = new CommonsMultipartResolver();
		resolver.setMaxUploadSize(100_000_000L);
		return resolver;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		WebMvcConfigurer.super.addInterceptors(registry);
		registry.addInterceptor(adminAccessIntercept).addPathPatterns("/admin/**");
//		registry.addInterceptor(new AppAccessIntercept()).addPathPatterns("/app/**").excludePathPatterns("/app/login").excludePathPatterns("/app/getopenid.do").excludePathPatterns("/app/wxrz.do");
//		registry.addInterceptor(new BindMobileIntercept()).addPathPatterns("/app/**").excludePathPatterns("/app/bindMobile/**").excludePathPatterns("/app/login").excludePathPatterns("/app/getopenid.do").excludePathPatterns("/app/wxrz.do")
//				.excludePathPatterns("/app/verification-code");
//		registry.addInterceptor(new StaffAccessIntercept()).addPathPatterns("/staff/**").excludePathPatterns("/staff/login/**");
//		registry.addInterceptor(new ApiIntercept()).addPathPatterns("/api/device/**");
	}

	@Resource
	public void configureDefaultViews(ContentNegotiatingViewResolver configurer) {
		MappingJackson2JsonView resolver = new MappingJackson2JsonView();
		resolver.setExtractValueFromSingleKeyModel(true);
		List<View> views = new ArrayList<>();
		views.add(resolver);
		configurer.setDefaultViews(views);
	}

	@Override
	public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
		WebMvcConfigurer.super.configureContentNegotiation(configurer);
	}

//	@Bean
//	public FilterRegistrationBean<ParamFilter> createParamsFilterBean() {
//		FilterRegistrationBean<ParamFilter> registration = new FilterRegistrationBean<>();
//		registration.setFilter(new ParamFilter());// 添加过滤器
//		registration.addUrlPatterns("/admin/*", "/api/*", "/app/*", "/staff/*");// 设置过滤路径，/*所有路径
//		registration.setName("params filter");
//		registration.setOrder(1);// 设置优先级
//		return registration;
//	}

	//@Bean
	//@Primary
	public ContentNegotiationManagerFactoryBean contentNegotiationManagerFactoryBean() {
		ContentNegotiationManagerFactoryBean cnManage = new ContentNegotiationManagerFactoryBean();
		// 1、开启默认匹配,如果所有的mediaType都没匹配上，就会使用defaultContentType
		cnManage.setDefaultContentType(MediaType.TEXT_HTML);
		// 2、支持accept-header匹配,这里是否忽略掉accept header，默认就是false
		cnManage.setIgnoreAcceptHeader(true);
		// 3、支持后缀匹配
		cnManage.setFavorPathExtension(true);
		// 4、支持参数匹配
		cnManage.setFavorParameter(true);
		// 预设key对应后缀名及参数format的值， 与匹配策略3，4相关
		cnManage.setMediaTypes(getMediaTypes());
		return cnManage;
	}

	/**
	 * 获取按后缀匹配类型列表
	 * 
	 * @return
	 */
	private Properties getMediaTypes() {
		Properties pro = new Properties();
		pro.put("html", MediaType.TEXT_HTML_VALUE);
		pro.put("spring", MediaType.TEXT_HTML_VALUE);
		pro.put("do", MediaType.TEXT_HTML_VALUE);
		pro.put("json", MediaType.APPLICATION_JSON_VALUE);
		pro.put("action", MediaType.APPLICATION_XML_VALUE);
		return pro;
	}
//	@Override
//	public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        //registry.addResourceHandler("/static/**").addResourceLocations("file:d:/tt/static/");
//        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/META-INF/resources/,classpath:/resources/,classpath:/static/");
//    }
}
