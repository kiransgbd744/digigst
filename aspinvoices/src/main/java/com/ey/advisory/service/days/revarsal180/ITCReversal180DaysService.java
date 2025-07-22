/**
 * 
 */
package com.ey.advisory.service.days.revarsal180;

import java.util.List;

/**
 * @author vishal.verma
 *
 */
public interface ITCReversal180DaysService {

	public List<ITCReversal180DaysRespDto> getItcReversalData(
			ITCReversal180DaysReqDto reqDto);

}
