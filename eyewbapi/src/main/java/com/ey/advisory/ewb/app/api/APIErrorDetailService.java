/**
 * 
 */
package com.ey.advisory.ewb.app.api;

import com.ey.advisory.gstnapi.domain.master.NICError;

/**
 * @author Sachindra.S
 *
 */
public interface APIErrorDetailService {

	public NICError getError(String category, Integer code);

	public NICError saveNicError(String category, Integer code, String desc);
}
