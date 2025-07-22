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
import com.ey.advisory.app.data.entities.simplified.client.GetAnx1SezwpInvoicesHeaderEntity;
import com.ey.advisory.app.data.entities.simplified.client.GetAnx1SezwpInvoicesItemEntity;
import com.ey.advisory.app.docs.dto.anx1.Anx1SezwpAndSezwopData;
import com.ey.advisory.app.docs.dto.anx1.Anx1SezwpAndSezwopDocumentData;
import com.ey.advisory.app.docs.dto.anx1.Anx1SezwpAndSezwopItemDetails;
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
@Service("Anx1SezwpDataParserImpl")
@Slf4j
public class Anx1SezwpDataParserImpl implements Anx1SezwpDataParser {

	@Autowired
	@Qualifier("DefaultOutwardTransDocKeyGenerator")
	private DefaultOutwardTransDocKeyGenerator outwardDocKey;

	@Override
	public Set<GetAnx1SezwpInvoicesHeaderEntity> parseSezwpData(
			Anx1GetInvoicesReqDto dto, String apiResp) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		Set<GetAnx1SezwpInvoicesHeaderEntity> invoiceList = new HashSet<>();

		JsonArray respObject = null;
		try {
			respObject = (new JsonParser()).parse(apiResp).getAsJsonObject()
					.get(APIConstants.SEZWP).getAsJsonArray();

			Type listType = new TypeToken<List<Anx1SezwpAndSezwopData>>() {
			}.getType();
			List<Anx1SezwpAndSezwopData> baseEntity = gson.fromJson(respObject,
					listType);
			for (Anx1SezwpAndSezwopData eachInv : baseEntity) {
				for (Anx1SezwpAndSezwopDocumentData eachInvData : eachInv
						.getDocs()) {
					List<GetAnx1SezwpInvoicesItemEntity> itemList = new ArrayList<>();
					GetAnx1SezwpInvoicesHeaderEntity invoice = setInvoiceData(
							eachInv, eachInvData, dto, dto.getBatchId());
					invoiceList.add(invoice);
					BigDecimal igst = BigDecimal.ZERO;
					BigDecimal cess = BigDecimal.ZERO;
					BigDecimal taxable = BigDecimal.ZERO;
					for (Anx1SezwpAndSezwopItemDetails lineItem : eachInvData
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
			}
		} catch (Exception ex) {
			String msg = "failed to parse Anx1 SEZWP response";
			LOGGER.error(msg, ex);
			throw new APIException(msg);
		}
		return invoiceList;
	}

	private GetAnx1SezwpInvoicesHeaderEntity setInvoiceData(
			Anx1SezwpAndSezwopData eachInv,
			Anx1SezwpAndSezwopDocumentData eachInvData,
			Anx1GetInvoicesReqDto dto, Long batchId) {
		GetAnx1SezwpInvoicesHeaderEntity invoice = new GetAnx1SezwpInvoicesHeaderEntity();

		String ctin = eachInv.getCgstin();

		String docType = eachInvData.getDoctyp();
		BigDecimal diffPercent = eachInvData.getDiffprcnt();
		String pos = eachInvData.getPos();
		String rfndElg = eachInvData.getRfndelg();
		String action = eachInvData.getAction();
		String checkSum = eachInvData.getCheckSum();
		String clmRfnd = eachInvData.getClmrfnd();

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
		invoice.setPos(pos);
		invoice.setDocType(docType);
		invoice.setChkSum(checkSum);
		invoice.setRfndElg(rfndElg);
		invoice.setClmRfnd(clmRfnd);
		invoice.setAction(action);
		if (new BigDecimal(0.65).compareTo(diffPercent) == 0) {
			invoice.setDiffPercent(APIConstants.Y);
		} else {
			invoice.setDiffPercent(APIConstants.N);
		}

		invoice.setDocNum(docNum);
		if (docDate != null) {
			invoice.setDocDate(
					LocalDate.parse(docDate, DateUtil.SUPPORTED_DATE_FORMAT2));
		}
		invoice.setDocAmt(docVal);

		invoice.setSezwpBatchId(batchId);
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

	private GetAnx1SezwpInvoicesItemEntity setItemData(
			Anx1SezwpAndSezwopItemDetails lineItem) {
		GetAnx1SezwpInvoicesItemEntity item = new GetAnx1SezwpInvoicesItemEntity();
		item.setHsn(Integer.parseInt(lineItem.getHsn()));
		item.setTaxRate(lineItem.getRate());
		item.setTaxableValue(lineItem.getTaxableValue());
		item.setIgstAmt(lineItem.getIgstAmount());
		item.setCessAmt(lineItem.getCessAmount());
		return item;
	}

	private void setDockey(GetAnx1SezwpInvoicesHeaderEntity invoice,
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
