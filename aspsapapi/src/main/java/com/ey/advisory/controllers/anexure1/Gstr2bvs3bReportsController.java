package com.ey.advisory.controllers.anexure1;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.aspose.cells.SaveFormat;
import com.aspose.cells.Workbook;
import com.ey.advisory.app.docs.dto.Gstr2AVsGstr3bProcessSummaryReqDto;
import com.ey.advisory.app.search.reports.BasicCommonSecParamRSReports;
import com.ey.advisory.app.services.reports.Gstr2bvs3bReviewSummaryReportHandler;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.ProcessedRecordsCommonSecParam;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.Gstr1VsGstr3bProcessSummaryReqDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Shashikant.Shukla
 *
 * 
 */
@RestController
@Slf4j
public class Gstr2bvs3bReportsController {

	@Autowired
	@Qualifier("Gstr2bvs3bReviewSummaryReportHandler")
	private Gstr2bvs3bReviewSummaryReportHandler gstr2bvs3bReviewSummaryReportHandler;

	@Autowired
	@Qualifier("BasicCommonSecParamRSReports")
	BasicCommonSecParamRSReports basicCommonSecParamRSReports;

	@Autowired
	@Qualifier("ProcessedRecordsCommonSecParam")
	private ProcessedRecordsCommonSecParam processedRecordsCommonSecParam;

	@RequestMapping(value = "/ui/gstr2bvs3bReviewSummaryDownload", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public void downloadreviewSummReport(@RequestBody String jsonString,
			HttpServletResponse response) {

		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();
		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();

		try {
			String fileName = null;
			Workbook workbook = null;
			String date = null;
			String time = null;
			Gstr2AVsGstr3bProcessSummaryReqDto req = gson.fromJson(json,
					Gstr2AVsGstr3bProcessSummaryReqDto.class);

			Gstr1VsGstr3bProcessSummaryReqDto criteria = new Gstr1VsGstr3bProcessSummaryReqDto();
			Map<String, List<String>> gstinmap = new HashMap<>();
			gstinmap.put("GSTIN", req.getGstin());
			criteria.setEntityId(req.getEntityId());
			criteria.setTaxPeriodFrom(req.getFromtaxPeriod());
			criteria.setTaxPeriodTo(req.getTotaxPeriod());
			criteria.setDataSecAttrs(gstinmap);
			criteria.setType(req.getType());
			LocalDateTime now = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			LocalDateTime istDateTimeFromUTC = EYDateUtil
					.toISTDateTimeFromUTC(now);
			DateTimeFormatter FOMATTER = DateTimeFormatter
					.ofPattern("ddMMyyyy");
			DateTimeFormatter FOMATTER1 = DateTimeFormatter.ofPattern("HHmmss");
			date = FOMATTER.format(istDateTimeFromUTC);
			time = FOMATTER1.format(istDateTimeFromUTC);
			workbook = gstr2bvs3bReviewSummaryReportHandler
					.findReviewSummaryData(criteria);
			if (criteria.getType().equalsIgnoreCase("P")) {
				fileName = "EntityLevelSummary_GSTR2BvsGSTR3B_"
						+ criteria.getTaxPeriodFrom() + "_"
						+ criteria.getTaxPeriodTo() + "_" + date + "" + time;
			} else {
				fileName = req.getGstin().get(0) + "_" + "GSTR2BvsGSTR3B_"
						+ criteria.getTaxPeriodFrom() + "_"
						+ criteria.getTaxPeriodTo() + "_" + date + "" + time;
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
