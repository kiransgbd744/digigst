/**
 * 
 */
package com.ey.advisory.app.services.search.docsearch;

import java.math.BigInteger;
import java.sql.Date;
import java.time.LocalDate;
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

import com.ey.advisory.app.docs.dto.InwardInvoiceFilterListingReqDto;
import com.ey.advisory.app.docs.dto.InwardInvoiceFilterListingResponseDto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.ErrorMasterUtil;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GSTConstants.DataOriginTypeCodes;
import com.ey.advisory.common.GSTConstants.GSTReturns;
import com.ey.advisory.common.GSTConstants.ProcessingStatus;
import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.domain.client.Gstr1SaveBatchEntity;
import com.ey.advisory.repositories.client.Gstr1BatchRepository;
import com.ey.advisory.app.docs.dto.InwardListingReqDto;
import com.ey.advisory.app.docs.dto.InwardListingResponseDto;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Laxmi.Salukuti
 *
 */
@Component("InwardListingManagementDaoImpl")
@Slf4j
public class InwardListingManagementDaoImpl
		implements InwardListingManagementDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("batchSaveStatusRepository")
	private Gstr1BatchRepository gstr1BatchRepository;

	@Override
	public List<InwardListingResponseDto> getInwardListing(
			InwardListingReqDto criteria, PageRequest pageReq) {

		InwardListingReqDto request = (InwardListingReqDto) criteria;

		LocalDate receiveFromDate = request.getReceivFromDate();
		LocalDate receiveToDate = request.getReceivToDate();
		String returnFrom = request.getReturnFrom();
		String returnTo = request.getReturnTo();
		LocalDate docFromDate = request.getDocFromDate();
		LocalDate docToDate = request.getDocToDate();
		List<String> docNoList = request.getDocNums();
		docNoList.replaceAll(String::toUpperCase);
		List<String> docType = request.getDocTypes();
		String counterPartyGstin = request.getCounterPartyGstins();
		String gstReturn = request.getGstReturn();
		Long refId = null;
		Long fileId = request.getFileId();
		List<String> supplyType = request.getSupplyType();
		LocalDate postingDate = request.getPostingDate();
		String suppGstin = request.getSuppGstin();
		List<String> accVoucherNo = request.getAccVoucherNum();
		accVoucherNo.replaceAll(String::toUpperCase);
		int pageN = pageReq.getPageNo();
		int pageSize = pageReq.getPageSize();
		int pageNo = pageN * pageSize;

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
		String purchase = null;
		String division = null;
		String location = null;
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
		List<String> purchaseList = null;
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
				if (key.equalsIgnoreCase(OnboardingConstant.PO)) {
					purchase = key;
					if (!dataSecAttrs.get(OnboardingConstant.PO).isEmpty()
							&& dataSecAttrs.get(OnboardingConstant.PO)
									.size() > 0) {
						purchaseList = dataSecAttrs.get(OnboardingConstant.PO);
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

		if (GSTIN != null && !GSTIN.isEmpty()) {
			if (gstinList != null && gstinList.size() > 0) {
				buildQuery.append(" AND HDR.CUST_GSTIN IN :gstinList");
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
		if (purchase != null && !purchase.isEmpty()) {
			if (purchaseList != null && purchaseList.size() > 0) {
				buildQuery.append(
						" AND HDR.PURCHASE_ORGANIZATION IN :purchaseList");
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
					buildQuery.append(" AND IS_PROCESSED = TRUE");
				} else if (criteria.getProcessingStatus()
						.equalsIgnoreCase(ProcessingStatus.ERROR.getStatus())) {
					// Get the records which have errors
					buildQuery.append(" AND IS_ERROR = TRUE");
				} else {// Get the records which are Processed with Info
					buildQuery.append(" AND IS_PROCESSED = TRUE");
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
				buildQuery.append(" AND IS_PROCESSED = TRUE");
				buildQuery.append(" AND IS_SAVED_TO_GSTN = FALSE");
			}
			if (aspProcessedSaved.equalsIgnoreCase(gstReturnStatus)) {
				buildQuery.append(" AND IS_PROCESSED = TRUE");
				buildQuery.append(" AND IS_SAVED_TO_GSTN = TRUE");
			}
			if (gstnError.equalsIgnoreCase(gstReturnStatus)) {
				buildQuery.append(" AND GSTN_ERROR = TRUE");
			}
		});
		if (counterPartyGstin != null) {
			if (GSTConstants.R.equalsIgnoreCase(counterPartyGstin)) {
				buildQuery.append(" AND LENGTH(HDR.SUPPLIER_GSTIN) = 15 ");
			}
			if (GSTConstants.U.equalsIgnoreCase(counterPartyGstin)) {
				buildQuery.append(" AND ( HDR.SUPPLIER_GSTIN IS NULL OR LENGTH(HDR.SUPPLIER_GSTIN) < 15 ) ");
			} 
		}
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
		if (docType != null && !docType.isEmpty()) {
			buildQuery.append(" AND HDR.DOC_TYPE IN :docType");
		}
		if (supplyType != null && !supplyType.isEmpty()) {
			buildQuery.append(" AND HDR.SUPPLY_TYPE IN :supplyType");
		}
		if (postingDate != null) {
			buildQuery.append(" AND HDR.POSTING_DATE = :postingDate");
		}
		if (gstReturn != null
				&& GSTConstants.GSTR2.equalsIgnoreCase(gstReturn)) {
			buildQuery.append(" AND GIN.REG_TYPE = 'REGULAR' ");
		} else if (gstReturn != null
				&& GSTConstants.GSTR6.equalsIgnoreCase(gstReturn)) {
			buildQuery.append(" AND GIN.REG_TYPE = 'ISD' ");
		}
		if (suppGstin != null && !suppGstin.isEmpty()) {
			buildQuery.append(" AND HDR.SUPPLIER_GSTIN like :suppGstin  ");
		}

		if (accVoucherNo != null && !accVoucherNo.isEmpty()) {
			if (accVoucherNo.size() > 1)
				buildQuery.append(
						" AND HDR.PURCHASE_VOUCHER_NUM IN :accVoucherNo ");
			else
				buildQuery.append(
						" AND HDR.PURCHASE_VOUCHER_NUM like :accVoucherNo  ");
		}

		String queryStr = createEInvdownloadQueryString(buildQuery.toString(),
				pageNo, pageSize);
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
		if (purchase != null && !purchase.isEmpty() && purchaseList != null
				&& !purchaseList.isEmpty()) {
			q.setParameter("purchaseList", purchaseList);
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
		if (refId != null) {
			q.setParameter("refId", refId);
		}
		if (fileId != null) {
			q.setParameter("fileId", fileId);
		}
		if (docType != null && !docType.isEmpty()) {
			q.setParameter("docType", docType);
		}
		if (postingDate != null) {
			q.setParameter("postingDate", postingDate);
		}
		if (supplyType != null && !supplyType.isEmpty()) {
			q.setParameter("supplyType", supplyType);
		}
		if (suppGstin != null && !suppGstin.isEmpty()) {
			q.setParameter("suppGstin", "%" + suppGstin + "%");
		}

		if (accVoucherNo != null && !accVoucherNo.isEmpty()) {
			if (accVoucherNo.size() > 1)
				q.setParameter("accVoucherNo", accVoucherNo);
			else
				q.setParameter("accVoucherNo", "%" + accVoucherNo.get(0) + "%");
		}
		List<Object[]> list = q.getResultList();
		return list.parallelStream().map(o -> convertInwwardListing(o))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	private InwardListingResponseDto convertInwwardListing(
			Object[] arr) {
		InwardListingResponseDto obj = new InwardListingResponseDto();
		String errDesc = null;
		BigInteger id = GenUtil.getBigInteger(arr[0]);
		obj.setId(id.longValue());
		obj.setGstin(arr[1] != null ? String.valueOf(arr[1]) : null);
		obj.setDocType(arr[2] != null ? String.valueOf(arr[2]) : null);
		obj.setDocNo(arr[3] != null ? String.valueOf(arr[3]) : null);
		if (arr[4] != null) {
			Date date = (Date) arr[4];
			obj.setDocDate(date.toLocalDate());
		} else { 
			obj.setDocDate(null);
		}
		obj.setCounterPartyGstin(
				arr[5] != null ? String.valueOf(arr[5]) : null);
		obj.setSupplierLegalName(
				arr[6] != null ? String.valueOf(arr[6]) : null);
		String errCode = arr[7] != null ? String.valueOf(arr[7]) : null;
		obj.setAspErrorCode(errCode);
		if (!Strings.isNullOrEmpty(errCode)) {
			String[] errorCodes = errCode.split(",");
			List<String> errCodeList = Arrays.asList(errorCodes);
			errDesc = ErrorMasterUtil.getErrorDesc(errCodeList, "INWARD");
		}
		obj.setAspErrorDesc(errDesc);
		obj.setIs240Format((boolean) arr[8]);
		obj.setDocKey(arr[9] != null ? String.valueOf(arr[9]) : null);
		obj.setGstReturnStatus(arr[10] != null ? String.valueOf(arr[10]) : null);
		obj.setReturnType(arr[11] != null ? String.valueOf(arr[11]) : null);

		String tableNo = (String) arr[12];
		String docCategory = (String) arr[13];
		String tableNumber = null;
		if ((tableNo != null && !tableNo.isEmpty())
				&& (docCategory != null && !docCategory.isEmpty())) {
			tableNumber = tableNo.concat("-").concat(docCategory);
		}
		obj.setTableNumber(
				tableNumber != null ? String.valueOf(tableNumber) : null);
		obj.setGstnErrorCode(arr[14] != null ? String.valueOf(arr[14]) : null);
		obj.setGstnErrorDesc(arr[15] != null ? String.valueOf(arr[15]) : null);
		obj.setAccVoucherNo(arr[16] != null ? String.valueOf(arr[16]) : null);

		return obj;
	}

	private static String createEInvdownloadQueryString(String buildQuery, int pageNo,
			int pageSize) {

		return "SELECT DISTINCT HDR.ID,HDR.CUST_GSTIN,HDR.DOC_TYPE,"
				+ "HDR.DOC_NUM,HDR.DOC_DATE,HDR.SUPPLIER_GSTIN,HDR.CUST_SUPP_NAME, "
				+ "TRIM(', ' FROM IFNULL(HDR.ERROR_CODES,'')||','|| "
				+ "IFNULL(ERR_ITM_AGG.AGG,''))  AS ERROR_CODE_ASP, "
				+ "HDR.IS_240_FORMAT,HDR.DOC_KEY, case when "
				+ "HDR.IS_SAVED_TO_GSTN = TRUE THEN 'IS_SAVED' WHEN "
				+ "HDR.IS_SAVED_TO_GSTN = FALSE AND HDR.GSTN_ERROR = TRUE THEN "
				+ "'IS_ERROR' WHEN HDR.IS_SAVED_TO_GSTN = FALSE AND "
				+ "HDR.GSTN_ERROR = FALSE THEN 'NOT_SAVED' END AS "
				+ "RETURN_SAVE_STATUS,HDR.AN_RETURN_TYPE,HDR.AN_TABLE_SECTION,"
				+ "HDR.AN_TAX_DOC_TYPE,HDR.GSTN_ERROR_CODE,HDR.GSTN_ERROR_DESC ,"
				+ "HDR.PURCHASE_VOUCHER_NUM "
				+ "FROM ANX_INWARD_DOC_HEADER HDR INNER JOIN "
				+ "ANX_INWARD_DOC_ITEM ITM ON HDR.ID = ITM.DOC_HEADER_ID "
				+ "AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD "
				+ "INNER JOIN (SELECT DOC_HEADER_ID, STRING_AGG(ERROR_CODES,',') "
				+ "AS agg FROM ANX_INWARD_DOC_ITEM GROUP BY DOC_HEADER_ID) "
				+ "AS ERR_ITM_AGG ON ITM.DOC_HEADER_ID = "
				+ "ERR_ITM_AGG.DOC_HEADER_ID INNER JOIN GSTIN_INFO GIN "
				+ "ON HDR.CUST_GSTIN = GIN.GSTIN WHERE HDR.IS_DELETE = FALSE "
				+ "AND IS_SUBMITTED = FALSE " + buildQuery
				+ " ORDER BY ID DESC " + "limit " + pageSize + " offset "
				+ pageNo;
	}
	
	public static void main(String[] args) {
		StringBuilder buildQuery = new StringBuilder();

	
		
			buildQuery.append(" AND HDR.RECEIVED_DATE BETWEEN ");
			buildQuery.append(":receiveFromDate AND :receiveToDate");
	
		System.out.println(createEInvdownloadQueryString(buildQuery.toString(), 1,10));;
	}
}
