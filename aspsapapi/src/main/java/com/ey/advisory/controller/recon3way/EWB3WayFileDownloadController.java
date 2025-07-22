package com.ey.advisory.controller.recon3way;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Optional;

import org.apache.chemistry.opencmis.client.api.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.data.entities.client.asprecon.EWB3WayReconDownloadReportsEntity;
import com.ey.advisory.app.data.repositories.client.asprecon.EWB3WayDownloadReconReportsRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.multitenancy.TenantContext;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */

@RestController
@Slf4j
public class EWB3WayFileDownloadController {

	@Autowired
	@Qualifier("EWB3WayDownloadReconReportsRepository")
	EWB3WayDownloadReconReportsRepository reconDownlRepo;

	@GetMapping(value = "/ui/eWB3WayDownloadDocument")
	public void eWB3WayDownloadDocument(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		LOGGER.debug(
				"inside eWB3WayDownloadDocument method and file type is {} ",
				"3WayReconReport");

		String tenantCode = TenantContext.getTenantId();
		LOGGER.debug("Tenant Id Is {}", tenantCode);

		String configId = request.getParameter("configId");
		String reportType = request.getParameter("reportType");
		
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					" Downloading report with configId  %s"
							+ " and Report Type  %s and request is %s",configId,
							reportType, request);
			LOGGER.debug(msg);
		}

		Optional<EWB3WayReconDownloadReportsEntity> configEntity = reconDownlRepo
				.findByConfigIdAndReportType(Long.valueOf(configId),
						reportType);

		if (!configEntity.isPresent()) {
			String msg = "No records found";
			throw new AppException(msg);
		}
		String fileName = configEntity.get().getPath();
		String docId = configEntity.get().getDocId();
		String fileFolder = "3WayReconReport";

		/*Document document = DocumentUtility.downloadDocument(fileName,
				fileFolder);*/
		Document document=null;
		if (docId != null && !docId.isEmpty()) {
			 
			String msg = String.format(
					"Doc Id is available for File Name %s and Doc Id %s",
					fileName, docId);
			LOGGER.debug(msg);
			document = DocumentUtility
					.downloadDocumentByDocId(docId);

		} else {

			document = DocumentUtility.downloadDocument(fileName,
					fileFolder);
			if (fileName == null ) {
				throw new AppException("FileName is null");

			}
		}

		if (document == null) {
			return;
		}

		InputStream inputStream = document.getContentStream().getStream();
		int read = 0;
		byte[] bytes = new byte[1024];

		if (document != null) {
			response.setHeader("Content-Disposition",
					String.format("attachment; filename = " + fileName));
			OutputStream outputStream = response.getOutputStream();
			while ((read = inputStream.read(bytes)) != -1) {
				outputStream.write(bytes, 0, read);
			}
		}

	}

}
