/**
 * 
 */
package com.ey.advisory.monitor.processors;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.AutoReconStatusEntity;
import com.ey.advisory.admin.data.entities.client.ConfigQuestionEntity;
import com.ey.advisory.admin.data.repositories.client.ConfigQuestionRepository;
import com.ey.advisory.admin.data.repositories.client.EntityConfigPrmtRepository;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.data.entities.client.asprecon.Gstr2ReconConfigEntity;
import com.ey.advisory.app.data.repositories.client.AutoReconStatusRepository;
import com.ey.advisory.app.data.repositories.client.EntityInfoRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.AIM3BLockStatusRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2ReconConfigRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2ReconGstinRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.ReconStatusConstants;
import com.ey.advisory.common.multitenancy.DefaultMultiTenantTaskProcessor;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.async.domain.master.Group;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;

import lombok.extern.slf4j.Slf4j;

/**
 * @author sakshi.jain
 *
 */
@Slf4j
@Service("Monitor2APR2BPRInitiateMatchingQueueProcessor")
public class Monitor2APR2BPRInitiateMatchingQueueProcessor
		extends DefaultMultiTenantTaskProcessor {

	@Autowired
	AsyncJobsService asyncJobsService;

	@Autowired
	@Qualifier("Gstr2ReconConfigRepository")
	Gstr2ReconConfigRepository reconConfigRepo;

	@Autowired
	@Qualifier("ConfigQuestionRepository")
	private ConfigQuestionRepository configQuestionRepo;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	private ConfigManager configManager;

	@Autowired
	@Qualifier("entityInfoRepository")
	private EntityInfoRepository entityInfoRepo;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gSTNDetailRepo;

	@Autowired
	@Qualifier("EntityConfigPrmtRepository")
	private EntityConfigPrmtRepository entityConfigPemtRepo;

	@Autowired
	private AutoReconStatusRepository autoReconStatusRepo;

	@Autowired
	@Qualifier("Gstr2ReconGstinRepository")
	Gstr2ReconGstinRepository reconGstinRepo;
	
	private static List<String> status = ImmutableList
			.of(ReconStatusConstants.RECON_IN_QUEUE);

	private static List<String> auto3BLockStatusList = Arrays
			.asList("INITIATED", "INPROGRESS");

	@Autowired
	@Qualifier("AIM3BLockStatusRepository")
	AIM3BLockStatusRepository status3BRepo;

	private static List<String> ineligibleStatus = ImmutableList.of(
			ReconStatusConstants.RECON_INITIATED,
			ReconStatusConstants.RECON_INPROGRESS);

	@Override
	public void executeForGroup(Group group, Message message,
			AppExecContext ctx) {
		Gstr2ReconConfigEntity entity = null;
		try {

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Inside MonitorInitiateMatchingQueueProcessor "
								+ "group code {} : ",
						TenantContext.getTenantId());
			}

			if (!isReconEligible()) {

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Inside MonitorInitiateMatchingQueueProcessor "
							+ "group code {} no eligible recon found "
							+ "to proceed, hence terminating the "
							+ "process: ", TenantContext.getTenantId());
				}
				return;
			}

			// to find the recon in queue list
			List<Gstr2ReconConfigEntity> entityList = reconConfigRepo
					.findByStatusIn(status);

			entityList = entityList.stream()
					.filter(o -> !o.getType().equalsIgnoreCase("EINVPR"))
					.collect(Collectors.toList());

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Inside MonitorInitiateMatchingQueueProcessor "
								+ "group code {}, entityList {} whih are in queue: ",
						TenantContext.getTenantId(), entityList);
			}
			if (entityList.isEmpty()) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Inside MonitorInitiateMatchingQueueProcessor "
							+ "group code {} entityList list is empty "
							+ "no pending recon available to " + "process : ",
							TenantContext.getTenantId());
				}

				return;
			}

			// check for auto recon eligible or not
			
			Map<String, Config> configMap = configManager
					.getConfigs("AUTORECON", "autorecon.optout", "DEFAULT");

			String groupCode = group.getGroupCode();

			String inEligibleGrps = configMap != null
					&& configMap.get("autorecon.optout.groups") != null
							? configMap.get("autorecon.optout.groups")
									.getValue()
							: null;
			boolean isAutoReconCheckRequired = true;
			
			
			if (inEligibleGrps != null && !Strings.isNullOrEmpty(inEligibleGrps)) {
				List<String> grpDtls = Arrays.asList(inEligibleGrps.split(","));
				if (grpDtls.contains(group.getGroupCode())) {
					String logMsg = String.format(
							"Group Code %s is opted to disable Auto Recon , Hence returning the Job",
							groupCode);
					LOGGER.error(logMsg);
					isAutoReconCheckRequired = false;
				}
			}
			
			//have to make it isAutoReconCheckRequired when moving to prod 
			// check if any auto recon is inprogress
			if (false) {

				List<Long> entityIds = entityInfoRepo.findActiveEntityIds();

				Set<Long> optedEntities = new HashSet<>();

				ConfigQuestionEntity confQuestionEntity = configQuestionRepo
						.findByQuestionCodeAndQuestionType("I27", "R");
				List<Long> optedEntities2A = entityConfigPemtRepo
						.getAllEntitiesOpted2A(entityIds, "I27",
								confQuestionEntity.getId());

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Entities opted for Auto GSTR2A for groupcode :{} ,are {}",
							group.getGroupCode(), optedEntities);
				}

				ConfigQuestionEntity confQuestion6AEntity = configQuestionRepo
						.findByQuestionCodeAndQuestionType("I35", "R"); // check
				List<Long> optedEntities6A = entityConfigPemtRepo
						.getAllEntitiesOpted2A(entityIds, "I35",
								confQuestion6AEntity.getId());

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Entities opted for Auto GSTR6A for groupcode :{} ,are {}",
							group.getGroupCode(), optedEntities6A);
				}

				optedEntities.addAll(optedEntities2A);
				optedEntities.addAll(optedEntities6A);

				if (!optedEntities.isEmpty()) {

					List<AutoReconStatusEntity> entities = new ArrayList<>();

					List<AutoReconStatusEntity> entities2A = autoReconStatusRepo
							.findByGet2aStatusAndReconStatusIsNullAndDate(
									"INITIATED",
									EYDateUtil
											.toISTDateTimeFromUTC(
													LocalDateTime.now())
											.toLocalDate());

					List<AutoReconStatusEntity> entities6A = autoReconStatusRepo
							.findByGet6aStatusAndReconStatusIsNullAndDate(
									"INITIATED",
									EYDateUtil
											.toISTDateTimeFromUTC(
													LocalDateTime.now())
											.toLocalDate());
					entities.addAll(entities2A);
					entities.addAll(entities6A);

					if (!entities.isEmpty()) {
						LOGGER.error(" AUTO RECON IS IN PROGRESS FOR GROUP {} ",
								TenantContext.getTenantId());

						return;
					}

				}
			}
				
				// check for 3b locking is in progress or not 
				int auto3BstatusCount = status3BRepo
						.findByStatusIn(auto3BLockStatusList);
				
				List<Long> reconHaltEntities = new ArrayList<>();
				reconHaltEntities = reconConfigRepo.findReconHaultEntityId();
				
				
				// notification for recon halt
				
				if (auto3BstatusCount == 0 && reconHaltEntities.isEmpty()) {
					

					LOGGER.error(" 3b locking and recon halt is not running/present {} ",
							TenantContext.getTenantId());
					
					postReconJob(entityList, Arrays.asList("NON_AP_M_2APR",
							"AP_M_2APR", "2BPR"));
				} else {
					
					LOGGER.error(" 3b locking and recon halt is running/present {} ",
							TenantContext.getTenantId());
					
					postReconJob(entityList, Arrays.asList("2BPR"));

				}

			
		} catch (Exception e) {
			LOGGER.error(
					"Error occured Inside MonitorInitiateMatchingQueueProcessor "
							+ "group code {}, while recon job submitting : "
							+ " : ",
					TenantContext.getTenantId());
			throw new AppException(e);

		}

	}

	private boolean isReconEligible() {

		LOGGER.debug(" INSIDE is recon eligible block ");
		List<Gstr2ReconConfigEntity> entityList = reconConfigRepo
				.findByStatusIn(ineligibleStatus);

		entityList = entityList.stream()
				.filter(o -> !o.getType().equalsIgnoreCase("EINVPR"))
				.collect(Collectors.toList());
			
		if (!entityList.isEmpty()) {

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Inside MonitorInitiateMatchingQueueProcessor - "
								+ "isReconEligible() group code {} previous recon "
								+ "is already in Initiate / Inprogress status hence "
								+ "returing false ",
						TenantContext.getTenantId());
			}
			return false;
		}

		return true;
	}

	private void postReconJob(List<Gstr2ReconConfigEntity> entityList,
			List<String> reconTypes) {
		Gstr2ReconConfigEntity entity = null;

		entityList.sort(
				Comparator.comparing(Gstr2ReconConfigEntity::getConfigId));
		
		LOGGER.debug(" entityList {} recontypes {} ",entityList,reconTypes);
		List<Gstr2ReconConfigEntity> recon2BprInQueue = new ArrayList<>();

		recon2BprInQueue = entityList.stream()
				.filter(o -> o.getType().equalsIgnoreCase("2BPR"))
				.collect(Collectors.toList());

		if (recon2BprInQueue.isEmpty()) {

			LOGGER.error(" NO 2BPR recons are in queue");

		} else {
			
			recon2BprInQueue.sort(
					Comparator.comparing(Gstr2ReconConfigEntity::getConfigId));
			entity = recon2BprInQueue.get(0);
			initiateRecon("2BPR", false, entity);
			return;
		}

		if (reconTypes.contains("NON_AP_M_2APR")
				|| reconTypes.contains("AP_M_2APR"))

		{
			entity = entityList.get(0);

			LOGGER.error(" Ainside 2Apr queue {} ",
					TenantContext.getTenantId());
			
			initiateRecon(entity.getType(), false, entity);
			return;
		}

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Inside MonitorInitiateMatchingQueueProcessor "
							+ "group code {}, recon job submitted :: ",
					TenantContext.getTenantId());
		}

	}

	private void initiateRecon(String reconType, boolean apFlag,
			Gstr2ReconConfigEntity entity) {
		reconConfigRepo.updateReconConfigStatusAndReportName(
				ReconStatusConstants.RECON_INITIATED, null, LocalDateTime.now(),
				entity.getConfigId());

		JsonObject jsonParams = new JsonObject();
		jsonParams.addProperty("configId", entity.getConfigId());

		if ("2BPR".equalsIgnoreCase(reconType)) {

			String groupCode = TenantContext.getTenantId();
			asyncJobsService.createJob(groupCode,
					JobConstants.GSTR2_RECON_INITIATE, jsonParams.toString(),
					"SYSTEM", 50L, null, null);

		} else {
			if (reconType.equalsIgnoreCase("AP_M_2APR")) {
				apFlag = true;
			}

			jsonParams.addProperty("apFlag", apFlag);
			

			LOGGER.error(" posting 2APr FOR GROUP {}, config id {} ",
					TenantContext.getTenantId(),entity.getConfigId());
			
			String groupCode = TenantContext.getTenantId();
			asyncJobsService.createJob(groupCode,
					JobConstants.GSTR2_AP_RECON_INITIATE, jsonParams.toString(),
					"SYSTEM", 50L, null, null);
		}

	}

}
