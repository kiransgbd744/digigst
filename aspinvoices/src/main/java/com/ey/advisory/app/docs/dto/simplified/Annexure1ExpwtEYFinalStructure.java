package com.ey.advisory.app.docs.dto.simplified;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

/**
 * 
 * @author Mohana.Dasari
 *
 */
@Service("Annexure1ExpwtEYFinalStructure")
public class Annexure1ExpwtEYFinalStructure {
	
	
	public List<Annexure1SummaryRespDto> getExpwtEyList(
			List<Annexure1SummaryRespDto> eyList,
			List<Annexure1SummarySectionDto> eySummaryListFromView) {
		
		if (eySummaryListFromView != null) {
			// Filter the List for Doc Type - INV
			List<Annexure1SummarySectionDto> queryINVFiltered = 
					eySummaryListFromView
					.stream()
					.filter(p -> p.getDocType().equalsIgnoreCase("INV"))
					.collect(Collectors.toList());

			// Filter the List for Doc Type - DR
			List<Annexure1SummarySectionDto> queryDRFiltered = 
					eySummaryListFromView
					.stream().filter(p -> p.getDocType().equalsIgnoreCase("DR"))
					.collect(Collectors.toList());

			// Filter the List for Doc Type - CR
			List<Annexure1SummarySectionDto> queryCRFiltered = 
					eySummaryListFromView
					.stream().filter(p -> p.getDocType().equalsIgnoreCase("CR"))
					.collect(Collectors.toList());

			// Set Values
			// If Doc Type INV filtered list is not null
			if (queryINVFiltered != null & queryINVFiltered.size() > 0) {
				// then filter default List - INV
				List<Annexure1SummaryRespDto> defaultINVFiltered = eyList
						.stream()
						.filter(p -> p.getDocType().equalsIgnoreCase("INV"))
						.collect(Collectors.toList());

				// If the default filtered list is not null
				defaultINVFiltered.forEach(defaultINV -> {
					// then remove it from List
					eyList.remove(defaultINV);
				});

				// Iterate view list
				queryINVFiltered.forEach(queryINV -> {
					Annexure1SummaryRespDto summaryRespDto = 
												new Annexure1SummaryRespDto();
					summaryRespDto.setTableSection(queryINV.getTableSection());
					summaryRespDto.setDocType(queryINV.getDocType());
					summaryRespDto.setRecords(queryINV.getRecords());
					if (queryINV.getTaxableValue() != null) {
						summaryRespDto
								.setTaxableValue(queryINV.getTaxableValue());
					} else {
						summaryRespDto.setTaxableValue(new BigDecimal("0.00"));
					}
					if (queryINV.getInvValue() != null) {
						summaryRespDto.setInvValue(queryINV.getInvValue());
					} else {
						summaryRespDto.setInvValue(new BigDecimal("0.00"));
					}
					if (queryINV.getIgst() != null) {
						summaryRespDto.setIgst(queryINV.getIgst());
					} else {
						summaryRespDto.setIgst(new BigDecimal("0.00"));
					}
					if (queryINV.getSgst() != null) {
						summaryRespDto.setSgst(queryINV.getSgst());
					} else {
						summaryRespDto.setSgst(new BigDecimal("0.00"));
					}
					if (queryINV.getCgst() != null) {
						summaryRespDto.setCgst(queryINV.getCgst());
					} else {
						summaryRespDto.setCgst(new BigDecimal("0.00"));
					}
					if (queryINV.getCess() != null) {
						summaryRespDto.setCess(queryINV.getCess());
					} else {
						summaryRespDto.setCess(new BigDecimal("0.00"));
					}
					// Add INV to the final list
					eyList.add(summaryRespDto);
				});
			}

			// If Doc Type DR filtered list is not null
			if (queryDRFiltered != null & queryDRFiltered.size() > 0) {
				// then filter default List for DR
				List<Annexure1SummaryRespDto> defaultDRFiltered = eyList
						.stream()
						.filter(p -> p.getDocType().equalsIgnoreCase("DR"))
						.collect(Collectors.toList());

				// If the default filtered list is not null
				defaultDRFiltered.forEach(defaultDR -> {
					// then remove it from List
					eyList.remove(defaultDR);
				});

				// Iterate view list
				queryDRFiltered.forEach(queryDR -> {
					Annexure1SummaryRespDto summaryRespDto = 
												new Annexure1SummaryRespDto();
					summaryRespDto.setTableSection(queryDR.getTableSection());
					summaryRespDto.setDocType(queryDR.getDocType());
					summaryRespDto.setRecords(queryDR.getRecords());
					if (queryDR.getTaxableValue() != null) {
						summaryRespDto
								.setTaxableValue(queryDR.getTaxableValue());
					} else {
						summaryRespDto.setTaxableValue(new BigDecimal("0.00"));
					}
					if (queryDR.getInvValue() != null) {
						summaryRespDto.setInvValue(queryDR.getInvValue());
					} else {
						summaryRespDto.setInvValue(new BigDecimal("0.00"));
					}
					if (queryDR.getIgst() != null) {
						summaryRespDto.setIgst(queryDR.getIgst());
					} else {
						summaryRespDto.setIgst(new BigDecimal("0.00"));
					}
					if (queryDR.getSgst() != null) {
						summaryRespDto.setSgst(queryDR.getSgst());
					} else {
						summaryRespDto.setSgst(new BigDecimal("0.00"));
					}
					if (queryDR.getCgst() != null) {
						summaryRespDto.setCgst(queryDR.getCgst());
					} else {
						summaryRespDto.setCgst(new BigDecimal("0.00"));
					}
					if (queryDR.getCess() != null) {
						summaryRespDto.setCess(queryDR.getCess());
					} else {
						summaryRespDto.setCess(new BigDecimal("0.00"));
					}
					// Add DR to the final list
					eyList.add(summaryRespDto);
				});
			}

			// If Doc Type CR filtered list is not null
			if (queryCRFiltered != null & queryCRFiltered.size() > 0) {
				// then filter default List for CR
				List<Annexure1SummaryRespDto> defaultCRFiltered = eyList
						.stream()
						.filter(p -> p.getDocType().equalsIgnoreCase("CR"))
						.collect(Collectors.toList());

				// If the default filtered list is not null
				defaultCRFiltered.forEach(defaultCR -> {
					// then remove it from List
					eyList.remove(defaultCR);
				});

				// Iterate view list
				queryCRFiltered.forEach(queryCR -> {
					Annexure1SummaryRespDto summaryRespDto = 
												new Annexure1SummaryRespDto();
					summaryRespDto.setTableSection(queryCR.getTableSection());
					summaryRespDto.setDocType(queryCR.getDocType());
					summaryRespDto.setRecords(queryCR.getRecords());
					if (queryCR.getTaxableValue() != null) {
						summaryRespDto
								.setTaxableValue(queryCR.getTaxableValue());
					} else {
						summaryRespDto.setTaxableValue(new BigDecimal("0.00"));
					}
					if (queryCR.getInvValue() != null) {
						summaryRespDto.setInvValue(queryCR.getInvValue());
					} else {
						summaryRespDto.setInvValue(new BigDecimal("0.00"));
					}
					if (queryCR.getIgst() != null) {
						summaryRespDto.setIgst(queryCR.getIgst());
					} else {
						summaryRespDto.setIgst(new BigDecimal("0.00"));
					}
					if (queryCR.getSgst() != null) {
						summaryRespDto.setSgst(queryCR.getSgst());
					} else {
						summaryRespDto.setSgst(new BigDecimal("0.00"));
					}
					if (queryCR.getCgst() != null) {
						summaryRespDto.setCgst(queryCR.getCgst());
					} else {
						summaryRespDto.setCgst(new BigDecimal("0.00"));
					}
					if (queryCR.getCess() != null) {
						summaryRespDto.setCess(queryCR.getCess());
					} else {
						summaryRespDto.setCess(new BigDecimal("0.00"));
					}
					// Add CR to the final list
					eyList.add(summaryRespDto);
				});
			}
		}

		// Sort the list in Ascending Order
		Collections.sort(eyList, new Comparator<Annexure1SummaryRespDto>() {
			@Override
			public int compare(Annexure1SummaryRespDto respDto1,
					Annexure1SummaryRespDto respDto2) {
				return respDto1.getTableSection()
						.compareTo(respDto2.getTableSection());
			}
		});

		return eyList;
	}

}
