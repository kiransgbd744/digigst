package com.ey.advisory.app.gstr3b;

public interface Table4d1ValidationtSaveUpdateService {

	public Table4d1ValidationStatusDto saveValidationStatusInput(String gstin, String taxPeriod,
			Table4d1ValidationStatusDto validationInput);

	public boolean isValidated(String gstin, String taxPeriod);

	public int update4D1ValidationStatus(String gstin, String taxPeriod);

	public Table4d1ValidationStatusDto get4D1ValidationStatus(String gstin,
			String taxPeriod);

}
