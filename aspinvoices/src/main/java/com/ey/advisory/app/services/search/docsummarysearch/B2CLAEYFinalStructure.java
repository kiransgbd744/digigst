package com.ey.advisory.app.services.search.docsummarysearch;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.Gstr1BasicSummarySectionDto;
import com.ey.advisory.app.docs.dto.Gstr1SummaryRespDto;

@Service("B2CLAEYFinalStructure")
public class B2CLAEYFinalStructure {
	

	public List<Gstr1SummaryRespDto> getB2claEyList(
			List<Gstr1SummaryRespDto> b2claEYList,
			List<Gstr1BasicSummarySectionDto> eySummaryListFromView) {

		// Filter the List for 9A
		List<Gstr1BasicSummarySectionDto> view9AFiltered = eySummaryListFromView
				.stream()
				.filter(p -> p.getTableSection().equalsIgnoreCase("9A"))
				.collect(Collectors.toList());
				
		
		//Set Values
		
		// If 9A filtered list is not null
		if (view9AFiltered != null & view9AFiltered.size() > 0) {
			// then filter default List for 9A
			List<Gstr1SummaryRespDto> default9AFiltered = b2claEYList.stream()
					.filter(p -> p.getTableSection().equalsIgnoreCase("9A"))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			default9AFiltered.forEach(default9A -> {
				// then remove it from List
				b2claEYList.remove(default9A);
			});

			// Iterate view list
			view9AFiltered.forEach(view9A -> {
				Gstr1SummaryRespDto summaryRespDto = new Gstr1SummaryRespDto();
				summaryRespDto.setTableSection(view9A.getTableSection());
				summaryRespDto.setRecords(view9A.getRecords());
				summaryRespDto.setTaxableValue(view9A.getTaxableValue());
				summaryRespDto.setTaxPayble(view9A.getTaxPayble());
				summaryRespDto.setInvValue(view9A.getInvValue());
				summaryRespDto.setIgst(view9A.getIgst());
			//	summaryRespDto.setSgst(view9A.getSgst());
			//	summaryRespDto.setCgst(view9A.getCgst());
				summaryRespDto.setCess(view9A.getCess());
				// Add 9A to the final list
				b2claEYList.add(summaryRespDto);
			});
		}
		// Sort the list in Ascending Order
		Collections.sort(b2claEYList, new Comparator<Gstr1SummaryRespDto>() {
			@Override
			public int compare(Gstr1SummaryRespDto respDto1,
					Gstr1SummaryRespDto respDto2) {
				return respDto1.getTableSection()
						.compareTo(respDto2.getTableSection());
			}
		});

		return b2claEYList;
	}


}
