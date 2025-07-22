package com.ey.advisory.processors.test;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.asprecon.FrequencyDataStorageStatusEntity;
import com.ey.advisory.app.data.repositories.client.asprecon.FrequencyDataStorageStatusRepository;
import com.ey.advisory.app.data.services.noncomplaintvendor.GetFilingFrequencyVendorComServiceImpl;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("GetVendorReturnFilingFrequencyProcessor")
public class GetVendorReturnFilingFrequencyProcessor implements TaskProcessor {

	@Autowired
	@Qualifier("GetFilingFrequencyVendorComServiceImpl")
	private GetFilingFrequencyVendorComServiceImpl service;

	@Autowired
	@Qualifier("FrequencyDataStorageStatusRepository")
	private FrequencyDataStorageStatusRepository freqDataStorageStatusRepo;

	@Override
	public void execute(Message message, AppExecContext context) {

		String jsonString = message.getParamsJson();
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject json = JsonParser.parseString(jsonString).getAsJsonObject();
		Optional<FrequencyDataStorageStatusEntity> freqDataStorageStatusEntity = Optional.empty();
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"InitiateGetVendorDaysFilingProcessor Job"
							+ " executing for params %s and for GroupCode %s",
					jsonString, message.getGroupCode());
			LOGGER.debug(msg);
		}
		try {
			List<String> vendorGstins = null;
			String financialYear = json.get("financialYear").getAsString();
			String complianceType = json.get("complianceType").getAsString();
			if (json.has("vendorGstins")) {
				String vGstinString = json.get("vendorGstins").getAsString();
				Type listType = new TypeToken<ArrayList<String>>() {
				}.getType();
				vendorGstins = gson.fromJson(vGstinString, listType);
			}
			if (json.has("requestId")) {
				Long requestId = json.get("requestId").getAsLong();
				freqDataStorageStatusEntity = freqDataStorageStatusRepo
						.findById(requestId);
			}
			if (freqDataStorageStatusEntity.isPresent()) {
				freqDataStorageStatusEntity.get().setStatus("InProgress");
				freqDataStorageStatusEntity.get()
						.setModifiedOn(LocalDateTime.now());
				freqDataStorageStatusRepo
						.save(freqDataStorageStatusEntity.get());
			}
			service.getVendorFilingFrequency(vendorGstins, financialYear,
					complianceType);
			if (freqDataStorageStatusEntity.isPresent()) {
				freqDataStorageStatusEntity.get().setStatus("Success");
				freqDataStorageStatusEntity.get()
						.setModifiedOn(LocalDateTime.now());
				freqDataStorageStatusRepo
						.save(freqDataStorageStatusEntity.get());
			}

		} catch (Exception e) {
			LOGGER.error(
					"Exception while processing the vendor filing request:", e);
			if (freqDataStorageStatusEntity.isPresent()) {
				freqDataStorageStatusEntity.get().setStatus("Failed");
				freqDataStorageStatusEntity.get()
						.setModifiedOn(LocalDateTime.now());
				freqDataStorageStatusRepo
						.save(freqDataStorageStatusEntity.get());
			}
		}
	}
}
