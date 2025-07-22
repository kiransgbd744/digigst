package com.ey.advisory.app.services.daos.gstr6a;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.docs.dto.gstr6a.Gstr6ASummaryDataItemsResponseDto;
import com.ey.advisory.app.docs.dto.gstr6a.Gstr6ASummaryDataRequestDto;
import com.ey.advisory.app.docs.dto.gstr6a.Gstr6ASummaryDataResponseDto;

/**
 * 
 * @author SriBhavya
 *
 */

@Component("Gstr6AReviewSummaryDetails")

public class Gstr6AReviewSummaryDetails {

	@Autowired
	@Qualifier("Gstr6ASummaryDataDaoImpl")
	Gstr6ASummaryDataDaoImpl gstr6ASummaryDataDaoImpl;
	
	
	private static final String B2B = "B2B";
	private static final String B2BA = "B2BA";
	private static final String CDN = "CDN";
	private static final String CDNA = "CDNA";
	
	private static final String CR_NOTE = "Credit Note";
	private static final String DR_NOTE = "Debit Note";
	private static final String CR_NOTE_AMENDMENT = "Credit Note Amendment";
	private static final String DR_NOTE_AMENDMENT = "Debit Note Amendment";


	public List<Gstr6ASummaryDataResponseDto> getReviewSummaryData(
			Gstr6ASummaryDataRequestDto criteria) {

		List<Gstr6ASummaryDataResponseDto> entityResponse = gstr6ASummaryDataDaoImpl
				.getGstr6ASummaryData(criteria);
		
		List<Gstr6ASummaryDataResponseDto> totalRecords = new ArrayList<>();
		
		if (entityResponse != null && !entityResponse.isEmpty()) {	
			
			Map<String, List<Gstr6ASummaryDataResponseDto>> mapGstr6ReviewRespItem = getGstr6AReviewRespItem(
					entityResponse);
			getGstr6AB2BSection(mapGstr6ReviewRespItem,totalRecords);
			getGstr6AB2BASection(mapGstr6ReviewRespItem,totalRecords);
			getGstr6ACDNSection(mapGstr6ReviewRespItem,totalRecords);
			getGstr6ACDNASection(mapGstr6ReviewRespItem,totalRecords);
			
		}else if(entityResponse == null || entityResponse.isEmpty()){
			
			Gstr6ASummaryDataResponseDto b2bData = new Gstr6ASummaryDataResponseDto();
			b2bData.setTable(B2B);
			totalRecords.add(b2bData);
			
			Gstr6ASummaryDataResponseDto b2baData = new Gstr6ASummaryDataResponseDto();
			b2baData.setTable(B2BA);
			totalRecords.add(b2baData);
			
			Gstr6ASummaryDataResponseDto cdnData = new Gstr6ASummaryDataResponseDto();
			List<Gstr6ASummaryDataItemsResponseDto> cdnItemsList = new ArrayList<>();
			Gstr6ASummaryDataItemsResponseDto crData = new Gstr6ASummaryDataItemsResponseDto();
			Gstr6ASummaryDataItemsResponseDto drData = new Gstr6ASummaryDataItemsResponseDto();			
			crData.setTable(CR_NOTE);
			cdnItemsList.add(crData);
			drData.setTable(DR_NOTE);
			cdnItemsList.add(drData);
			cdnData.setTable(CDN);
			cdnData.setItems(cdnItemsList);			
			totalRecords.add(cdnData);
			
			Gstr6ASummaryDataResponseDto cdnaData = new Gstr6ASummaryDataResponseDto();
			List<Gstr6ASummaryDataItemsResponseDto> cdnaItemsList = new ArrayList<>();
			Gstr6ASummaryDataItemsResponseDto rcrData = new Gstr6ASummaryDataItemsResponseDto();
			Gstr6ASummaryDataItemsResponseDto rdrData = new Gstr6ASummaryDataItemsResponseDto();			
			rcrData.setTable(CR_NOTE_AMENDMENT);
			cdnaItemsList.add(rcrData);
			rdrData.setTable(DR_NOTE_AMENDMENT);
			cdnaItemsList.add(rdrData);
			cdnaData.setTable(CDNA);
			cdnaData.setItems(cdnaItemsList);			
			totalRecords.add(cdnaData);
		}
		return totalRecords;
	}

	
	
	
	private void getGstr6ACDNASection(Map<String, List<Gstr6ASummaryDataResponseDto>> mapGstr6ReviewRespItem,
			List<Gstr6ASummaryDataResponseDto> totalRecords) {
		List<Gstr6ASummaryDataResponseDto> respCDNADtos = mapGstr6ReviewRespItem
				.get("CDNA");
		Gstr6ASummaryDataResponseDto cdna = new Gstr6ASummaryDataResponseDto();
		List<Gstr6ASummaryDataItemsResponseDto> cdnaItems = new ArrayList<>();
		Gstr6ASummaryDataItemsResponseDto rcr = new Gstr6ASummaryDataItemsResponseDto();
		Gstr6ASummaryDataItemsResponseDto rdr = new Gstr6ASummaryDataItemsResponseDto();
		if (respCDNADtos != null) {				
			respCDNADtos.forEach(response -> {					
					if (response.getDocType().equalsIgnoreCase("C")) {
						rcr.setTable(CR_NOTE_AMENDMENT);
						rcr.setCount(response.getCount());
						rcr.setInVoiceVal(response.getInVoiceVal());
						rcr.setTaxableValue(response.getTaxableValue());
						rcr.setTotalTax(response.getTotalTax());
						rcr.setIgst(response.getIgst());
						rcr.setCgst(response.getCgst());
						rcr.setSgst(response.getSgst());
						rcr.setCess(response.getCess());
						cdnaItems.add(rcr);
					}else if (response.getDocType().equalsIgnoreCase("D")) {
						rdr.setTable(DR_NOTE_AMENDMENT);
						rdr.setCount(response.getCount());
						rdr.setInVoiceVal(response.getInVoiceVal());
						rdr.setTaxableValue(response.getTaxableValue());
						rdr.setTotalTax(response.getTotalTax());
						rdr.setIgst(response.getIgst());
						rdr.setCgst(response.getCgst());
						rdr.setSgst(response.getSgst());
						rdr.setCess(response.getCess());
						cdnaItems.add(rdr);
					}
			});
			if(!cdnaItems.contains(rcr)){
				rcr.setTable(CR_NOTE_AMENDMENT);
				cdnaItems.add(rcr);
			}
			if(!cdnaItems.contains(rdr)){
				rdr.setTable(DR_NOTE_AMENDMENT);
				cdnaItems.add(rdr);
			}			
			if ((cdnaItems != null && !cdnaItems.isEmpty())) {
				cdna.setTable(CDNA);
				cdna.setCount(rcr.getCount().add(rdr.getCount()));
				cdna.setInVoiceVal(
						rdr.getInVoiceVal().subtract(rcr.getInVoiceVal()));
				cdna.setTaxableValue(
						rdr.getTaxableValue().subtract(rcr.getTaxableValue()));
				cdna.setTotalTax(rdr.getTotalTax().subtract(rcr.getTotalTax()));
				cdna.setIgst(rdr.getIgst().subtract(rcr.getIgst()));
				cdna.setCgst(rdr.getCgst().subtract(rcr.getCgst()));
				cdna.setSgst(rdr.getSgst().subtract(rcr.getSgst()));
				cdna.setCess(rdr.getCess().subtract(rcr.getCess()));
				cdna.setItems(cdnaItems);
				totalRecords.add(cdna);
			}
		}else {							
			rcr.setTable(CR_NOTE_AMENDMENT);
			cdnaItems.add(rcr);
			rdr.setTable(DR_NOTE_AMENDMENT);
			cdnaItems.add(rdr);
			cdna.setTable(CDNA);
			cdna.setItems(cdnaItems);			
			totalRecords.add(cdna);			
		}	
	}

	private void getGstr6ACDNSection(Map<String, List<Gstr6ASummaryDataResponseDto>> mapGstr6ReviewRespItem,
			List<Gstr6ASummaryDataResponseDto> totalRecords) {
		List<Gstr6ASummaryDataResponseDto> respCDNDtos = mapGstr6ReviewRespItem
				.get("CDN");
		Gstr6ASummaryDataResponseDto cdn = new Gstr6ASummaryDataResponseDto();
		Gstr6ASummaryDataItemsResponseDto cr = new Gstr6ASummaryDataItemsResponseDto();
		Gstr6ASummaryDataItemsResponseDto dr = new Gstr6ASummaryDataItemsResponseDto();	
		List<Gstr6ASummaryDataItemsResponseDto> cdnItems = new ArrayList<>();
		if (respCDNDtos != null) {				
			respCDNDtos.forEach(response -> {					
				if (response.getDocType().equalsIgnoreCase("C")) {
					cr.setTable(CR_NOTE);
					cr.setCount(response.getCount());
					cr.setInVoiceVal(response.getInVoiceVal());
					cr.setTaxableValue(response.getTaxableValue());
					cr.setTotalTax(response.getTotalTax());
					cr.setIgst(response.getIgst());
					cr.setCgst(response.getCgst());
					cr.setSgst(response.getSgst());
					cr.setCess(response.getCess());
					cdnItems.add(cr);
				}else if (response.getDocType().equalsIgnoreCase("D")) {
					dr.setTable(DR_NOTE);
					dr.setCount(response.getCount());
					dr.setInVoiceVal(response.getInVoiceVal());
					dr.setTaxableValue(response.getTaxableValue());
					dr.setTotalTax(response.getTotalTax());
					dr.setIgst(response.getIgst());
					dr.setCgst(response.getCgst());
					dr.setSgst(response.getSgst());
					dr.setCess(response.getCess());
					cdnItems.add(dr);
				}				
			});	
			if(!cdnItems.contains(cr)){
				cr.setTable(CR_NOTE);
				cdnItems.add(cr);
			}
			if(!cdnItems.contains(dr)){
				dr.setTable(DR_NOTE);
				cdnItems.add(dr);
			}
			if (cdnItems != null && !cdnItems.isEmpty()) {					
				cdn.setTable(CDN);
				cdn.setCount(cr.getCount().add(dr.getCount()));
				cdn.setInVoiceVal(
						dr.getInVoiceVal().subtract(cr.getInVoiceVal()));
				cdn.setTaxableValue(
						dr.getTaxableValue().subtract(cr.getTaxableValue()));
				cdn.setTotalTax(dr.getTotalTax().subtract(cr.getTotalTax()));
				cdn.setIgst(dr.getIgst().subtract(cr.getIgst()));
				cdn.setCgst(dr.getCgst().subtract(cr.getCgst()));
				cdn.setSgst(dr.getSgst().subtract(cr.getSgst()));
				cdn.setCess(dr.getCess().subtract(cr.getCess()));
				cdn.setItems(cdnItems);
				totalRecords.add(cdn);
			}	
		}else {							
			cr.setTable(CR_NOTE);
			cdnItems.add(cr);
			dr.setTable(DR_NOTE);
			cdnItems.add(dr);
			cdn.setTable(CDN);
			cdn.setItems(cdnItems);			
			totalRecords.add(cdn);			
		}
		
	}




	private void getGstr6AB2BASection(Map<String, List<Gstr6ASummaryDataResponseDto>> mapGstr6ReviewRespItem,
			List<Gstr6ASummaryDataResponseDto> totalRecords) {
		List<Gstr6ASummaryDataResponseDto> respB2BADtos = mapGstr6ReviewRespItem
				.get("B2BA");
		if (respB2BADtos != null) {	
			Gstr6ASummaryDataResponseDto b2ba = new Gstr6ASummaryDataResponseDto();
			respB2BADtos.forEach(response -> {
				b2ba.setTable(response.getTable());
				b2ba.setCount(response.getCount());
				b2ba.setInVoiceVal(response.getInVoiceVal());
				b2ba.setTaxableValue(response.getTaxableValue());
				b2ba.setTotalTax(response.getTotalTax());
				b2ba.setIgst(response.getIgst());
				b2ba.setCgst(response.getCgst());
				b2ba.setSgst(response.getSgst());
				b2ba.setCess(response.getCess());
				totalRecords.add(b2ba);		
			});			
		}else {
			Gstr6ASummaryDataResponseDto b2bDefaultData = new Gstr6ASummaryDataResponseDto();
			b2bDefaultData.setTable(B2BA);
			totalRecords.add(b2bDefaultData);
		}
		
	}




	private void getGstr6AB2BSection(Map<String, List<Gstr6ASummaryDataResponseDto>> mapGstr6ReviewRespItem,
			List<Gstr6ASummaryDataResponseDto> totalRecords) {		
		List<Gstr6ASummaryDataResponseDto> respB2BDtos = mapGstr6ReviewRespItem
				.get("B2B");
		if (respB2BDtos != null) {	
			Gstr6ASummaryDataResponseDto b2b = new Gstr6ASummaryDataResponseDto();
			respB2BDtos.forEach(response -> {
				b2b.setTable(response.getTable());
				b2b.setCount(response.getCount());
				b2b.setInVoiceVal(response.getInVoiceVal());
				b2b.setTaxableValue(response.getTaxableValue());
				b2b.setTotalTax(response.getTotalTax());
				b2b.setIgst(response.getIgst());
				b2b.setCgst(response.getCgst());
				b2b.setSgst(response.getSgst());
				b2b.setCess(response.getCess());
				totalRecords.add(b2b);
			});			
		}else {
			Gstr6ASummaryDataResponseDto b2bDefaultData = new Gstr6ASummaryDataResponseDto();
			b2bDefaultData.setTable(B2B);
			totalRecords.add(b2bDefaultData);
		}		
	}




	private Map<String, List<Gstr6ASummaryDataResponseDto>> getGstr6AReviewRespItem(
			List<Gstr6ASummaryDataResponseDto> entityResponse) {
		Map<String, List<Gstr6ASummaryDataResponseDto>> mapRevSumResItemDtos = new HashMap<>();
		entityResponse.forEach(respItemDto -> {
			StringBuilder key = new StringBuilder();
			key.append(respItemDto.getTable());
			String docKey = key.toString();
			if (mapRevSumResItemDtos.containsKey(docKey)) {
				List<Gstr6ASummaryDataResponseDto> revSumItemDtos = mapRevSumResItemDtos
						.get(docKey);
				revSumItemDtos.add(respItemDto);
				mapRevSumResItemDtos.put(docKey, revSumItemDtos);
			} else {
				List<Gstr6ASummaryDataResponseDto> revSumItemDtos = new ArrayList<>();
				revSumItemDtos.add(respItemDto);
				mapRevSumResItemDtos.put(docKey, revSumItemDtos);
			}
		});
		return mapRevSumResItemDtos;
	}

}
