package com.ey.advisory.app.itcmatching.vendorupload;

import java.util.List;

import com.aspose.cells.Workbook;

/**
 * @author Saif.S
 *
 */
public interface VendorExclusionService {

	public Workbook downlaodVendorExclusionReport(
			List<String> reciepientPANList, List<String> vendorGSTINList);

	List<VendorGstinDto> getExcludedVendorGstinList(Long entityId);

	int softDeleteExcludedVendorGstins(List<String> reciepientPanList,
			List<String> vendorGSTINList);

	List<VendorExclusionDto> getVendorExclusionData(
			List<String> reciepientPanList, List<String> vendorGSTINList);
}
