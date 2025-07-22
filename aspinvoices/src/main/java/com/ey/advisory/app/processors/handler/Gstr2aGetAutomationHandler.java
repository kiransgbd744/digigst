package com.ey.advisory.app.processors.handler;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.javatuples.Triplet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.AutoReconStatusEntity;
import com.ey.advisory.admin.data.entities.client.EntityConfigPrmtEntity;
import com.ey.advisory.admin.data.entities.client.Get2aAutomationEntity;
import com.ey.advisory.admin.data.repositories.client.EntityConfigPrmtRepository;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.admin.data.repositories.client.Get2aAutomationRepository;
import com.ey.advisory.app.data.entities.client.GetAnx1BatchEntity;
import com.ey.advisory.app.data.repositories.client.AutoReconStatusRepository;
import com.ey.advisory.app.data.repositories.client.GetAnx1BatchRepository;
import com.ey.advisory.app.util.Anx1GetBatchUtil;
import com.ey.advisory.app.util.GstnUserRequestUtil;
import com.ey.advisory.common.DateUtil;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.async.JobStatusConstants;
import com.ey.advisory.core.async.domain.master.AsyncExecJob;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Hemasundar.J
 *
 */
@Slf4j
@Service("Gstr2aGetAutomationHandler")
public class Gstr2aGetAutomationHandler {

	private static final List<String> GET2A_SECTIONS = ImmutableList.of(
			APIConstants.B2B, APIConstants.B2BA, APIConstants.CDN,
			APIConstants.CDNA, APIConstants.ISD, APIConstants.IMPG,
			APIConstants.IMPGSEZ, APIConstants.ECOM,APIConstants.ECOMA);

	@Autowired
	private AsyncJobsService asyncJobsService;

	@Autowired
	private GstnUserRequestUtil gstnUserRequestUtil;

	@Autowired
	private GSTNDetailRepository gstnDetailRepo;

	@Autowired
	private EntityConfigPrmtRepository entityConfigRepo;

	@Autowired
	private Get2aAutomationRepository get2aAutomationRepo;

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

	@Autowired
	private Gstr2aGetEmailHandler gstr2aGetEmailHandler;

	private static final ImmutableList<String> STATUS = ImmutableList
			.of("SUCCESS", "SUCCESS_WITH_NO_DATA");

	public Map<Long, List<Triplet<String, String, Integer>>> jobSubmissionAtEntityLevel(
			Get2aAutomationEntity entity, Message message,
			Map<Long, List<Triplet<String, String, Integer>>> uniqueMap) {

		// Fetch all the GSTIN's for the entity
		List<String> activeGstins = gstnDetailRepo
				.findgstinByEntityIdWithRegTypeForGstr1(entity.getEntityId());

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Total REGULAR GSTIN'S {} for the entityId is {} ",
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
		 *//*
			 * uniqueActiveGstins.removeAll(inActiveGstins);
			 */
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("inActiveGstins {} for the entity is {} ",
					inActiveGstins, entity.getEntityId());
		}
		if (!inActiveGstins.isEmpty()) {

			String paramValue = entityConfigRepo
					.findByOptedforAP(entity.getEntityId());
			paramValue = paramValue != null ? paramValue : "B";
			/**
			 * Answer B informs that No Handshake table insert
			 */
			if ("B".equals(paramValue)) {

				LOGGER.error(
						"I27 answer is B so No Hand shake table insert made for the Entity {} ",
						entity.getEntityId());
			} else {

				List<AutoReconStatusEntity> inactiveEntities = new ArrayList<>();
				inActiveGstins.forEach(inactiveGstin -> {

					AutoReconStatusEntity reconStatus = new AutoReconStatusEntity();
					reconStatus.setGstin(inactiveGstin);
					reconStatus.setEntityId(entity.getEntityId());
					reconStatus.setDate(
							EYDateUtil.toUTCDateTimeFromLocal(LocalDate.now()));
					reconStatus.setGet2aStatus(
							APIConstants.NOT_INITIATED.toUpperCase());
					reconStatus.setGet2aRemarks("Auth Token is Inactive");
					reconStatus.setGetEvent(entity.getGetEvent());
					reconStatus.setNumOfTaxPeriods(entity.getNumOfTaxPeriods());
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

		get2aAutomationRepo.save(entity);

		Integer numOfTaxPeriods = entity.getNumOfTaxPeriods();
		// Get the number of tax periods to be initiated
		List<String> taxPeriods = getEligibleTaxPeriods(numOfTaxPeriods,
				entity.isFinYearGet());

		if (!activeGstins.isEmpty()) {
			/**
			 * Submit job at GSTIN level
			 */
			automatedActiveGstins = gstinLevelJobSegrigartion(entity,
					activeGstins, automatedActiveGstins, message, taxPeriods);

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

			List<Triplet<String, String, Integer>> tripletList = new ArrayList<>();

			automatedActiveGstins.stream().forEach(eachGstin -> {
				tripletList.add(new Triplet<>(eachGstin, entity.getGetEvent(),
						taxPeriods.size()));
			});
			uniqueMap.put(entity.getEntityId(), tripletList);
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Get2aAutomation EntityId wise uniqueMap is {} ",
					uniqueMap);
		}

		/**
		 * This method is responsible for sending emails to primary and
		 * secondary Ids
		 */
		gstr2aGetEmailHandler.prepareEmailBodyContent(entity,
				automatedActiveGstins, inActiveGstins);

		return uniqueMap;
	}

	private List<String[]> isGstinAndTaxPeriodsJobInprogress(String gstin,
			List<String> taxPeriods) {

		List<String[]> initAndInprogressBatchs = null;
		List<String> initAndInProgressStatus = new ArrayList<>();
		initAndInProgressStatus.add(APIConstants.INITIATED.toUpperCase());
		initAndInProgressStatus
				.add(JobStatusConstants.IN_PROGRESS.toUpperCase());

		String returnType = APIConstants.GSTR2A.toUpperCase();

		initAndInprogressBatchs = batchRepo.findBatchByStatus(gstin, taxPeriods,
				returnType, initAndInProgressStatus);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Auto GET2A All GET Not eligible sections to execute are {} ",
					initAndInprogressBatchs);
		}

		return initAndInprogressBatchs;
	}

	private List<String> gstinLevelJobSegrigartion(Get2aAutomationEntity entity,
			List<String> activeGstins, List<String> automatedActiveGstins,
			Message message, List<String> taxPeriods) {

		automatedActiveGstins.addAll(activeGstins);

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
						.findByOptedforAP(entity.getEntityId());
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
					reconStatus.setDate(
							EYDateUtil.toUTCDateTimeFromLocal(LocalDate.now()));
					reconStatus.setGet2aStatus(
							APIConstants.NOT_INITIATED.toUpperCase());
					reconStatus.setGet2aRemarks("jobs are inprogress");
					reconStatus.setGetEvent(entity.getGetEvent());
					reconStatus.setNumOfTaxPeriods(entity.getNumOfTaxPeriods());
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
							"I25");
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

			Gstr1GetInvoicesReqDto dto = new Gstr1GetInvoicesReqDto();
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
			Gstr1GetInvoicesReqDto dto, String gstin,
			Get2aAutomationEntity entity, Message message) {

		// Inner Loop at taxPeriod level
		taxPeriods.forEach(taxPeriod -> {

			dto.setGstin(gstin);
			dto.setReturnPeriod(taxPeriod);
			dto.setGroupcode(TenantContext.getTenantId());
			dto.setApiSection(APIConstants.GSTR2A.toUpperCase());
			dto.setIsAutoGet(true);

			Long userRequestId = gstnUserRequestUtil.createGstnUserRequest(
					dto.getGstin(), dto.getReturnPeriod(), APIConstants.GET,
					APIConstants.GSTR2A.toUpperCase(), dto.getGroupcode(),
					APIConstants.SYSTEM.toUpperCase(), false, false, false);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("GstnUserRequest has created with id {} ",
						userRequestId);
			}
			dto.setUserRequestId(userRequestId);

			GET2A_SECTIONS.forEach(get2aSection -> {

				dto.setType(get2aSection);
				/**
				 * Calculating from time and setting it to Request Dto
				 */

				// if (!"M".equals(entity.getGetEvent())) {

				// List<String> returnTypes = new ArrayList<>();
				// returnTypes.add(APIConstants.GSTR2A.toUpperCase());
				// returnTypes.add(APIConstants.GSTR2A_AUTO.toUpperCase());
				String returnType = APIConstants.GSTR2A.toUpperCase();
				/**
				 * If there is no SUCCESS/SUCCESS_WITH_NO_DATA GET calls in the
				 * history of Batch table then Full GET call will be made Even
				 * on daily basis This happens Ideally If it is first time GET
				 * call.
				 */

				String fromTime = null;
				String toTime = null;
				if ("D".equalsIgnoreCase(entity.getGetEvent())) {
					Object objArray = batchRepo.findLastSuccessDate(returnType,
							dto.getGstin(), dto.getReturnPeriod(),
							dto.getType().toUpperCase(), STATUS);
					String lastExecutedOn = objArray != null
							? String.valueOf(objArray) : null;
					if (lastExecutedOn != null) {
						LocalDate locDate = LocalDate.parse(
								lastExecutedOn.substring(0, 10),
								DateUtil.SUPPORTED_DATE_FORMAT1);
						fromTime = locDate
								.format(DateUtil.SUPPORTED_DATE_FORMAT2);

						toTime = LocalDate.now()
								.format(DateUtil.SUPPORTED_DATE_FORMAT2);

						dto.setFromTime(fromTime);
						dto.setToTime(toTime);
					}
				} /*
					 * else if ("D".equalsIgnoreCase(entity.getGetEvent()) &&
					 * entity.isFinYearGet()) {
					 * 
					 * // full get call for first date and incremental for //
					 * remaining dates get call for custom dates
					 * 
					 * List<String> customDates = get2aAutomationRepo
					 * .getCustomDatesinAsc(entity.getEntityId(),
					 * entity.getGetEvent());
					 * 
					 * // converting from string to integer List<Integer>
					 * custDates = customDates.stream() .map(Integer::parseInt)
					 * .collect(Collectors.toList()); //5 ,10, 15, 20 if
					 * (LocalDate.now().getDayOfMonth() != custDates.get(0)) {
					 * 
					 * // checking for first date of an month. If yes, then //
					 * it should be full get call else incremental get // call
					 * Object objArray = batchRepo.findLastSuccessDate(
					 * returnType, dto.getGstin(), dto.getReturnPeriod(),
					 * dto.getType().toUpperCase(), STATUS); String
					 * lastExecutedOn = objArray != null ?
					 * String.valueOf(objArray) : null; if (lastExecutedOn !=
					 * null) { LocalDate locDate = LocalDate.parse(
					 * lastExecutedOn.substring(0, 10),
					 * DateUtil.SUPPORTED_DATE_FORMAT1); fromTime = locDate
					 * .format(DateUtil.SUPPORTED_DATE_FORMAT2);
					 * 
					 * toTime = LocalDate.now()
					 * .format(DateUtil.SUPPORTED_DATE_FORMAT2);
					 * 
					 * dto.setFromTime(fromTime); dto.setToTime(toTime); }
					 * 
					 * }
					 * 
					 * }
					 */

				// for Montly and Weekly it will be full get call, so from date
				// and totime will be null

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"GSTR2A AUTO GET call for GSTIN, TAXPERIOD, FROM_TIME, TO_TIME is {}, {}, {} for the section {} ",
							dto.getGstin(), dto.getReturnPeriod(), fromTime,
							toTime, dto.getType().toUpperCase());
				}
				// }

				gstr2aSectionWiseGetJobInsertion(dto, entity, message);

			});

		});

	}

	private void gstr2aSectionWiseGetJobInsertion(Gstr1GetInvoicesReqDto dto,
			Get2aAutomationEntity entity, Message message) {

		Gson gson = GsonUtil.newSAPGsonInstance();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"gstr2aGetJobsInsertion with reqDto {}, Get2aAutomationEntity {} and message {} ",
					dto, entity, message);
		}

		// dto.setType(section.toUpperCase());
		dto = createBatchAndSave(dto.getGroupcode(), dto);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Batch has created and value set to dto {} ", dto);
		}
		String jsonParam = gson.toJson(dto);
		AsyncExecJob job = asyncJobsService.createJob(dto.getGroupcode(),
				JobConstants.GSTR2A_GSTN_GET_SECTION, jsonParam,
				APIConstants.SYSTEM.toUpperCase(), JobConstants.PRIORITY,
				message.getId(), JobConstants.SCHEDULE_AFTER_IN_MINS);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("AsyncExecJob has created as {} ", job);
		}
		entity.setLastPostedJobId(job.getJobId());
		entity.setLastPostedDate(
				EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
		get2aAutomationRepo.save(entity);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Job Created for Gstr2A Get as {} ", job);
		}

	}

	private Gstr1GetInvoicesReqDto createBatchAndSave(String groupCode,
			Gstr1GetInvoicesReqDto dto) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("groupCode {} for the Gstr1GetInvoicesReqDto {} ",
					groupCode, dto);
		}
		TenantContext.setTenantId(groupCode);
		// InActiveting Previous Batch Records
		batchRepo.softlyDelete(dto.getType().toUpperCase(),
				APIConstants.GSTR2A.toUpperCase(), dto.getGstin(),
				dto.getReturnPeriod());
		GetAnx1BatchEntity batch = batchUtil.makeBatchGstr1(dto, dto.getType(),
				APIConstants.GSTR2A.toUpperCase());
		// Save new Batch
		batch = batchRepo.save(batch);
		dto.setBatchId(batch.getId());
		return dto;

	}

	private List<String> getEligibleTaxPeriods(Integer numOfTaxPeriods,
			boolean isFinYearGet) {

		/**
		 * If Fin Year GET is chosen by the client then the difference in the
		 * months calculation is written below based the functional requirement
		 * documentation as for Oct, Nov and Dec months Get call to initiate
		 * from current finyear and for rest of the months Get call to initiate
		 * from Previous finyear
		 */
		if (isFinYearGet || numOfTaxPeriods == null) {
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
			LOGGER.debug("Getting all the active GSTNs for GSTR1 save");
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
