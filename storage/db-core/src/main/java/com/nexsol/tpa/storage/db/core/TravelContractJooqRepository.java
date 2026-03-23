package com.nexsol.tpa.storage.db.core;

import com.nexsol.tpa.core.domain.contract.ContractSearchCriteria;
import com.nexsol.tpa.core.enums.ContractStatus;
import com.nexsol.tpa.core.support.SortPage;
import lombok.RequiredArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.SortField;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

@Repository
@RequiredArgsConstructor
public class TravelContractJooqRepository {

	private static final org.jooq.Table<?> CONTRACT = table("travel_contract");

	private static final org.jooq.Table<?> PAYMENT = table("travel_payment");

	private static final org.jooq.Table<?> INSURED = table("travel_insured");

	private final DSLContext dsl;

	public int count(ContractSearchCriteria criteria) {
		return dsl.fetchCount(dsl.selectFrom(CONTRACT).where(buildCondition(criteria)));
	}

	public List<Long> findIds(ContractSearchCriteria criteria, SortPage sortPage) {
		return dsl.select(field("id", Long.class))
			.from(CONTRACT)
			.where(buildCondition(criteria))
			.orderBy(buildSortField(sortPage))
			.limit(sortPage.size())
			.offset(sortPage.page() * sortPage.size())
			.fetchInto(Long.class);
	}

	public List<Long> findIds(ContractSearchCriteria criteria) {
		return dsl.select(field("id", Long.class))
			.from(CONTRACT)
			.where(buildCondition(criteria))
			.orderBy(field("id").desc())
			.fetchInto(Long.class);
	}

	private Condition buildCondition(ContractSearchCriteria criteria) {
		List<Condition> conditions = new ArrayList<>();

		conditions.add(field("deleted_at").isNull());

		if (criteria.startDate() != null) {
			conditions.add(field("apply_date").greaterOrEqual(criteria.startDate().atStartOfDay()));
		}
		if (criteria.endDate() != null) {
			conditions.add(field("apply_date").lessThan(criteria.endDate().plusDays(1).atStartOfDay()));
		}
		if (StringUtils.hasText(criteria.partnerName())) {
			conditions.add(field("partner_name").eq(criteria.partnerName()));
		}
		if (StringUtils.hasText(criteria.channelName())) {
			conditions.add(field("channel_name").eq(criteria.channelName()));
		}
		if (StringUtils.hasText(criteria.insurerName())) {
			conditions.add(field("insurer_name").eq(criteria.insurerName()));
		}

		conditions.add(buildStatusCondition(criteria.status()));

		if (StringUtils.hasText(criteria.applicantName())) {
			conditions.add(DSL.exists(dsl.selectOne()
				.from(INSURED)
				.where(field("travel_insured.contract_id").eq(field("travel_contract.id")),
						field("travel_insured.is_contractor").eq(1), field("travel_insured.deleted_at").isNull(),
						field("travel_insured.name").like("%" + criteria.applicantName() + "%"))));
		}

		return DSL.and(conditions);
	}

	private Condition buildStatusCondition(ContractStatus status) {
		Condition isCompleted = field("status").eq("COMPLETED");
		Condition hasCanceledPayment = DSL.exists(dsl.selectOne()
			.from(PAYMENT)
			.where(field("travel_payment.contract_id").eq(field("travel_contract.id")),
					field("travel_payment.status").eq("CANCELED")));
		LocalDate today = LocalDate.now();
		Condition isEndDatePassed = field("insure_end_date").lessThan(today);
		Condition isEndDateNotPassed = field("insure_end_date").greaterOrEqual(today);

		if (status == null) {
			return isCompleted;
		}

		return switch (status) {
			case COMPLETED -> DSL.and(isCompleted, DSL.not(hasCanceledPayment), isEndDateNotPassed);
			case CANCELED -> DSL.and(isCompleted, hasCanceledPayment);
			case EXPIRED -> DSL.and(isCompleted, DSL.not(hasCanceledPayment), isEndDatePassed);
			default -> field("status").eq(status.name());
		};
	}

	private SortField<?> buildSortField(SortPage sortPage) {
		if (!StringUtils.hasText(sortPage.sortBy())) {
			return field("id").desc();
		}
		String column = mapSortColumn(sortPage.sortBy());
		return (sortPage.direction() != null && sortPage.direction().isAscending()) ? field(column).asc()
				: field(column).desc();
	}

	private String mapSortColumn(String requestProperty) {
		return switch (requestProperty) {
			case "startDate", "insuranceStartDate" -> "insure_start_date";
			case "endDate", "insuranceEndDate" -> "insure_end_date";
			case "applicationDate" -> "apply_date";
			default -> requestProperty;
		};
	}

}