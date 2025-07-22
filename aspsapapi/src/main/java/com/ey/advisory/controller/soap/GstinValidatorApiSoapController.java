package com.ey.advisory.controller.soap;

import javax.jws.HandlerChain;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import com.ey.advisory.app.services.common.SoapCommonUtil;
import com.ey.advisory.app.services.jobs.erp.GstinValidationApiReqRespLogHelper;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.soap.integration.api.dto.GstinValidatorApiReqDocument;
import com.ey.advisory.soap.integration.api.dto.GstinValidatorPayloadHdrDataSoapDto;
import com.ey.advisory.soap.integration.api.dto.GstinValidatorSoapApiResponseDto;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Sakshi.jain
 *
 */
@Slf4j
@WebService(targetNamespace = "http://www.ey.com/digigst/wsapi", name = "GstinValidatorApiService")
@HandlerChain(file = "handler-chain.xml")
public class GstinValidatorApiSoapController {

	private GstinValidationApiReqRespLogHelper reqLogHelper;

	@WebMethod
	public @WebResult(name = "gen-gstvld-resp") GstinValidatorSoapApiResponseDto gstinValidatorApi(
			@WebParam(name = "gstvld-api-req") GstinValidatorPayloadHdrDataSoapDto req1) {
		if (TenantContext.getErrorMsg() != null) {
			throw new AppException(TenantContext.getErrorMsg());
		}
		reqLogHelper = StaticContextHolder.getBean(
				"GstinValidationApiReqRespLogHelper",
				GstinValidationApiReqRespLogHelper.class);
		GstinValidatorSoapApiResponseDto response;
		try {
			GstinValidatorApiReqDocument req = SoapCommonUtil
					.getGstinValidatorApiReq(req1);

			response = reqLogHelper.processRequest(req.getPayloadId(), "GSTIN_VALIDATOR",
					req.getCompanyCode(),req.getPushType(),req.getJsonString(),req.getCheckSum()); 
			return response;
		} catch (Exception e) {
			LOGGER.error("Error while parsing the Gene Inv Soap Request", e);
			response = new GstinValidatorSoapApiResponseDto();
			response.setStatus("E");
			response.setMsg("INTERNALEXP");
			return response;
		} finally {
			TenantContext.clearTenant();
		}
	}
}
