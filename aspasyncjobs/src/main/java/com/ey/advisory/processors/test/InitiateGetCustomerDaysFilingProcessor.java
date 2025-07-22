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

import com.ey.advisory.app.data.entities.client.CustomerFilingStatusEntity;
import com.ey.advisory.app.data.entities.client.asprecon.ReturnDataStorageStatusEntity;
import com.ey.advisory.app.data.repositories.client.CustomerFilingStatusRepository;
import com.ey.advisory.app.data.repositories.client.DocRepository;
import com.ey.advisory.app.data.repositories.client.EntityInfoRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.ReturnDataStorageStatusRepository;
import com.ey.advisory.app.data.services.noncomplaintvendor.NonComplaintVendorCommunicationServiceImpl;
import com.ey.advisory.common.AppException;
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

@Service("InitiateGetCustomerDaysFilingProcessor")
@Slf4j
public class InitiateGetCustomerDaysFilingProcessor implements TaskProcessor {

	@Autowired
	@Qualifier("NonComplaintVendorCommunicationServiceImpl")
	private NonComplaintVendorCommunicationServiceImpl nonComplaintVendorCommunicationServiceImpl;

	@Autowired
	@Qualifier("entityInfoRepository")
	private EntityInfoRepository entityInfoRepository;

	@Autowired
	@Qualifier("CustomerFilingStatusRepository")
	private CustomerFilingStatusRepository customerFilingStatusRepository;

	@Autowired
	private DocRepository docRepo;
	
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
			List<String> custGstinList = new ArrayList<>();

			// Entities from Customer
			List<String> custPans = docRepo.getDistinctCustomerPans();
			List<List<String>> panChunks = Lists.partition(custPans, 2000);

			for (List<String> chunk : panChunks) {
				custGstinList.addAll(docRepo.getDistinctCustomerGstin(chunk));
			}
		
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Vendor Master Gstins Size"
								+ " %d and for GroupCode %s",
								custGstinList.size(), message.getGroupCode());
				LOGGER.debug(msg);
			}

			if (!custGstinList.isEmpty() && noOfDays != null) {

				LocalDateTime noOfDaysAgoDate = LocalDateTime.now()
						.minusDays(Long.valueOf(noOfDays));
				List<List<String>> chunks = Lists.partition(custGstinList,
						2000);
				for (List<String> chunk : chunks) {
					List<String> gstins = customerFilingStatusRepository
							.findByFinancialYearAndGstinIn(chunk,
									financialYear);
					List<CustomerFilingStatusEntity> entityList = new ArrayList<>();
					// new gstins which are not available in
					// VendorFilingStatusEntity
					List<String> requiredCustomerGstinList = new ArrayList<>();
					for (String gstin : chunk) {
						if (!gstins.contains(gstin)) {
							requiredCustomerGstinList.add(gstin);
							CustomerFilingStatusEntity entity = new CustomerFilingStatusEntity();
							entity.setGstin(gstin);
							entity.setCreatedOn(LocalDateTime.now());
							entity.setModifiedOn(LocalDateTime.now());
							entity.setModifiedBy(userName);
							entity.setCreatedBy(userName);
							entity.setFinancialYear(financialYear);
							entityList.add(entity);
						}
					}
					customerFilingStatusRepository.saveAll(entityList);

					if (LOGGER.isDebugEnabled()) {
						String msg = String.format(
								"new Gstins Size" + " %d and for GroupCode %s",
								entityList.size(), message.getGroupCode());
						LOGGER.debug(msg);
					}
					// entities which are failed from a perticular date and now
					List<CustomerFilingStatusEntity> entities = customerFilingStatusRepository
							.findAllFailedWithDateAfter(noOfDaysAgoDate, chunk,
									financialYear);

					entities.addAll(customerFilingStatusRepository
							.findAllNotInitiatedWithDateBefore(noOfDaysAgoDate,
									chunk, financialYear));
					if (LOGGER.isDebugEnabled()) {
						String msg = String.format(
								"Gstins entities which are failed from date %s and now Size"
										+ " %d and for GroupCode %s",
								noOfDaysAgoDate.toString(), entityList.size(),
								message.getGroupCode());
						LOGGER.debug(msg);
					}

					requiredCustomerGstinList.addAll(entities.stream()
							.map(CustomerFilingStatusEntity::getGstin)
							.distinct().collect(Collectors.toList()));
					List<String> successStatusGstins = new ArrayList<>();
					List<String> failedStatusGstins = new ArrayList<>();
					for (String customerGstin : requiredCustomerGstinList) {
						Pair<String, Boolean> pair = persistGstnApiForSelectedFinancialYear(
								financialYear, Arrays.asList(customerGstin));
						if (pair.getValue1())
							successStatusGstins.add(customerGstin);
						else
							failedStatusGstins.add(customerGstin);
					}
					if (!successStatusGstins.isEmpty()) {
						customerFilingStatusRepository.updateStatus(
								successStatusGstins, "Success", LocalDateTime.now(),financialYear);
					}
					if (!failedStatusGstins.isEmpty()) {
						customerFilingStatusRepository.updateStatus(
								failedStatusGstins, "Failed", LocalDateTime.now(),financialYear);
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
			throw new AppException(e.getMessage());
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
			return new Pair<String, Boolean>(vendorGstins.get(0), false);
		}
	}
}
