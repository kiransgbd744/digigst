package com.ey.advisory.app.gstr.taxpayerdetail;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.core.api.APIResponse;
import com.ey.advisory.core.api.impl.APIError;
import com.ey.advisory.core.config.ConfigManager;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;
import com.ey.advisory.gstnapi.services.GenerateAuthTokenService;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("taxPayerDetailsServiceImpl")
public class TaxPayerDetailsServiceImpl implements TaxPayerDetailsService {

	@Autowired
	@Qualifier("taxPayerDetailsDaoImpl")
	TaxPayerDetailsDao taxPayerDao;

	@Autowired
	@Qualifier("GenerateAuthTokenServiceImpl")
	GenerateAuthTokenService authTokenService;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	GSTNAuthTokenService gstnAuthService;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	private ConfigManager configManager;

	@Override
	public TaxPayerDetailsDto getTaxPayerDetails(String gstin,
			String groupCode) {
		TaxPayerDetailsDto respDto = new TaxPayerDetailsDto();
		APIResponse apiResponse = taxPayerDao.findTaxPayerDetails(gstin,
				groupCode);
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Following is Tax Payer Detailed "
							+ " Response Dto from Gstin  %s :",
					apiResponse.toString());
			LOGGER.debug(msg);
		}
		if (!apiResponse.isSuccess()) {

			List<APIError> apiRespErrors = apiResponse.getErrors();
			APIError respError = apiRespErrors.get(0);
			respDto.setGstin(gstin);
			respDto.setErrorCode(respError.getErrorCode());
			respDto.setErrorMsg(respError.getErrorDesc());
			return respDto;

		}
		String apiResp = apiResponse.getResponse();

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("Following is Tax Payer Detailed "
					+ " Response %s from Gstin  %s :", apiResp, gstin);
			LOGGER.debug(msg);
		}
		JsonObject requestObject = JsonParser.parseString(apiResp)
				.getAsJsonObject();

		respDto.setGstin(gstin);
		respDto.setConstOfBuss(checkForNull(requestObject.get("ctb")));
		respDto.setCentreJuri(checkForNull(requestObject.get("ctj")));
		respDto.setTaxPayType(checkForNull(requestObject.get("dty")));
		respDto.setLegalBussNam(checkForNull(requestObject.get("lgnm")));
		respDto.setTradeName(checkForNull(requestObject.get("tradeNam")));
		respDto.setNatOfBusiness(checkForNullNBA((requestObject.get("nba"))));
		String estatus = requestObject.has("einvoiceStatus")
				? ("Yes".equalsIgnoreCase(
						checkForNull(requestObject.get("einvoiceStatus")))
								? "Applicable" : "Not Applicable")
				: "Not Applicable";
		respDto.setEinvApplicable(estatus);
		if (requestObject.has("pradr")
				&& !requestObject.get("pradr").isJsonNull()) {
			JsonObject address = requestObject.get("pradr").getAsJsonObject();
			if (address.has("addr")) {
				JsonObject addDetails = address.get("addr").getAsJsonObject();
				respDto.setBuildingName(checkForNull(addDetails.get("bnm")));
				respDto.setStateName(checkForNull(addDetails.get("st")));
				respDto.setLocation(checkForNull(addDetails.get("loc")));
				respDto.setBuildingNo(checkForNull(addDetails.get("bno")));
				respDto.setStateName(checkForNull(addDetails.get("stcd")));
				respDto.setFloorNo(checkForNull(addDetails.get("flno")));
				respDto.setPincode(checkForNull(addDetails.get("pncd")));
				respDto.setStreet(checkForNull(addDetails.get("st")));
			}
		}
		respDto.setDateOfReg(checkForNull(requestObject.get("rgdt")));
		respDto.setStateJuri(checkForNull(requestObject.get("stj")));
		respDto.setDateOfCan(checkForNull(requestObject.get("cxdt")));
		respDto.setGstnStatus(checkForNull(requestObject.get("sts")));
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("Following is Tax Payer Detailed "
					+ " Response Send Back to UI {} ", respDto);
			LOGGER.debug(msg);
		}

		return respDto;
	}

	private String checkForNull(JsonElement jsonElement) {
		return jsonElement == null || jsonElement.isJsonNull() ? ""
				: jsonElement.getAsString();
	}

	private String checkForNullNBA(JsonElement jsonElement) {
		return jsonElement == null || jsonElement.isJsonNull() ? ""
				: jsonElement.getAsJsonArray().toString().replace("[", "")
						.replace("]", "");
	}

}
