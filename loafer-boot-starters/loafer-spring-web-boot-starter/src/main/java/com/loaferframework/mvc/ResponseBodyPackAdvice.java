package com.loaferframework.mvc;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Role;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.Objects;
import java.util.Optional;

/**
 * Rest控制器返回值如果没有用${@link ResponseEntity}包裹，则主动添加
 * <p>
 * 创建时间: 2020年10月28号
 * 联系方式: houchao.kang@hydosky.com
 * </p>
 *
 * @author kanghouchao
 * @since 2.0.0
 */
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
@ControllerAdvice
public class ResponseBodyPackAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(@NonNull MethodParameter returnType, @NonNull Class<? extends HttpMessageConverter<?>> converterType) {
        return !ResponseEntity.class.equals(returnType.getParameterType());
    }

    @Override
    public Object beforeBodyWrite(Object body, @NonNull MethodParameter returnType, @NonNull MediaType selectedContentType,
                                  @NonNull Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  @NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response) {
        if (Objects.isNull(body)) {
            return ResponseEntity.ok(Optional.empty());
        }
        return ResponseEntity.ok(body);
    }
}
