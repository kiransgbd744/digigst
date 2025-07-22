package com.ey.advisory.app.data.repositories.client;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.InwardTransDocument;
import com.ey.advisory.app.util.DataSecurityAttributeUtil;
import com.ey.advisory.common.GSTConstants.DataOriginTypeCodes;
import com.ey.advisory.common.GSTConstants.ProcessingStatus;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.DocSearchReqDto;
import com.ey.advisory.domain.client.Gstr1SaveBatchEntity;
import com.ey.advisory.repositories.client.Gstr1BatchRepository;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

/**
 * This class is responsible for building the criteria that can be used to
 * search OutwardTransDocument objects, based on the SearchCriteria provided.
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

@Component("DefaultInwardTransDocSearchPredicateBuilder")
public class DefaultInwardTransDocSearchPredicateBuilder
		implements InwardTransDocSearchPredicateBuilder {

	@Autowired
	@Qualifier("batchSaveStatusRepository")
	private Gstr1BatchRepository gstr1BatchRepository;

	// @Override
	public List<Predicate> build(DocSearchReqDto criteria,
			Root<InwardTransDocument> root, CriteriaBuilder criteriaBuilder) {

		List<Predicate> predicates = new LinkedList<>();

		if (criteria == null)
			return predicates;

		// This map contains all the security attribute values for the user
		// that is either passed from the UI or in case of screen load, the
		// default values.
		Map<String, List<String>> applicableAttrMap = criteria
				.getDataSecAttrs();

		if (applicableAttrMap == null || applicableAttrMap.isEmpty()) {
			return predicates;
		}

		predicates.add(criteriaBuilder.equal(root.get("isDeleted"), false));
		predicates.add(criteriaBuilder.equal(root.get("isSubmitted"), false));

		// Apply the Supplier GSTIN condition. Perform optimization for a
		// single GSTIN search, in which case, use 'equals' operation in
		// query, rather than the 'IN' operation.
		/*
		 * if (criteria.getGstins() != null && !criteria.getGstins().isEmpty())
		 * { if (criteria.getGstins().size() == 1) {
		 * predicates.add(criteriaBuilder.equal(root.get("cgstin"),
		 * criteria.getGstins().get(0))); } else { predicates.add(
		 * root.get("cgstin").in(criteria.getGstins())); } }
		 */

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

		// docKey
		if (criteria.getDocKey() != null) {
			predicates.add(criteriaBuilder.equal(root.get("docKey"),
					(criteria.getDocKey())));
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

		// Get the documents based on selected GSTN Status
		// GSTN Status - Saved,NotSaved,Error,Submitted
		/*
		 * if(criteria.getGstnStatus() != null &&
		 * !criteria.getGstnStatus().isEmpty()){
		 * //criteria.getGstnStatus().forEach(action); String saved =
		 * GSTNStatus.SAVED.getGstnStatus(); String notSaved =
		 * GSTNStatus.NOTSAVED.getGstnStatus(); String error =
		 * GSTNStatus.ERROR.getGstnStatus(); String submitted =
		 * GSTNStatus.SUBMITTED.getGstnStatus();
		 * 
		 * criteria.getGstnStatus().forEach(gstnStatus -> {
		 * if(saved.equalsIgnoreCase(gstnStatus)){ predicates.add(
		 * criteriaBuilder.equal(root.get("isSaved"), true)); }
		 * 
		 * if(notSaved.equalsIgnoreCase(gstnStatus)){ predicates.add(
		 * criteriaBuilder.equal(root.get("isSaved"), false)); }
		 * 
		 * if(error.equalsIgnoreCase(gstnStatus)){ predicates.add(
		 * criteriaBuilder.equal(root.get("isError"), true)); }
		 * 
		 * if(submitted.equalsIgnoreCase(gstnStatus)){
		 * predicates.add(criteriaBuilder .equal(root.get("isSubmitted"),
		 * true)); }
		 * 
		 * }); }
		 */

		if (criteria.isShowGstnData()) {
			if (criteria.getProcessingStatus() != null
					&& criteria.getProcessingStatus().length() > 0) {
				if (criteria.getProcessingStatus().equalsIgnoreCase(
						ProcessingStatus.PROCESSED.getStatus())) {
					// Get the records which are processed
					predicates.add(
							criteriaBuilder.equal(root.get("isSaved"), true));
				} else if (criteria.getProcessingStatus()
						.equalsIgnoreCase(ProcessingStatus.ERROR.getStatus())) {
					// Get the records which have errors
					predicates.add(criteriaBuilder
							.equal(root.get("isGstnError"), true));
				} else {// Get the records which are Processed with Info
					Predicate isProcessed = criteriaBuilder
							.equal(root.get("isSaved"), true);
					Predicate isInfo = criteriaBuilder.equal(root.get("isInfo"),
							true);
					predicates.add(criteriaBuilder.and(isProcessed, isInfo));
				}
			}
			/*
			 * if (criteria.getGstnBatchId() != null){// Reference Id
			 * predicates.add(criteriaBuilder.equal(root.get("gstnBatchId"),
			 * (criteria.getGstnBatchId()))); }
			 */
			if (criteria.getGstnRefId() != null) {// Reference Id
				List<Gstr1SaveBatchEntity> refDetails = gstr1BatchRepository
						.selectByrefId(criteria.getGstnRefId());
				if (refDetails != null) {
					for (Gstr1SaveBatchEntity ref : refDetails) {
						Long refId = ref.getId();
						predicates.add(criteriaBuilder
								.equal(root.get("gstnBatchId"), refId));
					}
				}
			}
		} else {
			if (criteria.getProcessingStatus() != null
					&& criteria.getProcessingStatus().length() > 0) {
				if (criteria.getProcessingStatus().equalsIgnoreCase(
						ProcessingStatus.PROCESSED.getStatus())) {
					// Get the records which are processed
					predicates.add(criteriaBuilder
							.equal(root.get("isProcessed"), true));
				} else if (criteria.getProcessingStatus()
						.equalsIgnoreCase(ProcessingStatus.ERROR.getStatus())) {
					// Get the records which have errors
					predicates.add(
							criteriaBuilder.equal(root.get("isError"), true));
				} else {// Get the records which are Processed with Info
					Predicate isProcessed = criteriaBuilder
							.equal(root.get("isProcessed"), true);
					Predicate isInfo = criteriaBuilder.equal(root.get("isInfo"),
							true);
					predicates.add(criteriaBuilder.and(isProcessed, isInfo));
				}
			}
		}

		if (criteria.getFileId() != null) {
			predicates.add(criteriaBuilder.equal(root.get("acceptanceId"),
					(criteria.getFileId())));
		}

		if (criteria.getDataOriginTypeCode() != null) {
			List<String> dataOrgTypCodeList = new ArrayList<>();
			if (DataOriginTypeCodes.ERP_API.getDataOriginTypeCode()
					.equalsIgnoreCase(criteria.getDataOriginTypeCode())) {
				dataOrgTypCodeList.add(
						DataOriginTypeCodes.ERP_API.getDataOriginTypeCode());
				dataOrgTypCodeList
						.add(DataOriginTypeCodes.ERP_API_INV_MANAGMENT_CORR
								.getDataOriginTypeCode());
			}

			if (DataOriginTypeCodes.EXCL_UPLOAD.getDataOriginTypeCode()
					.equalsIgnoreCase(criteria.getDataOriginTypeCode())) {
				dataOrgTypCodeList.add(DataOriginTypeCodes.EXCL_UPLOAD
						.getDataOriginTypeCode());
				dataOrgTypCodeList
						.add(DataOriginTypeCodes.EXCL_UPLOAD_INV_MANAGMENT_CORR
								.getDataOriginTypeCode());
			}
			if (!dataOrgTypCodeList.isEmpty()) {
				predicates.add(
						root.get("dataOriginTypeCode").in(dataOrgTypCodeList));
			}
		}

		if (criteria.getDataCategoryList() != null
				&& !criteria.getDataCategoryList().isEmpty()) {
			List<Predicate> dataCategoryPredicatesList = new ArrayList<>();

			Predicate predicate = root.get("gstnBifurcationNew")
					.in(criteria.getDataCategoryList());
			dataCategoryPredicatesList.add(predicate);
			predicates
					.add(criteriaBuilder.or(dataCategoryPredicatesList.toArray(
							new Predicate[dataCategoryPredicatesList.size()])));
		}

		if (criteria.getTableNumberList() != null
				&& !criteria.getTableNumberList().isEmpty()) {
			List<Predicate> tableNumPredicatesList = new ArrayList<>();

			Predicate predicate = root.get("tableTypeNew")
					.in(criteria.getTableNumberList());
			tableNumPredicatesList.add(predicate);
			predicates.add(criteriaBuilder.or(tableNumPredicatesList
					.toArray(new Predicate[tableNumPredicatesList.size()])));
		}

		if (criteria.getDocTypes() != null
				&& !criteria.getDocTypes().isEmpty()) {
			List<Predicate> docTypePredicatesList = new ArrayList<>();

			Predicate predicate = root.get("docType")
					.in(criteria.getDocTypes());
			docTypePredicatesList.add(predicate);
			predicates.add(criteriaBuilder.or(docTypePredicatesList
					.toArray(new Predicate[docTypePredicatesList.size()])));
		}

		if (criteria.getReturnTypes() != null
				&& !criteria.getReturnTypes().isEmpty()) {
			List<Predicate> returnTypePredicatesList = new ArrayList<>();

			Predicate predicate = root.get("returnType")
					.in(criteria.getReturnTypes());
			returnTypePredicatesList.add(predicate);
			predicates.add(criteriaBuilder.or(returnTypePredicatesList
					.toArray(new Predicate[returnTypePredicatesList.size()])));

		}

		/**
		 * Start - Applicable Data Security Filter Attributes
		 */

		Map<String, String> securityAtMap = DataSecurityAttributeUtil
				.getInwardSecurityAttributeMap();

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
		predicates.add(criteriaBuilder.and(
				predicatesList.toArray(new Predicate[predicatesList.size()])));

		/**
		 * End - Applicable Data Security Filter Attributes
		 */

		return predicates;
	}

}
