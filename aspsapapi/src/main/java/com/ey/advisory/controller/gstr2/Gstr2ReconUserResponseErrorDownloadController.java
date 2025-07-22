package com.ey.advisory.controller.gstr2;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Optional;

import org.apache.chemistry.opencmis.client.api.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.admin.data.repositories.client.FileStatusRepository;
import com.ey.advisory.app.asprecon.gstr2.reconresponse.upload.ReportDownloadPathEntity;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2PathRepository;
import com.ey.advisory.app.downloadreports.DownloadReportsConstant;
import com.ey.advisory.app.util.Gstr1FileUploadUtil;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.config.ConfigConstants;
import com.google.common.base.Strings;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */

@RestController
@Slf4j
public class Gstr2ReconUserResponseErrorDownloadController {

	@Autowired
	@Qualifier("Gstr2PathRepository")
	private Gstr2PathRepository pathRepo;

	@Autowired
	@Qualifier("FileStatusRepository")
	private FileStatusRepository fileStatusRepo;

	@Autowired
	@Qualifier("Gstr1FileUploadUtil")
	private Gstr1FileUploadUtil gstr1FileUploadUtil;

	@RequestMapping(value = "/ui/gstrUserResponse2ReportsDownload", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public void fileDownloads(@RequestBody String jsonString,
			HttpServletResponse response) throws Exception {
		LOGGER.debug("inside fileDownloads method and file type is {} ",
				"Gstr2ReconUserResponseErrorDownloadController");

		String tenantCode = TenantContext.getTenantId();
		LOGGER.debug("Tenant Id Is {}", tenantCode);

		JsonObject requestObject = JsonParser.parseString(jsonString)
				.getAsJsonObject();
		JsonObject json = requestObject.get("req").getAsJsonObject();

		String fileId = json.get("fileId").getAsString();
		String fileType = json.get("type").getAsString();

		ReportDownloadPathEntity entity = pathRepo
				.findByFileId(Long.valueOf(fileId));

		Optional<Gstr1FileStatusEntity> fileStatusentity = fileStatusRepo
				.findById(Long.valueOf(fileId));

		if (entity == null && !fileStatusentity.isPresent()) {
			return;
		}
		
		Document document = null;
		
		String errorFile = null;
		String psdFile = null;
		String totalFile = null;
		String infoFile = null;
		String fileFolder = ConfigConstants.GSTR2USERRESPONSEUPLOADS;
		
		
		if(entity!=null)
		{
		 errorFile = entity.getErrorPath();
		 psdFile = entity.getProceesedPath();
		 totalFile = entity.getTotalPath();
		 infoFile = entity.getInfoPath();
		 fileFolder = ConfigConstants.GSTR2USERRESPONSEUPLOADS;

		

		if (fileType != null && fileType
				.equalsIgnoreCase(DownloadReportsConstant.PROCESSED)) {

			document = DocumentUtility.downloadDocument(psdFile, fileFolder);

			if (document == null) {
				return;
			}

			InputStream inputStream = document.getContentStream().getStream();
			int read = 0;
			byte[] bytes = new byte[1024];

			if (document != null) {
				response.setHeader("Content-Disposition",
						String.format("attachment; filename =" + psdFile));
				OutputStream outputStream = response.getOutputStream();
				while ((read = inputStream.read(bytes)) != -1) {
					outputStream.write(bytes, 0, read);
				}
			}
		}

		if (fileType != null
				&& fileType.equalsIgnoreCase(DownloadReportsConstant.ERROR)) {

			document = DocumentUtility.downloadDocument(errorFile, fileFolder);

			if (document == null) {
				return;
			}

			InputStream inputStream = document.getContentStream().getStream();
			int read = 0;
			byte[] bytes = new byte[1024];

			if (document != null) {
				response.setHeader("Content-Disposition",
						String.format("attachment; filename =" + errorFile));
				OutputStream outputStream = response.getOutputStream();
				while ((read = inputStream.read(bytes)) != -1) {
					outputStream.write(bytes, 0, read);
				}
			}
		}

		if (fileType != null && fileType
				.equalsIgnoreCase(DownloadReportsConstant.INFORMATION)) {

			document = DocumentUtility.downloadDocument(infoFile, fileFolder);

			if (document == null) {
				return;
			}

			InputStream inputStream = document.getContentStream().getStream();
			int read = 0;
			byte[] bytes = new byte[1024];

			if (document != null) {
				response.setHeader("Content-Disposition",
						String.format("attachment; filename =" + infoFile));
				OutputStream outputStream = response.getOutputStream();
				while ((read = inputStream.read(bytes)) != -1) {
					outputStream.write(bytes, 0, read);
				}
			}
		}
		
		}

		if (fileType != null && fileType
				.equalsIgnoreCase(DownloadReportsConstant.TOTALERECORDS)) {
			String docId = null;
			
			if(fileStatusentity.isPresent()){
			totalFile = fileStatusentity.get().getFileName();
			}
			if(fileStatusentity.isPresent())
			{
				docId = fileStatusentity.get().getDocId();
				
				LOGGER.debug("docid {} ",docId);
			}
			if (!Strings.isNullOrEmpty(docId)) {
				LOGGER.debug(" Inside doc id");
				document = DocumentUtility.downloadDocumentByDocId(docId);
				
				
			} else {
				document = DocumentUtility.downloadDocument(totalFile,fileFolder);
				
				LOGGER.debug(" Inside else");

			}
			
			LOGGER.debug(" document {} ",document);

			if (document == null) {
				return;
			}

			InputStream inputStream = document.getContentStream().getStream();
			int read = 0;
			byte[] bytes = new byte[1024];

			if (document != null) {
				response.setHeader("Content-Disposition",
						String.format("attachment; filename =" + totalFile));
				OutputStream outputStream = response.getOutputStream();
				while ((read = inputStream.read(bytes)) != -1) {
					outputStream.write(bytes, 0, read);
				}
			}
		}

	}

}
