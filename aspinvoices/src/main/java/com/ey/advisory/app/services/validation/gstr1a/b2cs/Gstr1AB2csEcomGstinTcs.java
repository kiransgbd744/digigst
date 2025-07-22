/**
 * 
 */
package com.ey.advisory.app.services.validation.gstr1a.b2cs;

import java.util.ArrayList;
import java.util.List;

import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AAsEnteredB2csEntity;
import com.ey.advisory.app.services.validation.B2csBusinessRuleValidator;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;

/**
 * @author Shashikant.Shukla
 *
 */
public class Gstr1AB2csEcomGstinTcs
		implements B2csBusinessRuleValidator<Gstr1AAsEnteredB2csEntity> {
	/*
	 * @Autowired
	 * 
	 * @Qualifier("GSTNDetailRepository") private GSTNDetailRepository
	 * gstinInfoRepository;
	 */

	@Override
	public List<ProcessingResult> validate(Gstr1AAsEnteredB2csEntity document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();

		/*
		 * if (document.getSgstin() == null || document.getSgstin().isEmpty())
		 * return errors;
		 * 
		 * gstinInfoRepository = StaticContextHolder
		 * .getBean("GSTNDetailRepository", GSTNDetailRepository.class);
		 * List<GSTNDetailEntity> gstin = gstinInfoRepository
		 * .findRegDate(document.getSgstin()); if (gstin != null && gstin.size()
		 * > 0) { if (gstin.get(0).getRegistrationType() != null) { String
		 * regType = gstin.get(0).getRegistrationType(); if
		 * ("TCS".equalsIgnoreCase(regType)) {
		 * 
		 * } } }
		 */
		return errors;
	}
}
