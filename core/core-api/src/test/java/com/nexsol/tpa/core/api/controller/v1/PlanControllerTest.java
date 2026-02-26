package com.nexsol.tpa.core.api.controller.v1;

import com.nexsol.tpa.core.domain.plan.Plan;
import com.nexsol.tpa.core.domain.plan.PlanService;
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
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PlanControllerTest extends RestDocsTest {

	private PlanService planService;

	@BeforeEach
	public void setUp(RestDocumentationContextProvider restDocumentation) {
		super.setUp(restDocumentation);
		planService = mock(PlanService.class);
		mockMvc = mockController(new PlanController(planService));
	}

	@Test
	@DisplayName("플랜 목록 조회 API 문서화")
	void getPlans() throws Exception {
		// Given
		List<Plan> mockPlans = List.of(new Plan(1L, "PLAN001", "알뜰 플랜", "해외여행자보험", "알뜰플랜(15~69세)", 1L),
				new Plan(2L, "PLAN002", "표준 플랜", "해외여행자보험", "표준플랜(15~69세)", 1L),
				new Plan(3L, "PLAN003", "프리미엄 플랜", "해외여행자보험", "프리미엄플랜(15~69세)", 1L));

		given(planService.getActivePlans()).willReturn(mockPlans);

		// When & Then
		mockMvc.perform(get("/v1/admin/travel/plan").contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(document("plan-list",
					queryParameters(parameterWithName("insurerId").description("보험사 ID (필터링용, 선택)").optional()),
					responseFields(fieldWithPath("result").description("API 실행 결과 (SUCCESS/ERROR)"),
							fieldWithPath("error").description("에러 정보 (성공 시 null)").optional(),
							fieldWithPath("data").description("플랜 목록"), fieldWithPath("data[].id").description("플랜 ID"),
							fieldWithPath("data[].value").description("플랜 코드"),
							fieldWithPath("data[].label").description("플랜명"),
							fieldWithPath("data[].fullName").description("플랜 전체명"),
							fieldWithPath("data[].insurerId").description("보험사 ID"))));
	}

	@Test
	@DisplayName("보험사별 플랜 목록 조회 API 문서화")
	void getPlansByInsurerId() throws Exception {
		// Given
		Long insurerId = 1L;
		List<Plan> mockPlans = List.of(new Plan(1L, "PLAN001", "알뜰 플랜", "해외여행자보험", "알뜰플랜(15~69세)", insurerId),
				new Plan(2L, "PLAN002", "표준 플랜", "해외여행자보험", "표준플랜(15~69세)", insurerId));

		given(planService.getActivePlansByInsurerId(insurerId)).willReturn(mockPlans);

		// When & Then
		mockMvc
			.perform(get("/v1/admin/travel/plan").param("insurerId", insurerId.toString())
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(document("plan-list-by-insurer",
					queryParameters(parameterWithName("insurerId").description("보험사 ID (필터링용)")),
					responseFields(fieldWithPath("result").description("API 실행 결과 (SUCCESS/ERROR)"),
							fieldWithPath("error").description("에러 정보 (성공 시 null)").optional(),
							fieldWithPath("data").description("해당 보험사의 플랜 목록"),
							fieldWithPath("data[].id").description("플랜 ID"),
							fieldWithPath("data[].value").description("플랜 코드"),
							fieldWithPath("data[].label").description("플랜명"),
							fieldWithPath("data[].fullName").description("플랜 전체명"),
							fieldWithPath("data[].insurerId").description("보험사 ID"))));
	}

}
