package com.ey.advisory.processors.test;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.EntityInfoEntity;
import com.ey.advisory.admin.data.repositories.client.EntityInfoDetailsRepository;
import com.ey.advisory.app.data.entities.client.FileStatusDownloadReportEntity;
import com.ey.advisory.app.data.entities.client.asprecon.ReturnDataStorageStatusEntity;
import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.ReturnDataStorageStatusRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.VendorMasterUploadEntityRepository;
import com.ey.advisory.app.data.services.noncomplaintvendor.NonComplaintVendorCommunicationServiceImpl;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

@Service("InitiateGetVendorFilingStatusProcessor")
@Slf4j
public class InitiateGetVendorFilingStatusProcessor implements TaskProcessor {

	@Autowired
	@Qualifier("NonComplaintVendorCommunicationServiceImpl")
	private NonComplaintVendorCommunicationServiceImpl nonComplaintVendorCommunicationServiceImpl;

	@Autowired
	@Qualifier("ReturnDataStorageStatusRepository")
	private ReturnDataStorageStatusRepository returnDataStorageStatusRepo;

	@Autowired
	EntityInfoDetailsRepository entityInfoDetailsRepository;
	
	@Autowired
	private VendorMasterUploadEntityRepository vendorMasterUploadEntityRepository;
	
	@Autowired
	FileStatusDownloadReportRepository fileStatusDownloadReportRepo;
	
	@Override
	public void execute(Message message, AppExecContext context) {

		List<String> vendorGstins = null;
		List<String> vendorPans = null;
		Gson gson = GsonUtil.newSAPGsonInstance();
		String jsonString = message.getParamsJson();
		JsonParser parser = new JsonParser();
		JsonObject jsonObject = (JsonObject) parser.parse(jsonString);
		Long id = jsonObject.get("id").getAsLong();
		Optional<FileStatusDownloadReportEntity> optEntity = fileStatusDownloadReportRepo
				.findById(id);
		
		if (!optEntity.isPresent()) {
		    LOGGER.error("FileStatusDownloadReportEntity not found for ID: {}", id);
		    throw new EntityNotFoundException("FileStatusDownloadReportEntity not found for ID: " + id);
		}

		FileStatusDownloadReportEntity entity = optEntity.get();
		JsonObject json = (JsonObject) parser.parse(entity.getReqPayload());
		
		Long requestId = json.get("requestId").getAsLong();
		if (json.has("vendorGstins")) {
			String vGstinString = json.get("vendorGstins").getAsString();
			Type listType = new TypeToken<ArrayList<String>>() {
			}.getType();
			vendorGstins = gson.fromJson(vGstinString, listType);
		}
		else {
			if (json.has("vendorPans") && json.has("entityId")) {
				String vPanString = json.get("vendorPans").getAsString();
				Type listType = new TypeToken<ArrayList<String>>() {
				}.getType();
				vendorPans = gson.fromJson(vPanString, listType);
				
				EntityInfoEntity entityInfoEntity = entityInfoDetailsRepository
						.findEntityByEntityId(Long.valueOf(json.get("entityId").getAsString()));

				vendorGstins = new ArrayList<>();
				List<List<String>> chunks = Lists.partition(vendorPans, 2000);

				for (List<String> chunk : chunks) {
					vendorGstins.addAll(vendorMasterUploadEntityRepository
							.findAllNonCompVendorGstinByVendorPans(chunk,
									entityInfoEntity.getPan()));
				}
			}
		}
		Optional<ReturnDataStorageStatusEntity> returnDataStorageStatusEntity = Optional.empty();;
		try {
			if (LOGGER.isDebugEnabled()) {
				String msg = String
						.format("InitiateGetVendorFilingStatusProcessor Job"
								+ " executing for params %s", jsonString);
				LOGGER.debug(msg);
			}

			String financialYear = json.get("financialYear").getAsString();
			String complianceType = json.get("complianceType").getAsString();
			returnDataStorageStatusEntity = returnDataStorageStatusRepo
					.findById(requestId);
			if (returnDataStorageStatusEntity.isPresent()) {
				returnDataStorageStatusEntity.get().setStatus("InProgress");
				returnDataStorageStatusEntity.get()
						.setModifiedOn(LocalDateTime.now());
				returnDataStorageStatusRepo
						.save(returnDataStorageStatusEntity.get());
				nonComplaintVendorCommunicationServiceImpl
						.persistGstnApiForSelectedFinancialYear(financialYear,
								vendorGstins,complianceType);
				returnDataStorageStatusEntity.get().setStatus("Completed");
				returnDataStorageStatusEntity.get()
						.setModifiedOn(LocalDateTime.now());
				returnDataStorageStatusRepo
						.save(returnDataStorageStatusEntity.get());
			}

		} catch (Exception e) {
			LOGGER.error(
					"Exception while processing the vendor filing request:", e);
			if (returnDataStorageStatusEntity.isPresent()) {
				returnDataStorageStatusEntity.get().setStatus("Failed");
				returnDataStorageStatusEntity.get()
						.setModifiedOn(LocalDateTime.now());
				returnDataStorageStatusRepo
						.save(returnDataStorageStatusEntity.get());
			}
		}
	}

}
