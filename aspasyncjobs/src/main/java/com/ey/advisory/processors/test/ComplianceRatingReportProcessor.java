package com.ey.advisory.processors.test;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.FileStatusDownloadReportEntity;
import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.app.data.services.compliancerating.VendorComplianceRatingRequestDto;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.ReportStatusConstants;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.service.vendor.compliance.VendorTableReportDownloadService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("ComplianceRatingReportProcessor")
public class ComplianceRatingReportProcessor implements TaskProcessor {

	@Autowired
	FileStatusDownloadReportRepository fileStatusDownloadReportRepo;

	@Autowired
	@Qualifier("VendorTableReportDownloadServiceImpl")
	private VendorTableReportDownloadService ratingService;

	@Override
	public void execute(Message message, AppExecContext context) {

		String jsonString = message.getParamsJson();
		JsonParser parser = new JsonParser();
		JsonObject json = (JsonObject) parser.parse(jsonString);

		Long id = json.get("id").getAsLong();
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Inside Async Report Download processor with Report id : %d",
					id);
			LOGGER.debug(msg);
		}
		Gson gson = GsonUtil.newSAPGsonInstance();
		FileStatusDownloadReportEntity entity=null;
		Optional<FileStatusDownloadReportEntity> optEntity = fileStatusDownloadReportRepo
				.findById(id);
		String jsonPayload = null;
		if (optEntity.isPresent()) {
			jsonPayload = optEntity.get().getReqPayload();
		}
		JsonObject request = (new JsonParser()).parse(jsonPayload)
				.getAsJsonObject().getAsJsonObject("req");
		VendorComplianceRatingRequestDto requestDto = gson.fromJson(
				request.toString(), VendorComplianceRatingRequestDto.class);

		fileStatusDownloadReportRepo.updateStatus(id,
				ReportStatusConstants.REPORT_GENERATION_INPROGRESS, null,
				EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
		

		if (LOGGER.isDebugEnabled()) {
			String msg = "Updated file status as 'REPORT_GENERATION_INPROGRESS'";
			LOGGER.debug(msg);
		}

		try {
			ratingService.getData(requestDto, id);
		} catch (Exception ex) {
			String msg = "Exception occured while downloading csv report";
			LOGGER.error(msg);
			throw new AppException(msg, ex);
		}

	}

}
