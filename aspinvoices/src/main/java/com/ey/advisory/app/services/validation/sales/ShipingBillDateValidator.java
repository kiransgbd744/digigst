package com.ey.advisory.app.services.validation.sales;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GSTConstants.SHIPPING_BILL_DATE;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;

public class ShipingBillDateValidator 
                          implements DocRulesValidator<OutwardTransDocument> {
	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstinInfoRepository;
	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {
		
		List<ProcessingResult> errors = new ArrayList<>();
		List<String> errorLocations = new ArrayList<>();
		if(document.getShippingBillDate()!=null){
		if(document.getSgstin()!=null && !document.getSgstin().isEmpty()){
			gstinInfoRepository = StaticContextHolder.getBean(
	                 "GSTNDetailRepository", GSTNDetailRepository.class);
			List<GSTNDetailEntity> gstin = gstinInfoRepository
		              .findByGstin(document.getSgstin());
			if(gstin!=null && gstin.size() > 0){
				if(gstin.get(0).getRegDate()!=null){
			LocalDate	gstinRegDate =	gstin.get(0).getRegDate();
		LocalDate presentDate=LocalDate.now();
		if (document.getShippingBillDate().compareTo(presentDate) > 0
				|| document.getShippingBillDate().compareTo(gstinRegDate) < 0){
			errorLocations.add(SHIPPING_BILL_DATE);
			

			TransDocProcessingResultLoc location = new 
					TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER069",
					"Invalid Shipping Bill Date ",
					location));
		
		
		               }	
		          }
		     }		
		  }
		}
		return errors;
	}

}
