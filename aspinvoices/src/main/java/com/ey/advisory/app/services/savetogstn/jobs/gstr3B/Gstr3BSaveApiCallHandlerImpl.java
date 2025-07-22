package com.ey.advisory.app.services.savetogstn.jobs.gstr3B;

import org.apache.chemistry.opencmis.commons.impl.json.JSONObject;
import org.apache.chemistry.opencmis.commons.impl.json.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.repositories.client.Gstr3BSaveStatusRepository;
import com.ey.advisory.app.data.repositories.client.GstrReturnStatusRepository;
import com.ey.advisory.app.docs.dto.gstr3B.Gstr3BSavetoGstnDTO;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.app.util.HitGstnServer;
import com.ey.advisory.common.AppException;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIResponse;
import com.google.gson.Gson;

@Service("Gstr3BSaveApiCallHandlerImpl")
public class Gstr3BSaveApiCallHandlerImpl implements Gstr3BApiCallHandler {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr3BSaveApiCallHandlerImpl.class);

	@Autowired
	@Qualifier("hitGstnServer")
	private HitGstnServer gstnServer;

	@Autowired
	@Qualifier("gstrReturnStatusRepository")
	private GstrReturnStatusRepository gstrReturnStatusRepository;

	@Autowired
	@Qualifier("gstr3BSaveStatusRepository")
	private Gstr3BSaveStatusRepository gstr3BSaveStatusRepository;

	@Autowired
	@Qualifier("gstr3BSaveStatusEntryHandlerImpl")
	Gstr3BSaveStatusEntryHandlerImpl gstr3BSaveStatusEntryHandlerImpl;

	public void execute(Gstr3BSavetoGstnDTO dto, String groupCode) {

		Gson gson = GsonUtil.gsonInstanceWithExpose();
		try {
			String reqJson = gson.toJson(dto);
			String logMsg = String.format(
					"GSTR3B Save Request Payload for GSTIN '%s', TaxPeriod '%s' is '%s'",
					dto.getGstin(), dto.getRetPeriod(), reqJson);
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(logMsg);
			APIResponse resp = null;
			try {
				resp = gstnServer.gstr3BApiCall(groupCode, reqJson,
						dto.getGstin(), dto.getRetPeriod());
				logMsg = String.format("GSTR3B SAVE API response :'%s'", resp);
				LOGGER.debug(logMsg);
				if (resp.isSuccess()) {
					JSONObject refIdObj = (JSONObject) new JSONParser()
							.parse(resp.getResponse());
					String refId = refIdObj.get("reference_id").toString();
					gstrReturnStatusRepository.updateReturnStatus(
							APIConstants.SAVE_INITIATED, dto.getGstin(),
							dto.getRetPeriod(), APIConstants.GSTR3B);
					gstr3BSaveStatusEntryHandlerImpl
							.createGstr3BSaveStatusEntry(dto.getRetPeriod(),
									dto.getGstin(), refId,
									APIConstants.SAVE_INITIATED, null,
									groupCode, reqJson, resp.getResponse(),
									APIConstants.GSTR3B_SAVE);
				} else {
					gstrReturnStatusRepository.updateReturnStatus(
							APIConstants.SAVE_INITIATION_FAILED, dto.getGstin(),
							dto.getRetPeriod(), APIConstants.GSTR3B);
					gstr3BSaveStatusEntryHandlerImpl
							.createGstr3BSaveStatusEntry(dto.getRetPeriod(),
									dto.getGstin(), null,
									APIConstants.SAVE_INITIATION_FAILED, null,
									groupCode, reqJson,
									resp.getErrors().toString(),
									APIConstants.GSTR3B_SAVE);
				}
			} catch (Exception ex) {
				LOGGER.error("Exception while executing the SAVE3B api call",
						ex);
				gstrReturnStatusRepository.updateReturnStatus(
						APIConstants.SAVE_INITIATION_FAILED, dto.getGstin(),
						dto.getRetPeriod(), APIConstants.GSTR3B);
				throw new AppException(ex,
						"{} error while saving batch to Gstn");
			}

		} catch (Exception ex) {
			String msg = "Unexpected error while saving 3B documents to GSTN";
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}

	}

}
