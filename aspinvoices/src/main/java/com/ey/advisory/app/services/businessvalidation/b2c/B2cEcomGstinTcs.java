/**
 * 
 */
package com.ey.advisory.app.services.businessvalidation.b2c;

import java.util.ArrayList;
import java.util.List;

import com.ey.advisory.app.data.entities.client.OutwardB2cEntity;
import com.ey.advisory.common.BusinessRuleValidator;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;

/**
 * @author Mahesh.Golla
 *
 */
public class B2cEcomGstinTcs
		implements BusinessRuleValidator<OutwardB2cEntity> {
	/*@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstinInfoRepository;*/

	@Override
	public List<ProcessingResult> validate(OutwardB2cEntity document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();

		/*if (document.getSgstin() == null || document.getSgstin().isEmpty())
			return errors;

		gstinInfoRepository = StaticContextHolder
				.getBean("GSTNDetailRepository", GSTNDetailRepository.class);
		List<GSTNDetailEntity> gstin = gstinInfoRepository
				.findRegDate(document.getSgstin());
		if (gstin != null && gstin.size() > 0) {
			if (gstin.get(0).getRegistrationType() != null) {
				String regType = gstin.get(0).getRegistrationType();
				if ("TCS".equalsIgnoreCase(regType)) {
					
				}
			}
		}*/
		return errors;
	}
}
