package com.ey.advisory.controllers.anexure2;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.docs.dto.anx2.Anx2GetAnx2SummaryDetailsReqDto;
import com.ey.advisory.app.docs.dto.anx2.Anx2GetFinalSummaryDetailsResDto;
import com.ey.advisory.app.services.search.anx2.Anx2GetAnx2SummaryDetailsService;
import com.ey.advisory.app.util.DataSecurityAttributeUtil;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

/**
 * 
 * @author Anand3.M
 *
 */
@RestController
public class Anx2GetAnx2SummaryDetailsController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Anx2GetAnx2SummaryDetailsController.class);

	@Autowired
	@Qualifier("Anx2GetAnx2SummaryDetailsService")
	private Anx2GetAnx2SummaryDetailsService anx2GetAnx2SummaryDetailsService;

	@PostMapping(value = "/ui/getAnx2SummaryDetails", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getAnx2SummaryDetails(
			@RequestBody String jsonString) {
		try {
			JsonObject obj = new JsonParser().parse(jsonString)
					.getAsJsonObject();
			String reqJson = obj.get("req").getAsJsonObject().toString();
			Gson gson = GsonUtil.newSAPGsonInstance();

			Anx2GetAnx2SummaryDetailsReqDto criteria = gson.fromJson(
					reqJson.toString(), Anx2GetAnx2SummaryDetailsReqDto.class);
			List<Long> entityIds = criteria.getEntityId();
			Map<String, String> inwardSecurityAttributeMap = DataSecurityAttributeUtil
					.getInwardSecurityAttributeMap();
			Map<String, List<String>> dataSecurityAttrMap = DataSecurityAttributeUtil
					.dataSecurityAttrMapForQuery(entityIds,
							inwardSecurityAttributeMap);
			if (criteria.getDataSecAttrs() == null
					|| criteria.getDataSecAttrs().isEmpty()) {
				criteria.setDataSecAttrs(dataSecurityAttrMap);
			} else {
				Map<String, List<String>> dataSecReqMap = criteria
						.getDataSecAttrs();
				List<String> gstinList = dataSecReqMap
						.get(OnboardingConstant.GSTIN);
				if ((gstinList == null || gstinList.isEmpty())) {
					criteria.setDataSecAttrs(dataSecurityAttrMap);
				} else {
					if ((gstinList != null && !gstinList.isEmpty())) {
						dataSecurityAttrMap.put(OnboardingConstant.GSTIN,
								gstinList);
					}
					criteria.setDataSecAttrs(dataSecurityAttrMap);
				}
			}
			Anx2GetFinalSummaryDetailsResDto resp = anx2GetAnx2SummaryDetailsService
					.<Anx2GetFinalSummaryDetailsResDto>loadSummaryDetails(
							criteria);

			JsonObject resps = new JsonObject();
			if (!resp.getTable().isEmpty()) {
				JsonElement respBody = gson.toJsonTree(resp);
				resps.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				resps.add("resp", respBody);
				return new ResponseEntity<>(resps.toString(), HttpStatus.OK);
			} else {
				JsonElement respBody = gson.toJsonTree(
						"No Summary details data found for the Given Date.");
				resps.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
				resps.add("resp", respBody);
				return new ResponseEntity<>(resps.toString(), HttpStatus.OK);
			}

		}

		catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json for Summary details";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error for Summary details";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
