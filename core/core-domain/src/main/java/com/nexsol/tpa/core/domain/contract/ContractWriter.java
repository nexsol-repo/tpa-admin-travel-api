package com.nexsol.tpa.core.domain.contract;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ContractWriter {

	private final ContractCommandRepository contractCommandRepository;

	public Long write(InsuranceContract contract) {
		return contractCommandRepository.save(contract);
	}

}