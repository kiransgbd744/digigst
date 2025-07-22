/**
 * 
 */
package com.ey.advisory.services.gstr3b.itc.reclaim;

import java.sql.Clob;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.GstnUserRequestEntity;
import com.ey.advisory.app.data.repositories.client.GstnUserRequestRepository;
import com.ey.advisory.app.data.repositories.client.Gstr3BGstinItcReclaimRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIExecutor;
import com.ey.advisory.core.api.APIIdentifiers;
import com.ey.advisory.core.api.APIParam;
import com.ey.advisory.core.api.APIParams;
import com.ey.advisory.core.api.APIProviderEnum;
import com.ey.advisory.core.api.APIResponse;
import com.ey.advisory.core.api.impl.APIError;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */

@Slf4j
@Component("Gstr3BGetITCReclaimServiceImpl")
public class Gstr3BGetITCReclaimServiceImpl
		implements Gstr3BGetITCReclaimService {

	@Autowired
	@Qualifier("DefaultAPIExecutor")
	private APIExecutor apiExecutor;
	
	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	GSTNAuthTokenService authTokenService;

	@Autowired
	GstnUserRequestRepository gstnUserRequestRepo;
	
	@Autowired
	@Qualifier("Gstr3BGstinItcReclaimRepository")
	private Gstr3BGstinItcReclaimRepository itcReclaimRepo;

	@Override
	public String get3BReclaimAmount(String gstin, String taxPeriod) {

		Gson gson = GsonUtil.newSAPGsonInstance();

		try {

			String groupCode = TenantContext.getTenantId();
			
			String authStatus = authTokenService
					.getAuthTokenStatusForGstin(gstin);
			if (!authStatus.equals("A")) {
				
				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(
							"Gstr3BGetITCReclaimServiceImpl invoking "
									+ "gstr3BApiCall() method with gstin %s, "
									+ "taxPeriod %s, groupCode %s  "
								+ "Auth Token is Inactive, Please Activate: ",
							gstin, taxPeriod, groupCode);
					LOGGER.debug(msg);
				}
				String msg = "Auth Token is Inactive, Please Activate";
				throw new AppException (msg);
				
			}

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Gstr3BGetITCReclaimServiceImpl invoking "
								+ "gstr3BApiCall() method with gstin %s, "
								+ "taxPeriod %s, groupCode %s  : ",
						gstin, taxPeriod, groupCode);
				LOGGER.debug(msg);
			}
			
			

			APIResponse apiResponse = gstr3BApiCall(groupCode, gstin,
					taxPeriod);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("After invoking Save3B Get ITC Reversal"
						+ ".gstr3BApiCall() method " + "json response : {}",
						apiResponse);
			}

			if (apiResponse == null) {

				gstnUserRequestRepo.updateGstnResponse(null, 0, gstin,
						taxPeriod, APIConstants.GSTR3B_ITC_RECLAIM,
						LocalDateTime.now());
				String msg = "GSTIN has returned NULL response";
				LOGGER.error(msg);
				return msg;
			}

			if (!apiResponse.isSuccess()) {
				gstnUserRequestRepo.updateGstnResponse(null, 0, gstin,
						taxPeriod, APIConstants.GSTR3B_ITC_RECLAIM,
						LocalDateTime.now());
				APIError error = apiResponse.getError();
				String msg = "GSTIN has returned " + error.getErrorDesc();
				LOGGER.error(msg);
				new AppException(msg);
			}
			
			if (apiResponse.isSuccess()) {

				// save data into gstn user table
				saveOrUpdateGstnUserRequest(gstin, taxPeriod,
						apiResponse.getResponse());
				
				Gstr3BItcReclaimParseDto reqDto = gson.fromJson(apiResponse
						.getResponse(),Gstr3BItcReclaimParseDto.class);
				
				saveRespToDb(reqDto, taxPeriod);
				
			}
			

		}
		
		catch (Exception ex) {
			LOGGER.error(
					"Exception occured in Gstr3BGetITCReclaimServiceImpl {} ",
					ex);
			throw new AppException(ex);
		}

		return "success";

	}

	private void saveRespToDb(Gstr3BItcReclaimParseDto reqDto, String taxPeriod) {
		
		String userName = (SecurityContext.getUser() != null
				&& SecurityContext.getUser()
						.getUserPrincipalName() != null)
								? SecurityContext.getUser()
										.getUserPrincipalName()
								: "SYSTEM";
	
		Gstr3BGstinItcReclaimEntity entity = new Gstr3BGstinItcReclaimEntity();
		
		entity.setCess(reqDto.getClsbal().getCess());
		entity.setCgst(reqDto.getClsbal().getCgst());
		entity.setCreateDate(LocalDateTime.now());
		entity.setCreatedBy(userName);
		entity.setGstin(reqDto.getGstin());
		entity.setIgst(reqDto.getClsbal().getIgst());
		entity.setIsActive(true);
		entity.setSectionName("GET_4D1(A)");
		entity.setSgst(reqDto.getClsbal().getSgst());
		entity.setTaxPeriod(taxPeriod);
		entity.setUpdatedBy(userName);
		entity.setUpdateDate(LocalDateTime.now());
		
		//updating Active flag 
		itcReclaimRepo.updateUserActiveFlag(taxPeriod, 
				reqDto.getGstin());
		
		itcReclaimRepo.save(entity);
		
	}

	public APIResponse gstr3BApiCall(String groupCode, String gstin,
			String retPeriod) {
		APIResponse resp = new APIResponse();
		try {
			APIParam param1 = new APIParam("gstin", gstin);
			APIParam param2 = new APIParam("ret_period", retPeriod);

			APIParams params = new APIParams(groupCode, APIProviderEnum.GSTN,
					APIIdentifiers.GSTR3B_ITC_RECLAIM, param1, param2);
			resp = apiExecutor.execute(params, null);

		} catch (Exception ex) {
			LOGGER.error("Exception while hitting Save3B Get ITC Reversal"
					+ " Balance call", ex);
			throw new AppException("Error in Gstn API call.", ex);
		}
		return resp;

	}

	private void saveOrUpdateGstnUserRequest(String gstin, String taxPeriod,
			String getGstnData) {

		GstnUserRequestEntity gstnUserRequestEntity = null;

		gstnUserRequestEntity = gstnUserRequestRepo
				.findByGstinAndTaxPeriodAndReturnType(gstin, taxPeriod,
						APIConstants.GSTR3B_ITC_RECLAIM);

		Clob reqClob;
		try {
			reqClob = new javax.sql.rowset.serial.SerialClob(
					getGstnData.toCharArray());

			if (gstnUserRequestEntity == null) {

				gstnUserRequestEntity = new GstnUserRequestEntity();

				String userName = (SecurityContext.getUser() != null
						&& SecurityContext.getUser()
								.getUserPrincipalName() != null)
										? SecurityContext.getUser()
												.getUserPrincipalName()
										: "SYSTEM";

				if (userName == null || userName.isEmpty()) {
					userName = "SYSTEM";
				}

				gstnUserRequestEntity.setGstin(gstin);
				gstnUserRequestEntity.setTaxPeriod(taxPeriod);
				gstnUserRequestEntity
						.setReturnType(APIConstants.GSTR3B_ITC_RECLAIM);
				gstnUserRequestEntity.setRequestType("GET");
				gstnUserRequestEntity.setRequestStatus(1);
				gstnUserRequestEntity.setCreatedBy(userName);
				gstnUserRequestEntity.setCreatedOn(LocalDateTime.now());
				gstnUserRequestEntity.setDelete(false);
				gstnUserRequestEntity.setGetResponsePayload(reqClob);
				gstnUserRequestEntity.setDerivedRetPeriod(
						Integer.valueOf(taxPeriod.substring(2)
								.concat(taxPeriod.substring(0, 2))));

				gstnUserRequestRepo.save(gstnUserRequestEntity);

			} else {
				gstnUserRequestRepo.updateGstnResponse(reqClob, 1, gstin,
						taxPeriod, APIConstants.GSTR3B_ITC_RECLAIM,
						LocalDateTime.now());

			}
		} catch (Exception e) {
			String msg = "Exception occured while persisting gstnUserRequest data";
			LOGGER.error(msg, e);

			throw new AppException(msg, e);
		}
	}

}
