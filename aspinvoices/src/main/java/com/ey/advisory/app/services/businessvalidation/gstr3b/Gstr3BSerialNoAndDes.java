package com.ey.advisory.app.services.businessvalidation.gstr3b;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ey.advisory.admin.data.entities.master.Gstr3BItcEntityMaster;
import com.ey.advisory.app.caches.ehcache.EhcacheGstr3bItc;
import com.ey.advisory.app.data.entities.client.Gstr3bExcelEntity;
import com.ey.advisory.app.services.validation.BusinessRuleValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;

/**
 * 
 * @author Mahesh.Golla
 *
 */

public class Gstr3BSerialNoAndDes
		implements BusinessRuleValidator<Gstr3bExcelEntity> {

	@Autowired
	@Qualifier("DefaultGstr3BItcCache")
	private EhcacheGstr3bItc ehcacheGstr3bItc;

	@Override
	public List<ProcessingResult> validate(Gstr3bExcelEntity document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		Set<String> errorLocations = new HashSet<>();

		Integer serialNumber = Integer.parseInt(document.getSerialNo());
		String description = document.getDescription();

		if (serialNumber != null && serialNumber > 0) {
			if (description != null && !description.isEmpty()) {
				ehcacheGstr3bItc = StaticContextHolder.getBean(
						"DefaultGstr3BItcCache", EhcacheGstr3bItc.class);
				Gstr3BItcEntityMaster entity = ehcacheGstr3bItc
						.findGstr3Bitc(serialNumber);
				description = description.replaceAll("\\s", "").toUpperCase();
				String natureOfDocs = entity != null ? entity.getDescription()
						.replaceAll("\\s", "").toUpperCase() : null;
				if (!description.equalsIgnoreCase(natureOfDocs)) {
					errorLocations.add(GSTConstants.DESCRIPTION);
					errorLocations.add(GSTConstants.SERIAL_NUMBER);
					TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
							null, errorLocations.toArray());
					errors.add(new ProcessingResult(GSTConstants.APP_VALIDATION,
							"ER6112",
							"Mapping of Serial Number and Description is in correct.",
							location));
				}
			}
		}
		return errors;
	}

}
