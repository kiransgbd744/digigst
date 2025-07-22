package com.ey.advisory.controllers.gstr6a;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.docs.dto.gstr6a.Gstr6UpdatedMofifiedDateFetchDaoImpl;
import com.ey.advisory.app.services.daos.gstr6a.Gstr6ReviewSummaryServiceImpl;
import com.ey.advisory.app.services.jobs.anx2.Gstr6SummaryAtGstnImpl;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;
import com.ey.advisory.core.dto.Gstr6GetInvoicesReqDto;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author vishal.verma
 *
 */

@Slf4j
@RestController
public class Gstr6EnitityUpdateGstnController {
	
	@Autowired
	@Qualifier("Gstr6ReviewSummaryServiceImpl")
	private Gstr6ReviewSummaryServiceImpl gstr6RevSumSerImpl;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	GSTNAuthTokenService authTokenService;

	@Autowired
	@Qualifier("Gstr6UpdatedMofifiedDateFetchDaoImpl")
	private Gstr6UpdatedMofifiedDateFetchDaoImpl gstr6UpdatedMofifiedDateFetchDao;

	@Autowired
	@Qualifier("Gstr6SummaryAtGstnImpl")
	private Gstr6SummaryAtGstnImpl gstr6SummaryAtGstn;

	@Autowired
	@Qualifier("Gstr6SummaryAtGstnImpl")
	private Gstr6SummaryAtGstnImpl gstnImpl;

	@PostMapping(value = "/ui/getGstr6EntityReviewSummary", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getGstr6ReviewSummary(@RequestBody String jsonReq) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonArray respBody = new JsonArray();
		
		String msg = null;
		try {
			JsonObject reqObj = (new JsonParser()).parse(jsonReq)
					.getAsJsonObject().get("req").getAsJsonObject();
			Annexure1SummaryReqDto reqDto = gson.fromJson(reqObj,
					Annexure1SummaryReqDto.class);

			if ("UPDATEGSTIN".equalsIgnoreCase(reqDto.getAction())) {
				List<String> gstinList = null;
				Map<String, List<String>> dataSecAttrs = reqDto
						.getDataSecAttrs();
				if (!dataSecAttrs.isEmpty()) {
					for (String key : dataSecAttrs.keySet()) {
						if (key.equalsIgnoreCase(OnboardingConstant.GSTIN)
								&& !dataSecAttrs.get(OnboardingConstant.GSTIN)
										.isEmpty()) {
							gstinList = dataSecAttrs
									.get(OnboardingConstant.GSTIN);
						}
					}
				}

				String authStatus = null;

				Map<String, String> authTokenStatusMap = authTokenService
						.getAuthTokenStatusForGstins(gstinList);

				List<String> inActiveGstins = new ArrayList<>();

				for (String sGstin : gstinList) {
					JsonObject json = new JsonObject();
					authStatus = authTokenStatusMap.get(sGstin);

					if (!"A".equalsIgnoreCase(authStatus)) {
						msg = String.format(" Auth Token is InActive, "
								+ "Please  Activate %s ", sGstin);

						inActiveGstins.add(sGstin);

						json.addProperty("gstin", sGstin);
						json.addProperty("msg", msg);
						respBody.add(json);
					}
				}

				gstinList.removeAll(inActiveGstins);

				if (gstinList != null && !gstinList.isEmpty()) {
					String groupCode = TenantContext.getTenantId();
					gstinList.forEach(gstin -> {
						Gstr6GetInvoicesReqDto reqDto1 = new Gstr6GetInvoicesReqDto();
						reqDto1.setGstin(gstin);
						reqDto1.setReturnPeriod(reqDto.getTaxPeriod());
						JsonObject json = new JsonObject();
						gstr6SummaryAtGstn.getGstr6Summary(reqDto1, groupCode);

						// ITC Section
						gstnImpl.getGstr6ITCDetailsSummary(reqDto1, groupCode);
						String successMsg = String.format("Success,  %s ",
								gstin);

						json.addProperty("gstin", gstin);
						json.addProperty("msg", successMsg);
						respBody.add(json);

					});
				}
			}

			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gson.toJsonTree(respBody));
			

			return new ResponseEntity<>(resp.toString(), HttpStatus.CREATED);

		} catch (JsonParseException ex) {
			 msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		catch (Exception ex) {
			 msg = " Unexpected error occured";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
}
