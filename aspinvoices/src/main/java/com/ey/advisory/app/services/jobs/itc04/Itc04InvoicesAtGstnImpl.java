package com.ey.advisory.app.services.jobs.itc04;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.repositories.client.GetAnx1BatchRepository;
import com.ey.advisory.app.util.Anx1GetBatchUtil;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.core.api.APIException;
import com.ey.advisory.core.dto.Itc04GetInvoicesReqDto;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Anand3.M
 *
 */
@Service("Itc04InvoicesAtGstnImpl")
@Slf4j
public class Itc04InvoicesAtGstnImpl implements Itc04InvoicesAtGstn {

	@Autowired
	@Qualifier("Itc04DataAtGstnImpl")
	private Itc04DataAtGstn itc04DataAtGstn;

	@Autowired
	@Qualifier("getAnx1BatchRepository")
	private GetAnx1BatchRepository batchRepo;

	@Autowired
	@Qualifier("anx1GetBatchUtil")
	private Anx1GetBatchUtil batchUtil;

	@Override
	public Long findInvFromGstn(Itc04GetInvoicesReqDto dto, String groupCode, String type, Long batchId) {
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
			reqId = itc04DataAtGstn.findItc04DataAtGstn(dto, groupCode, type, json);

		} catch (Exception ex) {

			if (LOGGER.isErrorEnabled()) {
				LOGGER.error(ex.getMessage());
			}
			throw new APIException("Unexpected error while executing Itc04 Get Invoices");

		}
		return reqId;
	}

}
