package com.nexsol.tpa.core.api.controller.v1;

import com.nexsol.tpa.core.api.controller.v1.request.ContractCreateRequest;
import com.nexsol.tpa.core.api.controller.v1.request.ContractSearchRequest;
import com.nexsol.tpa.core.api.controller.v1.request.ContractUpdateRequest;
import com.nexsol.tpa.core.api.controller.v1.response.ContractDetailResponse;
import com.nexsol.tpa.core.api.controller.v1.response.ContractResponse;
import com.nexsol.tpa.core.domain.admin.AdminUser;
import com.nexsol.tpa.core.domain.admin.LoginAdmin;
import com.nexsol.tpa.core.domain.contract.ContractService;
import com.nexsol.tpa.core.domain.contract.InsuranceContract;
import com.nexsol.tpa.core.support.SortPage;
import com.nexsol.tpa.core.support.excel.ContractExcelGenerator;
import com.nexsol.tpa.core.support.excel.ExcelGeneratorFactory;
import com.nexsol.tpa.core.support.response.ApiResponse;
import com.nexsol.tpa.core.support.response.PageResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/admin/travel")
public class ContractController {

	private final ContractService contractService;

	private final ExcelGeneratorFactory excelGeneratorFactory;

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

	@GetMapping("/contract/excel")
	public void downloadExcel(ContractSearchRequest request, @RequestParam(required = false) Long targetInsurerId,
			HttpServletResponse response) throws IOException {

		var criteria = request.toCriteria();
		List<InsuranceContract> contracts = contractService.downloadContracts(criteria);
		Long insurerId = (targetInsurerId != null) ? targetInsurerId : 0L;
		ContractExcelGenerator generator = excelGeneratorFactory.getGenerator(insurerId);

		generator.generate(response, contracts);

	}

	@PostMapping("/contract")
	public ApiResponse<Long> createContract(@LoginAdmin AdminUser adminUser,
			@RequestBody ContractCreateRequest request) {
		Long contractId = contractService.createContract(request.toCommand(adminUser.userId()));

		return ApiResponse.success(contractId);
	}

	@PutMapping("/contract/{contractId}")
	public ApiResponse<Long> updateContract(@LoginAdmin AdminUser adminUser, @PathVariable Long contractId,
			@RequestBody ContractUpdateRequest request) {

		Long updatedContractId = contractService.updateContract(request.toCommand(contractId, adminUser.userId()));

		return ApiResponse.success(updatedContractId);
	}

}