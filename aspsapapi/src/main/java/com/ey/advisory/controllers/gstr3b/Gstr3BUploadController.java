package com.ey.advisory.controllers.gstr3b;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.aspose.cells.Workbook;
import com.ey.advisory.app.gstr3b.Gstr3BFileUploadService;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import lombok.extern.slf4j.Slf4j;
/**
 * 
 * @author vishal.verma
 *
 */

@Slf4j
@RestController
public class Gstr3BUploadController {
	
	@Autowired
	private Gstr3BFileUploadService uploadService;
	
	@RequestMapping(value = "/ui/exelInput", method = RequestMethod.POST,
			produces  = { MediaType.APPLICATION_XML_VALUE})
	public ResponseEntity<String> saveGstinDashboardExcelInput(
			@RequestParam("file") MultipartFile file) {
		

		try {
			Gson gson = GsonUtil.newSAPGsonInstance();

			Workbook workbook = new Workbook(file.getInputStream());
			workbook.setFileName(file.getOriginalFilename());
			String fileFolder = "gstr3bFiles";
			
			uploadService.gstr3bFileUpload(workbook, fileFolder);

			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		} catch (JsonParseException ex) {
		    String msg = "Error while parsing file";
		    LOGGER.error(msg, ex);

		    JsonObject resp = new JsonObject();
		    resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

		    return new ResponseEntity<>(resp.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (IOException ex) {
		    String msg = "IO exception occurred";
		    LOGGER.error(msg, ex);

		    JsonObject resp = new JsonObject();
		    resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

		    return new ResponseEntity<>(resp.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
		    String msg = "An unexpected error occurred";
		    LOGGER.error(msg, ex);

		    JsonObject resp = new JsonObject();
		    resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

		    return new ResponseEntity<>(resp.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

		//return null;

	}


}
