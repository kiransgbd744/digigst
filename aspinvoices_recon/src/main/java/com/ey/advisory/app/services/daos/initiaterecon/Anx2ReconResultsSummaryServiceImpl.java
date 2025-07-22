package com.ey.advisory.app.services.daos.initiaterecon;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.EntityIRDto;

	@Service("Anx2ReconResultsSummaryService")
	public class Anx2ReconResultsSummaryServiceImpl
			implements Anx2ReconResultsSummaryService {

		@Autowired
		@Qualifier("Anx2ReconResultsSummaryDaoImpl")
		private Anx2ReconResultsSummaryDao anx2ReconResultsSummaryDao;

		public List<Anx2ReconResultsSummaryResDto> 
		absoluteMatchSummary(EntityIRDto dto) {

			String returnPeriod = dto
					.getReturnPeriod();
			
			List<String> selectedSgtins = dto
					.getSgstins();
			
			return anx2ReconResultsSummaryDao.reconResultsabsoluteMatchSummary(selectedSgtins,
					returnPeriod);
		}
		
		public List<Anx2ReconResultsMisMatchSummaryResDto> 
		misMatchSummary(EntityIRDto dto) {

			String returnPeriod = dto
					.getReturnPeriod();
			
			List<String> selectedSgtins = dto
					.getSgstins();
			
			return anx2ReconResultsSummaryDao.reconResultmisMatchSummary(selectedSgtins,
					returnPeriod);
		}

		@Override
		public List<Anx2ReconResultsPotentialMatchSummaryResDto> potentialMatchSummary(
				EntityIRDto dto) {
			String returnPeriod = dto
					.getReturnPeriod();
			
			List<String> selectedSgtins = dto
					.getSgstins();
			
			return anx2ReconResultsSummaryDao.reconResultpotentialMatchSummary(selectedSgtins,
					returnPeriod);
		}

}
