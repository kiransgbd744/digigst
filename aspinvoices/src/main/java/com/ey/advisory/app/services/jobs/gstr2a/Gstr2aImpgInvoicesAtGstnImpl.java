package com.ey.advisory.app.services.jobs.gstr2a;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.repositories.client.GetAnx1BatchRepository;
import com.ey.advisory.app.util.Anx1GetBatchUtil;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.core.api.APIException;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;
import com.ey.advisory.core.dto.Gstr2aDataAtGstn;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Anand3.M
 *
 */
@Service("Gstr2aImpgInvoicesAtGstnImpl")
@Slf4j
public class Gstr2aImpgInvoicesAtGstnImpl implements Gstr2aAmdhistInvoicesAtGstn {

	@Autowired
	@Qualifier("Gstr2aDataAtGstnImpl")
	private Gstr2aDataAtGstn gstr2aDataAtGstn;

	@Autowired
	@Qualifier("getAnx1BatchRepository")
	private GetAnx1BatchRepository batchRepo;

	@Autowired
	@Qualifier("anx1GetBatchUtil")
	private Anx1GetBatchUtil batchUtil;

	@Override
	public Long findInvFromGstn(Gstr1GetInvoicesReqDto dto, String groupCode, String type, Long batchId) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("I am from findTdsFromGstn Method type: {}", type);
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
			reqId = gstr2aDataAtGstn.findGstr2aDataAtGstn(dto, groupCode, type, json);

		} catch (Exception ex) {

			if (LOGGER.isErrorEnabled()) {
				LOGGER.error(ex.getMessage());
			}
			throw new APIException("Unexpected error while executing Gstr2a Get Impg");

		}
		return reqId;
	}

}
