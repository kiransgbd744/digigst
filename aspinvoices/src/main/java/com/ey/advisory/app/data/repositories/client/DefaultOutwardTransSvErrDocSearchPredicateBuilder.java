package com.ey.advisory.app.data.repositories.client;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.Anx1OutWardErrHeader;
import com.ey.advisory.app.util.DataSecurityAttributeUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.DocSearchReqDto;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

/**
 * This class is responsible for building the criteria that can be used to 
 * search Structurally Validated Error OutwardTransDocument objects, 
 * based on the SearchCriteria provided.
 * Since there can be multiple null/empty fields in the search criteria, it is
 * highly inefficient to provide several combinations of repository query 
 * methods to cover all scenarios. Instead, this class uses the JPA crieria
 * builder class to build dynamic predicates based on the usable fields in the
 * input criteria. The return predicate list will be used by the repository 
 * method to implement the search.
 * 
 * @author Mohana.Dasari
 *
 */
@Component("DefaultOutwardTransSvErrDocSearchPredicateBuilder")
public class DefaultOutwardTransSvErrDocSearchPredicateBuilder
		implements OutwardTransSvErrDocSearchPredicateBuilder {

	@Override
	public List<Predicate> build(DocSearchReqDto criteria,
			Root<Anx1OutWardErrHeader> root,
			CriteriaBuilder criteriaBuilder) {
		List<Predicate> predicates = new LinkedList<>();
		if (criteria == null) return predicates;
		
		predicates.add(criteriaBuilder.equal(root.get("isDeleted"),"false"));
		
		Expression<String> empty = criteriaBuilder.literal("");
		//Select Records where Doc Type is not null and not empty
		Predicate docTypeNotNullPredicate = criteriaBuilder
				.isNotNull(root.get("docType"));
		Predicate docTypeNotEmptyPredicate = 
				criteriaBuilder
				.notLike(criteriaBuilder.trim(root.get("docType")), empty);
		predicates.add(criteriaBuilder.and(docTypeNotEmptyPredicate,
				docTypeNotNullPredicate));
		
		//Select Records where Doc Date is not null and not empty
		Predicate docDateNotNullPredicate = criteriaBuilder
				.isNotNull(root.get("docDate"));
		Predicate docDateNotEmptyPredicate = 
				criteriaBuilder
				.notLike(criteriaBuilder.trim(root.get("docDate")), empty);
		predicates.add(criteriaBuilder.and(docDateNotNullPredicate,
				docDateNotEmptyPredicate));
		
		//Select Records where Supplier GSTIN is not null and not empty
		Predicate sgstinNotNullPredicate = criteriaBuilder
				.isNotNull(root.get("sgstin"));
		Predicate sgstinNotEmptyPredicate = criteriaBuilder.notLike(
				criteriaBuilder.trim(root.get("sgstin")), empty);
		predicates.add(criteriaBuilder.and(sgstinNotNullPredicate,
				sgstinNotEmptyPredicate));
		
		//Select Records where Document Number is not null and not empty
		Predicate docNumNotNullPredicate = criteriaBuilder
				.isNotNull(root.get("docNo"));
		Predicate docNumNotEmptyPredicate = criteriaBuilder.notLike(
				criteriaBuilder.trim(root.get("docNo")), empty);
		predicates.add(criteriaBuilder.and(docNumNotNullPredicate,
				docNumNotEmptyPredicate));
		
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
		// and will have the format yyyyMMdd, so that this field can be used for
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
		
		if (criteria.getFileId() != null) {
			predicates.add(criteriaBuilder.equal(root.get("acceptanceId"),
					(criteria.getFileId())));
		}
		
		if(criteria.getDocTypes() != null && !criteria.getDocTypes().isEmpty()){
			List<Predicate> docTypePredicatesList = new ArrayList<>();
			Predicate predicate = root.get("docType")
					.in(criteria.getDocTypes());
			docTypePredicatesList.add(predicate);
			predicates
					.add(criteriaBuilder.or(docTypePredicatesList.toArray(
							new Predicate[docTypePredicatesList.size()])));
		}
		
		/**
		 * Start - Applicable Data Security Filter Attributes
		 */
		
		// This map contains  all the security attribute values for the user
		// that is either passed from the UI or in case of screen load, the
		// default values.		
		Map<String, List<String>> applicableAttrMap = criteria
				.getDataSecAttrs();
		Map<String, String> securityAtMap = DataSecurityAttributeUtil
				.getOutwardSecurityAttributeMap();
		
		List<Predicate> predicatesList = new ArrayList<>();
		applicableAttrMap.forEach((attrCode, attrValList) -> {
			if (securityAtMap.containsKey(attrCode)) {
				if (attrValList != null && !attrValList.isEmpty()) {
					Predicate predicate = root.get(securityAtMap.get(attrCode))
							.in(attrValList);
					predicatesList.add(predicate);
				}
			}
		});
		predicates.add(criteriaBuilder.and(predicatesList
				.toArray(new Predicate[predicatesList.size()])));
		
		/**
		 * End - Applicable Data Security Filter Attributes
		 */
		
		return predicates;
	}

}
