package com.ey.advisory.controllers.anexure2;

import java.util.List;
import java.util.function.LongFunction;
import java.util.function.Predicate;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.anx2.reconsummary.Anx2ReconSummaryDto;
import com.ey.advisory.app.anx2.reconsummary.Anx2ReconSummaryService;
import com.ey.advisory.app.anx2.reconsummary.ReconSummaryReqDto;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.controllers.anexure1.InputValidationUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/*
 * 
 * @author Nikhil.Duseja
 * 
 * 
 */

@Slf4j
@RestController
public class Anx2ReconSummaryDetailsController {

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstinRepo;

	@Autowired
	@Qualifier("Anx2ReconSummaryServiceImpl")
	private Anx2ReconSummaryService reconSummService;

	LongFunction<List<String>> getGstins = id -> gstinRepo
			.findgstinByEntityId(id);

	Predicate<List<String>> isGstinEmpty = obj -> obj == null || obj.isEmpty();

	@PostMapping(value = "/ui/Anx2ReconSummaryDetails", produces = {
			MediaType.APPLICATION_JSON_VALUE }, consumes = {
					MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getAnnexure2ReconSummaryDetails(
			@RequestBody String jsonReq) {
		try {
			JsonObject jsonObj = (new JsonParser()).parse(jsonReq)
					.getAsJsonObject();
			JsonObject requestObj = jsonObj.get("req").getAsJsonObject();
			Gson gson = GsonUtil.newSAPGsonInstance();
			int taxPeriod = 0;
			ReconSummaryReqDto reqDto = gson.fromJson(requestObj,
					ReconSummaryReqDto.class);
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"The Request Data Got for "
								+ "ReconSummarySearch is %s",
						reqDto.toString());
				LOGGER.debug(msg);
			}
			if (reqDto.getEntityId() == null || reqDto.getTaxPeriod() == null ||
					reqDto.getTaxPeriod().length() != 6) {
				String msg = "EntityId or TaxPeriod Cannot be Empty";
				throw new AppException(msg);
			}
			else
			{
				taxPeriod = GenUtil
						.convertTaxPeriodToInt(reqDto.getTaxPeriod());
			}
			
			if (CollectionUtils.isEmpty(reqDto.getGstins()))
				throw new AppException("User Does not have any gstin");

			List<Anx2ReconSummaryDto> respObj = reconSummService
					.getReconSummaryDetails(reqDto,taxPeriod);
			JsonObject resp = new JsonObject();
			JsonObject detResp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(respObj);
			detResp.add("det", respBody);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", detResp);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ae) {
			return InputValidationUtil.createJsonErrResponse(ae);
		}

	}

}
