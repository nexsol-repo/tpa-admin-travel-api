package com.nexsol.tpa.core.domain;

import lombok.Builder;

@Builder
public record InsuranceSection(ProductInfo product, // 상품, 플랜, 국가
        SubscriptionInfo subscription, // 제휴사, 채널, 보험사
        TermInfo term, // 기간, 신청일
        ContractStatusInfo status, // 상태, 인원수, 총 보험료
        String policyNumber // 증권번호 (중요해서 밖으로 뺌)
) {
    public static InsuranceSection toInsuranceSection(InsuranceContract domain) {
        return InsuranceSection.builder()
            .product(ProductInfo.toProductInfo(domain.productPlan()))
            .subscription(SubscriptionInfo.toSubscriptionInfo(domain.metaInfo()))
            .term(TermInfo.toTermInfo(domain.metaInfo()))
            .status(ContractStatusInfo.toContractStatusInfo(domain))
            .policyNumber(domain.metaInfo().policyNumber())
            .build();
    }
}