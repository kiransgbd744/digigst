package com.ey.advisory.app.services.search.docsummarysearch;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.Gstr1BasicSummarySectionDto;
import com.ey.advisory.app.docs.dto.Gstr1SummaryRespDto;

@Service("B2CSAEYFinalStructure")
public class B2CSAEYFinalStructure {

	public List<Gstr1SummaryRespDto> getb2csaEyList(
			List<Gstr1SummaryRespDto> b2csaEYList,
			List<Gstr1BasicSummarySectionDto> eySummaryListFromView) {

		// Filter the List for 10A
		List<Gstr1BasicSummarySectionDto> view10AFiltered = eySummaryListFromView
				.stream()
				.filter(p -> p.getTableSection().equalsIgnoreCase("10A"))
				.collect(Collectors.toList());

		// Filter the List for 10A(1)
		List<Gstr1BasicSummarySectionDto> view10A1Filtered = eySummaryListFromView
				.stream()
				.filter(p -> p.getTableSection().equalsIgnoreCase("10A(1)"))
				.collect(Collectors.toList());

		// Filter the List for 10B
		List<Gstr1BasicSummarySectionDto> view10BFiltered = eySummaryListFromView
				.stream()
				.filter(p -> p.getTableSection().equalsIgnoreCase("10B"))
				.collect(Collectors.toList());

		// Filter the List for 10B(1)
		List<Gstr1BasicSummarySectionDto> view10B1Filtered = eySummaryListFromView
				.stream()
				.filter(p -> p.getTableSection().equalsIgnoreCase("10B(1)"))
				.collect(Collectors.toList());

		// Set Values

		// If 5A filtered list is not null
		if (view10AFiltered != null & view10AFiltered.size() > 0) {
			// then filter default List for 10A
			List<Gstr1SummaryRespDto> default10AFiltered = b2csaEYList.stream()
					.filter(p -> p.getTableSection().equalsIgnoreCase("10A"))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			default10AFiltered.forEach(default10A -> {
				// then remove it from List
				b2csaEYList.remove(default10A);
			});

			// Iterate view list
			view10AFiltered.forEach(view10A -> {
				Gstr1SummaryRespDto summaryRespDto = new Gstr1SummaryRespDto();
				summaryRespDto.setTableSection(view10A.getTableSection());
				// summaryRespDto.setRecords(view10A.getRecords());
				summaryRespDto.setTaxableValue(view10A.getTaxableValue());
				summaryRespDto.setTaxPayble(view10A.getTaxPayble());
				// summaryRespDto.setInvValue(view10A.getInvValue());
				summaryRespDto.setIgst(view10A.getIgst());
				summaryRespDto.setSgst(view10A.getSgst());
				summaryRespDto.setCgst(view10A.getCgst());
				summaryRespDto.setCess(view10A.getCess());
				// Add 10A to the final list
				b2csaEYList.add(summaryRespDto);
			});
		}

		// If 10A(1) filtered list is not null
		if (view10A1Filtered != null & view10A1Filtered.size() > 0) {
			// then filter default List for 10A
			List<Gstr1SummaryRespDto> default10A1Filtered = b2csaEYList.stream()
					.filter(p -> p.getTableSection().equalsIgnoreCase("10A(1)"))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			default10A1Filtered.forEach(default10A1 -> {
				// then remove it from List
				b2csaEYList.remove(default10A1);
			});

			// Iterate view list
			view10A1Filtered.forEach(view10A1 -> {
				Gstr1SummaryRespDto summaryRespDto = new Gstr1SummaryRespDto();
				summaryRespDto.setTableSection(view10A1.getTableSection());
				// summaryRespDto.setRecords(view10A.getRecords());
				summaryRespDto.setTaxableValue(view10A1.getTaxableValue());
				summaryRespDto.setTaxPayble(view10A1.getTaxPayble());
				// summaryRespDto.setInvValue(view10A.getInvValue());
				summaryRespDto.setIgst(view10A1.getIgst());
				summaryRespDto.setSgst(view10A1.getSgst());
				summaryRespDto.setCgst(view10A1.getCgst());
				summaryRespDto.setCess(view10A1.getCess());
				// Add 10A to the final list
				b2csaEYList.add(summaryRespDto);
			});
		}

		// If 10B filtered list is not null
		if (view10BFiltered != null & view10BFiltered.size() > 0) {
			// then filter default List for 10B
			List<Gstr1SummaryRespDto> default5BFiltered = b2csaEYList.stream()
					.filter(p -> p.getTableSection().equalsIgnoreCase("10B"))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			default5BFiltered.forEach(default10B -> {
				// then remove it from List
				b2csaEYList.remove(default10B);
			});

			// Iterate view list
			view10BFiltered.forEach(view10B -> {
				Gstr1SummaryRespDto summaryRespDto = new Gstr1SummaryRespDto();
				summaryRespDto.setTableSection(view10B.getTableSection());
				// summaryRespDto.setRecords(view10B.getRecords());
				summaryRespDto.setTaxableValue(view10B.getTaxableValue());
				summaryRespDto.setTaxPayble(view10B.getTaxPayble());
				// summaryRespDto.setInvValue(view10B.getInvValue());
				summaryRespDto.setIgst(view10B.getIgst());
				summaryRespDto.setSgst(view10B.getSgst());
				summaryRespDto.setCgst(view10B.getCgst());
				summaryRespDto.setCess(view10B.getCess());
				// Add 4A to the final list
				b2csaEYList.add(summaryRespDto);
			});
		}

		// If 10B filtered list is not null
		if (view10B1Filtered != null & view10B1Filtered.size() > 0) {
			// then filter default List for 10B
			List<Gstr1SummaryRespDto> default5B1Filtered = b2csaEYList.stream()
					.filter(p -> p.getTableSection().equalsIgnoreCase("10B(1)"))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			default5B1Filtered.forEach(default10B1 -> {
				// then remove it from List
				b2csaEYList.remove(default10B1);
			});

			// Iterate view list
			view10B1Filtered.forEach(view10B1 -> {
				Gstr1SummaryRespDto summaryRespDto = new Gstr1SummaryRespDto();
				summaryRespDto.setTableSection(view10B1.getTableSection());
				// summaryRespDto.setRecords(view10B.getRecords());
				summaryRespDto.setTaxableValue(view10B1.getTaxableValue());
				summaryRespDto.setTaxPayble(view10B1.getTaxPayble());
				// summaryRespDto.setInvValue(view10B.getInvValue());
				summaryRespDto.setIgst(view10B1.getIgst());
				summaryRespDto.setSgst(view10B1.getSgst());
				summaryRespDto.setCgst(view10B1.getCgst());
				summaryRespDto.setCess(view10B1.getCess());
				// Add 4A to the final list
				b2csaEYList.add(summaryRespDto);
			});
		}

		// Sort the list in Ascending Order
		Collections.sort(b2csaEYList, new Comparator<Gstr1SummaryRespDto>() {
			@Override
			public int compare(Gstr1SummaryRespDto respDto1,
					Gstr1SummaryRespDto respDto2) {
				return respDto1.getTableSection()
						.compareTo(respDto2.getTableSection());
			}
		});

		return b2csaEYList;
	}

}
