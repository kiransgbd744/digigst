/**
 * 
 */
package com.ey.advisory.app.services.jobs.anx1;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.data.entities.simplified.client.GetAnx1DeInvoicesHeaderEntity;
import com.ey.advisory.app.data.entities.simplified.client.GetAnx1DeInvoicesItemEntity;
import com.ey.advisory.app.docs.dto.anx1.Anx1DeemadExportsData;
import com.ey.advisory.app.docs.dto.anx1.Anx1DeemadExportsDocumentData;
import com.ey.advisory.app.docs.dto.anx1.Anx1DeemadExportsItemDetails;
import com.ey.advisory.app.services.docs.DefaultOutwardTransDocKeyGenerator;
import com.ey.advisory.common.DateUtil;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIException;
import com.ey.advisory.core.dto.Anx1GetInvoicesReqDto;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Hemasundar.J
 *
 */
@Service("Anx1DeDataParserImpl")
@Slf4j
public class Anx1DeDataParserImpl implements Anx1DeDataParser {

	@Autowired
	@Qualifier("DefaultOutwardTransDocKeyGenerator")
	private DefaultOutwardTransDocKeyGenerator outwardDocKey;

	@Override
	public Set<GetAnx1DeInvoicesHeaderEntity> parseDeData(
			Anx1GetInvoicesReqDto dto, String apiResp) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		Set<GetAnx1DeInvoicesHeaderEntity> invoiceList = new HashSet<>();

		JsonArray respObject = null;
		try {
			respObject = (new JsonParser()).parse(apiResp).getAsJsonObject()
					.get(APIConstants.DE).getAsJsonArray();

			Type listType = new TypeToken<List<Anx1DeemadExportsData>>() {
			}.getType();
			List<Anx1DeemadExportsData> baseEntity = gson.fromJson(respObject,
					listType);
			for (Anx1DeemadExportsData eachInv : baseEntity) {
				for (Anx1DeemadExportsDocumentData eachInvData : eachInv
						.getDocs()) {
					List<GetAnx1DeInvoicesItemEntity> itemList = new ArrayList<>();
					GetAnx1DeInvoicesHeaderEntity invoice = setInvoiceData(
							eachInv, eachInvData, dto, dto.getBatchId());
					invoiceList.add(invoice);
					BigDecimal igst = BigDecimal.ZERO;
					BigDecimal cgst = BigDecimal.ZERO;
					BigDecimal sgst = BigDecimal.ZERO;
					BigDecimal cess = BigDecimal.ZERO;
					BigDecimal taxable = BigDecimal.ZERO;
					for (Anx1DeemadExportsItemDetails lineItem : eachInvData
							.getItems()) {

						itemList.add(setItemData(lineItem));

						if (lineItem.getIgstAmount() != null) {
							igst = igst.add(lineItem.getIgstAmount());
						}
						if (lineItem.getCgstAmount() != null) {
							cgst = cgst.add(lineItem.getCgstAmount());
						}
						if (lineItem.getSgstAmount() != null) {
							sgst = sgst.add(lineItem.getSgstAmount());
						}
						if (lineItem.getCessAmount() != null) {
							cess = cess.add(lineItem.getCessAmount());
						}
						if (lineItem.getTaxableValue() != null) {
							taxable = taxable.add(lineItem.getTaxableValue());
						}
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
					itemList.forEach(item -> {
						item.setHeader(invoice);
					});

				}
			}
		} catch (Exception ex) {
			String msg = "failed to parse Anx1 DE response";
			LOGGER.error(msg, ex);
			throw new APIException(msg);
		}
		return invoiceList;
	}

	private GetAnx1DeInvoicesHeaderEntity setInvoiceData(
			Anx1DeemadExportsData eachInv,
			Anx1DeemadExportsDocumentData eachInvData,
			Anx1GetInvoicesReqDto dto, Long batchId) {
		GetAnx1DeInvoicesHeaderEntity invoice = new GetAnx1DeInvoicesHeaderEntity();

		String ctin = eachInv.getCgstin();

		String docType = eachInvData.getDoctyp();
		String pos = eachInvData.getPos();
		String rfndelg = eachInvData.getRfndelg();
		String sec7 = eachInvData.getSec7act();
		BigDecimal diffPercent = eachInvData.getDiffprcnt();
		String clmRfnd = eachInvData.getClmrfnd();
		String checkSum = eachInvData.getCheckSum();
		String action = eachInvData.getAction();

		String docDate = eachInvData.getDoc().getDocDate();
		String docNum = eachInvData.getDoc().getDocNum();
		String finYear = GenUtil.getFinYear(
				LocalDate.parse(docDate, DateUtil.SUPPORTED_DATE_FORMAT2));
		BigDecimal docVal = eachInvData.getDoc().getDocVal();

		/**
		 * setting B2BInvoices
		 */
		invoice.setCtin(ctin);

		/**
		 * setting B2BInvoiceData
		 */
		invoice.setDocType(docType);
		invoice.setChkSum(checkSum);
		invoice.setDiffPercent(diffPercent);
		/*
		 * if (new BigDecimal(0.65).equals(diffPercent)) {
		 * invoice.setDiffPercent(APIConstants.Y); } else {
		 * invoice.setDiffPercent(APIConstants.N); }
		 */
		invoice.setPos(pos);
		invoice.setAction(action);
		invoice.setSec7Act(sec7);
		invoice.setRfndElg(rfndelg);
		invoice.setClmRfnd(clmRfnd);
		invoice.setDocNum(docNum);
		if (docDate != null) {
			invoice.setDocDate(
					LocalDate.parse(docDate, DateUtil.SUPPORTED_DATE_FORMAT2));
		}
		invoice.setDocAmt(docVal);

		invoice.setDeBatchId(batchId);
		invoice.setsGstin(dto.getGstin());
		invoice.setTaxPeriod(dto.getReturnPeriod());
		invoice.setDelete(false);

		setDockey(invoice, dto.getGstin(), docNum, finYear);

		if (ctin != null && ctin.trim().length() == 15) {
			invoice.setCgstinPan(ctin.trim().substring(2, 12));
		}
		if (dto.getGstin() != null && dto.getGstin().trim().length() == 15) {
			invoice.setSgstinPan(dto.getGstin().trim().substring(2, 12));
		}
		invoice.setDerTaxPeriod(
				GenUtil.convertTaxPeriodToInt(dto.getReturnPeriod()));

		return invoice;
	}

	private GetAnx1DeInvoicesItemEntity setItemData(
			Anx1DeemadExportsItemDetails lineItem) {
		GetAnx1DeInvoicesItemEntity item = new GetAnx1DeInvoicesItemEntity();
		item.setHsn(Integer.parseInt(lineItem.getHsn()));
		item.setTaxRate(lineItem.getRate());
		item.setTaxableValue(lineItem.getTaxableValue());
		item.setIgstAmt(lineItem.getIgstAmount());
		item.setCgstAmt(lineItem.getCgstAmount());
		item.setSgstAmt(lineItem.getSgstAmount());
		item.setCessAmt(lineItem.getCessAmount());
		return item;
	}

	private void setDockey(GetAnx1DeInvoicesHeaderEntity invoice, String gstin,
			String docNo, String finYear) {

		OutwardTransDocument doc = new OutwardTransDocument();
		doc.setSgstin(gstin);
		String docType = null;
		if (invoice.getDocType() != null) {

			if (invoice.getDocType().equalsIgnoreCase(APIConstants.C)) {
				docType = GSTConstants.CR;
			} else if (invoice.getDocType().equalsIgnoreCase(APIConstants.D)) {
				docType = GSTConstants.DR;
			} else {
				docType = GSTConstants.INV;
			}

		}
		doc.setDocType(docType);
		doc.setDocNo(docNo);
		doc.setFinYear(finYear);

		invoice.setDocKey(outwardDocKey.generateKey(doc));

	}
}
