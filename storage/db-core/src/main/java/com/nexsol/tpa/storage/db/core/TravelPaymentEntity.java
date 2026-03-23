package com.nexsol.tpa.storage.db.core;

import com.nexsol.tpa.core.domain.payment.PaymentInfo;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "travel_payment")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TravelPaymentEntity extends BaseEntity {

	private Long contractId;

	private String paymentMethod;

	private BigDecimal paidAmount;

	private LocalDateTime paymentDate;

	private LocalDateTime cancelDate;

	private String status;

	/**
	 * 도메인 객체로부터 결제 엔티티 생성
	 */
	public static TravelPaymentEntity create(Long contractId, PaymentInfo payment) {
		TravelPaymentEntity entity = new TravelPaymentEntity();
		entity.contractId = contractId;
		entity.paymentMethod = payment.method();
		entity.paidAmount = payment.totalAmount();
		entity.paymentDate = payment.paidAt();
		entity.cancelDate = payment.canceledAt();
		entity.status = payment.canceledAt() != null ? "CANCELED" : "COMPLETED";
		return entity;
	}

	public void updatePaymentInfo(Long contractId, String status, String paymentMethod, BigDecimal paidAmount,
			LocalDateTime paymentDate, LocalDateTime cancelDate) {
		if (contractId != null) {
			this.contractId = contractId;
		}
		if (status != null) {
			this.status = status;
		}
		if (paymentMethod != null) {
			this.paymentMethod = paymentMethod;
		}
		if (paidAmount != null) {
			this.paidAmount = paidAmount;
		}
		if (paymentDate != null) {
			this.paymentDate = paymentDate;
		}
		this.cancelDate = cancelDate;
	}

	public PaymentInfo toDomain() {
		return PaymentInfo.builder()
			.status(this.status)
			.method(this.paymentMethod)
			.totalAmount(this.paidAmount)
			.paidAt(this.paymentDate)
			.canceledAt(this.cancelDate)
			.build();
	}

	public PaymentInfo toDomain(BigDecimal fallbackAmount) {
		return PaymentInfo.builder()
			.status(this.status)
			.method(this.paymentMethod)
			.totalAmount(this.paidAmount != null ? this.paidAmount : fallbackAmount)
			.paidAt(this.paymentDate)
			.canceledAt(this.cancelDate)
			.build();
	}

}
