/**
 * 
 */
package com.ey.advisory.service.days.revarsal180;

import java.util.List;

/**
 * @author vishal.verma
 *
 */
public interface ITCReversal180DaysRequestIdWiseService {

	public List<ITCReversal180DaysRequestIdWiseDto> getRequestStatus(
			ITCReversal180DaysReqDto reqDto, String userName);

}
