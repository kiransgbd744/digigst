/**
 * 
 */
package com.ey.advisory.app.services.search.docsummarysearch;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.simplified.Gstr1SummaryScreenDocRespDto;
import com.ey.advisory.app.docs.dto.simplified.Gstr1SummaryScreenRespDto;

/**
 * @author BalaKrishna S
 *
 */
@Service("Gstr1DocFinalStructure")
public class Gstr1DocFinalStructure {

	public List<Gstr1SummaryScreenDocRespDto> getDocEyList(
			List<Gstr1SummaryScreenDocRespDto> docEYList,
			List<Gstr1SummaryScreenDocRespDto> eySummaryListFromView) {
				
		List<Gstr1SummaryScreenDocRespDto> viewDocFiltered = eySummaryListFromView
				.stream().filter(p -> "DOC ISSUED".equalsIgnoreCase(p.getTaxDocType()))
				.collect(Collectors.toList());
		
		if (viewDocFiltered != null & viewDocFiltered.size() > 0) {
			// then filter default List for 4A
			List<Gstr1SummaryScreenDocRespDto> defaultdocFiltered = docEYList
					.stream()
					.filter(p -> "DOC ISSUED".equalsIgnoreCase(p.getTaxDocType()))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			defaultdocFiltered.forEach(defaultdoc -> {
				// then remove it from List
				docEYList.remove(defaultdoc);
			});
			
			viewDocFiltered.forEach(viewdoc -> {
				Gstr1SummaryScreenDocRespDto summaryRespDto = 
						new Gstr1SummaryScreenDocRespDto();
				summaryRespDto.setTotal(viewdoc.getTotal());
				summaryRespDto.setAspCancelled(viewdoc.getAspCancelled());
				summaryRespDto.setAspNetIssued(viewdoc.getAspNetIssued());
				summaryRespDto.setAspTotal(viewdoc.getAspTotal());
				summaryRespDto.setGstnCancelled(viewdoc.getGstnCancelled());
				summaryRespDto.setGstnNetIssued(viewdoc.getGstnNetIssued());
				summaryRespDto.setGstnTotal(viewdoc.getGstnTotal());
				summaryRespDto.setDiffCancelled(viewdoc.getDiffCancelled());
				summaryRespDto.setDiffNetIssued(viewdoc.getDiffNetIssued());
				summaryRespDto.setDiffTotal(viewdoc.getDiffTotal());
				summaryRespDto.setTaxDocType(viewdoc.getTaxDocType());
				docEYList.add(summaryRespDto);
			});
		}
		return docEYList;
	}
	
	
}
