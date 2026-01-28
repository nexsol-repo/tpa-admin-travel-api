package com.nexsol.tpa.core.api.controller.v1;

import com.nexsol.tpa.core.domain.channel.Channel;
import com.nexsol.tpa.core.domain.channel.ChannelService;
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

public class ChannelControllerTest extends RestDocsTest {

    private ChannelService channelService;

    @BeforeEach
    public void setUp(RestDocumentationContextProvider restDocumentation) {
        super.setUp(restDocumentation);
        channelService = mock(ChannelService.class);
        mockMvc = mockController(new ChannelController(channelService));
    }

    @Test
    @DisplayName("채널 목록 조회 API 문서화")
    void getChannels() throws Exception {
        // Given
        List<Channel> mockChannels = List.of(
                new Channel(1L, "CH001", "TPA KOREA"),
                new Channel(2L, "CH002", "모바일앱"),
                new Channel(3L, "CH003", "제휴몰")
        );

        given(channelService.getActiveChannels()).willReturn(mockChannels);

        // When & Then
        mockMvc.perform(get("/v1/admin/travel/channel")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("channel-list",
                        responseFields(
                                fieldWithPath("result").description("API 실행 결과 (SUCCESS/ERROR)"),
                                fieldWithPath("error").description("에러 정보 (성공 시 null)").optional(),
                                fieldWithPath("data").description("채널 목록"),
                                fieldWithPath("data[].id").description("채널 ID"),
                                fieldWithPath("data[].code").description("채널 코드"),
                                fieldWithPath("data[].name").description("채널명")
                        )
                ));
    }

}
