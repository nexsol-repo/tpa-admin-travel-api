package com.nexsol.tpa.core.domain.admin;

import com.nexsol.tpa.core.enums.ServiceType;

/**
 * 시스템 로그 등록 도구 클래스 (Implement Layer) 시스템 로그 서비스 연동의 상세 구현을 담당
 */
public interface SystemLogRegistrar {

    void register(Long contractId, String logMessage, ServiceType serviceType);

}
