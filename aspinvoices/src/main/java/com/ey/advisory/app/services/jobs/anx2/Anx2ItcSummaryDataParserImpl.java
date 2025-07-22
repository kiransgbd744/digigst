/**
 * 
 */
package com.ey.advisory.app.services.jobs.anx2;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.simplified.client.GetAnx2ItcSummaryInvoicesEntity;
import com.ey.advisory.app.docs.dto.anx2.Anx2GetInvoicesReqDto;
import com.ey.advisory.app.docs.dto.anx2.Anx2ItcSumryData;
import com.ey.advisory.common.GsonUtil;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Hemasundar.J
 *
 */
@Service("Anx2ItcSummaryDataParserImpl")
@Slf4j
public class Anx2ItcSummaryDataParserImpl implements Anx2ItcSummaryDataParser {

	
	@Override
	public Set<GetAnx2ItcSummaryInvoicesEntity> parseItcSummryData(
			Anx2GetInvoicesReqDto dto, String apiResp, Long batchId) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		Set<GetAnx2ItcSummaryInvoicesEntity> invoiceList = new HashSet<>();
		LOGGER.debug("Parsing Gstn response into Entities");
		JsonArray respObject = null;
		try {
			respObject = (new JsonParser()).parse(apiResp).getAsJsonObject()
					.get("itcsum").getAsJsonArray();

			Type listType = new TypeToken<List<Anx2ItcSumryData>>() {
			}.getType();
			List<Anx2ItcSumryData> baseEntity = gson.fromJson(respObject, listType);
			for (Anx2ItcSumryData eachInv : baseEntity) {
					GetAnx2ItcSummaryInvoicesEntity invoice = setInvoiceData(
							eachInv, dto, batchId);
					invoiceList.add(invoice);
			}
		} catch (Exception ex) {
			String msg = "failed to parse Anx2 ITC Summary response";
			LOGGER.error(msg, ex);
		}
		return invoiceList;
	}

	private GetAnx2ItcSummaryInvoicesEntity setInvoiceData(Anx2ItcSumryData eachInv,
			Anx2GetInvoicesReqDto dto, Long batchId) {
		GetAnx2ItcSummaryInvoicesEntity invoice = new 
				GetAnx2ItcSummaryInvoicesEntity();

		String action = eachInv.getAction();
		BigDecimal docVal = eachInv.getVal();
		BigDecimal igstAmt = eachInv.getIgstAmount();
		BigDecimal cgstAmt = eachInv.getCgstAmount();
		BigDecimal sgstAmt = eachInv.getSgstAmount();
		BigDecimal cessAmt = eachInv.getCessAmount();

		invoice.setAction(action);
		invoice.setDocVal(docVal);
		invoice.setIgstAmt(igstAmt);
		invoice.setCgstAmt(cgstAmt);
		invoice.setSgstAmt(sgstAmt);
		invoice.setCessAmt(cessAmt);
		
		invoice.setItcSumBatchIdAnx2(batchId);
		invoice.setCgstin(dto.getGstin()); // input
		invoice.setReturnPeriod(dto.getReturnPeriod());
		invoice.setDelete(false);
		
		return invoice;
	}
}
