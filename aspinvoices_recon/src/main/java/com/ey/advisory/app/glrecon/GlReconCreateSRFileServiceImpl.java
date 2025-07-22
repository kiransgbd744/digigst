package com.ey.advisory.app.glrecon;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.GlReconFileStatusEntity;
import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.admin.data.repositories.client.GlReconFileStatusRepository;
import com.ey.advisory.app.data.entities.client.asprecon.GlReconGstinEntity;
import com.ey.advisory.app.data.entities.client.asprecon.GlReconReportConfigEntity;
import com.ey.advisory.app.data.repositories.client.asprecon.GLReconReportConfigRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.GlReconGstinRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.ReconStatusConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Sakshi.jain
 *
 */
/*
 * 
 */
@Component("GlReconCreateSRFileServiceImpl")
@Slf4j
public class GlReconCreateSRFileServiceImpl
		implements GlReconSRFileCreationService {

	@Autowired
	@Qualifier("GlReconFileStatusRepository")
	private GlReconFileStatusRepository GlReconFileStatusRepository;

	@Autowired
	@Qualifier("GLReconReportConfigRepository")
	private GLReconReportConfigRepository glReconReportConfig;

	@Autowired
	@Qualifier("GlReconGstinRepository")
	private GlReconGstinRepository glReconGstinConfig;

	@Autowired
	AsyncJobsService asyncJobsService;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;
	
	@Autowired
	private CommonUtility commonUtility;

	private List<String> file_Uploads_Data_Type = ImmutableList.of(
			"Business_Unit_code", "Document_type", "Supply_Type",
			"GL_Code_Mapping_Master_GL", "Tax_code", "GL_dump_mapping_file",
			"GL Dump");

	@Override
	public String createSRFile(Long entityId, List<String> gstins,
			String fromTaxPerd, String toTaxPerd ,String transactionType) {

		try {
			User user = SecurityContext.getUser();
			String userName = user.getUserPrincipalName()!=null?user.getUserPrincipalName():null;
			LOGGER.debug("Retrieved userName: {}", userName);
			
			/*List<GlReconFileStatusEntity> filesUpload = GlReconFileStatusRepository
					.findByIsActiveTrue();*/
			
			/*
			 * LOGGER.debug("Fetching answer for entityId: {} with question: {}"
			 * , entityId, "Multiple User Access to Async Reports");
			 * 
			 * String ansfromques = commonUtility.getAnsFromQue(entityId,
			 * "Multiple User Access to Async Reports");
			 * 
			 * LOGGER.debug("Answer retrieved: {}", ansfromques);
			 * 
			 * List<GlReconFileStatusEntity> filesUpload=null;
			 * 
			 * if (ansfromques.equalsIgnoreCase("A")) { filesUpload =
			 * GlReconFileStatusRepository
			 * .findActiveFileTypesAndGLDumpByCreatedBy(userName);
			 * 
			 * } else if (ansfromques.equalsIgnoreCase("B")) { filesUpload =
			 * GlReconFileStatusRepository.findActiveFileTypesAndLatestGLDump();
			 * } LOGGER.debug(" MASTER FILES ACTIVE - {} ",filesUpload);
			 * 
			 * if (filesUpload.isEmpty()) { throw new AppException(
			 * "Master Files are not Uploaded. Hence Recon can not be initiated. "
			 * ); } else if (filesUpload.size() !=7 ) { List<String> rptTypes =
			 * filesUpload.stream() .map(o -> o.getFileType())
			 * .collect(Collectors.toCollection(ArrayList::new)); List<String>
			 * rptTypesMutableList = file_Uploads_Data_Type;
			 * 
			 * throw new AppException("Master Files" +
			 * rptTypesMutableList.removeAll(rptTypes) +
			 * "are not Uploaded. Hence Recon can not be initiated.");
			 * 
			 * }
			 * 
			 * List<String> masterFileIds = filesUpload.stream() .map(o ->
			 * String.valueOf(o.getId()))
			 * .collect(Collectors.toCollection(ArrayList::new));
			 */

			Long configId = generateCustomId(entityManager);

			GlReconReportConfigEntity entity = new GlReconReportConfigEntity();

			entity.setEntityId(entityId);
			entity.setConfigId(configId);
			
			 entity.setCreatedDate(LocalDateTime.now());
			 entity.setType("GL Recon");

			if (userName != null)
				entity.setCreatedBy(userName);

			else {
				entity.setCreatedBy("SYSTEM");
			}

			if ((fromTaxPerd != null && fromTaxPerd != ""
					&& !fromTaxPerd.isEmpty())
					&& (toTaxPerd != null && toTaxPerd != ""
							&& !toTaxPerd.isEmpty())) {
				entity.setToTaxPeriod(toTaxPerd);
				entity.setFromTaxPeriod(fromTaxPerd);
			}
			//entity.setMasterFileIds(String.join(",", masterFileIds));
			entity.setStatus(ReconStatusConstants.RECON_REQUESTED);

			GlReconReportConfigEntity obj = glReconReportConfig.save(entity);

			// saving in child table
			List<GlReconGstinEntity> reconGstinObjList = gstins.stream()
					.map(o -> new GlReconGstinEntity(o, obj.getConfigId()))
					.collect(Collectors.toList());

			glReconGstinConfig.saveAll(reconGstinObjList);

			JsonObject jsonParams = new JsonObject();

			jsonParams.addProperty("reconId", configId);
			
			jsonParams.addProperty("transactionType", transactionType);

			String groupCode = TenantContext.getTenantId();
			asyncJobsService.createJob(groupCode,
					JobConstants.GL_INITIATE_RECON, jsonParams.toString(),
					userName, 1L, null, null);

			return String.valueOf(configId);
			
		} catch (Exception ex) {
			String msg = "Error occured in GlReconCreateSRFileServiceImpl";
			LOGGER.error(msg, ex);
			throw new AppException(ex);
		}
	}

	private static Long getNextSequencevalue(EntityManager entityManager) {

		String queryStr = "SELECT GL_RECON_REPORT_SEQ.nextval " + "FROM DUMMY";

		Query query = entityManager.createNativeQuery(queryStr);

		Long seqId = ((BigInteger) query.getSingleResult()).longValue();

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