/**
 * 
 */
package com.ey.advisory.app.service.ims;

import java.util.List;

import org.javatuples.Pair;
import org.springframework.data.domain.Pageable;

/**
 * @author vishal.verma
 *
 */

public interface ImsRecodsDataService {

	public Pair<List<ImsRecordDataResponseDto>,Integer> getImsRecodsScreenData(
			ImsRecordDataRequestDto reqDto, Pageable pageReq);

}
