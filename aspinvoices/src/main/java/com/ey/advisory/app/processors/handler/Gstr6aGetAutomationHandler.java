package com.ey.advisory.app.processors.handler;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.javatuples.Triplet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.AutoReconStatusEntity;
import com.ey.advisory.admin.data.entities.client.EntityConfigPrmtEntity;
import com.ey.advisory.admin.data.entities.client.Get6aAutomationEntity;
import com.ey.advisory.admin.data.repositories.client.EntityConfigPrmtRepository;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.admin.data.repositories.client.Get6aAutomationRepository;
import com.ey.advisory.app.data.entities.client.GetAnx1BatchEntity;
import com.ey.advisory.app.data.repositories.client.AutoReconStatusRepository;
import com.ey.advisory.app.data.repositories.client.GetAnx1BatchRepository;
import com.ey.advisory.app.util.Anx1GetBatchUtil;
import com.ey.advisory.app.util.GstnUserRequestUtil;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.async.JobStatusConstants;
import com.ey.advisory.core.async.domain.master.AsyncExecJob;
import com.ey.advisory.core.dto.Gstr6aGetInvoicesReqDto;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Hemasundar.J
 *
 */
@Slf4j
@Service("Gstr6aGetAutomationHandler")
public class Gstr6aGetAutomationHandler {

	private static final List<String> GET6A_SECTIONS = ImmutableList.of(
			APIConstants.B2B, APIConstants.B2BA, APIConstants.CDN,
			APIConstants.CDNA);

	@Autowired
	private AsyncJobsService asyncJobsService;

	@Autowired
	private GstnUserRequestUtil gstnUserRequestUtil;

	@Autowired
	private GSTNDetailRepository gstnDetailRepo;

	@Autowired
	private EntityConfigPrmtRepository entityConfigRepo;

	@Autowired
	private Get6aAutomationRepository get6aAutomationRepo;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	private GSTNAuthTokenService authTokenService;

	@Autowired
	private AutoReconStatusRepository autoReconStatusRepo;

	@Autowired
	@Qualifier("getAnx1BatchRepository")
	private GetAnx1BatchRepository batchRepo;

	@Autowired
	@Qualifier("anx1GetBatchUtil")
	private Anx1GetBatchUtil batchUtil;

	public Map<Long, List<Triplet<String, String, Integer>>> jobSubmissionAtEntityLevel(
			Get6aAutomationEntity entity, Message message,
			Map<Long, List<Triplet<String, String, Integer>>> uniqueMap) {

		// Fetch all the GSTIN's for the entity
		List<String> activeGstins = gstnDetailRepo.findgstinByEntityIdWithISD(
				Arrays.asList(entity.getEntityId()));

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Total ISD GSTIN'S {} for the entityId is {} ",
					activeGstins, entity.getEntityId());
		}

		List<String> automatedActiveGstins = new ArrayList<>();

		/**
		 * Returning inactive Gstins and setting active Gstins to
		 * uniqueActiveGstins list
		 */
		List<String> inActiveGstins = getAllInActiveGstnList(activeGstins);
		/**
		 * Removing the inactiveGstins from the uniqueGstins List Since we are
		 * inserting to AutoReconStatusEntity here as NOT_INITIATED status and
		 * not to insert later with INITIATED status
		 */
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("inActiveGstins {} for the entity is {} ",
					inActiveGstins, entity.getEntityId());
		}
		if (!inActiveGstins.isEmpty()) {

			String paramValue = entityConfigRepo
					.findByOpted6aRecon(entity.getEntityId()); 
			paramValue = paramValue != null ? paramValue : "B";
			/**
			 * Answer B informs that No Handshake table insert
			 */
			if ("B".equals(paramValue)) {

				LOGGER.error(
						"I35 answer is B so No Hand shake table insert made for the Entity {} ",
						entity.getEntityId());
			} else {

				List<AutoReconStatusEntity> inactiveEntities = new ArrayList<>();
				inActiveGstins.forEach(inactiveGstin -> {

					AutoReconStatusEntity reconStatus = new AutoReconStatusEntity();
					reconStatus.setGstin(inactiveGstin);
					reconStatus.setEntityId(entity.getEntityId());
					reconStatus.setDate(EYDateUtil.toISTDateTimeFromUTC(LocalDateTime.now())
							.toLocalDate());
					reconStatus.setGet6aStatus(
							APIConstants.NOT_INITIATED.toUpperCase());
					reconStatus.setGet6aRemarks("Auth Token is Inactive");
					reconStatus.setGetEvent(entity.getGetEvent());
					reconStatus.setCreatedOn(LocalDateTime.now());
					inactiveEntities.add(reconStatus);

				});
				autoReconStatusRepo.saveAll(inactiveEntities);
			}
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("ActiveGstins {} for the entity is {} ", activeGstins,
					entity.getEntityId());
		}

		/**
		 * Populating the last post Date So that it wont execute in next
		 * periodic job execution
		 */
		entity.setLastPostedDate(LocalDateTime.now());
		entity.setModifiedOn(LocalDateTime.now());

		get6aAutomationRepo.save(entity);

		if (!activeGstins.isEmpty()) {

			/**
			 * Submit job at GSTIN level
			 */
			automatedActiveGstins = gstinLevelJobSegrigartion(entity,
					activeGstins, automatedActiveGstins, message);

		}
		/**
		 * Adding Gstins at entity level for ReconStatus job submission with the
		 * status as INITIATED
		 */
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"automatedActiveGstins {} for the entity {} putting to empty uniqueMap ",
					automatedActiveGstins, entity.getEntityId());

		}
		if (automatedActiveGstins != null && !automatedActiveGstins.isEmpty()) {

			int noOfTaxPeriods = getEligibleTaxPeriods().size();

			List<Triplet<String, String, Integer>> tripletList = new ArrayList<>();

			automatedActiveGstins.stream().forEach(eachGstin -> {
				tripletList.add(new Triplet<>(eachGstin, entity.getGetEvent(),
						noOfTaxPeriods));
			});
			uniqueMap.put(entity.getEntityId(), tripletList);
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Get6aAutomation EntityId wise uniqueMap is {} ",
					uniqueMap);
		}

		/**
		 * This method is responsible for sending emails to primary and
		 * secondary Ids
		 */
		
		//TODO 
		/*
		 * gstr2aGetEmailHandler.prepareEmailBodyContent(entity,
		 * automatedActiveGstins, inActiveGstins);
		 */
		
		return uniqueMap;
	}

	private List<String[]> isGstinAndTaxPeriodsJobInprogress(String gstin,
			List<String> taxPeriods) {

		List<String[]> initAndInprogressBatchs = null;
		List<String> initAndInProgressStatus = new ArrayList<>();
		initAndInProgressStatus.add(APIConstants.INITIATED.toUpperCase());
		initAndInProgressStatus
				.add(JobStatusConstants.IN_PROGRESS.toUpperCase());

		String returnType = APIConstants.GSTR6A.toUpperCase();

		initAndInprogressBatchs = batchRepo.findBatchByStatus(gstin, taxPeriods,
				returnType, initAndInProgressStatus);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Auto GET6A All GET Not eligible sections to execute are {} ",
					initAndInprogressBatchs);
		}

		return initAndInprogressBatchs;
	}

	private List<String> gstinLevelJobSegrigartion(Get6aAutomationEntity entity,
			List<String> activeGstins, List<String> automatedActiveGstins,
			Message message) {

		automatedActiveGstins.addAll(activeGstins);

		// Get the number of tax periods to be initiated
		final List<String> taxPeriods = getEligibleTaxPeriods();

		// Looping at GSTIN level
		activeGstins.forEach(gstin -> {
			/**
			 * If any eligible return period for the GSTIN is in_progress then
			 * the GSTIN for all return periods request should not be submitted
			 */
			List<String[]> inprogressJobs = isGstinAndTaxPeriodsJobInprogress(
					gstin, taxPeriods);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"InprogressJobs {} for the gstin {} and taxPeriods {} ",
						inprogressJobs, gstin, taxPeriods);
			}
			if (!inprogressJobs.isEmpty()) {
				/**
				 * Removing the Gstin from the uniqueGstins List Since we are
				 * inserting to AutoReconStatusEntity here as FAILED status and
				 * not to insert later with INITIATED status
				 */
				automatedActiveGstins.remove(gstin);
				
				String paramValue = entityConfigRepo
						.findByOptedforAP(entity.getEntityId()); // change in
																	// question
																	// number
				paramValue = paramValue != null ? paramValue : "B";
				/**
				 * Answer B informs that No Handshake table insert
				 */
				if ("B".equals(paramValue)) {

					LOGGER.error(
							"I27 answer is B so No Hand shake table insert made for the Entity {} ",
							entity.getEntityId());
				} else {
					AutoReconStatusEntity reconStatus = new AutoReconStatusEntity();
					reconStatus.setGstin(gstin);
					reconStatus.setEntityId(entity.getEntityId());
					reconStatus.setDate(EYDateUtil.toISTDateTimeFromUTC(LocalDateTime.now())
							.toLocalDate());
					reconStatus.setGet6aStatus(
							APIConstants.NOT_INITIATED.toUpperCase());
					reconStatus.setGet6aRemarks("InProgress Jobs exists");
					reconStatus.setGetEvent(entity.getGetEvent());
					reconStatus.setCreatedOn(LocalDateTime.now());
					autoReconStatusRepo.save(reconStatus);
					return;
				}
			}

			/** set isDeltaGet status **/
			boolean isFDeltaGetData = false;
			EntityConfigPrmtEntity entityConfig = entityConfigRepo
					.findByGroupCodeAndEntityIdAndparamkryIdAndIsDeleteFalse(
							TenantContext.getTenantId(), entity.getEntityId(),
							"I25"); // change in question number
			String paramValue = entityConfig != null
					? entityConfig.getParamValue() : "A";
			if ("B".equals(paramValue)) {
				isFDeltaGetData = true;
			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"findByGroupCodeAndEntityIdAndparamkryIdAndIsDeleteFalse has returned EntityConfigPrmtEntity {} ",
						entityConfig);

			}

			Gstr6aGetInvoicesReqDto dto = new Gstr6aGetInvoicesReqDto();
			dto.setIsDeltaGet(isFDeltaGetData);

			/**
			 * Submit job at TAX_PERIOD level
			 */

			taxPeriodLevelJobSegrigartion(taxPeriods, dto, gstin, entity,
					message);

		});

		return automatedActiveGstins;

	}

	private void taxPeriodLevelJobSegrigartion(List<String> taxPeriods,
			Gstr6aGetInvoicesReqDto dto, String gstin,
			Get6aAutomationEntity entity, Message message) {

		// Inner Loop at taxPeriod level
		taxPeriods.forEach(taxPeriod -> {

			dto.setGstin(gstin);
			dto.setReturnPeriod(taxPeriod);
			dto.setGroupcode(TenantContext.getTenantId());
			dto.setApiSection(APIConstants.GSTR6A.toUpperCase());
			dto.setIsAutoGet(true);

			Long userRequestId = gstnUserRequestUtil.createGstnUserRequest(
					dto.getGstin(), dto.getReturnPeriod(), APIConstants.GET,
					APIConstants.GSTR6A.toUpperCase(), dto.getGroupcode(),
					APIConstants.SYSTEM.toUpperCase(), false, false, false);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("GstnUserRequest has created with id {} ",
						userRequestId);
			}
			dto.setUserRequestId(userRequestId);

			GET6A_SECTIONS.forEach(get6aSection -> {

				dto.setType(get6aSection);
				/**
				 * Calculating from time and setting it to Request Dto
				 */
				// TODO doubt
				String returnType = APIConstants.GSTR6A.toUpperCase();
				/**
				 * If there is no SUCCESS/SUCCESS_WITH_NO_DATA GET calls in the
				 * history of Batch table then Full GET call will be made Even
				 * on daily basis This happens Ideally If it is first time GET
				 * call.
				 */

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"GSTR6A AUTO GET call for GSTIN, TAXPERIOD is {}, {}, for the section {} ",
							dto.getGstin(), dto.getReturnPeriod(),
							dto.getType().toUpperCase());
				}
				// }

				gstr6aSectionWiseGetJobInsertion(dto, entity, message);

			});

		});

	}

	private void gstr6aSectionWiseGetJobInsertion(Gstr6aGetInvoicesReqDto dto,
			Get6aAutomationEntity entity, Message message) {

		Gson gson = GsonUtil.newSAPGsonInstance();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"gstr6aGetJobsInsertion with reqDto {}, Get6aAutomationEntity {} and message {} ",
					dto, entity, message);
		}

		dto = createBatchAndSave(dto.getGroupcode(), dto);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Batch has created and value set to dto {} ", dto);
		}
		String jsonParam = gson.toJson(dto);
		AsyncExecJob job = asyncJobsService.createJob(dto.getGroupcode(),
				JobConstants.GSTR6A_GSTN_GET_SECTION, jsonParam,
				APIConstants.SYSTEM.toUpperCase(), JobConstants.PRIORITY,
				message.getId(), JobConstants.SCHEDULE_AFTER_IN_MINS);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("AsyncExecJob has created as {} ", job);
		}
		entity.setLastPostedJobId(job.getJobId());
		entity.setLastPostedDate(
				EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
		get6aAutomationRepo.save(entity);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Job Created for Gstr6A Get as {} ", job);
		}

	}

	private Gstr6aGetInvoicesReqDto createBatchAndSave(String groupCode,
			Gstr6aGetInvoicesReqDto dto) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("groupCode {} for the Gstr1GetInvoicesReqDto {} ",
					groupCode, dto);
		}
		TenantContext.setTenantId(groupCode);
		// InActiveting Previous Batch Records
		batchRepo.softlyDelete(dto.getType().toUpperCase(),
				APIConstants.GSTR6A.toUpperCase(), dto.getGstin(),
				dto.getReturnPeriod());
		GetAnx1BatchEntity batch = batchUtil.makeBatchGstr6a(dto,
				dto.getType());
		// Save new Batch
		batch = batchRepo.save(batch);
		dto.setBatchId(batch.getId());
		return dto;

	}

	private List<String> getEligibleTaxPeriods() {

		/**
		 * If Fin Year GET is chosen by the client then the difference in the
		 * months calculation is written below based the functional requirement
		 * documentation as for Oct, Nov and Dec months Get call to initiate
		 * from current finyear and for rest of the months Get call to initiate
		 * from Previous finyear
		 */
		int numOfTaxPeriods = 0;
		int monthValue = LocalDate.now().getMonthValue();
		if (monthValue == 1) {
			numOfTaxPeriods = 10;
		} else if (monthValue == 2) {
			numOfTaxPeriods = 11;
		} else if (monthValue == 3) {
			numOfTaxPeriods = 12;
		} else if (monthValue == 4) {
			numOfTaxPeriods = 13;
		} else if (monthValue == 5) {
			numOfTaxPeriods = 14;
		} else if (monthValue == 6) {
			numOfTaxPeriods = 15;
		} else if (monthValue == 7) {
			numOfTaxPeriods = 16;
		} else if (monthValue == 8) {
			numOfTaxPeriods = 17;
		} else if (monthValue == 9) {
			numOfTaxPeriods = 18;
		} else if (monthValue == 10) {
			numOfTaxPeriods = 19;
		} else if (monthValue == 11) {
			numOfTaxPeriods = 20;
		} else if (monthValue == 12) {
			numOfTaxPeriods = 9;
		}

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMyyyy");

		List<String> taxPeriods = new ArrayList<>();
		for (numOfTaxPeriods = numOfTaxPeriods
				- 1; numOfTaxPeriods >= 0; numOfTaxPeriods--) {

			LocalDate minusMonths = LocalDate.now()
					.minusMonths(numOfTaxPeriods);
			String taxPeriod = minusMonths.format(formatter);
			taxPeriods.add(taxPeriod);

		}
		return taxPeriods;

	}

	private List<String> getAllInActiveGstnList(
			List<String> uniqueActiveGstins) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Getting all the active GSTNs for GSTR6A save");
		}

		List<String> activeGstins = new ArrayList<>();
		List<String> inActiveGstins = new ArrayList<>();
		if (!uniqueActiveGstins.isEmpty()) {
			for (String gstin : uniqueActiveGstins) {
				String authStatus = authTokenService
						.getAuthTokenStatusForGstin(gstin);
				if (authStatus.equals("A")) {
					activeGstins.add(gstin);
				} else {
					inActiveGstins.add(gstin);
				}
			}
			uniqueActiveGstins.clear();
			uniqueActiveGstins.addAll(activeGstins);
		}
		return inActiveGstins;
	}

}
