package com.ey.advisory.controller.gstr2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ey.advisory.app.gstr2jsonupload.Gstr2JsonUploadService;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */
@RestController
@Slf4j
public class Gstr2JsonUploadController {

	@Autowired
	private Gstr2JsonUploadService uploadService;

	@RequestMapping(value = "/ui/jsonInput", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> saveJstr2JsonFile(
			@RequestParam("file") MultipartFile[] files, 
					@RequestParam("file-data") String gstin) {

		try {
			
			String fileFolder = GSTConstants.GSTR2_JSON_FOLDER_NAME;

			return uploadService
					.gstr2JsonFileUpload(files, fileFolder, gstin);
			
			
		} catch (JsonParseException ex) {
			String msg = "Error while parsing file";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);

		} catch (Exception e) {
			String msg = "Error while parsing file";
			LOGGER.error(msg, e);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}
}
