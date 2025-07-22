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
@Service("Annexure1B2BEYFinalStructure")
public class Annexure1B2BEYFinalStructure {
	
	
	public List<Annexure1SummaryRespDto> getB2BEyList(
			List<Annexure1SummaryRespDto> b2bEYList,
			List<Annexure1SummarySectionDto> eySummaryListFromView) {
		
		if(eySummaryListFromView !=  null){
		// Filter the List for Doc Type - INV
		List<Annexure1SummarySectionDto> queryINVFiltered = 
				eySummaryListFromView.stream()
				.filter(p -> p.getDocType().equalsIgnoreCase("INV"))
				.collect(Collectors.toList());
		
		// Filter the List for Doc Type - DR
		List<Annexure1SummarySectionDto> queryDRFiltered = 
				eySummaryListFromView.stream()
				.filter(p -> p.getDocType().equalsIgnoreCase("DR"))
				.collect(Collectors.toList());
		
		// Filter the List for Doc Type - CR
		List<Annexure1SummarySectionDto> queryCRFiltered = 
				eySummaryListFromView.stream()
				.filter(p -> p.getDocType().equalsIgnoreCase("CR"))
				.collect(Collectors.toList());
		
		// Filter the List for Doc Type - RNV
		List<Annexure1SummarySectionDto> queryRNVFiltered = 
				eySummaryListFromView.stream()
				.filter(p -> p.getDocType().equalsIgnoreCase("RNV"))
				.collect(Collectors.toList());
		
		// Filter the List for Doc Type - RDR
		List<Annexure1SummarySectionDto> queryRDRFiltered = 
				eySummaryListFromView.stream()
				.filter(p -> p.getDocType().equalsIgnoreCase("RDR"))
				.collect(Collectors.toList());
		
		// Filter the List for Doc Type - RCR
		List<Annexure1SummarySectionDto> queryRCRFiltered = 
				eySummaryListFromView.stream()
				.filter(p -> p.getDocType().equalsIgnoreCase("RCR"))
				.collect(Collectors.toList());
				
				
		
		//Set Values
		// If Doc Type INV filtered list is not null
		if (queryINVFiltered != null & queryINVFiltered.size() > 0) {
			// then filter default List for 4A
			List<Annexure1SummaryRespDto> defaultINVFiltered = 
							b2bEYList.stream()
					.filter(p -> p.getDocType().equalsIgnoreCase("INV"))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			defaultINVFiltered.forEach(defaultINV -> {
				// then remove it from List
				b2bEYList.remove(defaultINV);
			});

			// Iterate view list
			queryINVFiltered.forEach(queryINV -> {
				Annexure1SummaryRespDto summaryRespDto = 
											new Annexure1SummaryRespDto();
				summaryRespDto.setTableSection(queryINV.getTableSection());
				summaryRespDto.setDocType(queryINV.getDocType());
				summaryRespDto.setRecords(queryINV.getRecords());
				if (queryINV.getTaxableValue() != null) {
					summaryRespDto.setTaxableValue(queryINV.getTaxableValue());
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
				b2bEYList.add(summaryRespDto);
			});
		}
		
		
		// If Doc Type DR filtered list is not null
		if (queryDRFiltered != null & queryDRFiltered.size() > 0) {
			// then filter default List for DR
			List<Annexure1SummaryRespDto> defaultDRFiltered = b2bEYList.stream()
					.filter(p -> p.getDocType().equalsIgnoreCase("DR"))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			defaultDRFiltered.forEach(defaultDR -> {
				// then remove it from List
				b2bEYList.remove(defaultDR);
			});

			// Iterate view list
			queryDRFiltered.forEach(queryDR -> {
				Annexure1SummaryRespDto summaryRespDto = 
						new Annexure1SummaryRespDto();
				summaryRespDto.setTableSection(queryDR.getTableSection());
				summaryRespDto.setDocType(queryDR.getDocType());
				summaryRespDto.setRecords(queryDR.getRecords());
				if (queryDR.getTaxableValue() != null) {
					summaryRespDto.setTaxableValue(queryDR.getTaxableValue());
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
				b2bEYList.add(summaryRespDto);
			});
		}
		
		// If Doc Type CR filtered list is not null
		if (queryCRFiltered != null & queryCRFiltered.size() > 0) {
			// then filter default List for CR
			List<Annexure1SummaryRespDto> defaultCRFiltered = b2bEYList.stream()
					.filter(p -> p.getDocType().equalsIgnoreCase("CR"))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			defaultCRFiltered.forEach(defaultCR -> {
				// then remove it from List
				b2bEYList.remove(defaultCR);
			});

			// Iterate view list
			queryCRFiltered.forEach(queryCR -> {
				Annexure1SummaryRespDto summaryRespDto 
							= new Annexure1SummaryRespDto();
				summaryRespDto.setTableSection(queryCR.getTableSection());
				summaryRespDto.setDocType(queryCR.getDocType());
				summaryRespDto.setRecords(queryCR.getRecords());
				if (queryCR.getTaxableValue() != null) {
					summaryRespDto.setTaxableValue(queryCR.getTaxableValue());
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
				b2bEYList.add(summaryRespDto);
			});
		}
		
		// If Doc Type RNV filtered list is not null
		if (queryRNVFiltered != null & queryRNVFiltered.size() > 0) {
			// then filter default List for CR
			List<Annexure1SummaryRespDto> defaultRNVFiltered = b2bEYList
					.stream()
					.filter(p -> p.getDocType().equalsIgnoreCase("RNV"))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			defaultRNVFiltered.forEach(defaultRNV -> {
				// then remove it from List
				b2bEYList.remove(defaultRNV);
			});

			// Iterate view list
			queryRNVFiltered.forEach(queryRNV -> {
				Annexure1SummaryRespDto summaryRespDto = 
											new Annexure1SummaryRespDto();
				summaryRespDto.setTableSection(queryRNV.getTableSection());
				summaryRespDto.setDocType(queryRNV.getDocType());
				summaryRespDto.setRecords(queryRNV.getRecords());
				if (queryRNV.getTaxableValue() != null) {
					summaryRespDto.setTaxableValue(queryRNV.getTaxableValue());
				} else {
					summaryRespDto.setTaxableValue(new BigDecimal("0.00"));
				}
				if (queryRNV.getInvValue() != null) {
					summaryRespDto.setInvValue(queryRNV.getInvValue());
				} else {
					summaryRespDto.setInvValue(new BigDecimal("0.00"));
				}
				if (queryRNV.getIgst() != null) {
					summaryRespDto.setIgst(queryRNV.getIgst());
				} else {
					summaryRespDto.setIgst(new BigDecimal("0.00"));
				}
				if (queryRNV.getSgst() != null) {
					summaryRespDto.setSgst(queryRNV.getSgst());
				} else {
					summaryRespDto.setSgst(new BigDecimal("0.00"));
				}
				if (queryRNV.getCgst() != null) {
					summaryRespDto.setCgst(queryRNV.getCgst());
				} else {
					summaryRespDto.setCgst(new BigDecimal("0.00"));
				}
				if (queryRNV.getCess() != null) {
					summaryRespDto.setCess(queryRNV.getCess());
				} else {
					summaryRespDto.setCess(new BigDecimal("0.00"));
				}
				// Add RNV to the final list
				b2bEYList.add(summaryRespDto);
			});
		}
		
		// If Doc Type RDR filtered list is not null
		if (queryRDRFiltered != null & queryRDRFiltered.size() > 0) {
			// then filter default List for RDR
			List<Annexure1SummaryRespDto> defaultRDRFiltered = b2bEYList
					.stream()
					.filter(p -> p.getDocType().equalsIgnoreCase("RDR"))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			defaultRDRFiltered.forEach(defaultRDR -> {
				// then remove it from List
				b2bEYList.remove(defaultRDR);
			});

			// Iterate view list
			queryRDRFiltered.forEach(queryRDR -> {
				Annexure1SummaryRespDto summaryRespDto = 
											new Annexure1SummaryRespDto();
				summaryRespDto.setTableSection(queryRDR.getTableSection());
				summaryRespDto.setDocType(queryRDR.getDocType());
				summaryRespDto.setRecords(queryRDR.getRecords());
				if (queryRDR.getTaxableValue() != null) {
					summaryRespDto.setTaxableValue(queryRDR.getTaxableValue());
				} else {
					summaryRespDto.setTaxableValue(new BigDecimal("0.00"));
				}
				if (queryRDR.getInvValue() != null) {
					summaryRespDto.setInvValue(queryRDR.getInvValue());
				} else {
					summaryRespDto.setInvValue(new BigDecimal("0.00"));
				}
				if (queryRDR.getIgst() != null) {
					summaryRespDto.setIgst(queryRDR.getIgst());
				} else {
					summaryRespDto.setIgst(new BigDecimal("0.00"));
				}
				if (queryRDR.getSgst() != null) {
					summaryRespDto.setSgst(queryRDR.getSgst());
				} else {
					summaryRespDto.setSgst(new BigDecimal("0.00"));
				}
				if (queryRDR.getCgst() != null) {
					summaryRespDto.setCgst(queryRDR.getCgst());
				} else {
					summaryRespDto.setCgst(new BigDecimal("0.00"));
				}
				if (queryRDR.getCess() != null) {
					summaryRespDto.setCess(queryRDR.getCess());
				} else {
					summaryRespDto.setCess(new BigDecimal("0.00"));
				}
				// Add RDR to the final list
				b2bEYList.add(summaryRespDto);
			});
		}
		
		// If Doc Type RCR filtered list is not null
		if (queryRCRFiltered != null & queryRCRFiltered.size() > 0) {
			// then filter default List for RCR
			List<Annexure1SummaryRespDto> defaultRCRFiltered = b2bEYList
					.stream()
					.filter(p -> p.getDocType().equalsIgnoreCase("RCR"))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			defaultRCRFiltered.forEach(defaultRCR -> {
				// then remove it from List
				b2bEYList.remove(defaultRCR);
			});

			// Iterate view list
			queryRCRFiltered.forEach(queryRCR -> {
				Annexure1SummaryRespDto summaryRespDto = 
											new Annexure1SummaryRespDto();
				summaryRespDto.setTableSection(queryRCR.getTableSection());
				summaryRespDto.setDocType(queryRCR.getDocType());
				summaryRespDto.setRecords(queryRCR.getRecords());
				if (queryRCR.getTaxableValue() != null) {
					summaryRespDto.setTaxableValue(queryRCR.getTaxableValue());
				} else {
					summaryRespDto.setTaxableValue(new BigDecimal("0.00"));
				}
				if (queryRCR.getInvValue() != null) {
					summaryRespDto.setInvValue(queryRCR.getInvValue());
				} else {
					summaryRespDto.setInvValue(new BigDecimal("0.00"));
				}
				if (queryRCR.getIgst() != null) {
					summaryRespDto.setIgst(queryRCR.getIgst());
				} else {
					summaryRespDto.setIgst(new BigDecimal("0.00"));
				}
				if (queryRCR.getSgst() != null) {
					summaryRespDto.setSgst(queryRCR.getSgst());
				} else {
					summaryRespDto.setSgst(new BigDecimal("0.00"));
				}
				if (queryRCR.getCgst() != null) {
					summaryRespDto.setCgst(queryRCR.getCgst());
				} else {
					summaryRespDto.setCgst(new BigDecimal("0.00"));
				}
				if (queryRCR.getCess() != null) {
					summaryRespDto.setCess(queryRCR.getCess());
				} else {
					summaryRespDto.setCess(new BigDecimal("0.00"));
				}
				// Add RCR to the final list
				b2bEYList.add(summaryRespDto);
			});
		}
		}

		// Sort the list in Ascending Order
		Collections.sort(b2bEYList, new Comparator<Annexure1SummaryRespDto>() {
			@Override
			public int compare(Annexure1SummaryRespDto respDto1,
					Annexure1SummaryRespDto respDto2) {
				return respDto1.getTableSection()
						.compareTo(respDto2.getTableSection());
			}
		});

		return b2bEYList;
	}

}
