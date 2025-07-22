package com.ey.advisory.app.services.search.docsummarysearch;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.Gstr1BasicSummarySectionDto;
import com.ey.advisory.app.docs.dto.Gstr1SummaryRespDto;

@Service("ATAEYFinalStructure")
public class ATAEYFinalStructure {

	public List<Gstr1SummaryRespDto> getAtAEyList(
			List<Gstr1SummaryRespDto> ataEYList,
			List<Gstr1BasicSummarySectionDto> eySummaryListFromView) {

		// Filter the List for 11B
		List<Gstr1BasicSummarySectionDto> view11BFiltered = eySummaryListFromView
				.stream()
				.filter(p -> p.getTableSection().equalsIgnoreCase("11A"))
				.collect(Collectors.toList());
				
		
		//Set Values
		
		// If 11A filtered list is not null
		if (view11BFiltered != null & view11BFiltered.size() > 0) {
			// then filter default List for 11A
			List<Gstr1SummaryRespDto> default11BFiltered = ataEYList.stream()
					.filter(p -> p.getTableSection().equalsIgnoreCase("11A"))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			default11BFiltered.forEach(default11B -> {
				// then remove it from List
				ataEYList.remove(default11B);
			});

			// Iterate view list
			view11BFiltered.forEach(view11B -> {
				Gstr1SummaryRespDto summaryRespDto = new Gstr1SummaryRespDto();
				summaryRespDto.setTableSection(view11B.getTableSection());
				summaryRespDto.setRecords(view11B.getRecords());
				summaryRespDto.setTaxableValue(view11B.getTaxableValue());
				summaryRespDto.setTaxPayble(view11B.getTaxPayble());
				summaryRespDto.setInvValue(view11B.getInvValue());
				summaryRespDto.setIgst(view11B.getIgst());
				summaryRespDto.setSgst(view11B.getSgst());
				summaryRespDto.setCgst(view11B.getCgst());
				summaryRespDto.setCess(view11B.getCess());
				// Add 11A to the final list
				ataEYList.add(summaryRespDto);
			});
		}
		// Sort the list in Ascending Order
		Collections.sort(ataEYList, new Comparator<Gstr1SummaryRespDto>() {
			@Override
			public int compare(Gstr1SummaryRespDto respDto1,
					Gstr1SummaryRespDto respDto2) {
				return respDto1.getTableSection()
						.compareTo(respDto2.getTableSection());
			}
		});

		return ataEYList;
	}
}
