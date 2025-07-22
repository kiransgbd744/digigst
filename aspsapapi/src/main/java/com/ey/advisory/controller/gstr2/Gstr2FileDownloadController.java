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

import com.ey.advisory.app.data.entities.client.asprecon.Gstr2Recon2BPRAddlReportsEntity;
import com.ey.advisory.app.data.entities.client.asprecon.Gstr2ReconAddlReportsEntity;
import com.ey.advisory.app.data.entities.client.asprecon.Gstr2ReconConfigEntity;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2Recon2BPRAddlReportsRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2ReconAddlReportsRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2ReconConfigRepository;
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
public class Gstr2FileDownloadController {

	@Autowired
	@Qualifier("Gstr2ReconAddlReportsRepository")
	Gstr2ReconAddlReportsRepository addlnRepo;

	@Autowired
	@Qualifier("Gstr2Recon2BPRAddlReportsRepository")
	Gstr2Recon2BPRAddlReportsRepository addln2BPRRepo;

	@Autowired
	@Qualifier("Gstr2ReconConfigRepository")
	Gstr2ReconConfigRepository reconConfigRepo;

	@RequestMapping(value = "/ui/gstr2DownloadDocument", 
			method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public void fileDownloads(@RequestBody String jsonString,
			HttpServletResponse response) throws Exception {

		LOGGER.debug("inside fileDownloads method and file type is {} ",
				"Gstr2ReconReports");

		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();
		JsonObject json = requestObject.get("req").getAsJsonObject();

		String tenantCode = TenantContext.getTenantId();
		LOGGER.debug("Tenant Id Is {}", tenantCode);

		Gstr2ReconConfigEntity configEntity = null;
		String fileName = null;

		String configId = json.get("configId").getAsString();
		String reportType = json.get("reportType").getAsString();
		String reconType = json.get("reconType").getAsString();
		reportType = changeReportName(reportType);

		if (reportType != null && !reportType.isEmpty()) {

			if (reconType != null && !reconType.isEmpty()
					&& reconType.equalsIgnoreCase("2APR")) {

				Gstr2ReconAddlReportsEntity addlnEntity = addlnRepo
						.findByConfigId(Long.valueOf(configId), reportType);
				fileName = addlnEntity != null ? addlnEntity.getFilePath()
						: null;
			} else if (reconType != null && !reconType.isEmpty()
					&& reconType.equalsIgnoreCase("2BPR")) {
				Gstr2Recon2BPRAddlReportsEntity addlnEntity = addln2BPRRepo
						.findByConfigId(Long.valueOf(configId), reportType);
				fileName = addlnEntity != null ? addlnEntity.getFilePath()
						: null;
			} else {

				Gstr2ReconAddlReportsEntity addlnEntity = addlnRepo
						.findByConfigId(Long.valueOf(configId), reportType);
				fileName = addlnEntity != null ? addlnEntity.getFilePath()
						: null;
			}

		} else {
			return;
		}

		if (fileName == null) {
			return;
		}

		String fileFolder = "Gstr2ReconReports";

		Document document = DocumentUtility.downloadDocument(fileName,
				fileFolder);

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
	
	private String changeReportName(String reportName) {

		if (reportName.equalsIgnoreCase("GSTR 2A/6A Time Stamp Report")) {
			reportName = "GSTR_2A_Time_Stamp_Report";

		} else if (reportName.equalsIgnoreCase(
				"CR/DR-Invoice Reference Register-GSTR 2A/6A")) {
			reportName = "CRD-INV_Ref_Reg_GSTR_2A_Records";

		} else if (reportName
				.equalsIgnoreCase("Consolidated PR 2A/6A Report")) {
			reportName = "Consolidated PR 2A_6A Report";

		} else if (reportName.equalsIgnoreCase("Addition in 2A/6A")) {
			reportName = "Addition in 2A_6A";

		} else if (reportName
				.equalsIgnoreCase("Dropped 2A/6A Records Report")) {
			reportName = "Dropped 2A_6A Records Report";

		} else if (reportName
				.equalsIgnoreCase("CR/DR-Invoice Reference Register-PR")) {
			reportName = "CRD-INV_Ref_Reg_PR_Records";

		} else if (reportName.equalsIgnoreCase("Force Match")) {
			reportName = "Force_Match_Records";

		} else if (reportName.equalsIgnoreCase("Consolidated PR Register")) {
			reportName = "Consolidated_PR_Register";

		} else if (reportName
				.equalsIgnoreCase("Summary Report Calendar Period")) {
			reportName = "Summary_CalendarPeriod_Records";

		} else if (reportName.equalsIgnoreCase("Summary Report Tax Period")) {
			reportName = "Summary_TaxPeriod_Record";

		} else if (reportName
				.equalsIgnoreCase("Supplier GSTIN Summary Report")) {
			reportName = "Supplier_GSTIN_Summary_Records";

		}/* else if (reportName
				.equalsIgnoreCase("Supplier GSTIN Summary Report")) {
			reportName = "Supplier_GSTIN_Summary_Records";

		}*/ else if (reportName.equalsIgnoreCase("Supplier PAN Summary Report")) {
			reportName = "Supplier_PAN_Summary_Records";

		} else if (reportName
				.equalsIgnoreCase("Recipient GSTIN Tax Period Wise Report")) {
			reportName = "Recipient_GSTIN_Period_Wise_Record";

		} else if (reportName.equalsIgnoreCase("Recipient GSTIN Wise Report")) {
			reportName = "Recipient_GSTIN_Wise_Records";

		} else if (reportName
				.equalsIgnoreCase("Vendor GSTIN Tax Period Wise Report")) {
			reportName = "Vendor_GSTIN_Period_Wise_Records";

		} else if (reportName.equalsIgnoreCase("Vendor GSTIN Wise Report")) {
			reportName = "Vendor_GSTIN_Wise_Records";

		} else if (reportName
				.equalsIgnoreCase("Vendor PAN Tax Period Wise Report")) {
			reportName = "Vendor_PAN_Period_Wise_Records";

		} else if (reportName.equalsIgnoreCase("Vendor PAN Wise Report")) {
			reportName = "Vendor_PAN_Wise_Records";

		} else if (reportName.equalsIgnoreCase("Reverse Charge Register")) {
			reportName = "Reverse_Charge_Register";

		} else if (reportName
				.equalsIgnoreCase("Locked CFS N Amended Records")) {
			reportName = "Locked_CFS_N_Amended_Records";

		} else if (reportName
				.equalsIgnoreCase("Vendor GSTIN Wise Detailed Report")) {
			reportName = "Vendor_Records_GSTIN";

		} else if (reportName
				.equalsIgnoreCase("Vendor PAN Wise Detailed Report")) {
			reportName = "Vendor_Records_PAN";

		} else if (reportName.equalsIgnoreCase("Dropped PR Records Report")) {
			reportName = "Dropped_PR_Records_Report";

		}
		if (reportName.equalsIgnoreCase("GSTR 2B Time Stamp Report")) {
			reportName = "GSTR_2B_Time_Stamp_Report";
			
		} else if (reportName.equalsIgnoreCase(
				"CR/DR-Invoice Reference Register- GSTR 2B")) {
			reportName = "CRD-INV_Ref_Reg_GSTR_2B_Records";
			
		}  else if (reportName
				.equalsIgnoreCase("Consolidated PR 2BReport")) {
			reportName = "Consolidated PR 2B Report";

		} else if (reportName
				.equalsIgnoreCase("Dropped 2B Records Report")) {
			reportName = "Dropped 2B Records Report";

		}else if (reportName
				.equalsIgnoreCase("Imports SEZG Matching Report")) {
			reportName = "Consolidated IMPG Report";

		}else if (reportName
				.equalsIgnoreCase("Recipient GSTIN Tax Period Wise Report II")) {
			reportName = "Recipient_GSTIN_Period_Wise_Record_II";

		}
		
		return reportName;

	}
}
