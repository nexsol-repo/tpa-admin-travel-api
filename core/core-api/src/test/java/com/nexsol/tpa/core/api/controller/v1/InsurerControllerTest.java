package com.nexsol.tpa.core.api.controller.v1;

import com.nexsol.tpa.core.domain.insurer.Insurer;
import com.nexsol.tpa.core.domain.insurer.InsurerService;
import com.nexsol.tpa.test.api.RestDocsTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class InsurerControllerTest extends RestDocsTest {

	private InsurerService insurerService;

	@BeforeEach
	public void setUp(RestDocumentationContextProvider restDocumentation) {
		super.setUp(restDocumentation);
		insurerService = mock(InsurerService.class);
		mockMvc = mockController(new InsurerController(insurerService));
	}

	@Test
	@DisplayName("보험사 목록 조회 API 문서화")
	void getInsurers() throws Exception {
		// Given
		List<Insurer> mockInsurers = List.of(new Insurer(1L, "MERITZ", "메리츠화재"), new Insurer(2L, "SAMSUNG", "삼성화재"),
				new Insurer(3L, "KB", "KB손해보험"));

		given(insurerService.getActiveInsurers()).willReturn(mockInsurers);

		// When & Then
		mockMvc.perform(get("/v1/admin/travel/insurer").contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(document("insurer-list",
					responseFields(fieldWithPath("result").description("API 실행 결과 (SUCCESS/ERROR)"),
							fieldWithPath("error").description("에러 정보 (성공 시 null)").optional(),
							fieldWithPath("data").description("보험사 목록"),
							fieldWithPath("data[].id").description("보험사 ID"),
							fieldWithPath("data[].value").description("보험사 코드"),
							fieldWithPath("data[].label").description("보험사명"))));
	}

}
