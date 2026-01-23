package com.nexsol.tpa.storage.db.core;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "travel_insure_people")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TravelInsurePeopleEntity extends BaseEntity {

    @Column(name = "contract_id") // FK
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

    @Column(name = "insure_fee")
    private Long fee;

    @Column(name = "insure_number")
    private String insureNumber;

}
