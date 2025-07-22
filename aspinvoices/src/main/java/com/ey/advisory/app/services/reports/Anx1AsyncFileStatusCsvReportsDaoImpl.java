/**
 * 
 */
package com.ey.advisory.app.services.reports;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.docs.dto.Anx1FileStatusReportsReqDto;
import com.ey.advisory.app.docs.dto.einvoice.DataStatusEinvoiceDto;
import com.ey.advisory.app.downloadreports.DownloadReportsConstant;
import com.ey.advisory.common.AppException;
import com.ey.advisory.core.search.SearchCriteria;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Arun KA
 *
 */
@Component("Anx1AsyncFileStatusCsvReportsDaoImpl")
@Slf4j
public class Anx1AsyncFileStatusCsvReportsDaoImpl
		implements Anx1AsyncFileStatusCsvReportsDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	static Integer cutoffPeriod = null;
	static String fileType = null;
	String type = null;
	String status = null;
	private static final String OLDFARMATTER = "yyyy-MM-dd";
	private static final String NEWFARMATTER = "dd-MM-yyyy";

	@Override
	public List<DataStatusEinvoiceDto> getFileStatusCsvReports(
			SearchCriteria criteria, int noOfRowsToFetch, int pageNo) {
		Anx1FileStatusReportsReqDto request = (Anx1FileStatusReportsReqDto) criteria;
		Long fileId = request.getFileId();
		type = request.getType();
		status = request.getStatus();
		cutoffPeriod = request.getAnswer();
		fileType = request.getFileType();

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("status,type,fileId, fileType " + fileId + "" + type
					+ "" + status + "" + fileType);
		}

		StringBuffer buildQuery1 = new StringBuffer();
		StringBuffer buildQuery = new StringBuffer();
		StringBuffer buildRetTypeTableNo = new StringBuffer();
		StringBuffer buildTaxDocType = new StringBuffer();

		if (DownloadReportsConstant.ERRORBV.equalsIgnoreCase(type)) {

			/*buildRetTypeTableNo.append("HDR.RETURN_TYPE AS RETURN_TYPE,"
					+ "HDR.TABLE_SECTION AS TABLE_NUMBER,");

			buildTaxDocType.append("HDR.TAX_DOC_TYPE AS DATA_CATEGORY ");*/

			buildQuery1.append(" IS_ERROR = TRUE AND ITM.DOC_HEADER_ID IN "
					+ "(SELECT DOC_HEADER_ID FROM ANX_OUTWARD_DOC_ERROR WHERE "
					+ "VAL_TYPE = 'BV' ) ");
		}
		if (DownloadReportsConstant.PROCESSED.equalsIgnoreCase(type)) {
			buildRetTypeTableNo.append("CASE WHEN HDR.DERIVED_RET_PERIOD  >= "
					+ cutoffPeriod + " THEN HDR.AN_RETURN_TYPE "
					+ "ELSE HDR.RETURN_TYPE END AS RET_TYPE, "
					+ "CASE WHEN HDR.DERIVED_RET_PERIOD  >= " + cutoffPeriod
					+ " THEN HDR.AN_TABLE_SECTION "
					+ "ELSE HDR.TABLE_SECTION END AS TABLE_NUMBER,");

			buildTaxDocType.append("CASE WHEN HDR.DERIVED_RET_PERIOD  >= "
					+ cutoffPeriod + " THEN HDR.AN_TAX_DOC_TYPE "
					+ "ELSE HDR.TAX_DOC_TYPE END AS DATA_CATEGORY ");

			buildQuery1.append(" IS_PROCESSED = TRUE ");
		}
		/*if (DownloadReportsConstant.ERRORTOTAL.equalsIgnoreCase(type)) {
			buildRetTypeTableNo.append("HDR.RETURN_TYPE AS RETURN_TYPE,"
					+ "HDR.TABLE_SECTION AS TABLE_NUMBER,");

			buildTaxDocType.append("HDR.TAX_DOC_TYPE AS DATA_CATEGORY ");
			

			buildQuery1.append(" AND HDR.IS_DELETE = FALSE ");
		}*/
		if (DownloadReportsConstant.TOTALERECORDS.equalsIgnoreCase(type)) {

			buildRetTypeTableNo.append("CASE WHEN HDR.DERIVED_RET_PERIOD  >= "
					+ cutoffPeriod + " THEN TO_CHAR(HDR.AN_RETURN_TYPE) "
					+ "ELSE TO_CHAR(HDR.RETURN_TYPE) END AS RET_TYPE, "
					+ "CASE WHEN HDR.DERIVED_RET_PERIOD  >= " + cutoffPeriod
					+ " THEN TO_CHAR(HDR.AN_TABLE_SECTION) "
					+ "ELSE TO_CHAR(HDR.TABLE_SECTION) END AS TABLE_NUMBER, ");

			buildTaxDocType.append("CASE WHEN HDR.DERIVED_RET_PERIOD  >= "
					+ cutoffPeriod + " THEN TO_CHAR(HDR.AN_TAX_DOC_TYPE) "
					+ "ELSE TO_CHAR(HDR.TAX_DOC_TYPE) END AS DATA_CATEGORY ");
		}
		if (fileId != null) {
			buildQuery.append(" HDR.FILE_ID= :fileId");
		}
		if (status != null && !status.isEmpty()
				&& status.equalsIgnoreCase(DownloadReportsConstant.ACTIVE)) {
			buildQuery.append(" AND HDR.IS_DELETE = FALSE ");
		}
		if (status != null && !status.isEmpty()
				&& status.equalsIgnoreCase(DownloadReportsConstant.INACTIVE)) {
			buildQuery.append(" AND HDR.IS_DELETE = TRUE ");
		}

		String queryStr = createQueryString(buildQuery.toString(),
				buildQuery1.toString(), buildRetTypeTableNo.toString(),
				buildTaxDocType.toString(), noOfRowsToFetch, pageNo);
		
		
		Query q = entityManager.createNativeQuery(queryStr);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("queryStr  " + queryStr);
		}

		if (fileId != null) {
			q.setParameter("fileId", fileId);
		}
		List<Object[]> list = null;
		try{
			list = q.getResultList();
		}catch(Exception ex){
			String msg = "Exception occured while fetching Resultset";
			LOGGER.error(msg,ex);
			throw new AppException(msg,ex);
		}

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("DB data," + list);
		}
		return list.stream().map(o -> convertObj(o))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	public DataStatusEinvoiceDto convertObj(Object[] arr) {
		DataStatusEinvoiceDto obj = new DataStatusEinvoiceDto();
		if (fileType != null) {
			if (DownloadReportsConstant.FileTypes.RAW.getFileType()
					.equalsIgnoreCase(fileType)) {
				obj.setUserId(arr[200] != null ? arr[200].toString() : null);
				obj.setReturnType(arr[23] != null ? arr[23].toString() : null);
				obj.setTableNumber(arr[24] != null ? arr[24].toString() : null);
				obj.setSourceFileName(
						arr[203] != null ? arr[203].toString() : null);
				obj.setProfitCentre1(
						arr[210] != null ? arr[210].toString() : null);
				obj.setPlantCode(arr[204] != null ? arr[204].toString() : null);
				obj.setDivision(arr[205] != null ? arr[205].toString() : null);
				obj.setLocation(arr[207] != null ? arr[207].toString() : null);
				obj.setSalesOrganisation(
						arr[208] != null ? arr[208].toString() : null);
				obj.setDistributionChannel(
						arr[209] != null ? arr[209].toString() : null);
				obj.setProfitCentre3(
						arr[212] != null ? arr[212].toString() : null);
				obj.setProfitCentre4(
						arr[213] != null ? arr[213].toString() : null);
				obj.setProfitCentre5(
						arr[214] != null ? arr[214].toString() : null);
				obj.setProfitCentre6(
						arr[215] != null ? arr[215].toString() : null);
				obj.setProfitCentre7(
						arr[216] != null ? arr[216].toString() : null);
				obj.setProfitCentre8(
						arr[217] != null ? arr[217].toString() : null);
				obj.setReturnPeriod(
						arr[174] != null ? DownloadReportsConstant.CSVCHARACTER
								.concat(arr[174].toString()) : null);
				obj.setSupplierGSTIN(
						arr[36] != null ? arr[36].toString() : null);
				obj.setDocumentType(
						arr[32] != null ? arr[32].toString() : null);
				obj.setSupplyType(arr[30] != null ? arr[30].toString() : null);
				obj.setDocumentNumber(
						arr[33] != null ? DownloadReportsConstant.CSVCHARACTER
								.concat(arr[33].toString()) : null);
				if ((DownloadReportsConstant.PROCESSED).equalsIgnoreCase(type)
						|| (DownloadReportsConstant.ERRORBV)
								.equalsIgnoreCase(type)) {
					if (arr[34] != null) {
						String strdate = arr[34].toString();
						DateTimeFormatter f = new DateTimeFormatterBuilder()
								.appendPattern(OLDFARMATTER).toFormatter();
						LocalDate parsedDate = LocalDate.parse(strdate, f);
						DateTimeFormatter f2 = DateTimeFormatter
								.ofPattern(NEWFARMATTER);
						String newDate = parsedDate.format(f2);
						obj.setDocumentDate(newDate);
					} else {
						obj.setDocumentDate(null);
					}
				} else {
					obj.setDocumentDate(
							arr[34] != null ? arr[34].toString() : null);
				}
				obj.setPreceedingInvoiceNumber(
						arr[135] != null ? arr[135].toString() : null);
				if ((DownloadReportsConstant.PROCESSED).equalsIgnoreCase(type)
						|| (DownloadReportsConstant.ERRORBV)
								.equalsIgnoreCase(type)) {
					if (arr[136] != null) {
						String strdate = arr[136].toString();
						DateTimeFormatter f = new DateTimeFormatterBuilder()
								.appendPattern(OLDFARMATTER).toFormatter();
						LocalDate parsedDate = LocalDate.parse(strdate, f);
						DateTimeFormatter f2 = DateTimeFormatter
								.ofPattern(NEWFARMATTER);
						String newDate = parsedDate.format(f2);
						obj.setPreceedingInvoiceDate(newDate);
					} else {
						obj.setPreceedingInvoiceDate(null);
					}
				} else {
					obj.setPreceedingInvoiceDate(
							arr[136] != null ? arr[136].toString() : null);
				}
				obj.setOriginalDocumentType(
						arr[175] != null ? arr[175].toString() : null);
				obj.setcRDRPreGST(
						arr[181] != null ? arr[181].toString() : null);
				obj.setCustomerGSTIN(
						arr[46] != null ? arr[46].toString() : null);
				obj.setCustomerType(
						arr[182] != null ? arr[182].toString() : null);
				obj.setDifferentialPercentageFlag(
						arr[177] != null ? arr[177].toString() : null);
				obj.setOriginalCustomerGSTIN(
						arr[176] != null ? arr[176].toString() : null);
				obj.setCustomerLegalName(
						arr[48] != null ? arr[48].toString() : null);
				obj.setCustomerCode(
						arr[183] != null ? arr[183].toString() : null);
				obj.setCustomerAddress1(
						arr[49] != null ? arr[49].toString() : null);
				obj.setCustomerAddress2(
						arr[50] != null ? arr[50].toString() : null);
				obj.setCustomerLocation(
						arr[51] != null ? arr[51].toString() : null);
				obj.setCustomerStateCode(
						arr[53] != null ? DownloadReportsConstant.CSVCHARACTER
								.concat(arr[53].toString()) : null);
				obj.setBillingPOS(
						arr[54] != null ? DownloadReportsConstant.CSVCHARACTER
								.concat(arr[54].toString()) : null);
				obj.setStateApplyingCess(
						arr[187] != null ? arr[187].toString() : null);
				obj.setPortCode(arr[129] != null ? arr[129].toString() : null);
				obj.setShippingBillNumber(
						arr[130] != null ? arr[130].toString() : null);
				if ((DownloadReportsConstant.PROCESSED).equalsIgnoreCase(type)
						|| (DownloadReportsConstant.ERRORBV)
								.equalsIgnoreCase(type)) {
					if (arr[131] != null) {
						String strdate = arr[131].toString();
						DateTimeFormatter f = new DateTimeFormatterBuilder()
								.appendPattern(OLDFARMATTER).toFormatter();
						LocalDate parsedDate = LocalDate.parse(strdate, f);
						DateTimeFormatter f2 = DateTimeFormatter
								.ofPattern(NEWFARMATTER);
						String newDate = parsedDate.format(f2);
						obj.setShippingBillDate(newDate);
					} else {
						obj.setShippingBillDate(null);
					}
				} else {
					obj.setShippingBillDate(
							arr[131] != null ? arr[131].toString() : null);
				}
				obj.setSec7ofIGSTFlag(
						arr[178] != null ? arr[178].toString() : null);
				obj.setInvoiceValueASP(
						arr[279] != null ? arr[279].toString() : null);
				obj.setInvoiceValue(
						arr[119] != null ? arr[119].toString() : null);
				obj.setReverseChargeFlag(
						arr[35] != null ? arr[35].toString() : null);
				obj.setEcomGSTIN(arr[158] != null ? arr[158].toString() : null);
				obj.setClaimRefndFlag(
						arr[179] != null ? arr[179].toString() : null);
				obj.setAutoPopltToRefund(
						arr[180] != null ? arr[180].toString() : null);
				obj.setAccountingVoucherNumber(
						arr[230] != null ? arr[230].toString() : null);
				if ((DownloadReportsConstant.PROCESSED).equalsIgnoreCase(type)
						|| (DownloadReportsConstant.ERRORBV)
								.equalsIgnoreCase(type)) {
					if (arr[231] != null) {
						String strdate = arr[231].toString();
						DateTimeFormatter f = new DateTimeFormatterBuilder()
								.appendPattern(OLDFARMATTER).toFormatter();
						LocalDate parsedDate = LocalDate.parse(strdate, f);
						DateTimeFormatter f2 = DateTimeFormatter
								.ofPattern(NEWFARMATTER);
						String newDate = parsedDate.format(f2);
						obj.setAccountingVoucherDate(newDate);
					} else {
						obj.setAccountingVoucherDate(null);
					}
				} else {
					obj.setAccountingVoucherDate(
							arr[231] != null ? arr[231].toString() : null);
				}
				obj.seteWBNumber(arr[228] != null ? arr[228].toString() : null);
				obj.seteWBDate(arr[229] != null ? arr[229].toString() : null);
				obj.setGlAssessableValue(
						arr[218] != null ? arr[218].toString() : null);
				obj.setGlIGST(arr[219] != null ? arr[219].toString() : null);
				obj.setGlCGST(arr[220] != null ? arr[220].toString() : null);
				obj.setGlSGST(arr[221] != null ? arr[221].toString() : null);
				obj.setGlAdvaloremCess(
						arr[222] != null ? arr[222].toString() : null);
				obj.setGlSpecificCess(
						arr[223] != null ? arr[223].toString() : null);
				obj.setgLStateCessAdvalorem(
						arr[224] != null ? arr[224].toString() : null);
				obj.setItemSerialNumber(
						arr[72] != null ? arr[72].toString() : null);
				obj.setHsn(
						arr[77] != null ? DownloadReportsConstant.CSVCHARACTER
								.concat(arr[77].toString()) : null);
				obj.setProductCode(
						arr[184] != null ? arr[184].toString() : null);
				obj.setProductDescription(
						arr[75] != null ? arr[75].toString() : null);
				obj.setCategoryOfProduct(
						arr[185] != null ? arr[185].toString() : null);
				obj.setuQC(arr[86] != null ? arr[86].toString() : null);
				obj.setQuantity(arr[87] != null ? arr[87].toString() : null);
				obj.setiTCFlag(arr[186] != null ? arr[186].toString() : null);
				obj.setReasonForCreditDebitNote(
						arr[191] != null ? arr[191].toString() : null);
				obj.settCSFlagGST(
						arr[192] != null ? arr[192].toString() : null);
				obj.setfOB(arr[188] != null ? arr[188].toString() : null);
				obj.setExportDuty(
						arr[189] != null ? arr[189].toString() : null);
				obj.setUserDefField1(
						arr[234] != null ? arr[234].toString() : null);
				obj.setUserDefField2(
						arr[235] != null ? arr[235].toString() : null);
				obj.setUserDefField3(
						arr[236] != null ? arr[236].toString() : null);
				obj.setUserDefField4(
						arr[237] != null ? arr[237].toString() : null);
				obj.setUserDefField5(
						arr[238] != null ? arr[238].toString() : null);
				obj.setUserDefField6(
						arr[239] != null ? arr[239].toString() : null);
				obj.setUserDefField7(
						arr[240] != null ? arr[240].toString() : null);
				obj.setUserDefField8(
						arr[241] != null ? arr[241].toString() : null);
				obj.setUserDefField9(
						arr[242] != null ? arr[242].toString() : null);
				obj.setUserDefField10(
						arr[243] != null ? arr[243].toString() : null);
				obj.setUserDefField11(
						arr[244] != null ? arr[244].toString() : null);
				obj.setUserDefField12(
						arr[245] != null ? arr[245].toString() : null);
				obj.setUserDefField13(
						arr[246] != null ? arr[246].toString() : null);
				obj.setUserDefField14(
						arr[247] != null ? arr[247].toString() : null);
				obj.setUserDefField15(
						arr[248] != null ? arr[248].toString() : null);
				obj.setItemAssessableAmount(
						arr[93] != null ? arr[93].toString() : null);
				obj.setiGSTRate(arr[94] != null ? arr[94].toString() : null);
				obj.setiGSTAmount(arr[95] != null ? arr[95].toString() : null);
				obj.setcGSTRate(arr[96] != null ? arr[96].toString() : null);
				obj.setcGSTAmount(arr[97] != null ? arr[97].toString() : null);
				obj.setsGSTRate(arr[98] != null ? arr[98].toString() : null);
				obj.setsGSTAmount(arr[99] != null ? arr[99].toString() : null);
				obj.setCessSpecificRate(
						arr[102] != null ? arr[102].toString() : null);
				obj.setCessSpecificAmount(
						arr[103] != null ? arr[103].toString() : null);
				obj.setCessAdvaloremRate(
						arr[100] != null ? arr[100].toString() : null);
				obj.setCessAdvaloremAmount(
						arr[101] != null ? arr[101].toString() : null);
				obj.setStateCessSpecificRate(
						arr[106] != null ? arr[106].toString() : null);
				obj.setStateCessSpecificAmount(
						arr[107] != null ? arr[107].toString() : null);
				obj.setItemOtherCharges(
						arr[108] != null ? arr[108].toString() : null);
				obj.setAspErrorCode(arr[0] != null ? arr[0].toString() : null);
				obj.setAspErrorDesc(arr[1] != null ? arr[1].toString() : null);
				obj.setAspInformationId(
						arr[2] != null ? arr[2].toString() : null);
				obj.setAspInformationDesc(
						arr[3] != null ? arr[3].toString() : null);
				obj.setIntegratedTaxAmountASP(
						arr[280] != null ? arr[280].toString() : null);
				obj.setCentralTaxAmountASP(
						arr[281] != null ? arr[281].toString() : null);
				obj.setStateUTTaxAmountASP(
						arr[282] != null ? arr[282].toString() : null);
				obj.setIntegratedTaxAmountRET1Impact(
						arr[285] != null ? arr[285].toString() : null);
				obj.setCentralTaxAmountRET1Impact(
						arr[286] != null ? arr[286].toString() : null);
				obj.setStateUTTaxAmountRET1Impact(
						arr[287] != null ? arr[287].toString() : null);
				obj.setRecordStatus(
						arr[290] != null ? arr[290].toString() : null);
				obj.setAdjRefNo(arr[291] != null ? arr[291].toString() : null);
				obj.setAdjRefDate(
						arr[292] != null ? arr[292].toString() : null);
				obj.setTaxableValueAdj(
						arr[293] != null ? arr[293].toString() : null);
				obj.setIntegdTaxAmtAdj(
						arr[294] != null ? arr[294].toString() : null);
				obj.setCentralTaxAmtAdj(
						arr[295] != null ? arr[295].toString() : null);
				obj.setStateUTTaxAmtAdj(
						arr[296] != null ? arr[296].toString() : null);
				obj.setAdvlmCessAmtAdj(
						arr[297] != null ? arr[297].toString() : null);
				obj.setSpecfCessAmtAdj(
						arr[298] != null ? arr[298].toString() : null);
				obj.setStateCessAmtAdj(
						arr[298] != null ? arr[298].toString() : null);
				obj.setTcsAmount(arr[299] != null ? arr[299].toString() : null);
				obj.setRecpadd3(arr[300] != null ? arr[300].toString() : null);
				obj.setShipToStateCode(
						arr[301] != null ? DownloadReportsConstant.CSVCHARACTER
								.concat(arr[301].toString()) : null);
			}
			if (DownloadReportsConstant.FileTypes.COMPREHENSIVE_RAW
					.getFileType().equalsIgnoreCase(fileType)) {

				obj.setAspErrorCode(arr[0] != null ? arr[0].toString() : null);
				obj.setAspErrorDesc(arr[1] != null ? arr[1].toString() : null);
				obj.setAspInformationId(
						arr[2] != null ? arr[2].toString() : null);
				obj.setAspInformationDesc(
						arr[3] != null ? arr[3].toString() : null);
				obj.setIrnStatus(arr[4] != null ? arr[4].toString() : null);
				obj.setIrnNo(arr[5] != null ? arr[5].toString() : null);
				obj.setIrnAcknowledgmentNo(
						arr[6] != null ? arr[6].toString() : null);
				obj.setIrnAcknowledgmentDateTime(
						arr[7] != null ? arr[7].toString() : null);
				obj.setSignedQRCode(arr[8] != null ? arr[8].toString() : null);
				obj.setSignedInvoice(arr[9] != null ? arr[9].toString() : null);
				obj.setIrpErrorCode(
						arr[10] != null ? arr[10].toString() : null);
				obj.setIrpErrorDescription(
						arr[11] != null ? arr[11].toString() : null);
				obj.setEwbStatus(arr[12] != null ? arr[12].toString() : null);
				obj.setEwbValidupto(
						arr[13] != null ? arr[13].toString() : null);
				obj.setEwbNo(arr[14] != null ? arr[14].toString() : null);
				obj.seteWBDate(arr[15] != null ? arr[15].toString() : null);
				obj.setEwbErrorCode(
						arr[16] != null ? arr[16].toString() : null);
				obj.setEwbErrorDescription(
						arr[17] != null ? arr[17].toString() : null);
				obj.setGstnStatus(arr[18] != null ? arr[18].toString() : null);
				obj.setGstnRefid(arr[19] != null ? arr[19].toString() : null);
				obj.setGstnRefidDateTime(
						arr[20] != null ? arr[20].toString() : null);
				obj.setGstnErrorCode(
						arr[21] != null ? arr[21].toString() : null);
				obj.setGstnErrorDescription(
						arr[22] != null ? arr[22].toString() : null);
				obj.setReturnType(arr[23] != null ? arr[23].toString() : null);
				obj.setTableNumber(arr[24] != null ? arr[24].toString() : null);
				obj.setIrn(arr[25] != null ? arr[25].toString() : null);
				obj.setIrnDate(arr[26] != null ? arr[26].toString() : null);
				obj.setTaxScheme(arr[27] != null ? arr[27].toString() : null);
				obj.setCancellationReason(
						arr[28] != null ? arr[28].toString() : null);
				obj.setCancellationRemarks(
						arr[29] != null ? arr[29].toString() : null);
				obj.setSupplyType(arr[30] != null ? arr[30].toString() : null);
				obj.setDocCategory(arr[31] != null ? arr[31].toString() : null);
				obj.setDocumentType(
						arr[32] != null ? arr[32].toString() : null);
				obj.setDocumentNumber(
						arr[33] != null ? DownloadReportsConstant.CSVCHARACTER
								.concat(arr[33].toString()) : null);
				if ((DownloadReportsConstant.PROCESSED).equalsIgnoreCase(type)
						|| (DownloadReportsConstant.ERRORBV)
								.equalsIgnoreCase(type)) {
					if (arr[34] != null) {
						String strdate = arr[34].toString();
						DateTimeFormatter f = new DateTimeFormatterBuilder()
								.appendPattern(OLDFARMATTER).toFormatter();
						LocalDate parsedDate = LocalDate.parse(strdate, f);
						DateTimeFormatter f2 = DateTimeFormatter
								.ofPattern(NEWFARMATTER);
						String newDate = parsedDate.format(f2);
						obj.setDocumentDate(newDate);
					} else {
						obj.setDocumentDate(null);
					}
				} else {
					obj.setDocumentDate(
							arr[34] != null ? arr[34].toString() : null);
				}
				obj.setReverseChargeFlag(
						arr[35] != null ? arr[35].toString() : null);
				obj.setSupplierGSTIN(
						arr[36] != null ? arr[36].toString() : null);
				obj.setSupplierTradeName(
						arr[37] != null ? arr[37].toString() : null);
				obj.setSupplierLegalName(
						arr[38] != null ? arr[38].toString() : null);
				obj.setSupplierAddress1(
						arr[39] != null ? arr[39].toString() : null);
				obj.setSupplierAddress2(
						arr[40] != null ? arr[40].toString() : null);
				obj.setSupplierLocation(
						arr[41] != null ? arr[41].toString() : null);
				obj.setSupplierPincode(
						arr[42] != null ? arr[42].toString() : null);
				obj.setSupplierStateCode(
						arr[43] != null ? DownloadReportsConstant.CSVCHARACTER
								.concat(arr[43].toString()) : null);
				obj.setSupplierPhone(
						arr[44] != null ? arr[44].toString() : null);
				obj.setSupplierEmail(
						arr[45] != null ? arr[45].toString() : null);
				obj.setCustomerGSTIN(
						arr[46] != null ? arr[46].toString() : null);
				obj.setCustomerTradeName(
						arr[47] != null ? arr[47].toString() : null);
				obj.setCustomerLegalName(
						arr[48] != null ? arr[48].toString() : null);
				obj.setCustomerAddress1(
						arr[49] != null ? arr[49].toString() : null);
				obj.setCustomerAddress2(
						arr[50] != null ? arr[50].toString() : null);
				obj.setCustomerLocation(
						arr[51] != null ? arr[51].toString() : null);
				obj.setCustomerPincode(
						arr[52] != null ? arr[52].toString() : null);
				obj.setCustomerStateCode(
						arr[53] != null ? DownloadReportsConstant.CSVCHARACTER
								.concat(arr[53].toString()) : null);
				obj.setBillingPOS(
						arr[54] != null ? DownloadReportsConstant.CSVCHARACTER
								.concat(arr[54].toString()) : null);
				obj.setCustomerPhone(
						arr[55] != null ? arr[55].toString() : null);
				obj.setCustomerEmail(
						arr[56] != null ? arr[56].toString() : null);
				obj.setDispatcherGSTIN(
						arr[57] != null ? arr[57].toString() : null);
				obj.setDispatcherTradeName(
						arr[58] != null ? arr[58].toString() : null);
				obj.setDispatcherAddress1(
						arr[59] != null ? arr[59].toString() : null);
				obj.setDispatcherAddress2(
						arr[60] != null ? arr[60].toString() : null);
				obj.setDispatcherLocation(
						arr[61] != null ? arr[61].toString() : null);
				obj.setDispatcherPincode(
						arr[62] != null ? arr[62].toString() : null);
				obj.setDispatcherStateCode(
						arr[63] != null ? DownloadReportsConstant.CSVCHARACTER
								.concat(arr[63].toString()) : null);
				obj.setShipToGSTIN(arr[64] != null ? arr[64].toString() : null);
				obj.setShipToTradeName(
						arr[65] != null ? arr[65].toString() : null);
				obj.setShipToLegalName(
						arr[66] != null ? arr[66].toString() : null);
				obj.setShipToAddress1(
						arr[67] != null ? arr[67].toString() : null);
				obj.setShipToAddress2(
						arr[68] != null ? arr[68].toString() : null);
				obj.setShipToLocation(
						arr[69] != null ? arr[69].toString() : null);
				obj.setShipToPincode(
						arr[70] != null ? arr[70].toString() : null);
				
				 obj.setShipToStateCode( arr[71] != null ? arr[71].toString()
				  : null);
				 
				obj.setShipToStateCode(
						arr[301] != null ? DownloadReportsConstant.CSVCHARACTER
								.concat(arr[301].toString()) : null);
				obj.setItemSerialNumber(
						arr[72] != null ? arr[72].toString() : null);
				obj.setProductSerialNumber(
						arr[73] != null ? arr[73].toString() : null);
				obj.setProductName(arr[74] != null ? arr[74].toString() : null);
				obj.setProductDescription(
						arr[75] != null ? arr[75].toString() : null);
				obj.setIsService(arr[76] != null ? arr[76].toString() : null);
				//obj.setHsn(arr[77] != null ? arr[77].toString() : null);
				obj.setHsn(
						arr[77] != null ? DownloadReportsConstant.CSVCHARACTER
								.concat(arr[77].toString()) : null);
				obj.setBarcode(arr[78] != null ? arr[78].toString() : null);
				obj.setBatchName(arr[79] != null ? arr[79].toString() : null);
				if ((DownloadReportsConstant.PROCESSED).equalsIgnoreCase(type)
						|| (DownloadReportsConstant.ERRORBV)
								.equalsIgnoreCase(type)) {
					if (arr[80] != null) {
						String strdate = arr[80].toString();
						DateTimeFormatter f = new DateTimeFormatterBuilder()
								.appendPattern(OLDFARMATTER).toFormatter();
						LocalDate parsedDate = LocalDate.parse(strdate, f);
						DateTimeFormatter f2 = DateTimeFormatter
								.ofPattern(NEWFARMATTER);
						String newDate = parsedDate.format(f2);
						obj.setBatchExpiryDate(newDate);
					} else {
						obj.setBatchExpiryDate(null);
					}
				} else {
					obj.setBatchExpiryDate(
							arr[80] != null ? arr[80].toString() : null);
				}
				if ((DownloadReportsConstant.PROCESSED).equalsIgnoreCase(type)
						|| (DownloadReportsConstant.ERRORBV)
								.equalsIgnoreCase(type)) {
					if (arr[81] != null) {
						String strdate = arr[81].toString();
						DateTimeFormatter f = new DateTimeFormatterBuilder()
								.appendPattern(OLDFARMATTER).toFormatter();
						LocalDate parsedDate = LocalDate.parse(strdate, f);
						DateTimeFormatter f2 = DateTimeFormatter
								.ofPattern(NEWFARMATTER);
						String newDate = parsedDate.format(f2);
						obj.setWarrantyDate(newDate);
					} else {
						obj.setWarrantyDate(null);
					}
				} else {
					obj.setWarrantyDate(
							arr[81] != null ? arr[81].toString() : null);
				}
				obj.setOrderlineReference(
						arr[82] != null ? arr[82].toString() : null);
				obj.setAttributeName(
						arr[83] != null ? arr[83].toString() : null);
				obj.setAttributeValue(
						arr[84] != null ? arr[84].toString() : null);
				obj.setOriginCountry(
						arr[85] != null ? arr[85].toString() : null);
				obj.setuQC(arr[86] != null ? arr[86].toString() : null);
				obj.setQuantity(arr[87] != null ? arr[87].toString() : null);
				obj.setFreeQuantity(
						arr[88] != null ? arr[88].toString() : null);
				obj.setUnitPrice(arr[89] != null ? arr[89].toString() : null);
				obj.setItemAmount(arr[90] != null ? arr[90].toString() : null);
				obj.setItemDiscount(
						arr[91] != null ? arr[91].toString() : null);
				obj.setPreTaxAmount(
						arr[92] != null ? arr[92].toString() : null);
				obj.setItemAssessableAmount(
						arr[93] != null ? arr[93].toString() : null);
				obj.setiGSTRate(arr[94] != null ? arr[94].toString() : null);
				obj.setiGSTAmount(arr[95] != null ? arr[95].toString() : null);
				obj.setcGSTRate(arr[96] != null ? arr[96].toString() : null);
				obj.setcGSTAmount(arr[97] != null ? arr[97].toString() : null);
				obj.setsGSTRate(arr[98] != null ? arr[98].toString() : null);
				obj.setsGSTAmount(arr[99] != null ? arr[99].toString() : null);
				obj.setCessAdvaloremRate(
						arr[100] != null ? arr[100].toString() : null);
				obj.setCessAdvaloremAmount(
						arr[101] != null ? arr[101].toString() : null);
				obj.setCessSpecificRate(
						arr[102] != null ? arr[102].toString() : null);
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
				obj.setItemOtherCharges(
						arr[108] != null ? arr[108].toString() : null);
				obj.setTotalItemAmount(
						arr[109] != null ? arr[109].toString() : null);
				obj.setInvoiceOtherCharges(
						arr[110] != null ? arr[110].toString() : null);
				obj.setInvoiceAssessableAmount(
						arr[111] != null ? arr[111].toString() : null);
				obj.setInvoiceIGSTAmount(
						arr[112] != null ? arr[112].toString() : null);
				obj.setInvoiceCGSTAmount(
						arr[113] != null ? arr[113].toString() : null);
				obj.setInvoiceSGSTAmount(
						arr[114] != null ? arr[114].toString() : null);
				obj.setInvoiceCessAdvaloremAmount(
						arr[115] != null ? arr[115].toString() : null);
				obj.setInvoiceCessSpecificAmount(
						arr[116] != null ? arr[116].toString() : null);
				obj.setInvoiceStateCessAdvaloremAmount(
						arr[117] != null ? arr[117].toString() : null);
				obj.setInvoiceStateCessSpecificAmount(
						arr[118] != null ? arr[118].toString() : null);
				obj.setInvoiceValue(
						arr[119] != null ? arr[119].toString() : null);
				obj.setRoundOff(arr[120] != null ? arr[120].toString() : null);
				obj.setTotalInvoiceValue(
						arr[121] != null ? arr[121].toString() : null);
				obj.settCSFlagIncomeTax(
						arr[122] != null ? arr[122].toString() : null);
				obj.settCSRateIncomeTax(
						arr[123] != null ? arr[123].toString() : null);
				obj.settCSAmountIncomeTax(
						arr[124] != null ? arr[124].toString() : null);
				obj.setCustomerPANOrAadhaar(
						arr[125] != null ? arr[125].toString() : null);
				obj.setCurrencyCode(
						arr[126] != null ? arr[126].toString() : null);
				obj.setCountryCode(
						arr[127] != null ? arr[127].toString() : null);
				obj.setInvoiceValueFC(
						arr[128] != null ? arr[128].toString() : null);
				obj.setPortCode(arr[129] != null ? arr[129].toString() : null);
				obj.setShippingBillNumber(
						arr[130] != null ? arr[130].toString() : null);
				if ((DownloadReportsConstant.PROCESSED).equalsIgnoreCase(type)
						|| (DownloadReportsConstant.ERRORBV)
								.equalsIgnoreCase(type)) {
					if (arr[131] != null) {
						String strdate = arr[131].toString();
						DateTimeFormatter f = new DateTimeFormatterBuilder()
								.appendPattern(OLDFARMATTER).toFormatter();
						LocalDate parsedDate = LocalDate.parse(strdate, f);
						DateTimeFormatter f2 = DateTimeFormatter
								.ofPattern(NEWFARMATTER);
						String newDate = parsedDate.format(f2);
						obj.setShippingBillDate(newDate);
					} else {
						obj.setShippingBillDate(null);
					}
				} else {
					obj.setShippingBillDate(
							arr[131] != null ? arr[131].toString() : null);
				}
				obj.setInvoiceRemarks(
						arr[132] != null ? arr[132].toString() : null);
				if ((DownloadReportsConstant.PROCESSED).equalsIgnoreCase(type)
						|| (DownloadReportsConstant.ERRORBV)
								.equalsIgnoreCase(type)) {
					if (arr[133] != null) {
						String strdate = arr[133].toString();
						DateTimeFormatter f = new DateTimeFormatterBuilder()
								.appendPattern(OLDFARMATTER).toFormatter();
						LocalDate parsedDate = LocalDate.parse(strdate, f);
						DateTimeFormatter f2 = DateTimeFormatter
								.ofPattern(NEWFARMATTER);
						String newDate = parsedDate.format(f2);
						obj.setInvoicePeriodStartDate(newDate);
					} else {
						obj.setInvoicePeriodStartDate(null);
					}
				} else {
					obj.setInvoicePeriodStartDate(
							arr[133] != null ? arr[133].toString() : null);
				}

				if ((DownloadReportsConstant.PROCESSED).equalsIgnoreCase(type)
						|| (DownloadReportsConstant.ERRORBV)
								.equalsIgnoreCase(type)) {
					if (arr[134] != null) {
						String strdate = arr[134].toString();
						DateTimeFormatter f = new DateTimeFormatterBuilder()
								.appendPattern(OLDFARMATTER).toFormatter();
						LocalDate parsedDate = LocalDate.parse(strdate, f);
						DateTimeFormatter f2 = DateTimeFormatter
								.ofPattern(NEWFARMATTER);
						String newDate = parsedDate.format(f2);
						obj.setInvoicePeriodEndDate(newDate);
					} else {
						obj.setInvoicePeriodEndDate(null);
					}
				} else {
					obj.setInvoicePeriodEndDate(
							arr[134] != null ? arr[134].toString() : null);
				}
				obj.setPreceedingInvoiceNumber(
						arr[135] != null ? arr[135].toString() : null);
				if ((DownloadReportsConstant.PROCESSED).equalsIgnoreCase(type)
						|| (DownloadReportsConstant.ERRORBV)
								.equalsIgnoreCase(type)) {
					if (arr[136] != null) {
						String strdate = arr[136].toString();
						DateTimeFormatter f = new DateTimeFormatterBuilder()
								.appendPattern(OLDFARMATTER).toFormatter();
						LocalDate parsedDate = LocalDate.parse(strdate, f);
						DateTimeFormatter f2 = DateTimeFormatter
								.ofPattern(NEWFARMATTER);
						String newDate = parsedDate.format(f2);
						obj.setPreceedingInvoiceDate(newDate);
					} else {
						obj.setPreceedingInvoiceDate(null);
					}
				} else {
					obj.setPreceedingInvoiceDate(
							arr[136] != null ? arr[136].toString() : null);
				}
				obj.setOtherReference(
						arr[137] != null ? arr[137].toString() : null);
				obj.setReceiptAdviceReference(
						arr[138] != null ? arr[138].toString() : null);
				obj.setReceiptAdviceDate(
						arr[139] != null ? arr[139].toString() : null);
				obj.setTenderReference(
						arr[140] != null ? arr[140].toString() : null);
				obj.setContractReference(
						arr[141] != null ? arr[141].toString() : null);
				obj.setExternalReference(
						arr[142] != null ? arr[142].toString() : null);
				obj.setProjectReference(
						arr[143] != null ? arr[143].toString() : null);
				obj.setCustomerPOReferenceNumber(
						arr[144] != null ? arr[144].toString() : null);
				obj.setCustomerPOReferenceDate(
						arr[145] != null ? arr[145].toString() : null);
				obj.setPayeeName(arr[146] != null ? arr[146].toString() : null);
				obj.setModeOfPayment(
						arr[147] != null ? arr[147].toString() : null);
				obj.setBranchOrIFSCCode(
						arr[148] != null ? arr[148].toString() : null);
				obj.setPaymentTerms(
						arr[149] != null ? arr[149].toString() : null);
				obj.setPaymentInstruction(
						arr[150] != null ? arr[150].toString() : null);
				obj.setCreditTransfer(
						arr[151] != null ? arr[151].toString() : null);
				obj.setDirectDebit(
						arr[152] != null ? arr[152].toString() : null);
				obj.setCreditDays(
						arr[153] != null ? arr[153].toString() : null);
				obj.setPaidAmount(
						arr[154] != null ? arr[154].toString() : null);
				obj.setBalanceAmount(
						arr[155] != null ? arr[155].toString() : null);
				if ((DownloadReportsConstant.PROCESSED).equalsIgnoreCase(type)
						|| (DownloadReportsConstant.ERRORBV)
								.equalsIgnoreCase(type)) {
					if (arr[156] != null) {
						String strdate = arr[156].toString();
						DateTimeFormatter f = new DateTimeFormatterBuilder()
								.appendPattern(OLDFARMATTER).toFormatter();
						LocalDate parsedDate = LocalDate.parse(strdate, f);
						DateTimeFormatter f2 = DateTimeFormatter
								.ofPattern(NEWFARMATTER);
						String newDate = parsedDate.format(f2);
						obj.setPaymentDueDate(newDate);
					} else {
						obj.setPaymentDueDate(null);
					}
				} else {
					obj.setPaymentDueDate(
							arr[156] != null ? arr[156].toString() : null);
				}
				obj.setAccountDetail(
						arr[157] != null ? arr[157].toString() : null);
				obj.setEcomGSTIN(arr[158] != null ? arr[158].toString() : null);
				obj.setEcomTransactionID(
						arr[159] != null ? arr[159].toString() : null);
				obj.setSupportingDocURL(
						arr[160] != null ? arr[160].toString() : null);
				obj.setSupportingDocument(
						arr[161] != null ? arr[161].toString() : null);
				obj.setAdditionalInformation(
						arr[162] != null ? arr[162].toString() : null);
				obj.setTransactionType(
						arr[163] != null ? arr[163].toString() : null);
				obj.setSubSupplyType(
						arr[164] != null ? arr[164].toString() : null);
				obj.setOtherSupplyTypeDescription(
						arr[165] != null ? arr[165].toString() : null);
				obj.setTransporterID(
						arr[166] != null ? arr[166].toString() : null);
				obj.setTransporterName(
						arr[167] != null ? arr[167].toString() : null);
				obj.setTransportMode(
						arr[168] != null ? arr[168].toString() : null);
				obj.setTransportDocNo(
						arr[169] != null ? arr[169].toString() : null);
				if ((DownloadReportsConstant.PROCESSED).equalsIgnoreCase(type)
						|| (DownloadReportsConstant.ERRORBV)
								.equalsIgnoreCase(type)) {
					if (arr[170] != null) {
						String strdate = arr[170].toString();
						DateTimeFormatter f = new DateTimeFormatterBuilder()
								.appendPattern(OLDFARMATTER).toFormatter();
						LocalDate parsedDate = LocalDate.parse(strdate, f);
						DateTimeFormatter f2 = DateTimeFormatter
								.ofPattern(NEWFARMATTER);
						String newDate = parsedDate.format(f2);
						obj.setTransportDocDate(newDate);
					} else {
						obj.setTransportDocDate(null);
					}
				} else {
					obj.setTransportDocDate(
							arr[170] != null ? arr[170].toString() : null);
				}
				obj.setDistance(arr[171] != null ? arr[171].toString() : null);
				obj.setVehicleNo(arr[172] != null ? arr[172].toString() : null);
				obj.setVehicleType(
						arr[173] != null ? arr[173].toString() : null);
				obj.setReturnPeriod(
						arr[174] != null ? DownloadReportsConstant.CSVCHARACTER
								.concat(arr[174].toString()) : null);
				obj.setOriginalDocumentType(
						arr[175] != null ? arr[175].toString() : null);
				obj.setOriginalCustomerGSTIN(
						arr[176] != null ? arr[176].toString() : null);
				obj.setDifferentialPercentageFlag(
						arr[177] != null ? arr[177].toString() : null);
				obj.setSec7ofIGSTFlag(
						arr[178] != null ? arr[178].toString() : null);
				obj.setClaimRefndFlag(
						arr[179] != null ? arr[179].toString() : null);
				obj.setAutoPopltToRefund(
						arr[180] != null ? arr[180].toString() : null);
				obj.setcRDRPreGST(
						arr[181] != null ? arr[181].toString() : null);
				obj.setCustomerType(
						arr[182] != null ? arr[182].toString() : null);
				obj.setCustomerCode(
						arr[183] != null ? arr[183].toString() : null);
				obj.setProductCode(
						arr[184] != null ? arr[184].toString() : null);
				obj.setCategoryOfProduct(
						arr[185] != null ? arr[185].toString() : null);
				obj.setiTCFlag(arr[186] != null ? arr[186].toString() : null);
				obj.setStateApplyingCess(
						arr[187] != null ? arr[187].toString() : null);
				obj.setfOB(arr[188] != null ? arr[188].toString() : null);
				obj.setExportDuty(
						arr[189] != null ? arr[189].toString() : null);
				obj.setExchangeRate(
						arr[190] != null ? arr[190].toString() : null);
				obj.setReasonForCreditDebitNote(
						arr[191] != null ? arr[191].toString() : null);
				obj.settCSFlagGST(
						arr[192] != null ? arr[192].toString() : null);
				obj.settCSIGSTAmount(
						arr[193] != null ? arr[193].toString() : null);
				obj.settCSCGSTAmount(
						arr[194] != null ? arr[194].toString() : null);
				obj.settCSSGSTAmount(
						arr[195] != null ? arr[195].toString() : null);
				obj.settDSFlagGST(
						arr[196] != null ? arr[196].toString() : null);
				obj.settDSIGSTAmount(
						arr[197] != null ? arr[197].toString() : null);
				obj.settDSCGSTAmount(
						arr[198] != null ? arr[198].toString() : null);
				obj.settDSSGSTAmount(
						arr[199] != null ? arr[199].toString() : null);
				obj.setUserId(arr[200] != null ? arr[200].toString() : null);
				obj.setCompanyCode(
						arr[201] != null ? arr[201].toString() : null);
				obj.setSourceIdentifier(
						arr[202] != null ? arr[202].toString() : null);
				obj.setSourceFileName(
						arr[203] != null ? arr[203].toString() : null);
				obj.setPlantCode(arr[204] != null ? arr[204].toString() : null);
				obj.setDivision(arr[205] != null ? arr[205].toString() : null);
				obj.setSubDivision(
						arr[206] != null ? arr[206].toString() : null);
				obj.setLocation(arr[207] != null ? arr[207].toString() : null);
				obj.setSalesOrganisation(
						arr[208] != null ? arr[208].toString() : null);
				obj.setDistributionChannel(
						arr[209] != null ? arr[209].toString() : null);
				obj.setProfitCentre1(
						arr[210] != null ? arr[210].toString() : null);
				obj.setProfitCentre2(
						arr[211] != null ? arr[211].toString() : null);
				obj.setProfitCentre3(
						arr[212] != null ? arr[212].toString() : null);
				obj.setProfitCentre4(
						arr[213] != null ? arr[213].toString() : null);
				obj.setProfitCentre5(
						arr[214] != null ? arr[214].toString() : null);
				obj.setProfitCentre6(
						arr[215] != null ? arr[215].toString() : null);
				obj.setProfitCentre7(
						arr[216] != null ? arr[216].toString() : null);
				obj.setProfitCentre8(
						arr[217] != null ? arr[217].toString() : null);
				obj.setGlAssessableValue(
						arr[218] != null ? arr[218].toString() : null);
				obj.setGlIGST(arr[219] != null ? arr[219].toString() : null);
				obj.setGlCGST(arr[220] != null ? arr[220].toString() : null);
				obj.setGlSGST(arr[221] != null ? arr[221].toString() : null);
				obj.setGlAdvaloremCess(
						arr[222] != null ? arr[222].toString() : null);
				obj.setGlSpecificCess(
						arr[223] != null ? arr[223].toString() : null);
				obj.setgLStateCessAdvalorem(
						arr[224] != null ? arr[224].toString() : null);
				obj.setgLStateCessSpecific(
						arr[225] != null ? arr[225].toString() : null);
				if ((DownloadReportsConstant.PROCESSED).equalsIgnoreCase(type)
						|| (DownloadReportsConstant.ERRORBV)
								.equalsIgnoreCase(type)) {
					if (arr[226] != null) {
						String strdate = arr[226].toString();
						DateTimeFormatter f = new DateTimeFormatterBuilder()
								.appendPattern(OLDFARMATTER).toFormatter();
						LocalDate parsedDate = LocalDate.parse(strdate, f);
						DateTimeFormatter f2 = DateTimeFormatter
								.ofPattern(NEWFARMATTER);
						String newDate = parsedDate.format(f2);
						obj.setGlPostingDate(newDate);
					} else {
						obj.setGlPostingDate(null);
					}
				} else {
					obj.setGlPostingDate(
							arr[226] != null ? arr[226].toString() : null);
				}
				obj.setSalesOrderNumber(
						arr[227] != null ? arr[227].toString() : null);
				obj.seteWBNumber(arr[228] != null ? arr[228].toString() : null);
				obj.seteWBDate(arr[229] != null ? arr[229].toString() : null);
				obj.setAccountingVoucherNumber(
						arr[230] != null ? arr[230].toString() : null);
				if ((DownloadReportsConstant.PROCESSED).equalsIgnoreCase(type)
						|| (DownloadReportsConstant.ERRORBV)
								.equalsIgnoreCase(type)) {
					if (arr[231] != null) {
						String strdate = arr[231].toString();
						DateTimeFormatter f = new DateTimeFormatterBuilder()
								.appendPattern(OLDFARMATTER).toFormatter();
						LocalDate parsedDate = LocalDate.parse(strdate, f);
						DateTimeFormatter f2 = DateTimeFormatter
								.ofPattern(NEWFARMATTER);
						String newDate = parsedDate.format(f2);
						obj.setAccountingVoucherDate(newDate);
					} else {
						obj.setAccountingVoucherDate(null);
					}
				} else {
					obj.setAccountingVoucherDate(
							arr[231] != null ? arr[231].toString() : null);
				}
				obj.setDocumentReferenceNumber(
						arr[232] != null ? arr[232].toString() : null);
				obj.setCustomerTAN(
						arr[233] != null ? arr[233].toString() : null);
				obj.setUserDefField1(
						arr[234] != null ? arr[234].toString() : null);
				obj.setUserDefField2(
						arr[235] != null ? arr[235].toString() : null);
				obj.setUserDefField3(
						arr[236] != null ? arr[236].toString() : null);
				obj.setUserDefField4(
						arr[237] != null ? arr[237].toString() : null);
				obj.setUserDefField5(
						arr[238] != null ? arr[238].toString() : null);
				obj.setUserDefField6(
						arr[239] != null ? arr[239].toString() : null);
				obj.setUserDefField7(
						arr[240] != null ? arr[240].toString() : null);
				obj.setUserDefField8(
						arr[241] != null ? arr[241].toString() : null);
				obj.setUserDefField9(
						arr[242] != null ? arr[242].toString() : null);
				obj.setUserDefField10(
						arr[243] != null ? arr[243].toString() : null);
				obj.setUserDefField11(
						arr[244] != null ? arr[244].toString() : null);
				obj.setUserDefField12(
						arr[245] != null ? arr[245].toString() : null);
				obj.setUserDefField13(
						arr[246] != null ? arr[246].toString() : null);
				obj.setUserDefField14(
						arr[247] != null ? arr[247].toString() : null);
				obj.setUserDefField15(
						arr[248] != null ? arr[248].toString() : null);
				obj.setUserDefField16(
						arr[249] != null ? arr[249].toString() : null);
				obj.setUserDefField17(
						arr[250] != null ? arr[250].toString() : null);
				obj.setUserDefField18(
						arr[251] != null ? arr[251].toString() : null);
				obj.setUserDefField19(
						arr[252] != null ? arr[252].toString() : null);
				obj.setUserDefField20(
						arr[253] != null ? arr[253].toString() : null);
				obj.setUserDefField21(
						arr[254] != null ? arr[254].toString() : null);
				obj.setUserDefField22(
						arr[255] != null ? arr[255].toString() : null);
				obj.setUserDefField23(
						arr[256] != null ? arr[256].toString() : null);
				obj.setUserDefField24(
						arr[257] != null ? arr[257].toString() : null);
				obj.setUserDefField25(
						arr[258] != null ? arr[258].toString() : null);
				obj.setUserDefField26(
						arr[259] != null ? arr[259].toString() : null);
				obj.setUserDefField27(
						arr[260] != null ? arr[260].toString() : null);
				obj.setUserDefField28(
						arr[261] != null ? arr[261].toString() : null);
				obj.setUserDefField29(
						arr[262] != null ? arr[262].toString() : null);
				obj.setUserDefField30(
						arr[263] != null ? arr[263].toString() : null);
				obj.setSupplyTypeASP(
						arr[264] != null ? arr[264].toString() : null);
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
				obj.setInvoiceValueASP(
						arr[279] != null ? arr[279].toString() : null);
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
				obj.setRecordStatus(
						arr[290] != null ? arr[290].toString() : null);
			}
		}

		return obj;
	}

	private String createQueryString(String buildQuery, String buildQuery1,
			String buildRetTypeTableNo, String buildTaxDocType,
			int noOfRowsToFetch, int pageNo) {
		String queryStr = "";
		
		pageNo = pageNo * noOfRowsToFetch;
		

		if (DownloadReportsConstant.PROCESSED.equalsIgnoreCase(type))
				 {

			queryStr = "SELECT  '' AS ERROR_CODE_ASP,"
					+ " '' AS ERROR_DESCRIPTION_ASP,TRIM(', ' FROM IFNULL"
					+ "(ERRH.INFO_ERROR_CODE_ASP,'') ||','|| IFNULL"
					+ "(ERRI.INFO_ERROR_CODE_ASP,'')) AS INFO_ERROR_CODE_ASP, "
					+ "TRIM(', ' FROM IFNULL(ERRH.INFO_ERROR_DESCRIPTION_ASP,'') "
					+ "||','|| IFNULL(ERRI.INFO_ERROR_DESCRIPTION_ASP,'')) AS "
					+ "INFO_ERROR_DESCRIPTION_ASP,EINV.STATUS_DESCRIPTION AS "
					+ "IRN_STATUS, EINV.IRN AS IRN_NO, EINV.ACK_NUM AS IRN_ACK_NO, "
					+ "HDR.IRN_DATE AS IRN_ACK_DATE_TIME, EINV.SIGNED_QR AS "
					+ "SIGNED_QR_CODE, EINV.SIGNED_INV AS SIGNED_INVOICE, "
					+ "HDR.EINV_ERROR_CODE AS IRP_ERROR_CODE, HDR.EINV_ERROR_DESC "
					+ "AS IRP_ERROR_DESCRIPTION, "
				    + "case when HDR.EWB_STATUS = 1 then 'Not Applicable' "
				    + "when HDR.EWB_STATUS = 2 then 'Pending' "
				    + "when HDR.EWB_STATUS = 3 then 'Generation Error' "
                    + "when HDR.EWB_STATUS = 4 then 'Part A Generated' "
                    + "when HDR.EWB_STATUS = 5 then 'EWB Active' "
                    + "when HDR.EWB_STATUS = 6 then 'Cancelled' "
                    + "when HDR.EWB_STATUS = 7 then 'Discarded' "
                    + "when HDR.EWB_STATUS = 8 then 'Rejected' "
                    + "when HDR.EWB_STATUS = 9 then 'Expired' end as EWBPartAStatusA,"
					+ "'' AS EWBValidUpTo, HDR.EWAY_BILL_NUM AS EWBNO,"
					+ "CAST(HDR.EWAY_BILL_DATE AS DATE) AS EWBDATE, "
					+ "HDR.EWB_ERROR_CODE AS EWB_ERROR_CODE, HDR.EWB_ERROR_DESC "
					+ "AS EWB_ERROR_DESCRIPTION, case when HDR.IS_SAVED_TO_GSTN = "
					+ "TRUE THEN 'IS_SAVED' WHEN HDR.IS_SAVED_TO_GSTN = FALSE AND "
					+ "HDR.GSTN_ERROR = TRUE THEN 'IS_ERROR' WHEN HDR.IS_SAVED_TO_GSTN = "
					+ "FALSE AND HDR.GSTN_ERROR = FALSE THEN 'NOT_SAVED' END AS "
					+ "GSTIN_STATUS, GSB.GSTN_SAVE_REF_ID AS GSTIN_REF_ID, "
					+ "GSB.GSTN_RESP_DATE AS GSTIN_REFID_DATE_TIME, TRIM(', ' "
					+ "FROM IFNULL(ERRH.ERROR_CODE_GSTIN,'') ||','|| IFNULL"
					+ "(ERRI.ERROR_CODE_GSTN,'')) AS GSTIN_ERROR_CODE, TRIM(', ' "
					+ "FROM IFNULL(ERRH.ERROR_DESCRIPTION_GSTIN,'') ||','|| "
					+ "IFNULL(ERRI.ERROR_DESCRIPTION_GSTN,'')) AS "
					+ "GSTIN_ERROR_DESCRIPTION_ASP," + buildRetTypeTableNo
					+ "HDR.IRN, HDR.IRN_DATE,HDR.TAX_SCHEME,"
					+ "HDR.CANCEL_REASON, HDR.CANCEL_REMARKS, ITM.SUPPLY_TYPE,"
					+ "HDR.DOC_CATEGORY, HDR.DOC_TYPE,HDR.DOC_NUM,HDR.DOC_DATE,"
					+ "HDR.REVERSE_CHARGE, HDR.SUPPLIER_GSTIN,HDR.SUPP_TRADE_NAME,"
					+ "HDR.SUPP_LEGAL_NAME, HDR.SUPP_BUILDING_NUM,"
					+ "HDR.CUST_SUPP_ADDRESS2 AS SUPPLIERADDRESS2,HDR.SUPP_LOCATION, HDR.SUPP_PINCODE,"
					+ "HDR.SUPP_STATE_CODE,HDR.SUPP_PHONE, HDR.SUPP_EMAIL,"
					+ "HDR.CUST_GSTIN,HDR.CUST_TRADE_NAME, HDR.CUST_SUPP_NAME,"
					+ "HDR.CUST_SUPP_ADDRESS1,HDR.CUST_SUPP_ADDRESS2, "
					+ "HDR.CUST_SUPP_ADDRESS4,HDR.CUST_PINCODE,HDR.BILL_TO_STATE, "
					+ "HDR.POS,HDR.CUST_PHONE,HDR.CUST_EMAIL,HDR.DISPATCHER_GSTIN,"
					+ "HDR.DISPATCHER_TRADE_NAME,HDR.DISPATCHER_BUILDING_NUM,"
					+ "HDR.DISPATCHER_BUILDING_NAME,HDR.DISPATCHER_LOCATION,"
					+ "HDR.DISPATCHER_PINCODE,HDR.DISPATCHER_STATE_CODE, '' AS "
					+ "SHIP_TO_FLOOR_NUM,HDR.SHIP_TO_TRADE_NAME,"
					+ "HDR.SHIP_TO_LEGAL_NAME,HDR.SHIP_TO_BUILDING_NUM,"
					+ "HDR.SHIP_TO_BUILDING_NAME,HDR.SHIP_TO_LOCATION,"
					+ "ITM.SHIP_TO_PINCODE,ITM.SHIP_TO_STATE,ITM.ITM_NO,"
					+ "ITM.SERIAL_NUM2,ITM.PRODUCT_NAME,ITM.ITM_DESCRIPTION,"
					+ "ITM.IS_SERVICE,ITM.ITM_HSNSAC,ITM.BAR_CODE,"
					+ "ITM.BATCH_NAME_OR_NUM,ITM.BATCH_EXPIRY_DATE,"
					+ "ITM.WARRANTY_DATE, ITM.ORDER_ITEM_REFERENCE,ITM.ATTRIBUTE_NAME,"
					+ "ITM.ATTRIBUTE_VALUE, ITM.ORIGIN_COUNTRY,ITM.ITM_UQC,ITM.ITM_QTY,"
					+ "ITM.FREE_QTY, ITM.UNIT_PRICE, ITM.ITEM_AMT_UP_QTY,"
					+ "ITM.ITEM_DISCOUNT,ITM.PRE_TAX_AMOUNT,ITM.TAXABLE_VALUE,"
					+ "ITM.IGST_RATE, ITM.IGST_AMT,ITM.CGST_RATE,ITM.CGST_AMT,"
					+ "ITM.SGST_RATE,ITM.SGST_AMT,ITM.CESS_RATE_ADVALOREM,"
					+ "ITM.CESS_AMT_ADVALOREM, ITM.CESS_RATE_SPECIFIC,"
					+ "ITM.CESS_AMT_SPECIFIC, ITM.STATECESS_RATE,ITM.STATECESS_AMT,"
					+ "ITM.STATE_CESS_SPECIFIC_RATE, ITM.STATE_CESS_SPECIFIC_AMOUNT,"
					+ "ITM.OTHER_VALUES,TOT_ITEM_AMT, ITM.INV_OTHER_CHARGES,"
					+ "ITM.INV_ASSESSABLE_AMT,ITM.INV_IGST_AMT,ITM.INV_CGST_AMT,"
					+ "ITM.INV_SGST_AMT,ITM.INV_CESS_ADVLRM_AMT,"
					+ "ITM.INV_CESS_SPECIFIC_AMT,ITM.INV_STATE_CESS_AMT,"
					+ "ITM.INV_STATE_CESS_SPECIFIC_AMOUNT,ITM.LINE_ITEM_AMT,"
					+ "HDR.ROUND_OFF,HDR.TOT_INV_VAL_WORLDS,HDR.TCS_FLAG_INCOME_TAX,"
					+ "ITM.TCS_RATE_INCOME_TAX,ITM.TCS_AMOUNT_INCOME_TAX,"
					+ "CUSTOMER_PAN_OR_AADHAAR, HDR.FOREIGN_CURRENCY,HDR.COUNTRY_CODE, "
					+ "HDR.INV_VAL_FC, HDR.SHIP_PORT_CODE,HDR.SHIP_BILL_NUM,"
					+ "HDR.SHIP_BILL_DATE, HDR.INV_REMARKS,HDR.INV_PERIOD_START_DATE,"
					+ "HDR.INV_PERIOD_END_DATE, ITM.PRECEEDING_INV_NUM,"
					+ "ITM.PRECEEDING_INV_DATE,ITM.INV_REFERENCE,"
					+ "ITM.RECEIPT_ADVICE_REFERENCE,ITM.RECEIPT_ADVICE_DATE,"
					+ "ITM.TENDER_REFERENCE ,ITM.CONTRACT_REFERENCE,"
					+ "ITM.EXTERNAL_REFERENCE, ITM.PROJECT_REFERENCE,"
					+ "ITM.CUST_PO_REF_NUM,ITM.CUST_PO_REF_DATE,HDR.PAYEE_NAME,"
					+ "HDR.MODE_OF_PAYMENT,HDR.BRANCH_IFSC_CODE,HDR.PAYMENT_TERMS,"
					+ "HDR.PAYMENT_INSTRUCTION,HDR.CR_TRANSFER, HDR.DB_DIRECT,"
					+ "HDR.CR_DAYS,ITM.PAID_AMT,ITM.BAL_AMT, HDR.PAYMENT_DUE_DATE,"
					+ "HDR.ACCOUNT_DETAIL,HDR.ECOM_GSTIN, HDR.ECOM_TRANS_ID,"
					+ "ITM.SUPPORTING_DOC_URL,ITM.SUPPORTING_DOC_BASE64,"
					+ "ITM.ADDITIONAL_INFORMATION,HDR.TRANS_TYPE,HDR.SUB_SUPP_TYPE,"
					+ "HDR.OTHER_SUPP_TYPE_DESC,HDR.TRANSPORTER_ID,"
					+ "HDR.TRANSPORTER_NAME, HDR.TRANSPORT_MODE,HDR.TRANSPORT_DOC_NUM,"
					+ "HDR.TRANSPORT_DOC_DATE, HDR.DISTANCE ,HDR.VEHICLE_NUM,"
					+ "HDR.VEHICLE_TYPE, HDR.RETURN_PERIOD,HDR.ORIGINAL_DOC_TYPE,"
					+ "HDR.ORIGINAL_CUST_GSTIN, HDR.DIFF_PERCENT,"
					+ "HDR.SECTION7_OF_IGST_FLAG,HDR.CLAIM_REFUND_FLAG,"
					+ "HDR.AUTOPOPULATE_TO_REFUND,HDR.CRDR_PRE_GST,HDR.CUST_SUPP_TYPE,"
					+ "HDR.CUST_SUPP_CODE AS CUSTOMERCODE,ITM.PRODUCT_CODE,ITM.ITM_TYPE,"
					+ "ITM.ITC_FLAG,HDR.STATE_APPLYING_CESS,ITM.FOB,"
					+ "ITM.EXPORT_DUTY,HDR.EXCHANGE_RATE,ITM.CRDR_REASON,"
					+ "HDR.TCS_FLAG ,ITM.TCS_AMT,TCS_CGST_AMT,TCS_SGST_AMT,"
					+ "HDR.TDS_FLAG,TDS_IGST_AMT,TDS_CGST_AMT,TDS_SGST_AMT,"
					+ "HDR.USER_ID AS userid,HDR.COMPANY_CODE,HDR.SOURCE_IDENTIFIER,"
					+ "HDR.SOURCE_FILENAME,ITM.PLANT_CODE,HDR.DIVISION,"
					+ "SUB_DIVISION,ITM.LOCATION,HDR.SALES_ORGANIZATION,"
					+ "HDR.DISTRIBUTION_CHANNEL,ITM.PROFIT_CENTRE,HDR.PROFIT_CENTRE2, "
					+ "HDR.USERACCESS1,HDR.USERACCESS2,HDR.USERACCESS3,"
					+ "HDR.USERACCESS4,HDR.USERACCESS5,HDR.USERACCESS6,"
					+ "ITM.GLCODE_TAXABLEVALUE,ITM.GLCODE_IGST,ITM.GLCODE_CGST,"
					+ "ITM.GLCODE_SGST,ITM.GLCODE_ADV_CESS,ITM.GLCODE_SP_CESS,"
					+ "ITM.GLCODE_STATE_CESS, HDR.GL_STATE_CESS_SPECIFIC,"
					+ "HDR.GL_POSTING_DATE,HDR.SALES_ORD_NUM,HDR.EWAY_BILL_NUM,"
					+ "CAST(HDR.EWAY_BILL_DATE AS DATE),HDR.ACCOUNTING_VOUCHER_NUM,"
					+ "HDR.ACCOUNTING_VOUCHER_DATE, ITM.DOCUMENT_REFERENCE_NUMBER,"
					+ "HDR.CUST_TAN,ITM.USERDEFINED_FIELD1,ITM.USERDEFINED_FIELD2,"
					+ "ITM.USERDEFINED_FIELD3,ITM.USERDEFINED_FIELD4,"
					+ "ITM.USERDEFINED_FIELD5,ITM.USERDEFINED_FIELD6,"
					+ "ITM.USERDEFINED_FIELD7,ITM.USERDEFINED_FIELD8,"
					+ "ITM.USERDEFINED_FIELD9,ITM.USERDEFINED_FIELD10,"
					+ "ITM.USERDEFINED_FIELD11,ITM.USERDEFINED_FIELD12,"
					+ "ITM.USERDEFINED_FIELD13,ITM.USERDEFINED_FIELD14,"
					+ "ITM.USERDEFINED_FIELD15,ITM.USERDEFINED_FIELD16,"
					+ "ITM.USERDEFINED_FIELD17,ITM.USERDEFINED_FIELD18,"
					+ "ITM.USERDEFINED_FIELD19, ITM.USERDEFINED_FIELD20,"
					+ "ITM.USERDEFINED_FIELD21,ITM.USERDEFINED_FIELD22,"
					+ "ITM.USERDEFINED_FIELD23,ITM.USERDEFINED_FIELD24,"
					+ "ITM.USERDEFINED_FIELD25, ITM.USERDEFINED_FIELD26,"
					+ "ITM.USERDEFINED_FIELD27,ITM.USERDEFINED_FIELD28,"
					+ "ITM.USERDEFINED_FIELD29,ITM.USERDEFINED_FIELD30,"
					+ "HDR.SUPPLY_TYPE AS SUPPLYTYPEASP, "
					+ "'' AS ApproximateDistance_ASP, '' AS "
					+ "DistanceSavedtoEWB, HDR.CREATED_BY as USER_ID,HDR.FILE_ID,"
					+ "FIL.FILE_NAME as FILE_NAME,HDR.INV_OTHER_CHARGES as InvoiceOtherChargesASP,"
					+ "HDR.INV_ASSESSABLE_AMT as InvoiceAssessableAmountASP, "
					+ "HDR.INV_IGST_AMT AS InvoiceIGSTAmountASP,HDR.INV_CGST_AMT as InvoiceCGSTAmountASP,"
					+ "HDR.INV_SGST_AMT as InvoiceSGSTAmountASP,HDR.INV_CESS_ADVLRM_AMT "
					+ "as InvoiceCessAdvaloremAmountASP,"
					+ "HDR.INV_CESS_SPECIFIC_AMT as InvoiceCessSpecificAmountASP, "
					+ "HDR.INV_STATE_CESS_AMT as InvoiceStateCessAdvaloremAmountASP,"
					+ "HDR.INV_STATE_CESS_SPECIFIC_AMOUNT AS InvoiceStateCessSpecificAmountASP,"
					+ "HDR.DOC_AMT,"
					+ "HDR.MEMO_VALUE_IGST,HDR.MEMO_VALUE_CGST,HDR.MEMO_VALUE_SGST,"
					+ "ITM.MEMO_VALUE_CESS_ADV AS CessAdvaloremAmount_ASP,"
					+ "ITM.MEMO_VALUE_STATE_CESS_ADV AS StateCessAdvaloremAmount_ASP,"
					+ "ITM.IGST_RET1_IMPACT,ITM.CGST_RET1_IMPACT,ITM.SGST_RET1_IMPACT,"
					+ "ITM.CESS_ADV_RET1_IMPACT AS CessAdvaloremAmount_Difference,"
					+ "ITM.STATE_CESS_ADV_RET1_IMPACT AS StateCessAdvaloremAmount_Difference,"
					+ "CASE WHEN HDR.IS_DELETE = TRUE THEN 'INACTIVE' WHEN "
					+ "HDR.IS_DELETE = FALSE THEN 'ACTIVE'END AS RECORD_STATUS,"
					+ "ITM.ADJ_REF_NO,ITM.ADJ_REF_DATE, ITM.ADJ_TAXABLE_VALUE,"
					+ "ITM.ADJ_IGST_AMT,ITM.ADJ_CGST_AMT, ITM.ADJ_SGST_AMT,"
					+ "ITM.ADJ_CESS_AMT_ADVALOREM, ITM.ADJ_CESS_AMT_SPECIFIC,"
					+ "ITM.ADJ_STATECESS_AMT, ITM.TCS_AMT AS Tcsamount,"
					+ "HDR.CUST_SUPP_ADDRESS3," + buildTaxDocType
					+ " FROM ANX_OUTWARD_DOC_HEADER HDR LEFT OUTER JOIN "
					+ "TF_OUTWARD_HEADER_GSTIN_ASP_ERROR_INFO ()ERRH ON "
					+ "HDR.ID = ERRH.DOC_HEADER_ID INNER JOIN ANX_OUTWARD_DOC_ITEM "
					+ "ITM ON HDR.ID = ITM.DOC_HEADER_ID AND HDR.DERIVED_RET_PERIOD = "
					+ "ITM.DERIVED_RET_PERIOD LEFT OUTER JOIN "
					+ "TF_OUTWARD_ITEM_GSTIN_ASP_ERROR_INFO ()ERRI ON "
					+ "ITM.DOC_HEADER_ID = ERRI.DOC_HEADER_ID AND ITM.ITEM_INDEX "
					+ "= ERRI.ITEM_INDEX AND "
					+ "TO_CHAR(ITM.ITM_NO) = TO_CHAR(ERRI.ITM_NO) "
					+ "INNER JOIN "
					+ "FILE_STATUS FIL ON HDR.FILE_ID=FIL.ID LEFT OUTER JOIN "
					+ "GSTR1_GSTN_SAVE_BATCH GSB ON GSB.ID = HDR.BATCH_ID LEFT "
					+ "OUTER JOIN EINV_MASTER EINV ON EINV.DOC_HEADER_ID = HDR.ID "
					+ "AND ITM.DOC_HEADER_ID = EINV.DOC_HEADER_ID WHERE "
					+ buildQuery + " AND " + buildQuery1
					+ " ORDER BY DOC_NUM,ITM.ITM_NO ";
			
		}else if (DownloadReportsConstant.ERRORBV.equalsIgnoreCase(type)){
		queryStr = " SELECT TRIM(', ' FROM IFNULL(HDR.ERROR_CODES,'')  ||','|| IFNULL(ITM.ERROR_CODES,'')) AS ERROR_CODE_ASP, "
				+" '' AS  ERROR_DESCRIPTION_ASP,  "
				+" '' AS INFO_ERROR_CODE_ASP, "
				+" '' AS  INFO_ERROR_DESCRIPTION_ASP, EINV.STATUS_DESCRIPTION AS  "
				+" IRN_STATUS, EINV.IRN AS IRN_NO, EINV.ACK_NUM AS IRN_ACK_NO,  "
				+" HDR.IRN_DATE AS IRN_ACK_DATE_TIME, EINV.SIGNED_QR AS  "
				+" SIGNED_QR_CODE, EINV.SIGNED_INV AS SIGNED_INVOICE,  "
				+" HDR.EINV_ERROR_CODE AS IRP_ERROR_CODE, HDR.EINV_ERROR_DESC  "
				+" AS IRP_ERROR_DESCRIPTION, "
				+" case when HDR.EWB_STATUS = 1 then 'Not Applicable'  "
				+" when HDR.EWB_STATUS = 2 then 'Pending'  "
				+" when HDR.EWB_STATUS = 3 then 'Generation Error'  "
				+" when HDR.EWB_STATUS = 4 then 'Part A Generated'  "
				+" when HDR.EWB_STATUS = 5 then 'EWB Active'  "
				+" when HDR.EWB_STATUS = 6 then 'Cancelled'  "
				+" when HDR.EWB_STATUS = 7 then 'Discarded'  "
				+" when HDR.EWB_STATUS = 8 then 'Rejected'  "
				+" when HDR.EWB_STATUS = 9 then 'Expired' end as EWBPartAStatusA, "
				+" '' AS EWBValidUpTo, HDR.EWAY_BILL_NUM AS EWBNO, "
				+" CAST(HDR.EWAY_BILL_DATE AS DATE) AS EWBDATE,  "
				+" HDR.EWB_ERROR_CODE AS EWB_ERROR_CODE, HDR.EWB_ERROR_DESC  "
				+" AS EWB_ERROR_DESCRIPTION, case when HDR.IS_SAVED_TO_GSTN =  "
				+" TRUE THEN 'IS_SAVED' WHEN HDR.IS_SAVED_TO_GSTN = FALSE AND  "
				+" HDR.GSTN_ERROR = TRUE THEN 'IS_ERROR' WHEN HDR.IS_SAVED_TO_GSTN =  "
				+" FALSE AND HDR.GSTN_ERROR = FALSE THEN 'NOT_SAVED' END AS  "
				+" GSTIN_STATUS, GSB.GSTN_SAVE_REF_ID AS GSTIN_REF_ID,  "
				+" GSB.GSTN_RESP_DATE AS GSTIN_REFID_DATE_TIME, '' AS GSTIN_ERROR_CODE,  "
				+" '' AS GSTIN_ERROR_DESCRIPTION_ASP,'' AS RETURN_TYPE,'' AS TABLE_NUMBER, "
				+" HDR.IRN, HDR.IRN_DATE,HDR.TAX_SCHEME, "
				+" HDR.CANCEL_REASON, HDR.CANCEL_REMARKS, ITM.SUPPLY_TYPE, "
				+" HDR.DOC_CATEGORY, HDR.DOC_TYPE,HDR.DOC_NUM,HDR.DOC_DATE, "
				+" HDR.REVERSE_CHARGE, HDR.SUPPLIER_GSTIN,HDR.SUPP_TRADE_NAME, "
				+" HDR.SUPP_LEGAL_NAME, HDR.SUPP_BUILDING_NUM, "
				+" HDR.CUST_SUPP_ADDRESS2 AS SUPPLIERADDRESS2,HDR.SUPP_LOCATION, HDR.SUPP_PINCODE, "
				+" HDR.SUPP_STATE_CODE,HDR.SUPP_PHONE, HDR.SUPP_EMAIL, "
				+" HDR.CUST_GSTIN,HDR.CUST_TRADE_NAME, HDR.CUST_SUPP_NAME, "
				+" HDR.CUST_SUPP_ADDRESS1,HDR.CUST_SUPP_ADDRESS2,  "
				+" HDR.CUST_SUPP_ADDRESS4,HDR.CUST_PINCODE,HDR.BILL_TO_STATE,  "
				+" HDR.POS,HDR.CUST_PHONE,HDR.CUST_EMAIL,HDR.DISPATCHER_GSTIN, "
				+" HDR.DISPATCHER_TRADE_NAME,HDR.DISPATCHER_BUILDING_NUM, "
				+" HDR.DISPATCHER_BUILDING_NAME,HDR.DISPATCHER_LOCATION, "
				+" HDR.DISPATCHER_PINCODE,HDR.DISPATCHER_STATE_CODE, '' AS  "
				+" SHIP_TO_FLOOR_NUM,HDR.SHIP_TO_TRADE_NAME, "
				+" HDR.SHIP_TO_LEGAL_NAME,HDR.SHIP_TO_BUILDING_NUM, "
				+" HDR.SHIP_TO_BUILDING_NAME,HDR.SHIP_TO_LOCATION, "
				+" ITM.SHIP_TO_PINCODE,ITM.SHIP_TO_STATE,ITM.ITM_NO, "
				+" ITM.SERIAL_NUM2,ITM.PRODUCT_NAME,ITM.ITM_DESCRIPTION, "
				+" ITM.IS_SERVICE,ITM.ITM_HSNSAC,ITM.BAR_CODE, "
				+" ITM.BATCH_NAME_OR_NUM,ITM.BATCH_EXPIRY_DATE, "
				+" ITM.WARRANTY_DATE, ITM.ORDER_ITEM_REFERENCE,ITM.ATTRIBUTE_NAME, "
				+" ITM.ATTRIBUTE_VALUE, ITM.ORIGIN_COUNTRY,ITM.ITM_UQC,ITM.ITM_QTY, "
				+" ITM.FREE_QTY, ITM.UNIT_PRICE, ITM.ITEM_AMT_UP_QTY, "
				+" ITM.ITEM_DISCOUNT,ITM.PRE_TAX_AMOUNT,ITM.TAXABLE_VALUE, "
				+" ITM.IGST_RATE, ITM.IGST_AMT,ITM.CGST_RATE,ITM.CGST_AMT, "
				+" ITM.SGST_RATE,ITM.SGST_AMT,ITM.CESS_RATE_ADVALOREM, "
				+" ITM.CESS_AMT_ADVALOREM, ITM.CESS_RATE_SPECIFIC, "
				+" ITM.CESS_AMT_SPECIFIC, ITM.STATECESS_RATE,ITM.STATECESS_AMT, "
				+" ITM.STATE_CESS_SPECIFIC_RATE, ITM.STATE_CESS_SPECIFIC_AMOUNT, "
				+" ITM.OTHER_VALUES,TOT_ITEM_AMT, ITM.INV_OTHER_CHARGES, "
				+" ITM.INV_ASSESSABLE_AMT,ITM.INV_IGST_AMT,ITM.INV_CGST_AMT, "
				+" ITM.INV_SGST_AMT,ITM.INV_CESS_ADVLRM_AMT, "
				+" ITM.INV_CESS_SPECIFIC_AMT,ITM.INV_STATE_CESS_AMT, "
				+" ITM.INV_STATE_CESS_SPECIFIC_AMOUNT,ITM.LINE_ITEM_AMT, "
				+" HDR.ROUND_OFF,HDR.TOT_INV_VAL_WORLDS,HDR.TCS_FLAG_INCOME_TAX, "
				+" ITM.TCS_RATE_INCOME_TAX,ITM.TCS_AMOUNT_INCOME_TAX, "
				+" CUSTOMER_PAN_OR_AADHAAR, HDR.FOREIGN_CURRENCY,HDR.COUNTRY_CODE,  "
				+" HDR.INV_VAL_FC, HDR.SHIP_PORT_CODE,HDR.SHIP_BILL_NUM, "
				+" HDR.SHIP_BILL_DATE, HDR.INV_REMARKS,HDR.INV_PERIOD_START_DATE, "
				+" HDR.INV_PERIOD_END_DATE, ITM.PRECEEDING_INV_NUM, "
				+" ITM.PRECEEDING_INV_DATE,ITM.INV_REFERENCE, "
				+" ITM.RECEIPT_ADVICE_REFERENCE,ITM.RECEIPT_ADVICE_DATE, "
				+" ITM.TENDER_REFERENCE ,ITM.CONTRACT_REFERENCE, "
				+" ITM.EXTERNAL_REFERENCE, ITM.PROJECT_REFERENCE, "
				+" ITM.CUST_PO_REF_NUM,ITM.CUST_PO_REF_DATE,HDR.PAYEE_NAME, "
				+" HDR.MODE_OF_PAYMENT,HDR.BRANCH_IFSC_CODE,HDR.PAYMENT_TERMS, "
				+" HDR.PAYMENT_INSTRUCTION,HDR.CR_TRANSFER, HDR.DB_DIRECT, "
				+" HDR.CR_DAYS,ITM.PAID_AMT,ITM.BAL_AMT, HDR.PAYMENT_DUE_DATE, "
				+" HDR.ACCOUNT_DETAIL,HDR.ECOM_GSTIN, HDR.ECOM_TRANS_ID, "
				+" ITM.SUPPORTING_DOC_URL,ITM.SUPPORTING_DOC_BASE64, "
				+" ITM.ADDITIONAL_INFORMATION,HDR.TRANS_TYPE,HDR.SUB_SUPP_TYPE, "
				+" HDR.OTHER_SUPP_TYPE_DESC,HDR.TRANSPORTER_ID, "
				+" HDR.TRANSPORTER_NAME, HDR.TRANSPORT_MODE,HDR.TRANSPORT_DOC_NUM, "
				+" HDR.TRANSPORT_DOC_DATE, HDR.DISTANCE ,HDR.VEHICLE_NUM, "
				+" HDR.VEHICLE_TYPE, HDR.RETURN_PERIOD,HDR.ORIGINAL_DOC_TYPE, "
				+" HDR.ORIGINAL_CUST_GSTIN, HDR.DIFF_PERCENT, "
				+" HDR.SECTION7_OF_IGST_FLAG,HDR.CLAIM_REFUND_FLAG, "
				+" HDR.AUTOPOPULATE_TO_REFUND,HDR.CRDR_PRE_GST,HDR.CUST_SUPP_TYPE, "
				+" HDR.CUST_SUPP_CODE AS CUSTOMERCODE,ITM.PRODUCT_CODE,ITM.ITM_TYPE, "
				+" ITM.ITC_FLAG,HDR.STATE_APPLYING_CESS,ITM.FOB, "
				+" ITM.EXPORT_DUTY,HDR.EXCHANGE_RATE,ITM.CRDR_REASON, "
				+" HDR.TCS_FLAG ,ITM.TCS_AMT,TCS_CGST_AMT,TCS_SGST_AMT, "
				+" HDR.TDS_FLAG,TDS_IGST_AMT,TDS_CGST_AMT,TDS_SGST_AMT, "
				+" HDR.USER_ID AS userid,HDR.COMPANY_CODE,HDR.SOURCE_IDENTIFIER, "
				+" HDR.SOURCE_FILENAME,ITM.PLANT_CODE,HDR.DIVISION, "
				+" SUB_DIVISION,ITM.LOCATION,HDR.SALES_ORGANIZATION, "
				+" HDR.DISTRIBUTION_CHANNEL,ITM.PROFIT_CENTRE,HDR.PROFIT_CENTRE2,  "
				+" HDR.USERACCESS1,HDR.USERACCESS2,HDR.USERACCESS3, "
				+" HDR.USERACCESS4,HDR.USERACCESS5,HDR.USERACCESS6, "
				+" ITM.GLCODE_TAXABLEVALUE,ITM.GLCODE_IGST,ITM.GLCODE_CGST, "
				+" ITM.GLCODE_SGST,ITM.GLCODE_ADV_CESS,ITM.GLCODE_SP_CESS, "
				+" ITM.GLCODE_STATE_CESS, HDR.GL_STATE_CESS_SPECIFIC, "
				+" HDR.GL_POSTING_DATE,HDR.SALES_ORD_NUM,HDR.EWAY_BILL_NUM, "
				+" CAST(HDR.EWAY_BILL_DATE AS DATE), "
				+" HDR.ACCOUNTING_VOUCHER_NUM, "
				+" HDR.ACCOUNTING_VOUCHER_DATE, ITM.DOCUMENT_REFERENCE_NUMBER, "
				+" HDR.CUST_TAN,ITM.USERDEFINED_FIELD1,ITM.USERDEFINED_FIELD2, "
				+" ITM.USERDEFINED_FIELD3,ITM.USERDEFINED_FIELD4, "
				+" ITM.USERDEFINED_FIELD5,ITM.USERDEFINED_FIELD6, "
				+" ITM.USERDEFINED_FIELD7,ITM.USERDEFINED_FIELD8, "
				+" ITM.USERDEFINED_FIELD9,ITM.USERDEFINED_FIELD10, "
				+" ITM.USERDEFINED_FIELD11,ITM.USERDEFINED_FIELD12, "
				+" ITM.USERDEFINED_FIELD13,ITM.USERDEFINED_FIELD14, "
				+" ITM.USERDEFINED_FIELD15,ITM.USERDEFINED_FIELD16, "
				+" ITM.USERDEFINED_FIELD17,ITM.USERDEFINED_FIELD18, "
				+" ITM.USERDEFINED_FIELD19, ITM.USERDEFINED_FIELD20, "
				+" ITM.USERDEFINED_FIELD21,ITM.USERDEFINED_FIELD22, "
				+" ITM.USERDEFINED_FIELD23,ITM.USERDEFINED_FIELD24, "
				+" ITM.USERDEFINED_FIELD25, ITM.USERDEFINED_FIELD26, "
				+" ITM.USERDEFINED_FIELD27,ITM.USERDEFINED_FIELD28, "
				+" ITM.USERDEFINED_FIELD29,ITM.USERDEFINED_FIELD30, "
				+" HDR.SUPPLY_TYPE AS SUPPLYTYPEASP,  "
				+" '' AS ApproximateDistance_ASP, '' AS  "
				+" DistanceSavedtoEWB, HDR.CREATED_BY as USER_ID,HDR.FILE_ID, "
				+" FIL.FILE_NAME as FILE_NAME,HDR.INV_OTHER_CHARGES as InvoiceOtherChargesASP, "
				+" HDR.INV_ASSESSABLE_AMT as InvoiceAssessableAmountASP,  "
				+" HDR.INV_IGST_AMT AS InvoiceIGSTAmountASP,HDR.INV_CGST_AMT as InvoiceCGSTAmountASP, "
				+" HDR.INV_SGST_AMT as InvoiceSGSTAmountASP,HDR.INV_CESS_ADVLRM_AMT  "
				+" as InvoiceCessAdvaloremAmountASP, "
				+" HDR.INV_CESS_SPECIFIC_AMT as InvoiceCessSpecificAmountASP,  "
				+" HDR.INV_STATE_CESS_AMT as InvoiceStateCessAdvaloremAmountASP, "
				+" HDR.INV_STATE_CESS_SPECIFIC_AMOUNT AS InvoiceStateCessSpecificAmountASP, "
				+" HDR.DOC_AMT, "
				+" HDR.MEMO_VALUE_IGST,HDR.MEMO_VALUE_CGST,HDR.MEMO_VALUE_SGST, "
				+" ITM.MEMO_VALUE_CESS_ADV AS CessAdvaloremAmount_ASP, "
				+" ITM.MEMO_VALUE_STATE_CESS_ADV AS StateCessAdvaloremAmount_ASP, "
				+" ITM.IGST_RET1_IMPACT,ITM.CGST_RET1_IMPACT,ITM.SGST_RET1_IMPACT, "
				+" ITM.CESS_ADV_RET1_IMPACT AS CessAdvaloremAmount_Difference, "
				+" ITM.STATE_CESS_ADV_RET1_IMPACT AS StateCessAdvaloremAmount_Difference, "
				+" CASE WHEN HDR.IS_DELETE = TRUE THEN 'INACTIVE' WHEN  "
				+" HDR.IS_DELETE = FALSE THEN 'ACTIVE'END AS RECORD_STATUS, "
				+" ITM.ADJ_REF_NO,ITM.ADJ_REF_DATE, ITM.ADJ_TAXABLE_VALUE, "
				+" ITM.ADJ_IGST_AMT,ITM.ADJ_CGST_AMT, ITM.ADJ_SGST_AMT, "
				+" ITM.ADJ_CESS_AMT_ADVALOREM, ITM.ADJ_CESS_AMT_SPECIFIC, "
				+" ITM.ADJ_STATECESS_AMT, ITM.TCS_AMT AS Tcsamount, "
				+" HDR.CUST_SUPP_ADDRESS3 "
				+" FROM ANX_OUTWARD_DOC_HEADER HDR  "
				+" INNER JOIN ANX_OUTWARD_DOC_ITEM ITM  "
				+" ON HDR.ID = ITM.DOC_HEADER_ID AND "
				+" HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD "
				+" INNER JOIN FILE_STATUS FIL ON HDR.FILE_ID=FIL.ID "
				+"  "
				+" LEFT OUTER JOIN  "
				+" GSTR1_GSTN_SAVE_BATCH GSB "
				+" ON GSB.ID = HDR.BATCH_ID  "
				+" LEFT OUTER JOIN EINV_MASTER EINV  "
				+" ON EINV.DOC_HEADER_ID = HDR.ID  "
				+" AND ITM.DOC_HEADER_ID =  EINV.DOC_HEADER_ID  "
				+" WHERE  "
				+ buildQuery  +" AND "+   buildQuery1 
				+" ORDER BY DOC_NUM,ITM.ITM_NO LIMIT "+noOfRowsToFetch+" OFFSET "+ pageNo;
		} else if (DownloadReportsConstant.ERRORSV.equalsIgnoreCase(type)) {
			queryStr = "SELECT TRIM(', ' FROM IFNULL(ERRH.ERROR_CODE_ASP,'') "
					+ "||','|| IFNULL(ERRI.ERROR_CODE_ASP,'')) AS ERROR_CODE_ASP, "
					+ "TRIM(', ' FROM IFNULL(ERRH.ERROR_DESCRIPTION_ASP,'') "
					+ "||','|| IFNULL(ERRI.ERROR_DESCRIPTION_ASP,'')) AS "
					+ "ERROR_DESCRIPTION_ASP, TRIM(', ' FROM IFNULL"
					+ "(ERRH.INFO_ERROR_CODE_ASP,'') ||','|| IFNULL"
					+ "(ERRI.INFO_ERROR_CODE_ASP,'')) AS INFO_ERROR_CODE_ASP,"
					+ "TRIM(', ' FROM IFNULL(ERRH.INFO_ERROR_DESCRIPTION_ASP,'') "
					+ "||','|| IFNULL(ERRI.INFO_ERROR_DESCRIPTION_ASP,'')) AS "
					+ "INFO_ERROR_DESCRIPTION_ASP, EINV.STATUS_DESCRIPTION AS "
					+ "IRN_STATUS, EINV.IRN AS IRN_NO, EINV.ACK_NUM AS IRN_ACK_NO,"
					+ "HDR.IRN_DATE AS IRN_ACK_DATE_TIME, EINV.SIGNED_QR AS "
					+ "SIGNED_QR_CODE, EINV.SIGNED_INV AS SIGNED_INVOICE, "
					+ "HDR.EINV_ERROR_CODE AS IRP_ERROR_CODE, HDR.EINV_ERROR_DESC "
					+ "AS IRP_ERROR_DESCRIPTION,"
					+ "case when HDR.EWB_STATUS = 1 then 'Not Applicable' "
				    + "when HDR.EWB_STATUS = 2 then 'Pending' "
					+ "when HDR.EWB_STATUS = 3 then 'Generation Error' "
	                + "when HDR.EWB_STATUS = 4 then 'Part A Generated' "
	                + "when HDR.EWB_STATUS = 5 then 'EWB Active' "
	                + "when HDR.EWB_STATUS = 6 then 'Cancelled' "
	                + "when HDR.EWB_STATUS = 7 then 'Discarded' "
	                + "when HDR.EWB_STATUS = 8 then 'Rejected' "
	                + "when HDR.EWB_STATUS = 9 then 'Expired' end as EWBPartAStatusA,"
					+ "'' AS EWBValidUpTo,HDR.EWAY_BILL_NUM AS EWBNO,"
					+ "CAST(HDR.EWAY_BILL_DATE AS DATE) AS EWBDATE,"
					+ "HDR.EWB_ERROR_CODE AS EWB_ERROR_CODE, HDR.EWB_ERROR_DESC AS "
					+ "EWB_ERROR_DESCRIPTION,"
					+ "'' AS GSTIN_STATUS,"
					+ "GSB.GSTN_SAVE_REF_ID "
					+ "AS GSTIN_REF_ID, GSB.GSTN_RESP_DATE AS GSTIN_REFID_DATE_TIME,"
					+ "TRIM(', ' FROM IFNULL(ERRH.ERROR_CODE_GSTIN,'') ||','|| "
					+ "IFNULL(ERRI.ERROR_CODE_GSTN,'')) AS GSTIN_ERROR_CODE,"
					+ "TRIM(', ' FROM IFNULL(ERRH.ERROR_DESCRIPTION_GSTIN,'') "
					+ "||','|| IFNULL(ERRI.ERROR_DESCRIPTION_GSTN,'')) AS "
					+ "GSTIN_ERROR_DESCRIPTION_ASP,'' AS RETURN_TYPE,'' AS "
					+ "TABLE_NUMBER,HDR.IRN,HDR.IRN_DATE,HDR.TAX_SCHEME,"
					+ "HDR.CANCEL_REASON, HDR.CANCEL_REMARKS,ITM.SUPPLY_TYPE,"
					+ "HDR.DOC_CATEGORY, HDR.DOC_TYPE,HDR.DOC_NUM,HDR.DOC_DATE,"
					+ "HDR.REVERSE_CHARGE, HDR.SUPPLIER_GSTIN,HDR.SUPP_TRADE_NAME,"
					+ "HDR.SUPP_LEGAL_NAME, HDR.SUPP_BUILDING_NUM,"
					+ "HDR.CUST_SUPP_ADDRESS2 AS SUPPLIERADDRESS2,HDR.SUPP_LOCATION, "
					+ "HDR.SUPP_PINCODE,"
					+ "HDR.SUPP_STATE_CODE,HDR.SUPP_PHONE, HDR.SUPP_EMAIL,"
					+ "HDR.CUST_GSTIN,HDR.CUST_TRADE_NAME, HDR.CUST_SUPP_NAME,"
					+ "HDR.CUST_SUPP_ADDRESS1,HDR.CUST_SUPP_ADDRESS2,"
					+ "HDR.CUST_SUPP_ADDRESS4,HDR.CUST_PINCODE,HDR.BILL_TO_STATE,"
					+ "HDR.POS,HDR.CUST_PHONE,HDR.CUST_EMAIL,HDR.DISPATCHER_GSTIN,"
					+ "HDR.DISPATCHER_TRADE_NAME,HDR.DISPATCHER_BUILDING_NUM,"
					+ "HDR.DISPATCHER_BUILDING_NAME,HDR.DISPATCHER_LOCATION,"
					+ "HDR.DISPATCHER_PINCODE,HDR.DISPATCHER_STATE_CODE, "
					+ "'' AS SHIP_TO_FLOOR_NUM,HDR.SHIP_TO_TRADE_NAME,"
					+ "HDR.SHIP_TO_LEGAL_NAME,HDR.SHIP_TO_BUILDING_NUM,"
					+ "HDR.SHIP_TO_BUILDING_NAME,HDR.SHIP_TO_LOCATION,"
					+ "ITM.SHIP_TO_PINCODE,ITM.SHIP_TO_STATE,ITM.ITM_NO,"
					+ "ITM.SERIAL_NUM2,ITM.PRODUCT_NAME,ITM.ITM_DESCRIPTION,"
					+ "ITM.IS_SERVICE,ITM.ITM_HSNSAC,ITM.BAR_CODE,"
					+ "ITM.BATCH_NAME_OR_NUM,ITM.BATCH_EXPIRY_DATE,ITM.WARRANTY_DATE,"
					+ "ITM.ORDER_ITEM_REFERENCE,ITM.ATTRIBUTE_NAME,ITM.ATTRIBUTE_VALUE,"
					+ "ITM.ORIGIN_COUNTRY,ITM.ITM_UQC,ITM.ITM_QTY,ITM.FREE_QTY,"
					+ "ITM.UNIT_PRICE, ITM.ITEM_AMT_UP_QTY, ITM.ITEM_DISCOUNT,"
					+ "ITM.PRE_TAX_AMOUNT,ITM.TAXABLE_VALUE,ITM.IGST_RATE,"
					+ "ITM.IGST_AMT,ITM.CGST_RATE,ITM.CGST_AMT, ITM.SGST_RATE,"
					+ "ITM.SGST_AMT,ITM.CESS_RATE_ADVALOREM, ITM.CESS_AMT_ADVALOREM,"
					+ "ITM.CESS_RATE_SPECIFIC, ITM.CESS_AMT_SPECIFIC,"
					+ "ITM.STATECESS_RATE,ITM.STATECESS_AMT,ITM.STATE_CESS_SPECIFIC_RATE,"
					+ "ITM.STATE_CESS_SPECIFIC_AMOUNT,ITM.OTHER_VALUES,TOT_ITEM_AMT,"
					+ "ITM.INV_OTHER_CHARGES,ITM.INV_ASSESSABLE_AMT,ITM.INV_IGST_AMT,"
					+ "ITM.INV_CGST_AMT,ITM.INV_SGST_AMT,ITM.INV_CESS_ADVLRM_AMT,"
					+ "ITM.INV_CESS_SPECIFIC_AMT,ITM.INV_STATE_CESS_AMT,"
					+ "ITM.INV_STATE_CESS_SPECIFIC_AMOUNT,ITM.LINE_ITEM_AMT,"
					+ "HDR.ROUND_OFF,HDR.TOT_INV_VAL_WORLDS,HDR.TCS_FLAG_INCOME_TAX,"
					+ "ITM.TCS_RATE_INCOME_TAX,ITM.TCS_AMOUNT_INCOME_TAX,"
					+ "CUSTOMER_PAN_OR_AADHAAR, HDR.FOREIGN_CURRENCY ,HDR.COUNTRY_CODE,"
					+ "HDR.INV_VAL_FC, HDR.SHIP_PORT_CODE,HDR.SHIP_BILL_NUM,"
					+ "HDR.SHIP_BILL_DATE, HDR.INV_REMARKS,HDR.INV_PERIOD_START_DATE,"
					+ "HDR.INV_PERIOD_END_DATE, ITM.PRECEEDING_INV_NUM,"
					+ "ITM.PRECEEDING_INV_DATE,ITM.INV_REFERENCE,"
					+ "ITM.RECEIPT_ADVICE_REFERENCE,ITM.RECEIPT_ADVICE_DATE,"
					+ "ITM.TENDER_REFERENCE ,ITM.CONTRACT_REFERENCE,"
					+ "ITM.EXTERNAL_REFERENCE, ITM.PROJECT_REFERENCE,"
					+ "ITM.CUST_PO_REF_NUM,ITM.CUST_PO_REF_DATE, HDR.PAYEE_NAME,"
					+ "HDR.MODE_OF_PAYMENT,HDR.BRANCH_IFSC_CODE, HDR.PAYMENT_TERMS,"
					+ "HDR.PAYMENT_INSTRUCTION,HDR.CR_TRANSFER, HDR.DB_DIRECT,"
					+ "HDR.CR_DAYS,ITM.PAID_AMT,ITM.BAL_AMT, HDR.PAYMENT_DUE_DATE,"
					+ "HDR.ACCOUNT_DETAIL,HDR.ECOM_GSTIN, HDR.ECOM_TRANS_ID,"
					+ "ITM.SUPPORTING_DOC_URL,ITM.SUPPORTING_DOC_BASE64,"
					+ "ITM.ADDITIONAL_INFORMATION,HDR.TRANS_TYPE,HDR.SUB_SUPP_TYPE,"
					+ "HDR.OTHER_SUPP_TYPE_DESC,HDR.TRANSPORTER_ID,HDR.TRANSPORTER_NAME,"
					+ "HDR.TRANSPORT_MODE,HDR.TRANSPORT_DOC_NUM,HDR.TRANSPORT_DOC_DATE,"
					+ "HDR.DISTANCE ,HDR.VEHICLE_NUM,HDR.VEHICLE_TYPE,"
					+ "HDR.RETURN_PERIOD,HDR.ORIGINAL_DOC_TYPE,HDR.ORIGINAL_CUST_GSTIN,"
					+ "HDR.DIFF_PERCENT,HDR.SECTION7_OF_IGST_FLAG,HDR.CLAIM_REFUND_FLAG,"
					+ "HDR.AUTOPOPULATE_TO_REFUND,HDR.CRDR_PRE_GST,HDR.CUST_SUPP_TYPE,"
					+ "HDR.CUST_SUPP_CODE AS CUSTOMERCODE,ITM.PRODUCT_CODE,ITM.ITM_TYPE, ITM.ITC_FLAG,"
					+ "HDR.STATE_APPLYING_CESS,ITM.FOB , ITM.EXPORT_DUTY,HDR.EXCHANGE_RATE,"
					+ "ITM.CRDR_REASON, HDR.TCS_FLAG ,ITM.TCS_AMT,TCS_CGST_AMT,TCS_SGST_AMT,"
					+ "HDR.TDS_FLAG,TDS_IGST_AMT,TDS_CGST_AMT,TDS_SGST_AMT, HDR.USER_ID AS userid,"
					+ "HDR.COMPANY_CODE,HDR.SOURCE_IDENTIFIER, HDR.SOURCE_FILENAME,"
					+ "ITM.PLANT_CODE,HDR.DIVISION, SUB_DIVISION,ITM.LOCATION,"
					+ "HDR.SALES_ORGANIZATION, HDR.DISTRIBUTION_CHANNEL,"
					+ "HDR.PROFIT_CENTRE,HDR.PROFIT_CENTRE2, HDR.USERACCESS1,"
					+ "HDR.USERACCESS2,HDR.USERACCESS3, HDR.USERACCESS4,"
					+ "HDR.USERACCESS5,HDR.USERACCESS6, ITM.GLCODE_TAXABLEVALUE,"
					+ "ITM.GLCODE_IGST,ITM.GLCODE_CGST, ITM.GLCODE_SGST,"
					+ "ITM.GLCODE_ADV_CESS,ITM.GLCODE_SP_CESS, ITM.GLCODE_STATE_CESS,"
					+ "HDR.GL_STATE_CESS_SPECIFIC, HDR.GL_POSTING_DATE,"
					+ "HDR.SALES_ORD_NUM,HDR.EWAY_BILL_NUM,"
					+ "CAST(HDR.EWAY_BILL_DATE AS DATE),"
					+ "HDR.ACCOUNTING_VOUCHER_NUM,HDR.ACCOUNTING_VOUCHER_DATE,"
					+ "ITM.DOCUMENT_REFERENCE_NUMBER,HDR.CUST_TAN,ITM.USERDEFINED_FIELD1,"
					+ "ITM.USERDEFINED_FIELD2,ITM.USERDEFINED_FIELD3,ITM.USERDEFINED_FIELD4,"
					+ "ITM.USERDEFINED_FIELD5,ITM.USERDEFINED_FIELD6,ITM.USERDEFINED_FIELD7,"
					+ "ITM.USERDEFINED_FIELD8,ITM.USERDEFINED_FIELD9,ITM.USERDEFINED_FIELD10,"
					+ "ITM.USERDEFINED_FIELD11,ITM.USERDEFINED_FIELD12,ITM.USERDEFINED_FIELD13,"
					+ "ITM.USERDEFINED_FIELD14,ITM.USERDEFINED_FIELD15,ITM.USERDEFINED_FIELD16,"
					+ "ITM.USERDEFINED_FIELD17,ITM.USERDEFINED_FIELD18,ITM.USERDEFINED_FIELD19,"
					+ "ITM.USERDEFINED_FIELD20,ITM.USERDEFINED_FIELD21,ITM.USERDEFINED_FIELD22,"
					+ "ITM.USERDEFINED_FIELD23,ITM.USERDEFINED_FIELD24,ITM.USERDEFINED_FIELD25,"
					+ "ITM.USERDEFINED_FIELD26,ITM.USERDEFINED_FIELD27,ITM.USERDEFINED_FIELD28,"
					+ "ITM.USERDEFINED_FIELD29,ITM.USERDEFINED_FIELD30, HDR.SUPPLY_TYPE AS SUPPLYTYPEASP, "
					+ "'' AS ApproximateDistance_ASP, '' AS DistanceSavedtoEWB,"
					+ "HDR.CREATED_BY as USER_ID,HDR.FILE_ID, FIL.FILE_NAME as "
					+ "FILE_NAME,HDR.INV_OTHER_CHARGES as InvoiceOtherChargesASP, "
					+ "HDR.INV_ASSESSABLE_AMT as InvoiceAssessableAmountASP,"
					+ "HDR.INV_IGST_AMT AS InvoiceIGSTAmountASP,HDR.INV_CGST_AMT "
					+ "as InvoiceCGSTAmountASP, HDR.INV_SGST_AMT as InvoiceSGSTAmountASP,"
					+ "HDR.INV_CESS_ADVLRM_AMT as InvoiceCessAdvaloremAmountASP, "
					+ "HDR.INV_CESS_SPECIFIC_AMT as InvoiceCessSpecificAmountASP,"
					+ "HDR.INV_STATE_CESS_AMT as InvoiceStateCessAdvaloremAmountASP,"
					+ "HDR.INV_STATE_CESS_SPECIFIC_AMOUNT AS InvoiceStateCessSpecificAmountASP,"
					+ "HDR.DOC_AMT, HDR.MEMO_VALUE_IGST,HDR.MEMO_VALUE_CGST,"
					+ "HDR.MEMO_VALUE_SGST, ITM.MEMO_VALUE_CESS_ADV AS "
					+ "CessAdvaloremAmount_ASP, ITM.MEMO_VALUE_STATE_CESS_ADV "
					+ "AS StateCessAdvaloremAmount_ASP, ITM.IGST_RET1_IMPACT,"
					+ "ITM.CGST_RET1_IMPACT,ITM.SGST_RET1_IMPACT,"
					+ "ITM.CESS_ADV_RET1_IMPACT AS CessAdvaloremAmount_Difference,"
					+ "ITM.STATE_CESS_ADV_RET1_IMPACT AS StateCessAdvaloremAmount_Difference,"
					+ "'' AS RECORD_STATUS,"
					+ "ITM.ADJ_REF_NO,ITM.ADJ_REF_DATE, "
					+ "ITM.ADJ_TAXABLE_VALUE,ITM.ADJ_IGST_AMT,ITM.ADJ_CGST_AMT,"
					+ "ITM.ADJ_SGST_AMT,ITM. ADJ_CESS_AMT_ADVALOREM,"
					+ "ITM.ADJ_CESS_AMT_SPECIFIC,ITM.ADJ_STATECESS_AMT,"
					+ "ITM.TCS_AMT AS Tcsamount,HDR.CUST_SUPP_ADDRESS3,'' AS DATA_CATEGORY "
					+ " FROM ANX_OUTWARD_ERR_HEADER HDR LEFT OUTER "
					+ "JOIN TF_OUTWARD_HEADER_GSTIN_ASP_ERROR_INFO ()ERRH ON "
					+ "HDR.ID = ERRH.DOC_HEADER_ID INNER JOIN ANX_OUTWARD_ERR_ITEM "
					+ "ITM ON HDR.ID = ITM.DOC_HEADER_ID AND HDR.DERIVED_RET_PERIOD = "
					+ "ITM.DERIVED_RET_PERIOD LEFT OUTER JOIN "
					+ "TF_OUTWARD_ITEM_GSTIN_ASP_ERROR_INFO ()ERRI ON "
					+ "ITM.DOC_HEADER_ID = ERRI.DOC_HEADER_ID AND ITM.ITEM_INDEX = "
					+ "ERRI.ITEM_INDEX AND "
					+ "TO_CHAR(ITM.ITM_NO) = TO_CHAR(ERRI.ITM_NO) "
					+ "INNER JOIN "
					+ "FILE_STATUS FIL ON HDR.FILE_ID=FIL.ID LEFT OUTER JOIN "
					+ "GSTR1_GSTN_SAVE_BATCH GSB ON GSB.ID = HDR.BATCH_ID LEFT "
					+ "OUTER JOIN EINV_MASTER EINV ON EINV.DOC_HEADER_ID = HDR.ID "
					+ "AND ITM.DOC_HEADER_ID = EINV.DOC_HEADER_ID WHERE "
					+ buildQuery 
					+" ORDER BY DOC_NUM,ITM.ITM_NO ";

		} else if (DownloadReportsConstant.ERRORTOTAL.equalsIgnoreCase(type))
				{
			queryStr = ""
					+ "(SELECT TRIM(', ' FROM IFNULL(ERRH.ERROR_CODE_ASP,'') "
					+ "            ||','|| IFNULL(ERRI.ERROR_CODE_ASP,'')) AS ERROR_CODE_ASP, "
					+ "             TRIM(', ' FROM IFNULL(ERRH.ERROR_DESCRIPTION_ASP,'') "
					+ "             ||','|| IFNULL(ERRI.ERROR_DESCRIPTION_ASP,'')) AS "
					+ "             ERROR_DESCRIPTION_ASP, TRIM(', ' FROM IFNULL "
					+ "             (ERRH.INFO_ERROR_CODE_ASP,'') ||','|| IFNULL( "
					+ "             ERRI.INFO_ERROR_CODE_ASP,'')) AS INFO_ERROR_CODE_ASP, "
					+ "             TRIM(', ' FROM IFNULL(ERRH.INFO_ERROR_DESCRIPTION_ASP,'') "
					+ "             ||','|| IFNULL(ERRI.INFO_ERROR_DESCRIPTION_ASP,'')) AS "
					+ "             INFO_ERROR_DESCRIPTION_ASP, EINV.STATUS_DESCRIPTION AS "
					+ "             IRN_STATUS, TO_CHAR(EINV.IRN) AS IRN_NO, "
					+ "             TO_CHAR(EINV.ACK_NUM) AS IRN_ACK_NO, TO_CHAR( HDR.IRN_DATE) "
					+ "             AS IRN_ACK_DATE_TIME, EINV.SIGNED_QR AS SIGNED_QR_CODE, "
					+ "             EINV.SIGNED_INV AS SIGNED_INVOICE, HDR.EINV_ERROR_CODE AS "
					+ "              IRP_ERROR_CODE, HDR.EINV_ERROR_DESC AS IRP_ERROR_DESCRIPTION, "
					+ "             case when HDR.EWB_STATUS = 1 then 'Not Applicable' "
					+ "                when HDR.EWB_STATUS = 2 then 'Pending' "
					+ "                when HDR.EWB_STATUS = 3 then 'Generation Error' "
					+ "                  when HDR.EWB_STATUS = 4 then 'Part A Generated' "
					+ "                  when HDR.EWB_STATUS = 5 then 'EWB Active' "
					+ "                  when HDR.EWB_STATUS = 6 then 'Cancelled' "
					+ "                  when HDR.EWB_STATUS = 7 then 'Discarded' "
					+ "                  when HDR.EWB_STATUS = 8 then 'Rejected' "
					+ "                  when HDR.EWB_STATUS = 9 then 'Expired' "
					+ "              end as EWBPartAStatusA, "
					+ "                '' AS EWBValidUpTo, "
					+ "                HDR.EWAY_BILL_NUM AS EWBNO, "
					+ "                CAST(HDR.EWAY_BILL_DATE AS DATE) AS EWBDATE, "
					+ "                 HDR.EWB_ERROR_CODE AS EWB_ERROR_CODE, "
					+ "                HDR.EWB_ERROR_DESC AS EWB_ERROR_DESCRIPTION, "
					+ "                           case when HDR.IS_SAVED_TO_GSTN = TRUE THEN 'IS_SAVED' "
					+ "                                  WHEN HDR.IS_SAVED_TO_GSTN = FALSE AND HDR.GSTN_ERROR = TRUE THEN 'IS_ERROR' "
					+ "                                  WHEN HDR.IS_SAVED_TO_GSTN = FALSE AND HDR.GSTN_ERROR = FALSE THEN 'NOT_SAVED' "
					+ "                        END AS GSTIN_STATUS, "
					+ "                TO_CHAR(GSB.GSTN_SAVE_REF_ID) AS GSTIN_REF_ID, "
					+ "                TO_CHAR(GSB.GSTN_RESP_DATE) AS GSTIN_REFID_DATE_TIME, "
					+ "                TRIM(', ' FROM IFNULL(ERRH.ERROR_CODE_GSTIN,'') ||','|| "
					+ "                IFNULL(ERRI.ERROR_CODE_GSTN,'')) AS GSTIN_ERROR_CODE, "
					+ "                TRIM(', ' FROM IFNULL(ERRH.ERROR_DESCRIPTION_GSTIN,'') "
					+ "                ||','|| IFNULL(ERRI.ERROR_DESCRIPTION_GSTN,'')) AS "
					+ "                GSTIN_ERROR_DESCRIPTION_ASP, "
					+ "                 '' AS RETURN_TYPE,'' AS TABLE_NUMBER, "
					+ "                TO_CHAR(HDR.IRN), "
					+ "                TO_CHAR( HDR.IRN_DATE), HDR.TAX_SCHEME, HDR.CANCEL_REASON, "
					+ "                HDR.CANCEL_REMARKS, ITM.SUPPLY_TYPE,HDR.DOC_CATEGORY, "
					+ "                HDR.DOC_TYPE,TO_CHAR(HDR.DOC_NUM),TO_CHAR(HDR.DOC_DATE), "
					+ "                HDR.REVERSE_CHARGE, HDR.SUPPLIER_GSTIN,HDR.SUPP_TRADE_NAME, "
					+ "                HDR.SUPP_LEGAL_NAME,HDR.SUPP_BUILDING_NUM, "
					+ "                HDR.CUST_SUPP_ADDRESS2 AS SUPPLIERADDRESS2, HDR.SUPP_LOCATION, "
					+ "                TO_CHAR(HDR.SUPP_PINCODE),TO_CHAR(HDR.SUPP_STATE_CODE),TO_CHAR(HDR.SUPP_PHONE), "
					+ "                HDR.SUPP_EMAIL,HDR.CUST_GSTIN,HDR.CUST_TRADE_NAME, "
					+ "                HDR.CUST_SUPP_NAME,HDR.CUST_SUPP_ADDRESS1,HDR.CUST_SUPP_ADDRESS2, "
					+ "                HDR.CUST_SUPP_ADDRESS4,TO_CHAR(HDR.CUST_PINCODE), "
					+ "                TO_CHAR(HDR.BILL_TO_STATE), TO_CHAR(HDR.POS), "
					+ "                TO_CHAR(HDR.CUST_PHONE),TO_CHAR(HDR.CUST_EMAIL),HDR.DISPATCHER_GSTIN, "
					+ "                HDR.DISPATCHER_TRADE_NAME,TO_CHAR(HDR.DISPATCHER_BUILDING_NUM), "
					+ "                HDR.DISPATCHER_BUILDING_NAME,HDR.DISPATCHER_LOCATION, "
					+ "                TO_CHAR(HDR.DISPATCHER_PINCODE),TO_CHAR(HDR.DISPATCHER_STATE_CODE), "
					+ "                '' AS SHIP_TO_FLOOR_NUM, TO_CHAR(HDR.SHIP_TO_TRADE_NAME), "
					+ "                HDR.SHIP_TO_LEGAL_NAME,HDR.SHIP_TO_BUILDING_NUM, "
					+ "                HDR.SHIP_TO_BUILDING_NAME,HDR.SHIP_TO_LOCATION, "
					+ "                TO_CHAR(ITM.SHIP_TO_PINCODE),TO_CHAR(ITM.SHIP_TO_STATE) "
					+ "                AS SHIPTOSTATECODE, TO_CHAR(ITM.ITM_NO), "
					+ "                TO_CHAR(ITM.SERIAL_NUM2),ITM.PRODUCT_NAME,ITM.ITM_DESCRIPTION, "
					+ "                TO_CHAR(ITM.IS_SERVICE),TO_CHAR(ITM.ITM_HSNSAC), "
					+ "                TO_CHAR(ITM.BAR_CODE), TO_CHAR(ITM.BATCH_NAME_OR_NUM), "
					+ "                TO_CHAR(ITM.BATCH_EXPIRY_DATE),TO_CHAR(ITM.WARRANTY_DATE), "
					+ "                TO_CHAR(ITM.ORDER_ITEM_REFERENCE), TO_CHAR(ITM.ATTRIBUTE_NAME), "
					+ "                TO_CHAR(ITM.ATTRIBUTE_VALUE), TO_CHAR(ITM.ORIGIN_COUNTRY), "
					+ "                TO_CHAR(ITM.ITM_UQC), TO_CHAR(ITM.ITM_QTY), "
					+ "                TO_CHAR(ITM.FREE_QTY), TO_CHAR(ITM.UNIT_PRICE), "
					+ "                TO_CHAR(ITM.ITEM_AMT_UP_QTY), TO_CHAR(ITM.ITEM_DISCOUNT), "
					+ "                TO_CHAR(ITM.PRE_TAX_AMOUNT), TO_CHAR(ITM.TAXABLE_VALUE), "
					+ "                TO_CHAR(ITM.IGST_RATE), TO_CHAR(ITM.IGST_AMT), "
					+ "                TO_CHAR(ITM.CGST_RATE), TO_CHAR(ITM.CGST_AMT), "
					+ "                TO_CHAR(ITM.SGST_RATE), TO_CHAR(ITM.SGST_AMT), "
					+ "                TO_CHAR(ITM.CESS_RATE_ADVALOREM), TO_CHAR(ITM.CESS_AMT_ADVALOREM), "
					+ "                TO_CHAR(ITM.CESS_RATE_SPECIFIC), TO_CHAR(ITM.CESS_AMT_SPECIFIC), "
					+ "                TO_CHAR(ITM.STATECESS_RATE), TO_CHAR(ITM.STATECESS_AMT), "
					+ "                TO_CHAR(ITM.STATE_CESS_SPECIFIC_RATE), "
					+ "                TO_CHAR(ITM.STATE_CESS_SPECIFIC_AMOUNT), TO_CHAR(ITM.OTHER_VALUES), "
					+ "                TO_CHAR(TOT_ITEM_AMT), TO_CHAR(ITM.INV_OTHER_CHARGES), "
					+ "                TO_CHAR(ITM.INV_ASSESSABLE_AMT), TO_CHAR(ITM.INV_IGST_AMT), "
					+ "                TO_CHAR(ITM.INV_CGST_AMT), TO_CHAR(ITM.INV_SGST_AMT), "
					+ "                TO_CHAR(ITM.INV_CESS_ADVLRM_AMT), TO_CHAR(ITM. "
					+ "                INV_CESS_SPECIFIC_AMT), TO_CHAR(ITM.INV_STATE_CESS_AMT), "
					+ "                TO_CHAR(ITM.INV_STATE_CESS_SPECIFIC_AMOUNT), "
					+ "                TO_CHAR(ITM.LINE_ITEM_AMT), TO_CHAR(HDR.ROUND_OFF), "
					+ "                TO_CHAR(HDR.TOT_INV_VAL_WORLDS), TO_CHAR(HDR.TCS_FLAG_INCOME_TAX), "
					+ "                TO_CHAR(ITM.TCS_RATE_INCOME_TAX), TO_CHAR(ITM.TCS_AMOUNT_INCOME_TAX), "
					+ "                TO_CHAR(CUSTOMER_PAN_OR_AADHAAR), TO_CHAR( HDR.FOREIGN_CURRENCY), "
					+ "                TO_CHAR(HDR.COUNTRY_CODE), TO_CHAR( HDR.INV_VAL_FC), "
					+ "                TO_CHAR(HDR.SHIP_PORT_CODE), TO_CHAR(HDR.SHIP_BILL_NUM), "
					+ "                TO_CHAR(HDR.SHIP_BILL_DATE), HDR.INV_REMARKS, "
					+ "                TO_CHAR(HDR.INV_PERIOD_START_DATE), TO_CHAR(HDR. "
					+ "                INV_PERIOD_END_DATE), TO_CHAR(ITM.PRECEEDING_INV_NUM), "
					+ "                TO_CHAR(ITM.PRECEEDING_INV_DATE), TO_CHAR(ITM.INV_REFERENCE), "
					+ "                ITM.RECEIPT_ADVICE_REFERENCE, TO_CHAR(ITM.RECEIPT_ADVICE_DATE), "
					+ "                TO_CHAR(ITM.TENDER_REFERENCE) , TO_CHAR(ITM.CONTRACT_REFERENCE), "
					+ "                TO_CHAR(ITM.EXTERNAL_REFERENCE), TO_CHAR(ITM.PROJECT_REFERENCE), "
					+ "                TO_CHAR(ITM.CUST_PO_REF_NUM), TO_CHAR(ITM.CUST_PO_REF_DATE), "
					+ "                HDR.PAYEE_NAME,HDR.MODE_OF_PAYMENT,TO_CHAR(HDR.BRANCH_IFSC_CODE), "
					+ "                HDR.PAYMENT_TERMS,HDR.PAYMENT_INSTRUCTION,HDR.CR_TRANSFER, "
					+ "                HDR.DB_DIRECT,HDR.CR_DAYS,TO_CHAR(ITM.PAID_AMT), "
					+ "                TO_CHAR(ITM.BAL_AMT), TO_CHAR(HDR.PAYMENT_DUE_DATE), "
					+ "                TO_CHAR(HDR.ACCOUNT_DETAIL),HDR.ECOM_GSTIN, "
					+ "                TO_CHAR(HDR.ECOM_TRANS_ID), ITM.SUPPORTING_DOC_URL, "
					+ "                ITM.SUPPORTING_DOC_BASE64, ITM.ADDITIONAL_INFORMATION, "
					+ "                HDR.TRANS_TYPE,HDR.SUB_SUPP_TYPE, HDR.OTHER_SUPP_TYPE_DESC, "
					+ "                TO_CHAR(HDR.TRANSPORTER_ID), HDR.TRANSPORTER_NAME, "
					+ "                HDR.TRANSPORT_MODE,HDR.TRANSPORT_DOC_NUM,TO_CHAR( "
					+ "                HDR.TRANSPORT_DOC_DATE), TO_CHAR(HDR.DISTANCE) , "
					+ "                TO_CHAR(HDR.VEHICLE_NUM),HDR.VEHICLE_TYPE, "
					+ "                TO_CHAR(HDR.RETURN_PERIOD),HDR.ORIGINAL_DOC_TYPE, "
					+ "                HDR.ORIGINAL_CUST_GSTIN, TO_CHAR(HDR.DIFF_PERCENT), "
					+ "                TO_CHAR(HDR.SECTION7_OF_IGST_FLAG), TO_CHAR(HDR.CLAIM_REFUND_FLAG), "
					+ "                TO_CHAR(HDR.AUTOPOPULATE_TO_REFUND),TO_CHAR(HDR.CRDR_PRE_GST), "
					+ "                TO_CHAR(HDR.CUST_SUPP_TYPE), "
					+ "                HDR.CUST_SUPP_CODE AS CUSTOMERCODE, "
					+ "                ITM.PRODUCT_CODE,ITM.ITM_TYPE, TO_CHAR(ITM.ITC_FLAG), "
					+ "                TO_CHAR(HDR.STATE_APPLYING_CESS),TO_CHAR(ITM.FOB) , "
					+ "                TO_CHAR(ITM.EXPORT_DUTY), TO_CHAR(HDR.EXCHANGE_RATE), "
					+ "                TO_CHAR(ITM.CRDR_REASON), TO_CHAR(HDR.TCS_FLAG) , "
					+ "                TO_CHAR(ITM.TCS_AMT),TO_CHAR(TCS_CGST_AMT),TO_CHAR(TCS_SGST_AMT), "
					+ "                TO_CHAR( HDR.TDS_FLAG), TO_CHAR(TDS_IGST_AMT),TO_CHAR(TDS_CGST_AMT), "
					+ "                TO_CHAR(TDS_SGST_AMT), TO_CHAR(HDR.USER_ID) AS userid, "
					+ "                TO_CHAR(HDR.COMPANY_CODE),HDR.SOURCE_IDENTIFIER, HDR.SOURCE_FILENAME, "
					+ "                ITM.PLANT_CODE,HDR.DIVISION, SUB_DIVISION,ITM.LOCATION, "
					+ "                HDR.SALES_ORGANIZATION, HDR.DISTRIBUTION_CHANNEL, "
					+ "                ITM.PROFIT_CENTRE,HDR.PROFIT_CENTRE2, HDR.USERACCESS1, "
					+ "                HDR.USERACCESS2,HDR.USERACCESS3, HDR.USERACCESS4,HDR.USERACCESS5, "
					+ "                HDR.USERACCESS6, TO_CHAR(ITM.GLCODE_TAXABLEVALUE), "
					+ "                TO_CHAR(ITM.GLCODE_IGST), TO_CHAR(ITM.GLCODE_CGST), "
					+ "                TO_CHAR( ITM.GLCODE_SGST), TO_CHAR(ITM.GLCODE_ADV_CESS), "
					+ "                TO_CHAR(ITM.GLCODE_SP_CESS), TO_CHAR(ITM.GLCODE_STATE_CESS), "
					+ "                TO_CHAR(HDR.GL_STATE_CESS_SPECIFIC), TO_CHAR(HDR.GL_POSTING_DATE), "
					+ "                TO_CHAR(HDR.SALES_ORD_NUM),TO_CHAR(HDR.EWAY_BILL_NUM), "
					+ "                TO_CHAR(CAST(HDR.EWAY_BILL_DATE AS DATE)), "
					+ "                TO_CHAR(HDR.ACCOUNTING_VOUCHER_NUM), "
					+ "                TO_CHAR(HDR.ACCOUNTING_VOUCHER_DATE), "
					+ "                TO_CHAR(ITM.DOCUMENT_REFERENCE_NUMBER),TO_CHAR(HDR.CUST_TAN), "
					+ "                ITM.USERDEFINED_FIELD1, ITM.USERDEFINED_FIELD2, "
					+ "                ITM.USERDEFINED_FIELD3,ITM.USERDEFINED_FIELD4, "
					+ "                ITM.USERDEFINED_FIELD5,ITM.USERDEFINED_FIELD6, "
					+ "                ITM.USERDEFINED_FIELD7, ITM.USERDEFINED_FIELD8, "
					+ "                ITM.USERDEFINED_FIELD9,ITM.USERDEFINED_FIELD10, "
					+ "                ITM.USERDEFINED_FIELD11,ITM.USERDEFINED_FIELD12, "
					+ "                ITM.USERDEFINED_FIELD13, ITM.USERDEFINED_FIELD14, "
					+ "                ITM.USERDEFINED_FIELD15,ITM.USERDEFINED_FIELD16, "
					+ "                ITM.USERDEFINED_FIELD17,ITM.USERDEFINED_FIELD18, "
					+ "                ITM.USERDEFINED_FIELD19, ITM.USERDEFINED_FIELD20, "
					+ "                ITM.USERDEFINED_FIELD21,ITM.USERDEFINED_FIELD22, "
					+ "                ITM.USERDEFINED_FIELD23,ITM.USERDEFINED_FIELD24, "
					+ "                ITM.USERDEFINED_FIELD25, ITM.USERDEFINED_FIELD26, "
					+ "                ITM.USERDEFINED_FIELD27,ITM.USERDEFINED_FIELD28, "
					+ "                ITM.USERDEFINED_FIELD29,ITM.USERDEFINED_FIELD30, "
					+ "                HDR.SUPPLY_TYPE AS SUPPLYTYPEASP, '' AS ApproximateDistance_ASP, "
					+ "                '' AS DistanceSavedtoEWB, HDR.CREATED_BY as USER_ID, "
					+ "                HDR.FILE_ID, FIL.FILE_NAME as FILE_NAME, "
					+ "                TO_CHAR(HDR.INV_OTHER_CHARGES) as InvoiceOtherChargesASP, "
					+ "                TO_CHAR(HDR.INV_ASSESSABLE_AMT) as InvoiceAssessableAmountASP, "
					+ "                TO_CHAR(HDR.INV_IGST_AMT) as InvoiceIGSTAmountASP, "
					+ "                TO_CHAR(HDR.INV_CGST_AMT) as InvoiceCGSTAmountASP, "
					+ "                TO_CHAR(HDR.INV_SGST_AMT) as InvoiceSGSTAmountASP, "
					+ "                TO_CHAR(HDR.INV_CESS_ADVLRM_AMT) as InvoiceCessAdvaloremAmountASP, "
					+ "                TO_CHAR(HDR.INV_CESS_SPECIFIC_AMT) as InvoiceCessSpecificAmountASP, "
					+ "                TO_CHAR(HDR.INV_STATE_CESS_AMT) as InvoiceStateCessAdvaloremAmountASP, "
					+ "                TO_CHAR(HDR.INV_STATE_CESS_SPECIFIC_AMOUNT) as "
					+ "                InvoiceStateCessSpecificAmountASP,TO_CHAR(HDR.DOC_AMT), "
					+ "                TO_CHAR(ITM.MEMO_VALUE_IGST), TO_CHAR(HDR.MEMO_VALUE_CGST), "
					+ "                TO_CHAR(HDR.MEMO_VALUE_SGST), TO_CHAR(ITM.MEMO_VALUE_CESS_ADV) "
					+ "                AS CessAdvaloremAmount_ASP, TO_CHAR(ITM.MEMO_VALUE_STATE_CESS_ADV) "
					+ "                AS StateCessAdvaloremAmount_ASP, TO_CHAR(ITM.IGST_RET1_IMPACT), "
					+ "                TO_CHAR(ITM.CGST_RET1_IMPACT), TO_CHAR(ITM.SGST_RET1_IMPACT), "
					+ "                TO_CHAR(ITM.CESS_ADV_RET1_IMPACT) AS CessAdvaloremAmount_Difference, "
					+ "                TO_CHAR(ITM.STATE_CESS_ADV_RET1_IMPACT) AS "
					+ "                StateCessAdvaloremAmount_Difference, "
					+ "                CASE WHEN HDR.IS_DELETE = TRUE THEN 'INACTIVE' WHEN "
					+ "                HDR.IS_DELETE = FALSE THEN 'ACTIVE'END AS RECORD_STATUS, "
					+ "                TO_CHAR(ITM.ADJ_REF_NO),TO_CHAR(ITM.ADJ_REF_DATE), "
					+ "                TO_CHAR(ITM.ADJ_TAXABLE_VALUE), "
					+ "                TO_CHAR(ITM.ADJ_IGST_AMT),TO_CHAR(ITM.ADJ_CGST_AMT), "
					+ "                TO_CHAR(ITM.ADJ_SGST_AMT), "
					+ "                TO_CHAR(ITM.ADJ_CESS_AMT_ADVALOREM), "
					+ "                TO_CHAR(ITM.ADJ_CESS_AMT_SPECIFIC), "
					+ "                TO_CHAR(ITM.ADJ_STATECESS_AMT), "
					+ "                TO_CHAR(ITM.TCS_AMT) as TCSamount, "
					+ "                TO_CHAR(HDR.CUST_SUPP_ADDRESS3) "
					+ "FROM ANX_OUTWARD_DOC_HEADER HDR "
					+ "LEFT OUTER JOIN(SELECT  DOC_HEADER_ID "
					+ "                      , ITEM_INDEX "
					+ "                      , ITM_NO "
					+ "                      , STRING_AGG(ERROR_CODE_GSTIN,',') ERROR_CODE_GSTIN "
					+ "                      , STRING_AGG(ERROR_DESCRIPTION_GSTIN,',') ERROR_DESCRIPTION_GSTIN "
					+ "                      , STRING_AGG(INFO_ERROR_CODE_GSTIN,',') INFO_ERROR_CODE_GSTIN "
					+ "                      , STRING_AGG(INFO_ERROR_DESCRIPTION_GSTIN,',') INFO_ERROR_DESCRIPTION_GSTIN "
					+ "                      , STRING_AGG(ERROR_CODE_ASP,',') ERROR_CODE_ASP "
					+ "                      , STRING_AGG(ERROR_DESCRIPTION_ASP,',') ERROR_DESCRIPTION_ASP "
					+ "                      , STRING_AGG(INFO_ERROR_CODE_ASP,',') INFO_ERROR_CODE_ASP "
					+ "                      , STRING_AGG(INFO_ERROR_DESCRIPTION_ASP,',') INFO_ERROR_DESCRIPTION_ASP "
					+ "                      ,SUPPLIER_GSTIN, FILE_ID, DERIVED_RET_PERIOD "
					+ "              "
					+ "             FROM "
					+ "             ( "
					+ "             SELECT DOC_HEADER_ID "
					+ "                    , ITEM_INDEX "
					+ "                    , ITM_NO "
					+ "                    , (CASE WHEN ERROR_TYPE='ERR'  THEN ERROR_CODE END) AS ERROR_CODE_GSTIN "
					+ "                    , (CASE WHEN ERROR_TYPE='ERR'  THEN ERROR_DESCRIPTION END) AS ERROR_DESCRIPTION_GSTIN "
					+ "                     , (CASE WHEN ERROR_TYPE='INFO' THEN ERROR_CODE END)  AS INFO_ERROR_CODE_GSTIN "
					+ "                    , (CASE WHEN ERROR_TYPE='INFO' THEN ERROR_DESCRIPTION END)AS INFO_ERROR_DESCRIPTION_GSTIN "
					+ "                    , ' ' AS ERROR_CODE_ASP,' ' AS ERROR_DESCRIPTION_ASP "
					+ "                    , ' ' AS INFO_ERROR_CODE_ASP, ' ' AS INFO_ERROR_DESCRIPTION_ASP "
					+ "                            ,SUPPLIER_GSTIN, FILE_ID, DERIVED_RET_PERIOD "
					+ "             FROM \"ANX_OUTWARD_DOC_ERROR\" "
					+ "             WHERE ITEM_INDEX IS NULL AND ERROR_SOURCE='GSTN' "
					+ "              "
					+ "             UNION ALL "
					+ "             SELECT DOC_HEADER_ID "
					+ "                    , ITEM_INDEX "
					+ "                    , ITM_NO "
					+ "                    ,' 'AS ERROR_CODE_GSTIN "
					+ "                    ,' ' AS ERROR_DESCRIPTION_GSTIN "
					+ "                    ,' ' AS INFO_ERROR_CODE_GSTIN "
					+ "                    ,' ' AS INFO_ERROR_DESCRIPTION_GSTIN "
					+ "                    , (CASE WHEN ERROR_TYPE='ERR' THEN ERROR_CODE END) AS ERROR_CODE_ASP "
					+ "                    , (CASE WHEN ERROR_TYPE='ERR' THEN ERROR_DESCRIPTION END) AS ERROR_DESCRIPTION_ASP "
					+ "                     , (CASE WHEN ERROR_TYPE='INFO' THEN ERROR_CODE END)  AS INFO_ERROR_CODE_ASP "
					+ "                    , (CASE WHEN ERROR_TYPE='INFO' THEN ERROR_DESCRIPTION END) AS INFO_ERROR_DESCRIPTION_ASP "
					+ "                             ,SUPPLIER_GSTIN, FILE_ID, DERIVED_RET_PERIOD "
					+ "                    "
					+ "              FROM \"ANX_OUTWARD_DOC_ERROR\" "
					+ "             WHERE ITEM_INDEX IS NULL AND ERROR_SOURCE='ASP' AND FILE_ID = :fileId) "
					+ "             GROUP BY DOC_HEADER_ID, ITEM_INDEX, ITM_NO      ,SUPPLIER_GSTIN, FILE_ID, DERIVED_RET_PERIOD "
					+ "             )ERRH "
					+ "             ON ERRH.FILE_ID = HDR.FILE_ID "
					+ "             AND ERRH.DOC_HEADER_ID = HDR.ID "
					+ "             AND ERRH.SUPPLIER_GSTIN = HDR.SUPPLIER_GSTIN "
					+ "             AND ERRH.DERIVED_RET_PERIOD = HDR.DERIVED_RET_PERIOD "
					+ "             AND " + buildQuery   
					+ "             INNER JOIN ANX_OUTWARD_DOC_ITEM ITM ON HDR.ID = ITM.DOC_HEADER_ID AND " + buildQuery
					+ "             LEFT OUTER JOIN "
					+ "             (SELECT DOC_HEADER_ID "
					+ "                    , ITEM_INDEX "
					+ "                    , ITM_NO "
					+ "                    , CREATED_ON "
					+ "                    , STRING_AGG(ERROR_CODE_GSTN,',') ERROR_CODE_GSTN "
					+ "                    , STRING_AGG(ERROR_DESCRIPTION_GSTN,',') ERROR_DESCRIPTION_GSTN "
					+ "                    , STRING_AGG(INFO_ERROR_CODE_GSTN,',') INFO_ERROR_CODE_GSTN "
					+ "                    , STRING_AGG(INFO_ERROR_DESCRIPTION_GSTN,',') INFO_ERROR_DESCRIPTION_GSTN "
					+ "                    , STRING_AGG(ERROR_CODE_ASP,',') ERROR_CODE_ASP "
					+ "                    , STRING_AGG(ERROR_DESCRIPTION_ASP,',') ERROR_DESCRIPTION_ASP "
					+ "                    , STRING_AGG(INFO_ERROR_CODE_ASP,',') INFO_ERROR_CODE_ASP "
					+ "                    , STRING_AGG(INFO_ERROR_DESCRIPTION_ASP,',') INFO_ERROR_DESCRIPTION_ASP "
					+ "                   , SUPPLIER_GSTIN, FILE_ID, DERIVED_RET_PERIOD "
					+ "             FROM "
					+ "             ( "
					+ "             SELECT DOC_HEADER_ID "
					+ "                    , ITEM_INDEX "
					+ "                    , ITM_NO "
					+ "                    , CREATED_ON "
					+ "                    , (CASE WHEN ERROR_TYPE='ERR'  THEN ERROR_CODE END) AS ERROR_CODE_GSTN, "
					+ "                    (CASE WHEN ERROR_TYPE='ERR'  THEN ERROR_DESCRIPTION END) AS ERROR_DESCRIPTION_GSTN , "
					+ "                    (CASE WHEN ERROR_TYPE='INFO' THEN ERROR_CODE END)  AS INFO_ERROR_CODE_GSTN, "
					+ "                    (CASE WHEN ERROR_TYPE='INFO' THEN ERROR_DESCRIPTION END)AS INFO_ERROR_DESCRIPTION_GSTN, "
					+ "                    ' ' AS ERROR_CODE_ASP,' ' AS ERROR_DESCRIPTION_ASP, "
					+ "                    ' ' AS INFO_ERROR_CODE_ASP, ' ' AS INFO_ERROR_DESCRIPTION_ASP "
					+ "                            ,SUPPLIER_GSTIN, FILE_ID, DERIVED_RET_PERIOD "
					+ "             FROM \"ANX_OUTWARD_DOC_ERROR\" "
					+ "             WHERE ITEM_INDEX IS NOT NULL AND ERROR_SOURCE='GSTN' "
					+ "              "
					+ "             UNION ALL "
					+ "              "
					+ "             SELECT DOC_HEADER_ID "
					+ "                    , ITEM_INDEX "
					+ "                    , ITM_NO "
					+ "                    , CREATED_ON "
					+ "                    , ' 'AS ERROR_CODE_GSTN "
					+ "                    , ' ' AS ERROR_DESCRIPTION_GSTN "
					+ "                    , ' ' AS INFO_ERROR_CODE_GSTN "
					+ "                    , ' ' AS INFO_ERROR_DESCRIPTION_GSTN "
					+ "                    , (CASE WHEN ERROR_TYPE='ERR' THEN ERROR_CODE END) AS ERROR_CODE_ASP, "
					+ "                    (CASE WHEN ERROR_TYPE='ERR' THEN ERROR_DESCRIPTION END) AS ERROR_DESCRIPTION_ASP , "
					+ "                    (CASE WHEN ERROR_TYPE='INFO' THEN ERROR_CODE END)  AS INFO_ERROR_CODE_ASP, "
					+ "                    (CASE WHEN ERROR_TYPE='INFO' THEN ERROR_DESCRIPTION END) AS INFO_ERROR_DESCRIPTION_ASP "
					+ "                            ,SUPPLIER_GSTIN, FILE_ID, DERIVED_RET_PERIOD "
					+ "             FROM \"ANX_OUTWARD_DOC_ERROR\" "
					+ "             WHERE ITEM_INDEX IS NOT NULL AND ERROR_SOURCE='ASP' AND FILE_ID = :fileId "
					+ "             ) "
					+ "             GROUP BY DOC_HEADER_ID, ITEM_INDEX, ITM_NO,CREATED_ON        ,SUPPLIER_GSTIN, FILE_ID, DERIVED_RET_PERIOD "
					+ "             ) ERRI "
					+ "             ON  ERRI.FILE_ID = HDR.FILE_ID "
					+ "             AND ERRH.FILE_ID = ERRI.FILE_ID "
					+ "             AND ERRI.DERIVED_RET_PERIOD= HDR.DERIVED_RET_PERIOD "
					+ "             AND HDR.ID = ERRI.DOC_HEADER_ID "
					+ "             AND TO_CHAR(ITM.ITM_NO) = TO_CHAR(ERRI.ITM_NO) "
					+ "   INNER JOIN FILE_STATUS FIL ON HDR.FILE_ID=FIL.ID "
					+ "   LEFT OUTER JOIN GSTR1_GSTN_SAVE_BATCH GSB ON GSB.ID = HDR.BATCH_ID "
					+ "   LEFT OUTER JOIN EINV_MASTER EINV ON EINV.DOC_HEADER_ID = HDR.ID AND ITM.DOC_HEADER_ID = EINV.DOC_HEADER_ID "
					+ "   WHERE IS_ERROR=TRUE AND       HDR.IS_DELETE=FALSE  "  
					+ "                           ORDER BY  HDR.DOC_NUM,ITM.ITM_NO ) "
					+ "                 "
					+ "                UNION ALL "
					+ "                 "
					+ "                (SELECT TRIM(', ' "
					+ "                FROM IFNULL(ERRH.ERROR_CODE_ASP,'') ||','|| IFNULL( "
					+ "                ERRI.ERROR_CODE_ASP,'')) AS ERROR_CODE_ASP, TRIM(', ' "
					+ "                FROM IFNULL(ERRH.ERROR_DESCRIPTION_ASP,'') ||','|| "
					+ "                IFNULL(ERRI.ERROR_DESCRIPTION_ASP,'')) AS ERROR_DESCRIPTION_ASP, "
					+ "                TRIM(', ' FROM IFNULL(ERRH.INFO_ERROR_CODE_ASP,'') ||','|| "
					+ "                IFNULL(ERRI.INFO_ERROR_CODE_ASP,'')) AS INFO_ERROR_CODE_ASP, "
					+ "                TRIM(', ' FROM IFNULL(ERRH.INFO_ERROR_DESCRIPTION_ASP,'') "
					+ "                ||','|| IFNULL(ERRI.INFO_ERROR_DESCRIPTION_ASP,'')) AS "
					+ "                INFO_ERROR_DESCRIPTION_ASP, EINV.STATUS_DESCRIPTION AS "
					+ "                IRN_STATUS, EINV.IRN AS IRN_NO, EINV.ACK_NUM AS IRN_ACK_NO, "
					+ "                HDR.IRN_DATE AS IRN_ACK_DATE_TIME, EINV.SIGNED_QR AS "
					+ "                SIGNED_QR_CODE, EINV.SIGNED_INV AS SIGNED_INVOICE, "
					+ "                HDR.EINV_ERROR_CODE AS IRP_ERROR_CODE, HDR.EINV_ERROR_DESC "
					+ "                AS IRP_ERROR_DESCRIPTION, "
					+ "                case  when HDR.EWB_STATUS = 1 then 'Not Applicable' "
					+ "                      when HDR.EWB_STATUS = 2 then 'Pending' "
					+ "                      when HDR.EWB_STATUS = 3 then 'Generation Error' "
					+ "                                 when HDR.EWB_STATUS = 4 then 'Part A Generated' "
					+ "                                 when HDR.EWB_STATUS = 5 then 'EWB Active' "
					+ "                                 when HDR.EWB_STATUS = 6 then 'Cancelled' "
					+ "                                 when HDR.EWB_STATUS = 7 then 'Discarded' "
					+ "                                 when HDR.EWB_STATUS = 8 then 'Rejected' "
					+ "                                 when HDR.EWB_STATUS = 9 then 'Expired' "
					+ "                           end as EWBPartAStatusA, "
					+ "                '' AS EWBValidUpTo, HDR.EWAY_BILL_NUM AS EWBNO, "
					+ "                CAST(HDR.EWAY_BILL_DATE AS DATE) AS EWBDATE, "
					+ "                HDR.EWB_ERROR_CODE AS EWB_ERROR_CODE, HDR.EWB_ERROR_DESC "
					+ "                AS EWB_ERROR_DESCRIPTION, "
					+ "                '' AS GSTIN_STATUS, "
					+ "                GSB.GSTN_SAVE_REF_ID AS GSTIN_REF_ID, GSB.GSTN_RESP_DATE "
					+ "                AS GSTIN_REFID_DATE_TIME, TRIM(', ' FROM IFNULL( "
					+ "                ERRH.ERROR_CODE_GSTIN,'') ||','|| IFNULL( "
					+ "                ERRI.ERROR_CODE_GSTN,'')) AS GSTIN_ERROR_CODE, TRIM(', ' "
					+ "                FROM IFNULL(ERRH.ERROR_DESCRIPTION_GSTIN,'') ||','|| "
					+ "                IFNULL(ERRI.ERROR_DESCRIPTION_GSTN,'')) AS "
					+ "                GSTIN_ERROR_DESCRIPTION_ASP,HDR.AN_RETURN_TYPE AS RETURN_TYPE, "
					+ "                HDR.AN_TABLE_SECTION AS TABLE_NUMBER,HDR.IRN, HDR.IRN_DATE,HDR.TAX_SCHEME, "
					+ "                HDR.CANCEL_REASON, HDR.CANCEL_REMARKS, ITM.SUPPLY_TYPE, "
					+ "                HDR.DOC_CATEGORY, HDR.DOC_TYPE,TO_CHAR(HDR.DOC_NUM), "
					+ "                HDR.DOC_DATE,HDR.REVERSE_CHARGE, HDR.SUPPLIER_GSTIN, "
					+ "                HDR.SUPP_TRADE_NAME, HDR.SUPP_LEGAL_NAME,HDR.SUPP_BUILDING_NUM, "
					+ "                HDR.CUST_SUPP_ADDRESS2 AS SUPPLIERADDRESS2,HDR.SUPP_LOCATION, "
					+ "                HDR.SUPP_PINCODE,HDR.SUPP_STATE_CODE,HDR.SUPP_PHONE, "
					+ "                HDR.SUPP_EMAIL,HDR.CUST_GSTIN,HDR.CUST_TRADE_NAME, "
					+ "                HDR.CUST_SUPP_NAME,HDR.CUST_SUPP_ADDRESS1,HDR.CUST_SUPP_ADDRESS2, "
					+ "                HDR.CUST_SUPP_ADDRESS4,HDR.CUST_PINCODE,HDR.BILL_TO_STATE, "
					+ "                HDR.POS,HDR.CUST_PHONE,HDR.CUST_EMAIL,HDR.DISPATCHER_GSTIN, "
					+ "                HDR.DISPATCHER_TRADE_NAME,HDR.DISPATCHER_BUILDING_NUM, "
					+ "                HDR.DISPATCHER_BUILDING_NAME,HDR.DISPATCHER_LOCATION, "
					+ "                HDR.DISPATCHER_PINCODE,HDR.DISPATCHER_STATE_CODE, '' AS "
					+ "                SHIP_TO_FLOOR_NUM,HDR.SHIP_TO_TRADE_NAME, "
					+ "                HDR.SHIP_TO_LEGAL_NAME,HDR.SHIP_TO_BUILDING_NUM, "
					+ "                HDR.SHIP_TO_BUILDING_NAME,HDR.SHIP_TO_LOCATION, "
					+ "                ITM.SHIP_TO_PINCODE,ITM.SHIP_TO_STATE AS SHIPTOSTATECODE, "
					+ "                ITM.ITM_NO, ITM.SERIAL_NUM2,ITM.PRODUCT_NAME,ITM.ITM_DESCRIPTION, "
					+ "                ITM.IS_SERVICE,ITM.ITM_HSNSAC,ITM.BAR_CODE, ITM.BATCH_NAME_OR_NUM, "
					+ "                ITM.BATCH_EXPIRY_DATE,ITM.WARRANTY_DATE, ITM.ORDER_ITEM_REFERENCE, "
					+ "                ITM.ATTRIBUTE_NAME,ITM.ATTRIBUTE_VALUE, ITM.ORIGIN_COUNTRY, "
					+ "                ITM.ITM_UQC,ITM.ITM_QTY,ITM.FREE_QTY, ITM.UNIT_PRICE, "
					+ "                ITM.ITEM_AMT_UP_QTY, ITM.ITEM_DISCOUNT, ITM.PRE_TAX_AMOUNT, "
					+ "                ITM.TAXABLE_VALUE,ITM.IGST_RATE, ITM.IGST_AMT,ITM.CGST_RATE, "
					+ "                ITM.CGST_AMT,ITM.SGST_RATE, ITM.SGST_AMT, "
					+ "                ITM.CESS_RATE_ADVALOREM,ITM.CESS_AMT_ADVALOREM, "
					+ "                ITM.CESS_RATE_SPECIFIC, ITM.CESS_AMT_SPECIFIC, "
					+ "                ITM.STATECESS_RATE,ITM.STATECESS_AMT, "
					+ "                ITM.STATE_CESS_SPECIFIC_RATE, ITM.STATE_CESS_SPECIFIC_AMOUNT, "
					+ "                ITM.OTHER_VALUES,TOT_ITEM_AMT, ITM.INV_OTHER_CHARGES, "
					+ "                ITM.INV_ASSESSABLE_AMT,ITM.INV_IGST_AMT, ITM.INV_CGST_AMT, "
					+ "                ITM.INV_SGST_AMT,ITM.INV_CESS_ADVLRM_AMT, "
					+ "                ITM.INV_CESS_SPECIFIC_AMT,ITM.INV_STATE_CESS_AMT, "
					+ "                ITM.INV_STATE_CESS_SPECIFIC_AMOUNT,ITM.LINE_ITEM_AMT, "
					+ "                HDR.ROUND_OFF,HDR.TOT_INV_VAL_WORLDS,HDR.TCS_FLAG_INCOME_TAX, "
					+ "                ITM.TCS_RATE_INCOME_TAX,ITM.TCS_AMOUNT_INCOME_TAX, "
					+ "                CUSTOMER_PAN_OR_AADHAAR, HDR.FOREIGN_CURRENCY , "
					+ "                HDR.COUNTRY_CODE, HDR.INV_VAL_FC, HDR.SHIP_PORT_CODE, "
					+ "                HDR.SHIP_BILL_NUM,HDR.SHIP_BILL_DATE, HDR.INV_REMARKS, "
					+ "                HDR.INV_PERIOD_START_DATE,HDR.INV_PERIOD_END_DATE, "
					+ "                ITM.PRECEEDING_INV_NUM,ITM.PRECEEDING_INV_DATE, "
					+ "                ITM.INV_REFERENCE, ITM.RECEIPT_ADVICE_REFERENCE, "
					+ "                ITM.RECEIPT_ADVICE_DATE, ITM.TENDER_REFERENCE , "
					+ "                ITM.CONTRACT_REFERENCE,ITM.EXTERNAL_REFERENCE, "
					+ "                ITM.PROJECT_REFERENCE,ITM.CUST_PO_REF_NUM,ITM.CUST_PO_REF_DATE, "
					+ "                HDR.PAYEE_NAME,HDR.MODE_OF_PAYMENT,HDR.BRANCH_IFSC_CODE, "
					+ "                 HDR.PAYMENT_TERMS,HDR.PAYMENT_INSTRUCTION,HDR.CR_TRANSFER, "
					+ "                 HDR.DB_DIRECT,HDR.CR_DAYS,ITM.PAID_AMT,ITM.BAL_AMT, "
					+ "                HDR.PAYMENT_DUE_DATE,HDR.ACCOUNT_DETAIL,HDR.ECOM_GSTIN, "
					+ "                HDR.ECOM_TRANS_ID,ITM.SUPPORTING_DOC_URL,ITM.SUPPORTING_DOC_BASE64, "
					+ "                ITM.ADDITIONAL_INFORMATION,HDR.TRANS_TYPE,HDR.SUB_SUPP_TYPE, "
					+ "                HDR.OTHER_SUPP_TYPE_DESC,HDR.TRANSPORTER_ID,HDR.TRANSPORTER_NAME, "
					+ "                HDR.TRANSPORT_MODE,HDR.TRANSPORT_DOC_NUM,HDR.TRANSPORT_DOC_DATE, "
					+ "                HDR.DISTANCE ,HDR.VEHICLE_NUM,HDR.VEHICLE_TYPE, "
					+ "                HDR.RETURN_PERIOD,HDR.ORIGINAL_DOC_TYPE,HDR.ORIGINAL_CUST_GSTIN, "
					+ "                HDR.DIFF_PERCENT,HDR.SECTION7_OF_IGST_FLAG,HDR.CLAIM_REFUND_FLAG, "
					+ "                HDR.AUTOPOPULATE_TO_REFUND,HDR.CRDR_PRE_GST,HDR.CUST_SUPP_TYPE, "
					+ "                HDR.CUST_SUPP_CODE AS CUSTOMERCODE,ITM.PRODUCT_CODE,ITM.ITM_TYPE, "
					+ "                ITM.ITC_FLAG, HDR.STATE_APPLYING_CESS,ITM.FOB , ITM.EXPORT_DUTY, "
					+ "                HDR.EXCHANGE_RATE,ITM.CRDR_REASON, HDR.TCS_FLAG , "
					+ "                ITM.TCS_AMT,TCS_CGST_AMT,TCS_SGST_AMT, HDR.TDS_FLAG, "
					+ "                TDS_IGST_AMT,TDS_CGST_AMT,TDS_SGST_AMT, HDR.USER_ID AS "
					+ "                userid, HDR.COMPANY_CODE,HDR.SOURCE_IDENTIFIER, "
					+ "                HDR.SOURCE_FILENAME, ITM.PLANT_CODE,HDR.DIVISION, "
					+ "                SUB_DIVISION,ITM.LOCATION, HDR.SALES_ORGANIZATION, "
					+ "                HDR.DISTRIBUTION_CHANNEL, HDR.PROFIT_CENTRE,HDR.PROFIT_CENTRE2, "
					+ "                HDR.USERACCESS1, HDR.USERACCESS2,HDR.USERACCESS3, "
					+ "                HDR.USERACCESS4,HDR.USERACCESS5, HDR.USERACCESS6, "
					+ "                ITM.GLCODE_TAXABLEVALUE,ITM.GLCODE_IGST, ITM.GLCODE_CGST, "
					+ "                ITM.GLCODE_SGST,ITM.GLCODE_ADV_CESS,ITM.GLCODE_SP_CESS, "
					+ "                ITM.GLCODE_STATE_CESS, HDR.GL_STATE_CESS_SPECIFIC, "
					+ "                HDR.GL_POSTING_DATE,HDR.SALES_ORD_NUM,HDR.EWAY_BILL_NUM, "
					+ "                CAST(HDR.EWAY_BILL_DATE AS DATE), "
					+ "                HDR.ACCOUNTING_VOUCHER_NUM, "
					+ "                HDR.ACCOUNTING_VOUCHER_DATE, ITM.DOCUMENT_REFERENCE_NUMBER, "
					+ "                HDR.CUST_TAN, ITM.USERDEFINED_FIELD1, ITM.USERDEFINED_FIELD2, "
					+ "                ITM.USERDEFINED_FIELD3,ITM.USERDEFINED_FIELD4, "
					+ "                ITM.USERDEFINED_FIELD5,ITM.USERDEFINED_FIELD6, "
					+ "                ITM.USERDEFINED_FIELD7, ITM.USERDEFINED_FIELD8, "
					+ "                ITM.USERDEFINED_FIELD9,ITM.USERDEFINED_FIELD10, "
					+ "                ITM.USERDEFINED_FIELD11,ITM.USERDEFINED_FIELD12, "
					+ "                ITM.USERDEFINED_FIELD13, ITM.USERDEFINED_FIELD14, "
					+ "                ITM.USERDEFINED_FIELD15,ITM.USERDEFINED_FIELD16, "
					+ "                ITM.USERDEFINED_FIELD17,ITM.USERDEFINED_FIELD18, "
					+ "                ITM.USERDEFINED_FIELD19, ITM.USERDEFINED_FIELD20, "
					+ "                ITM.USERDEFINED_FIELD21,ITM.USERDEFINED_FIELD22, "
					+ "                ITM.USERDEFINED_FIELD23,ITM.USERDEFINED_FIELD24, "
					+ "                ITM.USERDEFINED_FIELD25, ITM.USERDEFINED_FIELD26, "
					+ "                ITM.USERDEFINED_FIELD27,ITM.USERDEFINED_FIELD28, "
					+ "                ITM.USERDEFINED_FIELD29,ITM.USERDEFINED_FIELD30, "
					+ "                HDR.SUPPLY_TYPE AS SUPPLYTYPEASP, '' AS ApproximateDistance_ASP, "
					+ "                '' AS DistanceSavedtoEWB, HDR.CREATED_BY as USER_ID, "
					+ "                HDR.FILE_ID, FIL.FILE_NAME as FILE_NAME, HDR.INV_OTHER_CHARGES "
					+ "                as InvoiceOtherChargesASP, HDR.INV_ASSESSABLE_AMT as "
					+ "                InvoiceAssessableAmountASP, HDR.INV_IGST_AMT as "
					+ "                InvoiceIGSTAmountASP, HDR.INV_CGST_AMT as InvoiceCGSTAmountASP, "
					+ "                HDR.INV_SGST_AMT as InvoiceSGSTAmountASP, HDR.INV_CESS_ADVLRM_AMT "
					+ "                as InvoiceCessAdvaloremAmountASP, HDR.INV_CESS_SPECIFIC_AMT "
					+ "                as InvoiceCessSpecificAmountASP, HDR.INV_STATE_CESS_AMT as "
					+ "                InvoiceStateCessAdvaloremAmountASP, HDR.INV_STATE_CESS_SPECIFIC_AMOUNT "
					+ "                as InvoiceStateCessSpecificAmountASP, HDR.DOC_AMT, "
					+ "                ITM.MEMO_VALUE_IGST,HDR.MEMO_VALUE_CGST,HDR.MEMO_VALUE_SGST, "
					+ "                ITM.MEMO_VALUE_CESS_ADV AS CessAdvaloremAmount_ASP, "
					+ "                ITM.MEMO_VALUE_STATE_CESS_ADV AS StateCessAdvaloremAmount_ASP, "
					+ "                ITM.IGST_RET1_IMPACT,ITM.CGST_RET1_IMPACT, ITM.SGST_RET1_IMPACT, "
					+ "                ITM.CESS_ADV_RET1_IMPACT AS CessAdvaloremAmount_Difference, "
					+ "                ITM.STATE_CESS_ADV_RET1_IMPACT AS StateCessAdvaloremAmount_Difference, "
					+ "                '' AS RECORD_STATUS, "
					+ "                ITM.ADJ_REF_NO,ITM.ADJ_REF_DATE, "
					+ "                ITM.ADJ_TAXABLE_VALUE,ITM.ADJ_IGST_AMT,ITM.ADJ_CGST_AMT, "
					+ "                ITM.ADJ_SGST_AMT,ITM. ADJ_CESS_AMT_ADVALOREM, "
					+ "                ITM.ADJ_CESS_AMT_SPECIFIC,ITM.ADJ_STATECESS_AMT, "
					+ "                ITM.TCS_AMT AS Tcsamount,HDR.CUST_SUPP_ADDRESS3 "
					+ "             FROM ANX_OUTWARD_ERR_HEADER HDR "
					+ "             LEFT OUTER JOIN( "
					+ "                           SELECT  DOC_HEADER_ID "
					+ "                                    , ITEM_INDEX "
					+ "                                    , ITM_NO "
					+ "                                    , STRING_AGG(ERROR_CODE_GSTIN,',') ERROR_CODE_GSTIN "
					+ "                                    , STRING_AGG(ERROR_DESCRIPTION_GSTIN,',') ERROR_DESCRIPTION_GSTIN "
					+ "                                    , STRING_AGG(INFO_ERROR_CODE_GSTIN,',') INFO_ERROR_CODE_GSTIN "
					+ "                                    , STRING_AGG(INFO_ERROR_DESCRIPTION_GSTIN,',') INFO_ERROR_DESCRIPTION_GSTIN "
					+ "                                    , STRING_AGG(ERROR_CODE_ASP,',') ERROR_CODE_ASP "
					+ "                                    , STRING_AGG(ERROR_DESCRIPTION_ASP,',') ERROR_DESCRIPTION_ASP "
					+ "                                    , STRING_AGG(INFO_ERROR_CODE_ASP,',') INFO_ERROR_CODE_ASP "
					+ "                                    , STRING_AGG(INFO_ERROR_DESCRIPTION_ASP,',') INFO_ERROR_DESCRIPTION_ASP "
					+ "                                    ,SUPPLIER_GSTIN, FILE_ID, DERIVED_RET_PERIOD "
					+ "                            "
					+ "                           FROM "
					+ "                           ( "
					+ "                           SELECT DOC_HEADER_ID "
					+ "                                  , ITEM_INDEX "
					+ "                                  , ITM_NO "
					+ "                                  ,' 'AS ERROR_CODE_GSTIN "
					+ "                                  ,' ' AS ERROR_DESCRIPTION_GSTIN "
					+ "                                  ,' ' AS INFO_ERROR_CODE_GSTIN "
					+ "                                  ,' ' AS INFO_ERROR_DESCRIPTION_GSTIN "
					+ "                                  , (CASE WHEN ERROR_TYPE='ERR' THEN ERROR_CODE END) AS ERROR_CODE_ASP "
					+ "                                  , (CASE WHEN ERROR_TYPE='ERR' THEN ERROR_DESCRIPTION END) AS ERROR_DESCRIPTION_ASP "
					+ "                                   , (CASE WHEN ERROR_TYPE='INFO' THEN ERROR_CODE END)  AS INFO_ERROR_CODE_ASP "
					+ "                                  , (CASE WHEN ERROR_TYPE='INFO' THEN ERROR_DESCRIPTION END) AS INFO_ERROR_DESCRIPTION_ASP "
					+ "                                           ,SUPPLIER_GSTIN, FILE_ID, DERIVED_RET_PERIOD "
					+ "                                  "
					+ "                            FROM \"ANX_OUTWARD_DOC_ERROR\" "
					+ "                           WHERE ITEM_INDEX IS NULL AND ERROR_SOURCE='ASP' AND FILE_ID = :fileId) "
					+ "                           GROUP BY DOC_HEADER_ID, ITEM_INDEX, ITM_NO      ,SUPPLIER_GSTIN, FILE_ID, DERIVED_RET_PERIOD "
					+ "                           )ERRH "
					+ "                           ON ERRH.FILE_ID = HDR.FILE_ID "
					+ "                           AND ERRH.DOC_HEADER_ID = HDR.ID "
					+ "                           AND ERRH.SUPPLIER_GSTIN = HDR.SUPPLIER_GSTIN "
					+ "                           AND ERRH.DERIVED_RET_PERIOD = HDR.DERIVED_RET_PERIOD AND "+buildQuery
					+ "                           INNER JOIN ANX_OUTWARD_ERR_ITEM ITM ON HDR.ID = ITM.DOC_HEADER_ID AND "+ buildQuery
					+ "                           LEFT OUTER JOIN "
					+ "                           (SELECT DOC_HEADER_ID "
					+ "                                  , ITEM_INDEX "
					+ "                                  , ITM_NO "
					+ "                                  , CREATED_ON "
					+ "                                  , STRING_AGG(ERROR_CODE_GSTN,',') ERROR_CODE_GSTN "
					+ "                                  , STRING_AGG(ERROR_DESCRIPTION_GSTN,',') ERROR_DESCRIPTION_GSTN "
					+ "                                  , STRING_AGG(INFO_ERROR_CODE_GSTN,',') INFO_ERROR_CODE_GSTN "
					+ "                                  , STRING_AGG(INFO_ERROR_DESCRIPTION_GSTN,',') INFO_ERROR_DESCRIPTION_GSTN "
					+ "                                  , STRING_AGG(ERROR_CODE_ASP,',') ERROR_CODE_ASP "
					+ "                                  , STRING_AGG(ERROR_DESCRIPTION_ASP,',') ERROR_DESCRIPTION_ASP "
					+ "                                  , STRING_AGG(INFO_ERROR_CODE_ASP,',') INFO_ERROR_CODE_ASP "
					+ "                                  , STRING_AGG(INFO_ERROR_DESCRIPTION_ASP,',') INFO_ERROR_DESCRIPTION_ASP "
					+ "                                 , SUPPLIER_GSTIN, FILE_ID, DERIVED_RET_PERIOD "
					+ "                           FROM "
					+ "                           ( "
					+ "                            "
					+ "                            "
					+ "                           SELECT DOC_HEADER_ID "
					+ "                                  , ITEM_INDEX "
					+ "                                  , ITM_NO "
					+ "                                  , CREATED_ON "
					+ "                                  , ' 'AS ERROR_CODE_GSTN "
					+ "                                  , ' ' AS ERROR_DESCRIPTION_GSTN "
					+ "                                  , ' ' AS INFO_ERROR_CODE_GSTN "
					+ "                                  , ' ' AS INFO_ERROR_DESCRIPTION_GSTN "
					+ "                                  , (CASE WHEN ERROR_TYPE='ERR' THEN ERROR_CODE END) AS ERROR_CODE_ASP, "
					+ "                                  (CASE WHEN ERROR_TYPE='ERR' THEN ERROR_DESCRIPTION END) AS ERROR_DESCRIPTION_ASP , "
					+ "                                  (CASE WHEN ERROR_TYPE='INFO' THEN ERROR_CODE END)  AS INFO_ERROR_CODE_ASP, "
					+ "                                  (CASE WHEN ERROR_TYPE='INFO' THEN ERROR_DESCRIPTION END) AS INFO_ERROR_DESCRIPTION_ASP "
					+ "                                          ,SUPPLIER_GSTIN, FILE_ID, DERIVED_RET_PERIOD "
					+ "                           FROM \"ANX_OUTWARD_DOC_ERROR\" "
					+ "                           WHERE ITEM_INDEX IS NOT NULL AND ERROR_SOURCE='ASP' AND FILE_ID = :fileId "
					+ "                           ) "
					+ "                           GROUP BY DOC_HEADER_ID, ITEM_INDEX, ITM_NO,CREATED_ON        ,SUPPLIER_GSTIN, FILE_ID, DERIVED_RET_PERIOD "
					+ "                           ) ERRI "
					+ "                           ON  ERRI.FILE_ID = HDR.FILE_ID "
					+ "                           AND ERRH.FILE_ID = ERRI.FILE_ID "
					+ "                           AND ERRI.DERIVED_RET_PERIOD= HDR.DERIVED_RET_PERIOD "
					+ "                           AND HDR.ID = ERRI.DOC_HEADER_ID "
					+ "INNER JOIN FILE_STATUS FIL ON HDR.FILE_ID=FIL.ID "
					+ "LEFT OUTER JOIN  GSTR1_GSTN_SAVE_BATCH GSB ON GSB.ID = HDR.BATCH_ID "
					+ "LEFT OUTER JOIN EINV_MASTER EINV ON EINV.DOC_HEADER_ID = HDR.ID "
					+ "AND ITM.DOC_HEADER_ID = EINV.DOC_HEADER_ID " 
					+ "ORDER BY HDR.DOC_NUM,ITM.ITM_NO ) ;";
			
			
				} else if (DownloadReportsConstant.TOTALERECORDS.equalsIgnoreCase(type))
				{

					 queryStr = ""
							+ "(SELECT TRIM(', ' FROM IFNULL(ERRH.ERROR_CODE_ASP,'') "
							+ "||','|| IFNULL(ERRI.ERROR_CODE_ASP,'')) AS ERROR_CODE_ASP, "
							+ "TRIM(', ' FROM IFNULL(ERRH.ERROR_DESCRIPTION_ASP,'') "
							+ "||','|| IFNULL(ERRI.ERROR_DESCRIPTION_ASP,'')) AS "
							+ "ERROR_DESCRIPTION_ASP, TRIM(', ' FROM IFNULL "
							+ "(ERRH.INFO_ERROR_CODE_ASP,'') ||','|| IFNULL( "
							+ "ERRI.INFO_ERROR_CODE_ASP,'')) AS INFO_ERROR_CODE_ASP, "
							+ "TRIM(', ' FROM IFNULL(ERRH.INFO_ERROR_DESCRIPTION_ASP,'') "
							+ "||','|| IFNULL(ERRI.INFO_ERROR_DESCRIPTION_ASP,'')) AS "
							+ "INFO_ERROR_DESCRIPTION_ASP, EINV.STATUS_DESCRIPTION AS "
							+ "IRN_STATUS, TO_CHAR(EINV.IRN) AS IRN_NO, "
							+ "TO_CHAR(EINV.ACK_NUM) AS IRN_ACK_NO, TO_CHAR( HDR.IRN_DATE) "
							+ "AS IRN_ACK_DATE_TIME, EINV.SIGNED_QR AS SIGNED_QR_CODE, "
							+ "EINV.SIGNED_INV AS SIGNED_INVOICE, HDR.EINV_ERROR_CODE AS "
							+ "IRP_ERROR_CODE, HDR.EINV_ERROR_DESC AS IRP_ERROR_DESCRIPTION, "
							+ "case when HDR.EWB_STATUS = 1 then 'Not Applicable' "
							+ "when HDR.EWB_STATUS = 2 then 'Pending' "
							+ "when HDR.EWB_STATUS = 3 then 'Generation Error' "
							+ "when HDR.EWB_STATUS = 4 then 'Part A Generated' "
							+ "when HDR.EWB_STATUS = 5 then 'EWB Active' "
							+ "when HDR.EWB_STATUS = 6 then 'Cancelled' "
							+ "when HDR.EWB_STATUS = 7 then 'Discarded' "
							+ "when HDR.EWB_STATUS = 8 then 'Rejected' "
							+ "when HDR.EWB_STATUS = 9 then 'Expired' end as EWBPartAStatusA, "
							+ "'' AS EWBValidUpTo, "
							+ "HDR.EWAY_BILL_NUM AS EWBNO, "
							+ "CAST(HDR.EWAY_BILL_DATE AS DATE) AS EWBDATE, "
							+ "HDR.EWB_ERROR_CODE AS EWB_ERROR_CODE, "
							+ "HDR.EWB_ERROR_DESC AS EWB_ERROR_DESCRIPTION, "
							+ "case when HDR.IS_SAVED_TO_GSTN =  TRUE THEN 'IS_SAVED' WHEN HDR.IS_SAVED_TO_GSTN = FALSE AND "
							+ "HDR.GSTN_ERROR = TRUE THEN 'IS_ERROR' WHEN HDR.IS_SAVED_TO_GSTN = "
							+ "FALSE AND HDR.GSTN_ERROR = FALSE THEN 'NOT_SAVED' END AS "
							+ "GSTIN_STATUS, "
							+ "TO_CHAR(GSB.GSTN_SAVE_REF_ID) AS GSTIN_REF_ID, "
							+ "TO_CHAR(GSB.GSTN_RESP_DATE) AS GSTIN_REFID_DATE_TIME, "
							+ "TRIM(', ' FROM IFNULL(ERRH.ERROR_CODE_GSTIN,'') ||','|| "
							+ "IFNULL(ERRI.ERROR_CODE_GSTN,'')) AS GSTIN_ERROR_CODE, "
							+ "TRIM(', ' FROM IFNULL(ERRH.ERROR_DESCRIPTION_GSTIN,'') "
							+ "||','|| IFNULL(ERRI.ERROR_DESCRIPTION_GSTN,'')) AS "
							+ "GSTIN_ERROR_DESCRIPTION_ASP,"+ buildRetTypeTableNo
							+ " TO_CHAR(HDR.IRN), "
							+ "TO_CHAR( HDR.IRN_DATE), HDR.TAX_SCHEME, HDR.CANCEL_REASON, "
							+ "HDR.CANCEL_REMARKS, ITM.SUPPLY_TYPE,HDR.DOC_CATEGORY, "
							+ "HDR.DOC_TYPE,TO_CHAR(HDR.DOC_NUM),TO_CHAR(HDR.DOC_DATE), "
							+ "HDR.REVERSE_CHARGE, HDR.SUPPLIER_GSTIN,HDR.SUPP_TRADE_NAME, "
							+ "HDR.SUPP_LEGAL_NAME,HDR.SUPP_BUILDING_NUM, "
							+ "HDR.CUST_SUPP_ADDRESS2 AS SUPPLIERADDRESS2, HDR.SUPP_LOCATION, "
							+ "TO_CHAR(HDR.SUPP_PINCODE),TO_CHAR(HDR.SUPP_STATE_CODE),TO_CHAR(HDR.SUPP_PHONE), "
							+ "HDR.SUPP_EMAIL,HDR.CUST_GSTIN,HDR.CUST_TRADE_NAME, "
							+ "HDR.CUST_SUPP_NAME,HDR.CUST_SUPP_ADDRESS1,HDR.CUST_SUPP_ADDRESS2, "
							+ "HDR.CUST_SUPP_ADDRESS4,TO_CHAR(HDR.CUST_PINCODE), "
							+ "TO_CHAR(HDR.BILL_TO_STATE), TO_CHAR(HDR.POS), "
							+ "TO_CHAR(HDR.CUST_PHONE),TO_CHAR(HDR.CUST_EMAIL),HDR.DISPATCHER_GSTIN, "
							+ "HDR.DISPATCHER_TRADE_NAME,TO_CHAR(HDR.DISPATCHER_BUILDING_NUM), "
							+ "HDR.DISPATCHER_BUILDING_NAME,HDR.DISPATCHER_LOCATION, "
							+ "TO_CHAR(HDR.DISPATCHER_PINCODE),TO_CHAR(HDR.DISPATCHER_STATE_CODE), "
							+ "'' AS SHIP_TO_FLOOR_NUM, TO_CHAR(HDR.SHIP_TO_TRADE_NAME), "
							+ "HDR.SHIP_TO_LEGAL_NAME,HDR.SHIP_TO_BUILDING_NUM, "
							+ "HDR.SHIP_TO_BUILDING_NAME,HDR.SHIP_TO_LOCATION, "
							+ "TO_CHAR(ITM.SHIP_TO_PINCODE),TO_CHAR(ITM.SHIP_TO_STATE) "
							+ "AS SHIPTOSTATECODE, TO_CHAR(ITM.ITM_NO), "
							+ "TO_CHAR(ITM.SERIAL_NUM2),ITM.PRODUCT_NAME,ITM.ITM_DESCRIPTION, "
							+ "TO_CHAR(ITM.IS_SERVICE),TO_CHAR(ITM.ITM_HSNSAC), "
							+ "TO_CHAR(ITM.BAR_CODE), TO_CHAR(ITM.BATCH_NAME_OR_NUM), "
							+ "TO_CHAR(ITM.BATCH_EXPIRY_DATE),TO_CHAR(ITM.WARRANTY_DATE), "
							+ "TO_CHAR(ITM.ORDER_ITEM_REFERENCE), TO_CHAR(ITM.ATTRIBUTE_NAME), "
							+ "TO_CHAR(ITM.ATTRIBUTE_VALUE), TO_CHAR(ITM.ORIGIN_COUNTRY), "
							+ "TO_CHAR(ITM.ITM_UQC), TO_CHAR(ITM.ITM_QTY), "
							+ "TO_CHAR(ITM.FREE_QTY), TO_CHAR(ITM.UNIT_PRICE), "
							+ "TO_CHAR(ITM.ITEM_AMT_UP_QTY), TO_CHAR(ITM.ITEM_DISCOUNT), "
							+ "TO_CHAR(ITM.PRE_TAX_AMOUNT), TO_CHAR(ITM.TAXABLE_VALUE), "
							+ "TO_CHAR(ITM.IGST_RATE), TO_CHAR(ITM.IGST_AMT), "
							+ "TO_CHAR(ITM.CGST_RATE), TO_CHAR(ITM.CGST_AMT), "
							+ "TO_CHAR(ITM.SGST_RATE), TO_CHAR(ITM.SGST_AMT), "
							+ "TO_CHAR(ITM.CESS_RATE_ADVALOREM), TO_CHAR(ITM.CESS_AMT_ADVALOREM), "
							+ "TO_CHAR(ITM.CESS_RATE_SPECIFIC), TO_CHAR(ITM.CESS_AMT_SPECIFIC), "
							+ "TO_CHAR(ITM.STATECESS_RATE), TO_CHAR(ITM.STATECESS_AMT), "
							+ "TO_CHAR(ITM.STATE_CESS_SPECIFIC_RATE), "
							+ "TO_CHAR(ITM.STATE_CESS_SPECIFIC_AMOUNT),TO_CHAR(ITM.OTHER_VALUES), "
							+ "TO_CHAR(TOT_ITEM_AMT), TO_CHAR(ITM.INV_OTHER_CHARGES), "
							+ "TO_CHAR(ITM.INV_ASSESSABLE_AMT), TO_CHAR(ITM.INV_IGST_AMT), "
							+ "TO_CHAR(ITM.INV_CGST_AMT), TO_CHAR(ITM.INV_SGST_AMT), "
							+ "TO_CHAR(ITM.INV_CESS_ADVLRM_AMT), TO_CHAR(ITM. "
							+ "INV_CESS_SPECIFIC_AMT), TO_CHAR(ITM.INV_STATE_CESS_AMT), "
							+ "TO_CHAR(ITM.INV_STATE_CESS_SPECIFIC_AMOUNT), "
							+ "TO_CHAR(ITM.LINE_ITEM_AMT), TO_CHAR(HDR.ROUND_OFF), "
							+ "TO_CHAR(HDR.TOT_INV_VAL_WORLDS), TO_CHAR(HDR.TCS_FLAG_INCOME_TAX), "
							+ "TO_CHAR(ITM.TCS_RATE_INCOME_TAX), TO_CHAR(ITM.TCS_AMOUNT_INCOME_TAX), "
							+ "TO_CHAR(CUSTOMER_PAN_OR_AADHAAR), TO_CHAR( HDR.FOREIGN_CURRENCY), "
							+ "TO_CHAR(HDR.COUNTRY_CODE), TO_CHAR( HDR.INV_VAL_FC), "
							+ "TO_CHAR(HDR.SHIP_PORT_CODE), TO_CHAR(HDR.SHIP_BILL_NUM), "
							+ "TO_CHAR(HDR.SHIP_BILL_DATE), HDR.INV_REMARKS, "
							+ "TO_CHAR(HDR.INV_PERIOD_START_DATE), TO_CHAR(HDR. "
							+ "INV_PERIOD_END_DATE), TO_CHAR(ITM.PRECEEDING_INV_NUM), "
							+ "TO_CHAR(ITM.PRECEEDING_INV_DATE), TO_CHAR(ITM.INV_REFERENCE), "
							+ "ITM.RECEIPT_ADVICE_REFERENCE, TO_CHAR(ITM.RECEIPT_ADVICE_DATE), "
							+ "TO_CHAR(ITM.TENDER_REFERENCE) , TO_CHAR(ITM.CONTRACT_REFERENCE), "
							+ "TO_CHAR(ITM.EXTERNAL_REFERENCE), TO_CHAR(ITM.PROJECT_REFERENCE), "
							+ "TO_CHAR(ITM.CUST_PO_REF_NUM), TO_CHAR(ITM.CUST_PO_REF_DATE), "
							+ "HDR.PAYEE_NAME,HDR.MODE_OF_PAYMENT,TO_CHAR(HDR.BRANCH_IFSC_CODE), "
							+ "HDR.PAYMENT_TERMS,HDR.PAYMENT_INSTRUCTION,HDR.CR_TRANSFER, "
							+ "HDR.DB_DIRECT,HDR.CR_DAYS,TO_CHAR(ITM.PAID_AMT), "
							+ "TO_CHAR(ITM.BAL_AMT), TO_CHAR(HDR.PAYMENT_DUE_DATE), "
							+ "TO_CHAR(HDR.ACCOUNT_DETAIL),HDR.ECOM_GSTIN, "
							+ "TO_CHAR(HDR.ECOM_TRANS_ID), ITM.SUPPORTING_DOC_URL, "
							+ "ITM.SUPPORTING_DOC_BASE64, ITM.ADDITIONAL_INFORMATION, "
							+ "HDR.TRANS_TYPE,HDR.SUB_SUPP_TYPE, HDR.OTHER_SUPP_TYPE_DESC, "
							+ "TO_CHAR(HDR.TRANSPORTER_ID), HDR.TRANSPORTER_NAME, "
							+ "HDR.TRANSPORT_MODE,HDR.TRANSPORT_DOC_NUM,TO_CHAR( "
							+ "HDR.TRANSPORT_DOC_DATE), TO_CHAR(HDR.DISTANCE) , "
							+ "TO_CHAR(HDR.VEHICLE_NUM),HDR.VEHICLE_TYPE, "
							+ "TO_CHAR(HDR.RETURN_PERIOD),HDR.ORIGINAL_DOC_TYPE, "
							+ "HDR.ORIGINAL_CUST_GSTIN, TO_CHAR(HDR.DIFF_PERCENT), "
							+ "TO_CHAR(HDR.SECTION7_OF_IGST_FLAG), TO_CHAR(HDR.CLAIM_REFUND_FLAG), "
							+ "TO_CHAR(HDR.AUTOPOPULATE_TO_REFUND),TO_CHAR(HDR.CRDR_PRE_GST), "
							+ "TO_CHAR(HDR.CUST_SUPP_TYPE), "
							+ "HDR.CUST_SUPP_CODE AS CUSTOMERCODE, "
							+ "ITM.PRODUCT_CODE,ITM.ITM_TYPE, TO_CHAR(ITM.ITC_FLAG), "
							+ "TO_CHAR(HDR.STATE_APPLYING_CESS),TO_CHAR(ITM.FOB) , "
							+ "TO_CHAR(ITM.EXPORT_DUTY), TO_CHAR(HDR.EXCHANGE_RATE), "
							+ "TO_CHAR(ITM.CRDR_REASON), TO_CHAR(HDR.TCS_FLAG) , "
							+ "TO_CHAR(ITM.TCS_AMT),TO_CHAR(TCS_CGST_AMT),TO_CHAR(TCS_SGST_AMT), "
							+ "TO_CHAR( HDR.TDS_FLAG), TO_CHAR(TDS_IGST_AMT),TO_CHAR(TDS_CGST_AMT), "
							+ "TO_CHAR(TDS_SGST_AMT), TO_CHAR(HDR.USER_ID) AS userid, "
							+ "TO_CHAR(HDR.COMPANY_CODE),HDR.SOURCE_IDENTIFIER, HDR.SOURCE_FILENAME, "
							+ "ITM.PLANT_CODE,HDR.DIVISION, SUB_DIVISION,ITM.LOCATION, "
							+ "HDR.SALES_ORGANIZATION, HDR.DISTRIBUTION_CHANNEL, "
							+ "ITM.PROFIT_CENTRE,HDR.PROFIT_CENTRE2, HDR.USERACCESS1, "
							+ "HDR.USERACCESS2,HDR.USERACCESS3, HDR.USERACCESS4,HDR.USERACCESS5, "
							+ "HDR.USERACCESS6, TO_CHAR(ITM.GLCODE_TAXABLEVALUE), "
							+ "TO_CHAR(ITM.GLCODE_IGST), TO_CHAR(ITM.GLCODE_CGST), "
							+ "TO_CHAR( ITM.GLCODE_SGST), TO_CHAR(ITM.GLCODE_ADV_CESS), "
							+ "TO_CHAR(ITM.GLCODE_SP_CESS), TO_CHAR(ITM.GLCODE_STATE_CESS), "
							+ "TO_CHAR(HDR.GL_STATE_CESS_SPECIFIC), TO_CHAR(HDR.GL_POSTING_DATE), "
							+ "TO_CHAR(HDR.SALES_ORD_NUM),TO_CHAR(HDR.EWAY_BILL_NUM), "
							+ "TO_CHAR(CAST(HDR.EWAY_BILL_DATE AS DATE)), "
							+ "TO_CHAR(HDR.ACCOUNTING_VOUCHER_NUM), "
							+ "TO_CHAR(HDR.ACCOUNTING_VOUCHER_DATE), "
							+ "TO_CHAR(ITM.DOCUMENT_REFERENCE_NUMBER),TO_CHAR(HDR.CUST_TAN), "
							+ "ITM.USERDEFINED_FIELD1, ITM.USERDEFINED_FIELD2, "
							+ "ITM.USERDEFINED_FIELD3,ITM.USERDEFINED_FIELD4, "
							+ "ITM.USERDEFINED_FIELD5,ITM.USERDEFINED_FIELD6, "
							+ "ITM.USERDEFINED_FIELD7, ITM.USERDEFINED_FIELD8, "
							+ "ITM.USERDEFINED_FIELD9,ITM.USERDEFINED_FIELD10, "
							+ "ITM.USERDEFINED_FIELD11,ITM.USERDEFINED_FIELD12, "
							+ "ITM.USERDEFINED_FIELD13, ITM.USERDEFINED_FIELD14, "
							+ "ITM.USERDEFINED_FIELD15,ITM.USERDEFINED_FIELD16, "
							+ "ITM.USERDEFINED_FIELD17,ITM.USERDEFINED_FIELD18, "
							+ "ITM.USERDEFINED_FIELD19, ITM.USERDEFINED_FIELD20, "
							+ "ITM.USERDEFINED_FIELD21,ITM.USERDEFINED_FIELD22, "
							+ "ITM.USERDEFINED_FIELD23,ITM.USERDEFINED_FIELD24, "
							+ "ITM.USERDEFINED_FIELD25, ITM.USERDEFINED_FIELD26, "
							+ "ITM.USERDEFINED_FIELD27,ITM.USERDEFINED_FIELD28, "
							+ "ITM.USERDEFINED_FIELD29,ITM.USERDEFINED_FIELD30, "
							+ "HDR.SUPPLY_TYPE AS SUPPLYTYPEASP, '' AS ApproximateDistance_ASP, "
							+ "'' AS DistanceSavedtoEWB, HDR.CREATED_BY as USER_ID, "
							+ "HDR.FILE_ID, FIL.FILE_NAME as FILE_NAME, "
							+ "TO_CHAR(HDR.INV_OTHER_CHARGES) as InvoiceOtherChargesASP, "
							+ "TO_CHAR(HDR.INV_ASSESSABLE_AMT) as InvoiceAssessableAmountASP, "
							+ "TO_CHAR(HDR.INV_IGST_AMT) as InvoiceIGSTAmountASP, "
							+ "TO_CHAR(HDR.INV_CGST_AMT) as InvoiceCGSTAmountASP, "
							+ "TO_CHAR(HDR.INV_SGST_AMT) as InvoiceSGSTAmountASP, "
							+ "TO_CHAR(HDR.INV_CESS_ADVLRM_AMT) as InvoiceCessAdvaloremAmountASP, "
							+ "TO_CHAR(HDR.INV_CESS_SPECIFIC_AMT) as InvoiceCessSpecificAmountASP, "
							+ "TO_CHAR(HDR.INV_STATE_CESS_AMT) as InvoiceStateCessAdvaloremAmountASP, "
							+ "TO_CHAR(HDR.INV_STATE_CESS_SPECIFIC_AMOUNT) as "
							+ "InvoiceStateCessSpecificAmountASP,TO_CHAR(HDR.DOC_AMT), "
							+ "TO_CHAR(ITM.MEMO_VALUE_IGST), TO_CHAR(HDR.MEMO_VALUE_CGST), "
							+ "TO_CHAR(HDR.MEMO_VALUE_SGST), TO_CHAR(ITM.MEMO_VALUE_CESS_ADV) "
							+ "AS CessAdvaloremAmount_ASP, TO_CHAR(ITM.MEMO_VALUE_STATE_CESS_ADV) "
							+ "AS StateCessAdvaloremAmount_ASP, TO_CHAR(ITM.IGST_RET1_IMPACT), "
							+ "TO_CHAR(ITM.CGST_RET1_IMPACT), TO_CHAR(ITM.SGST_RET1_IMPACT), "
							+ "TO_CHAR(ITM.CESS_ADV_RET1_IMPACT) AS CessAdvaloremAmount_Difference, "
							+ "TO_CHAR(ITM.STATE_CESS_ADV_RET1_IMPACT) AS "
							+ "StateCessAdvaloremAmount_Difference, "
							+ "CASE WHEN HDR.IS_DELETE = TRUE THEN 'INACTIVE' WHEN "
							+ "HDR.IS_DELETE = FALSE THEN 'ACTIVE'END AS RECORD_STATUS, "
							+ "TO_CHAR(ITM.ADJ_REF_NO),TO_CHAR(ITM.ADJ_REF_DATE), "
							+ "TO_CHAR(ITM.ADJ_TAXABLE_VALUE), "
							+ "TO_CHAR(ITM.ADJ_IGST_AMT),TO_CHAR(ITM.ADJ_CGST_AMT), "
							+ "TO_CHAR(ITM.ADJ_SGST_AMT), "
							+ "TO_CHAR(ITM.ADJ_CESS_AMT_ADVALOREM), "
							+ "TO_CHAR(ITM.ADJ_CESS_AMT_SPECIFIC), "
							+ "TO_CHAR(ITM.ADJ_STATECESS_AMT), "
							+ "TO_CHAR(ITM.TCS_AMT) as TCSamount, "
							+ "TO_CHAR(HDR.CUST_SUPP_ADDRESS3), " +  buildTaxDocType
							+ " FROM ANX_OUTWARD_DOC_HEADER HDR "
							+ "LEFT OUTER JOIN(SELECT  DOC_HEADER_ID "
							+ "                      , ITEM_INDEX "
							+ "                      , ITM_NO "
							+ "                      , STRING_AGG(ERROR_CODE_GSTIN,',') ERROR_CODE_GSTIN "
							+ "                      , STRING_AGG(ERROR_DESCRIPTION_GSTIN,',') ERROR_DESCRIPTION_GSTIN "
							+ "                      , STRING_AGG(INFO_ERROR_CODE_GSTIN,',') INFO_ERROR_CODE_GSTIN "
							+ "                      , STRING_AGG(INFO_ERROR_DESCRIPTION_GSTIN,',') INFO_ERROR_DESCRIPTION_GSTIN "
							+ "                      , STRING_AGG(ERROR_CODE_ASP,',') ERROR_CODE_ASP "
							+ "                      , STRING_AGG(ERROR_DESCRIPTION_ASP,',') ERROR_DESCRIPTION_ASP "
							+ "                      , STRING_AGG(INFO_ERROR_CODE_ASP,',') INFO_ERROR_CODE_ASP "
							+ "                      , STRING_AGG(INFO_ERROR_DESCRIPTION_ASP,',') INFO_ERROR_DESCRIPTION_ASP "
							+ "                      ,SUPPLIER_GSTIN, FILE_ID, DERIVED_RET_PERIOD "
							+ "              "
							+ "             FROM "
							+ "             ( "
							+ "             SELECT DOC_HEADER_ID "
							+ "                    , ITEM_INDEX "
							+ "                    , ITM_NO "
							+ "                    , (CASE WHEN ERROR_TYPE='ERR'  THEN ERROR_CODE END) AS ERROR_CODE_GSTIN "
							+ "                    , (CASE WHEN ERROR_TYPE='ERR'  THEN ERROR_DESCRIPTION END) AS ERROR_DESCRIPTION_GSTIN "
							+ "                     , (CASE WHEN ERROR_TYPE='INFO' THEN ERROR_CODE END)  AS INFO_ERROR_CODE_GSTIN "
							+ "                    , (CASE WHEN ERROR_TYPE='INFO' THEN ERROR_DESCRIPTION END)AS INFO_ERROR_DESCRIPTION_GSTIN "
							+ "                    , ' ' AS ERROR_CODE_ASP,' ' AS ERROR_DESCRIPTION_ASP "
							+ "                    , ' ' AS INFO_ERROR_CODE_ASP, ' ' AS INFO_ERROR_DESCRIPTION_ASP "
							+ "                            ,SUPPLIER_GSTIN, FILE_ID, DERIVED_RET_PERIOD "
							+ "             FROM \"ANX_OUTWARD_DOC_ERROR\" "
							+ "             WHERE ITEM_INDEX IS NULL AND ERROR_SOURCE='GSTN' "
							+ "              "
							+ "             UNION ALL "
							+ "             SELECT DOC_HEADER_ID "
							+ "                    , ITEM_INDEX "
							+ "                    , ITM_NO "
							+ "                    ,' 'AS ERROR_CODE_GSTIN "
							+ "                    ,' ' AS ERROR_DESCRIPTION_GSTIN "
							+ "                    ,' ' AS INFO_ERROR_CODE_GSTIN "
							+ "                    ,' ' AS INFO_ERROR_DESCRIPTION_GSTIN "
							+ "                    , (CASE WHEN ERROR_TYPE='ERR' THEN ERROR_CODE END) AS ERROR_CODE_ASP "
							+ "                    , (CASE WHEN ERROR_TYPE='ERR' THEN ERROR_DESCRIPTION END) AS ERROR_DESCRIPTION_ASP "
							+ "                     , (CASE WHEN ERROR_TYPE='INFO' THEN ERROR_CODE END)  AS INFO_ERROR_CODE_ASP "
							+ "                    , (CASE WHEN ERROR_TYPE='INFO' THEN ERROR_DESCRIPTION END) AS INFO_ERROR_DESCRIPTION_ASP "
							+ "                             ,SUPPLIER_GSTIN, FILE_ID, DERIVED_RET_PERIOD "
							+ "                    "
							+ "              FROM \"ANX_OUTWARD_DOC_ERROR\" "
							+ "             WHERE ITEM_INDEX IS NULL AND ERROR_SOURCE='ASP' AND FILE_ID =:fileId) "
							+ "             GROUP BY DOC_HEADER_ID, ITEM_INDEX, ITM_NO      ,SUPPLIER_GSTIN, FILE_ID, DERIVED_RET_PERIOD "
							+ "             )ERRH "
							+ "             ON ERRH.FILE_ID = HDR.FILE_ID "
							+ "             AND ERRH.DOC_HEADER_ID = HDR.ID "
							+ "             AND ERRH.SUPPLIER_GSTIN = HDR.SUPPLIER_GSTIN "
							+ "             AND ERRH.DERIVED_RET_PERIOD = HDR.DERIVED_RET_PERIOD "
							+ "             AND HDR.FILE_ID=:fileId "
							+ "             INNER JOIN ANX_OUTWARD_DOC_ITEM ITM ON HDR.ID = ITM.DOC_HEADER_ID AND HDR.FILE_ID= :fileId "
							+ "             LEFT OUTER JOIN "
							+ "             (SELECT DOC_HEADER_ID "
							+ "                    , ITEM_INDEX "
							+ "                    , ITM_NO "
							+ "                    , CREATED_ON "
							+ "                    , STRING_AGG(ERROR_CODE_GSTN,',') ERROR_CODE_GSTN "
							+ "                    , STRING_AGG(ERROR_DESCRIPTION_GSTN,',') ERROR_DESCRIPTION_GSTN "
							+ "                    , STRING_AGG(INFO_ERROR_CODE_GSTN,',') INFO_ERROR_CODE_GSTN "
							+ "                    , STRING_AGG(INFO_ERROR_DESCRIPTION_GSTN,',') INFO_ERROR_DESCRIPTION_GSTN "
							+ "                    , STRING_AGG(ERROR_CODE_ASP,',') ERROR_CODE_ASP "
							+ "                    , STRING_AGG(ERROR_DESCRIPTION_ASP,',') ERROR_DESCRIPTION_ASP "
							+ "                    , STRING_AGG(INFO_ERROR_CODE_ASP,',') INFO_ERROR_CODE_ASP "
							+ "                    , STRING_AGG(INFO_ERROR_DESCRIPTION_ASP,',') INFO_ERROR_DESCRIPTION_ASP "
							+ "                   , SUPPLIER_GSTIN, FILE_ID, DERIVED_RET_PERIOD "
							+ "             FROM "
							+ "             ( "
							+ "             SELECT DOC_HEADER_ID "
							+ "                    , ITEM_INDEX "
							+ "                    , ITM_NO "
							+ "                    , CREATED_ON "
							+ "                    , (CASE WHEN ERROR_TYPE='ERR'  THEN ERROR_CODE END) AS ERROR_CODE_GSTN, "
							+ "                    (CASE WHEN ERROR_TYPE='ERR'  THEN ERROR_DESCRIPTION END) AS ERROR_DESCRIPTION_GSTN , "
							+ "                    (CASE WHEN ERROR_TYPE='INFO' THEN ERROR_CODE END)  AS INFO_ERROR_CODE_GSTN, "
							+ "                    (CASE WHEN ERROR_TYPE='INFO' THEN ERROR_DESCRIPTION END)AS INFO_ERROR_DESCRIPTION_GSTN, "
							+ "                    ' ' AS ERROR_CODE_ASP,' ' AS ERROR_DESCRIPTION_ASP, "
							+ "                    ' ' AS INFO_ERROR_CODE_ASP, ' ' AS INFO_ERROR_DESCRIPTION_ASP "
							+ "                            ,SUPPLIER_GSTIN, FILE_ID, DERIVED_RET_PERIOD "
							+ "             FROM \"ANX_OUTWARD_DOC_ERROR\" "
							+ "             WHERE ITEM_INDEX IS NOT NULL AND ERROR_SOURCE='GSTN' "
							+ "              "
							+ "             UNION ALL "
							+ "              "
							+ "             SELECT DOC_HEADER_ID "
							+ "                    , ITEM_INDEX "
							+ "                    , ITM_NO "
							+ "                    , CREATED_ON "
							+ "                    , ' 'AS ERROR_CODE_GSTN "
							+ "                    , ' ' AS ERROR_DESCRIPTION_GSTN "
							+ "                    , ' ' AS INFO_ERROR_CODE_GSTN "
							+ "                    , ' ' AS INFO_ERROR_DESCRIPTION_GSTN "
							+ "                    , (CASE WHEN ERROR_TYPE='ERR' THEN ERROR_CODE END) AS ERROR_CODE_ASP, "
							+ "                    (CASE WHEN ERROR_TYPE='ERR' THEN ERROR_DESCRIPTION END) AS ERROR_DESCRIPTION_ASP , "
							+ "                    (CASE WHEN ERROR_TYPE='INFO' THEN ERROR_CODE END)  AS INFO_ERROR_CODE_ASP, "
							+ "                    (CASE WHEN ERROR_TYPE='INFO' THEN ERROR_DESCRIPTION END) AS INFO_ERROR_DESCRIPTION_ASP "
							+ "                            ,SUPPLIER_GSTIN, FILE_ID, DERIVED_RET_PERIOD "
							+ "             FROM \"ANX_OUTWARD_DOC_ERROR\" "
							+ "             WHERE ITEM_INDEX IS NOT NULL AND ERROR_SOURCE='ASP' AND FILE_ID = :fileId "
							+ "             ) "
							+ "             GROUP BY DOC_HEADER_ID, ITEM_INDEX, ITM_NO,CREATED_ON        ,SUPPLIER_GSTIN, FILE_ID, DERIVED_RET_PERIOD "
							+ "             ) ERRI "
							+ "             ON  ERRI.FILE_ID = HDR.FILE_ID "
							+ "             AND ERRH.FILE_ID = ERRI.FILE_ID "
							+ "             AND ERRI.DERIVED_RET_PERIOD= HDR.DERIVED_RET_PERIOD "
							+ "             AND HDR.ID = ERRI.DOC_HEADER_ID "
							+ "             AND TO_CHAR(ITM.ITM_NO) = TO_CHAR(ERRI.ITM_NO) "
							+ " "
							+ "INNER JOIN FILE_STATUS FIL ON HDR.FILE_ID=FIL.ID "
							+ "LEFT OUTER JOIN GSTR1_GSTN_SAVE_BATCH GSB ON GSB.ID = HDR.BATCH_ID "
							+ "LEFT OUTER JOIN EINV_MASTER EINV ON EINV.DOC_HEADER_ID = HDR.ID AND ITM.DOC_HEADER_ID = EINV.DOC_HEADER_ID "
							+ "WHERE  " + buildQuery + " " + buildQuery1 
							+ " ORDER BY  HDR.DOC_NUM,ITM.ITM_NO "
							+ ") "
							+ "                                                                          "
							+ "UNION ALL "
							+ "                                                                                                                          "
							+ " (SELECT TRIM(', ' "
							+ "FROM IFNULL(ERRH.ERROR_CODE_ASP,'') ||','|| IFNULL( "
							+ "ERRI.ERROR_CODE_ASP,'')) AS ERROR_CODE_ASP, TRIM(', ' "
							+ "FROM IFNULL(ERRH.ERROR_DESCRIPTION_ASP,'') ||','|| "
							+ "IFNULL(ERRI.ERROR_DESCRIPTION_ASP,'')) AS ERROR_DESCRIPTION_ASP, "
							+ "TRIM(', ' FROM IFNULL(ERRH.INFO_ERROR_CODE_ASP,'') ||','|| "
							+ "IFNULL(ERRI.INFO_ERROR_CODE_ASP,'')) AS INFO_ERROR_CODE_ASP, "
							+ "TRIM(', ' FROM IFNULL(ERRH.INFO_ERROR_DESCRIPTION_ASP,'') "
							+ "||','|| IFNULL(ERRI.INFO_ERROR_DESCRIPTION_ASP,'')) AS "
							+ "INFO_ERROR_DESCRIPTION_ASP, EINV.STATUS_DESCRIPTION AS "
							+ "IRN_STATUS, EINV.IRN AS IRN_NO, EINV.ACK_NUM AS IRN_ACK_NO, "
							+ "HDR.IRN_DATE AS IRN_ACK_DATE_TIME, EINV.SIGNED_QR AS "
							+ "SIGNED_QR_CODE, EINV.SIGNED_INV AS SIGNED_INVOICE, "
							+ "HDR.EINV_ERROR_CODE AS IRP_ERROR_CODE, HDR.EINV_ERROR_DESC "
							+ "AS IRP_ERROR_DESCRIPTION, "
							+ "case when HDR.EWB_STATUS = 1 then 'Not Applicable' "
							+ "when HDR.EWB_STATUS = 2 then 'Pending' "
							+ "when HDR.EWB_STATUS = 3 then 'Generation Error' "
							+ "when HDR.EWB_STATUS = 4 then 'Part A Generated' "
							+ "when HDR.EWB_STATUS = 5 then 'EWB Active' "
							+ "when HDR.EWB_STATUS = 6 then 'Cancelled' "
							+ "when HDR.EWB_STATUS = 7 then 'Discarded' "
							+ "when HDR.EWB_STATUS = 8 then 'Rejected' "
							+ "when HDR.EWB_STATUS = 9 then 'Expired' end as EWBPartAStatusA, "
							+ "'' AS EWBValidUpTo, HDR.EWAY_BILL_NUM AS EWBNO, "
							+ "CAST(HDR.EWAY_BILL_DATE AS DATE) AS EWBDATE, "
							+ "HDR.EWB_ERROR_CODE AS EWB_ERROR_CODE, HDR.EWB_ERROR_DESC "
							+ "AS EWB_ERROR_DESCRIPTION, "
							+ "'' AS GSTIN_STATUS, "
							+ "GSB.GSTN_SAVE_REF_ID AS GSTIN_REF_ID, GSB.GSTN_RESP_DATE "
							+ "AS GSTIN_REFID_DATE_TIME, TRIM(', ' FROM IFNULL( "
							+ "ERRH.ERROR_CODE_GSTIN,'') ||','|| IFNULL( "
							+ "ERRI.ERROR_CODE_GSTN,'')) AS GSTIN_ERROR_CODE, TRIM(', ' "
							+ "FROM IFNULL(ERRH.ERROR_DESCRIPTION_GSTIN,'') ||','|| "
							+ "IFNULL(ERRI.ERROR_DESCRIPTION_GSTN,'')) AS "
							+ "GSTIN_ERROR_DESCRIPTION_ASP,HDR.AN_RETURN_TYPE AS RETURN_TYPE, "
							+ "HDR.AN_TABLE_SECTION AS TABLE_NUMBER,HDR.IRN, HDR.IRN_DATE,HDR.TAX_SCHEME, "
							+ "HDR.CANCEL_REASON, HDR.CANCEL_REMARKS, ITM.SUPPLY_TYPE, "
							+ "HDR.DOC_CATEGORY, HDR.DOC_TYPE,TO_CHAR(HDR.DOC_NUM), "
							+ "HDR.DOC_DATE,HDR.REVERSE_CHARGE, HDR.SUPPLIER_GSTIN, "
							+ "HDR.SUPP_TRADE_NAME, HDR.SUPP_LEGAL_NAME,HDR.SUPP_BUILDING_NUM, "
							+ "HDR.CUST_SUPP_ADDRESS2 AS SUPPLIERADDRESS2,HDR.SUPP_LOCATION, "
							+ "HDR.SUPP_PINCODE,HDR.SUPP_STATE_CODE,HDR.SUPP_PHONE, "
							+ "HDR.SUPP_EMAIL,HDR.CUST_GSTIN,HDR.CUST_TRADE_NAME, "
							+ "HDR.CUST_SUPP_NAME,HDR.CUST_SUPP_ADDRESS1,HDR.CUST_SUPP_ADDRESS2, "
							+ "HDR.CUST_SUPP_ADDRESS4,HDR.CUST_PINCODE,HDR.BILL_TO_STATE, "
							+ "HDR.POS,HDR.CUST_PHONE,HDR.CUST_EMAIL,HDR.DISPATCHER_GSTIN, "
							+ "HDR.DISPATCHER_TRADE_NAME,HDR.DISPATCHER_BUILDING_NUM, "
							+ "HDR.DISPATCHER_BUILDING_NAME,HDR.DISPATCHER_LOCATION, "
							+ "HDR.DISPATCHER_PINCODE,HDR.DISPATCHER_STATE_CODE, '' AS "
							+ "SHIP_TO_FLOOR_NUM,HDR.SHIP_TO_TRADE_NAME, "
							+ "HDR.SHIP_TO_LEGAL_NAME,HDR.SHIP_TO_BUILDING_NUM, "
							+ "HDR.SHIP_TO_BUILDING_NAME,HDR.SHIP_TO_LOCATION, "
							+ "ITM.SHIP_TO_PINCODE,ITM.SHIP_TO_STATE AS SHIPTOSTATECODE, "
							+ "ITM.ITM_NO, ITM.SERIAL_NUM2,ITM.PRODUCT_NAME,ITM.ITM_DESCRIPTION, "
							+ "ITM.IS_SERVICE,ITM.ITM_HSNSAC,ITM.BAR_CODE, ITM.BATCH_NAME_OR_NUM, "
							+ "ITM.BATCH_EXPIRY_DATE,ITM.WARRANTY_DATE, ITM.ORDER_ITEM_REFERENCE, "
							+ "ITM.ATTRIBUTE_NAME,ITM.ATTRIBUTE_VALUE, ITM.ORIGIN_COUNTRY, "
							+ "ITM.ITM_UQC,ITM.ITM_QTY,ITM.FREE_QTY, ITM.UNIT_PRICE, "
							+ "ITM.ITEM_AMT_UP_QTY, ITM.ITEM_DISCOUNT, ITM.PRE_TAX_AMOUNT, "
							+ "ITM.TAXABLE_VALUE,ITM.IGST_RATE, ITM.IGST_AMT,ITM.CGST_RATE, "
							+ "ITM.CGST_AMT,ITM.SGST_RATE, ITM.SGST_AMT, "
							+ "ITM.CESS_RATE_ADVALOREM,ITM.CESS_AMT_ADVALOREM, "
							+ "ITM.CESS_RATE_SPECIFIC, ITM.CESS_AMT_SPECIFIC, "
							+ "ITM.STATECESS_RATE,ITM.STATECESS_AMT, "
							+ "ITM.STATE_CESS_SPECIFIC_RATE, ITM.STATE_CESS_SPECIFIC_AMOUNT, "
							+ "ITM.OTHER_VALUES,TOT_ITEM_AMT, ITM.INV_OTHER_CHARGES, "
							+ "ITM.INV_ASSESSABLE_AMT,ITM.INV_IGST_AMT, ITM.INV_CGST_AMT, "
							+ "ITM.INV_SGST_AMT,ITM.INV_CESS_ADVLRM_AMT, "
							+ "ITM.INV_CESS_SPECIFIC_AMT,ITM.INV_STATE_CESS_AMT, "
							+ "ITM.INV_STATE_CESS_SPECIFIC_AMOUNT,ITM.LINE_ITEM_AMT, "
							+ "HDR.ROUND_OFF,HDR.TOT_INV_VAL_WORLDS,HDR.TCS_FLAG_INCOME_TAX, "
							+ "ITM.TCS_RATE_INCOME_TAX,ITM.TCS_AMOUNT_INCOME_TAX, "
							+ "CUSTOMER_PAN_OR_AADHAAR, HDR.FOREIGN_CURRENCY , "
							+ "HDR.COUNTRY_CODE, HDR.INV_VAL_FC, HDR.SHIP_PORT_CODE, "
							+ "HDR.SHIP_BILL_NUM,HDR.SHIP_BILL_DATE, HDR.INV_REMARKS, "
							+ "HDR.INV_PERIOD_START_DATE,HDR.INV_PERIOD_END_DATE, "
							+ "ITM.PRECEEDING_INV_NUM,ITM.PRECEEDING_INV_DATE, "
							+ "ITM.INV_REFERENCE, ITM.RECEIPT_ADVICE_REFERENCE, "
							+ "ITM.RECEIPT_ADVICE_DATE, ITM.TENDER_REFERENCE , "
							+ "ITM.CONTRACT_REFERENCE,ITM.EXTERNAL_REFERENCE, "
							+ "ITM.PROJECT_REFERENCE,ITM.CUST_PO_REF_NUM,ITM.CUST_PO_REF_DATE, "
							+ "HDR.PAYEE_NAME,HDR.MODE_OF_PAYMENT,HDR.BRANCH_IFSC_CODE, "
							+ "HDR.PAYMENT_TERMS,HDR.PAYMENT_INSTRUCTION,HDR.CR_TRANSFER, "
							+ "HDR.DB_DIRECT,HDR.CR_DAYS,ITM.PAID_AMT,ITM.BAL_AMT, "
							+ "HDR.PAYMENT_DUE_DATE,HDR.ACCOUNT_DETAIL,HDR.ECOM_GSTIN, "
							+ "HDR.ECOM_TRANS_ID,ITM.SUPPORTING_DOC_URL,ITM.SUPPORTING_DOC_BASE64, "
							+ "ITM.ADDITIONAL_INFORMATION,HDR.TRANS_TYPE,HDR.SUB_SUPP_TYPE, "
							+ "HDR.OTHER_SUPP_TYPE_DESC,HDR.TRANSPORTER_ID,HDR.TRANSPORTER_NAME, "
							+ "HDR.TRANSPORT_MODE,HDR.TRANSPORT_DOC_NUM,HDR.TRANSPORT_DOC_DATE, "
							+ "HDR.DISTANCE ,HDR.VEHICLE_NUM,HDR.VEHICLE_TYPE, "
							+ "HDR.RETURN_PERIOD,HDR.ORIGINAL_DOC_TYPE,HDR.ORIGINAL_CUST_GSTIN, "
							+ "HDR.DIFF_PERCENT,HDR.SECTION7_OF_IGST_FLAG,HDR.CLAIM_REFUND_FLAG, "
							+ "HDR.AUTOPOPULATE_TO_REFUND,HDR.CRDR_PRE_GST,HDR.CUST_SUPP_TYPE, "
							+ "HDR.CUST_SUPP_CODE AS CUSTOMERCODE,ITM.PRODUCT_CODE,ITM.ITM_TYPE, "
							+ "ITM.ITC_FLAG, HDR.STATE_APPLYING_CESS,ITM.FOB , ITM.EXPORT_DUTY, "
							+ "HDR.EXCHANGE_RATE,ITM.CRDR_REASON, HDR.TCS_FLAG , "
							+ "ITM.TCS_AMT,TCS_CGST_AMT,TCS_SGST_AMT, HDR.TDS_FLAG, "
							+ "TDS_IGST_AMT,TDS_CGST_AMT,TDS_SGST_AMT, HDR.USER_ID AS "
							+ "userid, HDR.COMPANY_CODE,HDR.SOURCE_IDENTIFIER, "
							+ "HDR.SOURCE_FILENAME, ITM.PLANT_CODE,HDR.DIVISION, "
							+ "SUB_DIVISION,ITM.LOCATION, HDR.SALES_ORGANIZATION, "
							+ "HDR.DISTRIBUTION_CHANNEL, ITM.PROFIT_CENTRE,HDR.PROFIT_CENTRE2, "
							+ "HDR.USERACCESS1, HDR.USERACCESS2,HDR.USERACCESS3, "
							+ "HDR.USERACCESS4,HDR.USERACCESS5, HDR.USERACCESS6, "
							+ "ITM.GLCODE_TAXABLEVALUE,ITM.GLCODE_IGST, ITM.GLCODE_CGST, "
							+ "ITM.GLCODE_SGST,ITM.GLCODE_ADV_CESS,ITM.GLCODE_SP_CESS, "
							+ "ITM.GLCODE_STATE_CESS, HDR.GL_STATE_CESS_SPECIFIC, "
							+ "HDR.GL_POSTING_DATE,HDR.SALES_ORD_NUM,HDR.EWAY_BILL_NUM, "
							+ "CAST(HDR.EWAY_BILL_DATE AS DATE), "
							+ "HDR.ACCOUNTING_VOUCHER_NUM, "
							+ "HDR.ACCOUNTING_VOUCHER_DATE, ITM.DOCUMENT_REFERENCE_NUMBER, "
							+ "HDR.CUST_TAN, ITM.USERDEFINED_FIELD1, ITM.USERDEFINED_FIELD2, "
							+ "ITM.USERDEFINED_FIELD3,ITM.USERDEFINED_FIELD4, "
							+ "ITM.USERDEFINED_FIELD5,ITM.USERDEFINED_FIELD6, "
							+ "ITM.USERDEFINED_FIELD7, ITM.USERDEFINED_FIELD8, "
							+ "ITM.USERDEFINED_FIELD9,ITM.USERDEFINED_FIELD10, "
							+ "ITM.USERDEFINED_FIELD11,ITM.USERDEFINED_FIELD12, "
							+ "ITM.USERDEFINED_FIELD13, ITM.USERDEFINED_FIELD14, "
							+ "ITM.USERDEFINED_FIELD15,ITM.USERDEFINED_FIELD16, "
							+ "ITM.USERDEFINED_FIELD17,ITM.USERDEFINED_FIELD18, "
							+ "ITM.USERDEFINED_FIELD19, ITM.USERDEFINED_FIELD20, "
							+ "ITM.USERDEFINED_FIELD21,ITM.USERDEFINED_FIELD22, "
							+ "ITM.USERDEFINED_FIELD23,ITM.USERDEFINED_FIELD24, "
							+ "ITM.USERDEFINED_FIELD25, ITM.USERDEFINED_FIELD26, "
							+ "ITM.USERDEFINED_FIELD27,ITM.USERDEFINED_FIELD28, "
							+ "ITM.USERDEFINED_FIELD29,ITM.USERDEFINED_FIELD30, "
							+ "HDR.SUPPLY_TYPE AS SUPPLYTYPEASP, '' AS ApproximateDistance_ASP, "
							+ "'' AS DistanceSavedtoEWB, HDR.CREATED_BY as USER_ID, "
							+ "HDR.FILE_ID, FIL.FILE_NAME as FILE_NAME, HDR.INV_OTHER_CHARGES "
							+ "as InvoiceOtherChargesASP, HDR.INV_ASSESSABLE_AMT as "
							+ "InvoiceAssessableAmountASP, HDR.INV_IGST_AMT as "
							+ "InvoiceIGSTAmountASP, HDR.INV_CGST_AMT as InvoiceCGSTAmountASP, "
							+ "HDR.INV_SGST_AMT as InvoiceSGSTAmountASP, HDR.INV_CESS_ADVLRM_AMT "
							+ "as InvoiceCessAdvaloremAmountASP, HDR.INV_CESS_SPECIFIC_AMT "
							+ "as InvoiceCessSpecificAmountASP, HDR.INV_STATE_CESS_AMT as "
							+ "InvoiceStateCessAdvaloremAmountASP, HDR.INV_STATE_CESS_SPECIFIC_AMOUNT "
							+ "as InvoiceStateCessSpecificAmountASP, HDR.DOC_AMT, "
							+ "ITM.MEMO_VALUE_IGST,HDR.MEMO_VALUE_CGST,HDR.MEMO_VALUE_SGST, "
							+ "ITM.MEMO_VALUE_CESS_ADV AS CessAdvaloremAmount_ASP, "
							+ "ITM.MEMO_VALUE_STATE_CESS_ADV AS StateCessAdvaloremAmount_ASP, "
							+ "ITM.IGST_RET1_IMPACT,ITM.CGST_RET1_IMPACT, ITM.SGST_RET1_IMPACT, "
							+ "ITM.CESS_ADV_RET1_IMPACT AS CessAdvaloremAmount_Difference, "
							+ "ITM.STATE_CESS_ADV_RET1_IMPACT AS StateCessAdvaloremAmount_Difference, "
							+ "'' AS RECORD_STATUS, "
							+ "ITM.ADJ_REF_NO,ITM.ADJ_REF_DATE, "
							+ "ITM.ADJ_TAXABLE_VALUE,ITM.ADJ_IGST_AMT,ITM.ADJ_CGST_AMT, "
							+ "ITM.ADJ_SGST_AMT,ITM. ADJ_CESS_AMT_ADVALOREM, "
							+ "ITM.ADJ_CESS_AMT_SPECIFIC,ITM.ADJ_STATECESS_AMT, "
							+ "ITM.TCS_AMT AS Tcsamount,HDR.CUST_SUPP_ADDRESS3, "
							+ "HDR.AN_TAX_DOC_TYPE AS DATA_CATEGORY "
							+ " "
							+ "FROM ANX_OUTWARD_ERR_HEADER HDR "
							+ "             LEFT OUTER JOIN( "
							+ "                           SELECT  DOC_HEADER_ID "
							+ "                                    , ITEM_INDEX "
							+ "                                    , ITM_NO "
							+ "                                    , STRING_AGG(ERROR_CODE_GSTIN,',') ERROR_CODE_GSTIN "
							+ "                                    , STRING_AGG(ERROR_DESCRIPTION_GSTIN,',') ERROR_DESCRIPTION_GSTIN "
							+ "                                    , STRING_AGG(INFO_ERROR_CODE_GSTIN,',') INFO_ERROR_CODE_GSTIN "
							+ "                                    , STRING_AGG(INFO_ERROR_DESCRIPTION_GSTIN,',') INFO_ERROR_DESCRIPTION_GSTIN "
							+ "                                    , STRING_AGG(ERROR_CODE_ASP,',') ERROR_CODE_ASP "
							+ "                                    , STRING_AGG(ERROR_DESCRIPTION_ASP,',') ERROR_DESCRIPTION_ASP "
							+ "                                    , STRING_AGG(INFO_ERROR_CODE_ASP,',') INFO_ERROR_CODE_ASP "
							+ "                                    , STRING_AGG(INFO_ERROR_DESCRIPTION_ASP,',') INFO_ERROR_DESCRIPTION_ASP "
							+ "                                    ,SUPPLIER_GSTIN, FILE_ID, DERIVED_RET_PERIOD "
							+ "                            "
							+ "                           FROM "
							+ "                           ( "
							+ "                           SELECT DOC_HEADER_ID "
							+ "                                  , ITEM_INDEX "
							+ "                                  , ITM_NO "
							+ "                                  ,' 'AS ERROR_CODE_GSTIN "
							+ "                                  ,' ' AS ERROR_DESCRIPTION_GSTIN "
							+ "                                  ,' ' AS INFO_ERROR_CODE_GSTIN "
							+ "                                  ,' ' AS INFO_ERROR_DESCRIPTION_GSTIN "
							+ "                                  , (CASE WHEN ERROR_TYPE='ERR' THEN ERROR_CODE END) AS ERROR_CODE_ASP "
							+ "                                  , (CASE WHEN ERROR_TYPE='ERR' THEN ERROR_DESCRIPTION END) AS ERROR_DESCRIPTION_ASP "
							+ "                                   , (CASE WHEN ERROR_TYPE='INFO' THEN ERROR_CODE END)  AS INFO_ERROR_CODE_ASP "
							+ "                                  , (CASE WHEN ERROR_TYPE='INFO' THEN ERROR_DESCRIPTION END) AS INFO_ERROR_DESCRIPTION_ASP "
							+ "                                           ,SUPPLIER_GSTIN, FILE_ID, DERIVED_RET_PERIOD "
							+ "                                  "
							+ "                            FROM \"ANX_OUTWARD_DOC_ERROR\" "
							+ "                           WHERE ITEM_INDEX IS NULL AND ERROR_SOURCE='ASP' AND FILE_ID =:fileId) "
							+ "                           GROUP BY DOC_HEADER_ID, ITEM_INDEX, ITM_NO      ,SUPPLIER_GSTIN, FILE_ID, DERIVED_RET_PERIOD "
							+ "                           )ERRH "
							+ "                           ON ERRH.FILE_ID = HDR.FILE_ID "
							+ "                           AND ERRH.DOC_HEADER_ID = HDR.ID "
							+ "                           AND ERRH.SUPPLIER_GSTIN = HDR.SUPPLIER_GSTIN "
							+ "                           AND ERRH.DERIVED_RET_PERIOD = HDR.DERIVED_RET_PERIOD "
							+ "                           AND HDR.FILE_ID=:fileId "
							+ "                           INNER JOIN ANX_OUTWARD_ERR_ITEM ITM ON HDR.ID = ITM.DOC_HEADER_ID AND HDR.FILE_ID= :fileId "
							+ "                           LEFT OUTER JOIN "
							+ "                           (SELECT DOC_HEADER_ID "
							+ "                                  , ITEM_INDEX "
							+ "                                  , ITM_NO "
							+ "                                  , CREATED_ON "
							+ "                                  , STRING_AGG(ERROR_CODE_GSTN,',') ERROR_CODE_GSTN "
							+ "                                  , STRING_AGG(ERROR_DESCRIPTION_GSTN,',') ERROR_DESCRIPTION_GSTN "
							+ "                                  , STRING_AGG(INFO_ERROR_CODE_GSTN,',') INFO_ERROR_CODE_GSTN "
							+ "                                  , STRING_AGG(INFO_ERROR_DESCRIPTION_GSTN,',') INFO_ERROR_DESCRIPTION_GSTN "
							+ "                                  , STRING_AGG(ERROR_CODE_ASP,',') ERROR_CODE_ASP "
							+ "                                  , STRING_AGG(ERROR_DESCRIPTION_ASP,',') ERROR_DESCRIPTION_ASP "
							+ "                                  , STRING_AGG(INFO_ERROR_CODE_ASP,',') INFO_ERROR_CODE_ASP "
							+ "                                  , STRING_AGG(INFO_ERROR_DESCRIPTION_ASP,',') INFO_ERROR_DESCRIPTION_ASP "
							+ "                                 , SUPPLIER_GSTIN, FILE_ID, DERIVED_RET_PERIOD "
							+ "                           FROM "
							+ "                           ( "
							+ "                            "
							+ "                            "
							+ "                           SELECT DOC_HEADER_ID "
							+ "                                  , ITEM_INDEX "
							+ "                                  , ITM_NO "
							+ "                                  , CREATED_ON "
							+ "                                  , ' 'AS ERROR_CODE_GSTN "
							+ "                                  , ' ' AS ERROR_DESCRIPTION_GSTN "
							+ "                                  , ' ' AS INFO_ERROR_CODE_GSTN "
							+ "                                  , ' ' AS INFO_ERROR_DESCRIPTION_GSTN "
							+ "                                  , (CASE WHEN ERROR_TYPE='ERR' THEN ERROR_CODE END) AS ERROR_CODE_ASP, "
							+ "                                  (CASE WHEN ERROR_TYPE='ERR' THEN ERROR_DESCRIPTION END) AS ERROR_DESCRIPTION_ASP , "
							+ "                                  (CASE WHEN ERROR_TYPE='INFO' THEN ERROR_CODE END)  AS INFO_ERROR_CODE_ASP, "
							+ "                                  (CASE WHEN ERROR_TYPE='INFO' THEN ERROR_DESCRIPTION END) AS INFO_ERROR_DESCRIPTION_ASP "
							+ "                                          ,SUPPLIER_GSTIN, FILE_ID, DERIVED_RET_PERIOD "
							+ "                           FROM ANX_OUTWARD_DOC_ERROR "
							+ "                           WHERE ITEM_INDEX IS NOT NULL AND ERROR_SOURCE='ASP' AND FILE_ID = :fileId "
							+ "                           ) "
							+ "                           GROUP BY DOC_HEADER_ID, ITEM_INDEX, ITM_NO,CREATED_ON        ,SUPPLIER_GSTIN, FILE_ID, DERIVED_RET_PERIOD "
							+ "                           ) ERRI "
							+ "                           ON  ERRI.FILE_ID = HDR.FILE_ID "
							+ "                           AND ERRH.FILE_ID = ERRI.FILE_ID "
							+ "                           AND ERRI.DERIVED_RET_PERIOD= HDR.DERIVED_RET_PERIOD "
							+ "                           AND HDR.ID = ERRI.DOC_HEADER_ID "
							+ " "
							+ "INNER JOIN FILE_STATUS FIL ON HDR.FILE_ID = FIL.ID "
							+ "LEFT OUTER JOIN GSTR1_GSTN_SAVE_BATCH GSB ON GSB.ID = HDR.BATCH_ID "
							+ "LEFT OUTER JOIN EINV_MASTER EINV ON EINV.DOC_HEADER_ID = HDR.ID "
							+ "AND ITM.DOC_HEADER_ID = EINV.DOC_HEADER_ID where "+ buildQuery
							+ " ORDER BY HDR.DOC_NUM,ITM.ITM_NO "
							+ " "
							+ ");";
		}
		return queryStr;
	}
}