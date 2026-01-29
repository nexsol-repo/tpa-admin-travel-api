package com.nexsol.tpa.storage.db.core;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tpa_channel")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TravelChannelEntity extends BaseEntity {

    private Long partnerId;

    private String channelCode;

    private String channelName;

    private String serviceType;

    @Column(columnDefinition = "TINYINT(1)")
    private boolean isActive;

}
