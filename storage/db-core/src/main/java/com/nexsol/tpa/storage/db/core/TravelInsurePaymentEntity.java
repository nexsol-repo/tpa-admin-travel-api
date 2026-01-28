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
@Table(name = "travel_insure_payment")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TravelInsurePaymentEntity extends BaseEntity {

    private Long contractId;

    private String paymentMethod;

    private BigDecimal paidAmount;

    private LocalDateTime paymentDate;

    private LocalDateTime cancelDate;

    private String status;

    /**
     * 도메인 객체로부터 결제 엔티티 생성
     */
    public static TravelInsurePaymentEntity create(Long contractId, PaymentInfo payment) {
        TravelInsurePaymentEntity entity = new TravelInsurePaymentEntity();
        entity.contractId = contractId;
        entity.paymentMethod = payment.method();
        entity.paidAmount = payment.totalAmount();
        entity.paymentDate = payment.paidAt();
        entity.cancelDate = payment.canceledAt();
        entity.status = payment.canceledAt() != null ? "CANCELED" : "COMPLETED";
        return entity;
    }

    public void updatePaymentInfo(Long contractId, String paymentMethod, LocalDateTime paymentDate,
            LocalDateTime cancelDate) {
        if (contractId != null) {
            this.contractId = contractId;
        }
        if (paymentMethod != null) {
            this.paymentMethod = paymentMethod;
        }
        // 날짜는 null 업데이트가 허용될 수 있음 (예: 취소 철회 등)
        // 비즈니스 요건에 따라 null 체크 여부 결정. 여기서는 입력된 값으로 덮어쓰기 구현
        if (paymentDate != null) {
            this.paymentDate = paymentDate;
        }

        // 해지일은 값이 들어올 때만 수정하거나, 로직에 따라 null로 초기화가 필요할 수도 있음
        this.cancelDate = cancelDate;
    }

    public PaymentInfo toDomain() {
        return PaymentInfo.builder()
            .method(this.paymentMethod)
            .totalAmount(this.paidAmount)
            .paidAt(this.paymentDate)
            .canceledAt(this.cancelDate)
            .build();
    }

    public PaymentInfo toDomain(BigDecimal fallbackAmount) {
        return PaymentInfo.builder()
            .method(this.paymentMethod)
            .totalAmount(this.paidAmount != null ? this.paidAmount : fallbackAmount)
            .paidAt(this.paymentDate)
            .canceledAt(this.cancelDate)
            .build();
    }

}
