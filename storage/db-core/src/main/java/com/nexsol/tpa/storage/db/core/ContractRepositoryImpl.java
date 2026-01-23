package com.nexsol.tpa.storage.db.core;

import com.nexsol.tpa.core.domain.ContractRepository;
import com.nexsol.tpa.core.domain.ContractSearchCriteria;
import com.nexsol.tpa.core.domain.InsuranceContract;
import com.nexsol.tpa.core.support.PageResult;
import com.nexsol.tpa.core.support.SortPage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ContractRepositoryImpl implements ContractRepository {

    private final TravelContractJpaRepository travelContractJpaRepository;

    private final PaymentJpaRepository paymentJpaRepository;

    private final InsuredPersonJpaRepository insuredPersonJpaRepository;

    private final ContractEntityMapper mapper;

    @Override
    public Optional<InsuranceContract> findById(Long contractId) {
        var contractEntityOptional = travelContractJpaRepository.findById(contractId);

        if (contractEntityOptional.isEmpty()) {
            return Optional.empty();
        }

        TravelContractEntity contract = contractEntityOptional.get();

        var payment = paymentJpaRepository.findByContractId(contractId).orElse(null);
        var people = insuredPersonJpaRepository.findAllByContractId(contractId);

        return Optional.of(mapper.toDomain(contract, payment, people));
    }

    @Override
    public PageResult<InsuranceContract> findAll(ContractSearchCriteria criteria, SortPage sortPage) {
        return PageResult.of(List.of(), 0, sortPage.size(), sortPage.page());
    }

    @Override
    public InsuranceContract save(InsuranceContract contract) {
        return null;
    }

}
