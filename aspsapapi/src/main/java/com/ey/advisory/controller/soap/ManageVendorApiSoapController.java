package com.ey.advisory.controller.soap;

import javax.jws.HandlerChain;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import com.ey.advisory.app.services.common.SoapCommonUtil;
import com.ey.advisory.app.services.vendor.master.apipush.VendorMasterApiReqRespLogHelper;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.vendor.master.api.dto.VendorMasterApiReqDocument;
import com.ey.advisory.vendor.master.api.dto.VendorMasterApiResponseDto;
import com.ey.advisory.vendor.master.api.dto.VendorMasterApiSoapHdrRq;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Shashikant.Shukla
 *
 */
@Slf4j
@WebService(targetNamespace = "http://www.ey.com/digigst/wsapi", name = "VendorApiService")
@HandlerChain(file = "handler-chain.xml")
public class ManageVendorApiSoapController {

	private VendorMasterApiReqRespLogHelper reqLogHelper;

	@WebMethod
	public @WebResult(name = "gen-vendor-api-resp") VendorMasterApiResponseDto vendorMasterApi(
			@WebParam(name = "vendor-api-req") VendorMasterApiSoapHdrRq req1) {
		if (TenantContext.getErrorMsg() != null) {
			throw new AppException(TenantContext.getErrorMsg());
		}
		reqLogHelper = StaticContextHolder.getBean(
				"VendorMasterApiReqRespLogHelper",
				VendorMasterApiReqRespLogHelper.class);
		VendorMasterApiResponseDto response;
		try {
			VendorMasterApiReqDocument req = SoapCommonUtil
					.getVendorMasterApiReq(req1);

			response = reqLogHelper.processRequest(req.getPayloadId(), null,
					req.getPushType(), req.getJsonString(), req.getCheckSum(),
					req.getSourceId());
			return response;
		} catch (Exception e) {
			LOGGER.error("Error while parsing the Gene Inv Soap Request", e);
			response = new VendorMasterApiResponseDto();
			response.setStatus("E");
			response.setMsg("INTERNALEXP");
			return response;
		} finally {
			TenantContext.clearTenant();
		}
	}
}
