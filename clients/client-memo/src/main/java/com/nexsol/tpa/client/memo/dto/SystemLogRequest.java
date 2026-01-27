package com.nexsol.tpa.client.memo.dto;

import com.nexsol.tpa.core.enums.ServiceType;
import lombok.Builder;
import lombok.Data;

@Builder
public record SystemLogRequest (String logMessage,ServiceType serviceType){



}
