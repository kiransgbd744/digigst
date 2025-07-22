package com.ey.advisory.app.inward.einvoice;

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

import com.ey.advisory.admin.data.entities.client.InwardEinvoiceAutomationEntity;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.admin.data.repositories.client.InwardEinvoiceAutomationRepository;
import com.ey.advisory.app.data.entities.client.GetAnx1BatchEntity;
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
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Sakshi.jain
 *
 */
@Slf4j
@Service("GetInwardEinvoiceAutomationHandler")
public class GetInwardEinvoiceAutomationHandler {

	private static final List<String> GETIRN_SUPPLY_TYPES = ImmutableList.of(
			APIConstants.INV_TYPE_B2B, APIConstants.INV_TYPE_SEZWP,
			APIConstants.INV_TYPE_SEZWOP, APIConstants.INV_TYPE_DXP,
			APIConstants.INV_TYPE_EXPWP, APIConstants.INV_TYPE_EXPWOP);
	
	
	@Autowired
	private AsyncJobsService asyncJobsService;

	@Autowired
	private GstnUserRequestUtil gstnUserRequestUtil;

	@Autowired
	private GSTNDetailRepository gstnDetailRepo;

	@Autowired
	private InwardEinvoiceAutomationRepository getInwardEinvAutomationRepo;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	private GSTNAuthTokenService authTokenService;

	@Autowired
	@Qualifier("getAnx1BatchRepository")
	private GetAnx1BatchRepository batchRepo;

	@Autowired
	@Qualifier("anx1GetBatchUtil")
	private Anx1GetBatchUtil batchUtil;
	
	/*@Autowired
	private AutoInwardEinvoiceStatusRepository autoInwardEinvoiceStatusRepo;
	*/
	
	private static final String REGULAR = "REGULAR";
	private static final String SEZ = "SEZ";
	private static final String SEZU = "SEZU";
	private static final String SEZD = "SEZD";
	private static final String ISD = "ISD";
	
	
	public Map<Long, List<Triplet<String, String, Integer>>> jobSubmissionAtEntityLevel(
			InwardEinvoiceAutomationEntity entity, Message message,
			Map<Long, List<Triplet<String, String, Integer>>> uniqueMap) {
		
		List<String> regTypeList = Arrays.asList(REGULAR, SEZ, SEZU, SEZD, 
				ISD);
		
		// Fetch all the GSTIN's for the entity
		List<String> activeGstins = gstnDetailRepo
				.getGstinBasedOnRegType(entity.getEntityId(),regTypeList);

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
		/*if (!inActiveGstins.isEmpty()) {

				List<AutoInwardEinvoiceStatusEntity> inactiveEntities = new ArrayList<>();
				inActiveGstins.forEach(inactiveGstin -> {

					AutoInwardEinvoiceStatusEntity reconStatus = new AutoInwardEinvoiceStatusEntity();
					reconStatus.setGstin(inactiveGstin);
					reconStatus.setEntityId(entity.getEntityId());
					reconStatus.setDate(
							EYDateUtil.toUTCDateTimeFromLocal(LocalDate.now()));
					reconStatus.setGetInwardEinvStatus(
							APIConstants.NOT_INITIATED.toUpperCase());
					reconStatus.setGetInwrdEinvRemarks("Auth Token is Inactive");
					reconStatus.setGetEvent(entity.getGetEvent());
					reconStatus.setNumOfTaxPeriods(1);
					reconStatus.setCreatedOn(LocalDateTime.now());
					inactiveEntities.add(reconStatus);

				});
				autoInwardEinvoiceStatusRepo.saveAll(inactiveEntities);
			}
*/		
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

		getInwardEinvAutomationRepo.save(entity);

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

		String returnType = APIConstants.GET_IRN_LIST.toUpperCase();

		initAndInprogressBatchs = batchRepo.findBatchByStatus(gstin, taxPeriods,
				returnType, initAndInProgressStatus);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Auto Inward Einvoice All GET Not eligible sections to execute are {} ",
					initAndInprogressBatchs);
		}

		return initAndInprogressBatchs;
	}

	private List<String> gstinLevelJobSegrigartion(InwardEinvoiceAutomationEntity entity,
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
									
				/*		AutoInwardEinvoiceStatusEntity reconStatus = new AutoInwardEinvoiceStatusEntity();
					reconStatus.setGstin(gstin);
					reconStatus.setEntityId(entity.getEntityId());
					reconStatus.setDate(
							EYDateUtil.toUTCDateTimeFromLocal(LocalDate.now()));
					reconStatus.setGetInwardEinvStatus(
							APIConstants.NOT_INITIATED.toUpperCase());
					reconStatus.setGetInwrdEinvRemarks("jobs are inprogress");
					reconStatus.setGetEvent(entity.getGetEvent());
					reconStatus.setNumOfTaxPeriods(1);
					reconStatus.setCreatedOn(LocalDateTime.now());
					autoInwardEinvoiceStatusRepo.save(reconStatus);
					return;*/
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
			InwardEinvoiceAutomationEntity entity, Message message) {

		// Inner Loop at taxPeriod level
		taxPeriods.forEach(taxPeriod -> {

			dto.setGstin(gstin);
			dto.setReturnPeriod(taxPeriod);
			dto.setGroupcode(TenantContext.getTenantId());
			dto.setApiSection(APIConstants.GET_IRN_LIST.toUpperCase());
			dto.setIsAutoGet(true);

			Long userRequestId = gstnUserRequestUtil.createGstnUserRequest(
					dto.getGstin(), dto.getReturnPeriod(), APIConstants.GET,
					APIConstants.GET_IRN_LIST.toUpperCase(), dto.getGroupcode(),
					APIConstants.SYSTEM.toUpperCase(), false, false, false);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("GstnUserRequest has created with id {} ",
						userRequestId);
			}
			dto.setUserRequestId(userRequestId);

			GETIRN_SUPPLY_TYPES.forEach(section -> {
				dto.setType(section);
				inwardEinvSupplyWiseGetJobInsertion(dto, entity, message);

			});

		});

	}

	private void inwardEinvSupplyWiseGetJobInsertion(Gstr1GetInvoicesReqDto dto,
			InwardEinvoiceAutomationEntity entity, Message message) {

		Gson gson = GsonUtil.newSAPGsonInstance();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Get Inward Einovoice JobsInsertion with reqDto {}, InwardEinvoiceAutomationEntity {} and message {} ",
					dto, entity, message);
		}

		dto = createBatchAndSave(dto.getGroupcode(), dto);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Batch has created and value set to dto {} ", dto);
		}
		String jsonParam = gson.toJson(dto);
		AsyncExecJob job = asyncJobsService.createJob(dto.getGroupcode(),
				JobConstants.GET_IRN_LIST, jsonParam,
				APIConstants.SYSTEM.toUpperCase(), JobConstants.PRIORITY,
				message.getId(), JobConstants.SCHEDULE_AFTER_IN_MINS);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("AsyncExecJob has created as {} ", job);
		}
		entity.setLastPostedJobId(job.getJobId());
		entity.setLastPostedDate(
				EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
		getInwardEinvAutomationRepo.save(entity);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Job Created for Inward Einvoice Get as {} ", job);
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
				APIConstants.GET_IRN_LIST.toUpperCase(), dto.getGstin(),
				dto.getReturnPeriod());
		GetAnx1BatchEntity batch = batchUtil.makeBatchGstr1(dto, dto.getType(),
				APIConstants.GET_IRN_LIST.toUpperCase());
		// Save new Batch
		batch = batchRepo.save(batch);
		dto.setBatchId(batch.getId());
		return dto;

	}


	private List<String> getAllInActiveGstnList(
			List<String> uniqueActiveGstins) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Getting all the active GSTNs for GET INWARDEINVOICE save");
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
