/**
 * 
 */
package com.ey.advisory.ewb.common;

import java.util.List;

import org.javatuples.Pair;
import org.springframework.stereotype.Component;

import com.ey.advisory.ewb.app.api.APIError;
import com.google.common.base.Strings;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Khalid1.Khan
 *
 */
@Slf4j
@Component("EyEwbUtilMethods")
public class EyEwbUtilMethods {
	
	private EyEwbUtilMethods() {}
	
	public static  Pair<String ,String> createErrorResponse(
			List<APIError> errors) {
		String errorCode = "";
		String errorDesc = "";
		for (int i = 0; i < errors.size(); i++) {
			if (!Strings.isNullOrEmpty(errors.get(i).getErrorCode()))
				errorCode = errorCode + (i + 1) + ") "
						+ errors.get(i).getErrorCode() + " ";
			if (!Strings.isNullOrEmpty(errors.get(i).getErrorCode()))
				errorDesc = errorDesc + (i + 1) + ") "
						+ errors.get(i).getErrorDesc() + " ";
		}
		
		return new Pair<>(errorCode, errorDesc);

	}
}
