package com.ey.advisory.sap.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.data.entities.client.GetGstr1SummaryEntity;
import com.ey.advisory.app.data.repositories.client.Gstr1SummaryAtGstnRepository;
import com.ey.advisory.app.services.jobs.erp.Gstr1SummaryRIDestination;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * 
 * @author Hemasundar.J
 *
 */
@RestController
public class Gstr1SummaryRevIntegrationTestController {

	private static final String GROUP_CODE = TestController.staticTenantId();
	
	@Autowired
	@Qualifier("gstr1SummaryRIDestinationImpl")
	private Gstr1SummaryRIDestination gstr1SummaryRIDestination;

	@Autowired
	private Gstr1SummaryAtGstnRepository gstr1SummaryAtGstnRepository;

	@PostMapping(value = "/gstr1GstnSummaryToErp", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> summaryRI(@RequestBody String jsonString) {
		TenantContext.setTenantId(GROUP_CODE);
		String gstin = "33GSPTN0481G1ZA";
		String retPeriod = "072018";
		JsonObject json = new JsonObject();

		List<GetGstr1SummaryEntity> childEntities = new ArrayList<>();
		List<GetGstr1SummaryEntity> doc = gstr1SummaryAtGstnRepository
				.getDocIssuedSummaryForRI(gstin, retPeriod);
		childEntities.addAll(doc);
		List<GetGstr1SummaryEntity> nil = gstr1SummaryAtGstnRepository
				.getNilSummaryForRI(gstin, retPeriod);
		childEntities.addAll(nil);
		List<GetGstr1SummaryEntity> rate = gstr1SummaryAtGstnRepository
				.getRateSummaryForRI(gstin, retPeriod);
		childEntities.addAll(rate);
		Gson gson = GsonUtil.newSAPGsonInstance();
		String string = gson.toJson(childEntities);
		if (string != null && string.length() > 0) {
			JsonArray array = (new JsonParser()).parse(string).getAsJsonArray();
			json.add("array", array);
		}

		if (json.get("array") != null) {
			// Reverse Integration to ERP
			return gstr1SummaryRIDestination.pushToErp(json, "GSTR1SummaryAPI");
		} else {
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree("No Data found");
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		}

	}

}
