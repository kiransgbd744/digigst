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

import com.ey.advisory.app.data.entities.simplified.client.GetAnx1B2cInvoicesHeaderEntity;
import com.ey.advisory.app.data.entities.simplified.client.GetAnx1B2cInvoicesItemEntity;
import com.ey.advisory.app.docs.dto.anx1.Anx1B2cData;
import com.ey.advisory.app.docs.dto.anx1.Anx1B2cItemDetails;
import com.ey.advisory.app.services.docs.DefaultOutwardTransDocKeyGenerator;
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
@Service("anx1B2cDataParserImpl")
@Slf4j
public class Anx1B2cDataParserImpl implements Anx1B2cDataParser {

	@Autowired
	@Qualifier("DefaultOutwardTransDocKeyGenerator")
	private DefaultOutwardTransDocKeyGenerator outwardDocKey;

	@Override
	public Set<GetAnx1B2cInvoicesHeaderEntity> parseB2cData(
			Anx1GetInvoicesReqDto dto, String apiResp) {
		// This needs to be changed according to ANX2
		Gson gson = GsonUtil.newSAPGsonInstance();
		Set<GetAnx1B2cInvoicesHeaderEntity> invoiceList = new HashSet<>();

		JsonArray respObject = null;
		try {
			respObject = (new JsonParser()).parse(apiResp).getAsJsonObject()
					.get(APIConstants.B2C).getAsJsonArray();

			Type listType = new TypeToken<List<Anx1B2cData>>() {
			}.getType();
			List<Anx1B2cData> baseEntity = gson.fromJson(respObject, listType);
			for (Anx1B2cData eachInv : baseEntity) {
				List<GetAnx1B2cInvoicesItemEntity> itemList = new ArrayList<>();
				GetAnx1B2cInvoicesHeaderEntity invoice = setInvoiceData(eachInv,
						dto, dto.getBatchId());
				invoiceList.add(invoice);
				BigDecimal igst = BigDecimal.ZERO;
				BigDecimal cgst = BigDecimal.ZERO;
				BigDecimal sgst = BigDecimal.ZERO;
				BigDecimal cess = BigDecimal.ZERO;
				BigDecimal taxable = BigDecimal.ZERO;
				for (Anx1B2cItemDetails lineItem : eachInv.getItems()) {

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
		} catch (Exception ex) {
			String msg = "failed to parse Anx1 B2C response";
			LOGGER.error(msg, ex);
			throw new APIException(msg);
		}
		return invoiceList;
	}

	private GetAnx1B2cInvoicesHeaderEntity setInvoiceData(Anx1B2cData eachInv,
			Anx1GetInvoicesReqDto dto, Long batchId) {
		GetAnx1B2cInvoicesHeaderEntity invoice = new GetAnx1B2cInvoicesHeaderEntity();

		String pos = eachInv.getPos();
		String chkSum = eachInv.getCheckSum();
		String rfndelg = eachInv.getRfndelg();
		String sec7 = eachInv.getSec7act();
		/*
		 * String finYear = GenUtil.getFinYear( LocalDate.parse(docDate,
		 * DateUtil.SUPPORTED_DATE_FORMAT2));
		 */
		BigDecimal diffPercent = eachInv.getDiffprcnt();

		invoice.setChkSum(chkSum);
		invoice.setDiffPercentage(diffPercent);

		invoice.setPos(pos);
		invoice.setSec7(sec7);
		invoice.setRfndElg(rfndelg);

		invoice.setB2cBatchId(batchId);
		invoice.setsGstin(dto.getGstin());
		invoice.setTaxPeriod(dto.getReturnPeriod());
		invoice.setDelete(false);

		// setDockey(invoice, dto.getGstin(), docNum, finYear);

		invoice.setDerTaxPeriod(
				GenUtil.convertTaxPeriodToInt(dto.getReturnPeriod()));
		return invoice;
	}

	private GetAnx1B2cInvoicesItemEntity setItemData(
			Anx1B2cItemDetails lineItem) {
		GetAnx1B2cInvoicesItemEntity item = new GetAnx1B2cInvoicesItemEntity();
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
	 * gstin, String docNo, String finYear) {
	 * 
	 * OutwardTransDocument doc = new OutwardTransDocument();
	 * doc.setSgstin(gstin); String docType = null; if (invoice.getDocType() !=
	 * null) {
	 * 
	 * if (invoice.getDocType().equalsIgnoreCase(APIConstants.C)) { docType =
	 * GSTConstants.CR; } else if
	 * (invoice.getDocType().equalsIgnoreCase(APIConstants.D)) { docType =
	 * GSTConstants.DR; } else { docType = GSTConstants.INV; }
	 * 
	 * } doc.setDocType(docType); doc.setDocNo(docNo); doc.setFinYear(finYear);
	 * 
	 * invoice.setDocKey(outwardDocKey.generateKey(doc));
	 * 
	 * }
	 */
}
