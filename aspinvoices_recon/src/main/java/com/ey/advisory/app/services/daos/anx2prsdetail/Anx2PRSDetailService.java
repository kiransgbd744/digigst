package com.ey.advisory.app.services.daos.anx2prsdetail;

import java.util.List;

import com.ey.advisory.core.dto.Anx2PRSProcessedRequestDto;

public interface Anx2PRSDetailService {

	List<Anx2PRSDetailHeaderDto> getAnx2PRSDetail(
			Anx2PRSProcessedRequestDto dto);

}
