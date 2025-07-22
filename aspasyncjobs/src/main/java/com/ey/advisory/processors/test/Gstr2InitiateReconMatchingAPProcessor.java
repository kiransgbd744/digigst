/**
 * 
 */
package com.ey.advisory.processors.test;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.asprecon.Gstr2ReconAddlReportsEntity;
import com.ey.advisory.app.data.entities.client.asprecon.Gstr2ReconConfigEntity;
import com.ey.advisory.app.data.entities.client.asprecon.Gstr2ReconGstinEntity;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2ReconAddlReportsRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2ReconConfigRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2ReconGstinRepository;
import com.ey.advisory.asprecon.gstr2.ap.recon.Gstr2InitiateMatchingAPManualServiceImpl;
import com.ey.advisory.asprecon.gstr2.ap.recon.Gstr2InitiateMatchingNonAPManualServiceImpl;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.ReconStatusConstants;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.StoredProcedureQuery;
import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */

@Slf4j
@Component("Gstr2InitiateReconMatchingAPProcessor")
public class Gstr2InitiateReconMatchingAPProcessor implements TaskProcessor {

	@Autowired
	@Qualifier("Gstr2InitiateMatchingNonAPManualServiceImpl")
	Gstr2InitiateMatchingNonAPManualServiceImpl nonApService;

	@Autowired
	@Qualifier("Gstr2InitiateMatchingAPManualServiceImpl")
	Gstr2InitiateMatchingAPManualServiceImpl apService;

	@Autowired
	@Qualifier("Gstr2ReconConfigRepository")
	Gstr2ReconConfigRepository reconConfigRepo;

	@Autowired
	@Qualifier("Gstr2ReconGstinRepository")
	Gstr2ReconGstinRepository reconGstinRepo;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	private AsyncJobsService asyncJobsService;

	@Autowired
	@Qualifier("Gstr2ReconAddlReportsRepository")
	Gstr2ReconAddlReportsRepository addlReportRepo;

	private static final String NON_AP_M_2APR = "NON_AP_M_2APR";
	private static final String AP_M_2APR = "AP_M_2APR";

	private static Map<String, Integer> reportTypeIdMap = new HashMap<>();
	{
		reportTypeIdMap.put("Exact Match", 1);
		reportTypeIdMap.put("Match With Tolerance", 2);
		reportTypeIdMap.put("Value Mismatch", 3);
		reportTypeIdMap.put("POS Mismatch", 4);
		reportTypeIdMap.put("Doc Date Mismatch", 5);
		reportTypeIdMap.put("Doc Type Mismatch", 6);
		reportTypeIdMap.put("Doc No Mismatch I", 7);
		reportTypeIdMap.put("Multi-Mismatch", 8);
		reportTypeIdMap.put("Addition in PR", 14);
		reportTypeIdMap.put("Addition in 2A_6A", 13);
		reportTypeIdMap.put("Consolidated PR 2A_6A Report", 16);
		reportTypeIdMap.put("ERP_Report", 23);
		
		reportTypeIdMap.put("Doc No Mismatch II", 10);
		reportTypeIdMap.put("Dropped 2A_6A Records Report", 15);
		reportTypeIdMap.put("Force_Match_Records", 17);
		reportTypeIdMap.put("Logical Match", 12);
		reportTypeIdMap.put("Potential-I", 9);
		reportTypeIdMap.put("Potential-II", 11);
		reportTypeIdMap.put("Doc No & Doc Date Mismatch", 25);
		
	}

	private static List<String> defaultReports = new ArrayList<>(Arrays.asList(
			"Exact Match", "Match With Tolerance", "Value Mismatch",
			"POS Mismatch", "Doc Date Mismatch", "Doc Type Mismatch",
			"Doc No Mismatch I", "Multi-Mismatch", "Addition in PR",
			"Addition in 2A_6A", "Consolidated PR 2A_6A Report", "ERP_Report"));

	@Override
	public void execute(Message message, AppExecContext context) {

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Begin Gstr2InitiateReconMatchingAPProcessor :%s",
					message.toString());
			LOGGER.debug(msg);
		}

		String jsonString = message.getParamsJson();
		JsonParser parser = new JsonParser();
		JsonObject json = (JsonObject) parser.parse(jsonString);

		Long configId = json.get("configId").getAsLong();
		Boolean apFlag = json.get("apFlag").getAsBoolean();

		try {

			Optional<Gstr2ReconConfigEntity> entity = reconConfigRepo
					.findById(configId);
			
			if (!entity.isPresent()) {
			    LOGGER.error("Recon config entity not found for configId: {}", configId);
			    throw new AppException("Recon config entity not found");
			}

			reconConfigRepo.updateReconConfigStatusAndReportName(
					ReconStatusConstants.RECON_INPROGRESS, null,
					LocalDateTime.now(), configId);
			
			List<String> gstins = reconGstinRepo
					.findAllGstinsByConfigId(configId);

			String reconType = entity.get().getType();
			Long entityId = entity.get().getEntityId();

			if (reconType.equalsIgnoreCase(NON_AP_M_2APR)) {

				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(
							"Gstr2InitiateReconMatchingAPProcessor, Invoking "
									+ "executeNonAPManualRecon() configId %s, "
									+ "gstins %s " + "apFlag %s :",
							configId.toString(), gstins, apFlag);
					LOGGER.debug(msg);
				}

				nonApService.executeNonAPManualRecon(configId, apFlag, gstins);

			} else if (reconType.equalsIgnoreCase(AP_M_2APR)) {
				
				//fix for FIL sequencing 
				for (String gstin : gstins) {
					
					LocalDateTime fromdate = fetch2aAutoReconCompletedDate(
							entityId,gstin);
					
					if (LOGGER.isDebugEnabled()) {
						String msg = String.format(
								"Gstr2InitiateReconMatchingAPProcessor, Invoked "
										+ "fetch2aAutoReconCompletedDate() "
										+ "configId %s, "
										+ "gstin %s fromdate %s :",
								configId.toString(), gstin, fromdate.toString());
						LOGGER.debug(msg);
					}

					reconGstinRepo.updateFromDateByGstinAndConfigId(gstin,
							configId, fromdate);
					
					if (LOGGER.isDebugEnabled()) {
						String msg = String.format(
								"Gstr2InitiateReconMatchingAPProcessor, updated "
										+ "updateFromDateByGstinAndConfigId() "
										+ "configId %s, "
										+ "gstin %s fromdate %s :",
								configId.toString(), gstin, fromdate.toString());
						LOGGER.debug(msg);
					}
					
				}

				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(
							"Gstr2InitiateReconMatchingAPProcessor, Invoking "
									+ "executeAPManualRecon() configId %s,"
									+ " gstins %s apFlag %s :",
							configId.toString(), gstins, apFlag);
					LOGGER.debug(msg);
				}

				if (getIncrementalDataSummary(gstins)) {

					if (LOGGER.isDebugEnabled()) {
						String msg = String.format(
								"Gstr2InitiateReconMatchingAPProcessor, "
										+ " got delta data for configId %s,"
										+ " gstins %s apFlag %s ",
								configId.toString(), gstins, apFlag);
						LOGGER.debug(msg);
					}

					apService.executeAPManualRecon(configId, gstins, entityId);

				} else if (!checkForPreviousRecon(configId, gstins)) {

					if (LOGGER.isDebugEnabled()) {
						String msg = String.format(
								"Gstr2InitiateReconMatchingAPProcessor, "
										+ " No previous recon found with same "
										+ " criteria configId %s,"
										+ " gstins %s apFlag %s ",
								configId.toString(), gstins, apFlag);
						LOGGER.debug(msg);
					}
					apService.executeAPManualRecon(configId, gstins, entityId);

				} else if(checkForOnboardingAndRespChanges(configId, gstins, 
						entityId, entity)) {
					
					if (LOGGER.isDebugEnabled()) {
						String msg = String.format(
								"Gstr2InitiateReconMatchingAPProcessor, "
										+ "onboarding or response upload"
										+ " changes found with same "
										+ " criteria configId %s,"
										+ " gstins %s apFlag %s ",
								configId.toString(), gstins, apFlag);
						LOGGER.debug(msg);
					}
					apService.executeAPManualRecon(configId, gstins, entityId);
				}
				else {
					if (LOGGER.isDebugEnabled()) {
						String msg = String.format(
								"Gstr2InitiateReconMatchingAPProcessor, Didn't"
										+ " get delta data for configId %s,"
										+ " gstins %s apFlag %s  hence "
										+ "submitting report generation job "
										+ "directly :",
								configId.toString(), gstins, apFlag);
						LOGGER.debug(msg);
					}
					//updating reportTypeId
					
					List<Gstr2ReconAddlReportsEntity> selectedReportList = 
							addlReportRepo.findByConfigId(configId);

					for (Gstr2ReconAddlReportsEntity list : selectedReportList) {
						setReportTypeIdforAdditionalReport(list);
					}
					
					// create entry for in download table
					makeReportListEntry(configId);
					
					// update in TBL_RECON_REPORT_GSTIN_DETAILS table
					reconGstinRepo.updateReconCompletedStatusAndIsDeltaBulk(gstins,
							configId, "RECON_COMPLETED", LocalDateTime.now());

					// report Generation
					JsonObject jsonParams = new JsonObject();
					jsonParams.addProperty("configId", configId);
					jsonParams.addProperty("entityId", entityId);
					jsonParams.addProperty("apFlag", true);

					asyncJobsService.createJob(TenantContext.getTenantId(),
							JobConstants.GSTR2_RECON_REPORT_GENERATE,
							jsonParams.toString(), "SYSTEM", 50L, null, null);

				}
			}

		} catch (Exception e) {
			LOGGER.error(
					"Error occured in Gstr2InitiateReconMatchingAPProcessor  "
							+ "configId {} ",
					configId.toString());
			reconConfigRepo.updateReconConfigStatusAndReportName(
					ReconStatusConstants.RECON_FAILED, null,
					LocalDateTime.now(), configId);
			throw new AppException(e);
		}

	}
	
	private boolean checkForOnboardingAndRespChanges(Long configId,
			List<String> gstins, Long entityId, Optional<Gstr2ReconConfigEntity> entity) {

		try {
			if (!entity.isPresent()) {
				LOGGER.error("Entity is not present");
				throw new AppException("Entity not found");
			}
			Integer fromTaxPeriod2A = entity.get().getFromTaxPeriod2A();
			Integer toTaxPeriod2A = entity.get().getToTaxPeriod2A();
			
			Integer fromTaxPeriodPR = entity.get().getFromTaxPeriodPR();
			Integer toTaxPeriodPR = entity.get().getToTaxPeriodPR();
			
			String queryString1 = createQueryString1(gstins, fromTaxPeriodPR, 
					toTaxPeriodPR, fromTaxPeriod2A, toTaxPeriod2A, entityId);
			Query q = entityManager.createNativeQuery(queryString1);
			q.setParameter("gstins", gstins);
			q.setParameter("fromTaxPeriodPR", fromTaxPeriodPR);
			q.setParameter("toTaxPeriodPR",toTaxPeriodPR);
			q.setParameter("fromTaxPeriod2A", fromTaxPeriod2A);
			q.setParameter("toTaxPeriod2A", toTaxPeriod2A);
			q.setParameter("entityId", entityId);
			
			if (LOGGER.isDebugEnabled()) {
				LOGGER.info("executing query to get the data from "
						+ "onBoarding and Response Upload tables");
			}
			@SuppressWarnings("unchecked")
			List<Timestamp> timestamp1List = q.getResultList();
			Timestamp timestamp1 = timestamp1List.get(0);
			
			String queryString2 = createQueryString2(configId);
			Query q1 = entityManager.createNativeQuery(queryString2);
			
			q1.setParameter("configId", configId);
			
			@SuppressWarnings("unchecked")
			List<Timestamp> timestamp2List = q1.getResultList();
			Timestamp timestamp2 = timestamp2List.get(0);
			
			int timestamp = timestamp1.compareTo(timestamp2);  
			
			if(timestamp > 0)
			return true;
			else {
				return false;
			}
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage(), ex);
			throw new AppException("Exception Occured in "
					+ "Gstr3bComuteCopyToUserInputDaoImpl.gstr3bCopyData");
		}
	}

	private void setReportTypeIdforAdditionalReport(
			Gstr2ReconAddlReportsEntity o) {

		Integer reportTypeId = reportTypeIdMap.get(o.getReportType());
		addlReportRepo.updateReportTypeId(o.getConfigId(), o.getReportType(),
				reportTypeId);

	}

	private boolean getIncrementalDataSummary(List<String> recipientGstins) {
		String rGstins = "";
		try {
			if (recipientGstins != null && !recipientGstins.isEmpty()) {
				rGstins = String.join(",", recipientGstins);
			} else {
				rGstins = "";
			}

			StoredProcedureQuery storedProc = null;

			if (LOGGER.isDebugEnabled()) {
				String msg = String
						.format("Inside Gstr2InitiateReconMatchingAPProcessor"
								+ ".getIncrementalDataSummary():: "
								+ "Invoking USP_AUTO_2APR_PRE_RECON_SUMMARY_DELTA "
								+ "Stored Proc");
				LOGGER.debug(msg);
			}
			storedProc = entityManager.createStoredProcedureQuery(
					"USP_AUTO_2APR_PRE_RECON_SUMMARY_DELTA");

			storedProc.registerStoredProcedureParameter("P_GSTIN_LIST",
					String.class, ParameterMode.IN);
			storedProc.setParameter("P_GSTIN_LIST", rGstins);

			@SuppressWarnings("unchecked")
			String status = (String) storedProc.getSingleResult();

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("2APR AP manual, "
						+ "USP_AUTO_2APR_PRE_RECON_SUMMARY_DELTA response {}",
						status);
			}

			boolean reconFlag = ReconStatusConstants.SUCCESS
					.equalsIgnoreCase(status) ? true : false;
			return reconFlag;

		} catch (Exception ee) {
			String msg = String.format(
					"Error while Executing Stored Proc to get "
							+ " IncrementalDataSummary for recipientGstins :%s",
					recipientGstins);
			LOGGER.error(msg, ee);
			throw new AppException(msg);
		}
	}

	private boolean checkForPreviousRecon(Long configId, List<String> gstins) {

		try {
			List<Long> selectedConfigIds = new ArrayList<>();

			Gstr2ReconConfigEntity entity = reconConfigRepo
					.findByConfigId(configId);

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Gstr2InitiateReconMatchingAPProcessor, Invoking "
								+ "checkForPreviousRecon() configId %s,"
								+ " entity %s  :",
						configId.toString(), entity);
				LOGGER.debug(msg);
			}

			Integer fromTaxPeriod2A = entity.getFromTaxPeriod2A();
			Integer fromTaxPeriodPR = entity.getFromTaxPeriodPR();
			Integer toTaxPeriod2A = entity.getToTaxPeriod2A();
			Integer toTaxPeriodPR = entity.getToTaxPeriodPR();
			Long entityId = entity.getEntityId();

			List<Long> configIdList = reconConfigRepo.findAPConfigId(entityId,
					toTaxPeriodPR, fromTaxPeriodPR, fromTaxPeriod2A,
					toTaxPeriod2A);

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Gstr2InitiateReconMatchingAPProcessor, Invoking "
								+ "checkForPreviousRecon() configId %s,"
								+ " configIdList %s  :",
						configId.toString(), configIdList);
				LOGGER.debug(msg);
			}

			if (configIdList.isEmpty()) {

				String msg = String.format(
						"Gstr2InitiateReconMatchingAPProcessor, Invoking "
								+ "checkForPreviousRecon() configId %s,"
								+ " configIdList list is empty hence "
								+ "returning false  :",
						configId.toString());
				LOGGER.debug(msg);

				return false;
			}

			List<Gstr2ReconGstinEntity> gstinDetailsEntity = reconGstinRepo
					.findAllGstinsByConfigIdIn(configIdList);

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Gstr2InitiateReconMatchingAPProcessor, Invoking "
								+ "checkForPreviousRecon() configId %s,"
								+ " gstinDetailsEntity %s  :",
						configId.toString(), gstinDetailsEntity);
				LOGGER.debug(msg);
			}

			Map<Long, List<Gstr2ReconGstinEntity>> gstinMap = gstinDetailsEntity
					.stream()
					.collect(Collectors.groupingBy(o -> o.getConfigId()));

			for (Long conf : gstinMap.keySet()) {

				List<Gstr2ReconGstinEntity> list = gstinMap.get(conf);

				List<String> confGstin = list.stream().map(o -> o.getGstin())
						.collect(Collectors.toList());

				if (confGstin.size() == gstins.size()
						&& confGstin.containsAll(gstins)) {
					if (LOGGER.isDebugEnabled()) {
						String msg = String.format(
								"Gstr2InitiateReconMatchingAPProcessor, Invoking "
										+ "checkForPreviousRecon() confGstin %s,"
										+ " gstins %s  :",
								confGstin, gstins);
						LOGGER.debug(msg);
					}
					selectedConfigIds.add(conf);
				}

			}

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Gstr2InitiateReconMatchingAPProcessor, Invoking "
								+ "checkForPreviousRecon() "
								+ " selectedConfigIds %s  :",
						selectedConfigIds);
				LOGGER.debug(msg);
			}
			if (selectedConfigIds.isEmpty()) {
				return false;
			}

			List<String> reportList = addlReportRepo
					.getAddlnReportTypeList(configId);

			reportList.addAll(defaultReports);

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Gstr2InitiateReconMatchingAPProcessor, Invoking "
								+ "checkForPreviousRecon() "
								+ " reportList %s  :",
						reportList);
				LOGGER.debug(msg);
			}
			List<Gstr2ReconAddlReportsEntity> otherReportListsEntity = addlReportRepo
					.findReportListData(selectedConfigIds);

			Map<Long, List<Gstr2ReconAddlReportsEntity>> reportListMap = otherReportListsEntity
					.stream()
					.collect(Collectors.groupingBy(o -> o.getConfigId()));

			for (Long conf : reportListMap.keySet()) {

				List<Gstr2ReconAddlReportsEntity> list = reportListMap
						.get(conf);

				List<String> confReports = list.stream()
						.map(o -> o.getReportType())
						.collect(Collectors.toList());

				if (confReports.size() == reportList.size()
						&& confReports.containsAll(reportList)) {
					if (LOGGER.isDebugEnabled()) {
						String msg = String.format(
								"Gstr2InitiateReconMatchingAPProcessor, Invoking "
										+ "checkForPreviousRecon() "
										+ " reportList %s  confReports %s :",
								reportList, confReports);
						LOGGER.debug(msg);
					}
					return true;
				}

			}
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Gstr2InitiateReconMatchingAPProcessor, Invoking "
								+ "checkForPreviousRecon() returning false");
				LOGGER.debug(msg);
			}
			return false;
		} catch (Exception e) {
			String msg = String.format(
					"Error while invoking checkForPreviousRecon method() %d",
					configId);
			LOGGER.error(msg, e);
			throw new AppException(msg);
		}

	}

	private void makeReportListEntry(Long configId) {

		List<Gstr2ReconAddlReportsEntity> entities = defaultReports.stream()
				.map(o -> createEntry(o, configId))
				.collect(Collectors.toCollection(ArrayList::new));

		addlReportRepo.saveAll(entities);
	}

	private Gstr2ReconAddlReportsEntity createEntry(String o, Long configId) {

		Gstr2ReconAddlReportsEntity obj = new Gstr2ReconAddlReportsEntity();
		obj.setConfigId(configId);
		obj.setIsDownloadable(false);
		obj.setIsReportProcExecuted(false);
		obj.setReportType(o);
		obj.setReportTypeId(reportTypeIdMap.get(o));
		return obj;
	}
	
	private String createQueryString1(List<String> gstins, Integer fromTaxPeriodPR,
			Integer toTaxPeriodPR, Integer fromTaxPeriod2A, Integer toTaxPeriod2A,
			Long entityId) {

		String query = "select max(created_on) from ( "
				+ "select max(created_date) created_on from TBL_AUTO_2APR_ERP_ONBOARDING "
				+ "where is_active = true and entity_id =:entityId "
				+ "union all "
				+ "select max(created_date) from TBL_AUTO_2APR_ERP_ADD_PRM "
				+ "where is_active = true and entity_id =:entityId "
				+ "union all "
				+ "select max(created_on) from entity_confg_prmtr "
				+ "where question_code in ('I21','I13','I18','I38') "
				+ "and is_delete = false and is_active = true "
				+ "and entity_id =:entityId union all "
				+ "select max(createdtm) from TBL_RECON_RESP_PSD "
				+ "where ifnull(rgstinpr,rgstin2a) in (:gstins) "
				+ "and (to_number(concat(right(taxperiodpr,4), "
				+ "left(taxperiodpr,2))) between :fromTaxPeriodPR and :toTaxPeriodPR "
				+ "or to_number(concat(right(taxperiod2a,4), "
				+ "left(taxperiod2a,2))) between :fromTaxPeriod2A and :toTaxPeriod2A "
				+ "))";
		return query;

	}
	
	private String createQueryString2(Long configId) {

		String query = "select min(from_date) from TBL_RECON_REPORT_GSTIN_DETAILS "
				+ "where recon_report_config_id =:configId";
		return query;

	}
	
		/*public static void main(String[] args) {
		Timestamp ts1 = Timestamp.valueOf("2018-09-01 09:01:15");
		 Timestamp ts2 = Timestamp.valueOf("2018-09-01 09:01:16");
	
		int value = ts1.compareTo(ts2);
		System.out.println("Hi"+ value);
		}*/

	private LocalDateTime fetch2aAutoReconCompletedDate(Long entityId,
			String gstin) {
		try {

			LocalDateTime fromDate = LocalDateTime.now();

			String query = "SELECT IFNULL(MAX(GD.COMPLETED_ON),'2020-04-01') "
					+ "AS FROM_DATE FROM GSTIN_INFO G "
					+ "LEFT OUTER JOIN TBL_RECON_REPORT_GSTIN_DETAILS GD ON "
					+ "GD.GSTIN=G.GSTIN AND GD.STATUS='RECON_COMPLETED' "
					+ "LEFT OUTER JOIN TBL_RECON_REPORT_CONFIG C ON "
					+ "C.RECON_REPORT_CONFIG_ID=GD.RECON_REPORT_CONFIG_ID "
					+ "AND C.RECON_TYPE IN ('AUTO_2APR','AP_M_2APR') "
					+ "WHERE G.IS_DELETE=FALSE AND G.GSTIN =:gstin ";
				

			Query q = entityManager.createNativeQuery(query);
			q.setParameter("gstin", gstin);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("executing query to get the recon complation date"
						+ " from AUTO_2A_2B_RECON_STATUS table for given entityID"
						+ +entityId + ", gstin " + gstin);
			}
			@SuppressWarnings("unchecked")
			Object obj = q.getSingleResult();

			if (obj == null)
				return LocalDateTime.of(2020, 04, 01, 0, 0, 0); // 01-Apr-2021
			fromDate = ((Timestamp) obj).toLocalDateTime();
			return fromDate;

		} catch (Exception ee) {
			String msg = "Exception occured while fetching AP Recon completion "
					+ "status";
			LOGGER.error(msg, ee);
			throw new AppException(msg);
		}
	}
}
