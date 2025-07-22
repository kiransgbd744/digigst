package com.ey.advisory.sap.functions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.common.AppException;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIExecutor;
import com.ey.advisory.core.api.APIParam;
import com.ey.advisory.core.api.APIParams;
import com.ey.advisory.core.api.APIProviderEnum;
import com.ey.advisory.core.api.APIResponse;
import com.ey.advisory.sap.controller.TestController;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Hemasundar.J
 *
 */
@RestController
@Component("gstnApiFramework")
@Slf4j
public class GstnApiFramework {

	@Autowired
	@Qualifier("DefaultNonStubExecutor")
	private APIExecutor apiExecutor;

	@PostMapping(value = "/otpCall")
	public APIResponse OtpCall(@RequestHeader("gstin") String gstin) {
		String groupCode = TestController.staticTenantId();
		
		//String gstin = "33GSPTN0481G1ZA";//Used for both new and old returns
		//String gstin = "33GSPTN0482G1Z9";//Used for old returns

		LOGGER.debug(String.format("One: %s", "1"));
		// SAP Tenant
		APIResponse resp = new APIResponse();
		try {

			TenantContext.setTenantId(groupCode);
			APIParam param1 = new APIParam("gstin", gstin);

			APIParams params = new APIParams(groupCode, APIProviderEnum.GSTN,
					"GET_OTP", "v0.2", param1);
			resp = apiExecutor.execute(params, "");
		} catch (Exception ex) {
			throw new AppException("Error in Gstn API Otp Genaration.", ex);
		} finally {
			TenantContext.clearTenant();
		}
		return resp;

	}

	@PostMapping(value = "/otpAuth")
	public APIResponse OtpAuthentication(@RequestHeader("otp") String otp, @RequestHeader("gstin") String gstin) {
		String groupCode = TestController.staticTenantId();
		//String gstin = "33GSPTN0481G1ZA";//Used for both new and old returns
		//String gstin = "33GSPTN0482G1Z9";//Used for old returns
		APIResponse resp = new APIResponse();
		// SAP Tenant
		try {
			TenantContext.setTenantId(groupCode);
			APIParam param1 = new APIParam("gstin", gstin);
			APIParam param3 = new APIParam("otp", otp);
			APIParams authParams = new APIParams(groupCode,
					APIProviderEnum.GSTN, "GET_AUTH_TOKEN", "v0.2", param1,
					param3);
			resp = apiExecutor.execute(authParams, "");
		} catch (Exception ex) {
			throw new AppException("Error in Gstn API Otp authentication.", ex);
		} finally {
			TenantContext.clearTenant();
		}
		return resp;

	}

	/*public APIResponse ApiCall(String groupCode, String data, String gstin,
			String ret_period) {
		LOGGER.debug(String.format("One: %s", "1"));
		// SAP Tenant
		APIResponse resp = new APIResponse();
		try {
			// JsonParser jsonParser = new JsonParser();
			TenantContext.setTenantId(groupCode);
			APIParam param1 = new APIParam("gstin", gstin);
			APIParam param2 = new APIParam("ret_period", ret_period);
			APIParams params = new APIParams(groupCode, APIProviderEnum.GSTN,
					JobCategoryConstants.GSTR1_SAVE, "v1.1", param1, param2);
			resp = apiExecutor.execute(params, data);
			// if(!resp.isSuccess()){
			// resp.getError().getErrorCode();
			// resp.getError().getErrorDesc();
			// }
			// String saveJsonResp = resp.getResponse();
			// JsonObject jsonObject = (JsonObject)
			// jsonParser.parse(saveJsonResp);
			// String ref_id = jsonObject.get("reference_id").getAsString();

			// APIParam param3 = new APIParam("ref_id", ref_id);
			// APIParams authParams = new APIParams(groupCode,
			// APIProviderEnum.GSTN, JobCategoryConstants.GSTR1_GET_SAVE_STATUS,
			// "v0.3", param1, param2, param3);
			// apiExecutor.execute(authParams, "");
			// mv.addObject("tenantAccountId", "test");
			// mv.addObject("name", "test");
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage(), ex);
			throw new AppException("Error in Gstn API call.", ex);
		} finally {
			TenantContext.clearTenant();
		}
		return resp;

	}

	public APIResponse getStatusByRefId(String groupCode, String ref_id,
			String gstin, String ret_period) {
		LOGGER.debug(String.format("One: %s", "1"));
		// SAP Tenant
		APIResponse resp = new APIResponse();
		try {
			TenantContext.setTenantId(groupCode);
			APIParam param1 = new APIParam("gstin", gstin);
			APIParam param2 = new APIParam("ret_period", ret_period);
			APIParam param3 = new APIParam("ref_id", ref_id);
			APIParams authParams = new APIParams(groupCode,
					APIProviderEnum.GSTN,
					JobCategoryConstants.GSTR1_GET_SAVE_STATUS, "v0.3", param1,
					param2, param3);
			apiExecutor.execute(authParams, "");
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage(), ex);
			throw new AppException("Error in reading Gstn API Response.", ex);
		} finally {
			TenantContext.clearTenant();
		}
		return resp;

	}
*/
}
