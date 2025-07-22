/**
 * 
 */
package com.ey.advisory.app.services.jobs.anx1;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.simplified.client.GetAnx1EcomInvoicesEntity;
import com.ey.advisory.app.docs.dto.anx1.Anx1EcomData;
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
@Service("anx1EcomDataParserImpl")
@Slf4j
public class Anx1EcomDataParserImpl implements Anx1EcomDataParser {

	@Autowired
	@Qualifier("DefaultOutwardTransDocKeyGenerator")
	private DefaultOutwardTransDocKeyGenerator outwardDocKey;

	@Override
	public Set<GetAnx1EcomInvoicesEntity> parseEcomData(
			Anx1GetInvoicesReqDto dto, String apiResp) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		Set<GetAnx1EcomInvoicesEntity> invoiceList = new HashSet<>();

		JsonArray respObject = null;
		try {
			respObject = (new JsonParser()).parse(apiResp).getAsJsonObject()
					.get(APIConstants.ECOM).getAsJsonArray();

			Type listType = new TypeToken<List<Anx1EcomData>>() {
			}.getType();
			List<Anx1EcomData> baseEntity = gson.fromJson(respObject, listType);
			for (Anx1EcomData eachInv : baseEntity) {
				invoiceList.add(setInvoiceData(eachInv, dto, dto.getBatchId()));
			}
		} catch (Exception ex) {
			String msg = "failed to parse Anx1 ECOM response";
			LOGGER.error(msg, ex);
			throw new APIException(msg);
		}
		return invoiceList;
	}

	private GetAnx1EcomInvoicesEntity setInvoiceData(Anx1EcomData eachInv,
			Anx1GetInvoicesReqDto dto, Long batchId) {

		String etin = eachInv.getEtin();
		String chkSum = eachInv.getCheckSum();
		BigDecimal supMadeVal = eachInv.getMadeSupVal();
		BigDecimal supRetVal = eachInv.getRetSupVal();
		BigDecimal supNetVal = eachInv.getNetSupval();
		BigDecimal igstAmt = eachInv.getIgstAmount();
		BigDecimal cgstAmt = eachInv.getCgstAmount();
		BigDecimal sgstAmt = eachInv.getSgstAmount();
		BigDecimal cessAmt = eachInv.getCessAmount();
		
		GetAnx1EcomInvoicesEntity invoice = new GetAnx1EcomInvoicesEntity();
		invoice.setEtin(etin);
		invoice.setChkSum(chkSum);
		invoice.setSupMadeVal(supMadeVal);
		invoice.setSupRetVal(supRetVal);
		invoice.setSupNetVal(supNetVal);
		invoice.setIgstAmt(igstAmt);
		invoice.setCgstAmt(cgstAmt);
		invoice.setSgstAmt(sgstAmt);
		invoice.setCessAmt(cessAmt);

		invoice.setEcomBatchId(batchId);
		invoice.setsGstin(dto.getGstin());
		invoice.setTaxPeriod(dto.getReturnPeriod());
		invoice.setDelete(false);

		//setDockey(invoice, dto.getGstin(), docNum, finYear);

		invoice.setDerTaxPeriod(
				GenUtil.convertTaxPeriodToInt(dto.getReturnPeriod()));
		return invoice;
	}

	/*private void setDockey(GetAnx1EcomInvoicesEntity invoice, String gstin,
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

	}*/

}
