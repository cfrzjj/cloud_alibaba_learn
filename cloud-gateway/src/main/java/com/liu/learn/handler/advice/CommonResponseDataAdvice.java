package com.liu.learn.handler.advice;


import com.alibaba.fastjson.JSON;
import com.liu.learn.handler.GlobalDefaultProperties;
import com.liu.learn.handler.Result;
import com.liu.learn.handler.annotation.IgnoreResponseAdvice;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.Nullable;

/**
 *
 * @author <a href="mailto:yaoonlyi@gmail.com">purgeyao</a>
 * @since 1.0.0
 */
//@RestControllerAdvice
public class CommonResponseDataAdvice  {

    private GlobalDefaultProperties globalDefaultProperties;

    public CommonResponseDataAdvice(GlobalDefaultProperties globalDefaultProperties) {
        this.globalDefaultProperties = globalDefaultProperties;
    }

    @SuppressWarnings("all")
    public boolean supports(MethodParameter methodParameter,
                            Class<? extends HttpMessageConverter<?>> aClass) {
        return filter(methodParameter);
    }

    @Nullable
    @SuppressWarnings("all")
    public Object beforeBodyWrite(Object o, MethodParameter methodParameter, MediaType mediaType,
                                  Class<? extends HttpMessageConverter<?>> aClass, ServerHttpRequest serverHttpRequest,
                                  ServerHttpResponse serverHttpResponse) {

        // 返回值为 Object 类型  并且返回为空是  AbstractMessageConverterMethodProcessor#writeWithMessageConverters 方法
        // 无法触发调用本类的 beforeBodyWrite 处理，开发在 Controller 尽量避免直接使用 Object 类型返回。

        // o is null -> return response
        if (o == null) {
            // 当 o 返回类型为 string 并且为null会出现 java.lang.ClassCastException: Result cannot be cast to java.lang.String
            if (methodParameter.getParameterType().getName().equals("java.lang.String")) {
                return JSON.toJSON(Result.ofSuccess()).toString();
            }
            return Result.ofSuccess();
        }
        // o is instanceof ConmmonResponse -> return o
        if (o instanceof Result) {
            return (Result<Object>) o;
        }
        // string 特殊处理 java.lang.ClassCastException: Result cannot be cast to java.lang.String
        if (o instanceof String) {
            return JSON.toJSON(Result.ofSuccess(o)).toString();
        }
        return Result.ofSuccess(o);
    }

    private Boolean filter(MethodParameter methodParameter) {
        Class<?> declaringClass = methodParameter.getDeclaringClass();
        // 检查过滤包路径
        long count = globalDefaultProperties.getAdviceFilterPackage().stream()
                .filter(l -> declaringClass.getName().contains(l)).count();
        if (count > 0) {
            return false;
        }
        // 检查<类>过滤列表
        if (globalDefaultProperties.getAdviceFilterClass().contains(declaringClass.getName())) {
            return false;
        }
        // 检查注解是否存在
        if (methodParameter.getDeclaringClass().isAnnotationPresent(IgnoreResponseAdvice.class)) {
            return false;
        }
        if (methodParameter.getMethod().isAnnotationPresent(IgnoreResponseAdvice.class)) {
            return false;
        }
        return true;
    }

}
