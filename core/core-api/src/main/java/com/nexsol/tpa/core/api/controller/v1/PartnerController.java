package com.nexsol.tpa.core.api.controller.v1;

import com.nexsol.tpa.core.api.controller.v1.response.PartnerResponse;
import com.nexsol.tpa.core.domain.partner.PartnerService;
import com.nexsol.tpa.core.enums.ServiceType;
import com.nexsol.tpa.core.support.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/admin/travel")
public class PartnerController {

    private final PartnerService partnerService;

    @GetMapping("/partner")
    public ApiResponse<List<PartnerResponse>> getPartners() {
        List<PartnerResponse> partners = partnerService.getActivePartners(ServiceType.TRAVEL)
            .stream()
            .map(PartnerResponse::of)
            .toList();

        return ApiResponse.success(partners);
    }

}