package com.nexsol.tpa.client.memo;

import com.nexsol.tpa.client.memo.dto.MemoRequest;
import com.nexsol.tpa.core.domain.MemoRegistrar;
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
    public void register(Long contractId, String content) {
        if (content == null || content.isBlank()) {
            return;
        }

        MemoRequest request = new MemoRequest();
        request.setContent(content);
        memoClient.createMemo(contractId, request);
    }

}
