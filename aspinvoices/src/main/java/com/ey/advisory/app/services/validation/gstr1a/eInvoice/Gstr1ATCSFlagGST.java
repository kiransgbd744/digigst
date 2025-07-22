package com.ey.advisory.app.services.validation.gstr1a.eInvoice;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.beust.jcommander.Strings;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AOutwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.google.common.collect.ImmutableList;

/**
 * @author Shashikant.Shukla
 *
 */
@Component("Gstr1ATCSFlagGST")
public class Gstr1ATCSFlagGST
		implements DocRulesValidator<Gstr1AOutwardTransDocument> {

	private static final List<String> YORNFLAG = ImmutableList
			.of(GSTConstants.Y, GSTConstants.N, GSTConstants.E, GSTConstants.O);

	private static final List<String> TCS_FLAG = ImmutableList
			.of(GSTConstants.Y, GSTConstants.E, GSTConstants.O);

	private static final List<String> DOC_TYPE = ImmutableList.of(
			GSTConstants.INV, GSTConstants.CR, GSTConstants.DR,
			GSTConstants.RNV, GSTConstants.RCR, GSTConstants.RDR);

	private static final List<String> SUPPLY_TYPE = ImmutableList.of(
			GSTConstants.TAX, GSTConstants.DTA, GSTConstants.DXP,
			GSTConstants.SEZWP, GSTConstants.SEZWOP);

	private static final List<String> SUPPLY_TYPE1 = ImmutableList
			.of(GSTConstants.NIL, GSTConstants.NON, GSTConstants.EXT);

	@Override
	public List<ProcessingResult> validate(Gstr1AOutwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		String tcsFlag = document.getTcsFlag();
		String docType = document.getDocType();
		String supplyType = document.getSupplyType();
		if (Strings.isStringEmpty(tcsFlag) || Strings.isStringEmpty(docType)
				|| Strings.isStringEmpty(supplyType))
			return errors;
		boolean valid = valid(document.getTcsFlag());
		if (!valid) {
			List<String> errorLocations = new ArrayList<>();
			errorLocations.add(GSTConstants.TCSFlag);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER0096",
					"Invalid Tcs Flag.", location));
			return errors;

		}
		String cgstin = document.getCgstin();
		String ecomGstin = document.getEgstin();
		if (GSTConstants.URP.equalsIgnoreCase(cgstin)) {
			cgstin = null;
		}
		if (GSTConstants.URP.equalsIgnoreCase(ecomGstin)) {
			ecomGstin = null;
		}
		if (Strings.isStringEmpty(tcsFlag)) {
			tcsFlag = GSTConstants.N;
		}
		if (DOC_TYPE.contains(docType.toUpperCase())
				&& SUPPLY_TYPE.contains(supplyType.toUpperCase())
				&& !Strings.isStringEmpty(cgstin)
				&& GSTConstants.Y.equalsIgnoreCase(document.getReverseCharge())
				&& TCS_FLAG.contains(tcsFlag.toUpperCase())) {
			List<String> errorLocations = new ArrayList<>();
			errorLocations.add(GSTConstants.TCSFlag);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER0096",
					"Invalid Tcs Flag.", location));
			return errors;
		}
		if (DOC_TYPE.contains(docType.toUpperCase())
				&& SUPPLY_TYPE1.contains(supplyType.toUpperCase())
				&& GSTConstants.N.equalsIgnoreCase(document.getReverseCharge())
				&& TCS_FLAG.contains(tcsFlag.toUpperCase())
				&& !Strings.isStringEmpty(ecomGstin)) {
			List<String> errorLocations = new ArrayList<>();
			errorLocations.add(GSTConstants.TCSFlag);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER0096",
					"Invalid Tcs Flag.", location));
			return errors;
		}

		return errors;
	}

	public static boolean valid(String flag) {
		if (YORNFLAG.contains(flag.toUpperCase())) {
			return true;
		}
		return false;

	}

}
