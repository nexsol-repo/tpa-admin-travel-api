package com.nexsol.tpa.core.domain.admin;

import com.nexsol.tpa.core.enums.ServiceType;

/**
 * 메모 등록 도구 클래스 (Implement Layer) 메모 서비스 연동의 상세 구현을 담당
 */
public interface MemoRegistrar {

	void register(Long contractId, String content, ServiceType serviceType);

}
