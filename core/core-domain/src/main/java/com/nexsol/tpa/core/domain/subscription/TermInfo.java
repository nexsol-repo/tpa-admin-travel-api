package com.nexsol.tpa.core.domain.subscription;

import com.nexsol.tpa.core.domain.contract.ContractMeta;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record TermInfo(LocalDateTime applicationDate, LocalDate startDate, LocalDate endDate) {
	public static TermInfo toTermInfo(ContractMeta meta) {
		return new TermInfo(meta.applicationDate(), meta.period().startDate(), meta.period().endDate());
	}
}