package com.nexsol.tpa.storage.db.core;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "travel_insurance_plan_family_map")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TravelInsurancePlanFamilyMapEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "family_id")
	private Long familyId;

	@Column(name = "plan_id")
	private Long planId;

	@Column(name = "created_at", updatable = false)
	private LocalDateTime createdAt;

}