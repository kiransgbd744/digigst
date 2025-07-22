/**
 * 
 */
package com.ey.advisory.controllers.gstr3b;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.repositories.client.EntityConfigPrmtRepository;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.gstr3b.Gstr3BEntityDashboardDto;
import com.ey.advisory.app.gstr3b.Gstr3BEntityDashboardService;
import com.ey.advisory.app.util.DataSecurityAttributeUtil;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Khalid1.Khan
 *
 */
@RestController
@Slf4j
public class Gstr3BEntityDashboardController {

	@Autowired
	private Gstr3BEntityDashboardService gstr3BEntityService;

	@Autowired
	private GSTNDetailRepository gstinDetailRepo;

	@Autowired
	@Qualifier("EntityConfigPrmtRepository")
	private EntityConfigPrmtRepository entityConfigPemtRepo;

	private static final String REGULAR = "REGULAR";
	private static final String SEZ = "SEZ";
	private static final String SEZU = "SEZU";
	private static final String SEZD = "SEZD";

	@PostMapping(value = "/ui/getGstr3BEntityDashboard", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getGstr3BEntityDashboard(
			@RequestBody String jsonString) {
		JsonObject errorResp = new JsonObject();
		JsonArray gstins = new JsonArray();
		Gson googleJson = new Gson();
		Gson gson = GsonUtil.newSAPGsonInstance();
		List<String> gstnsList = null;
		List<String> regTypeList = Arrays.asList(REGULAR, SEZ, SEZU, SEZD);

		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("INSIDE Gstr3BEntityDashboardController");
			}
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject().getAsJsonObject("req");

			String taxPeriod = null;

			if (requestObject.has("taxPeriod")) {
				taxPeriod = requestObject.get("taxPeriod").getAsString();
			}

			if (!requestObject.has("entityId"))
				throw new AppException("entityId cannot be empty");

			Long entityId = requestObject.get("entityId").getAsLong();
			List<Long> entityIds = new ArrayList<>();
			entityIds.add(entityId);

			if ((requestObject.has("gstins"))
					&& (requestObject.getAsJsonArray("gstins").size() > 0)) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"As gstins are provided in request, not Invoking"
									+ "service to get gstins for entity");
				}
				gstins = requestObject.getAsJsonArray("gstins");
				Type listType = new TypeToken<ArrayList<String>>() {
				}.getType();
				gstnsList = googleJson.fromJson(gstins, listType);
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

				gstnsList = gstinDetailRepo
						.filterGstinBasedByRegType(dataSecList, regTypeList);
			}
			if (gstnsList == null || gstnsList.isEmpty()) {
				String msg = "Gstins cannot be empty";
				errorResp.add("hdr",
						new Gson().toJsonTree(new APIRespDto("E", msg)));
				return new ResponseEntity<>(errorResp.toString(),
						HttpStatus.OK);
			}
			List<String> uniqueGstins = gstnsList.stream().distinct()
					.collect(Collectors.toList());
			List<Gstr3BEntityDashboardDto> respList = gstr3BEntityService
					.getEntityDashBoard(taxPeriod, uniqueGstins, entityId);

			// sorting the respList
			Comparator<Gstr3BEntityDashboardDto> compareById = new Comparator<Gstr3BEntityDashboardDto>() {

				@Override
				public int compare(Gstr3BEntityDashboardDto o1,
						Gstr3BEntityDashboardDto o2) {
					return o1.getGstin().compareTo(o2.getGstin());
				}
			};

			Collections.sort(respList, compareById);

			JsonObject resps = new JsonObject();
			JsonElement respBody = gson.toJsonTree(respList);
			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resps.add("resp", respBody);
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);

		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json payload";
			LOGGER.error(msg, ex);
			errorResp.add("hdr",
					new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(errorResp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);

		} catch (AppException ex) {
			String msg = ex.getMessage();
			LOGGER.error(ex.getMessage(), ex);
			errorResp.add("hdr",
					new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(errorResp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while retriving Data Status ";
			LOGGER.error(msg, ex);
			errorResp.add("hdr",
					new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(errorResp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);

		}

	}
}
