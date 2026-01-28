package com.nexsol.tpa.client.memo;

import com.nexsol.tpa.client.memo.dto.SystemLogRequest;
import com.nexsol.tpa.core.domain.admin.SystemLogRegistrar;
import com.nexsol.tpa.core.enums.ServiceType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 시스템 로그 등록 도구 구현체 (client 모듈) MemoClient를 사용하여 외부 메모 서비스에 시스템 로그 등록
 */
@Component
@RequiredArgsConstructor
public class SystemLogRegistrarImpl implements SystemLogRegistrar {

    private final MemoClient memoClient;

    @Override
    public void register(Long contractId, String logMessage, ServiceType serviceType) {
        if (logMessage == null || logMessage.isBlank()) {
            return;
        }

        SystemLogRequest request = SystemLogRequest.builder().content(logMessage).serviceType(serviceType).build();
        memoClient.createSystemLog(contractId, request);
    }

}
