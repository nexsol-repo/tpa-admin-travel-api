package com.nexsol.tpa.core.api.controller.v1;

import com.nexsol.tpa.core.domain.city.CityInfo;
import com.nexsol.tpa.core.domain.city.CitySearchService;
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
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CityControllerTest extends RestDocsTest {

	private CitySearchService citySearchService;

	@BeforeEach
	public void setUp(RestDocumentationContextProvider restDocumentation) {
		super.setUp(restDocumentation);
		citySearchService = mock(CitySearchService.class);
		mockMvc = mockController(new CityController(citySearchService));
	}

	@Test
	@DisplayName("도시/국가 검색 API 문서화")
	void searchCities() throws Exception {
		// Given
		List<CityInfo> mockCities = List.of(
				new CityInfo("JP001", "일본", "JAPAN", "도쿄", "TOKYO", "1", null),
				new CityInfo("JP002", "일본", "JAPAN", "오사카", "OSAKA", "1", null));

		given(citySearchService.search("일본", "1")).willReturn(mockCities);

		// When & Then
		mockMvc
			.perform(get("/v1/admin/travel/cities").param("keyword", "일본")
				.param("type", "1")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(document("city-search",
					queryParameters(parameterWithName("keyword").description("검색 키워드 (도시명 또는 국가명)"),
							parameterWithName("type").description("검색 유형 (1: 국가, 2: 도시)").optional()),
					responseFields(fieldWithPath("result").description("API 실행 결과 (SUCCESS/ERROR)"),
							fieldWithPath("error").description("에러 정보 (성공 시 null)").optional(),
							fieldWithPath("data").description("도시/국가 목록"),
							fieldWithPath("data[].cityNatlCd").description("도시/국가 코드"),
							fieldWithPath("data[].korNatlNm").description("국가명 (한글)"),
							fieldWithPath("data[].engNatlNm").description("국가명 (영문)"),
							fieldWithPath("data[].korCityNm").description("도시명 (한글)"),
							fieldWithPath("data[].engCityNm").description("도시명 (영문)"),
							fieldWithPath("data[].trvRskGrdeCd").description("여행 위험등급 코드"))));
	}

}