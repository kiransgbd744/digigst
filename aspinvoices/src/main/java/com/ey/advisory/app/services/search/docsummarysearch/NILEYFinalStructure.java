package com.ey.advisory.app.services.search.docsummarysearch;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.Gstr1NilRatedSummarySectionDto;

@Service("NILEYFinalStructure")
public class NILEYFinalStructure {
	

	public List<Gstr1NilRatedSummarySectionDto> getNilEyList(
			List<Gstr1NilRatedSummarySectionDto> nilEYList,
			List<Gstr1NilRatedSummarySectionDto> eySummaryListFromView) {

		
				List<Gstr1NilRatedSummarySectionDto> view8AFiltered = 
						eySummaryListFromView
				.stream()
				.filter(p -> p.getTableSection().equalsIgnoreCase("8A"))
				.collect(Collectors.toList());

				// Filter the List for 8B
				List<Gstr1NilRatedSummarySectionDto> view8BFiltered = 
						eySummaryListFromView
				.stream()
				.filter(p -> p.getTableSection().equalsIgnoreCase("8B"))
				.collect(Collectors.toList());	


				// Filter the List for 8C
				List<Gstr1NilRatedSummarySectionDto> view8CFiltered = 
						eySummaryListFromView
				.stream()
				.filter(p -> p.getTableSection().equalsIgnoreCase("8C"))
				.collect(Collectors.toList());

				// Filter the List for 8D
				List<Gstr1NilRatedSummarySectionDto> view8DFiltered = 
						eySummaryListFromView
				.stream()
				.filter(p -> p.getTableSection().equalsIgnoreCase("8D"))
				.collect(Collectors.toList());


				//Set Values

				// If 8A filtered list is not null
				if (view8AFiltered != null & view8AFiltered.size() > 0) {
				// then filter default List for 8A
				List<Gstr1NilRatedSummarySectionDto> default8AFiltered = 
						nilEYList.stream()
					.filter(p -> p.getTableSection().equalsIgnoreCase("8A"))
					.collect(Collectors.toList());

				// If the default filtered list is not null
				default8AFiltered.forEach(default8A -> {
				// then remove it from List
				nilEYList.remove(default8A);
				});

				// Iterate view list
				view8AFiltered.forEach(view8A -> {
					Gstr1NilRatedSummarySectionDto summaryRespDto = 
							new Gstr1NilRatedSummarySectionDto();
				summaryRespDto.setTableSection(view8A.getTableSection());
				summaryRespDto.setRecordCount(view8A.getRecordCount());
				summaryRespDto.setTotalExempted(view8A.getTotalExempted());
				summaryRespDto.setTotalNilRated(view8A.getTotalNilRated());
				summaryRespDto.setTotalNonGST(view8A.getTotalNonGST());
				
				// Add 8A to the final list
				nilEYList.add(summaryRespDto);
				});
				}

				//If 8B filtered is not null
				if (view8BFiltered != null & view8BFiltered.size() > 0) {
				List<Gstr1NilRatedSummarySectionDto> default8BFiltered = 
						nilEYList.stream()
					.filter(p -> p.getTableSection().equalsIgnoreCase("8B"))
					.collect(Collectors.toList());
				default8BFiltered.forEach(default8B -> {
				nilEYList.remove(default8B);
				});
				view8BFiltered.forEach(view8B -> {
					Gstr1NilRatedSummarySectionDto summaryRespDto = 
							new Gstr1NilRatedSummarySectionDto();
					summaryRespDto.setTableSection(view8B.getTableSection());
					summaryRespDto.setRecordCount(view8B.getRecordCount());
					summaryRespDto.setTotalExempted(view8B.getTotalExempted());
					summaryRespDto.setTotalNilRated(view8B.getTotalNilRated());
					summaryRespDto.setTotalNonGST(view8B.getTotalNonGST());
				// Add 8B to the final list
				nilEYList.add(summaryRespDto);
				});
				}

				//If 8C filtered is not null
				if (view8CFiltered != null & view8CFiltered.size() > 0) {
				List<Gstr1NilRatedSummarySectionDto> default8CFiltered = 
						nilEYList.stream()
					.filter(p -> p.getTableSection().equalsIgnoreCase("8C"))
					.collect(Collectors.toList());
				default8CFiltered.forEach(default8C -> {
				nilEYList.remove(default8C);
				});
				view8CFiltered.forEach(view8C -> {
				Gstr1NilRatedSummarySectionDto summaryRespDto = 
						new Gstr1NilRatedSummarySectionDto();
					summaryRespDto.setTableSection(view8C.getTableSection());
					summaryRespDto.setRecordCount(view8C.getRecordCount());
					summaryRespDto.setTotalExempted(view8C.getTotalExempted());
					summaryRespDto.setTotalNilRated(view8C.getTotalNilRated());
					summaryRespDto.setTotalNonGST(view8C.getTotalNonGST());
				// Add 8C to the final list
				nilEYList.add(summaryRespDto);
				});
				}

				

				//If 8D filtered is not null
				if (view8DFiltered != null & view8DFiltered.size() > 0) {
				List<Gstr1NilRatedSummarySectionDto> default8DFiltered = nilEYList.stream()
					.filter(p -> p.getTableSection().equalsIgnoreCase("8D"))
					.collect(Collectors.toList());
				default8DFiltered.forEach(default8D -> {
				nilEYList.remove(default8D);
				});
				view8DFiltered.forEach(view8D -> {
					Gstr1NilRatedSummarySectionDto summaryRespDto = new Gstr1NilRatedSummarySectionDto();
					summaryRespDto.setTableSection(view8D.getTableSection());
					summaryRespDto.setRecordCount(view8D.getRecordCount());
					summaryRespDto.setTotalExempted(view8D.getTotalExempted());
					summaryRespDto.setTotalNilRated(view8D.getTotalNilRated());
					summaryRespDto.setTotalNonGST(view8D.getTotalNonGST());
				// Add 8D to the final list
				nilEYList.add(summaryRespDto);
				});
				}

				// Sort the list in Ascending Order
				Collections.sort(nilEYList, new Comparator<Gstr1NilRatedSummarySectionDto>() {
				@Override
				public int compare(Gstr1NilRatedSummarySectionDto respDto1,
						Gstr1NilRatedSummarySectionDto respDto2) {
				return respDto1.getTableSection()
						.compareTo(respDto2.getTableSection());
				}
				});

				return nilEYList;

	}
}
