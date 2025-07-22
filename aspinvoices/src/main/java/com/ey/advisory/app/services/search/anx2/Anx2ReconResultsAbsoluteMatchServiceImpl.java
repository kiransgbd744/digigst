package com.ey.advisory.app.services.search.anx2;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.daos.client.anx2.Anx2ReconResultsAbsoluteMatchDao;
import com.ey.advisory.app.docs.dto.anx2.Anx2ReconResultsAbsoluteMatchReqDto;
import com.ey.advisory.app.docs.dto.anx2.Anx2ReconResultsAbsoluteMatchResDto;

@Service("Anx2ReconResultsAbsoluteMatchService")
public class Anx2ReconResultsAbsoluteMatchServiceImpl
		implements Anx2ReconResultsAbsoluteMatchService {

	@Autowired
	@Qualifier("Anx2ReconResultsAbsoluteMatchDao")
	private Anx2ReconResultsAbsoluteMatchDao anx2ReconResultsAbsoluteMatchDao;

	public List<Anx2ReconResultsAbsoluteMatchResDto> fetchAbsoluteDetails(
			Anx2ReconResultsAbsoluteMatchReqDto anx2ReconResultsAbsoluteMatchRequest) {

		String returnPeriod = anx2ReconResultsAbsoluteMatchRequest
				.getReturnPeriod();
		List<String> selectedSgtins = anx2ReconResultsAbsoluteMatchRequest
				.getSgstins();
		return anx2ReconResultsAbsoluteMatchDao.fetchAbsoluteDetails(selectedSgtins,
				returnPeriod);
	}

	@Override
	public List<Anx2ReconResultsAbsoluteMatchResDto> fetchMismatchDetails(
			Anx2ReconResultsAbsoluteMatchReqDto anx2ReconResultsAbsoluteMatchRequest) {
		String returnPeriod = anx2ReconResultsAbsoluteMatchRequest
				.getReturnPeriod();
		List<String> selectedSgtins = anx2ReconResultsAbsoluteMatchRequest
				.getSgstins();
		return anx2ReconResultsAbsoluteMatchDao.fetchMismatchDetails(selectedSgtins,
				returnPeriod);
	}

	@Override
	public List<Anx2ReconResultsAbsoluteMatchResDto> fetchPotentialMatchDetails(
			Anx2ReconResultsAbsoluteMatchReqDto anx2ReconResultsAbsoluteMatchRequest) {
		String returnPeriod = anx2ReconResultsAbsoluteMatchRequest
				.getReturnPeriod();
		List<String> selectedSgtins = anx2ReconResultsAbsoluteMatchRequest
				.getSgstins();
		return anx2ReconResultsAbsoluteMatchDao.fetchPotentialMatchDetails(selectedSgtins,
				returnPeriod);
	}

}
