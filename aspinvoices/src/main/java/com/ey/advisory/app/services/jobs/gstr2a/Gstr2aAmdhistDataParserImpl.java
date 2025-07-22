package com.ey.advisory.app.services.jobs.gstr2a;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.GetGstr2aStagingAmdhistHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2aStagingAmdhistItemEntity;
import com.ey.advisory.app.docs.dto.AmdItemDto;
import com.ey.advisory.app.docs.dto.AmdhistDto;
import com.ey.advisory.common.DateUtil;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIException;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Anand3.M
 *
 */
@Slf4j
@Service("Gstr2aAmdhistDataParserImpl")
public class Gstr2aAmdhistDataParserImpl implements Gstr2aAmdhistDataParser {

	@Autowired
	private GetGstr2aAmdhistUtil amdUtil;
	
	
	@Override
	public List<GetGstr2aStagingAmdhistHeaderEntity> parseAmdData(Gstr1GetInvoicesReqDto dto, String apiResp, String type,
			Long batchId, LocalDateTime now) {

		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonArray respObject = null;
		List<GetGstr2aStagingAmdhistHeaderEntity> headerList = new ArrayList<>();

		try {
			LOGGER.error("AmdHist Json as apiResp {}", apiResp);
			if (apiResp == null) {
				return null;
			}
			JsonObject asJsonObject = (new JsonParser()).parse(apiResp)
					.getAsJsonObject();
			LOGGER.error("AmdHist Json as asJsonObject {}", asJsonObject);
			if (asJsonObject == null) {
				return null;
			}
			respObject = asJsonObject.get(APIConstants.AMDHIST) != null
					? asJsonObject.get(APIConstants.AMDHIST).getAsJsonArray()
					: null;
			LOGGER.error("AmdHist Json as respObject {}", respObject);
			if (respObject == null) {
				return null;
			}

			Type listType = new TypeToken<List<AmdhistDto>>() {
			}.getType();

			// List of Header and Item Data
			List<AmdhistDto> baseEntity = gson.fromJson(respObject, listType);

			for (AmdhistDto eachInv : baseEntity) {
				List<GetGstr2aStagingAmdhistItemEntity> lineItems = new ArrayList<>();
				// New Header Entity
				GetGstr2aStagingAmdhistHeaderEntity header = new GetGstr2aStagingAmdhistHeaderEntity();
				/**
				 * Input data
				 */
				header.setGstin(dto.getGstin());
				header.setPortCode(eachInv.getPortcd());
				header.setBoeNum(eachInv.getBenum());

				String bedate = (eachInv.getBedt());
				if (bedate != null) {
					header.setBoeDate(String.format(bedate, DateUtil.SUPPORTED_DATE_FORMAT2));
				}

				header.setRetPeriod(dto.getReturnPeriod());
				if (dto.getReturnPeriod() != null && dto.getReturnPeriod().length() > 0) {
					header.setDerRetPeriod(GenUtil.convertTaxPeriodToInt(dto.getReturnPeriod()));
				}
				header.setBatchId(batchId);

				for (AmdItemDto eachInvData : eachInv.getInv()) {

					BigDecimal amdTaxValue = BigDecimal.ZERO;
					BigDecimal amdIgstAmt = BigDecimal.ZERO;
					BigDecimal amdCessAmt = BigDecimal.ZERO;

					if (eachInvData != null) {

						// New Item Entity
						GetGstr2aStagingAmdhistItemEntity item = new GetGstr2aStagingAmdhistItemEntity();
						/**
						 * AmdItemDetails data
						 */

						if (dto.getReturnPeriod() != null && dto.getReturnPeriod().length() > 0) {
							item.setDerReturnPeriod(GenUtil.convertTaxPeriodToInt(dto.getReturnPeriod()));
						}
					
						item.setAmdTaxableValue(eachInvData.getTaxValue());
						item.setAmdIgstAmt(eachInvData.getIgstAmount());
						item.setAmdCessAmt(eachInvData.getCessAmount());

						lineItems.add(item);

						// Header Amounts count
						if (item.getAmdTaxableValue() != null) {
							amdTaxValue = amdTaxValue.add(item.getAmdTaxableValue());
						}
						if (item.getAmdIgstAmt() != null) {
							amdIgstAmt = amdIgstAmt.add(item.getAmdIgstAmt());
						}
						if (item.getAmdCessAmt() != null) {
							amdCessAmt = amdCessAmt.add(item.getAmdCessAmt());
						}

					}

					/**
					 * AmdInvoices data
					 */
					header.setAmdSgstin(eachInvData.getSgstin());
					header.setAmdTradeName(eachInvData.getTdName());
					String amdBoeRefDate = eachInvData.getRefDate();
					if (amdBoeRefDate != null) {
						header.setAmdBoeRefDate(String.format(amdBoeRefDate, DateUtil.SUPPORTED_DATE_FORMAT9));
					}

					// taxes at header level by summing the item values
					header.setAmdTaxVal(amdTaxValue);
					header.setAmdIgstAmt(amdIgstAmt);
					header.setAmdCessAmt(amdCessAmt);

					LocalDateTime convertNow = EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now());
					header.setCreatedOn(convertNow);
					header.setCreatedBy("SYSTEM");

					header.setLineItems(lineItems);
					lineItems.forEach(item -> {
						item.setHeader(header);
					});
					String amdHistKey = amdUtil.generateKeyAmdhist(eachInvData.getSgstin(),
							eachInv.getPortcd(), eachInv.getBenum(),
							DateUtil.parseObjToDate(eachInv.getBedt()));
					header.setAmdHistKey(amdHistKey);
					header.setParentSection(dto.getParentSection().toUpperCase());
					header.setInvKey(amdHistKey);
					headerList.add(header);
				}
			}

		} catch (Exception ex) {
			String msg = "failed to parse Gstr2a Amdhist response";
			LOGGER.error(msg, ex);
			throw new APIException(msg);
		}
		return headerList;
	}

}
