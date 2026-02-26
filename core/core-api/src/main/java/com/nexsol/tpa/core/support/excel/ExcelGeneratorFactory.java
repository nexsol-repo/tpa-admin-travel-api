package com.nexsol.tpa.core.support.excel;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ExcelGeneratorFactory {

	// 모든 생성기들을 주입받음 (Basic, Meritz, Samsung 등...)
	private final List<ContractExcelGenerator> generators;

	private final BasicContractExcelGenerator basicGenerator; // 기본(전체) 양식

	public ContractExcelGenerator getGenerator(Long insurerId) {
		// 1. 0L이거나 null이면 바로 기본 생성기 반환 (빠른 종료)
		if (insurerId == null || insurerId == 0L) {
			return basicGenerator;
		}

		// 2. 등록된 생성기들 중 해당 ID를 지원하는 녀석이 있는지 찾음
		// (현재는 구현체가 Basic 하나뿐이라 이 루프는 돌지만 basicGenerator로 빠짐)
		// 3.지원하는게 없으면 기본 양식 반환
		return generators.stream().filter(g -> g.supports(insurerId)).findFirst().orElse(basicGenerator);
	}

}