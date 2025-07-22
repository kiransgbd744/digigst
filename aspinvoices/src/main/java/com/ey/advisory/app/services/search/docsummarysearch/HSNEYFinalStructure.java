package com.ey.advisory.app.services.search.docsummarysearch;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.Gstr1BasicSummarySectionDto;
import com.ey.advisory.app.docs.dto.Gstr1SummaryRespDto;

@Service("HSNEYFinalStructure")
public class HSNEYFinalStructure {
	

	public List<Gstr1SummaryRespDto> gethsnEyList(
			List<Gstr1SummaryRespDto> hsnEYList,
			List<Gstr1BasicSummarySectionDto> eySummaryListFromView) {

		// Filter the List for 9B
		List<Gstr1BasicSummarySectionDto> view12Filtered = eySummaryListFromView
				.stream()
				.filter(p -> p.getTableSection().equalsIgnoreCase("12"))
				.collect(Collectors.toList());
		//Set Values
		
		// If 7A filtered list is not null
		if (view12Filtered != null & view12Filtered.size() > 0) {
			// then filter default List for 7A
			List<Gstr1SummaryRespDto> default12Filtered = hsnEYList.stream()
					.filter(p -> p.getTableSection().equalsIgnoreCase("12"))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			default12Filtered.forEach(default12 -> {
				// then remove it from List
				hsnEYList.remove(default12);
			});

			// Iterate view list
			view12Filtered.forEach(view12 -> {
				Gstr1SummaryRespDto summaryRespDto = new Gstr1SummaryRespDto();
				summaryRespDto.setTableSection(view12.getTableSection());
				summaryRespDto.setRecords(view12.getRecords());
				summaryRespDto.setTaxableValue(view12.getTaxableValue());
				summaryRespDto.setTaxPayble(view12.getTaxPayble());
				summaryRespDto.setInvValue(view12.getInvValue());
				summaryRespDto.setIgst(view12.getIgst());
				summaryRespDto.setSgst(view12.getSgst());
				summaryRespDto.setCgst(view12.getCgst());
				summaryRespDto.setCess(view12.getCess());
				// Add 12 to the final list
				hsnEYList.add(summaryRespDto);
			});
		}
		// Sort the list in Ascending Order
		Collections.sort(hsnEYList, new Comparator<Gstr1SummaryRespDto>() {
			@Override
			public int compare(Gstr1SummaryRespDto respDto1,
					Gstr1SummaryRespDto respDto2) {
				return respDto1.getTableSection()
						.compareTo(respDto2.getTableSection());
			}
		});

		return hsnEYList;
	}



}
