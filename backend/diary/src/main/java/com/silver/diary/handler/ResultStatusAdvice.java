package com.silver.diary.handler;

import com.silver.diary.common.Result;
import org.jspecify.annotations.NonNull;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/** 兼容原有直接返回 Result.error 的控制器，避免失败响应仍为 HTTP 200。 */
@RestControllerAdvice
public class ResultStatusAdvice implements ResponseBodyAdvice<Object> {
    @Override
    public boolean supports(MethodParameter returnType, @NonNull Class<? extends HttpMessageConverter<?>> converterType) {
        // ResponseEntity 的状态由异常处理器或控制器自己负责。
        return Result.class.isAssignableFrom(returnType.getParameterType());
    }

    @Override
    public Object beforeBodyWrite(Object body, @NonNull MethodParameter returnType, @NonNull MediaType mediaType,
                                  @NonNull Class<? extends HttpMessageConverter<?>> converterType,
                                  @NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response) {
        if (body instanceof Result<?> result && !Integer.valueOf(200).equals(result.getCode())) {
            response.setStatusCode(HttpStatus.BAD_REQUEST);
        }
        return body;
    }
}
