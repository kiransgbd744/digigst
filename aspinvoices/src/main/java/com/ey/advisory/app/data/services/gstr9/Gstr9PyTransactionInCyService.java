package com.ey.advisory.app.data.services.gstr9;

import java.util.List;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.docs.dto.gstr9.Gstr9PyTransInCyDashboardDto;
import com.ey.advisory.app.docs.dto.gstr9.Gstr9PyTransactionInCyMapDto;

@Component("Gstr9PyTransactionInCyService")
public interface Gstr9PyTransactionInCyService {
	
	List<Gstr9PyTransInCyDashboardDto> getGstr9PyTransInCyDetails(String gstin,
			String fy);

	void saveGstr9PyTransInCyUserInput(List<Gstr9PyTransactionInCyMapDto> userInput, String gstin, String fy);
}
