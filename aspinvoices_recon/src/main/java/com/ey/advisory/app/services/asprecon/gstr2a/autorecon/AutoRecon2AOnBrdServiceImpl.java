package com.ey.advisory.app.services.asprecon.gstr2a.autorecon;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.admin.data.entities.client.EntityInfoEntity;
import com.ey.advisory.admin.data.repositories.client.EntityInfoDetailsRepository;
import com.ey.advisory.app.data.entities.client.asprecon.AutoRecon2APROnBoardingEntity;
import com.ey.advisory.app.data.entities.client.asprecon.AutoRecon2APROnBrdAdParEntity;
import com.ey.advisory.app.data.repositories.client.asprecon.AutoRecon2APROnBoardingRepo;
import com.ey.advisory.app.data.repositories.client.asprecon.AutoRecon2APROnBrdAdParRepo;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Siva.Reddy
 *
 */
@Slf4j
@Component("AutoRecon2AOnBrdServiceImpl")
public class AutoRecon2AOnBrdServiceImpl implements AutoRecon2AOnBrdService {

	final static Map<Integer, String> categOrderMap = ImmutableMap
			.<Integer, String>builder().put(0, "Match").put(1, "MisMatch")
			.put(2, "Additional Entries").put(3, "Locked Records").build();

	final static Map<Integer, String> addParamOrderMap = ImmutableMap
			.<Integer, String>builder().put(0, "GSTR-1 & 3B Filed")
			.put(1, "GSTR-1 Filed but GSTR-3B Not Filed").put(2, "GSTR-3B Filed but GSTR-1 Not Filed")
			.put(3, "Both GSTR-1 & 3B Not Filed").build();
	
	public static final String DEFAULT_IMS_ONBOARDING_DETAILS = "No Response";
	
	public static final String DEFAULT_IMS_ONBOARDINGPARAM_DETAILS = "No change";

	@Autowired
	@Qualifier("AutoRecon2APROnBoardingRepo")
	AutoRecon2APROnBoardingRepo autoRecon2APrRepo;

	@Autowired
	@Qualifier("AutoRecon2APROnBrdAdParRepo")
	AutoRecon2APROnBrdAdParRepo autoRecon2APrAddParaRepo;

	@Autowired
	@Qualifier("entityInfoDetailsRepository")
	private EntityInfoDetailsRepository entityInfoDetailsRepository;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	@Transactional(value = "clientTransactionManager")
	public String saveOnBoardingDetails(AutoRecon2APROnBoardingReqDto reqDto) {

		String createdBy = SecurityContext.getUser() != null
				? (SecurityContext.getUser().getUserPrincipalName() != null
						? SecurityContext.getUser().getUserPrincipalName()
						: "SYSTEM")
				: "SYSTEM";

		List<AutoRecon2APROnBoardingEntity> autoReconList = new ArrayList<>();

		try {
			String entityId = reqDto.getEntityId();

			EntityInfoEntity entityInfo = entityInfoDetailsRepository
					.findEntityByEntityId(Long.valueOf(entityId));
			String entityName = entityInfo.getEntityName();

			for (AutoRecon2APRReqList reqList : reqDto.getReq()) {

				String reportCategory = reqList.getParticulars();

				int categOrder = reqList.getLevel();

				for (AutoRecon2APRCategoriesDto categReqList : reqList
						.getCategories()) {
					AutoRecon2APROnBoardingEntity autoReconEntity = new AutoRecon2APROnBoardingEntity();
					String reportType = categReqList.getReportType();
					String autoLock = categReqList.getAutoLock();
					String imsActionAllowed = categReqList.getImsActionAllowed(); 
					String imsActionBlocked= categReqList.getImsActionBlocked();	
					String reverseFeed = categReqList.getReverseFeed();
					String erpReportType = categReqList.getERPReportType();
					String approvalStatus = categReqList.getApporvalStatus();
					autoReconEntity.setReportCategory(reportCategory);
					autoReconEntity.setReverseFeed(reverseFeed);
					autoReconEntity.setReportType(reportType);
					autoReconEntity.setAutoLock(autoLock);
					autoReconEntity.setErpReportType(erpReportType);
					autoReconEntity.setApprovalStatus(approvalStatus);
					autoReconEntity.setCreatedBy(createdBy);
					autoReconEntity.setEntityId(entityId);
					autoReconEntity.setEntityName(entityName);
					autoReconEntity.setCreatedDate(LocalDateTime.now());
					autoReconEntity.setActive(true);
					autoReconEntity.setCategOrder(categOrder);
					autoReconEntity.setOptionalReportSected(
							categReqList.getOptionalReportSelected());
					autoReconEntity.setImsActionAllowed(getValueOrDefault(
							imsActionAllowed, DEFAULT_IMS_ONBOARDING_DETAILS));
					autoReconEntity.setImsActionBlocked(getValueOrDefault(
							imsActionBlocked, DEFAULT_IMS_ONBOARDING_DETAILS));
					autoReconList.add(autoReconEntity);
				}
			}

			if (!autoReconList.isEmpty()) {
				int softdeleteCount = autoRecon2APrRepo
						.updateActiveExistingRecords(entityId, createdBy);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("{} records soft deleted for EntityId {} ",
							softdeleteCount, entityId);
				}
				autoRecon2APrRepo.saveAll(autoReconList);

				return "Success";
			} else {
				LOGGER.error("Auto Recon List is Empty {}", autoReconList);
				return "Failed";
			}
		} catch (Exception e) {
			String msg = "Exception Occured while Save the Data";
			LOGGER.error(msg, e);
			throw new AppException(e.getMessage(), e);
		}

	}

	@Override
	public JsonObject getOnBoardingDetails(String entityId) {

		List<AutoRecon2APROnBoardingEntity> onBrdEntity = getEntityList(
				entityId, true);
		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();

		JsonArray resparr = new JsonArray();
		JsonObject finalRespObj = new JsonObject();
		if (onBrdEntity.isEmpty()) {
			String errMsg = String.format(
					"No Data Available in OnBoarding Entity for the Entity %s",
					entityId);
			LOGGER.error(errMsg);
			finalRespObj.add("hdr",
					gson.toJsonTree(APIRespDto.creatErrorResp()));
			finalRespObj.add("resp", gson.toJsonTree("No Data Available"));
			return finalRespObj;
		}

		List<String> optionalReportList = Arrays.asList("Potential-I",
				"Potential-II", "Logical Match", "Doc No & Doc Date Mismatch",
				"Match (Imports)", "Mismatch (Imports)");

		Map<Integer, List<AutoRecon2APROnBoardingEntity>> groupByPriceMap = onBrdEntity
				.stream()
				.collect(Collectors.groupingBy(
						AutoRecon2APROnBoardingEntity::getCategOrder,
						TreeMap::new, Collectors.toList()));

		for (Map.Entry<Integer, List<AutoRecon2APROnBoardingEntity>> entry : groupByPriceMap
				.entrySet()) {
			JsonObject respObj = new JsonObject();
			JsonArray categArray = new JsonArray();
			respObj.addProperty("Particulars",
					categOrderMap.get(entry.getKey()));
			respObj.addProperty("lastUpdatedOn",
					EYDateUtil.fmt(EYDateUtil.toISTDateTimeFromUTC(
							onBrdEntity.get(0).getCreatedDate())));
			respObj.addProperty("Level", entry.getKey());
			for (int i = 0; i < entry.getValue().size(); i++) {
				JsonObject categObj = new JsonObject();
				if ("Doc No & Doc Date Mismatch".equalsIgnoreCase(
						entry.getValue().get(i).getReportType())) {
					categObj.addProperty("ReportType",
							"Doc No & Doc Date Mismatch");
				} else {
					categObj.addProperty("ReportType",
							entry.getValue().get(i).getReportType());
				}
				categObj.addProperty("AutoLock",
						entry.getValue().get(i).getAutoLock());
				categObj.addProperty("ImsActionAllowed",
						entry.getValue().get(i).getImsActionAllowed());
				categObj.addProperty("ImsActionBlocked",
						entry.getValue().get(i).getImsActionBlocked());
				categObj.addProperty("ReverseFeed",
						entry.getValue().get(i).getReverseFeed());
				categObj.addProperty("ERPReportType",
						entry.getValue().get(i).getErpReportType());
				categObj.addProperty("ApporvalStatus",
						entry.getValue().get(i).getApprovalStatus());
				// Optional Report
				if (!optionalReportList
						.contains(entry.getValue().get(i).getReportType())) {
					categObj.addProperty("OptionalReportSelected", true);
				} else {
					categObj.addProperty("OptionalReportSelected",
							entry.getValue().get(i)
									.getOptionalReportSected() != null
											? entry.getValue().get(i)
													.getOptionalReportSected()
											: false);
				}
				categArray.add(categObj);
			}
			respObj.add("categories", categArray);
			resparr.add(respObj);
		}
		finalRespObj.add("hdr",
				gson.toJsonTree(APIRespDto.createSuccessResp()));
		finalRespObj.add("resp", gson.toJsonTree(resparr));

		return finalRespObj;
	}

	@Override
	public JsonObject getOnBoardingAddParamDetails(String entityId) {

		List<AutoRecon2APROnBrdAdParEntity> onBrdEntity = autoRecon2APrAddParaRepo
				.findByEntityIdAndIsActiveTrue(entityId);
		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
		JsonArray resparr = new JsonArray();
		JsonObject finalRespObj = new JsonObject();
		if (onBrdEntity.isEmpty()) {
			String errMsg = String.format(
					"No Data Available in OnBoarding Entity for the Entity %s",
					entityId);
			LOGGER.error(errMsg);
			finalRespObj.add("hdr",
					gson.toJsonTree(APIRespDto.creatErrorResp()));
			finalRespObj.add("resp", gson.toJsonTree("No Data Available"));
			return finalRespObj;
		}

		Map<Integer, List<AutoRecon2APROnBrdAdParEntity>> groupByPriceMap = onBrdEntity
				.stream()
				.collect(Collectors.groupingBy(
						AutoRecon2APROnBrdAdParEntity::getCategOrder,
						TreeMap::new, Collectors.toList()));

		for (Map.Entry<Integer, List<AutoRecon2APROnBrdAdParEntity>> entry : groupByPriceMap
				.entrySet()) {
			JsonObject respObj = new JsonObject();
			respObj.addProperty("Particulars",
					addParamOrderMap.get(entry.getKey()));
			respObj.addProperty("lastUpdatedOn",
					EYDateUtil.fmt(EYDateUtil.toISTDateTimeFromUTC(
							onBrdEntity.get(0).getCreatedDate())));
			respObj.addProperty("Level", entry.getKey());
			respObj.addProperty("AutoLock",
					entry.getValue().get(0).getAutoLock());
			respObj.addProperty("ImsActionAllowed",
					entry.getValue().get(0).getImsActionAllowed());
			respObj.addProperty("ImsActionBlocked",
					entry.getValue().get(0).getImsActionBlocked());
			respObj.addProperty("ReverseFeed",
					entry.getValue().get(0).getReverseFeed());
			respObj.addProperty("ERPReportType",
					entry.getValue().get(0).getErpReportType());
			respObj.addProperty("ApporvalStatus",
					entry.getValue().get(0).getApprovalStatus());
			resparr.add(respObj);
		}
		finalRespObj.add("hdr",
				gson.toJsonTree(APIRespDto.createSuccessResp()));
		finalRespObj.add("resp", gson.toJsonTree(resparr));

		return finalRespObj;
	}

	@Override
	@Transactional(value = "clientTransactionManager")
	public String saveOnBrdAddParamDetails(
			AutoRecon2APROnBoardingReqDto reqDto) {

		String createdBy = SecurityContext.getUser() != null
				? (SecurityContext.getUser().getUserPrincipalName() != null
						? SecurityContext.getUser().getUserPrincipalName()
						: "SYSTEM")
				: "SYSTEM";

		List<AutoRecon2APROnBrdAdParEntity> autoReconList = new ArrayList<>();

		try {
			String entityId = reqDto.getEntityId();

			EntityInfoEntity entityInfo = entityInfoDetailsRepository
					.findEntityByEntityId(Long.valueOf(entityId));
			String entityName = entityInfo.getEntityName();

			for (AutoRecon2APRReqList reqList : reqDto.getReq()) {

				String reportCategory = reqList.getParticulars();

				int categOrder = reqList.getLevel();

				AutoRecon2APROnBrdAdParEntity autoReconEntity = new AutoRecon2APROnBrdAdParEntity();
				String autoLock = reqList.getAutoLock();
				String reverseFeed = reqList.getReverseFeed();
				String erpReportType = reqList.getERPReportType();
				String approvalStatus = reqList.getApporvalStatus();
				String imsActionAllowed = reqList.getImsActionAllowed(); 
				String imsActionBlocked= reqList.getImsActionBlocked(); 
				autoReconEntity.setParticulars(reportCategory);
				autoReconEntity.setReverseFeed(reverseFeed);
				autoReconEntity.setId(categOrder + 1);
				autoReconEntity.setAutoLock(autoLock);
				autoReconEntity.setErpReportType(erpReportType);
				autoReconEntity.setApprovalStatus(approvalStatus);
				autoReconEntity.setCreatedBy(createdBy);
				autoReconEntity.setEntityId(entityId);
				autoReconEntity.setEntityName(entityName);
				autoReconEntity.setCreatedDate(LocalDateTime.now());
				autoReconEntity.setActive(true);
				autoReconEntity.setCategOrder(categOrder);
				autoReconEntity.setImsActionAllowed(getValueOrDefault(
						imsActionAllowed, DEFAULT_IMS_ONBOARDINGPARAM_DETAILS));
				autoReconEntity.setImsActionBlocked(getValueOrDefault(
						imsActionBlocked, DEFAULT_IMS_ONBOARDINGPARAM_DETAILS));
				autoReconList.add(autoReconEntity);

			}

			if (!autoReconList.isEmpty()) {
				int softdeleteCount = autoRecon2APrAddParaRepo
						.updateActiveExistingRecords(entityId, createdBy);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("{} records soft deleted for EntityId {} ",
							softdeleteCount, entityId);
				}

				autoRecon2APrAddParaRepo.saveAll(autoReconList);

				return "Success";
			} else {
				LOGGER.error("Auto Recon List is Empty {}", autoReconList);
				return "Failed";
			}
		} catch (Exception e) {
			String msg = "Exception Occured while Save the Data";
			LOGGER.error(msg, e);
			throw new AppException(e.getMessage(), e);
		}

	}

	public List<AutoRecon2APROnBoardingEntity> getEntityList(String entityId,
			boolean isActive) throws AppException {
		try {
			String queryString = "SELECT C.REPORT_TYPE_ID, C.REPORT_CATEGORY, C.REPORT_TYPE, "
			        + "C.ENTITY_ID, C.ENTITY_NAME, C.AUTO_LOCK, C.REVERSE_FEED, C.ERP_REPORT_TYPE, "
			        + "C.APPROVAL_STATUS, C.Optional_Report, C.CREATED_BY, C.IS_ACTIVE, "
			        + "C.CATEG_ORDER, C.CREATED_DATE, C.MODIFIED_DATE, "
			        + "C.IMS_ACTION_ALLOWED, C.IMS_ACTION_BLOCKED "
			        + "FROM TBL_AUTO_2APR_ERP_ONBOARDING C "
			        + "WHERE C.ENTITY_ID = :entityId AND C.IS_ACTIVE = :isActive "
			        + "ORDER BY C.CATEG_ORDER, C.REPORT_TYPE_ID ASC";

			Query q = entityManager.createNativeQuery(queryString);
			q.setParameter("entityId", entityId);
			q.setParameter("isActive", isActive);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("executing query to get the data of RequestIds "
						+ ", entityId " + entityId + "and query = "
						+ queryString);
			}
			@SuppressWarnings("unchecked")
			List<Object[]> list = q.getResultList();
			List<AutoRecon2APROnBoardingEntity> reconDataList = list.stream()
					.map(o -> convert(o))
					.collect(Collectors.toCollection(ArrayList::new));

			return reconDataList;
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage(), ex);
			throw new AppException("Exception Occured in "
					+ "EWB3WayInitiateReconReportRequestStatusDaoImpl.getRequestIds");
		}
	}

	private AutoRecon2APROnBoardingEntity convert(Object[] arr) {
		AutoRecon2APROnBoardingEntity entity = new AutoRecon2APROnBoardingEntity();
		entity.setId((arr[0] != null) ? Long.valueOf(arr[0].toString()) : null);
		entity.setReportCategory((arr[1] != null) ? arr[1].toString() : null);
		entity.setReportType((arr[2] != null) ? arr[2].toString() : null);
		entity.setEntityId((arr[3] != null) ? arr[3].toString() : null);
		entity.setEntityName((arr[4] != null) ? arr[4].toString() : null);
		entity.setAutoLock((arr[5] != null) ? arr[5].toString() : null);
		entity.setReverseFeed((arr[6] != null) ? arr[6].toString() : null);
		entity.setErpReportType((arr[7] != null) ? arr[7].toString() : null);
		entity.setApprovalStatus((arr[8] != null) ? arr[8].toString() : null);
		entity.setOptionalReportSected(
				(arr[9] != null) ? Boolean.valueOf(arr[9].toString()) : null);
		entity.setCreatedBy((arr[10] != null) ? arr[10].toString() : null);
		entity.setActive(
				(arr[11] != null) ? Boolean.valueOf(arr[11].toString()) : null);
		entity.setCategOrder((arr[12] != null)
				? Integer.parseInt(arr[12].toString()) : null);
		Timestamp createdDate = (Timestamp) arr[13];
		if (createdDate != null) {
			LocalDateTime dt = createdDate.toLocalDateTime();
			entity.setCreatedDate(dt);
		}
		Timestamp updatedDate = (Timestamp) arr[14];
		if (updatedDate != null) {
			LocalDateTime dt = updatedDate.toLocalDateTime();
			entity.setModifiedDate(dt);
		}
		entity.setImsActionAllowed(
				arr[15] != null ? arr[15].toString() : null);
		entity.setImsActionBlocked(
				arr[16] != null ? arr[16].toString() : null);
		return entity;
	}
	
    private static String getValueOrDefault(String value, String defaultValue) {
        return (value != null && !value.isEmpty()) ? value : defaultValue;
    }
}
