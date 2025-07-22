package com.ey.advisory.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.OperationContext;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.chemistry.opencmis.commons.exceptions.CmisNameConstraintViolationException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.javatuples.Pair;

import com.aspose.cells.FileFormatType;
import com.aspose.cells.Workbook;
import com.ey.advisory.common.multitenancy.GroupService;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.async.domain.master.DocRepoServiceEntity;
import com.ey.advisory.core.async.domain.master.Group;
import com.ey.advisory.core.async.repositories.master.DocRepoServRepository;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.google.common.base.Strings;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 */

@Slf4j
public class DocumentUtility {

	public static String uploadDocumentWithFileName(Workbook workbook,
			String folderName, String fName) {
		if (workbook == null) {
			return "";
		}

		if (LOGGER.isDebugEnabled()) {
			String msg = "inside uploadDocument method";
			LOGGER.debug(msg);
		}
		Folder createdFolder = null;
		String uniqueFileName = null;
		try {
			// creating fileName
			uniqueFileName = fName;
			Session cmiSession = openCmisSession();
			// access the root folder of the repository
			Folder root = cmiSession.getRootFolder();
			if (LOGGER.isDebugEnabled()) {
				String msg = "Root Folder name is ";
				LOGGER.debug(msg + root.getName());
			}

			try {
				OperationContext oc = cmiSession.getDefaultContext();
				String query = String.format("cmis:name='%s'", folderName);
				ItemIterable<CmisObject> folders = cmiSession
						.queryObjects("cmis:folder", query, false, oc);
				for (CmisObject obj : folders) {
					if (obj instanceof Folder) {
						Folder folder = (Folder) obj;
						if (folder.getName().equalsIgnoreCase(folderName)) {
							if (LOGGER.isDebugEnabled()) {
								String msg = "Folder already exists ";
								LOGGER.debug(msg);
							}
							createdFolder = folder;
						}
					}
				}
				if (createdFolder == null) {
					if (LOGGER.isDebugEnabled()) {
						String msg = "Creating Folder";
						LOGGER.debug(msg);
					}
					// create a new folder
					Map<String, String> newFolderProps = new HashMap<String, String>();
					newFolderProps.put(PropertyIds.OBJECT_TYPE_ID,
							"cmis:folder");
					newFolderProps.put(PropertyIds.NAME, folderName);
					createdFolder = root.createFolder(newFolderProps);
					if (LOGGER.isDebugEnabled()) {
						String msg = "Folder Created";
						LOGGER.debug(msg);
					}

				}
			} catch (CmisNameConstraintViolationException e) {
				LOGGER.error("Exception while creating folder", e);
			}

			// create a new file in the root folder
			Map<String, Object> newFileProps = new HashMap<String, Object>();
			newFileProps.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");
			newFileProps.put(PropertyIds.NAME, uniqueFileName);
			InputStream stream = null;

			try {

				ByteArrayOutputStream ms = new ByteArrayOutputStream();
				workbook.save(ms, FileFormatType.XLSX);
				byte[] fileContent = ms.toByteArray();
				stream = new ByteArrayInputStream(fileContent);
				ContentStream contentStream = cmiSession.getObjectFactory()
						.createContentStream(uniqueFileName, fileContent.length,
								"application/vnd.ms-excel", stream);
				if (LOGGER.isDebugEnabled()) {
					String msg = "Creating document in the repo";
					LOGGER.debug(msg);
				}

				Document createdDocument = createdFolder.createDocument(
						newFileProps, contentStream, VersioningState.MAJOR);
				if (LOGGER.isDebugEnabled()) {
					String msg = "Created document in the repo  ";
					LOGGER.debug(msg + createdDocument.getId());
				}
				if (LOGGER.isDebugEnabled()) {
					String msg = "Created document name " + "in the repo  ";
					LOGGER.debug(msg + createdDocument.getName());
				}

			} catch (CmisNameConstraintViolationException e) {
				LOGGER.error("Exception while creating the document in repo",
						e);
			} finally {
				if (stream != null) {
					stream.close();
				}
			}
		}

		catch (Exception e) {
			String msg = "Unexpected error while uploading  files";
			LOGGER.error(msg, e);
		}
		return uniqueFileName;

	}

	public static String uploadDocument(Workbook workbook, String folderName,
			String fileFormat) {

		if (workbook == null) {
			return "";
		}

		String workbookPath = workbook.getFileName();
		File file = new File(workbookPath);
		String fileName = file.getName().replaceAll("[^a-zA-Z0-9._()\\-]", "");
		if (LOGGER.isDebugEnabled()) {
			String msg = "inside uploadDocument method";
			LOGGER.debug(msg);
		}
		Folder createdFolder = null;
		String uniqueFileName = null;
		try {
			// creating fileName

			uniqueFileName = getUniqueFileName(fileName);
			Session cmiSession = openCmisSession();
			// access the root folder of the repository
			Folder root = cmiSession.getRootFolder();
			if (LOGGER.isDebugEnabled()) {
				String msg = "Root Folder name is ";
				LOGGER.debug(msg + root.getName());
			}

			try {
				OperationContext oc = cmiSession.getDefaultContext();
				String query = String.format("cmis:name='%s'", folderName);
				ItemIterable<CmisObject> folders = cmiSession
						.queryObjects("cmis:folder", query, false, oc);
				for (CmisObject obj : folders) {
					if (obj instanceof Folder) {
						Folder folder = (Folder) obj;
						if (folder.getName().equalsIgnoreCase(folderName)) {
							if (LOGGER.isDebugEnabled()) {
								String msg = "Folder already exists ";
								LOGGER.debug(msg);
							}
							createdFolder = folder;
						}
					}
				}
				if (createdFolder == null) {
					if (LOGGER.isDebugEnabled()) {
						String msg = "Creating Folder";
						LOGGER.debug(msg);
					}
					// create a new folder
					Map<String, String> newFolderProps = new HashMap<String, String>();
					newFolderProps.put(PropertyIds.OBJECT_TYPE_ID,
							"cmis:folder");
					newFolderProps.put(PropertyIds.NAME, folderName);
					createdFolder = root.createFolder(newFolderProps);
					if (LOGGER.isDebugEnabled()) {
						String msg = "Folder Created";
						LOGGER.debug(msg);
					}

				}
			} catch (CmisNameConstraintViolationException e) {
				LOGGER.error("Exception while creating folder", e);
			}

			// create a new file in the root folder
			Map<String, Object> newFileProps = new HashMap<String, Object>();
			newFileProps.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");
			newFileProps.put(PropertyIds.NAME, uniqueFileName);
			InputStream stream = null;

			try {

				ByteArrayOutputStream ms = new ByteArrayOutputStream();
				if ("CSV".equalsIgnoreCase(fileFormat)) {
					workbook.save(ms, FileFormatType.CSV);
				} else {
					workbook.save(ms, FileFormatType.XLSX);
				}
				byte[] fileContent = ms.toByteArray();
				stream = new ByteArrayInputStream(fileContent);
				ContentStream contentStream = cmiSession.getObjectFactory()
						.createContentStream(uniqueFileName, fileContent.length,
								"application/vnd.ms-excel", stream);
				if (LOGGER.isDebugEnabled()) {
					String msg = "Creating document in the repo";
					LOGGER.debug(msg);
				}

				Document createdDocument = createdFolder.createDocument(
						newFileProps, contentStream, VersioningState.MAJOR);
				if (LOGGER.isDebugEnabled()) {
					String msg = "Created document in the repo  ";
					LOGGER.debug(msg + createdDocument.getId());
				}
				if (LOGGER.isDebugEnabled()) {
					String msg = "Created document name " + "in the repo  ";
					LOGGER.debug(msg + createdDocument.getName());
				}

			} catch (CmisNameConstraintViolationException e) {
				LOGGER.error("Exception while creating the document in repo",
						e);
			} finally {
				if (stream != null) {
					stream.close();
				}
			}
		}

		catch (Exception e) {
			String msg = "Unexpected error while uploading  files";
			LOGGER.error(msg, e);
		}
		return uniqueFileName;
	}

	public static Pair<String, String> uploadDocumentAndReturnDocID(
			Workbook workbook, String folderName, String fileFormat) {

		if (workbook == null) {
			return new Pair<String, String>("", "");
		}

		String workbookPath = workbook.getFileName();
		File file = new File(workbookPath);
		String fileName = file.getName();
		if (LOGGER.isDebugEnabled()) {
			String msg = String
					.format("inside uploadDocumentAndReturnDocID method "
							+ "- fileName : %s", fileName);
			LOGGER.debug(msg);
		}

		Folder createdFolder = null;
		String uniqueFileName = null;
		try {
			// creating fileName

			uniqueFileName = getUniqueFileName(fileName);
			Session cmiSession = openCmisSession();
			// access the root folder of the repository
			Folder root = cmiSession.getRootFolder();
			if (LOGGER.isDebugEnabled()) {
				String msg = "Root Folder name is ";
				LOGGER.debug(msg + root.getName());
			}

			try {
				OperationContext oc = cmiSession.getDefaultContext();
				String query = String.format("cmis:name='%s'", folderName);
				ItemIterable<CmisObject> folders = cmiSession
						.queryObjects("cmis:folder", query, false, oc);
				for (CmisObject obj : folders) {
					if (obj instanceof Folder) {
						Folder folder = (Folder) obj;
						if (folder.getName().equalsIgnoreCase(folderName)) {
							if (LOGGER.isDebugEnabled()) {
								String msg = "Folder already exists ";
								LOGGER.debug(msg);
							}
							createdFolder = folder;
						}
					}
				}
				if (createdFolder == null) {
					if (LOGGER.isDebugEnabled()) {
						String msg = "Creating Folder";
						LOGGER.debug(msg);
					}
					// create a new folder
					Map<String, String> newFolderProps = new HashMap<String, String>();
					newFolderProps.put(PropertyIds.OBJECT_TYPE_ID,
							"cmis:folder");
					newFolderProps.put(PropertyIds.NAME, folderName);
					createdFolder = root.createFolder(newFolderProps);
					if (LOGGER.isDebugEnabled()) {
						String msg = "Folder Created";
						LOGGER.debug(msg);
					}

				}
			} catch (CmisNameConstraintViolationException e) {
				LOGGER.error("Exception while creating folder", e);
			}

			// create a new file in the root folder
			Map<String, Object> newFileProps = new HashMap<String, Object>();
			newFileProps.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");
			newFileProps.put(PropertyIds.NAME, uniqueFileName);
			InputStream stream = null;

			try {

				ByteArrayOutputStream ms = new ByteArrayOutputStream();
				if ("CSV".equalsIgnoreCase(fileFormat)) {
					workbook.save(ms, FileFormatType.CSV);
				} else {
					workbook.save(ms, FileFormatType.XLSX);
				}
				byte[] fileContent = ms.toByteArray();
				stream = new ByteArrayInputStream(fileContent);
				ContentStream contentStream = cmiSession.getObjectFactory()
						.createContentStream(uniqueFileName, fileContent.length,
								"application/vnd.ms-excel", stream);
				if (LOGGER.isDebugEnabled()) {
					String msg = "Creating document in the repo";
					LOGGER.debug(msg);
				}

				Document createdDocument = createdFolder.createDocument(
						newFileProps, contentStream, VersioningState.MAJOR);
				if (LOGGER.isDebugEnabled()) {
					String msg = "Created document in the repo  ";
					LOGGER.debug(msg + createdDocument.getId());
				}
				if (LOGGER.isDebugEnabled()) {
					String msg = "Created document name " + "in the repo  ";
					LOGGER.debug(msg + createdDocument.getName());
				}
				return new Pair<String, String>(uniqueFileName,
						createdDocument.getId());
			} catch (CmisNameConstraintViolationException e) {
				LOGGER.error("Exception while creating the document in repo",
						e);
				throw new AppException(
						"Exception while creating the document in repo", e);
			} finally {
				if (stream != null) {
					stream.close();
				}
			}
		} catch (Exception e) {
			String msg = "Unexpected error while uploading  files";
			LOGGER.error(msg, e);
			throw new AppException(msg);
		}
	}

	public static Pair<String, String> uploadFile(File fileTobeUploaded,
			String folderName) {

		File file = new File(fileTobeUploaded.getAbsolutePath());
		String fileName = file.getName();
		if (LOGGER.isDebugEnabled()) {
			String msg = "inside uploadDocument method fileName {}";
			LOGGER.debug(msg, fileName);
		}
		Folder createdFolder = null;
		String uniqueFileName = null;
		try {
			// creating fileName
			uniqueFileName = getUniqueFileName(fileName);
			Session cmiSession = openCmisSession();
			// access the root folder of the repository
			Folder root = cmiSession.getRootFolder();
			if (LOGGER.isDebugEnabled()) {
				String msg = "Root Folder name is ";
				LOGGER.debug(msg + root.getName());
			}

			try {
				OperationContext oc = cmiSession.getDefaultContext();
				String query = String.format("cmis:name='%s'", folderName);
				ItemIterable<CmisObject> folders = cmiSession
						.queryObjects("cmis:folder", query, false, oc);
				for (CmisObject obj : folders) {
					if (obj instanceof Folder) {
						Folder folder = (Folder) obj;
						if (folder.getName().equalsIgnoreCase(folderName)) {
							if (LOGGER.isDebugEnabled()) {
								String msg = "Folder already exists ";
								LOGGER.debug(msg);
							}
							createdFolder = folder;
						}
					}
				}
				if (createdFolder == null) {
					if (LOGGER.isDebugEnabled()) {
						String msg = "Creating Folder";
						LOGGER.debug(msg);
					}
					// create a new folder
					Map<String, String> newFolderProps = new HashMap<>();
					newFolderProps.put(PropertyIds.OBJECT_TYPE_ID,
							"cmis:folder");
					newFolderProps.put(PropertyIds.NAME, folderName);
					createdFolder = root.createFolder(newFolderProps);
					if (LOGGER.isDebugEnabled()) {
						String msg = "Folder Created";
						LOGGER.debug(msg);
					}

				}
			} catch (CmisNameConstraintViolationException e) {
				LOGGER.error("Exception while creating folder", e);
			}

			// create a new file in the root folder
			Map<String, Object> newFileProps = new HashMap<>();
			newFileProps.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");
			newFileProps.put(PropertyIds.NAME, uniqueFileName);
			InputStream stream = null;

			try {
				byte[] fileContent = Files
						.readAllBytes(fileTobeUploaded.toPath());
				stream = new ByteArrayInputStream(fileContent);
				ContentStream contentStream = null;
				String[] fileType = uniqueFileName.split("\\.");

				if (LOGGER.isDebugEnabled()) {
					String msg = "File Type to upload is :";
					LOGGER.debug(msg + fileType[1]);
				}

				if (fileType[1].equalsIgnoreCase("zip")) {
					contentStream = cmiSession.getObjectFactory()
							.createContentStream(uniqueFileName,
									fileContent.length, "application/zip",
									stream);
				} else if (fileType[1].equalsIgnoreCase("json")) {
					contentStream = cmiSession.getObjectFactory()
							.createContentStream(uniqueFileName,
									fileContent.length, "application/json",
									stream);
				} else if (fileType[1].equalsIgnoreCase("csv")) {
					contentStream = cmiSession.getObjectFactory()
							.createContentStream(uniqueFileName,
									fileContent.length, "text/csv", stream);
				} else if (fileType[1].equalsIgnoreCase("pdf")) {
					contentStream = cmiSession.getObjectFactory()
							.createContentStream(uniqueFileName,
									fileContent.length, "application/pdf",
									stream);
				} else if (fileType[1].equalsIgnoreCase("xlsx")) {
					contentStream = cmiSession.getObjectFactory()
							.createContentStream(uniqueFileName,
									fileContent.length,
									"application/vnd.ms-excel", stream);
				}

				if (LOGGER.isDebugEnabled()) {
					String msg = "Creating document in the repo";
					LOGGER.debug(msg);
				}

				Document createdDocument = createdFolder.createDocument(
						newFileProps, contentStream, VersioningState.MAJOR);
				if (LOGGER.isDebugEnabled()) {
					String msg = "Created document in the repo  ";
					LOGGER.debug(msg + createdDocument.getId());
				}
				if (LOGGER.isDebugEnabled()) {
					String msg = "Created document name " + "in the repo  ";
					LOGGER.debug(msg + createdDocument.getName());
				}
				return new Pair<String, String>(uniqueFileName,
						createdDocument.getId());
			} catch (CmisNameConstraintViolationException e) {
				LOGGER.error("Exception while creating the document in repo",
						e);
				throw new AppException(
						"Exception while creating the document in repo", e);
			} finally {
				if (stream != null) {
					stream.close();
				}
			}
		} catch (Exception e) {
			String msg = "Unexpected error while uploading  files";
			LOGGER.error(msg, e);
			throw new AppException(msg);
		}
	}

	/**
	 * This method is creating file name using Time Stamp.
	 * 
	 * @param file
	 * @param fileType
	 * @return
	 */
	public static String getUniqueFileName(String fileName) {

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Inside getUniqueFileName() method " + "- fileName : %s",
					fileName);
			LOGGER.debug(msg);
		}
		String[] names = fileName.split("\\.");

		/*
		 * DateTimeFormatter dtf =
		 * DateTimeFormatter.ofPattern("yyyyMMddHHmmss"); LocalDateTime now =
		 * LocalDateTime.now();
		 * 
		 * String timeMilli = dtf.format(now);
		 */

		LocalDateTime now = LocalDateTime.now();
		String timeMilli = EYDateUtil.toISTDateTimeFromUTC(now).toString();
		timeMilli = timeMilli.replace(".", "");
		timeMilli = timeMilli.replace("-", "");
		timeMilli = timeMilli.replace(":", "");
		String fileNameWithTimeStamp = names[0] + "_" + timeMilli + "."
				+ names[1];

		if (LOGGER.isDebugEnabled()) {
			String msg = "UniqueFileName created ";
			LOGGER.debug(msg + fileNameWithTimeStamp);
		}

		return fileNameWithTimeStamp;

	}

	// opening Session
	public static Session openCmisSession() throws Exception {
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

			String idToken = updateIdToken(docRepServDetails.get(),
					serviceName);
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
			SessionFactoryImpl sessionFactory = SessionFactoryImpl
					.newInstance();
			Session cmisSession = sessionFactory.createSession(parameterMap);
			return cmisSession;
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	/**
	 * 
	 * @param cmisSession
	 * @param fileName
	 * @param fileFolder
	 * @return
	 * @throws Exception
	 */
	public static Document downloadDocument(String fileName, String fileFolder)
			throws Exception {

		if (LOGGER.isDebugEnabled()) {
			String msg = "inside downloadDocument() method ";
			LOGGER.debug(msg);
		}
		Session cmisSession = openCmisSession();
		OperationContext oc = cmisSession.getDefaultContext();

		String docQuery = String.format("cmis:name='%s'", fileName);
		ItemIterable<CmisObject> documents = cmisSession
				.queryObjects("cmis:document", docQuery, false, oc);

		for (CmisObject o2 : documents) {
			// Get the relevant Doc
			String currentDocName = o2.getName();
			if (o2 instanceof Document) {
				Document doc = (Document) o2;
				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(
							"Download Doc Name is: [%s], "
									+ "Download Doc Id is: [%s]",
							doc.getName(), doc.getId());
					LOGGER.debug(msg);
				}
				if (currentDocName.equalsIgnoreCase(fileName)) {

					if (LOGGER.isDebugEnabled()) {
						String msg = String.format(
								"Download Doc Name is: [%s], "
										+ "Download Doc Id is: [%s]",
								doc.getName(), doc.getId());
						LOGGER.debug(msg);
					}
					Document document = (Document) o2;
					return document;
				}
			}

		}
		return null;
	}

	public static Document downloadDocumentByDocId(String docId)
			throws Exception {

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"inside downloadDocument() method docId is %s", docId);
			LOGGER.debug(msg);
		}
		try {
			Session cmisSession = openCmisSession();
			return (Document) cmisSession.getObject(docId);
		} catch (CmisObjectNotFoundException e) {
			String errMsg = "Document not found.";
			throw new AppException(e, errMsg);
		}
	}

	public static String uploadZipFile(File zipFile, String folderName) {

		File file = new File(zipFile.getAbsolutePath());
		String fileName = file.getName();
		if (LOGGER.isDebugEnabled()) {
			String msg = "inside uploadDocument method";
			LOGGER.debug(msg);
		}
		Folder createdFolder = null;
		String uniqueFileName = null;
		try {
			// creating fileName
			uniqueFileName = getUniqueFileName(fileName);
			Session cmiSession = openCmisSession();
			// access the root folder of the repository
			Folder root = cmiSession.getRootFolder();
			if (LOGGER.isDebugEnabled()) {
				String msg = "Root Folder name is ";
				LOGGER.debug(msg + root.getName());
			}

			try {
				OperationContext oc = cmiSession.getDefaultContext();
				String query = String.format("cmis:name='%s'", folderName);
				ItemIterable<CmisObject> folders = cmiSession
						.queryObjects("cmis:folder", query, false, oc);
				for (CmisObject obj : folders) {
					if (obj instanceof Folder) {
						Folder folder = (Folder) obj;
						if (folder.getName().equalsIgnoreCase(folderName)) {
							if (LOGGER.isDebugEnabled()) {
								String msg = "Folder already exists ";
								LOGGER.debug(msg);
							}
							createdFolder = folder;
						}
					}
				}
				if (createdFolder == null) {
					if (LOGGER.isDebugEnabled()) {
						String msg = "Creating Folder";
						LOGGER.debug(msg);
					}
					// create a new folder
					Map<String, String> newFolderProps = new HashMap<>();
					newFolderProps.put(PropertyIds.OBJECT_TYPE_ID,
							"cmis:folder");
					newFolderProps.put(PropertyIds.NAME, folderName);
					createdFolder = root.createFolder(newFolderProps);
					if (LOGGER.isDebugEnabled()) {
						String msg = "Folder Created";
						LOGGER.debug(msg);
					}

				}
			} catch (CmisNameConstraintViolationException e) {
				LOGGER.error("Exception while creating folder", e);
			}

			// create a new file in the root folder
			Map<String, Object> newFileProps = new HashMap<>();
			newFileProps.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");
			newFileProps.put(PropertyIds.NAME, uniqueFileName);
			InputStream stream = null;

			try {
				byte[] fileContent = Files.readAllBytes(zipFile.toPath());
				stream = new ByteArrayInputStream(fileContent);
				ContentStream contentStream = null;
				String[] fileType = uniqueFileName.split("\\.");

				if (LOGGER.isDebugEnabled()) {
					String msg = "File Type to upload is :";
					LOGGER.debug(msg + fileType[1]);
				}

				if (fileType[1].equalsIgnoreCase("zip")) {
					contentStream = cmiSession.getObjectFactory()
							.createContentStream(uniqueFileName,
									fileContent.length, "application/zip",
									stream);
				} else if (fileType[1].equalsIgnoreCase("json")) {
					contentStream = cmiSession.getObjectFactory()
							.createContentStream(uniqueFileName,
									fileContent.length, "application/json",
									stream);
				} else if (fileType[1].equalsIgnoreCase("csv")) {
					contentStream = cmiSession.getObjectFactory()
							.createContentStream(uniqueFileName,
									fileContent.length, "text/csv", stream);
				} else if (fileType[1].equalsIgnoreCase("pdf")) {
					contentStream = cmiSession.getObjectFactory()
							.createContentStream(uniqueFileName,
									fileContent.length, "application/pdf",
									stream);
				}

				if (LOGGER.isDebugEnabled()) {
					String msg = "Creating document in the repo";
					LOGGER.debug(msg);
				}

				Document createdDocument = createdFolder.createDocument(
						newFileProps, contentStream, VersioningState.MAJOR);
				if (LOGGER.isDebugEnabled()) {
					String msg = "Created document in the repo  ";
					LOGGER.debug(msg + createdDocument.getId());
				}
				if (LOGGER.isDebugEnabled()) {
					String msg = "Created document name " + "in the repo  ";
					LOGGER.debug(msg + createdDocument.getName());
				}

			} catch (CmisNameConstraintViolationException e) {
				LOGGER.error("Exception while creating the document in repo",
						e);
			} finally {
				if (stream != null) {
					stream.close();
				}
			}
		} catch (Exception e) {
			String msg = "Unexpected error while uploading  files";
			LOGGER.error(msg, e);
		}
		return uniqueFileName;
	}

	public static String uploadCsvFile(File zipFile, String folderName) {

		File file = new File(zipFile.getAbsolutePath());
		String fileName = file.getName();
		if (LOGGER.isDebugEnabled()) {
			String msg = "inside uploadDocument method";
			LOGGER.debug(msg);
		}
		Folder createdFolder = null;
		String uniqueFileName = null;
		try {

			uniqueFileName = fileName;
			// creating fileName
			// uniqueFileName = getUniqueFileName(fileName);
			Session cmiSession = openCmisSession();
			// access the root folder of the repository
			Folder root = cmiSession.getRootFolder();
			if (LOGGER.isDebugEnabled()) {
				String msg = "Root Folder name is ";
				LOGGER.debug(msg + root.getName());
			}

			try {
				OperationContext oc = cmiSession.getDefaultContext();
				String query = String.format("cmis:name='%s'", folderName);
				ItemIterable<CmisObject> folders = cmiSession
						.queryObjects("cmis:folder", query, false, oc);
				for (CmisObject obj : folders) {
					if (obj instanceof Folder) {
						Folder folder = (Folder) obj;
						if (folder.getName().equalsIgnoreCase(folderName)) {
							if (LOGGER.isDebugEnabled()) {
								String msg = "Folder already exists ";
								LOGGER.debug(msg);
							}
							createdFolder = folder;
						}
					}
				}
				if (createdFolder == null) {
					if (LOGGER.isDebugEnabled()) {
						String msg = "Creating Folder";
						LOGGER.debug(msg);
					}
					// create a new folder
					Map<String, String> newFolderProps = new HashMap<>();
					newFolderProps.put(PropertyIds.OBJECT_TYPE_ID,
							"cmis:folder");
					newFolderProps.put(PropertyIds.NAME, folderName);
					createdFolder = root.createFolder(newFolderProps);
					if (LOGGER.isDebugEnabled()) {
						String msg = "Folder Created";
						LOGGER.debug(msg);
					}

				}
			} catch (CmisNameConstraintViolationException e) {
				LOGGER.error("Exception while creating folder", e);
			}

			// create a new file in the root folder
			Map<String, Object> newFileProps = new HashMap<>();
			newFileProps.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");
			newFileProps.put(PropertyIds.NAME, uniqueFileName);
			InputStream stream = null;

			try {
				byte[] fileContent = Files.readAllBytes(zipFile.toPath());
				stream = new ByteArrayInputStream(fileContent);
				ContentStream contentStream = null;
				String[] fileType = uniqueFileName.split("\\.");

				if (LOGGER.isDebugEnabled()) {
					String msg = "File Type to upload is :";
					LOGGER.debug(msg + fileType[1]);
				}

				if (fileType[1].equalsIgnoreCase("csv")) {
					contentStream = cmiSession.getObjectFactory()
							.createContentStream(uniqueFileName,
									fileContent.length, "text/csv", stream);
				} else if (fileType[1].equalsIgnoreCase("json")) {
					contentStream = cmiSession.getObjectFactory()
							.createContentStream(uniqueFileName,
									fileContent.length, "application/json",
									stream);
				}

				if (LOGGER.isDebugEnabled()) {
					String msg = "Creating document in the repo";
					LOGGER.debug(msg);
				}

				Document createdDocument = createdFolder.createDocument(
						newFileProps, contentStream, VersioningState.MAJOR);
				if (LOGGER.isDebugEnabled()) {
					String msg = "Created document in the repo  ";
					LOGGER.debug(msg + createdDocument.getId());
				}
				if (LOGGER.isDebugEnabled()) {
					String msg = "Created document name " + "in the repo  ";
					LOGGER.debug(msg + createdDocument.getName());
				}

			} catch (CmisNameConstraintViolationException e) {
				LOGGER.error("Exception while creating the document in repo",
						e);
			} finally {
				if (stream != null) {
					stream.close();
				}
			}
		} catch (Exception e) {
			String msg = "Unexpected error while uploading  files";
			LOGGER.error(msg, e);
		}
		return uniqueFileName;
	}

	public static boolean deleteDocument(String fileName, String fileFolder)
			throws Exception {

		if (LOGGER.isDebugEnabled()) {
			String msg = "inside downloadDocument() method ";
			LOGGER.debug(msg);
		}
		Session cmisSession = openCmisSession();
		boolean isDeleted = false;
		Folder root = cmisSession.getRootFolder();
		ItemIterable<CmisObject> children = root.getChildren();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format(
					"Downloading Root Folder name is: [%s] and Id is: [%s] ",
					root.getName(), root.getId()));
		}

		for (CmisObject o : children) {
			// Get the relevant folder
			if (o instanceof Folder) {
				String currentFolderName = ((Folder) o).getName();
				LOGGER.debug(String.format(
						"Download Folder Name: [%s], Download Folder Id: [%s]",
						currentFolderName, ((Folder) o).getId()));
				if (currentFolderName.equalsIgnoreCase(fileFolder)) {
					Folder folder = (Folder) o;
					if (LOGGER.isDebugEnabled()) {
						String msg = String.format(
								"----Inside the Folder, Folder Name is  "
										+ "[%s], Folder Id is: [%s]",
								o.getName(), o.getId());
						LOGGER.debug(msg);
					}

					// Get the relevant Doc
					for (CmisObject o2 : folder.getChildren()) {
						String currentDocName = o2.getName();
						if (o2 instanceof Document) {
							Document doc = (Document) o2;

							if (currentDocName.equalsIgnoreCase(fileName)) {

								o2.delete();
								isDeleted = true;

								if (LOGGER.isDebugEnabled()) {
									String msg = String.format(
											"Document deleted successfully: [%s]",
											doc.getName());
									LOGGER.debug(msg);
								}
								break;

							}
						}
					}
					break;
				}
			}
		}
		return isDeleted;
	}

	public static String uploadFileWithFileName(File zipFile, String folderName,
			String fileNameToBeUploaded) {

		if (LOGGER.isDebugEnabled()) {
			String msg = "inside uploadDocument method";
			LOGGER.debug(msg);
		}
		Folder createdFolder = null;
		try {

			Session cmiSession = openCmisSession();
			// access the root folder of the repository
			Folder root = cmiSession.getRootFolder();
			if (LOGGER.isDebugEnabled()) {
				String msg = "Root Folder name is ";
				LOGGER.debug(msg + root.getName());
			}

			try {
				OperationContext oc = cmiSession.getDefaultContext();
				String query = String.format("cmis:name='%s'", folderName);
				ItemIterable<CmisObject> folders = cmiSession
						.queryObjects("cmis:folder", query, false, oc);
				for (CmisObject obj : folders) {
					if (obj instanceof Folder) {
						Folder folder = (Folder) obj;
						if (folder.getName().equalsIgnoreCase(folderName)) {
							if (LOGGER.isDebugEnabled()) {
								String msg = "Folder already exists ";
								LOGGER.debug(msg);
							}
							createdFolder = folder;
						}
					}
				}
				if (createdFolder == null) {
					if (LOGGER.isDebugEnabled()) {
						String msg = "Creating Folder";
						LOGGER.debug(msg);
					}
					// create a new folder
					Map<String, String> newFolderProps = new HashMap<>();
					newFolderProps.put(PropertyIds.OBJECT_TYPE_ID,
							"cmis:folder");
					newFolderProps.put(PropertyIds.NAME, folderName);
					createdFolder = root.createFolder(newFolderProps);
					if (LOGGER.isDebugEnabled()) {
						String msg = "Folder Created";
						LOGGER.debug(msg);
					}

				}
			} catch (CmisNameConstraintViolationException e) {
				LOGGER.error("Exception while creating folder", e);
			}

			// create a new file in the root folder
			Map<String, Object> newFileProps = new HashMap<>();
			newFileProps.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");
			newFileProps.put(PropertyIds.NAME, fileNameToBeUploaded);
			InputStream stream = null;

			try {
				byte[] fileContent = Files.readAllBytes(zipFile.toPath());
				stream = new ByteArrayInputStream(fileContent);
				ContentStream contentStream = null;
				String[] fileType = fileNameToBeUploaded.split("\\.");

				if (LOGGER.isDebugEnabled()) {
					String msg = "File Type to upload is :";
					LOGGER.debug(msg + fileType[1]);
				}

				if (fileType[1].equalsIgnoreCase("zip")) {
					contentStream = cmiSession.getObjectFactory()
							.createContentStream(fileNameToBeUploaded,
									fileContent.length, "application/zip",
									stream);
				} else if (fileType[1].equalsIgnoreCase("json")) {
					contentStream = cmiSession.getObjectFactory()
							.createContentStream(fileNameToBeUploaded,
									fileContent.length, "application/json",
									stream);
				} else if (fileType[1].equalsIgnoreCase("csv")) {
					contentStream = cmiSession.getObjectFactory()
							.createContentStream(fileNameToBeUploaded,
									fileContent.length, "text/csv", stream);
				} else if (fileType[1].equalsIgnoreCase("xlsx")) {
					contentStream = cmiSession.getObjectFactory()
							.createContentStream(fileNameToBeUploaded,
									fileContent.length,
									"application/vnd.ms-excel", stream);
				} else if (fileType[1].equalsIgnoreCase("pdf")) {
					contentStream = cmiSession.getObjectFactory()
							.createContentStream(fileNameToBeUploaded,
									fileContent.length, "application/pdf",
									stream);
				}
				if (LOGGER.isDebugEnabled()) {
					String msg = "Creating document in the repo";
					LOGGER.debug(msg);
				}

				String docQuery = String.format("cmis:name='%s'",
						fileNameToBeUploaded);
				OperationContext docQueryOc = cmiSession.getDefaultContext();

				ItemIterable<CmisObject> documents = cmiSession
						.queryObjects("cmis:document", docQuery, false, docQueryOc);

				Document existingDoc = null;
				for (CmisObject o2 : documents) {
					if (o2 instanceof Document) {
						existingDoc = (Document) o2;
					}
				}

				if (existingDoc == null) {
					existingDoc = createdFolder.createDocument(newFileProps,
							contentStream, VersioningState.NONE);
				} else {
					existingDoc.setContentStream(contentStream, true);
				}

				if (LOGGER.isDebugEnabled()) {
					String msg = "Created document in the repo  ";
					LOGGER.debug(msg + existingDoc.getId());
				}
				if (LOGGER.isDebugEnabled()) {
					String msg = "Created document name " + "in the repo  ";
					LOGGER.debug(msg + existingDoc.getName());
				}

			} catch (CmisNameConstraintViolationException e) {
				LOGGER.error("Exception while creating the document in repo",
						e);
			} finally {
				if (stream != null) {
					stream.close();
				}
			}
		} catch (Exception e) {
			String msg = "Unexpected error while uploading  files";
			LOGGER.error(msg, e);
			throw new AppException(e);
		}
		return fileNameToBeUploaded;
	}

	private static Document findDocument(Folder folder, String fileName) {
		for (CmisObject o2 : folder.getChildren()) {
			String currentDocName = o2.getName();
			if (o2 instanceof Document) {
				Document doc = (Document) o2;
				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(
							"Download Doc Name is: [%s], "
									+ "Download Doc Id is: [%s]",
							doc.getName(), doc.getId());
					LOGGER.debug(msg);
				}
				if (currentDocName.equalsIgnoreCase(fileName)) {

					if (LOGGER.isDebugEnabled()) {
						String msg = String.format(
								"Download Doc Name is: [%s], "
										+ "Download Doc Id is: [%s]",
								doc.getName(), doc.getId());
						LOGGER.debug(msg);
					}
					Document document = (Document) o2;
					return document;
				}
			}
		}
		return null;
	}

	public static String deleteFolder(String fileFolder) throws Exception {

		try {
			if (LOGGER.isDebugEnabled()) {
				String msg = "inside deleteFolder() method ";
				LOGGER.debug(msg);
			}
			Session cmisSession = openCmisSession();
			Folder root = cmisSession.getRootFolder();
			ItemIterable<CmisObject> children = root.getChildren();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(String.format(
						"Downloading Root Folder name is: [%s] and Id is: [%s] ",
						root.getName(), root.getId()));
			}

			for (CmisObject o : children) {
				// Get the relevant folder
				if (o instanceof Folder) {
					String currentFolderName = ((Folder) o).getName();
					LOGGER.debug(String.format("Folder Name: [%s], Id: [%s]",
							currentFolderName, ((Folder) o).getId()));
					if (currentFolderName.equalsIgnoreCase(fileFolder)) {
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("Folder Name {} ", fileFolder);
						}
						Folder folder = (Folder) o;
						if (LOGGER.isDebugEnabled()) {
							String msg = String.format(
									"----Inside the Folder, Folder Name is  "
											+ "[%s], Folder Id is: [%s]",
									o.getName(), o.getId());
							LOGGER.debug(msg);
						}

						// Get the relevant Doc
						for (CmisObject o2 : folder.getChildren()) {
							if (o2 instanceof Document) {
								o2.delete();
							}
						}
					}
				}
			}
			return APIConstants.SUCCESS;
		} catch (Exception e) {
			String errMsg = String.format(
					"Exception while deleting the Folder %s", fileFolder);
			LOGGER.error(errMsg, e);
			return APIConstants.FAILED;
		}
	}

	/*
	 * public static Pair<String, String>
	 * uploadDocumentWithFilenameAndReturnDocID(File zipFile, String folderName,
	 * String fileNameToBeUploaded) {
	 * 
	 * if (LOGGER.isDebugEnabled()) { String msg =
	 * "inside uploadDocument method"; LOGGER.debug(msg); } Folder createdFolder
	 * = null; Document createdDocument = null; String docId = null;
	 * 
	 * try {
	 * 
	 * Session cmiSession = openCmisSession(); // access the root folder of the
	 * repository Folder root = cmiSession.getRootFolder(); if
	 * (LOGGER.isDebugEnabled()) { String msg = "Root Folder name is ";
	 * LOGGER.debug(msg + root.getName()); }
	 * 
	 * try { ItemIterable<CmisObject> folders = root.getChildren(); for
	 * (CmisObject obj : folders) { if (obj instanceof Folder) { Folder folder =
	 * (Folder) obj; if (folder.getName().equalsIgnoreCase(folderName)) { if
	 * (LOGGER.isDebugEnabled()) { String msg = "Folder already exists ";
	 * LOGGER.debug(msg); } createdFolder = folder; } } } if (createdFolder ==
	 * null) { if (LOGGER.isDebugEnabled()) { String msg = "Creating Folder";
	 * LOGGER.debug(msg); } // create a new folder Map<String, String>
	 * newFolderProps = new HashMap<>();
	 * newFolderProps.put(PropertyIds.OBJECT_TYPE_ID, "cmis:folder");
	 * newFolderProps.put(PropertyIds.NAME, folderName); createdFolder =
	 * root.createFolder(newFolderProps); if (LOGGER.isDebugEnabled()) { String
	 * msg = "Folder Created"; LOGGER.debug(msg); }
	 * 
	 * } } catch (CmisNameConstraintViolationException e) {
	 * LOGGER.error("Exception while creating folder", e); }
	 * 
	 * // create a new file in the root folder Map<String, Object> newFileProps
	 * = new HashMap<>(); newFileProps.put(PropertyIds.OBJECT_TYPE_ID,
	 * "cmis:document"); newFileProps.put(PropertyIds.NAME,
	 * fileNameToBeUploaded); InputStream stream = null; try { byte[]
	 * fileContent = Files.readAllBytes(zipFile.toPath()); stream = new
	 * ByteArrayInputStream(fileContent); ContentStream contentStream = null;
	 * String[] fileType = fileNameToBeUploaded.split("\\.");
	 * 
	 * if (LOGGER.isDebugEnabled()) { String msg = "File Type to upload is :";
	 * LOGGER.debug(msg + fileType[1]); }
	 * 
	 * if (fileType[1].equalsIgnoreCase("zip")) { contentStream =
	 * cmiSession.getObjectFactory() .createContentStream(fileNameToBeUploaded,
	 * fileContent.length, "application/zip", stream); } else if
	 * (fileType[1].equalsIgnoreCase("json")) { contentStream =
	 * cmiSession.getObjectFactory() .createContentStream(fileNameToBeUploaded,
	 * fileContent.length, "application/json", stream); } else if
	 * (fileType[1].equalsIgnoreCase("csv")) { contentStream =
	 * cmiSession.getObjectFactory() .createContentStream(fileNameToBeUploaded,
	 * fileContent.length, "text/csv", stream); } else if
	 * (fileType[1].equalsIgnoreCase("xlsx")) { contentStream =
	 * cmiSession.getObjectFactory() .createContentStream(fileNameToBeUploaded,
	 * fileContent.length, "application/vnd.ms-excel", stream); } else if
	 * (fileType[1].equalsIgnoreCase("pdf")) { contentStream =
	 * cmiSession.getObjectFactory() .createContentStream(fileNameToBeUploaded,
	 * fileContent.length, "application/pdf", stream); }
	 * 
	 * if (LOGGER.isDebugEnabled()) { String msg =
	 * "Creating document in the repo"; LOGGER.debug(msg); }
	 * 
	 * Document document = findDocument(createdFolder, fileNameToBeUploaded);
	 * 
	 * if (document == null) { document =
	 * createdFolder.createDocument(newFileProps, contentStream,
	 * VersioningState.MAJOR); } else { document.setContentStream(contentStream,
	 * true); }
	 * 
	 * if (LOGGER.isDebugEnabled()) { String msg =
	 * "Created document in the repo  "; LOGGER.debug(msg + document.getId()); }
	 * if (LOGGER.isDebugEnabled()) { String msg = "Created document name " +
	 * "in the repo  "; LOGGER.debug(msg + document.getName()); } docId =
	 * document.getId();
	 * 
	 * createdDocument = createdFolder.createDocument( newFileProps,
	 * contentStream, VersioningState.MAJOR); if (LOGGER.isDebugEnabled()) {
	 * String msg = "Created document in the repo  "; LOGGER.debug(msg +
	 * createdDocument.getId()); } if (LOGGER.isDebugEnabled()) { String msg =
	 * "Created document name " + "in the repo  "; LOGGER.debug(msg +
	 * createdDocument.getName()); }
	 * 
	 * } catch (CmisNameConstraintViolationException e) {
	 * LOGGER.error("Exception while creating the document in repo", e); }
	 * finally { if (stream != null) { stream.close(); } }
	 * 
	 * } catch (Exception e) { String msg =
	 * "Unexpected error while uploading  files"; LOGGER.error(msg); } return
	 * new Pair<String, String>(fileNameToBeUploaded,docId);
	 * 
	 * 
	 * }
	 */
	public static Pair<String, String> uploadFileWithFileNme(
			File fileTobeUploaded, String folderName,
			String fileNameToBeUploaded) {

		File file = new File(fileTobeUploaded.getAbsolutePath());
		String fileName = file.getName();
		if (LOGGER.isDebugEnabled()) {
			String msg = "inside uploadDocument method";
			LOGGER.debug(msg);
		}
		Folder createdFolder = null;
		String uniqueFileName = fileNameToBeUploaded;
		try {
			// creating fileName
			// uniqueFileName = getUniqueFileName(fileName);
			Session cmiSession = openCmisSession();
			// access the root folder of the repository
			Folder root = cmiSession.getRootFolder();
			if (LOGGER.isDebugEnabled()) {
				String msg = "Root Folder name is ";
				LOGGER.debug(msg + root.getName());
			}

			try {
				OperationContext oc = cmiSession.getDefaultContext();
				String query = String.format("cmis:name='%s'", folderName);
				ItemIterable<CmisObject> folders = cmiSession
						.queryObjects("cmis:folder", query, false, oc);
				for (CmisObject obj : folders) {
					if (obj instanceof Folder) {
						Folder folder = (Folder) obj;
						if (folder.getName().equalsIgnoreCase(folderName)) {
							if (LOGGER.isDebugEnabled()) {
								String msg = "Folder already exists ";
								LOGGER.debug(msg);
							}
							createdFolder = folder;
						}
					}
				}
				if (createdFolder == null) {
					if (LOGGER.isDebugEnabled()) {
						String msg = "Creating Folder";
						LOGGER.debug(msg);
					}
					// create a new folder
					Map<String, String> newFolderProps = new HashMap<>();
					newFolderProps.put(PropertyIds.OBJECT_TYPE_ID,
							"cmis:folder");
					newFolderProps.put(PropertyIds.NAME, folderName);
					createdFolder = root.createFolder(newFolderProps);
					if (LOGGER.isDebugEnabled()) {
						String msg = "Folder Created";
						LOGGER.debug(msg);
					}

				}
			} catch (CmisNameConstraintViolationException e) {
				LOGGER.error("Exception while creating folder", e);
			}

			// create a new file in the root folder
			Map<String, Object> newFileProps = new HashMap<>();
			newFileProps.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");
			newFileProps.put(PropertyIds.NAME, uniqueFileName);
			InputStream stream = null;

			try {
				byte[] fileContent = Files
						.readAllBytes(fileTobeUploaded.toPath());
				stream = new ByteArrayInputStream(fileContent);
				ContentStream contentStream = null;
				String[] fileType = uniqueFileName.split("\\.");

				if (LOGGER.isDebugEnabled()) {
					String msg = "File Type to upload is :";
					LOGGER.debug(msg + fileType[1]);
				}

				if (fileType[1].equalsIgnoreCase("zip")) {
					contentStream = cmiSession.getObjectFactory()
							.createContentStream(uniqueFileName,
									fileContent.length, "application/zip",
									stream);
				} else if (fileType[1].equalsIgnoreCase("json")) {
					contentStream = cmiSession.getObjectFactory()
							.createContentStream(uniqueFileName,
									fileContent.length, "application/json",
									stream);
				} else if (fileType[1].equalsIgnoreCase("csv")) {
					contentStream = cmiSession.getObjectFactory()
							.createContentStream(uniqueFileName,
									fileContent.length, "text/csv", stream);
				} else if (fileType[1].equalsIgnoreCase("pdf")) {
					contentStream = cmiSession.getObjectFactory()
							.createContentStream(uniqueFileName,
									fileContent.length, "application/pdf",
									stream);
				}

				if (LOGGER.isDebugEnabled()) {
					String msg = "Creating document in the repo";
					LOGGER.debug(msg);
				}

				Document createdDocument = createdFolder.createDocument(
						newFileProps, contentStream, VersioningState.MAJOR);
				if (LOGGER.isDebugEnabled()) {
					String msg = "Created document in the repo  ";
					LOGGER.debug(msg + createdDocument.getId());
				}
				if (LOGGER.isDebugEnabled()) {
					String msg = "Created document name " + "in the repo  ";
					LOGGER.debug(msg + createdDocument.getName());
				}
				return new Pair<String, String>(uniqueFileName,
						createdDocument.getId());
			} catch (CmisNameConstraintViolationException e) {
				LOGGER.error("Exception while creating the document in repo",
						e);
				throw new AppException(
						"Exception while creating the document in repo", e);
			} finally {
				if (stream != null) {
					stream.close();
				}
			}
		} catch (Exception e) {
			String msg = "Unexpected error while uploading  files";
			LOGGER.error(msg, e);
			throw new AppException(msg);
		}
	}

	public static String updateIdToken(
			DocRepoServiceEntity docIdTokenRepoEntity, String serviceName) {
		String idToken = null;
		DocRepoServRepository docRepoServRepo = StaticContextHolder
				.getBean("DocRepoServRepository", DocRepoServRepository.class);
		if (Strings.isNullOrEmpty(docIdTokenRepoEntity.getIdToken())) {
			idToken = getIdTokenforDocServRep();
			LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(25);
			docRepoServRepo.updateIdToken(idToken, serviceName, expiryTime);
			return idToken;
		} else {
			idToken = docIdTokenRepoEntity.getIdToken();
			LocalDateTime expiryTime = docIdTokenRepoEntity.getExpiryTime();
			LocalDateTime currentTime = LocalDateTime.now();
			if (expiryTime.isBefore(currentTime)) {
				LOGGER.debug(
						"Id Token is expired for Group Code {}, Hence generating a new one ",
						serviceName);
				idToken = getIdTokenforDocServRep();
				expiryTime = LocalDateTime.now().plusMinutes(25);
				docRepoServRepo.updateIdToken(idToken, serviceName, expiryTime);
				return idToken;
			} else {
				LOGGER.debug("Id Token is active for Group Code {} ",
						serviceName);
				return idToken;
			}
		}
	}

	private static String getIdTokenforDocServRep() {
		try {
			ConfigManager configManager = StaticContextHolder
					.getBean("ConfigManagerImpl", ConfigManager.class);
			HttpClient httpClient = StaticContextHolder
					.getBean("GSTNHttpClient", HttpClient.class);
			Map<String, Config> configMap = configManager
					.getConfigs("DOCSERVREPO", "doc.servrepo", "DEFAULT");
			String authUrl = configMap.get("doc.servrepo.url") == null ? ""
					: configMap.get("doc.servrepo.url").getValue();
			String clientId = configMap.get("doc.servrepo.clientId") == null
					? ""
					: configMap.get("doc.servrepo.clientId").getValue();
			String clientSecret = configMap
					.get("doc.servrepo.clientSecret") == null ? ""
							: configMap.get("doc.servrepo.clientSecret")
									.getValue();
			HttpGet httpGet = new HttpGet(authUrl);
			String userNamePswrdStr = clientId.concat(":").concat(clientSecret);
			String encodedStr = new String(
					Base64.encodeBase64(userNamePswrdStr.getBytes()));
			httpGet.setHeader("Authorization", "Basic " + encodedStr);
			LOGGER.error("Requesting {} ", httpGet.getRequestLine());
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			String responseBody = httpClient.execute(httpGet, responseHandler);
			LOGGER.error("responseBody {} ", responseBody);
			JsonObject requestObject = JsonParser.parseString(responseBody)
					.getAsJsonObject();
			if (requestObject.has("access_token")) {
				return requestObject.get("access_token").getAsString();
			} else {
				return "";
			}
		} catch (Exception ex) {
			LOGGER.error("Exception in Id Token Doc Serv Repo {}", ex);
			return ex.getMessage();
		}
	}
}
