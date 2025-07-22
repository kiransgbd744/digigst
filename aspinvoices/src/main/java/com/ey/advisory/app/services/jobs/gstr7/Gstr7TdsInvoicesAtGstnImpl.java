package com.ey.advisory.app.services.jobs.gstr7;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.repositories.client.GetAnx1BatchRepository;
import com.ey.advisory.app.util.Anx1GetBatchUtil;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.core.api.APIException;
import com.ey.advisory.core.dto.Gstr7GetInvoicesReqDto;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Anand3.M
 *
 */
@Service("Gstr7TdsInvoicesAtGstnImpl")
@Slf4j
public class Gstr7TdsInvoicesAtGstnImpl implements Gstr7InvoicesAtGstn {

	@Autowired
	@Qualifier("Gstr7DataAtGstnImpl")
	private Gstr7DataAtGstn gstr7DataAtGstn;

	@Autowired
	@Qualifier("getAnx1BatchRepository")
	private GetAnx1BatchRepository batchRepo;

	@Autowired
	@Qualifier("anx1GetBatchUtil")
	private Anx1GetBatchUtil batchUtil;

	@Override
	public Long findInvFromGstn(Gstr7GetInvoicesReqDto dto, String groupCode,
			String type, Long batchId) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("I am from findTdsFromGstn Method type: {}", type);
		}

		Long reqId = null;
		Gson gson = GsonUtil.newSAPGsonInstance();

		try {

			dto.setGroupcode(groupCode);
			dto.setBatchId(batchId);
			dto.setType(type);
			String json = gson.toJson(dto);
			reqId = gstr7DataAtGstn.findGstr7DataAtGstn(dto, groupCode, type,
					json);
			return reqId;
		} catch (Exception ex) {
			String msg = "Exception While Find the Gstr7 Data In Gstr7TdsInvoicesAtGstnImpl";
			LOGGER.error(msg, ex);
			throw new APIException(msg);
		}
	}

}
