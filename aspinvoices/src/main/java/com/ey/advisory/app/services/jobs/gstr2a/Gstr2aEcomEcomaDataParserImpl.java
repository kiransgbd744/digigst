
package com.ey.advisory.app.services.jobs.gstr2a;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.GetGstr2aStagingEcomInvoicesHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2aStagingEcomInvoicesItemEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2aStagingEcomaInvoicesHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2aStagingEcomaInvoicesItemEntity;
import com.ey.advisory.app.data.entities.client.InwardTransDocument;
import com.ey.advisory.app.docs.dto.B2BInvoiceData;
import com.ey.advisory.app.docs.dto.B2BInvoices;
import com.ey.advisory.app.docs.dto.B2bLineItem;
import com.ey.advisory.app.docs.dto.B2bLineItemDetail;
import com.ey.advisory.app.services.docs.DocKeyGenerator;
import com.ey.advisory.common.DateUtil;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.service.GstnApi;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Vishal.verma
 *
 */
@Slf4j
@Component("Gstr2aEcomEcomaDataParserImpl")
public class Gstr2aEcomEcomaDataParserImpl implements Gstr2aEcomEcomaDataParser {

	@Autowired
	private GstnApi gstnapi;
	
	@Autowired
	@Qualifier("DefaultInwardTransDocKeyGenerator")
	private DocKeyGenerator<InwardTransDocument, String> docKeyGenerator;
	
	private static final String DOC_KEY_JOINER = "|";

	@Override
	public List<GetGstr2aStagingEcomInvoicesHeaderEntity> parseEcomData(Gstr1GetInvoicesReqDto dto, String apiResp, String type,
			Long batchId, LocalDateTime now) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		List<GetGstr2aStagingEcomInvoicesHeaderEntity> invoiceList = new ArrayList<>();

		JsonArray respObject = null;
		// try {
		respObject = (new JsonParser()).parse(apiResp).getAsJsonObject().get(APIConstants.ECOM).getAsJsonArray();

		Type listType = new TypeToken<List<B2BInvoices>>() {
		}.getType();
		List<B2BInvoices> baseEntity = gson.fromJson(respObject, listType);
		for (B2BInvoices eachInv : baseEntity) {
			for (B2BInvoiceData eachInvData : eachInv.getB2bInvoiceData()) {
				List<GetGstr2aStagingEcomInvoicesItemEntity> itemList = new ArrayList<>();
				GetGstr2aStagingEcomInvoicesHeaderEntity invoice = setInvoiceData(eachInv, eachInvData, dto, batchId);
				invoiceList.add(invoice);
				BigDecimal igst = BigDecimal.ZERO;
				BigDecimal cgst = BigDecimal.ZERO;
				BigDecimal sgst = BigDecimal.ZERO;
				BigDecimal cess = BigDecimal.ZERO;
				BigDecimal taxable = BigDecimal.ZERO;
				for (B2bLineItem lineItem : eachInvData.getLineItems()) {
					GetGstr2aStagingEcomInvoicesItemEntity item = setItemData(lineItem);
					if (dto.getReturnPeriod() != null && dto.getReturnPeriod().length() > 0) {
						item.setDerReturnPeriod(GenUtil.convertTaxPeriodToInt(dto.getReturnPeriod()));
					}

					itemList.add(item);

					B2bLineItemDetail detail = lineItem.getItemDetail();
					BigDecimal detailsIgst = detail.getIgstAmount() != null ? detail.getIgstAmount() : BigDecimal.ZERO;
					BigDecimal detailsCgst = detail.getCgstAmount() != null ? detail.getCgstAmount() : BigDecimal.ZERO;
					BigDecimal detailsSgst = detail.getSgstAmount() != null ? detail.getSgstAmount() : BigDecimal.ZERO;
					BigDecimal detailsCess = detail.getCessAmount() != null ? detail.getCessAmount() : BigDecimal.ZERO;
					BigDecimal detailsTaxable = detail.getTaxableValue() != null ? detail.getTaxableValue()
							: BigDecimal.ZERO;

					igst = igst.add(detailsIgst);
					cgst = cgst.add(detailsCgst);
					sgst = sgst.add(detailsSgst);
					cess = cess.add(detailsCess);
					taxable = taxable.add(detailsTaxable);
				}
				/**
				 * setting summary rate info at invoice level.
				 */
				invoice.setIgstAmt(igst);
				invoice.setCgstAmt(cgst);
				invoice.setSgstAmt(sgst);
				invoice.setCessAmt(cess);
				invoice.setTaxable(taxable);
				invoice.setLineItems(itemList);
				invoice.setAction(APIConstants.N);
				invoice.setCreatedBy("SYSTEM");
				LocalDateTime convertNow = EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now());
				invoice.setCreatedOn(convertNow);
				itemList.forEach(item -> {
					item.setHeader(invoice);
				});

			}
		}
		/*
		 * //} catch (Exception ex) { String msg =
		 * "failed to parse Gstr2a B2B/B2BA response"; LOGGER.error(msg, ex); }
		 */ return invoiceList;
	}

	private GetGstr2aStagingEcomInvoicesHeaderEntity setInvoiceData(B2BInvoices eachInv, B2BInvoiceData eachInvData,
			Gstr1GetInvoicesReqDto dto, Long batchId) {
		GetGstr2aStagingEcomInvoicesHeaderEntity invoice = new GetGstr2aStagingEcomInvoicesHeaderEntity();
		String cfs = eachInv.getCfs();
		String sgstin = eachInv.getCgstin();
		String checkSum = eachInvData.getCheckSum();
		String invoiceNumber = eachInvData.getInvoiceNumber();
		String invDate = eachInvData.getInvoiceDate();
		// String origInvNumber = eachInvData.getOrigInvNumber();
		String origInvDate = eachInvData.getOrigInvDate();
		BigDecimal invoiceValue = eachInvData.getInvoiceValue();
		String pos = eachInvData.getPos();
		String reverseCharge = eachInvData.getReverseCharge();
		String invoiceType = eachInvData.getInvoiceType();
		BigDecimal diffPercent = eachInvData.getDiffPercent();
		
		/**
		 * setting B2BInvoices
		 */
		if (APIConstants.SUBMITTED.equalsIgnoreCase(cfs)) {
			cfs = APIConstants.Y;
		} else if (APIConstants.NOT_SUBMITTED.equalsIgnoreCase(cfs)) {
			cfs = APIConstants.N;
		}
		invoice.setCfsGstr1(cfs);
		invoice.setSgstin(sgstin); // output

		/**
		 * setting B2BInvoiceData
		 */
		invoice.setChkSum(checkSum);
		invoice.setInvNum(invoiceNumber);
		if (invDate != null && invDate.trim().length() > 0) {
			invoice.setInvDate(LocalDate.parse(invDate, DateUtil.SUPPORTED_DATE_FORMAT2));
		} else {
			invoice.setInvDate(null);
		}
		// invoice.setOrigInvNum(origInvNumber);

		if (origInvDate != null && origInvDate.trim().length() > 0) {
			// invoice.setOrigInvDate(LocalDate.parse(origInvDate,
			// DateUtil.SUPPORTED_DATE_FORMAT2));
		} else {
			// invoice.setOrigInvDate(null);
		}

		invoice.setInvValue(invoiceValue);

		if (pos != null && pos.trim().length() > 0) {
			invoice.setPos(pos);
		} else {
			invoice.setPos(null);
		}
		invoice.setRchrg(reverseCharge);
		invoice.setInvType(invoiceType);
		invoice.setDiffPercentage(diffPercent);
		/*invoice.setIrnSrcType(sourceType);
		invoice.setIrnNum(irn);
		if (!Strings.isNullOrEmpty(irnDate)) {
			invoice.setIrnGenDate(LocalDate.parse(irnDate, DateUtil.SUPPORTED_DATE_FORMAT2));
		}*/

		invoice.setEcomBatchIdGstr2a(batchId);
		invoice.setCgstin(dto.getGstin()); // input
		invoice.setReturnPeriod(dto.getReturnPeriod());
		//adding inv_key
		String docType = invoiceType == "R" ? "INV"
				: (invoiceType == "C" ? "CR"
						: (invoiceType == "D" ? "DR"
								: (invoiceType == "B" ? "BOS"
										: invoiceType)));
		String fy = GenUtil.getFinYear(LocalDate.parse(invDate, DateUtil.SUPPORTED_DATE_FORMAT2));
		
		InwardTransDocument inwardDoc = new InwardTransDocument();
		inwardDoc.setFinYear(fy);
		inwardDoc.setDocNo(invoiceNumber);
		inwardDoc.setDocType(docType);
		inwardDoc.setSgstin(sgstin);//output
		inwardDoc.setCgstin(dto.getGstin());//input
		
		String generateKey = docKeyGenerator.generateKey(inwardDoc);
		//String generateKey = docKeyGenerator.generateKey(sgstin, invoiceNumber, fy, docType);
		invoice.setInvKey(generateKey);
		
		invoice.setLnkingDocKey(deriveLinkingKey(
				LocalDate.parse(invDate, DateUtil.SUPPORTED_DATE_FORMAT2),
				dto.getGstin(), sgstin, "INV", invoiceNumber));
		
		if (dto.getReturnPeriod() != null && dto.getReturnPeriod().length() > 0) {
			invoice.setDerReturnPeriod(GenUtil.convertTaxPeriodToInt(dto.getReturnPeriod()));
		}
		invoice.setDelete(false);

		if (dto.getGstin() != null && dto.getGstin().trim().length() == 15) {
			// invoice.setCgstinPan(dto.getGstin().trim().substring(2, 12));
		}
		if (sgstin != null && sgstin.trim().length() == 15) {
			// invoice.setSgstinPan(sgstin.trim().substring(2, 12));
		}

		if (gstnapi.isDelinkingEligible(APIConstants.GSTR2A.toUpperCase())) {
			// version 2.0 changes here...
			String cfsGstr3b = eachInv.getCfsGstr3b();
			String cancelDate = eachInv.getCancelDate();
			String filedDate = eachInv.getFiledDate();
			String filedPeriod = eachInv.getFiledPeriod();
			String orgInvPeriod = eachInvData.getOrgInvPeriod();// need to check it
			String orgInvType = eachInvData.getOrgInvType();// need to check it

			invoice.setCfsGstr3B(cfsGstr3b);
			if (cancelDate != null) {
				invoice.setCancelDate(LocalDate.parse(cancelDate, DateUtil.SUPPORTED_DATE_FORMAT7));
			}
			if (filedDate != null) {
				invoice.setFileDate(LocalDate.parse(filedDate, DateUtil.SUPPORTED_DATE_FORMAT7));
			}
			invoice.setFilePeriod(filedPeriod);
			/*invoice.setOrgInvAmdPeriod(orgInvPeriod);
			invoice.setOrgInvAmdType(orgInvType);*/
		}
		return invoice;
	}

	private GetGstr2aStagingEcomInvoicesItemEntity setItemData(B2bLineItem lineItem) {
		GetGstr2aStagingEcomInvoicesItemEntity item = new GetGstr2aStagingEcomInvoicesItemEntity();
		item.setItmNum(lineItem.getLineNumber());
		B2bLineItemDetail detail = lineItem.getItemDetail();
		item.setTaxRate(detail.getRate());
		item.setTaxableValue(detail.getTaxableValue());
		item.setIgstAmt(detail.getIgstAmount());
		item.setCgstAmt(detail.getCgstAmount());
		item.setSgstAmt(detail.getSgstAmount());
		item.setCessAmt(detail.getCessAmount());
		return item;
	}

	@Override
	public List<GetGstr2aStagingEcomaInvoicesHeaderEntity> parseEcomaData(Gstr1GetInvoicesReqDto dto, String apiResp,
			String type, long batchId) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		List<GetGstr2aStagingEcomaInvoicesHeaderEntity> invoiceList = new ArrayList<>();

		JsonArray respObject = null;
		// try {
		respObject = (new JsonParser()).parse(apiResp).getAsJsonObject().get(APIConstants.B2BA).getAsJsonArray();

		Type listType = new TypeToken<List<B2BInvoices>>() {
		}.getType();
		List<B2BInvoices> baseEntity = gson.fromJson(respObject, listType);
		for (B2BInvoices eachInv : baseEntity) {
			for (B2BInvoiceData eachInvData : eachInv.getB2bInvoiceData()) {
				List<GetGstr2aStagingEcomaInvoicesItemEntity> itemList = new ArrayList<>();
				GetGstr2aStagingEcomaInvoicesHeaderEntity invoice = setInvoiceB2baData(eachInv, eachInvData, dto, batchId);
				invoiceList.add(invoice);
				BigDecimal igst = BigDecimal.ZERO;
				BigDecimal cgst = BigDecimal.ZERO;
				BigDecimal sgst = BigDecimal.ZERO;
				BigDecimal cess = BigDecimal.ZERO;
				BigDecimal taxable = BigDecimal.ZERO;
				for (B2bLineItem lineItem : eachInvData.getLineItems()) {
					GetGstr2aStagingEcomaInvoicesItemEntity item = setItemEcomaData(lineItem);
					if (dto.getReturnPeriod() != null && dto.getReturnPeriod().length() > 0) {
						item.setDerReturnPeriod(GenUtil.convertTaxPeriodToInt(dto.getReturnPeriod()));
					}
					itemList.add(item);
					B2bLineItemDetail detail = lineItem.getItemDetail();
					BigDecimal detailsIgst = detail.getIgstAmount() != null ? detail.getIgstAmount() : BigDecimal.ZERO;
					BigDecimal detailsCgst = detail.getCgstAmount() != null ? detail.getCgstAmount() : BigDecimal.ZERO;
					BigDecimal detailsSgst = detail.getSgstAmount() != null ? detail.getSgstAmount() : BigDecimal.ZERO;
					BigDecimal detailsCess = detail.getCessAmount() != null ? detail.getCessAmount() : BigDecimal.ZERO;
					BigDecimal detailsTaxable = detail.getTaxableValue() != null ? detail.getTaxableValue()
							: BigDecimal.ZERO;

					igst = igst.add(detailsIgst);
					cgst = cgst.add(detailsCgst);
					sgst = sgst.add(detailsSgst);
					cess = cess.add(detailsCess);
					taxable = taxable.add(detailsTaxable);
				}
				/**
				 * setting summary rate info at invoice level.
				 */
				invoice.setIgstAmt(igst);
				invoice.setCgstAmt(cgst);
				invoice.setSgstAmt(sgst);
				invoice.setCessAmt(cess);
				invoice.setTaxable(taxable);
				invoice.setLineItems(itemList);
				invoice.setAction(APIConstants.N);
				LocalDateTime convertNow = EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now());
				invoice.setCreatedOn(convertNow);
				invoice.setCreatedBy("SYSTEM");
				itemList.forEach(item -> {
					item.setHeader(invoice);
				});

			}
		}
		/*
		 * } catch (Exception ex) { String msg =
		 * "failed to parse Gstr2a B2B/B2BA response"; LOGGER.error(msg, ex); }
		 */
		return invoiceList;
	}

	private GetGstr2aStagingEcomaInvoicesItemEntity setItemEcomaData(B2bLineItem lineItem) {
		GetGstr2aStagingEcomaInvoicesItemEntity item = new GetGstr2aStagingEcomaInvoicesItemEntity();
		item.setItmNum(lineItem.getLineNumber());
		B2bLineItemDetail detail = lineItem.getItemDetail();
		item.setTaxRate(detail.getRate());
		item.setTaxableValue(detail.getTaxableValue());
		item.setIgstAmt(detail.getIgstAmount());
		item.setCgstAmt(detail.getCgstAmount());
		item.setSgstAmt(detail.getSgstAmount());
		item.setCessAmt(detail.getCessAmount());
		return item;
	}

	private GetGstr2aStagingEcomaInvoicesHeaderEntity setInvoiceB2baData(B2BInvoices eachInv, B2BInvoiceData eachInvData,
			Gstr1GetInvoicesReqDto dto, long batchId) {

		GetGstr2aStagingEcomaInvoicesHeaderEntity invoice = new GetGstr2aStagingEcomaInvoicesHeaderEntity();
		String cfs = eachInv.getCfs();
		String sgstin = eachInv.getCgstin();
		String checkSum = eachInvData.getCheckSum();
		String invoiceNumber = eachInvData.getInvoiceNumber();
		String invDate = eachInvData.getInvoiceDate();
		String origInvNumber = eachInvData.getOrigInvNumber();
		String origInvDate = eachInvData.getOrigInvDate();
		BigDecimal invoiceValue = eachInvData.getInvoiceValue();
		String pos = eachInvData.getPos();
		String reverseCharge = eachInvData.getReverseCharge();
		String invoiceType = eachInvData.getInvoiceType();
		BigDecimal diffPercent = eachInvData.getDiffPercent();
		/**
		 * setting B2BInvoices
		 */
		invoice.setCfsGstr1(cfs);
		invoice.setSgstin(sgstin); // output

		/**
		 * setting B2BInvoiceData
		 */
		invoice.setChkSum(checkSum);
		invoice.setInvNum(invoiceNumber);
		if (invDate != null && invDate.trim().length() > 0) {
			invoice.setInvDate(LocalDate.parse(invDate, DateUtil.SUPPORTED_DATE_FORMAT2));
		} else {
			invoice.setInvDate(null);
		}
		invoice.setOrigInvNum(origInvNumber);

		if (origInvDate != null && origInvDate.trim().length() > 0) {
			invoice.setOrigInvDate(LocalDate.parse(origInvDate, DateUtil.SUPPORTED_DATE_FORMAT2));
		} else {
			invoice.setOrigInvDate(null);
		}

		invoice.setInvValue(invoiceValue);

		if (pos != null && pos.trim().length() > 0) {
			invoice.setPos(pos);
		} else {
			invoice.setPos(null);
		}
		invoice.setRchrg(reverseCharge);
		invoice.setInvType(invoiceType);
		invoice.setDiffPercentage(diffPercent);

		invoice.setEcomBatchIdGstr2a(batchId);
		invoice.setCgstin(dto.getGstin()); // input
		invoice.setReturnPeriod(dto.getReturnPeriod());
		if (dto.getReturnPeriod() != null && dto.getReturnPeriod().length() > 0) {
			invoice.setDerReturnPeriod(GenUtil.convertTaxPeriodToInt(dto.getReturnPeriod()));
		}
		invoice.setDelete(false);
		//adding inv_key
		String docType = invoiceType == "R" ? "INV"
				: (invoiceType == "C" ? "CR"
						: (invoiceType == "D" ? "DR"
								: (invoiceType == "B" ? "BOS"
										: invoiceType)));
		String fy = GenUtil.getFinYear(LocalDate.parse(invDate, DateUtil.SUPPORTED_DATE_FORMAT2));
		
		InwardTransDocument inwardDoc = new InwardTransDocument();
		inwardDoc.setFinYear(fy);
		inwardDoc.setDocNo(invoiceNumber);
		inwardDoc.setDocType(docType);
		inwardDoc.setSgstin(sgstin);//output
		inwardDoc.setCgstin(dto.getGstin());//input
		
		String generateKey = docKeyGenerator.generateKey(inwardDoc);
		
		
		//String generateKey = docKeyGenerator.generateKey(sgstin, invoiceNumber, fy, docType);
		invoice.setInvKey(generateKey);
		
		invoice.setLnkingDocKey(deriveLinkingKey(
				LocalDate.parse(invDate, DateUtil.SUPPORTED_DATE_FORMAT2),
				dto.getGstin(), sgstin, "RNV", invoiceNumber));
		
		
		if (dto.getGstin() != null && dto.getGstin().trim().length() == 15) {
			// invoice.setCgstinPan(dto.getGstin().trim().substring(2, 12));
		}
		if (sgstin != null && sgstin.trim().length() == 15) {
			// invoice.setSgstinPan(sgstin.trim().substring(2, 12));
		}
		if (gstnapi.isDelinkingEligible(APIConstants.GSTR2A.toUpperCase())) {
			// version 2.0 changes here...
			String cfsGstr3b = eachInv.getCfsGstr3b();
			String cancelDate = eachInv.getCancelDate();
			String filedDate = eachInv.getFiledDate();
			String filedPeriod = eachInv.getFiledPeriod();
			String orgInvPeriod = eachInvData.getOrgInvPeriod();// need to check it
			String orgInvType = eachInvData.getOrgInvType();// need to check it 

			invoice.setCfsGstr3B(cfsGstr3b);
			if (cancelDate != null) {
				invoice.setCancelDate(LocalDate.parse(cancelDate, DateUtil.SUPPORTED_DATE_FORMAT7));
			}
			if (filedDate != null) {
				invoice.setFileDate(LocalDate.parse(filedDate, DateUtil.SUPPORTED_DATE_FORMAT7));
			}
			invoice.setFilePeriod(filedPeriod);
			/*invoice.setOrgInvAmdPeriod(orgInvPeriod);  
			invoice.setOrgInvAmdType(orgInvType);*/
		}
		return invoice;

	}
	
	private String deriveLinkingKey(LocalDate date, String cgstin,
			String sgstin, String docType, String documentNumber) {
		String finYear = GenUtil.getFinYear(date);

		return new StringJoiner(DOC_KEY_JOINER).add(finYear).add(cgstin)
				.add(sgstin).add(docType).add(documentNumber).toString();
	}
}