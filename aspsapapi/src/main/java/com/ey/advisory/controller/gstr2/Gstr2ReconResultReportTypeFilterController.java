package com.ey.advisory.controller.gstr2;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.asprecon.gstr2.recon.result.Gstr2ReportTypeDto;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2Link2APRrepository;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */
@Slf4j
@RestController
public class Gstr2ReconResultReportTypeFilterController {

	@Autowired
	@Qualifier("Gstr2Link2APRrepository")
	private Gstr2Link2APRrepository linkA2PrRepo;

	@PostMapping(value = "/ui/gstr2GetReportTypeList", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getReportTypeList(
			@RequestBody String jsonString) {

		try {
			if (LOGGER.isDebugEnabled()) {
				String msg = "Begin Gstr2ReconResultReportTypeFilterController"
						+ ".getMismatchReasons() method";
				LOGGER.debug(msg);
			}

			Gson gson = GsonUtil.newSAPGsonInstance();

			List<String> reportList = linkA2PrRepo
					.findAllReportType();
			
			List<String> desirList = Arrays.asList("Exact Match",
					"Match With Tolerance", "Value Mismatch", "POS Mismatch",
					"Doc Date Mismatch", "Doc Type Mismatch", "Doc No Mismatch I",
					"Multi-Mismatch", "Potential-I", "Doc No Mismatch II",
					"Potential-II", "Logical Match", "Addition in PR",
					"Addition in 2A","ForceMatch/GSTR3B");

			List<Gstr2ReportTypeDto> configDtoList = desirList.stream()
					.map(Gstr2ReportTypeDto::new).collect(Collectors.toList());
			
			JsonObject resp = new JsonObject();
			JsonObject reportTypeResp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(configDtoList);
			reportTypeResp.add("reportTypeList", respBody);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", reportTypeResp);

			if (LOGGER.isDebugEnabled()) {
				String msg = "End Gstr2ReconSummaryConfigIdController"
						+ ".getConfigId, before returning response";
				LOGGER.debug(msg);
			}
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (

		JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);

		} catch (Exception ex) {
			String msg = " Unexpected error occured";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
