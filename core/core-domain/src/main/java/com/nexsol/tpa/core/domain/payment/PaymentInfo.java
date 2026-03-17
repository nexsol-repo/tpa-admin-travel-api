package com.nexsol.tpa.core.domain.payment;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record PaymentInfo(String status, String method, BigDecimal totalAmount, LocalDateTime paidAt,
		LocalDateTime canceledAt) {

	public static PaymentInfo toPaymentInfo(PaymentInfo domain) {
		if (domain == null)
			return null;
		return PaymentInfo.builder()
			.status(domain.status())
			.method(domain.method())
			.totalAmount(domain.totalAmount())
			.paidAt(domain.paidAt())
			.canceledAt(domain.canceledAt())
			.build();
	}
}
