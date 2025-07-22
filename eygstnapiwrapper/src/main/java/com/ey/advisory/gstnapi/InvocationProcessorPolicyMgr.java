package com.ey.advisory.gstnapi;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.ey.advisory.core.api.APIResponse;
import com.google.common.collect.ImmutableList;

/**
 * @author Khalid1.Khan
 *
 */
@Component("InvocationProcessorPolicyMgr")
public class InvocationProcessorPolicyMgr implements PolicyManager {

	private static final List<String> NO_RETRY_ERROR_CODES = ImmutableList.of(
			GstnApiWrapperConstants.NO_INVOICE_ERR_CODE1,
			GstnApiWrapperConstants.NO_INVOICE_ERR_CODE2,
			GstnApiWrapperConstants.NO_INVOICE_ERR_CODE3,
			GstnApiWrapperConstants.NO_INVOICE_ERR_CODE4,
			GstnApiWrapperConstants.NO_INVOICE_ERR_CODE5,
			GstnApiWrapperConstants.NO_INVOICE_ERR_CODE6,
			GstnApiWrapperConstants.NO_INVOICE_ERR_CODE7,
			GstnApiWrapperConstants.NO_INVOICE_ERR_CODE8,
			GstnApiWrapperConstants.NO_INVOICE_ERR_CODE9,
			GstnApiWrapperConstants.NO_INVOICE_ERR_CODE10,
			GstnApiWrapperConstants.NO_EINVOICE_ERR_CODE11,
			GstnApiWrapperConstants.NO_EINVOICE_ERR_CODE12,
			GstnApiWrapperConstants.NO_EINVOICE_ERR_CODE13);

	private static final String GSTN_MAINTENANCE_ERROR_CODE = "GEN5008";
	
	private static final String EINVOICE_FILE_GENERATION_INPROGRESS = "EINV30109";
	
	private static final String IMS_GENERATION_INPROGRESS = "IMS_FILEGEN_03";

	
	@Override
	public RetryAction evaluteRetryPolicy(String retryBlockName,
			RetryInfo retryInfo, Map<String, Object> retryCtxMap,
			ExecResult<Boolean> execResult, ExecResult<Boolean> handlerResult) {
		@SuppressWarnings("unchecked")
		ExecResult<APIResponse> completeExecResult = retryCtxMap
				.containsKey(RetryMapKeysConstants.RESPONSE_ENTITY)
						? (ExecResult<APIResponse>) retryCtxMap
								.get(RetryMapKeysConstants.RESPONSE_ENTITY)
						: null;
		if (completeExecResult != null
				&& completeExecResult.getExecErrCode() != null
				&& NO_RETRY_ERROR_CODES
						.contains(completeExecResult.getExecErrCode())) {
			return new RetryAction(RetryActionType.MARK_AS_FAILED, retryInfo,
					1);
		}
		
		if (completeExecResult != null
				&& completeExecResult.getExecErrCode() != null
				&& EINVOICE_FILE_GENERATION_INPROGRESS.equalsIgnoreCase(
						completeExecResult.getExecErrCode())) {
			return new RetryAction(RetryActionType.RETRY_LATER, retryInfo,
					30);
		}
		
		if (completeExecResult != null
				&& completeExecResult.getExecErrCode() != null
				&& IMS_GENERATION_INPROGRESS.equalsIgnoreCase(
						completeExecResult.getExecErrCode())) {
			return new RetryAction(RetryActionType.RETRY_LATER, retryInfo,
					30);
		}
		if (retryInfo.getImmediateRetryCount() < 1 && !retryBlockName.equalsIgnoreCase(
				GstnApiWrapperConstants.FILE_CHUNK_RESPONSE_RETRY_BLOCK)) {
			return new RetryAction(RetryActionType.RETRY_IMMEDIATELY, retryInfo,
					1);
		} else if (retryInfo.getDelayedRetryCount() < 1
				&& !isTokenCase(retryBlockName)) {
			return new RetryAction(RetryActionType.RETRY_LATER, retryInfo, 1);
		}
		return new RetryAction(RetryActionType.MARK_AS_FAILED, retryInfo, 1);
	}

	private boolean isTokenCase(String retryBlockName) {
		return retryBlockName.equalsIgnoreCase(
				GstnApiWrapperConstants.TOKEN_RESPONSE_RETRY_BLOCK)
				|| retryBlockName.equalsIgnoreCase(
						GstnApiWrapperConstants.FILE_CHUNK_RESPONSE_RETRY_BLOCK);
	}

}
