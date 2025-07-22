package com.ey.advisory.processors.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.app.service.ims.ImsActioResponseReqDto;
import com.ey.advisory.app.service.ims.ImsRequestStatusService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Sakshi.jain
 *
 */
@Slf4j
@Component("ImsActionResponseUiProcessor")
public class ImsActionResponseUiProcessor implements TaskProcessor {

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository fileStatusRepository;

	@Autowired
	@Qualifier("ImsRequestStatusServiceImpl")
	ImsRequestStatusService reqService;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;


	
	@Override
	public void execute(Message message, AppExecContext context) {

		LOGGER.debug("Begin ImsActionResponseUiProcessor job");

		String groupCode = message.getGroupCode();
		String jsonString = message.getParamsJson();

		JsonParser parser = new JsonParser();
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"ImsActionResponseUiProcessor Upload Job"
							+ " executing for groupcode %s and params %s",
					groupCode, jsonString);
			LOGGER.debug(msg);
		}
		JsonObject json = (JsonObject) parser.parse(jsonString);
		Long batchId = json.get("batchId").getAsLong();
		Long fileId = json.get("fileId").getAsLong();

		Gson gson = GsonUtil.newSAPGsonInstance();

		JsonObject reqJson = json.get("reqDto").getAsJsonObject();

		ImsActioResponseReqDto reqDto = gson.fromJson(reqJson,
				ImsActioResponseReqDto.class);

		String userName = json.get("userName").getAsString();

		try {

			fileStatusRepository.updateFileStatus(fileId, "InProgress");

			LOGGER.debug("Ims ui response is in progress ");

			int getImsUniqueIdSize = reqDto.getImsUniqueId() != null
					? reqDto.getImsUniqueId().size()
					: 0;

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"ImsActionResponseUiProcessor ImsUniqueIdSize %d",
						getImsUniqueIdSize);
				LOGGER.debug(msg);
			}

			reqService.saveTosttagingTable(reqDto, fileId, userName, batchId);

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"ImsActionResponseUiProcessor groupCode %s saved "
								+ "into tbl for batchId %d",
						groupCode, batchId);
				LOGGER.debug(msg);
			}

		

		} catch (Exception ex) {
			String msg = String.format(
					"Error occured in ImsActionResponseUiProcessor for %s ",
					ex);
			LOGGER.error(msg, ex);
			/*fileStatusRepository.updateFailedStatus(fileId,
					StringUtils.truncate(ex.getLocalizedMessage(), 4000),
					"Failed");*/
			throw new AppException(msg, ex);

		}

	}


}
