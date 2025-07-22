package com.ey.advisory.app.anx2.initiaterecon;

import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.app.data.entities.client.asprecon.ReconConfigEntity;
import com.ey.advisory.app.data.entities.client.asprecon.ReconGstinEntity;
import com.ey.advisory.app.data.repositories.client.asprecon.InformationReportMasterRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.ReconConfigRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.ReconGstinRepository;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.ReconStatusConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.google.gson.JsonObject;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Arun.KA
 *
 */
@Slf4j
@Component("InitiateReconcileDaoImpl")
public class InitiateReconcileDaoImpl implements InitiateReconcileDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("ReconConfigRepository")
	ReconConfigRepository reconConfigRepo;

	@Autowired
	@Qualifier("ReconGstinRepository")
	ReconGstinRepository reconGstinRepo;

	@Autowired
	AsyncJobsService asyncJobsService;
	
	@Autowired
	@Qualifier("InformationReportMasterRepository")
	private InformationReportMasterRepository infoReportRepo;

	@Override
	public String createReconcileData(List<String> gstins,
			List<String> infoReports, String taxPeriod, Long entityId) {

		if (LOGGER.isDebugEnabled()) {
			String msg = "Inside InitiateReconcileDaoImpl.createReconcileData()"
					+ " method";
			LOGGER.debug(msg);
		}
		
		String infoReportIdAsString = null;
		
		if(infoReports != null && infoReports.size() > 0){
		List<Integer> infoReportId = infoReportRepo.findByName(infoReports);
		infoReportIdAsString = infoReportId
				.stream().map(String::valueOf).collect(Collectors.joining(","));
		}
		
		try {
			User user = SecurityContext.getUser();
			String userName = user.getUserPrincipalName();

			Date date = new Date();
			Long configId = generateCustomId(entityManager);

			ReconConfigEntity entity = new ReconConfigEntity();
			entity.setEntityId(entityId);
			entity.setConfigId(configId);
			entity.setCreatedDate(date);
			//entity.setCreatedBy("SYSTEM");
			entity.setCreatedBy(userName);
			entity.setTaxPeriod(taxPeriod);
			entity.setStatus(ReconStatusConstants.RECON_REQUESTED);
			
			if(infoReportIdAsString != null && !infoReportIdAsString.isEmpty())
			entity.setInfoReportId(infoReportIdAsString);
			else{
				entity.setInfoReportId("0");
			}
			ReconConfigEntity obj = reconConfigRepo.save(entity);

			//saving in child table
			List<ReconGstinEntity> reconGstinObjList = gstins.stream()
					.map(o -> new ReconGstinEntity(o, obj.getConfigId()))
					.collect(Collectors.toList());

			reconGstinRepo.saveAll(reconGstinObjList);

			reconConfigRepo.updateReconConfigStatusAndReportName(
					ReconStatusConstants.RECON_INITIATED, null, new Date(),
					configId);


			JsonObject jsonParams = new JsonObject();
			jsonParams.addProperty("configId", configId);

			String groupCode = TenantContext.getTenantId();
			asyncJobsService.createJob(groupCode,
					JobConstants.ANX2_RECON_INITIATE, jsonParams.toString(),
					userName, 1L, null, null);
		} catch (Exception ex) {
			return "failure";
		}

		return "Success";
	}

	private static Long getNextSequencevalue(EntityManager entityManager) {

		String queryStr = "SELECT RECON_REPORT_SEQ.nextval "
				+ "FROM DUMMY";

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
