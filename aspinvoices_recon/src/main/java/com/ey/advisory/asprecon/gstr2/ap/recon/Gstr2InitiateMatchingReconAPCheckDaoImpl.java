/**
 * 
 */
package com.ey.advisory.asprecon.gstr2.ap.recon;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.admin.data.repositories.client.EntityConfigPrmtRepository;
import com.ey.advisory.app.data.entities.client.asprecon.Gstr2ReconAddlReportsEntity;
import com.ey.advisory.app.data.entities.client.asprecon.Gstr2ReconConfigEntity;
import com.ey.advisory.app.data.entities.client.asprecon.Gstr2ReconGstinEntity;
import com.ey.advisory.app.data.repositories.client.ImsReconStatusConfigRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.AIM3BLockStatusRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2ReconAddlReportsRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2ReconConfigRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2ReconGstinRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.ReconStatusConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */

@Slf4j
@Component("Gstr2InitiateMatchingReconAPCheckDaoImpl")
public class Gstr2InitiateMatchingReconAPCheckDaoImpl
		implements Gstr2InitiateMatchingReconAPCheckDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("Gstr2ReconConfigRepository")
	Gstr2ReconConfigRepository reconConfigRepo;

	@Autowired
	@Qualifier("Gstr2ReconAddlReportsRepository")
	Gstr2ReconAddlReportsRepository addlReportRepo;

	@Autowired
	@Qualifier("Gstr2ReconGstinRepository")
	Gstr2ReconGstinRepository reconGstinRepo;

	@Autowired
	@Qualifier("EntityConfigPrmtRepository")
	private EntityConfigPrmtRepository entityConfigPemtRepo;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	private ConfigManager configManager;

	@Autowired
	AsyncJobsService asyncJobsService;

	@Autowired
	@Qualifier("AIM3BLockStatusRepository")
	AIM3BLockStatusRepository status3BRepo;

	@Autowired
	@Qualifier("ImsReconStatusConfigRepository")
	ImsReconStatusConfigRepository imsReconRepo;
	
	// Non Ap Mnual--NON_AP_M_2APR
	// Ap manual---AP_M_2APR

	private static final String DOCUMENT_DATE_WISE = "Document Date Wise";
	private static final String IMPG_Report = "Consolidated IMPG Report";
	private static final String ISD_Report = "ISD Matching Report";
	private static final String TAX_PERIOD_WISE = "Tax Period Wise";
	private static final String NON_AP_M_2APR = "NON_AP_M_2APR";
	private static final String AP_M_2APR = "AP_M_2APR";
	private static final String AUTO_2APR = "AUTO_2APR";

	private static final String CONF_KEY = "gstr2.recon.2apr.report.chunk.size";
	private static final String CONF_CATEG = "RECON_REPORTS";
	public static final Map<String, Integer> RPT_TYP_MAP = new HashMap<String, Integer>() {{
        put("Exact Match", 1);
        put("Match With Tolerance", 2);
        put("Value Mismatch", 3);
        put("POS Mismatch", 4);
        put("Doc Date Mismatch", 5);
        put("Doc Type Mismatch", 6);
        put("Doc No Mismatch I", 7);
        put("Multi-Mismatch", 8);
        put("Potential-I", 9);
        put("Doc No Mismatch II", 10);
        put("Potential-II", 11);
        put("Logical Match", 12);
        put("Addition in 2A_6A", 13);
        put("Addition in PR", 14);
        put("Addition in 2B", 13);
        put("Force_Match_Records", 17);
        put("Doc No & Doc Date Mismatch", 25);
        put("Consolidated PR 2A Report IMS", 16);
        put("Consolidated PR 2B Report", 16);
        put("Consolidated PR 2A_6A Report", 16);
        put("Dropped 2A_6A Records Report", 15);
        put("ISD Matching Report", 26);
    }};
	private static List<String> status = ImmutableList.of(
			ReconStatusConstants.RECON_INITIATED,
			ReconStatusConstants.RECON_INPROGRESS,
			ReconStatusConstants.RECON_IN_QUEUE,
			ReconStatusConstants.RECON_HALT);

	private static List<String> auto3BLockStatusList = Arrays
			.asList("INITIATED", "INPROGRESS");

	@Override
	public String createReconcileData(List<String> gstins, Long entityId,
			String toTaxPeriod2A, String fromTaxPeriod2A, String toTaxPeriodPR,
			String fromTaxPeriodPR, String toDocDate, String fromDocDate,
			List<String> addlReportsList, String reconType,
			Boolean mandatoryReports) {

		if (LOGGER.isDebugEnabled()) {
			String msg = "Inside Gstr2InitiateMatchingReconDaoImpl"
					+ ".createReconcileData() method";
			LOGGER.debug(msg);
		}

		Gstr2ReconConfigEntity entity = new Gstr2ReconConfigEntity();

		List<String> type = new ArrayList<>();
		try {

			int auto3BstatusCount = status3BRepo
					.findByStatusIn(auto3BLockStatusList);

			if (auto3BstatusCount != 0) {
				String msg = String.format(
						"Auto 3B Locking is in progress, please initiate recon after sometime");
				LOGGER.error(msg);
				return msg;
			}
			
			// monitor ims changes
			int imstatusCount = imsReconRepo
					.findByStatusIn(auto3BLockStatusList);

			if (imstatusCount != 0) {
				String msg = String.format(
						"Auto IMS action based on Auto recon parameters is in progress,"
						+ " Please try after sometime.");
				LOGGER.error(msg);
				return msg;
			}
			
			if (!reconType.equalsIgnoreCase("2BPR")) {
				type = Arrays.asList(NON_AP_M_2APR, AP_M_2APR, AUTO_2APR);
			} else {
				type = Arrays.asList("2BPR");
			}

			List<Gstr2ReconConfigEntity> reconEntity = reconConfigRepo
					.findByTypeIn(type);
			Long count = reconEntity.stream()
					.filter(o -> status.contains(o.getStatus())).count();

			Boolean apFlag = isApOpted(entityId, entity);

			Long configId = generateCustomId(entityManager);

			User user = SecurityContext.getUser();
			String userName = user.getUserPrincipalName();

			if ((toDocDate != null && toDocDate != "" && !toDocDate.isEmpty())
					&& (fromDocDate != null && fromDocDate != ""
							&& !fromDocDate.isEmpty())) {
				entity.setToDocDate(LocalDate.parse(toDocDate));
				entity.setFromDocDate(LocalDate.parse(fromDocDate));
				entity.setRequestType(DOCUMENT_DATE_WISE);
			}

			entity.setEntityId(entityId);
			entity.setConfigId(configId);
			entity.setIsMandatory(mandatoryReports);
			entity.setReportChunkSize(getChunkSize());

			entity.setCreatedDate(LocalDateTime.now());

			entity.setPan(gstins.get(0).substring(2, 12));

			entity.setCreatedBy(userName != null ? userName : "SYSTEM");

			if ((toTaxPeriod2A != null && toTaxPeriod2A != ""
					&& !toTaxPeriod2A.isEmpty())
					&& (fromTaxPeriod2A != null && fromTaxPeriod2A != ""
							&& !fromTaxPeriod2A.isEmpty())
					&& (toTaxPeriodPR != null && toTaxPeriodPR != ""
							&& !toTaxPeriodPR.isEmpty())
					&& (fromTaxPeriodPR != null && fromTaxPeriodPR != ""
							&& !fromTaxPeriodPR.isEmpty())) {
				entity.setToTaxPeriodPR(Integer.parseInt(toTaxPeriodPR));
				entity.setFromTaxPeriodPR(Integer.parseInt(fromTaxPeriodPR));
				entity.setToTaxPeriod2A(Integer.parseInt(toTaxPeriod2A));
				entity.setFromTaxPeriod2A(Integer.parseInt(fromTaxPeriod2A));
				entity.setRequestType(TAX_PERIOD_WISE);
			}

			boolean isdFlag = false;
			if (addlReportsList.contains(IMPG_Report)) {
				entity.setImpgRecon(true);
			}
			if (addlReportsList.contains(ISD_Report)) {
				entity.setIsdRecon(true);
				isdFlag = true;
			} else {
				entity.setIsdRecon(false);
			}

			entity.setStatus(ReconStatusConstants.RECON_REQUESTED);
			Gstr2ReconConfigEntity obj = reconConfigRepo.save(entity);

			// Additional Logic for AP recon
			if (!apFlag) {
				// saving in child-GSTIN table
				List<Gstr2ReconGstinEntity> reconGstinObjList = gstins.stream()
						.map(o -> new Gstr2ReconGstinEntity(o,
								obj.getConfigId(), entity.getIsdRecon()))
						.collect(Collectors.toList());
				reconGstinRepo.saveAll(reconGstinObjList);
			} else {

				List<Gstr2ReconGstinEntity> gstinDetailList = persistReconReportGstinDetails(
						configId, gstins, entityId, isdFlag);

				reconGstinRepo.saveAll(gstinDetailList);
			}

			// SAVING IN TBL_RECON_RPT_DWNLD table
			if (reconType != null && reconType.equalsIgnoreCase("2APR")) {
				List<Gstr2ReconAddlReportsEntity> addlEntityList =addlReportsList
	                    .stream()
	                    .map(o -> {
	                        Integer rptid = RPT_TYP_MAP.get(o);                   
	                        return new Gstr2ReconAddlReportsEntity(o, obj.getConfigId(), false, rptid != null ? rptid : null);  // -1 or some default value
	                    }).collect(Collectors.toList());
				addlReportRepo.saveAll(addlEntityList);

			}

			if (count == 0) {
				reconConfigRepo.updateReconConfigStatusAndReportName(
						ReconStatusConstants.RECON_IN_QUEUE, null,
						LocalDateTime.now(), configId);

				JsonObject jsonParams = new JsonObject();
				jsonParams.addProperty("configId", configId);
				jsonParams.addProperty("apFlag", apFlag);

				/*String groupCode = TenantContext.getTenantId();
				asyncJobsService.createJob(groupCode,
						JobConstants.GSTR2_AP_RECON_INITIATE,
						jsonParams.toString(), userName, 50L, null, null);*/

			} else {
				reconConfigRepo.updateReconConfigStatusAndReportName(
						ReconStatusConstants.RECON_IN_QUEUE, null,
						LocalDateTime.now(), configId);
			}

		} catch (Exception ex) {
			String msg = String.format(
					"Exception occured while submitting recon job ", ex);
			LOGGER.error(msg, ex);
			return "failure";
		}

		return "Success";
	}

	/**
	 * @param entityId
	 * @param entityIds
	 * @param entity
	 * @return
	 */
	private Boolean isApOpted(Long entityId, Gstr2ReconConfigEntity entity) {

		List<Long> entityIds = new ArrayList<Long>();

		Boolean apFlag = true;
		entity.setType(AP_M_2APR);

		entityIds.add(entityId);
		List<Long> optedEntities = entityConfigPemtRepo
				.getAllEntitiesOpted2B(entityIds, "I27");

		if (optedEntities == null || optedEntities.isEmpty()) {
			entity.setType(NON_AP_M_2APR);
			apFlag = false;
		}
		return apFlag;
	}

	private static Long getNextSequencevalue(EntityManager entityManager) {

		String queryStr = "SELECT RECON_REPORT_SEQ.nextval " + "FROM DUMMY";

		Query query = entityManager.createNativeQuery(queryStr);

		Long seqId = ((Long) query.getSingleResult());

		return seqId;
	}

	private static Long generateCustomId(EntityManager entityManager) {
		String reportId = "";
		String digits = "";
		Long nextSequencevalue = getNextSequencevalue(entityManager);

		int currentYear = Calendar.getInstance().get(Calendar.YEAR);
		int currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
		String currentDate = currentYear
				+ (currentMonth < 10 ? ("0" + currentMonth)
						: String.valueOf(currentMonth));
		if (nextSequencevalue != null && nextSequencevalue > 0) {
			digits = String.format("%06d", nextSequencevalue);
			reportId = currentDate.concat(digits);
		}

		return Long.valueOf(reportId);
	}

	private List<Gstr2ReconGstinEntity> persistReconReportGstinDetails(
			Long configId, List<String> gstinList, Long entityId,
			boolean isdFlag) {

		List<Gstr2ReconGstinEntity> gstinDetailsList = new ArrayList<>();

		for (String gstin : gstinList) {

			try {
				Gstr2ReconGstinEntity reconGstinEntiy = new Gstr2ReconGstinEntity();
				reconGstinEntiy.setConfigId(configId);
				reconGstinEntiy.setGstin(gstin);
				reconGstinEntiy.setActive(true);
				reconGstinEntiy.setAutoReconDate(LocalDate.now());
				reconGstinEntiy.setFromDate(null);
				/*
				 * reconGstinEntiy.setFromDate(
				 * fetch2aAutoReconCompletedDate(entityId, gstin));
				 */
				reconGstinEntiy.setToDate(LocalDateTime.now());
				reconGstinEntiy.setStatus(ReconStatusConstants.RECON_INITIATED);
				reconGstinEntiy.setCretaedOn(LocalDateTime.now());
				reconGstinEntiy.setIsdRecon(isdFlag);
				gstinDetailsList.add(reconGstinEntiy);

			} catch (Exception ee) {
				String msg = String.format("Exception occured while persisting"
						+ " recon report gstin details for configId :%s "
						+ " and gstin :%s", configId, gstin);
				LOGGER.error(msg, ee);
				throw new AppException(msg);
			}
		}

		return gstinDetailsList;
	}

	// FIL sequence fromdate fix.

	/*
	 * private LocalDateTime fetch2aAutoReconCompletedDate(Long entityId, String
	 * gstin) { try {
	 * 
	 * LocalDateTime fromDate = LocalDateTime.now();
	 * 
	 * String query = "SELECT IFNULL(MAX(GD.COMPLETED_ON),'2020-04-01') " +
	 * "AS FROM_DATE FROM GSTIN_INFO G " +
	 * "LEFT OUTER JOIN TBL_RECON_REPORT_GSTIN_DETAILS GD ON " +
	 * "GD.GSTIN=G.GSTIN AND GD.STATUS='RECON_COMPLETED' " +
	 * "LEFT OUTER JOIN TBL_RECON_REPORT_CONFIG C ON " +
	 * "C.RECON_REPORT_CONFIG_ID=GD.RECON_REPORT_CONFIG_ID " +
	 * "AND C.RECON_TYPE IN ('AUTO_2APR','AP_M_2APR') " +
	 * "WHERE G.IS_DELETE=FALSE AND G.GSTIN =:gstin ";
	 * 
	 * 
	 * Query q = entityManager.createNativeQuery(query); q.setParameter("gstin",
	 * gstin);
	 * 
	 * if (LOGGER.isDebugEnabled()) {
	 * LOGGER.debug("executing query to get the recon complation date" +
	 * " from AUTO_2A_2B_RECON_STATUS table for given entityID" + +entityId +
	 * ", gstin " + gstin); }
	 * 
	 * @SuppressWarnings("unchecked") Object obj = q.getSingleResult();
	 * 
	 * if (obj == null) return LocalDateTime.of(2020, 04, 01, 0, 0, 0); //
	 * 01-Apr-2021 fromDate = ((Timestamp) obj).toLocalDateTime(); return
	 * fromDate;
	 * 
	 * } catch (Exception ee) { String msg =
	 * "Exception occured while fetching AP Recon completion " + "status";
	 * LOGGER.error(msg, ee); throw new AppException(msg); } }
	 */

	private Integer getChunkSize() {

		Config config = configManager.getConfig(CONF_CATEG, CONF_KEY);
		String chunkSize = config.getValue();
		return (Integer.valueOf(chunkSize));
	}

}
