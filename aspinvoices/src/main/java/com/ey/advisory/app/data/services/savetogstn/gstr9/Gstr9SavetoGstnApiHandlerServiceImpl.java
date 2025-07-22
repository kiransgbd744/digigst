package com.ey.advisory.app.data.services.savetogstn.gstr9;

import java.sql.Clob;

import org.apache.chemistry.opencmis.commons.impl.json.JSONObject;
import org.apache.chemistry.opencmis.commons.impl.json.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.client.GstrReturnStatusRepository;
import com.ey.advisory.app.data.repositories.client.gstr9.Gstr9SaveStatusRepository;
import com.ey.advisory.app.docs.dto.gstr9.GetDetailsForGstr9ReqDto;
import com.ey.advisory.app.util.HitGstnServer;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Jithendra.B
 *
 */

@Slf4j
@Component("Gstr9SavetoGstnApiHandlerServiceImpl")
public class Gstr9SavetoGstnApiHandlerServiceImpl
		implements Gstr9SavetoGstnApiHandlerService {

	@Autowired
	@Qualifier("hitGstnServer")
	private HitGstnServer gstnServer;

	@Autowired
	private GstrReturnStatusRepository gstrReturnStatusRepository;

	@Autowired
	private Gstr9SaveStatusRepository gstr9SaveStatusRepository;

	@Autowired
	@Qualifier("Gstr9SaveStatusEntryHandlerImpl")
	private Gstr9SaveStatusEntryHandler gstr9SaveStatusEntryHandler;

	@Override
	public void executeGstr9SaveToGstn(GetDetailsForGstr9ReqDto dto) {

//		Gson gson = GsonUtil.gsonInstanceWithExpose();
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();

		Clob reqClob = null;
		try {
			String reqJson = gson.toJson(dto);
			String logMsg = String.format(
					"GSTR9 Save Request Payload for GSTIN '%s', TaxPeriod '%s' is '%s'",
					dto.getGstin(), dto.getFp(), reqJson);

			reqClob = GenUtil.convertStringToClob(reqJson);

			if (LOGGER.isDebugEnabled())
				LOGGER.debug(logMsg);
			APIResponse resp = null;
			try {
				resp = gstnServer.gstr9SaveApiCall(reqJson, dto.getGstin(),
						dto.getFp());
				logMsg = String.format("GSTR9 SAVE API response :'%s'", resp);
				LOGGER.debug(logMsg);
				if (resp.isSuccess()) {
					JSONObject refIdObj = (JSONObject) new JSONParser()
							.parse(resp.getResponse());
					String refId = refIdObj.get("reference_id").toString();
					// gstrReturnStatusRepository.updateReturnStatus(
					// APIConstants.SAVE_INITIATED, dto.getGstin(),
					// dto.getFp(), APIConstants.GSTR9);

					gstr9SaveStatusRepository.updateRefIdAndErrors(refId,
							APIConstants.SAVE_INITIATED, dto.getGstin(),
							dto.getFp(), reqClob, resp.getResponse());

					// gstr9SaveStatusEntryHandler.createGstr9SaveStatusEntry(
					// dto.getFp(), dto.getGstin(), refId,
					// APIConstants.SAVE_INITIATED, null,
					// TenantContext.getTenantId(), reqJson,
					// resp.getResponse());
				} else {
					// gstrReturnStatusRepository.updateReturnStatus(
					// APIConstants.SAVE_INITIATION_FAILED, dto.getGstin(),
					// dto.getFp(), APIConstants.GSTR9);

					gstr9SaveStatusRepository.updateRefIdAndErrors(null,
							APIConstants.SAVE_INITIATION_FAILED, dto.getGstin(),
							dto.getFp(), reqClob, resp.getErrors().toString());

					// gstr9SaveStatusEntryHandler.createGstr9SaveStatusEntry(
					// dto.getFp(), dto.getGstin(), null,
					// APIConstants.SAVE_INITIATION_FAILED, null,
					// TenantContext.getTenantId(), reqJson,
					// resp.getErrors().toString());
				}
			} catch (Exception ex) {
				LOGGER.error(
						"Exception while executing the SAVE Gstr9 api call",
						ex);
				// gstrReturnStatusRepository.updateReturnStatus(
				// APIConstants.SAVE_INITIATION_FAILED, dto.getGstin(),
				// dto.getFp(), APIConstants.GSTR9);
				throw new AppException(ex,
						"{} error while saving batch to Gstn");
			}

		} catch (Exception ex) {
			String msg = "Unexpected error while saving Gstr9 documents to GSTN";
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}

	}

}
