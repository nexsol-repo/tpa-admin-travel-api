package com.nexsol.tpa.storage.db.core;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "travel_channel")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TravelChannelEntity extends BaseEntity {

    private String channelCode;

    private String channelName;

    @Column(columnDefinition = "TINYINT(1)")
    private boolean isActive;

}
