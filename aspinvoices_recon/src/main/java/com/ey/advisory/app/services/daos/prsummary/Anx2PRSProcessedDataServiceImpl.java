package com.ey.advisory.app.services.daos.prsummary;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.core.dto.Anx2PRSProcessedRequestDto;
import com.ey.advisory.core.dto.Anx2PRSProcessedResponseDto;

@Service("Anx2PRSProcessedDataServiceImpl")
public class Anx2PRSProcessedDataServiceImpl
		implements Anx2PRSProcessedDataService {

	@Autowired
	@Qualifier("Anx2PRSProcessedDataDaoImpl")
	Anx2PRSProcessedDataDao anx2PRSProcessedDataDao;

	@Override
	public List<Anx2PRSProcessedResponseDto> getAnx2PRSProcessedRecords(
			Anx2PRSProcessedRequestDto criteria) {
		
		List<Anx2PRSProcessedResponseDto> recResponse =
		anx2PRSProcessedDataDao.getAnx2PRSProcessedRecs(criteria);
		
		return recResponse;
	}

}
