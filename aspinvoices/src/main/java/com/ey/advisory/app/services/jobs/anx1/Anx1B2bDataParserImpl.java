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
import com.ey.advisory.app.data.entities.simplified.client.GetAnx1B2bInvoicesHeaderEntity;
import com.ey.advisory.app.data.entities.simplified.client.GetAnx1B2bInvoicesItemEntity;
import com.ey.advisory.app.docs.dto.anx1.Anx1B2bData;
import com.ey.advisory.app.docs.dto.anx1.Anx1B2bDocumentData;
import com.ey.advisory.app.docs.dto.anx1.Anx1B2bItemDetails;
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
@Service("anx1B2bDataParserImpl")
@Slf4j
public class Anx1B2bDataParserImpl implements Anx1B2bDataParser {

	@Autowired
	@Qualifier("DefaultOutwardTransDocKeyGenerator")
	private DefaultOutwardTransDocKeyGenerator outwardDocKey;

	@Override
	public Set<GetAnx1B2bInvoicesHeaderEntity> parseB2bData(
			Anx1GetInvoicesReqDto dto, String apiResp) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		Set<GetAnx1B2bInvoicesHeaderEntity> invoiceList = new HashSet<>();

		JsonArray respObject = null;
		try {
			respObject = (new JsonParser()).parse(apiResp).getAsJsonObject()
					.get(APIConstants.B2B).getAsJsonArray();

			Type listType = new TypeToken<List<Anx1B2bData>>() {
			}.getType();
			List<Anx1B2bData> baseEntity = gson.fromJson(respObject, listType);
			for (Anx1B2bData eachInv : baseEntity) {
				for (Anx1B2bDocumentData eachInvData : eachInv.getDocs()) {
					List<GetAnx1B2bInvoicesItemEntity> itemList = new ArrayList<>();
					GetAnx1B2bInvoicesHeaderEntity invoice = setInvoiceData(
							eachInv, eachInvData, dto, dto.getBatchId());
					invoiceList.add(invoice);
					BigDecimal igst = BigDecimal.ZERO;
					BigDecimal cgst = BigDecimal.ZERO;
					BigDecimal sgst = BigDecimal.ZERO;
					BigDecimal cess = BigDecimal.ZERO;
					BigDecimal taxable = BigDecimal.ZERO;
					for (Anx1B2bItemDetails lineItem : eachInvData.getItems()) {

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
			String msg = "failed to parse Anx1 B2B response";
			LOGGER.error(msg, ex);
			throw new APIException(msg);
		}
		return invoiceList;
	}

	private GetAnx1B2bInvoicesHeaderEntity setInvoiceData(Anx1B2bData eachInv,
			Anx1B2bDocumentData eachInvData, Anx1GetInvoicesReqDto dto,
			Long batchId) {
		GetAnx1B2bInvoicesHeaderEntity invoice = new GetAnx1B2bInvoicesHeaderEntity();

		String ctin = eachInv.getCgstin();

		String docType = eachInvData.getDoctyp();
		String pos = eachInvData.getPos();
		String rfndelg = eachInvData.getRfndelg();
		String sec7 = eachInvData.getSec7act();
		BigDecimal diffPercent = eachInvData.getDiffprcntGet();
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
		invoice.setDiffPercentage(diffPercent);
		invoice.setPos(pos);
		invoice.setAction(action);
		invoice.setSec7(sec7);
		invoice.setRfndElg(rfndelg);

		invoice.setDocNum(docNum);
		if (docDate != null) {
			invoice.setDocDate(
					LocalDate.parse(docDate, DateUtil.SUPPORTED_DATE_FORMAT2));
		}
		invoice.setDocAmt(docVal);

		invoice.setB2bBatchId(batchId);
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

	private GetAnx1B2bInvoicesItemEntity setItemData(
			Anx1B2bItemDetails lineItem) {
		GetAnx1B2bInvoicesItemEntity item = new GetAnx1B2bInvoicesItemEntity();
		item.setHsn(lineItem.getHsn() != null
				? Integer.parseInt(lineItem.getHsn()) : null);
		item.setTaxRate(lineItem.getRate());
		item.setTaxableValue(lineItem.getTaxableValue());
		item.setIgstAmt(lineItem.getIgstAmount());
		item.setCgstAmt(lineItem.getCgstAmount());
		item.setSgstAmt(lineItem.getSgstAmount());
		item.setCessAmt(lineItem.getCessAmount());
		return item;
	}

	private void setDockey(GetAnx1B2bInvoicesHeaderEntity invoice, String gstin,
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
