package com.ey.advisory.app.inward.einvoice;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.api.APIExecutor;
import com.ey.advisory.core.api.APIIdentifiers;
import com.ey.advisory.core.api.APIParam;
import com.ey.advisory.core.api.APIParams;
import com.ey.advisory.core.api.APIProviderEnum;
import com.ey.advisory.core.api.APIResponse;
import com.ey.advisory.core.api.impl.APIError;
import com.ey.advisory.core.config.ConfigManager;
import com.ey.advisory.einv.common.JwtParserUtility;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;

@Service("GetInwardIrnSearchDetailsHandler")
@Slf4j
public class GetInwardIrnSearchDetailsHandler
		implements GetInwardIrnSearchDetailsService {

	@Autowired
	@Qualifier("DefaultNonStubExecutor")
	private APIExecutor apiExecutor;

	/*@Autowired
	private GetIrnSearchDtlPayloadRepository getIrnSearchDtlPayloadRepo;*/

	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	GSTNAuthTokenService gSTNAuthTokenService;

	public IrnSearchDetailsDto getIrnDtls(String irn,
			String groupCode, String activeGstin) {

		Gson gson = new Gson();
		IrnSearchDetailsDto resDto = new IrnSearchDetailsDto();
		//GetIrnSearchDetailsPayloadEntity succEntity = new GetIrnSearchDetailsPayloadEntity();

		//String selectedGstin = activeGstin.get(0);
		APIResponse apiResponse = getIrnDtlResp(irn, groupCode, activeGstin,
				null);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Get IRN Search Detail apiResponse {} "
							+ "groupcode {} for params {}",
					apiResponse.toString(), groupCode, irn);
		}

		if (!apiResponse.isSuccess()) {

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Get IRN Search Detail failure  block for irn {} ",
						irn);
			}

			List<APIError> apiRespErrors = apiResponse.getErrors();
			APIError respError = apiRespErrors.get(0);
			resDto.setIrn(irn);
			resDto.setErrCode(respError.getErrorCode());
			resDto.setErrMsg(respError.getErrorDesc());
			return resDto;
		}
		String apiResp = apiResponse.getResponse();

		JsonObject reqObj = JsonParser.parseString(apiResp).getAsJsonObject()
				.get("data").getAsJsonObject();

		GetIrnDtlsRespDto respDto = gson.fromJson(reqObj,
				GetIrnDtlsRespDto.class);

		Claims claims = JwtParserUtility.getJwtBodyWithoutSignature(
				reqObj.get("SignedInvoice").getAsString());
		Gson gsn = GsonUtil.gsonInstanceWithEWBDateFormat();

		String claimResp = gsn.toJson(claims);

		/*
		 * String requestObject = JsonParser.parseString(claimResp)
		 * .getAsJsonObject().get("data").getAsString();
		 */
		JsonObject signedObject = JsonParser.parseString(claimResp)
				.getAsJsonObject();
		String iss = signedObject.has("iss")
				? signedObject.get("iss").getAsString() : "DEV Nic";

		// Long longAckNo = requestObject.get("AckNo").getAsLong();
		// String stringAckNo = String.valueOf(longAckNo);
		/*
		 * GetIrnDtlsRespDto respDto = gson.fromJson(reqObj,
		 * GetIrnDtlsRespDto.class);
		 */
        
		resDto.setIrn(irn);
		if(respDto.getAckDt()!=null){
		resDto.setAckDate(respDto.getAckDt());
		}
		if(respDto.getIrnSts()!=null){
		resDto.setIrnSts(respDto.getIrnSts());
		}
		if(respDto.getCnlDt()!=null){
		resDto.setCnlDate(respDto.getCnlDt());
		}
		if(iss!=null){
		resDto.setIss(iss);
		}
		if(respDto.getAckNo()!=null){
		resDto.setAckNo(respDto.getAckNo());
		}
		if(reqObj.toString()!=null){
		resDto.setPayload(reqObj.toString());
		}
		/*Clob responseClob = GenUtil.convertStringToClob(reqObj.toString());
		succEntity.setPayload(responseClob);
		succEntity.setIrn(irn);
		succEntity.setCreatedOn(LocalDateTime.now());
		succEntity.setAckDate(resDto.getAckDt());
		succEntity.setAckNo(resDto.getAckNo());
		succEntity.setCnlDate(resDto.getCnlDt());
		succEntity.setIss(resDto.getIrpName());
		succEntity.setIrnSts(resDto.getIrnSts());
		getIrnSearchDtlPayloadRepo.save(succEntity);*/

		return resDto;

	}

	public APIResponse getIrnDtlResp(String irn, String groupCode, String gstin,
			String returnPrd) {
		APIResponse resp = new APIResponse();
		APIParam param1 = new APIParam("irn", irn);
		APIParam param2 = new APIParam("gstin", gstin);
		APIParams params = new APIParams(groupCode, APIProviderEnum.GSTN,
				APIIdentifiers.GET_IRN_DTL, param1, param2);

		return apiExecutor.execute(params, null);

	}

	private String checkForNull(JsonElement jsonElement) {
		return jsonElement == null || jsonElement.isJsonNull() ? ""
				: jsonElement.getAsString();
	}

}