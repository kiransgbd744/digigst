package com.ey.advisory.app.reconewbvsitc04;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.app.data.repositories.client.asprecon.EwbvsItc04ConfigRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.EwbvsItco4DownloadReconReportsRepository;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.ReconStatusConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
//import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.google.gson.JsonObject;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Ravindra V S
 *
 */
@Slf4j
@Component("EwbVsItc04InitiateReconDaoImpl")
public class EwbVsItc04InitiateReconDaoImpl
		implements EwbVsItc04InitiateReconDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("EwbvsItc04ConfigRepository")
	EwbvsItc04ConfigRepository reconConfigRepo;

	@Autowired
	@Qualifier("EwbvsItco4DownloadReconReportsRepository")
	private EwbvsItco4DownloadReconReportsRepository repoDownl;

	@Autowired
	AsyncJobsService asyncJobsService;

	static final String GSTIN_SUBMITTED_DATA = "GSTIN Submitted Data";
	static final String DIGIGST_PROCESSED_DATA = "DigiGST Processed Data";

	@Override
	@Transactional(value = "clientTransactionManager")
	public String createReconcileData(List<String> gstins, Long entityId,
			String fromTaxPeriod, String toTaxPeriod, String fy,
			String criteria, List<String> addReport) {

		
		Integer fromTaxPeriodEwb = null;
		Integer toTaxPeriodEwb = null;
		if (LOGGER.isDebugEnabled()) {
			String msg = "Inside EwbVsItc04InitiateReconDaoImpl"
					+ ".createReconcileData() method";
			LOGGER.debug(msg);
		}

		try {
			User user = SecurityContext.getUser();
			String userName = user.getUserPrincipalName();
			Long configId = generateCustomId(entityManager);
			for (String gstin : gstins) {

				EwbVsItc04ConfigEntity entity = new EwbVsItc04ConfigEntity();

				if (criteria != null && !criteria.equals("")) {

					if (fromTaxPeriod != null && !fromTaxPeriod.equals("")
							&& toTaxPeriod != null && !toTaxPeriod.equals("")) {
						if (GSTIN_SUBMITTED_DATA.equalsIgnoreCase(criteria)) {
							entity.setCriteria(GSTIN_SUBMITTED_DATA);
							entity.setItcFromTaxPeriod(GenUtil.getDerivedTaxPeriod(fromTaxPeriod));
							entity.setItcToTaxPeriod(GenUtil.getDerivedTaxPeriod(toTaxPeriod));
						} else if (DIGIGST_PROCESSED_DATA
								.equalsIgnoreCase(criteria)) {
							entity.setCriteria(DIGIGST_PROCESSED_DATA);
							entity.setItcFromTaxPeriod(GenUtil.getDerivedTaxPeriod(fromTaxPeriod));
							entity.setItcToTaxPeriod(GenUtil.getDerivedTaxPeriod(toTaxPeriod));
						}
					}
				}

				entity.setEntityId(entityId);
				entity.setConfigId(configId);
				entity.setFy(fy);
				entity.setGstin(gstin);
				String fy1 = fy.substring(0, 4);
				String fy2 = fy.substring(5, 9);

				if (fromTaxPeriod.contains("17")) {
					fromTaxPeriodEwb = Integer.parseInt(fy1 + "04");
				} else if (fromTaxPeriod.contains("18")) {
					fromTaxPeriodEwb = Integer.parseInt(fy1 + "10");
				} else if (fromTaxPeriod.contains("13")) {
					fromTaxPeriodEwb = Integer.parseInt(fy1 + "04");
				} else if (fromTaxPeriod.contains("14")) {
					fromTaxPeriodEwb = Integer.parseInt(fy1 + "07");
				} else if (fromTaxPeriod.contains("15")) {
					fromTaxPeriodEwb = Integer.parseInt(fy1 + "10");
				} else if (fromTaxPeriod.contains("16")) {
					fromTaxPeriodEwb = Integer.parseInt(fy2 + "01");
				}

				if (toTaxPeriod.contains("17")) {
					toTaxPeriodEwb = Integer.parseInt(fy1 + "09");
				} else if (toTaxPeriod.contains("18")) {
					toTaxPeriodEwb = Integer.parseInt(fy2 + "03");
				} else if (toTaxPeriod.contains("13")) {
					toTaxPeriodEwb = Integer.parseInt(fy1 + "06");
				} else if (toTaxPeriod.contains("14")) {
					toTaxPeriodEwb = Integer.parseInt(fy1 + "09");
				} else if (toTaxPeriod.contains("15")) {
					toTaxPeriodEwb = Integer.parseInt(fy1 + "12");
				} else if (toTaxPeriod.contains("16")) {
					toTaxPeriodEwb = Integer.parseInt(fy2 + "03");
				}

				entity.setEwbFromTaxPeriod(fromTaxPeriodEwb);
				entity.setEwbToTaxPeriod(toTaxPeriodEwb);

				entity.setInitiatedOn(LocalDateTime.now());

				if (userName != null)
					entity.setInitiatedBy(userName);

				else {
					entity.setInitiatedBy("SYSTEM");
				}

				entity.setStatus(ReconStatusConstants.RECON_REQUESTED);

				reconConfigRepo.save(entity);
			}

			reconConfigRepo.updateReconConfigStatusAndReportName(
					ReconStatusConstants.RECON_INITIATED, LocalDateTime.now(),
					configId);

			List<EwbvsItc04ReconDownloadReportsEntity> addlEntityList = addReport
					.stream()
					.map(o -> new EwbvsItc04ReconDownloadReportsEntity(o,
							configId, false))
					.collect(Collectors.toList());
			repoDownl.saveAll(addlEntityList);

			JsonObject jsonParams = new JsonObject();
			jsonParams.addProperty("configId", configId);

			String groupCode = TenantContext.getTenantId();

			asyncJobsService.createJob(groupCode,
					JobConstants.INITIATE_RECON_EWBVSITC04,
					jsonParams.toString(), userName, 1L, null, null);

		} catch (Exception ex) {
			LOGGER.debug("Exception Occured while data in the config table {}", ex);
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

}
