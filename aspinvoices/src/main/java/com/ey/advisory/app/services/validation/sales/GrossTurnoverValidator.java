package com.ey.advisory.app.services.validation.sales;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GSTConstants.HSNORSAC;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.admin.data.repositories.client.HsnOrSacRepository;
import com.ey.advisory.app.data.entities.client.OutwardTransDocLineItem;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.multitenancy.TenantContext;
@Component("GrossTurnoverValidator")
public class GrossTurnoverValidator implements 
                  DocRulesValidator<OutwardTransDocument> {
	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstinInfoRepository;
	@Autowired
	@Qualifier("hsnOrSacRepository")
	private HsnOrSacRepository hsnOrSacRepository;
	
	
	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {
		String groupCode = TenantContext.getTenantId();
		List<ProcessingResult> errors = new ArrayList<ProcessingResult>();
		List<OutwardTransDocLineItem> items = document.getLineItems();
		
		
		IntStream.range(0, items.size()).forEach(idx -> {
			OutwardTransDocLineItem item = items.get(idx);
			
			gstinInfoRepository = StaticContextHolder.
					getBean("GSTNDetailRepository",GSTNDetailRepository.class);
if(document.getSgstin()!=null && !document.getSgstin().isEmpty()){
			List<GSTNDetailEntity> gstin = gstinInfoRepository
					              .findByGstin(document.getSgstin());
			if(gstin.size() > 0){
				BigDecimal str= new BigDecimal("15000000");
				BigDecimal str1= new BigDecimal("50000000");
				BigDecimal Grossturnover=gstin.get(0).getTurnover();
				if(Grossturnover!=null){
				if(Grossturnover.compareTo(str) > 0 
						&& Grossturnover.compareTo(str1) < 0) {
					if(item.getHsnSac()==null ||item.getHsnSac().length() < 2){
						Set<String> errorLocations = new HashSet<>();
					errorLocations.add(HSNORSAC);
						TransDocProcessingResultLoc location = 
				                  new TransDocProcessingResultLoc(
				idx, errorLocations.toArray());
		errors.add(new ProcessingResult(APP_VALIDATION, "ER240",
				"Invalid HSN/SAC - minimum 2 digits are required "
				+ "as previous year Gross Turnover is more than INR 1.5 Cr",
				location));
					}}
				
				if(Grossturnover.compareTo(str1) > 0 ){
				if(item.getHsnSac()==null || item.getHsnSac().length() < 4){
					Set<String> errorLocations = new HashSet<>();
					errorLocations.add(HSNORSAC);
					TransDocProcessingResultLoc location = 
			                  new TransDocProcessingResultLoc(
			idx, errorLocations.toArray());
	errors.add(new ProcessingResult(APP_VALIDATION, "ER242",
			"Invalid HSN/SAC - minimum 4 digits are required as "
			+ "previous year Gross Turnover is more than INR 5 Cr",
			location));
				}
				}
				
				
				}
			}	
}
				
		});
		return errors;
	}

}
