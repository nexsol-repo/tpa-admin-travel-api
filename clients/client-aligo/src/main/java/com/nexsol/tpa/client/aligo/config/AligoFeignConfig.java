package com.nexsol.tpa.client.aligo.config;

import feign.Logger;

import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ResolvableType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.MediaType;

import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * AligoClient 전용 Feign 설정 (전역 빈이 아님)
 * AligoClient의 configuration 속성에서 참조됨
 */
public class AligoFeignConfig {

    // @Bean
    // public ClientHttpMessageConvertersCustomizer aligoHtmlResponseCustomizer() {
    // return builder -> {
    // // 1. Jackson 3 컨버터 생성
    // JacksonJsonHttpMessageConverter jacksonConverter = new
    // JacksonJsonHttpMessageConverter();
    //
    // // 2. 알리고의 "text/html" 응답도 JSON으로 처리하도록 설정
    // jacksonConverter.setSupportedMediaTypes(List.of(MediaType.APPLICATION_JSON,
    // MediaType.TEXT_HTML));
    //
    // // 3. 문서에 나온대로 builder.withJsonConverter() 사용
    // builder.withJsonConverter(jacksonConverter);
    // };
    // }

    @Bean
    public Decoder feignDecoder() {
        return (response, type) -> {
            if (response.body() == null)
                return null;

            // 1. Jackson 컨버터 생성
            JacksonJsonHttpMessageConverter jacksonConverter = new JacksonJsonHttpMessageConverter();
            // 알리고의 "text/html" 응답도 JSON으로 처리하도록 설정
            jacksonConverter.setSupportedMediaTypes(List.of(MediaType.APPLICATION_JSON, MediaType.TEXT_HTML));

            // 2. Feign Response를 Spring HttpInputMessage로 변환
            HttpInputMessage inputMessage = new HttpInputMessage() {
                @Override
                public InputStream getBody() throws IOException {
                    return response.body().asInputStream();
                }

                @Override
                public HttpHeaders getHeaders() {
                    HttpHeaders headers = new HttpHeaders();
                    if (response.headers() != null) {
                        response.headers().forEach((k, v) -> headers.put(k, new ArrayList<>(v)));
                    }
                    return headers;
                }
            };

            // 3. [핵심 수정] Feign의 Type을 Spring ResolvableType으로 변환하여 전달
            // read(ResolvableType type, HttpInputMessage inputMessage, Map<String,
            // Object> hints)
            return jacksonConverter.read(ResolvableType.forType(type), inputMessage, null);
        };
    }

    @Bean
    public Encoder feignFormEncoder() {
        return new SpringFormEncoder();
    }

    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

}