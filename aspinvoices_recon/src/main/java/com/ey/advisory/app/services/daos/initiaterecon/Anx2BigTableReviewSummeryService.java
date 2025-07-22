package com.ey.advisory.app.services.daos.initiaterecon;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.services.daos.prsummary.GetANX2ReviewSummaryBigTableDao;
import com.ey.advisory.core.dto.Anx2PRSProcessedRequestDto;

/**
 * @author Siva.Nandam
 *
 */
@Service("Anx2BigTableReviewSummeryService")
public class Anx2BigTableReviewSummeryService {
	@Autowired
	@Qualifier("GetANX2ReviewSummaryBigTableDao")
private GetANX2ReviewSummaryBigTableDao anx2Reviewsummery;

	@Autowired
	@Qualifier("Anx2B2bBigTableReviewSummery")
	private Anx2reviewSummery anx2B2bReviewSummery;

	@Autowired
	@Qualifier("Anx2B2bReviewSummeryBigTableImpl")
	private Anx2B2bReviewSummeryBigTableImpl anx2B2bReviewSummeryImpl;
	
	@Autowired
	@Qualifier("Anx2IsdReviewSummeryBigtableImpl")
	private Anx2IsdReviewSummeryBigtableImpl anx2IsdReviewSummeryImpl;

	@Autowired
	@Qualifier("Anx2IsdbigTableReviewSummery")
	private Anx2reviewSummery anx2IsdReviewSummery;

	public List<Object> find(Anx2PRSProcessedRequestDto criteria) {
	
		List<Anx2BigTableResponseDto> entityResponse = new ArrayList<>();

		entityResponse = anx2Reviewsummery.getAnx2PRSProcessedRecs(criteria);
		List<Object> headerList = new ArrayList<>();

				Anx2BigTableReviewSummeryHeaderDto B2breviewSummeryfile 
				= anx2B2bReviewSummeryImpl
						.getreviewSummery(entityResponse, null);
				headerList.add(B2breviewSummeryfile);
				
				Anx2BigTableReviewSummeryHeaderDto isdreviewSummeryfile 
				= anx2IsdReviewSummeryImpl
						.getreviewSummery(entityResponse, null);
				headerList.add(isdreviewSummeryfile);

		
		 
		 

		return headerList;

	}
}
