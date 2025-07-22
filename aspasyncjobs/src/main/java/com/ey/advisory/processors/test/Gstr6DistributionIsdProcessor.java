package com.ey.advisory.processors.test;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.FileStatusDownloadReportEntity;
import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.app.data.services.ewb.Gstr6IsdPdfService;
import com.ey.advisory.app.docs.dto.Gstr6DistributionDto;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.ErrMsgEnhancementStrategy;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

/**
 * @author kiran
 *
 */
@Slf4j
@Component("Gstr6DistributionIsdProcessor")
public class Gstr6DistributionIsdProcessor implements TaskProcessor {

	@Autowired
	@Qualifier("Gstr6IsdPrintServiceImpl")
	private Gstr6IsdPdfService gstr6Service;

	@Autowired
	FileStatusDownloadReportRepository downloadRepository;

	@Override
	public void execute(Message message, AppExecContext context) {

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("Begin gstr6 distibution processor :%s",
					message.toString());
			LOGGER.debug(msg);
		}

		String jsonString = message.getParamsJson();
		JsonObject json = JsonParser.parseString(jsonString).getAsJsonObject();
		Gson gson = new Gson();

		//JsonObject resp = json.get("resp").getAsJsonObject();
		Long entityId = json.get("id").getAsLong();
		try {
			Optional<FileStatusDownloadReportEntity> fileStatusEntity = downloadRepository
					.findById(entityId);
			if (fileStatusEntity.isPresent()) {
				String reqPayload = fileStatusEntity.get().getReqPayload();
				json = JsonParser.parseString(reqPayload).getAsJsonObject();
			} else {
				LOGGER.error("FileStatusDownloadReportEntity not found for ID: {}", entityId);
				throw new EntityNotFoundException("FileStatusDownloadReportEntity not found for ID: " + entityId);
			}
			Type listType = new TypeToken<List<Gstr6DistributionDto>>() {
			}.getType();

			List<Gstr6DistributionDto> reqDto = gson
					.fromJson(json.get("reqDto").getAsJsonArray(), listType);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Inside processor having reqDto as  {} for GSTR6 distribution",
						reqDto.toString());
			}

			String response = gstr6Service.getGstr6PdfPrint(reqDto, entityId);
			if (!"SUCCESS".equalsIgnoreCase(response)) {
				throw new AppException("service impl didn't return success");
			}
		} catch (Exception ex) {
			String msg = String.format(
					"Exception occured during report generation for gstr6 distribution");
			downloadRepository.updateStatus(entityId,
					"REPORT_GENERATION_FAILED", null, LocalDateTime.now());
			LOGGER.error(msg, ex);
			throw new AppException(ex,
					ErrMsgEnhancementStrategy.APPEND_FIRST_NON_APP_EXCEPTION);

		}
	}
}
