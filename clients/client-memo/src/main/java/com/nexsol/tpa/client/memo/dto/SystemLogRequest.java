package com.nexsol.tpa.client.memo.dto;

import com.nexsol.tpa.core.enums.ServiceType;
import lombok.Builder;

@Builder
public record SystemLogRequest(String content, ServiceType serviceType) {

}
