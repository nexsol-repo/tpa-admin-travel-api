package com.nexsol.tpa.core.api.controller.v1;

import com.nexsol.tpa.core.domain.notification.NotificationService;
import com.nexsol.tpa.test.api.RestDocsTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class NotificationControllerTest extends RestDocsTest {

    private NotificationService notificationService;

    @BeforeEach
    public void setUp(RestDocumentationContextProvider restDocumentation) {
        notificationService = mock(NotificationService.class);

        mockMvc = MockMvcBuilders.standaloneSetup(new NotificationController(notificationService))
            .apply(documentationConfiguration(restDocumentation))
            .build();
    }

    @Test
    @DisplayName("이메일 발송 API 문서화")
    void sendEmail() throws Exception {
        // Given
        Long contractId = 1L;
        doNothing().when(notificationService).sendEmail(any());

        String requestBody = """
                {
                    "type": "CERTIFICATE",
                    "link": "https://travel.tpakorea.com/certificate/123"
                }
                """;

        // When & Then
        mockMvc
            .perform(post("/v1/admin/travel/contract/{contractId}/notification/email", contractId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isOk())
            .andDo(document("notification-email", pathParameters(parameterWithName("contractId").description("계약 ID")),
                    requestFields(fieldWithPath("type").description("알림 유형 (REJOIN: 재가입 안내, CERTIFICATE: 가입확인서 안내)"),
                            fieldWithPath("link").description("발송할 링크 URL")),
                    responseFields(fieldWithPath("result").description("API 실행 결과 (SUCCESS/ERROR)"),
                            fieldWithPath("error").description("에러 정보 (성공 시 null)").optional(),
                            fieldWithPath("data").description("응답 데이터 (성공 시 null)").optional())));
    }

    @Test
    @DisplayName("SMS 발송 API 문서화")
    void sendSms() throws Exception {
        // Given
        Long contractId = 1L;
        doNothing().when(notificationService).sendSms(any());

        String requestBody = """
                {
                    "type": "REJOIN",
                    "link": "https://travel.tpakorea.com/rejoin/123"
                }
                """;

        // When & Then
        mockMvc
            .perform(post("/v1/admin/travel/contract/{contractId}/notification/sms", contractId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isOk())
            .andDo(document("notification-sms", pathParameters(parameterWithName("contractId").description("계약 ID")),
                    requestFields(fieldWithPath("type").description("알림 유형 (REJOIN: 재가입 안내, CERTIFICATE: 가입확인서 안내)"),
                            fieldWithPath("link").description("발송할 링크 URL")),
                    responseFields(fieldWithPath("result").description("API 실행 결과 (SUCCESS/ERROR)"),
                            fieldWithPath("error").description("에러 정보 (성공 시 null)").optional(),
                            fieldWithPath("data").description("응답 데이터 (성공 시 null)").optional())));
    }

    @Test
    @DisplayName("이메일 + SMS 동시 발송 API 문서화")
    void sendAll() throws Exception {
        // Given
        Long contractId = 1L;
        doNothing().when(notificationService).sendAll(any());

        String requestBody = """
                {
                    "type": "CERTIFICATE",
                    "link": "https://travel.tpakorea.com/certificate/123"
                }
                """;

        // When & Then
        mockMvc
            .perform(post("/v1/admin/travel/contract/{contractId}/notification/all", contractId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isOk())
            .andDo(document("notification-all", pathParameters(parameterWithName("contractId").description("계약 ID")),
                    requestFields(fieldWithPath("type").description("알림 유형 (REJOIN: 재가입 안내, CERTIFICATE: 가입확인서 안내)"),
                            fieldWithPath("link").description("발송할 링크 URL")),
                    responseFields(fieldWithPath("result").description("API 실행 결과 (SUCCESS/ERROR)"),
                            fieldWithPath("error").description("에러 정보 (성공 시 null)").optional(),
                            fieldWithPath("data").description("응답 데이터 (성공 시 null)").optional())));
    }

}
