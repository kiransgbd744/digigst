/**
 * 
 */
package com.ey.advisory.controller;

import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.business.dto.PayloadReqDto;
import com.ey.advisory.app.data.business.dto.PayloadRespDto;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.BusinessCriticalConstants;
import com.ey.advisory.common.PayloadCommUtility;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.domain.client.ERPRequestLogEntity;

import jakarta.jws.HandlerChain;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebResult;
import jakarta.jws.WebService;
import joptsimple.internal.Strings;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Arun.KA
 *
 */
@Component
@Slf4j
@WebService(targetNamespace = "http://www.ey.com/digigst/wsapi", name = "NicPayService", serviceName = "getPayloads")
@HandlerChain(file = "/handler-chain.xml")
public class ManagePayloadSoapController {

	private PayloadCommUtility payCommUtil;

	public static final DateTimeFormatter formatter = DateTimeFormatter
			.ofPattern("yyyy-MM-dd HH:mm:ss");

	@WebMethod
	public @WebResult(name = "nic-payload-resp") PayloadRespDto getPayloads(
			@WebParam(name = "nic-payload-req") PayloadReqDto hdr) {
		if (TenantContext.getErrorMsg() != null) {
			throw new AppException(TenantContext.getErrorMsg());
		}
		payCommUtil = StaticContextHolder.getBean("PayloadCommUtility",
				PayloadCommUtility.class);
		PayloadRespDto respDto = new PayloadRespDto();
		try {

			String apiType = hdr.getApiType();
			if (Strings.isNullOrEmpty(apiType)) {
				LOGGER.error("API Is Empty");
				throw new AppException("APIType is Mandatory");
			}
			Optional<ERPRequestLogEntity> ent = payCommUtil.payloadService(hdr);

			if (ent.isPresent()) {
				String reqBody = ent.get().getReqPayload();
				String nicReqPay = ent.get().getNicReqPayload();
				String nicResp = ent.get().getNicResPayload();
				String cloudTimeStamp = ent.get().getCloudTimestamp()
						.format(formatter);
				respDto.setReqBody(reqBody);
				respDto.setNicReqPayload(nicReqPay);
				respDto.setNicRespPayload(nicResp);
				respDto.setId(ent.get().getId());
				respDto.setCloudTimeStamp(cloudTimeStamp);
				respDto.setApiType(ent.get().getApiType());
			} else {
				respDto.setErrorMessage("No Error Records Available");
			}

			return respDto;
		} catch (Exception e) {
			LOGGER.error("Error while parsing the Gene Inv Soap Request", e);
			respDto.setErrCode(BusinessCriticalConstants.INTERNAL_ERROR_CODE);
			respDto.setErrorMessage(e.getMessage());
			return respDto;
		} finally {
			TenantContext.clearTenant();
		}
	}
}
