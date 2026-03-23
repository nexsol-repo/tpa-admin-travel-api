package com.nexsol.tpa.storage.db.core;

import com.nexsol.tpa.core.domain.product.InsurancePeriod;
import com.nexsol.tpa.core.domain.subscription.SubscriptionOrigin;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "travel_contract")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TravelContractEntity extends BaseEntity {

	private Long partnerId;

	private Long channelId;

	private Long insurerId;

	@Column(name = "family_id")
	private Long familyId;

	private String policyNumber;

	private String policyLink;

	private String partnerName;

	private String channelName;

	private String insurerName;

	private String countryName;

	private String countryCode;

	private String status;

	private LocalDateTime applyDate;

	private LocalDate insureStartDate;

	private LocalDate insureEndDate;

	private BigDecimal totalPremium;

	private Long employeeId;

	private LocalDateTime deletedAt;

	public void updateStatus(String status) {
		if (status != null) {
			this.status = status;
		}
	}

	public void updateInsurancePeriod(InsurancePeriod period) {
		if (period == null) {
			return;
		}
		this.insureStartDate = updateIfNotNull(this.insureStartDate, period.startDate());
		this.insureEndDate = updateIfNotNull(this.insureEndDate, period.endDate());
	}

	public void updateInsurancePeriod(LocalDate startDate, LocalDate endDate) {
		this.insureStartDate = updateIfNotNull(this.insureStartDate, startDate);
		this.insureEndDate = updateIfNotNull(this.insureEndDate, endDate);
	}

	public void updateSubscriptionOrigin(SubscriptionOrigin origin) {
		if (origin == null) {
			return;
		}
		this.partnerId = updateIfNotNull(this.partnerId, origin.partnerId());
		this.partnerName = updateIfNotNull(this.partnerName, origin.partnerName());
		this.channelId = updateIfNotNull(this.channelId, origin.channelId());
		this.channelName = updateIfNotNull(this.channelName, origin.channelName());
		this.insurerId = updateIfNotNull(this.insurerId, origin.insurerId());
		this.insurerName = updateIfNotNull(this.insurerName, origin.insurerName());
	}

	public void updateFamilyId(Long familyId) {
		this.familyId = updateIfNotNull(this.familyId, familyId);
	}

	public void updateCountryName(String countryName) {
		this.countryName = updateIfNotNull(this.countryName, countryName);
	}

	public void updateCountryCode(String countryCode) {
		this.countryCode = updateIfNotNull(this.countryCode, countryCode);
	}

	public void updateTotalPremium(BigDecimal totalPremium) {
		this.totalPremium = updateIfNotNull(this.totalPremium, totalPremium);
	}

	public void updatePolicyNumber(String policyNumber) {
		this.policyNumber = updateIfNotNull(this.policyNumber, policyNumber);
	}

	public void updatePolicyLink(String policyLink) {
		this.policyLink = updateIfNotNull(this.policyLink, policyLink);
	}

	public void updateApplyDate(LocalDateTime applyDate) {
		this.applyDate = updateIfNotNull(this.applyDate, applyDate);
	}

	public void updateEmployeeId(Long employeeId) {
		this.employeeId = updateIfNotNull(this.employeeId, employeeId);
	}

	private <T> T updateIfNotNull(T currentValue, T newValue) {
		return (newValue != null) ? newValue : currentValue;
	}

}
