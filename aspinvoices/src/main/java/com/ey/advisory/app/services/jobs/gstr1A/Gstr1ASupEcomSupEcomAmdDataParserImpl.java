package com.ey.advisory.app.services.jobs.gstr1A;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.GetGstr1SupEcomAmdHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr1SupEcomHeaderEntity;
import com.ey.advisory.app.data.gstr1A.entities.client.GetGstr1ASupEcomAmdHeaderEntity;
import com.ey.advisory.app.data.gstr1A.entities.client.GetGstr1ASupEcomHeaderEntity;
import com.ey.advisory.app.docs.dto.SupEcom;
import com.ey.advisory.app.docs.dto.SupEcomAmd;
import com.ey.advisory.app.docs.dto.SupEcomInvoices;
import com.ey.advisory.app.services.common.Gstr1GetKeyGenerator;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIException;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Siva.Reddy
 *
 */
@Component("Gstr1ASupEcomSupEcomAmdDataParserImpl")
@Slf4j
public class Gstr1ASupEcomSupEcomAmdDataParserImpl
		implements Gstr1ASupEcomSupEcomAmdDataParser {

	@Autowired
	private Gstr1GetKeyGenerator gstr1GetKeyGenerator;

	@Override
	public List<GetGstr1ASupEcomHeaderEntity> parseSupEcomData(
			Gstr1GetInvoicesReqDto dto, String apiResp) {

		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonArray respObject = null;
		List<GetGstr1ASupEcomHeaderEntity> headerList = new ArrayList<>();

		try {
			JsonObject asJsonObject = JsonParser.parseString(apiResp)
					.getAsJsonObject();
			if (asJsonObject == null) {
				LOGGER.error("API Resp is Null, hence returning Null.");
				return null;
			}

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("API Response is {} ", apiResp);
			}

			respObject = JsonParser.parseString(apiResp).getAsJsonObject()
					.get(APIConstants.SUPECO).getAsJsonArray();
			if (respObject == null) {
				return null;
			}

			SupEcom baseEntity = gson.fromJson(respObject, SupEcom.class);

			List<SupEcomInvoices> clttx = baseEntity.getClttx();
			List<SupEcomInvoices> paytx = baseEntity.getPaytx();

			for (SupEcomInvoices supEcomInvoices : clttx) {
				GetGstr1ASupEcomHeaderEntity header = new GetGstr1ASupEcomHeaderEntity();
				setSupEcomAttr(dto, supEcomInvoices, header);
				String sgtin = dto.getGstin();
				String retPeriod = dto.getReturnPeriod();
				String eTin = supEcomInvoices.getEtin();
				String tableSection = GSTConstants.GSTR1_14I;
				header.setDocKey(gstr1GetKeyGenerator.generateSupEcomKey(sgtin,
						retPeriod, tableSection, eTin));
				headerList.add(header);

			}

			for (SupEcomInvoices supEcomInvoices : paytx) {
				GetGstr1ASupEcomHeaderEntity header = new GetGstr1ASupEcomHeaderEntity();
				setSupEcomAttr(dto, supEcomInvoices, header);
				String sgtin = dto.getGstin();
				String retPeriod = dto.getReturnPeriod();
				String eTin = supEcomInvoices.getEtin();
				String tableSection = GSTConstants.GSTR1_14II;
				header.setDocKey(gstr1GetKeyGenerator.generateSupEcomKey(sgtin,
						retPeriod, tableSection, eTin));
				headerList.add(header);

			}
			return headerList;
		} catch (Exception e) {
			String errMsg = String.format(
					"Exception in SupEcom DataParsing for GSTIN %s and TaxPeriod %s",
					dto.getGstin(), dto.getReturnPeriod());
			LOGGER.error(errMsg, e);
			throw new APIException(errMsg);
		}
	}

	@Override
	public List<GetGstr1ASupEcomAmdHeaderEntity> parseSupEcomAmdData(
			Gstr1GetInvoicesReqDto dto, String apiResp) {

		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonArray respObject = null;
		List<GetGstr1ASupEcomAmdHeaderEntity> headerList = new ArrayList<>();

		try {
			JsonObject asJsonObject = JsonParser.parseString(apiResp)
					.getAsJsonObject();
			if (asJsonObject == null) {
				LOGGER.error("API Resp is Null, hence returning Null.");
				return null;
			}

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("API Response for SupEcomA is {} ", apiResp);
			}

			respObject = JsonParser.parseString(apiResp).getAsJsonObject()
					.get(APIConstants.SUPECOAMD).getAsJsonArray();
			if (respObject == null) {
				return null;
			}

			SupEcomAmd baseEntity = gson.fromJson(respObject, SupEcomAmd.class);

			List<SupEcomInvoices> clttx = baseEntity.getClttxa();
			List<SupEcomInvoices> paytx = baseEntity.getPaytxa();

			for (SupEcomInvoices supEcomInvoices : clttx) {
				GetGstr1ASupEcomAmdHeaderEntity header = new GetGstr1ASupEcomAmdHeaderEntity();
				setSupEcomAttrAmd(dto, supEcomInvoices, header);
				String sgtin = dto.getGstin();
				String retPeriod = dto.getReturnPeriod();
				String eTin = supEcomInvoices.getEtin();
				String tableSection = GSTConstants.GSTR1_14AI;
				header.setDocKey(gstr1GetKeyGenerator.generateSupEcomKey(sgtin,
						retPeriod, tableSection, eTin));
				headerList.add(header);

			}

			for (SupEcomInvoices supEcomInvoices : paytx) {
				GetGstr1ASupEcomAmdHeaderEntity header = new GetGstr1ASupEcomAmdHeaderEntity();
				setSupEcomAttrAmd(dto, supEcomInvoices, header);
				String sgtin = dto.getGstin();
				String retPeriod = dto.getReturnPeriod();
				String eTin = supEcomInvoices.getEtin();
				String tableSection = GSTConstants.GSTR1_14AII;
				header.setDocKey(gstr1GetKeyGenerator.generateSupEcomKey(sgtin,
						retPeriod, tableSection, eTin));
				headerList.add(header);

			}
			return headerList;
		} catch (Exception e) {
			String errMsg = String.format(
					"Exception in SupEcomAmd DataParsing for GSTIN %s and TaxPeriod %s",
					dto.getGstin(), dto.getReturnPeriod());
			LOGGER.error(errMsg, e);
			throw new APIException(errMsg);
		}
	}

	private void setSupEcomAttr(Gstr1GetInvoicesReqDto dto,
			SupEcomInvoices supEcomInvoices,
			GetGstr1ASupEcomHeaderEntity header) {
		header.setGstin(dto.getGstin());
		header.setReturnPeriod(dto.getReturnPeriod());
		if (dto.getReturnPeriod() != null
				&& dto.getReturnPeriod().length() > 0) {
			header.setDerivedTaxperiod(
					GenUtil.convertTaxPeriodToInt(dto.getReturnPeriod()));
		}
		header.setBatchId(dto.getBatchId());

		header.setEtin(supEcomInvoices.getEtin());
		header.setFlag(supEcomInvoices.getFlag());
		header.setSuppVal(supEcomInvoices.getSuppval());
		header.setIgstAmt(supEcomInvoices.getIgst());
		header.setCgstAmt(supEcomInvoices.getCgst());
		header.setSgstAmt(supEcomInvoices.getSgst());
		header.setCessAmt(supEcomInvoices.getCess());
		LocalDateTime convertNow = EYDateUtil
				.toUTCDateTimeFromLocal(LocalDateTime.now());
		header.setCreatedOn(convertNow);
		header.setCreatedBy("SYSTEM");
	}

	private void setSupEcomAttrAmd(Gstr1GetInvoicesReqDto dto,
			SupEcomInvoices supEcomInvoices,
			GetGstr1ASupEcomAmdHeaderEntity header) {
		header.setGstin(dto.getGstin());
		header.setReturnPeriod(dto.getReturnPeriod());
		if (dto.getReturnPeriod() != null
				&& dto.getReturnPeriod().length() > 0) {
			header.setDerivedTaxperiod(
					GenUtil.convertTaxPeriodToInt(dto.getReturnPeriod()));
		}
		header.setBatchId(dto.getBatchId());
		header.setEtin(supEcomInvoices.getEtin());
		header.setFlag(supEcomInvoices.getFlag());
		header.setSuppVal(supEcomInvoices.getSuppval());
		header.setIgstAmt(supEcomInvoices.getIgst());
		header.setCgstAmt(supEcomInvoices.getCgst());
		header.setSgstAmt(supEcomInvoices.getSgst());
		header.setCessAmt(supEcomInvoices.getCess());
		LocalDateTime convertNow = EYDateUtil
				.toUTCDateTimeFromLocal(LocalDateTime.now());
		header.setCreatedOn(convertNow);
		header.setCreatedBy("SYSTEM");
	}
}
