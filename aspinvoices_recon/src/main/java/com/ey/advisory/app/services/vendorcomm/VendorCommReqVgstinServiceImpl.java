package com.ey.advisory.app.services.vendorcomm;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.asprecon.VendorReqVendorGstinEntity;
import com.ey.advisory.app.data.repositories.client.asprecon.VendorReqVendorGstinRepository;
import com.ey.advisory.common.AppException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("VendorCommReqVgstinServiceImpl")
public class VendorCommReqVgstinServiceImpl
		implements VendorCommReqVgstinService {

	@Autowired
	@Qualifier("VendorReqVendorGstinRepository")
	private VendorReqVendorGstinRepository vendorReqVendorGstinRepository;

	@Override
	public List<VendorReqVendorGstinEntity> getVendorCommReqVgstinData(
			Long requestId) {
		List<VendorReqVendorGstinEntity> vendorReqVgstinList = null;
		try {
			vendorReqVgstinList = vendorReqVendorGstinRepository
					.findByRequestId(requestId);
		} catch (Exception ee) {
			LOGGER.error("Exception while fetching the Vendor Report Data", ee);
			throw new AppException(ee);

		}

		return vendorReqVgstinList;
	}

}
