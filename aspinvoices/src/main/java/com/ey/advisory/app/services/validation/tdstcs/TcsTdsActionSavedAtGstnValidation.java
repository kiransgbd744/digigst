package com.ey.advisory.app.services.validation.tdstcs;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ey.advisory.admin.data.entities.client.Gstr2XExcelTcsTdsEntity;
import com.ey.advisory.app.caches.TcsTdsActionSavedCache;
import com.ey.advisory.app.caches.TcsTdsRemarksSavedCache;
import com.ey.advisory.app.services.validation.B2csBusinessRuleValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import lombok.extern.slf4j.Slf4j;


/**
 * 
 * @author Mahesh.Golla
 *
 */
@Slf4j
public class TcsTdsActionSavedAtGstnValidation
		implements B2csBusinessRuleValidator<Gstr2XExcelTcsTdsEntity> {

	@Autowired
	@Qualifier("DefaultTcsTdsActionSavedCache")
	private TcsTdsActionSavedCache tcsTdsActionSavedCache;
	
	@Autowired
	@Qualifier("DefaultTcsTdsRemarksSavedCache")
	private TcsTdsRemarksSavedCache tcsTdsRemarksSavedCache;


	@Override
	public List<ProcessingResult> validate(Gstr2XExcelTcsTdsEntity document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		if (document.getSaveAction() != null
				&& !document.getSaveAction().trim().isEmpty()) {

			tcsTdsActionSavedCache = StaticContextHolder.getBean(
					"DefaultTcsTdsActionSavedCache",
					TcsTdsActionSavedCache.class);

			String actionSavedAtGstin = document.getSaveAction();
			int i = tcsTdsActionSavedCache
					.findActionSavedAt(actionSavedAtGstin);
			if (i <= 0) {
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.GSTNACTION);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER1353",
						"Invalid GSTN User Action", location));
				
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Invalid GSTN User Action error is {} ", errors);
					
				}
				
				return errors;
			}
		}
		if (document.getGstnRemarks() != null
				&& !document.getGstnRemarks().trim().isEmpty()) {

			tcsTdsRemarksSavedCache = StaticContextHolder.getBean(
					"DefaultTcsTdsRemarksSavedCache",
					TcsTdsRemarksSavedCache.class);

			String remarksSavedAtGstin = document.getGstnRemarks();
			int i = tcsTdsRemarksSavedCache
					.findActionSavedAt(remarksSavedAtGstin);
			if (i <= 0) {
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.GSTN_REMARKS);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER1354",
						"Invalid GSTN Remarks", location));
				
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Invalid GSTN Remarks error is {} ", errors);
					
				}
				
				return errors;

			}
		}
		
		if (document.getGstnRemarks() == null ||
			    document.getGstnRemarks().trim().isEmpty()) {
		
         if("R".equalsIgnoreCase(document.getSaveAction())){
        	 
        	 Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.GSTN_REMARKS);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER1354",
						"Invalid GSTN Remarks", location));
				
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Invalid GSTN Remarks error inside R is {} ", errors);
					
				}
				
				return errors;
        	 
         }
		}
		
		return errors;
	}

}
