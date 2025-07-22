package com.ey.advisory.app.services.search.docsummarysearch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.daos.client.BasicDocSummaryNilSectionDao;
import com.ey.advisory.app.docs.dto.Gstr1DocIssuedSummarySectionDto;
import com.ey.advisory.app.docs.dto.Gstr1NilRatedSummarySectionDto;

@Component("DefaultGstr1BasicSummaryNilSectionFetcher")
public class DefaultGstr1BasicSummaryNilSectionFetcher 
						implements Gstr1BasicSummaryNilSectionFetcher{

	@Autowired
	@Qualifier("BasicDocSummaryNilSectionDaoImpl")
	private BasicDocSummaryNilSectionDao basicDocSummarySectionDao;
	
	
	@Override
	public List<Gstr1NilRatedSummarySectionDto> fetch(String sectionType,
			String subSectionType, List<String> gstins, List<Long> entityIds,
			int fromTaxPeriod, int toTaxPeriod) {
		// TODO Auto-generated method stub
		
		List<Gstr1NilRatedSummarySectionDto> result = 
				basicDocSummarySectionDao.loadBasicSummarySection(
				sectionType,gstins, entityIds, fromTaxPeriod, toTaxPeriod);
		return convertGstr1SummaryListToDto(result);
		
	}
	private List<Gstr1NilRatedSummarySectionDto> convertGstr1SummaryListToDto(
			List<Gstr1NilRatedSummarySectionDto> list) {
		// TODO Auto-generated method stub
		Map<String, Gstr1NilRatedSummarySectionDto> map = new HashMap<>();
		/*List<Gstr1NilRatedSummarySectionDto> map = 
				new ArrayList<Gstr1NilRatedSummarySectionDto>();*/
		for(Gstr1NilRatedSummarySectionDto summEntity : list) {
			
			Gstr1NilRatedSummarySectionDto sectionSmry = 
					map.get(summEntity.getTableSection());
		sectionSmry = (sectionSmry == null) ? 
				new Gstr1NilRatedSummarySectionDto(
						summEntity.getTableSection()) : sectionSmry;
		
						Gstr1NilRatedSummarySectionDto newObj = 
					new Gstr1NilRatedSummarySectionDto(
							summEntity.getTableSection(),
							summEntity.getRecordCount(),
							summEntity.getTotalExempted(),
							summEntity.getTotalNilRated(),
							summEntity.getTotalNonGST());
			map.put(summEntity.getTableSection(), sectionSmry.add(newObj));
		}
		return new ArrayList<>(map.values());
	}


	
	
}
