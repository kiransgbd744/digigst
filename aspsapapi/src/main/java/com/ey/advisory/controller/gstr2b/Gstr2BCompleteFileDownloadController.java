package com.ey.advisory.controller.gstr2b;

import java.io.InputStream;
import java.io.OutputStream;

import org.apache.chemistry.opencmis.client.api.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.entities.client.GstnGetStatusEntity;
import com.ey.advisory.app.data.repositories.client.GstinApiCallRepository;
import com.ey.advisory.app.data.repositories.client.Gstr2bGet2bRequestStatusRepository;
import com.ey.advisory.app.gstr2b.Gstr2bGet2bRequestStatusEntity;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.config.ConfigConstants;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */

@RestController
@Slf4j
public class Gstr2BCompleteFileDownloadController {

	@Autowired
	@Qualifier("Gstr2bGet2bRequestStatusRepository")
	Gstr2bGet2bRequestStatusRepository reqStatusRepo;

	@Autowired
	@Qualifier("GstinApiCallRepository")
	private GstinApiCallRepository gstnStatusRepo;

	@GetMapping(value = "/ui/gstr2BDownloadFile")
	public void fileDownloads(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		LOGGER.debug("inside fileDownloads method and file type is {} ",
				"Gstr2BCompleteReport");

		String docId = null;

		Document document = null;

		String fileFolder = ConfigConstants.GSTR2BREPORTS;

		String tenantCode = TenantContext.getTenantId();
		LOGGER.debug("Tenant Id Is {}", tenantCode);

		String fileName = request.getParameter("filePath");
		if (fileName == null) {
			return;
		}

		Gstr2bGet2bRequestStatusEntity reqStatusEntity = reqStatusRepo
				.findByFilePath(fileName);

		if (reqStatusEntity != null) {
			docId = reqStatusEntity.getDocId();

		} else {

			GstnGetStatusEntity gstnStatus = gstnStatusRepo
					.findByCsvGenPath(fileName);
			if (gstnStatus != null) {
				docId = gstnStatus.getDocId();
			}
		}

		if (docId != null) {
			document = DocumentUtility.downloadDocumentByDocId(docId);
		} else {
			document = DocumentUtility.downloadDocument(fileName, fileFolder);
		}

		if (document == null) {
			return;
		}

		InputStream inputStream = document.getContentStream().getStream();
		int read = 0;
		byte[] bytes = new byte[1024];

		if (document != null) {
			response.setHeader("Content-Disposition",
					String.format("attachment; filename =" + fileName));
			OutputStream outputStream = response.getOutputStream();
			while ((read = inputStream.read(bytes)) != -1) {
				outputStream.write(bytes, 0, read);
			}
		}
	}
}
