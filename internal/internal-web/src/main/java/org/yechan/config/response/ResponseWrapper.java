package org.yechan.config.response;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import org.yechan.error.ErrorCode;

@RestControllerAdvice
public class ResponseWrapper implements ResponseBodyAdvice<Object> {
    @Override
    public boolean supports(final MethodParameter returnType,
                            final Class<? extends HttpMessageConverter<?>> converterType) {
        return MappingJackson2HttpMessageConverter.class.isAssignableFrom(converterType);
    }

    @Override
    public Object beforeBodyWrite(final Object body, final MethodParameter returnType,
                                  final MediaType selectedContentType,
                                  final Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  final ServerHttpRequest request, final ServerHttpResponse response) {
        if (body instanceof ErrorCode errorCode) {
            return new ErrorResponse(errorCode.getCode(), errorCode.getMessage());
        }

        if (body instanceof ApiResponse || body instanceof ErrorResponse) {
            return body;
        }

        return new ApiResponse<>("SUCCESS",body);
    }
}
