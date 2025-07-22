package com.ey.advisory.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InputValidationUtil {

	private InputValidationUtil() {}
	
	public static ResponseEntity<String> createJsonErrResponse(Exception ex) {
		
		String exMsg = ex.getMessage();
		String msg = null;
		
		if (ex instanceof AppException) {
			msg = exMsg;
		} else if (ex instanceof JsonParseException) {
			msg = String.format(
					"Error while parsing the request - [%s]", exMsg);
		} else {
			msg = String.format("Unexpected error occured - [%s]", exMsg);
		}
		
		LOGGER.error(msg, ex);

		JsonObject resp = new JsonObject();
		resp.add("hdr", new Gson()
				.toJsonTree(new APIRespDto("E", msg)));

		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}
}
