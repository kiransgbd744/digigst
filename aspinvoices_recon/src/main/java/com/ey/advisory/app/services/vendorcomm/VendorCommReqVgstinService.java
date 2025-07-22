package com.ey.advisory.app.services.vendorcomm;

import java.util.List;

import com.ey.advisory.app.data.entities.client.asprecon.VendorReqVendorGstinEntity;

public interface VendorCommReqVgstinService {

	List<VendorReqVendorGstinEntity> getVendorCommReqVgstinData(Long requestId);
}
