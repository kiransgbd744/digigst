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
import com.ey.advisory.app.data.entities.simplified.client.GetAnx1ExpwpInvoicesHeaderEntity;
import com.ey.advisory.app.data.entities.simplified.client.GetAnx1ExpwpInvoicesItemEntity;
import com.ey.advisory.app.docs.dto.anx1.Anx1ExpwpAndExpwopDocumentData;
import com.ey.advisory.app.docs.dto.anx1.Anx1ExpwpAndExpwopItemDetials;
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
@Service("Anx1ExpwpDataParserImpl")
@Slf4j
public class Anx1ExpwpDataParserImpl implements Anx1ExpwpDataParser {

	@Autowired
	@Qualifier("DefaultOutwardTransDocKeyGenerator")
	private DefaultOutwardTransDocKeyGenerator outwardDocKey;

	@Override
	public Set<GetAnx1ExpwpInvoicesHeaderEntity> parseExpwpData(
			Anx1GetInvoicesReqDto dto, String apiResp) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		Set<GetAnx1ExpwpInvoicesHeaderEntity> invoiceList = new HashSet<>();
		JsonArray respObject = null;
		try {
			respObject = (new JsonParser()).parse(apiResp).getAsJsonObject()
					.get(APIConstants.EXPWP).getAsJsonArray();

			Type listType = new TypeToken<List<Anx1ExpwpAndExpwopDocumentData>>() {
			}.getType();
			List<Anx1ExpwpAndExpwopDocumentData> baseEntity = gson
					.fromJson(respObject, listType);
			for (Anx1ExpwpAndExpwopDocumentData eachInv : baseEntity) {

				List<GetAnx1ExpwpInvoicesItemEntity> itemList = new ArrayList<>();
				GetAnx1ExpwpInvoicesHeaderEntity invoice = setInvoiceData(
						eachInv, dto, dto.getBatchId());
				invoiceList.add(invoice);
				BigDecimal igst = BigDecimal.ZERO;
				BigDecimal cess = BigDecimal.ZERO;
				BigDecimal taxable = BigDecimal.ZERO;
				for (Anx1ExpwpAndExpwopItemDetials lineItem : eachInv
						.getItems()) {
					itemList.add(setItemData(lineItem));

					if (lineItem.getIgstAmount() != null) {
						igst = igst.add(lineItem.getIgstAmount());
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
				invoice.setCessAmt(cess);
				invoice.setTaxable(taxable);
				invoice.setLineItems(itemList);
				itemList.forEach(item -> {
					item.setHeader(invoice);
				});
			}
		} catch (Exception ex) {
			String msg = "failed to parse Anx1 EXPWP response";
			LOGGER.error(msg, ex);
			throw new APIException(msg);
		}
		return invoiceList;
	}

	private GetAnx1ExpwpInvoicesHeaderEntity setInvoiceData(
			Anx1ExpwpAndExpwopDocumentData eachInvData,
			Anx1GetInvoicesReqDto dto, Long batchId) {
		GetAnx1ExpwpInvoicesHeaderEntity invoice = new GetAnx1ExpwpInvoicesHeaderEntity();

		String docType = eachInvData.getDoctyp();
		String rfndelg = eachInvData.getRfndelg();
		String checkSum = eachInvData.getCheckSum();
		String docDate = eachInvData.getDoc() != null
				? eachInvData.getDoc().getDocDate() : null;
		String docNum = eachInvData.getDoc() != null
				? eachInvData.getDoc().getDocNum() : null;
		String finYear = GenUtil.getFinYear(
				LocalDate.parse(docDate, DateUtil.SUPPORTED_DATE_FORMAT2));
		BigDecimal docVal = eachInvData.getDoc() != null
				? eachInvData.getDoc().getDocVal() : null;
		String shipBillNum = eachInvData.getShipBill() != null
				? eachInvData.getShipBill().getShipNum() : null;
		String shipDate = eachInvData.getShipBill() != null
				? eachInvData.getShipBill().getShipDate() : null;
		String portCode = eachInvData.getShipBill() != null
				? eachInvData.getShipBill().getPortCode() : null;

		invoice.setDocType(docType);
		invoice.setChkSum(checkSum);
		invoice.setRfndElg(rfndelg);

		invoice.setDocNum(docNum);
		if (docDate != null) {
			invoice.setDocDate(
					LocalDate.parse(docDate, DateUtil.SUPPORTED_DATE_FORMAT2));
		}
		invoice.setDocAmt(docVal);

		invoice.setShipBillNum(shipBillNum);
		if (shipDate != null) {
			invoice.setShipDate(
					LocalDate.parse(shipDate, DateUtil.SUPPORTED_DATE_FORMAT2));
		}
		invoice.setPortCode(portCode);

		invoice.setExpwpBatchId(batchId);
		invoice.setsGstin(dto.getGstin());
		invoice.setTaxPeriod(dto.getReturnPeriod());
		invoice.setDelete(false);
		setDockey(invoice, dto.getGstin(), docNum, finYear);

		invoice.setDerTaxPeriod(
				GenUtil.convertTaxPeriodToInt(dto.getReturnPeriod()));

		return invoice;
	}

	private GetAnx1ExpwpInvoicesItemEntity setItemData(
			Anx1ExpwpAndExpwopItemDetials lineItem) {
		GetAnx1ExpwpInvoicesItemEntity item = new GetAnx1ExpwpInvoicesItemEntity();
		item.setHsn(Integer.parseInt(lineItem.getHsn()));
		item.setTaxRate(lineItem.getRate());
		item.setTaxableValue(lineItem.getTaxableValue());
		item.setIgstAmt(lineItem.getIgstAmount());
		item.setCessAmt(lineItem.getCessAmount());

		return item;
	}

	private void setDockey(GetAnx1ExpwpInvoicesHeaderEntity invoice,
			String gstin, String docNo, String finYear) {

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
