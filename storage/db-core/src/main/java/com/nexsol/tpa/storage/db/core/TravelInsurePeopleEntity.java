package com.nexsol.tpa.storage.db.core;

import com.nexsol.tpa.core.domain.InsuredPerson;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "travel_insure_people")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TravelInsurePeopleEntity extends BaseEntity {

    @Column(name = "contract_id")
    private Long contractId;

    @Column(name = "insure_people_name")
    private String name;

    @Column(name = "insure_people_name_eng")
    private String englishName;

    @Column(name = "insure_people_resident_number")
    private String residentNumber;

    @Column(name = "insure_people_passport_number")
    private String passportNumber;

    @Column(name = "insure_people_gender")
    private String gender;

    @Column(name = "insure_premium")
    private BigDecimal insurePremium;

    @Column(name = "insure_number")
    private String insureNumber;

    @Builder
    private TravelInsurePeopleEntity(Long contractId, String name, String englishName, String residentNumber,
            String passportNumber, String gender, BigDecimal insurePremium, String insureNumber) {
        this.contractId = contractId;
        this.name = name;
        this.englishName = englishName;
        this.residentNumber = residentNumber;
        this.passportNumber = passportNumber;
        this.gender = gender;
        this.insurePremium = insurePremium;
        this.insureNumber = insureNumber;
    }

    public void updatePersonInfo(String name, String englishName, String residentNumber, String passportNumber,
            String gender) {
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
    }

    public InsuredPerson toDomain() {
        return InsuredPerson.builder()
            .name(this.name)
            .englishName(this.englishName)
            .residentNumber(this.residentNumber)
            .passportNumber(this.passportNumber)
            .gender(this.gender)
            .individualPremium(this.insurePremium)
            .iIndividualPolicyNumber(this.insureNumber)
            .build();
    }

}
