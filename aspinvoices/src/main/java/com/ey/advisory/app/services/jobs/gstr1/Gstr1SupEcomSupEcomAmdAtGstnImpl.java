package com.ey.advisory.app.services.jobs.gstr1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.api.APIException;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Siva.Reddy
 *
 */
@Slf4j
@Component("Gstr1SupEcomSupEcomAmdAtGstnImpl")
public class Gstr1SupEcomSupEcomAmdAtGstnImpl implements Gstr1InvoicesAtGstn {

	@Autowired
	@Qualifier("Gstr1SupEcomSupEcomAmdDataAtGstnImpl")
	Gstr1SupEcomSupEcomAmdDataAtGstnImpl gstr1SupEcomSupEcomAmdDataAtGstnImpl;

	@Override
	public Long findInvFromGstn(Gstr1GetInvoicesReqDto dto, String groupCode,
			String type, Long batchId) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.info("I am from findInvFromGstn method {}", type);
		}
		Gson gson = GsonUtil.newSAPGsonInstance();

		try {
			if (dto.getGroupcode() == null
					|| dto.getGroupcode().trim().length() == 0) {
				dto.setGroupcode(groupCode);
			}
			dto.setBatchId(batchId);
			String json = gson.toJson(dto);
			Long reqId = gstr1SupEcomSupEcomAmdDataAtGstnImpl
					.findSupEcomDataATGstn(dto, groupCode, type, json);
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
