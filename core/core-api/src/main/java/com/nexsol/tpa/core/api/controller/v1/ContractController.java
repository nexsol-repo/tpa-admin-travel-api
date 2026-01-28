package com.nexsol.tpa.core.api.controller.v1;

import com.nexsol.tpa.core.api.controller.v1.request.ContractCreateRequest;
import com.nexsol.tpa.core.api.controller.v1.request.ContractSearchRequest;
import com.nexsol.tpa.core.api.controller.v1.request.ContractUpdateRequest;
import com.nexsol.tpa.core.api.controller.v1.response.ContractDetailResponse;
import com.nexsol.tpa.core.api.controller.v1.response.ContractResponse;
import com.nexsol.tpa.core.domain.contract.ContractService;
import com.nexsol.tpa.core.domain.contract.InsuranceContract;
import com.nexsol.tpa.core.support.SortPage;
import com.nexsol.tpa.core.support.response.ApiResponse;
import com.nexsol.tpa.core.support.response.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/admin/travel")
public class ContractController {

    private final ContractService contractService;

    @GetMapping("/contract")
    public ApiResponse<PageResponse<ContractResponse>> getContracts(@ModelAttribute ContractSearchRequest request,
            @ModelAttribute SortPage sortPage) {
        // 1. 비즈니스 로직 호출 (Service -> Domain Page)
        var domainPage = contractService.searchContract(request.toCriteria(), sortPage);

        // 2. Domain -> Response DTO 변환
        List<ContractResponse> content = domainPage.getContent().stream().map(ContractResponse::of).toList();

        // 3. PageResult 재생성 (DTO 타입으로)
        return ApiResponse.success(PageResponse.of(domainPage, content));
    }

    @GetMapping("/contract/{contractId}")
    public ApiResponse<ContractDetailResponse> getContractDetail(@PathVariable Long contractId) {
        InsuranceContract contract = contractService.getContractDetail(contractId);

        return ApiResponse.success(ContractDetailResponse.of(contract));
    }

    @PostMapping("/contract")
    public ApiResponse<Long> createContract(@RequestBody ContractCreateRequest request) {
        Long contractId = contractService.createContract(request.toCommand());

        return ApiResponse.success(contractId);
    }

    @PutMapping("/contract/{contractId}")
    public ApiResponse<Long> updateContract(@PathVariable Long contractId, @RequestBody ContractUpdateRequest request) {

        Long updatedContractId = contractService.updateContract(request.toCommand(contractId));

        return ApiResponse.success(updatedContractId);
    }

}