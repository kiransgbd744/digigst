/**
 * 
 */
package com.ey.advisory.core.api.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.core.api.APIParams;
import com.ey.advisory.core.api.GSTNAPIUtil;

/**
 * @author Hemasundar.J
 *
 */
@Service("SaveBatchReqAndRespPayloadDumpHelper")
public class SaveBatchReqAndRespPayloadDumpHelper {

	@Autowired
	@Qualifier("SaveBatchPayloadHandlerImpl")
	private SaveBatchPayloadHandler batch;

	public void dumpReqJsonPayload(APIParams params, String reqData) {

		String id = params.getApiIdentifier();

		if (GSTNAPIUtil.isSaveRequest(id)) {
			Long batchId = params.getAPIParamValue("batchId") != null
					? Long.parseLong(params.getAPIParamValue("batchId")) : null;
			if (batchId != null) {
				batch.dumpSaveRequestPayload(params.getGroupCode(), batchId,
						reqData);
			}
		}
	}

	public void dumpRespJsonPayload(APIParams params, String respData) {

		String id = params.getApiIdentifier();
		if (GSTNAPIUtil.isSaveRequest(id)) {
			Long batchId = params.getAPIParamValue("batchId") != null
					? Long.parseLong(params.getAPIParamValue("batchId")) : null;
			if (batchId != null) {
				batch.dumpSaveResponsePayload(params.getGroupCode(), batchId,
						respData);
			}
		} else if (GSTNAPIUtil.isGetReturnStatusRequest(id)) {
			Long batchId = params.getAPIParamValue("batchId") != null
					? Long.parseLong(params.getAPIParamValue("batchId")) : null;
			if (batchId != null) {
				batch.dumpGetReturnStatusResponsePayload(params.getGroupCode(),
						batchId, respData);
			}
		}
	}

}
