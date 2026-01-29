package com.nexsol.tpa.client.aligo.config;

import com.nexsol.tpa.client.aligo.AligoClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

/**
 * Aligo 클라이언트 자동 설정 FeignClient 스캔만 담당
 */
@Configuration
@EnableFeignClients(basePackageClasses = AligoClient.class)
public class AligoClientAutoConfiguration {

}
