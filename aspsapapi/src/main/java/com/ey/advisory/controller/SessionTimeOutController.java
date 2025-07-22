package com.ey.advisory.controller;



import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.docs.dto.SessionTimeOutDto;
import com.ey.advisory.app.util.GsonUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
/**
 * 
 * @author Mahesh.Golla
 *
 */
@RestController
public class SessionTimeOutController  {


	@RequestMapping(value = "/ui/sessionTimeout", method = RequestMethod.GET, 
			produces = {MediaType.APPLICATION_JSON_VALUE })
	 public ResponseEntity<String> 
	                              sessionTimeOut(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
       /* Date lastAccessedTime = new Date(session.getLastAccessedTime());
        Instant instant = lastAccessedTime.toInstant();
        ZoneId defaultZoneId = ZoneId.systemDefault();
        LocalDateTime lastAccessedLocalDate = 
        		instant.atZone(defaultZoneId).toLocalDateTime();
        LocalDateTime currentTime = LocalDateTime.now();
        long minutes =
        		ChronoUnit.MINUTES.between(lastAccessedLocalDate, currentTime);*/
        SessionTimeOutDto sessionDto = null;
        JsonObject resp = new JsonObject();
    	Gson gson = GsonUtil.newSAPGsonInstance();
        if(session != null){
        	sessionDto = new SessionTimeOutDto();
        	session.invalidate();
        	sessionDto.setMessage("Session has been expired");
        	sessionDto.setStatus(0);
        	resp.add("resp", gson.toJsonTree(sessionDto));
        	return new 
        			ResponseEntity<String>(resp.toString(),HttpStatus.OK);
        }
        else{
        	sessionDto = new SessionTimeOutDto();
        	sessionDto.setMessage("Session is Active");
        	sessionDto.setStatus(1);
        	resp.add("resp", gson.toJsonTree(sessionDto));
        	return new 
        			ResponseEntity<String>(resp.toString(),HttpStatus.OK);
        }
	 }
	
	@RequestMapping(value = "/ui/sessionResume", method = RequestMethod.GET, 
			produces = {MediaType.APPLICATION_JSON_VALUE })
	 public ResponseEntity<String> 
	                              resume(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        /*Date lastAccessedTime = new Date(session.getLastAccessedTime());
        Instant instant = lastAccessedTime.toInstant();
        ZoneId defaultZoneId = ZoneId.systemDefault();
        LocalDateTime lastAccessedLocalDate = 
        		instant.atZone(defaultZoneId).toLocalDateTime();
        LocalDateTime currentTime = LocalDateTime.now();
        long minutes =
        		ChronoUnit.MINUTES.between(lastAccessedLocalDate, currentTime);
        */
        SessionTimeOutDto sessionDto = new SessionTimeOutDto();
        JsonObject resp = new JsonObject();
    	Gson gson = GsonUtil.newSAPGsonInstance();
        	session.invalidate();
        	sessionDto.setMessage("Resume service activated");
        	sessionDto.setStatus(1);
        	resp.add("resp", gson.toJsonTree(sessionDto));
        	return new 
        			ResponseEntity<String>(resp.toString(),HttpStatus.OK);
	 }
}
