package com.ey.advisory.app.data.repositories.client;

import java.util.LinkedList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.common.GSTConstants.ProcessingStatus;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GstnOutwardTransDocSearchPredicateBuilder;
import com.ey.advisory.core.dto.GstnDocSearchReqDto;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Component("GstnDefaultOutwardTransDocSearchPredicateBuilder")
public class GstnDefaultOutwardTransDocSearchPredicateBuilder
		implements GstnOutwardTransDocSearchPredicateBuilder {

	@Override
	public List<Predicate> build(GstnDocSearchReqDto criteria,
			Root<OutwardTransDocument> root, CriteriaBuilder criteriaBuilder) {
		// TODO Auto-generated method stub

		List<Predicate> predicates = new LinkedList<>();

		if (criteria == null)
			return predicates;

		predicates.add(criteriaBuilder.equal(root.get("isDeleted"), false));

		// Apply the Supplier GSTIN condition. Perform optimization for a
		// single GSTIN search, in which case, use 'equals' operation in
		// query, rather than the 'IN' operation.
		if (criteria.getGstins() != null && !criteria.getGstins().isEmpty()) {
			if (criteria.getGstins().size() == 1) {
				predicates.add(criteriaBuilder.equal(root.get("sgstin"),
						criteria.getGstins().get(0)));
			} else {
				predicates.add(root.get("sgstin").in(criteria.getGstins()));
			}
		}

		// Document Date
		if (criteria.getDocFromDate() != null
				&& criteria.getDocToDate() != null) {
			predicates.add(criteriaBuilder.between(root.get("docDate"),
					criteria.getDocFromDate(), criteria.getDocToDate()));
		}

		// Data Received Date
		if (criteria.getReceivFromDate() != null
				&& criteria.getReceivToDate() != null) {
			predicates.add(criteriaBuilder.between(root.get("receivedDate"),
					criteria.getReceivFromDate(), criteria.getReceivToDate()));
		}

		// Doc No
		if (criteria.getDocNo() != null) {
			predicates.add(criteriaBuilder.equal(root.get("docNo"),
					(criteria.getDocNo())));
		}

		// Since tax period is generally stored as string in the format of
		// MMyyyy, we cannot use it to search using the between, '<' or '>'
		// operators in the DB. So, usually, we will store another calculated
		// field called derived tax period in the DB which will be an integer
		// and will have the format yyyyMM, so that this field can be used for
		// comparisons.
		if (criteria.getReturnFrom() != null
				&& criteria.getReturnTo() != null) {

			int stPeriod = GenUtil
					.convertTaxPeriodToInt(criteria.getReturnFrom());
			int endPeriod = GenUtil
					.convertTaxPeriodToInt(criteria.getReturnTo());

			predicates.add(criteriaBuilder.between(root.get("derivedTaxperiod"),
					stPeriod, endPeriod));
		}

		if (criteria.getProcessingStatus() != null
				&& criteria.getProcessingStatus().length() > 0) {
			if (criteria.getProcessingStatus()
					.equalsIgnoreCase(ProcessingStatus.PROCESSED.getStatus())) {
				// Get the records which are processed
				predicates.add(
						criteriaBuilder.equal(root.get("isSaved"), true));
				predicates.add(
						criteriaBuilder.equal(root.get("isError"), false));
			} 
			if (criteria.getProcessingStatus()
					.equalsIgnoreCase(ProcessingStatus.ERROR.getStatus())) {
				// Get the records which have errors
				predicates
						.add(criteriaBuilder.equal(root.get("isError"), true));
				predicates
				.add(criteriaBuilder.equal(root.get("isSent"), true));
			} 
		}

		return predicates;
	}
}