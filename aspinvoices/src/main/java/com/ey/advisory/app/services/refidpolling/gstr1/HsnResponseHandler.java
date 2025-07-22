package com.ey.advisory.app.services.refidpolling.gstr1;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.javatuples.Triplet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.Gstr1SaveHsnPEResponseEntity;
import com.ey.advisory.app.data.entities.client.OutwardTransDocError;
import com.ey.advisory.app.data.repositories.client.Gstr1SaveHsnPEResponseRepository;
import com.ey.advisory.app.docs.dto.Gstr1ErrorReport;
import com.ey.advisory.app.docs.dto.HSNSummaryInvData;
import com.ey.advisory.app.docs.dto.HSNSummaryInvoices;
import com.ey.advisory.app.docs.dto.SaveGstr1;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonLocalDateConverter;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.domain.client.Gstr1SaveBatchEntity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("HsnResponseHandler")
public class HsnResponseHandler implements Gstr1ResponseHandler {

	@Autowired
	private Gstr1SaveHsnPEResponseRepository hsnRepo;

	@Override
	public Integer SaveResponse(JsonElement root, Gstr1SaveBatchEntity batch,
			Integer n) {

		Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class,
				new GsonLocalDateConverter()).create();
		List<Gstr1SaveHsnPEResponseEntity> entityList = new ArrayList<>();
		SaveGstr1 gstr1 = gson.fromJson(root, SaveGstr1.class);
		Gstr1ErrorReport gsterrors = gstr1.getGstr1ErrorReport();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("GSTR1 SAVE HSNSUM PE Response json is {} ",
					gsterrors);
		}
		// temp variable in place consider the dynamic response
		List<HSNSummaryInvoices> temp = gsterrors.getHsnSumInvoices() != null
				? gsterrors.getHsnSumInvoices() : gsterrors.getHsnInvoices();
		if (temp == null) {
			LOGGER.error("Unable to load the Invoive level PE error response");
			return 0;
		}
		n = temp.size();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("GSTR1 SAVE HSNSUM PE error invoices count is {} ", n);
		}
		for (HSNSummaryInvoices invoice : temp) {
			entityList.addAll(addSaveHsnPeEntity(batch,
					invoice.getHsnSummaryInvData(), invoice, ""));
			entityList.addAll(addSaveHsnPeEntity(batch, invoice.getHsnB2b(),
					invoice, "B2B"));
			entityList.addAll(addSaveHsnPeEntity(batch, invoice.getHsnB2c(),
					invoice, "B2C"));
		}
		hsnRepo.saveAll(entityList);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"GSTR1 SAVE HSNSUM PE error invoices SaveAll is Completed");
		}
		return n;

	}

	private List<Gstr1SaveHsnPEResponseEntity> addSaveHsnPeEntity(
			Gstr1SaveBatchEntity batch, List<HSNSummaryInvData> docDataList,
			HSNSummaryInvoices invoice, String recordType) {
		LocalDateTime now = EYDateUtil
				.toUTCDateTimeFromLocal(LocalDateTime.now());
		List<Gstr1SaveHsnPEResponseEntity> entityList = new ArrayList<>();

		//added null check for invoice.getHsnSummaryInvData() old case.
		
		if(docDataList != null && !docDataList.isEmpty()) {
		for (HSNSummaryInvData docData : docDataList) {
			Gstr1SaveHsnPEResponseEntity entity = new Gstr1SaveHsnPEResponseEntity();
			entity.setSgstin(batch.getSgstin());
			entity.setReturnPeriod(batch.getReturnPeriod());
			entity.setDerivedRetPeriod(
					GenUtil.convertTaxPeriodToInt(batch.getReturnPeriod()));
			entity.setGstnBatchId(batch.getId());
			entity.setGstnErrorCode(invoice.getErrorCode());
			entity.setGstnErrorDesc(invoice.getErrorMsg());
			entity.setSerialNumber(docData.getSerialNumber());
			entity.setItmhsnsac(docData.getHsnGoodsOrService());
			entity.setItmdesc(docData.getDescOfGoodsSold());
			entity.setItmuqc(docData.getUnitOfMeasureOfGoodsSold());
			entity.setItmqty(docData.getQtyOfGoodsSold());
			entity.setTotalvalue(docData.getTotalValue());
			entity.setTaxableval(docData.getTaxValOfGoodsOrService());
			entity.setTaxRate(docData.getTaxRate());
			entity.setIgst(docData.getIgstAmount());
			entity.setSgst(docData.getSgstAmount());
			entity.setCgst(docData.getCgstAmount());
			entity.setCess(docData.getCessAmount());
			entity.setCreatedBy(APIConstants.SYSTEM.toUpperCase());
			entity.setCreatedOn(now);
			entity.setDelete(false);
			entity.setRecordType(recordType);

			entityList.add(entity);
		}
		}
		return entityList;
	}

	@Override
	public Triplet<List<String>, Map<String, OutwardTransDocError>, Integer> response(
			JsonElement root, Gstr1SaveBatchEntity batch, Integer n) {
		// TODO Auto-generated method stub
		return null;
	}

}
