package com.ey.advisory.app.services.search.docsummarysearch;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.Gstr1B2BGstnRespDto;
import com.ey.advisory.app.docs.dto.Gstr1BasicSummarySectionDto;
import com.ey.advisory.app.docs.dto.Gstr1DocIssuedSummarySectionDto;
import com.ey.advisory.app.docs.dto.Gstr1NilRatedSummarySectionDto;

@Service("GstnDataRetrive")
public class GstnDataRetrive {
	
	public List<Gstr1B2BGstnRespDto> getGstnData(
			List<Gstr1BasicSummarySectionDto> gstnSummary) {
		
		List<Gstr1B2BGstnRespDto> gstnList=new ArrayList<>();
		Gstr1B2BGstnRespDto dto=new Gstr1B2BGstnRespDto();
		for(Gstr1BasicSummarySectionDto sumDto : gstnSummary)
		{
			dto.setInvValue(sumDto.getInvValue());
			dto.setIgst(sumDto.getIgst());
			dto.setCgst(sumDto.getCgst());
			dto.setSgst(sumDto.getSgst());
			dto.setTaxableValue(sumDto.getTaxableValue());
			dto.setTaxPayble(sumDto.getTaxPayble());
			dto.setCess(sumDto.getCess());
			dto.setRecords(sumDto.getRecords());
		}
		gstnList.add(dto);
		return gstnList;
		
	}
	public List<Gstr1NilRatedSummarySectionDto> getNilGstnData(
			List<Gstr1NilRatedSummarySectionDto> gstnSummary) {
		
		List<Gstr1NilRatedSummarySectionDto> gstnList=new ArrayList<>();
		Gstr1NilRatedSummarySectionDto dto=new Gstr1NilRatedSummarySectionDto();
		for(Gstr1NilRatedSummarySectionDto sumDto : gstnSummary)
		{
			
			dto.setRecordCount(sumDto.getRecordCount());
			dto.setTotalExempted(sumDto.getTotalExempted());
			dto.setTotalNilRated(sumDto.getTotalNilRated());
			dto.setTotalNonGST(sumDto.getTotalNonGST());
		}
		gstnList.add(dto);
		return gstnList;
		
	}
	public List<Gstr1DocIssuedSummarySectionDto> getDocGstnData(
			List<Gstr1DocIssuedSummarySectionDto> gstnSummary) {
		
		List<Gstr1DocIssuedSummarySectionDto> gstnList=new ArrayList<>();
		Gstr1DocIssuedSummarySectionDto dto=new Gstr1DocIssuedSummarySectionDto();
		for(Gstr1DocIssuedSummarySectionDto sumDto : gstnSummary)
		{
			
			dto.setRecords(sumDto.getRecords());
			dto.setTotalIssued(sumDto.getTotalIssued());
			dto.setNetIssued(sumDto.getNetIssued());
			dto.setCancelled(sumDto.getCancelled());
		}
		gstnList.add(dto);
		return gstnList;
		
	}


}
