package com.ey.advisory.app.itcmatching.vendorupload;

import java.util.List;

import com.ey.advisory.app.data.entities.client.asprecon.VendorMasterApiEntity;
import com.ey.advisory.gstr2.userdetails.GstinDto;

public interface VendorMasterApiService {

	List<GstinDto> getListOfApiVendorPans(Long entityId);

	List<GstinDto> getListOfApiVendorGstin(List<String> vendorPans,
			Long entityId);
	
	List<VendorMasterApiEntity> extractAndPopulateVendorProcessedRecords(
			Long entityId, List<String> vendorGstinsList,
			List<String> vendorPans);
	
	public List<String> getOnlyGstInList(
			List<VendorMasterApiEntity> uploadEntities);
}
