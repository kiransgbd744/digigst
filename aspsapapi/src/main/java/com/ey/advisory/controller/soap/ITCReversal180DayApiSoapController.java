package com.ey.advisory.controller.soap;

import javax.jws.HandlerChain;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import com.ey.advisory.app.common.ITCReversal180DayApiReqRespLogHelper;
import com.ey.advisory.app.services.common.SoapCommonUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.itc.reversal.api.dto.ITCReversal180DayApiReqDocument;
import com.ey.advisory.itc.reversal.api.dto.ITCReversal180DayApiResponseDto;
import com.ey.advisory.itc.reversal.api.dto.ITCReversal180DayApiSoapHdrRq;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Akhilesh.Yadav
 *
 */
@Slf4j
@WebService(targetNamespace = "http://www.ey.com/digigst/wsapi", name = "ITCReversalApiService")
@HandlerChain(file = "handler-chain.xml")
public class ITCReversal180DayApiSoapController {
	
	private ITCReversal180DayApiReqRespLogHelper reqLogHelper;

	@WebMethod
	public @WebResult(name = "itcrev-api-resp") ITCReversal180DayApiResponseDto itcReversalApi(
			@WebParam(name = "itcrev-api-req") ITCReversal180DayApiSoapHdrRq req1) {
		if (TenantContext.getErrorMsg() != null) {
			throw new AppException(TenantContext.getErrorMsg());
		}
		reqLogHelper = StaticContextHolder.getBean(
				"ITCReversal180DayApiReqRespLogHelper",
				ITCReversal180DayApiReqRespLogHelper.class);
		ITCReversal180DayApiResponseDto response;
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("lower version soap api request ", req1);
			}
			ITCReversal180DayApiReqDocument req = SoapCommonUtil
					.getITCReversal180DayApiReq(req1);
			
			response = reqLogHelper.processRequest(req.getPayloadId(), null,
					req.getCheckSum(),req.getDocCount(), req.getPushType(), req.getCompanyCode(),
					req.getSourceId(),req.getJsonString());
			return response;
		} catch (Exception e) {
			LOGGER.error("Error while parsing the Gene Inv Soap Request", e);
			response = new ITCReversal180DayApiResponseDto();
			response.setStatus("E");
			response.setMsg("INTERNALEXP");
			return response;
		} finally {
			TenantContext.clearTenant();
		}
	}


}
