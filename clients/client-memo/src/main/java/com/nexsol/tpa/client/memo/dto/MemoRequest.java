package com.nexsol.tpa.client.memo.dto;

import com.nexsol.tpa.core.enums.ServiceType;
import lombok.Data;

@Data
public class MemoRequest {

    private String content;

    private ServiceType serviceType;

}
