package com.ey.advisory.app.services.vendorcomm;

import java.util.List;

public interface VendorCommDAO {

	public List<Object[]> getDistinctCombFromLink2APR(
			Integer derivedFromTaxPeriod, Integer derivedToTaxPeriod,
			List<String> reportTypeList, boolean isApOpted);

	
	public List<Object[]> getDistinctCombFromLink2BPR(
			Integer derivedFromTaxPeriod, Integer derivedToTaxPeriod,
			List<String> reportTypeList);

	public List<Object[]> getVendorNamePR(List<String> vendorGstinList);

}
