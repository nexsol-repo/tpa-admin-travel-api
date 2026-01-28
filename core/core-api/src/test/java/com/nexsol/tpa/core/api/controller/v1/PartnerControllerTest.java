package com.nexsol.tpa.core.api.controller.v1;

import com.nexsol.tpa.core.domain.partner.Partner;
import com.nexsol.tpa.core.domain.partner.PartnerService;
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

public class PartnerControllerTest extends RestDocsTest {

    private PartnerService partnerService;

    @BeforeEach
    public void setUp(RestDocumentationContextProvider restDocumentation) {
        super.setUp(restDocumentation);
        partnerService = mock(PartnerService.class);
        mockMvc = mockController(new PartnerController(partnerService));
    }

    @Test
    @DisplayName("제휴사 목록 조회 API 문서화")
    void getPartners() throws Exception {
        // Given
        List<Partner> mockPartners = List.of(
                new Partner(1L, "TPA001", "TPA KOREA"),
                new Partner(2L, "TPA002", "여행사A"),
                new Partner(3L, "TPA003", "여행사B")
        );

        given(partnerService.getActivePartners()).willReturn(mockPartners);

        // When & Then
        mockMvc.perform(get("/v1/admin/travel/partner")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("partner-list",
                        responseFields(
                                fieldWithPath("result").description("API 실행 결과 (SUCCESS/ERROR)"),
                                fieldWithPath("error").description("에러 정보 (성공 시 null)").optional(),
                                fieldWithPath("data").description("제휴사 목록"),
                                fieldWithPath("data[].id").description("제휴사 ID"),
                                fieldWithPath("data[].code").description("제휴사 코드"),
                                fieldWithPath("data[].name").description("제휴사명")
                        )
                ));
    }

}
