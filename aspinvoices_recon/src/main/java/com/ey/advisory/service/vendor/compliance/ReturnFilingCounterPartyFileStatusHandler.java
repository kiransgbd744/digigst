/**
 * 
 */
package com.ey.advisory.service.vendor.compliance;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.ey.advisory.admin.data.repositories.client.MasterVendorRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.VendorMasterUploadEntityRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GstnReturnFilingStatus;
import com.ey.advisory.core.dto.ReturnFilingGstnResponseDto;
import com.ey.advisory.gstnapi.PublicApiConstants;
import com.ey.advisory.gstnapi.PublicApiContext;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Hemasundar.J
 *
 */
@Slf4j
@Service("ReturnFilingCounterPartyFileStatusHandler")
public class ReturnFilingCounterPartyFileStatusHandler {

	@Autowired
	@Qualifier("masterVendorRepository")
	private MasterVendorRepository vendorMaster;

	@Autowired
	private GstnReturnFilingStatus gstnReturnFiling;

	@Autowired
	private VendorMasterUploadEntityRepository vendorMasterUploadEntityRepository;

	@Transactional(value = "clientTransactionManager")
	public void loadAndPersistFillingStatus() {

		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"inside the method of callGstnApi of class GstnReturnFilingStatus");
			}
			List<String> customerGstins = vendorMasterUploadEntityRepository
					.findVendorGstin();
			if (CollectionUtils.isEmpty(customerGstins)) {
				String errMsg = "Vendor GSTIN's are not available,"
						+ " Hence skipping the Return Filling Status call";
				LOGGER.error(errMsg);
				throw new AppException(errMsg);
			}
			String financialYear = GenUtil.getCurrentFinancialYear();

			if (LOGGER.isDebugEnabled())
				LOGGER.debug(
						"About to get the Counter party Return Filling status for "
								+ "FY {} and customer_GSTINs {}",
						financialYear, customerGstins);
			PublicApiContext.setContextMap(PublicApiConstants.SOURCE,
					PublicApiConstants.RET_CNTR_PARTY_JOB);

			List<ReturnFilingGstnResponseDto> retFilingList = gstnReturnFiling
					.callGstnApi(customerGstins, financialYear, true);
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(
						"About to persist Counter party Return Filling status "
								+ "sanbox response data into table");
			gstnReturnFiling.persistReturnFillingStatus(retFilingList, true);

			if (LOGGER.isDebugEnabled())
				LOGGER.debug(
						"Counter party Return Filling status data persisted Success.");
		} catch (Exception e) {
			LOGGER.error(
					"Exception while Fetching Vendor Return Filling Status", e);
			throw new AppException(e);
		}

	}

}
