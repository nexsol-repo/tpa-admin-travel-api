package com.nexsol.tpa.storage.db.core;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "travel_insurance_plan_family")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TravelInsurancePlanFamilyEntity extends BaseEntity {

	@Column(name = "insurer_id")
	private Long insurerId;

	@Column(name = "insurance_product_name")
	private String insuranceProductName;

	@Column(name = "family_name")
	private String familyName;

	@Column(name = "sort_order")
	private Integer sortOrder;

	@Column(name = "is_active", columnDefinition = "TINYINT(1)")
	private boolean isActive;

	@Column(name = "is_loss", columnDefinition = "TINYINT(1)")
	private boolean isLoss;

}