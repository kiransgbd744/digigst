/**
 * 
 */
package com.ey.advisory.controller.gstr2;

import java.io.InputStream;
import java.io.OutputStream;

import org.apache.chemistry.opencmis.client.api.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.data.entities.client.asprecon.Gstr2Recon2APRReportTypeEntity;
import com.ey.advisory.app.data.entities.client.asprecon.Gstr2Recon2BPRAddlReportsEntity;
import com.ey.advisory.app.data.entities.client.asprecon.Gstr2Recon2BPRReportTypeEntity;
import com.ey.advisory.app.data.entities.client.asprecon.Gstr2ReconAddlReportsEntity;
import com.ey.advisory.app.data.entities.client.asprecon.InwardEinvReconChildReportTypeEntity;
import com.ey.advisory.app.data.entities.client.asprecon.InwardEinvoiceReconAddlReportsEntity;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2Recon2APRReportTypeRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2Recon2BPRAddlReportsRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2Recon2BPRReportTypeRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2ReconAddlReportsRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.InwardEinvoiceChildRReportTypeRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.InwardEinvoiceReconAddlReportsRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.multitenancy.TenantContext;
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
public class Gstr2ReconFileDownloadController {
	
	@Autowired
	@Qualifier("Gstr2ReconAddlReportsRepository")
	Gstr2ReconAddlReportsRepository recon2APRConfigRepo;

	@Autowired
	@Qualifier("Gstr2Recon2BPRAddlReportsRepository")
	Gstr2Recon2BPRAddlReportsRepository recon2BPRConfigRepo;

	@Autowired
	@Qualifier("Gstr2Recon2BPRReportTypeRepository")
	Gstr2Recon2BPRReportTypeRepository recon2BPRChildConfigRepo;

	@Autowired
	@Qualifier("Gstr2Recon2APRReportTypeRepository")
	Gstr2Recon2APRReportTypeRepository recon2APRChildConfigRepo;
	
	@Autowired
	@Qualifier("InwardEinvoiceReconAddlReportsRepository")
	InwardEinvoiceReconAddlReportsRepository addlInwardReportRepo;
	
	@Autowired
	private InwardEinvoiceChildRReportTypeRepository reportTypeRepo;
	
	private static String fileFolder = "Gstr2ReconReports";

	@RequestMapping(value = "/ui/gstr2ReconReportDownload", 
			method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public void fileDownloads(@RequestBody String jsonString,
			HttpServletResponse response) throws Exception {

		LOGGER.debug("inside fileDownloads method and file type is {} ",
				"Gstr2ReconFileDownloadController");
		try {

		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();
		JsonObject json = requestObject.get("req").getAsJsonObject();

		String tenantCode = TenantContext.getTenantId();
		LOGGER.debug("Tenant Id Is {}", tenantCode);

		String fileName = json.get("filePath").getAsString();
		
		if (fileName == null) {
			return;
		}
		
			Gstr2Recon2APRReportTypeEntity entity2AChild = 
					recon2APRChildConfigRepo.findByDocId(fileName);
			String docId = entity2AChild != null ? 
					entity2AChild.getDocId() : null;

			if(docId == null) {
			Gstr2ReconAddlReportsEntity entity2A = 
					recon2APRConfigRepo.findByDocId(fileName);
			docId = entity2A != null ? entity2A.getDocId() : null;
			} 

			if(docId == null) {
			Gstr2Recon2BPRReportTypeEntity entity2BChild = 
					recon2BPRChildConfigRepo.findByDocId(fileName);
			docId = entity2BChild != null ? entity2BChild.getDocId() : null;
			}

			if(docId == null) {
			Gstr2Recon2BPRAddlReportsEntity entity2B = 
					recon2BPRConfigRepo.findByDocId(fileName);
			docId = entity2B != null ? entity2B.getDocId() : null;
			}
			
			if(docId == null) {
				InwardEinvReconChildReportTypeEntity entityInvChild = 
						reportTypeRepo.findByDocId(fileName);
			docId = entityInvChild != null ? entityInvChild.getDocId() : null;
			}

			if(docId == null) {
				InwardEinvoiceReconAddlReportsEntity entityInv = 
						addlInwardReportRepo.findByDocId(fileName);
			docId = entityInv != null ? entityInv.getDocId() : null;
			}

			

			Document document = null;
			
			if(docId != null) {
				document = DocumentUtility.downloadDocumentByDocId(docId);
			} else {
				document = DocumentUtility.downloadDocument(fileName,
						fileFolder);
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
	}catch(Exception ex) {
		LOGGER.error("Exception occured - "
				+ "Gstr2ReconFileDownloadController{} ", ex.getMessage());
		throw new AppException(ex);
	}
	} 
	
}
