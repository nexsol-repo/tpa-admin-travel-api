package com.nexsol.tpa.client.memo;

import com.nexsol.tpa.client.memo.dto.MemoRequest;
import com.nexsol.tpa.client.memo.dto.NotificationRequest;
import com.nexsol.tpa.client.memo.dto.SystemLogRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "memo-service", url = "${external.memo-service.url}")
public interface MemoClient {

    @PostMapping("/v1/admin/memo/{contractId}")
    void createMemo(@PathVariable("contractId") Long contractId, @RequestBody MemoRequest request);

    @PostMapping("/v1/admin/memo/{contractId}/system-log")
    void createSystemLog(@PathVariable("contractId") Long contractId, @RequestBody SystemLogRequest request);

    @PostMapping("/v1/admin/memo/{contractId}/notification")
    void createNotification(@PathVariable("contractId") Long contractId, @RequestBody NotificationRequest request);

}
