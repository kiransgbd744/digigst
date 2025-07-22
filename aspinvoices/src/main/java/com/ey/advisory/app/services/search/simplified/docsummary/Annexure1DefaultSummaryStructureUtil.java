package com.ey.advisory.app.services.search.simplified.docsummary;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.simplified.Annexure1SummaryRespDto;

@Service("Annexure1DefaultSummaryStructureUtil")
public class Annexure1DefaultSummaryStructureUtil {
	
	public Annexure1SummaryRespDto defaultStructure(
			Annexure1SummaryRespDto respDto) {
		respDto.setRecords(0);
		respDto.setInvValue(new BigDecimal("0.00"));
		respDto.setTaxableValue(new BigDecimal("0.00"));
		respDto.setTaxPayble(new BigDecimal("0.00"));
		respDto.setIgst(new BigDecimal("0.00"));
		respDto.setCgst(new BigDecimal("0.00"));
		respDto.setSgst(new BigDecimal("0.00"));
		respDto.setCess(new BigDecimal("0.00"));
		return respDto;
	}

}
