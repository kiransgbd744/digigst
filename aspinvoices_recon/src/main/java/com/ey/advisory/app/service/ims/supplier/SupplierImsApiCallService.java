package com.ey.advisory.app.service.ims.supplier;

import java.util.List;

import com.ey.advisory.app.data.entities.client.GetAnx1BatchEntity;

public interface SupplierImsApiCallService {
	
	public List<SupplierImsGstinDetailsDto> getTaxPeriodDetailsIms(
			List<GetAnx1BatchEntity> getBatchEntityDetails);

}
