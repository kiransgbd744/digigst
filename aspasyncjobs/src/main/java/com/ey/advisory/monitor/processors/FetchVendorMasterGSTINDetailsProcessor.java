package com.ey.advisory.monitor.processors;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.client.asprecon.VendorMasterConfigEntityRepository;
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

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Ravindra V S
 *
 */
@Component("FetchVendorMasterGSTINDetailsProcessor")
@Slf4j
public class FetchVendorMasterGSTINDetailsProcessor
		extends DefaultMultiTenantTaskProcessor {

	@Autowired
	@Qualifier("VendorMasterConfigEntityRepository")
	private VendorMasterConfigEntityRepository vendorMasterConfigRepo;

	@Autowired
	@Qualifier("taxPayerDetailsServiceImpl")
	TaxPayerDetailsService taxPayerDetailsService;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	private static final DateTimeFormatter formatter = DateTimeFormatter
			.ofPattern("dd/MM/yyyy");

	public void executeForGroup(Group group, Message message,
			AppExecContext ctx) {
		try {
			String groupCode = group.getGroupCode();

			if (LOGGER.isDebugEnabled()) {
				String logMsg = String
						.format("Executing FetchVendorMasterGSTINDetailsProcessor"
								+ " job" + ".executeForGroup()"
								+ " method for group: '%s'", groupCode);
				LOGGER.debug(logMsg);
			}

			if (LOGGER.isDebugEnabled()) {
				String logMsg = String.format(
						"About to monitor FetchVendorMasterGSTINDetailsProcessor for group '%s' ",
						groupCode);
				LOGGER.debug(logMsg);
			}

			LOGGER.debug(
					"Calling stored proc BEGIN for Vendor Master GSTIN DetailsProcessor ");

			List<String> procList = Arrays.asList(
					"USP_VENDOR_NAME_DATA_LOAD_GET2A",
					"USP_VENDOR_NAME_DATA_LOAD_GET6A",
					"USP_VENDOR_NAME_DATA_LOAD_GET2B",
					"USP_VENDOR_NAME_DATA_LOAD_OW_DOC_HDR",
					"USP_VENDOR_NAME_DATA_LOAD_GSTIN_INFO");

			for (String proc : procList) {
				try {
					StoredProcedureQuery storedProc = entityManager
							.createStoredProcedureQuery(proc);

					String result = (String) storedProc.getSingleResult();

					if (result.equalsIgnoreCase("SUCCESS")) {
						LOGGER.debug(" Stored Proc data loaded successfully");
					} else {
						LOGGER.debug(" Stored Proc is failed");
					}
				} catch (Exception ex) {

					String msg = String.format("Error while executing proc %s",
							proc);
					LOGGER.error(msg, ex);
				}
			}
			LOGGER.debug(
					"Calling stored proc ENDED for Vendor Master GSTIN DetailsProcessor ");

			// get Distinct Vendor Master GSTIN.
			List<String> getDistinctVendorGSTIN = vendorMasterConfigRepo
					.getDistinctVendorGSTINByIsFetched();

			if (LOGGER.isDebugEnabled()) {
				String logMsg = String.format("Fetched getDistinctVendorGSTIN",
						getDistinctVendorGSTIN.size());
				LOGGER.debug(logMsg);
			}
			if (getDistinctVendorGSTIN != null
					&& !getDistinctVendorGSTIN.isEmpty()) {

				getDistinctVendorGSTIN.forEach(distinctVendorGstn -> {

					if (distinctVendorGstn != null) {
						String gstin = distinctVendorGstn;

						if (LOGGER.isDebugEnabled()) {
							String logMsg = String
									.format("Before  get TaxPayerDetails for "
											+ "Gstin: '%s',", gstin);
							LOGGER.debug(logMsg);
						}
						persistVendorGstinDetails(gstin, groupCode);

					}
				});

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

			// }
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

		PublicApiContext.setContextMap(PublicApiConstants.SOURCE,
				PublicApiConstants.FETCH_VMASTER_DTL_JOB);
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

			String vendGstin = taxPayerDetailsDto.getGstin();
			String gstinStatus = "Failed";
			LocalDateTime updatedOn = LocalDateTime.now();
			Boolean isFetched = true;
			if ("ER-1000".equals(errorCode) || "GEN5008".equals(errorCode) || "INVJ01".equals(errorCode)) {
				gstinStatus = null;
				isFetched = false;
			}
			vendorMasterConfigRepo.updateErrCodeErrDes(vendGstin, gstinStatus,
					updatedOn, errorCode, errorDescription, isFetched);

		} else {
			String vendGstin = taxPayerDetailsDto.getGstin();
			String legalName = taxPayerDetailsDto.getLegalBussNam();
			String gstinStatus = taxPayerDetailsDto.getGstnStatus();
			String tradeName = taxPayerDetailsDto.getTradeName();
			String taxpayerType = taxPayerDetailsDto.getTaxPayType();
			LocalDateTime updatedOn = LocalDateTime.now();
			Boolean isNameUpdated = true;
			LocalDateTime nameUpdatedOn = LocalDateTime.now();
			Boolean isFetched = true;

			LocalDate regDate = null;
			LocalDate canDate = null;
			try {

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Recieved reg date {} and can date {} for gstin {}",
							taxPayerDetailsDto.getDateOfReg(),
							taxPayerDetailsDto.getDateOfCan(), vendGstin);
				}
				regDate = LocalDate.parse(taxPayerDetailsDto.getDateOfReg(),
						formatter);
				
				if (StringUtils
						.isNotEmpty(taxPayerDetailsDto.getDateOfCan())) {
					canDate = LocalDate.parse(taxPayerDetailsDto.getDateOfCan(),
							formatter);
				}
			} catch (Exception e) {
				LOGGER.error("Error while parsing date {}", e);
			}
			// Update
			vendorMasterConfigRepo.updateLegalTradeName(vendGstin, legalName,
					gstinStatus, tradeName, taxpayerType, updatedOn,
					isNameUpdated, nameUpdatedOn, isFetched, regDate, canDate);
		}

		if (LOGGER.isDebugEnabled()) {
			String logMsg = String
					.format("Get TaxPayerDetails for " + "Gstin: '%s'", gstin);
			LOGGER.debug(logMsg);
		}

	}

}
