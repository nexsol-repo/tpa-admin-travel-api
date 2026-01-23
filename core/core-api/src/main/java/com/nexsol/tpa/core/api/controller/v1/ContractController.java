package com.nexsol.tpa.core.api.controller.v1;

import com.nexsol.tpa.core.api.controller.v1.request.ContractSearchRequest;
import com.nexsol.tpa.core.api.controller.v1.response.ContractResponse;
import com.nexsol.tpa.core.domain.ContractService;
import com.nexsol.tpa.core.support.PageResult;
import com.nexsol.tpa.core.support.SortPage;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/admin/travel")
public class ContractController {

    private final ContractService contractService;

    @GetMapping("/contract")
    public PageResult<ContractResponse> getContracts(@ModelAttribute ContractSearchRequest request,
            @ModelAttribute SortPage sortPage) {
        // 1. 비즈니스 로직 호출 (Service -> Domain Page)
        var domainPage = contractService.searchContract(request.toCriteria(), sortPage);

        // 2. Domain -> Response DTO 변환
        List<ContractResponse> content = domainPage.getContent().stream().map(ContractResponse::from).toList();

        // 3. PageResult 재생성 (DTO 타입으로)
        return new PageResult<>(content, domainPage.getTotalElements(), domainPage.getTotalPages(),
                domainPage.getCurrentPage(), domainPage.hasNext());
    }

}