
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

import com.ey.advisory.app.data.entities.simplified.client.GetAnx1ImpsInvoicesHeaderEntity;
import com.ey.advisory.app.data.entities.simplified.client.GetAnx1ImpsInvoicesItemEntity;
import com.ey.advisory.app.docs.dto.anx1.Anx1ImpsData;
import com.ey.advisory.app.docs.dto.anx1.Anx1ImpsDocumentData;
import com.ey.advisory.app.docs.dto.anx1.Anx1ImpsItemDetails;
import com.ey.advisory.app.services.docs.gstr2.DefaultInwardTransDocKeyGenerator;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.GenUtil;
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
@Service("Anx1ImpsDataParserImpl")
@Slf4j
public class Anx1ImpsDataParserImpl implements Anx1ImpsDataParser {

	@Autowired
	@Qualifier("DefaultInwardTransDocKeyGenerator")
	private DefaultInwardTransDocKeyGenerator inwardDocKey;

	@Override
	public Set<GetAnx1ImpsInvoicesHeaderEntity> parseImpsData(
			Anx1GetInvoicesReqDto dto, String apiResp) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		Set<GetAnx1ImpsInvoicesHeaderEntity> invoiceList = new HashSet<>();

		JsonArray respObject = null;
		try {
			respObject = (new JsonParser()).parse(apiResp).getAsJsonObject()
					.get(APIConstants.IMPS).getAsJsonArray();

			Type listType = new TypeToken<List<Anx1ImpsData>>() {
			}.getType();
			List<Anx1ImpsDocumentData> baseEntity = gson.fromJson(respObject,
					listType);
			// for (Anx1ImpsData eachInv : baseEntity) {
			for (Anx1ImpsDocumentData eachInvData : baseEntity) {
				List<GetAnx1ImpsInvoicesItemEntity> itemList = new ArrayList<>();
				GetAnx1ImpsInvoicesHeaderEntity invoice = setInvoiceData(
						eachInvData, dto, dto.getBatchId());
				invoiceList.add(invoice);
				BigDecimal igst = BigDecimal.ZERO;
				BigDecimal cess = BigDecimal.ZERO;
				BigDecimal taxable = BigDecimal.ZERO;
				for (Anx1ImpsItemDetails lineItem : eachInvData.getItems()) {

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
			// }
		} catch (Exception ex) {
			String msg = "failed to parse Anx1 IMPS response";
			LOGGER.error(msg, ex);
			throw new APIException(msg);
		}
		return invoiceList;
	}

	private GetAnx1ImpsInvoicesHeaderEntity setInvoiceData(
			Anx1ImpsDocumentData eachInvData, Anx1GetInvoicesReqDto dto,
			Long batchId) {
		GetAnx1ImpsInvoicesHeaderEntity invoice = new GetAnx1ImpsInvoicesHeaderEntity();

		String docType = eachInvData.getDoctyp();
		String checkSum = eachInvData.getChksum();
		String pos = eachInvData.getPos();
		String rfndelg = eachInvData.getRfndelg();

		invoice.setChkSum(checkSum);
		invoice.setPos(pos);

		invoice.setImpsBatchId(batchId);
		invoice.setsGstin(dto.getGstin());
		invoice.setTaxPeriod(dto.getReturnPeriod());
		invoice.setRfndelg(rfndelg);
		invoice.setDelete(false);

		// setDockey(invoice, dto.getGstin());

		invoice.setDerTaxPeriod(
				GenUtil.convertTaxPeriodToInt(dto.getReturnPeriod()));
		return invoice;
	}

	private GetAnx1ImpsInvoicesItemEntity setItemData(
			Anx1ImpsItemDetails lineItem) {
		GetAnx1ImpsInvoicesItemEntity item = new GetAnx1ImpsInvoicesItemEntity();
		item.setHsn(Integer.parseInt(lineItem.getHsn()));
		item.setTaxRate(lineItem.getRate());
		item.setTaxableValue(lineItem.getTaxableValue());
		item.setIgstAmt(lineItem.getIgstAmount());
		item.setCessAmt(lineItem.getCessAmount());
		return item;
	}

	/*
	 * private void setDockey(GetAnx1ImpsInvoicesHeaderEntity invoice, String
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
