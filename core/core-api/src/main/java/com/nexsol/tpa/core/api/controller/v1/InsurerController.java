    package com.nexsol.tpa.core.api.controller.v1;

import com.nexsol.tpa.core.api.controller.v1.response.InsurerResponse;
import com.nexsol.tpa.core.domain.insurer.InsurerService;
import com.nexsol.tpa.core.support.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/admin/travel")
public class InsurerController {

    private final InsurerService insurerService;

    @GetMapping("/insurer")
    public ApiResponse<List<InsurerResponse>> getInsurers() {
        List<InsurerResponse> insurers = insurerService.getActiveInsurers()
                .stream()
                .map(InsurerResponse::of)
                .toList();

        return ApiResponse.success(insurers);
    }

}