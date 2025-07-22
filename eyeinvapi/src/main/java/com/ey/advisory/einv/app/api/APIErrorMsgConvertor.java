package com.ey.advisory.einv.app.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.apache.commons.lang3.StringUtils;

import com.ey.advisory.gstnapi.domain.master.NICError;
import com.ey.advisory.gstnapi.repositories.master.NICErrorRepository;
@Component("EINVAPIErrorMsgConvertor")
public class APIErrorMsgConvertor {
	
	private static final Pattern MSG_WITH_CODES_PATTERN = 
								Pattern.compile("^(.*):\\s*([0-9,\\s]*)\\s*$");
	private static final Pattern ONLY_CODES_PATTERN = 
								Pattern.compile("^\\s*([0-9,\\s]*)\\s*$");	
	
	@Autowired
	private NICErrorRepository nicErrorRepository;
	
	public  List<APIError> getErrorList(String input){
		Matcher matcher1 = MSG_WITH_CODES_PATTERN.matcher(input);
		Matcher matcher2 = ONLY_CODES_PATTERN.matcher(input);
		String commaSeprtdErrorCode = "";
		 List<APIError> erroList = new ArrayList<>();;
		 if(matcher1.find()) {
			 commaSeprtdErrorCode = matcher1.group(2);
		 } else if (matcher2.find()){
			 commaSeprtdErrorCode = matcher2.group(1);
		 } else {
			 APIError apiError = new APIError("-1", input);
			 erroList.add(apiError);
			 return erroList;
		 }
		 List<String> errorCodesList = Arrays.asList(
			 commaSeprtdErrorCode.split(","));
		 for(String code : errorCodesList){
			 if(code == null || StringUtils.isEmpty(StringUtils.stripToEmpty(code)))
			 continue;
			 int errorCodeInt = Integer.parseInt(StringUtils.stripToEmpty(code));
			 String errorCategory = 
				 errorCodeInt > 200 ? "EWB" : "AUTH";
			NICError nicErrorObj = 
				 nicErrorRepository.findByErrCategoryAndErrCode(
						 errorCategory, errorCodeInt);
			String errDesc = nicErrorObj == null ? 
				 "Unknown Error from NIC API Invocation" : 
					 nicErrorObj.getErrDesc();
			APIError apiError = new APIError(code, errDesc);
			erroList.add(apiError);
		 }
		return erroList;
		
	}
		

}
