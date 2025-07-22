/**
 * 
 */
package com.ey.advisory.service.days.revarsal180;

import java.util.List;

/**
 * @author vishal.verma
 *
 */
public interface ITCReversal180DaysDao {

	public List<ITCReversal180DaysRespDto> getItcReversalDBResp(
			List<String> gstins);
}
