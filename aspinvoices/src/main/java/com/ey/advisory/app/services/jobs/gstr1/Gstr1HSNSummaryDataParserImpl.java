package com.ey.advisory.app.services.jobs.gstr1;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.GetGstr1HSNOrSACInvoicesEntity;
import com.ey.advisory.app.docs.dto.HSNSummaryInvData;
import com.ey.advisory.app.docs.dto.HSNSummaryInvoices;
import com.ey.advisory.app.services.common.Gstr1GetKeyGenerator;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import lombok.extern.slf4j.Slf4j;

@Component("gstr1HSNSummaryDataParserImpl")
@Slf4j
public class Gstr1HSNSummaryDataParserImpl
		implements Gstr1HSNSummaryDataParser {

	@Autowired
	private Gstr1GetKeyGenerator gstr1GetKeyGenerator;

	@Override
	public List<GetGstr1HSNOrSACInvoicesEntity> gstr1HSNSummaryDataParser(
			Gstr1GetInvoicesReqDto dto, String apiResp) {

		List<GetGstr1HSNOrSACInvoicesEntity> entities = new ArrayList<>();
		JsonObject jsonObject = null;
		Gson gson = GsonUtil.newSAPGsonInstance();
		// try {

		if (APIConstants.HSN.equalsIgnoreCase(dto.getType())) {
			jsonObject = (new JsonParser().parse(apiResp)).getAsJsonObject()
					.get(APIConstants.HSN.toLowerCase()).getAsJsonObject();
		}

		Type listType = new TypeToken<HSNSummaryInvoices>() {
		}.getType();
		HSNSummaryInvoices hsnSummaryInvoices = gson.fromJson(jsonObject,
				listType);

		List<HSNSummaryInvData> hsnSummaryInvDatas = hsnSummaryInvoices
				.getHsnSummaryInvData();
		// hsnB2b
		List<HSNSummaryInvData> hsnB2b = hsnSummaryInvoices.getHsnB2b();
		// hsnb2c
		List<HSNSummaryInvData> hsnB2c = hsnSummaryInvoices.getHsnB2c();

		if (hsnSummaryInvDatas != null && !hsnSummaryInvDatas.isEmpty()) {
			hsnSummaryInvDatas.forEach(hsnSummaryInvData -> {
				saveData(dto, entities, hsnSummaryInvoices, hsnSummaryInvData,
						null);
			});
		}
		if (hsnB2b != null && !hsnB2b.isEmpty()) {
			hsnB2b.forEach(hsnSummaryInvData -> {
				saveData(dto, entities, hsnSummaryInvoices, hsnSummaryInvData,
						"HSN_B2B");
			});
		}
		if (hsnB2c != null && !hsnB2c.isEmpty()) {
			hsnB2c.forEach(hsnSummaryInvData -> {
				saveData(dto, entities, hsnSummaryInvoices, hsnSummaryInvData,
						"HSN_B2C");
			});
		}
		/*
		 * } catch (Exception e) { String msg =
		 * "Failed to Parse HSN Summary Details"; LOGGER.error(msg, e); throw
		 * new APIException(msg);
		 * 
		 * }
		 */
		return entities;
	}
	/*
	 * public static void main(String[] args) { Object value =
	 * CommonUtility.exponentialAndZeroCheckForBigDecimal(4.7397325159E8);
	 * BigDecimal b=(new BigDecimal(value.toString())); System.out.println(b);
	 * 
	 * }
	 */

	/**
	 * @param dto
	 * @param entities
	 * @param hsnSummaryInvoices
	 * @param hsnSummaryInvData
	 */
	private void saveData(Gstr1GetInvoicesReqDto dto,
			List<GetGstr1HSNOrSACInvoicesEntity> entities,
			HSNSummaryInvoices hsnSummaryInvoices,
			HSNSummaryInvData hsnSummaryInvData, String recordType) {
		GetGstr1HSNOrSACInvoicesEntity entity = new GetGstr1HSNOrSACInvoicesEntity();
		/**
		 * Input data
		 */
		entity.setGstinOfTaxPayer(dto.getGstin());
		entity.setReturnPeriod(dto.getReturnPeriod());
		if (dto.getReturnPeriod() != null
				&& dto.getReturnPeriod().length() > 0) {
			entity.setDerivedRetPeriod(
					GenUtil.convertTaxPeriodToInt(dto.getReturnPeriod()));
		}
		entity.setBatchId(dto.getBatchId());

		entity.setFlag(hsnSummaryInvoices.getFlag());
		entity.setInvoiceCheckSum(hsnSummaryInvoices.getInvoiceCheckSum());
		entity.setSerialNumber(hsnSummaryInvData.getSerialNumber());
		entity.setHsnOrSac(hsnSummaryInvData.getHsnGoodsOrService());
		entity.setDescOfGoodsSold(hsnSummaryInvData.getDescOfGoodsSold());
		entity.setUnitOfMeasureOfGoodsSold(
				hsnSummaryInvData.getUnitOfMeasureOfGoodsSold());
		entity.setQtyOfGoodsSold(hsnSummaryInvData.getQtyOfGoodsSold());

		Object value = CommonUtility.exponentialAndZeroCheckForBigDecimal(
				hsnSummaryInvData.getTotalValue());
		if (value != null) {
			entity.setTotalValue(new BigDecimal(value.toString()));
		}

		Object taxValue = CommonUtility.exponentialAndZeroCheckForBigDecimal(
				hsnSummaryInvData.getTaxValOfGoodsOrService());
		entity.setTaxValOfGoodsOrService(new BigDecimal(taxValue.toString()));

		entity.setIgstAmount(hsnSummaryInvData.getIgstAmount());
		entity.setCgstAmount(hsnSummaryInvData.getCgstAmount());
		entity.setSgstAmount(hsnSummaryInvData.getSgstAmount());
		entity.setCessAmount(hsnSummaryInvData.getCessAmount());
		entity.setRate(hsnSummaryInvData.getTaxRate());
		entity.setRecordType(recordType);

		String sgtin = dto.getGstin();
		String retPeriod = dto.getReturnPeriod();
		String hsn = hsnSummaryInvData.getHsnGoodsOrService();
		String uqc = hsnSummaryInvData.getUnitOfMeasureOfGoodsSold();
		entity.setDocKey(gstr1GetKeyGenerator.generateHsnKey(sgtin, retPeriod,
				hsn, uqc, recordType,
				hsnSummaryInvData.getTaxRate() != null
						? hsnSummaryInvData.getTaxRate().toString()
						: ""));
		entities.add(entity);
	}

}
