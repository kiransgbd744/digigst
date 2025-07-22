package com.ey.advisory.app.services.vendorcomm;

import java.util.List;

import com.ey.advisory.app.data.entities.client.asprecon.VendorCommRequestEntity;

public interface VendorCommService {

	List<VendorCommRequestEntity> getVendorCommDataByUserName(String userName,
			String reconType);
}
