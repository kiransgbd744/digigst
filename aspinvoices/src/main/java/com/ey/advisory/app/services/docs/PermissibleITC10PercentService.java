package com.ey.advisory.app.services.docs;

import java.util.List;

import com.ey.advisory.app.docs.dto.PermissibleITC10PercentDto;
import com.ey.advisory.app.docs.dto.PermissibleITC10PerGstinDetailsDto;

/**
 * 
 * @author Rajesh NK
 *
 */
public interface PermissibleITC10PercentService {

	List<PermissibleITC10PercentDto> getPermissibleRecord(
			List<String> gstnsList, String toTaxPeriod, String fromTaxPeriod,
			List<String> docType, String reconType);

	public List<PermissibleITC10PerGstinDetailsDto> getPermissibleGstinDetails(
			List<String> gstins, String toTaxPeriod, String fromTaxPeriod,
			List<String> docType, String reconType);

}
