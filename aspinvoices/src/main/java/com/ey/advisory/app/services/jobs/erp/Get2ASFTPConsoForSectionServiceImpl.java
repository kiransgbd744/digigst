package com.ey.advisory.app.services.jobs.erp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.views.client.ConsolidatedGstr2ADto;
import com.ey.advisory.app.downloadreports.DownloadReportsConstant;
import com.ey.advisory.app.sftp.service.SFTPFileTransferService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;

import lombok.extern.slf4j.Slf4j;

@Service("Get2ASFTPConsoForSectionServiceImpl")
@Slf4j
public class Get2ASFTPConsoForSectionServiceImpl
		implements Get2ASFTPConsoForSectionService {
	@Autowired
	private CommonUtility commonUtility;

	@Autowired
	@Qualifier("SFTPFileTransferServiceImpl")
	private SFTPFileTransferService sftpService;

	private static int CSV_BUFFER_SIZE = 8192;

	private static final String GSTR2A_SFTP_RESPONSE_DESTINATION = "ey.internal.get.gstr2a.sftp.response.destination";

	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	private static final String B2B = "B2B";
	private static final String B2BA = "B2BA";
	private static final String CDN = "CDN";
	private static final String CDNA = "CDNA";
	private static final String ISD = "ISD";
	private static final String IMPG = "IMPG";
	private static final String IMPGSEZ = "IMPGSEZ";
	private static final String ECOM = "ECOM";
	private static final String ECOMA = "ECOMA";


	@Override
	public List<ConsolidatedGstr2ADto> getConsolidatedItemList(
			String sectionName, List<Object[]> finalDtoList) {
		List<ConsolidatedGstr2ADto> resp = new ArrayList<>();

		if (B2B.equalsIgnoreCase(sectionName) && finalDtoList != null
				&& !finalDtoList.isEmpty()) {
			resp = finalDtoList.stream().map(o -> convertB2bSection(o))
					.collect(Collectors.toCollection(ArrayList::new));
		} else if (B2BA.equalsIgnoreCase(sectionName) && finalDtoList != null
				&& !finalDtoList.isEmpty()) {
			resp = finalDtoList.stream().map(o -> convertB2baSection(o))
					.collect(Collectors.toCollection(ArrayList::new));
		}else if (CDN.equalsIgnoreCase(sectionName) && finalDtoList != null
				&& !finalDtoList.isEmpty()) {
			resp = finalDtoList.stream().map(o -> convertCdnSection(o))
					.collect(Collectors.toCollection(ArrayList::new));
		} else if (CDNA.equalsIgnoreCase(sectionName) && finalDtoList != null
				&& !finalDtoList.isEmpty()) {
			resp = finalDtoList.stream().map(o -> convertCdnaSection(o))
					.collect(Collectors.toCollection(ArrayList::new));
		} else if (ISD.equalsIgnoreCase(sectionName) && finalDtoList != null
				&& !finalDtoList.isEmpty()) {
			resp = finalDtoList.stream().map(o -> convertIsdSection(o))
					.collect(Collectors.toCollection(ArrayList::new));
		} else if (IMPG.equalsIgnoreCase(sectionName) && finalDtoList != null
				&& !finalDtoList.isEmpty()) {
			resp = finalDtoList.stream().map(o -> convertImpgSection(o))
					.collect(Collectors.toCollection(ArrayList::new));
		} else if (IMPGSEZ.equalsIgnoreCase(sectionName) && finalDtoList != null
				&& !finalDtoList.isEmpty()) {
			resp = finalDtoList.stream().map(o -> convertImpgsezSection(o))
					.collect(Collectors.toCollection(ArrayList::new));
		} else if (ECOM.equalsIgnoreCase(sectionName) && finalDtoList != null
				&& !finalDtoList.isEmpty()) {
			resp = finalDtoList.stream().map(o -> convertEcomSection(o))
					.collect(Collectors.toCollection(ArrayList::new));
		} else if (ECOMA.equalsIgnoreCase(sectionName) && finalDtoList != null
				&& !finalDtoList.isEmpty()) {
			resp = finalDtoList.stream().map(o -> convertEcomaSection(o))
					.collect(Collectors.toCollection(ArrayList::new));
		}
	return resp;
}

	private ConsolidatedGstr2ADto convertB2bSection(Object[] arr) {

		ConsolidatedGstr2ADto obj = new ConsolidatedGstr2ADto();

		obj.setReturnPeriod(arr[0] != null ? "'"+arr[0].toString() : null);
		obj.setRecipientGSTIN(arr[1] != null ? arr[1].toString() : null);
		obj.setSupplierGSTIN(arr[2] != null ? arr[2].toString() : null);
		obj.setSupplierName(arr[3] != null ? arr[3].toString() : null);
		obj.setLegalName(arr[4] != null ? arr[4].toString() : null);
		obj.setDocumentType(arr[5] != null ? arr[5].toString() : null);
		obj.setSupplyType(arr[6] != null ? arr[6].toString() : null);
		obj.setDocumentNumber(arr[8] != null ? DownloadReportsConstant.CSVCHARACTER.concat(arr[8].toString()) : null);
		obj.setDocumentDate(arr[9] != null ? arr[9].toString() : null);
		BigDecimal bigDecimalTaxVal = (BigDecimal) arr[10];
		if (bigDecimalTaxVal != null) {
		//	BigDecimal aspTax = new BigDecimal(bigDecimalTaxVal.longValue());
			obj.setTaxableValue(bigDecimalTaxVal);
		}
		// obj.setTaxableValue(arr[10] != null ? arr[10].toString() : null);
		obj.setTaxRate(arr[11] != null ? arr[11].toString() : null);
		// obj.setIgstAmt(arr[12] != null ? arr[12].toString() : null);
		BigDecimal bigDecimalIGST = (BigDecimal) arr[12];
		if (bigDecimalIGST != null) {
			//BigDecimal aspIGST = new BigDecimal(bigDecimalIGST.longValue());
			obj.setIgstAmt(bigDecimalIGST);
		}
		BigDecimal bigDecimalCGST = (BigDecimal) arr[13];
		if (bigDecimalCGST != null) {
			//BigDecimal aspCGST = new BigDecimal(bigDecimalCGST.longValue());
			obj.setCgstAmt(bigDecimalCGST);
		}
		BigDecimal bigDecimalSGST = (BigDecimal) arr[14];
		if (bigDecimalSGST != null) {
		//	BigDecimal aspSGST = new BigDecimal(bigDecimalSGST.longValue());
			obj.setSgstAmt(bigDecimalSGST);
		}
		BigDecimal bigDecimalCESS = (BigDecimal) arr[15];
		if (bigDecimalCESS != null) {
			//BigDecimal aspCESS = new BigDecimal(bigDecimalCESS.longValue());
			obj.setCessAmt(bigDecimalCESS);
		}
		BigDecimal bigDecimalTOT = (BigDecimal) arr[16];
		if (bigDecimalTOT != null) {
			//BigDecimal aspTOT = new BigDecimal(bigDecimalTOT.longValue());
			obj.setTotalTaxAmt(bigDecimalTOT);
		}
		BigDecimal bigDecimalINV = (BigDecimal) arr[17];
		if (bigDecimalINV != null) {
			//BigDecimal inv = new BigDecimal(bigDecimalINV.longValue());
			obj.setInvoiceValue(bigDecimalINV);
		}
		/*
		 * obj.setCgstAmt(arr[13] != null ? arr[13].toString() : null);
		 * obj.setSgstAmt(arr[14] != null ? arr[14].toString() : null);
		 * obj.setCessAmt(arr[15] != null ? arr[15].toString() : null);
		 * obj.setTotalTaxAmt(arr[16] != null ? arr[16].toString() : null);
		 * obj.setInvoiceValue(arr[17] != null ? arr[17].toString() : null);
		 */
		obj.setPos(arr[18] != null ? arr[18].toString() : null);
		obj.setStateName(arr[19] != null ? arr[19].toString() : null);
		obj.setPortCode(arr[20] != null ? arr[20].toString() : null);
		obj.setBillofentryNumber(arr[21] != null ? arr[21].toString() : null);
		obj.setBillofentryDate(arr[22] != null ? arr[22].toString() : null);
		obj.setBillOfEntryRefDate(arr[23] != null ? arr[23].toString() : null);
		obj.setbOEAmendmentFlag(arr[24] != null ? arr[24].toString() : null);
		obj.setOriginalSupplierGSTIN(
				arr[25] != null ? arr[25].toString() : null);
		obj.setOriginalSupplierTradeName(
				arr[26] != null ? arr[26].toString() : null);
		obj.setOriginalPortCode(arr[27] != null ? arr[27].toString() : null);
		obj.setOriginalBillOfEntryNumber(
				arr[28] != null ? arr[28].toString() : null);
		obj.setOriginalBillOfEntryDate(
				arr[29] != null ? arr[29].toString() : null);
		obj.setOriginalBillOfEntryRefDate(
				arr[30] != null ? arr[30].toString() : null);
		/*
		 * BigDecimal bigDecimalORGTAX = (BigDecimal) arr[31]; if
		 * (bigDecimalORGTAX != null) { BigDecimal Orgtax = new
		 * BigDecimal(bigDecimalORGTAX.longValue());
		 * obj.setOriginalTaxableValue(Orgtax); } BigDecimal bigDecimalORGIGST =
		 * (BigDecimal) arr[32]; if (bigDecimalORGIGST != null) { BigDecimal
		 * Orgigst = new BigDecimal(bigDecimalORGIGST.longValue());
		 * obj.setOriginalIGSTAmount(Orgigst); } BigDecimal bigDecimalORGCESS =
		 * (BigDecimal) arr[33]; if (bigDecimalORGCESS != null) { BigDecimal
		 * Orgcess = new BigDecimal(bigDecimalORGCESS.longValue());
		 * obj.setOriginalCessAmount(Orgcess); }
		 */
		obj.setOriginalTaxableValue(
				arr[31] != null ? arr[31].toString() : null);
		obj.setOriginalIGSTAmount(arr[32] != null ? arr[32].toString() : null);
		obj.setOriginalCessAmount(arr[33] != null ? arr[33].toString() : null);
		obj.setOriginalDocumentNumber(
				arr[34] != null ? arr[34].toString() : null);
		obj.setOriginalDocumentDate(
				arr[35] != null ? arr[35].toString() : null);
		obj.setInvoiceNumber(arr[36] != null ? arr[36].toString() : null);
		obj.setInvoiceDate(arr[37] != null ? arr[37].toString() : null);
		obj.setOriginalInvAmendmentPeriod(
				arr[38] != null ? arr[38].toString() : null);
		obj.setOriginalAmendmentType(
				arr[39] != null ? arr[39].toString() : null);
		obj.setReverseChargeFlag(arr[40] != null ? arr[40].toString() : null);
		obj.setgSTR1FilingStatus(arr[41] != null ? arr[41].toString() : null);
		obj.setgSTR1FilingDate(arr[42] != null ? arr[42].toString() : null);
		obj.setgSTR1FilingPeriod(arr[43] != null ? arr[43].toString() : null);
		obj.setgSTR3BFilingStatus(arr[44] != null ? arr[44].toString() : null);
		obj.setCancellationDate(arr[45] != null ? arr[45].toString() : null);
		obj.setcDNDelinkingFlag(arr[46] != null ? arr[46].toString() : null);
		obj.setCrdrpreGst(arr[47] != null ? arr[47].toString() : null);
		obj.setItcEligible(arr[48] != null ? arr[48].toString() : null);
		obj.setDifferentialPercentage(
				arr[49] != null ? arr[49].toString() : null);
		obj.setLineNumber(arr[50] != null ? arr[50].toString() : null);
		obj.setEcomGstin(arr[51] != null ? arr[51].toString() : null);
		obj.setMerchantID(arr[52] != null ? arr[52].toString() : null);
		obj.setInitiatedDate(arr[53] != null ? arr[53].toString() : null);
		obj.setInitiatedTime(arr[54] != null ? arr[54].toString() : null);
		/*
		 * if (arr[54] == null || arr[54] == "null") { obj.setInitiatedTime("");
		 * } else { Timestamp date = (Timestamp) arr[54]; LocalDateTime dt =
		 * date.toLocalDateTime(); LocalDateTime dateTimeFormatter = EYDateUtil
		 * .toISTDateTimeFromUTC(dt); DateTimeFormatter FOMATTER =
		 * DateTimeFormatter .ofPattern("dd-MM-yyyy HH:mm:ss"); String newdate =
		 * FOMATTER.format(dateTimeFormatter);
		 * 
		 * obj.setInitiatedTime(newdate); }
		 */
		obj.setIrnNum(arr[56] != null ? arr[56].toString() : null);
		obj.setIrnGenDate(arr[57] != null ? arr[57].toString() : null);
		obj.setIrnSourceType(arr[58] != null ? arr[58].toString() : null);

		return obj;
	}

	private ConsolidatedGstr2ADto convertCdnSection(Object[] arr) {
		ConsolidatedGstr2ADto obj = new ConsolidatedGstr2ADto();

		obj.setReturnPeriod(arr[0] != null ? arr[0].toString() : null);
		obj.setRecipientGSTIN(arr[1] != null ? arr[1].toString() : null);
		obj.setSupplierGSTIN(arr[2] != null ? arr[2].toString() : null);
		obj.setSupplierName(arr[3] != null ? arr[3].toString() : null);
		obj.setLegalName(arr[4] != null ? arr[4].toString() : null);
		obj.setDocumentType(arr[5] != null ? arr[5].toString() : null);
		obj.setSupplyType(arr[6] != null ? arr[6].toString() : null);
		obj.setDocumentNumber(arr[8] != null ? DownloadReportsConstant.CSVCHARACTER.concat(arr[8].toString()) : null);
		obj.setDocumentDate(arr[9] != null ? arr[9].toString() : null);
		BigDecimal bigDecimalTaxVal = (BigDecimal) arr[10];
		if (bigDecimalTaxVal != null) {
		//	BigDecimal aspTax = new BigDecimal(bigDecimalTaxVal.longValue());
			obj.setTaxableValue(bigDecimalTaxVal);
		}
		// obj.setTaxableValue(arr[10] != null ? arr[10].toString() : null);
		obj.setTaxRate(arr[11] != null ? arr[11].toString() : null);
		// obj.setIgstAmt(arr[12] != null ? arr[12].toString() : null);
		BigDecimal bigDecimalIGST = (BigDecimal) arr[12];
		if (bigDecimalIGST != null) {
			//BigDecimal aspIGST = new BigDecimal(bigDecimalIGST.longValue());
			obj.setIgstAmt(bigDecimalIGST);
		}
		BigDecimal bigDecimalCGST = (BigDecimal) arr[13];
		if (bigDecimalCGST != null) {
		//	BigDecimal aspCGST = new BigDecimal(bigDecimalCGST.longValue());
			obj.setCgstAmt(bigDecimalCGST);
		}
		BigDecimal bigDecimalSGST = (BigDecimal) arr[14];
		if (bigDecimalSGST != null) {
			//BigDecimal aspSGST = new BigDecimal(bigDecimalSGST.longValue());
			obj.setSgstAmt(bigDecimalSGST);
		}
		BigDecimal bigDecimalCESS = (BigDecimal) arr[15];
		if (bigDecimalCESS != null) {
		//	BigDecimal aspCESS = new BigDecimal(bigDecimalCESS.longValue());
			obj.setCessAmt(bigDecimalCESS);
		}
		BigDecimal bigDecimalTOT = (BigDecimal) arr[16];
		if (bigDecimalTOT != null) {
		//	BigDecimal aspTOT = new BigDecimal(bigDecimalTOT.longValue());
			obj.setTotalTaxAmt(bigDecimalTOT);
		}
		BigDecimal bigDecimalINV = (BigDecimal) arr[17];
		if (bigDecimalINV != null) {
		//	BigDecimal inv = new BigDecimal(bigDecimalINV.longValue());
			obj.setInvoiceValue(bigDecimalINV);
		}
		/*
		 * obj.setCgstAmt(arr[13] != null ? arr[13].toString() : null);
		 * obj.setSgstAmt(arr[14] != null ? arr[14].toString() : null);
		 * obj.setCessAmt(arr[15] != null ? arr[15].toString() : null);
		 * obj.setTotalTaxAmt(arr[16] != null ? arr[16].toString() : null);
		 * obj.setInvoiceValue(arr[17] != null ? arr[17].toString() : null);
		 */
		obj.setPos(arr[18] != null ? arr[18].toString() : null);
		obj.setStateName(arr[19] != null ? arr[19].toString() : null);
		obj.setPortCode(arr[20] != null ? arr[20].toString() : null);
		obj.setBillofentryNumber(arr[21] != null ? arr[21].toString() : null);
		obj.setBillofentryDate(arr[22] != null ? arr[22].toString() : null);
		obj.setBillOfEntryRefDate(arr[23] != null ? arr[23].toString() : null);
		obj.setbOEAmendmentFlag(arr[24] != null ? arr[24].toString() : null);
		obj.setOriginalSupplierGSTIN(
				arr[25] != null ? arr[25].toString() : null);
		obj.setOriginalSupplierTradeName(
				arr[26] != null ? arr[26].toString() : null);
		obj.setOriginalPortCode(arr[27] != null ? arr[27].toString() : null);
		obj.setOriginalBillOfEntryNumber(
				arr[28] != null ? arr[28].toString() : null);
		obj.setOriginalBillOfEntryDate(
				arr[29] != null ? arr[29].toString() : null);
		obj.setOriginalBillOfEntryRefDate(
				arr[30] != null ? arr[30].toString() : null);
		/*
		 * BigDecimal bigDecimalORGTAX = (BigDecimal) arr[31]; if
		 * (bigDecimalORGTAX != null) { BigDecimal Orgtax = new
		 * BigDecimal(bigDecimalORGTAX.longValue());
		 * obj.setOriginalTaxableValue(Orgtax); } BigDecimal bigDecimalORGIGST =
		 * (BigDecimal) arr[32]; if (bigDecimalORGIGST != null) { BigDecimal
		 * Orgigst = new BigDecimal(bigDecimalORGIGST.longValue());
		 * obj.setOriginalIGSTAmount(Orgigst); } BigDecimal bigDecimalORGCESS =
		 * (BigDecimal) arr[33]; if (bigDecimalORGCESS != null) { BigDecimal
		 * Orgcess = new BigDecimal(bigDecimalORGCESS.longValue());
		 * obj.setOriginalCessAmount(Orgcess); }
		 */
		obj.setOriginalTaxableValue(
				arr[31] != null ? arr[31].toString() : null);
		obj.setOriginalIGSTAmount(arr[32] != null ? arr[32].toString() : null);
		obj.setOriginalCessAmount(arr[33] != null ? arr[33].toString() : null);
		obj.setOriginalDocumentNumber(
				arr[34] != null ? arr[34].toString() : null);
		obj.setOriginalDocumentDate(
				arr[35] != null ? arr[35].toString() : null);
		obj.setInvoiceNumber(arr[36] != null ? arr[36].toString() : null);
		obj.setInvoiceDate(arr[37] != null ? arr[37].toString() : null);
		obj.setOriginalInvAmendmentPeriod(
				arr[38] != null ? arr[38].toString() : null);
		obj.setOriginalAmendmentType(
				arr[39] != null ? arr[39].toString() : null);
		obj.setReverseChargeFlag(arr[40] != null ? arr[40].toString() : null);
		obj.setgSTR1FilingStatus(arr[41] != null ? arr[41].toString() : null);
		obj.setgSTR1FilingDate(arr[42] != null ? arr[42].toString() : null);
		obj.setgSTR1FilingPeriod(arr[43] != null ? arr[43].toString() : null);
		obj.setgSTR3BFilingStatus(arr[44] != null ? arr[44].toString() : null);
		obj.setCancellationDate(arr[45] != null ? arr[45].toString() : null);
		obj.setcDNDelinkingFlag(arr[46] != null ? arr[46].toString() : null);
		obj.setCrdrpreGst(arr[47] != null ? arr[47].toString() : null);
		obj.setItcEligible(arr[48] != null ? arr[48].toString() : null);
		obj.setDifferentialPercentage(
				arr[49] != null ? arr[49].toString() : null);
		obj.setLineNumber(arr[50] != null ? arr[50].toString() : null);
		obj.setEcomGstin(arr[51] != null ? arr[51].toString() : null);
		obj.setMerchantID(arr[52] != null ? arr[52].toString() : null);
		obj.setInitiatedDate(arr[53] != null ? arr[53].toString() : null);
		obj.setInitiatedTime(arr[54] != null ? arr[54].toString() : null);
		/*
		 * if (arr[54] == null || arr[54] == "null") { obj.setInitiatedTime("");
		 * } else { Timestamp date = (Timestamp) arr[54]; LocalDateTime dt =
		 * date.toLocalDateTime(); LocalDateTime dateTimeFormatter = EYDateUtil
		 * .toISTDateTimeFromUTC(dt); DateTimeFormatter FOMATTER =
		 * DateTimeFormatter .ofPattern("dd-MM-yyyy HH:mm:ss"); String newdate =
		 * FOMATTER.format(dateTimeFormatter);
		 * 
		 * obj.setInitiatedTime(newdate); }
		 */
		obj.setIrnNum(arr[56] != null ? arr[56].toString() : null);
		obj.setIrnGenDate(arr[57] != null ? arr[57].toString() : null);
		obj.setIrnSourceType(arr[58] != null ? arr[58].toString() : null);

		return obj;
	}

	private ConsolidatedGstr2ADto convertCdnaSection(Object[] arr) {
		ConsolidatedGstr2ADto obj = new ConsolidatedGstr2ADto();

		obj.setReturnPeriod(arr[0] != null ? arr[0].toString() : null);
		obj.setRecipientGSTIN(arr[1] != null ? arr[1].toString() : null);
		obj.setSupplierGSTIN(arr[2] != null ? arr[2].toString() : null);
		obj.setSupplierName(arr[3] != null ? arr[3].toString() : null);
		obj.setLegalName(arr[4] != null ? arr[4].toString() : null);
		obj.setDocumentType(arr[5] != null ? arr[5].toString() : null);
		obj.setSupplyType(arr[6] != null ? arr[6].toString() : null);
		obj.setDocumentNumber(arr[8] != null ? DownloadReportsConstant.CSVCHARACTER.concat(arr[8].toString()) : null);
		obj.setDocumentDate(arr[9] != null ? arr[9].toString() : null);
		BigDecimal bigDecimalTaxVal = (BigDecimal) arr[10];
		if (bigDecimalTaxVal != null) {
			//BigDecimal aspTax = new BigDecimal(bigDecimalTaxVal.longValue());
			obj.setTaxableValue(bigDecimalTaxVal);
		}
		//obj.setTaxableValue(arr[10] != null ? arr[10].toString() : null);
		obj.setTaxRate(arr[11] != null ? arr[11].toString() : null);
		//obj.setIgstAmt(arr[12] != null ? arr[12].toString() : null);
		BigDecimal bigDecimalIGST = (BigDecimal) arr[12];
		if (bigDecimalIGST != null) {
			//BigDecimal aspIGST = new BigDecimal(bigDecimalIGST.longValue());
			obj.setIgstAmt(bigDecimalIGST);
		}
		BigDecimal bigDecimalCGST = (BigDecimal) arr[13];
		if (bigDecimalCGST != null) {
			//BigDecimal aspCGST = new BigDecimal(bigDecimalCGST.longValue());
			obj.setCgstAmt(bigDecimalCGST);
		}
		BigDecimal bigDecimalSGST = (BigDecimal) arr[14];
		if (bigDecimalSGST != null) {
			//BigDecimal aspSGST = new BigDecimal(bigDecimalSGST.longValue());
			obj.setSgstAmt(bigDecimalSGST);
		}
		BigDecimal bigDecimalCESS = (BigDecimal) arr[15];
		if (bigDecimalCESS != null) {
			//BigDecimal aspCESS = new BigDecimal(bigDecimalCESS.longValue());
			obj.setCessAmt(bigDecimalCESS);
		}
		BigDecimal bigDecimalTOT = (BigDecimal) arr[16];
		if (bigDecimalTOT != null) {
			//BigDecimal aspTOT = new BigDecimal(bigDecimalTOT.longValue());
			obj.setTotalTaxAmt(bigDecimalTOT);
		}
		BigDecimal bigDecimalINV = (BigDecimal) arr[17];
		if (bigDecimalINV != null) {
		//	BigDecimal inv = new BigDecimal(bigDecimalINV.longValue());
			obj.setInvoiceValue(bigDecimalINV);
		}
		/*obj.setCgstAmt(arr[13] != null ? arr[13].toString() : null);
		obj.setSgstAmt(arr[14] != null ? arr[14].toString() : null);
		obj.setCessAmt(arr[15] != null ? arr[15].toString() : null);
		obj.setTotalTaxAmt(arr[16] != null ? arr[16].toString() : null);
		obj.setInvoiceValue(arr[17] != null ? arr[17].toString() : null);*/
		obj.setPos(arr[18] != null ? arr[18].toString() : null);
		obj.setStateName(arr[19] != null ? arr[19].toString() : null);
		obj.setPortCode(arr[20] != null ? arr[20].toString() : null);
		obj.setBillofentryNumber(arr[21] != null ? arr[21].toString() : null);
		obj.setBillofentryDate(arr[22] != null ? arr[22].toString() : null);
		obj.setBillOfEntryRefDate(arr[23] != null ? arr[23].toString() : null);
		obj.setbOEAmendmentFlag(arr[24] != null ? arr[24].toString() : null);
		obj.setOriginalSupplierGSTIN(
				arr[25] != null ? arr[25].toString() : null);
		obj.setOriginalSupplierTradeName(
				arr[26] != null ? arr[26].toString() : null);
		obj.setOriginalPortCode(arr[27] != null ? arr[27].toString() : null);
		obj.setOriginalBillOfEntryNumber(
				arr[28] != null ? arr[28].toString() : null);
		obj.setOriginalBillOfEntryDate(
				arr[29] != null ? arr[29].toString() : null);
		obj.setOriginalBillOfEntryRefDate(
				arr[30] != null ? arr[30].toString() : null);
		/*BigDecimal bigDecimalORGTAX = (BigDecimal) arr[31];
		if (bigDecimalORGTAX != null) {
			BigDecimal Orgtax = new BigDecimal(bigDecimalORGTAX.longValue());
			obj.setOriginalTaxableValue(Orgtax);
		}
		BigDecimal bigDecimalORGIGST = (BigDecimal) arr[32];
		if (bigDecimalORGIGST != null) {
			BigDecimal Orgigst = new BigDecimal(bigDecimalORGIGST.longValue());
			obj.setOriginalIGSTAmount(Orgigst);
		}
		BigDecimal bigDecimalORGCESS = (BigDecimal) arr[33];
		if (bigDecimalORGCESS != null) {
			BigDecimal Orgcess = new BigDecimal(bigDecimalORGCESS.longValue());
			obj.setOriginalCessAmount(Orgcess);
		}*/
		obj.setOriginalTaxableValue(
				arr[31] != null ? arr[31].toString() : null);
		obj.setOriginalIGSTAmount(arr[32] != null ? arr[32].toString() : null);
		obj.setOriginalCessAmount(arr[33] != null ? arr[33].toString() : null);
		obj.setOriginalDocumentNumber(
				arr[34] != null ? arr[34].toString() : null);
		obj.setOriginalDocumentDate(
				arr[35] != null ? arr[35].toString() : null);
		obj.setInvoiceNumber(arr[36] != null ? arr[36].toString() : null);
		obj.setInvoiceDate(arr[37] != null ? arr[37].toString() : null);
		obj.setOriginalInvAmendmentPeriod(
				arr[38] != null ? arr[38].toString() : null);
		obj.setOriginalAmendmentType(
				arr[39] != null ? arr[39].toString() : null);
		obj.setReverseChargeFlag(arr[40] != null ? arr[40].toString() : null);
		obj.setgSTR1FilingStatus(arr[41] != null ? arr[41].toString() : null);
		obj.setgSTR1FilingDate(arr[42] != null ? arr[42].toString() : null);
		obj.setgSTR1FilingPeriod(arr[43] != null ? arr[43].toString() : null);
		obj.setgSTR3BFilingStatus(arr[44] != null ? arr[44].toString() : null);
		obj.setCancellationDate(arr[45] != null ? arr[45].toString() : null);
		obj.setcDNDelinkingFlag(arr[46] != null ? arr[46].toString() : null);
		obj.setCrdrpreGst(arr[47] != null ? arr[47].toString() : null);
		obj.setItcEligible(arr[48] != null ? arr[48].toString() : null);
		obj.setDifferentialPercentage(
				arr[49] != null ? arr[49].toString() : null);
		obj.setLineNumber(arr[50] != null ? arr[50].toString() : null);
		obj.setEcomGstin(arr[51] != null ? arr[51].toString() : null);
		obj.setMerchantID(arr[52] != null ? arr[52].toString() : null);
		obj.setInitiatedDate(arr[53] != null ? arr[53].toString() : null);
		obj.setInitiatedTime(arr[54] != null ? arr[54].toString() : null);
		/*if (arr[54] == null || arr[54] == "null") {
			obj.setInitiatedTime("");
		} else {
			Timestamp date = (Timestamp) arr[54];
			LocalDateTime dt = date.toLocalDateTime();
			LocalDateTime dateTimeFormatter = EYDateUtil
					.toISTDateTimeFromUTC(dt);
			DateTimeFormatter FOMATTER = DateTimeFormatter
					.ofPattern("dd-MM-yyyy HH:mm:ss");
			String newdate = FOMATTER.format(dateTimeFormatter);

			obj.setInitiatedTime(newdate);
		}*/

		return obj;
	}

	private ConsolidatedGstr2ADto convertIsdSection(Object[] arr) {
		ConsolidatedGstr2ADto obj = new ConsolidatedGstr2ADto();
		
		obj.setReturnPeriod(arr[0] != null ? arr[0].toString() : null);
		obj.setRecipientGSTIN(arr[1] != null ? arr[1].toString() : null);
		obj.setSupplierGSTIN(arr[2] != null ? arr[2].toString() : null);
		obj.setSupplierName(arr[3] != null ? arr[3].toString() : null);
		obj.setLegalName(arr[4] != null ? arr[4].toString() : null);
		obj.setDocumentType(arr[5] != null ? arr[5].toString() : null);
		obj.setSupplyType(arr[6] != null ? arr[6].toString() : null);
		obj.setDocumentNumber(arr[8] != null ? DownloadReportsConstant.CSVCHARACTER.concat(arr[8].toString()) : null);
		obj.setDocumentDate(arr[9] != null ? arr[9].toString() : null);
		BigDecimal bigDecimalTaxVal = (BigDecimal) arr[10];
		if (bigDecimalTaxVal != null) {
			//BigDecimal aspTax = new BigDecimal(bigDecimalTaxVal.longValue());
			obj.setTaxableValue(bigDecimalTaxVal);
		}
		//obj.setTaxableValue(arr[10] != null ? arr[10].toString() : null);
		obj.setTaxRate(arr[11] != null ? arr[11].toString() : null);
		//obj.setIgstAmt(arr[12] != null ? arr[12].toString() : null);
		BigDecimal bigDecimalIGST = (BigDecimal) arr[12];
		if (bigDecimalIGST != null) {
			//BigDecimal aspIGST = new BigDecimal(bigDecimalIGST.longValue());
			obj.setIgstAmt(bigDecimalIGST);
		}
		BigDecimal bigDecimalCGST = (BigDecimal) arr[13];
		if (bigDecimalCGST != null) {
			//BigDecimal aspCGST = new BigDecimal(bigDecimalCGST.longValue());
			obj.setCgstAmt(bigDecimalCGST);
		}
		BigDecimal bigDecimalSGST = (BigDecimal) arr[14];
		if (bigDecimalSGST != null) {
			//BigDecimal aspSGST = new BigDecimal(bigDecimalSGST.longValue());
			obj.setSgstAmt(bigDecimalSGST);
		}
		BigDecimal bigDecimalCESS = (BigDecimal) arr[15];
		if (bigDecimalCESS != null) {
			//BigDecimal aspCESS = new BigDecimal(bigDecimalCESS.longValue());
			obj.setCessAmt(bigDecimalCESS);
		}
		BigDecimal bigDecimalTOT = (BigDecimal) arr[16];
		if (bigDecimalTOT != null) {
		//	BigDecimal aspTOT = new BigDecimal(bigDecimalTOT.longValue());
			obj.setTotalTaxAmt(bigDecimalTOT);
		}
		BigDecimal bigDecimalINV = (BigDecimal) arr[17];
		if (bigDecimalINV != null) {
		//	BigDecimal inv = new BigDecimal(bigDecimalINV.longValue());
			obj.setInvoiceValue(bigDecimalINV);
		}
		/*obj.setCgstAmt(arr[13] != null ? arr[13].toString() : null);
		obj.setSgstAmt(arr[14] != null ? arr[14].toString() : null);
		obj.setCessAmt(arr[15] != null ? arr[15].toString() : null);
		obj.setTotalTaxAmt(arr[16] != null ? arr[16].toString() : null);
		obj.setInvoiceValue(arr[17] != null ? arr[17].toString() : null);*/
		obj.setPos(arr[18] != null ? arr[18].toString() : null);
		obj.setStateName(arr[19] != null ? arr[19].toString() : null);
		obj.setPortCode(arr[20] != null ? arr[20].toString() : null);
		obj.setBillofentryNumber(arr[21] != null ? arr[21].toString() : null);
		obj.setBillofentryDate(arr[22] != null ? arr[22].toString() : null);
		obj.setBillOfEntryRefDate(arr[23] != null ? arr[23].toString() : null);
		obj.setbOEAmendmentFlag(arr[24] != null ? arr[24].toString() : null);
		obj.setOriginalSupplierGSTIN(
				arr[25] != null ? arr[25].toString() : null);
		obj.setOriginalSupplierTradeName(
				arr[26] != null ? arr[26].toString() : null);
		obj.setOriginalPortCode(arr[27] != null ? arr[27].toString() : null);
		obj.setOriginalBillOfEntryNumber(
				arr[28] != null ? arr[28].toString() : null);
		obj.setOriginalBillOfEntryDate(
				arr[29] != null ? arr[29].toString() : null);
		obj.setOriginalBillOfEntryRefDate(
				arr[30] != null ? arr[30].toString() : null);
		/*BigDecimal bigDecimalORGTAX = (BigDecimal) arr[31];
		if (bigDecimalORGTAX != null) {
			BigDecimal Orgtax = new BigDecimal(bigDecimalORGTAX.longValue());
			obj.setOriginalTaxableValue(Orgtax);
		}
		BigDecimal bigDecimalORGIGST = (BigDecimal) arr[32];
		if (bigDecimalORGIGST != null) {
			BigDecimal Orgigst = new BigDecimal(bigDecimalORGIGST.longValue());
			obj.setOriginalIGSTAmount(Orgigst);
		}
		BigDecimal bigDecimalORGCESS = (BigDecimal) arr[33];
		if (bigDecimalORGCESS != null) {
			BigDecimal Orgcess = new BigDecimal(bigDecimalORGCESS.longValue());
			obj.setOriginalCessAmount(Orgcess);
		}*/
		obj.setOriginalTaxableValue(
				arr[31] != null ? arr[31].toString() : null);
		obj.setOriginalIGSTAmount(arr[32] != null ? arr[32].toString() : null);
		obj.setOriginalCessAmount(arr[33] != null ? arr[33].toString() : null);
		obj.setOriginalDocumentNumber(
				arr[34] != null ? arr[34].toString() : null);
		obj.setOriginalDocumentDate(
				arr[35] != null ? arr[35].toString() : null);
		obj.setInvoiceNumber(arr[36] != null ? arr[36].toString() : null);
		obj.setInvoiceDate(arr[37] != null ? arr[37].toString() : null);
		obj.setOriginalInvAmendmentPeriod(
				arr[38] != null ? arr[38].toString() : null);
		obj.setOriginalAmendmentType(
				arr[39] != null ? arr[39].toString() : null);
		obj.setReverseChargeFlag(arr[40] != null ? arr[40].toString() : null);
		obj.setgSTR1FilingStatus(arr[41] != null ? arr[41].toString() : null);
		obj.setgSTR1FilingDate(arr[42] != null ? arr[42].toString() : null);
		obj.setgSTR1FilingPeriod(arr[43] != null ? arr[43].toString() : null);
		obj.setgSTR3BFilingStatus(arr[44] != null ? arr[44].toString() : null);
		obj.setCancellationDate(arr[45] != null ? arr[45].toString() : null);
		obj.setcDNDelinkingFlag(arr[46] != null ? arr[46].toString() : null);
		obj.setCrdrpreGst(arr[47] != null ? arr[47].toString() : null);
		obj.setItcEligible(arr[48] != null ? arr[48].toString() : null);
		obj.setDifferentialPercentage(
				arr[49] != null ? arr[49].toString() : null);
		obj.setLineNumber(arr[50] != null ? arr[50].toString() : null);
		obj.setEcomGstin(arr[51] != null ? arr[51].toString() : null);
		obj.setMerchantID(arr[52] != null ? arr[52].toString() : null);
		obj.setInitiatedDate(arr[53] != null ? arr[53].toString() : null);
		obj.setInitiatedTime(arr[54] != null ? arr[54].toString() : null);
		/*if (arr[54] == null || arr[54] == "null") {
			obj.setInitiatedTime("");
		} else {
			Timestamp date = (Timestamp) arr[54];
			LocalDateTime dt = date.toLocalDateTime();
			LocalDateTime dateTimeFormatter = EYDateUtil
					.toISTDateTimeFromUTC(dt);
			DateTimeFormatter FOMATTER = DateTimeFormatter
					.ofPattern("dd-MM-yyyy HH:mm:ss");
			String newdate = FOMATTER.format(dateTimeFormatter);

			obj.setInitiatedTime(newdate);
		}*/

		return obj;
	}

	private ConsolidatedGstr2ADto convertImpgSection(Object[] arr) {
		ConsolidatedGstr2ADto obj = new ConsolidatedGstr2ADto();
		
		obj.setReturnPeriod(arr[0] != null ? arr[0].toString() : null);
		obj.setRecipientGSTIN(arr[1] != null ? arr[1].toString() : null);
		obj.setSupplierGSTIN(arr[2] != null ? arr[2].toString() : null);
		obj.setSupplierName(arr[3] != null ? arr[3].toString() : null);
		obj.setLegalName(arr[4] != null ? arr[4].toString() : null);
		obj.setDocumentType(arr[5] != null ? arr[5].toString() : null);
		obj.setSupplyType(arr[6] != null ? arr[6].toString() : null);
		obj.setDocumentNumber(arr[8] != null ? DownloadReportsConstant.CSVCHARACTER.concat(arr[8].toString()) : null);
		obj.setDocumentDate(arr[9] != null ? arr[9].toString() : null);
		BigDecimal bigDecimalTaxVal = (BigDecimal) arr[10];
		if (bigDecimalTaxVal != null) {
		//	BigDecimal aspTax = new BigDecimal(bigDecimalTaxVal.longValue());
			obj.setTaxableValue(bigDecimalTaxVal);
		}
		//obj.setTaxableValue(arr[10] != null ? arr[10].toString() : null);
		obj.setTaxRate(arr[11] != null ? arr[11].toString() : null);
		//obj.setIgstAmt(arr[12] != null ? arr[12].toString() : null);
		BigDecimal bigDecimalIGST = (BigDecimal) arr[12];
		if (bigDecimalIGST != null) {
		//	BigDecimal aspIGST = new BigDecimal(bigDecimalIGST.longValue());
			obj.setIgstAmt(bigDecimalIGST);
		}
		/*BigDecimal bigDecimalCGST = (BigDecimal) arr[13];
		if (bigDecimalCGST != null) {
			BigDecimal aspCGST = new BigDecimal(bigDecimalCGST.longValue());
			obj.setCgstAmt(aspCGST);
		}*/
		/*BigDecimal bigDecimalSGST = (BigDecimal) arr[14];
		if (bigDecimalSGST != null) {
			BigDecimal aspSGST = new BigDecimal(bigDecimalSGST.longValue());
			obj.setSgstAmt(aspSGST);
		}*/
		BigDecimal bigDecimalCESS = (BigDecimal) arr[15];
		if (bigDecimalCESS != null) {
		//	BigDecimal aspCESS = new BigDecimal(bigDecimalCESS.longValue());
			obj.setCessAmt(bigDecimalCESS);
		}
		BigDecimal bigDecimalTOT = (BigDecimal) arr[16];
		if (bigDecimalTOT != null) {
			//BigDecimal aspTOT = new BigDecimal(bigDecimalTOT.longValue());
			obj.setTotalTaxAmt(bigDecimalTOT);
		}
		/*BigDecimal bigDecimalINV = (BigDecimal) arr[17];
		if (bigDecimalINV != null) {
			BigDecimal inv = new BigDecimal(bigDecimalINV.longValue());
			obj.setInvoiceValue(inv);
		}*/
		/*obj.setCgstAmt(arr[13] != null ? arr[13].toString() : null);
		obj.setSgstAmt(arr[14] != null ? arr[14].toString() : null);
		obj.setCessAmt(arr[15] != null ? arr[15].toString() : null);
		obj.setTotalTaxAmt(arr[16] != null ? arr[16].toString() : null);
		obj.setInvoiceValue(arr[17] != null ? arr[17].toString() : null);*/
		obj.setPos(arr[18] != null ? arr[18].toString() : null);
		obj.setStateName(arr[19] != null ? arr[19].toString() : null);
		obj.setPortCode(arr[20] != null ? arr[20].toString() : null);
		obj.setBillofentryNumber(arr[21] != null ? arr[21].toString() : null);
		obj.setBillofentryDate(arr[22] != null ? arr[22].toString() : null);
		obj.setBillOfEntryRefDate(arr[23] != null ? arr[23].toString() : null);
		obj.setbOEAmendmentFlag(arr[24] != null ? arr[24].toString() : null);
		obj.setOriginalSupplierGSTIN(
				arr[25] != null ? arr[25].toString() : null);
		obj.setOriginalSupplierTradeName(
				arr[26] != null ? arr[26].toString() : null);
		obj.setOriginalPortCode(arr[27] != null ? arr[27].toString() : null);
		obj.setOriginalBillOfEntryNumber(
				arr[28] != null ? arr[28].toString() : null);
		obj.setOriginalBillOfEntryDate(
				arr[29] != null ? arr[29].toString() : null);
		obj.setOriginalBillOfEntryRefDate(
				arr[30] != null ? arr[30].toString() : null);
		/*BigDecimal bigDecimalORGTAX = (BigDecimal) arr[31];
		if (bigDecimalORGTAX != null) {
			BigDecimal Orgtax = new BigDecimal(bigDecimalORGTAX.longValue());
			obj.setOriginalTaxableValue(Orgtax);
		}
		BigDecimal bigDecimalORGIGST = (BigDecimal) arr[32];
		if (bigDecimalORGIGST != null) {
			BigDecimal Orgigst = new BigDecimal(bigDecimalORGIGST.longValue());
			obj.setOriginalIGSTAmount(Orgigst);
		}
		BigDecimal bigDecimalORGCESS = (BigDecimal) arr[33];
		if (bigDecimalORGCESS != null) {
			BigDecimal Orgcess = new BigDecimal(bigDecimalORGCESS.longValue());
			obj.setOriginalCessAmount(Orgcess);
		}*/
		obj.setOriginalTaxableValue(
				arr[31] != null ? arr[31].toString() : null);
		obj.setOriginalIGSTAmount(arr[32] != null ? arr[32].toString() : null);
		obj.setOriginalCessAmount(arr[33] != null ? arr[33].toString() : null);
		obj.setOriginalDocumentNumber(
				arr[34] != null ? arr[34].toString() : null);
		obj.setOriginalDocumentDate(
				arr[35] != null ? arr[35].toString() : null);
		obj.setInvoiceNumber(arr[36] != null ? arr[36].toString() : null);
		obj.setInvoiceDate(arr[37] != null ? arr[37].toString() : null);
		obj.setOriginalInvAmendmentPeriod(
				arr[38] != null ? arr[38].toString() : null);
		obj.setOriginalAmendmentType(
				arr[39] != null ? arr[39].toString() : null);
		obj.setReverseChargeFlag(arr[40] != null ? arr[40].toString() : null);
		obj.setgSTR1FilingStatus(arr[41] != null ? arr[41].toString() : null);
		obj.setgSTR1FilingDate(arr[42] != null ? arr[42].toString() : null);
		obj.setgSTR1FilingPeriod(arr[43] != null ? arr[43].toString() : null);
		obj.setgSTR3BFilingStatus(arr[44] != null ? arr[44].toString() : null);
		obj.setCancellationDate(arr[45] != null ? arr[45].toString() : null);
		obj.setcDNDelinkingFlag(arr[46] != null ? arr[46].toString() : null);
		obj.setCrdrpreGst(arr[47] != null ? arr[47].toString() : null);
		obj.setItcEligible(arr[48] != null ? arr[48].toString() : null);
		obj.setDifferentialPercentage(
				arr[49] != null ? arr[49].toString() : null);
		obj.setLineNumber(arr[50] != null ? arr[50].toString() : null);
		obj.setEcomGstin(arr[51] != null ? arr[51].toString() : null);
		obj.setMerchantID(arr[52] != null ? arr[52].toString() : null);
		obj.setInitiatedDate(arr[53] != null ? arr[53].toString() : null);
		obj.setInitiatedTime(arr[54] != null ? arr[54].toString() : null);
		/*if (arr[54] == null || arr[54] == "null") {
			obj.setInitiatedTime("");
		} else {
			Timestamp date = (Timestamp) arr[54];
			LocalDateTime dt = date.toLocalDateTime();
			LocalDateTime dateTimeFormatter = EYDateUtil
					.toISTDateTimeFromUTC(dt);
			DateTimeFormatter FOMATTER = DateTimeFormatter
					.ofPattern("dd-MM-yyyy HH:mm:ss");
			String newdate = FOMATTER.format(dateTimeFormatter);

			obj.setInitiatedTime(newdate);
		}*/

		return obj;
	}

	private ConsolidatedGstr2ADto convertB2baSection(Object[] arr) {
		ConsolidatedGstr2ADto obj = new ConsolidatedGstr2ADto();

		obj.setReturnPeriod(arr[0] != null ? arr[0].toString() : null);
		obj.setRecipientGSTIN(arr[1] != null ? arr[1].toString() : null);
		obj.setSupplierGSTIN(arr[2] != null ? arr[2].toString() : null);
		obj.setSupplierName(arr[3] != null ? arr[3].toString() : null);
		obj.setLegalName(arr[4] != null ? arr[4].toString() : null);
		obj.setDocumentType(arr[5] != null ? arr[5].toString() : null);
		obj.setSupplyType(arr[6] != null ? arr[6].toString() : null);
		obj.setDocumentNumber(arr[8] != null ? DownloadReportsConstant.CSVCHARACTER.concat(arr[8].toString()) : null);
		obj.setDocumentDate(arr[9] != null ? arr[9].toString() : null);
		BigDecimal bigDecimalTaxVal = (BigDecimal) arr[10];
		if (bigDecimalTaxVal != null) {
			//BigDecimal aspTax = new BigDecimal(bigDecimalTaxVal.longValue());
			obj.setTaxableValue(bigDecimalTaxVal);
		}
		// obj.setTaxableValue(arr[10] != null ? arr[10].toString() : null);
		obj.setTaxRate(arr[11] != null ? arr[11].toString() : null);
		// obj.setIgstAmt(arr[12] != null ? arr[12].toString() : null);
		BigDecimal bigDecimalIGST = (BigDecimal) arr[12];
		if (bigDecimalIGST != null) {
			BigDecimal aspIGST = new BigDecimal(bigDecimalIGST.longValue());
			obj.setIgstAmt(bigDecimalIGST);
		}
		BigDecimal bigDecimalCGST = (BigDecimal) arr[13];
		if (bigDecimalCGST != null) {
			BigDecimal aspCGST = new BigDecimal(bigDecimalCGST.longValue());
			obj.setCgstAmt(bigDecimalCGST);
		}
		BigDecimal bigDecimalSGST = (BigDecimal) arr[14];
		if (bigDecimalSGST != null) {
			BigDecimal aspSGST = new BigDecimal(bigDecimalSGST.longValue());
			obj.setSgstAmt(bigDecimalSGST);
		}
		BigDecimal bigDecimalCESS = (BigDecimal) arr[15];
		if (bigDecimalCESS != null) {
			BigDecimal aspCESS = new BigDecimal(bigDecimalCESS.longValue());
			obj.setCessAmt(bigDecimalCESS);
		}
		BigDecimal bigDecimalTOT = (BigDecimal) arr[16];
		if (bigDecimalTOT != null) {
	//		BigDecimal aspTOT = new BigDecimal(bigDecimalTOT.longValue());
			obj.setTotalTaxAmt(bigDecimalTOT);
		}
		BigDecimal bigDecimalINV = (BigDecimal) arr[17];
		if (bigDecimalINV != null) {
		//	BigDecimal inv = new BigDecimal(bigDecimalINV.longValue());
			obj.setInvoiceValue(bigDecimalINV);
		}
		/*
		 * obj.setCgstAmt(arr[13] != null ? arr[13].toString() : null);
		 * obj.setSgstAmt(arr[14] != null ? arr[14].toString() : null);
		 * obj.setCessAmt(arr[15] != null ? arr[15].toString() : null);
		 * obj.setTotalTaxAmt(arr[16] != null ? arr[16].toString() : null);
		 * obj.setInvoiceValue(arr[17] != null ? arr[17].toString() : null);
		 */
		obj.setPos(arr[18] != null ? arr[18].toString() : null);
		obj.setStateName(arr[19] != null ? arr[19].toString() : null);
		obj.setPortCode(arr[20] != null ? arr[20].toString() : null);
		obj.setBillofentryNumber(arr[21] != null ? arr[21].toString() : null);
		obj.setBillofentryDate(arr[22] != null ? arr[22].toString() : null);
		obj.setBillOfEntryRefDate(arr[23] != null ? arr[23].toString() : null);
		obj.setbOEAmendmentFlag(arr[24] != null ? arr[24].toString() : null);
		obj.setOriginalSupplierGSTIN(
				arr[25] != null ? arr[25].toString() : null);
		obj.setOriginalSupplierTradeName(
				arr[26] != null ? arr[26].toString() : null);
		obj.setOriginalPortCode(arr[27] != null ? arr[27].toString() : null);
		obj.setOriginalBillOfEntryNumber(
				arr[28] != null ? arr[28].toString() : null);
		obj.setOriginalBillOfEntryDate(
				arr[29] != null ? arr[29].toString() : null);
		obj.setOriginalBillOfEntryRefDate(
				arr[30] != null ? arr[30].toString() : null);
		/*
		 * BigDecimal bigDecimalORGTAX = (BigDecimal) arr[31]; if
		 * (bigDecimalORGTAX != null) { BigDecimal Orgtax = new
		 * BigDecimal(bigDecimalORGTAX.longValue());
		 * obj.setOriginalTaxableValue(Orgtax); } BigDecimal bigDecimalORGIGST =
		 * (BigDecimal) arr[32]; if (bigDecimalORGIGST != null) { BigDecimal
		 * Orgigst = new BigDecimal(bigDecimalORGIGST.longValue());
		 * obj.setOriginalIGSTAmount(Orgigst); } BigDecimal bigDecimalORGCESS =
		 * (BigDecimal) arr[33]; if (bigDecimalORGCESS != null) { BigDecimal
		 * Orgcess = new BigDecimal(bigDecimalORGCESS.longValue());
		 * obj.setOriginalCessAmount(Orgcess); }
		 */
		obj.setOriginalTaxableValue(
				arr[31] != null ? arr[31].toString() : null);
		obj.setOriginalIGSTAmount(arr[32] != null ? arr[32].toString() : null);
		obj.setOriginalCessAmount(arr[33] != null ? arr[33].toString() : null);
		obj.setOriginalDocumentNumber(
				arr[34] != null ? arr[34].toString() : null);
		obj.setOriginalDocumentDate(
				arr[35] != null ? arr[35].toString() : null);
		obj.setInvoiceNumber(arr[36] != null ? arr[36].toString() : null);
		obj.setInvoiceDate(arr[37] != null ? arr[37].toString() : null);
		obj.setOriginalInvAmendmentPeriod(
				arr[38] != null ? arr[38].toString() : null);
		obj.setOriginalAmendmentType(
				arr[39] != null ? arr[39].toString() : null);
		obj.setReverseChargeFlag(arr[40] != null ? arr[40].toString() : null);
		obj.setgSTR1FilingStatus(arr[41] != null ? arr[41].toString() : null);
		obj.setgSTR1FilingDate(arr[42] != null ? arr[42].toString() : null);
		obj.setgSTR1FilingPeriod(arr[43] != null ? arr[43].toString() : null);
		obj.setgSTR3BFilingStatus(arr[44] != null ? arr[44].toString() : null);
		obj.setCancellationDate(arr[45] != null ? arr[45].toString() : null);
		obj.setcDNDelinkingFlag(arr[46] != null ? arr[46].toString() : null);
		obj.setCrdrpreGst(arr[47] != null ? arr[47].toString() : null);
		obj.setItcEligible(arr[48] != null ? arr[48].toString() : null);
		obj.setDifferentialPercentage(
				arr[49] != null ? arr[49].toString() : null);
		obj.setLineNumber(arr[50] != null ? arr[50].toString() : null);
		obj.setEcomGstin(arr[51] != null ? arr[51].toString() : null);
		obj.setMerchantID(arr[52] != null ? arr[52].toString() : null);
		obj.setInitiatedDate(arr[53] != null ? arr[53].toString() : null);
		obj.setInitiatedTime(arr[54] != null ? arr[54].toString() : null);
		/*
		 * if (arr[54] == null || arr[54] == "null") { obj.setInitiatedTime("");
		 * } else { Timestamp date = (Timestamp) arr[54]; LocalDateTime dt =
		 * date.toLocalDateTime(); LocalDateTime dateTimeFormatter = EYDateUtil
		 * .toISTDateTimeFromUTC(dt); DateTimeFormatter FOMATTER =
		 * DateTimeFormatter .ofPattern("dd-MM-yyyy HH:mm:ss"); String newdate =
		 * FOMATTER.format(dateTimeFormatter);
		 * 
		 * obj.setInitiatedTime(newdate); }
		 */
		return obj;
	}

	private ConsolidatedGstr2ADto convertImpgsezSection(Object[] arr) {
		ConsolidatedGstr2ADto obj = new ConsolidatedGstr2ADto();
		
		obj.setReturnPeriod(arr[0] != null ? arr[0].toString() : null);
		obj.setRecipientGSTIN(arr[1] != null ? arr[1].toString() : null);
		obj.setSupplierGSTIN(arr[2] != null ? arr[2].toString() : null);
		obj.setSupplierName(arr[3] != null ? arr[3].toString() : null);
		obj.setLegalName(arr[4] != null ? arr[4].toString() : null);
		obj.setDocumentType(arr[5] != null ? arr[5].toString() : null);
		obj.setSupplyType(arr[6] != null ? arr[6].toString() : null);
		obj.setDocumentNumber(arr[8] != null ? DownloadReportsConstant.CSVCHARACTER.concat(arr[8].toString()) : null);
		obj.setDocumentDate(arr[9] != null ? arr[9].toString() : null);
		BigDecimal bigDecimalTaxVal = (BigDecimal) arr[10];
		if (bigDecimalTaxVal != null) {
			//BigDecimal aspTax = new BigDecimal(bigDecimalTaxVal.longValue());
			obj.setTaxableValue(bigDecimalTaxVal);
		}
		//obj.setTaxableValue(arr[10] != null ? arr[10].toString() : null);
		obj.setTaxRate(arr[11] != null ? arr[11].toString() : null);
		//obj.setIgstAmt(arr[12] != null ? arr[12].toString() : null);
		BigDecimal bigDecimalIGST = (BigDecimal) arr[12];
		if (bigDecimalIGST != null) {
		//	BigDecimal aspIGST = new BigDecimal(bigDecimalIGST.longValue());
			obj.setIgstAmt(bigDecimalIGST);
		}
		/*BigDecimal bigDecimalCGST = (BigDecimal) arr[13];
		if (bigDecimalCGST != null) {
			BigDecimal aspCGST = new BigDecimal(bigDecimalCGST.longValue());
			obj.setCgstAmt(aspCGST);
		}*/
		/*BigDecimal bigDecimalSGST = (BigDecimal) arr[14];
		if (bigDecimalSGST != null) {
			BigDecimal aspSGST = new BigDecimal(bigDecimalSGST.longValue());
			obj.setSgstAmt(aspSGST);
		}*/
		BigDecimal bigDecimalCESS = (BigDecimal) arr[15];
		if (bigDecimalCESS != null) {
			//BigDecimal aspCESS = new BigDecimal(bigDecimalCESS.longValue());
			obj.setCessAmt(bigDecimalCESS);
		}
		BigDecimal bigDecimalTOT = (BigDecimal) arr[16];
		if (bigDecimalTOT != null) {
			//BigDecimal aspTOT = new BigDecimal(bigDecimalTOT.longValue());
			obj.setTotalTaxAmt(bigDecimalTOT);
		}
		/*BigDecimal bigDecimalINV = (BigDecimal) arr[17];
		if (bigDecimalINV != null) {
			BigDecimal inv = new BigDecimal(bigDecimalINV.longValue());
			obj.setInvoiceValue(inv);
		}*/
		/*obj.setCgstAmt(arr[13] != null ? arr[13].toString() : null);
		obj.setSgstAmt(arr[14] != null ? arr[14].toString() : null);
		obj.setCessAmt(arr[15] != null ? arr[15].toString() : null);
		obj.setTotalTaxAmt(arr[16] != null ? arr[16].toString() : null);
		obj.setInvoiceValue(arr[17] != null ? arr[17].toString() : null);*/
		obj.setPos(arr[18] != null ? arr[18].toString() : null);
		obj.setStateName(arr[19] != null ? arr[19].toString() : null);
		obj.setPortCode(arr[20] != null ? arr[20].toString() : null);
		obj.setBillofentryNumber(arr[21] != null ? arr[21].toString() : null);
		obj.setBillofentryDate(arr[22] != null ? arr[22].toString() : null);
		obj.setBillOfEntryRefDate(arr[23] != null ? arr[23].toString() : null);
		obj.setbOEAmendmentFlag(arr[24] != null ? arr[24].toString() : null);
		obj.setOriginalSupplierGSTIN(
				arr[25] != null ? arr[25].toString() : null);
		obj.setOriginalSupplierTradeName(
				arr[26] != null ? arr[26].toString() : null);
		obj.setOriginalPortCode(arr[27] != null ? arr[27].toString() : null);
		obj.setOriginalBillOfEntryNumber(
				arr[28] != null ? arr[28].toString() : null);
		obj.setOriginalBillOfEntryDate(
				arr[29] != null ? arr[29].toString() : null);
		obj.setOriginalBillOfEntryRefDate(
				arr[30] != null ? arr[30].toString() : null);
		/*BigDecimal bigDecimalORGTAX = (BigDecimal) arr[31];
		if (bigDecimalORGTAX != null) {
			BigDecimal Orgtax = new BigDecimal(bigDecimalORGTAX.longValue());
			obj.setOriginalTaxableValue(Orgtax);
		}
		BigDecimal bigDecimalORGIGST = (BigDecimal) arr[32];
		if (bigDecimalORGIGST != null) {
			BigDecimal Orgigst = new BigDecimal(bigDecimalORGIGST.longValue());
			obj.setOriginalIGSTAmount(Orgigst);
		}
		BigDecimal bigDecimalORGCESS = (BigDecimal) arr[33];
		if (bigDecimalORGCESS != null) {
			BigDecimal Orgcess = new BigDecimal(bigDecimalORGCESS.longValue());
			obj.setOriginalCessAmount(Orgcess);
		}*/
		obj.setOriginalTaxableValue(
				arr[31] != null ? arr[31].toString() : null);
		obj.setOriginalIGSTAmount(arr[32] != null ? arr[32].toString() : null);
		obj.setOriginalCessAmount(arr[33] != null ? arr[33].toString() : null);
		obj.setOriginalDocumentNumber(
				arr[34] != null ? arr[34].toString() : null);
		obj.setOriginalDocumentDate(
				arr[35] != null ? arr[35].toString() : null);
		obj.setInvoiceNumber(arr[36] != null ? arr[36].toString() : null);
		obj.setInvoiceDate(arr[37] != null ? arr[37].toString() : null);
		obj.setOriginalInvAmendmentPeriod(
				arr[38] != null ? arr[38].toString() : null);
		obj.setOriginalAmendmentType(
				arr[39] != null ? arr[39].toString() : null);
		obj.setReverseChargeFlag(arr[40] != null ? arr[40].toString() : null);
		obj.setgSTR1FilingStatus(arr[41] != null ? arr[41].toString() : null);
		obj.setgSTR1FilingDate(arr[42] != null ? arr[42].toString() : null);
		obj.setgSTR1FilingPeriod(arr[43] != null ? arr[43].toString() : null);
		obj.setgSTR3BFilingStatus(arr[44] != null ? arr[44].toString() : null);
		obj.setCancellationDate(arr[45] != null ? arr[45].toString() : null);
		obj.setcDNDelinkingFlag(arr[46] != null ? arr[46].toString() : null);
		obj.setCrdrpreGst(arr[47] != null ? arr[47].toString() : null);
		obj.setItcEligible(arr[48] != null ? arr[48].toString() : null);
		obj.setDifferentialPercentage(
				arr[49] != null ? arr[49].toString() : null);
		obj.setLineNumber(arr[50] != null ? arr[50].toString() : null);
		obj.setEcomGstin(arr[51] != null ? arr[51].toString() : null);
		obj.setMerchantID(arr[52] != null ? arr[52].toString() : null);
		obj.setInitiatedDate(arr[53] != null ? arr[53].toString() : null);
		obj.setInitiatedTime(arr[54] != null ? arr[54].toString() : null);
		/*if (arr[54] == null || arr[54] == "null") {
			obj.setInitiatedTime("");
		} else {
			Timestamp date = (Timestamp) arr[54];
			LocalDateTime dt = date.toLocalDateTime();
			LocalDateTime dateTimeFormatter = EYDateUtil
					.toISTDateTimeFromUTC(dt);
			DateTimeFormatter FOMATTER = DateTimeFormatter
					.ofPattern("dd-MM-yyyy HH:mm:ss");
			String newdate = FOMATTER.format(dateTimeFormatter);

			obj.setInitiatedTime(newdate);
		}*/

		return obj;
	}

	private ConsolidatedGstr2ADto convertEcomSection(Object[] arr) {

		ConsolidatedGstr2ADto obj = new ConsolidatedGstr2ADto();

		obj.setReturnPeriod(arr[0] != null ? "'"+arr[0].toString() : null);
		obj.setRecipientGSTIN(arr[1] != null ? arr[1].toString() : null);
		obj.setSupplierGSTIN(arr[2] != null ? arr[2].toString() : null);
		obj.setSupplierName(arr[3] != null ? arr[3].toString() : null);
		obj.setLegalName(arr[4] != null ? arr[4].toString() : null);
		obj.setDocumentType(arr[5] != null ? arr[5].toString() : null);
		obj.setSupplyType(arr[6] != null ? arr[6].toString() : null);
		obj.setDocumentNumber(arr[8] != null ? DownloadReportsConstant.CSVCHARACTER.concat(arr[8].toString()) : null);
		obj.setDocumentDate(arr[9] != null ? arr[9].toString() : null);
		BigDecimal bigDecimalTaxVal = (BigDecimal) arr[10];
		if (bigDecimalTaxVal != null) {
		//	BigDecimal aspTax = new BigDecimal(bigDecimalTaxVal.longValue());
			obj.setTaxableValue(bigDecimalTaxVal);
		}
		// obj.setTaxableValue(arr[10] != null ? arr[10].toString() : null);
		obj.setTaxRate(arr[11] != null ? arr[11].toString() : null);
		// obj.setIgstAmt(arr[12] != null ? arr[12].toString() : null);
		BigDecimal bigDecimalIGST = (BigDecimal) arr[12];
		if (bigDecimalIGST != null) {
			//BigDecimal aspIGST = new BigDecimal(bigDecimalIGST.longValue());
			obj.setIgstAmt(bigDecimalIGST);
		}
		BigDecimal bigDecimalCGST = (BigDecimal) arr[13];
		if (bigDecimalCGST != null) {
			//BigDecimal aspCGST = new BigDecimal(bigDecimalCGST.longValue());
			obj.setCgstAmt(bigDecimalCGST);
		}
		BigDecimal bigDecimalSGST = (BigDecimal) arr[14];
		if (bigDecimalSGST != null) {
		//	BigDecimal aspSGST = new BigDecimal(bigDecimalSGST.longValue());
			obj.setSgstAmt(bigDecimalSGST);
		}
		BigDecimal bigDecimalCESS = (BigDecimal) arr[15];
		if (bigDecimalCESS != null) {
			//BigDecimal aspCESS = new BigDecimal(bigDecimalCESS.longValue());
			obj.setCessAmt(bigDecimalCESS);
		}
		BigDecimal bigDecimalTOT = (BigDecimal) arr[16];
		if (bigDecimalTOT != null) {
			//BigDecimal aspTOT = new BigDecimal(bigDecimalTOT.longValue());
			obj.setTotalTaxAmt(bigDecimalTOT);
		}
		BigDecimal bigDecimalINV = (BigDecimal) arr[17];
		if (bigDecimalINV != null) {
			//BigDecimal inv = new BigDecimal(bigDecimalINV.longValue());
			obj.setInvoiceValue(bigDecimalINV);
		}
		/*
		 * obj.setCgstAmt(arr[13] != null ? arr[13].toString() : null);
		 * obj.setSgstAmt(arr[14] != null ? arr[14].toString() : null);
		 * obj.setCessAmt(arr[15] != null ? arr[15].toString() : null);
		 * obj.setTotalTaxAmt(arr[16] != null ? arr[16].toString() : null);
		 * obj.setInvoiceValue(arr[17] != null ? arr[17].toString() : null);
		 */
		obj.setPos(arr[18] != null ? arr[18].toString() : null);
		obj.setStateName(arr[19] != null ? arr[19].toString() : null);
		obj.setPortCode(arr[20] != null ? arr[20].toString() : null);
		obj.setBillofentryNumber(arr[21] != null ? arr[21].toString() : null);
		obj.setBillofentryDate(arr[22] != null ? arr[22].toString() : null);
		obj.setBillOfEntryRefDate(arr[23] != null ? arr[23].toString() : null);
		obj.setbOEAmendmentFlag(arr[24] != null ? arr[24].toString() : null);
		obj.setOriginalSupplierGSTIN(
				arr[25] != null ? arr[25].toString() : null);
		obj.setOriginalSupplierTradeName(
				arr[26] != null ? arr[26].toString() : null);
		obj.setOriginalPortCode(arr[27] != null ? arr[27].toString() : null);
		obj.setOriginalBillOfEntryNumber(
				arr[28] != null ? arr[28].toString() : null);
		obj.setOriginalBillOfEntryDate(
				arr[29] != null ? arr[29].toString() : null);
		obj.setOriginalBillOfEntryRefDate(
				arr[30] != null ? arr[30].toString() : null);
		/*
		 * BigDecimal bigDecimalORGTAX = (BigDecimal) arr[31]; if
		 * (bigDecimalORGTAX != null) { BigDecimal Orgtax = new
		 * BigDecimal(bigDecimalORGTAX.longValue());
		 * obj.setOriginalTaxableValue(Orgtax); } BigDecimal bigDecimalORGIGST =
		 * (BigDecimal) arr[32]; if (bigDecimalORGIGST != null) { BigDecimal
		 * Orgigst = new BigDecimal(bigDecimalORGIGST.longValue());
		 * obj.setOriginalIGSTAmount(Orgigst); } BigDecimal bigDecimalORGCESS =
		 * (BigDecimal) arr[33]; if (bigDecimalORGCESS != null) { BigDecimal
		 * Orgcess = new BigDecimal(bigDecimalORGCESS.longValue());
		 * obj.setOriginalCessAmount(Orgcess); }
		 */
		obj.setOriginalTaxableValue(
				arr[31] != null ? arr[31].toString() : null);
		obj.setOriginalIGSTAmount(arr[32] != null ? arr[32].toString() : null);
		obj.setOriginalCessAmount(arr[33] != null ? arr[33].toString() : null);
		obj.setOriginalDocumentNumber(
				arr[34] != null ? arr[34].toString() : null);
		obj.setOriginalDocumentDate(
				arr[35] != null ? arr[35].toString() : null);
		obj.setInvoiceNumber(arr[36] != null ? arr[36].toString() : null);
		obj.setInvoiceDate(arr[37] != null ? arr[37].toString() : null);
		obj.setOriginalInvAmendmentPeriod(
				arr[38] != null ? arr[38].toString() : null);
		obj.setOriginalAmendmentType(
				arr[39] != null ? arr[39].toString() : null);
		obj.setReverseChargeFlag(arr[40] != null ? arr[40].toString() : null);
		obj.setgSTR1FilingStatus(arr[41] != null ? arr[41].toString() : null);
		obj.setgSTR1FilingDate(arr[42] != null ? arr[42].toString() : null);
		obj.setgSTR1FilingPeriod(arr[43] != null ? arr[43].toString() : null);
		obj.setgSTR3BFilingStatus(arr[44] != null ? arr[44].toString() : null);
		obj.setCancellationDate(arr[45] != null ? arr[45].toString() : null);
		obj.setcDNDelinkingFlag(arr[46] != null ? arr[46].toString() : null);
		obj.setCrdrpreGst(arr[47] != null ? arr[47].toString() : null);
		obj.setItcEligible(arr[48] != null ? arr[48].toString() : null);
		obj.setDifferentialPercentage(
				arr[49] != null ? arr[49].toString() : null);
		obj.setLineNumber(arr[50] != null ? arr[50].toString() : null);
		obj.setEcomGstin(arr[51] != null ? arr[51].toString() : null);
		obj.setMerchantID(arr[52] != null ? arr[52].toString() : null);
		obj.setInitiatedDate(arr[53] != null ? arr[53].toString() : null);
		obj.setInitiatedTime(arr[54] != null ? arr[54].toString() : null);
		/*
		 * if (arr[54] == null || arr[54] == "null") { obj.setInitiatedTime("");
		 * } else { Timestamp date = (Timestamp) arr[54]; LocalDateTime dt =
		 * date.toLocalDateTime(); LocalDateTime dateTimeFormatter = EYDateUtil
		 * .toISTDateTimeFromUTC(dt); DateTimeFormatter FOMATTER =
		 * DateTimeFormatter .ofPattern("dd-MM-yyyy HH:mm:ss"); String newdate =
		 * FOMATTER.format(dateTimeFormatter);
		 * 
		 * obj.setInitiatedTime(newdate); }
		 */
		obj.setIrnNum(arr[56] != null ? arr[56].toString() : null);
		obj.setIrnGenDate(arr[57] != null ? arr[57].toString() : null);
		obj.setIrnSourceType(arr[58] != null ? arr[58].toString() : null);

		return obj;
	}

	private ConsolidatedGstr2ADto convertEcomaSection(Object[] arr) {
		ConsolidatedGstr2ADto obj = new ConsolidatedGstr2ADto();

		obj.setReturnPeriod(arr[0] != null ? arr[0].toString() : null);
		obj.setRecipientGSTIN(arr[1] != null ? arr[1].toString() : null);
		obj.setSupplierGSTIN(arr[2] != null ? arr[2].toString() : null);
		obj.setSupplierName(arr[3] != null ? arr[3].toString() : null);
		obj.setLegalName(arr[4] != null ? arr[4].toString() : null);
		obj.setDocumentType(arr[5] != null ? arr[5].toString() : null);
		obj.setSupplyType(arr[6] != null ? arr[6].toString() : null);
		obj.setDocumentNumber(arr[8] != null ? DownloadReportsConstant.CSVCHARACTER.concat(arr[8].toString()) : null);
		obj.setDocumentDate(arr[9] != null ? arr[9].toString() : null);
		BigDecimal bigDecimalTaxVal = (BigDecimal) arr[10];
		if (bigDecimalTaxVal != null) {
			//BigDecimal aspTax = new BigDecimal(bigDecimalTaxVal.longValue());
			obj.setTaxableValue(bigDecimalTaxVal);
		}
		// obj.setTaxableValue(arr[10] != null ? arr[10].toString() : null);
		obj.setTaxRate(arr[11] != null ? arr[11].toString() : null);
		// obj.setIgstAmt(arr[12] != null ? arr[12].toString() : null);
		BigDecimal bigDecimalIGST = (BigDecimal) arr[12];
		if (bigDecimalIGST != null) {
			BigDecimal aspIGST = new BigDecimal(bigDecimalIGST.longValue());
			obj.setIgstAmt(bigDecimalIGST);
		}
		BigDecimal bigDecimalCGST = (BigDecimal) arr[13];
		if (bigDecimalCGST != null) {
			BigDecimal aspCGST = new BigDecimal(bigDecimalCGST.longValue());
			obj.setCgstAmt(bigDecimalCGST);
		}
		BigDecimal bigDecimalSGST = (BigDecimal) arr[14];
		if (bigDecimalSGST != null) {
			BigDecimal aspSGST = new BigDecimal(bigDecimalSGST.longValue());
			obj.setSgstAmt(bigDecimalSGST);
		}
		BigDecimal bigDecimalCESS = (BigDecimal) arr[15];
		if (bigDecimalCESS != null) {
			BigDecimal aspCESS = new BigDecimal(bigDecimalCESS.longValue());
			obj.setCessAmt(bigDecimalCESS);
		}
		BigDecimal bigDecimalTOT = (BigDecimal) arr[16];
		if (bigDecimalTOT != null) {
	//		BigDecimal aspTOT = new BigDecimal(bigDecimalTOT.longValue());
			obj.setTotalTaxAmt(bigDecimalTOT);
		}
		BigDecimal bigDecimalINV = (BigDecimal) arr[17];
		if (bigDecimalINV != null) {
		//	BigDecimal inv = new BigDecimal(bigDecimalINV.longValue());
			obj.setInvoiceValue(bigDecimalINV);
		}
		/*
		 * obj.setCgstAmt(arr[13] != null ? arr[13].toString() : null);
		 * obj.setSgstAmt(arr[14] != null ? arr[14].toString() : null);
		 * obj.setCessAmt(arr[15] != null ? arr[15].toString() : null);
		 * obj.setTotalTaxAmt(arr[16] != null ? arr[16].toString() : null);
		 * obj.setInvoiceValue(arr[17] != null ? arr[17].toString() : null);
		 */
		obj.setPos(arr[18] != null ? arr[18].toString() : null);
		obj.setStateName(arr[19] != null ? arr[19].toString() : null);
		obj.setPortCode(arr[20] != null ? arr[20].toString() : null);
		obj.setBillofentryNumber(arr[21] != null ? arr[21].toString() : null);
		obj.setBillofentryDate(arr[22] != null ? arr[22].toString() : null);
		obj.setBillOfEntryRefDate(arr[23] != null ? arr[23].toString() : null);
		obj.setbOEAmendmentFlag(arr[24] != null ? arr[24].toString() : null);
		obj.setOriginalSupplierGSTIN(
				arr[25] != null ? arr[25].toString() : null);
		obj.setOriginalSupplierTradeName(
				arr[26] != null ? arr[26].toString() : null);
		obj.setOriginalPortCode(arr[27] != null ? arr[27].toString() : null);
		obj.setOriginalBillOfEntryNumber(
				arr[28] != null ? arr[28].toString() : null);
		obj.setOriginalBillOfEntryDate(
				arr[29] != null ? arr[29].toString() : null);
		obj.setOriginalBillOfEntryRefDate(
				arr[30] != null ? arr[30].toString() : null);
		/*
		 * BigDecimal bigDecimalORGTAX = (BigDecimal) arr[31]; if
		 * (bigDecimalORGTAX != null) { BigDecimal Orgtax = new
		 * BigDecimal(bigDecimalORGTAX.longValue());
		 * obj.setOriginalTaxableValue(Orgtax); } BigDecimal bigDecimalORGIGST =
		 * (BigDecimal) arr[32]; if (bigDecimalORGIGST != null) { BigDecimal
		 * Orgigst = new BigDecimal(bigDecimalORGIGST.longValue());
		 * obj.setOriginalIGSTAmount(Orgigst); } BigDecimal bigDecimalORGCESS =
		 * (BigDecimal) arr[33]; if (bigDecimalORGCESS != null) { BigDecimal
		 * Orgcess = new BigDecimal(bigDecimalORGCESS.longValue());
		 * obj.setOriginalCessAmount(Orgcess); }
		 */
		obj.setOriginalTaxableValue(
				arr[31] != null ? arr[31].toString() : null);
		obj.setOriginalIGSTAmount(arr[32] != null ? arr[32].toString() : null);
		obj.setOriginalCessAmount(arr[33] != null ? arr[33].toString() : null);
		obj.setOriginalDocumentNumber(
				arr[34] != null ? arr[34].toString() : null);
		obj.setOriginalDocumentDate(
				arr[35] != null ? arr[35].toString() : null);
		obj.setInvoiceNumber(arr[36] != null ? arr[36].toString() : null);
		obj.setInvoiceDate(arr[37] != null ? arr[37].toString() : null);
		obj.setOriginalInvAmendmentPeriod(
				arr[38] != null ? arr[38].toString() : null);
		obj.setOriginalAmendmentType(
				arr[39] != null ? arr[39].toString() : null);
		obj.setReverseChargeFlag(arr[40] != null ? arr[40].toString() : null);
		obj.setgSTR1FilingStatus(arr[41] != null ? arr[41].toString() : null);
		obj.setgSTR1FilingDate(arr[42] != null ? arr[42].toString() : null);
		obj.setgSTR1FilingPeriod(arr[43] != null ? arr[43].toString() : null);
		obj.setgSTR3BFilingStatus(arr[44] != null ? arr[44].toString() : null);
		obj.setCancellationDate(arr[45] != null ? arr[45].toString() : null);
		obj.setcDNDelinkingFlag(arr[46] != null ? arr[46].toString() : null);
		obj.setCrdrpreGst(arr[47] != null ? arr[47].toString() : null);
		obj.setItcEligible(arr[48] != null ? arr[48].toString() : null);
		obj.setDifferentialPercentage(
				arr[49] != null ? arr[49].toString() : null);
		obj.setLineNumber(arr[50] != null ? arr[50].toString() : null);
		obj.setEcomGstin(arr[51] != null ? arr[51].toString() : null);
		obj.setMerchantID(arr[52] != null ? arr[52].toString() : null);
		obj.setInitiatedDate(arr[53] != null ? arr[53].toString() : null);
		obj.setInitiatedTime(arr[54] != null ? arr[54].toString() : null);
		/*
		 * if (arr[54] == null || arr[54] == "null") { obj.setInitiatedTime("");
		 * } else { Timestamp date = (Timestamp) arr[54]; LocalDateTime dt =
		 * date.toLocalDateTime(); LocalDateTime dateTimeFormatter = EYDateUtil
		 * .toISTDateTimeFromUTC(dt); DateTimeFormatter FOMATTER =
		 * DateTimeFormatter .ofPattern("dd-MM-yyyy HH:mm:ss"); String newdate =
		 * FOMATTER.format(dateTimeFormatter);
		 * 
		 * obj.setInitiatedTime(newdate); }
		 */
		return obj;
	}

	@Override
	public String createSftpFileName(String gstin, String index, String total,
			String taxPeriod, String section) {
		LocalDateTime now = LocalDateTime.now();
		String timeMilli = EYDateUtil.toISTDateTimeFromUTC(now).toString();

		timeMilli = timeMilli.replace(".", "");
		timeMilli = timeMilli.replace("-", "");
		timeMilli = timeMilli.replace(":", "");

		// To-Do have to add timestamp and sectionName
		String fileName = gstin + "_" + taxPeriod + "_" + section + "_"
				+ timeMilli + "_" + index + "_" + total;
		return fileName;
	}

	@Override
	public Integer uploadToSftpServer(String filename,
			List<ConsolidatedGstr2ADto> reconGetDataList) {

		File tempDir = null;

		try {
			Map<String, Config> configMap = configManager.getConfigs("SFTP",
					"ey.internal.get.gstr2a.sftp.response");

			tempDir = createTempDir();
			String fullPath = tempDir.getAbsolutePath() + File.separator
					+ filename;

			try (Writer writer = new BufferedWriter(new FileWriter(fullPath),
					CSV_BUFFER_SIZE)) {

				String invoiceHeadersTemplate = commonUtility
						.getProp("gstr2.get.recon.sftp.report.header");
				writer.append(invoiceHeadersTemplate);
				String[] columnMappings = commonUtility
						.getProp("gstr2.get.recon.sftp.report.column")
						.split(",");

				if (reconGetDataList != null && !reconGetDataList.isEmpty()) {

					ColumnPositionMappingStrategy<ConsolidatedGstr2ADto> mappingStrategy = new ColumnPositionMappingStrategy<>();
					mappingStrategy.setType(ConsolidatedGstr2ADto.class);
					mappingStrategy.setColumnMapping(columnMappings);
					StatefulBeanToCsvBuilder<ConsolidatedGstr2ADto> builder = new StatefulBeanToCsvBuilder<>(
							writer);
					StatefulBeanToCsv<ConsolidatedGstr2ADto> beanWriter = builder
							.withMappingStrategy(mappingStrategy)
							.withSeparator(CSVWriter.DEFAULT_SEPARATOR)
							.withLineEnd(CSVWriter.DEFAULT_LINE_END)
							.withEscapechar(CSVWriter.DEFAULT_ESCAPE_CHARACTER)
							.build();
					long generationStTime = System.currentTimeMillis();
					beanWriter.write(reconGetDataList);
					long generationEndTime = System.currentTimeMillis();
					long generationTimeDiff = (generationEndTime
							- generationStTime);
					if (LOGGER.isDebugEnabled()) {
						String msg = String.format("Total Time taken to"
								+ " Generate the report is '%d'" + "millisecs,"
								+ " Report Name and Data count:" + " '%d'",
								generationTimeDiff, reconGetDataList.size());
						LOGGER.debug(msg);
					}

				}
				flushWriter(writer);

				boolean isReverseInt = sftpService.uploadFiles(
						Arrays.asList(fullPath),
						configMap.get(GSTR2A_SFTP_RESPONSE_DESTINATION)
								.getValue());
				if (isReverseInt) {
					return 200;
				} else {
					return 0;
				}
			} catch (Exception e) {
				String msg = "Exception while writing to csv";
				LOGGER.error(msg, e);
				throw new AppException(msg, e);

			}
		} catch (Exception e) {
			String msg = "Exception while doing get gstr2a sftp";
			LOGGER.error(msg, e);
			throw new AppException(msg, e);

		} finally {
			deleteTemporaryDirectory(tempDir);
		}
	}

	private void flushWriter(Writer writer) {
		if (writer != null) {
			try {
				writer.flush();
				writer.close();
				if (LOGGER.isDebugEnabled()) {
					String msg = "Flushed writer " + "successfully";
					LOGGER.debug(msg);
				}
			} catch (IOException e) {
				String msg = "Exception while " + "closing the file writer";
				LOGGER.error(msg);
				throw new AppException(msg, e);
			}
		}
	}

	private void deleteTemporaryDirectory(File tempFile) {

		if (tempFile != null && tempFile.exists()) {
			try {
				FileUtils.deleteDirectory(tempFile);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(String.format(
							"Deleted the Temp directory/Folder '%s'",
							tempFile.getAbsolutePath()));
				}
			} catch (Exception ex) {
				String msg = String.format(
						"Failed to remove the temp "
								+ "directory created for zip: '%s'. This will "
								+ "lead to clogging of disk space.",
						tempFile.getAbsolutePath());
				LOGGER.error(msg, ex);
			}
		}

	}

	private static File createTempDir() throws IOException {

		return Files.createTempDirectory("GSTR2aSftpPush").toFile();
	}
}
