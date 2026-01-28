package com.nexsol.tpa.storage.db.core;

import com.nexsol.tpa.core.domain.insurer.Insurer;
import com.nexsol.tpa.core.domain.insurer.InsurerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class InsurerRepositoryImpl implements InsurerRepository {

    private final TravelInsurerJpaRepository travelInsurerJpaRepository;

    @Override
    public List<Insurer> findAllActive() {
        return travelInsurerJpaRepository.findByIsActiveTrue().stream().map(this::toDomain).toList();
    }

    private Insurer toDomain(TravelInsurerEntity entity) {
        return new Insurer(entity.getId(), entity.getInsurerCode(), entity.getInsurerName());
    }

}
