/**
 * 
 */
package com.ey.advisory.app.services.reports;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.views.client.Gstr1ProcessedReviewSummDto;
import com.ey.advisory.app.docs.dto.einvoice.DataStatusEinvoiceDto;
import com.ey.advisory.app.downloadreports.DownloadReportsConstant;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.ErrorMasterUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;
import com.ey.advisory.core.search.SearchCriteria;

import com.google.common.base.Strings;

/**
 * @author Laxmi.Salukuti
 *
 */

@Component("Gstr1RSHsnRateLevelProcessedReportsDaoImpl")
public class Gstr1RSHsnRateLevelProcessedReportsDaoImpl
		implements Gstr1ReviewSummaryReportsDao {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr1RSHsnRateLevelProcessedReportsDaoImpl.class);

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	private static final String OLDFARMATTER = "yyyy-MM-dd";
	private static final String NEWFARMATTER = "dd-MM-yyyy";

	@Override
	public List<Object> getGstr1RSReports(SearchCriteria criteria) {
		Gstr1ReviwSummReportsReqDto request = (Gstr1ReviwSummReportsReqDto) criteria;

		String dataType = request.getDataType();
		String taxperiod = request.getTaxperiod();
		List<String> tableType = request.getTableType();
		List<String> cdnrTableType = new ArrayList<>();
		for (String table : tableType) {
			if (table.equalsIgnoreCase("CDNUR")) {
				cdnrTableType.add("CDNUR-EXPORTS");
				cdnrTableType.add("CDNUR-B2CL");
			}
		}
		tableType.addAll(cdnrTableType);
		List<String> docType = request.getDocType();
		LocalDate docFromDate = request.getDocDateFrom();
		LocalDate docToDate = request.getDocDateTo();
		List<String> einvGenerated = request.geteInvGenerated();
		List<String> ewbGenerated = request.geteWbGenerated();

		String einvGen = null;
		if (einvGenerated != null && einvGenerated.size() > 0) {
			einvGen = einvGenerated.get(0);
		}

		String ewbResp = null;
		if (ewbGenerated != null && ewbGenerated.size() > 0) {
			ewbResp = ewbGenerated.get(0);
		}

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

		StringBuilder buildHeader = new StringBuilder();

		if (GSTIN != null && !GSTIN.isEmpty()) {
			if (gstinList != null && gstinList.size() > 0) {
				buildHeader.append(" AND HDR.SUPPLIER_GSTIN IN :gstinList");

			}
		}
		if (ProfitCenter != null && !ProfitCenter.isEmpty()) {
			if (pcList != null && pcList.size() > 0) {

				buildHeader.append(" AND HDR.PROFIT_CENTRE IN :pcList");

			}
		}
		if (plant != null && !plant.isEmpty()) {
			if (plantList != null && plantList.size() > 0) {

				buildHeader.append(" AND HDR.PLANT_CODE IN :plantList");

			}
		}
		if (sales != null && !sales.isEmpty()) {
			if (salesList != null && salesList.size() > 0) {

				buildHeader.append(" AND HDR.SALES_ORGANIZATION IN :salesList");

			}
		}
		if (distChannel != null && !distChannel.isEmpty()) {
			if (distList != null && distList.size() > 0) {

				buildHeader
						.append(" AND HDR.DISTRIBUTION_CHANNEL IN :distList");

			}
		}
		if (division != null && !division.isEmpty()) {
			if (divisionList != null && divisionList.size() > 0) {

				buildHeader.append(" AND HDR.DIVISION IN :divisionList");

			}
		}
		if (location != null && !location.isEmpty()) {
			if (locationList != null && locationList.size() > 0) {

				buildHeader.append(" AND HDR.LOCATION IN :locationList");

			}
		}
		if (ud1 != null && !ud1.isEmpty()) {
			if (ud1List != null && ud1List.size() > 0) {

				buildHeader.append(" AND HDR.USERACCESS1 IN :ud1List");
			}
		}
		if (ud2 != null && !ud2.isEmpty()) {
			if (ud2List != null && ud2List.size() > 0) {

				buildHeader.append(" AND HDR.USERACCESS2 IN :ud2List");
			}
		}
		if (ud3 != null && !ud3.isEmpty()) {
			if (ud3List != null && ud3List.size() > 0) {

				buildHeader.append(" AND HDR.USERACCESS3 IN :ud3List");
			}
		}
		if (ud4 != null && !ud4.isEmpty()) {
			if (ud4List != null && ud4List.size() > 0) {

				buildHeader.append(" AND HDR.USERACCESS4 IN :ud4List");
			}
		}
		if (ud5 != null && !ud5.isEmpty()) {
			if (ud5List != null && ud5List.size() > 0) {

				buildHeader.append(" AND HDR.USERACCESS5 IN :ud5List");
			}
		}
		if (ud6 != null && !ud6.isEmpty()) {
			if (ud6List != null && ud6List.size() > 0) {

				buildHeader.append(" AND HDR.USERACCESS6 IN :ud6List");
			}
		}

		if (taxperiod != null && !taxperiod.isEmpty()) {

			buildHeader.append(" AND HDR.DERIVED_RET_PERIOD = :taxperiod ");
			buildHeader.append(" AND ITM.DERIVED_RET_PERIOD = :taxperiod ");
		}

		if (docFromDate != null && docToDate != null) {
			buildHeader.append(
					" AND HDR.DOC_DATE BETWEEN :docFromDate AND :docToDate ");
		}

		if (ewbResp != null && ewbResp.equalsIgnoreCase("YES")) {
			buildHeader.append(" AND EWB_NO_RESP IS NOT NULL ");
		}
		if (ewbResp != null && ewbResp.equalsIgnoreCase("NO")) {
			buildHeader.append(" AND EWB_NO_RESP IS NULL ");

		}
		if (einvGen != null && einvGen.equalsIgnoreCase("YES")) {
			buildHeader.append(" AND IRN_RESPONSE IS NOT NULL ");

		}
		if (einvGen != null && einvGen.equalsIgnoreCase("NO")) {
			buildHeader.append(" AND IRN_RESPONSE IS NULL ");
		}

		if (tableType != null && tableType.size() > 0) {
			buildHeader.append(" AND HDR.TAX_DOC_TYPE IN (:tableType) ");
		}

		if (docType != null && docType.size() > 0) {
			buildHeader.append(" AND HDR.DOC_TYPE IN (:docType) ");
			// nillQueryBuilder.append(" AND DOC_TYPE IN (:docType) ");
		}

		String queryStr = createApiProcessedQueryString(buildHeader.toString());
		Query q = entityManager.createNativeQuery(queryStr);

		if (GSTIN != null && !GSTIN.isEmpty()) {
			if (gstinList != null && !gstinList.isEmpty()
					&& gstinList.size() > 0) {
				q.setParameter("gstinList", gstinList);
			}
		}

		if (taxperiod != null && !taxperiod.isEmpty()) {
			int derivedRetPeriod = GenUtil
					.convertTaxPeriodToInt(request.getTaxperiod());
			q.setParameter("taxperiod", derivedRetPeriod);
		}

		if (tableType != null && tableType.size() > 0
				&& !tableType.contains("")) {
			q.setParameter("tableType", tableType);
		}
		if (docType != null && docType.size() > 0 && !docType.contains("")) {
			q.setParameter("docType", docType);
		}
		if (docFromDate != null && docToDate != null) {
			q.setParameter("docFromDate", docFromDate);
			q.setParameter("docToDate", docToDate);
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

		List<Object[]> list = q.getResultList();
		return list.parallelStream().map(o -> convertProcessed(o))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	private DataStatusEinvoiceDto convertProcessed(Object[] arr) {
		DataStatusEinvoiceDto obj = new DataStatusEinvoiceDto();

		String errDesc = null;
		String infoDesc = null;

		String errCode = (arr[0] != null) ? arr[0].toString() : null;

		if (!Strings.isNullOrEmpty(errCode)) {
			String[] errorCodes = errCode.split(",");
			List<String> errCodeList = Arrays.asList(errorCodes);
			errCodeList.replaceAll(String::trim);
			errDesc = ErrorMasterUtil.getErrorInfo(errCodeList, "OUTWARD");
		}

		// obj.setAspErrorCode(errCode);
		obj.setAspErrorDesc(errDesc);

		String infoCode = (arr[2] != null) ? arr[2].toString() : null;

		if (!Strings.isNullOrEmpty(infoCode)) {
			String[] infoCodes = infoCode.split(",");
			List<String> infoCodeList = Arrays.asList(infoCodes);
			infoCodeList.replaceAll(String::trim);
			infoDesc = ErrorMasterUtil.getErrorInfo(infoCodeList, "OUTWARD");
		}

		obj.setAspInformationDesc(infoDesc);
		obj.setIrnStatus(arr[4] != null ? arr[4].toString() : null);
		// obj.setIrnStatus(arr[4] != null ? deriveEinvStatus(arr[4].toString())
		// : null);
		obj.setIrnNo(arr[5] != null ? arr[5].toString() : null);
		obj.setIrnAcknowledgmentNo(arr[6] != null
				? DownloadReportsConstant.CSVCHARACTER.concat(arr[6].toString())
				: null);
		/*
		 * if (arr[7] != null) {
		 * 
		 * LOGGER.debug("Irn Ack Date :" + arr[7].toString());
		 * 
		 * String timestamp = arr[7].toString(); String[] dateTime =
		 * timestamp.split(" ");
		 * 
		 * String date = dateTime[0]; String time = dateTime[1];
		 * 
		 * obj.setIrnAcknowledgmentDate(date);
		 * obj.setIrnAcknowledgmentTime(DownloadReportsConstant.CSVCHARACTER.
		 * concat(time)); }
		 */
		obj.setIrnAcknowledgmentDate(arr[7] != null ? arr[7].toString() : null);
		obj.setSignedQRCode(arr[8] != null ? arr[8].toString() : null);
		obj.setSignedInvoice(arr[9] != null ? arr[9].toString() : null);
		obj.setIrpErrorCode(arr[10] != null ? arr[10].toString() : null);
		obj.setIrpErrorDescription(arr[11] != null ? arr[11].toString() : null);
		obj.setEwbStatus(arr[12] != null ? arr[12].toString() : null);
		obj.setEwbValidupto(arr[13] != null ? arr[13].toString() : null);
		/*
		 * if(arr[13] != null){ LOGGER.debug("validUpto :" +arr[13].toString());
		 * 
		 * String timestamp = arr[13].toString(); String[] dateTime =
		 * timestamp.split(" ");
		 * 
		 * String date = dateTime[0]; String time = dateTime[1];
		 * 
		 * obj.setEwbValidupto(date);
		 * obj.setEwbValiduptoTime(DownloadReportsConstant.CSVCHARACTER
		 * .concat(time)); }
		 */
		obj.setEwbNo(arr[296] != null ? DownloadReportsConstant.CSVCHARACTER
				.concat(arr[296].toString()) : null);

		/*
		 * if (arr[297] != null) {
		 * 
		 * LOGGER.debug("EwbRespDate :" + arr[297].toString());
		 * 
		 * String timestamp = arr[297].toString(); String[] dateTime =
		 * timestamp.split(" ");
		 * 
		 * String date = dateTime[0]; String time = dateTime[1];
		 * obj.setEwbRespDate(date);
		 * obj.setEwbTime(DownloadReportsConstant.CSVCHARACTER.concat(time));
		 * 
		 * }
		 */
		obj.setEwbRespDate(arr[297] != null ? arr[297].toString() : null);
		obj.setEwbErrorCode(arr[16] != null ? arr[16].toString() : null);
		obj.setEwbErrorDescription(arr[17] != null ? arr[17].toString() : null);
		obj.setGstnStatus(arr[18] != null ? arr[18].toString() : null);
		obj.setGstnRefid(arr[19] != null ? arr[19].toString() : null);
		obj.setGstnRefidDate(arr[20] != null ? arr[20].toString() : null);

		/*
		 * if (arr[20] != null) {
		 * 
		 * LOGGER.debug("GstnRefidTime :" + arr[20].toString());
		 * 
		 * String timestamp = arr[20].toString(); String[] dateTime =
		 * timestamp.split(" ");
		 * 
		 * String date = dateTime[0]; String time = dateTime[1];
		 * obj.setGstnRefidDate(date);
		 * obj.setGstnRefidTime(DownloadReportsConstant.CSVCHARACTER.concat(time
		 * ));
		 * 
		 * }
		 */
		/*
		 * obj.setGstnRefidTime( arr[20] != null ? arr[20].toString() : null);
		 */
		obj.setGstnErrorCode(arr[21] != null ? arr[21].toString() : null);
		obj.setGstnErrorDescription(
				arr[22] != null ? arr[22].toString() : null);
		obj.setReturnType(arr[23] != null ? arr[23].toString() : null);
		obj.setTableNumber(arr[24] != null ? arr[24].toString() : null);
		obj.setIrn(arr[25] != null ? arr[25].toString() : null);
		obj.setIrnDate(arr[26] != null ? DownloadReportsConstant.CSVCHARACTER
				.concat(arr[26].toString()) : null);
		obj.setTaxScheme(arr[27] != null ? arr[27].toString() : null);
		obj.setCancellationReason(arr[28] != null ? arr[28].toString() : null);
		obj.setCancellationRemarks(arr[29] != null ? arr[29].toString() : null);
		obj.setSupplyType(arr[30] != null ? arr[30].toString() : null);
		obj.setDocCategory(arr[31] != null ? arr[31].toString() : null);
		obj.setDocumentType(arr[32] != null ? arr[32].toString() : null);
		obj.setDocumentNumber(
				arr[33] != null ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[33].toString()) : null);

		if (arr[34] != null) {
			String strdate = arr[34].toString();
			DateTimeFormatter f = new DateTimeFormatterBuilder()
					.appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(strdate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setDocumentDate(newDate);
		} else {
			obj.setDocumentDate(null);
		}
		obj.setReverseChargeFlag(arr[35] != null ? arr[35].toString() : null);
		obj.setSupplierGSTIN(arr[36] != null ? arr[36].toString() : null);
		obj.setSupplierTradeName(arr[37] != null ? arr[37].toString() : null);
		obj.setSupplierLegalName(arr[38] != null ? arr[38].toString() : null);
		obj.setSupplierAddress1(arr[39] != null ? arr[39].toString() : null);
		obj.setSupplierAddress2(arr[40] != null ? arr[40].toString() : null);
		obj.setSupplierLocation(arr[41] != null ? arr[41].toString() : null);
		obj.setSupplierPincode(arr[42] != null ? arr[42].toString() : null);
		obj.setSupplierStateCode(
				arr[43] != null ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[43].toString()) : null);
		obj.setSupplierPhone(arr[44] != null ? arr[44].toString() : null);
		obj.setSupplierEmail(arr[45] != null ? arr[45].toString() : null);
		obj.setCustomerGSTIN(arr[46] != null ? arr[46].toString() : null);
		obj.setCustomerTradeName(arr[47] != null ? arr[47].toString() : null);
		obj.setCustomerLegalName(arr[48] != null ? arr[48].toString() : null);
		obj.setCustomerAddress1(arr[49] != null ? arr[49].toString() : null);
		obj.setCustomerAddress2(arr[50] != null ? arr[50].toString() : null);
		obj.setCustomerLocation(arr[51] != null ? arr[51].toString() : null);
		obj.setCustomerPincode(arr[52] != null ? arr[52].toString() : null);
		obj.setCustomerStateCode(
				arr[53] != null ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[53].toString()) : null);
		obj.setBillingPOS(arr[54] != null ? DownloadReportsConstant.CSVCHARACTER
				.concat(arr[54].toString()) : null);
		obj.setCustomerPhone(arr[55] != null ? arr[55].toString() : null);
		obj.setCustomerEmail(arr[56] != null ? arr[56].toString() : null);
		obj.setDispatcherGSTIN(arr[57] != null ? arr[57].toString() : null);
		obj.setDispatcherTradeName(arr[58] != null ? arr[58].toString() : null);
		obj.setDispatcherAddress1(arr[59] != null ? arr[59].toString() : null);
		obj.setDispatcherAddress2(arr[60] != null ? arr[60].toString() : null);
		obj.setDispatcherLocation(arr[61] != null ? arr[61].toString() : null);
		obj.setDispatcherPincode(arr[62] != null ? arr[62].toString() : null);
		obj.setDispatcherStateCode(
				arr[63] != null ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[63].toString()) : null);
		obj.setShipToGSTIN(arr[64] != null ? arr[64].toString() : null);
		obj.setShipToTradeName(arr[65] != null ? arr[65].toString() : null);
		obj.setShipToLegalName(arr[66] != null ? arr[66].toString() : null);
		obj.setShipToAddress1(arr[67] != null ? arr[67].toString() : null);
		obj.setShipToAddress2(arr[68] != null ? arr[68].toString() : null);
		obj.setShipToLocation(arr[69] != null ? arr[69].toString() : null);
		obj.setShipToPincode(arr[70] != null ? arr[70].toString() : null);

		obj.setShipToStateCode(
				arr[71] != null ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[71].toString()) : null);

		/*
		 * obj.setShipToStateCode( arr[301] != null ?
		 * DownloadReportsConstant.CSVCHARACTER .concat(arr[301].toString()) :
		 * null);
		 */
		obj.setItemSerialNumber(arr[72] != null ? arr[72].toString() : null);
		obj.setProductSerialNumber(arr[73] != null ? arr[73].toString() : null);
		obj.setProductName(arr[74] != null ? arr[74].toString() : null);
		obj.setProductDescription(arr[75] != null ? arr[75].toString() : null);
		obj.setIsService(arr[76] != null ? arr[76].toString() : null);
		// obj.setHsn(arr[77] != null ? arr[77].toString() : null);
		obj.setHsn(arr[77] != null ? DownloadReportsConstant.CSVCHARACTER
				.concat(arr[77].toString()) : null);
		obj.setBarcode(arr[78] != null ? arr[78].toString() : null);
		obj.setBatchName(arr[79] != null ? arr[79].toString() : null);
		if (arr[80] != null) {
			String strdate = arr[80].toString();
			DateTimeFormatter f = new DateTimeFormatterBuilder()
					.appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(strdate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setBatchExpiryDate(newDate);
		} else {
			obj.setBatchExpiryDate(null);
		}
		if (arr[81] != null) {
			String strdate = arr[81].toString();
			DateTimeFormatter f = new DateTimeFormatterBuilder()
					.appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(strdate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setWarrantyDate(newDate);
		} else {
			obj.setWarrantyDate(null);
		}
		obj.setOrderlineReference(arr[82] != null ? arr[82].toString() : null);
		obj.setAttributeName(arr[83] != null ? arr[83].toString() : null);
		obj.setAttributeValue(arr[84] != null ? arr[84].toString() : null);
		obj.setOriginCountry(arr[85] != null ? arr[85].toString() : null);
		/*obj.setuQC(arr[86] != null ? arr[86].toString() : null);
		obj.setQuantity(arr[87] != null ? arr[87].toString() : null);*/
		obj.setFreeQuantity(arr[88] != null ? arr[88].toString() : null);
		obj.setUnitPrice(arr[89] != null ? arr[89].toString() : null);
		obj.setItemAmount(arr[90] != null ? arr[90].toString() : null);
		obj.setItemDiscount(arr[91] != null ? arr[91].toString() : null);
		obj.setPreTaxAmount(arr[92] != null ? arr[92].toString() : null);
		obj.setItemAssessableAmount(
				arr[93] != null ? arr[93].toString() : null);
		obj.setiGSTRate(arr[94] != null ? arr[94].toString() : null);
		obj.setiGSTAmount(arr[95] != null ? arr[95].toString() : null);
		obj.setcGSTRate(arr[96] != null ? arr[96].toString() : null);
		obj.setcGSTAmount(arr[97] != null ? arr[97].toString() : null);
		obj.setsGSTRate(arr[98] != null ? arr[98].toString() : null);
		obj.setsGSTAmount(arr[99] != null ? arr[99].toString() : null);
		obj.setCessAdvaloremRate(arr[100] != null ? arr[100].toString() : null);
		obj.setCessAdvaloremAmount(
				arr[101] != null ? arr[101].toString() : null);
		obj.setCessSpecificRate(arr[102] != null ? arr[102].toString() : null);
		obj.setCessSpecificAmount(
				arr[103] != null ? arr[103].toString() : null);
		obj.setStateCessAdvaloremRate(
				arr[104] != null ? arr[104].toString() : null);
		obj.setStateCessAdvaloremAmount(
				arr[105] != null ? arr[105].toString() : null);
		obj.setStateCessSpecificRate(
				arr[106] != null ? arr[106].toString() : null);
		obj.setStateCessSpecificAmount(
				arr[107] != null ? arr[107].toString() : null);
		obj.setItemOtherCharges(arr[108] != null ? arr[108].toString() : null);
		obj.setTotalItemAmount(arr[109] != null ? arr[109].toString() : null);
		obj.setInvoiceOtherCharges(
				arr[110] != null ? arr[110].toString() : null);
		obj.setInvoiceAssessableAmount(
				arr[111] != null ? arr[111].toString() : null);
		obj.setInvoiceIGSTAmount(arr[112] != null ? arr[112].toString() : null);
		obj.setInvoiceCGSTAmount(arr[113] != null ? arr[113].toString() : null);
		obj.setInvoiceSGSTAmount(arr[114] != null ? arr[114].toString() : null);
		obj.setInvoiceCessAdvaloremAmount(
				arr[115] != null ? arr[115].toString() : null);
		obj.setInvoiceCessSpecificAmount(
				arr[116] != null ? arr[116].toString() : null);
		obj.setInvoiceStateCessAdvaloremAmount(
				arr[117] != null ? arr[117].toString() : null);
		obj.setInvoiceStateCessSpecificAmount(
				arr[118] != null ? arr[118].toString() : null);
		obj.setInvoiceValue(arr[119] != null ? arr[119].toString() : null);
		obj.setRoundOff(arr[120] != null ? arr[120].toString() : null);
		obj.setTotalInvoiceValue(arr[121] != null ? arr[121].toString() : null);
		obj.settCSFlagIncomeTax(arr[122] != null ? arr[122].toString() : null);
		obj.settCSRateIncomeTax(arr[123] != null ? arr[123].toString() : null);
		obj.settCSAmountIncomeTax(
				arr[124] != null ? arr[124].toString() : null);
		obj.setCustomerPANOrAadhaar(
				arr[125] != null ? arr[125].toString() : null);
		obj.setCurrencyCode(arr[126] != null ? arr[126].toString() : null);
		obj.setCountryCode(arr[127] != null ? arr[127].toString() : null);
		obj.setInvoiceValueFC(arr[128] != null ? arr[128].toString() : null);
		obj.setPortCode(arr[129] != null ? arr[129].toString() : null);
		obj.setShippingBillNumber(
				arr[130] != null ? arr[130].toString() : null);
		if (arr[131] != null) {
			String strdate = arr[131].toString();
			DateTimeFormatter f = new DateTimeFormatterBuilder()
					.appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(strdate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setShippingBillDate(newDate);
		} else {
			obj.setShippingBillDate(null);
		}
		obj.setInvoiceRemarks(arr[132] != null ? arr[132].toString() : null);
		if (arr[133] != null) {
			String strdate = arr[133].toString();
			DateTimeFormatter f = new DateTimeFormatterBuilder()
					.appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(strdate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setInvoicePeriodStartDate(newDate);
		} else {
			obj.setInvoicePeriodStartDate(null);
		}

		if (arr[134] != null) {
			String strdate = arr[134].toString();
			DateTimeFormatter f = new DateTimeFormatterBuilder()
					.appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(strdate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setInvoicePeriodEndDate(newDate);
		} else {
			obj.setInvoicePeriodEndDate(null);
		}

		obj.setPreceedingInvoiceNumber(
				arr[135] != null ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[135].toString()) : null);
		if (arr[136] != null) {
			String strdate = arr[136].toString();
			DateTimeFormatter f = new DateTimeFormatterBuilder()
					.appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(strdate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setPreceedingInvoiceDate(newDate);
		} else {
			obj.setPreceedingInvoiceDate(null);
		}

		obj.setOtherReference(arr[137] != null ? arr[137].toString() : null);
		obj.setReceiptAdviceReference(
				arr[138] != null ? arr[138].toString() : null);
		obj.setReceiptAdviceDate(arr[139] != null ? arr[139].toString() : null);
		obj.setTenderReference(arr[140] != null ? arr[140].toString() : null);
		obj.setContractReference(arr[141] != null ? arr[141].toString() : null);
		obj.setExternalReference(arr[142] != null ? arr[142].toString() : null);
		obj.setProjectReference(arr[143] != null ? arr[143].toString() : null);
		obj.setCustomerPOReferenceNumber(
				arr[144] != null ? arr[144].toString() : null);
		obj.setCustomerPOReferenceDate(
				arr[145] != null ? arr[145].toString() : null);
		obj.setPayeeName(arr[146] != null ? arr[146].toString() : null);
		obj.setModeOfPayment(arr[147] != null ? arr[147].toString() : null);
		obj.setBranchOrIFSCCode(arr[148] != null ? arr[148].toString() : null);
		obj.setPaymentTerms(arr[149] != null ? arr[149].toString() : null);
		obj.setPaymentInstruction(
				arr[150] != null ? arr[150].toString() : null);
		obj.setCreditTransfer(arr[151] != null ? arr[151].toString() : null);
		obj.setDirectDebit(arr[152] != null ? arr[152].toString() : null);
		obj.setCreditDays(arr[153] != null ? arr[153].toString() : null);
		obj.setPaidAmount(arr[154] != null ? arr[154].toString() : null);
		obj.setBalanceAmount(arr[155] != null ? arr[155].toString() : null);
		if (arr[156] != null) {
			String strdate = arr[156].toString();
			DateTimeFormatter f = new DateTimeFormatterBuilder()
					.appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(strdate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setPaymentDueDate(newDate);
		} else {
			obj.setPaymentDueDate(null);
		}

		obj.setAccountDetail(
				arr[157] != null ? "'" + arr[157].toString() : null);
		// Object accountdetail =
		// CommonUtility.exponentialAndZeroCheck(arr[157]);
		// obj.setAccountDetail(
		// accountdetail != null ? accountdetail.toString() : null);
		obj.setEcomGSTIN(arr[158] != null ? arr[158].toString() : null);
		obj.setEcomTransactionID(arr[159] != null ? arr[159].toString() : null);
		obj.setSupportingDocURL(arr[160] != null ? arr[160].toString() : null);
		obj.setSupportingDocument(
				arr[161] != null ? arr[161].toString() : null);
		obj.setAdditionalInformation(
				arr[162] != null ? arr[162].toString() : null);
		obj.setTransactionType(arr[163] != null ? arr[163].toString() : null);
		obj.setSubSupplyType(arr[164] != null ? arr[164].toString() : null);
		obj.setOtherSupplyTypeDescription(
				arr[165] != null ? arr[165].toString() : null);
		obj.setTransporterID(arr[166] != null ? arr[166].toString() : null);
		obj.setTransporterName(arr[167] != null ? arr[167].toString() : null);
		obj.setTransportMode(arr[168] != null ? arr[168].toString() : null);
		obj.setTransportDocNo(arr[169] != null ? arr[169].toString() : null);
		if (arr[170] != null) {
			String strdate = arr[170].toString();
			DateTimeFormatter f = new DateTimeFormatterBuilder()
					.appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(strdate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setTransportDocDate(newDate);
		} else {
			obj.setTransportDocDate(null);
		}
		obj.setDistance(arr[171] != null ? arr[171].toString() : null);
		obj.setVehicleNo(arr[172] != null ? arr[172].toString() : null);
		obj.setVehicleType(arr[173] != null ? arr[173].toString() : null);
		obj.setReturnPeriod(
				arr[174] != null ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[174].toString()) : null);
		obj.setOriginalDocumentType(
				arr[175] != null ? arr[175].toString() : null);
		obj.setOriginalCustomerGSTIN(
				arr[176] != null ? arr[176].toString() : null);
		obj.setDifferentialPercentageFlag(
				arr[177] != null ? arr[177].toString() : null);
		obj.setSec7ofIGSTFlag(arr[178] != null ? arr[178].toString() : null);
		obj.setClaimRefndFlag(arr[179] != null ? arr[179].toString() : null);
		obj.setAutoPopltToRefund(arr[180] != null ? arr[180].toString() : null);
		obj.setcRDRPreGST(arr[181] != null ? arr[181].toString() : null);
		obj.setCustomerType(arr[182] != null ? arr[182].toString() : null);
		obj.setCustomerCode(arr[183] != null ? arr[183].toString() : null);
		obj.setProductCode(arr[184] != null ? arr[184].toString() : null);
		obj.setCategoryOfProduct(arr[185] != null ? arr[185].toString() : null);
		obj.setiTCFlag(arr[186] != null ? arr[186].toString() : null);
		obj.setStateApplyingCess(arr[187] != null ? arr[187].toString() : null);
		obj.setfOB(arr[188] != null ? arr[188].toString() : null);
		obj.setExportDuty(arr[189] != null ? arr[189].toString() : null);
		obj.setExchangeRate(arr[190] != null ? arr[190].toString() : null);
		obj.setReasonForCreditDebitNote(
				arr[191] != null ? arr[191].toString() : null);
		obj.settCSFlagGST(arr[192] != null ? arr[192].toString() : null);
		obj.settCSIGSTAmount(arr[193] != null ? arr[193].toString() : null);
		obj.settCSCGSTAmount(arr[194] != null ? arr[194].toString() : null);
		obj.settCSSGSTAmount(arr[195] != null ? arr[195].toString() : null);
		obj.settDSFlagGST(arr[196] != null ? arr[196].toString() : null);
		obj.settDSIGSTAmount(arr[197] != null ? arr[197].toString() : null);
		obj.settDSCGSTAmount(arr[198] != null ? arr[198].toString() : null);
		obj.settDSSGSTAmount(arr[199] != null ? arr[199].toString() : null);
		obj.setUserId(arr[200] != null ? arr[200].toString() : null);
		obj.setCompanyCode(arr[201] != null ? arr[201].toString() : null);
		obj.setSourceIdentifier(arr[202] != null ? arr[202].toString() : null);
		obj.setSourceFileName(arr[203] != null ? arr[203].toString() : null);
		obj.setPlantCode(arr[204] != null ? arr[204].toString() : null);
		obj.setDivision(arr[205] != null ? arr[205].toString() : null);
		obj.setSubDivision(arr[206] != null ? arr[206].toString() : null);
		obj.setLocation(arr[207] != null ? arr[207].toString() : null);
		obj.setSalesOrganisation(arr[208] != null ? arr[208].toString() : null);
		obj.setDistributionChannel(
				arr[209] != null ? arr[209].toString() : null);
		obj.setProfitCentre1(arr[210] != null ? arr[210].toString() : null);
		obj.setProfitCentre2(arr[211] != null ? arr[211].toString() : null);
		obj.setProfitCentre3(arr[212] != null ? arr[212].toString() : null);
		obj.setProfitCentre4(arr[213] != null ? arr[213].toString() : null);
		obj.setProfitCentre5(arr[214] != null ? arr[214].toString() : null);
		obj.setProfitCentre6(arr[215] != null ? arr[215].toString() : null);
		obj.setProfitCentre7(arr[216] != null ? arr[216].toString() : null);
		obj.setProfitCentre8(arr[217] != null ? arr[217].toString() : null);
		obj.setGlAssessableValue(arr[218] != null ? arr[218].toString() : null);
		obj.setGlIGST(arr[219] != null ? arr[219].toString() : null);
		obj.setGlCGST(arr[220] != null ? arr[220].toString() : null);
		obj.setGlSGST(arr[221] != null ? arr[221].toString() : null);
		obj.setGlAdvaloremCess(arr[222] != null ? arr[222].toString() : null);
		obj.setGlSpecificCess(arr[223] != null ? arr[223].toString() : null);
		obj.setgLStateCessAdvalorem(
				arr[224] != null ? arr[224].toString() : null);
		obj.setgLStateCessSpecific(
				arr[225] != null ? arr[225].toString() : null);
		if (arr[226] != null) {
			String strdate = arr[226].toString();
			DateTimeFormatter f = new DateTimeFormatterBuilder()
					.appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(strdate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setGlPostingDate(newDate);
		} else {
			obj.setGlPostingDate(null);
		}
		obj.setSalesOrderNumber(arr[227] != null ? arr[227].toString() : null);
		obj.seteWBNumber(arr[14] != null ? DownloadReportsConstant.CSVCHARACTER
				.concat(arr[14].toString()) : null);
		obj.seteWBDate(arr[15] != null ? DownloadReportsConstant.CSVCHARACTER
				.concat(arr[15].toString()) : null);
		obj.setAccountingVoucherNumber(
				arr[230] != null ? arr[230].toString() : null);
		if (arr[231] != null) {
			String strdate = arr[231].toString();
			DateTimeFormatter f = new DateTimeFormatterBuilder()
					.appendPattern(OLDFARMATTER).toFormatter();
			LocalDate parsedDate = LocalDate.parse(strdate, f);
			DateTimeFormatter f2 = DateTimeFormatter.ofPattern(NEWFARMATTER);
			String newDate = parsedDate.format(f2);
			obj.setAccountingVoucherDate(newDate);
		} else {
			obj.setAccountingVoucherDate(null);
		}
		obj.setDocumentReferenceNumber(
				arr[232] != null ? arr[232].toString() : null);
		obj.setCustomerTAN(arr[233] != null ? arr[233].toString() : null);
		obj.setUserDefField1(arr[234] != null ? arr[234].toString() : null);
		obj.setUserDefField2(arr[235] != null ? arr[235].toString() : null);
		obj.setUserDefField3(arr[236] != null ? arr[236].toString() : null);
		obj.setUserDefField4(arr[237] != null ? arr[237].toString() : null);
		obj.setUserDefField5(arr[238] != null ? arr[238].toString() : null);
		obj.setUserDefField6(arr[239] != null ? arr[239].toString() : null);
		obj.setUserDefField7(arr[240] != null ? arr[240].toString() : null);
		obj.setUserDefField8(arr[241] != null ? arr[241].toString() : null);
		obj.setUserDefField9(arr[242] != null ? arr[242].toString() : null);
		obj.setUserDefField10(arr[243] != null ? arr[243].toString() : null);
		obj.setUserDefField11(arr[244] != null ? arr[244].toString() : null);
		obj.setUserDefField12(arr[245] != null ? arr[245].toString() : null);
		obj.setUserDefField13(arr[246] != null ? arr[246].toString() : null);
		obj.setUserDefField14(arr[247] != null ? arr[247].toString() : null);
		obj.setUserDefField15(arr[248] != null ? arr[248].toString() : null);
		obj.setUserDefField16(arr[249] != null ? arr[249].toString() : null);
		obj.setUserDefField17(arr[250] != null ? arr[250].toString() : null);
		obj.setUserDefField18(arr[251] != null ? arr[251].toString() : null);
		obj.setUserDefField19(arr[252] != null ? arr[252].toString() : null);
		obj.setUserDefField20(arr[253] != null ? arr[253].toString() : null);
		obj.setUserDefField21(arr[254] != null ? arr[254].toString() : null);
		obj.setUserDefField22(arr[255] != null ? arr[255].toString() : null);
		obj.setUserDefField23(arr[256] != null ? arr[256].toString() : null);
		obj.setUserDefField24(arr[257] != null ? arr[257].toString() : null);
		obj.setUserDefField25(arr[258] != null ? arr[258].toString() : null);
		obj.setUserDefField26(arr[259] != null ? arr[259].toString() : null);
		obj.setUserDefField27(arr[260] != null ? arr[260].toString() : null);
		obj.setUserDefField28(arr[261] != null ? arr[261].toString() : null);
		obj.setUserDefField29(arr[262] != null ? arr[262].toString() : null);
		obj.setUserDefField30(arr[263] != null ? arr[263].toString() : null);
		obj.setSupplyTypeASP(arr[264] != null ? arr[264].toString() : null);
		obj.setApproximateDistanceASP(
				arr[265] != null ? arr[265].toString() : null);
		obj.setDistanceSavedtoEWB(
				arr[266] != null ? arr[266].toString() : null);
		obj.setUserID(arr[267] != null ? arr[267].toString() : null);
		obj.setFileID(arr[268] != null ? arr[268].toString() : null);
		obj.setFileName(arr[269] != null ? arr[269].toString() : null);
		obj.setInvoiceOtherChargesASP(
				arr[270] != null ? arr[270].toString() : null);
		obj.setInvoiceAssessableAmountASP(
				arr[271] != null ? arr[271].toString() : null);
		obj.setInvoiceIGSTAmountASP(
				arr[272] != null ? arr[272].toString() : null);
		obj.setInvoiceCGSTAmountASP(
				arr[273] != null ? arr[273].toString() : null);
		obj.setInvoiceSGSTAmountASP(
				arr[274] != null ? arr[274].toString() : null);
		obj.setInvoiceCessAdvaloremAmountASP(
				arr[275] != null ? arr[275].toString() : null);
		obj.setInvoiceCessSpecificAmountASP(
				arr[276] != null ? arr[276].toString() : null);
		obj.setInvoiceStateCessAdvaloremAmountASP(
				arr[277] != null ? arr[277].toString() : null);
		obj.setInvoiceStateCessSpecificAmountASP(
				arr[278] != null ? arr[278].toString() : null);
		obj.setInvoiceValueASP(arr[279] != null ? arr[279].toString() : null);
		obj.setIntegratedTaxAmountASP(
				arr[280] != null ? arr[280].toString() : null);
		obj.setCentralTaxAmountASP(
				arr[281] != null ? arr[281].toString() : null);
		obj.setStateUTTaxAmountASP(
				arr[282] != null ? arr[282].toString() : null);
		obj.setCessAdvaloremAmountASP(
				arr[283] != null ? arr[283].toString() : null);
		obj.setStateCessAdvaloremAmountASP(
				arr[284] != null ? arr[284].toString() : null);
		obj.setIntegratedTaxAmountRET1Impact(
				arr[285] != null ? arr[285].toString() : null);
		obj.setCentralTaxAmountRET1Impact(
				arr[286] != null ? arr[286].toString() : null);
		obj.setStateUTTaxAmountRET1Impact(
				arr[287] != null ? arr[287].toString() : null);
		obj.setCessAdvaloremAmountDifference(
				arr[288] != null ? arr[288].toString() : null);
		obj.setStateCessAdvaloremAmountDifference(
				arr[289] != null ? arr[289].toString() : null);
		obj.setRecordStatus(arr[290] != null ? arr[290].toString() : null);
		obj.setIrnCancellationDate(
				arr[291] != null ? arr[291].toString() : null);
		/*
		 * if (arr[291] != null) {
		 * 
		 * LOGGER.debug("Irn Cancellation Date :" + arr[291].toString());
		 * 
		 * String timestamp = arr[291].toString(); String[] dateTime =
		 * timestamp.split(" ");
		 * 
		 * String date = dateTime[0]; String time = dateTime[1];
		 * 
		 * obj.setIrnCancellationDate(date);
		 * obj.setIrnCancellationTime(DownloadReportsConstant.CSVCHARACTER.
		 * concat(time)); }
		 */
		/*
		 * if (arr[292] != null) {
		 * 
		 * LOGGER.debug("EWB Cancellation Date :" + arr[292].toString()); String
		 * timestamp = arr[292].toString(); String[] dateTime =
		 * timestamp.split(" ");
		 * 
		 * String date = dateTime[0]; String time = dateTime[1];
		 * 
		 * obj.seteWBCancellationDate(date);
		 * obj.seteWBCancellationTime(DownloadReportsConstant.CSVCHARACTER.
		 * concat(time)); }
		 */
		obj.seteWBCancellationDate(
				arr[292] != null ? arr[292].toString() : null);
		obj.setDigiGSTStatus(arr[293] != null ? arr[293].toString() : null);
		obj.setInfoErrorCode(arr[294] != null ? arr[294].toString() : null);
		obj.setInfoErrorMsg(arr[295] != null ? arr[295].toString() : null);
		
		
		String itemUqc = arr[311] != null ? arr[311].toString() : null; 
		String itemQty = arr[312] != null ? arr[312].toString() : null; 
		
		//String eINVvsGSTR1Reponse = arr[313] != null ? arr[313].toString() : null; 
		obj.setEINVvsGstr1Reponse(arr[313] != null ? arr[313].toString() : null);
		String itemUqcUser = arr[86] != null ? arr[86].toString() : null; 
		String itemQtyUser = arr[87] != null ? arr[87].toString() : null; 

		String uqc = itemUqcUser != null ? itemUqcUser : itemUqc;
		String qty = itemQtyUser != null ? itemQtyUser : itemQty;

		obj.setuQC(uqc != null ? uqc : null);
		obj.setQuantity(qty != null ? qty : null);
		
		return obj;
	}

	private String createApiProcessedQueryString(String buildHeader) {

		return "SELECT MAX(ERROR_CODE_ASP)ERROR_CODE_ASP, MAX(ERROR_DESCRIPTION_ASP)ERROR_DESCRIPTION_ASP, "
				+ "MAX(INFO_ERROR_CODE_ASP)INFO_ERROR_CODE_ASP, MAX(INFO_ERROR_DESCRIPTION_ASP)INFO_ERROR_DESCRIPTION_ASP, "
				+ "MAX(IRN_STATUS)IRN_STATUS,MAX(IRN_NO)IRN_NO, MAX( IRN_ACK_NO)IRN_ACK_NO, MAX(IRN_ACK_DATE_TIME) IRN_ACK_DATE_TIME,"
				+ "MAX(SIGNED_QR_CODE)SIGNED_QR_CODE, MAX(SIGNED_INVOICE)SIGNED_INVOICE, MAX(IRP_ERROR_CODE)IRP_ERROR_CODE, "
				+ "MAX( IRP_ERROR_DESCRIPTION)IRP_ERROR_DESCRIPTION, MAX(EWBPartAStatusA)EWBPartAStatusA, MAX( EWBValidUpTo)EWBValidUpTo, "
				+ "MAX( EWBNO)EWBNO, MAX( EWBDATE)EWBDATE1, MAX(EWB_ERROR_CODE)EWB_ERROR_CODE, MAX( EWB_ERROR_DESCRIPTION)EWB_ERROR_DESCRIPTION, "
				+ "MAX( SAVE_GSTIN_STATUS)SAVE_GSTIN_STATUS, MAX(GSTIN_REF_ID)GSTIN_REF_ID, MAX(GSTIN_REFID_DATE_TIME) GSTIN_REFID_DATE_TIME, "
				+ "MAX(GSTIN_ERROR_CODE)GSTIN_ERROR_CODE, MAX(GSTIN_ERROR_DESCRIPTION_ASP)GSTIN_ERROR_DESCRIPTION_ASP, MAX(RET_TYPE)RETURN_TYPE,"
				+ "MAX(TABLE_NUMBER) TABLE_NUMBER,MAX( IRN)IRN, MAX( IRN_DATE)IRN_DATE, MAX( TAX_SCHEME)TAX_SCHEME, "
				+ "MAX(CANCEL_REASON) CANCEL_REASON,MAX( CANCEL_REMARKS)CANCEL_REMARKS, MAX( SUPPLY_TYPE)SUPPLY_TYPE,MAX( DOC_CATEGORY) "
				+ "DOC_CATEGORY,DOC_TYPE, DOC_NUM, MAX(DOC_DATE)DOC_DATE,MAX(REVERSE_CHARGE)REVERSE_CHARGE, "
				+ "SUPPLIER_GSTIN,MAX( SUPP_TRADE_NAME) SUPP_TRADE_NAME, MAX( SUPP_LEGAL_NAME)SUPP_LEGAL_NAME, "
				+ "MAX(SUPP_BUILDING_NUM)SUPP_BUILDING_NUM, MAX(SUPPLIERADDRESS2)SUPPLIERADDRESS2,MAX(SUPP_LOCATION) SUPP_LOCATION, "
				+ "MAX( SUPP_PINCODE)SUPP_PINCODE, MAX(SUPP_STATE_CODE)SUPP_STATE_CODE, MAX(SUPP_PHONE) SUPP_PHONE, MAX( SUPP_EMAIL)SUPP_EMAIL, "
				+ "MAX(CUST_GSTIN)CUST_GSTIN,MAX(CUST_TRADE_NAME) CUST_TRADE_NAME, MAX(CUST_SUPP_NAME)CUST_SUPP_NAME, "
				+ "MAX(CUST_SUPP_ADDRESS1)CUST_SUPP_ADDRESS1, MAX(CUST_SUPP_ADDRESS2)CUST_SUPP_ADDRESS2, MAX(CUST_SUPP_ADDRESS4)CUST_SUPP_ADDRESS4, "
				+ "MAX(CUST_PINCODE)CUST_PINCODE, MAX(BILL_TO_STATE)BILL_TO_STATE,MAX( POS)POS, MAX(CUST_PHONE)CUST_PHONE, "
				+ "MAX(CUST_EMAIL)CUST_EMAIL, MAX(DISPATCHER_GSTIN)DISPATCHER_GSTIN , MAX(DISPATCHER_TRADE_NAME)DISPATCHER_TRADE_NAME, "
				+ "MAX(DISPATCHER_BUILDING_NUM)DISPATCHER_BUILDING_NUM, MAX( DISPATCHER_BUILDING_NAME)DISPATCHER_BUILDING_NAME, "
				+ "MAX(DISPATCHER_LOCATION)DISPATCHER_LOCATION , MAX(DISPATCHER_PINCODE)DISPATCHER_PINCODE, "
				+ "MAX(DISPATCHER_STATE_CODE)DISPATCHER_STATE_CODE, MAX( SHIP_TO_GSTIN)SHIP_TO_GSTIN, MAX(SHIP_TO_TRADE_NAME)SHIP_TO_TRADE_NAME, "
				+ "MAX(SHIP_TO_LEGAL_NAME)SHIP_TO_LEGAL_NAME, MAX(SHIP_TO_BUILDING_NUM)SHIP_TO_BUILDING_NUM, "
				+ "MAX(SHIP_TO_BUILDING_NAME)SHIP_TO_BUILDING_NAME, MAX(SHIP_TO_LOCATION)SHIP_TO_LOCATION, MAX( SHIP_TO_PINCODE)SHIP_TO_PINCODE,"
				+ "MAX(SHIPTOSTATECODE) SHIPTOSTATECODE, MAX( ITM_NO)ITM_NO, MAX( SERIAL_NUM2)SERIAL_NUM2,MAX(PRODUCT_NAME)PRODUCT_NAME, "
				+ "MAX(ITM_DESCRIPTION)ITM_DESCRIPTION, MAX( IS_SERVICE)IS_SERVICE,ITM_HSNSAC, MAX(BAR_CODE)BAR_CODE,"
				+ "MAX( BATCH_NAME_OR_NUM)BATCH_NAME_OR_NUM, MAX( BATCH_EXPIRY_DATE)BATCH_EXPIRY_DATE, MAX(WARRANTY_DATE)WARRANTY_DATE, "
				+ "MAX( ORDER_ITEM_REFERENCE)ORDER_ITEM_REFERENCE, MAX( ATTRIBUTE_NAME)ATTRIBUTE_NAME, MAX(ATTRIBUTE_VALUE)ATTRIBUTE_VALUE, "
				+ "MAX( ORIGIN_COUNTRY)ORIGIN_COUNTRY, MAX(ITM_UQC_USER)ITM_UQC_USER,SUM(QTY_USER)QTY_USER, SUM(FREE_QTY)FREE_QTY,MAX( UNIT_PRICE)UNIT_PRICE, "
				+ "SUM( ITEM_AMT_UP_QTY)ITEM_AMT_UP_QTY, SUM( ITEM_DISCOUNT)ITEM_DISCOUNT, SUM( PRE_TAX_AMOUNT)PRE_TAX_AMOUNT, "
				+ "SUM(TAXABLE_VALUE)TAXABLE_VALUE,IGST_RATE, SUM( IGST_AMT)IGST_AMT, CGST_RATE , SUM( CGST_AMT)CGST_AMT, SGST_RATE , "
				+ "SUM( SGST_AMT)SGST_AMT,MAX(CESS_RATE_ADVALOREM)CESS_RATE_ADVALOREM, SUM(CESS_AMT_ADVALOREM)CESS_AMT_ADVALOREM , "
				+ "MAX(CESS_RATE_SPECIFIC )CESS_RATE_SPECIFIC, SUM( CESS_AMT_SPECIFIC) CESS_AMT_SPECIFIC,MAX(STATECESS_RATE )STATECESS_RATE, "
				+ "SUM(STATECESS_AMT)STATECESS_AMT, MAX(STATE_CESS_SPECIFIC_RATE)STATE_CESS_SPECIFIC_RATE , "
				+ "SUM( STATE_CESS_SPECIFIC_AMOUNT)STATE_CESS_SPECIFIC_AMOUNT, SUM( OTHER_VALUES)OTHER_VALUES, SUM(TOT_ITEM_AMT)TOT_ITEM_AMT, "
				+ "SUM( INV_OTHER_CHARGES)INV_OTHER_CHARGES, SUM(INV_ASSESSABLE_AMT)INV_ASSESSABLE_AMT, SUM(INV_IGST_AMT)INV_IGST_AMT, "
				+ "SUM( INV_CGST_AMT)INV_CGST_AMT, SUM( INV_SGST_AMT)INV_SGST_AMT, SUM(INV_CESS_ADVLRM_AMT)INV_CESS_ADVLRM_AMT, "
				+ "SUM( INV_CESS_SPECIFIC_AMT)INV_CESS_SPECIFIC_AMT, SUM(INV_STATE_CESS_AMT)INV_STATE_CESS_AMT, "
				+ "SUM( INV_STATE_CESS_SPECIFIC_AMOUNT) INV_STATE_CESS_SPECIFIC_AMOUNT, SUM(LINE_ITEM_AMT)LINE_ITEM_AMT, "
				+ "MAX(ROUND_OFF)ROUND_OFF, MAX(TOT_INV_VAL_WORLDS)TOT_INV_VAL_WORLDS, MAX(TCS_FLAG_INCOME_TAX)TCS_FLAG_INCOME_TAX, "
				+ "MAX( TCS_RATE_INCOME_TAX)TCS_RATE_INCOME_TAX, MAX( TCS_AMOUNT_INCOME_TAX)TCS_AMOUNT_INCOME_TAX, "
				+ "MAX( CUSTOMER_PAN_OR_AADHAAR) CUSTOMER_PAN_OR_AADHAAR, MAX( FOREIGN_CURRENCY )FOREIGN_CURRENCY, MAX(COUNTRY_CODE)COUNTRY_CODE, "
				+ "MAX( INV_VAL_FC)INV_VAL_FC, MAX( SHIP_PORT_CODE)SHIP_PORT_CODE, MAX( SHIP_BILL_NUM)SHIP_BILL_NUM, "
				+ "MAX(SHIP_BILL_DATE)SHIP_BILL_DATE, MAX( INV_REMARKS)INV_REMARKS, MAX( INV_PERIOD_START_DATE)INV_PERIOD_START_DATE, "
				+ "MAX(INV_PERIOD_END_DATE)INV_PERIOD_END_DATE, MAX( PRECEEDING_INV_NUM)PRECEEDING_INV_NUM, MAX(PRECEEDING_INV_DATE)PRECEEDING_INV_DATE, "
				+ "MAX( INV_REFERENCE)INV_REFERENCE, MAX( RECEIPT_ADVICE_REFERENCE)RECEIPT_ADVICE_REFERENCE, "
				+ "MAX(RECEIPT_ADVICE_DATE)RECEIPT_ADVICE_DATE, MAX( TENDER_REFERENCE )TENDER_REFERENCE, MAX( CONTRACT_REFERENCE)CONTRACT_REFERENCE, "
				+ "MAX( EXTERNAL_REFERENCE)EXTERNAL_REFERENCE, MAX( PROJECT_REFERENCE)PROJECT_REFERENCE, MAX( CUST_PO_REF_NUM)CUST_PO_REF_NUM, "
				+ "MAX( CUST_PO_REF_DATE)CUST_PO_REF_DATE, MAX( PAYEE_NAME)PAYEE_NAME, MAX(MODE_OF_PAYMENT)MODE_OF_PAYMENT, "
				+ "MAX(BRANCH_IFSC_CODE)BRANCH_IFSC_CODE, MAX(PAYMENT_TERMS)PAYMENT_TERMS, MAX(PAYMENT_INSTRUCTION)PAYMENT_INSTRUCTION, "
				+ "MAX(CR_TRANSFER)CR_TRANSFER, MAX( DB_DIRECT)DB_DIRECT,MAX(CR_DAYS)CR_DAYS, SUM( PAID_AMT)PAID_AMT, SUM( BAL_AMT)BAL_AMT, "
				+ "MAX( PAYMENT_DUE_DATE)PAYMENT_DUE_DATE, MAX(ACCOUNT_DETAIL)ACCOUNT_DETAIL , MAX(ECOM_GSTIN)ECOM_GSTIN, "
				+ "MAX( ECOM_TRANS_ID)ECOM_TRANS_ID, MAX( SUPPORTING_DOC_URL)SUPPORTING_DOC_URL, MAX( SUPPORTING_DOC_BASE64)SUPPORTING_DOC_BASE64, "
				+ "MAX( ADDITIONAL_INFORMATION)ADDITIONAL_INFORMATION , MAX(TRANS_TYPE)TRANS_TYPE,MAX(SUB_SUPP_TYPE)SUB_SUPP_TYPE, "
				+ "MAX(OTHER_SUPP_TYPE_DESC)OTHER_SUPP_TYPE_DESC, MAX(TRANSPORTER_ID)TRANSPORTER_ID,MAX(TRANSPORTER_NAME) TRANSPORTER_NAME, "
				+ "MAX(TRANSPORT_MODE)TRANSPORT_MODE, MAX(TRANSPORT_DOC_NUM)TRANSPORT_DOC_NUM, MAX(TRANSPORT_DOC_DATE)TRANSPORT_DOC_DATE, "
				+ "MAX( DISTANCE )DISTANCE, MAX(VEHICLE_NUM)VEHICLE_NUM, MAX(VEHICLE_TYPE)VEHICLE_TYPE, MAX( RETURN_PERIOD)RETURN_PERIOD, "
				+ "MAX(ORIGINAL_DOC_TYPE)ORIGINAL_DOC_TYPE , MAX(ORIGINAL_CUST_GSTIN)ORIGINAL_CUST_GSTIN, MAX(DIFF_PERCENT)DIFF_PERCENT, "
				+ "MAX(SECTION7_OF_IGST_FLAG)SECTION7_OF_IGST_FLAG, MAX(CLAIM_REFUND_FLAG)CLAIM_REFUND_FLAG, "
				+ "MAX( AUTOPOPULATE_TO_REFUND)AUTOPOPULATE_TO_REFUND, MAX(CRDR_PRE_GST)CRDR_PRE_GST, MAX(CUST_SUPP_TYPE)CUST_SUPP_TYPE, "
				+ "MAX( CUSTOMERCODE)CUSTOMERCODE, MAX( PRODUCT_CODE)PRODUCT_CODE, MAX( ITM_TYPE)ITM_TYPE, MAX(ITC_FLAG)ITC_FLAG, "
				+ "MAX( STATE_APPLYING_CESS)STATE_APPLYING_CESS, MAX( FOB )FOB, MAX( EXPORT_DUTY)EXPORT_DUTY, MAX( EXCHANGE_RATE)EXCHANGE_RATE, "
				+ "MAX( CRDR_REASON)CRDR_REASON,MAX( TCS_FLAG ) TCS_FLAG,SUM( TCS_AMT)TCS_AMT, SUM( TCS_CGST_AMT) TCS_CGST_AMT,"
				+ "SUM( TCS_SGST_AMT)TCS_SGST_AMT, MAX( TDS_FLAG)TDS_FLAG,SUM( TDS_IGST_AMT)TDS_IGST_AMT, SUM( TDS_CGST_AMT)TDS_CGST_AMT,"
				+ "SUM( TDS_SGST_AMT)TDS_SGST_AMT, MAX( userid)userid,MAX( COMPANY_CODE)COMPANY_CODE, MAX(SOURCE_IDENTIFIER)SOURCE_IDENTIFIER, "
				+ "MAX(SOURCE_FILENAME)SOURCE_FILENAME, MAX( PLANT_CODE)PLANT_CODE,MAX(DIVISION)DIVISION, MAX(SUB_DIVISION)SUB_DIVISION, "
				+ "MAX( LOCATION)LOCATION, MAX( SALES_ORGANIZATION)SALES_ORGANIZATION, MAX( DISTRIBUTION_CHANNEL)DISTRIBUTION_CHANNEL, "
				+ "MAX( PROFIT_CENTRE)PROFIT_CENTRE, MAX(PROFIT_CENTRE2)PROFIT_CENTRE2, MAX(USERACCESS1)USERACCESS1, MAX( USERACCESS2)USERACCESS2, "
				+ "MAX(USERACCESS3)USERACCESS3, MAX( USERACCESS4)USERACCESS4, MAX(USERACCESS5)USERACCESS5, MAX( USERACCESS6)USERACCESS6, "
				+ "MAX( GLCODE_TAXABLEVALUE)GLCODE_TAXABLEVALUE, MAX(GLCODE_IGST)GLCODE_IGST,MAX( GLCODE_CGST)GLCODE_CGST, "
				+ "MAX( GLCODE_SGST)GLCODE_SGST,MAX(GLCODE_ADV_CESS) GLCODE_ADV_CESS,MAX(GLCODE_SP_CESS)GLCODE_SP_CESS , "
				+ "MAX( GLCODE_STATE_CESS)GLCODE_STATE_CESS, MAX( GL_STATE_CESS_SPECIFIC)GL_STATE_CESS_SPECIFIC, "
				+ "MAX( GL_POSTING_DATE)GL_POSTING_DATE, MAX(SALES_ORD_NUM)SALES_ORD_NUM, MAX(EWAY_BILL_NUM)EWAY_BILL_NUM, "
				+ "MAX( EWBDATE)EWBDATE, MAX( ACCOUNTING_VOUCHER_NUM)ACCOUNTING_VOUCHER_NUM, MAX( ACCOUNTING_VOUCHER_DATE)ACCOUNTING_VOUCHER_DATE, "
				+ "MAX( DOCUMENT_REFERENCE_NUMBER)DOCUMENT_REFERENCE_NUMBER, MAX(CUST_TAN)CUST_TAN, MAX( USERDEFINED_FIELD1)USERDEFINED_FIELD1, "
				+ "MAX( USERDEFINED_FIELD2)USERDEFINED_FIELD2, MAX( USERDEFINED_FIELD3)USERDEFINED_FIELD3, MAX( USERDEFINED_FIELD4)USERDEFINED_FIELD4, "
				+ "MAX( USERDEFINED_FIELD5)USERDEFINED_FIELD5, MAX( USERDEFINED_FIELD6)USERDEFINED_FIELD6, MAX( USERDEFINED_FIELD7)USERDEFINED_FIELD7, "
				+ "MAX( USERDEFINED_FIELD8)USERDEFINED_FIELD8, MAX( USERDEFINED_FIELD9)USERDEFINED_FIELD9, MAX( USERDEFINED_FIELD10)USERDEFINED_FIELD10, "
				+ "MAX( USERDEFINED_FIELD11)USERDEFINED_FIELD11, MAX( USERDEFINED_FIELD12)USERDEFINED_FIELD12, "
				+ "MAX( USERDEFINED_FIELD13)USERDEFINED_FIELD13, MAX( USERDEFINED_FIELD14)USERDEFINED_FIELD14 , "
				+ "MAX( USERDEFINED_FIELD15)USERDEFINED_FIELD15, MAX( USERDEFINED_FIELD16)USERDEFINED_FIELD16, "
				+ "MAX( USERDEFINED_FIELD17)USERDEFINED_FIELD17, MAX( USERDEFINED_FIELD18)USERDEFINED_FIELD18, "
				+ "MAX( USERDEFINED_FIELD19)USERDEFINED_FIELD19, MAX( USERDEFINED_FIELD20)USERDEFINED_FIELD20, "
				+ "MAX( USERDEFINED_FIELD21)USERDEFINED_FIELD21, MAX( USERDEFINED_FIELD22)USERDEFINED_FIELD22, "
				+ "MAX( USERDEFINED_FIELD23)USERDEFINED_FIELD23, MAX( USERDEFINED_FIELD24)USERDEFINED_FIELD24, "
				+ "MAX( USERDEFINED_FIELD25)USERDEFINED_FIELD25, MAX( USERDEFINED_FIELD26)USERDEFINED_FIELD26, "
				+ "MAX( USERDEFINED_FIELD27)USERDEFINED_FIELD27, MAX( USERDEFINED_FIELD28)USERDEFINED_FIELD28, "
				+ "MAX( USERDEFINED_FIELD29)USERDEFINED_FIELD29, MAX( USERDEFINED_FIELD30)USERDEFINED_FIELD30, "
				+ "MAX(SUPPLYTYPEASP)SUPPLYTYPEASP, MAX( ApproximateDistance_ASP)ApproximateDistance_ASP, "
				+ "MAX( DistanceSavedtoEWB)DistanceSavedtoEWB, MAX( USER_ID)USER_ID, MAX( FILE_ID)FILE_ID, MAX( FILE_NAME)FILE_NAME, "
				+ "SUM( InvoiceOtherChargesASP)InvoiceOtherChargesASP, SUM(InvoiceAssessableAmountASP)InvoiceAssessableAmountASP, "
				+ "SUM( InvoiceIGSTAmountASP)InvoiceIGSTAmountASP, SUM( InvoiceCGSTAmountASP)InvoiceCGSTAmountASP, "
				+ "SUM(InvoiceSGSTAmountASP)InvoiceSGSTAmountASP, SUM( InvoiceCessAdvaloremAmountASP) InvoiceCessAdvaloremAmountASP, "
				+ "SUM( InvoiceCessSpecificAmountASP)InvoiceCessSpecificAmountASP, SUM( InvoiceStateCessAdvaloremAmountASP) "
				+ "InvoiceStateCessAdvaloremAmountASP, SUM( InvoiceStateCessSpecificAmountASP) InvoiceStateCessSpecificAmountASP, "
				+ "SUM( DOC_AMT)DOC_AMT, SUM(MEMO_VALUE_IGST)MEMO_VALUE_IGST, SUM(MEMO_VALUE_CGST)MEMO_VALUE_CGST, "
				+ "SUM(MEMO_VALUE_SGST)MEMO_VALUE_SGST, SUM(CessAdvaloremAmount_ASP)CessAdvaloremAmount_ASP, "
				+ "SUM( StateCessAdvaloremAmount_ASP)StateCessAdvaloremAmount_ASP, SUM( IGST_RET1_IMPACT)IGST_RET1_IMPACT, "
				+ "SUM(CGST_RET1_IMPACT)CGST_RET1_IMPACT, SUM( SGST_RET1_IMPACT)SGST_RET1_IMPACT, SUM( CessAdvaloremAmount_Difference) "
				+ "CessAdvaloremAmount_Difference, SUM( StateCessAdvaloremAmount_Difference) StateCessAdvaloremAmount_Difference, "
				+ "MAX( RECORD_STATUS)RECORD_STATUS, MAX( IRN_CANCELLATION_DATE)IRN_CANCELLATION_DATE, "
				+ "MAX( EWB_CANCELLATION_DATE)EWB_CANCELLATION_DATE, MAX( DIGI_GST_STATUS)DIGI_GST_STATUS, MAX( EWB_INFO_CODE)EWB_INFO_CODE, "
				+ "MAX( EWB_INFO_ERROR_MSG)EWB_INFO_ERROR_MSG, MAX( EWB_NUM)EWB_NUM, MAX( EWB_DATE_TIME)EWB_DATE_TIME, MAX( ADJ_REF_NO)ADJ_REF_NO, "
				+ "MAX( ADJ_REF_DATE)ADJ_REF_DATE, MAX( ADJ_TAXABLE_VALUE)ADJ_TAXABLE_VALUE, SUM( ADJ_IGST_AMT)ADJ_IGST_AMT, "
				+ "SUM( ADJ_CGST_AMT)ADJ_CGST_AMT, SUM(ADJ_SGST_AMT)ADJ_SGST_AMT, SUM( ADJ_CESS_AMT_ADVALOREM)ADJ_CESS_AMT_ADVALOREM, "
				+ "SUM( ADJ_CESS_AMT_SPECIFIC)ADJ_CESS_AMT_SPECIFIC, SUM( ADJ_STATECESS_AMT)ADJ_STATECESS_AMT, SUM( Tcsamount)Tcsamount,"
				+ "MAX(CUST_SUPP_ADDRESS3)CUST_SUPP_ADDRESS3, MAX(DATA_CATEGORY) DATA_CATEGORY ,FI_YEAR,MAX(ITM_UQC)ITM_UQC,SUM(ITM_QTY)ITM_QTY,EINVVSGSTR1RESPONSE FROM ( SELECT '' AS ERROR_CODE_ASP, "
				+ " '' AS ERROR_DESCRIPTION_ASP, '' INFO_ERROR_CODE_ASP,'' AS INFO_ERROR_DESCRIPTION_ASP, '' IRN_STATUS, ''IRN_NO, '' IRN_ACK_NO, "
				+ " '' AS IRN_ACK_DATE_TIME, '' AS SIGNED_QR_CODE, '' AS SIGNED_INVOICE, '' IRP_ERROR_CODE, '' IRP_ERROR_DESCRIPTION, "
				+ " '' EWBPARTASTATUSA, '' AS EWBVALIDUPTO, '' EWBNO,'' EWBDATE, '' EWB_ERROR_CODE, '' EWB_ERROR_DESCRIPTION, "
				+ "(CASE WHEN HDR.IS_SAVED_TO_GSTN = TRUE THEN 'IS_SAVED' WHEN HDR.IS_SAVED_TO_GSTN = FALSE AND HDR.GSTN_ERROR = TRUE "
				+ "THEN 'IS_ERROR' WHEN HDR.IS_SAVED_TO_GSTN = FALSE AND HDR.GSTN_ERROR = FALSE THEN 'NOT_SAVED' END) AS SAVE_GSTIN_STATUS, "
				+ " '' AS GSTIN_REF_ID, '' AS GSTIN_REFID_DATE_TIME, NULL AS GSTIN_ERROR_CODE, NULL AS GSTIN_ERROR_DESCRIPTION_ASP, HDR.RETURN_TYPE "
				+ "AS RET_TYPE, '' AS TABLE_NUMBER, '' IRN, '' IRN_DATE,'' TAX_SCHEME, NULL CANCEL_REASON, NULL CANCEL_REMARKS, NULL "
				+ "AS SUPPLY_TYPE, NULL DOC_CATEGORY, HDR.DOC_TYPE, HDR.DOC_NUM,NULL DOC_DATE, NULL REVERSE_CHARGE, HDR.SUPPLIER_GSTIN,"
				+ "NULL SUPP_TRADE_NAME, NULL SUPP_LEGAL_NAME, NULL SUPP_BUILDING_NUM, NULL SUPPLIERADDRESS2,NULL SUPP_LOCATION, NULL SUPP_PINCODE, "
				+ "NULL SUPP_STATE_CODE,NULL SUPP_PHONE, NULL SUPP_EMAIL, NULL CUST_GSTIN,NULL CUST_TRADE_NAME, NULL CUST_SUPP_NAME, "
				+ "NULL CUST_SUPP_ADDRESS1, NULL CUST_SUPP_ADDRESS2, NULL CUST_SUPP_ADDRESS4,NULL CUST_PINCODE,NULL BILL_TO_STATE, NULL POS,"
				+ "NULL CUST_PHONE,NULL CUST_EMAIL,NULL DISPATCHER_GSTIN, NULL DISPATCHER_TRADE_NAME, NULL DISPATCHER_BUILDING_NUM, "
				+ "NULL DISPATCHER_BUILDING_NAME,NULL DISPATCHER_LOCATION, NULL DISPATCHER_PINCODE,NULL DISPATCHER_STATE_CODE ,NULL SHIP_TO_GSTIN , "
				+ "NULL SHIP_TO_TRADE_NAME, NULL SHIP_TO_LEGAL_NAME,NULL SHIP_TO_BUILDING_NUM, NULL SHIP_TO_BUILDING_NAME,NULL SHIP_TO_LOCATION, "
				+ "NULL SHIP_TO_PINCODE, NULL SHIPTOSTATECODE,NULL ITM_NO, NULL SERIAL_NUM2,NULL PRODUCT_NAME, NULL ITM_DESCRIPTION, "
				+ "NULL IS_SERVICE,ITM.ITM_HSNSAC,NULL BAR_CODE, NULL BATCH_NAME_OR_NUM, NULL BATCH_EXPIRY_DATE, NULL WARRANTY_DATE, "
				+ "NULL ORDER_ITEM_REFERENCE,NULL ATTRIBUTE_NAME, NULL ATTRIBUTE_VALUE, NULL ORIGIN_COUNTRY,NULL ITM_UQC_USER,ITM.QTY_USER, "
				+ "ITM.FREE_QTY, NULL UNIT_PRICE, ITM.ITEM_AMT_UP_QTY, ITM.ITEM_DISCOUNT,ITM.PRE_TAX_AMOUNT,ITM.TAXABLE_VALUE, ITM.IGST_RATE, "
				+ "ITM.IGST_AMT,ITM.CGST_RATE,ITM.CGST_AMT, ITM.SGST_RATE,ITM.SGST_AMT, ITM.CESS_RATE_ADVALOREM, ITM.CESS_AMT_ADVALOREM, "
				+ "ITM.CESS_RATE_SPECIFIC, ITM.CESS_AMT_SPECIFIC, ITM.STATECESS_RATE,ITM.STATECESS_AMT, ITM.STATE_CESS_SPECIFIC_RATE, "
				+ "ITM.STATE_CESS_SPECIFIC_AMOUNT, ITM.OTHER_VALUES,TOT_ITEM_AMT, ITM.INV_OTHER_CHARGES, ITM.INV_ASSESSABLE_AMT,ITM.INV_IGST_AMT,"
				+ "ITM.INV_CGST_AMT, ITM.INV_SGST_AMT, ITM.INV_CESS_ADVLRM_AMT, ITM.INV_CESS_SPECIFIC_AMT,ITM.INV_STATE_CESS_AMT, "
				+ "ITM.INV_STATE_CESS_SPECIFIC_AMOUNT, CASE WHEN HDR.DATAORIGINTYPECODE IN ('A','AI') THEN HDR.DOC_AMT "
				+ "WHEN HDR.DATAORIGINTYPECODE IN ('E','EI') THEN ITM.LINE_ITEM_AMT END LINE_ITEM_AMT, HDR.ROUND_OFF,HDR.TOT_INV_VAL_WORLDS,"
				+ "HDR.TCS_FLAG_INCOME_TAX, ITM.TCS_RATE_INCOME_TAX,ITM.TCS_AMOUNT_INCOME_TAX, NULL CUSTOMER_PAN_OR_AADHAAR, "
				+ "NULL FOREIGN_CURRENCY,NULL COUNTRY_CODE, NULL INV_VAL_FC, NULL SHIP_PORT_CODE, NULL SHIP_BILL_NUM, "
				+ "NULL SHIP_BILL_DATE, NULL INV_REMARKS,NULL INV_PERIOD_START_DATE, NULL INV_PERIOD_END_DATE, NULL PRECEEDING_INV_NUM, "
				+ "NULL PRECEEDING_INV_DATE,NULL INV_REFERENCE, NULL RECEIPT_ADVICE_REFERENCE,NULL RECEIPT_ADVICE_DATE, NULL TENDER_REFERENCE , "
				+ "NULL CONTRACT_REFERENCE, NULL EXTERNAL_REFERENCE, NULL PROJECT_REFERENCE, NULL CUST_PO_REF_NUM,NULL CUST_PO_REF_DATE,"
				+ "NULL PAYEE_NAME, NULL MODE_OF_PAYMENT, NULL BRANCH_IFSC_CODE,NULL PAYMENT_TERMS, NULL PAYMENT_INSTRUCTION,NULL CR_TRANSFER, "
				+ "NULL DB_DIRECT, NULL CR_DAYS,NULL PAID_AMT,NULL BAL_AMT, NULL PAYMENT_DUE_DATE, NULL ACCOUNT_DETAIL,NULL ECOM_GSTIN, "
				+ "NULL ECOM_TRANS_ID, NULL SUPPORTING_DOC_URL, NULL SUPPORTING_DOC_BASE64, NULL ADDITIONAL_INFORMATION,NULL TRANS_TYPE,"
				+ "NULL SUB_SUPP_TYPE, NULL OTHER_SUPP_TYPE_DESC,NULL TRANSPORTER_ID, NULL TRANSPORTER_NAME, NULL TRANSPORT_MODE, "
				+ "NULL TRANSPORT_DOC_NUM, NULL TRANSPORT_DOC_DATE, NULL DISTANCE ,NULL VEHICLE_NUM, NULL VEHICLE_TYPE, NULL RETURN_PERIOD,"
				+ "NULL ORIGINAL_DOC_TYPE, NULL ORIGINAL_CUST_GSTIN, NULL DIFF_PERCENT, NULL SECTION7_OF_IGST_FLAG,NULL CLAIM_REFUND_FLAG, "
				+ "NULL AUTOPOPULATE_TO_REFUND,NULL CRDR_PRE_GST,NULL CUST_SUPP_TYPE, NULL CUSTOMERCODE, NULL PRODUCT_CODE,NULL ITM_TYPE, "
				+ "NULL ITC_FLAG,NULL STATE_APPLYING_CESS,NULL FOB, NULL EXPORT_DUTY,NULL EXCHANGE_RATE,NULL CRDR_REASON, NULL TCS_FLAG ,"
				+ "ITM.TCS_AMT,TCS_CGST_AMT, TCS_SGST_AMT, NULL TDS_FLAG,TDS_IGST_AMT,TDS_CGST_AMT,TDS_SGST_AMT, NULL AS USERID, NULL COMPANY_CODE,"
				+ "NULL SOURCE_IDENTIFIER, NULL SOURCE_FILENAME,NULL PLANT_CODE,NULL DIVISION, SUB_DIVISION,NULL LOCATION,NULL SALES_ORGANIZATION, "
				+ "NULL DISTRIBUTION_CHANNEL,NULL PROFIT_CENTRE, NULL PROFIT_CENTRE2, NULL USERACCESS1,NULL USERACCESS2,NULL USERACCESS3, "
				+ "NULL USERACCESS4, NULL USERACCESS5,NULL USERACCESS6, ITM.GLCODE_TAXABLEVALUE,ITM.GLCODE_IGST,ITM.GLCODE_CGST, "
				+ "ITM.GLCODE_SGST,ITM.GLCODE_ADV_CESS,ITM.GLCODE_SP_CESS, ITM.GLCODE_STATE_CESS, HDR.GL_STATE_CESS_SPECIFIC, NULL GL_POSTING_DATE,"
				+ "NULL SALES_ORD_NUM,NULL EWAY_BILL_NUM, NULL EWB_DATE,NULL ACCOUNTING_VOUCHER_NUM, NULL ACCOUNTING_VOUCHER_DATE, "
				+ "NULL DOCUMENT_REFERENCE_NUMBER, NULL CUST_TAN,NULL USERDEFINED_FIELD1,NULL USERDEFINED_FIELD2, NULL USERDEFINED_FIELD3,"
				+ "NULL USERDEFINED_FIELD4, NULL USERDEFINED_FIELD5,NULL USERDEFINED_FIELD6, NULL USERDEFINED_FIELD7,NULL USERDEFINED_FIELD8, "
				+ "NULL USERDEFINED_FIELD9,NULL USERDEFINED_FIELD10, NULL USERDEFINED_FIELD11,NULL USERDEFINED_FIELD12, NULL USERDEFINED_FIELD13, "
				+ "NULL USERDEFINED_FIELD14, NULL USERDEFINED_FIELD15,NULL USERDEFINED_FIELD16, NULL USERDEFINED_FIELD17,NULL USERDEFINED_FIELD18, "
				+ "NULL USERDEFINED_FIELD19, NULL USERDEFINED_FIELD20, NULL USERDEFINED_FIELD21,NULL USERDEFINED_FIELD22, NULL USERDEFINED_FIELD23,"
				+ "NULL USERDEFINED_FIELD24, NULL USERDEFINED_FIELD25, NULL USERDEFINED_FIELD26, NULL USERDEFINED_FIELD27,NULL USERDEFINED_FIELD28, "
				+ "NULL USERDEFINED_FIELD29,NULL USERDEFINED_FIELD30, NULL SUPPLYTYPEASP, NULL AS APPROXIMATEDISTANCE_ASP, NULL AS DISTANCESAVEDTOEWB, "
				+ "NULL USER_ID,NULL FILE_ID, NULL FILE_NAME,HDR.INV_OTHER_CHARGES AS INVOICEOTHERCHARGESASP, HDR.INV_ASSESSABLE_AMT "
				+ "AS INVOICEASSESSABLEAMOUNTASP, HDR.INV_IGST_AMT AS INVOICEIGSTAMOUNTASP,HDR.INV_CGST_AMT AS INVOICECGSTAMOUNTASP, "
				+ "HDR.INV_SGST_AMT AS INVOICESGSTAMOUNTASP,HDR.INV_CESS_ADVLRM_AMT AS INVOICECESSADVALOREMAMOUNTASP, "
				+ "HDR.INV_CESS_SPECIFIC_AMT AS INVOICECESSSPECIFICAMOUNTASP, HDR.INV_STATE_CESS_AMT AS INVOICESTATECESSADVALOREMAMOUNTASP, "
				+ "HDR.INV_STATE_CESS_SPECIFIC_AMOUNT AS INVOICESTATECESSSPECIFICAMOUNTASP, HDR.DOC_AMT, HDR.MEMO_VALUE_IGST,HDR.MEMO_VALUE_CGST, "
				+ "HDR.MEMO_VALUE_SGST, ITM.MEMO_VALUE_CESS_ADV AS CESSADVALOREMAMOUNT_ASP, ITM.MEMO_VALUE_STATE_CESS_ADV "
				+ "AS STATECESSADVALOREMAMOUNT_ASP, ITM.IGST_RET1_IMPACT, ITM.CGST_RET1_IMPACT,ITM.SGST_RET1_IMPACT, ITM.CESS_ADV_RET1_IMPACT "
				+ "AS CESSADVALOREMAMOUNT_DIFFERENCE, ITM.STATE_CESS_ADV_RET1_IMPACT AS STATECESSADVALOREMAMOUNT_DIFFERENCE, "
				+ " '' RECORD_STATUS, NULL AS IRN_CANCELLATION_DATE, NULL AS EWB_CANCELLATION_DATE, NULL AS DIGI_GST_STATUS, NULL EWB_INFO_CODE, "
				+ "NULL AS EWB_INFO_ERROR_MSG, NULL EWB_NUM, NULL EWB_DATE_TIME, '' ADJ_REF_NO, '' ADJ_REF_DATE, ITM.ADJ_TAXABLE_VALUE, "
				+ "ITM.ADJ_IGST_AMT,ITM.ADJ_CGST_AMT, ITM.ADJ_SGST_AMT, ITM.ADJ_CESS_AMT_ADVALOREM, ITM.ADJ_CESS_AMT_SPECIFIC, ITM.ADJ_STATECESS_AMT, "
				+ "ITM.TCS_AMT AS TCSAMOUNT, '' CUST_SUPP_ADDRESS3, '' DATA_CATEGORY,FI_YEAR,NULL ITM_UQC,ITM.ITM_QTY,"
				+ "CASE WHEN PSD.USER_RESPONSE='S' THEN 'S-Save to GSTN' WHEN PSD.USER_RESPONSE='N' THEN 'N-Do not Save to GSTN' ELSE '' END AS EINVVSGSTR1RESPONSE  FROM ANX_OUTWARD_DOC_HEADER HDR INNER JOIN "
				+ "ANX_OUTWARD_DOC_ITEM ITM ON HDR.ID = ITM.DOC_HEADER_ID AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD LEFT OUTER JOIN "
				+ "FILE_STATUS FIL ON HDR.FILE_ID=FIL.ID LEFT OUTER JOIN GSTR1_GSTN_SAVE_BATCH GSTNBATCH ON GSTNBATCH.ID = HDR.BATCH_ID "
				+ "LEFT OUTER JOIN EINV_MASTER EINV ON HDR.IRN_RESPONSE = EINV.IRN "
				+ "LEFT OUTER JOIN TBL_EINV_RECON_RESP_PSD PSD ON PSD.DOC_HEADER_ID=ITM.DOC_HEADER_ID AND PSD.IS_PROCESSED=TRUE AND PSD.IS_DELETE=FALSE  "
				+ "LEFT OUTER JOIN EWB_MASTER EWB ON HDR.EWB_NO_RESP = EWB.EWB_NUM "
				+ " WHERE HDR.ASP_INVOICE_STATUS = 2 AND COMPLIANCE_APPLICABLE=TRUE AND HDR.IS_DELETE=FALSE AND HDR.RETURN_TYPE='GSTR1' "
				+ " AND HDR.TAX_DOC_TYPE NOT IN ('B2CS','NILEXTNON') "
				/*
				 * +
				 * "--AND HDR.TAX_DOC_TYPE IN ('__','__') -- AND HDR.SUPPLIER_GSTIN='33GSPTN0481G1ZA' AND HDR.DERIVED_RET_PERIOD=202001 "
				 * +
				 * "-- AND HDR.DOC_DATE BETWEEN '2017-09-01' AND '2020-10-30' -- AND IRN_RESPONSE IS NULL AND EINV_STATUS IN (5)"
				 * +
				 * " -- AND IRN_RESPONSE IS NOT NULL AND EINV_STATUS IN (7,10,11) -- AND HDR.DOC_TYPE IN ('INV','CR','DR','RNV','RCR','RDR') "
				 * +
				 * "-- AND EWB_NO_RESP IS NULL -- AND EWB_NO_RESP IS NOT NULL "
				 */
				+ buildHeader + " ORDER BY DOC_NUM,ITM.ITM_NO ) "
				+ " GROUP BY ITM_HSNSAC,IGST_RATE,CGST_RATE,SGST_RATE, DOC_TYPE,DOC_NUM,SUPPLIER_GSTIN,FI_YEAR,EINVVSGSTR1RESPONSE ";

	}
}
