package com.ey.advisory.common.service;

import java.util.List;
import java.util.Map;

public interface ErrorMasterService {
	
	String findErrDescByErrorCode(String errCode, String tableType);
	
	String findErrorInfoByErrorCodes(List<String> errcodes, String tableType);
	
	String findErrorDescByCodes(List<String> errcodes, String tableType);
	
	String findHsnDescByCodes(List<String> hsncodes);
	
	String findInfoByErrorcodeWithoutIndex(List<String> errcodes,
			String tableType) ;
	String findDynamicErrorInfoByErrorCodes(List<String> errcodes,
			String tableType,Map<String,String> dynamicMap);
}
