package com.ey.advisory.app.services.jobs.gstr1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.repositories.client.GetAnx1BatchRepository;
import com.ey.advisory.app.util.Anx1GetBatchUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.api.APIException;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Anand3.M
 *
 */
@Service("Gstr1CdnurEInvAtGstnImpl")
@Slf4j
public class Gstr1CdnurEInvAtGstnImpl implements Gstr1InvoicesAtGstn {

	@Autowired
	@Qualifier("gstr1EInvDataAtGstnImpl")
	Gstr1EInvDataAtGstn gstr1EInvDataAtGstn;

	@Autowired
	@Qualifier("getAnx1BatchRepository")
	private GetAnx1BatchRepository batchRepo;

	@Autowired
	@Qualifier("anx1GetBatchUtil")
	private Anx1GetBatchUtil batchUtil;

	@Override
	public Long findInvFromGstn(Gstr1GetInvoicesReqDto dto, String groupCode, String type, Long batchId) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("I am from findCDNURFromGstn Method type: {}", type);
		}

		Long reqId = null;
		Gson gson = GsonUtil.newSAPGsonInstance();

		try {

			if (dto.getGroupcode() == null || dto.getGroupcode().trim().length() == 0) {
				dto.setGroupcode(groupCode);
			}
			dto.setBatchId(batchId);
			dto.setType(type);
			String json = gson.toJson(dto);
			reqId = gstr1EInvDataAtGstn.findEInvDataAtGstn(dto, groupCode, type, json);

		} catch (Exception ex) {

			if (LOGGER.isErrorEnabled()) {
				LOGGER.error(ex.getMessage());
			}
			throw new APIException("Unexpected error while executing Gstr1 Get Cdnur");
		}
		return reqId;
	}

}




