package com.nexsol.tpa.storage.db.core;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "travel_insurance_plan_family_map")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TravelInsurancePlanFamilyMapEntity extends BaseEntity {

	@Column(name = "family_id")
	private Long familyId;

	@Column(name = "plan_id")
	private Long planId;

}