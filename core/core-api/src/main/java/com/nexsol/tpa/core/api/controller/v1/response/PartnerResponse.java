package com.nexsol.tpa.core.api.controller.v1.response;

import com.nexsol.tpa.core.domain.partner.Partner;

public record PartnerResponse(
        Long id,
        String code,
        String name
) {

    public static PartnerResponse of(Partner partner) {
        return new PartnerResponse(
                partner.id(),
                partner.code(),
                partner.name()
        );
    }

}
