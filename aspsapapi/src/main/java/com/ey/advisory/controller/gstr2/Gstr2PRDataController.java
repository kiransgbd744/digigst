package com.ey.advisory.controller.gstr2;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.docs.dto.gstr2.Gstr2PRDataDto;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

/**
 * 
 * @author Balakrishna.S
 *
 */

@RestController
public class Gstr2PRDataController {
	
	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr2PRDataController.class);
	
	@RequestMapping(value = "/ui/getGstr2PRData", method = 
			RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })

	public ResponseEntity<String> getGstr2PRData(
			@RequestBody String jsonString) {
		try {
			Gson gson = GsonUtil.newSAPGsonInstance();
			
			List<Gstr2PRDataDto> respList = new ArrayList<Gstr2PRDataDto>();
			Gstr2PRDataDto prDto = new Gstr2PRDataDto();
			prDto.setDate(LocalDate.now());
			prDto.setUploadBy("mahesh");
			prDto.setFileType("xlsx");
			prDto.setFileName("B2CL");
			prDto.setFileStatus("In Progress");
			prDto.setTotal(100);
			prDto.setProcessASP(60);
			prDto.setErrorASP(40);
			prDto.setInfo(10);
			prDto.setStatus("saved");
			prDto.setProcessGSTN(50);
			prDto.setErrorGSTN(20);
			respList.add(prDto);
			
			Gstr2PRDataDto prDto2 = new Gstr2PRDataDto();
			prDto2.setDate(LocalDate.now());
			prDto2.setUploadBy("bala");
			prDto2.setFileType("xlsx");
			prDto2.setFileName("B2B");
			prDto2.setFileStatus("In Progress");
			prDto2.setTotal(100);
			prDto2.setProcessASP(60);
			prDto2.setErrorASP(40);
			prDto2.setInfo(10);
			prDto2.setStatus("saved");
			prDto2.setProcessGSTN(50);
			prDto2.setErrorGSTN(20);
			respList.add(prDto2);
			
			Gstr2PRDataDto prDto3 = new Gstr2PRDataDto();
			prDto3.setDate(LocalDate.now());
			prDto3.setUploadBy("hema");
			prDto3.setFileType("xlsx");
			prDto3.setFileName("B2B");
			prDto3.setFileStatus("In Progress");
			prDto3.setTotal(100);
			prDto3.setProcessASP(60);
			prDto3.setErrorASP(40);
			prDto3.setInfo(10);
			prDto3.setStatus("saved");
			prDto3.setProcessGSTN(50);
			prDto3.setErrorGSTN(20);
			respList.add(prDto3);

			Gstr2PRDataDto prDto4 = new Gstr2PRDataDto();
			prDto4.setDate(LocalDate.now());
			prDto4.setUploadBy("siva");
			prDto4.setFileType("xlsx");
			prDto4.setFileName("B2B");
			prDto4.setFileStatus("In Progress");
			prDto4.setTotal(100);
			prDto4.setProcessASP(60);
			prDto4.setErrorASP(40);
			prDto4.setInfo(10);
			prDto4.setStatus("saved");
			prDto4.setProcessGSTN(50);
			prDto4.setErrorGSTN(20);
			respList.add(prDto4);

			
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gson.toJsonTree(respList));

			return new ResponseEntity<>(resp.toString(),HttpStatus.OK);
		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while getting PR Data  for Gstr2";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}


}
