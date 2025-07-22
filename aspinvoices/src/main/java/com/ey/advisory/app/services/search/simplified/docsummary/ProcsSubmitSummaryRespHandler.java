package com.ey.advisory.app.services.search.simplified.docsummary;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.simplified.Gstr1SummaryScreenRespDto;
import com.ey.advisory.app.services.search.docsummarysearch.Gstr1SummaryScreenStructureUtil;

/**
 * 
 * @author Balakrishna.S
 *
 */
@Service("ProcsSubmitSummaryRespHandler")
public class ProcsSubmitSummaryRespHandler {

	@Autowired
	@Qualifier("Gstr1SummaryScreenStructureUtil")
	private Gstr1SummaryScreenStructureUtil defaultStructureUtil;

	
	public List<Gstr1SummaryScreenRespDto> pvsSubmitB2BResp(
			List<Gstr1SummaryScreenRespDto> b2bEySummary,String docType) {

		List<Gstr1SummaryScreenRespDto> defaultB2bEYList = 
				getDefaultB2BEYStructure(docType);

		List<Gstr1SummaryScreenRespDto> b2cRespbody = getB2BEyList(defaultB2bEYList, b2bEySummary,docType);

		return b2cRespbody;

	}
	
	private List<Gstr1SummaryScreenRespDto> getDefaultB2BEYStructure(String docType) {

		List<Gstr1SummaryScreenRespDto> defaultB2CEY = new ArrayList<>();

		Gstr1SummaryScreenRespDto b2bEy = new Gstr1SummaryScreenRespDto();
		b2bEy.setTaxDocType(docType);
		b2bEy = defaultStructureUtil.gstr1DefaultStructure(b2bEy);
		
		defaultB2CEY.add(b2bEy);
		return defaultB2CEY;
	
}
	
	public List<Gstr1SummaryScreenRespDto> getB2BEyList(
			List<Gstr1SummaryScreenRespDto> b2bEYList,
			List<Gstr1SummaryScreenRespDto> eySummaryListFromView,String docType) {
				
		List<Gstr1SummaryScreenRespDto> viewB2bFiltered = eySummaryListFromView
				.stream().filter(p -> docType.equalsIgnoreCase(p.getTaxDocType()))
				.collect(Collectors.toList());
		if (viewB2bFiltered != null & viewB2bFiltered.size() > 0) {
			// then filter default List for 4A
			List<Gstr1SummaryScreenRespDto> defaultB2bFiltered = b2bEYList
					.stream()
					.filter(p -> docType.equalsIgnoreCase(p.getTaxDocType()))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			defaultB2bFiltered.forEach(defaultB2b -> {
				// then remove it from List
				b2bEYList.remove(defaultB2b);
			});
			
			viewB2bFiltered.forEach(viewB2b -> {
				Gstr1SummaryScreenRespDto summaryRespDto = 
						new Gstr1SummaryScreenRespDto();
				summaryRespDto.setTaxDocType(viewB2b.getTaxDocType());
				summaryRespDto.setAspTaxableValue(viewB2b.getAspTaxableValue());
				summaryRespDto.setAspTaxPayble(viewB2b.getAspTaxPayble());
				summaryRespDto.setAspInvoiceValue(viewB2b.getAspInvoiceValue());
				summaryRespDto.setAspIgst(viewB2b.getAspIgst());
				summaryRespDto.setAspSgst(viewB2b.getAspSgst());
				summaryRespDto.setAspCgst(viewB2b.getAspCgst());
				summaryRespDto.setAspCess(viewB2b.getAspCess());
				summaryRespDto.setAspCount(viewB2b.getAspCount());
				summaryRespDto.setGstnTaxableValue(viewB2b.getGstnTaxableValue());
				summaryRespDto.setGstnTaxPayble(viewB2b.getGstnTaxPayble());
				summaryRespDto.setGstnInvoiceValue(viewB2b.getGstnInvoiceValue());
				summaryRespDto.setGstnCount(viewB2b.getGstnCount());
				summaryRespDto.setGstnIgst(viewB2b.getGstnIgst());
				summaryRespDto.setGstnCgst(viewB2b.getGstnCgst());
				summaryRespDto.setGstnSgst(viewB2b.getGstnSgst());
				summaryRespDto.setGstnCess(viewB2b.getGstnCess());
				summaryRespDto.setDiffTaxableValue(viewB2b.getDiffTaxableValue());
				summaryRespDto.setDiffTaxPayble(viewB2b.getDiffTaxPayble());
				summaryRespDto.setDiffInvoiceValue(viewB2b.getDiffInvoiceValue());
				summaryRespDto.setDiffIgst(viewB2b.getDiffIgst());
				summaryRespDto.setDiffCgst(viewB2b.getDiffCgst());
				summaryRespDto.setDiffSgst(viewB2b.getDiffSgst());
				summaryRespDto.setDiffCess(viewB2b.getDiffCess());
				summaryRespDto.setDiffCount(viewB2b.getDiffCount());
				
				b2bEYList.add(summaryRespDto);
			});
		}
		return b2bEYList;
	}
	
}
