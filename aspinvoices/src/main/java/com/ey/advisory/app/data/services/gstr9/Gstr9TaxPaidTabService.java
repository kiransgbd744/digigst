package com.ey.advisory.app.data.services.gstr9;

import java.util.List;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.docs.dto.gstr9.Gstr9TaxPaidDashboardDto;
import com.ey.advisory.app.docs.dto.gstr9.Gstr9TaxPaidMapDto;

@Component("Gstr9TaxPaidTabService")
public interface Gstr9TaxPaidTabService {

	List<Gstr9TaxPaidDashboardDto> getGstr9TaxPaidDetails(String gstin,
			String fy);

	void saveGstr9TaxPaidUserInput(List<Gstr9TaxPaidMapDto> userInput, String gstin, String fy);
}
