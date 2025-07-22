/**
 * 
 */
package com.ey.advisory.common;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.data.repositories.client.EwbRepository;
import com.ey.advisory.app.util.DataSecurityAttributeUtil;
import com.ey.advisory.common.GSTConstants.DataOriginTypeCodes;
import com.ey.advisory.common.GSTConstants.GSTReturns;
import com.ey.advisory.common.GSTConstants.ProcessingStatus;
import com.ey.advisory.core.dto.EInvoiceDocSearchReqDto;
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
 * @author Laxmi.Salukuti
 *
 */

@Component("EinvoiceDefaultOutwardDocSearchPredicateBuilder")
public class EinvoiceDefaultOutwardDocSearchPredicateBuilder
		implements EInvoiceOutwardDocSearchPredicateBuilder {

	@Autowired
	@Qualifier("batchSaveStatusRepository")
	private Gstr1BatchRepository gstr1BatchRepository;

	@Autowired
	@Qualifier("EwbRepository")
	private EwbRepository ewbRepository;

	public List<Predicate> build(EInvoiceDocSearchReqDto criteria,
			Root<OutwardTransDocument> root, CriteriaBuilder criteriaBuilder) {

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
		 * predicates.add(criteriaBuilder.equal(root.get("sgstin"),
		 * criteria.getGstins().get(0))); } else {
		 * predicates.add(root.get("sgstin").in(criteria.getGstins())); } }
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

		// ACK Date Format are stored in DB YYYY-mm-ddTHH:mm:ss
		/*
		 * if (criteria.getDateEACKFrom() != null && criteria.getDateEACKTo() !=
		 * null) {
		 * predicates.add(criteriaBuilder.between(root.get("createdDate"),
		 * criteria.getDateEACKFrom(), criteria.getDateEACKTo())); }
		 */
		if (criteria.getDateEACKFrom() != null
				&& criteria.getDateEACKTo() != null) {
			predicates.add(criteriaBuilder.between(root.get("irnDate"),
					criteria.getDateEACKFrom(), criteria.getDateEACKTo()));
		}

		if (criteria.getEwbNo() != null) {
			predicates.add(criteriaBuilder.equal(root.get("eWayBillNo"),
					criteria.getEwbNo()));
		}

		if (criteria.getEwbDate() != null) {
			predicates.add(criteriaBuilder.equal(root.get("eWayBillDate"),
					criteria.getEwbDate()));
		}

		// Get the documents based on selected GSTReturns Status
		// GSTReturns Status - NotApplicable,AspError,AspprocessedNotSaved,
		// AspprocessedSaved,GstnError
		if (criteria.getGstReturnsStatus() != null
				&& !criteria.getGstReturnsStatus().isEmpty()) {

			String notApplicable = GSTReturns.NOTAPPLICABLE
					.getGstnReturnStatus();
			String aspError = GSTReturns.ASPERROR.getGstnReturnStatus();
			String aspProcessedNotSaved = GSTReturns.ASP_P_NOT_SAVED_GSTN
					.getGstnReturnStatus();
			String aspProcessedSaved = GSTReturns.ASP_P_SAVED_GSTN
					.getGstnReturnStatus();
			String gstnError = GSTReturns.GSTNERROR.getGstnReturnStatus();

			criteria.getGstReturnsStatus().forEach(gstReturnStatus -> {
				if (notApplicable.equalsIgnoreCase(gstReturnStatus)) {

					Predicate notProcessed = criteriaBuilder
							.equal(root.get("isProcessed"), false);
					Predicate notError = criteriaBuilder
							.equal(root.get("isError"), false);
					predicates.add(criteriaBuilder.and(notProcessed, notError));
				}
				if (aspError.equalsIgnoreCase(gstReturnStatus)) {
					predicates.add(
							criteriaBuilder.equal(root.get("isError"), true));
				}
				if (aspProcessedNotSaved.equalsIgnoreCase(gstReturnStatus)) {

					Predicate isProcessed = criteriaBuilder
							.equal(root.get("isProcessed"), true);
					Predicate isSaved = criteriaBuilder
							.equal(root.get("isSaved"), false);
					predicates.add(criteriaBuilder.and(isProcessed, isSaved));
				}
				if (aspProcessedSaved.equalsIgnoreCase(gstReturnStatus)) {

					Predicate isProcessed = criteriaBuilder
							.equal(root.get("isProcessed"), true);
					Predicate notSaved = criteriaBuilder
							.equal(root.get("isSaved"), true);
					predicates.add(criteriaBuilder.and(isProcessed, notSaved));
				}
				if (gstnError.equalsIgnoreCase(gstReturnStatus)) {
					predicates.add(
							criteriaBuilder.equal(root.get("isGstnError"), true));
				}
			});
		}
		if (criteria.getEwbStatus() != null
				&& !criteria.getEwbStatus().isEmpty()) {
			predicates.add(root.get("ewbStatus").in(criteria.getEwbStatus()));
		}
		if (criteria.getEwbErrorPoint() != null
				&& !criteria.getEwbErrorPoint().isEmpty()) {
			predicates.add(root.get("ewbProcessingStatus")
					.in(criteria.getEwbErrorPoint()));
		}
		if (criteria.getSubSupplyType() != null
				&& !criteria.getSubSupplyType().isEmpty()) {
			predicates.add(
					root.get("subSupplyType").in(criteria.getSubSupplyType()));
		}
		if (criteria.getSupplyType() != null
				&& !criteria.getSupplyType().isEmpty()) {
			predicates.add(root.get("supplyType").in(criteria.getSupplyType()));
		}
		if (criteria.getPostingDate() != null) {
			predicates.add(criteriaBuilder.equal(root.get("glPostingDate"),
					criteria.getPostingDate()));
		}
		if (criteria.getTransporterID() != null) {
			predicates.add(criteriaBuilder.equal(root.get("transporterID"),
					criteria.getTransporterID()));
		}
		if (criteria.getTransType() != null) {
			
			predicates.add(criteriaBuilder.equal(root.get("transactionType"),
					criteria.getTransType()));
		}
		if (criteria.getCounterPartyGstins() != null
				&& criteria.getTransType() != null) {
			if (GSTConstants.O.equalsIgnoreCase(criteria.getTransType())) {
				if (GSTConstants.R
						.equalsIgnoreCase(criteria.getCounterPartyGstins())) {
					predicates.add(
							criteriaBuilder.notEqual(root.get("cgstin"), null));
				}
				if (GSTConstants.U
						.equalsIgnoreCase(criteria.getCounterPartyGstins())) {
					predicates.add(
							criteriaBuilder.equal(root.get("cgstin"), null));
				}
			}
			if (GSTConstants.I.equalsIgnoreCase(criteria.getTransType())) {
				if (GSTConstants.R
						.equalsIgnoreCase(criteria.getCounterPartyGstins())) {
					predicates.add(
							criteriaBuilder.notEqual(root.get("sgstin"), null));
				}
				if (GSTConstants.U
						.equalsIgnoreCase(criteria.getCounterPartyGstins())) {
					predicates.add(
							criteriaBuilder.equal(root.get("sgstin"), null));
				}
			}
		}

		// Get the documents based on selected GSTN Status
		// GSTN Status - Saved,NotSaved,Error,Submitted
		/*
		 * if (criteria.getGstnStatus() != null &&
		 * !criteria.getGstnStatus().isEmpty()) { //
		 * criteria.getGstnStatus().forEach(action); String saved =
		 * GSTNStatus.SAVED.getGstnStatus(); String notSaved =
		 * GSTNStatus.NOTSAVED.getGstnStatus(); String error =
		 * GSTNStatus.ERROR.getGstnStatus(); String submitted =
		 * GSTNStatus.SUBMITTED.getGstnStatus();
		 * 
		 * criteria.getGstnStatus().forEach(gstnStatus -> { if
		 * (saved.equalsIgnoreCase(gstnStatus)) { predicates.add(
		 * criteriaBuilder.equal(root.get("isSaved"), true)); } if
		 * (notSaved.equalsIgnoreCase(gstnStatus)) { predicates.add(
		 * criteriaBuilder.equal(root.get("isSaved"), false)); } if
		 * (error.equalsIgnoreCase(gstnStatus)) { predicates.add(
		 * criteriaBuilder.equal(root.get("isError"), true)); } if
		 * (submitted.equalsIgnoreCase(gstnStatus)) {
		 * predicates.add(criteriaBuilder .equal(root.get("isSubmitted"),
		 * true)); } }); }
		 */
		// if (criteria.isShowGstnData()) {
		/*
		 * if (criteria.getProcessingStatus() != null &&
		 * criteria.getProcessingStatus().length() > 0) { if
		 * (criteria.getProcessingStatus().equalsIgnoreCase(
		 * ProcessingStatus.PROCESSED.getStatus())) { // Get the records which
		 * are processed predicates.add(
		 * criteriaBuilder.equal(root.get("isSaved"), true)); } else if
		 * (criteria.getProcessingStatus()
		 * .equalsIgnoreCase(ProcessingStatus.ERROR.getStatus())) { // Get the
		 * records which have errors predicates.add(criteriaBuilder
		 * .equal(root.get("isGstnError"), true)); predicates.add(
		 * criteriaBuilder.equal(root.get("isSaved"), false)); } else {// Get
		 * the records which are Processed with Info Predicate isProcessed =
		 * criteriaBuilder .equal(root.get("isSaved"), true); Predicate isInfo =
		 * criteriaBuilder.equal(root.get("isInfo"), true);
		 * predicates.add(criteriaBuilder.and(isProcessed, isInfo)); } }
		 */
		if (criteria.isShowGstnData()) {
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

		if (criteria.getDocTypes() != null
				&& !criteria.getDocTypes().isEmpty()) {
			List<Predicate> docTypePredicatesList = new ArrayList<>();

			Predicate predicate = root.get("docType")
					.in(criteria.getDocTypes());
			docTypePredicatesList.add(predicate);
			predicates.add(criteriaBuilder.or(docTypePredicatesList
					.toArray(new Predicate[docTypePredicatesList.size()])));
		}

		/*
		 * if (criteria.getReturnTypes() != null &&
		 * !criteria.getReturnTypes().isEmpty()) { if
		 * (criteria.getReturnTypes().contains(GSTConstants.GSTR1)) { if
		 * (criteria.getDataCategoryList() != null &&
		 * !criteria.getDataCategoryList().isEmpty()) {
		 * predicates.add(root.get("gstnBifurcation")
		 * .in(criteria.getDataCategoryList())); } if
		 * (criteria.getTableNumberList() != null &&
		 * !criteria.getTableNumberList().isEmpty()) {
		 * predicates.add(root.get("tableType")
		 * .in(criteria.getTableNumberList())); }
		 * predicates.add(root.get("gstrReturnType")
		 * .in(criteria.getReturnTypes())); } else { if
		 * (criteria.getDataCategoryList() != null &&
		 * !criteria.getDataCategoryList().isEmpty()) {
		 * predicates.add(root.get("gstnBifurcationNew")
		 * .in(criteria.getDataCategoryList())); }
		 * 
		 * if (criteria.getTableNumberList() != null &&
		 * !criteria.getTableNumberList().isEmpty()) {
		 * predicates.add(root.get("tableTypeNew")
		 * .in(criteria.getTableNumberList())); } predicates.add(
		 * root.get("returnType").in(criteria.getReturnTypes())); } } else { if
		 * (criteria.getDataCategoryList() != null &&
		 * !criteria.getDataCategoryList().isEmpty()) {
		 * predicates.add(root.get("gstnBifurcation")
		 * .in(criteria.getDataCategoryList())); } if
		 * (criteria.getTableNumberList() != null &&
		 * !criteria.getTableNumberList().isEmpty()) {
		 * predicates.add(root.get("tableType")
		 * .in(criteria.getTableNumberList())); } }
		 */

		/**
		 * Start - Applicable Data Security Filter Attributes
		 */

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
		predicates.add(criteriaBuilder.and(
				predicatesList.toArray(new Predicate[predicatesList.size()])));

		/**
		 * End - Applicable Data Security Filter Attributes
		 */
		return predicates;
	}

}
