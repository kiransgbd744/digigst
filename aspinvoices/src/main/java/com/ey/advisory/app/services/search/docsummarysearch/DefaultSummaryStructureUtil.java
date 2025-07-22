package com.ey.advisory.app.services.search.docsummarysearch;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.Gstr1DocIssuedSummarySectionDto;
import com.ey.advisory.app.docs.dto.Gstr1NilRatedSummarySectionDto;
import com.ey.advisory.app.docs.dto.Gstr1SummaryRespDto;

@Service("DefaultStructureUtil")
public class DefaultSummaryStructureUtil {
	
	public Gstr1SummaryRespDto defaultStructure(Gstr1SummaryRespDto respDto){
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

	public Gstr1SummaryRespDto defaultB2CLStructure(Gstr1SummaryRespDto respDto){
		respDto.setRecords(0);
		respDto.setInvValue(new BigDecimal("0.00"));
		respDto.setTaxableValue(new BigDecimal("0.00"));
		respDto.setTaxPayble(new BigDecimal("0.00"));
		respDto.setIgst(new BigDecimal("0.00"));
	//	respDto.setCgst(new BigDecimal("0.00"));
	//	respDto.setSgst(new BigDecimal("0.00"));
		respDto.setCess(new BigDecimal("0.00"));
		return respDto;
	}

	public Gstr1SummaryRespDto defaultB2CSStructure(Gstr1SummaryRespDto respDto){
		//respDto.setRecords(0);
	//	respDto.setInvValue(new BigDecimal("0.00"));
		respDto.setTaxableValue(new BigDecimal("0.00"));
		respDto.setTaxPayble(new BigDecimal("0.00"));
		respDto.setIgst(new BigDecimal("0.00"));
		respDto.setCgst(new BigDecimal("0.00"));
		respDto.setSgst(new BigDecimal("0.00"));
		respDto.setCess(new BigDecimal("0.00"));
		return respDto;
	}
	
	public Gstr1DocIssuedSummarySectionDto defaultDOCStructure(
			Gstr1DocIssuedSummarySectionDto respDto) {
		respDto.setRecords(0);
		respDto.setTotalIssued(0);
		respDto.setNetIssued(0);
		respDto.setCancelled(0);
		return respDto;
	}
	
	public Gstr1NilRatedSummarySectionDto defaultNILStructure(
			Gstr1NilRatedSummarySectionDto respDto) {
		respDto.setRecordCount(0);
		respDto.setTotalExempted(new BigDecimal(0.0));
		respDto.setTotalNilRated(new BigDecimal(0.0));
		respDto.setTotalNonGST(new BigDecimal(0.0));
		return respDto;
	}

}
