package com.ey.advisory.controller.gstr1.sales.register;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.docs.dto.FileStatusReqDto;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.JobStatusConstants;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.search.SearchResult;
import com.ey.advisory.service.gstr1.sales.register.SalesRegisterFileStatusResponseDto;
import com.ey.advisory.service.gstr1.sales.register.SalesRegisterFileStatusService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Shashikant.Shukla
 *
 */

@RestController
@Slf4j
public class SalesRegisterFileStatusController {

	@Autowired
	@Qualifier("SalesRegisterFileStatusService")
	private SalesRegisterFileStatusService SalesRegisterFileStatusService;

	@PostMapping(value = { "/ui/salesRegisterFileStatus" }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	/**
	 * File status
	 * 
	 * @param fileStatusJson
	 * @return ResponseEntity<String>
	 */
	public ResponseEntity<String> fileStatus(@RequestBody String fileStatusJson,
			HttpServletRequest request) {
		String tenantCode = TenantContext.getTenantId();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Tenant Id Is {}", tenantCode);
			LOGGER.debug("fileStatus methods{}");
		}

		JsonObject jsonReqObj = (new JsonParser().parse(fileStatusJson)
				.getAsJsonObject());

		JsonObject json = jsonReqObj.get("req").getAsJsonObject();

		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();

		// Execute the File status service method and get the result.

		try {

			FileStatusReqDto fileReqDto = gson.fromJson(json,
					FileStatusReqDto.class);
			fileReqDto.setSource(JobStatusConstants.WEB_UPLOAD);

			// String path = request.getServletPath();
			SearchResult<SalesRegisterFileStatusResponseDto> searchResult = SalesRegisterFileStatusService
					.find(fileReqDto, null, SalesRegisterFileStatusResponseDto.class);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gson.toJsonTree(searchResult.getResult()));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(msg, ex);
			}

			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while retriving File Status ";
			LOGGER.error(msg, ex);
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
