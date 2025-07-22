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

public class HansacExistOrNot 
                        implements DocRulesValidator<OutwardTransDocument> {
	
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
			if(item.getHsnSac()!=null){
				if(ValidatorUtil.isEvenNumber(item.getHsnSac().length())){
			String hsnOrSac = item.getHsnSac();
			hsnOrSacRepository = StaticContextHolder.getBean(
					"hsnOrSacRepository", HsnOrSacRepository.class);
			int getHsnOrSac = hsnOrSacRepository
					.findByHsnOrSac(hsnOrSac);
			
			if (getHsnOrSac<=0) {
				errorLocations.add(HSNORSAC);
				TransDocProcessingResultLoc location = 
						                  new TransDocProcessingResultLoc(
						idx, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER205",
						"HSN does not exist or SAC does not exist" + "Error",
						location));
							}
				}
			}
		});
		return errors;
	}

}
