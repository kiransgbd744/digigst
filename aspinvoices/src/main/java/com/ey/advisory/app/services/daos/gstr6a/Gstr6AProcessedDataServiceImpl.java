package com.ey.advisory.app.services.daos.gstr6a;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.gstr6a.Gstr6AProcessedDataRequestDto;
import com.ey.advisory.app.docs.dto.gstr6a.Gstr6AProcessedDataResponseDto;

@Service("Gstr6AProcessedDataServiceImpl")
public class Gstr6AProcessedDataServiceImpl
		implements Gstr6AProcessedDataService {

	@Autowired
	@Qualifier("Gstr6AProcessedDataDaoImpl")
	Gstr6AProcessedDataDao gstr6AProcessedDataDao;

	@Override
	public List<Gstr6AProcessedDataResponseDto> getGstr6AProcessedData(
			Gstr6AProcessedDataRequestDto criteria) throws Exception {
		return gstr6AProcessedDataDao.getGstr6AProcessedData(criteria);
	}

}
