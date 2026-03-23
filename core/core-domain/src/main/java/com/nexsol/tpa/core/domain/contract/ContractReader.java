package com.nexsol.tpa.core.domain.contract;

import com.nexsol.tpa.core.error.CoreErrorType;
import com.nexsol.tpa.core.error.CoreException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ContractReader {

	private final ContractQueryRepository contractQueryRepository;

	public InsuranceContract read(Long contractId) {
		return contractQueryRepository.findById(contractId)
			.orElseThrow(() -> new CoreException(CoreErrorType.INSURANCE_NOT_FOUND_DATA));
	}

}
