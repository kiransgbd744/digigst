package com.ey.advisory.app.services.search.docsummarysearch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.daos.client.BasicDocSummaryDocSectionDao;
import com.ey.advisory.app.docs.dto.Gstr1DocIssuedSummarySectionDto;

@Component("DefaultGstr1BasicSummaryDocSectionFetcher")
public class DefaultGstr1BasicSummaryDocSectionFetcher 
		 	implements Gstr1BasicSummaryDocSectionFetcher{
	
	
	@Autowired
	@Qualifier("BasicDocSummaryDocSectionDaoImpl")
	private BasicDocSummaryDocSectionDao basicDocSummarySectionDao;

	@Override
	public List<Gstr1DocIssuedSummarySectionDto> fetch(String sectionType,
			String subSectionType, List<String> gstins, List<Long> entityIds,
			int fromTaxPeriod, int toTaxPeriod) {
		// TODO Auto-generated method stub
		
		List<Gstr1DocIssuedSummarySectionDto> result = 
				basicDocSummarySectionDao.loadBasicSummarySection(sectionType, 
								 gstins, entityIds, fromTaxPeriod, toTaxPeriod);
		return convertGstr1SummaryListToDto(result);
	}

	private List<Gstr1DocIssuedSummarySectionDto> convertGstr1SummaryListToDto(
			List<Gstr1DocIssuedSummarySectionDto> list) {
		// TODO Auto-generated method stub
		Map<String, Gstr1DocIssuedSummarySectionDto> map = new HashMap<>();
		/*List<Gstr1DocIssuedSummarySectionDto> map = 
				new ArrayList<Gstr1DocIssuedSummarySectionDto>();*/
		for(Gstr1DocIssuedSummarySectionDto summEntity : list) {
			//Gstr1DocIssuedSummarySectionDto sectionSmry= 
			//new Gstr1DocIssuedSummarySectionDto();			
			
			Gstr1DocIssuedSummarySectionDto sectionSmry = 
					map.get(summEntity.getTableSection());
		sectionSmry = (sectionSmry == null) ? 
				new Gstr1DocIssuedSummarySectionDto(
						summEntity.getTableSection()) : sectionSmry;
						
						
		
			Gstr1DocIssuedSummarySectionDto newObj = 
					new Gstr1DocIssuedSummarySectionDto(
							summEntity.getTableSection(),
							summEntity.getRecords(),
							summEntity.getTotalIssued(),
							summEntity.getCancelled(),
							summEntity.getNetIssued());
			
			map.put(summEntity.getTableSection(), sectionSmry.add(newObj));
			
			
		}
		return new ArrayList<>(map.values());
	}
}