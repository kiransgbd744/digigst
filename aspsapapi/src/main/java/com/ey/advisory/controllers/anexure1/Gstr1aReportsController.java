/**
 * 
 */
package com.ey.advisory.controllers.anexure1;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import com.ey.advisory.app.docs.dto.GstnConsolidatedReqDto;
import com.ey.advisory.app.services.reports.Gstr1aGetRateReportHandler;
import com.ey.advisory.app.services.reports.Gstr1aGetReportHandler;
import com.ey.advisory.common.BasicGstr6SecCommonParam;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.service.GstnApi;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
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
public class Gstr1aReportsController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr1aReportsController.class);

	@Autowired
	@Qualifier("BasicGstr6SecCommonParam")
	BasicGstr6SecCommonParam basicGstr6SecCommonParam;

	@Autowired
	@Qualifier("Gstr1aGetReportHandler")
	private Gstr1aGetReportHandler gstr1aGetReportHandler;

	@Autowired
	@Qualifier("Gstr1aGetRateReportHandler")
	private Gstr1aGetRateReportHandler gstr1aGetRateReportHandler;

	@Autowired
	private GstnApi gstnapi;

	@RequestMapping(value = "/ui/gstr1aGetDownloads", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public void downloadreviewSummReport(@RequestBody String jsonString,
			HttpServletResponse response) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonArray jsonReqArray = (new JsonParser()).parse(jsonString)
				.getAsJsonObject().get("req").getAsJsonArray();
		@SuppressWarnings("serial")
		Type listType = new TypeToken<List<GstnConsolidatedReqDto>>() {
		}.getType();

		List<GstnConsolidatedReqDto> reqDtoCriterias = gson
				.fromJson(jsonReqArray, listType);

		try {
			String fileName = null;
			Workbook workbook = null;
			String returnType = null;
			String returnTypeStr = null;
			if (reqDtoCriterias.get(0).getReturnType()!=null && "GSTR1A".equalsIgnoreCase(reqDtoCriterias.get(0).getReturnType()))

			{
				returnType = "GSTR1A";
				returnTypeStr = "GSTR-1A";
			} else {
				returnType = "GSTR1";
				returnTypeStr = "GSTR-1";
			}
			
			LOGGER.debug(" returnType {} ,returnTypeStr {} ",returnType,returnTypeStr);

			if (gstnapi.isDelinkingEligible(returnType)) {
				workbook = gstr1aGetReportHandler
						.findGstnErrorReports(reqDtoCriterias,returnType);
				/*
				 * else { // Write new handler to fetch the limited columns that
				 * is // required in report workbook = gstr1aGetReportHandler
				 * .downloadGstr6SaveSectionsReport(reqDtos); }
				 */
				Set<String> gstinset = new HashSet<>();
				reqDtoCriterias.forEach(dto -> gstinset.add(dto.getGstin()));
				String date = null;
				String time = null;
				LocalDateTime now = EYDateUtil
						.toUTCDateTimeFromLocal(LocalDateTime.now());

				LocalDateTime istDateTimeFromUTC = EYDateUtil
						.toISTDateTimeFromUTC(now);
				DateTimeFormatter FOMATTER = DateTimeFormatter
						.ofPattern("ddMMyyyy");

				DateTimeFormatter FOMATTER1 = DateTimeFormatter
						.ofPattern("HHmmss");

				date = FOMATTER.format(istDateTimeFromUTC);
				time = FOMATTER1.format(istDateTimeFromUTC);

				fileName = returnTypeStr+"_GetDetails_" + "_" + date + "" + time;

				if (gstinset.size() > 1) {
					fileName = returnTypeStr+"_GetDetails_" + "_" + date + "" + time;
				} else {
					if (reqDtoCriterias.get(0).getGstr1aSections() == null
							|| reqDtoCriterias.get(0).getGstr1aSections()
									.isEmpty()
							|| reqDtoCriterias.get(0).getGstr1aSections()
									.size() > 1) {
						fileName = returnTypeStr+"_" + reqDtoCriterias.get(0).getGstin()
								+ "_" + reqDtoCriterias.get(0).getTaxPeriod();
					} else {
						/*
						 * fileName = "GSTR-1_" +
						 * reqDtoCriterias.get(0).getGstr1aSections()+ "_" +
						 * reqDtoCriterias.get(0).getTaxPeriod();
						 */
						fileName = returnTypeStr+"_"
								+ reqDtoCriterias.get(0).getGstr1aSections()
										.get(0)
								+ "_" + reqDtoCriterias.get(0).getTaxPeriod();

					}

				}
			}

			Boolean rateIncludedInHsn = gstnapi
					.isRateIncludedInHsn(reqDtoCriterias.get(0).getTaxPeriod());
			if (rateIncludedInHsn) {
				workbook = gstr1aGetRateReportHandler.findRate(reqDtoCriterias,returnType);
				/*
				 * else { // Write new handler to fetch the limited columns that
				 * is // required in report workbook = gstr1aGetReportHandler
				 * .downloadGstr6SaveSectionsReport(reqDtos); }
				 */
				Set<String> gstinset = new HashSet<>();
				reqDtoCriterias.forEach(dto -> gstinset.add(dto.getGstin()));
				String date = null;
				String time = null;
				LocalDateTime now = EYDateUtil
						.toUTCDateTimeFromLocal(LocalDateTime.now());

				LocalDateTime istDateTimeFromUTC = EYDateUtil
						.toISTDateTimeFromUTC(now);
				DateTimeFormatter FOMATTER = DateTimeFormatter
						.ofPattern("ddMMyyyy");

				DateTimeFormatter FOMATTER1 = DateTimeFormatter
						.ofPattern("HHmmss");

				date = FOMATTER.format(istDateTimeFromUTC);
				time = FOMATTER1.format(istDateTimeFromUTC);

				fileName = returnTypeStr+"_GetDetails_" + "_" + date + "" + time;

				if (gstinset.size() > 1) {
					fileName = returnTypeStr+"_GetDetails_" + "_" + date + "" + time;
				} else {
					if (reqDtoCriterias.get(0).getGstr1aSections() == null
							|| reqDtoCriterias.get(0).getGstr1aSections()
									.isEmpty()
							|| reqDtoCriterias.get(0).getGstr1aSections()
									.size() > 1) {
						fileName = returnTypeStr+"_" + reqDtoCriterias.get(0).getGstin()
								+ "_" + reqDtoCriterias.get(0).getTaxPeriod();
					} else {
						/*
						 * fileName = "GSTR-1_" +
						 * reqDtoCriterias.get(0).getGstr1aSections()+ "_" +
						 * reqDtoCriterias.get(0).getTaxPeriod();
						 */
						fileName = returnTypeStr+"_"
								+ reqDtoCriterias.get(0).getGstr1aSections()
										.get(0)
								+ "_" + reqDtoCriterias.get(0).getTaxPeriod();

					}

				}
			}

			/*
			 * if (workbook == null) { workbook = new Workbook(); }
			 */
			if (fileName != null && workbook != null) {
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
