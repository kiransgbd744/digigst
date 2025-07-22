package com.ey.advisory.app.anx2.reconresponse.reviewsummary;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.google.common.collect.ImmutableList;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author vishal.verma
 *
 */

@Slf4j
@Component("ReconrResponseReviewSummaryServiceImpl")
public class ReconrResponseReviewSummaryServiceImpl
		implements ReconrResponseReviewSummaryService {

	@Autowired
	@Qualifier("ReconrResponseReviewSummaryDaoImpl")
	private ReconrResponseReviewSummaryDao dao;

	@Override
	public List<ReconrResponseReviewSummaryDto> getReconResponseSummary(
			String taxPeriod, String gstin, List<String> docTypeList,
			List<String> tableTypeList, List<String> typeList) {

		List<ReconrResponseReviewSummaryDto> l3List = dao
				.findReconResponseSummary(taxPeriod, gstin, docTypeList,
						tableTypeList, typeList);
		
		if (LOGGER.isDebugEnabled()) {
			String str = String.format("Invoked getReconResponseSummary"
					+ ".findReconResponseSummary() method dbResponse : ",
					l3List);
			LOGGER.debug(str);
		}
		
		// Create an L2 Level collection at GSTIN level, by summing up elements
		// in the above Level 3 collection.
		Collection<ReconrResponseReviewSummaryDto> l2List = l3List.stream()
				.collect(Collectors.groupingBy(o1 -> o1.getTableType() 
						+ o1.getDocType(),
						getCollectorForLevel("L2")))
				.values();

		// Create an L1 level collection at TableType level, 
		// in the above Level 2 collection.
		Collection<ReconrResponseReviewSummaryDto> l1List = l2List.stream()
				.collect(Collectors.groupingBy(o1 ->  o1.getTableType(),
						getCollectorForLevel("L1")))
				.values();
		
		List<ReconrResponseReviewSummaryDto> list = 
				new ImmutableList.Builder<ReconrResponseReviewSummaryDto>()
					.addAll(l1List)
					.addAll(l2List)
					.addAll(l3List)
					.build();
		
		// Sort the above list by a key that orders the elements in a
	    // hierarchical manner.
		List<ReconrResponseReviewSummaryDto> respList = list.stream()
				.sorted(Comparator.comparing(o -> o.getKey()))
				.collect(Collectors.toList());
		
		if (LOGGER.isDebugEnabled()) {
			String str = String.format("Before return getReconResponseSummary"
					+ ".findReconResponseSummary(): respList : ",
					respList);
			LOGGER.debug(str);
		}
		
		
		return respList;
		
	}

	private Collector<ReconrResponseReviewSummaryDto, ?, 
			ReconrResponseReviewSummaryDto> getCollectorForLevel(
			String level) {
		return Collectors.reducing(new ReconrResponseReviewSummaryDto(level),
				(o1, o2) -> add(o1, o2, level));
	}

	private ReconrResponseReviewSummaryDto add(
			ReconrResponseReviewSummaryDto o1,
			ReconrResponseReviewSummaryDto o2, String level) {
		
		return new ReconrResponseReviewSummaryDto(o2.getTableType(),
				o2.getDocType(),level);
		
	}
	
	

}
