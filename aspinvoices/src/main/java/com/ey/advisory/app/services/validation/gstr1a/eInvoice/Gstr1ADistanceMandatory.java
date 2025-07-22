package com.ey.advisory.app.services.validation.gstr1a.eInvoice;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AOutwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.EInvoiceStatus;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Shashikant.Shukla
 *
 */
@Slf4j
public class Gstr1ADistanceMandatory
		implements DocRulesValidator<Gstr1AOutwardTransDocument> {

	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	@Override
	public List<ProcessingResult> validate(Gstr1AOutwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();

		if (document.geteInvStatus() == null)
			return errors;
		if (LOGGER.isDebugEnabled()) {

			LOGGER.debug("DistanceMandatory class " + "calling for docKey{}",
					document.getDocKey());
		}

		configManager = StaticContextHolder.getBean("ConfigManagerImpl",
				ConfigManager.class);
		Map<String, Config> configMapEwb = configManager.getConfigs("EWB",
				"ewb.distance", TenantContext.getTenantId());

		boolean calcDistanceEwb = configMapEwb != null
				&& configMapEwb.get("ewb.distance.userinputalways") != null
						? "true".equalsIgnoreCase(configMapEwb
								.get("ewb.distance.userinputalways").getValue())
						: Boolean.FALSE;

		Map<String, Config> configMapEinv = configManager.getConfigs("EINV",
				"einv.distance", TenantContext.getTenantId());
		boolean calcDistanceEinv = configMapEinv != null
				&& configMapEinv.get("einv.distance.userinputalways") != null
						? "true".equalsIgnoreCase(configMapEinv
								.get("einv.distance.userinputalways")
								.getValue())
						: Boolean.FALSE;

		if (LOGGER.isDebugEnabled()) {

			LOGGER.debug("calcDistanceEwb{} for docKey{}", calcDistanceEwb,
					document.getDocKey());
			LOGGER.debug("calcDistanceEinv{} for docKey{}", calcDistanceEinv,
					document.getDocKey());
		}

		if (document.geteInvStatus() == EInvoiceStatus.NOT_OPTED
				.geteInvoiceStatusCode()
				|| document.geteInvStatus() == EInvoiceStatus.NOT_APPLICABLE
						.geteInvoiceStatusCode()) {

			if (calcDistanceEwb && document.getDistance() == null) {

				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.DISTANCE);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER15172",
						"Distance Cannot be left blank", location));
				return errors;
			}
		}
		if (document.geteInvStatus() == EInvoiceStatus.ASP_PROCESSED
				.geteInvoiceStatusCode()
				|| document.geteInvStatus() == EInvoiceStatus.ASP_ERROR
						.geteInvoiceStatusCode()) {

			if ((calcDistanceEinv) && document.getDistance() == null) {

				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.DISTANCE);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER15172",
						"Distance Cannot be left blank", location));
				return errors;
			}
		}
		return errors;
	}
}
