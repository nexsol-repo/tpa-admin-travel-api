package com.nexsol.tpa.core.domain.contract;

import com.nexsol.tpa.core.domain.payment.PaymentInfo;
import com.nexsol.tpa.core.domain.payment.RefundInfo;
import com.nexsol.tpa.core.enums.ContractStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 계약의 결제/환불/상태 수정을 담당하는 도구 클래스 (Implement Layer)
 */
@Component
public class ContractPaymentUpdater {

	public ContractStatus resolveStatus(InsuranceContract existing, ModifyContract mc) {
		if (mc.status() != null) {
			return mc.status();
		}
		if (mc.statusName() != null) {
			return ContractStatus.COMPLETED;
		}
		return existing.status();
	}

	public PaymentInfo updatePayment(PaymentInfo existing, ContractPayment payment, String statusName) {
		if (payment == null && statusName == null) {
			return existing;
		}

		PaymentSnapshot current = PaymentSnapshot.from(existing);
		String resolvedStatus = resolvePaymentStatus(payment, statusName, current.status);

		if (payment == null) {
			return current.toPaymentInfo(resolvedStatus);
		}

		return PaymentInfo.builder()
			.status(resolvedStatus)
			.method(firstNonNull(payment.method(), current.method))
			.totalAmount(firstNonNull(payment.totalAmount(), current.totalAmount))
			.paidAt(firstNonNull(payment.paidAt(), current.paidAt))
			.canceledAt(payment.canceledAt())
			.build();
	}

	public RefundInfo updateRefund(RefundInfo existing, ContractRefund refund) {
		if (refund == null) {
			return existing;
		}

		BigDecimal refundAmount = firstNonNull(refund.refundAmount(), safeGet(existing, RefundInfo::refundAmount));
		String refundMethod = firstNonNull(refund.refundMethod(), safeGet(existing, RefundInfo::refundMethod));
		LocalDateTime refundedAt = firstNonNull(refund.refundedAt(), safeGet(existing, RefundInfo::refundedAt));

		return RefundInfo.builder()
			.refundAmount(refundAmount)
			.refundMethod(refundMethod)
			.bankName(refund.bankName())
			.accountNumber(refund.accountNumber())
			.depositorName(refund.depositorName())
			.refundReason(refund.refundReason())
			.refundedAt(refundedAt)
			.build();
	}

	private String resolvePaymentStatus(ContractPayment payment, String statusName, String currentStatus) {
		if (payment != null && payment.status() != null) {
			return payment.status();
		}
		if (statusName != null) {
			return switch (statusName) {
				case "임의해지" -> "CANCELED";
				case "가입완료" -> "COMPLETED";
				default -> currentStatus;
			};
		}
		return currentStatus;
	}

	private <T> T firstNonNull(T first, T second) {
		if (first != null) {
			return first;
		}
		return second;
	}

	private <T, R> R safeGet(T obj, java.util.function.Function<T, R> getter) {
		if (obj == null) {
			return null;
		}
		return getter.apply(obj);
	}

	/**
	 * 기존 결제 정보의 스냅샷 (null-safe 추출)
	 */
	private record PaymentSnapshot(String status, String method, BigDecimal totalAmount, LocalDateTime paidAt,
			LocalDateTime canceledAt) {

		static PaymentSnapshot from(PaymentInfo existing) {
			if (existing == null) {
				return new PaymentSnapshot(null, null, BigDecimal.ZERO, null, null);
			}
			return new PaymentSnapshot(existing.status(), existing.method(), existing.totalAmount(), existing.paidAt(),
					existing.canceledAt());
		}

		PaymentInfo toPaymentInfo(String resolvedStatus) {
			return PaymentInfo.builder()
				.status(resolvedStatus)
				.method(method)
				.totalAmount(totalAmount)
				.paidAt(paidAt)
				.canceledAt(canceledAt)
				.build();
		}
	}

}