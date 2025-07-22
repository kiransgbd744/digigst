package com.ey.advisory.app.services.annexure1fileupload;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.ey.advisory.common.AppException;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.multitenancy.DocumentIdContext;
import com.ey.advisory.common.multitenancy.GroupService;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.domain.master.DocRepoServiceEntity;
import com.ey.advisory.core.async.domain.master.Group;
import com.ey.advisory.core.async.repositories.master.DocRepoServRepository;
import com.google.common.base.Strings;
import com.ibm.icu.text.SimpleDateFormat;

import jakarta.xml.ws.BindingType;

/**
 * 
 * @author Mahesh.Golla
 *
 */

@Component("OutwardFileUploadUtil")
public class OutwardFileUploadUtil {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(OutwardFileUploadUtil.class);

	public String getFileName(MultipartFile file, String fileType) {
		/*
		 * This will display the date and time in the format of 12/09/2017
		 * 24:12:35. See the complete program below
		 */
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
		LocalDateTime now = LocalDateTime.now();

		LocalDateTime convertUTC = EYDateUtil.toUTCDateTimeFromLocal(now);
		LocalDateTime convertIST = EYDateUtil.toISTDateTimeFromUTC(convertUTC);

		// Getting the current date
		// Date date = new Date();

		// This method returns the time in millis
		String timeMilli = dtf.format(convertIST);
		// Normalize file name
		String fileName = StringUtils.cleanPath(file.getOriginalFilename());

		String fileNames = fileType + "_" + timeMilli + "_" + fileName;
		return fileNames;

	}

	public Session openCmisSession() throws Exception {
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
				"browser");
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
			LOGGER.debug("Parameter Map {} ", parameterMap);
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
			String fileFolder) throws Exception  {
		
		String docId = DocumentIdContext.getDocumentId();
		if(!Strings.isNullOrEmpty(docId))
		{
			Document document = DocumentUtility.downloadDocumentByDocId(docId);
			return document;
			
		}
		else{
		Folder root = openCmisSession.getRootFolder();
		ItemIterable<CmisObject> children = root.getChildren();

		for (CmisObject o : children) {
			// Get the relevant folder
			if (o instanceof Folder
					&& ((Folder) o).getName().equalsIgnoreCase(fileFolder)) {
				Folder folder = (Folder) o;
				LOGGER.error("Folder Name " + folder.getName());
				// Get the relevant Doc
				for (CmisObject o2 : folder.getChildren()) {
					if (o2 instanceof Document && ((Document) o2).getName()
							.equalsIgnoreCase(fileName)) {
						LOGGER.error(
								"Document Name " + ((Document) o2).getName());
						Document document = (Document) o2;
						return document;
					}
				}
			}
		}
		return null;
		}
	}

	public static LocalDate getLocalDate(String strRetperiod, String strYear) {
		SimpleDateFormat inputFormat = new SimpleDateFormat("MMMM");
		Calendar cal = Calendar.getInstance();
		try {
			cal.setTime(inputFormat.parse(strRetperiod));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		SimpleDateFormat outputFormat = new SimpleDateFormat("MM");
		String format = outputFormat.format(cal.getTime());

		String startDate = "01" + format + strYear;

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
		LocalDate returnPeriod = LocalDate.parse(startDate, formatter);
		LocalDate retPer = returnPeriod
				.with(TemporalAdjusters.lastDayOfMonth());
		return retPer;
	}

}
