package com.ey.advisory.app.services.search.docsummarysearch;

import java.math.BigDecimal;

import com.ey.advisory.app.docs.dto.anx1a.Anx1aSummaryInwardOutwardRespDto;

public class Anx1ASectionSummeryDefaultValuesUtil {

	private Anx1ASectionSummeryDefaultValuesUtil(){}
	
	public static void 
	        DefaultValues(Anx1aSummaryInwardOutwardRespDto dto,String section){
		
		dto.setTable(section);
		dto.setAspTaxableValue(BigDecimal.ZERO);
		dto.setAspTaxPayble(BigDecimal.ZERO);
		dto.setAspInvoiceValue(BigDecimal.ZERO);
		dto.setAspTaxableValue(BigDecimal.ZERO);
		dto.setAspTaxPayble(BigDecimal.ZERO);
		dto.setAspIgst(BigDecimal.ZERO);
		dto.setAspSgst(BigDecimal.ZERO);
		dto.setAspCgst(BigDecimal.ZERO);
		dto.setAspCess(BigDecimal.ZERO);
		dto.setGstnInvoiceValue(BigDecimal.ZERO);
		dto.setGstnTaxableValue(BigDecimal.ZERO);
		dto.setGstnTaxPayble(BigDecimal.ZERO);
		dto.setGstnIgst(BigDecimal.ZERO);
		dto.setGstnSgst(BigDecimal.ZERO);
		dto.setGstnCgst(BigDecimal.ZERO);
		dto.setGstnCess(BigDecimal.ZERO);
		dto.setDiffInvoiceValue(BigDecimal.ZERO);
		dto.setDiffTaxableValue(BigDecimal.ZERO);
		dto.setDiffTaxPayble(BigDecimal.ZERO);
		dto.setDiffIgst(BigDecimal.ZERO);
		dto.setDiffSgst(BigDecimal.ZERO);
		dto.setDiffCgst(BigDecimal.ZERO);
		dto.setDiffCess(BigDecimal.ZERO);
	}
}
