package com.ey.advisory.app.gstr3b;

import java.util.List;

import com.ey.advisory.app.gstr3b.itc10perc.service.Gstr3BItc10PercDto;

public interface Gstr3bTable4OtherReversalService {

	List<Gstr3BItc10PercDto> getOtherReversalData(String gstin,
			String taxPeriod);
	
	void saveOthReversalChangesToUserInput(String gstin, String taxPeriod,
			List<Gstr3BGstinAspUserInputDto> userInputList);
}
