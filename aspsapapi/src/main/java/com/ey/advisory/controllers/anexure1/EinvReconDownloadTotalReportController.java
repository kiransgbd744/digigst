package com.ey.advisory.controllers.anexure1;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Optional;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.app.util.Gstr1FileUploadUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.google.common.base.Strings;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Saif.S
 *
 */

@RestController
@Slf4j
public class EinvReconDownloadTotalReportController {

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository gstr1FileStatusRepository;

	@Autowired
	@Qualifier("Gstr1FileUploadUtil")
	private Gstr1FileUploadUtil gstr1FileUploadUtil;

	@GetMapping(value = "/ui/downloadEinvReconTotalReport")
	public void downloadEinvReconTotalReport(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Long fileId = Long.valueOf(request.getParameter("fileId"));

		downloadTotalEinvReconReport(response, fileId);

	}

	private void downloadTotalEinvReconReport(HttpServletResponse response,
			Long fileId) throws Exception {
		
		String tenantCode = TenantContext.getTenantId();
		LOGGER.debug("Tenant Id Is {}", tenantCode);

		InputStream inputStream = null;
		String fileName = null;

		Optional<Gstr1FileStatusEntity> gstr1FileStatusEntity = gstr1FileStatusRepository
				.findById(fileId);

		if(gstr1FileStatusEntity.isPresent()){
		 fileName = gstr1FileStatusEntity.get().getFileName();
		}
		String folderName = GSTConstants.EINVOICE_RECON_FOLDER;
		if (!gstr1FileStatusEntity.isPresent()) {
			String errMsg = String.format(
					"No Record available for the file Name %s", fileName);
			LOGGER.error(errMsg);
			throw new AppException(errMsg);
		}
		Document document = null;
		String docId = gstr1FileStatusEntity.get().getDocId();
		try {
			Session openCmisSession = gstr1FileUploadUtil.getCmisSession();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("openCmisSession:{} ", openCmisSession);
			}
			if (Strings.isNullOrEmpty(docId)) {
				document = gstr1FileUploadUtil.getDocument(openCmisSession,
						fileName, folderName);
			} else {
				document = DocumentUtility.downloadDocumentByDocId(docId);

			}
			if (document == null) {
				LOGGER.debug("Document is not available in repo");
				throw new AppException("Document is not available in repo ");

			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("document name:{}", document.getName());
			}
			inputStream = document.getContentStream().getStream();
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
		} catch (Exception e) {
			String msg = String.format("Exception occured while Downloading total file of Einvoice Recon for %s and %s",
					docId,fileName);
			LOGGER.error(msg, e);
			throw new AppException(msg,e);
		}
		
	}

}
