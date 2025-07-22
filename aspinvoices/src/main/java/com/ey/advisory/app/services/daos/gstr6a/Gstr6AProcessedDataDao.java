package com.ey.advisory.app.services.daos.gstr6a;

import java.util.List;

import com.ey.advisory.app.docs.dto.gstr6a.Gstr6AProcessedDataRequestDto;
import com.ey.advisory.app.docs.dto.gstr6a.Gstr6AProcessedDataResponseDto;

public interface Gstr6AProcessedDataDao {
	
	List<Gstr6AProcessedDataResponseDto> getGstr6AProcessedData(
			Gstr6AProcessedDataRequestDto criteria) throws Exception;

}
