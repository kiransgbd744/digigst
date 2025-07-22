package com.ey.advisory.app.services.validation.sales;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GSTConstants.HSNORSAC;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.admin.data.repositories.client.HsnOrSacRepository;
import com.ey.advisory.app.data.entities.client.OutwardTransDocLineItem;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.services.strcutvalidation.ValidatorUtil;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.multitenancy.TenantContext;

import com.google.common.base.Strings;
/**
 * 
 * @author Mahesh.Golla
 * 
 * BR_OUTWARD_5
 */
/**
 * 
 *   This class is responsible for validating the to check the HSN or SAC in 
 *   Master table if it exist it's show like there is no error 
 *   if it is not exist in global master it throws the error description like
 *   HSN or SAC should be validated against HSN or SAC Global master
 *    
 *
 */

@Component("hsnOrSacValidator")
public class HsnOrSacValidator
		implements DocRulesValidator<OutwardTransDocument> {
	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstinInfoRepository;
	@Autowired
	@Qualifier("hsnOrSacRepository")
	private HsnOrSacRepository hsnOrSacRepository;
	/**
	 *  Here we are getting HSN code from Global master then compare the data 
	 *  itself 
	 *  @param  OutwardTransDocument
	 *  @param  ProcessingContext
	 *  @return List<ProcessingResult>
	 */

	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {
		String groupCode = TenantContext.getTenantId();
		List<ProcessingResult> errors = new ArrayList<ProcessingResult>();
		List<OutwardTransDocLineItem> items = document.getLineItems();
		Set<String> errorLocations = new HashSet<>();
		IntStream.range(0, items.size()).forEach(idx -> {
			OutwardTransDocLineItem item = items.get(idx);
			if(!Strings.isNullOrEmpty(item.getHsnSac())){
			if ( !ValidatorUtil.isEvenNumber(item.getHsnSac().length())) {
				errorLocations.add(HSNORSAC);
				TransDocProcessingResultLoc location = 
						                  new TransDocProcessingResultLoc(
						idx, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER072",
						"Invalid HSN / SAC",
						location));
			}
			}
		});
		return errors;
	}

}
