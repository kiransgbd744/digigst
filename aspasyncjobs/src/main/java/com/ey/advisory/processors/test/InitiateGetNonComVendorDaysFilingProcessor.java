package com.ey.advisory.processors.test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.asprecon.ReturnDataStorageStatusEntity;
import com.ey.advisory.app.data.entities.client.asprecon.VendorFilingStatusEntity;
import com.ey.advisory.app.data.repositories.client.asprecon.ReturnDataStorageStatusRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.VendorFilingStatusRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.VendorMasterUploadEntityRepository;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.GstnReturnFilingStatus;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.core.dto.ReturnFilingGstnResponseDto;
import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

@Service("InitiateGetNonComVendorDaysFilingProcessor")
@Slf4j
public class InitiateGetNonComVendorDaysFilingProcessor implements TaskProcessor {

	@Autowired
	private VendorMasterUploadEntityRepository vendorMasterUploadEntityRepository;

	@Autowired
	@Qualifier("VendorFilingStatusRepository")
	private VendorFilingStatusRepository vendorFilingStatusRepository;

	@Autowired
	@Qualifier("ReturnDataStorageStatusRepository")
	private ReturnDataStorageStatusRepository returnDataStorageStatusRepo;

	@Autowired
	private GstnReturnFilingStatus gstnReturnFiling;

	@Override
	public void execute(Message message, AppExecContext context) {

		String jsonString = message.getParamsJson();
		JsonObject json = JsonParser.parseString(jsonString).getAsJsonObject();
		Optional<ReturnDataStorageStatusEntity> returnDataStorageStatusEntity = null;
		try {
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"InitiateGetVendorDaysFilingProcessor Job"
								+ " executing for params %s and for GroupCode %s",
						jsonString, message.getGroupCode());
				LOGGER.debug(msg);
			}
			String financialYear = json.get("financialYear").getAsString();
			String noOfDays = json.get("noOfDays").getAsString();
			String userName = SecurityContext.getUser().getUserPrincipalName();
			Long requestId = json.get("requestId").getAsLong();

			Long successGstinCount = Long.valueOf(0);
			Long failedGstinCount = Long.valueOf(0);
			returnDataStorageStatusEntity = returnDataStorageStatusRepo
					.findById(requestId);
			if (returnDataStorageStatusEntity.isPresent()) {
				returnDataStorageStatusEntity.get().setStatus("InProgress");
				returnDataStorageStatusEntity.get()
						.setModifiedOn(LocalDateTime.now());
				returnDataStorageStatusRepo
						.save(returnDataStorageStatusEntity.get());
			}
			List<String> vendorGstinList = new ArrayList<>();

			vendorGstinList.addAll(
					vendorMasterUploadEntityRepository.findVendorGstin());
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Vendor Master Gstins Size"
								+ " %d and for GroupCode %s",
						vendorGstinList.size(), message.getGroupCode());
				LOGGER.debug(msg);
			}
			if (!vendorGstinList.isEmpty() && noOfDays != null) {
				LocalDateTime noOfDaysAgoDate = LocalDateTime.now()
						.minusDays(Long.valueOf(noOfDays));
				List<List<String>> chunks = Lists.partition(vendorGstinList,
						2000);
				for (List<String> chunk : chunks) {
					List<String> gstins = vendorFilingStatusRepository
							.findByFinancialYearAndGstinIn(chunk,
									financialYear);
					List<VendorFilingStatusEntity> entityList = new ArrayList<>();
					List<String> requiredVendorGstinList = new ArrayList<>();
					for (String gstin : chunk) {
						if (!gstins.contains(gstin)) {
							requiredVendorGstinList.add(gstin);
							VendorFilingStatusEntity entity = new VendorFilingStatusEntity();
							entity.setGstin(gstin);
							entity.setCreatedOn(LocalDateTime.now());
							entity.setModifiedOn(LocalDateTime.now());
							entity.setModifiedBy(userName);
							entity.setCreatedBy(userName);
							entity.setFinancialYear(financialYear);
							entityList.add(entity);
						}
					}
					vendorFilingStatusRepository.saveAll(entityList);

					if (LOGGER.isDebugEnabled()) {
						String msg = String.format(
								"new Gstins Size" + " %d and for GroupCode %s",
								entityList.size(), message.getGroupCode());
						LOGGER.debug(msg);
					}
					// entities which are failed from a perticular date and now
					List<VendorFilingStatusEntity> entities = vendorFilingStatusRepository
							.findAllFailedWithDateAfter(noOfDaysAgoDate, chunk,
									financialYear);

					entities.addAll(vendorFilingStatusRepository
							.findAllNotInitiatedWithDateBefore(noOfDaysAgoDate,
									chunk, financialYear));
					if (LOGGER.isDebugEnabled()) {
						String msg = String.format(
								"Gstins entities which are failed from date %s and now Size"
										+ " %d and for GroupCode %s",
								noOfDaysAgoDate.toString(), entities.size(),
								message.getGroupCode());
						LOGGER.debug(msg);
					}

					requiredVendorGstinList.addAll(entities.stream()
							.map(VendorFilingStatusEntity::getGstin).distinct()
							.collect(Collectors.toList()));

					if (LOGGER.isDebugEnabled()) {
						String msg = String.format(
								"Gstins Get Call Size"
										+ " %d and for GroupCode %s",
								requiredVendorGstinList.size(),
								message.getGroupCode());
						LOGGER.debug(msg);
					}

					List<String> successStatusGstins = new ArrayList<>();
					List<String> failedStatusGstins = new ArrayList<>();
					for (String vendorGstin : requiredVendorGstinList) {
						Pair<String, Boolean> pair = persistGstnApiForSelectedFinancialYear(
								financialYear, Arrays.asList(vendorGstin));
						if (pair.getValue1())
							successStatusGstins.add(vendorGstin);
						else
							failedStatusGstins.add(vendorGstin);
					}

					if (!successStatusGstins.isEmpty()) {
						vendorFilingStatusRepository.updateStatus(
								successStatusGstins, "Success", LocalDateTime.now(),
								financialYear);
					}
					if (!failedStatusGstins.isEmpty()) {
						vendorFilingStatusRepository.updateStatus(
								failedStatusGstins, "Failed", LocalDateTime.now(),
								financialYear);
					}
					successGstinCount += successStatusGstins.size();
					failedGstinCount += failedStatusGstins.size();

				}
			}
			if (returnDataStorageStatusEntity.isPresent()) {
				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(
							"successGstinCount %d" + " %d and for GroupCode %s",
							successGstinCount, failedGstinCount,
							message.getGroupCode());
					LOGGER.debug(msg);
				}
				if (successGstinCount > 0 && failedGstinCount > 0) {
					returnDataStorageStatusEntity.get()
							.setStatus("Partial Success");
					returnDataStorageStatusEntity.get()
							.setModifiedOn(LocalDateTime.now());
					returnDataStorageStatusRepo
							.save(returnDataStorageStatusEntity.get());
				} else if (failedGstinCount > 0) {
					returnDataStorageStatusEntity.get().setStatus("Failed");
					returnDataStorageStatusEntity.get()
							.setModifiedOn(LocalDateTime.now());
					returnDataStorageStatusRepo
							.save(returnDataStorageStatusEntity.get());
				} else {
					returnDataStorageStatusEntity.get().setStatus("Success");
					returnDataStorageStatusEntity.get()
							.setModifiedOn(LocalDateTime.now());
					returnDataStorageStatusRepo
							.save(returnDataStorageStatusEntity.get());
				}
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

	public Pair<String, Boolean> persistGstnApiForSelectedFinancialYear(
			String finYear, List<String> vendorGstins) {
		try {
			List<ReturnFilingGstnResponseDto> retFilingList = gstnReturnFiling
					.callGstnApi(vendorGstins, finYear, true);
			gstnReturnFiling.persistReturnFillingStatus(retFilingList, true);
			
			if (!retFilingList.isEmpty() && retFilingList.get(0).getErrCode() != null && 
					retFilingList.get(0).getErrCode().equalsIgnoreCase("ER-1000"))
				return new Pair<String, Boolean>(vendorGstins.get(0), false);
			else
				return new Pair<String, Boolean>(vendorGstins.get(0), true);
		} catch (Exception e) {
			LOGGER.error(
					"Exception while processing the vendor filing request:", e);
			return new Pair<String, Boolean>(vendorGstins.get(0), false);
		}
	}
}
