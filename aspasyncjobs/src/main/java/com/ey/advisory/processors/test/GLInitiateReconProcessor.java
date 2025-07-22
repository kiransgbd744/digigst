package com.ey.advisory.processors.test;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.asprecon.GlReconReportConfigEntity;
import com.ey.advisory.app.glrecon.GLReconAPICallServiceImpl;
import com.ey.advisory.app.glrecon.GLReconCreateMasterDataServiceImpl;
import com.ey.advisory.app.glrecon.PRFileCreationDao;
import com.ey.advisory.app.glrecon.SFTPPushFileDao;
import com.ey.advisory.app.glrecon.SRFileCreationDao;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.google.common.base.Strings;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/*
 * @author Sakshi.jain
 *	
 *	GL initiate recon processor 
 */
@Slf4j
@Component("GLInitiateReconProcessor")
public class GLInitiateReconProcessor implements TaskProcessor {

	@Autowired
	@Qualifier("SRFileCreationDaoImpl")
	private SRFileCreationDao srFileCreationService;
	
	@Autowired
	@Qualifier("PRFileCreationDaoImpl")
	private PRFileCreationDao prFileCreationService;

	@Autowired
	@Qualifier("SFTPPushFileDaoImpl")
	private SFTPPushFileDao sftpPushFileService;

	@Autowired
	private GLReconAPICallServiceImpl apiCallService;

	@Autowired
	@Qualifier("GLReconReportConfigRepository")
	private com.ey.advisory.app.data.repositories.client.asprecon.GLReconReportConfigRepository glReconReportConfig;

	@Autowired
	@Qualifier("GLReconCreateMasterDataServiceImpl")
	private GLReconCreateMasterDataServiceImpl glReconCreateMasterDataServiceImpl;

	@Override
	public void execute(Message message, AppExecContext context) {

		LOGGER.debug("Begin GLInitiateReconProcessor job");

		String groupCode = message.getGroupCode();
		String jsonString = message.getParamsJson();

		JsonParser parser = new JsonParser();
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"GLInitiateReconProcessor Upload Job"
							+ " executing for groupcode %s and params %s",
					groupCode, jsonString);
			LOGGER.debug(msg);
		}
		JsonObject json = (JsonObject) parser.parse(jsonString);
		Long configId = json.get("reconId").getAsLong();
		String transactiontype = json.get("transactionType").getAsString();
		try {

			// TO-Do check to not generate report again in reruning the
			// processor

			glReconReportConfig.updateReconConfigStatusAndReportName(
					"RECON_INPROGRESS", null, null,LocalDateTime.now(), configId);

			Optional<GlReconReportConfigEntity> entity = glReconReportConfig
					.findById(configId);
			Long entityId = entity.get().getEntityId();
			String resp = null;
			if (entity.isPresent()) {
				//i pr or o sr
				if("O".equalsIgnoreCase(transactiontype)) {
					if (entity.get().getSrFilePath() == null) {
						resp = srFileCreationService.generateReports(configId);

						if (LOGGER.isDebugEnabled()) {
							String msg = String.format(
									"GLInitiateReconProcessor SR file generated" 
											+ " for groupcode %s and configId %s",
									groupCode, configId.toString());
							LOGGER.debug(msg);
						}
					}
				}
				else {
					if ("I".equalsIgnoreCase(transactiontype)) {
						resp = prFileCreationService.generateReports(configId);

						if (LOGGER.isDebugEnabled()) {
							String msg = String.format(
									"GLInitiateReconProcessor PR file generated" 
											+ " for groupcode %s and configId %s",
									groupCode, configId.toString());
							LOGGER.debug(msg);
						}
					}
				
					
				}
				
			
			}
			//processer which query out the active records and create 7 master file
			//GL_RECON_REPORT_CONFIG check no data found then not execute
			glReconCreateMasterDataServiceImpl.create(configId);
			
			if(!Strings.isNullOrEmpty(resp))
			{
			sftpPushFileService.sftpFilePushService(configId,entityId);

			// calling azure api
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"GLInitiateReconProcessor sftp push files completed"
								+ " for groupcode %s and configId %s",
						groupCode, configId.toString());
				LOGGER.debug(msg);
			}

			String apiResp = apiCallService.glReconSendReqId(configId);

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format("api response we receiving : %s",
						apiResp);
				LOGGER.debug(msg);
			}

			if (!apiResp.isEmpty()) {
				JsonObject respObject = (new JsonParser()).parse(apiResp)
						.getAsJsonObject();
				JsonObject hdrObject = respObject.getAsJsonObject("hdr");

				String status = hdrObject.get("status").getAsString();
				if ("S".equals(status)) {
					glReconReportConfig.updateReconConfigStatusAndReportName(
							"RECON_INITIATED", null,null, LocalDateTime.now(),
							configId);
				} else {
					String msg = String.format(
							"apiresp status is not success %s ", apiResp);
					LOGGER.error(msg);
					glReconReportConfig.updateReconConfigStatusAndReportName(
							"RECON_FAILED", null,null, LocalDateTime.now(),
							configId);
				}
			} else {
				String msg = String.format("apiresp is empty %s ", apiResp);
				LOGGER.error(msg);
				glReconReportConfig.updateReconConfigStatusAndReportName(
						"RECON_FAILED", null,null, LocalDateTime.now(), configId);
			}
			}
		} catch (Exception ex) {
			String msg = String.format(
					"Error occured in GLInitiateReconProcessor for %s ", ex);
			LOGGER.error(msg, ex);
			glReconReportConfig.updateReconConfigStatusAndReportName(
					"RECON_FAILED", null,null, LocalDateTime.now(), configId);
			throw new AppException(msg, ex);

		}

	}

}
