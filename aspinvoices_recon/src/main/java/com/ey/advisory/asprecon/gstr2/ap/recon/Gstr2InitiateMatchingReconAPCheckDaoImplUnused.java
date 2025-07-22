package com.ey.advisory.asprecon.gstr2.ap.recon;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.admin.data.repositories.client.EntityConfigPrmtRepository;
import com.ey.advisory.app.data.entities.client.asprecon.Gstr2Recon2BPRAddlReportsEntity;
import com.ey.advisory.app.data.entities.client.asprecon.Gstr2ReconAddlReportsEntity;
import com.ey.advisory.app.data.entities.client.asprecon.Gstr2ReconConfigEntity;
import com.ey.advisory.app.data.entities.client.asprecon.Gstr2ReconGstinEntity;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2Recon2BPRAddlReportsRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2ReconAddlReportsRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2ReconConfigRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2ReconGstinRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2ReconGstinTaxPeriodRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.ReconStatusConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.google.gson.JsonObject;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */
@Slf4j
public class Gstr2InitiateMatchingReconAPCheckDaoImplUnused{

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
	@Qualifier("Gstr2ReconGstinTaxPeriodRepository")
	Gstr2ReconGstinTaxPeriodRepository gstinTaxPeriodRepo;
	
	@Autowired
	@Qualifier("Gstr2Recon2BPRAddlReportsRepository")
	Gstr2Recon2BPRAddlReportsRepository addl2BPRReportRepo;
	
	@Autowired
	@Qualifier("EntityConfigPrmtRepository")
	private EntityConfigPrmtRepository entityConfigPemtRepo;

	@Autowired
	AsyncJobsService asyncJobsService;
	
	private static final String DOCUMENT_DATE_WISE = "Document Date Wise";
	private static final String IMPG_Report = "Consolidated IMPG Report";
	private static final String TAX_PERIOD_WISE = "Tax Period Wise";
	
	public String createReconcileData(List<String> gstins, Long entityId,
			String toTaxPeriod2A, String fromTaxPeriod2A, String toTaxPeriodPR,
			String fromTaxPeriodPR, String toDocDate, String fromDocDate,
			List<String> addlReportsList, String reconType) {

		if (LOGGER.isDebugEnabled()) {
			String msg = "Inside Gstr2InitiateMatchingReconDaoImpl"
					+ ".createReconcileData() method";
			LOGGER.debug(msg);
		}
		List<Long> entityIds = new ArrayList<Long>();
		Gstr2ReconConfigEntity entity = new Gstr2ReconConfigEntity();

		try {
			
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
			entity.setCreatedDate(
					EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
			entity.setType(reconType);
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
			
			if (addlReportsList.contains(IMPG_Report)) {
				entity.setImpgRecon(true);
			}
			entity.setStatus(ReconStatusConstants.RECON_REQUESTED);
			Gstr2ReconConfigEntity obj = reconConfigRepo.save(entity);
			
			// saving in child-GSTIN table
			List<Gstr2ReconGstinEntity> reconGstinObjList = gstins.stream()
					.map(o -> persistReconReportGstinDetails(o, configId))
					.collect(Collectors.toList());

			reconGstinRepo.saveAll(reconGstinObjList);

			// SAVING IN TBL_RECON_RPT_DWNLD table
			if (reconType != null && reconType.equalsIgnoreCase("2APR")) {} // SAVING IN TBL_RECON_2BPR_RPT_DWNLD table
			else if (reconType != null && reconType.equalsIgnoreCase("2BPR")) {}
			
			entityIds.add(entityId);
			List<Long> optedEntities = entityConfigPemtRepo
					.getAllEntitiesOpted2B(entityIds, "I27");
			
			if(optedEntities == null || optedEntities.isEmpty()){
				
				// preparing data for child-GSTIN-TAXPERIOD table
				
				List<Integer> taxPeriods = null;

				if (entity.getFromTaxPeriodPR() != null
						&& entity.getToTaxPeriodPR() != null) {

					taxPeriods = getTaxPeriods(entity.getFromTaxPeriodPR(),
							entity.getToTaxPeriodPR());
				} else {
					LocalDate fromDocDate2 = entity.getFromDocDate();
					LocalDate toDocDate2 = entity.getToDocDate();

					Integer fromYear = fromDocDate2.getYear();
					Integer fromMonthValue = fromDocDate2.getMonthValue();
					String fromDate = fromYear.toString()
							+ fromMonthValue.toString();
					Integer fromDatePeriod = Integer.parseInt(fromDate);

					Integer toYear = toDocDate2.getYear();
					Integer toMonthValue = toDocDate2.getMonthValue();
					String toDate = toYear.toString() + toMonthValue.toString();
					Integer toDatePeriod = Integer.parseInt(toDate);

					taxPeriods = getTaxPeriods(fromDatePeriod, toDatePeriod);
				}
				
				List<Gstr2ReconGstinTaxPeriodEntity> gstinTaxPeriodList = 
						new ArrayList<>();

				for (String gstin : gstins) {

					for (Integer taxPeriod : taxPeriods) {

						Gstr2ReconGstinTaxPeriodEntity gstinTaxPeriodObj = 
								persistReconReportGstinTaxPeriodDetails(
								gstin, configId, taxPeriod);
						gstinTaxPeriodList.add(gstinTaxPeriodObj);
					}
				}

				gstinTaxPeriodRepo.saveAll(gstinTaxPeriodList);
			}

	
			reconConfigRepo.updateReconConfigStatusAndReportName(
					ReconStatusConstants.RECON_INITIATED, null,
					EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()),
					configId);

			JsonObject jsonParams = new JsonObject();
			jsonParams.addProperty("configId", configId);

			String groupCode = TenantContext.getTenantId();
			asyncJobsService.createJob(groupCode,
					JobConstants.GSTR2_RECON_INITIATE, jsonParams.toString(),
					userName, 1L, null, null);
		} catch (Exception ex) {
			String msg = String.format("Exception occured while persisting"
					+ " recon report gstin taxperiod for configId :%s ", ex);
			LOGGER.error(msg, ex);
			
			return "failure";
		}

		return "Success";
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
		String currentDate = currentYear + (currentMonth < 10
				? ("0" + currentMonth) : String.valueOf(currentMonth));
		if (nextSequencevalue != null && nextSequencevalue > 0) {
			digits = String.format("%06d", nextSequencevalue);
			reportId = currentDate.concat(digits);
		}

		return Long.valueOf(reportId);
	}
	
	private Gstr2ReconGstinEntity persistReconReportGstinDetails(String gstin, 
			Long configId) {

			try {
				Gstr2ReconGstinEntity reconGstinEntiy = new Gstr2ReconGstinEntity();
				reconGstinEntiy.setConfigId(configId);
				reconGstinEntiy.setGstin(gstin);
				reconGstinEntiy.setActive(true);
				reconGstinEntiy.setAutoReconDate(LocalDate.now());
				reconGstinEntiy.setFromDate(
						fetch2aAutoReconCompletedDate(gstin));
				reconGstinEntiy.setToDate(LocalDateTime.now());
				reconGstinEntiy.setStatus("RECON_SUBMITTED");
				reconGstinEntiy.setCretaedOn(LocalDateTime.now());
				
				return reconGstinEntiy;

			} catch (Exception ee) {
				String msg = String.format("Exception occured while persisting"
						+ " recon report gstin details for configId :%d "
						+ " and gstin :%s", configId, gstin);
				LOGGER.error(msg, ee);
				throw new AppException(msg);
			}
		}
	
	
	private LocalDateTime fetch2aAutoReconCompletedDate(String gstin) {
		try {
			LocalDateTime completedOn = null;
			LocalDateTime fromDate = LocalDateTime.now();

			String query = "SELECT MAX(CREATED_ON) FROM "
					+ " TBL_RECON_REPORT_GSTIN_DETAILS WHERE GSTIN=:gstin AND "
					+ " IS_ACTIVE=TRUE AND STATUS='RECON_COMPLETED'";

			Query q = entityManager.createNativeQuery(query);
			
			q.setParameter("gstin", gstin);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("executing query to get the recon complation date"
						+ " from AUTO_2A_2B_RECON_STATUS table for gstin " 
						+ gstin);
			}
			
			Object obj = q.getSingleResult();

			if (obj == null)
				return LocalDateTime.of(2021, 04, 01, 0, 0, 0); //01-Apr-2021
			completedOn = ((Timestamp) obj).toLocalDateTime();
			fromDate = completedOn.plusDays(1);
			return fromDate;

		} catch (Exception ee) {
			String msg = "Exception occured while fetching auto Recon "
					+ "completion status";
			LOGGER.error(msg, ee);
			throw new AppException(msg);
		}
	}
	
	private Gstr2ReconGstinTaxPeriodEntity 
					persistReconReportGstinTaxPeriodDetails(
			String gstin, Long configId, Integer taxPeriod) {
		try {

			Gstr2ReconGstinTaxPeriodEntity reconGstinTaxPeriodEntiy =
					new Gstr2ReconGstinTaxPeriodEntity();
			reconGstinTaxPeriodEntiy.setConfigId(configId);
			reconGstinTaxPeriodEntiy.setGstin(gstin);
			reconGstinTaxPeriodEntiy.setTaxPeriod(taxPeriod.toString());
			reconGstinTaxPeriodEntiy.setReturnPeriod(taxPeriod);
			reconGstinTaxPeriodEntiy.setActive(true);
			reconGstinTaxPeriodEntiy.setAutoReconDate(LocalDate.now());
			reconGstinTaxPeriodEntiy.setToDate(LocalDate.now());
			reconGstinTaxPeriodEntiy.setStatus("RECON_SUBMITTED");
			reconGstinTaxPeriodEntiy.setCretaedOn(LocalDate.now());
			reconGstinTaxPeriodEntiy.setFromDate(
					fetch2aAutoReconCompletedDateForGstinTaxPeriod(gstin,
							taxPeriod.toString()));
			
			return reconGstinTaxPeriodEntiy;

		} catch (Exception ee) {
			String msg = String.format("Exception occured while persisting"
					+ " recon report gstin-TaxPeriod details for configId :%d "
					+ " and gstin :%s", configId, gstin);
			LOGGER.error(msg, ee);
			throw new AppException(msg);
		}
		
	}

	private LocalDate fetch2aAutoReconCompletedDateForGstinTaxPeriod(String 
			gstin, String taxPeriod) {
		try {
			LocalDateTime completedOn = null;
			LocalDate fromDate = LocalDate.now();

			String query = "SELECT MAX(CREATED_ON) FROM "
					+ " TBL_RECON_GSTIN_TAXPERIOD WHERE GSTIN=:gstin AND "
					+ " TAX_PERIOD=:taxPeriod AND " 
					+ " IS_ACTIVE=TRUE AND STATUS='RECON_COMPLETED'";

			Query q = entityManager.createNativeQuery(query);
			
			q.setParameter("gstin", gstin);
			q.setParameter("taxPeriod", taxPeriod);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("executing query to get the recon complation date"
						+ " from TBL_RECON_GSTIN_TAXPERIOD table for gstin " 
						+ gstin);
			}
			
			Object obj = q.getSingleResult();

			if (obj == null){
				
			String month= taxPeriod.toString().substring(4);
			String year= taxPeriod.toString().substring(0,4);
				return LocalDate.of(Integer.parseInt(year), 
						Integer.parseInt(month), 01); //01-mm-yyyy
			}
			completedOn = ((Timestamp) obj).toLocalDateTime();
			fromDate = completedOn.toLocalDate();
			LocalDate now = LocalDate.now();
			
			int compareTo = fromDate.compareTo(now);
			
			fromDate = compareTo != 0 ? fromDate.plusDays(1) : fromDate;
			
			return fromDate;

		} catch (Exception ee) {
			String msg = "Exception occured while fetching Gstin taxperiod "
					+ "Recon completion status";
			LOGGER.error(msg, ee);
			throw new AppException(msg);
		}
	}
	
	private List<Integer> getTaxPeriods(int from, int to){
		
		List<Integer> taxPeriods = new ArrayList<>();
	
		for(int i = from; i<= to ; i++ ){
			taxPeriods.add(i);
		}
		
		return taxPeriods;
		
	}
	
}
