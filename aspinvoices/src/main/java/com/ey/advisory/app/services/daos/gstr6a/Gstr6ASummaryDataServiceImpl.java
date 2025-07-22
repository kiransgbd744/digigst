package com.ey.advisory.app.services.daos.gstr6a;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.gstr6a.Gstr6ASummaryDataRequestDto;
import com.ey.advisory.app.docs.dto.gstr6a.Gstr6ASummaryDataResponseDto;

/**
 * 
 * @author SriBhavya
 *
 */

@Service("Gstr6ASummaryDataServiceImpl")
public class Gstr6ASummaryDataServiceImpl implements Gstr6ASummaryDataService {
	@Autowired
	@Qualifier("Gstr6ASummaryDataDaoImpl")
	Gstr6ASummaryDataDao gstr6ASummaryDataDao;

	@Override
	public List<Gstr6ASummaryDataResponseDto> getGstr6ASummaryData(
			Gstr6ASummaryDataRequestDto criteria) {
		return gstr6ASummaryDataDao.getGstr6ASummaryData(criteria);
	}

}
