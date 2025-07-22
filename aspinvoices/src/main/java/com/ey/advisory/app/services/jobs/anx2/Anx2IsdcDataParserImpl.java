package com.ey.advisory.app.services.jobs.anx2;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.simplified.client.GetAnx2IsdcInvoicesHeaderEntity;
import com.ey.advisory.app.docs.dto.anx2.Anx2GetInvoicesReqDto;
import com.ey.advisory.app.docs.dto.anx2.Anx2IsdcAmendementDetails;
import com.ey.advisory.app.docs.dto.anx2.Anx2IsdcDocumentData;
import com.ey.advisory.app.docs.dto.anx2.Anx2IsdcOriginalDetails;
import com.ey.advisory.common.DateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.api.APIConstants;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Hemasundar.J
 *
 */
@Service("Anx2IsdcDataParserImpl")
@Slf4j
public class Anx2IsdcDataParserImpl implements Anx2IsdcDataParser {

	@Override
	public Set<GetAnx2IsdcInvoicesHeaderEntity> parseIsdcData(
			Anx2GetInvoicesReqDto dto, String apiResp, Long batchId) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		Set<GetAnx2IsdcInvoicesHeaderEntity> invoiceList = new HashSet<>();
		LOGGER.debug("Parsing Gstn response into Entities");
		JsonArray respObject = null;
		try {
			respObject = (new JsonParser()).parse(apiResp).getAsJsonObject()
					.get("isdc").getAsJsonArray();

			Type listType = new TypeToken<List<Anx2IsdcDocumentData>>() {
			}.getType();
			List<Anx2IsdcDocumentData> baseEntity = gson.fromJson(respObject
					, listType);
			for (Anx2IsdcDocumentData eachInv : baseEntity) {
				Anx2IsdcOriginalDetails original = eachInv.getOrginal();
				Anx2IsdcAmendementDetails amd= eachInv.getAmedement();
				if (original != null) {
					GetAnx2IsdcInvoicesHeaderEntity invoice = setOrigData(
							original, dto, batchId);
					invoiceList.add(invoice);
				}
				if (amd != null) {
					GetAnx2IsdcInvoicesHeaderEntity invoice = setAmdData(amd,
							dto, batchId);
					invoiceList.add(invoice);
				}
			}
		} catch (Exception ex) {
			String msg = "failed to parse Anx2 Isdc response";
			LOGGER.error(msg, ex);
		}
		return invoiceList;
	}

	private GetAnx2IsdcInvoicesHeaderEntity setOrigData(
			Anx2IsdcOriginalDetails org, Anx2GetInvoicesReqDto dto, Long batchId) {
		GetAnx2IsdcInvoicesHeaderEntity invoice = new 
				GetAnx2IsdcInvoicesHeaderEntity();

		String sgstin = org.getCgstin();
		String docDate = org.getDocDate();
		String docNum = org.getDocNum();
		String docType = org.getDocType();
		String isAmd = org.getIsamended();
		String distbtrRetPeriod = org.getDistbtrRetPeriod();
		BigDecimal cess = org.getCessAmount();
		BigDecimal sgst = org.getSgstAmount();
		BigDecimal cgst = org.getCgstAmount();
		BigDecimal igst = org.getIgstAmount();
		
		invoice.setTableSection(APIConstants.ISDC.toUpperCase());
		
		/**
		 * setting B2BInvoices
		 */
		invoice.setSgstin(sgstin); // output
		
		/**
		 * setting B2BInvoiceData
		 */
		
		invoice.setInvNum(docNum);
		if(docDate != null) {
			invoice.setInvDate(LocalDate.parse(docDate, 
					DateUtil.SUPPORTED_DATE_FORMAT2));
			}
		invoice.setInvType(docType);
		invoice.setIsAmended(
				isAmd != null && isAmd.equals(APIConstants.Y));
		invoice.setDistbtrRetPeriod(distbtrRetPeriod);
		invoice.setIgstAmt(igst);
		invoice.setSgstAmt(sgst);
		invoice.setCgstAmt(cgst);
		invoice.setCessAmt(cess);
		
		invoice.setIsdcBatchIdAnx2(batchId);
		invoice.setCgstin(dto.getGstin()); // input
		invoice.setReturnPeriod(dto.getReturnPeriod());
		invoice.setDerivedRetPeriod(GenUtil.convertTaxPeriodToInt(dto.getReturnPeriod()));
		invoice.setDelete(false);
		
		if (dto.getGstin() != null && dto.getGstin().trim().length() == 15) {
			invoice.setCgstinPan(dto.getGstin().trim().substring(2, 12));
		}
		if (sgstin != null && sgstin.trim().length() == 15) {
			invoice.setSgstinPan(sgstin.trim().substring(2, 12));
		}
		
		return invoice;
	}
	
	private GetAnx2IsdcInvoicesHeaderEntity setAmdData(
			Anx2IsdcAmendementDetails amd, Anx2GetInvoicesReqDto dto, Long batchId) {
		GetAnx2IsdcInvoicesHeaderEntity invoice = new 
				GetAnx2IsdcInvoicesHeaderEntity();

		String sgstin = amd.getCgstin();
		String docDate = amd.getDocDate();
		String docNum = amd.getDocNum();
		String docType = amd.getDocType();
		String distbtrRetPeriod = amd.getDistbtrRetPeriod();
		BigDecimal cess = amd.getCessAmount();
		BigDecimal sgst = amd.getSgstAmount();
		BigDecimal cgst = amd.getCgstAmount();
		BigDecimal igst = amd.getIgstAmount();
		
		/**
		 * setting B2BInvoices
		 */
		invoice.setSgstin(sgstin); // output
		
		/**
		 * setting B2BInvoiceData
		 */
		
		invoice.setInvNum(docNum);
		if(docDate != null) {
			invoice.setInvDate(LocalDate.parse(docDate, 
					DateUtil.SUPPORTED_DATE_FORMAT2));
			}
		invoice.setInvType(docType);
		invoice.setDistbtrRetPeriod(distbtrRetPeriod);
		invoice.setIgstAmt(igst);
		invoice.setSgstAmt(sgst);
		invoice.setCgstAmt(cgst);
		invoice.setCessAmt(cess);
		
		invoice.setIsdcBatchIdAnx2(batchId);
		invoice.setCgstin(dto.getGstin()); // input
		invoice.setReturnPeriod(dto.getReturnPeriod());
		invoice.setDerivedRetPeriod(GenUtil.convertTaxPeriodToInt(dto.getReturnPeriod()));
		invoice.setDelete(false);
		
		if (dto.getGstin() != null && dto.getGstin().trim().length() == 15) {
			invoice.setCgstinPan(dto.getGstin().trim().substring(2, 12));
		}
		if (sgstin != null && sgstin.trim().length() == 15) {
			invoice.setSgstinPan(sgstin.trim().substring(2, 12));
		}
		return invoice;
	}

}
