package com.nexsol.tpa.core.support.excel;

import com.nexsol.tpa.core.domain.contract.InsuranceContract;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

public interface ContractExcelGenerator {

	/**
	 * 엑셀 파일을 생성하여 Response에 씁니다.
	 * @param response HTTP 응답 객체
	 * @param contracts 조회된 계약 목록
	 */
	void generate(HttpServletResponse response, List<InsuranceContract> contracts) throws IOException;

	/**
	 * 이 생성기가 지원하는지 여부를 반환 (선택적 확장 포인트) 예: return insurerId == 1L;
	 */
	boolean supports(Long insurerId);

}