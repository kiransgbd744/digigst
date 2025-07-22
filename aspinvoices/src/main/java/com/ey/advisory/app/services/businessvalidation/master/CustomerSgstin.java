/*package com.ey.advisory.app.services.businessvalidation.master;

import static com.ey.advisory.app.data.entities.client.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.app.data.entities.client.GSTConstants.SGSTIN;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.admin.data.entities.client.MasterCustomerEntity;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.services.businessvalidation.table4.BusinessRuleValidator;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;

public class CustomerSgstin implements 
                                BusinessRuleValidator<MasterCustomerEntity> {
	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstinInfoRepository;

	@Override
	public List<ProcessingResult> validate(MasterCustomerEntity document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		if (document.getSupplierGstnOrPan() != null && 
				!document.getSupplierGstnOrPan().isEmpty() && 
				document.getSupplierGstnOrPan().length() == 15) {
			gstinInfoRepository = StaticContextHolder.getBean(
					"GSTNDetailRepository", GSTNDetailRepository.class);
			String gstin = document.getSupplierGstnOrPan();
			List<GSTNDetailEntity> out = 
					gstinInfoRepository.findByGstin(gstin);
			
			if (out == null || out.isEmpty()) {
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(SGSTIN);
				TransDocProcessingResultLoc location = 
						new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER1603",
						"Supplier GSTIN is not as per On-Boarding data.",
						location));
			}
		}
		return errors;
	}
}
*/