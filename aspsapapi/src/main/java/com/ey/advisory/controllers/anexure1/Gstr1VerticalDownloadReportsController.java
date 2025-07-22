/**
 * 
 */
package com.ey.advisory.controllers.anexure1;

import java.util.List;

import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.aspose.cells.SaveFormat;
import com.aspose.cells.Workbook;
import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.app.docs.dto.Gstr1VerticalDownloadReportsReqDto;
import com.ey.advisory.app.downloadreports.DownloadReportsConstant;
import com.ey.advisory.app.services.reports.Gstr1VerticalErrorReportHandler;
import com.ey.advisory.app.services.reports.Gstr1VerticalProcessedInfoReportHandler;
import com.ey.advisory.app.services.reports.Gstr1VerticalProcessedReportHandler;
import com.ey.advisory.app.services.reports.Gstr1VerticalTotalReportHandler;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

/**
 * @author Laxmi.Salukuti
 *
 */
@RestController
public class Gstr1VerticalDownloadReportsController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr1VerticalDownloadReportsController.class);

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository gstr1FileStatusRepository;

	@Autowired
	@Qualifier("Gstr1VerticalErrorReportHandler")
	private Gstr1VerticalErrorReportHandler gstr1VerticalErrorReportHandler;

	@Autowired
	@Qualifier("Gstr1VerticalProcessedReportHandler")
	private Gstr1VerticalProcessedReportHandler gstr1VerticalProcessedReportHandler;

	@Autowired
	@Qualifier("Gstr1VerticalProcessedInfoReportHandler")
	private Gstr1VerticalProcessedInfoReportHandler gstr1VerticalProcessedInfoReportHandler;

	@Autowired
	@Qualifier("Gstr1VerticalTotalReportHandler")
	private Gstr1VerticalTotalReportHandler gstr1VerticalTotalReportHandler;

	@RequestMapping(value = "/ui/downloadGstr1VerticalReports", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public void downloadGstr1VerticalReport(@RequestBody String jsonString,
			HttpServletResponse response) throws Exception {

		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();
		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();

		try {
			Gstr1VerticalDownloadReportsReqDto criteria = gson.fromJson(json,
					Gstr1VerticalDownloadReportsReqDto.class);

			if (null != criteria.getFileId()) {
				if ((criteria.getFileType() == null
						|| criteria.getFileType().isEmpty())
						&& (criteria.getDataType() == null
								|| criteria.getDataType().isEmpty())) {
					List<Object[]> objects = gstr1FileStatusRepository
							.getTypes(criteria.getFileId());

					objects.forEach(object -> {

						String filetype = (String) object[0];
						String datatype = (String) object[1];

						criteria.setFileType(filetype);
						criteria.setDataType(datatype);

					});
				}
			}

			String fileName = null;
			Workbook workbook = null;

			if ((criteria.getDataType() != null && (criteria.getDataType()
					.equalsIgnoreCase(DownloadReportsConstant.GSTR1)))
					|| (criteria.getDataType() != null
							&& (criteria.getDataType()
									.equalsIgnoreCase("GSTR1A")))
							&& (criteria.getFileType() != null && (criteria
									.getFileType()
									.equalsIgnoreCase(
											DownloadReportsConstant.B2CS)
									|| criteria.getFileType().equalsIgnoreCase(
											DownloadReportsConstant.ADVANCERECEIVED)
									|| criteria.getFileType().equalsIgnoreCase(
											DownloadReportsConstant.ADVANCEADJUSTMENT)
									|| criteria.getFileType().equalsIgnoreCase(
											DownloadReportsConstant.INVOICE)))) {

				if (criteria.getType() != null && !criteria.getType().isEmpty()
						&& criteria.getType().equalsIgnoreCase(
								DownloadReportsConstant.ERROR)) {
					workbook = gstr1VerticalErrorReportHandler
							.downloadGstr1VerticalErrorReport(criteria);
					if (criteria.getFileType() != null && criteria.getFileType()
							.equalsIgnoreCase(DownloadReportsConstant.B2CS)) {
						if (criteria.getDataType()
								.equalsIgnoreCase("GSTR1A"))
							fileName = "Gstr1A_VerticalB2CSErrorRecords";
						else
							fileName = "Gstr1_VerticalB2CSErrorRecords";

					} else if (criteria.getFileType() != null
							&& criteria.getFileType().equalsIgnoreCase(
									DownloadReportsConstant.ADVANCERECEIVED)) {
						if (criteria.getDataType()
								.equalsIgnoreCase("GSTR1A"))
							fileName = "Gstr1A_VerticalAdvRecErrorRecords";
						else
							fileName = "Gstr1_VerticalAdvRecErrorRecords";
					} else if (criteria.getFileType() != null
							&& criteria.getFileType().equalsIgnoreCase(
									DownloadReportsConstant.ADVANCEADJUSTMENT)) {
						if (criteria.getDataType()
								.equalsIgnoreCase("GSTR1A"))
							fileName = "Gstr1A_VerticalAdvAdjErrorRecords";
						else
							fileName = "Gstr1_VerticalAdvAdjErrorRecords";
					} else if (criteria.getFileType() != null
							&& criteria.getFileType().equalsIgnoreCase(
									DownloadReportsConstant.INVOICE)) {
						if (criteria.getDataType()
								.equalsIgnoreCase("GSTR1A"))
							fileName = "Gstr1A_VerticalInvoiceErrorRecords";
						else
							fileName = "Gstr1_VerticalInvoiceErrorRecords";
					}
				}

				if (criteria.getType() != null && !criteria.getType().isEmpty()
						&& DownloadReportsConstant.PROCESSED
								.equalsIgnoreCase(criteria.getType().trim())) {
					workbook = gstr1VerticalProcessedReportHandler
							.downloadGstr1VerticalProcessedReport(criteria);
					if (criteria.getFileType() != null && criteria.getFileType()
							.equalsIgnoreCase(DownloadReportsConstant.B2CS)) {
						if (!criteria.getDataType()
								.equalsIgnoreCase("GSTR1A"))
							fileName = "Gstr1_VerticalB2CSProcessRecords";
						else
							fileName = "Gstr1A_VerticalB2CSProcessRecords";
					} else if (criteria.getFileType() != null
							&& DownloadReportsConstant.ADVANCERECEIVED
									.equalsIgnoreCase(criteria.getFileType())) {
						if (!criteria.getDataType()
								.equalsIgnoreCase("GSTR1A"))
							fileName = "Gstr1_VerticalAdvRecProcessRecords";
						else
							fileName = "Gstr1A_VerticalAdvRecProcessRecords";
					} else if (criteria.getFileType() != null
							&& DownloadReportsConstant.ADVANCEADJUSTMENT
									.equalsIgnoreCase(criteria.getFileType())) {
						if (!criteria.getDataType()
								.equalsIgnoreCase("GSTR1A"))
							fileName = "Gstr1_VerticalAdvAdjProcessRecords";
						else
							fileName = "Gstr1A_VerticalAdvAdjProcessRecords";
					} else if (criteria.getFileType() != null
							&& DownloadReportsConstant.INVOICE
									.equalsIgnoreCase(criteria.getFileType())) {
						if (!criteria.getDataType()
								.equalsIgnoreCase("GSTR1A"))
							fileName = "Gstr1_VerticalInvoiceProcessRecords";
						else
							fileName = "Gstr1A_VerticalAdvAdjProcessRecords";
					}
				}

				if (criteria.getType() != null && !criteria.getType().isEmpty()
						&& criteria.getType().equalsIgnoreCase(
								DownloadReportsConstant.PROCESSEDINFO)) {
					workbook = gstr1VerticalProcessedInfoReportHandler
							.downloadGstr1VerticalProcessedInfoReport(criteria);
					if (criteria.getFileType() != null && criteria.getFileType()
							.equalsIgnoreCase(DownloadReportsConstant.B2CS)) {
						if (!criteria.getDataType()
								.equalsIgnoreCase("GSTR1A"))
							fileName = "Gstr1_VerticalB2CSProcessedInfoRecords";
						else
							fileName = "Gstr1A_VerticalB2CSProcessedInfoRecords";
					} else if (criteria.getFileType() != null
							&& criteria.getFileType().equalsIgnoreCase(
									DownloadReportsConstant.ADVANCERECEIVED)) {
						if (!criteria.getDataType()
								.equalsIgnoreCase("GSTR1A"))
							fileName = "Gstr1_VerticalAdvRecProcessedInfoRecords";
						else
							fileName = "Gstr1A_VerticalAdvRecProcessedInfoRecords";
					} else if (criteria.getFileType() != null
							&& criteria.getFileType().equalsIgnoreCase(
									DownloadReportsConstant.ADVANCEADJUSTMENT)) {
						if (!criteria.getDataType()
								.equalsIgnoreCase("GSTR1A"))
							fileName = "Gstr1_VerticalAdvAdjProcessedInfoRecords";
						else
							fileName = "Gstr1A_VerticalAdvAdjProcessedInfoRecords";
					}
				}

				if (criteria.getType() != null && !criteria.getType().isEmpty()
						&& criteria.getType().equalsIgnoreCase(
								DownloadReportsConstant.TOTALERECORDS)) {
					workbook = gstr1VerticalTotalReportHandler
							.downloadGstr1VerticalTotalReport(criteria);
					if (criteria.getFileType() != null && criteria.getFileType()
							.equalsIgnoreCase(DownloadReportsConstant.B2CS)) {
						if (!criteria.getDataType()
								.equalsIgnoreCase("GSTR1A"))
						fileName = "Gstr1_VerticalB2CSTotalRecords";
						else
							fileName = "Gstr1A_VerticalB2CSTotalRecords";
					} else if (criteria.getFileType() != null
							&& criteria.getFileType().equalsIgnoreCase(
									DownloadReportsConstant.ADVANCERECEIVED)) {
						if (!criteria.getDataType()
								.equalsIgnoreCase("GSTR1A"))
						fileName = "Gstr1_VerticalAdvRecTotalRecords";
						else
							fileName = "Gstr1A_VerticalAdvRecTotalRecords";
					} else if (criteria.getFileType() != null
							&& criteria.getFileType().equalsIgnoreCase(
									DownloadReportsConstant.ADVANCEADJUSTMENT)) {
						if (!criteria.getDataType()
								.equalsIgnoreCase("GSTR1A"))
						fileName = "Gstr1_VerticalAdvAdjTotalRecords";
						else
							fileName = "Gstr1A_VerticalAdvAdjTotalRecords";
					} else if (criteria.getFileType() != null
							&& criteria.getFileType().equalsIgnoreCase(
									DownloadReportsConstant.INVOICE)) {
						if (!criteria.getDataType()
								.equalsIgnoreCase("GSTR1A"))
						fileName = "Gstr1_VerticalInvoiceTotalRecords";
						else
							fileName = "Gstr1a_VerticalInvoiceTotalRecords";
					}
				}

			} else {
				LOGGER.error("Invalid fileId");
				throw new Exception("Invalid fileId");
			}

			if (workbook == null) {
				workbook = new Workbook();
			}
			if (fileName != null) {
				response.setContentType("APPLICATION/OCTET-STREAM");
				response.setHeader("Content-Disposition", String
						.format("attachment; filename=" + fileName + ".xlsx"));
				workbook.save(response.getOutputStream(), SaveFormat.XLSX);
				response.getOutputStream().flush();
			}
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

		} catch (Exception ex) {
			String msg = "Unexpected error while retriving "
					+ "GSTN Save and Submit Documents ";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
		}

	}

}
