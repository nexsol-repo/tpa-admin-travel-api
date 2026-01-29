package com.nexsol.tpa.core.api.controller.v1;

import com.nexsol.tpa.core.api.controller.v1.request.NotificationSendRequest;
import com.nexsol.tpa.core.domain.notification.NotificationService;
import com.nexsol.tpa.core.support.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/admin/travel")
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * 이메일 발송 API
     */
    @PostMapping("/contract/{contractId}/notification/email")
    public ApiResponse<Void> sendEmail(@PathVariable Long contractId, @RequestBody NotificationSendRequest request) {
        notificationService.sendEmail(request.toCommand(contractId));
        return ApiResponse.success(null);
    }

    /**
     * SMS 발송 API
     */
    @PostMapping("/contract/{contractId}/notification/sms")
    public ApiResponse<Void> sendSms(@PathVariable Long contractId, @RequestBody NotificationSendRequest request) {
        notificationService.sendSms(request.toCommand(contractId));
        return ApiResponse.success(null);
    }

    /**
     * 이메일 + SMS 동시 발송 API
     */
    @PostMapping("/contract/{contractId}/notification/all")
    public ApiResponse<Void> sendAll(@PathVariable Long contractId, @RequestBody NotificationSendRequest request) {
        notificationService.sendAll(request.toCommand(contractId));
        return ApiResponse.success(null);
    }

}
