package com.ey.advisory.app.recon3way;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.app.data.entities.client.asprecon.EWB3WayReconDownloadReportsEntity;
import com.ey.advisory.app.data.entities.client.asprecon.Recon3WayConfigEntity;
import com.ey.advisory.app.data.entities.client.asprecon.Recon3WayGstinEntity;
import com.ey.advisory.app.data.repositories.client.asprecon.EWB3WayDownloadReconReportsRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.Recon3WayConfigRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.Recon3WayGstinRepository;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.ReconStatusConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
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
@Component("EWB3WayInitiateReconDaoImpl")
public class EWB3WayInitiateReconDaoImpl implements EWB3WayInitiateReconDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("Recon3WayConfigRepository")
	Recon3WayConfigRepository reconConfigRepo;

	@Autowired
	@Qualifier("Recon3WayGstinRepository")
	Recon3WayGstinRepository reconGstinRepo;

	@Autowired
	@Qualifier("EWB3WayDownloadReconReportsRepository")
	private EWB3WayDownloadReconReportsRepository repoDownl;

	@Autowired
	AsyncJobsService asyncJobsService;

	static final String Return_Period_Wise = "ReturnPeriodWise";
	static final String RECON_THREE_WAY = "3 Way Recon";
	
	
	@Override
	@Transactional(value = "clientTransactionManager")
	public String createReconcileData(List<String> gstins, Long entityId,
			String fromReturnPeriod, String toReturnPeriod, String criteria,
			String gstr1Type, String eInvType, String gewbType,
			List<String> addReport) {

		if (LOGGER.isDebugEnabled()) {
			String msg = "Inside EWB3WayInitiateReconDaoImpl"
					+ ".createReconcileData() method";
			LOGGER.debug(msg);
		}

		try {
			User user = SecurityContext.getUser();
			String userName = user.getUserPrincipalName();

			Long configId = generateCustomId(entityManager);

			Recon3WayConfigEntity entity = new Recon3WayConfigEntity();

			// Gstr2ReconConfigEntity entity = new Gstr2ReconConfigEntity();

			if (criteria != null && !criteria.equals("")) {

				if ((fromReturnPeriod != null && !fromReturnPeriod.equals("")
						&& !fromReturnPeriod.isEmpty())
						&& (toReturnPeriod != null
								&& (!toReturnPeriod.equals("")
										&& !toReturnPeriod.isEmpty()))) {
					if (Return_Period_Wise.equalsIgnoreCase(criteria)) {
						entity.setFromRetPeriod(
								Integer.parseInt(fromReturnPeriod));
						entity.setToRetPeriod(Integer.parseInt(toReturnPeriod));
					} else {
						entity.setToDocDate(LocalDate.parse(toReturnPeriod));
						entity.setFromDocDate(
								(LocalDate.parse(fromReturnPeriod)));
					}
				}
			}

			entity.setEntityId(entityId);
			entity.setConfigId(configId);
			entity.setCreatedDate(LocalDateTime.now());
			entity.setReconType(RECON_THREE_WAY);
			entity.setRequestType(criteria);
			entity.setIsEINV(true);
			entity.setIsEWB(true);
			entity.setIsGSTR1(true);
			
			if(gstr1Type != null && !gstr1Type.equals(""))
				entity.setGstr1Type(gstr1Type);
			if(eInvType != null && !eInvType.equals(""))
				entity.setEinvType(eInvType);
			if(gewbType != null && !gewbType.equals(""))
				entity.setEwbType(gewbType);
			
				
			if (userName != null)
				entity.setCreatedBy(userName);

			else {
				entity.setCreatedBy("SYSTEM");
			}

			entity.setStatus(ReconStatusConstants.RECON_REQUESTED);
			
			
			
			Recon3WayConfigEntity obj = reconConfigRepo.save(entity);

			// saving in child table
			List<Recon3WayGstinEntity> reconGstinObjList = gstins.stream()
					.map(o -> new Recon3WayGstinEntity(o, obj.getConfigId()))
					.collect(Collectors.toList());

			reconGstinRepo.saveAll(reconGstinObjList);

			reconConfigRepo.updateReconConfigStatusAndReportName(
					ReconStatusConstants.RECON_INITIATED,
					LocalDateTime.now(),
					configId);
			
			List<EWB3WayReconDownloadReportsEntity> addlEntityList = addReport
					.stream()
					.map(o -> new EWB3WayReconDownloadReportsEntity(o,
							obj.getConfigId(), false))
					.collect(Collectors.toList());
			repoDownl.saveAll(addlEntityList);
			
			JsonObject jsonParams = new JsonObject();
			jsonParams.addProperty("configId", configId);

			String groupCode = TenantContext.getTenantId();
			asyncJobsService.createJob(groupCode,
					JobConstants.Initiate_Recon_3_Way, jsonParams.toString(),
					userName, 1L, null, null);
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
