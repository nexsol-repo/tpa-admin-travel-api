package com.nexsol.tpa.core.support.excel;

import com.nexsol.tpa.core.domain.applicant.InsuredPerson;
import com.nexsol.tpa.core.domain.contract.InsuranceContract;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class BasicContractExcelGenerator implements ContractExcelGenerator {

	private static final String[] HEADERS = { "가입상태", "가입 제휴사", "가입 채널", "피보험자명", "주민등록번호", "전화번호", "신청일", "보험기간 시작일",
			"보험기간 종료일", "보험사", "보험료(원)", "증권번호" };

	@Override
	public void generate(HttpServletResponse response, List<InsuranceContract> contracts) throws IOException {
		try (SXSSFWorkbook workbook = new SXSSFWorkbook(100)) {
			Sheet sheet = workbook.createSheet("계약내역");
			createHeaderRow(sheet);

			// 데이터 생성 (동반자 분리 로직)
			createDataRows(sheet, contracts);

			// 파일 다운로드 설정
			String fileName = URLEncoder.encode("여행자보험_계약내역_" + java.time.LocalDate.now() + ".xlsx",
					StandardCharsets.UTF_8);
			response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

			workbook.write(response.getOutputStream());
		}
	}

	private void createHeaderRow(Sheet sheet) {
		Row row = sheet.createRow(0);
		for (int i = 0; i < HEADERS.length; i++) {
			Cell cell = row.createCell(i);
			cell.setCellValue(HEADERS[i]);
		}
	}

	private void createDataRows(Sheet sheet, List<InsuranceContract> contracts) {
		int rowIndex = 1;
		DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		DateTimeFormatter dateTimeFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

		for (InsuranceContract contract : contracts) {
			// [중요] 피보험자(동반자 포함) 수만큼 행 반복 생성
			for (InsuredPerson person : contract.insuredPeople()) {
				Row row = sheet.createRow(rowIndex++);
				int cellIdx = 0;

				row.createCell(cellIdx++).setCellValue(contract.status().getDescription());
				row.createCell(cellIdx++).setCellValue(contract.metaInfo().origin().partnerName());
				row.createCell(cellIdx++).setCellValue(contract.metaInfo().origin().channelName());

				// 피보험자별 정보
				row.createCell(cellIdx++).setCellValue(person.name());
				row.createCell(cellIdx++).setCellValue(person.residentNumber());

				// 대표 계약자 연락처
				row.createCell(cellIdx++).setCellValue(contract.applicant().phoneNumber());

				row.createCell(cellIdx++).setCellValue(contract.metaInfo().applicationDate().format(dateFmt));
				row.createCell(cellIdx++).setCellValue(contract.metaInfo().period().startDate().format(dateTimeFmt));
				row.createCell(cellIdx++).setCellValue(contract.metaInfo().period().endDate().format(dateTimeFmt));

				row.createCell(cellIdx++).setCellValue(contract.metaInfo().origin().insurerName());

				// 피보험자 개별 보험료
				long premium = (person.individualPremium() != null) ? person.individualPremium().longValue() : 0;
				row.createCell(cellIdx++).setCellValue(premium);

				// 피보험자 개별 증권번호 (없으면 대표 증권번호)
				String policyNo = (person.individualPolicyNumber() != null) ? person.individualPolicyNumber()
						: contract.metaInfo().policyNumber();
				row.createCell(cellIdx++).setCellValue(policyNo);
			}
		}
	}

	@Override
	public boolean supports(Long insurerId) {
		return true;
	}

}
