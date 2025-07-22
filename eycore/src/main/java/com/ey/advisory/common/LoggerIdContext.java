package com.ey.advisory.common;

import com.ey.advisory.domain.client.B2CQRCodeRequestLogEntity;
import com.ey.advisory.domain.client.ERPRequestLogEntity;

/**
 * This class to propagate the logId in request.
 * 
 * @author Sai.Pakanati
 *
 */
public class LoggerIdContext {

	/**
	 * Make the class non-instantiable.
	 */
	private LoggerIdContext() {
	}

	private static final ThreadLocal<ERPRequestLogEntity> context = new ThreadLocal<>();

	private static final ThreadLocal<B2CQRCodeRequestLogEntity> b2cOnboardingcontext = new ThreadLocal<>();

	public static void setLoggerId(ERPRequestLogEntity logEntity) {
		context.set(logEntity);
	}

	public static ERPRequestLogEntity getLoggerId() {

		return context.get();
	}

	public static void setB2CLoggerContext(
			B2CQRCodeRequestLogEntity logEntity) {
		b2cOnboardingcontext.set(logEntity);
	}

	public static B2CQRCodeRequestLogEntity getB2CLoggerContext() {

		return b2cOnboardingcontext.get();
	}

	public static void clearLoggerId() {
		context.remove();
		b2cOnboardingcontext.remove();
	}

}
