package com.ey.advisory.app.services.jobs.gstr1;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.GetGstr1AtHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr1AtItemEntity;
import com.ey.advisory.app.data.entities.client.GetGstr1AtaHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr1AtaItemEntity;
import com.ey.advisory.app.docs.dto.ATInvoices;
import com.ey.advisory.app.docs.dto.ATItemDetails;
import com.ey.advisory.app.services.common.Gstr1GetKeyGenerator;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIException;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;
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
@Component("gstr1AtAtaDataParserImpl")
@Slf4j
public class Gstr1AtAtaDataParserImpl implements Gstr1AtAtaDataParser {

	@Autowired
	private Gstr1GetKeyGenerator gstr1GetKeyGenerator;

	@Override
	public List<GetGstr1AtHeaderEntity> parseAtData(Gstr1GetInvoicesReqDto dto, String apiResp) {

		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonArray respObject = null;
		List<GetGstr1AtHeaderEntity> headerList = new ArrayList<>();

		//try {

			respObject = (new JsonParser()).parse(apiResp).getAsJsonObject().get(APIConstants.AT).getAsJsonArray();

			Type listType = new TypeToken<List<ATInvoices>>() {
			}.getType();

			// List of Header and Item Data
			List<ATInvoices> baseEntity = gson.fromJson(respObject, listType);

			for (ATInvoices eachInv : baseEntity) {
				BigDecimal taxRate = BigDecimal.ZERO;
				BigDecimal advRecAmt = BigDecimal.ZERO;
				BigDecimal igstAmt = BigDecimal.ZERO;
				BigDecimal cgstAmt = BigDecimal.ZERO;
				BigDecimal sgstAmt = BigDecimal.ZERO;
				BigDecimal cessAmt = BigDecimal.ZERO;
				
				List<GetGstr1AtItemEntity> lineItems = new ArrayList<>();
				if (eachInv.getItems() != null) {
					for (ATItemDetails eachInvData : eachInv.getItems()) {

						// New Item Entity
						GetGstr1AtItemEntity item = new GetGstr1AtItemEntity();
						/**
						 * ATItemDetails data
						 */
						item.setReturnPeriod(dto.getReturnPeriod());
						if (dto.getReturnPeriod() != null && dto.getReturnPeriod().length() > 0) {
							item.setDerivedTaxperiod(GenUtil.convertTaxPeriodToInt(dto.getReturnPeriod()));
						}
						item.setTaxRate(eachInvData.getRt());
						item.setAdvRecAmt(eachInvData.getAdAmt());
						item.setCgstAmt(eachInvData.getCamt());
						item.setCessAmt(eachInvData.getCsamt());
						item.setIgstAmt(eachInvData.getIamt());
						item.setSgstAmt(eachInvData.getSamt());

						lineItems.add(item);
						// Header Amounts count
						if (item.getTaxRate() != null) {
							taxRate = taxRate.add(item.getTaxRate());
						}
						if (item.getAdvRecAmt() != null) {
							advRecAmt = advRecAmt.add(item.getAdvRecAmt());
						}
						if (item.getIgstAmt() != null) {

							igstAmt = igstAmt.add(item.getIgstAmt());
						}
						if (item.getCgstAmt() != null) {
							cgstAmt = cgstAmt.add(item.getCgstAmt());
						}
						if (item.getSgstAmt() != null) {

							sgstAmt = sgstAmt.add(item.getSgstAmt());
						}
						if (item.getCessAmt() != null) {
							cessAmt = cessAmt.add(item.getCessAmt());
						}

					}
					// New Header Entity
					GetGstr1AtHeaderEntity header = new GetGstr1AtHeaderEntity();
					/**
					 * Input data
					 */
					header.setGstin(dto.getGstin());
					header.setReturnPeriod(dto.getReturnPeriod());
					if (dto.getReturnPeriod() != null && dto.getReturnPeriod().length() > 0) {
						header.setDerivedTaxperiod(GenUtil.convertTaxPeriodToInt(dto.getReturnPeriod()));
					}
					header.setBatchId(dto.getBatchId());

					/**
					 * ATInvoices data
					 */
					header.setChksum(eachInv.getChksum());
					header.setDiffPercent(eachInv.getDiffPercent());
					header.setFlag(eachInv.getFlag());
					header.setPos(eachInv.getPos());
					header.setSuppType(eachInv.getSplyTy());

					// taxes at header level by summing the item values
					header.setAdvRecAmt(advRecAmt);
					header.setIgstAmt(igstAmt);
					header.setCgstAmt(cgstAmt);
					header.setSgstAmt(sgstAmt);
					header.setCessAmt(cessAmt);
					
					LocalDateTime convertNow = EYDateUtil
							.toUTCDateTimeFromLocal(LocalDateTime.now());
					header.setCreatedOn(convertNow);
					header.setCreatedBy("SYSTEM");

					String sgtin = dto.getGstin();
					String retPeriod = dto.getReturnPeriod();
					String type = eachInv.getSplyTy();
					String newPos = eachInv.getPos();
					header.setDocKey(gstr1GetKeyGenerator.generateAtKey(sgtin, retPeriod, type, newPos));

					header.setLineItems(lineItems);
					lineItems.forEach(item -> {
						item.setDocument(header);
					});

					headerList.add(header);
				}
			}
		/*} catch (Exception ex) {
			String msg = "failed to parse Gstr1 AT response";
			LOGGER.error(msg, ex);
			throw new APIException(msg);

		}*/
		return headerList;
	}

	@Override
	public List<GetGstr1AtaHeaderEntity> parseAtaData(Gstr1GetInvoicesReqDto dto, String apiResp) {

		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonArray respObject = null;
		List<GetGstr1AtaHeaderEntity> headerList = new ArrayList<>();

		//try {

			respObject = (new JsonParser()).parse(apiResp).getAsJsonObject().get(APIConstants.ATA).getAsJsonArray();

			Type listType = new TypeToken<List<ATInvoices>>() {
			}.getType();

			// List of Header and Item Data
			List<ATInvoices> baseEntity = gson.fromJson(respObject, listType);
			for (ATInvoices eachInv : baseEntity) {
				BigDecimal taxRate = BigDecimal.ZERO;
				BigDecimal advRecAmt = BigDecimal.ZERO;
				BigDecimal igstAmt = BigDecimal.ZERO;
				BigDecimal cgstAmt = BigDecimal.ZERO;
				BigDecimal sgstAmt = BigDecimal.ZERO;
				BigDecimal cessAmt = BigDecimal.ZERO;
				

				List<GetGstr1AtaItemEntity> lineItems = new ArrayList<>();

				if (eachInv.getItems() != null) {

					for (ATItemDetails eachInvData : eachInv.getItems()) {

						// New Item Entity
						GetGstr1AtaItemEntity item = new GetGstr1AtaItemEntity();
						/**
						 * ATaItemDetails data
						 */
						item.setReturnPeriod(dto.getReturnPeriod());
						if (dto.getReturnPeriod() != null && dto.getReturnPeriod().length() > 0) {
							item.setDerivedTaxperiod(GenUtil.convertTaxPeriodToInt(dto.getReturnPeriod()));
						}

						item.setAdvRecAmt(eachInvData.getAdAmt());
						item.setCgstAmt(eachInvData.getCamt());
						item.setCessAmt(eachInvData.getCsamt());
						item.setIgstAmt(eachInvData.getIamt());
						item.setTaxRate(eachInvData.getRt());
						item.setSgstAmt(eachInvData.getSamt());

						lineItems.add(item);
						// Header Amounts count
						if (item.getTaxRate() != null) {
							taxRate = taxRate.add(item.getTaxRate());
						}
						if (item.getAdvRecAmt() != null) {
							advRecAmt = advRecAmt.add(item.getAdvRecAmt());
						}
						if (item.getIgstAmt() != null) {

							igstAmt = igstAmt.add(item.getIgstAmt());
						}
						if (item.getCgstAmt() != null) {
							cgstAmt = cgstAmt.add(item.getCgstAmt());
						}
						if (item.getSgstAmt() != null) {

							sgstAmt = sgstAmt.add(item.getSgstAmt());
						}
						if (item.getCessAmt() != null) {
							cessAmt = cessAmt.add(item.getCessAmt());
						}

					}
					// New Header Entity
					GetGstr1AtaHeaderEntity header = new GetGstr1AtaHeaderEntity();

					/**
					 * ATaInvoices data
					 */
					header.setChksum(eachInv.getChksum());
					header.setDiff_percent(eachInv.getDiffPercent());
					header.setFlag(eachInv.getFlag());
					header.setPos(eachInv.getPos());
					header.setSuppType(eachInv.getSplyTy());
					header.setOrgMonth(eachInv.getOmon());
					/**
					 * Input data
					 */
					header.setGstin(dto.getGstin());
					header.setReturnPeriod(dto.getReturnPeriod());
					if (dto.getReturnPeriod() != null && dto.getReturnPeriod().length() > 0) {
						header.setDerivedTaxperiod(GenUtil.convertTaxPeriodToInt(dto.getReturnPeriod()));
					}
					header.setBatchId(dto.getBatchId());
					
					
					// taxes at header level by summing the item values

					header.setAdvRecAmt(advRecAmt);
					header.setIgstAmt(igstAmt);
					header.setCgstAmt(cgstAmt);
					header.setSgstAmt(sgstAmt);
					header.setCessAmt(cessAmt);
					
					LocalDateTime convertNow = EYDateUtil
							.toUTCDateTimeFromLocal(LocalDateTime.now());
					header.setCreatedOn(convertNow);
					header.setCreatedBy("SYSTEM");

					String sgtin = dto.getGstin();
					String retPeriod = dto.getReturnPeriod();
					String type = eachInv.getSplyTy();
					String month = eachInv.getOmon();
					String newPos = eachInv.getPos();
					header.setDocKey(gstr1GetKeyGenerator.generateAtaKey(sgtin, retPeriod, type, month, newPos));

					header.setLineItems(lineItems);
					lineItems.forEach(item -> {
						item.setDocument(header);
					});
					headerList.add(header);
				}

			}
		/*}

		catch (Exception ex) {
			String msg = "failed to parse Gstr1 ATa response";
			LOGGER.error(msg, ex);
			throw new APIException(msg);
		}*/
		return headerList;
	}
}
