package com.ey.advisory.app.services.docs.gstr2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.daos.client.BasicDocSummarySectionDao;
import com.ey.advisory.app.data.daos.client.gstr2.Gstr2BasicDocSummarySectionDao;
import com.ey.advisory.app.data.entities.client.BaseGstr1SummaryEntity;
import com.ey.advisory.app.docs.dto.Gstr1BasicSummarySectionDto;
import com.ey.advisory.app.docs.dto.gstr2.BaseGstr2SummaryEntity;
import com.ey.advisory.app.docs.dto.gstr2.Gstr2BasicSummarySectionDto;
import com.ey.advisory.app.docs.dto.gstr2.Gstr2BasicSummarySectionFetcher;

@Component("DefaultGstr2BasicSummarySectionFetcher")
public class DefaultGstr2BasicSummarySectionFetcher
		implements Gstr2BasicSummarySectionFetcher {
	
	@Autowired
	@Qualifier("Gstr2BasicDocSummarySectionDaoImpl")
	private Gstr2BasicDocSummarySectionDao gstr2BasicDocSummarySectionDao;

	@Override
	public List<Gstr2BasicSummarySectionDto> fetch(String sectionType,
			String subSectionType, List<String> gstins, List<Long> entityIds,
			int fromTaxPeriod, int toTaxPeriod) {
		// TODO Auto-generated method stub
		
		
		List<BaseGstr2SummaryEntity> result =
				gstr2BasicDocSummarySectionDao.loadBasicSummarySection(sectionType, 
								 gstins, entityIds, fromTaxPeriod, toTaxPeriod);	
				
				return convertGstr2SummaryListToDto(result);
	}
	private List<Gstr2BasicSummarySectionDto> convertGstr2SummaryListToDto(
			List<? extends BaseGstr2SummaryEntity> list) {

Map<String, Gstr2BasicSummarySectionDto> map = new HashMap<>();

for(BaseGstr2SummaryEntity summEntity : list) {
	
	Gstr2BasicSummarySectionDto sectionSmry = 
				map.get(summEntity.getTableSection());
	sectionSmry = (sectionSmry == null) ? 
			new Gstr2BasicSummarySectionDto(
					summEntity.getTableSection()) : sectionSmry;
				
	Gstr2BasicSummarySectionDto newObj = 
			new Gstr2BasicSummarySectionDto(
					summEntity.getTableSection(), 
					summEntity.getRecords(),
					summEntity.getTaxableValue(),
					summEntity.getIgst(),
					summEntity.getCgst(),
					summEntity.getSgst(),
					summEntity.getCess(),
					summEntity.getItcIgst(),
					summEntity.getItcSgst(),
					summEntity.getItcCgst(),
					summEntity.getItcCess());
					
	map.put(summEntity.getTableSection(), sectionSmry.add(newObj));
}
return new ArrayList<>(map.values());
}

	
	

}
