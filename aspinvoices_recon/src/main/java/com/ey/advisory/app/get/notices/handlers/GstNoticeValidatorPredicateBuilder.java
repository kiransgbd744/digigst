package com.ey.advisory.app.get.notices.handlers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.asprecon.TblGetNoticesEntity;
import com.ey.advisory.common.GenUtil;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("GstNoticeValidatorPredicateBuilder")
public class GstNoticeValidatorPredicateBuilder {

	public List<Predicate> buildAsycnExecJobSearchPredicates(
			GstnNoticeReqDto searchParams, Root<TblGetNoticesEntity> root,
			CriteriaBuilder criteriaBuilder) {

		List<Predicate> predicates = new LinkedList<>();

		if (searchParams == null) {
			LOGGER.debug("Gst Notice params are blank");
			return predicates;
		}

		// Gstin
		if (searchParams.getGstins() != null
				&& !searchParams.getGstins().isEmpty()) {

			if (searchParams.getGstins().size() == 1) {
				predicates.add(criteriaBuilder.equal(root.get("gstin"),
						searchParams.getGstins().get(0)));
			} else {
				predicates.add(root.get("gstin").in(searchParams.getGstins()));
			}
		}
		
		predicates.add(criteriaBuilder.equal(root.get("isDelete"), false));
		
		if ("Tax Period".equalsIgnoreCase(searchParams.getSelectionCriteria()))

		{// tax period range

			if (searchParams.getFromTaxPeriod() != null
					&& !searchParams.getFromTaxPeriod().isEmpty()
					&& searchParams.getToTaxPeriod() != null
					&& !searchParams.getToTaxPeriod().isEmpty()) {

				predicates.add(criteriaBuilder.greaterThanOrEqualTo(
						root.get("derivedFromTaxPeriod"),
						GenUtil.getDerivedTaxPeriod(searchParams.getFromTaxPeriod())));

				predicates.add(criteriaBuilder.lessThanOrEqualTo(
						root.get("derivedToTaxPeriod"),
						GenUtil.getDerivedTaxPeriod(searchParams.getToTaxPeriod())));

			}

		} else {
			// date range

			if (searchParams.getFromDate() != null
					&& !searchParams.getFromDate().isEmpty()
					&& searchParams.getToDate() != null
					&& !searchParams.getToDate().isEmpty()) {

				DateTimeFormatter formatter = DateTimeFormatter
						.ofPattern("dd-MM-yyyy");

				LocalDate fromDateTime = LocalDate
						.parse(searchParams.getFromDate(), formatter);
					// Start of the day for fromDate

				LocalDate toDateTime = LocalDate
						.parse(searchParams.getToDate(), formatter);
	
				predicates.add(criteriaBuilder.between(root.get("dateOfIssue"),
						fromDateTime, toDateTime));
			}

		}

		return predicates;
	}

}
