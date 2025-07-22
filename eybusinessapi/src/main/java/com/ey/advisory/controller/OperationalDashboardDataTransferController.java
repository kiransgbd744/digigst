package com.ey.advisory.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.common.APIRespDto;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.async.domain.master.Group;
import com.ey.advisory.core.async.repositories.master.GroupRepository;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Sakshi.jain
 *
 */
@Slf4j
@RestController
public class OperationalDashboardDataTransferController {

	@Autowired
	@Qualifier("GroupRepository")
	private GroupRepository grpRepo;

	@PostMapping(value = "/api/getEinvEwbData", consumes = {
			MediaType.APPLICATION_JSON_VALUE }, produces = {
					MediaType.APPLICATION_JSON_VALUE })

	public ResponseEntity<String> generateBulkEinvoice(
			@RequestBody String jsonString, HttpServletRequest req) {
		JsonObject jsonObject = new JsonObject();
		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();
		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
		String groupCode = requestObject.get("groupCode").getAsString();
		if(LOGGER.isDebugEnabled())
		{
			LOGGER.debug(" jsonString {} ",jsonString);
		}
		
		try {
			String bcapiCreateDate = null;
			Group grp = grpRepo.findByGroupCodeAndIsActiveTrue(groupCode);
			if (grp != null) {
				bcapiCreateDate = grp.getCreatedDate().toString();
			}
			if(LOGGER.isDebugEnabled())
			{
				LOGGER.debug(" bcapiCreateDate {} ",bcapiCreateDate);
			}
			jsonObject.add("hdr",
					gson.toJsonTree(APIRespDto.createSuccessResp()));
			jsonObject.add("resp", gson.toJsonTree(bcapiCreateDate));
			
			if(LOGGER.isDebugEnabled())
			{
				LOGGER.debug(" jsonObject {} ",jsonObject);
			}
			
			return new ResponseEntity<>(jsonObject.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			LOGGER.error("Exception while fetching groupData ", ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gson.toJsonTree("Error While fetching data"));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}
	
	public static void main(String[] args) {
		JsonObject jsonObject = new JsonObject();
		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
		
		jsonObject.add("hdr",
				gson.toJsonTree(APIRespDto.createSuccessResp()));
		jsonObject.add("resp", gson.toJsonTree(LocalDateTime.now()));
		System.out.println(jsonObject);
		
		JsonObject respObj = jsonObject;
		String status = respObj.has("hdr")?respObj.get("hdr").getAsJsonObject().get("status").getAsString():null;
		if(status.equalsIgnoreCase("S"))
		{
			String bcapiTime = respObj.has("resp")?respObj.get("resp").getAsString():null;
				System.out.println(bcapiTime);
		}
	}

}
