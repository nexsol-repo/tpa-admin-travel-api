package com.nexsol.tpa.client.memo.config;

import com.nexsol.tpa.client.memo.MemoClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

/**
 * Memo 모듈 Feign 설정 client-memo 모듈이 로드되면 자동으로 FeignClient 활성화
 */
@Configuration
@EnableFeignClients(basePackageClasses = MemoClient.class)
public class MemoFeignConfig {

}
