package com.ey.advisory.common;

import java.util.List;
import java.util.Map;

import com.ey.advisory.common.service.ErrorMasterService;

public class ErrorMasterUtil {

	private ErrorMasterUtil() {
	}

	public static String getErrorInfo(List<String> errCodes, String tableType) {
		ErrorMasterService errService = StaticContextHolder
				.getBean("ErrorMasterServiceImpl", ErrorMasterService.class);
		return errService.findErrorInfoByErrorCodes(errCodes, tableType);
	}
	
	public static String getErrorDesc(List<String> errCodes, String tableType) {
		ErrorMasterService errService = StaticContextHolder
				.getBean("ErrorMasterServiceImpl", ErrorMasterService.class);
		return errService.findErrorDescByCodes(errCodes, tableType);
	}
		
		public static String getHsnDesc(List<String> hsnCodes) {
			ErrorMasterService errService = StaticContextHolder
					.getBean("ErrorMasterServiceImpl", ErrorMasterService.class);
			return errService.findHsnDescByCodes(hsnCodes);
	}
		
		public static String getErrorCodeWithoutIndex(List<String> errCodes, String tableType) {
			ErrorMasterService errService = StaticContextHolder
					.getBean("ErrorMasterServiceImpl", ErrorMasterService.class);
			return errService.findInfoByErrorcodeWithoutIndex(errCodes, tableType);
		}
	
	public static String findErrDescByErrCode(String errCode, String tableType) {
		ErrorMasterService errService = StaticContextHolder
				.getBean("ErrorMasterServiceImpl", ErrorMasterService.class);
		return errService.findErrDescByErrorCode(errCode, tableType);
	}

	public static String findDynamicErrorInfoByErrorCodes(List<String> errCodes,
			String tableType, Map<String, String> dynamicMap) {
		ErrorMasterService errService = StaticContextHolder
				.getBean("ErrorMasterServiceImpl", ErrorMasterService.class);
		return errService.findDynamicErrorInfoByErrorCodes(errCodes, tableType,
				dynamicMap);
	}
}
