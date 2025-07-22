package com.ey.advisory.app.services.search.docsummarysearch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.daos.client.BasicDocSummarySectionDao;
import com.ey.advisory.app.data.entities.client.BaseGstr1SummaryEntity;
import com.ey.advisory.app.docs.dto.Gstr1BasicSummarySectionDto;

/**
 * 
 * @author Mahesh.Golla
 *
 */

@Component("DefaultGstr1BasicSummarySectionFetcher")
public class DefaultGstr1BasicSummarySectionFetcher 
			implements Gstr1BasicSummarySectionFetcher {
	
		
	@Autowired
	@Qualifier("BasicDocSummarySectionDaoImpl")
	private BasicDocSummarySectionDao basicDocSummarySectionDao;

	@Override
	public List<Gstr1BasicSummarySectionDto> fetch(String sectionType,
			String subSectionType, List<String> gstins, List<Long> entityIds, 
			int fromTaxPeriod, int toTaxPeriod) {
	

				List<BaseGstr1SummaryEntity> result =
				basicDocSummarySectionDao.loadBasicSummarySection(sectionType, 
								 gstins, entityIds, fromTaxPeriod, toTaxPeriod);	
				
				return convertGstr1SummaryListToDto(result);

			// throw new AppException("Invalid Section Type Encountered!!");
		}
		
		private List<Gstr1BasicSummarySectionDto> convertGstr1SummaryListToDto(
						List<? extends BaseGstr1SummaryEntity> list) {
		
			Map<String, Gstr1BasicSummarySectionDto> map = new HashMap<>();
			
			for(BaseGstr1SummaryEntity summEntity : list) {
				
				Gstr1BasicSummarySectionDto sectionSmry = 
							map.get(summEntity.getTableSection());
				sectionSmry = (sectionSmry == null) ? 
						new Gstr1BasicSummarySectionDto(
								summEntity.getTableSection()) : sectionSmry;
							
				Gstr1BasicSummarySectionDto newObj = 
						new Gstr1BasicSummarySectionDto(
								summEntity.getTableSection(), 
								summEntity.getRecordCount(),
								summEntity.getTaxbleValue(),
								summEntity.getTaxPayable(),
								summEntity.getInvoiceValue(),
								summEntity.getIgstAmt(),
								summEntity.getCgstAmt(),
								summEntity.getSgstAmt(),
								summEntity.getCessAmt());
				map.put(summEntity.getTableSection(), sectionSmry.add(newObj));
			}
			return new ArrayList<>(map.values());
			}
			}
