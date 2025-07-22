package com.ey.advisory.app.ims.handlers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.javatuples.Triplet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.ImsAutomationEntity;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.admin.data.repositories.client.ImsAutomationRepository;
import com.ey.advisory.app.data.entities.client.GetAnx1BatchEntity;
import com.ey.advisory.app.data.repositories.client.GetAnx1BatchRepository;
import com.ey.advisory.app.util.Anx1GetBatchUtil;
import com.ey.advisory.app.util.GstnUserRequestUtil;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIIdentifiers;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.async.JobStatusConstants;
import com.ey.advisory.core.async.domain.master.AsyncExecJob;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Shashikant.Shukla
 *
 */
@Slf4j
@Service("GetImsAutomationHandler")
public class GetImsAutomationHandler {

	private static final List<String> GETIMS_SUPPLY_TYPES = ImmutableList.of(
			APIConstants.IMS_TYPE_B2B, APIConstants.IMS_TYPE_B2BA,
			APIConstants.IMS_TYPE_CN, APIConstants.IMS_TYPE_CNA,
			APIConstants.IMS_TYPE_DN, APIConstants.IMS_TYPE_DNA,
			APIConstants.IMS_TYPE_ECOM, APIConstants.IMS_TYPE_ECOMA);

	@Autowired
	private AsyncJobsService asyncJobsService;

	@Autowired
	private GstnUserRequestUtil gstnUserRequestUtil;

	@Autowired
	private GSTNDetailRepository gstnDetailRepo;

	@Autowired
	private ImsAutomationRepository imsAutomationRepo;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	private GSTNAuthTokenService authTokenService;

	@Autowired
	@Qualifier("getAnx1BatchRepository")
	private GetAnx1BatchRepository batchRepo;

	@Autowired
	@Qualifier("anx1GetBatchUtil")
	private Anx1GetBatchUtil batchUtil;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	private static final String REGULAR = "REGULAR";
	private static final String SEZ = "SEZ";
	private static final String SEZU = "SEZU";
	private static final String SEZD = "SEZD";

	public Map<Long, List<Triplet<String, String, Integer>>> jobSubmissionAtEntityLevel(
			ImsAutomationEntity entity, Message message,
			Map<Long, List<Triplet<String, String, Integer>>> uniqueMap) {

		// Fetch all the GSTIN's for the entity
		List<String> activeGstins = gstnDetailRepo.getGstr1Gstr3bActiveGstns();

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Total GSTIN'S {} for the entityId is {} ",
					activeGstins, entity.getEntityId());
		}

		List<String> automatedActiveGstins = new ArrayList<>();

		/**
		 * Returning inactive Gstins and setting active Gstins to
		 * uniqueActiveGstins list
		 */
		List<String> inActiveGstins = getAllInActiveGstnList(activeGstins);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("inActiveGstins {} for the entity is {} ",
					inActiveGstins, entity.getEntityId());
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

		imsAutomationRepo.save(entity);

		// current tax period only get call to be submitted

		LocalDate today = LocalDate.now();
		String[] datestr = today.toString().split("-");
		List<String> taxPeriods = Arrays.asList(datestr[1].concat(datestr[0]));

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

		return uniqueMap;
	}

	private List<String[]> isGstinAndTaxPeriodsJobInprogress(String gstin,
			List<String> taxPeriods) {

		List<String[]> initAndInprogressBatchs = null;
		List<String> initAndInProgressStatus = new ArrayList<>();
		initAndInProgressStatus.add(APIConstants.INITIATED.toUpperCase());
		initAndInProgressStatus
				.add(JobStatusConstants.IN_PROGRESS.toUpperCase());

		String returnType = APIConstants.GET_IMS_LIST.toUpperCase();

		initAndInprogressBatchs = batchRepo.findBatchByStatus(gstin, taxPeriods,
				returnType, initAndInProgressStatus);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Auto Inward Einvoice All GET Not eligible sections to execute are {} ",
					initAndInprogressBatchs);
		}

		return initAndInprogressBatchs;
	}

	private List<String> gstinLevelJobSegrigartion(ImsAutomationEntity entity,
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
			}

			Gstr1GetInvoicesReqDto dto = new Gstr1GetInvoicesReqDto();
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
			ImsAutomationEntity entity, Message message) {
		dto.setGstin(gstin);
		dto.setReturnPeriod("000000");
		dto.setGroupcode(TenantContext.getTenantId());
		dto.setApiSection(APIConstants.GET_IMS_LIST.toUpperCase());
		dto.setIsAutoGet(true);
		imsSupplyWiseGetJobInsertion(dto, entity, message);
	}

	private void imsSupplyWiseGetJobInsertion(Gstr1GetInvoicesReqDto dto,
			ImsAutomationEntity entity, Message message) {

		Gson gson = GsonUtil.newSAPGsonInstance();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Get ImsJobsInsertion with reqDto {}, ImsAutomationEntity {} and message {} ",
					dto, entity, message);
		}

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Batch has created and value set to dto {} ", dto);
		}
		String jsonParam = gson.toJson(dto);
		String groupCode = TenantContext.getTenantId();
		String userName = SecurityContext.getUser() != null
				? (SecurityContext.getUser().getUserPrincipalName() != null
						? SecurityContext.getUser().getUserPrincipalName()
						: "SYSTEM")
				: "SYSTEM";
        Long userCountRequestId = gstnUserRequestUtil.createGstnUserRequest(
				dto.getGstin(), dto.getReturnPeriod(), APIConstants.GET,
				APIConstants.IMS_COUNT.toUpperCase(), groupCode, userName,
				false, false, false);
		dto.setUserRequestId(userCountRequestId);
		dto.setApiSection(APIIdentifiers.IMS_COUNT.toUpperCase());

		List<AsyncExecJob> countJobs = new ArrayList<>();

		LOGGER.debug("Dto :: ", dto.toString());

		Map<String, Config> configMap = configManager.getConfigs("ImsConfig",
				"count.api.goodstypes", "DEFAULT");

		String goodtypeStr = configMap != null
				&& configMap.get("count.api.goodstypes") != null
						? configMap.get("count.api.goodstypes").getValue()
						: APIConstants.IMS_COUNT_TYPE_ALL_OTH;

		// need imutablelist of list of string from goodTypeStr
		List<String> GETIMS_GOODS_TYPES = ImmutableList
				.copyOf(Arrays.asList(goodtypeStr.split(",")));
		List<AsyncExecJob> jobList = new ArrayList<>();
		for (String suplyTyplist : GETIMS_GOODS_TYPES) {

			dto.setType(suplyTyplist.toUpperCase());
			dto = createCountBatchAndSave(groupCode, dto);
			LOGGER.debug("Dto after creating batch:: {}", dto.toString());
			jsonParam = gson.toJson(dto);
			jobList.add(asyncJobsService.createJobAndReturn(groupCode,
					JobConstants.GET_IMS_COUNT, jsonParam, userName,
					JobConstants.PRIORITY, JobConstants.PARENT_JOB_ID,
					JobConstants.SCHEDULE_AFTER_IN_MINS));
			LOGGER.debug("Dto after creating jsonparams:: {}",
					jsonParam.toString());

		}
		Long userInvoiceRequestId = gstnUserRequestUtil.createGstnUserRequest(
				dto.getGstin(), dto.getReturnPeriod(), APIConstants.GET,
				APIConstants.IMS_INVOICE.toUpperCase(), groupCode, userName,
				false, false, false);
		dto.setUserRequestId(userInvoiceRequestId);
		dto.setApiSection(APIIdentifiers.IMS_INVOICE.toUpperCase());

		for (String suplyTyplistInvoices : GETIMS_SUPPLY_TYPES) {

			dto.setType(suplyTyplistInvoices.toUpperCase());
			dto = createInvoiceBatchAndSave(groupCode, dto);
			LOGGER.debug("Dto after creating batch:: {}", dto.toString());
			jsonParam = gson.toJson(dto);
			jobList.add(asyncJobsService.createJobAndReturn(groupCode,
					JobConstants.GET_IMS_INVOICE, jsonParam, userName,
					JobConstants.PRIORITY, JobConstants.PARENT_JOB_ID,
					JobConstants.SCHEDULE_AFTER_IN_MINS));
			LOGGER.debug("Dto after creating jsonparams:: {}",
					jsonParam.toString());
		}

		if (!jobList.isEmpty())
			asyncJobsService.createJobs(jobList);

		// entity.setLastPostedJobId(job.getJobId());
		entity.setLastPostedDate(
				EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
		imsAutomationRepo.save(entity);

	}

	private Gstr1GetInvoicesReqDto createCountBatchAndSave(String groupCode,
			Gstr1GetInvoicesReqDto dto) {
		TenantContext.setTenantId(groupCode);
		// InActiveting Previous Batch Records
		batchRepo.softlyDelete(dto.getType().toUpperCase(),
				APIConstants.IMS_COUNT, dto.getGstin(), dto.getReturnPeriod());
		GetAnx1BatchEntity batch = batchUtil.makeBatchGstr1(dto, dto.getType(),
				APIConstants.IMS_COUNT);
		// Save new Batch
		batch = batchRepo.save(batch);
		dto.setBatchId(batch.getId());
		return dto;

	}

	private Gstr1GetInvoicesReqDto createInvoiceBatchAndSave(String groupCode,
			Gstr1GetInvoicesReqDto dto) {
		TenantContext.setTenantId(groupCode);
		// InActiveting Previous Batch Records
		batchRepo.softlyDelete(dto.getType().toUpperCase(),
				APIConstants.IMS_INVOICE, dto.getGstin(),
				dto.getReturnPeriod());
		GetAnx1BatchEntity batch = batchUtil.makeBatchGstr1(dto, dto.getType(),
				APIConstants.IMS_INVOICE);
		// Save new Batch
		batch = batchRepo.save(batch);
		dto.setBatchId(batch.getId());
		return dto;

	}

	private List<String> getAllInActiveGstnList(
			List<String> uniqueActiveGstins) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Getting all the active GSTNs for GET INWARDEINVOICE save");
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
