package com.ey.advisory.app.services.validation.b2cs;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GSTConstants.NewHsnOrSac;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ey.advisory.admin.data.repositories.master.HsnOrSacRepository;
import com.ey.advisory.app.caches.HsnCache;
import com.ey.advisory.app.data.entities.client.Gstr1AsEnteredB2csEntity;
import com.ey.advisory.app.services.validation.B2csBusinessRuleValidator;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;

/**
 * 
 * @author Mahesh.Golla
 *
 */
public class NewHSNorSAC
		implements B2csBusinessRuleValidator<Gstr1AsEnteredB2csEntity> {
	@Autowired
	@Qualifier("DefaultHsnCache")
	private HsnCache hsnCache;

	@Autowired
	@Qualifier("HsnOrSacRepositoryMaster")
	private HsnOrSacRepository hsnRep;

	@Override
	public List<ProcessingResult> validate(Gstr1AsEnteredB2csEntity document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		List<String> errorLocations = new ArrayList<>();
		if (document.getNewHsnOrSac() != null
				&& !document.getNewHsnOrSac().isEmpty()) {
			String hsnOrSac = document.getNewHsnOrSac().trim();
			hsnCache = StaticContextHolder.getBean("DefaultHsnCache",
					HsnCache.class);
			int getHsnOrSac = hsnCache.findhsn(hsnOrSac);
			if (getHsnOrSac <= 0) {
				hsnRep = StaticContextHolder.getBean("HsnOrSacRepositoryMaster",
						HsnOrSacRepository.class);
				int findByHsnOrSac = hsnRep.findByHsnOrSac(hsnOrSac);
				if (findByHsnOrSac == 0) {
					errorLocations.add(NewHsnOrSac);
					TransDocProcessingResultLoc location = 
							new TransDocProcessingResultLoc(
							null, errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION, "ER5020",
							"Invalid NewHSNorSAC", location));
				}
			}
		}
		return errors;
	}

}
