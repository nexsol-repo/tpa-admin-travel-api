package com.nexsol.tpa.storage.db.core;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "travel_insurance_plan")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TravelInsurancePlanEntity extends BaseEntity {

    @Column(name = "plan_name")
    private String planName;

    @Column(name = "plan_full_name")
    private String planFullName;

    @Column(name = "plan_code")
    private String planCode;

    @Column(name = "unit_product_code")
    private String unitProductCode;

    @Column(name = "plan_group_code")
    private String planGroupCode;

    @Column(name = "insurer_id")
    private Long insurerId;

    @Column(name = "is_active", columnDefinition = "TINYINT(1)")
    private boolean isActive;

}