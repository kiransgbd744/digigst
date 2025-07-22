/**
 * 
 */
package com.ey.advisory.app.services.search.docsummarysearch;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.simplified.Gstr1SummaryScreenRespDto;
import com.ey.advisory.common.GSTConstants;

/**
 * @author BalaKrishna S
 *
 */
@Service("Gstr1SummaryScreenStructureUtil")
public class Gstr1SummaryScreenStructureUtil {

	public Gstr1SummaryScreenRespDto gstr1DefaultStructure(
			Gstr1SummaryScreenRespDto respDto) {

		String taxDocType = respDto.getTaxDocType();
		respDto.setAspTaxableValue(BigDecimal.ZERO);
		respDto.setAspTaxPayble(BigDecimal.ZERO);
		respDto.setAspInvoiceValue(BigDecimal.ZERO);
		respDto.setAspCount(0);
		respDto.setAspIgst(BigDecimal.ZERO);
		respDto.setAspSgst(BigDecimal.ZERO);
		respDto.setAspCgst(BigDecimal.ZERO);
		respDto.setAspCess(BigDecimal.ZERO);

		if (taxDocType.equals(GSTConstants.GSTR1_15I)
				|| taxDocType.equals(GSTConstants.GSTR1_15II)
				|| taxDocType.equals(GSTConstants.GSTR1_15III)
				|| taxDocType.equals(GSTConstants.GSTR1_15IV)
				|| taxDocType.equals(GSTConstants.GSTR1_15AIIA)
				|| taxDocType.equals(GSTConstants.GSTR1_15AIIB)
				|| taxDocType.equals(GSTConstants.GSTR1_15AIA)
				|| taxDocType.equals(GSTConstants.GSTR1_15AIB)) {

			respDto.setGstnTaxableValue(null);
			respDto.setGstnTaxPayble(null);
			respDto.setGstnInvoiceValue(null);
			respDto.setGstnCount(null);
			respDto.setGstnIgst(null);
			respDto.setGstnCgst(null);
			respDto.setGstnSgst(null);
			respDto.setGstnCess(null);
			respDto.setDiffTaxableValue(null);
			respDto.setDiffTaxPayble(null);
			respDto.setDiffInvoiceValue(null);
			respDto.setDiffIgst(null);
			respDto.setDiffCgst(null);
			respDto.setDiffSgst(null);
			respDto.setDiffCess(null);
			respDto.setDiffCount(null);

		} else {
			respDto.setGstnTaxableValue(BigDecimal.ZERO);
			respDto.setGstnTaxPayble(BigDecimal.ZERO);
			respDto.setGstnInvoiceValue(BigDecimal.ZERO);
			respDto.setGstnIgst(BigDecimal.ZERO);
			respDto.setGstnSgst(BigDecimal.ZERO);
			respDto.setGstnCgst(BigDecimal.ZERO);
			respDto.setGstnCess(BigDecimal.ZERO);
			respDto.setGstnCount(0);
			respDto.setDiffTaxableValue(BigDecimal.ZERO);
			respDto.setDiffTaxPayble(BigDecimal.ZERO);
			respDto.setDiffInvoiceValue(BigDecimal.ZERO);
			respDto.setDiffIgst(BigDecimal.ZERO);
			respDto.setDiffSgst(BigDecimal.ZERO);
			respDto.setDiffCgst(BigDecimal.ZERO);
			respDto.setDiffCess(BigDecimal.ZERO);
			respDto.setDiffCount(0);

		}

		return respDto;

	}

	public Gstr1SummaryScreenRespDto gstr1SezDefaultStructure(
			Gstr1SummaryScreenRespDto respDto) {

		respDto.setAspTaxableValue(BigDecimal.ZERO);
		respDto.setAspTaxPayble(BigDecimal.ZERO);
		respDto.setAspInvoiceValue(BigDecimal.ZERO);
		respDto.setAspCount(0);
		respDto.setAspIgst(BigDecimal.ZERO);
		respDto.setAspSgst(BigDecimal.ZERO);
		respDto.setAspCgst(BigDecimal.ZERO);
		respDto.setAspCess(BigDecimal.ZERO);

		return respDto;

	}

}
