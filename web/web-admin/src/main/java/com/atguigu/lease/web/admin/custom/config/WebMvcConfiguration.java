package com.atguigu.lease.web.admin.custom.config;

import com.atguigu.lease.web.admin.custom.converter.StringToBaseEnumConverterFactory;
import com.atguigu.lease.web.admin.custom.converter.StringToItemTypeConverter;
import com.atguigu.lease.web.admin.interceptor.AuthenticationInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

    @Autowired
    private StringToItemTypeConverter converter;

    @Autowired
    private StringToBaseEnumConverterFactory stringToBaseEnumConverterFactory;

    @Autowired
    private AuthenticationInterceptor authenticationInterceptor;

    @Override
    public void addFormatters(FormatterRegistry registry) {
        //registry.addConverter(this.converter);
        registry.addConverterFactory(stringToBaseEnumConverterFactory); // 枚举转换器
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry
                .addInterceptor(authenticationInterceptor) // 拦截器
                .addPathPatterns("/admin/**") // 拦截所有/admin开头的请求
                .excludePathPatterns("/admin/login/**"); // 排除/admin/login开头的请求
    }
}
