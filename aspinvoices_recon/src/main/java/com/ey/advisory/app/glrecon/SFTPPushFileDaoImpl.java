package com.ey.advisory.app.glrecon;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.GlReconFileStatusEntity;
import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.admin.data.repositories.client.GlReconFileStatusRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.GLReconReportConfigRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.GlReconSrFileUploadRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.SecurityContext;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Sakshi.jain
 *
 */
@Component("SFTPPushFileDaoImpl")
@Slf4j
public class SFTPPushFileDaoImpl implements SFTPPushFileDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("GLReconReportConfigRepository")
	private GLReconReportConfigRepository glReconReportConfig;

	@Autowired
	@Qualifier("GlReconFileStatusRepository")
	private GlReconFileStatusRepository GlReconFileStatusRepository;

	@Autowired
	@Qualifier("GLReconSFTPFileTransferServiceImpl")
	private GLReconSFTPFileTransferService sftpService;
	// sr
	@Autowired
	@Qualifier("GlReconSrFileUploadRepository")
	private GlReconSrFileUploadRepository glReconSrFileUploadRepository;
	
	@Autowired
	private CommonUtility commonUtility;

	@Override
	public void sftpFilePushService(Long configId,Long entityId) {

		try {
			//180742--multi user access
			String userName = null;
			User user = SecurityContext.getUser();

			if (user != null) {
				userName = user.getUserPrincipalName();
			} else {
				userName = "SYSTEM";
			}
			
			String ansfromques = commonUtility.getAnsFromQue(entityId,
					"Multiple User Access to Async Reports");
			
		
			List<GlReconSrFileUploadEntity> entities = glReconSrFileUploadRepository
					.findByReqId(configId);
			
			List<GlReconFileStatusEntity> filesUpload=null;
			
			/*if (ansfromques.equalsIgnoreCase("A")) {
				filesUpload = GlReconFileStatusRepository
						.findActiveFileTypesAndGLDumpByCreatedBy(userName);

			} else if (ansfromques.equalsIgnoreCase("B")) {
				filesUpload = GlReconFileStatusRepository.findActiveFileTypesAndLatestGLDump();
			}
			

			Map<String, String> uploadedMasterDocsMap = filesUpload.stream()
					.collect(Collectors.toMap(GlReconFileStatusEntity::getDocId, // Key:
																					// uploadedDocNumber
							GlReconFileStatusEntity::getFileName // Value:
																	// uploadedDocName
			));*/

			boolean fldrFlag = sftpService.createFolder(
					"SFTP_ROOT/INTG_INBOUND/GLRECON", String.valueOf(configId));

			Map<String, String> uploadedDocsMap = entities.stream()
					.collect(Collectors.toMap(
							GlReconSrFileUploadEntity::getUploadedDocNumber, // Key:
																				// uploadedDocNumber
							GlReconSrFileUploadEntity::getUploadedDocName // Value:
																			// uploadedDocName
			));

			if (LOGGER.isDebugEnabled()) {
				/*LOGGER.debug(" uploadedMasterDocsMap {} ",
						uploadedMasterDocsMap);*/

				LOGGER.debug("fldrflag {}", fldrFlag);

				LOGGER.debug(" uploadedDocsMap {} ", uploadedDocsMap);
			}
			/*
			 * List<String> uploadedDocNumbers = entities.stream()
			 * .map(GlReconSrFileUploadEntity::getUploadedDocNumber)
			 * .collect(Collectors.toList());
			 */
			
			/*if (fldrFlag) {
				for (Map.Entry<String, String> entry : uploadedMasterDocsMap
						.entrySet()) {
					String docId = entry.getKey();
					String docName = entry.getValue();

					Document uploadedDocName = DocumentUtility
							.downloadDocumentByDocId(docId);

					File outputFile = new File(docName);

					InputStream contentStream = uploadedDocName
							.getContentStream().getStream();

					try (OutputStream outputStream = FileUtils
							.openOutputStream(outputFile)) {
						byte[] buffer = new byte[1024];
						int bytesRead;
						while ((bytesRead = contentStream.read(buffer)) != -1) {
							outputStream.write(buffer, 0, bytesRead);
						}
					}
					if (!outputFile.exists()) {
						outputFile.mkdirs();
					}
					sftpService.uploadFile(outputFile.getAbsolutePath(),
							"SFTP_ROOT/INTG_INBOUND/GLRECON/" + configId);
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("outputFile for docId {}",
								outputFile.getAbsolutePath());
					}
				}
			}*/
			// iterating based on docID
			if (fldrFlag) {
				for (Map.Entry<String, String> entry : uploadedDocsMap
						.entrySet()) {
					String docId = entry.getKey();
					String docName = entry.getValue();

					Document uploadedDocName = DocumentUtility
							.downloadDocumentByDocId(docId);

					File outputFile = new File(docName);

					InputStream contentStream = uploadedDocName
							.getContentStream().getStream();

					try (OutputStream outputStream = FileUtils
							.openOutputStream(outputFile)) {
						byte[] buffer = new byte[1024];
						int bytesRead;
						while ((bytesRead = contentStream.read(buffer)) != -1) {
							outputStream.write(buffer, 0, bytesRead);
						}
					}
					if (!outputFile.exists()) {
						outputFile.mkdirs();
					}
					sftpService.uploadFile(outputFile.getAbsolutePath(),
							"SFTP_ROOT/INTG_INBOUND/GLRECON/" + configId);
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("outputFile for docId {}",
								outputFile.getAbsolutePath());
					}
				}
			}
		} catch (Exception ex) {
			LOGGER.error("ERROR in SFTPPushFileDaoImpl " + ex);
			throw new AppException(ex);
		}

	}

}
