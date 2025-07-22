package com.ey.advisory.app.services.jobs.anx2;

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

import com.ey.advisory.app.data.entities.simplified.client.GetAnx2SezwopInvoicesHeaderEntity;
import com.ey.advisory.app.data.entities.simplified.client.GetAnx2SezwopInvoicesItemEntity;
import com.ey.advisory.app.docs.dto.anx2.Anx2Data;
import com.ey.advisory.app.docs.dto.anx2.Anx2DocumentData;
import com.ey.advisory.app.docs.dto.anx2.Anx2GetInvoicesReqDto;
import com.ey.advisory.app.docs.dto.anx2.Anx2ItemDetails;
import com.ey.advisory.app.services.common.DocKeyGenerator;
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
@Service("Anx2SezwopDataParserImpl")
@Slf4j
public class Anx2SezwopDataParserImpl implements Anx2SezwopDataParser {

	@Autowired
	@Qualifier("DocKeyGenerator")
	private DocKeyGenerator docKey;

	@Override
	public Set<GetAnx2SezwopInvoicesHeaderEntity> parseSezwopData(
			Anx2GetInvoicesReqDto dto, String apiResp, Long batchId) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		Set<GetAnx2SezwopInvoicesHeaderEntity> invoiceList = new HashSet<>();
		LOGGER.debug("Parsing Gstn response into Entities");
		JsonArray respObject = null;
		try {
			respObject = (new JsonParser()).parse(apiResp).getAsJsonObject()
					.get(APIConstants.SEZWOP).getAsJsonArray();

			Type listType = new TypeToken<List<Anx2Data>>() {
			}.getType();
			List<Anx2Data> baseEntity = gson.fromJson(respObject, listType);
			for (Anx2Data eachInv : baseEntity) {
				for (Anx2DocumentData eachInvData : eachInv.getInvoiceData()) {
					List<GetAnx2SezwopInvoicesItemEntity> itemList = new ArrayList<>();
					GetAnx2SezwopInvoicesHeaderEntity invoice = setInvoiceData(
							eachInv, eachInvData, dto, batchId);
					invoiceList.add(invoice);

					BigDecimal taxable = BigDecimal.ZERO;
					for (Anx2ItemDetails lineItem : eachInvData.getItems()) {
						GetAnx2SezwopInvoicesItemEntity item = setItemData(
								lineItem);
						itemList.add(item);
						if (lineItem.getTaxableValue() != null) {
							taxable = taxable.add(lineItem.getTaxableValue());
						}
					}
					/**
					 * setting summary rate info at invoice level.
					 */
					invoice.setTaxable(taxable);
					invoice.setLineItems(itemList);
					itemList.forEach(item -> {
						item.setHeader(invoice);
					});

				}
			}
		} catch (Exception ex) {
			String msg = "failed to parse Gstr2a SEZWP response";
			LOGGER.error(msg, ex);
		}
		return invoiceList;
	}

	private GetAnx2SezwopInvoicesHeaderEntity setInvoiceData(Anx2Data eachInv,
			Anx2DocumentData eachInvData, Anx2GetInvoicesReqDto dto,
			Long batchId) {
		GetAnx2SezwopInvoicesHeaderEntity invoice = new GetAnx2SezwopInvoicesHeaderEntity();

		String cfs = eachInv.getCfs();
		String sgstin = eachInv.getSgstin();
		String action = eachInvData.getAction();
		String checkSum = eachInvData.getCheckSum();
		String docType = eachInvData.getInvoiceType();
		String itcent = eachInvData.getItcent();
		String pos = eachInvData.getPos();
		String rfndelg = eachInvData.getRfndelg();
		String uploadDate = eachInvData.getUploadDate();
		String docDate = eachInvData.getDoc().getDocDate();
		String docNum = eachInvData.getDoc().getDocNum();
		String docVal = eachInvData.getDoc().getDocVal();

		invoice.setTableSection(APIConstants.SEZWOP.toUpperCase());
		/**
		 * setting B2BInvoices
		 */
		invoice.setCfs(cfs);
		invoice.setSgstin(sgstin); // output

		/**
		 * setting B2BInvoiceData
		 */
		invoice.setChkSum(checkSum);
		invoice.setInvNum(docNum);
		if (docDate != null) {
			invoice.setInvDate(
					LocalDate.parse(docDate, DateUtil.SUPPORTED_DATE_FORMAT2));
		}
		invoice.setInvValue(new BigDecimal(docVal));

		invoice.setPos(pos);

		invoice.setInvType(docType);
		invoice.setAction(action);
		if (uploadDate != null) {
			invoice.setUploadDate(LocalDate.parse(uploadDate,
					DateUtil.SUPPORTED_DATE_FORMAT2));
		}
		invoice.setItc(itcent);
		invoice.setRfndElg(rfndelg);

		invoice.setSezwopBatchIdAnx2(batchId);
		invoice.setCgstin(dto.getGstin()); // input
		invoice.setReturnPeriod(dto.getReturnPeriod());
		invoice.setDerivedRetPeriod(
				GenUtil.convertTaxPeriodToInt(dto.getReturnPeriod()));
		invoice.setDelete(false);

		/*
		 * invoice.setDocKey( docKey.generateKey(dto.getGstin(),
		 * invoice.getDocNum(), invoice.getDocDate(), invoice.getDocType()));
		 */
		if (dto.getGstin() != null && dto.getGstin().trim().length() == 15) {
			invoice.setCgstinPan(dto.getGstin().trim().substring(2, 12));
		}
		if (sgstin != null && sgstin.trim().length() == 15) {
			invoice.setSgstinPan(sgstin.trim().substring(2, 12));
		}

		return invoice;
	}

	private GetAnx2SezwopInvoicesItemEntity setItemData(
			Anx2ItemDetails lineItem) {
		GetAnx2SezwopInvoicesItemEntity item = new GetAnx2SezwopInvoicesItemEntity();
		item.setHsn(lineItem.getHsn());
		item.setTaxRate(lineItem.getRate());
		item.setTaxableValue(lineItem.getTaxableValue());
		return item;
	}
}
