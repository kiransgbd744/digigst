package com.ey.advisory.app.services.daos.saveanx2;

import java.util.List;

import com.ey.advisory.app.docs.dto.Anx2SaveAnx2ProcessedRequestDto;
import com.ey.advisory.app.docs.dto.Anx2SaveAnx2ProcessedResponseDto;

public interface SaveAnx2ProcessedDataDao {

	List<Anx2SaveAnx2ProcessedResponseDto> getSaveAnx2ProcessedData(
			Anx2SaveAnx2ProcessedRequestDto dto);

}
