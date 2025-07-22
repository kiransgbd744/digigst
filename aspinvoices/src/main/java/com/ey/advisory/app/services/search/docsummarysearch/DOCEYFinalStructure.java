package com.ey.advisory.app.services.search.docsummarysearch;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.Gstr1DocIssuedSummarySectionDto;

@Service("DOCEYFinalStructure")
public class DOCEYFinalStructure {


	public List<Gstr1DocIssuedSummarySectionDto> getDocEyList(
			List<Gstr1DocIssuedSummarySectionDto> docEYList,
			List<Gstr1DocIssuedSummarySectionDto> eySummaryListFromView) {

		// Filter the List for 11B
		List<Gstr1DocIssuedSummarySectionDto> view13Filtered = eySummaryListFromView
				.stream()
				.filter(p -> p.getTableSection().equalsIgnoreCase("13"))
				.collect(Collectors.toList());
				
		
		//Set Values
		
		// If 11B filtered list is not null
		if (view13Filtered != null & view13Filtered.size() > 0) {
			// then filter default List for 11B
			List<Gstr1DocIssuedSummarySectionDto> default13Filtered = docEYList.stream()
					.filter(p -> p.getTableSection().equalsIgnoreCase("13"))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			default13Filtered.forEach(default13 -> {
				// then remove it from List
				docEYList.remove(default13);
			});

			// Iterate view list
			view13Filtered.forEach(view13 -> {
				Gstr1DocIssuedSummarySectionDto summaryRespDto = new Gstr1DocIssuedSummarySectionDto();
				summaryRespDto.setTableSection(view13.getTableSection());
				summaryRespDto.setRecords(view13.getRecords());
				summaryRespDto.setTotalIssued(view13.getTotalIssued());
				summaryRespDto.setNetIssued(view13.getNetIssued());
				summaryRespDto.setCancelled(view13.getCancelled());
				
				// Add 13 to the final list
				docEYList.add(summaryRespDto);
			});
		}
		// Sort the list in Ascending Order
		Collections.sort(docEYList, new Comparator<Gstr1DocIssuedSummarySectionDto>() {
			@Override
			public int compare(Gstr1DocIssuedSummarySectionDto respDto1,
					Gstr1DocIssuedSummarySectionDto respDto2) {
				return respDto1.getTableSection()
						.compareTo(respDto2.getTableSection());
			}
		});

		return docEYList;
	}

}
