/**
 * 
 */
package com.ey.advisory.app.services.search.docsearch;

import java.math.BigInteger;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.client.EwbRepository;
import com.ey.advisory.app.docs.dto.EInvoiceDocumentDto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.AspInvoiceStatus;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.ErrorMasterUtil;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.GSTConstants.DataOriginTypeCodes;
import com.ey.advisory.common.GSTConstants.GSTReturns;
import com.ey.advisory.common.GSTConstants.ProcessingStatus;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.EInvoiceDocSearchReqDto;
import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.domain.client.Gstr1SaveBatchEntity;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;
import com.ey.advisory.repositories.client.Gstr1BatchRepository;

import com.google.common.base.Strings;

/**
 * @author Laxmi.Salukuti
 *
 */
@Component("EinvoiceManagementDaoImpl")
public class EinvoiceManagementDaoImpl implements EinvoiceManagementDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("batchSaveStatusRepository")
	private Gstr1BatchRepository gstr1BatchRepository;

	@Autowired
	@Qualifier("EwbRepository")
	private EwbRepository ewbRepository;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	GSTNAuthTokenService gSTNAuthTokenService;

	static String transType = null;

	@Override
	public List<EInvoiceDocumentDto> getEInvMngmtListing(
			EInvoiceDocSearchReqDto criteria, PageRequest pageReq) {

		EInvoiceDocSearchReqDto request = (EInvoiceDocSearchReqDto) criteria;

		LocalDate receiveFromDate = request.getReceivFromDate();
		LocalDate receiveToDate = request.getReceivToDate();
		String returnFrom = request.getReturnFrom();
		String returnTo = request.getReturnTo();
		LocalDate docFromDate = request.getDocFromDate();
		LocalDate docToDate = request.getDocToDate();
		List<String> docNoList = request.getDocNums();
		docNoList.replaceAll(String::toUpperCase);
		List<String> docType = criteria.getDocTypes();
		Long ewbNo = request.getEwbNo();
		Long refId = null;
		LocalDate validUpto = request.getEwbValidUpto() != null
				? request.getEwbValidUpto().toLocalDate() : null;
		Long fileId = request.getFileId();
		List<Long> ewbStatus = request.getEwbStatus();
		List<Long> einvStatus = request.getEinvStatus();
		List<Long> ewbErrorPoint = request.getEwbErrorPoint();
		List<String> subSupplyType = request.getSubSupplyType();
		List<String> supplyType = request.getSupplyType();
		String transporterId = request.getTransporterID();
		LocalDate postingDate = request.getPostingDate();
		transType = request.getTransType();
		String counterPartyGstin = request.getCounterPartyGstins();
		String irnNum = request.getIrnNum();
		String returnType = request.getType();
		int pageN = pageReq.getPageNo();
		int pageSize = pageReq.getPageSize();
		int pageNo = pageN * pageSize;

		Integer processedStatus = AspInvoiceStatus.ASP_PROCESSED
				.getAspInvoiceStatusCode();
		Integer errorStatus = AspInvoiceStatus.ASP_ERROR
				.getAspInvoiceStatusCode();

		// Get the documents based on selected GSTReturns Status
		// GSTReturns Status - NotApplicable,AspError,AspprocessedNotSaved,
		// AspprocessedSaved,GstnError
		String notApplicable = GSTReturns.NOTAPPLICABLE.getGstnReturnStatus();
		String aspError = GSTReturns.ASPERROR.getGstnReturnStatus();
		String aspProcessedNotSaved = GSTReturns.ASP_P_NOT_SAVED_GSTN
				.getGstnReturnStatus();
		String aspProcessedSaved = GSTReturns.ASP_P_SAVED_GSTN
				.getGstnReturnStatus();
		String gstnError = GSTReturns.GSTNERROR.getGstnReturnStatus();

		Map<String, List<String>> dataSecAttrs = request.getDataSecAttrs();

		String ProfitCenter = null;
		String plant = null;
		String sales = null;
		String division = null;
		String location = null;
		String distChannel = null;
		String ud1 = null;
		String ud2 = null;
		String ud3 = null;
		String ud4 = null;
		String ud5 = null;
		String ud6 = null;
		String GSTIN = null;

		List<String> pcList = null;
		List<String> plantList = null;
		List<String> divisionList = null;
		List<String> locationList = null;
		List<String> salesList = null;
		List<String> distList = null;
		List<String> ud1List = null;
		List<String> ud2List = null;
		List<String> ud3List = null;
		List<String> ud4List = null;
		List<String> ud5List = null;
		List<String> ud6List = null;
		List<String> gstinList = null;

		if (!dataSecAttrs.isEmpty()) {
			for (String key : dataSecAttrs.keySet()) {

				if (key.equalsIgnoreCase(OnboardingConstant.GSTIN)) {
					GSTIN = key;
					if (!dataSecAttrs.get(OnboardingConstant.GSTIN).isEmpty()
							&& dataSecAttrs.get(OnboardingConstant.GSTIN)
									.size() > 0) {
						gstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.PC)) {
					ProfitCenter = key;
					if (!dataSecAttrs.get(OnboardingConstant.PC).isEmpty()
							&& dataSecAttrs.get(OnboardingConstant.PC)
									.size() > 0) {
						pcList = dataSecAttrs.get(OnboardingConstant.PC);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.PLANT)) {

					plant = key;
					if (!dataSecAttrs.get(OnboardingConstant.PLANT).isEmpty()
							&& dataSecAttrs.get(OnboardingConstant.PLANT)
									.size() > 0) {
						plantList = dataSecAttrs.get(OnboardingConstant.PLANT);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.DIVISION)) {
					division = key;
					if (!dataSecAttrs.get(OnboardingConstant.DIVISION).isEmpty()
							&& dataSecAttrs.get(OnboardingConstant.DIVISION)
									.size() > 0) {
						divisionList = dataSecAttrs
								.get(OnboardingConstant.DIVISION);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.LOCATION)) {
					location = key;
					if (!dataSecAttrs.get(OnboardingConstant.LOCATION).isEmpty()
							&& dataSecAttrs.get(OnboardingConstant.LOCATION)
									.size() > 0) {
						locationList = dataSecAttrs
								.get(OnboardingConstant.LOCATION);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.SO)) {
					sales = key;
					if (!dataSecAttrs.get(OnboardingConstant.SO).isEmpty()
							&& dataSecAttrs.get(OnboardingConstant.SO)
									.size() > 0) {
						salesList = dataSecAttrs.get(OnboardingConstant.SO);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.DC)) {
					distChannel = key;
					if (dataSecAttrs.get(OnboardingConstant.DC) != null
							&& dataSecAttrs.get(OnboardingConstant.DC)
									.size() > 0) {
						distList = dataSecAttrs.get(OnboardingConstant.DC);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD1)) {
					ud1 = key;
					if (dataSecAttrs.get(OnboardingConstant.UD1) != null
							&& dataSecAttrs.get(OnboardingConstant.UD1)
									.size() > 0) {
						ud1List = dataSecAttrs.get(OnboardingConstant.UD1);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD2)) {
					ud2 = key;
					if (dataSecAttrs.get(OnboardingConstant.UD2) != null
							&& dataSecAttrs.get(OnboardingConstant.UD2)
									.size() > 0) {
						ud2List = dataSecAttrs.get(OnboardingConstant.UD2);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD3)) {
					ud3 = key;
					if (dataSecAttrs.get(OnboardingConstant.UD3) != null
							&& dataSecAttrs.get(OnboardingConstant.UD3)
									.size() > 0) {
						ud3List = dataSecAttrs.get(OnboardingConstant.UD3);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD4)) {
					ud4 = key;
					if (dataSecAttrs.get(OnboardingConstant.UD4) != null
							&& dataSecAttrs.get(OnboardingConstant.UD4)
									.size() > 0) {
						ud4List = dataSecAttrs.get(OnboardingConstant.UD4);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD5)) {
					ud5 = key;
					if (dataSecAttrs.get(OnboardingConstant.UD5) != null
							&& dataSecAttrs.get(OnboardingConstant.UD5)
									.size() > 0) {
						ud5List = dataSecAttrs.get(OnboardingConstant.UD5);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD6)) {
					ud6 = key;
					if (dataSecAttrs.get(OnboardingConstant.UD6) != null
							&& dataSecAttrs.get(OnboardingConstant.UD6)
									.size() > 0) {
						ud6List = dataSecAttrs.get(OnboardingConstant.UD6);
					}
				}
			}
		}
		StringBuilder buildQuery = new StringBuilder();

		if ((transType != null && !transType.isEmpty())
				&& (GSTIN != null && !GSTIN.isEmpty())) {
			if (GSTConstants.O.equalsIgnoreCase(transType)) {
				if (gstinList != null && gstinList.size() > 0) {
					buildQuery.append(" AND HDR.SUPPLIER_GSTIN IN :gstinList");
				}
			}
			if (GSTConstants.I.equalsIgnoreCase(transType)) {
				if (gstinList != null && gstinList.size() > 0) {
					buildQuery.append(" AND HDR.CUST_GSTIN IN :gstinList");
				}
			}
		}
		if (ProfitCenter != null && !ProfitCenter.isEmpty()) {
			if (pcList != null && pcList.size() > 0) {
				buildQuery.append(" AND HDR.PROFIT_CENTRE IN :pcList");
			}
		}
		if (plant != null && !plant.isEmpty()) {
			if (plantList != null && plantList.size() > 0) {
				buildQuery.append(" AND HDR.PLANT_CODE IN :plantList");
			}
		}
		if (sales != null && !sales.isEmpty()) {
			if (salesList != null && salesList.size() > 0) {
				buildQuery.append(" AND HDR.SALES_ORGANIZATION IN :salesList");
			}
		}
		if (distChannel != null && !distChannel.isEmpty()) {
			if (distList != null && distList.size() > 0) {
				buildQuery.append(" AND HDR.DISTRIBUTION_CHANNEL IN :distList");
			}
		}
		if (division != null && !division.isEmpty()) {
			if (divisionList != null && divisionList.size() > 0) {
				buildQuery.append(" AND HDR.DIVISION IN :divisionList");
			}
		}
		if (location != null && !location.isEmpty()) {
			if (locationList != null && locationList.size() > 0) {
				buildQuery.append(" AND HDR.LOCATION IN :locationList");
			}
		}
		if (ud1 != null && !ud1.isEmpty()) {
			if (ud1List != null && ud1List.size() > 0) {
				buildQuery.append(" AND HDR.USERACCESS1 IN :ud1List");
			}
		}
		if (ud2 != null && !ud2.isEmpty()) {
			if (ud2List != null && ud2List.size() > 0) {
				buildQuery.append(" AND HDR.USERACCESS2 IN :ud2List");
			}
		}
		if (ud3 != null && !ud3.isEmpty()) {
			if (ud3List != null && ud3List.size() > 0) {
				buildQuery.append(" AND HDR.USERACCESS3 IN :ud3List");
			}
		}
		if (ud4 != null && !ud4.isEmpty()) {
			if (ud4List != null && ud4List.size() > 0) {
				buildQuery.append(" AND HDR.USERACCESS4 IN :ud4List");
			}
		}
		if (ud5 != null && !ud5.isEmpty()) {
			if (ud5List != null && ud5List.size() > 0) {
				buildQuery.append(" AND HDR.USERACCESS5 IN :ud5List");
			}
		}
		if (ud6 != null && !ud6.isEmpty()) {
			if (ud6List != null && ud6List.size() > 0) {
				buildQuery.append(" AND HDR.USERACCESS6 IN :ud6List");
			}
		}

		if (receiveFromDate != null && receiveToDate != null) {
			buildQuery.append(" AND HDR.RECEIVED_DATE BETWEEN ");
			buildQuery.append(":receiveFromDate AND :receiveToDate");
		}
		if (docFromDate != null && docToDate != null) {
			buildQuery.append(" AND HDR.DOC_DATE BETWEEN :docFromDate "
					+ "AND :docToDate");
		}
		if (returnFrom != null && returnTo != null) {
			buildQuery.append(" AND HDR.DERIVED_RET_PERIOD BETWEEN ");
			buildQuery.append(":returnFrom AND :returnTo");
			/*
			 * buildQuery.append(" AND ITM.DERIVED_RET_PERIOD BETWEEN ");
			 * buildQuery.append(":returnFrom AND :returnTo");
			 */
		}
		if (docNoList != null && !docNoList.isEmpty()) {
			if (docNoList.size() > 1)
				buildQuery.append(" AND HDR.DOC_NUM IN :docNo ");
			else
				buildQuery.append(" AND HDR.DOC_NUM LIKE :docNo ");
		}

		if (irnNum != null && !irnNum.isEmpty()) {
			buildQuery.append(
					" AND (HDR.IRN = :irnNum OR HDR.IRN_RESPONSE = :irnNum) ");
		}

		if (criteria.getEwbValidUpto() != null) { // validUpto

			buildQuery.append(" AND EWB.VALID_UPTO BETWEEN (:startValidUpto) "
					+ "AND (:endValidUpto)");
		}
		if (criteria.isShowGstnData()) {
			if (criteria.getGstnRefId() != null) {// Reference Id
				List<Gstr1SaveBatchEntity> refDetails = gstr1BatchRepository
						.selectByrefId(criteria.getGstnRefId());
				if (!refDetails.isEmpty()) {
					for (Gstr1SaveBatchEntity ref : refDetails) {
						refId = ref.getId();
						buildQuery.append(" AND HDR.BATCH_ID = :refId");
					}
				}
			}
		} else {
			if (criteria.getProcessingStatus() != null
					&& criteria.getProcessingStatus().length() > 0) {
				if (criteria.getProcessingStatus().equalsIgnoreCase(
						ProcessingStatus.PROCESSED.getStatus())) {
					// Get the records which are processed
					buildQuery.append(
							" AND ASP_INVOICE_STATUS = :processedStatus");

				} else if (criteria.getProcessingStatus()
						.equalsIgnoreCase(ProcessingStatus.ERROR.getStatus())) {
					// Get the records which have errors
					buildQuery.append(" AND ASP_INVOICE_STATUS = :errorStatus");

				} else {// Get the records which are Processed with Info
					buildQuery.append(
							" AND ASP_INVOICE_STATUS = :processedStatus");
					buildQuery.append(" AND IS_INFORMATION = TRUE");
				}
			}
		}
		criteria.getGstReturnsStatus().forEach(gstReturnStatus -> {
			if (notApplicable.equalsIgnoreCase(gstReturnStatus)) {
				buildQuery.append(" AND IS_PROCESSED = FALSE");
				buildQuery.append(" AND IS_ERROR = FALSE");
			}
			if (aspError.equalsIgnoreCase(gstReturnStatus)) {
				buildQuery.append(" AND IS_ERROR = TRUE");
			}
			if (aspProcessedNotSaved.equalsIgnoreCase(gstReturnStatus)) {
				buildQuery.append(" AND ASP_INVOICE_STATUS = 2");
				buildQuery.append(" AND IS_SAVED_TO_GSTN = FALSE");
				// buildQuery.append(" AND COMPLIANCE_APPLICABLE = TRUE");
			}
			if (aspProcessedSaved.equalsIgnoreCase(gstReturnStatus)) {
				buildQuery.append(" AND ASP_INVOICE_STATUS = 2");
				buildQuery.append(" AND IS_SAVED_TO_GSTN = TRUE");
				buildQuery.append(" AND COMPLIANCE_APPLICABLE = TRUE");
			}
			if (gstnError.equalsIgnoreCase(gstReturnStatus)) {
				buildQuery.append(" AND GSTN_ERROR = TRUE");
			}
		});
		if (fileId != null) {
			buildQuery.append(" AND HDR.FILE_ID = :fileId");
		}
		if (criteria.getDataOriginTypeCode() != null) {
			if (DataOriginTypeCodes.ERP_API.getDataOriginTypeCode()
					.equalsIgnoreCase(criteria.getDataOriginTypeCode())) {
				buildQuery.append(" AND HDR.DATAORIGINTYPECODE IN ('A','AI')");
			}
			if (DataOriginTypeCodes.EXCL_UPLOAD.getDataOriginTypeCode()
					.equalsIgnoreCase(criteria.getDataOriginTypeCode())) {
				buildQuery.append(" AND HDR.DATAORIGINTYPECODE IN ('E','EI')");
			}
		}
		if (counterPartyGstin != null && transType != null) {
			if (GSTConstants.O.equalsIgnoreCase(transType)) {
				if (GSTConstants.R.equalsIgnoreCase(counterPartyGstin)) {
					buildQuery.append(" AND HDR.CUST_GSTIN IS NOT NULL AND HDR.CUST_GSTIN <> 'URP' ");
				}
				if (GSTConstants.U.equalsIgnoreCase(counterPartyGstin)) {
					buildQuery.append(" AND ( HDR.CUST_GSTIN IS NULL OR HDR.CUST_GSTIN = 'URP' ) ");
				}
			}
			if (GSTConstants.I.equalsIgnoreCase(transType)) {
				if (GSTConstants.R.equalsIgnoreCase(counterPartyGstin)) {
					buildQuery.append(" AND HDR.SUPPLIER_GSTIN IS NOT NULL AND HDR.SUPPLIER_GSTIN <> 'URP' ");
				}
				if (GSTConstants.U.equalsIgnoreCase(counterPartyGstin)) {
					buildQuery.append(" AND ( HDR.SUPPLIER_GSTIN IS NULL OR HDR.SUPPLIER_GSTIN = 'URP' ) ");
				}
			}
		}
		if (docType != null && !docType.isEmpty()) {
			buildQuery.append(" AND HDR.DOC_TYPE IN :docType");
		}
		if (ewbNo != null) {
			buildQuery.append(" AND HDR.EWB_NO_RESP = :ewbNo");
		}
		if (ewbStatus != null && !ewbStatus.isEmpty()) {
			buildQuery.append(" AND HDR.EWB_STATUS IN :ewbStatus");
		}
		if (einvStatus != null && !einvStatus.isEmpty()) {
			buildQuery.append(" AND HDR.IRN_STATUS IN :einvStatus");
		}
		if (ewbErrorPoint != null && !ewbErrorPoint.isEmpty()) {
			buildQuery
					.append(" AND HDR.EWB_PROCESSING_STATUS IN :ewbErrorPoint");
		}
		if (subSupplyType != null && !subSupplyType.isEmpty()) {
			buildQuery.append(" AND HDR.SUB_SUPP_TYPE IN :subSupplyType");
		}
		if (supplyType != null && !supplyType.isEmpty()) {
			buildQuery.append(" AND HDR.SUPPLY_TYPE IN :supplyType");
		}
		if (transporterId != null) {

			buildQuery.append(
					" AND (HDR.TRANSPORTER_ID  = :transporterId OR EWB.TRANSPORTER_ID = :transporterId) ");
		}
		if (postingDate != null) {
			buildQuery.append(" AND HDR.GL_POSTING_DATE = :postingDate");
		}
		if (transType != null && !transType.isEmpty()) {
			buildQuery.append(
					" AND (UPPER(HDR.TRANS_TYPE) = UPPER(:transType) OR HDR.TRANS_TYPE IS NULL)");
		}

		String queryStr = createEInvdownloadQueryString(buildQuery.toString(),
				pageNo, pageSize,returnType);
		Query q = entityManager.createNativeQuery(queryStr);

		if (GSTIN != null && !GSTIN.isEmpty()) {
			if (gstinList != null && !gstinList.isEmpty()
					&& gstinList.size() > 0) {
				q.setParameter("gstinList", gstinList);
			}
		}
		if (ProfitCenter != null && !ProfitCenter.isEmpty()) {
			if (pcList != null && !pcList.isEmpty() && pcList.size() > 0) {
				q.setParameter("pcList", pcList);
			}
		}
		if (plant != null && !plant.isEmpty()) {
			if (plantList != null && !plantList.isEmpty()
					&& plantList.size() > 0) {
				q.setParameter("plantList", plantList);
			}
		}
		if (sales != null && !sales.isEmpty()) {
			if (salesList != null && !salesList.isEmpty()
					&& salesList.size() > 0) {
				q.setParameter("salesList", salesList);
			}
		}
		if (division != null && !division.isEmpty()) {
			if (divisionList != null && !divisionList.isEmpty()
					&& divisionList.size() > 0) {
				q.setParameter("divisionList", divisionList);
			}
		}
		if (location != null && !location.isEmpty()) {
			if (locationList != null && !locationList.isEmpty()
					&& locationList.size() > 0) {
				q.setParameter("locationList", locationList);
			}
		}
		if (distChannel != null && !distChannel.isEmpty()) {
			if (distList != null && !distList.isEmpty()
					&& distList.size() > 0) {
				q.setParameter("distList", distList);
			}
		}
		if (ud1 != null && !ud1.isEmpty()) {
			if (ud1List != null && !ud1List.isEmpty() && ud1List.size() > 0) {
				q.setParameter("ud1List", ud1List);
			}
		}
		if (ud2 != null && !ud2.isEmpty()) {
			if (ud2List != null && !ud2List.isEmpty() && ud2List.size() > 0) {
				q.setParameter("ud2List", ud2List);
			}
		}
		if (ud3 != null && !ud3.isEmpty()) {
			if (ud3List != null && !ud3List.isEmpty() && ud3List.size() > 0) {
				q.setParameter("ud3List", ud3List);
			}
		}
		if (ud4 != null && !ud4.isEmpty()) {
			if (ud4List != null && !ud4List.isEmpty() && ud4List.size() > 0) {
				q.setParameter("ud4List", ud4List);
			}
		}
		if (ud5 != null && !ud5.isEmpty()) {
			if (ud5List != null && !ud5List.isEmpty() && ud5List.size() > 0) {
				q.setParameter("ud5List", ud5List);
			}
		}
		if (ud6 != null && !ud6.isEmpty()) {
			if (ud6List != null && !ud6List.isEmpty() && ud6List.size() > 0) {
				q.setParameter("ud6List", ud6List);
			}
		}
		if (returnFrom != null && returnTo != null) {
			int derivedRetPeriodFrom = GenUtil
					.convertTaxPeriodToInt(request.getReturnFrom());
			int derivedRetPeriodTo = GenUtil
					.convertTaxPeriodToInt(request.getReturnTo());
			q.setParameter("returnFrom", derivedRetPeriodFrom);
			q.setParameter("returnTo", derivedRetPeriodTo);
		}
		if (receiveFromDate != null && receiveToDate != null) {
			q.setParameter("receiveFromDate", receiveFromDate);
			q.setParameter("receiveToDate", receiveToDate);
		}
		if (docFromDate != null && docToDate != null) {
			q.setParameter("docFromDate", docFromDate);
			q.setParameter("docToDate", docToDate);
		}
		if (docNoList != null && !docNoList.isEmpty()) {
			if (docNoList.size() > 1)
				q.setParameter("docNo", docNoList);
			else
				q.setParameter("docNo", "%" + docNoList.get(0) + "%");
		}
		if (irnNum != null) {
			q.setParameter("irnNum", irnNum);
		}
		if (refId != null) {
			q.setParameter("refId", refId);
		}
		if (fileId != null) {
			q.setParameter("fileId", fileId);
		}
		if (docType != null && !docType.isEmpty()) {
			q.setParameter("docType", docType);
		}
		if (ewbNo != null) {
			q.setParameter("ewbNo", ewbNo);
		}
		if (validUpto != null) {
			q.setParameter("startValidUpto",
					EYDateUtil.toUTCDateTimeFromIST(validUpto.atStartOfDay()));
			q.setParameter("endValidUpto", EYDateUtil
					.toUTCDateTimeFromIST(validUpto.atTime(23, 59, 59)));
		}
		if (ewbStatus != null && !ewbStatus.isEmpty()) {
			q.setParameter("ewbStatus", ewbStatus);
		}
		if (einvStatus != null && !einvStatus.isEmpty()) {
			q.setParameter("einvStatus", einvStatus);
		}
		if (ewbErrorPoint != null && !ewbErrorPoint.isEmpty()) {
			q.setParameter("ewbErrorPoint", ewbErrorPoint);
		}
		if (subSupplyType != null && !subSupplyType.isEmpty()) {
			q.setParameter("subSupplyType", subSupplyType);
		}
		if (supplyType != null && !supplyType.isEmpty()) {
			q.setParameter("supplyType", supplyType);
		}
		if (transporterId != null) {
			q.setParameter("transporterId", transporterId);
		}
		if (postingDate != null) {
			q.setParameter("postingDate", postingDate);
		}
		if (transType != null) {
			q.setParameter("transType", transType);
		}

		if (criteria.getProcessingStatus() != null
				&& criteria.getProcessingStatus().length() > 0) {
			if (criteria.getProcessingStatus()
					.equalsIgnoreCase(ProcessingStatus.PROCESSED.getStatus())) {
				q.setParameter("processedStatus", processedStatus);
			} else if (criteria.getProcessingStatus()
					.equalsIgnoreCase(ProcessingStatus.ERROR.getStatus())) {
				q.setParameter("errorStatus", errorStatus);
			} else {
				q.setParameter("processedStatus", processedStatus);
			}
		}
		List<Object[]> list = q.getResultList();
		return list.parallelStream().map(o -> convertEInv(o))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	private EInvoiceDocumentDto convertEInv(Object[] arr) {
		EInvoiceDocumentDto obj = new EInvoiceDocumentDto();
		String errDesc = null;

		BigInteger id = GenUtil.getBigInteger(arr[0]);
		obj.setId(id.longValue());
		if ((transType != null)
				&& (GSTConstants.O.equalsIgnoreCase(transType))) {
			obj.setGstin(arr[1] != null ? String.valueOf(arr[1]) : null);
		} else {
			obj.setGstin(arr[5] != null ? String.valueOf(arr[5]) : null);
		}
		obj.setDocType(arr[2] != null ? String.valueOf(arr[2]) : null);
		obj.setDocNo(arr[3] != null ? String.valueOf(arr[3]) : null);
		if (arr[4] != null) {
			Date date = (Date) arr[4];
			obj.setDocDate(date.toLocalDate());
		} else {
			obj.setDocDate(null);
		}
		if ((transType != null)
				&& (GSTConstants.O.equalsIgnoreCase(transType))) {
			obj.setCounterPartyGstin(
					arr[5] != null ? String.valueOf(arr[5]) : null);
		} else {
			obj.setCounterPartyGstin(
					arr[1] != null ? String.valueOf(arr[1]) : null);
		}
		String errCode = arr[6] != null ? String.valueOf(arr[6]) : null;
		obj.setAspErrorCode(errCode);
		if (!Strings.isNullOrEmpty(errCode)) {
			String[] errorCodes = errCode.split(",");
			List<String> errCodeList = Arrays.asList(errorCodes);
			errDesc = ErrorMasterUtil.getErrorDesc(errCodeList, "OUTWARD");
		}
		obj.setAspErrorDesc(errDesc);
		obj.setEWBPartAStatus((Integer) arr[8]);
		obj.setEWBPartANo(arr[9] != null ? String.valueOf(arr[9]) : null);
		if (arr[10] != null) {
			Timestamp timeStamp = (Timestamp) arr[10];
			LocalDateTime localDT = timeStamp.toLocalDateTime();
			obj.setEWBPartADate(localDT);
		}
		obj.setEWBPartATransId(
				arr[11] != null ? String.valueOf(arr[11]) : null);
		if (arr[12] != null) {
			Timestamp timeStamp1 = (Timestamp) arr[12];
			LocalDateTime localDT1 = timeStamp1.toLocalDateTime();
			obj.setEwbValidUpto(localDT1);
		}
		obj.setEwbSubSupplyType(
				arr[13] != null ? String.valueOf(arr[13]) : null);
		obj.setEwbErrorPoint((Integer) arr[14]);
		obj.setEwbErrorCode(arr[15] != null ? String.valueOf(arr[15]) : null);
		obj.setEwbErrorDesc(arr[16] != null ? String.valueOf(arr[16]) : null);

		Boolean complianceapplicableDb = (Boolean) arr[38];
		// Long complianceapplicable = complianceapplicableDb.longValue();
		String gstnSaveStatus = arr[17] != null ? String.valueOf(arr[17])
				: null;
		if (!complianceapplicableDb) {
			obj.setGstStatus("Not Applicable");
		} else {
			obj.setGstStatus(gstnSaveStatus);
		}

		obj.setGstRetType(arr[18] != null ? String.valueOf(arr[18]) : null);
		String tableNo = (String) arr[19];
		String docCategory = (String) arr[20];
		String tableNumber = null;
		if ((tableNo != null && !tableNo.isEmpty())
				&& (docCategory != null && !docCategory.isEmpty())) {
			tableNumber = tableNo.concat("-").concat(docCategory);
		}
		obj.setGstTableNo(
				tableNumber != null ? String.valueOf(tableNumber) : null);
		obj.setGstErrorCode(arr[21] != null ? String.valueOf(arr[21]) : null);
		obj.setGstErrorDesc(arr[22] != null ? String.valueOf(arr[22]) : null);
		obj.setEinvStatus((Integer) arr[23]);
		obj.setIrnNum(arr[24] != null ? String.valueOf(arr[24]) : null);
		if (arr[25] != null) {
			Timestamp timeStamp1 = (Timestamp) arr[25];
			LocalDateTime localDT1 = timeStamp1.toLocalDateTime();
			obj.setIrnDate(localDT1);
		} else {
			obj.setIrnDate(null);
		}
		obj.setInfoErrCode(arr[26] != null ? String.valueOf(arr[26]) : null);
		obj.setInfoErrDesc(arr[27] != null ? String.valueOf(arr[27]) : null);
		Integer irnStatus = (Integer) arr[28];
		LocalDateTime ackDate = null;
		if (arr[32] != null) {
			Timestamp timeStamp1 = (Timestamp) arr[32];
			ackDate = timeStamp1.toLocalDateTime();
		}
		if (irnStatus != null && irnStatus == 5) {
			if (CommonUtility.deriveEinvStatus(ackDate)) {
				obj.setIrnStatus(14); // Generated – Available for cancellation
			} else {
				obj.setIrnStatus(irnStatus);
			}
		} else {
			obj.setIrnStatus(irnStatus);
		}
		obj.setIrnResponse(arr[29] != null ? String.valueOf(arr[29]) : null);
		obj.setEwbNoresp(arr[30] != null ? String.valueOf(arr[30]) : null);
		if (arr[31] != null) {
			Timestamp timeStamp1 = (Timestamp) arr[31];
			LocalDateTime ewbDateResp = timeStamp1.toLocalDateTime();
			obj.setEwbDateResp(ewbDateResp);
		} else {
			obj.setEwbDateResp(null);
		}
		obj.setAckDate(ackDate);
		obj.setAckNum(arr[33] != null ? String.valueOf(arr[33]) : null);
		obj.setEinvErrorCode(arr[34] != null ? String.valueOf(arr[34]) : null);
		obj.setEinvErrorDesc(arr[35] != null ? String.valueOf(arr[35]) : null);
		obj.setTaxDocType(arr[36] != null ? String.valueOf(arr[36]) : null);
		obj.setSupplyType(arr[37] != null ? String.valueOf(arr[37]) : null);
		if (Strings.isNullOrEmpty(obj.getTaxDocType())
				&& obj.getSupplyType().equalsIgnoreCase("CAN")) {
			obj.setTaxDocType("CAN");
		}
		obj.setDocKey(arr[39] != null ? String.valueOf(arr[39]) : null);
		obj.setRetPeriod(arr[40] != null ? String.valueOf(arr[40]) : null);
		obj.setRefId(arr[41] != null ? String.valueOf(arr[41]) : null);
		
		Boolean multiSupplyTypeApplicable = (Boolean) arr[43];
		if (multiSupplyTypeApplicable) {
			obj.setMultiSupplyApplicable(true);
		}else{
			obj.setMultiSupplyApplicable(false);
		}

		return obj;
	}

	private String createEInvdownloadQueryString(String buildQuery, int pageNo,
			int pageSize, String returnType) {

		if (returnType != null && returnType.equalsIgnoreCase("Gstr1")) {
			return "SELECT distinct HDR.ID AS ID_0,HDR.SUPPLIER_GSTIN AS GSTIN_1, HDR.DOC_TYPE "
					+ "AS DOC_TYPE_2, HDR.DOC_NUM AS DOC_NUM_3, HDR.DOC_DATE AS "
					+ "DOC_DATE_4, HDR.CUST_GSTIN AS CUST_GSTIN_5, "
					+ "TRIM(', ' FROM IFNULL(HDR.ERROR_CODES,''||','|| "
					+ "IFNULL(ERR_ITM_AGG.AGG,''))) AS ERROR_CODE_ASP_6,"
					+ "NULL AS ERROR_DESCRIPTION_ASP_7,"
					+ "HDR.EWB_STATUS AS EWB_STATUS_8, HDR.EWAY_BILL_NUM AS EWB_NUM_9, "
					+ "HDR.EWAY_BILL_DATE AS EWB_DATE_10, IFNULL(EWB.TRANSPORTER_ID,"
					+ "HDR.TRANSPORTER_ID) AS TRANSPORTER_ID_11, EWB.VALID_UPTO AS "
					+ "VALID_UPTO_12, HDR.SUB_SUPP_TYPE AS SBU_SYPPLY_TYPE_13, CASE "
					+ "WHEN HDR.EWB_PROCESSING_STATUS IN (2,5,9,12,15,18) THEN "
					+ "EWB_PROCESSING_STATUS END ERROR_POINT_14, CASE WHEN "
					+ "HDR.EWB_PROCESSING_STATUS IN (5,9) THEN HDR.EWB_ERROR_CODE ELSE "
					+ "EWBL.ERROR_CODE END ERROR_CODE_EWB_15, CASE WHEN "
					+ "HDR.EWB_PROCESSING_STATUS IN (5,9) THEN HDR.EWB_ERROR_DESC ELSE "
					+ "EWBL.ERROR_DESC END ERROR_DESC_EWB_16, case when "
					+ "HDR.IS_SAVED_TO_GSTN = TRUE THEN 'IS_SAVED' WHEN "
					+ "HDR.IS_SAVED_TO_GSTN = FALSE AND HDR.GSTN_ERROR = TRUE THEN "
					+ "'IS_ERROR' WHEN HDR.IS_SAVED_TO_GSTN = FALSE AND "
					+ "HDR.GSTN_ERROR = FALSE THEN 'NOT_SAVED' END AS "
					+ "RETURN_SAVE_STATUS_17,CASE WHEN ASP_INVOICE_STATUS = 2 THEN "
					+ "HDR.RETURN_TYPE WHEN ASP_INVOICE_STATUS = 1 THEN '' END AS "
					+ "RETURN_TYPE_18,CASE WHEN ASP_INVOICE_STATUS = 2 THEN "
					+ "HDR.TABLE_SECTION WHEN ASP_INVOICE_STATUS = 1 THEN '' END "
					+ "AS TABLE_NUMBER_19, CASE WHEN ASP_INVOICE_STATUS = 2 THEN "
					+ "HDR.TAX_DOC_TYPE WHEN ASP_INVOICE_STATUS = 1 THEN ''END AS "
					+ "DATA_CATEGORY_20, HDR.GSTN_ERROR_CODE AS GSTIN_ERROR_CODE_21,"
					+ "HDR.GSTN_ERROR_DESC AS GSTIN_ERROR_DESCRIPTION_ASP_22,HDR.EINV_STATUS AS "
					+ "EINV_STATUS_23,HDR.IRN AS IRN_24,HDR.IRN_DATE AS IRN_DATE_25,"
					+ "HDR.INFO_ERROR_CODE AS INFO_ERROR_CODE_26,"
					+ "HDR.INFO_ERROR_MSG AS INFO_ERROR_MSG_27,HDR.IRN_STATUS AS "
					+ "IRN_STATUS_28,CASE WHEN HDR.IRN_RESPONSE IS NULL THEN"
					+ " HDR.IRN ELSE HDR.IRN_RESPONSE END IRN_RESPONSE_29,"
					+ "CASE WHEN TO_CHAR (HDR.EWB_NO_RESP) IS NULL THEN "
					+ "TO_CHAR(HDR.EWAY_BILL_NUM) ELSE TO_CHAR(HDR.EWB_NO_RESP) END "
					+ "EWB_NO_RESP_30,CASE WHEN HDR.EWB_DATE_RESP "
					+ "IS NULL THEN HDR.EWAY_BILL_DATE ELSE HDR.EWB_DATE_RESP END "
					+ "EWB_DATE_RESP_31,CASE WHEN HDR.ACK_DATE IS NULL THEN "
					+ "HDR.IRN_DATE ELSE HDR.ACK_DATE END ACK_DATE_32,"
					+ "HDR.ACK_NUM AS ACK_NUM_33, HDR.EINV_ERROR_CODE AS "
					+ "EINV_ERROR_CODE_34, HDR.EINV_ERROR_DESC AS EINV_ERROR_DESC_35,"
					+ "HDR.TAX_DOC_TYPE AS TAX_DOC_TYPE_36,HDR.SUPPLY_TYPE AS "
					+ "SUPPLY_TYPE_37,HDR.COMPLIANCE_APPLICABLE AS COMPLIANCE_APPLICABLE_38,"
					+ "HDR.DOC_KEY AS DOC_KEY_39,HDR.RETURN_PERIOD,HDR.GSTN_SAVE_REF_ID, HDR.CREATED_ON, HDR.IS_MULTI_SUPP_INV "
					+ "FROM ANX_OUTWARD_DOC_HEADER HDR LEFT OUTER JOIN "
					+ "EWB_MASTER EWB ON HDR.EWB_NO_RESP = EWB.EWB_NUM LEFT OUTER JOIN "
					+ "EINV_MASTER EINV ON HDR.IRN_RESPONSE = EINV.IRN INNER JOIN "
					+ "ANX_OUTWARD_DOC_ITEM ITM ON HDR.ID = ITM.DOC_HEADER_ID AND "
					+ "HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD INNER JOIN "
					+ "(SELECT DOC_HEADER_ID, STRING_AGG(ERROR_CODES,',') "
					+ "AS agg FROM ANX_OUTWARD_DOC_ITEM GROUP BY "
					+ "DOC_HEADER_ID) AS ERR_ITM_AGG ON ITM.DOC_HEADER_ID = "
					+ "ERR_ITM_AGG.DOC_HEADER_ID LEFT OUTER JOIN EWB_LIFECYCLE EWBL "
					+ "ON EWB.EWB_LIFECYCLE_ID = EWBL.ID AND EWBL.ERROR_CODE IS NOT NULL "
					+ "WHERE HDR.IS_DELETE = FALSE AND IS_SUBMITTED = FALSE "
					+ buildQuery + " ORDER BY HDR.CREATED_ON DESC limit "
					+ pageSize + " offset " + pageNo;
		} else {
			return "SELECT distinct HDR.ID AS ID_0,HDR.SUPPLIER_GSTIN AS GSTIN_1, HDR.DOC_TYPE "
					+ "AS DOC_TYPE_2, HDR.DOC_NUM AS DOC_NUM_3, HDR.DOC_DATE AS "
					+ "DOC_DATE_4, HDR.CUST_GSTIN AS CUST_GSTIN_5, "
					+ "TRIM(', ' FROM IFNULL(HDR.ERROR_CODES,''||','|| "
					+ "IFNULL(ERR_ITM_AGG.AGG,''))) AS ERROR_CODE_ASP_6,"
					+ "NULL AS ERROR_DESCRIPTION_ASP_7,"
					+ "HDR.EWB_STATUS AS EWB_STATUS_8, HDR.EWAY_BILL_NUM AS EWB_NUM_9, "
					+ "HDR.EWAY_BILL_DATE AS EWB_DATE_10, IFNULL(EWB.TRANSPORTER_ID,"
					+ "HDR.TRANSPORTER_ID) AS TRANSPORTER_ID_11, EWB.VALID_UPTO AS "
					+ "VALID_UPTO_12, HDR.SUB_SUPP_TYPE AS SBU_SYPPLY_TYPE_13, CASE "
					+ "WHEN HDR.EWB_PROCESSING_STATUS IN (2,5,9,12,15,18) THEN "
					+ "EWB_PROCESSING_STATUS END ERROR_POINT_14, CASE WHEN "
					+ "HDR.EWB_PROCESSING_STATUS IN (5,9) THEN HDR.EWB_ERROR_CODE ELSE "
					+ "EWBL.ERROR_CODE END ERROR_CODE_EWB_15, CASE WHEN "
					+ "HDR.EWB_PROCESSING_STATUS IN (5,9) THEN HDR.EWB_ERROR_DESC ELSE "
					+ "EWBL.ERROR_DESC END ERROR_DESC_EWB_16, case when "
					+ "HDR.IS_SAVED_TO_GSTN = TRUE THEN 'IS_SAVED' WHEN "
					+ "HDR.IS_SAVED_TO_GSTN = FALSE AND HDR.GSTN_ERROR = TRUE THEN "
					+ "'IS_ERROR' WHEN HDR.IS_SAVED_TO_GSTN = FALSE AND "
					+ "HDR.GSTN_ERROR = FALSE THEN 'NOT_SAVED' END AS "
					+ "RETURN_SAVE_STATUS_17,CASE WHEN ASP_INVOICE_STATUS = 2 THEN "
					+ "HDR.RETURN_TYPE WHEN ASP_INVOICE_STATUS = 1 THEN '' END AS "
					+ "RETURN_TYPE_18,CASE WHEN ASP_INVOICE_STATUS = 2 THEN "
					+ "HDR.TABLE_SECTION WHEN ASP_INVOICE_STATUS = 1 THEN '' END "
					+ "AS TABLE_NUMBER_19, CASE WHEN ASP_INVOICE_STATUS = 2 THEN "
					+ "HDR.TAX_DOC_TYPE WHEN ASP_INVOICE_STATUS = 1 THEN ''END AS "
					+ "DATA_CATEGORY_20, HDR.GSTN_ERROR_CODE AS GSTIN_ERROR_CODE_21,"
					+ "HDR.GSTN_ERROR_DESC AS GSTIN_ERROR_DESCRIPTION_ASP_22,HDR.EINV_STATUS AS "
					+ "EINV_STATUS_23,HDR.IRN AS IRN_24,HDR.IRN_DATE AS IRN_DATE_25,"
					+ "HDR.INFO_ERROR_CODE AS INFO_ERROR_CODE_26,"
					+ "HDR.INFO_ERROR_MSG AS INFO_ERROR_MSG_27,HDR.IRN_STATUS AS "
					+ "IRN_STATUS_28,CASE WHEN HDR.IRN_RESPONSE IS NULL THEN"
					+ " HDR.IRN ELSE HDR.IRN_RESPONSE END IRN_RESPONSE_29,"
					+ "CASE WHEN TO_CHAR (HDR.EWB_NO_RESP) IS NULL THEN "
					+ "TO_CHAR(HDR.EWAY_BILL_NUM) ELSE TO_CHAR(HDR.EWB_NO_RESP) END "
					+ "EWB_NO_RESP_30,CASE WHEN HDR.EWB_DATE_RESP "
					+ "IS NULL THEN HDR.EWAY_BILL_DATE ELSE HDR.EWB_DATE_RESP END "
					+ "EWB_DATE_RESP_31,CASE WHEN HDR.ACK_DATE IS NULL THEN "
					+ "HDR.IRN_DATE ELSE HDR.ACK_DATE END ACK_DATE_32,"
					+ "HDR.ACK_NUM AS ACK_NUM_33, HDR.EINV_ERROR_CODE AS "
					+ "EINV_ERROR_CODE_34, HDR.EINV_ERROR_DESC AS EINV_ERROR_DESC_35,"
					+ "HDR.TAX_DOC_TYPE AS TAX_DOC_TYPE_36,HDR.SUPPLY_TYPE AS "
					+ "SUPPLY_TYPE_37,HDR.COMPLIANCE_APPLICABLE AS COMPLIANCE_APPLICABLE_38,"
					+ "HDR.DOC_KEY AS DOC_KEY_39,HDR.RETURN_PERIOD,HDR.GSTN_SAVE_REF_ID, HDR.CREATED_ON, HDR.IS_MULTI_SUPP_INV "
					+ "FROM ANX_OUTWARD_DOC_HEADER_1A HDR LEFT OUTER JOIN "
					+ "EWB_MASTER EWB ON HDR.EWB_NO_RESP = EWB.EWB_NUM LEFT OUTER JOIN "
					+ "EINV_MASTER EINV ON HDR.IRN_RESPONSE = EINV.IRN INNER JOIN "
					+ "ANX_OUTWARD_DOC_ITEM_1A ITM ON HDR.ID = ITM.DOC_HEADER_ID AND "
					+ "HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD INNER JOIN "
					+ "(SELECT DOC_HEADER_ID, STRING_AGG(ERROR_CODES,',') "
					+ "AS agg FROM ANX_OUTWARD_DOC_ITEM_1A GROUP BY "
					+ "DOC_HEADER_ID) AS ERR_ITM_AGG ON ITM.DOC_HEADER_ID = "
					+ "ERR_ITM_AGG.DOC_HEADER_ID LEFT OUTER JOIN EWB_LIFECYCLE EWBL "
					+ "ON EWB.EWB_LIFECYCLE_ID = EWBL.ID AND EWBL.ERROR_CODE IS NOT NULL "
					+ "WHERE HDR.IS_DELETE = FALSE AND IS_SUBMITTED = FALSE "
					+ buildQuery + " ORDER BY HDR.CREATED_ON DESC limit "
					+ pageSize + " offset " + pageNo;

		}
	}

}
