/**
 * 
 */
package com.ey.advisory.controller.recon3way;

import java.lang.reflect.Type;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.GetEwbCntrlRepo;
import com.ey.advisory.app.recon3way.EWBSummaryDataRequestStatusService;
import com.ey.advisory.app.service.upload.way3recon.GetEwbCntrlEntity;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.gstr2.initiaterecon.EWBSummaryDataRequestStatusDto;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Sakshi.jain
 *
 */

@Slf4j
@RestController
public class EWBRequestStatusSummaryController {

	@Autowired
	@Qualifier("EWBSummaryDataRequestServiceImpl")
	private EWBSummaryDataRequestStatusService reportStatusService;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	GSTNDetailRepository entityRepository;

	@Autowired
	private GetEwbCntrlRepo getEwbCntrlRepo;
	
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter
			.ofPattern("yyyy-MM-dd");

	@PostMapping(value = "/ui/getEWBRequestStatusSummaryFilter", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getEWBRequestStatusSummaryFilter(
			@RequestBody String jsonString) {

		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();

		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject json = requestObject.get("req").getAsJsonObject();

		String msg = String
				.format("Inside EWBRequestStatusSummaryController.getEWBRequestStatusSummaryFilter() "
						+ "method : %s {} ", jsonString);
		LOGGER.debug(msg);

		String criteria = null;
		String fromDate = null;
		String toDate = null;

		try {
			Long entityId = json.get("entityId").getAsLong();
			JsonArray gstins = json.getAsJsonArray("gstins");
			criteria = json.get("criteria").getAsString();
			fromDate = json.get("fromdate").getAsString();
			toDate = json.get("toDate").getAsString();

			Gson googleJson = new Gson();

			Type listType = new TypeToken<ArrayList<String>>() {
			}.getType();
			ArrayList<String> gstinlist = googleJson.fromJson(gstins, listType);

			if (fromDate != null && toDate != null) {
				if (LOGGER.isDebugEnabled()) {
					String msz = String.format(
							"EWB Screen Search" + "Parameters fromDate %s To"
									+ " toDate %s for criteria %s" + ": ",
							fromDate, toDate, criteria);
					LOGGER.debug(msz);
				}
			} else {
				if (LOGGER.isDebugEnabled()) {
					String msz = String.format(" 3 Way Recon Screen Search on"
							+ "Parameters fromReturnPeriod %s To toReturnPeriod %s "
							+ ": ", fromDate, toDate);
					LOGGER.debug(msz);
				}
			}
			List<String> finalList = (gstinlist.isEmpty()
					? entityRepository.findgstinByEntityId(entityId)
					: gstinlist);

			List<EWBSummaryDataRequestStatusDto> recResponse = reportStatusService
					.getRequestDataStatus(finalList, criteria, fromDate, toDate,
							entityId);

			for (EWBSummaryDataRequestStatusDto respDto : recResponse) {
				Optional<GetEwbCntrlEntity> entity = getEwbCntrlRepo
						.findTop1ByGstinOrderByUpdatedOnDesc(respDto.getGstin());
				if (entity.isPresent()) {

					respDto.setEwbGetCallStatus(entity.get().getGetStatus());
					respDto.setEwbGetCallIniTime(
							EYDateUtil.fmtDate(EYDateUtil.toISTDateTimeFromUTC(
									entity.get().getUpdatedOn())));
				} else {
					respDto.setEwbGetCallStatus(APIConstants.NOT_INITIATED);
				}
			}

			JsonObject resps = new JsonObject();
			JsonElement respBody = gson.toJsonTree(recResponse);
			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resps.add("resp", respBody);
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);

		} catch (JsonParseException ex) {
			msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			msg = "Unexpected error while retriving Data Status ";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}
	
}
