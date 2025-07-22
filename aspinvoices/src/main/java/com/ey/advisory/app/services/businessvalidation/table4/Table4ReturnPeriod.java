package com.ey.advisory.app.services.businessvalidation.table4;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GSTConstants.RETURN_PREIOD;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.app.caches.ehcache.Ehcachegstin;
import com.ey.advisory.app.data.entities.client.OutwardTable4ExcelEntity;
import com.ey.advisory.common.BusinessRuleValidator;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.multitenancy.TenantContext;

/**
 * 
 * @author Mahesh.Golla
 *
 */

public class Table4ReturnPeriod implements 
                             BusinessRuleValidator<OutwardTable4ExcelEntity> {
	
	/*@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstinInfoRepository;*/
	@Autowired
	@Qualifier("Ehcachegstin")
	private Ehcachegstin ehcachegstin;

	@Override
	public List<ProcessingResult> validate(OutwardTable4ExcelEntity document,
			ProcessingContext context) {
		
		List<ProcessingResult> errors = new ArrayList<>();
		List<String> errorLocations = new ArrayList<>();
		String groupCode = TenantContext.getTenantId();	
		if( document.getSgstin() != null &&  !document.getSgstin().isEmpty()){
		if(document.getRetPeriod() != null 
				&& !document.getRetPeriod().trim().isEmpty()){
		String tax = "01" + document.getRetPeriod();
	//	String gstin = document.getSgstin();
		 
		DateTimeFormatter formatter = 
				DateTimeFormatter.ofPattern("ddMMyyyy");
	   LocalDate pregst=LocalDate.of(2017, 07, 01);
	// Calculate the last day of the month.
	LocalDate returnPeriod = LocalDate.parse(tax, formatter);
	
/*	gstinInfoRepository = StaticContextHolder.getBean(
			"GSTNDetailRepository", GSTNDetailRepository.class);

	List<GSTNDetailEntity> regDate = gstinInfoRepository
			                      .findByGstin(gstin);*/
	ehcachegstin = StaticContextHolder.
			getBean("Ehcachegstin",Ehcachegstin.class);
	
	GSTNDetailEntity gstin = ehcachegstin.getGstinInfo(groupCode,
			document.getSgstin());
	if(gstin.getRegDate() != null ){
	LocalDate regDates = gstin.getRegDate();
	if (regDates != null) {
	
	if(returnPeriod.compareTo(pregst) < 0 || 
			returnPeriod.compareTo(regDates) <0){
		errorLocations.add(RETURN_PREIOD);
		TransDocProcessingResultLoc location = 
				new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
		errors.add(new ProcessingResult(APP_VALIDATION, "ER0307",
				"Return Period cannot be before Date of Registration",
				location));
		
	}}}}
		}
		return errors;
	}
	
}
