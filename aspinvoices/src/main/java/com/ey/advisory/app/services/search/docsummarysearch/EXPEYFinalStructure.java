package com.ey.advisory.app.services.search.docsummarysearch;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.Gstr1BasicSummarySectionDto;
import com.ey.advisory.app.docs.dto.Gstr1SummaryRespDto;

@Service("EXPEYFinalStructure")
public class EXPEYFinalStructure {
	

	public List<Gstr1SummaryRespDto> getexpEyList(
			List<Gstr1SummaryRespDto> expEYList,
			List<Gstr1BasicSummarySectionDto> eySummaryListFromView) {

		// Filter the List for 6A
		List<Gstr1BasicSummarySectionDto> view6AFiltered = eySummaryListFromView
				.stream()
				.filter(p -> p.getTableSection().equalsIgnoreCase("6A"))
				.collect(Collectors.toList());
		//Set Values
		
		// If 6A filtered list is not null
		if (view6AFiltered != null & view6AFiltered.size() > 0) {
			// then filter default List for 6A
			List<Gstr1SummaryRespDto> default6AFiltered = expEYList.stream()
					.filter(p -> p.getTableSection().equalsIgnoreCase("6A"))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			default6AFiltered.forEach(default6A -> {
				// then remove it from List
				expEYList.remove(default6A);
			});

			// Iterate view list
			view6AFiltered.forEach(view6A -> {
				Gstr1SummaryRespDto summaryRespDto = new Gstr1SummaryRespDto();
				summaryRespDto.setTableSection(view6A.getTableSection());
				summaryRespDto.setRecords(view6A.getRecords());
				summaryRespDto.setTaxableValue(view6A.getTaxableValue());
				summaryRespDto.setTaxPayble(view6A.getTaxPayble());
				summaryRespDto.setInvValue(view6A.getInvValue());
				summaryRespDto.setIgst(view6A.getIgst());
			//	summaryRespDto.setSgst(view6A.getSgst());
			//	summaryRespDto.setCgst(view6A.getCgst());
				summaryRespDto.setCess(view6A.getCess());
				// Add 9B to the final list
				expEYList.add(summaryRespDto);
			});
		}
		// Sort the list in Ascending Order
		Collections.sort(expEYList, new Comparator<Gstr1SummaryRespDto>() {
			@Override
			public int compare(Gstr1SummaryRespDto respDto1,
					Gstr1SummaryRespDto respDto2) {
				return respDto1.getTableSection()
						.compareTo(respDto2.getTableSection());
			}
		});

		return expEYList;
	}

}
