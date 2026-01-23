package com.nexsol.tpa.core.api.controller.v1;

import com.nexsol.tpa.core.domain.*;
import com.nexsol.tpa.core.enums.ContractStatus;
import com.nexsol.tpa.core.support.PageResult;
import com.nexsol.tpa.core.support.SortPage;
import com.nexsol.tpa.test.api.RestDocsTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import tools.jackson.databind.json.JsonMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ContractControllerTest extends RestDocsTest {

    private ContractService contractService;

    private JsonMapper jsonMapper = JsonMapper.builder().findAndAddModules().build();

    @BeforeEach
    public void setUp(RestDocumentationContextProvider restDocumentation) {
        contractService = mock(ContractService.class);
        HandlerMethodArgumentResolver loginAdminResolver = new HandlerMethodArgumentResolver() {
            @Override
            public boolean supportsParameter(MethodParameter parameter) {
                return parameter.hasParameterAnnotation(LoginAdmin.class);
            }

            @Override
            public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                    NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
                // 테스트용 관리자 정보 (ID: 1L) 반환
                return new AdminUser(1L, "MASTER");
            }
        };

        mockMvc = MockMvcBuilders.standaloneSetup(new ContractController(contractService))
            .setCustomArgumentResolvers(loginAdminResolver)
            .apply(documentationConfiguration(restDocumentation))
            .build();

    }

    @Test
    @DisplayName("여행자 보험 계약 목록 조회 API 문서화")
    void getContracts() throws Exception {
        // Given: Mock Domain Object 생성
        InsuranceContract mockContract = InsuranceContract.builder()
            .contractId(1L)
            .status(ContractStatus.COMPLETED) // 가입완료
            // 1. 계약 메타 정보 (증권번호, 제휴사, 기간)
            .metaInfo(ContractMeta.builder()
                .policyNumber("15540-97222")
                .partnerName("TPA KOREA")
                .channelName("TPA WEB")
                .applicationDate(LocalDateTime.of(2024, 2, 1, 0, 0))
                .period(new InsurancePeriod(LocalDateTime.of(2024, 3, 15, 17, 0), LocalDateTime.of(2025, 3, 14, 19, 0)))
                .build())
            // 2. 상품 및 플랜 정보
            .productPlan(ProductPlan.builder().productName("해외여행보험").planName("알뜰 플랜").travelCountry("일본").build())
            // 3. 신청자(대표 계약자) 정보
            .applicant(Applicant.builder()
                .name("홍길동")
                .residentNumber("910504-1******")
                .phoneNumber("010-0000-0000")
                .email("contractor@abc.com")
                .build())
            // 4. 결제 정보
            .paymentInfo(PaymentInfo.builder()
                .method("카드 결제")
                .totalAmount(BigDecimal.valueOf(17000))
                .paidAt(LocalDateTime.of(2025, 12, 24, 15, 1, 42))
                .build())
            // 5. 피보험자(동반자) 목록
            .insuredPeople(List.of(InsuredPerson.builder()

                .name("홍길동")
                .englishName("Hong Gildong")
                .residentNumber("910504-1******")
                .passportNumber("M12345678")
                .gender("남성")
                .individualPremium(BigDecimal.valueOf(8000))
                .iIndividualPolicyNumber("15540-97222")
                .build(),
                    InsuredPerson.builder()

                        .name("김영희")
                        .englishName("Kim Younghee")
                        .residentNumber("910504-2111111")
                        .passportNumber("M87654321")
                        .gender("여성")
                        .individualPremium(BigDecimal.valueOf(9000))
                        .iIndividualPolicyNumber("15540-97222")
                        .build()))
            .build();
        PageResult<InsuranceContract> mockPage = PageResult.of(List.of(mockContract), 100, 10, 0);

        given(contractService.searchContract(any(ContractSearchCriteria.class), any(SortPage.class)))
            .willReturn(mockPage);

        // When & Then
        mockMvc
            .perform(get("/v1/contracts").param("page", "0")
                .param("size", "10")
                .param("startDate", "2025-01-01")
                .param("endDate", "2025-01-31")
                .param("partnerCode", "TPA")
                .param("status", "COMPLETED")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(document("contract-list",
                    queryParameters(parameterWithName("page").description("페이지 번호 (0부터 시작)").optional(),
                            parameterWithName("size").description("페이지 크기").optional(),
                            parameterWithName("startDate").description("조회 시작일 (yyyy-MM-dd)").optional(),
                            parameterWithName("endDate").description("조회 종료일 (yyyy-MM-dd)").optional(),
                            parameterWithName("partnerCode").description("제휴사 코드").optional(),
                            parameterWithName("status").description("계약 상태 (COMPLETED, CANCELED 등)").optional(),
                            parameterWithName("keywordType").description("검색어 타입 (NAME, PHONE 등)").optional(),
                            parameterWithName("keyword").description("검색어").optional()),
                    responseFields(fieldWithPath("content").type(JsonFieldType.ARRAY).description("계약 내역 리스트"),
                            fieldWithPath("content[].contractId").description("계약 ID"),
                            fieldWithPath("content[].contractStatus").description("계약 상태 (한글/영문)"),
                            fieldWithPath("content[].contractStatusCode").description("계약 상태 코드"),
                            fieldWithPath("content[].policyNumber").description("증권 번호"),
                            fieldWithPath("content[].partnerName").description("제휴사 명"),
                            fieldWithPath("content[].channelName").description("가입 채널"),
                            fieldWithPath("content[].applicantName").description("신청자(대표자) 이름"),
                            fieldWithPath("content[].applicantPhone").description("신청자 연락처"),
                            fieldWithPath("content[].insuredCount").description("총 가입 인원수"),
                            fieldWithPath("content[].totalPremium").description("총 보험료"),
                            fieldWithPath("content[].applicationDate").description("신청 일시"),
                            fieldWithPath("content[].insuranceStartDate").description("보험 시작 일시"),
                            fieldWithPath("content[].insuranceEndDate").description("보험 종료 일시"),
                            fieldWithPath("totalElements").description("총 데이터 수"),
                            fieldWithPath("totalPages").description("총 페이지 수"),
                            fieldWithPath("currentPage").description("현재 페이지 번호"),
                            fieldWithPath("hasNext").description("다음 페이지 존재 여부"))));
    }

}
