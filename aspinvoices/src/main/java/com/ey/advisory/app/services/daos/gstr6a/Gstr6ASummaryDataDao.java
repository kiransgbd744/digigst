package com.ey.advisory.app.services.daos.gstr6a;

import java.util.List;

import com.ey.advisory.app.docs.dto.gstr6a.Gstr6ASummaryDataRequestDto;
import com.ey.advisory.app.docs.dto.gstr6a.Gstr6ASummaryDataResponseDto;

/**
 * 
 * @author SriBhavya
 *
 */

public interface Gstr6ASummaryDataDao {

	List<Gstr6ASummaryDataResponseDto> getGstr6ASummaryData(
			Gstr6ASummaryDataRequestDto criteria);
}
