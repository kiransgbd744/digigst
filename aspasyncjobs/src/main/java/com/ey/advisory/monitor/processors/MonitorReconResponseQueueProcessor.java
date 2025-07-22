package com.ey.advisory.monitor.processors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.app.data.repositories.client.ImsReconStatusConfigRepository;
import com.ey.advisory.app.services.gstr1fileupload.Gstr1FileUploadJobInsertion;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.multitenancy.DefaultMultiTenantTaskProcessor;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.async.domain.master.Group;
import com.google.gson.JsonObject;

import io.jsonwebtoken.lang.Collections;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Sakshi.jain
 * 
 *         Monitor job to sequence recon response following 2APR File upload
 *         2APR ERP file upload 2BPR file upload recon result - locking ,
 *         unlocking
 *
 */
@Slf4j
@Service("MonitorReconResponseQueueProcessor")
@Transactional(value = "clientTransactionManager")
public class MonitorReconResponseQueueProcessor
		extends DefaultMultiTenantTaskProcessor {

	@Autowired
	@Qualifier("Gstr1FileUploadJobInsertion")
	private Gstr1FileUploadJobInsertion gstr1FileUploadJobInsertion;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	AsyncJobsService asyncJobsService;
	
	@Autowired
	Gstr1FileStatusRepository fileRepo;
	
	@Autowired
	@Qualifier("ImsReconStatusConfigRepository")
	ImsReconStatusConfigRepository imsReconRepo;
	
	private static List<String> autoimsStatusList = Arrays
			.asList("INITIATED", "INPROGRESS");
	
	@Override
	public void executeForGroup(Group group, Message message,
			AppExecContext ctx) {

		try {

			String groupCode = group.getGroupCode();
			if (LOGGER.isDebugEnabled()) {
				String logMsg = String.format(
						"Executing Monitoring"
								+ " MonitorReconResponseQueueProcessor"
								+ ".executeForGroup()  method for group: '%s'",
						group.getGroupCode());
				LOGGER.debug(logMsg);
			}
			
			List<Gstr1FileStatusEntity> isJobPostedEntityPresent = isJobPostedLockingPresent();
			
			if(isJobPostedEntityPresent !=null)
			{
				LOGGER.error(" JOBS are in progress , new job can not be taken. ");
				return;
			}
			
			List<Gstr1FileStatusEntity> reconLockBatchIds = isLockingPresent();
			
			String paramJson = "";
			if (reconLockBatchIds == null) {

				LOGGER.error(" NO RECON RESPONSE ARE IN QUEUE  for group {} ",
						groupCode);
				return;
			} else {

				Gstr1FileStatusEntity entity = reconLockBatchIds.get(0);
				Optional<Gstr1FileStatusEntity> fileStatusEntity = fileRepo
                        .findById(reconLockBatchIds.get(0).getId());
        if (fileStatusEntity.isPresent()) {
                LOGGER.error("fileStatusEntity {} ",
                                fileStatusEntity.get().getReq());
        }
				if (entity.getFileType() != null) {
					String filename = entity.getFileName();
					String fileId = entity.getId().toString();

					LOGGER.debug(" inside file upload block for entity {} ",
							entity);

					// String paramJson = "";
					if (entity.getFileType()
							.equalsIgnoreCase("RECON_RESPONSE")) {
						paramJson = "{\"filePath\":\""
								+ "Gstr2UserResponseUploads" + "\","
								+ "\"fileId\":\"" + fileId + "\","
								+ "\"fileName\":\"" + filename + "\"}";
						reconResponseJobPost("2APR_FILE_UPLOAD", paramJson,
								groupCode, fileId);
					} else if (entity.getFileType()
							.equalsIgnoreCase("RECON_RESPONSE_ERP")) {
						paramJson = "{\"filePath\":\""
								+ "Gstr2UserResponseUploads" + "\","
								+ "\"fileId\":\"" + fileId + "\","
								+ "\"fileName\":\"" + filename + "\"}";
						reconResponseJobPost("2APR_ERP", paramJson,
								groupCode, fileId);
					} else if (entity.getFileType()
							.equalsIgnoreCase("2BPR_RECON_RESPONSE")){

						JsonObject jsonParams = new JsonObject();
						jsonParams.addProperty("fileId", fileId);
						jsonParams.addProperty("filePath",
								"Gstr2bprUserResponseUploads");
						jsonParams.addProperty("fileName", filename);
						
						  LOGGER.debug("Value of entity id is {}", entity.getEntityId());
						  // Add null check before using entityId
					    if (entity.getEntityId() != null) {
					        jsonParams.addProperty("entityId", entity.getEntityId());
					    } else {
					        LOGGER.warn("Entity ID is null for entity: {}", entity);
					    }

						reconResponseJobPost("2BPR_FILE_UPLOAD",
								jsonParams.toString(), groupCode, fileId);
					}//2apr and 2bpr ims recon response
					else if(entity.getFileType()
							.equalsIgnoreCase("2APR_IMS_RECON_RESULT")|| entity.getFileType()
							.equalsIgnoreCase("2BPR_IMS_RECON_RESULT")) {
						
						//paramJson = entity.getReq();
						//fix for 2APR_IMS_RECON_RESULT and 2APR_IMS_RECON_RESULT
						
						if (fileStatusEntity.isPresent()) {
							LOGGER.error("fileStatusEntity {} ",
									fileStatusEntity.get().getReq());
							paramJson = fileStatusEntity.get().getReq();
								} else {
							paramJson = entity.getReq();
						}
						reconResponseJobPost("RESPONSE", paramJson.toString(),
								groupCode, entity.getId().toString());
				
						
					}
					else if(entity.getFileType()
							.equalsIgnoreCase("2APR_IMS_RECON_RESPONSE")) {
						
						JsonObject jsonParams = new JsonObject();
						jsonParams.addProperty("fileId", fileId);
						jsonParams.addProperty("filePath",
								"Gstr2aprImsUserResponseUploads");
						jsonParams.addProperty("fileName", filename);
						
						reconResponseJobPost("2APR_IMS_RECON_RESPONSE",
								jsonParams.toString(), groupCode, fileId);
						
					}else{
						JsonObject jsonParams = new JsonObject();
						jsonParams.addProperty("fileId", fileId);
						jsonParams.addProperty("filePath",
								"Gstr2bprImsUserResponseUploads");
						jsonParams.addProperty("fileName", filename);

					    LOGGER.debug("Value of entity id is {}", entity.getEntityId());

					    // Add null check before using entityId
					    if (entity.getEntityId() != null) {
					        jsonParams.addProperty("entityId", entity.getEntityId());
					    } else {
					        LOGGER.warn("Entity ID is null for entity: {}", entity);
					    }


						reconResponseJobPost("2BPR_IMS_RECON_RESPONSE",
								jsonParams.toString(), groupCode, fileId);
					}

				} else {

					LOGGER.debug(
							" inside recon result locking and unlocking block {} ",
							entity);

					if (entity.getDataType().equalsIgnoreCase("Multi Unlock")
							|| entity.getDataType().equalsIgnoreCase(
									"Multi Unlock (Bulk Response)")) {
						
						if (fileStatusEntity.isPresent()) {
							LOGGER.error("fileStatusEntity {} ",
									fileStatusEntity.get().getReq());
							paramJson = fileStatusEntity.get().getReq();
								} else {
							paramJson = entity.getReq();
						}
						LOGGER.error("paramJson {} ", paramJson);
					
						reconResponseJobPost("MULTI_UNLOCK",
								paramJson.toString(), groupCode, entity.getId().toString());
					} else {
						//bulk response and response for recon result screen
						if (fileStatusEntity.isPresent()) {
							LOGGER.error("fileStatusEntity {} ",
									fileStatusEntity.get().getReq());
							paramJson = fileStatusEntity.get().getReq();
						} else {
							paramJson = entity.getReq();
						}
					
						reconResponseJobPost("RESPONSE", paramJson.toString(),
								groupCode, entity.getId().toString());
					}

				}
			}

		} catch (Exception ex) {
			LOGGER.error(ex.getLocalizedMessage());
			throw new AppException(
					"Exception in MonitorReconResponseQueueProcessor ");

		}
	}

	public String reconResponseJobPost(String reconType, String paramJson,
			String groupCode, String fileId) {
		String userName = "SYSTEM";

		LOGGER.debug("reconType {} paramJson {} groupCode{} ", reconType,
				paramJson, groupCode);
		switch (reconType) {

		// DefaultGstr1FileUploadService class
		case "2APR_ERP":
			gstr1FileUploadJobInsertion.fileUploadJob(paramJson,
					JobConstants.GSTR2A_SFTP_RESP_UPLOAD, "SYSTEM");
			break;

		case "2BPR_FILE_UPLOAD":
			// different jobparams
			gstr1FileUploadJobInsertion.fileUploadJob(paramJson.toString(),
					JobConstants.GSTR2B_PR_USER_RESP_UPLOAD, userName);

			break;
		
		case "2BPR_IMS_RECON_RESPONSE":
			// different jobparams
			// monitor ims changes

			int imstatusCount = imsReconRepo.findByStatusIn(autoimsStatusList);
			if (imstatusCount != 0) {
				String msg = String.format("Auto IMS action based on Auto recon parameters is in progress,"
						+ " Please try after sometime.");
				LOGGER.error(msg);
				return msg;
			}
			gstr1FileUploadJobInsertion.fileUploadJob(paramJson.toString(),
					JobConstants.GSTR2B_PR_IMS_USER_RESP_UPLOAD,
					userName);

			break;
		case "2APR_FILE_UPLOAD":
			String jobCategory = JobConstants.GSTR2A_USER_RESP_UPLOAD;
			gstr1FileUploadJobInsertion.fileUploadJob(paramJson, jobCategory,
					userName);
			break;

		// Gstr2ReconResultResponseFilterController class
		case "RESPONSE":
			asyncJobsService.createJob(groupCode,
					JobConstants.GSTR2_RECON_RESPONSE, paramJson.toString(),
					userName, 1L, null, null);
			break;

		case "BULK_RESPONSE":
			asyncJobsService.createJob(groupCode,
					JobConstants.GSTR2_RECON_RESPONSE, paramJson.toString(),
					userName, 1L, null, null);
			break;
		case "MULTI_UNLOCK":
			asyncJobsService.createJob(groupCode,
					JobConstants.GSTR2_MULTI_UNLOCK, paramJson.toString(),
					userName, 1L, null, null);
			
			break;
			
		case "2APR_IMS_RECON_RESPONSE":

			int imsStatusCount = imsReconRepo.findByStatusIn(autoimsStatusList);
			if (imsStatusCount != 0) {
				String msg = String.format("Auto IMS action based on Auto recon parameters is in progress,"
						+ " Please try after sometime.");
				LOGGER.error(msg);
				return msg;
			}
			gstr1FileUploadJobInsertion.fileUploadJob(paramJson.toString(),
					JobConstants.GSTR2A_PR_IMS_USER_RESP_UPLOAD,
					userName);

			break;
			
	
		}
		
		fileRepo.updateFileStatus(Long.valueOf(fileId), "Job_Posted");
		
		return "SUCCESS";

	}

	private List<Gstr1FileStatusEntity> isLockingPresent() {

		String queryStr = createQuery();

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"query string created for recon response request status {}",
					queryStr);
		}
		Query q = entityManager.createNativeQuery(queryStr);

		@SuppressWarnings("unchecked")
		List<Object[]> resultList = q.getResultList();

		if (Collections.isEmpty(resultList))
			return null;
		else{
			
			List<Gstr1FileStatusEntity> entities = convertToEntities(resultList);
			LOGGER.debug("entities {} ",entities);
			return entities;
	}
	}

	
	private List<Gstr1FileStatusEntity> isJobPostedLockingPresent() {

		String queryStr = createQueryJobPosted();

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"query string created for job posted recon response request status {}",
					queryStr);
		}
		Query q = entityManager.createNativeQuery(queryStr);

		@SuppressWarnings("unchecked")
		List<Object[]> resultList = q.getResultList();

		if (Collections.isEmpty(resultList))
			return null;
		else{
			
			List<Gstr1FileStatusEntity> entities = convertToEntities(resultList);
			LOGGER.debug("entities {} ",entities);
			return entities;
	}
	}
	

	private String createQuery() {

		return "SELECT top 1 e.ID,e.FILE_NAME,e.FILE_TYPE,e.source,e.ENTITY_ID,e.data_type, e.REQ_PAYLOAD from FILE_STATUS e WHERE "
				+ " (e.DATA_TYPE IN ('Multi Unlock','Multi Unlock (Bulk Response)','Force Match','Unlock (Bulk Response)','Force Match (Bulk Response)','3B Response','3B Response (Bulk Response)','Unlock','IMS Action (Accept/Reject/Pending)') "
				+ " OR e.file_type in ('2BPR_RECON_RESPONSE','RECON_RESPONSE_ERP','RECON_RESPONSE','2BPR_IMS_RECON_RESPONSE', '2APR_IMS_RECON_RESPONSE','2APR_IMS_RECON_RESULT','2BPR_IMS_RECON_RESULT')"
				+ ") AND FILE_STATUS in ('Uploaded') "
				+ " ORDER BY ID ASC";
	}
	
	private String createQueryJobPosted() {

		return "SELECT top 1 e.ID,e.FILE_NAME,e.FILE_TYPE,e.source,e.ENTITY_ID, "
				+ " e.data_type, e.REQ_PAYLOAD from FILE_STATUS e WHERE "
				+ " (e.DATA_TYPE IN ('Multi Unlock','Multi Unlock (Bulk Response)', "
				+ " 'Force Match','Unlock (Bulk Response)',"
				+ " 'Force Match (Bulk Response)','3B Response','3B Response (Bulk Response)','Unlock','IMS Action (Accept/Reject/Pending)') "
				+ " OR e.file_type in ('2BPR_RECON_RESPONSE', "
				+ " 'RECON_RESPONSE_ERP','RECON_RESPONSE','2BPR_IMS_RECON_RESPONSE', '2APR_IMS_RECON_RESPONSE','2APR_IMS_RECON_RESULT','2BPR_IMS_RECON_RESULT')"
				+ ") AND FILE_STATUS in ('Job_Posted', 'InProgress') "
				+ " ORDER BY ID ASC";
	}

	// 2BPR_RECON_RESPONSE,RECON_RESPONSE_ERP,RECON_RESPONSE

	private List<Gstr1FileStatusEntity> convertToEntities(
			List<Object[]> dataList) {
		
		List<Gstr1FileStatusEntity> entities = new ArrayList<>();
		try{
		LOGGER.debug("inside convert method ");
	
		for (Object[] data : dataList) {
			Gstr1FileStatusEntity entity = new Gstr1FileStatusEntity();
			entity.setId(Long.valueOf(data[0].toString()));
			entity.setFileName(data[1] != null ? data[1].toString() : null);
			entity.setFileType(data[2] != null ? data[2].toString() : null);
			entity.setSource(data[3] != null ? data[3].toString() : null);
			entity.setEntityId(
					data[4] != null ? Long.valueOf(data[4].toString()) : null);
			entity.setDataType(data[5] != null ? data[5].toString() : null);
			entity.setReq(data[6] != null
					? data[6].toString() : null);
			entities.add(entity);
		}
		return entities;
		}catch(Exception ex)
		{
			LOGGER.error(ex.getLocalizedMessage());
			throw new AppException(ex);
		}
	}
}
