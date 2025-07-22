package com.ey.advisory.app.services.validation.gstr1a.eInvoice;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AOutwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.google.common.base.Strings;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("Gstr1ACMEcomGstinValidator")
public class Gstr1ACMEcomGstinValidator
		implements DocRulesValidator<Gstr1AOutwardTransDocument> {

	private static final String REGGSTN_REGEX = "^[0-9][0-9][A-Za-z][A-Za-z][A-Za-z]"
			+ "[A-Za-z][A-Za-z][0-9][0-9][0-9][0-9][A-Za-z][A-Za-z0-9]"
			+ "[A-Za-z0-9][A-Za-z0-9]$";

	private static final String REGGSTN_REGEX1 = "^[0-9][0-9][A-Za-z][A-Za-z][A-Za-z]"
			+ "[A-Za-z][0-9][0-9][0-9][0-9][0-9][A-Za-z][A-Za-z0-9]"
			+ "[A-Za-z0-9][A-Za-z0-9]$";
	// [0-9]{2}[a-zA-Z]{5}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[C]{1}[0-9a-zA-Z]{1}
	private static final String ECOMGSTN_REGEX = "^[0-9][0-9][A-Za-z][A-Za-z][A-Za-z]"
			+ "[A-Za-z][A-Za-z][0-9][0-9][0-9][0-9][A-Za-z][A-Za-z1-9]"
			+ "[C][A-Za-z0-9]$";

	@Override
	public List<ProcessingResult> validate(Gstr1AOutwardTransDocument document,
			ProcessingContext context) {
		String tcsFlag = document.getTcsFlag();
		String ecomgstin = document.getEgstin();
		if (GSTConstants.URP.equalsIgnoreCase(ecomgstin)) {
			ecomgstin = null;
		}
		List<ProcessingResult> errors = new ArrayList<>();
		if (Strings.isNullOrEmpty(tcsFlag)
				|| GSTConstants.N.equalsIgnoreCase(tcsFlag))
			return errors;
		if (Stream.of(GSTConstants.Y, GSTConstants.E)
				.anyMatch(tcsFlag::equalsIgnoreCase)
				&& Strings.isNullOrEmpty(ecomgstin)) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.E_ComGstin);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, " ER0099",
					"E-Com GSTIN cannot be left Blank", location));
			return errors;
		}
		if (!Strings.isNullOrEmpty(ecomgstin)) {
			ecomgstin = ecomgstin.trim();
		}

		if (GSTConstants.Y.equalsIgnoreCase(tcsFlag) && !Pattern
				.compile(ECOMGSTN_REGEX).matcher(ecomgstin).matches()) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.E_ComGstin);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"inside  the CMEcomGstinValidator and validate mehod and GSTConstants.Y.equalsIgnoreCase(tcsFlag) condition");
				LOGGER.debug(msg);
			}
			errors.add(new ProcessingResult(APP_VALIDATION, "ER0098",
					" Invalid E-Com GSTIN.", location));
			return errors;

		}
		if (GSTConstants.E.equalsIgnoreCase(tcsFlag)) {
			boolean isValidGstin = Pattern.compile(ECOMGSTN_REGEX)
					.matcher(ecomgstin).matches()
					|| Pattern.compile(REGGSTN_REGEX).matcher(ecomgstin.trim())
							.matches()
					|| Pattern.compile(REGGSTN_REGEX1).matcher(ecomgstin.trim())
							.matches();
			if (!isValidGstin) {
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.E_ComGstin);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"inside CMEcomGstinValidator and validate method");
				}

				errors.add(new ProcessingResult(APP_VALIDATION, "ER0098",
						"Invalid E-Com GSTIN.", location));
			}
		}
		if (GSTConstants.O.equalsIgnoreCase(tcsFlag)
				&& !Strings.isNullOrEmpty(ecomgstin)) {
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"inside  the CMEcomGstinValidator and validate mehod and Stream.of(GSTConstants.O, GSTConstants.E)");
				LOGGER.debug(msg);
			}
			// 32AAICA3918J1C2

			if (Pattern.compile(REGGSTN_REGEX).matcher(ecomgstin.trim())
					.matches()
					|| Pattern.compile(REGGSTN_REGEX1).matcher(ecomgstin.trim())
							.matches()) {
				if (ecomgstin.substring(13, 14)
						.equalsIgnoreCase(GSTConstants.C)) {
					Set<String> errorLocations = new HashSet<>();
					errorLocations.add(GSTConstants.E_ComGstin);
					TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
							null, errorLocations.toArray());

					errors.add(new ProcessingResult(APP_VALIDATION, "ER0098",
							"Invalid E-Com GSTIN.", location));
					return errors;
				}
			} else {

				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(
							" inside CMEcomGstinValidator and validate mehod and in else condition");
					LOGGER.debug(msg);
				}
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.E_ComGstin);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());

				errors.add(new ProcessingResult(APP_VALIDATION, "ER0098",
						"Invalid E-Com GSTIN.", location));
				return errors;

			}

		}
		return errors;
	}

}
