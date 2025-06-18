package org.yechan.config.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
public class CommonHttpMessageConverter extends AbstractHttpMessageConverter<Object> {
    private final ObjectMapper objectMapper;

    @Override
    protected boolean supports(final Class<?> clazz) {
        return ApiResponse.class.isAssignableFrom(clazz) || ErrorResponse.class.isAssignableFrom(clazz);
    }

    /**
     *
     */
    @Override
    protected ApiResponse<Object> readInternal(final Class<?> clazz,
                                               final HttpInputMessage inputMessage)
            throws HttpMessageNotReadableException {
        throw new UnsupportedOperationException("this converter does not support reading");
    }

    @Override
    protected void writeInternal(final Object objectApiResponse, final HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException {
        String responseMessage = objectMapper.writeValueAsString(objectApiResponse);
        StreamUtils.copy(responseMessage.getBytes(StandardCharsets.UTF_8), outputMessage.getBody());
    }

    @Override
    protected void addDefaultHeaders(HttpHeaders headers, Object objectApiResponse, MediaType contentType)
    {
        try {
            super.addDefaultHeaders(headers, objectApiResponse, contentType);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
