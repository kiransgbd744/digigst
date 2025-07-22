/**
 * 
 */
package com.ey.advisory.app.services.search.docsummarysearch;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.simplified.Gstr1SummaryScreenRespDto;
import com.ey.advisory.common.GSTConstants;

/**
 * @author Siva.Reddy
 *
 */
@Service("Gstr1Tbl14and15FinalStructure")
public class Gstr1Tbl14and15FinalStructure {

	public List<Gstr1SummaryScreenRespDto> getEyList(
			List<Gstr1SummaryScreenRespDto> eyList,
			List<Gstr1SummaryScreenRespDto> eySummaryListFromView,
			String taxDocType) {

		List<Gstr1SummaryScreenRespDto> viewExpFiltered = eySummaryListFromView
				.stream()
				.filter(p -> taxDocType.equalsIgnoreCase(p.getTaxDocType()))
				.collect(Collectors.toList());
		if (viewExpFiltered != null & viewExpFiltered.size() > 0) {

			List<Gstr1SummaryScreenRespDto> defaultExpFiltered = eyList.stream()
					.filter(p -> taxDocType.equalsIgnoreCase(p.getTaxDocType()))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			defaultExpFiltered.forEach(defaultExp -> {
				// then remove it from List
				eyList.remove(defaultExp);
			});

			viewExpFiltered.forEach(viewexp -> {
				Gstr1SummaryScreenRespDto summaryRespDto = new Gstr1SummaryScreenRespDto();
				summaryRespDto.setTaxDocType(viewexp.getTaxDocType());
				summaryRespDto.setAspTaxableValue(viewexp.getAspTaxableValue());
				summaryRespDto.setAspTaxPayble(viewexp.getAspTaxPayble());
				summaryRespDto.setAspInvoiceValue(viewexp.getAspInvoiceValue());
				summaryRespDto.setAspIgst(viewexp.getAspIgst());
				summaryRespDto.setAspSgst(viewexp.getAspSgst());
				summaryRespDto.setAspCgst(viewexp.getAspCgst());
				summaryRespDto.setAspCess(viewexp.getAspCess());
				summaryRespDto.setAspCount(viewexp.getAspCount());
				if (taxDocType.equals(GSTConstants.GSTR1_15I)
						|| taxDocType.equals(GSTConstants.GSTR1_15II)
						|| taxDocType.equals(GSTConstants.GSTR1_15III)
						|| taxDocType.equals(GSTConstants.GSTR1_15IV)
						|| taxDocType.equals(GSTConstants.GSTR1_15AIIA)
						|| taxDocType.equals(GSTConstants.GSTR1_15AIIB)
						|| taxDocType.equals(GSTConstants.GSTR1_15AIA)
						|| taxDocType.equals(GSTConstants.GSTR1_15AIB)) {

					summaryRespDto.setGstnTaxableValue(null);
					summaryRespDto.setGstnTaxPayble(null);
					summaryRespDto.setGstnInvoiceValue(null);
					summaryRespDto.setGstnCount(null);
					summaryRespDto.setGstnIgst(null);
					summaryRespDto.setGstnCgst(null);
					summaryRespDto.setGstnSgst(null);
					summaryRespDto.setGstnCess(null);
					summaryRespDto.setDiffTaxableValue(null);
					summaryRespDto.setDiffTaxPayble(null);
					summaryRespDto.setDiffInvoiceValue(null);
					summaryRespDto.setDiffIgst(null);
					summaryRespDto.setDiffCgst(null);
					summaryRespDto.setDiffSgst(null);
					summaryRespDto.setDiffCess(null);
					summaryRespDto.setDiffCount(null);

				} else {
					summaryRespDto
							.setGstnTaxableValue(viewexp.getGstnTaxableValue());
					summaryRespDto.setGstnTaxPayble(viewexp.getGstnTaxPayble());
					summaryRespDto
							.setGstnInvoiceValue(viewexp.getGstnInvoiceValue());
					summaryRespDto.setGstnCount(viewexp.getGstnCount());
					summaryRespDto.setGstnIgst(viewexp.getGstnIgst());
					summaryRespDto.setGstnCgst(viewexp.getGstnCgst());
					summaryRespDto.setGstnSgst(viewexp.getGstnSgst());
					summaryRespDto.setGstnCess(viewexp.getGstnCess());
					summaryRespDto
							.setDiffTaxableValue(viewexp.getDiffTaxableValue());
					summaryRespDto.setDiffTaxPayble(viewexp.getDiffTaxPayble());
					summaryRespDto
							.setDiffInvoiceValue(viewexp.getDiffInvoiceValue());
					summaryRespDto.setDiffIgst(viewexp.getDiffIgst());
					summaryRespDto.setDiffCgst(viewexp.getDiffCgst());
					summaryRespDto.setDiffSgst(viewexp.getDiffSgst());
					summaryRespDto.setDiffCess(viewexp.getDiffCess());
					summaryRespDto.setDiffCount(viewexp.getDiffCount());
				}

				eyList.add(summaryRespDto);
			});
		}
		return eyList;
	}
}
