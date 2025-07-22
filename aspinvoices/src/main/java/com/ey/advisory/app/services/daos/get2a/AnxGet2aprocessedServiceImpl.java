package com.ey.advisory.app.services.daos.get2a;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.Anx2GetProcessedRequestDto;
import com.ey.advisory.app.docs.dto.Anx2GetProcessedResponseDto;


@Service("AnxGet2aprocessedServiceImpl")
public class AnxGet2aprocessedServiceImpl
		implements AnxGet2aprocessedService {

	@Autowired
	@Qualifier("AnxGet2aprocessedDaoImpl")
	AnxGet2aprocessedDao anxGet2aprocessedDao;


	@Override
	public List<Anx2GetProcessedResponseDto> getAnx2Get2aProcessedRecords(
			Anx2GetProcessedRequestDto criteria) {

		List<Anx2GetProcessedResponseDto> recResponse =
				anxGet2aprocessedDao.getAnx2Get2aProcessedData(criteria);
				
				return recResponse;
	}

}