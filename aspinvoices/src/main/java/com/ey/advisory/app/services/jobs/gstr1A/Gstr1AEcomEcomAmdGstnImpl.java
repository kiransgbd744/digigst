package com.ey.advisory.app.services.jobs.gstr1A;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.services.jobs.gstr1.Gstr1InvoicesAtGstn;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIException;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;
import com.google.common.base.Strings;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Siva.Reddy
 *
 */
@Slf4j
@Component("Gstr1AEcomEcomAmdGstnImpl")
public class Gstr1AEcomEcomAmdGstnImpl implements Gstr1InvoicesAtGstn {

	@Autowired
	@Qualifier("Gstr1AEcomEcomAmdDataGstnImpl")
	Gstr1AEcomEcomAmdDataGstnImpl gstr1AEcomEcomAmdDataGstnImpl;

	@Override
	public Long findInvFromGstn(Gstr1GetInvoicesReqDto dto, String groupCode,
			String type, Long batchId) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.info("I am from findInvFromGstn method {}", type);
		}
		Gson gson = GsonUtil.newSAPGsonInstance();

		try {
			if (Strings.isNullOrEmpty(dto.getGroupcode())) {
				dto.setGroupcode(groupCode);
			}
			dto.setBatchId(batchId);
			String json = gson.toJson(dto);
			Long reqId = gstr1AEcomEcomAmdDataGstnImpl.findEcomDataGstn(dto,
					TenantContext.getTenantId(), type, json);
			return reqId;
		} catch (Exception ex) {
			String errMsg = String.format(
					"Unexpected error while executing Gstr1 Get SupEcom/SupEcom for Group %s",
					groupCode);
			LOGGER.error(errMsg, ex);
			throw new APIException(errMsg);
		}
	}
}
