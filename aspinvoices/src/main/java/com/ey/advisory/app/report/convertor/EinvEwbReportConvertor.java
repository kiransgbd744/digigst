/**
 * 
 */
package com.ey.advisory.app.report.convertor;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.client.EwbRepository;
import com.ey.advisory.app.docs.dto.einvoice.EwbEinvDownloadRequestDto;
import com.ey.advisory.app.services.search.docsearch.EinvReportConvertor;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.EinvEwbDto;
import com.ey.advisory.repositories.client.Gstr1BatchRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

/**
 * @author Sujith.Nanga
 *
 * 
 */
@Component("EinvEwbReportConvertor")
public class EinvEwbReportConvertor implements EinvReportConvertor {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("batchSaveStatusRepository")
	private Gstr1BatchRepository gstr1BatchRepository;

	@Autowired
	@Qualifier("EwbRepository")
	private EwbRepository ewbRepository;

	static String transType = null;

	private static final DateFormat df = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
	private static final DateFormat outputformat = new SimpleDateFormat(
			"dd/MM/yyyy hh:mm:ss a");

	private static final String NEWFARMATTER = "dd/MM/yyyy hh:mm:ss a";

	@Override
	public List<EinvEwbDto> getEInvMngmtListing(
			EwbEinvDownloadRequestDto criteria) {

		EwbEinvDownloadRequestDto request = (EwbEinvDownloadRequestDto) criteria;

		LocalDate receiveFromDate = request.getReceivFromDate();
		LocalDate receiveToDate = request.getReceivToDate();
		String returnFrom = request.getReturnFrom();
		String returnTo = request.getReturnTo();
		LocalDate docFromDate = request.getDocFromDate();
		LocalDate docToDate = request.getDocToDate();
		String docNo = request.getDocNo();
		Long fileId = request.getFileId();
		List<Long> einvStatus = request.getEinvStatus();
		List<String> docType = criteria.getDocTypes();
		transType = request.getTransType();

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
		if (docNo != null && !docNo.isEmpty()) {
			buildQuery.append(" AND HDR.DOC_NUM = :docNo");
		}

		if (fileId != null) {
			buildQuery.append(" AND HDR.FILE_ID = :fileId");
		}

		if (docType != null && !docType.isEmpty()) {
			buildQuery.append(" AND HDR.DOC_TYPE IN :docType");
		}

		if (einvStatus != null && !einvStatus.isEmpty()) {
			buildQuery.append(" AND HDR.IRN_STATUS IN :einvStatus");
		}

		if (transType != null && !transType.isEmpty()) {
			buildQuery.append(" AND UPPER(HDR.TRANS_TYPE) = UPPER(:transType)");
		}

		String queryStr = createEInvdownloadQueryString(buildQuery.toString());
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
		if (docNo != null && !docNo.isEmpty()) {
			q.setParameter("docNo", docNo);
		}

		if (fileId != null) {
			q.setParameter("fileId", fileId);
		}
		if (docType != null && !docType.isEmpty()) {
			q.setParameter("docType", docType);
		}

		if (einvStatus != null && !einvStatus.isEmpty()) {
			q.setParameter("einvStatus", einvStatus);
		}

		if (transType != null) {
			q.setParameter("transType", transType);
		}

		List<Object[]> list = q.getResultList();
		return list.parallelStream().map(o -> {
			try {
				return convertEInv(o);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}).collect(Collectors.toCollection(ArrayList::new));
	}

	private EinvEwbDto convertEInv(Object[] arr) throws ParseException {
		EinvEwbDto obj = new EinvEwbDto();

		obj.setCompanyCode(arr[0] != null ? arr[0].toString() : null);
		obj.setDocumentType(arr[1] != null ? arr[1].toString() : null);
		obj.setAccountingVoucherNumber(
				arr[2] != null ? arr[2].toString() : null);
		if (arr[3] != null) {
			String fyYear = arr[3].toString();
			String subFyYear = fyYear.substring(0, 4);
			obj.setFiscalYear(subFyYear);
		}
		obj.setSupplierGstin(arr[4] != null ? arr[4].toString() : null);
		obj.setDocumentNumber(arr[5] != null ? arr[5].toString() : null);
		obj.setDocumentDate(arr[6] != null ? arr[6].toString() : null);
		obj.setEinvoicingStatus(arr[7] != null ? arr[7].toString() : null);
		obj.setAcknowledgementNumber(arr[8] != null ? arr[8].toString() : null);
		if (arr[9] != null) {

			Timestamp timeStamp1 = (Timestamp) arr[9];
			LocalDateTime localDT1 = timeStamp1.toLocalDateTime();

			DateTimeFormatter formatter = DateTimeFormatter
					.ofPattern(NEWFARMATTER);
			String formatedDate = localDT1.format(formatter);

			obj.setAcknowledgementDate(formatedDate);
		} else {
			obj.setAcknowledgementDate(null);
		}
		// obj.setAcknowledgementDate(arr[9] != null ? arr[9].toString() :
		// null);
		obj.setiRNNumber(arr[10] != null ? arr[10].toString() : null);
		//obj.setSignedInvoiceData(arr[11] != null ? arr[11].toString() : null);
		obj.setSignedInvoiceData("");
		obj.setSignedQRCodeData(arr[12] != null ? arr[12].toString() : null);
		// obj.setqRData(arr[13] != null ? arr[13].toString() : null);
		// obj.setFormattedQRData(arr[14] != null ? arr[14].toString() : null);
		obj.setEwbNo(arr[15] != null ? arr[15].toString() : null);
		// obj.setEwbDate(arr[16] != null ? arr[16].toString() : null);
		if (arr[16] != null) {
			Timestamp timeStamp1 = (Timestamp) arr[16];
			LocalDateTime localDT1 = timeStamp1.toLocalDateTime();

			DateTimeFormatter formatter = DateTimeFormatter
					.ofPattern(NEWFARMATTER);
			String formatedDate = localDT1.format(formatter);

			obj.setEwbDate(formatedDate);
		} else {
			obj.setEwbDate(null);
		}
		// obj.setEwbValidityEnddate(arr[17] != null ? arr[17].toString() :
		// null);
		if (arr[17] != null) {

			Timestamp timeStamp1 = (Timestamp) arr[17];
			LocalDateTime localDT1 = timeStamp1.toLocalDateTime();

			DateTimeFormatter formatter = DateTimeFormatter
					.ofPattern(NEWFARMATTER);
			String formatedDate = localDT1.format(formatter);
			obj.setEwbValidityEnddate(formatedDate);
		} else {
			obj.setEwbValidityEnddate(null);
		}
		obj.setnICDistance(arr[18] != null ? arr[18].toString() : null);
		obj.seteWBErrorCode(arr[19] != null ? arr[19].toString() : null);
		obj.seteWBErrormessage(arr[20] != null ? arr[20].toString() : null);
		obj.seteWBInformationCode(arr[21] != null ? arr[21].toString() : null);
		obj.seteWBInformationmessage(
				arr[22] != null ? arr[22].toString() : null);
		if (arr[23] != null) {
			Timestamp timeStamp1 = (Timestamp) arr[23];
			LocalDateTime localDT1 = timeStamp1.toLocalDateTime();

			DateTimeFormatter formatter = DateTimeFormatter
					.ofPattern(NEWFARMATTER);
			String formatedDate = localDT1.format(formatter);

			obj.setCancellationDate(formatedDate);
		} else {
			obj.setCancellationDate(null);
		}
		// obj.setCancellationDate(arr[23] != null ? arr[23].toString() : null);
		obj.seteINVErrorCode(arr[24] != null ? arr[24].toString() : null);
		obj.seteINVErrormessage(arr[25] != null ? arr[25].toString() : null);
		obj.seteINVInformationCode(arr[26] != null ? arr[26].toString() : null);
		obj.seteINVInformationmessage(
				arr[27] != null ? arr[27].toString() : null);
		obj.setFileId(arr[28] != null ? arr[28].toString() : null);
		return obj;
	}

	private String createEInvdownloadQueryString(String buildQuery) {

		return "select HDR.COMPANY_CODE, HDR.DOC_TYPE, "
				+ "TO_CHAR(HDR.ACCOUNTING_VOUCHER_NUM) AS ACCOUNTING_VOUCHER_NUM , "
				+ "HDR.FI_YEAR, HDR.SUPPLIER_GSTIN, HDR.DOC_NUM, HDR.DOC_DATE, "
				+ "'Generated' AS EINV_STATUS,"
				+ "EINV.ACK_NUM ,EINV.ACK_DATE,EINV.IRN, EINV.SIGNED_INV, "
				+ "EINV.SIGNED_QR, EINV.QR_CODE, EINV.FORMATTED_QR_CODE, "
				+ "EWB.EWB_NUM, EWB.EWB_DATE, EWB.VALID_UPTO, "
				+ "EWB.REMAINING_DISTANCE, HDR.EWB_ERROR_CODE, HDR.EWB_ERROR_DESC, "
				+ " '' AS EWB_INFO_CODE, '' AS EWB_INFO_DESC, EWB.CANCELLATION_DATE, "
				+ "HDR.EINV_ERROR_CODE, HDR.EINV_ERROR_DESC, '' AS EINV_INFO_CODE, "
				+ " '' AS EINV_INFO_DESC,HDR.FILE_ID FROM ANX_OUTWARD_DOC_HEADER "
				+ "HDR LEFT OUTER JOIN EINV_MASTER EINV "
				+ "ON HDR.IRN_RESPONSE = EINV.IRN "
				+ "LEFT OUTER JOIN EWB_MASTER EWB "
				+ "ON HDR.EWB_NO_RESP = EWB.EWB_NUM "
				+ "WHERE HDR.IS_DELETE = FALSE AND IS_SUBMITTED = FALSE "
				+ buildQuery + " ORDER BY ID ASC  ";

	}
}
