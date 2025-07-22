package com.ey.advisory.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.app.data.entities.client.FileStatusDownloadReportEntity;
import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.app.docs.dto.FileStatusReportDto;
import com.ey.advisory.app.services.search.filestatussearch.AsyncReportHandler;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.ReportTypeConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.controllers.anexure1.InputValidationUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.service.hsn.summary.HsnSummaryInitiateReportDao;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Shashikant.Shukla
 *
 */

@RestController
@Slf4j
public class HsnSummaryReportController {

	@Autowired
	@Qualifier("HsnSummaryInitiateReportDaoImpl")
	HsnSummaryInitiateReportDao initiateReconcileService;

	@Autowired
	@Qualifier("AsyncProcessedReportHandlerImpl")
	AsyncReportHandler asyncFileStatusReportHandler;

	@Autowired
	FileStatusDownloadReportRepository downloadRepository;

	@PostMapping(value = "/ui/hsnSummaryReport", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> InitiateRecon(
			@RequestBody String jsonString) {

		String fromReturnPeriod = null;
		String toReturnPeriod = null;
		String criteria = null;

		if (LOGGER.isDebugEnabled()) {
			String msg = String
					.format("Begin SalesRegisterInitiateReconController to Initiate "
							+ "Recon : %s", jsonString);
			LOGGER.debug(msg);
		}
		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject(); 
		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		String groupCode = TenantContext.getTenantId();

		User user = SecurityContext.getUser();
		String userName = user.getUserPrincipalName();

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Request for Download CSV Report Controller: %s",
					json.toString());
			LOGGER.debug(msg);
		}

		try {
			FileStatusReportDto reqDto = gson.fromJson(json,
					FileStatusReportDto.class);
			String gstin = null;
			FileStatusDownloadReportEntity entity = new FileStatusDownloadReportEntity();
			asyncFileStatusReportHandler.setDataToEntity(entity, reqDto);
			List<String> gstinList = null;
			if (!reqDto.getDataSecAttrs().isEmpty()) {
				for (String key : reqDto.getDataSecAttrs().keySet()) {

					if (key.equalsIgnoreCase(OnboardingConstant.GSTIN)) {
						gstin = key;
						if (!reqDto.getDataSecAttrs().get(OnboardingConstant.GSTIN).isEmpty()
								&& reqDto.getDataSecAttrs().get(OnboardingConstant.GSTIN)
										.size() > 0) {
							gstinList = reqDto.getDataSecAttrs().get(OnboardingConstant.GSTIN);
						}
					}
				}
			}
			entity.setCreatedBy(userName);
			entity.setCreatedDate(LocalDateTime.now());
			entity.setReportCateg("GSTR1");
			entity.setReportType(ReportTypeConstants.HSN_SUMMARY);
			entity.setDataType("Outward");
			entity.setGstins(GenUtil.convertStringToClob(
					convertToQueryFormat(gstinList)));
			entity.setTaxPeriod(reqDto.getTaxPeriod());
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Request for Download CSV Report Controller: %s",
						json.toString());
				LOGGER.debug(msg);
			}

			entity = downloadRepository.save(entity);

			Long id = entity.getId();

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Successfully saved to DB with Report Id : %d", id);
				LOGGER.debug(msg);
			}
			Long entityId = entity.getEntityId();
			Gson googleJson = new Gson();
			String reportType = ReportTypeConstants.HSN_SUMMARY;

			if (CollectionUtils.isEmpty(gstinList))
				throw new AppException("User Does not have any gstin");

			JsonObject jobParams = new JsonObject();
			jobParams.addProperty("id", id);
			fromReturnPeriod = entity.getTaxPeriod();
			toReturnPeriod = entity.getTaxPeriod();

			String status = initiateReconcileService.createReconcileData(
					gstinList, entityId,
					GenUtil.getDerivedTaxPeriod(fromReturnPeriod).toString(),
					GenUtil.getDerivedTaxPeriod(toReturnPeriod).toString(), id);

			jobParams.addProperty("reportType", reportType);
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(jobParams);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			return InputValidationUtil.createJsonErrResponse(ex);
		}
	}

	private String convertToQueryFormat(List<String> list) {

		String queryData = null;
		if (list == null || list.isEmpty())
			return null;

		queryData = list.get(0);
		for (int i = 1; i < list.size(); i++) {
			queryData += "," + list.get(i);
		}

		return queryData;
	}
}
