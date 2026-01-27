package com.nexsol.tpa.client.memo;

import com.nexsol.tpa.client.memo.dto.MemoRequest;
import com.nexsol.tpa.core.domain.MemoRegistrar;
import com.nexsol.tpa.core.enums.ServiceType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 메모 등록 도구 구현체 (client 모듈) MemoClient를 사용하여 외부 메모 서비스에 등록
 */
@Component
@RequiredArgsConstructor
public class MemoRegistrarImpl implements MemoRegistrar {

    private final MemoClient memoClient;

    @Override
    public void register(Long contractId, String content, ServiceType serviceType) {
        if (content == null || content.isBlank()) {
            return;
        }

        MemoRequest request = MemoRequest.builder().content(content).serviceType(serviceType).build();
        memoClient.createMemo(contractId, request);
    }

}
