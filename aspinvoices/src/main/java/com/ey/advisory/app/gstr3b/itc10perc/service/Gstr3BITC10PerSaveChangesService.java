package com.ey.advisory.app.gstr3b.itc10perc.service;

import java.util.List;

import com.ey.advisory.app.gstr3b.Gstr3BGstinAspUserInputDto;

/**
 * @author vishal.verma
 *
 */
public interface Gstr3BITC10PerSaveChangesService {

	public void saveItcChangsToUserInputs(String gstin, String taxPeriod,
			List<Gstr3BGstinAspUserInputDto> userInputList, String status);
}
