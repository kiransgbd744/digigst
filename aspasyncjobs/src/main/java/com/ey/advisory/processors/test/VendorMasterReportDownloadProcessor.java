package com.ey.advisory.processors.test;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.FileStatusDownloadReportEntity;
import com.ey.advisory.app.data.repositories.client.EntityInfoRepository;
import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.VendorMasterUploadEntityRepository;
import com.ey.advisory.app.itcmatching.vendorupload.GstinDetailsdto;
import com.ey.advisory.app.itcmatching.vendorupload.VendorMasterReporDownloadServiceImpl;
import com.ey.advisory.app.itcmatching.vendorupload.VendorMasterUploadEntity;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.ReportStatusConstants;
import com.ey.advisory.common.TaskProcessor;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import jakarta.persistence.EntityNotFoundException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("VendorMasterReportDownloadProcessor")
public class VendorMasterReportDownloadProcessor implements TaskProcessor {

	@Autowired
	FileStatusDownloadReportRepository downloadRepository;

/*	@Autowired
	@Qualifier("AsyncGstr1EntityReportDownloadServiceImpl")
	AsyncReportDownloadService asyncReportDownloadService;
	*/
	/*@Autowired
	@Qualifier("VendorMasterReporDownloadServiceImpl")
	VendorMasterReporDownloadServiceImpl vendorMasterReporDownloadServiceImpl;*/
	
	@Autowired
	@Qualifier("VendorMasterReporDownloadServiceImpl")
	VendorMasterReporDownloadServiceImpl vendorMasterReporDwnldServiceImpl;
	
	@Autowired
	@Qualifier("entityInfoRepository")
	private EntityInfoRepository entityInfoRepository;

	@Autowired
	@Qualifier("VendorMasterUploadEntityRepository")
	private VendorMasterUploadEntityRepository vendorMasterUploadEntityRepository;

	@Override
	public void execute(Message message, AppExecContext context) {
		Long id=null;
		
		try {

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("Begin VendorMasterReportDownloadProcessor processor :%s",
					message.toString());
			LOGGER.debug(msg);
		}

		String jsonString = message.getParamsJson();
		JsonObject json = JsonParser.parseString(jsonString).getAsJsonObject();
		Gson gson = new Gson();
		 id = json.get("id").getAsLong();
		
		
			Optional<FileStatusDownloadReportEntity> fileStatusEntity = downloadRepository
					.findById(id);
			if (fileStatusEntity.isPresent()) {
				String reqPayload = fileStatusEntity.get().getReqPayload();
				json = JsonParser.parseString(reqPayload).getAsJsonObject();
			} else {
				LOGGER.error("No FileStatusDownloadReportEntity found for ID: {}", id);
				throw new EntityNotFoundException("FileStatusDownloadReportEntity not found for ID: " + id);
			}
			String entityId1 = json.get("entityId").getAsString();
			long entityId = Long.parseLong(entityId1);

			Type listType = new TypeToken<List<GstinDetailsdto>>() {
			}.getType();

			List<GstinDetailsdto> reqDto = gson
					.fromJson(json.get("gstinDetails").getAsJsonArray(), listType);
			
			/*List<GstinDetailsdto> reqDto=null;
			Long entityId=24l;*/
		
			if (reqDto == null || reqDto.isEmpty()) {
			    // If reqDto is null or empty, retrieve data from the code
			    List<String> recipientPanList = entityInfoRepository.findPanByEntityId(entityId);
			    List<VendorMasterUploadEntity> masterEntities = vendorMasterUploadEntityRepository
			            .findByIsDeleteFalseAndRecipientPANIn(recipientPanList);
			    reqDto = masterEntities.stream()
			            .map(entity -> {
			                GstinDetailsdto gstinDetailsdto = new GstinDetailsdto();
			                gstinDetailsdto.setGstin(entity.getVendorGstin());
			                return gstinDetailsdto;
			            })
			            .collect(Collectors.toList());
			}

			// Now you have reqDto with GSTIN details, either from the payload or retrieved from the code

			
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Inside Async Report Download processor with Report id : %d",
						id);
				LOGGER.debug(msg);
			}

			downloadRepository.updateStatus(id,
					ReportStatusConstants.REPORT_GENERATION_INPROGRESS, null,
					EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));

			if (LOGGER.isDebugEnabled()) {
				String msg = "Updated file status as 'REPORT_GENERATION_INPROGRESS'";
				LOGGER.debug(msg);
			}

			
			vendorMasterReporDwnldServiceImpl.getVendorMasterGstinReport(reqDto, id, entityId);

		} catch (Exception ex) {
			String msg = String.format(
					"Exception occured during report generation for credit ledger");
			downloadRepository.updateStatus(id,
					"REPORT_GENERATION_FAILED", null, LocalDateTime.now());
			LOGGER.error(msg, ex);
			throw new AppException(msg);

		}
	}
	@Data
	public class GstnData {
		private String gstin;
	}
}
