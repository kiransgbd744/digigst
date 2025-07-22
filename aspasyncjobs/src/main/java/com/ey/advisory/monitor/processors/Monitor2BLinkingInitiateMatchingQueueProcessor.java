/**
 * 
 */
package com.ey.advisory.monitor.processors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.Gstr2BLinkingConfigEntity;
import com.ey.advisory.app.data.entities.client.asprecon.Gstr2ReconConfigEntity;
import com.ey.advisory.app.data.entities.client.asprecon.Gstr2ReconGstinEntity;
import com.ey.advisory.app.data.repositories.client.Gstr2bLinkingConfigRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2ReconConfigRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2ReconGstinRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.ReconStatusConstants;
import com.ey.advisory.common.multitenancy.DefaultMultiTenantTaskProcessor;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.async.domain.master.Group;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Shashikant.Shukla
 *
 */
@Slf4j
@Service("Monitor2BLinkingInitiateMatchingQueueProcessor")
public class Monitor2BLinkingInitiateMatchingQueueProcessor
		extends DefaultMultiTenantTaskProcessor {

	@Autowired
	AsyncJobsService asyncJobsService;

	@Autowired
	@Qualifier("Gstr2ReconConfigRepository")
	Gstr2ReconConfigRepository reconConfigRepo;

	@Autowired
	@Qualifier("Gstr2ReconGstinRepository")
	Gstr2ReconGstinRepository reconGstinRepo;

	@Autowired
	@Qualifier("Gstr2bLinkingConfigRepository")
	private Gstr2bLinkingConfigRepository linkingRepo;

	private static List<String> eligibleStatus = ImmutableList
			.of("Linking In Queue");

	private static List<String> ineligibleStatus = ImmutableList
			.of("Linking In-Progress", "Linking Initiated");

	private static List<String> reconIneligibleStatus = ImmutableList.of(
			ReconStatusConstants.RECON_INITIATED,
			ReconStatusConstants.RECON_INPROGRESS);

	@Override
	public void executeForGroup(Group group, Message message,
			AppExecContext ctx) {
		try {

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Inside Monitor2BLinkingInitiateMatchingQueueProcessor "
								+ "group code {} : ",
						TenantContext.getTenantId());
			}
			List<Gstr2BLinkingConfigEntity> inEligibleEntities = linkingRepo
					.findByStatusInAndIsDelete(ineligibleStatus, false);
			if (!inEligibleEntities.isEmpty()) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Inside Monitor2BLinkingInitiateMatchingQueueProcessor "
									+ "group code {} no eligible linking found "
									+ "to proceed, hence terminating the "
									+ "process: ",
							TenantContext.getTenantId());
				}
				return;
			}
			List<Gstr2BLinkingConfigEntity> eligibleEntities = linkingRepo
					.findByStatusInAndIsDelete(eligibleStatus, false);

			Map<String, Gstr2BLinkingConfigEntity> linkingMap = new HashMap<>();
			for (Gstr2BLinkingConfigEntity linkingEntity : eligibleEntities) {
				String key = linkingEntity.getGstin() + "|"
						+ linkingEntity.getTaxPeriod();
				linkingMap.put(key, linkingEntity);
			}
			Map<String, String> reconMap = getAllReconGstinTaxPeriod();
			for (Map.Entry<String, Gstr2BLinkingConfigEntity> entry : linkingMap
					.entrySet()) {
				if (!reconMap.containsKey(entry.getKey())) {
					Gstr2BLinkingConfigEntity configEntity = entry.getValue();

					JsonObject jsonParams = new JsonObject();
					jsonParams.addProperty("batchId", configEntity.getId());
					jsonParams.addProperty("gstin", configEntity.getGstin());
					jsonParams.addProperty("taxPeriod",
							configEntity.getTaxPeriod());

					linkingRepo.gstr2bLinkingUpdateStatus(configEntity.getId(),
							"Linking Initiated");

					String groupCode = TenantContext.getTenantId();
					asyncJobsService.createJob(groupCode,
							JobConstants.GSTR2B_LINKING_INITIATE,
							jsonParams.toString(), configEntity.getCreatedBy(),
							50L, null, null);

					break;
				}
			}

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Inside Monitor2BLinkingInitiateMatchingQueueProcessor "
								+ "group code {}",
						TenantContext.getTenantId());
			}

		} catch (Exception e) {
			LOGGER.error(
					"Error occured Inside Monitor2BLinkingInitiateMatchingQueueProcessor "
							+ "group code {}, while 2B Linking job submitting ",
					TenantContext.getTenantId());
			throw new AppException(e);

		}

	}

	public Map<String, String> getAllReconGstinTaxPeriod() {
		Map<String, String> allGstinTaxPeriodMap = new HashMap<>();

		List<Gstr2ReconConfigEntity> entityList = reconConfigRepo
				.findByStatusIn(reconIneligibleStatus);
		entityList = entityList.stream()
				.filter(o -> o.getType().equalsIgnoreCase("2BPR"))
				.collect(Collectors.toList());

		List<Long> reconConfigs = new ArrayList<>();
		for (Gstr2ReconConfigEntity configEntity : entityList) {
			reconConfigs.add(configEntity.getConfigId());
		}
		List<Gstr2ReconGstinEntity> reconGstinEntities = reconGstinRepo
				.findAllGstinsByConfigIdIn(reconConfigs);
		for (Gstr2ReconConfigEntity configEntity : entityList) {
			Long configId = configEntity.getConfigId();
			List<String> taxPeriods = GenUtil
					.deriveTaxPeriodsGivenFromAndToPeriod(
							configEntity.getFromTaxPeriod2A().toString(),
							configEntity.getToTaxPeriod2A().toString());
			for (Gstr2ReconGstinEntity reconGstin : reconGstinEntities) {
				if (reconGstin.getConfigId() == configId) {
					for (String taxPeriod : taxPeriods) {
						String key = reconGstin.getGstin() + "|" + taxPeriod;
						allGstinTaxPeriodMap.put(key, key);
					}
				}
			}
		}

		return allGstinTaxPeriodMap;
	}
}
