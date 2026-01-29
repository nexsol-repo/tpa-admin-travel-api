package com.nexsol.tpa.storage.db.core;

import com.nexsol.tpa.core.enums.ServiceType;
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

    @Enumerated(value = EnumType.STRING)
    private ServiceType serviceType;

    @Column(columnDefinition = "TINYINT(1)")
    private boolean isActive;

}
