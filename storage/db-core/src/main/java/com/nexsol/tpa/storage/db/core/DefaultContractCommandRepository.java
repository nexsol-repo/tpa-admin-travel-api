package com.nexsol.tpa.storage.db.core;

import com.nexsol.tpa.core.domain.contract.ContractCommandRepository;
import com.nexsol.tpa.core.domain.contract.InsuranceContract;
import com.nexsol.tpa.core.domain.payment.PaymentInfo;
import com.nexsol.tpa.core.domain.payment.RefundInfo;
import com.nexsol.tpa.core.error.CoreErrorType;
import com.nexsol.tpa.core.error.CoreException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class DefaultContractCommandRepository implements ContractCommandRepository {

	private final TravelContractJpaRepository travelContractJpaRepository;

	private final PaymentJpaRepository paymentJpaRepository;

	private final InsuredPersonJpaRepository insuredPersonJpaRepository;

	private final TravelInsureRefundJpaRepository refundJpaRepository;

	private final TravelContractMapper travelContractMapper;

	@Override
	public Long create(InsuranceContract contract) {
		TravelContractEntity contractEntity = travelContractMapper.toEntity(contract);
		TravelContractEntity savedContract = travelContractJpaRepository.save(contractEntity);
		Long contractId = savedContract.getId();

		if (contract.insuredPeople() != null && !contract.insuredPeople().isEmpty()) {
			List<TravelInsuredEntity> peopleEntities = contract.insuredPeople()
				.stream()
				.map(person -> TravelInsuredEntity.create(contractId, person))
				.toList();
			insuredPersonJpaRepository.saveAll(peopleEntities);
		}

		if (contract.paymentInfo() != null) {
			TravelPaymentEntity paymentEntity = TravelPaymentEntity.create(contractId, contract.paymentInfo());
			TravelPaymentEntity savedPayment = paymentJpaRepository.save(paymentEntity);

			if (contract.refundInfo() != null) {
				TravelRefundEntity refundEntity = TravelRefundEntity.create(savedPayment.getId(), contractId,
						contract.refundInfo());
				refundJpaRepository.save(refundEntity);
			}
		}

		return contractId;
	}

	@Override
	public Long save(InsuranceContract contract) {
		TravelContractEntity entity = travelContractJpaRepository.findById(contract.contractId())
			.orElseThrow(() -> new CoreException(CoreErrorType.INSURANCE_NOT_FOUND_DATA));

		applyContractChanges(entity, contract);

		TravelContractEntity saved = travelContractJpaRepository.save(entity);

		savePayment(contract.contractId(), contract.paymentInfo());
		saveRefund(contract.contractId(), contract.refundInfo());

		return saved.getId();
	}

	private void applyContractChanges(TravelContractEntity entity, InsuranceContract contract) {
		if (contract.status() != null) {
			entity.updateStatus(contract.status().name());
		}
		if (contract.metaInfo() != null) {
			entity.updateInsurancePeriod(contract.metaInfo().period());
			entity.updateSubscriptionOrigin(contract.metaInfo().origin());
			entity.updatePolicyNumber(contract.metaInfo().policyNumber());
			entity.updatePolicyLink(contract.metaInfo().policyLink());
			entity.updateApplyDate(contract.metaInfo().applicationDate());
		}
		if (contract.productPlan() != null) {
			entity.updateFamilyId(contract.productPlan().familyId());
			entity.updateCountryName(contract.productPlan().travelCountry());
			entity.updateCountryCode(contract.productPlan().countryCode());
		}
		if (contract.totalPremium() != null) {
			entity.updateTotalPremium(contract.totalPremium());
		}
		if (contract.employeeId() != null) {
			entity.updateEmployeeId(contract.employeeId());
		}
	}

	private void savePayment(Long contractId, PaymentInfo paymentInfo) {
		if (paymentInfo == null) {
			return;
		}
		paymentJpaRepository.findByContractId(contractId).ifPresent(entity -> {
			entity.updatePaymentInfo(contractId, paymentInfo.status(), paymentInfo.method(), paymentInfo.totalAmount(),
					paymentInfo.paidAt(), paymentInfo.canceledAt());
			paymentJpaRepository.save(entity);
		});
	}

	private void saveRefund(Long contractId, RefundInfo refundInfo) {
		if (refundInfo == null) {
			return;
		}
		refundJpaRepository.findByContractId(contractId).ifPresentOrElse(entity -> {
			entity.update(refundInfo);
			refundJpaRepository.save(entity);
		}, () -> {
			paymentJpaRepository.findByContractId(contractId).ifPresent(payment -> {
				TravelRefundEntity newEntity = TravelRefundEntity.create(payment.getId(), contractId, refundInfo);
				refundJpaRepository.save(newEntity);
			});
		});
	}

}