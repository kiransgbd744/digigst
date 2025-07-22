package com.ey.advisory.app.services.jobs.gstr2a;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.GetGstr2aStagingImpgSezHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2aStagingImpgSezItemEntity;
import com.ey.advisory.app.docs.dto.ImpgSezItemDto;
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
@Component("Gstr2aImpgSezDataParserImpl")
public class Gstr2aImpgSezDataParserImpl implements Gstr2aImpgSezDataParser {

	@Autowired
	private GetGstr2aAmdhistUtil amdUtil;

	@Override
	public Pair<List<GetGstr2aStagingImpgSezHeaderEntity>, Set<String>> parseImpgSezData(
			Gstr1GetInvoicesReqDto dto, String apiResp, String type,
			Long batchId, LocalDateTime now) {
		Gson gson = GsonUtil.newSAPGsonInstance();

		JsonArray respObject = null;
		List<GetGstr2aStagingImpgSezHeaderEntity> headerList = new ArrayList<>();
		Set<String> uniqueKeys = new HashSet<String>();
		try {

			if (apiResp == null) {
				return null;
			}
			JsonObject asJsonObject = (new JsonParser()).parse(apiResp)
					.getAsJsonObject();
			if (asJsonObject == null) {
				return null;
			}
			respObject = asJsonObject.get(APIConstants.IMPGSEZ) != null
					? asJsonObject.get(APIConstants.IMPGSEZ).getAsJsonArray()
					: null;
			if (respObject == null) {
				return null;
			}

			Type listType = new TypeToken<List<ImpgSezItemDto>>() {
			}.getType();
			List<ImpgSezItemDto> baseEntity = gson.fromJson(respObject,
					listType);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Gstr2a GET ImpgSez call Data Paring is in execution.");
			}
			for (ImpgSezItemDto eachInv : baseEntity) {

				BigDecimal taxValue = BigDecimal.ZERO;
				BigDecimal igstAmt = BigDecimal.ZERO;
				BigDecimal cessAmt = BigDecimal.ZERO;

				List<GetGstr2aStagingImpgSezItemEntity> lineItems = new ArrayList<>();

				if (eachInv != null) {

					// New Item Entity
					GetGstr2aStagingImpgSezItemEntity item = new GetGstr2aStagingImpgSezItemEntity();
					/**
					 * ImpgSezItemDetails data
					 */

					if (dto.getReturnPeriod() != null
							&& dto.getReturnPeriod().length() > 0) {
						item.setDerReturnPeriod(GenUtil
								.convertTaxPeriodToInt(dto.getReturnPeriod()));
					}
					item.setTaxableValue(eachInv.getTxValue());
					item.setIgstAmt(eachInv.getIgstAmount());
					item.setCessAmt(eachInv.getCessAmount());

					lineItems.add(item);

					// Header Amounts count
					if (eachInv.getTxValue() != null) {
						taxValue = taxValue.add(item.getTaxableValue());
					}
					if (eachInv.getIgstAmount() != null) {
						igstAmt = igstAmt.add(item.getIgstAmt());
					}
					if (eachInv.getCessAmount() != null) {
						cessAmt = cessAmt.add(item.getCessAmt());
					}
				}

				GetGstr2aStagingImpgSezHeaderEntity header = new GetGstr2aStagingImpgSezHeaderEntity();

				header.setGstin(dto.getGstin());

				String refDate = eachInv.getRefDate();
				if (refDate != null) {
					header.setBoeRefDate(String.format(refDate,
							DateUtil.SUPPORTED_DATE_FORMAT2));
				}
				header.setPortCode(eachInv.getPortCode());
				header.setBoeNum(eachInv.getBeNum());
				String beDate = eachInv.getBeDate();
				if (beDate != null) {
					header.setBoeCreatedDate(String.format(beDate,
							DateUtil.SUPPORTED_DATE_FORMAT2));
				}
				header.setSgstin(eachInv.getSgstin());
				header.setTradeName(eachInv.getTdname());
				header.setTaxValue(taxValue);
				header.setIgstAmt(igstAmt);
				header.setCessAmt(cessAmt);
				header.setIsAmdBoe(eachInv.getAmd());
				String returnPeriod = dto.getReturnPeriod();
				Integer derivedRetPeriod  = GenUtil.convertTaxPeriodToInt(
						dto.getReturnPeriod());
				header.setRetPeriod(returnPeriod);
				header.setDerRetPeriod(derivedRetPeriod);
				header.setBatchId(batchId);
				header.setCreatedBy(APIConstants.SYSTEM);
				header.setModifiedOn(LocalDateTime.now());
				LocalDateTime convertNow = EYDateUtil
						.toUTCDateTimeFromLocal(LocalDateTime.now());
				header.setCreatedOn(convertNow);
				header.setCreatedBy("SYSTEM");
				header.setLineItems(lineItems);
				lineItems.forEach(item -> {
					item.setHeader(header);
				});
				String amdHistKey = amdUtil.generateImpgSezKey(dto.getGstin(),
						eachInv.getSgstin(), eachInv.getPortCode(),
						DateUtil.parseObjToDate(beDate), eachInv.getBeNum());
				header.setAmdHistKey(amdHistKey);
				header.setInvKey(amdHistKey);
				headerList.add(header);
			}
		}

		catch (Exception ex) {
			String msg = "failed to parse Gstr2a ImpgSez response";
			LOGGER.error(msg, ex);
			throw new APIException(msg);
		}
		return new Pair<List<GetGstr2aStagingImpgSezHeaderEntity>, Set<String>>(
				headerList, uniqueKeys);
	}

}
