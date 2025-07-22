
package com.ey.advisory.app.util;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;

import javax.naming.InitialContext;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.OperationContext;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.ey.advisory.app.data.entities.client.Gstr2FileStatusEntity;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.multitenancy.GroupService;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.domain.master.AsyncExecJob;
import com.ey.advisory.core.async.domain.master.Group;
import com.sap.ecm.api.EcmService;
import com.sap.ecm.api.RepositoryOptions;
import com.sap.ecm.api.RepositoryOptions.Visibility;

@Component("Gstr2FileUploadUtil")
public class Gstr2FileUploadUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(Gstr2FileUploadUtil.class);

	/*
	 * @Autowired
	 * 
	 * @Qualifier("groupService") private GroupService groupService;
	 */

	public Session getCmisSession() throws Exception {
		GroupService groupService = StaticContextHolder.getBean("groupService", GroupService.class);
		String groupCode = TenantContext.getTenantId();
		LOGGER.debug("Group Code " + groupCode);
		try {
			Map<String, Group> groupMap = groupService.getGroupMap();
			Group group = groupMap.get(groupCode);
			String uniqueName = group.getRepositoryName();
			LOGGER.info("uniqueName " + uniqueName);
			// Use a secret key only known to your application (min. 10 chars)
			String secretKey = group.getRepositoryKey();
			LOGGER.info("secretKey " + secretKey);
			Session openCmisSession = null;
			InitialContext ctx = new InitialContext();
			String lookupName = "java:comp/env/" + "EcmService";
			EcmService ecmSvc = (EcmService) ctx.lookup(lookupName);
			try {
				// connect to my repository
				openCmisSession = ecmSvc.connect(uniqueName, secretKey);
				LOGGER.info("try " + secretKey);
				return openCmisSession;
			} catch (CmisObjectNotFoundException e) {
				// repository does not exist, so try to create it
				RepositoryOptions options = new RepositoryOptions();
				options.setUniqueName(uniqueName);
				options.setRepositoryKey(secretKey);
				LOGGER.info("try " + secretKey);
				options.setVisibility(Visibility.PROTECTED);
				ecmSvc.createRepository(options);
				// should be created now, so connect to it
				openCmisSession = ecmSvc.connect(uniqueName, secretKey);
				return openCmisSession;
			}
		} catch (Exception e) {
			throw new Exception(e);
		}

	}

	public Document getDocument(Session openCmisSession, String fileName, String folderName) throws IOException {
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

	public String getFileName(MultipartFile file) {

		/*
		 * This will display the date and time in the format of 12/09/2017
		 * 24:12:35. See the complete program below
		 */
		DateFormat df = new SimpleDateFormat("yyyyMMddhhmmss");

		// Getting the current date
		Date date = new Date();

		// This method returns the time in millis
		String timeMilli = df.format(date);

		// Normalize file name
		String fileName = StringUtils.cleanPath(file.getOriginalFilename());
		String fileNames = null;

		if (fileName.contains(".")) {
			fileNames = fileName.substring(0, fileName.lastIndexOf(".")) + timeMilli
					+ fileName.substring(fileName.lastIndexOf("."));
		} else {
			fileNames = fileName + timeMilli;
		}

		return fileNames;

	}

	public Gstr2FileStatusEntity getGstr2FileStatus(String fileName, String userName, AsyncExecJob asyncExceJob) {
		Gstr2FileStatusEntity fileStatus = new Gstr2FileStatusEntity();

		LocalDateTime localDate = LocalDateTime.now();
		String oriFileName = fileName.substring(0, fileName.lastIndexOf("."));
		String extension = fileName.substring(fileName.lastIndexOf(".") + 1);
		String status = asyncExceJob.getStatus();

		fileStatus.setFileName(oriFileName);
		fileStatus.setFileType(extension);
		fileStatus.setUpdatedBy(userName);
		fileStatus.setUpdatedOn(localDate);
		fileStatus.setFileStatus(status);

		return fileStatus;
	}

}
