package com.nexsol.tpa.storage.db.core;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tpa_insurer")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TravelInsurerEntity extends BaseEntity {

	private String insurerCode;

	private String insurerName;

	private String apiBaseUrl;

	private String serviceType;

	@Column(columnDefinition = "TINYINT(1)")
	private boolean isActive;

}
