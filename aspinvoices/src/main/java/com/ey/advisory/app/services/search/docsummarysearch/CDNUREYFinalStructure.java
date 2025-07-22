package com.ey.advisory.app.services.search.docsummarysearch;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.Gstr1BasicSummarySectionDto;
import com.ey.advisory.app.docs.dto.Gstr1SummaryRespDto;

@Service("CDNUREYFinalStructure")
public class CDNUREYFinalStructure {
	

	public List<Gstr1SummaryRespDto> getcdnurEyList(
			List<Gstr1SummaryRespDto> cdnurEYList,
			List<Gstr1BasicSummarySectionDto> eySummaryListFromView) {

		// Filter the List for 9B
		List<Gstr1BasicSummarySectionDto> view9BFiltered = eySummaryListFromView
				.stream()
				.filter(p -> p.getTableSection().equalsIgnoreCase("9B"))
				.collect(Collectors.toList());
		//Set Values
		
		// If 7A filtered list is not null
		if (view9BFiltered != null & view9BFiltered.size() > 0) {
			// then filter default List for 7A
			List<Gstr1SummaryRespDto> default9BFiltered = cdnurEYList.stream()
					.filter(p -> p.getTableSection().equalsIgnoreCase("9B"))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			default9BFiltered.forEach(default9B -> {
				// then remove it from List
				cdnurEYList.remove(default9B);
			});

			// Iterate view list
			view9BFiltered.forEach(view9B -> {
				Gstr1SummaryRespDto summaryRespDto = new Gstr1SummaryRespDto();
				summaryRespDto.setTableSection(view9B.getTableSection());
				summaryRespDto.setRecords(view9B.getRecords());
				summaryRespDto.setTaxableValue(view9B.getTaxableValue());
				summaryRespDto.setTaxPayble(view9B.getTaxPayble());
				summaryRespDto.setInvValue(view9B.getInvValue());
				summaryRespDto.setIgst(view9B.getIgst());
				summaryRespDto.setSgst(view9B.getSgst());
				summaryRespDto.setCgst(view9B.getCgst());
				summaryRespDto.setCess(view9B.getCess());
				// Add 9B to the final list
				cdnurEYList.add(summaryRespDto);
			});
		}
		// Sort the list in Ascending Order
		Collections.sort(cdnurEYList, new Comparator<Gstr1SummaryRespDto>() {
			@Override
			public int compare(Gstr1SummaryRespDto respDto1,
					Gstr1SummaryRespDto respDto2) {
				return respDto1.getTableSection()
						.compareTo(respDto2.getTableSection());
			}
		});

		return cdnurEYList;
	}



}
