package com.ey.advisory.app.services.search.docsummarysearch;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.Gstr1BasicSummarySectionDto;
import com.ey.advisory.app.docs.dto.Gstr1SummaryRespDto;

@Service("B2CSEYFinalStructure")
public class B2CSEYFinalStructure {
	

	public List<Gstr1SummaryRespDto> getB2csEyList(
			List<Gstr1SummaryRespDto> b2csEYList,
			List<Gstr1BasicSummarySectionDto> eySummaryListFromView) {

		// Filter the List for 7A(1)
		List<Gstr1BasicSummarySectionDto> view7A1Filtered = eySummaryListFromView
				.stream()
				.filter(p -> p.getTableSection().equalsIgnoreCase("7A(1)"))
				.collect(Collectors.toList());
		
		// Filter the List for 7A(2)
		List<Gstr1BasicSummarySectionDto> view7A2Filtered = eySummaryListFromView
				.stream()
				.filter(p -> p.getTableSection().equalsIgnoreCase("7A(2)"))
				.collect(Collectors.toList());
		
		

		// Filter the List for 7B(1)
		List<Gstr1BasicSummarySectionDto> view7B1Filtered = eySummaryListFromView
				.stream()
				.filter(p -> p.getTableSection().equalsIgnoreCase("7B(1)"))
				.collect(Collectors.toList());	
		
		// Filter the List for 7B(1)
		List<Gstr1BasicSummarySectionDto> view7B2Filtered = eySummaryListFromView
				.stream()
				.filter(p -> p.getTableSection().equalsIgnoreCase("7B(2)"))
				.collect(Collectors.toList());
		
		//Set Values
		
		// If 7A(1) filtered list is not null
		if (view7A1Filtered != null & view7A1Filtered.size() > 0) {
			// then filter default List for 7A
			List<Gstr1SummaryRespDto> default7A1Filtered = b2csEYList.stream()
					.filter(p -> p.getTableSection().equalsIgnoreCase("7A(1)"))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			default7A1Filtered.forEach(default7A1 -> {
				// then remove it from List
				b2csEYList.remove(default7A1);
			});

			// Iterate view list
			view7A1Filtered.forEach(view7A1 -> {
				Gstr1SummaryRespDto summaryRespDto = new Gstr1SummaryRespDto();
				summaryRespDto.setTableSection(view7A1.getTableSection());
				summaryRespDto.setRecords(view7A1.getRecords());
				summaryRespDto.setTaxableValue(view7A1.getTaxableValue());
				summaryRespDto.setTaxPayble(view7A1.getTaxPayble());
			//	summaryRespDto.setInvValue(view7A.getInvValue());
				summaryRespDto.setIgst(view7A1.getIgst());
				summaryRespDto.setSgst(view7A1.getSgst());
				summaryRespDto.setCgst(view7A1.getCgst());
				summaryRespDto.setCess(view7A1.getCess());
				// Add 7A to the final list
				b2csEYList.add(summaryRespDto);
			});
		}

		// If 7A(2) filtered list is not null
				if (view7A2Filtered != null & view7A2Filtered.size() > 0) {
					// then filter default List for 7A
					List<Gstr1SummaryRespDto> default7A2Filtered = b2csEYList.stream()
							.filter(p -> p.getTableSection().equalsIgnoreCase("7A(2)"))
							.collect(Collectors.toList());

					// If the default filtered list is not null
					default7A2Filtered.forEach(default7A2 -> {
						// then remove it from List
						b2csEYList.remove(default7A2);
					});

					// Iterate view list
					view7A2Filtered.forEach(view7A2 -> {
						Gstr1SummaryRespDto summaryRespDto = new Gstr1SummaryRespDto();
						summaryRespDto.setTableSection(view7A2.getTableSection());
						summaryRespDto.setRecords(view7A2.getRecords());
						summaryRespDto.setTaxableValue(view7A2.getTaxableValue());
						summaryRespDto.setTaxPayble(view7A2.getTaxPayble());
					//	summaryRespDto.setInvValue(view7A.getInvValue());
						summaryRespDto.setIgst(view7A2.getIgst());
						summaryRespDto.setSgst(view7A2.getSgst());
						summaryRespDto.setCgst(view7A2.getCgst());
						summaryRespDto.setCess(view7A2.getCess());
						// Add 7A to the final list
						b2csEYList.add(summaryRespDto);
					});
				}

		// If 7B(1) filtered list is not null
		if (view7B1Filtered != null & view7B1Filtered.size() > 0) {
			// then filter default List for 7B
			List<Gstr1SummaryRespDto> default7B1Filtered = b2csEYList.stream()
					.filter(p -> p.getTableSection().equalsIgnoreCase("7B(1)"))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			default7B1Filtered.forEach(default7B1 -> {
				// then remove it from List
				b2csEYList.remove(default7B1);
			});

			// Iterate view list
			view7B1Filtered.forEach(view7B1 -> {
				Gstr1SummaryRespDto summaryRespDto = new Gstr1SummaryRespDto();
				summaryRespDto.setTableSection(view7B1.getTableSection());
				summaryRespDto.setRecords(view7B1.getRecords());
				summaryRespDto.setTaxableValue(view7B1.getTaxableValue());
				summaryRespDto.setTaxPayble(view7B1.getTaxPayble());
			//	summaryRespDto.setInvValue(view7B.getInvValue());
				summaryRespDto.setIgst(view7B1.getIgst());
				summaryRespDto.setSgst(view7B1.getSgst());
				summaryRespDto.setCgst(view7B1.getCgst());
				summaryRespDto.setCess(view7B1.getCess());
				// Add 7B to the final list
				b2csEYList.add(summaryRespDto);
			});
		}
		
		// If 7B(1) filtered list is not null
				if (view7B2Filtered != null & view7B2Filtered.size() > 0) {
					// then filter default List for 7B
					List<Gstr1SummaryRespDto> default7B2Filtered = b2csEYList.stream()
							.filter(p -> p.getTableSection().equalsIgnoreCase("7B(2)"))
							.collect(Collectors.toList());

					// If the default filtered list is not null
					default7B2Filtered.forEach(default7B2 -> {
						// then remove it from List
						b2csEYList.remove(default7B2);
					});

					// Iterate view list
					view7B2Filtered.forEach(view7B2 -> {
						Gstr1SummaryRespDto summaryRespDto = new Gstr1SummaryRespDto();
						summaryRespDto.setTableSection(view7B2.getTableSection());
						summaryRespDto.setRecords(view7B2.getRecords());
						summaryRespDto.setTaxableValue(view7B2.getTaxableValue());
						summaryRespDto.setTaxPayble(view7B2.getTaxPayble());
					//	summaryRespDto.setInvValue(view7B.getInvValue());
						summaryRespDto.setIgst(view7B2.getIgst());
						summaryRespDto.setSgst(view7B2.getSgst());
						summaryRespDto.setCgst(view7B2.getCgst());
						summaryRespDto.setCess(view7B2.getCess());
						// Add 7B to the final list
						b2csEYList.add(summaryRespDto);
					});
				}


		// Sort the list in Ascending Order
		Collections.sort(b2csEYList, new Comparator<Gstr1SummaryRespDto>() {
			@Override
			public int compare(Gstr1SummaryRespDto respDto1,
					Gstr1SummaryRespDto respDto2) {
				return respDto1.getTableSection()
						.compareTo(respDto2.getTableSection());
			}
		});

		return b2csEYList;
	}

}
