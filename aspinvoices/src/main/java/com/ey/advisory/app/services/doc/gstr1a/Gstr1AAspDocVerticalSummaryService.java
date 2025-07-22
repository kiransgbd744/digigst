package com.ey.advisory.app.services.doc.gstr1a;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.Gstr1HsnSummarySectionDto;
import com.ey.advisory.app.docs.dto.Gstr1SummaryDocSectionDto;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;


/**
 * 
 * @author Shashikant.Shukla
 *
 */
@Service("Gstr1AAspDocVerticalSummaryService")
public class Gstr1AAspDocVerticalSummaryService {

	@Autowired
	@Qualifier("Gstr1AAspDocVerticalSectionDaoImpl")
	Gstr1AAspDocVerticalSectionDaoImpl docVertical;

	public List<Gstr1SummaryDocSectionDto> find(Annexure1SummaryReqDto req) {
		// TODO Auto-generated method stub
		List<Gstr1SummaryDocSectionDto> aspvertical = docVertical
				.basicDocSummarySection(req);
		return aspvertical;
	}


	public List<Gstr1HsnSummarySectionDto> findHsn(Annexure1SummaryReqDto req) {
		// TODO Auto-generated method stub

		List<Gstr1HsnSummarySectionDto> hsnBasicSummarySection = docVertical
				.hsnBasicSummarySection(req);
		return hsnBasicSummarySection;
	}
	public List<Gstr1SummaryDocSectionDto> findVerticalDetails(
			Annexure1SummaryReqDto req) {
		// TODO Auto-generated method stub

		List<Gstr1SummaryDocSectionDto> verticalData = docVertical
				.docBasicSummarySection(req);

		return verticalData;
	}

}
