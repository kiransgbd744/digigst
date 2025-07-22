package com.ey.advisory.app.services.jobs.gstr6a;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.repositories.client.GetAnx1BatchRepository;
import com.ey.advisory.app.util.Anx1GetBatchUtil;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.core.api.APIException;
import com.ey.advisory.core.dto.Gstr6aGetInvoicesReqDto;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Anand3.M
 *
 */
@Service("Gstr6aCdnaInvoicesAtGstnImpl")
@Slf4j
public class Gstr6aCdnaInvoicesAtGstnImpl implements Gstr6aInvoicesAtGstn {

	@Autowired
	@Qualifier("Gstr6aDataAtGstnImpl")
	private Gstr6aDataAtGstn gstr6aDataAtGstn;

	@Autowired
	@Qualifier("getAnx1BatchRepository")
	private GetAnx1BatchRepository batchRepo;

	@Autowired
	@Qualifier("anx1GetBatchUtil")
	private Anx1GetBatchUtil batchUtil;

	@Override
	public Long findInvFromGstn(Gstr6aGetInvoicesReqDto dto, String groupCode, String type, Long batchId) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("I am from findCDNAFromGstn Method type: {}", type);
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
			reqId = gstr6aDataAtGstn.findGstr6aDataAtGstn(dto, groupCode, type, json);

		} catch (Exception ex) {

			if (LOGGER.isErrorEnabled()) {
				LOGGER.error(ex.getMessage());
			}
			throw new APIException("Unexpected error while executing Gstr6a Get Cdna");

		}
		return reqId;
	}

}
