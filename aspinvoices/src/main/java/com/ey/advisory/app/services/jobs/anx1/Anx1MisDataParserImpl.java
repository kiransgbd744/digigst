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

import com.ey.advisory.app.data.entities.client.InwardTransDocument;
import com.ey.advisory.app.data.entities.simplified.client.GetAnx1MisInvoicesHeaderEntity;
import com.ey.advisory.app.data.entities.simplified.client.GetAnx1MisInvoicesItemEntity;
import com.ey.advisory.app.docs.dto.anx1.Anx1MisData;
import com.ey.advisory.app.docs.dto.anx1.Anx1MisDocumentData;
import com.ey.advisory.app.docs.dto.anx1.Anx1MisItemDetails;
import com.ey.advisory.app.services.docs.gstr2.DefaultInwardTransDocKeyGenerator;
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
 * @author Mahesh.Golla
 *
 */
@Service("Anx1MisDataParserImpl")
@Slf4j
public class Anx1MisDataParserImpl implements Anx1MisDataParser {

	@Autowired
	@Qualifier("DefaultInwardTransDocKeyGenerator")
	private DefaultInwardTransDocKeyGenerator inwardDocKey;

	@Override
	public Set<GetAnx1MisInvoicesHeaderEntity> parseMisData(
			Anx1GetInvoicesReqDto dto, String apiResp) {
		// This needs to be changed according to ANX2
		Gson gson = GsonUtil.newSAPGsonInstance();
		Set<GetAnx1MisInvoicesHeaderEntity> invoiceList = new HashSet<>();

		JsonArray respObject = null;
		try {
			respObject = (new JsonParser()).parse(apiResp).getAsJsonObject()
					.get(APIConstants.MIS).getAsJsonArray();

			Type listType = new TypeToken<List<Anx1MisData>>() {
			}.getType();
			List<Anx1MisData> baseEntity = gson.fromJson(respObject, listType);
			for (Anx1MisData eachInv : baseEntity) {
				for (Anx1MisDocumentData eachInvData : eachInv.getDocs()) {
					List<GetAnx1MisInvoicesItemEntity> itemList = new ArrayList<>();
					GetAnx1MisInvoicesHeaderEntity invoice = setInvoiceData(
							eachInv, eachInvData, dto, dto.getBatchId());
					invoiceList.add(invoice);
					BigDecimal igst = BigDecimal.ZERO;
					BigDecimal cgst = BigDecimal.ZERO;
					BigDecimal sgst = BigDecimal.ZERO;
					BigDecimal cess = BigDecimal.ZERO;
					BigDecimal taxable = BigDecimal.ZERO;
					for (Anx1MisItemDetails lineItem : eachInvData.getItems()) {

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
			String msg = "failed to parse Anx1 MIS response";
			LOGGER.error(msg, ex);
			throw new APIException(msg);
		}
		return invoiceList;
	}

	private GetAnx1MisInvoicesHeaderEntity setInvoiceData(Anx1MisData eachInv,
			Anx1MisDocumentData eachInvData, Anx1GetInvoicesReqDto dto,
			Long batchId) {
		GetAnx1MisInvoicesHeaderEntity invoice = new GetAnx1MisInvoicesHeaderEntity();

		String ctin = eachInv.getCgstin();

		String docType = eachInvData.getDoctyp();
		String pos = eachInvData.getPos();
		String sec7 = eachInvData.getSec7act();
		BigDecimal diffPercent = eachInvData.getDiffprcnt();
		String checkSum = eachInvData.getCheckSum();

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
		invoice.setSec7(sec7);

		invoice.setDocNum(docNum);
		if (docDate != null) {
			invoice.setDocDate(
					LocalDate.parse(docDate, DateUtil.SUPPORTED_DATE_FORMAT2));
		}
		invoice.setDocAmt(docVal);

		invoice.setMisBatchId(batchId);
		invoice.setsGstin(dto.getGstin());
		invoice.setTaxPeriod(dto.getReturnPeriod());
		invoice.setDelete(false);
		setDockey(invoice, dto.getGstin(), docNum, finYear);
		invoice.setDerTaxPeriod(
				GenUtil.convertTaxPeriodToInt(dto.getReturnPeriod()));

		return invoice;
	}

	private GetAnx1MisInvoicesItemEntity setItemData(
			Anx1MisItemDetails lineItem) {
		GetAnx1MisInvoicesItemEntity item = new GetAnx1MisInvoicesItemEntity();
		item.setHsn(Integer.parseInt(lineItem.getHsn()));
		item.setTaxRate(lineItem.getRate());
		item.setTaxableValue(lineItem.getTaxableValue());
		item.setIgstAmt(lineItem.getIgstAmount());
		item.setCgstAmt(lineItem.getCgstAmount());
		item.setSgstAmt(lineItem.getSgstAmount());
		item.setCessAmt(lineItem.getCessAmount());
		return item;
	}

	private void setDockey(GetAnx1MisInvoicesHeaderEntity invoice, String gstin,
			String docNo, String finYear) {

		InwardTransDocument doc = new InwardTransDocument();
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

		invoice.setDocKey(inwardDocKey.generateKey(doc));

	}
}
