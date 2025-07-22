/**
 * 
 */
package com.ey.advisory.controllers.anexure2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.dto.compute.Annexure2ComputeDtoList;
import com.ey.advisory.app.services.compute.Annexure2ReconComputeServiceFacade;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.functions.Annexure2ComputeHelper;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

/**
 * @author Khalid1.Khan
 *
 */

@RestController
public class Annexure2ComputeController {

	@Autowired
	private Annexure2ReconComputeServiceFacade annexure2ReconComputeService;

	@Autowired
	private Annexure2ComputeHelper annexure2ComputeHelper;

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Annexure2ComputeController.class);

	@PostMapping(value = "/ui/executeComputeAction", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> takeComputeAction(
			@RequestBody String jsonString) {
		JsonObject errorResp = new JsonObject();
		try {

			String response = "";
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("INSIDE annexure2ComputeHelper.takeComputeAction");
			}
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();

			JsonObject json = requestObject.get("req").getAsJsonObject();
			Gson gson = GsonUtil.newSAPGsonInstance();
			Annexure2ComputeDtoList  annexure2ComputeDtoList= gson.fromJson(json,
					Annexure2ComputeDtoList.class);

			JsonObject successResp = new JsonObject();
			if (null != annexure2ComputeDtoList.getAnnexure2ComputeListDto()
					&& !annexure2ComputeDtoList.getAnnexure2ComputeListDto().isEmpty()) {

				response = annexure2ComputeHelper.validateReconComputeList(
						annexure2ComputeDtoList.getAnnexure2ComputeListDto(), response);
				if (response.isEmpty()) {
					annexure2ReconComputeService.reconComputeAction(
							annexure2ComputeDtoList.getAnnexure2ComputeListDto());
					JsonElement respBody = gson
							.toJsonTree("UPDATION SUCCESSFULL");
					successResp.add("hdr",
							gson.toJsonTree(APIRespDto.createSuccessResp()));
					successResp.add("resp", respBody);
					return new ResponseEntity<>(successResp.toString(),
							HttpStatus.OK);
				} else {
					JsonElement respBody = gson.toJsonTree(response);
					successResp.add("hdr",
							gson.toJsonTree(APIRespDto.creatErrorResp()));
					successResp.add("resp", respBody);
					return new ResponseEntity<>(successResp.toString(),
							HttpStatus.OK);
				}

			} else {
				JsonElement respBody = gson.toJsonTree("Input list is empty.");
				successResp.add("hdr",
						gson.toJsonTree(APIRespDto.creatErrorResp()));
				successResp.add("resp", respBody);
				return new ResponseEntity<>(successResp.toString(),
						HttpStatus.OK);
			}

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
