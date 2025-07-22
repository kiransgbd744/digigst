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
import com.ey.advisory.app.data.entities.simplified.client.GetAnx1SezwopaInvoicesHeaderEntity;
import com.ey.advisory.app.data.entities.simplified.client.GetAnx1SezwopaInvoicesItemEntity;
import com.ey.advisory.app.docs.dto.anx1.Anx1B2bItemDetails;
import com.ey.advisory.app.docs.dto.anx1.Anx1B2baDocumentData;
import com.ey.advisory.app.docs.dto.anx1.Anx1SezwopaData;
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
 * 
 * @author Anand3.M
 *
 */

@Service("Anx1SezwopaDataParserImpl")
@Slf4j
public class Anx1SezwopaDataParserImpl implements Anx1SezwopaDataParser {

	@Autowired
	@Qualifier("DefaultOutwardTransDocKeyGenerator")
	private DefaultOutwardTransDocKeyGenerator outwardDocKey;

	@Override
	public Set<GetAnx1SezwopaInvoicesHeaderEntity> parseSezwopaData(
			Anx1GetInvoicesReqDto dto, String apiResp) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		Set<GetAnx1SezwopaInvoicesHeaderEntity> invoiceList = new HashSet<>();

		JsonArray respObject = null;
		try {
			respObject = (new JsonParser()).parse(apiResp).getAsJsonObject()
					.get(APIConstants.SEZWOPA).getAsJsonArray();

			Type listType = new TypeToken<List<Anx1SezwopaData>>() {
			}.getType();
			List<Anx1SezwopaData> baseEntity = gson.fromJson(respObject,
					listType);
			for (Anx1SezwopaData eachInv : baseEntity) {
				for (Anx1B2baDocumentData eachInvData : eachInv.getDocs()) {
					List<GetAnx1SezwopaInvoicesItemEntity> itemList = new ArrayList<>();
					GetAnx1SezwopaInvoicesHeaderEntity invoice = setInvoiceData(
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
			String msg = "failed to parse Anx1 SEZWOPA response";
			LOGGER.error(msg, ex);
			throw new APIException(msg);
		}
		return invoiceList;
	}

	private GetAnx1SezwopaInvoicesHeaderEntity setInvoiceData(
			Anx1SezwopaData eachInv, Anx1B2baDocumentData eachInvData,
			Anx1GetInvoicesReqDto dto, Long batchId) {
		GetAnx1SezwopaInvoicesHeaderEntity invoice = new GetAnx1SezwopaInvoicesHeaderEntity();

		String ctin = eachInvData.getCgstin();
		String octin = eachInvData.getOctin();

		String docType = eachInvData.getDoctyp();
		String odocType = eachInvData.getOdoctyp();

		String rfndelg = eachInvData.getRfndelg();
		String action = eachInvData.getAction();
		String pos = eachInvData.getPos();

		String checkSum = eachInvData.getCheckSum();

		String aprd = eachInvData.getAprd();
		String amdTyp = eachInvData.getAmdtyp();

		String docDate = eachInvData.getDoc().getDocDate();
		String docNum = eachInvData.getDoc().getDocNum();
		String finYear = GenUtil.getFinYear(
				LocalDate.parse(docDate, DateUtil.SUPPORTED_DATE_FORMAT2));
		BigDecimal docVal = eachInvData.getDoc().getDocVal();

		/**
		 * setting B2BAInvoiceData
		 */
		invoice.setCtin(ctin);
		invoice.setOrgCtin(octin);

		invoice.setDocType(docType);
		invoice.setOrgDocType(odocType);
		invoice.setInvalid(false);

		invoice.setRfndElg(rfndelg);
		invoice.setAction(action);
		invoice.setPos(pos);
		invoice.setChkSum(checkSum);
		invoice.setAmended(false);
		invoice.setAmdPeriod(aprd);
		invoice.setAmdType(amdTyp);
		if (docDate != null) {
			invoice.setDocDate(
					LocalDate.parse(docDate, DateUtil.SUPPORTED_DATE_FORMAT2));
		}
		invoice.setDocNum(docNum);
		invoice.setDocAmt(docVal);

		invoice.setSezwopaBatchId(batchId);
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

	private GetAnx1SezwopaInvoicesItemEntity setItemData(
			Anx1B2bItemDetails lineItem) {
		GetAnx1SezwopaInvoicesItemEntity item = new GetAnx1SezwopaInvoicesItemEntity();
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

	private void setDockey(GetAnx1SezwopaInvoicesHeaderEntity invoice,
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
