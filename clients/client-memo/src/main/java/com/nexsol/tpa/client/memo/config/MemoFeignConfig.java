package com.nexsol.tpa.client.memo.config;

import com.nexsol.tpa.client.memo.MemoClient;
import feign.RequestInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
@EnableFeignClients(basePackageClasses = MemoClient.class)
public class MemoFeignConfig {

    /**
     * Feign 요청 시 현재 스레드의 HttpServletRequest 헤더(X-User-Id, X-Role)를 다운스트림 서비스(Memo)로
     * 전파(Propagation)하는 인터셉터
     */
    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();

                // 1. User ID 헤더 전파
                String userId = request.getHeader("X-User-Id");
                if (userId != null) {
                    requestTemplate.header("X-User-Id", userId);
                }

                // 2. Role 헤더 전파
                String role = request.getHeader("X-Role");
                if (role != null) {
                    requestTemplate.header("X-Role", role);
                }
            }
        };
    }

}