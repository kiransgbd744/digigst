package com.ey.advisory.app.util;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.OperationContext;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.multitenancy.GroupService;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.JobStatusConstants;
import com.ey.advisory.core.async.domain.master.AsyncExecJob;
import com.ey.advisory.core.async.domain.master.DocRepoServiceEntity;
import com.ey.advisory.core.async.domain.master.Group;
import com.ey.advisory.core.async.repositories.master.DocRepoServRepository;

@Component("Gstr1FileUploadUtil")
public class Gstr1FileUploadUtil {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr1FileUploadUtil.class);

	/*
	 * @Autowired
	 * 
	 * @Qualifier("groupService") private GroupService groupService;
	 */

	public Session getCmisSession() throws Exception {
		String groupCode = TenantContext.getTenantId();

		if (LOGGER.isDebugEnabled()) {
			String msg = "Group Code ";
			LOGGER.debug(msg + groupCode);
		}
		try {
			GroupService groupService = StaticContextHolder
					.getBean("groupService", GroupService.class);

			Map<String, Group> groupMap = groupService.getGroupMap();
			Group group = groupMap.get(groupCode);
			String serviceName = group.getRepositoryName();

			DocRepoServRepository docRepoServRepo = StaticContextHolder.getBean(
					"DocRepoServRepository", DocRepoServRepository.class);

			Optional<DocRepoServiceEntity> docRepServDetails = docRepoServRepo
					.findByServiceNameAndIsActiveTrue(serviceName);

			if (!docRepServDetails.isPresent()) {
				String errMsg = String.format(
						"Service is not available for this Group %s, Please configure and try again.",
						groupCode);
				LOGGER.error(errMsg);
				throw new AppException(errMsg);
			}

			String idToken = DocumentUtility
					.updateIdToken(docRepServDetails.get(), serviceName);
			Map<String, String> parameterMap = new HashMap<>();
			parameterMap.put(SessionParameter.USER_AGENT,
					"OpenCMIS-Workbench/1.1.0-sap-03 Apache-Chemistry-OpenCMIS/1.1.0-sap-03 (Java 1.8.0_281; Windows 10 10.0)");
			parameterMap.put(SessionParameter.BINDING_TYPE,
					BindingType.BROWSER.value());
			parameterMap.put(SessionParameter.OAUTH_ACCESS_TOKEN, idToken);
			parameterMap.put(SessionParameter.REPOSITORY_ID,
					group.getRepositoryKey());
			parameterMap.put(SessionParameter.BROWSER_URL,
					docRepServDetails.get().getUrl());

			parameterMap.put(SessionParameter.AUTH_HTTP_BASIC, "false");
			parameterMap.put(SessionParameter.AUTH_SOAP_USERNAMETOKEN, "false");
			parameterMap.put(SessionParameter.AUTH_OAUTH_BEARER, "true");
			parameterMap.put(SessionParameter.COMPRESSION, "true");
			parameterMap.put(SessionParameter.CLIENT_COMPRESSION, "false");
			parameterMap.put(SessionParameter.COOKIES, "true");
			parameterMap.put(SessionParameter.LOCALE_ISO639_LANGUAGE, "en");
			parameterMap.put(SessionParameter.CONNECT_TIMEOUT, "30000");
			parameterMap.put(SessionParameter.READ_TIMEOUT, "600000");
			LOGGER.error("Parameter Map {} ", parameterMap);
			SessionFactoryImpl sessionFactory = SessionFactoryImpl
					.newInstance();
			return sessionFactory.createSession(parameterMap);
		} catch (Exception e) {
			LOGGER.error("Error While Creating the CMIS Session ", e);
			throw new Exception(e);
		}
	}

	public Document getDocument(Session openCmisSession, String fileName,
			String folderName) throws IOException {
		OperationContext oc = openCmisSession.getDefaultContext();
		String query = String.format("cmis:name='%s'", fileName);
		ItemIterable<CmisObject> children = openCmisSession
				.queryObjects("cmis:document", query, false, oc);
		for (CmisObject o2 : children) {
			// Get the relevant Doc
			LOGGER.error(" Inside Folder ");
			if (o2 instanceof Document
					&& ((Document) o2).getName().equalsIgnoreCase(fileName)) {
				LOGGER.error("Document Name " + ((Document) o2).getName());
				Document document = (Document) o2;
				return document;
			}
		}
		return null;
	}

	public String getFileName(MultipartFile file, String fileType) {

		/*
		 * This will display the date and time in the format of 12/09/2017
		 * 24:12:35. See the complete program below
		 */
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
		LocalDateTime now = LocalDateTime.now();
		// DateFormat df = new SimpleDateFormat("yyyyMMddhhmmss");
		LocalDateTime convertUTC = EYDateUtil.toUTCDateTimeFromLocal(now);
		LocalDateTime convertIST = EYDateUtil.toISTDateTimeFromUTC(convertUTC);

		// Getting the current date
		// Date date = new Date();

		// This method returns the time in millis
		String timeMilli = dtf.format(convertIST);
		// Normalize file name
		String fileName = StringUtils.cleanPath(file.getOriginalFilename());

		String fileNames = fileType + "_" + timeMilli + "_" + fileName;

		/*
		 * String fileNames = null;
		 * 
		 * if(fileName.contains(".")){ fileNames = fileName.substring(0,
		 * fileName.lastIndexOf(".")) + timeMilli +
		 * fileName.substring(fileName.lastIndexOf(".")); } else{ fileNames =
		 * fileName + timeMilli; }
		 */

		return fileNames;

	}

	public Gstr1FileStatusEntity getGstr2aFileStatus(String fileName,
			String userName, String fileType, String dataType, Long entityId) {
		Gstr1FileStatusEntity fileStatus = new Gstr1FileStatusEntity();

		LocalDateTime localDate = LocalDateTime.now();
		fileStatus.setFileName(fileName);
		fileStatus.setFileType(fileType);
		fileStatus.setUpdatedBy(userName);
		fileStatus.setUpdatedOn(EYDateUtil.toUTCDateTimeFromLocal(localDate));
		fileStatus.setFileStatus(JobStatusConstants.UPLOADED);
		fileStatus.setReceivedDate(
				EYDateUtil.toUTCDateTimeFromLocal(localDate.toLocalDate()));
		fileStatus.setSource(JobStatusConstants.WEB_UPLOAD);
		if (entityId != null) {
			fileStatus.setEntityId(entityId);
		}
		fileStatus.setDataType(dataType);

		return fileStatus;
	}

	public Gstr1FileStatusEntity getGstr1FileStatusFor2bpr(String fileName,
			String userName, AsyncExecJob asyncExceJob, String fileType,
			String dataType, Long entityId, String fileHash) {
		Gstr1FileStatusEntity fileStatus = new Gstr1FileStatusEntity();

		LocalDateTime localDate = LocalDateTime.now();
		fileStatus.setFileName(fileName);
		fileStatus.setFileType(fileType);
		fileStatus.setUpdatedBy(userName);
		fileStatus.setUpdatedOn(localDate);
		fileStatus.setFileStatus(JobStatusConstants.UPLOADED);
		fileStatus.setReceivedDate(localDate.toLocalDate());
		fileStatus.setSource(JobStatusConstants.WEB_UPLOAD);
		fileStatus.setFileHash(fileHash);
		if (entityId != null) {
			fileStatus.setEntityId(entityId);
		}
		fileStatus.setDataType(dataType);

		return fileStatus;
	}

	public Gstr1FileStatusEntity getDmsGstr1FileStatus(String fileName,
			String userName, AsyncExecJob asyncExceJob, String fileType,
			String dataType, Long entityId, String fileHash,
			String transformationRule, String transformationStatus) {
		Gstr1FileStatusEntity fileStatus = new Gstr1FileStatusEntity();

		LocalDateTime localDate = LocalDateTime.now();
		fileStatus.setFileName(fileName);
		fileStatus.setFileType(fileType);
		fileStatus.setUpdatedBy(userName);
		fileStatus.setUpdatedOn(localDate);
		fileStatus.setFileStatus(JobStatusConstants.UPLOADED);
		fileStatus.setReceivedDate(localDate.toLocalDate());
		fileStatus.setSource(JobStatusConstants.WEB_UPLOAD);
		fileStatus.setFileHash(fileHash);
		fileStatus.setTransformationRule(transformationRule);
		fileStatus.setTransformationStatus(transformationStatus);
		if (entityId != null) {
			fileStatus.setEntityId(entityId);
		}
		fileStatus.setDataType(dataType);

		return fileStatus;
	}

	public Gstr1FileStatusEntity getGstr1FileStatus(String fileName,
			String userName, AsyncExecJob asyncExceJob, String fileType,
			String dataType, Long entityId) {
		Gstr1FileStatusEntity fileStatus = new Gstr1FileStatusEntity();

		LocalDateTime localDate = LocalDateTime.now();
		fileStatus.setFileName(fileName);
		fileStatus.setFileType(fileType);
		fileStatus.setUpdatedBy(userName);
		fileStatus.setUpdatedOn(localDate);
		fileStatus.setFileStatus(JobStatusConstants.UPLOADED);
		fileStatus.setReceivedDate(localDate.toLocalDate());
		fileStatus.setSource(JobStatusConstants.WEB_UPLOAD);
		if (entityId != null) {
			fileStatus.setEntityId(entityId);
		}
		fileStatus.setDataType(dataType);

		return fileStatus;
	}

	public Gstr1FileStatusEntity getGstr6FileStatus(String fileName,
			String userName, AsyncExecJob asyncExceJob, String fileType,
			String dataType, Long entityId, String docId) {
		Gstr1FileStatusEntity fileStatus = new Gstr1FileStatusEntity();

		LocalDateTime localDate = LocalDateTime.now();
		fileStatus.setFileName(fileName);
		fileStatus.setFileType(fileType);
		fileStatus.setUpdatedBy(userName);
		fileStatus.setUpdatedOn(localDate);
		fileStatus.setFileStatus(JobStatusConstants.UPLOADED);
		fileStatus.setReceivedDate(localDate.toLocalDate());
		fileStatus.setSource(JobStatusConstants.WEB_UPLOAD);
		if (entityId != null) {
			fileStatus.setEntityId(entityId);
		}
		fileStatus.setDataType(dataType);
		fileStatus.setDocId(docId);

		return fileStatus;
	}

	public Gstr1FileStatusEntity getSftpFileStatus(String fileName,
			String userName, AsyncExecJob asyncExceJob, String fileType,
			String dataType, Long entityId) {
		Gstr1FileStatusEntity fileStatus = new Gstr1FileStatusEntity();

		LocalDateTime localDate = LocalDateTime.now();
		fileStatus.setFileName(fileName);
		fileStatus.setFileType(fileType);
		fileStatus.setUpdatedBy(userName);
		fileStatus.setUpdatedOn(localDate);
		fileStatus.setFileStatus(JobStatusConstants.UPLOADED);
		fileStatus.setReceivedDate(localDate.toLocalDate());
		fileStatus.setSource(JobStatusConstants.SFTP_WEB_UPLOAD);
		if (entityId != null) {
			fileStatus.setEntityId(entityId);
		}
		fileStatus.setDataType(dataType);

		return fileStatus;
	}

	public Gstr1FileStatusEntity getSftpFileStatusGstr7(String fileName,
			String userName, AsyncExecJob asyncExceJob, String fileType,
			String dataType, Long entityId, String docId) {
		Gstr1FileStatusEntity fileStatus = new Gstr1FileStatusEntity();

		LocalDateTime localDate = LocalDateTime.now();
		fileStatus.setFileName(fileName);
		fileStatus.setFileType(fileType);
		fileStatus.setUpdatedBy(userName);
		fileStatus.setUpdatedOn(localDate);
		fileStatus.setFileStatus(JobStatusConstants.UPLOADED);
		fileStatus.setReceivedDate(localDate.toLocalDate());
		fileStatus.setSource(JobStatusConstants.SFTP_WEB_UPLOAD);
		if (entityId != null) {
			fileStatus.setEntityId(entityId);
		}
		fileStatus.setDocId(docId);
		fileStatus.setDataType(dataType);

		return fileStatus;
	}

	public Gstr1FileStatusEntity getErpFileStatus(String fileName,
			String userName, AsyncExecJob asyncExceJob, String fileType,
			String dataType, Long entityId) {
		Gstr1FileStatusEntity fileStatus = new Gstr1FileStatusEntity();

		LocalDateTime localDate = LocalDateTime.now();
		fileStatus.setFileName(fileName);
		fileStatus.setFileType(fileType);
		fileStatus.setUpdatedBy(userName);
		fileStatus.setUpdatedOn(localDate);
		fileStatus.setFileStatus(JobStatusConstants.UPLOADED);
		fileStatus.setReceivedDate(localDate.toLocalDate());
		fileStatus.setSource(JobStatusConstants.ERP);
		if (entityId != null) {
			fileStatus.setEntityId(entityId);
		}
		fileStatus.setDataType(dataType);

		return fileStatus;
	}

	public Gstr1FileStatusEntity getMasterFileStatusEntity(String fileName,
			String userName, AsyncExecJob asyncExceJob, String fileType,
			String dataType, Long entityId) {
		Gstr1FileStatusEntity fileStatus = getGstr1FileStatus(fileName,
				userName, asyncExceJob, fileType, dataType, null);
		fileStatus.setEntityId(entityId);
		return fileStatus;

	}

	public Gstr1FileStatusEntity getGstr2bFileStatus(String fileName,
			String userName, String fileType, String dataType, Long entityId) {
		Gstr1FileStatusEntity fileStatus = new Gstr1FileStatusEntity();

		LocalDateTime localDate = LocalDateTime.now();
		fileStatus.setFileName(fileName);
		fileStatus.setFileType(fileType);
		fileStatus.setUpdatedBy(userName);
		fileStatus.setUpdatedOn(EYDateUtil.toUTCDateTimeFromLocal(localDate));
		fileStatus.setFileStatus(JobStatusConstants.UPLOADED);
		fileStatus.setReceivedDate(
				EYDateUtil.toUTCDateTimeFromLocal(localDate.toLocalDate()));
		fileStatus.setSource(JobStatusConstants.WEB_UPLOAD);
		if (entityId != null) {
			fileStatus.setEntityId(entityId);
		}
		fileStatus.setDataType(dataType);

		return fileStatus;
	}

	public Gstr1FileStatusEntity getDmsGstr1FileStatus(String fileName,
			String userName, AsyncExecJob asyncExceJob, String fileType,
			String dataType, Long entityId, String source,
			String transformationRule, String transformationStatus,
			String transformationRuleName, String errorDesc) {
		Gstr1FileStatusEntity fileStatus = new Gstr1FileStatusEntity();

		LocalDateTime localDate = LocalDateTime.now();
		fileStatus.setFileName(fileName);
		fileStatus.setFileType(fileType);
		fileStatus.setUpdatedBy(userName);
		fileStatus.setUpdatedOn(localDate);
		fileStatus.setFileStatus(JobStatusConstants.UPLOADED);
		fileStatus.setReceivedDate(localDate.toLocalDate());
		fileStatus.setSource(source);
		fileStatus.setErrorDesc(errorDesc != null ? errorDesc : "");
		fileStatus.setFileHash("");
		fileStatus.setTransformationRule(transformationRule);
		fileStatus.setTransformationStatus(transformationStatus);
		fileStatus.setTransformationRuleName(transformationRuleName);
		if (entityId != null) {
			fileStatus.setEntityId(entityId);
		}
		fileStatus.setDataType(dataType);

		return fileStatus;
	}
}
