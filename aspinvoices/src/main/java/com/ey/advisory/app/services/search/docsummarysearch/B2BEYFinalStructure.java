package com.ey.advisory.app.services.search.docsummarysearch;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.Gstr1BasicSummarySectionDto;
import com.ey.advisory.app.docs.dto.Gstr1SummaryRespDto;

@Service("B2BEYFinalStructure")
public class B2BEYFinalStructure {
	
	
	public List<Gstr1SummaryRespDto> getB2BEyList(
			List<Gstr1SummaryRespDto> b2bEYList,
			List<Gstr1BasicSummarySectionDto> eySummaryListFromView) {

		// Filter the List for 4A
		List<Gstr1BasicSummarySectionDto> view4AFiltered = eySummaryListFromView
				.stream()
				.filter(p -> p.getTableSection().equalsIgnoreCase("4A"))
				.collect(Collectors.toList());

		// Filter the List for 4B
		List<Gstr1BasicSummarySectionDto> view4bFiltered = eySummaryListFromView
				.stream()
				.filter(p -> p.getTableSection().equalsIgnoreCase("4B"))
				.collect(Collectors.toList());	
		
		
		// Filter the List for 4C
		List<Gstr1BasicSummarySectionDto> view4CFiltered = eySummaryListFromView
				.stream()
				.filter(p -> p.getTableSection().equalsIgnoreCase("4C"))
				.collect(Collectors.toList());
		
		// Filter the List for 6B
		List<Gstr1BasicSummarySectionDto> view6BFiltered = eySummaryListFromView
				.stream()
				.filter(p -> p.getTableSection().equalsIgnoreCase("6B"))
				.collect(Collectors.toList());

		// Filter the List for 6C
		List<Gstr1BasicSummarySectionDto> view6CFiltered = eySummaryListFromView
				.stream()
				.filter(p -> p.getTableSection().equalsIgnoreCase("6C"))
				.collect(Collectors.toList());
				
		
		
		//Set Values
		
		// If 4A filtered list is not null
		if (view4AFiltered != null & view4AFiltered.size() > 0) {
			// then filter default List for 4A
			List<Gstr1SummaryRespDto> default4AFiltered = b2bEYList.stream()
					.filter(p -> p.getTableSection().equalsIgnoreCase("4A"))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			default4AFiltered.forEach(default4A -> {
				// then remove it from List
				b2bEYList.remove(default4A);
			});

			// Iterate view list
			view4AFiltered.forEach(view4A -> {
				Gstr1SummaryRespDto summaryRespDto = new Gstr1SummaryRespDto();
				summaryRespDto.setTableSection(view4A.getTableSection());
				summaryRespDto.setRecords(view4A.getRecords());
				summaryRespDto.setTaxableValue(view4A.getTaxableValue());
				summaryRespDto.setTaxPayble(view4A.getTaxPayble());
				summaryRespDto.setInvValue(view4A.getInvValue());
				summaryRespDto.setIgst(view4A.getIgst());
				summaryRespDto.setSgst(view4A.getSgst());
				summaryRespDto.setCgst(view4A.getCgst());
				summaryRespDto.setCess(view4A.getCess());
				// Add 4A to the final list
				b2bEYList.add(summaryRespDto);
			});
		}

		//If 4B filtered is not null
		if (view4bFiltered != null & view4bFiltered.size() > 0) {
			List<Gstr1SummaryRespDto> default4BFiltered = b2bEYList.stream()
					.filter(p -> p.getTableSection().equalsIgnoreCase("4B"))
					.collect(Collectors.toList());
			default4BFiltered.forEach(default4B -> {
				b2bEYList.remove(default4B);
			});
			view4bFiltered.forEach(view4B -> {
				Gstr1SummaryRespDto summaryRespDto = new Gstr1SummaryRespDto();
				summaryRespDto.setTableSection(view4B.getTableSection());
				summaryRespDto.setRecords(view4B.getRecords());
				summaryRespDto.setTaxableValue(view4B.getTaxableValue());
				summaryRespDto.setTaxPayble(view4B.getTaxPayble());
				summaryRespDto.setInvValue(view4B.getInvValue());
				summaryRespDto.setIgst(view4B.getIgst());
				summaryRespDto.setSgst(view4B.getSgst());
				summaryRespDto.setCgst(view4B.getCgst());
				summaryRespDto.setCess(view4B.getCess());
				// Add 4B to the final list
				b2bEYList.add(summaryRespDto);
			});
		}
		
		
		if (view4CFiltered != null & view4CFiltered.size() > 0) {
			// then filter default List for 4A
			List<Gstr1SummaryRespDto> default4CFiltered = b2bEYList.stream()
					.filter(p -> p.getTableSection().equalsIgnoreCase("4C"))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			default4CFiltered.forEach(default4C -> {
				// then remove it from List
				b2bEYList.remove(default4C);
			});

			// Iterate view list
			view4CFiltered.forEach(view4C -> {
				Gstr1SummaryRespDto summaryRespDto = new Gstr1SummaryRespDto();
				summaryRespDto.setTableSection(view4C.getTableSection());
				summaryRespDto.setRecords(view4C.getRecords());
				summaryRespDto.setTaxableValue(view4C.getTaxableValue());
				summaryRespDto.setTaxPayble(view4C.getTaxPayble());
				summaryRespDto.setInvValue(view4C.getInvValue());
				summaryRespDto.setIgst(view4C.getIgst());
				summaryRespDto.setSgst(view4C.getSgst());
				summaryRespDto.setCgst(view4C.getCgst());
				summaryRespDto.setCess(view4C.getCess());
				// Add 4C to the final list
				b2bEYList.add(summaryRespDto);
			});
		}
		
		
		if (view6BFiltered != null & view6BFiltered.size() > 0) {
			// then filter default List for 6B
			List<Gstr1SummaryRespDto> default6BFiltered = b2bEYList.stream()
					.filter(p -> p.getTableSection().equalsIgnoreCase("6B"))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			default6BFiltered.forEach(default6B -> {
				// then remove it from List
				b2bEYList.remove(default6B);
			});

			// Iterate view list
			view6BFiltered.forEach(view6B -> {
				Gstr1SummaryRespDto summaryRespDto = new Gstr1SummaryRespDto();
				summaryRespDto.setTableSection(view6B.getTableSection());
				summaryRespDto.setRecords(view6B.getRecords());
				summaryRespDto.setTaxableValue(view6B.getTaxableValue());
				summaryRespDto.setTaxPayble(view6B.getTaxPayble());
				summaryRespDto.setInvValue(view6B.getInvValue());
				summaryRespDto.setIgst(view6B.getIgst());
				summaryRespDto.setSgst(view6B.getSgst());
				summaryRespDto.setCgst(view6B.getCgst());
				summaryRespDto.setCess(view6B.getCess());
				// Add 6B to the final list
				b2bEYList.add(summaryRespDto);
			});
		}
		
		if (view6CFiltered != null & view6CFiltered.size() > 0) {
			// then filter default List for 6C
			List<Gstr1SummaryRespDto> default6CFiltered = b2bEYList.stream()
					.filter(p -> p.getTableSection().equalsIgnoreCase("6C"))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			default6CFiltered.forEach(default6C -> {
				// then remove it from List
				b2bEYList.remove(default6C);
			});

			// Iterate view list
			view6CFiltered.forEach(view6C -> {
				Gstr1SummaryRespDto summaryRespDto = new Gstr1SummaryRespDto();
				summaryRespDto.setTableSection(view6C.getTableSection());
				summaryRespDto.setRecords(view6C.getRecords());
				summaryRespDto.setTaxableValue(view6C.getTaxableValue());
				summaryRespDto.setTaxPayble(view6C.getTaxPayble());
				summaryRespDto.setInvValue(view6C.getInvValue());
				summaryRespDto.setIgst(view6C.getIgst());
				summaryRespDto.setSgst(view6C.getSgst());
				summaryRespDto.setCgst(view6C.getCgst());
				summaryRespDto.setCess(view6C.getCess());
				// Add 6C to the final list
				b2bEYList.add(summaryRespDto);
			});
		}
	
		// Sort the list in Ascending Order
		Collections.sort(b2bEYList, new Comparator<Gstr1SummaryRespDto>() {
			@Override
			public int compare(Gstr1SummaryRespDto respDto1,
					Gstr1SummaryRespDto respDto2) {
				return respDto1.getTableSection()
						.compareTo(respDto2.getTableSection());
			}
		});

		return b2bEYList;
	}

}
