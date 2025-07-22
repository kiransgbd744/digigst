package com.ey.advisory.app.services.jobs.gstr8;

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
 * @author Shashikant.Shukla
 *
 */
@Slf4j
@Component("Gstr8UrdUrdaAtGstnImpl")
public class Gstr8UrdUrdaAtGstnImpl implements Gstr8InvoicesAtGstn {

	@Autowired
	@Qualifier("Gstr8UrdUrdaDataAtGstnImpl")
	private Gstr8UrdUrdaDataAtGstn getGstr8UrdUrdaDataAtGstn;

	@Override
	public Long findInvFromGstn(Gstr1GetInvoicesReqDto dto,
			String groupCode, String type,Long batchId) {
		Long reqId = null;
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			
			if (dto.getGroupcode() == null
					|| dto.getGroupcode().trim().length() == 0) {
				dto.setGroupcode(groupCode);
			}
			dto.setType(type);
			dto.setBatchId(batchId);
			String json = gson.toJson(dto);

			reqId = getGstr8UrdUrdaDataAtGstn.findUrdUrdaDataAtGstn(dto,
					groupCode, type, json);

		} catch (Exception ex) {
			if (LOGGER.isErrorEnabled()) {
				LOGGER.error(ex.getMessage());
			}
			throw new APIException(
					"Unexpected error while executing Gstr1 Get B2b/B2ba");

		}
		return reqId;
	}
}
