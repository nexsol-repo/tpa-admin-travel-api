package com.nexsol.tpa.storage.db.core;

import com.nexsol.tpa.core.domain.payment.RefundInfo;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "travel_insure_refund")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TravelInsureRefundEntity extends BaseEntity {

	@Column(name = "payment_id")
	private Long paymentId;

	@Column(name = "contract_id")
	private Long contractId;

	@Column(name = "refund_amount")
	private BigDecimal refundAmount;

	@Column(name = "refund_method")
	private String refundMethod;

	@Column(name = "bank_name")
	private String bankName;

	@Column(name = "account_number")
	private String accountNumber;

	@Column(name = "depositor_name")
	private String depositorName;

	@Column(name = "refund_reason")
	private String refundReason;

	@Column(name = "refunded_at")
	private LocalDateTime refundedAt;

	public static TravelInsureRefundEntity create(Long paymentId, Long contractId, RefundInfo refund) {
		TravelInsureRefundEntity entity = new TravelInsureRefundEntity();
		entity.paymentId = paymentId;
		entity.contractId = contractId;
		entity.refundAmount = refund.refundAmount();
		entity.refundMethod = refund.refundMethod();
		entity.bankName = refund.bankName();
		entity.accountNumber = refund.accountNumber();
		entity.depositorName = refund.depositorName();
		entity.refundReason = refund.refundReason();
		entity.refundedAt = refund.refundedAt();
		return entity;
	}

	public void update(RefundInfo refund) {
		if (refund.refundAmount() != null) {
			this.refundAmount = refund.refundAmount();
		}
		if (refund.refundMethod() != null) {
			this.refundMethod = refund.refundMethod();
		}
		if (refund.refundedAt() != null) {
			this.refundedAt = refund.refundedAt();
		}
		this.bankName = refund.bankName();
		this.accountNumber = refund.accountNumber();
		this.depositorName = refund.depositorName();
		this.refundReason = refund.refundReason();
	}

	public RefundInfo toDomain() {
		return RefundInfo.builder()
			.refundAmount(this.refundAmount)
			.refundMethod(this.refundMethod)
			.bankName(this.bankName)
			.accountNumber(this.accountNumber)
			.depositorName(this.depositorName)
			.refundReason(this.refundReason)
			.refundedAt(this.refundedAt)
			.build();
	}

}