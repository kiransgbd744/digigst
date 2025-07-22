package com.ey.advisory.service.interest.gstr3b;

import java.sql.Clob;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.GstnUserRequestEntity;
import com.ey.advisory.app.data.repositories.client.GstnUserRequestRepository;
import com.ey.advisory.app.gstr3b.Gstr3BAutoCalConstants;
import com.ey.advisory.app.gstr3b.Gstr3BGetLiabilityAutoCalcHelper;
import com.ey.advisory.app.gstr3b.Gstr3BGstinsDto;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIExecutor;
import com.ey.advisory.core.api.APIIdentifiers;
import com.ey.advisory.core.api.APIParam;
import com.ey.advisory.core.api.APIParams;
import com.ey.advisory.core.api.APIProviderEnum;
import com.ey.advisory.core.api.APIResponse;
import com.ey.advisory.core.api.impl.APIError;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author vishal.verma
 *
 */
@Service("Gstr3BInterestGSNTGetServiceImpl")
@Slf4j
public class Gstr3BInterestGSNTGetServiceImpl
		implements Gstr3BInterestGSNTGetService {

	@Autowired
	@Qualifier("DefaultNonStubExecutor")
	private APIExecutor apiExecutor;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	GSTNAuthTokenService authTokenService;

	@Autowired
	@Qualifier("GstnUserRequestRepository")
	private GstnUserRequestRepository gstnUserRequestRepo;

	@Autowired
	Gstr3BGetLiabilityAutoCalcHelper gstr3BAutoCalcHelper;

	/**
	 * this method is invoking GSTN API.
	 */
	
	@Override
	public String getGstnInterestAutoCalc(String gstin, String taxPeriod) {

		APIResponse gstnApiResponse = null;
		String gstnResponse = null;
		String respBody = null;

		try {

			String authStatus = authTokenService
					.getAuthTokenStatusForGstin(gstin);
			if (authStatus.equals("A")) {

				// get gstn call

				gstnApiResponse = getGSTR3BAutoCalc(taxPeriod, gstin);

				if (gstnApiResponse.isSuccess()) {
					gstnResponse = gstnApiResponse.getResponse();

					if (LOGGER.isDebugEnabled()) {
						LOGGER.info(
								"Gstr3BInterestGSNTGetServiceImpl"
										+ ".getGstnInterestAutoCalc parsing the "
										+ "json response");
					}

					List<Gstr3BGstinsDto> resultList = getGstrList(
							gstnResponse);

					
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(
								"Gstr3BInterestGSNTGetServiceImpl"
					+ ".getGstnInterestAutoCalc persisting GSTR3B Interest "
					+ "auto calc response to GSTR3B_AUTO_CALC table");
					}
					
					// Saving Response to DB Table.
					gstr3BAutoCalcHelper.saveOrUpdateAutoCalc(gstin, taxPeriod,
							resultList, "Interest");

					// Saving Response to DB Table.
					saveAutoCalcDetails(gstin, taxPeriod, gstnResponse);

					respBody = "GSTR3B Interest Auto Calc Submitted Successfully";
				} else {
					APIError error = gstnApiResponse.getError();
					String msg = error.getErrorDesc();
					LOGGER.error(msg);
					respBody = msg;
					return respBody;
				}
			}

			else {
				respBody = "Auth Token is Inactive, Please Activate";
			}
		} catch (Exception e) {
			String msg = String.format("Internal error occured while get "
					+ "getGstnInterestAutoCalc {} ", e);
			LOGGER.error(msg);
			return msg;
		}

		return respBody;

	}

	private void saveAutoCalcDetails(String gstin, String taxPeriod,
			String gstnResponse) {

		GstnUserRequestEntity gstnUserRequestEntity = gstnUserRequestRepo
				.findByGstinAndTaxPeriodAndReturnType(gstin, taxPeriod,
						Gstr3BAutoCalConstants.GSTR3B);

		Clob reqClob;
		try {
			reqClob = new javax.sql.rowset.serial.SerialClob(
					gstnResponse.toCharArray());

			if (gstnUserRequestEntity == null) {

				gstnUserRequestEntity = new GstnUserRequestEntity();

				String userName = SecurityContext.getUser()
						.getUserPrincipalName() == null ? "SYSTEM"
								: SecurityContext.getUser()
										.getUserPrincipalName();

				gstnUserRequestEntity.setGstin(gstin);
				gstnUserRequestEntity.setTaxPeriod(taxPeriod);
				gstnUserRequestEntity.setReturnType(Gstr3BAutoCalConstants.GSTR3B);
				gstnUserRequestEntity.setRequestType("GET");
				gstnUserRequestEntity.setRequestStatus(1);
				gstnUserRequestEntity.setCreatedBy(userName);
				gstnUserRequestEntity.setCreatedOn(LocalDateTime.now());
				gstnUserRequestEntity.setDelete(false);
				gstnUserRequestEntity.setIntrtAutoCalcResponse(reqClob);

				gstnUserRequestRepo.save(gstnUserRequestEntity);

			} else {
				gstnUserRequestRepo.updateGstinInterestAutoCalGstnResponse(reqClob, 1,
						gstin, taxPeriod, Gstr3BAutoCalConstants.GSTR3B,
						LocalDateTime.now());

			}
		} catch (Exception e) {
			String msg = "Exception occured while persisting GSTR3B Interest "
					+ "auto calc response";
			LOGGER.error(msg, e);

			throw new AppException(msg, e);
		}

	}

	private APIResponse getGSTR3BAutoCalc(String taxPeriod, String gstin) {

		try {
			APIParam param1 = new APIParam("gstin", gstin);
			APIParam param2 = new APIParam("ret_period", taxPeriod);
			APIParams params = new APIParams(TenantContext.getTenantId(),
					APIProviderEnum.GSTN, APIIdentifiers
					.GSTR3B_GET_INTEREST_AUTO_CALC, param1, param2);
			return apiExecutor.execute(params, null);
		} catch (Exception ex) {
			String msg = String.format(
					"Exception while invoking '%s',"
							+ " GSTIN - '%s' and Taxperiod - '%s'",
					APIIdentifiers.GSTR3B_GET_INTEREST_AUTO_CALC, gstin, taxPeriod);
			LOGGER.error(msg, ex);
			throw new AppException(ex.getMessage());
		}
	}

	private List<Gstr3BGstinsDto> getGstrList(String gstnResponse) {

		List<Gstr3BGstinsDto> responseList = new ArrayList<>();
		Gstr3BGstinsDto gstr3bgstnDto = new Gstr3BGstinsDto();

		JsonParser jsonParser = new JsonParser();
		JsonObject parseResponse = (JsonObject) jsonParser
				.parse(gstnResponse).getAsJsonObject();

		JsonObject interest = parseResponse.has("interest") ? 
				parseResponse.getAsJsonObject("interest") : null;

		if (interest.has("csamt"))
			gstr3bgstnDto.setCess(interest.get("csamt").getAsBigDecimal());

		if (interest.has("camt"))
			gstr3bgstnDto.setCgst(interest.get("camt").getAsBigDecimal());

		if (interest.has("samt"))
			gstr3bgstnDto.setSgst(interest.get("samt").getAsBigDecimal());

		if (interest.has("iamt"))
			gstr3bgstnDto.setIgst(interest.get("iamt").getAsBigDecimal());
		
		gstr3bgstnDto.setSectionName("5.1(a)");
		gstr3bgstnDto.setSubSectionName("Intr");
		
		responseList.add(gstr3bgstnDto);

		return responseList;

	}

}

	
