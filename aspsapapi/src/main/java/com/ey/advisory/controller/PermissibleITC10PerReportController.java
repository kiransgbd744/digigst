package com.ey.advisory.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.aspose.cells.SaveFormat;
import com.aspose.cells.Workbook;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.docs.dto.PermissibleITC10PercentReqDto;
import com.ey.advisory.app.services.docs.PermissibleITC10PerctDwnldService;
import com.ey.advisory.app.util.DataSecurityAttributeUtil;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.config.ConfigConstants;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Rajesh N K
 *
 */
@Slf4j
@RestController
public class PermissibleITC10PerReportController {

	@Autowired
	private GSTNDetailRepository gstinDetailRepo;

	@Autowired
	@Qualifier("PermissibleITC10PerctDwnldServiceImpl")
	private PermissibleITC10PerctDwnldService permissibleITCService;

	@PostMapping(value = "/ui/permissibleITC10pReport", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public void permissibleDownloadReport(HttpServletResponse response,
			@RequestBody String jsonString) throws Exception {

		JsonObject errorResp = new JsonObject();

		Workbook workBook = null;

		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();

		JsonObject json = requestObject.get("req").getAsJsonObject();

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Request Json {}", json);
		}
		Gson gson = GsonUtil.newSAPGsonInstance();

		List<String> gstnsLists = null;

		try {

			PermissibleITC10PercentReqDto itcReqDto = gson.fromJson(json,
					PermissibleITC10PercentReqDto.class);

			Long entityId = itcReqDto.getEntityId();

			String toTaxPeriod = itcReqDto.getToTaxPeriod();

			String fromTaxPeriod = itcReqDto.getFromTaxPeriod();

			List<String> docType = itcReqDto.getDocType();

			String reconType = itcReqDto.getReconType();

			List<String> gstinList = itcReqDto.getGstinList();

			List<Long> entityIds = new ArrayList<>();
			entityIds.add(entityId);

			if (gstinList != null && !gstinList.isEmpty()) {

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"As gstins are provided in request, not Invoking"
									+ "service to get gstins for entity");
				}
				gstnsLists = gstinList;
			} else {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Invoking service to get gstins for entity");
				}
				Map<String, String> outwardSecurityAttributeMap = DataSecurityAttributeUtil
						.getOutwardSecurityAttributeMap();
				Map<String, List<String>> dataSecAttrs = DataSecurityAttributeUtil
						.dataSecurityAttrMapForQuery(entityIds,
								outwardSecurityAttributeMap);
				List<String> dataSecList = dataSecAttrs
						.get(OnboardingConstant.GSTIN);

				gstnsLists = gstinDetailRepo
						.getRegularandSezGstins(dataSecList);

			}
			if (gstnsLists == null || gstnsLists.isEmpty()) {
				String msg = "Gstins cannot be empty";
				errorResp.add("hdr",
						new Gson().toJsonTree(new APIRespDto("E", msg)));
				LOGGER.error(msg);
			}

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						" permissibleDownloadReport method{} entityId %d,  "
								+ " gstinList %s : ",
						gstnsLists);
			}

			workBook = permissibleITCService.getPermissibleReport(gstnsLists,
					toTaxPeriod, fromTaxPeriod, docType, reconType,entityId);

			String fileName = DocumentUtility
					.getUniqueFileName(ConfigConstants.PERMISSIBLE_ITC_10_PERC);

			if (workBook != null) {
				response.setContentType("APPLICATION/OCTET-STREAM");
				response.setHeader("Content-Disposition",
						String.format("attachment; filename=" + fileName));
				workBook.save(response.getOutputStream(), SaveFormat.XLSX);
				response.getOutputStream().flush();

			}

		} catch (Exception ex) {
			String msg = "Error occured while generating reconSummary report ";
			LOGGER.error(msg, ex);
			errorResp.add("hdr",
					new Gson().toJsonTree(new APIRespDto("E", msg)));
			response.flushBuffer();

		}
	}
}
