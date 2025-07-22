package com.ey.advisory.common;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.javatuples.Triplet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.AutoDraftAttributeConfig;
import com.ey.advisory.app.data.entities.client.BCAPIPushCtrlEntity;
import com.ey.advisory.app.data.entities.master.IdTokenGrpMapEntity;
import com.ey.advisory.app.data.repositories.client.AutoDrafAttrRepository;
import com.ey.advisory.app.data.repositories.client.BCAPIPushCtrlRepository;
import com.ey.advisory.app.data.repositories.master.IdTokenGrpMapRepository;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.async.domain.master.Group;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.ey.advisory.domain.client.ERPRequestLogEntity;
import com.ey.advisory.repositories.client.LoggerAdviceRepository;
import com.google.common.base.Strings;
import com.google.gson.JsonObject;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("MonitorCommonUtility")
public class MonitorCommonUtility {

	private int connectionTimeout;

	private int readTimeout;

	private int connectionManagerTimeout;

	private String region;

	@Autowired
	private BCAPIPushCtrlRepository bcAPIPushCtrlRepo;

	@Autowired
	private AutoDrafAttrRepository autoDraftAttrRepo;

	@Autowired
	private IdTokenGrpMapRepository idtokenGrpMapRepo;

	@Autowired
	private LoggerAdviceRepository logAdvRepo;

	@Autowired
	private IdTokenUtility idTokeUtility;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	@Autowired
	private AsyncJobsService asyncJobsService;

	@PostConstruct
	public void init() {
		Map<String, Config> timeOutconfigMap = configManager.getConfigs("BCAPI",
				"api.sap", "DEFAULT");
		Map<String, Config> regionconfigMap = configManager.getConfigs("BCAPI",
				"auto.drafting", "DEFAULT");

		connectionTimeout = timeOutconfigMap
				.containsKey("api.sap.global.conn_timeout")
						? Integer.valueOf(timeOutconfigMap
								.get("api.sap.global.conn_timeout").getValue())
						: 15000;
		readTimeout = timeOutconfigMap
				.containsKey("api.sap.global.read_timeout")
						? Integer.valueOf(timeOutconfigMap
								.get("api.sap.global.read_timeout").getValue())
						: 15000;
		connectionManagerTimeout = timeOutconfigMap
				.containsKey("api.sap.global.conn_mngr_timeout")
						? Integer.valueOf(timeOutconfigMap
								.get("api.sap.global.conn_mngr_timeout")
								.getValue())
						: 15000;

		region = regionconfigMap.containsKey("auto.drafting.region")
				? regionconfigMap.get("auto.drafting.region").getValue()
				: BusinessCriticalConstants.INDIA;
	}

	private static Long getNextSequencevalue(EntityManager entityManager) {

		String queryStr = "SELECT BCAPI_PAYLOAD_SEQ.nextval FROM DUMMY";

		Query query = entityManager.createNativeQuery(queryStr);

		Long seqId = ((Long) query.getSingleResult());

		return seqId;
	}

	private String generateCustomId(EntityManager entityManager) {
		Long nextSequencevalue = getNextSequencevalue(entityManager);
		String batchId = "BCAPI_" + region + "_" + nextSequencevalue;
		return batchId;
	}

	public String createAndPresistBatch(
			List<ERPRequestLogEntity> getSuccessReqPayloads, int payloadSize) {
		int requestListSize = getSuccessReqPayloads.size();
		BCAPIPushCtrlEntity bcAPIPushCtrlEntity = new BCAPIPushCtrlEntity();
		bcAPIPushCtrlEntity.setBatchId(generateCustomId(entityManager));
		bcAPIPushCtrlEntity.setFromId(getSuccessReqPayloads.get(0).getId());
		bcAPIPushCtrlEntity.setToId(
				getSuccessReqPayloads.get(requestListSize - 1).getId());
		bcAPIPushCtrlEntity.setPayloadSize(String.valueOf(payloadSize));
		bcAPIPushCtrlEntity.setPushStatus("Initiated");
		bcAPIPushCtrlEntity.setBatchCreatedDate(LocalDateTime.now());
		bcAPIPushCtrlRepo.save(bcAPIPushCtrlEntity);
		return bcAPIPushCtrlEntity.getBatchId();
	}

	public List<AutoDraftAttributeConfig> isEligibleForAutoDrafting() {

		String groupCode = TenantContext.getTenantId();
		try {
			IdTokenGrpMapEntity idtokenGrpEntity = idtokenGrpMapRepo
					.findByGroupCode(groupCode);

			if (idtokenGrpEntity == null) {
				String msg = String.format(
						"Group %s is not configured in GrpMapping Table",
						groupCode);
				LOGGER.error(msg);
				return null;
			}

			updateIdToken(idtokenGrpEntity, groupCode);

			List<AutoDraftAttributeConfig> attributeConfigList = autoDraftAttrRepo
					.findByIsActive(true);

			if (attributeConfigList.isEmpty()) {
				String msg = String.format(
						"No Active Combinations are available,Hence not "
								+ "monitoring AutoDraftinf for GroupCode %s",
						groupCode);
				LOGGER.error(msg);
				return null;
			}
			return attributeConfigList;
		} catch (Exception e) {
			String errMsg = String.format(
					"Exception in Monitor Job for AutoDrafting for  Group %s",
					groupCode);
			LOGGER.error(errMsg);
			return null;
		}
	}

	public void postCanorGenEwbJob(List<String> apiType, Group group,
			String chunkSizeKey) {
		try {
			List<AutoDraftAttributeConfig> activeCombList = isEligibleForAutoDrafting();
			if (activeCombList == null) {
				return;
			}

			Map<String, Config> configMap = configManager.getConfigs("BCAPI",
					"auto.drafting", TenantContext.getTenantId());

			String invChunkSize = configMap != null
					&& configMap.get(chunkSizeKey) != null
							? configMap.get(chunkSizeKey).getValue()
							: String.valueOf(250);

			Integer invChunkSizeInt = Integer.valueOf(invChunkSize);

			for (int i = 0; i < activeCombList.size(); i++) {

				String companyCode = activeCombList.get(i).getCompanyCode();
				String sourceId = activeCombList.get(i).getSourceId();

				LOGGER.debug("CompanyCode {} and SourceId {}", companyCode,
						sourceId);
				Pageable pageReq = PageRequest.of(0, invChunkSizeInt,
						Direction.ASC, "id");

				List<ERPRequestLogEntity> getSuccessReqPayloads = logAdvRepo
						.findByApiTypeInAndIsDuplicateFalseAndNicStatusTrueAndBatchIdIsNullAndCompanyCodeAndSourceId(
								apiType, companyCode, sourceId, pageReq);

				if (getSuccessReqPayloads.isEmpty()) {
					String msg = String.format(
							"No Invoices are available to Push to Cloud for Group-%s",
							group.getGroupCode());
					LOGGER.debug(msg);
					continue;
				}

				List<Long> idList = new ArrayList<>();
				if (BusinessCriticalConstants.GENEWB_IRN_V3
						.equalsIgnoreCase(apiType.get(0))) {

					Map<String, Long> irnToIdMap = getSuccessReqPayloads
							.stream().filter(e -> e.getIrnNum() != null)
							.collect(Collectors.toMap(
									ERPRequestLogEntity::getIrnNum,
									ERPRequestLogEntity::getId,
									(existing, replacement) -> existing));

					// If there’s already a value in the map for a given key
					// (the existing value), and a new one comes along with the
					// same key (the replacement), keep the existing one and
					// ignore the replacement.

					List<String> irnNumbers = new ArrayList<>(
							irnToIdMap.keySet());

					List<ERPRequestLogEntity> genEinvOriginalPayloads = logAdvRepo
							.findByIrnNumInAndApiTypeAndIsDuplicateFalseAndNicStatusTrueAndBatchIdIsNotNullAndCompanyCodeAndSourceIdAndIsAutoDraftedTrue(
									irnNumbers.isEmpty()
											? Collections.emptyList()
											: irnNumbers,
									BusinessCriticalConstants.GENEINV_V3,
									companyCode, sourceId);

					if (genEinvOriginalPayloads != null
							&& !genEinvOriginalPayloads.isEmpty()) {
						idList = genEinvOriginalPayloads.stream()
								.map(ERPRequestLogEntity::getIrnNum)
								.filter(irn -> irn != null
										&& irnToIdMap.containsKey(irn))
								.map(irnToIdMap::get)
								.collect(Collectors.toList());
					}

				} else {
					idList = getSuccessReqPayloads.stream().map(o -> o.getId())
							.collect(Collectors.toList());
				}

				String batchId = createAndPresistBatch(getSuccessReqPayloads,
						getSuccessReqPayloads.size());
				logAdvRepo.updateBatchIds(batchId, idList);
				JsonObject jobParams = new JsonObject();
				jobParams.addProperty("batchId", batchId);
				jobParams.addProperty("companyCode", companyCode);
				jobParams.addProperty("sourceId", sourceId);
				jobParams.addProperty("apiIdentifier", apiType.get(0));
				asyncJobsService.createJob(group.getGroupCode(),
						"PushInvoicesToCloud", jobParams.toString(), "SYSTEM",
						1L, null, null);
				String msg = String.format(
						"Job Posted Successfully for apiType %s", apiType);
				LOGGER.debug(msg);

			}
		} catch (

		Exception ex) {
			String msg = "Exception occured while periodic job for Can or Generate Ewb By Irn";
			LOGGER.error(msg, ex);
			throw new AppException(ex);
		}
	}

	public Triplet<Integer, Integer, Integer> getTimeOutDtls() {

		return new Triplet<Integer, Integer, Integer>(connectionTimeout,
				readTimeout, connectionManagerTimeout);

	}

	public void updateIdToken(IdTokenGrpMapEntity idtokenGrpEntity,
			String groupCode) {
		String idToken = null;
		if (Strings.isNullOrEmpty(idtokenGrpEntity.getIdToken())) {
			idToken = idTokeUtility.getIdTokenValue(
					idtokenGrpEntity.getUsername(),
					idtokenGrpEntity.getPassword());
			LocalDateTime expiryTime = LocalDateTime.now().plusHours(1);
			idtokenGrpMapRepo.updateIdToken(idToken, groupCode, expiryTime);
		} else {
			idToken = idtokenGrpEntity.getIdToken();
			LocalDateTime expiryTime = idtokenGrpEntity.getExpiryTime();
			LocalDateTime currentTime = LocalDateTime.now();

			if (expiryTime.isBefore(currentTime)) {
				LOGGER.debug(
						"Id Token is expired for Group Code {}, Hence generating a new one ",
						groupCode);
				expiryTime = LocalDateTime.now().plusHours(1);
				idToken = idTokeUtility.getIdTokenValue(
						idtokenGrpEntity.getUsername(),
						idtokenGrpEntity.getPassword());
				idtokenGrpMapRepo.updateIdToken(idToken, groupCode, expiryTime);
			} else {
				LOGGER.debug("Id Token is active for Group Code {} ",
						groupCode);
			}
		}
	}

}
