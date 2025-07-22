package com.ey.advisory.app.util;

import static com.ey.advisory.core.api.APIIdentifiers.GSTR1_FILE;
import static com.ey.advisory.core.api.APIIdentifiers.GSTR1_SUBMIT;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.common.AppException;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIExecutor;
import com.ey.advisory.core.api.APIIdentifiers;
import com.ey.advisory.core.api.APIParam;
import com.ey.advisory.core.api.APIParams;
import com.ey.advisory.core.api.APIProviderEnum;
import com.ey.advisory.core.api.APIResponse;
import com.ey.advisory.domain.client.Gstr1SaveBatchEntity;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Hemasundar.J
 *
 */
@Service("hitGstnServer")
@Slf4j
public class HitGstnServer {

	@Autowired
	@Qualifier("DefaultAPIExecutor")
	private APIExecutor apiExecutor;

	public APIResponse gstr1ApiCall(String groupCode, String data, String gstin,
			String ret_period, Long batchId) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format("Gstr1 GSTN Save API Call Initiated."));
		}
		APIResponse resp = new APIResponse();
		/* try { */
		TenantContext.setTenantId(groupCode);
		APIParam param1 = new APIParam(APIIdentifiers.GSTIN, gstin);
		APIParam param2 = new APIParam(APIIdentifiers.RET_PERIOD, ret_period);
		APIParam param3 = new APIParam("batchId",
				batchId != null ? batchId.toString() : null);
		APIParams params = new APIParams(groupCode, APIProviderEnum.GSTN,
				APIIdentifiers.GSTR1_SAVE,
				/* APIIdentifiers.VERSION_1_1, */ param1, param2, param3);
		resp = apiExecutor.execute(params, data);
		/*
		 * } catch (Exception ex) { LOGGER.error(ex.getMessage(), ex); throw new
		 * AppException("Error in Gstn API call.", ex); } finally {
		 * TenantContext.clearTenant(); }
		 */
		return resp;

	}
	
	public APIResponse gstr1AApiCall(String groupCode, String data, String gstin,
			String ret_period, Long batchId) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format("Gstr1A GSTN Save API Call Initiated."));
		}
		APIResponse resp = new APIResponse();
		TenantContext.setTenantId(groupCode);
		APIParam param1 = new APIParam(APIIdentifiers.GSTIN, gstin);
		APIParam param2 = new APIParam(APIIdentifiers.RET_PERIOD, ret_period);
		APIParam param3 = new APIParam("batchId",
				batchId != null ? batchId.toString() : null);
		APIParams params = new APIParams(groupCode, APIProviderEnum.GSTN,
				APIIdentifiers.GSTR1A_SAVE,param1, param2, param3);
		resp = apiExecutor.execute(params, data);
		
		return resp;

	}

	public APIResponse anx1ApiCall(String groupCode, String data, String gstin,
			String ret_period, Long batchId) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format("Anx1 GSTN Save API Call Initiated."));
		}
		APIResponse resp = new APIResponse();
		/* try { */
		TenantContext.setTenantId(groupCode);
		APIParam param1 = new APIParam(APIIdentifiers.GSTIN, gstin);
		APIParam param2 = new APIParam(APIIdentifiers.RET_PERIOD, ret_period);
		APIParam param3 = new APIParam(APIConstants.RETURN_TYPE,
				APIConstants.ANX1_RETURN_TYPE);
		APIParam param4 = new APIParam("batchId",
				batchId != null ? batchId.toString() : null);
		APIParams params = new APIParams(groupCode, APIProviderEnum.GSTN,
				APIIdentifiers.ANX1_SAVE,
				/* APIIdentifiers.VERSION_1_0, */ param1, param2, param3,
				param4);
		resp = apiExecutor.execute(params, data);
		/*
		 * } catch (Exception ex) { LOGGER.error(ex.getMessage(), ex); throw new
		 * AppException("Error in Gstn API call.", ex); } finally {
		 * TenantContext.clearTenant(); }
		 */
		return resp;

	}

	public APIResponse anx2ApiCall(String groupCode, String data, String gstin,
			String ret_period, Long batchId) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format("Anx2 GSTN Save API Call Initiated."));
		}
		APIResponse resp = new APIResponse();
		/* try { */
		TenantContext.setTenantId(groupCode);
		APIParam param1 = new APIParam(APIIdentifiers.GSTIN, gstin);
		APIParam param2 = new APIParam(APIIdentifiers.RET_PERIOD, ret_period);
		APIParam param3 = new APIParam(APIConstants.RETURN_TYPE,
				APIConstants.ANX2_RETURN_TYPE);
		APIParam param4 = new APIParam("batchId",
				batchId != null ? batchId.toString() : null);
		APIParams params = new APIParams(groupCode, APIProviderEnum.GSTN,
				APIIdentifiers.ANX2_SAVE,
				/* APIIdentifiers.VERSION_1_0, */ param1, param2, param3,
				param4);
		resp = apiExecutor.execute(params, data);
		/*
		 * } catch (Exception ex) { LOGGER.error(ex.getMessage(), ex); throw new
		 * AppException("Error in Gstn API call.", ex); } finally {
		 * TenantContext.clearTenant(); }
		 */
		return resp;

	}

	public APIResponse getStatusByRefId(String groupCode, String ref_id,
			String gstin, String ret_period) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Gstr3B Transaction Polling Initiated.");
		}
		APIResponse resp = new APIResponse();
		try {
			APIParam param1 = new APIParam(APIIdentifiers.GSTIN, gstin);
			APIParam param2 = new APIParam(APIIdentifiers.RET_PERIOD,
					ret_period);
			APIParam param3 = new APIParam(APIIdentifiers.REF_ID, ref_id);

			APIParams authParams = new APIParams(groupCode,
					APIProviderEnum.GSTN, APIIdentifiers.GSTR1_GET_SAVE_STATUS,
					/* APIIdentifiers.VERSION_0_3, */ param1, param2, param3);
			resp = apiExecutor.execute(authParams, "");
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage(), ex);
			throw new AppException("Error in reading Gstn API Response.", ex);
		}
		return resp;

	}
	
	public APIResponse poolingApiCallForGstr1A(String groupCode, String gstin,
			String ret_period, String refId) {

		// Invoke the GSTN API - Get Return Status API and get the JSON.
		APIParam param1 = new APIParam(APIConstants.GSTIN, gstin);
		APIParam param2 = new APIParam(APIIdentifiers.RET_PERIOD, ret_period);
		APIParam param3 = new APIParam(APIIdentifiers.REF_ID, refId);
		APIParams params = new APIParams(groupCode, APIProviderEnum.GSTN,
				APIIdentifiers.GSTR1A_GET_SAVE_STATUS, param1, param2, param3);
		APIResponse resp = apiExecutor.execute(params, null);
		return resp;
	}
	
	
	

	public APIResponse gstr3BApiCall(String groupCode, String data,
			String gstin, String retPeriod) {
		LOGGER.debug(String.format("One: %s", "1"));
		APIResponse resp = new APIResponse();
		try {
			APIParam param1 = new APIParam("gstin", gstin);
			APIParam param2 = new APIParam("ret_period", retPeriod);

			APIParams params = new APIParams(groupCode, APIProviderEnum.GSTN,
					APIIdentifiers.GSTR3B_SAVE, /* "v0.3", */ param1, param2);
			resp = apiExecutor.execute(params, data);

		} catch (Exception ex) {
			LOGGER.error("Exception while hitting Save3B api call", ex);
			throw new AppException("Error in Gstn API call.", ex);
		}
		return resp;

	}

	public APIResponse gstr3BFileApiCall(String groupCode, String data,
			String gstin, String taxPeriod) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format(
					"Gstr3B Sign &_FILE API Call Initiated for GSTIN %S, taxPeriod %s",
					gstin, taxPeriod));
		}
		try {
			APIParam param1 = new APIParam(APIIdentifiers.GSTIN, gstin);
			APIParam param2 = new APIParam(APIIdentifiers.RET_PERIOD,
					taxPeriod);
			APIParams params = new APIParams(groupCode, APIProviderEnum.GSTN,
					APIIdentifiers.GSTR3B_FILE, param1, param2);
			return apiExecutor.execute(params, data);
		} catch (Exception ex) {
			LOGGER.error("Exception while hitting GSTR3B file api call", ex);
			throw new AppException("Error in Gstn API call.", ex);
		}

	}

	// -----------------------GSTR6----------------------------//
	public APIResponse gstr6ApiCall(String groupCode, String data, String gstin,
			String ret_period, Long batchId) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format("Gstr6 GSTN Save API Call Initiated."));
		}
		APIResponse resp = new APIResponse();
		TenantContext.setTenantId(groupCode);
		APIParam param1 = new APIParam(APIIdentifiers.GSTIN, gstin);
		APIParam param2 = new APIParam(APIIdentifiers.RET_PERIOD, ret_period);
		APIParam param3 = new APIParam(APIConstants.RETURN_TYPE,
				APIConstants.GSTR6_RETURN_TYPE);
		APIParam param4 = new APIParam("batchId",
				batchId != null ? batchId.toString() : null);
		APIParams params = new APIParams(groupCode, APIProviderEnum.GSTN,
				APIIdentifiers.GSTR6_SAVE,
				/* APIIdentifiers.VERSION_0_3, */ param1, param2, param3,
				param4);
		resp = apiExecutor.execute(params, data);
		return resp;
	}

	public APIResponse poolingApiCall(String groupCode, String gstin,
			String ret_period, String refId, Long batchId) {

		// Invoke the GSTN API - Get Return Status API and get the JSON.
		APIParam param1 = new APIParam(APIConstants.GSTIN, gstin);
		APIParam param2 = new APIParam(APIIdentifiers.RET_PERIOD, ret_period);
		APIParam param3 = new APIParam(APIIdentifiers.REF_ID, refId);
		APIParam param4 = new APIParam("batchId",
				batchId != null ? batchId.toString() : null);
		APIParams params = new APIParams(groupCode, APIProviderEnum.GSTN,
				APIIdentifiers.GSTR1_GET_SAVE_STATUS, param1, param2, param3,
				param4);
		APIResponse resp = apiExecutor.execute(params, null);
		return resp;
	}
	

	public static final String rtntyp = "rtntyp";
	public static final String refid = "refid";
	public static final String ret_period = "ret_period";
	public static final String txn = "txn";
	public static final String rtnprd = "rtnprd";
	public static final String P = "P";
	public static final String PE = "PE";
	public static final String ER = "ER";

	public APIResponse newPoolingApiCall(Gstr1SaveBatchEntity batch) {

		APIParam param5 = null;
		APIParam param6 = null;
		// Invoke the GSTN API - Get Return Status API and get the JSON.
		APIParam param1 = new APIParam(APIConstants.GSTIN, batch.getSgstin());
		APIParam param2 = new APIParam(rtnprd, batch.getReturnPeriod());
		APIParam param3 = new APIParam(APIIdentifiers.RET_PERIOD,
				batch.getReturnPeriod());
		APIParam param4 = new APIParam(refid, batch.getRefId());
		if (APIConstants.ANX1_RETURN_TYPE
				.equalsIgnoreCase(batch.getReturnType())) {
			param5 = new APIParam(rtntyp, APIConstants.ANX1_RETURN_TYPE);
			param6 = new APIParam(APIConstants.RETURN_TYPE,
					APIConstants.ANX1_RETURN_TYPE);
		}
		if (APIConstants.ANX2_RETURN_TYPE
				.equalsIgnoreCase(batch.getReturnType())) {
			param5 = new APIParam(rtntyp, APIConstants.ANX2_RETURN_TYPE);
			param6 = new APIParam(APIConstants.RETURN_TYPE,
					APIConstants.ANX2_RETURN_TYPE);
		}

		APIParam param7 = new APIParam(txn, batch.getTxnId());
		APIParam param8 = new APIParam("batchId",
				batch.getId() != null ? batch.getId().toString() : null);
		APIParams params = new APIParams(batch.getGroupCode(),
				APIProviderEnum.GSTN, APIIdentifiers.ANX1_GET_SAVE_STATUS,
				param1, param2, param3, param4, param5, param6, param7, param8);
		APIResponse resp = apiExecutor.execute(params, null);

		return resp;
	}

	public APIResponse gstr1SubmitApiCall(String groupCode, String data,
			String gstin, String ret_period) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					String.format("Gstr1 GSTN_SUBMIT API Call Initiated."));
		}
		APIResponse resp = new APIResponse();
		TenantContext.setTenantId(groupCode);
		APIParam param1 = new APIParam(APIIdentifiers.GSTIN, gstin);
		APIParam param2 = new APIParam(APIIdentifiers.RET_PERIOD, ret_period);

		APIParams params = new APIParams(groupCode, APIProviderEnum.GSTN,
				GSTR1_SUBMIT, /* APIIdentifiers.VERSION_1_1, */ param1, param2);
		resp = apiExecutor.execute(params, data);
		return resp;

	}

	public APIResponse gstr1GetSummaryApiCall(String groupCode, String data,
			String gstin, String ret_period) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					String.format("Gstr1 GSTN_SUBMIT API Call Initiated."));
		}
		APIParam param1 = new APIParam(APIIdentifiers.GSTIN, gstin);
		APIParam param2 = new APIParam(APIIdentifiers.RET_PERIOD, ret_period);

		APIParams params = new APIParams(groupCode, APIProviderEnum.GSTN,
				GSTR1_SUBMIT, param1, param2);
		return apiExecutor.execute(params, data);

	}

	public APIResponse gstr1FileApiCall(String groupCode, String data,
			String gstin, String ret_period) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format("Gstr1 GSTN_FILE API Call Initiated."));
		}
		APIResponse resp = new APIResponse();
		TenantContext.setTenantId(groupCode);
		APIParam param1 = new APIParam(APIIdentifiers.GSTIN, gstin);
		APIParam param2 = new APIParam(APIIdentifiers.RET_PERIOD, ret_period);

		APIParams params = new APIParams(groupCode, APIProviderEnum.GSTN,
				GSTR1_FILE, param1, param2);
		resp = apiExecutor.execute(params, data);
		return resp;

	}
	
	public APIResponse gstr1AFileApiCall(String groupCode, String data,
			String gstin, String ret_period) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format("Gstr1A GSTN_FILE API Call Initiated."));
		}
		APIResponse resp = new APIResponse();
		TenantContext.setTenantId(groupCode);
		APIParam param1 = new APIParam(APIIdentifiers.GSTIN, gstin);
		APIParam param2 = new APIParam(APIIdentifiers.RET_PERIOD, ret_period);

		APIParams params = new APIParams(groupCode, APIProviderEnum.GSTN,APIIdentifiers.GSTR1A_FILE, param1, param2);
		resp = apiExecutor.execute(params, data);
		return resp;

	}

	public APIResponse gstr6CalculateR6Call(String groupCode, String data,
			String gstin, String ret_period, Long batchId) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String
					.format("Gstr1 GSTR6_CALCULATE_R6 API Call Initiated."));
		}
		APIResponse resp = new APIResponse();
		TenantContext.setTenantId(groupCode);
		APIParam param1 = new APIParam(APIIdentifiers.GSTIN, gstin);
		APIParam param2 = new APIParam(APIIdentifiers.RET_PERIOD, ret_period);
		APIParam param3 = new APIParam("batchId",
				batchId != null ? batchId.toString() : null);

		APIParams params = new APIParams(groupCode, APIProviderEnum.GSTN,
				APIIdentifiers.GSTR6_CALCULATE_R6, param1, param2, param3);
		resp = apiExecutor.execute(params, data);
		return resp;

	}

	public APIResponse gstr6SaveCrossItcCall(String groupCode, String data,
			String gstin, String ret_period, Long batchId) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String
					.format("Gstr1 GSTR6_SAVE_CROSS_ITC API Call Initiated."));
		}
		APIResponse resp = new APIResponse();
		TenantContext.setTenantId(groupCode);
		APIParam param1 = new APIParam(APIIdentifiers.GSTIN, gstin);
		APIParam param2 = new APIParam(APIIdentifiers.RET_PERIOD, ret_period);
		APIParam param3 = new APIParam("batchId",
				batchId != null ? batchId.toString() : null);

		APIParams params = new APIParams(groupCode, APIProviderEnum.GSTN,
				APIIdentifiers.GSTR6_SAVE_CROSS_ITC, param1, param2, param3);
		resp = apiExecutor.execute(params, data);
		return resp;

	}

	// -----------------------GSTR7----------------------------//
	public APIResponse gstr7ApiCall(String groupCode, String data, String gstin,
			String ret_period, Long batchId) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format("Gstr7 GSTN Save API Call Initiated."));
		}
		APIResponse resp = new APIResponse();
		TenantContext.setTenantId(groupCode);
		APIParam param1 = new APIParam(APIIdentifiers.GSTIN, gstin);
		APIParam param2 = new APIParam(APIIdentifiers.RET_PERIOD, ret_period);
		APIParam param3 = new APIParam(APIConstants.RETURN_TYPE,
				APIConstants.GSTR7_RETURN_TYPE);
		/*
		 * APIParam param3 = new APIParam("rtn_typ",
		 * APIConstants.GSTR7_RETURN_TYPE);
		 */
		APIParam param4 = new APIParam("batchId",
				batchId != null ? batchId.toString() : null);
		/*
		 * APIParam param5 = new APIParam("userrole",
		 * APIConstants.GSTR7_RETURN_TYPE,APIParamType.HEADER);
		 */
		// APIParam param6 = new
		// APIParam("api_version","1.1",APIParamType.HEADER);
		APIParams params = new APIParams(groupCode, APIProviderEnum.GSTN,
				APIIdentifiers.GSTR7_SAVE, param1, param2, param3, param4);
		resp = apiExecutor.execute(params, data);
		return resp;
	}

	// -----------------------ITC04----------------------------//
	public APIResponse itc04ApiCall(String groupCode, String data, String gstin,
			String ret_period, Long batchId) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format("Itc04 GSTN Save API Call Initiated."));
		}
		APIResponse resp = new APIResponse();
		TenantContext.setTenantId(groupCode);
		APIParam param1 = new APIParam(APIIdentifiers.GSTIN, gstin);
		APIParam param2 = new APIParam(APIIdentifiers.RET_PERIOD, ret_period);
		APIParam param3 = new APIParam(APIConstants.RETURN_TYPE,
				APIConstants.ITC04_RETURN_TYPE);
		APIParam param4 = new APIParam("batchId",
				batchId != null ? batchId.toString() : null);
		APIParams params = new APIParams(groupCode, APIProviderEnum.GSTN,
				APIIdentifiers.ITC04_SAVE, param1, param2, param3, param4);
		resp = apiExecutor.execute(params, data);
		return resp;
	}

	// --------------------------GSTR6SUBMIT-------------------//
	public APIResponse gstr6SubmitApiCall(String groupCode, String data,
			String gstin, String ret_period) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					String.format("Gstr6 GSTN_SUBMIT API Call Initiated."));
		}
		APIResponse resp = new APIResponse();
		TenantContext.setTenantId(groupCode);
		APIParam param1 = new APIParam(APIIdentifiers.GSTIN, gstin);
		APIParam param2 = new APIParam(APIIdentifiers.RET_PERIOD, ret_period);
		APIParams params = new APIParams(groupCode, APIProviderEnum.GSTN,
				APIIdentifiers.GSTR6_SUBMIT,
				/* APIIdentifiers.VERSION_1_1, */ param1, param2);
		resp = apiExecutor.execute(params, data);
		return resp;
	}

	// -----------------------GSTR2XSAVE----------------------------//
	public APIResponse gstr2XApiCall(String groupCode, String data,
			String gstin, String ret_period, Long batchId) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format("Gstr2x GSTN Save API Call Initiated."));
		}
		APIResponse resp = new APIResponse();
		TenantContext.setTenantId(groupCode);
		APIParam param1 = new APIParam(APIIdentifiers.GSTIN, gstin);
		APIParam param2 = new APIParam(APIIdentifiers.RET_PERIOD, ret_period);
		APIParam param3 = new APIParam(APIConstants.RETURN_TYPE,
				APIConstants.GSTR2X_RETURN_TYPE);
		APIParam param4 = new APIParam("batchId",
				batchId != null ? batchId.toString() : null);
		APIParams params = new APIParams(groupCode, APIProviderEnum.GSTN,
				APIIdentifiers.GSTR2X_SAVE,
				/* APIIdentifiers.VERSION_0_3, */ param1, param2, param3,
				param4);
		resp = apiExecutor.execute(params, data);
		return resp;
	}

	// -----------------------GSTR7 SigninFile---------------------//

	public APIResponse gstr7FileApiCall(String groupCode, String data,
			String gstin, String ret_period) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format("Gstr7 GSTN_FILE API Call Initiated."));
		}
		APIResponse resp = new APIResponse();
		TenantContext.setTenantId(groupCode);
		APIParam param1 = new APIParam(APIIdentifiers.GSTIN, gstin);
		APIParam param2 = new APIParam(APIIdentifiers.RET_PERIOD, ret_period);

		APIParams params = new APIParams(groupCode, APIProviderEnum.GSTN,
				APIIdentifiers.GSTR7_FILE, param1, param2);
		resp = apiExecutor.execute(params, data);
		return resp;

	}

	// -----------------------ITC04 SiginFile--------------------//
	public APIResponse itc04FileApiCall(String groupCode, String data,
			String gstin, String ret_period) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format("Itc04 GSTN_FILE API Call Initiated."));
		}
		APIResponse resp = new APIResponse();
		TenantContext.setTenantId(groupCode);
		APIParam param1 = new APIParam(APIIdentifiers.GSTIN, gstin);
		APIParam param2 = new APIParam(APIIdentifiers.RET_PERIOD, ret_period);

		APIParams params = new APIParams(groupCode, APIProviderEnum.GSTN,
				APIIdentifiers.ITC04_FILE, param1, param2);
		resp = apiExecutor.execute(params, data);
		return resp;

	}

	// ---------------------------GSTR1DeleteFromGstn------------------------//
	public APIResponse gstr1DeleteApiCall(String groupCode, String data,
			String gstin, String ret_period, Long batchId) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format("Gstr1 GSTN Save API Call Initiated."));
		}
		APIResponse resp = new APIResponse();
		/* try { */
		TenantContext.setTenantId(groupCode);
		APIParam param1 = new APIParam(APIIdentifiers.GSTIN, gstin);
		APIParam param2 = new APIParam(APIIdentifiers.RET_PERIOD, ret_period);
		APIParam param3 = new APIParam("batchId",
				batchId != null ? batchId.toString() : null);
		APIParams params = new APIParams(groupCode, APIProviderEnum.GSTN,
				APIIdentifiers.GSTR1_RESET,
				/* APIIdentifiers.VERSION_1_1, */ param1, param2, param3);
		resp = apiExecutor.execute(params, data);
		return resp;
	}

	// ---------------------------GSTR9SaveToGstn------------------------//

	public APIResponse gstr9SaveApiCall(String data, String gstin,
			String retPeriod) {
		APIResponse resp = null;
		try {
			APIParam param1 = new APIParam("gstin", gstin);
			APIParam param2 = new APIParam("ret_period", retPeriod);
			APIParam param3 = new APIParam(APIConstants.RETURN_TYPE,
					APIConstants.GSTR9);
			APIParams params = new APIParams(TenantContext.getTenantId(),
					APIProviderEnum.GSTN, APIIdentifiers.GSTR9_SAVE, param1,
					param2, param3);
			resp = apiExecutor.execute(params, data);

		} catch (Exception ex) {
			LOGGER.error("Exception while hitting Save Gstr9 api call", ex);
			throw new AppException("Error in Gstn API call.", ex);
		}
		return resp;

	}

	// ---------------------------GSTR6Sign&File----------------------//

	public APIResponse gstr6FileApiCall(String groupCode, String data,
			String gstin, String ret_period) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format("Gstr6 GSTN_FILE API Call Initiated."));
		}
		APIResponse resp = new APIResponse();
		TenantContext.setTenantId(groupCode);
		APIParam param1 = new APIParam(APIIdentifiers.GSTIN, gstin);
		APIParam param2 = new APIParam(APIIdentifiers.RET_PERIOD, ret_period);

		APIParams params = new APIParams(groupCode, APIProviderEnum.GSTN,
				APIIdentifiers.GSTR6_FILE, param1, param2);
		resp = apiExecutor.execute(params, data);
		return resp;

	}

	// ---------------------------GSTR6ProceedToFile----------------------//

	public APIResponse gstnProceedToFileApiCall(String groupCode, String data,
			String gstin, String ret_period, String retTypeUseRolParam,
			boolean isNilAppc) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					String.format("GSTR6 PROCEED_TO_FILE API Call Initiated."));
		}
		APIResponse resp = new APIResponse();
		TenantContext.setTenantId(groupCode);
		APIParam param1 = new APIParam(APIIdentifiers.GSTIN, gstin);
		APIParam param2 = new APIParam(APIIdentifiers.RET_PERIOD, ret_period);

		APIParam returnTypeParam = new APIParam(APIConstants.RETURN_TYPE,
				retTypeUseRolParam);
		APIParams params = null;
		if (isNilAppc) {
			APIParam param3 = new APIParam(APIIdentifiers.ISNIL, "Y");
			params = new APIParams(groupCode, APIProviderEnum.GSTN,
					APIIdentifiers.GSTR6_PROCEED_TO_FILE, param1, param2,
					param3, returnTypeParam);
		} else {
			params = new APIParams(groupCode, APIProviderEnum.GSTN,
					APIIdentifiers.GSTR6_PROCEED_TO_FILE, param1, param2,
					returnTypeParam);
		}
		resp = apiExecutor.execute(params, data);
		return resp;
	}

	

	public APIResponse gstr1AProceedToFileApiCall(String groupCode, String data,
			String gstin, String ret_period, String retTypeUseRolParam,
			boolean isNilAppc) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					String.format("GSTR1A PROCEED_TO_FILE API Call Initiated."));
		}
		APIResponse resp = new APIResponse();
		TenantContext.setTenantId(groupCode);
		APIParam param1 = new APIParam(APIIdentifiers.GSTIN, gstin);
		APIParam param2 = new APIParam(APIIdentifiers.RET_PERIOD, ret_period);

		APIParam returnTypeParam = new APIParam(APIConstants.RETURN_TYPE,
				retTypeUseRolParam);
		APIParams params = null;
		if (isNilAppc) {
			APIParam param3 = new APIParam(APIIdentifiers.ISNIL, "Y");
			params = new APIParams(groupCode, APIProviderEnum.GSTN,
					APIIdentifiers.GSTR1A_PROCEED_TO_FILE, param1, param2,
					param3, returnTypeParam);
		} else {
			params = new APIParams(groupCode, APIProviderEnum.GSTN,
					APIIdentifiers.GSTR1A_PROCEED_TO_FILE, param1, param2,
					returnTypeParam);
		}
		resp = apiExecutor.execute(params, data);
		return resp;
	}
	// =============================DRC01B==================================

	public APIResponse drc01BFileApiCall(String groupCode, String data,
			String gstin, String taxPeriod) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					String.format(
							"DRC01B Sign &_FILE API Call Initiated for"
									+ " GSTIN %S, taxPeriod %s",
							gstin, taxPeriod));
		}
		try {
			APIParam param1 = new APIParam(APIIdentifiers.GSTIN, gstin);
			APIParam param2 = new APIParam(APIIdentifiers.RET_PERIOD,
					taxPeriod);
			APIParams params = new APIParams(groupCode, APIProviderEnum.GSTN,
					APIIdentifiers.DRC01B_FILE, param1, param2);
			return apiExecutor.execute(params, data);
		} catch (Exception ex) {
			LOGGER.error("Exception while hitting DRC01B file api call", ex);
			throw new AppException("Error in Gstn API call.", ex);
		}

	}

	// =============================DRC01C==================================

	public APIResponse drc01CFileApiCall(String groupCode, String data,
			String gstin, String taxPeriod) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					String.format(
							"DRC01C Sign &_FILE API Call Initiated for"
									+ " GSTIN %S, taxPeriod %s",
							gstin, taxPeriod));
		}
		try {
			APIParam param1 = new APIParam(APIIdentifiers.GSTIN, gstin);
			APIParam param2 = new APIParam(APIIdentifiers.RET_PERIOD,
					taxPeriod);
			APIParams params = new APIParams(groupCode, APIProviderEnum.GSTN,
					APIIdentifiers.DRC01C_FILE, param1, param2);
			return apiExecutor.execute(params, data);
		} catch (Exception ex) {
			LOGGER.error("Exception while hitting DRC01C file api call", ex);
			throw new AppException("Error in Gstn API call.", ex);
		}

	}

	// =============================GSTR8==================================

	public APIResponse gstr8SaveApiCall(String data, String gstin,
			String retPeriod, Long batchId) {
		APIResponse resp = null;
		try {
			APIParam param1 = new APIParam("gstin", gstin);
			APIParam param2 = new APIParam("ret_period", retPeriod);
			APIParam param3 = new APIParam(APIConstants.RETURN_TYPE,
					APIConstants.GSTR8.toUpperCase());
			APIParam param4 = new APIParam("batchId",
					batchId != null ? batchId.toString() : null);
			APIParams params = new APIParams(TenantContext.getTenantId(),
					APIProviderEnum.GSTN, APIIdentifiers.GSTR8_SAVE, param1,
					param2, param3, param4);
			resp = apiExecutor.execute(params, data);

		} catch (Exception ex) {
			LOGGER.error("Exception while hitting Save Gstr8 api call", ex);
			throw new AppException("Error in Gstn API call.", ex);
		}
		return resp;

	}
	
	// ---------------------------GSTR1ADeleteFromGstn------------------------//
		public APIResponse gstr1ADeleteApiCall(String groupCode, String data,
				String gstin, String ret_period, Long batchId) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(String.format("Gstr1A GSTN Save API Call Initiated."));
			}
			APIResponse resp = new APIResponse();
			/* try { */
			TenantContext.setTenantId(groupCode);
			APIParam param1 = new APIParam(APIIdentifiers.GSTIN, gstin);
			APIParam param2 = new APIParam(APIIdentifiers.RET_PERIOD, ret_period);
			APIParam param3 = new APIParam("batchId",
					batchId != null ? batchId.toString() : null);
			APIParams params = new APIParams(groupCode, APIProviderEnum.GSTN,
					APIIdentifiers.GSTR1A_RESET,
					/* APIIdentifiers.VERSION_1_1, */ param1, param2, param3);
			resp = apiExecutor.execute(params, data);
			return resp;
		}
		
		
		//----------------------------------GSTR2B Regenerate ----------------
		
		public APIResponse poolingApiCallForGstr2b(String groupCode, String gstin,
				String ret_period, String refId) {

			// Invoke the GSTN API - Get Return Status API and get the JSON.
			APIParam param1 = new APIParam(APIConstants.GSTIN, gstin);
			APIParam param2 = new APIParam(APIIdentifiers.RET_PERIOD, ret_period);
			APIParam param3 = new APIParam("int_tran_id", refId);
			APIParams params = new APIParams(groupCode, APIProviderEnum.GSTN,
					APIIdentifiers.GSTR2B_REG_SAVE_STATUS, param1, param2, param3);
			APIResponse resp = apiExecutor.execute(params, null);
			return resp;
		}
		
		//IMS Save
	public APIResponse imsSaveApiCall(String groupCode, String data,
			String gstin, Long batchId) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format("IMS Save API Call Initiated."));
		}
		APIResponse resp = null;
		APIParam param1 = new APIParam(APIIdentifiers.GSTIN, gstin);
		APIParam param2 = new APIParam("batchId",
				batchId != null ? batchId.toString() : null);
		APIParams params = new APIParams(groupCode, APIProviderEnum.GSTN,
				APIIdentifiers.IMS_SAVE, param1, param2);
		resp = apiExecutor.execute(params, data);

		return resp;

	}
	
	// IMS Reset
	public APIResponse imsResetApiCall(String groupCode, String data,
			String gstin, Long batchId) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format("IMS RESET API Call Initiated."));
		}
		APIResponse resp = null;
		APIParam param1 = new APIParam(APIIdentifiers.GSTIN, gstin);
		APIParam param2 = new APIParam("batchId",
				batchId != null ? batchId.toString() : null);
		APIParams params = new APIParams(groupCode, APIProviderEnum.GSTN,
				APIIdentifiers.IMS_RESET, param1, param2);
		resp = apiExecutor.execute(params, data);

		return resp;

	}
	
	//Ims Pooling
	public APIResponse imsPoolingApiCall(String groupCode, String refId, 
			String gstin) {

		// Invoke the GSTN API - Get Return Status API and get the JSON.
		APIParam param1 = new APIParam(APIConstants.GSTIN, gstin);
		APIParam param2 = new APIParam("int_tran_id", refId);
		APIParams params = new APIParams(groupCode, APIProviderEnum.GSTN,
				APIIdentifiers.IMS_SAVE_STATUS, param1, param2);
		APIResponse resp = apiExecutor.execute(params, null);
		return resp;
	}
}



