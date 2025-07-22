/**
 * 
 */
package com.ey.advisory.admin.services.onboarding.gstinfileupload;

import com.ey.advisory.common.AppException;

/**
 * @author Sasidhar Reddy
 *
 */
public class OnboardingFileUploadException extends AppException {

	/**
	 * 
	 */
	public OnboardingFileUploadException(String msg) {
		super(msg);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param errCode
	 */
	public OnboardingFileUploadException(String message, String errCode) {
		super(message, errCode);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 * @param errCode
	 */
	public OnboardingFileUploadException(String message, Throwable cause,
			String errCode) {
		super(message, cause, errCode);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 */
	public OnboardingFileUploadException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	
}
