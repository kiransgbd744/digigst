package com.ey.advisory.app.services.businessvalidation.master;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ey.advisory.admin.data.entities.client.MasterItemEntity;
import com.ey.advisory.admin.data.repositories.client.HsnOrSacRepository;
import com.ey.advisory.common.BusinessRuleValidator;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;

/**
 * 
 * @author Anand3.M
 *
 */
public class ItemHsn implements BusinessRuleValidator<MasterItemEntity> {

	@Autowired
	@Qualifier("hsnOrSacRepository")
	private HsnOrSacRepository hsnOrSacRepository;

	@Override
	public List<ProcessingResult> validate(MasterItemEntity document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();

		if (document.getHsnOrSac() != null) {
			hsnOrSacRepository = StaticContextHolder
					.getBean("hsnOrSacRepository", HsnOrSacRepository.class);
			Integer hson = document.getHsnOrSac();
			int count = hsnOrSacRepository.findByHsnOrSac(String.valueOf(hson));
			if (count == 0) {
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add("HSNorSAC");
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER1639",
						"Invalid HSN or SAC", location));
				return errors;
			}
		}
		return errors;
	}
}
