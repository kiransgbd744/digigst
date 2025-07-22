package com.ey.advisory.admin.onboarding.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.multitenancy.MultiTenancyUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.domain.master.Group;
import com.ey.advisory.core.async.repositories.master.GroupRepository;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.GroupCodeSwitchReqDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * This class is responsible for sending group code data from MASTER DB, 
 * EY_GROUP Table and enables Admin to switch group code
 * @author Mohana.Dasari
 *
 */
@RestController
public class GroupCodeSwitchController {
	
	private static final Logger LOGGER = LoggerFactory
			.getLogger(GroupCodeSwitchController.class);
	
	@Autowired
	@Qualifier("GroupRepository")
	private GroupRepository groupRepository;
	
	
	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getAllGroupCodes", method = RequestMethod.POST, 
			produces = {MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getAllGroupCodes() {
		try {
			List<Group> activeGroups = groupRepository.findByIsActive(true);
			Gson gson = GsonUtil.newSAPGsonInstance();
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(activeGroups);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);
			return new ResponseEntity<>(resp.toString(), HttpStatus.CREATED);
		} catch (Exception ex) {
			String msg = "Unexpected error while getting group codes";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@RequestMapping(value = "/switchGroupCode", method = RequestMethod.POST, 
			produces = {MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> switcjGroupCode(
			@RequestBody String jsonString,HttpServletRequest request) {
		try {
			JsonObject reqObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();
			String reqJson = reqObject.get("req").getAsJsonObject().toString();
			Gson gson = GsonUtil.newSAPGsonInstance();
			GroupCodeSwitchReqDto dto = gson.fromJson(reqJson,
					GroupCodeSwitchReqDto.class);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Before switching... Current Grp Code " 
						+ dto.getCurrGroupCode()
						+ " New Group Code " + dto.getNewGroupCode());
			}
			MultiTenancyUtil.switchToNewTenantContext(dto.getCurrGroupCode(),
					dto.getNewGroupCode());
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("After switching... Current Grp Code " 
						+ dto.getCurrGroupCode()
						+ " New Group Code " + dto.getNewGroupCode());
			}			
			HttpSession session = request.getSession(false);
			session.setAttribute("newGroupCode", dto.getNewGroupCode());
			
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Getting the group code from session. " 
						+ session.getAttribute("newGroupCode"));
			}						
			
			JsonObject resp = new JsonObject();
			resp.add("hdr",
					gson.toJsonTree(new APIRespDto(APIRespDto.SUCCESS_STATUS,
							"Group Code has been successfully switched")));
			return new ResponseEntity<>(resp.toString(), HttpStatus.CREATED);
		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error switching group code";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@RequestMapping(value = "/sayHello", method = RequestMethod.GET, 
			produces = "application/json")
	public ResponseEntity<String> sayHello() {
		
		try{
			String str = "Hello "+TenantContext.getTenantId();
	    	   return new ResponseEntity<String>(str, HttpStatus.OK);
	    	}catch(Exception e){
	    	   return new ResponseEntity<String>(e.getMessage(),
	    			   HttpStatus.INTERNAL_SERVER_ERROR);
	    	}		
	}
}
