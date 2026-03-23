package com.nexsol.tpa.storage.db.core;

import com.nexsol.tpa.core.domain.applicant.InsuredPerson;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "travel_insured")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TravelInsuredEntity extends BaseEntity {

	@Column(name = "contract_id")
	private Long contractId;

	@Column(name = "plan_id")
	private Long planId;

	@Column(name = "is_contractor", columnDefinition = "TINYINT(1)")
	private Boolean isContractor;

	@Column(name = "name")
	private String name;

	@Column(name = "english_name")
	private String englishName;

	@Column(name = "resident_number")
	private String residentNumber;

	@Column(name = "passport_number")
	private String passportNumber;

	@Column(name = "gender")
	private String gender;

	@Column(name = "phone")
	private String phone;

	@Column(name = "email")
	private String email;

	@Column(name = "insure_premium")
	private BigDecimal insurePremium;

	@Column(name = "deleted_at")
	private java.time.LocalDateTime deletedAt;

	@Builder
	private TravelInsuredEntity(Long contractId, Long planId, Boolean isContractor, String name, String englishName,
			String residentNumber, String passportNumber, String gender, String phone, String email,
			BigDecimal insurePremium) {
		this.contractId = contractId;
		this.planId = planId;
		this.isContractor = isContractor;
		this.name = name;
		this.englishName = englishName;
		this.residentNumber = residentNumber;
		this.passportNumber = passportNumber;
		this.gender = gender;
		this.phone = phone;
		this.email = email;
		this.insurePremium = insurePremium;
	}

	/**
	 * 도메인 객체로부터 엔티티 생성
	 */
	public static TravelInsuredEntity create(Long contractId, InsuredPerson person) {
		return TravelInsuredEntity.builder()
			.contractId(contractId)
			.planId(person.planId())
			.isContractor(person.isContractor())
			.name(person.name())
			.englishName(person.englishName())
			.residentNumber(person.residentNumber())
			.passportNumber(person.passportNumber())
			.gender(person.gender())
			.phone(person.phone())
			.email(person.email())
			.insurePremium(person.individualPremium())
			.build();
	}

	public void updatePersonInfo(String name, String englishName, String residentNumber, String passportNumber,
			String gender, String phone, String email, BigDecimal insurePremium) {
		if (name != null) {
			this.name = name;
		}
		if (englishName != null) {
			this.englishName = englishName;
		}
		if (residentNumber != null) {
			this.residentNumber = residentNumber;
		}
		if (passportNumber != null) {
			this.passportNumber = passportNumber;
		}
		if (gender != null) {
			this.gender = gender;
		}
		if (phone != null) {
			this.phone = phone;
		}
		if (email != null) {
			this.email = email;
		}
		if (insurePremium != null) {
			this.insurePremium = insurePremium;
		}
	}

	public InsuredPerson toDomain() {
		return InsuredPerson.builder()
			.id(this.getId())
			.planId(this.planId)
			.isContractor(this.isContractor)
			.name(this.name)
			.englishName(this.englishName)
			.residentNumber(this.residentNumber)
			.passportNumber(this.passportNumber)
			.gender(this.gender)
			.phone(this.phone)
			.email(this.email)
			.individualPremium(this.insurePremium)
			.build();
	}

	public void softDelete() {
		this.deletedAt = java.time.LocalDateTime.now();
	}

	public void restore() {
		this.deletedAt = null;
	}

	public boolean isDeleted() {
		return this.deletedAt != null;
	}

}
