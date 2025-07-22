/**
 * 
 */
package com.ey.advisory.monitor.processors;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.admin.data.repositories.client.EntityConfigPrmtRepository;
import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.app.data.entities.client.ImsReconConfigEntity;
import com.ey.advisory.app.data.entities.client.asprecon.Gstr2ReconConfigEntity;
import com.ey.advisory.app.data.repositories.client.EntityInfoRepository;
import com.ey.advisory.app.data.repositories.client.ImsReconStatusConfigRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2ReconConfigRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.multitenancy.DefaultMultiTenantTaskProcessor;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.domain.master.Group;
import com.google.common.collect.ImmutableList;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.StoredProcedureQuery;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Shashikant.Shukla
 *
 */
@Slf4j
@Service("MonitorAutoImsReconProcessor")
public class MonitorAutoImsReconProcessor
		extends DefaultMultiTenantTaskProcessor {

	@Autowired
	@Qualifier("ImsReconStatusConfigRepository")
	ImsReconStatusConfigRepository imsStatusRepo;

	@Autowired
	@Qualifier("EntityConfigPrmtRepository")
	private EntityConfigPrmtRepository onboardingRepo;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("Gstr2ReconConfigRepository")
	Gstr2ReconConfigRepository reconConfigRepo;

	@Autowired
	@Qualifier("entityInfoRepository")
	private EntityInfoRepository entityInfoRepo;
	
	@Autowired
	Gstr1FileStatusRepository fileRepo;

	private static final String AP_M_2APR = "AP_M_2APR";
	private static final String AUTO_2APR = "AUTO_2APR";

	private static List<String> type = Arrays.asList(AP_M_2APR, AUTO_2APR);

	private static List<String> statusList = Arrays.asList("RECON_INPROGRESS",
			"RECON_IN_QUEUE", "RECON_HALT", "INITIATED", "INPROGRESS");

	final List<String> fileStatusList = ImmutableList.of("Uploaded",
			"InProgress","Job_Posted");
	
	final List<String> fileTypeList = ImmutableList.of("IMS",
			"2BPR_IMS_RECON_RESPONSE");
	
	@Override
	public void executeForGroup(Group group, Message message,
			AppExecContext ctx) {

		Long batchId = null;
		List<Long> batchIds = new ArrayList<>();
		List<ImsReconConfigEntity> entityList = new ArrayList<>();
		try {

			if (LOGGER.isDebugEnabled()) {

				LOGGER.debug(
						"inside MonitorAutoImsReconProcessor "
								+ "group code {} : ",
						TenantContext.getTenantId());
			}

			List<Gstr2ReconConfigEntity> reconEntity = reconConfigRepo
					.findByTypeIn(type);

			Long count = reconEntity.stream()
					.filter(o -> statusList.contains(o.getStatus())).count();

			if (LOGGER.isDebugEnabled()) {

				LOGGER.debug(
						"inside MonitorAutoImsReconProcessor "
								+ "group code {}  and count {}: ",
						TenantContext.getTenantId(), count);
			}

			if (count != 0) {

				LOGGER.error(
						"inside MonitorAutoImsReconProcessor "
								+ "group code {}  and count {} Not eligible due to recon in progress "
								+ "for Auto Lock hence returing: ",
						TenantContext.getTenantId(), count);
				return;
			}
			List<Gstr1FileStatusEntity> typeFileList = fileRepo.getFileBasedonFileType(fileTypeList);
			List<Gstr1FileStatusEntity> sourceFileList = fileRepo.getFileBasedonSource("IMS_UI");
			
			if ((typeFileList.size() > 0) || (sourceFileList.size() > 0 )) {

				LOGGER.error(
						"inside MonitorAutoImsReconProcessor "
								+ "group code {}  and ims file upload count {}  and IMS_UI count {} "
								+ "Not eligible due to ims file upload "
								+ "for Auto Lock hence returing: ",
						TenantContext.getTenantId(), typeFileList.size(),sourceFileList.size() > 0);
				return;
			}
			
			List<Long> entityIds = getAllAPEnabledEntity();

			for (Long entityId : entityIds) {
				LOGGER.error(
						"inside MonitorAutoImsReconProcessor "
								+ "group code {}  entityId {}  ",
						TenantContext.getTenantId(), entityId);

				batchId = generateCustomId(entityManager);// revisit
				ImsReconConfigEntity dto = new ImsReconConfigEntity();

				dto.setBatchId(batchId);
				LocalDate currDate = LocalDate.now();
		        int month = currDate.getMonthValue();
		        int year = currDate.getYear();
		        String currentTaxPeriod = String.format("%02d%d", month, year);
				dto.setTaxPeriod(currentTaxPeriod);
				dto.setCreatedDate(LocalDateTime.now());
				dto.setEntityId(entityId);
				dto.setStatus("INITIATED");
				entityList.add(dto);
				batchIds.add(batchId);
			}

			imsStatusRepo.saveAll(entityList);

			for (ImsReconConfigEntity entity : entityList) {

				imsStatusRepo.updateStatusAndResp(entity.getBatchId(),
						"INPROGRESS");
				procCall(entity.getBatchId(), entity.getEntityId(),entity.getTaxPeriod());

			}

		} catch (Exception e) {

			String msg = String.format(
					"MonitorAutoImsReconProcessor"
							+ " - error occured group code  {}, ",
					TenantContext.getTenantId());
			LOGGER.error(msg, e);
			imsStatusRepo.updateStatus(batchIds, "FAILED");
			throw new AppException(e);

		}
	}

	private void procCall(Long batchId, Long entityId, String taxPeriod) {
		try {

			StoredProcedureQuery storedProc = entityManager
					.createStoredProcedureQuery("USP_AIM_ONB_IMS_UPDATE");

			storedProc.registerStoredProcedureParameter("IMS_CONFIG_ID",
					Long.class, ParameterMode.IN);

			storedProc.registerStoredProcedureParameter("ENTITY_ID", Long.class,
					ParameterMode.IN);

			storedProc.registerStoredProcedureParameter("TAX_PERIOD", String.class,
					ParameterMode.IN);
			

			storedProc.setParameter("IMS_CONFIG_ID", batchId);
			storedProc.setParameter("ENTITY_ID", entityId);
			storedProc.setParameter("TAX_PERIOD", taxPeriod);

			String loadingProcResp = (String) storedProc.getSingleResult();

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Executed Stored proc USP_AIM_ONB_IMS_UPDATE and "
								+ "got resp : %s",
						loadingProcResp);
				LOGGER.debug(msg);
			}

			if (!loadingProcResp.equalsIgnoreCase("SUCCESS")) {

				String msg = String
						.format("USP_AIM_ONB_IMS_UPDATE did not return "
								+ "success hence returing : EntityId %d ,"
								+ " batchId %d", entityId, batchId);
				LOGGER.error(msg);

				imsStatusRepo.updateStatusAndResp(batchId, "FAILED");
				return;
			} else {
				imsStatusRepo.updateStatusAndResp(batchId,"SUCCESS");
			}

		} catch (Exception e) {

			String msg = String.format(
					"Exeption occured while Invoking "
							+ "Proc Call : EntityId %d , batchId %d",
					entityId, batchId);
			LOGGER.error(msg);

			imsStatusRepo.updateStatusAndResp(batchId, "FAILED");
		}
	}

	private static Long getNextSequencevalue(EntityManager entityManager) {

		String queryStr = "SELECT AIM_ONB_IMS_UPDATE_SEQ.nextval FROM DUMMY";

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

	private List<Long> getAllAPEnabledEntity() {

		List<Long> entityIds = entityInfoRepo.findActiveEntityIds();

		List<Long> optedEntities = onboardingRepo
				.getAllEntitiesOptedAIM(entityIds);

		return optedEntities;
	}

}
