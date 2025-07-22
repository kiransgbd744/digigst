package com.ey.advisory.app.services.search.docsummarysearch;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.Gstr1BasicSummarySectionDto;
import com.ey.advisory.app.docs.dto.Gstr1SummaryRespDto;

@Service("B2CLEYFinalStructure")
public class B2CLEYFinalStructure {
	
	public List<Gstr1SummaryRespDto> getB2clEyList(
			List<Gstr1SummaryRespDto> b2clEYList,
			List<Gstr1BasicSummarySectionDto> eySummaryListFromView) {

		// Filter the List for 4A
		List<Gstr1BasicSummarySectionDto> view5AFiltered = eySummaryListFromView
				.stream()
				.filter(p -> p.getTableSection().equalsIgnoreCase("5A"))
				.collect(Collectors.toList());

		// Filter the List for 4B
		List<Gstr1BasicSummarySectionDto> view5BFiltered = eySummaryListFromView
				.stream()
				.filter(p -> p.getTableSection().equalsIgnoreCase("5B"))
				.collect(Collectors.toList());	
		
		//Set Values
		
		// If 5A filtered list is not null
		if (view5AFiltered != null & view5AFiltered.size() > 0) {
			// then filter default List for 4A
			List<Gstr1SummaryRespDto> default5AFiltered = b2clEYList.stream()
					.filter(p -> p.getTableSection().equalsIgnoreCase("5A"))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			default5AFiltered.forEach(default5A -> {
				// then remove it from List
				b2clEYList.remove(default5A);
			});

			// Iterate view list
			view5AFiltered.forEach(view5A -> {
				Gstr1SummaryRespDto summaryRespDto = new Gstr1SummaryRespDto();
				summaryRespDto.setTableSection(view5A.getTableSection());
				summaryRespDto.setRecords(view5A.getRecords());
				summaryRespDto.setTaxableValue(view5A.getTaxableValue());
				summaryRespDto.setTaxPayble(view5A.getTaxPayble());
				summaryRespDto.setInvValue(view5A.getInvValue());
				summaryRespDto.setIgst(view5A.getIgst());
				summaryRespDto.setSgst(view5A.getSgst());
				summaryRespDto.setCgst(view5A.getCgst());
				summaryRespDto.setCess(view5A.getCess());
				// Add 4A to the final list
				b2clEYList.add(summaryRespDto);
			});
		}
		
		// If 5B filtered list is not null
		if (view5BFiltered != null & view5BFiltered.size() > 0) {
			// then filter default List for 4A
			List<Gstr1SummaryRespDto> default5BFiltered = b2clEYList.stream()
					.filter(p -> p.getTableSection().equalsIgnoreCase("5B"))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			default5BFiltered.forEach(default5B -> {
				// then remove it from List
				b2clEYList.remove(default5B);
			});

			// Iterate view list
			view5BFiltered.forEach(view5B -> {
				Gstr1SummaryRespDto summaryRespDto = new Gstr1SummaryRespDto();
				summaryRespDto.setTableSection(view5B.getTableSection());
				summaryRespDto.setRecords(view5B.getRecords());
				summaryRespDto.setTaxableValue(view5B.getTaxableValue());
				summaryRespDto.setTaxPayble(view5B.getTaxPayble());
				summaryRespDto.setInvValue(view5B.getInvValue());
				summaryRespDto.setIgst(view5B.getIgst());
				//summaryRespDto.setSgst(view5B.getSgst());
				//summaryRespDto.setCgst(view5B.getCgst());
				summaryRespDto.setCess(view5B.getCess());
				// Add 4A to the final list
				b2clEYList.add(summaryRespDto);
			});
		}

		// Sort the list in Ascending Order
		Collections.sort(b2clEYList, new Comparator<Gstr1SummaryRespDto>() {
			@Override
			public int compare(Gstr1SummaryRespDto respDto1,
					Gstr1SummaryRespDto respDto2) {
				return respDto1.getTableSection()
						.compareTo(respDto2.getTableSection());
			}
		});

		return b2clEYList;
	}



}
