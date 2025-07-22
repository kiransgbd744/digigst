package com.ey.advisory.app.gstr3b.itc10perc.service;

import java.util.List;

import com.ey.advisory.app.gstr3b.Gstr3BGstinAspUserInputDto;

public interface Gstr3BRule38OtherReversalService {

	List<Gstr3BItc10PercDto> getOtherReversalData(String gstin,
			String taxPeriod);
	
	void saveOthReversalChangesToUserInput(String gstin, String taxPeriod,
			List<Gstr3BGstinAspUserInputDto> userInputList);
}
