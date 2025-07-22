/**
 * 
 */
package com.ey.advisory.app.services.jobs.anx1;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.simplified.client.GetAnx1RevInvoicesHeaderEntity;
import com.ey.advisory.app.data.entities.simplified.client.GetAnx1RevInvoicesItemEntity;
import com.ey.advisory.app.docs.dto.anx1.Anx1RevData;
import com.ey.advisory.app.docs.dto.anx1.Anx1RevDocumentData;
import com.ey.advisory.app.docs.dto.anx1.Anx1RevItemDetails;
import com.ey.advisory.app.services.docs.gstr2.DefaultInwardTransDocKeyGenerator;
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
@Service("anx1RevDataParserImpl")
@Slf4j
public class Anx1RevDataParserImpl implements Anx1RevDataParser {

	@Autowired
	@Qualifier("DefaultInwardTransDocKeyGenerator")
	private DefaultInwardTransDocKeyGenerator inwardDocKey;

	@Override
	public Set<GetAnx1RevInvoicesHeaderEntity> parseRevData(
			Anx1GetInvoicesReqDto dto, String apiResp) {
		// This needs to be changed according to ANX2
		Gson gson = GsonUtil.newSAPGsonInstance();
		Set<GetAnx1RevInvoicesHeaderEntity> invoiceList = new HashSet<>();

		JsonArray respObject = null;
		try {
			respObject = (new JsonParser()).parse(apiResp).getAsJsonObject()
					.get(APIConstants.REV).getAsJsonArray();

			Type listType = new TypeToken<List<Anx1RevData>>() {
			}.getType();
			List<Anx1RevData> baseEntity = gson.fromJson(respObject, listType);
			for (Anx1RevData eachInv : baseEntity) {
				for (Anx1RevDocumentData eachInvData : eachInv.getDocs()) {
					List<GetAnx1RevInvoicesItemEntity> itemList = new ArrayList<>();
					GetAnx1RevInvoicesHeaderEntity invoice = setInvoiceData(
							eachInv, eachInvData, dto, dto.getBatchId());
					invoiceList.add(invoice);
					BigDecimal igst = BigDecimal.ZERO;
					BigDecimal cgst = BigDecimal.ZERO;
					BigDecimal sgst = BigDecimal.ZERO;
					BigDecimal cess = BigDecimal.ZERO;
					BigDecimal taxable = BigDecimal.ZERO;
					for (Anx1RevItemDetails lineItem : eachInvData.getItems()) {

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
			String msg = "failed to parse Anx1 REV response";
			LOGGER.error(msg, ex);
			throw new APIException(msg);
		}
		return invoiceList;
	}

	private GetAnx1RevInvoicesHeaderEntity setInvoiceData(Anx1RevData eachInv,
			Anx1RevDocumentData eachInvData, Anx1GetInvoicesReqDto dto,
			Long batchId) {
		GetAnx1RevInvoicesHeaderEntity invoice = new GetAnx1RevInvoicesHeaderEntity();

		String ctin = eachInv.getCgstin();
		String pos = eachInvData.getPos();
		String sec7 = eachInvData.getSec7act();
		BigDecimal diffPercent = eachInvData.getDiffprcntGet();
		String checkSum = eachInvData.getCheckSum();

		/**
		 * setting B2BInvoices
		 */
		invoice.setCtin(ctin);

		/**
		 * setting B2BInvoiceData
		 */
		invoice.setChkSum(checkSum);
		invoice.setDiffPercentage(diffPercent);
		invoice.setPos(pos);
		invoice.setSec7(sec7);

		invoice.setRevBatchId(batchId);
		invoice.setsGstin(dto.getGstin());
		invoice.setTaxPeriod(dto.getReturnPeriod());
		invoice.setDelete(false);

		// setDockey(invoice, dto.getGstin());

		invoice.setDerTaxPeriod(
				GenUtil.convertTaxPeriodToInt(dto.getReturnPeriod()));
		return invoice;
	}

	private GetAnx1RevInvoicesItemEntity setItemData(
			Anx1RevItemDetails lineItem) {
		GetAnx1RevInvoicesItemEntity item = new GetAnx1RevInvoicesItemEntity();
		item.setHsn(Integer.parseInt(lineItem.getHsn()));
		item.setTaxRate(lineItem.getRate());
		item.setTaxableValue(lineItem.getTaxableValue());
		item.setIgstAmt(lineItem.getIgstAmount());
		item.setCgstAmt(lineItem.getCgstAmount());
		item.setSgstAmt(lineItem.getSgstAmount());
		item.setCessAmt(lineItem.getCessAmount());
		return item;
	}

	/*
	 * private void setDockey(GetAnx1B2cInvoicesHeaderEntity invoice, String
	 * gstin) {
	 * 
	 * InwardTransDocument doc = new InwardTransDocument();
	 * doc.setSgstin(gstin); String docType = null; if (invoice.getDocType() !=
	 * null) {
	 * 
	 * if (invoice.getDocType().equalsIgnoreCase(APIConstants.C)) { docType =
	 * GSTConstants.CR; } else if
	 * (invoice.getDocType().equalsIgnoreCase(APIConstants.D)) { docType =
	 * GSTConstants.DR; } else { docType = GSTConstants.INV; }
	 * 
	 * } doc.setDocType(docType);
	 * 
	 * invoice.setDocKey(inwardDocKey.generateKey(doc));
	 * 
	 * }
	 */
}
