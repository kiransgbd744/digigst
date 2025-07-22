
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
import com.ey.advisory.app.data.entities.simplified.client.GetAnx1ImpgSezInvoicesHeaderEntity;
import com.ey.advisory.app.data.entities.simplified.client.GetAnx1ImpgSezInvoicesItemEntity;
import com.ey.advisory.app.docs.dto.anx1.Anx1ImpgAndImpgSezData;
import com.ey.advisory.app.docs.dto.anx1.Anx1ImpgAndImpgSezDocumentData;
import com.ey.advisory.app.docs.dto.anx1.Anx1ImpgAndImpgSezItemDetails;
import com.ey.advisory.app.services.docs.gstr2.DefaultInwardTransDocKeyGenerator;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.DateUtil;
import com.ey.advisory.common.GSTConstants;
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
@Service("Anx1ImpgSezDataParserImpl")
@Slf4j
public class Anx1ImpgSezDataParserImpl implements Anx1ImpgSezDataParser {

	@Autowired
	@Qualifier("DefaultInwardTransDocKeyGenerator")
	private DefaultInwardTransDocKeyGenerator inwardDocKey;

	@Override
	public Set<GetAnx1ImpgSezInvoicesHeaderEntity> parseImpgSezData(
			Anx1GetInvoicesReqDto dto, String apiResp) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		Set<GetAnx1ImpgSezInvoicesHeaderEntity> invoiceList = new HashSet<>();

		JsonArray respObject = null;
		try {
			respObject = (new JsonParser()).parse(apiResp).getAsJsonObject()
					.get(APIConstants.IMPGSEZ).getAsJsonArray();

			Type listType = new TypeToken<List<Anx1ImpgAndImpgSezData>>() {
			}.getType();
			List<Anx1ImpgAndImpgSezData> baseEntity = gson.fromJson(respObject,
					listType);
			for (Anx1ImpgAndImpgSezData eachInv : baseEntity) {
				for (Anx1ImpgAndImpgSezDocumentData eachInvData : eachInv
						.getDocs()) {
					List<GetAnx1ImpgSezInvoicesItemEntity> itemList = new ArrayList<>();
					GetAnx1ImpgSezInvoicesHeaderEntity invoice = setInvoiceData(
							eachInv, eachInvData, dto, dto.getBatchId());
					invoiceList.add(invoice);
					BigDecimal igst = BigDecimal.ZERO;
					BigDecimal cess = BigDecimal.ZERO;
					BigDecimal taxable = BigDecimal.ZERO;
					for (Anx1ImpgAndImpgSezItemDetails lineItem : eachInvData
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
			String msg = "failed to parse Anx1 IMPGSEZ response";
			LOGGER.error(msg, ex);
			throw new APIException(msg);
		}
		return invoiceList;
	}

	private GetAnx1ImpgSezInvoicesHeaderEntity setInvoiceData(
			Anx1ImpgAndImpgSezData eachInv,
			Anx1ImpgAndImpgSezDocumentData eachInvData,
			Anx1GetInvoicesReqDto dto, Long batchId) {
		GetAnx1ImpgSezInvoicesHeaderEntity invoice = new GetAnx1ImpgSezInvoicesHeaderEntity();

		String ctin = eachInv.getCgstin();

		String docType = eachInvData.getDoctyp();
		String pos = eachInvData.getImpgSezPos();
		String rfndelg = eachInvData.getRfndelg();
		String checkSum = eachInvData.getCheckSum();
		String billEntryDate = eachInvData.getBoe().getBillDate();
		BigDecimal billEntryNum = eachInvData.getBoe().getBillNum() != null
				? new BigDecimal(eachInvData.getBoe().getBillNum()) : null;
		String finYear = GenUtil.getFinYear(LocalDate.parse(billEntryDate,
				DateUtil.SUPPORTED_DATE_FORMAT2));
		String bilPort = eachInvData.getBoe().getPortCode();

		BigDecimal value = eachInvData.getBoe().getBillValue();

		/**
		 * setting IMPGSEZInvoices
		 */
		invoice.setCtin(ctin);

		/**
		 * setting IMPGSEZInvoiceData
		 */
		invoice.setDocType(docType);
		invoice.setPos(pos);
		invoice.setRfndElg(rfndelg);
		invoice.setChkSum(checkSum);
		invoice.setBillEntryNum(billEntryNum);
		invoice.setPortCode(bilPort);
		if (billEntryDate != null) {
			invoice.setBillEntryDate(LocalDate.parse(billEntryDate,
					DateUtil.SUPPORTED_DATE_FORMAT2));
		}
		invoice.setDocAmt(value);

		invoice.setImpgSezBatchId(batchId);
		invoice.setTaxPeriod(dto.getReturnPeriod());
		invoice.setDelete(false);
		setDockey(invoice, dto.getGstin(), billEntryNum, finYear);

		invoice.setDerTaxPeriod(
				GenUtil.convertTaxPeriodToInt(dto.getReturnPeriod()));
		return invoice;
	}

	private GetAnx1ImpgSezInvoicesItemEntity setItemData(
			Anx1ImpgAndImpgSezItemDetails lineItem) {
		GetAnx1ImpgSezInvoicesItemEntity item = new GetAnx1ImpgSezInvoicesItemEntity();
		item.setHsn(Integer.parseInt(lineItem.getHsn()));
		item.setTaxRate(lineItem.getRate());
		item.setTaxableValue(lineItem.getTaxableValue());
		item.setIgstAmt(lineItem.getIgstAmount());
		item.setCessAmt(lineItem.getCessAmount());
		return item;
	}

	private void setDockey(GetAnx1ImpgSezInvoicesHeaderEntity invoice,
			String gstin, BigDecimal billEntryNum, String finYear) {

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
		doc.setDocNo(String.valueOf(billEntryNum));
		doc.setFinYear(finYear);

		invoice.setDocKey(inwardDocKey.generateKey(doc));

	}
}
