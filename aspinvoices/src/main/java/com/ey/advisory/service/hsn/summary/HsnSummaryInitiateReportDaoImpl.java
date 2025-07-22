package com.ey.advisory.service.hsn.summary;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.app.data.repositories.client.HsnSummaryConfigRepository;
import com.ey.advisory.app.data.repositories.client.HsnSummaryGstinDetailRepository;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.ReconStatusConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.google.gson.JsonObject;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Shashikant.Shukla
 *
 */
@Slf4j
@Component("HsnSummaryInitiateReportDaoImpl")
public class HsnSummaryInitiateReportDaoImpl
		implements HsnSummaryInitiateReportDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("HsnSummaryGstinDetailRepository")
	HsnSummaryGstinDetailRepository reconGstinDetailRepo;

	@Autowired
	@Qualifier("HsnSummaryConfigRepository")
	HsnSummaryConfigRepository reconConfigRepo;

	@Autowired
	AsyncJobsService asyncJobsService;

	static final String Return_Period_Wise = "ReturnPeriodWise";

	@Override
	public String createReconcileData(List<String> gstins, Long entityId,
			String fromReturnPeriod, String toReturnPeriod, Long id) {

		if (LOGGER.isDebugEnabled()) {
			String msg = "Inside HsnSummaryInitiateReportDaoImpl"
					+ ".createReconcileData() method";
			LOGGER.debug(msg);
		}

		try {
			User user = SecurityContext.getUser();
			String userName = user.getUserPrincipalName();

			Long configId = id;

			HsnSummaryConfigEntity entity = new HsnSummaryConfigEntity();

			if ((fromReturnPeriod != null && !fromReturnPeriod.equals("")
					&& !fromReturnPeriod.isEmpty())
					&& (toReturnPeriod != null && (!toReturnPeriod.equals("")
							&& !toReturnPeriod.isEmpty()))) {
				entity.setFromTaxPeriod(Integer.parseInt(fromReturnPeriod));
				entity.setToTaxPeriod(Integer.parseInt(toReturnPeriod));

			}
			// entity.setGstin(gstin);
//			entity.setEntityId(entityId);
			entity.setConfigId(configId);
			entity.setInitatedOn(LocalDateTime.now());

			if (userName != null)
				entity.setInitiatedBy(userName);

			else {
				entity.setInitiatedBy("SYSTEM");
			}
			entity.setIsDelete(false);
			entity.setStatus(ReconStatusConstants.RECON_REQUESTED);
			reconConfigRepo.save(entity);

			for (String gstin : gstins) {

				HsnSummaryGstinDetailsEntity configEntity = new HsnSummaryGstinDetailsEntity();

				configEntity.setGstin(gstin);
				configEntity.setConfigId(configId);
				configEntity.setIsActive(true);
				configEntity.setStatus(ReconStatusConstants.RECON_REQUESTED);

				HsnSummaryGstinDetailsEntity obj = reconGstinDetailRepo
						.save(configEntity);
			}
			// saving in child table;

			reconConfigRepo.updateReconConfigStatusAndReportName(
					ReconStatusConstants.RECON_INITIATED, LocalDateTime.now(),
					configId);

			JsonObject jsonParams = new JsonObject();
			jsonParams.addProperty("configId", configId);

			String groupCode = TenantContext.getTenantId();
			asyncJobsService.createJob(groupCode,
					JobConstants.Initiate_Recon_Hsn_Summary,
					jsonParams.toString(), userName, 1L, null, null);
		} catch (Exception ex) {
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
