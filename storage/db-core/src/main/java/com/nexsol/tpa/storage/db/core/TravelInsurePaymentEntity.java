package com.nexsol.tpa.storage.db.core;

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

}
