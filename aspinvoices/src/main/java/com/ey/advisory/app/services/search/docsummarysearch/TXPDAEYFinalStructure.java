package com.ey.advisory.app.services.search.docsummarysearch;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.Gstr1BasicSummarySectionDto;
import com.ey.advisory.app.docs.dto.Gstr1SummaryRespDto;

@Service("TXPDAEYFinalStructure")
public class TXPDAEYFinalStructure {
	

	public List<Gstr1SummaryRespDto> gettxpdaEyList(
			List<Gstr1SummaryRespDto> txpdaEYList,
			List<Gstr1BasicSummarySectionDto> eySummaryListFromView) {

		// Filter the List for 11A
		List<Gstr1BasicSummarySectionDto> view11AFiltered = eySummaryListFromView
				.stream()
				.filter(p -> p.getTableSection().equalsIgnoreCase("11B"))
				.collect(Collectors.toList());
				
		
		//Set Values
		
		// If 11A filtered list is not null
		if (view11AFiltered != null & view11AFiltered.size() > 0) {
			// then filter default List for 11B
			List<Gstr1SummaryRespDto> default11BFiltered = txpdaEYList.stream()
					.filter(p -> p.getTableSection().equalsIgnoreCase("11B"))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			default11BFiltered.forEach(default11B -> {
				// then remove it from List
				txpdaEYList.remove(default11B);
			});

			// Iterate view list
			view11AFiltered.forEach(view11A -> {
				Gstr1SummaryRespDto summaryRespDto = new Gstr1SummaryRespDto();
				summaryRespDto.setTableSection(view11A.getTableSection());
				summaryRespDto.setRecords(view11A.getRecords());
				summaryRespDto.setTaxableValue(view11A.getTaxableValue());
				summaryRespDto.setTaxPayble(view11A.getTaxPayble());
				summaryRespDto.setInvValue(view11A.getInvValue());
				summaryRespDto.setIgst(view11A.getIgst());
				summaryRespDto.setSgst(view11A.getSgst());
				summaryRespDto.setCgst(view11A.getCgst());
				summaryRespDto.setCess(view11A.getCess());
				// Add 11B to the final list
				txpdaEYList.add(summaryRespDto);
			});
		}
		// Sort the list in Ascending Order
		Collections.sort(txpdaEYList, new Comparator<Gstr1SummaryRespDto>() {
			@Override
			public int compare(Gstr1SummaryRespDto respDto1,
					Gstr1SummaryRespDto respDto2) {
				return respDto1.getTableSection()
						.compareTo(respDto2.getTableSection());
			}
		});

		return txpdaEYList;
	}

}

