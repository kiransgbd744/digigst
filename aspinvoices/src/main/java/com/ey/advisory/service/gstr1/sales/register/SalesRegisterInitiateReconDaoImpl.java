package com.ey.advisory.service.gstr1.sales.register;

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
import com.ey.advisory.app.data.repositories.client.SalesRegisterConfigRepository;
import com.ey.advisory.app.data.repositories.client.SalesRegisterDownloadReconReportsRepository;
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
@Component("SalesRegisterInitiateReconDaoImpl")
public class SalesRegisterInitiateReconDaoImpl
		implements SalesRegisterInitiateReconDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("SalesRegisterConfigRepository")
	SalesRegisterConfigRepository reconConfigRepo;

	@Autowired
	@Qualifier("SalesRegisterDownloadReconReportsRepository")
	private SalesRegisterDownloadReconReportsRepository repoDownl;

	@Autowired
	AsyncJobsService asyncJobsService;

	static final String Return_Period_Wise = "ReturnPeriodWise";
	// static final String RECON_THREE_WAY = "3 Way Recon";

	@Override
	@Transactional(value = "clientTransactionManager")
	public String createReconcileData(List<String> gstins, Long entityId,
			String fromReturnPeriod, String toReturnPeriod) {

		if (LOGGER.isDebugEnabled()) {
			String msg = "Inside SalesRegisterInitiateReconDaoImpl"
					+ ".createReconcileData() method";
			LOGGER.debug(msg);
		}

		try {
			User user = SecurityContext.getUser();
			String userName = user.getUserPrincipalName();

			Long configId = generateCustomId(entityManager);
			
			for (String gstin : gstins){

			SalesRegisterConfigEntity entity = new SalesRegisterConfigEntity();

			if ((fromReturnPeriod != null && !fromReturnPeriod.equals("")
					&& !fromReturnPeriod.isEmpty())
					&& (toReturnPeriod != null && (!toReturnPeriod.equals("")
							&& !toReturnPeriod.isEmpty()))) {
				entity.setFromTaxPeriod(Integer.parseInt(fromReturnPeriod));
				entity.setToTaxPeriod(Integer.parseInt(toReturnPeriod));

			}
			entity.setGstin(gstin);
			entity.setEntityId(entityId);
			entity.setConfigId(configId);
			entity.setInitatedOn(LocalDateTime.now());

			if (userName != null)
				entity.setInitiatedBy(userName);

			else {
				entity.setInitiatedBy("SYSTEM");
			}

			entity.setStatus(ReconStatusConstants.RECON_REQUESTED);

			SalesRegisterConfigEntity obj = reconConfigRepo.save(entity);
			}
			// saving in child table;

			reconConfigRepo.updateReconConfigStatusAndReportName(
					ReconStatusConstants.RECON_INITIATED, LocalDateTime.now(),
					configId);

//			List<SalesRegisterReconDownloadReportsEntity> addlEntityList = addReport
//					.stream()
//					.map(o -> new SalesRegisterReconDownloadReportsEntity(o,
//							obj.getConfigId(), false))
//					.collect(Collectors.toList());
//			repoDownl.saveAll(addlEntityList);

			JsonObject jsonParams = new JsonObject();
			jsonParams.addProperty("configId", configId);

			String groupCode = TenantContext.getTenantId();
			asyncJobsService.createJob(groupCode,
					JobConstants.Initiate_Recon_Sales_Register,
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
