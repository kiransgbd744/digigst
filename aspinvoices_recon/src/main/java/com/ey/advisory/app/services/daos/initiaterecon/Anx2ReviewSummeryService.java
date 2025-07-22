package com.ey.advisory.app.services.daos.initiaterecon;

import static com.ey.advisory.common.GenUtil.trimAndConvToUpperCase;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.services.daos.prsummary.GetANX2ReviewSummaryDaoImpl;
import com.ey.advisory.core.dto.Anx2PRSProcessedRequestDto;

/**
 * @author Siva.Nandam
 *
 */
@Service("Anx2ReviewSummeryService")
public class Anx2ReviewSummeryService {
	@Autowired
	@Qualifier("GetANX2ReviewSummaryDaoImpl")
	GetANX2ReviewSummaryDaoImpl anx2Reviewsummery;

	@Autowired
	@Qualifier("Anx2B2bReviewSummery")
	private Anx2reviewSummery anx2B2bReviewSummery;

	@Autowired
	@Qualifier("Anx2B2bReviewSummeryImpl")
	private Anx2reviewSummery anx2B2bReviewSummeryImpl;
	
	@Autowired
	@Qualifier("Anx2IsdReviewSummeryImpl")
	private Anx2reviewSummery anx2IsdReviewSummeryImpl;

	@Autowired
	@Qualifier("Anx2IsdReviewSummery")
	private Anx2reviewSummery anx2IsdReviewSummery;

	public List<Object> find(Anx2PRSProcessedRequestDto criteria) {
		List<String> docType = criteria.getDocType();
		List<Anx2PRReviewSummeryResponseDto> entityResponse = new ArrayList<>();

		entityResponse = anx2Reviewsummery.getAnx2PRSProcessedRecs(criteria);
		List<Object> headerList = new ArrayList<>();

		List<String> recordType = criteria.getRecordType();
		if (docType.size() > 0) {
			if (recordType.size() <= 0
					|| recordType.contains(trimAndConvToUpperCase("B2B"))) {
				Anx2PrReviewSummeryHeaderDto B2breviewSummeryfile = anx2B2bReviewSummery
						.getreviewSummery(entityResponse, docType);
				headerList.add(B2breviewSummeryfile);
			}
			 if(recordType.size() <= 0 ||
					  recordType.contains(trimAndConvToUpperCase("ISD"))){
					 Anx2PrReviewSummeryHeaderDto IsdreviewSummeryfile =
					 anx2IsdReviewSummery .getreviewSummery(entityResponse,docType);
					 headerList.add(IsdreviewSummeryfile); }
		}
		if (docType.size() <= 0) {
			if (recordType.size() <= 0
					|| recordType.contains(trimAndConvToUpperCase("B2B"))) {
				Anx2PrReviewSummeryHeaderDto B2breviewSummeryfile = anx2B2bReviewSummeryImpl
						.getreviewSummery(entityResponse, docType);
				headerList.add(B2breviewSummeryfile);
			}
			if (recordType.size() <= 0
					|| recordType.contains(trimAndConvToUpperCase("ISD"))) {
				Anx2PrReviewSummeryHeaderDto B2breviewSummeryfile = anx2IsdReviewSummeryImpl
						.getreviewSummery(entityResponse, docType);
				headerList.add(B2breviewSummeryfile);
			}
		}

		
		 
		 

		return headerList;

	}
}
