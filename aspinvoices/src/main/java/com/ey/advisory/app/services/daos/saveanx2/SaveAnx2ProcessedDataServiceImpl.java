package com.ey.advisory.app.services.daos.saveanx2;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.Anx2SaveAnx2ProcessedRequestDto;
import com.ey.advisory.app.docs.dto.Anx2SaveAnx2ProcessedResponseDto;

@Service("SaveAnx2ProcessedDataServiceImpl")
public class SaveAnx2ProcessedDataServiceImpl
		implements SaveAnx2ProcessedDataService {

	@Autowired
	@Qualifier("SaveAnx2ProcessedDataDaoImpl")
	SaveAnx2ProcessedDataDao saveAnx2ProcessedDataDao;

	@Override
	public List<Anx2SaveAnx2ProcessedResponseDto> getSaveAnx2ProcessedData(
			Anx2SaveAnx2ProcessedRequestDto criteria) {
		return saveAnx2ProcessedDataDao.getSaveAnx2ProcessedData(criteria);
	}

}
