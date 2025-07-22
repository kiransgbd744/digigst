/**
 * 
 */
package com.ey.advisory.controllers.anexure1;

import java.util.List;

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
import com.ey.advisory.app.docs.dto.Anx1VerticalDownloadReportsReqDto;
import com.ey.advisory.app.downloadreports.DownloadReportsConstant;
import com.ey.advisory.app.services.reports.Ret1And1AVerticalErrorReportHandler;
import com.ey.advisory.app.services.reports.Ret1And1AVerticalProcessedInfoReportHandler;
import com.ey.advisory.app.services.reports.Ret1And1AVerticalProcessedReportHandler;
import com.ey.advisory.app.services.reports.Ret1And1AVerticalTotalReportHandler;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletResponse;

/**
 * @author Sujith.Nanga
 *
 * 
 */

@RestController
public class Ret1And1AVerticalDownloadReportsController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Ret1And1AVerticalDownloadReportsController.class);

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository gstr1FileStatusRepository;

	@Autowired
	@Qualifier("Ret1And1AVerticalProcessedReportHandler")
	private Ret1And1AVerticalProcessedReportHandler ret1And1AVerticalProcessedReportHandler;

	@Autowired
	@Qualifier("Ret1And1AVerticalProcessedInfoReportHandler")
	private Ret1And1AVerticalProcessedInfoReportHandler ret1And1AVerticalProcessedInfoReportHandler;

	@Autowired
	@Qualifier("Ret1And1AVerticalErrorReportHandler")
	private Ret1And1AVerticalErrorReportHandler ret1And1AVerticalErrorReportHandler;

	@Autowired
	@Qualifier("Ret1And1AVerticalTotalReportHandler")
	private Ret1And1AVerticalTotalReportHandler ret1And1AVerticalTotalReportHandler;

	@RequestMapping(value = "/ui/downloadRet1And1AVerticalReports", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public void downloadGstr1VerticalReport(@RequestBody String jsonString,
			HttpServletResponse response) throws Exception {

		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();
		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();

		try {
			Anx1VerticalDownloadReportsReqDto criteria = gson.fromJson(json,
					Anx1VerticalDownloadReportsReqDto.class);

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
					.equalsIgnoreCase(DownloadReportsConstant.RET)))
					&& (criteria.getFileType() != null && (criteria
							.getFileType()
							.equalsIgnoreCase(DownloadReportsConstant.RET1AND1A)
							|| criteria.getFileType().equalsIgnoreCase(
									DownloadReportsConstant.INTEREST)
							|| criteria.getFileType().equalsIgnoreCase(
									DownloadReportsConstant.SETOFFANDUTIL)
							|| criteria.getFileType().equalsIgnoreCase(
									DownloadReportsConstant.REFUNDS)))) {

				if (criteria.getType() != null && !criteria.getType().isEmpty()
						&& criteria.getType().equalsIgnoreCase(
								DownloadReportsConstant.PROCESSED)) {
					workbook = ret1And1AVerticalProcessedReportHandler
							.downloadRet1VerticalProcessedReport(criteria);
					if (criteria.getFileType() != null
							&& criteria.getFileType().equalsIgnoreCase(
									DownloadReportsConstant.RET1AND1A)) {
						fileName = "Ret1And1AUserInputsProcessedRecords";
					} else if (criteria.getFileType() != null
							&& criteria.getFileType().equalsIgnoreCase(
									DownloadReportsConstant.INTEREST)) {
						fileName = "Ret1And1AInterestAndLateFeeProcessedRecords";
					} else if (criteria.getFileType() != null
							&& criteria.getFileType().equalsIgnoreCase(
									DownloadReportsConstant.SETOFFANDUTIL)) {
						fileName = "Ret1And1ASetoffUtilizationProcessedRecords";
					} else if (criteria.getFileType() != null
							&& criteria.getFileType().equalsIgnoreCase(
									DownloadReportsConstant.REFUNDS)) {
						fileName = "Ret1And1ARefundsProcessedRecords";
					}
				}

				if (criteria.getType() != null && !criteria.getType().isEmpty()
						&& DownloadReportsConstant.PROCESSEDINFO
								.equalsIgnoreCase(criteria.getType().trim())) {
					workbook = ret1And1AVerticalProcessedInfoReportHandler
							.downloadRet1VerticalProcessedInfoReport(criteria);
					if (criteria.getFileType() != null
							&& criteria.getFileType().equalsIgnoreCase(
									DownloadReportsConstant.RET1AND1A)) {
						fileName = "Ret1And1AUserInputsProcessedInfoRecords";
					} else if (criteria.getFileType() != null
							&& criteria.getFileType().equalsIgnoreCase(
									DownloadReportsConstant.INTEREST)) {
						fileName = "Ret1And1AInterestAndLateFeeProcessedInfoRecords";
					} else if (criteria.getFileType() != null
							&& criteria.getFileType().equalsIgnoreCase(
									DownloadReportsConstant.SETOFFANDUTIL)) {
						fileName = "Ret1And1ASetoffUtilizationProcessedInfoRecords";
					} else if (criteria.getFileType() != null
							&& criteria.getFileType().equalsIgnoreCase(
									DownloadReportsConstant.REFUNDS)) {
						fileName = "Ret1And1ARefundsProcessedInfoRecords";
					}
				}
				if (criteria.getType() != null && !criteria.getType().isEmpty()
						&& criteria.getType().equalsIgnoreCase(
								DownloadReportsConstant.ERROR)) {
					workbook = ret1And1AVerticalErrorReportHandler
							.downloadRet1VerticalErrorReport(criteria);
					if (criteria.getFileType() != null
							&& criteria.getFileType().equalsIgnoreCase(
									DownloadReportsConstant.RET1AND1A)) {
						fileName = "Ret1And1AUserInputsErrorRecords";
					} else if (criteria.getFileType() != null
							&& criteria.getFileType().equalsIgnoreCase(
									DownloadReportsConstant.INTEREST)) {
						fileName = "Ret1And1AInterestAndLateFeeErrorRecords";
					} else if (criteria.getFileType() != null
							&& criteria.getFileType().equalsIgnoreCase(
									DownloadReportsConstant.SETOFFANDUTIL)) {
						fileName = "Ret1And1ASetoffUtilizationErrorRecords";
					} else if (criteria.getFileType() != null
							&& criteria.getFileType().equalsIgnoreCase(
									DownloadReportsConstant.REFUNDS)) {
						fileName = "Ret1And1ARefundsErrorRecords";
					}
				}

				if (criteria.getType() != null && !criteria.getType().isEmpty()
						&& criteria.getType().equalsIgnoreCase(
								DownloadReportsConstant.TOTALERECORDS)) {
					workbook = ret1And1AVerticalTotalReportHandler
							.downloadRet1VerticalTotalReport(criteria);
					if (criteria.getFileType() != null
							&& criteria.getFileType().equalsIgnoreCase(
									DownloadReportsConstant.RET1AND1A)) {
						fileName = "Ret1And1AUserInputsTotalRecords";
					} else if (criteria.getFileType() != null
							&& criteria.getFileType().equalsIgnoreCase(
									DownloadReportsConstant.INTEREST)) {
						fileName = "Ret1And1AInterestAndLateFeeTotalRecords";
					} else if (criteria.getFileType() != null
							&& criteria.getFileType().equalsIgnoreCase(
									DownloadReportsConstant.SETOFFANDUTIL)) {
						fileName = "Ret1And1ASetoffUtilizationTotalRecords";
					} else if (criteria.getFileType() != null
							&& criteria.getFileType().equalsIgnoreCase(
									DownloadReportsConstant.REFUNDS)) {
						fileName = "Ret1And1ARefundsTotalRecords";
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
					+ "Data from Report ";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
		}

	}

}
