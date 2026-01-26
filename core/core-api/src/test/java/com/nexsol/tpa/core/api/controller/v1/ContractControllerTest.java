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
import static org.springframework.restdocs.request.RequestDocumentation.*;
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
                .origin(new SubscriptionOrigin("TPA KOREA", "TPA KOREA", "메리츠"))
                .applicationDate(LocalDateTime.of(2024, 2, 1, 0, 0)) // 2024.02.01
                .period(new InsurancePeriod(LocalDateTime.of(2024, 3, 15, 17, 0), // 2024.03.15
                        // 17:00
                        LocalDateTime.of(2025, 3, 14, 19, 0) // 2025.03.14 19:00
                ))
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

        List<InsuranceContract> content = List.of(mockContract);
        PageResult<InsuranceContract> pageResult = new PageResult<>(content, 1L, 1, 0, false);

        given(contractService.searchContract(any(ContractSearchCriteria.class), any(SortPage.class)))
            .willReturn(pageResult);

        // When & Then
        mockMvc
            .perform(get("/v1/admin/travel/contract").param("page", "0")
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
                    responseFields(fieldWithPath("result").description("API 실행 결과 (SUCCESS/ERROR)"),
                            fieldWithPath("error").description("에러 정보 (성공 시 null)").optional(),

                            // 2. data 객체 내부로 경로 변경 (prefix: "data.")
                            fieldWithPath("data").description("응답 데이터"),
                            fieldWithPath("data.content").type(JsonFieldType.ARRAY).description("계약 내역 리스트"),
                            fieldWithPath("data.content[].contractId").description("계약 ID"),
                            fieldWithPath("data.content[].contractStatus").description("계약 상태 (한글/영문)"),
                            fieldWithPath("data.content[].contractStatusCode").description("계약 상태 코드"),
                            fieldWithPath("data.content[].policyNumber").description("증권 번호"),
                            fieldWithPath("data.content[].partnerName").description("제휴사 명"),
                            fieldWithPath("data.content[].channelName").description("가입 채널"),
                            fieldWithPath("data.content[].applicantName").description("신청자(대표자) 이름"),
                            fieldWithPath("data.content[].applicantPhone").description("신청자 연락처"),
                            fieldWithPath("data.content[].insuredCount").description("총 가입 인원수"),
                            fieldWithPath("data.content[].totalPremium").description("총 보험료"),
                            fieldWithPath("data.content[].applicationDate").description("신청 일시"),
                            fieldWithPath("data.content[].insuranceStartDate").description("보험 시작 일시"),
                            fieldWithPath("data.content[].insuranceEndDate").description("보험 종료 일시"),

                            // 3. PageResult 메타 정보 문서화
                            fieldWithPath("data.totalElements").description("총 데이터 수"),
                            fieldWithPath("data.totalPages").description("총 페이지 수"),
                            fieldWithPath("data.currentPage").description("현재 페이지 번호"),
                            fieldWithPath("data.hasNext").description("다음 페이지 존재 여부"))));
    }

    @Test
    @DisplayName("여행자 보험 계약 상세 조회 API 문서화")
    void getContractDetail() throws Exception {
        // Given: UI 이미지 기반 Mock Data 생성
        Long contractId = 1L;

        InsuranceContract mockContract = InsuranceContract.builder()
            .contractId(contractId)
            .status(ContractStatus.COMPLETED)
            // 1. 보험 가입 정보
            .metaInfo(ContractMeta.builder()
                .policyNumber("15540-97222")
                .origin(new SubscriptionOrigin("TPA KOREA", "TPA KOREA", "메리츠"))
                .applicationDate(LocalDateTime.of(2024, 2, 1, 0, 0)) // 2024.02.01
                .period(new InsurancePeriod(LocalDateTime.of(2024, 3, 15, 17, 0), // 2024.03.15
                                                                                  // 17:00
                        LocalDateTime.of(2025, 3, 14, 19, 0) // 2025.03.14 19:00
                ))
                .build())
            // 2. 상품 정보
            .productPlan(ProductPlan.builder().productName("해외여행보험").planName("알뜰 플랜").travelCountry("일본").build())
            // 3. 신청자 정보
            .applicant(Applicant.builder()
                .name("홍길동")
                .residentNumber("910504-1234567") // 변환 시 마스킹 처리됨
                .phoneNumber("010-0000-0000")
                .email("contractor@abc.com")
                .build())
            // 4. 결제 정보
            .paymentInfo(PaymentInfo.builder()
                .method("카드 결제")
                .totalAmount(BigDecimal.valueOf(17000))
                .paidAt(LocalDateTime.of(2025, 12, 24, 15, 1, 42))
                .canceledAt(LocalDateTime.of(2025, 12, 25, 15, 1, 42)) // 해지 일시 존재 시
                .build())
            // 5. 동반자 정보 (UI 테이블 참조)
            .insuredPeople(List.of(InsuredPerson.builder() // 1번
                .name("홍길동")
                .englishName("Hong Gildong")
                .residentNumber("910504-1234567")
                .passportNumber("M12345678")
                .gender("남성")
                .individualPremium(BigDecimal.valueOf(8000))
                .iIndividualPolicyNumber("15540-97222")
                .build(),
                    InsuredPerson.builder() // 2번
                        .name("김영희")
                        .englishName("Kim Younghee")
                        .residentNumber("910504-2111111")
                        .passportNumber("M87654321")
                        .gender("여성")
                        .individualPremium(BigDecimal.valueOf(9000))
                        .iIndividualPolicyNumber("15540-97222")
                        .build()))
            .build();

        given(contractService.getContractDetail(contractId)).willReturn(mockContract);

        // When & Then
        mockMvc
            .perform(get("/v1/admin/travel/contract/{contractId}", contractId).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(document("contract-detail", pathParameters(parameterWithName("contractId").description("조회할 계약 ID")),
                    responseFields(fieldWithPath("result").description("API 실행 결과 (SUCCESS/ERROR)"),
                            fieldWithPath("error").description("에러 정보 (성공 시 null)").optional(),

                            // 1. Root Data (ID, Companions 등)
                            fieldWithPath("data.contractId").description("계약 ID"),

                            // 2. Insurance Section (보험 가입 정보)
                            fieldWithPath("data.insuranceSection").description("보험 가입 정보 섹션"),

                            // 2-1. Product (상품)
                            fieldWithPath("data.insuranceSection.product.name").description("보험 상품명"),
                            fieldWithPath("data.insuranceSection.product.plan").description("플랜명"),
                            fieldWithPath("data.insuranceSection.product.country").description("여행 국가"),

                            // 2-2. Subscription (가입 출처)
                            fieldWithPath("data.insuranceSection.subscription.partner").description("제휴사"),
                            fieldWithPath("data.insuranceSection.subscription.channel").description("채널"),
                            fieldWithPath("data.insuranceSection.subscription.insurer").description("보험사"),

                            // 2-3. Term (기간)
                            fieldWithPath("data.insuranceSection.term.applicationDate").description("신청 일시"),
                            fieldWithPath("data.insuranceSection.term.startDate").description("보험 시작 일시"),
                            fieldWithPath("data.insuranceSection.term.endDate").description("보험 종료 일시"),

                            // 2-4. Status (상태 및 통계)
                            fieldWithPath("data.insuranceSection.status.statusName").description("계약 상태 (한글)"),
                            fieldWithPath("data.insuranceSection.status.statusCode").description("계약 상태 코드"),
                            fieldWithPath("data.insuranceSection.status.insuredCount").description("총 가입 인원수"),
                            fieldWithPath("data.insuranceSection.status.totalPremium").description("총 보험료"),

                            // 2-5. Policy Number
                            fieldWithPath("data.insuranceSection.policyNumber").description("증권번호"),

                            // 3. Applicant Section (신청자 정보)
                            fieldWithPath("data.applicantSection").description("신청자 정보 섹션"),
                            fieldWithPath("data.applicantSection.name").description("신청자 이름"),
                            fieldWithPath("data.applicantSection.residentNumber").description("주민등록번호 (마스킹)"),
                            fieldWithPath("data.applicantSection.phoneNumber").description("연락처"),
                            fieldWithPath("data.applicantSection.email").description("이메일"),

                            // 4. Payment Section (결제 정보)
                            fieldWithPath("data.paymentSection").description("결제 정보 섹션"),
                            fieldWithPath("data.paymentSection.method").description("결제 수단"),
                            fieldWithPath("data.paymentSection.totalAmount").description("결제 총액"),
                            fieldWithPath("data.paymentSection.paidAt").description("결제 일시"),
                            fieldWithPath("data.paymentSection.canceledAt").description("결제 취소 일시").optional(),

                            // 5. Companions (동반자 정보)
                            fieldWithPath("data.companions").description("피보험자(동반자) 목록"),
                            fieldWithPath("data.companions[].name").description("이름"),
                            fieldWithPath("data.companions[].englishName").description("영문 이름"),
                            fieldWithPath("data.companions[].residentNumber").description("주민등록번호 (마스킹)"),
                            fieldWithPath("data.companions[].passportNumber").description("여권 번호"),
                            fieldWithPath("data.companions[].gender").description("성별"),
                            fieldWithPath("data.companions[].premium").description("개별 보험료"),
                            fieldWithPath("data.companions[].policyNumber").description("개별 증권번호"))));
    }

}
