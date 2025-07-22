package com.ey.advisory.monitor.processors;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.asprecon.VendorGstinDetailsEntity;
import com.ey.advisory.app.data.repositories.client.asprecon.VendorGstinDetailsRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.VendorMasterUploadEntityRepository;
import com.ey.advisory.app.gstr.taxpayerdetail.TaxPayerDetailsDto;
import com.ey.advisory.app.gstr.taxpayerdetail.TaxPayerDetailsService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.ErrMsgEnhancementStrategy;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.multitenancy.DefaultMultiTenantTaskProcessor;
import com.ey.advisory.core.async.domain.master.Group;
import com.ey.advisory.gstnapi.PublicApiConstants;
import com.ey.advisory.gstnapi.PublicApiContext;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Ravindra
 *
 */
@Component("FetchVendorGSTINDetailsProcessor")
@Slf4j
public class FetchVendorGSTINDetailsProcessor
		extends DefaultMultiTenantTaskProcessor {

	@Autowired
	@Qualifier("VendorMasterUploadEntityRepository")
	VendorMasterUploadEntityRepository vendorMasterUploadRepo;

	@Autowired
	@Qualifier("taxPayerDetailsServiceImpl")
	TaxPayerDetailsService taxPayerDetailsService;

	@Autowired
	@Qualifier("VendorGstinDetailsRepository")
	VendorGstinDetailsRepository vendorGstinDetailsRepo;

	public void executeForGroup(Group group, Message message,
			AppExecContext ctx) {
		try {
			String groupCode = group.getGroupCode();

			if (LOGGER.isDebugEnabled()) {
				String logMsg = String
						.format("Executing FetchVendorGSTINDetailsProcessor"
								+ " job" + ".executeForGroup()"
								+ " method for group: '%s'", groupCode);
				LOGGER.debug(logMsg);
			}

			if (LOGGER.isDebugEnabled()) {
				String logMsg = String.format(
						"About to monitor FetchVendorGSTINDetailsProcessor for group '%s' ",
						groupCode);
				LOGGER.debug(logMsg);
			}

			List<String> distinctVGstins = vendorMasterUploadRepo
					.getDistinctVendorGSTINByIsFETCHED();

			List<String> updatedGstins = new ArrayList<>();
			if (LOGGER.isDebugEnabled()) {
				String logMsg = String.format("Fetched vendor gstins are %d",
						distinctVGstins.size());
				LOGGER.debug(logMsg);
			}
			if (!distinctVGstins.isEmpty()) {
				distinctVGstins.forEach(distinctVendorGstn -> {
					String gstin = distinctVendorGstn;

					if (LOGGER.isDebugEnabled()) {
						String logMsg = String
								.format("Before  get TaxPayerDetails for "
										+ "Gstin: '%s',", gstin);
						LOGGER.debug(logMsg);
					}
					try {
						persistVendorGstinDetails(gstin, groupCode);
						updatedGstins.add(gstin);
					} catch (Exception e) {

						String logMsg = String
								.format("Error While Persisting Vendor"
										+ "Gstin: '%s',", gstin);
						LOGGER.error(logMsg, e);
					}
				});
				if (!updatedGstins.isEmpty()) {
					vendorMasterUploadRepo
							.updateisFetchedAfterPersist(updatedGstins);
				}

			} else {
				if (LOGGER.isDebugEnabled()) {
					String logMsg = String.format("There are No eligible"
							+ " gstinInvoiceCombinations which are ready ");
					LOGGER.debug(logMsg);
				}
			}

			if (LOGGER.isDebugEnabled()) {
				String logMsg = String
						.format("Completed one cycle of periodic Computation job"
								+ " for group '%s'", group.getGroupCode());
				LOGGER.debug(logMsg);
			}

		} catch (Exception ex) {
			// Throw the App Exception here. If the exception obtained is
			// AppException, then propagate it. Otherwise, create a new
			// app exception. This particular constructor of the AppException
			// will extract the nested exception and attach the message to the
			// specified string. (This way the person who monitors the
			// EY_JOB_DETAILS database will come to know the root cause of the
			// exception).
			throw new AppException(ex,
					ErrMsgEnhancementStrategy.APPEND_FIRST_NON_APP_EXCEPTION);

		}

	}

	private void persistVendorGstinDetails(String gstin, String groupCode) {

		if (LOGGER.isDebugEnabled()) {
			String logMsg = String
					.format("About to TaxPayerDetails for Gstin: '%s'", gstin);
			LOGGER.debug(logMsg);
		}

		PublicApiContext.setContextMap(PublicApiConstants.SOURCE,
				PublicApiConstants.FETCH_VDETAILS_JOB);
		TaxPayerDetailsDto taxPayerDetailsDto = taxPayerDetailsService
				.getTaxPayerDetails(gstin, groupCode);

		if (Optional.ofNullable(taxPayerDetailsDto.getErrorCode()).isPresent()
				|| Optional.ofNullable(taxPayerDetailsDto.getErrorMsg())
						.isPresent()) {

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("taxPayerDetailsDto response : ErrorCode "
						+ taxPayerDetailsDto.getErrorCode() + "  ErrorMsg : "
						+ taxPayerDetailsDto.getErrorMsg());

			}
			String errorCode = taxPayerDetailsDto.getErrorCode();
			String errorDescription = taxPayerDetailsDto.getErrorMsg();

			VendorGstinDetailsEntity vendorGstin = vendorGstinDetailsRepo
					.findByGstin(gstin);

			if (vendorGstin == null) {
				vendorGstin = new VendorGstinDetailsEntity();
				vendorGstin.setGstin(gstin);
			}

			vendorGstin.setGstinStatus("Failed");
			vendorGstin.setUpdatedOn(LocalDateTime.now());
			vendorGstin.setCreatedOn(LocalDateTime.now());
			vendorGstin.setErrorCode(errorCode);
			vendorGstin.setErrorDescription(errorDescription);
			vendorGstinDetailsRepo.save(vendorGstin);
		} else {
			String gst = taxPayerDetailsDto.getGstin();
			String legalNameBusiness = taxPayerDetailsDto.getLegalBussNam();
			String taxpayerType = taxPayerDetailsDto.getTaxPayType();
			String gstinStatus = taxPayerDetailsDto.getGstnStatus();

			String tradeName = taxPayerDetailsDto.getTradeName();

			VendorGstinDetailsEntity vendorGstinDetailsEntity = new VendorGstinDetailsEntity(
					gst, legalNameBusiness, taxpayerType, gstinStatus, null,
					tradeName);

			vendorGstinDetailsEntity.setCreatedOn(LocalDateTime.now());
			vendorGstinDetailsEntity.setUpdatedOn(LocalDateTime.now());
			if (LOGGER.isDebugEnabled()) {
				String logMsg = String.format(
						"About to Persist TaxPayer for Gstin: '%s'", gstin);
				LOGGER.debug(logMsg);
			}
			vendorGstinDetailsRepo.save(vendorGstinDetailsEntity);
		}

	}

}
