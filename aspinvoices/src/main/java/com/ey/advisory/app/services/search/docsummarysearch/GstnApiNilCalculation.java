package com.ey.advisory.app.services.search.docsummarysearch;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.Gstr1BasicCDSectionSummaryDto;
import com.ey.advisory.app.docs.dto.Gstr1BasicNilSectionSummaryDto;
import com.ey.advisory.app.docs.dto.Gstr1BasicSectionSummaryDto;
import com.ey.advisory.app.docs.dto.Gstr1CompleteSummaryDto;
import com.ey.advisory.app.docs.dto.Gstr1SummaryCDSectionDto;
import com.ey.advisory.app.docs.dto.Gstr1SummaryNilSectionDto;
import com.ey.advisory.app.docs.dto.Gstr1SummarySectionDto;

/**
 * @author Balakrishna.S
 *
 */
@Service("GstnApiNilCalculation")
public class GstnApiNilCalculation {
	
	@Autowired
	@Qualifier("Gstr1B2CSCalculation")
	Gstr1B2CSCalculation gstnData;
	
	public List<Gstr1SummaryNilSectionDto> addNilGstnDocTypes(
			List<? extends Gstr1CompleteSummaryDto> gstnResult){
		
		//Gstr1SummaryNilSectionDto addNilGstnData = new Gstr1SummaryNilSectionDto();
		
		List<Gstr1SummaryNilSectionDto> gstnSummary = new ArrayList<>();
		for(Gstr1CompleteSummaryDto nil : gstnResult)
		{
			
			Gstr1BasicNilSectionSummaryDto doc = nil.getNil();
			 gstnSummary = doc.getGstnSummary();
			
		/*	addNilGstnData = gstnData.addNilGstnData(gstnSummary);*/
		}
		
		return gstnSummary;
		
	}
	
	public List<Gstr1SummaryNilSectionDto> addNilaspGstnDocTypes(
			List<Gstr1SummaryNilSectionDto> gstnResult){
		
		
			
		List<Gstr1SummaryNilSectionDto> addNilGstnData2 = gstnData
				.addNilGstnData(gstnResult);
	
		
		return addNilGstnData2;
		
	}
	
	public List<Gstr1SummarySectionDto> addCDNRGstnDocTypes(
			List<? extends Gstr1CompleteSummaryDto> gstnResult){
		
		List<Gstr1SummarySectionDto> gstnSummary = new ArrayList<>();
		for(Gstr1CompleteSummaryDto cdnr : gstnResult)
		{
			
			Gstr1BasicSectionSummaryDto doc = cdnr.getGstnCdnr();
			 gstnSummary = doc.getGstnSummary();
			
		}
		
		return gstnSummary;
		
	}
	
	public List<Gstr1SummarySectionDto> addCDNRAGstnDocTypes(
			List<? extends Gstr1CompleteSummaryDto> gstnResult){
		
		List<Gstr1SummarySectionDto> gstnSummary = new ArrayList<>();
		for(Gstr1CompleteSummaryDto cdnra : gstnResult)
		{
			
			Gstr1BasicSectionSummaryDto doc = cdnra.getGstnCdnra();
			 gstnSummary = doc.getGstnSummary();
			
		}
		
		return gstnSummary;
		
	}
	public List<Gstr1SummarySectionDto> addCDNURGstnDocTypes(
			List<? extends Gstr1CompleteSummaryDto> gstnResult){
		
		List<Gstr1SummarySectionDto> gstnSummary = new ArrayList<>();
		for(Gstr1CompleteSummaryDto cdnur : gstnResult)
		{
			
			Gstr1BasicSectionSummaryDto doc = cdnur.getGstnCdnur();
			 gstnSummary = doc.getGstnSummary();
			
		}
		
		return gstnSummary;
		
	}
	
	public List<Gstr1SummarySectionDto> addCDNURAGstnDocTypes(
			List<? extends Gstr1CompleteSummaryDto> gstnResult){
		
		List<Gstr1SummarySectionDto> gstnSummary = new ArrayList<>();
		for(Gstr1CompleteSummaryDto cdnura : gstnResult)
		{
			
			Gstr1BasicSectionSummaryDto doc = cdnura.getGstnCdnura();
			 gstnSummary = doc.getGstnSummary();
			
		}
		
		return gstnSummary;
		
	}
	
	public List<Gstr1SummarySectionDto> addB2CSGstnDocTypes(
			List<? extends Gstr1CompleteSummaryDto> gstnResult){
		
		List<Gstr1SummarySectionDto> gstnSummary = new ArrayList<>();
		for(Gstr1CompleteSummaryDto b2cs : gstnResult)
		{
			
			Gstr1BasicSectionSummaryDto doc = b2cs.getGstnB2cs();
			 gstnSummary = doc.getGstnSummary();
			
		}
		
		return gstnSummary;
		
	}
	
	public List<Gstr1SummarySectionDto> addB2CSAGstnDocTypes(
			List<? extends Gstr1CompleteSummaryDto> gstnResult){
		
		List<Gstr1SummarySectionDto> gstnSummary = new ArrayList<>();
		for(Gstr1CompleteSummaryDto b2csa : gstnResult)
		{
			
			Gstr1BasicSectionSummaryDto doc = b2csa.getGstnB2csa();
			 gstnSummary = doc.getGstnSummary();
			
		}
		
		return gstnSummary;
		
	}
	
	public List<Gstr1SummarySectionDto> addAdvRecGstnDocTypes(
			List<? extends Gstr1CompleteSummaryDto> gstnResult){
		
		List<Gstr1SummarySectionDto> gstnSummary = new ArrayList<>();
		for(Gstr1CompleteSummaryDto  advRec : gstnResult)
		{
			
			Gstr1BasicSectionSummaryDto doc = advRec.getAt();
			 gstnSummary = doc.getGstnSummary();
			
		}
		
		return gstnSummary;
		
	}
	
	public List<Gstr1SummarySectionDto> addAdvRecAmGstnDocTypes(
			List<? extends Gstr1CompleteSummaryDto> gstnResult){
		
		List<Gstr1SummarySectionDto> gstnSummary = new ArrayList<>();
		for(Gstr1CompleteSummaryDto  advRecAm : gstnResult)
		{
			
			Gstr1BasicSectionSummaryDto doc = advRecAm.getAta();
			 gstnSummary = doc.getGstnSummary();
			
		}
		
		return gstnSummary;
		
	}
	public List<Gstr1SummarySectionDto> addAdvAdjGstnDocTypes(
			List<? extends Gstr1CompleteSummaryDto> gstnResult){
		
		List<Gstr1SummarySectionDto> gstnSummary = new ArrayList<>();
		for(Gstr1CompleteSummaryDto  advAdj : gstnResult)
		{
			
			Gstr1BasicSectionSummaryDto doc = advAdj.getTxpd();
			 gstnSummary = doc.getGstnSummary();
			
		}
		
		return gstnSummary;
		
	}
	
	public List<Gstr1SummarySectionDto> addAdvAdjAmGstnDocTypes(
			List<? extends Gstr1CompleteSummaryDto> gstnResult){
		
		List<Gstr1SummarySectionDto> gstnSummary = new ArrayList<>();
		for(Gstr1CompleteSummaryDto  advAdjAm : gstnResult)
		{
			
			Gstr1BasicSectionSummaryDto doc = advAdjAm.getTxpda();
			 gstnSummary = doc.getGstnSummary();
			
		}
		
		return gstnSummary;
		
	}
	
}
