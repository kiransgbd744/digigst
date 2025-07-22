package com.ey.advisory.app.services.vendorcomm;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.asprecon.VendorCommRequestEntity;
import com.ey.advisory.app.data.repositories.client.asprecon.VendorCommRequestRepository;
import com.ey.advisory.common.AppException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("VendorCommServiceImpl")
public class VendorCommServiceImpl implements VendorCommService {

	@Autowired
	@Qualifier("VendorCommRequestRepository")
	private VendorCommRequestRepository vendorCommRequestRepository;

	@Override
	public List<VendorCommRequestEntity> getVendorCommDataByUserName(
			String userName, String reconType) {
		List<VendorCommRequestEntity> vendorReportList = null;
		try {
			vendorReportList = vendorCommRequestRepository
					.findByCreatedByAndReconType(userName, reconType);
			if (vendorReportList == null || vendorReportList.isEmpty()) {
				String errMsg = "No requests submitted";
				LOGGER.error(errMsg);
				throw new AppException(errMsg);
			}
		} catch (Exception ee) {
			LOGGER.error("Exception while fetching the Vendor Report Data", ee);
			throw new AppException(ee);

		}

		return vendorReportList;
	}

}
