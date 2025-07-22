package com.ey.advisory.app.services.vendorcomm;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.ey.advisory.app.data.entities.client.asprecon.VendorCommRequestEntity;
import com.ey.advisory.app.data.entities.client.asprecon.VendorReqRecipientGstinEntity;
import com.ey.advisory.app.data.entities.client.asprecon.VendorReqVendorGstinEntity;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2Link2APRrepository;
import com.ey.advisory.app.data.repositories.client.asprecon.VendorCommRequestRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.VendorReqRecipientGstinRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.VendorReqVendorGstinRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.ReportStatusConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.google.gson.JsonObject;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("VendorCommunicationServiceImpl")
public class VendorCommunicationServiceImpl
		implements VendorCommunicationService {

	@Autowired
	@Qualifier("VendorCommRequestRepository")
	private VendorCommRequestRepository vendorCommRequestRepository;

	@Autowired
	@Qualifier("VendorReqRecipientGstinRepository")
	private VendorReqRecipientGstinRepository vendorReqRecipientGstinRepository;

	@Autowired
	@Qualifier("VendorReqVendorGstinRepository")
	private VendorReqVendorGstinRepository vendorReqVendorGstinRepository;

	@Autowired
	Gstr2Link2APRrepository gstr2Link2APRrepository;

	@Autowired
	private AsyncJobsService asyncJobsService;

	@Autowired
	@Qualifier("VendorCommDAOImpl")
	private VendorCommDAO vendorCommDAO;

	private static final String B2PR = "2B_PR";

	@Override
	public Long createEntryVendorCommRequest(Long noOfRecipientsGstin,
			Long noOfVendorGstins, Long noOfReportTypes, String reportTypes,
			String fromTaxPeriod, String toTaxPeriod, String reconType) {
		String status = "SUBMITTED";
		String userName = (SecurityContext.getUser() != null
				&& SecurityContext.getUser().getUserPrincipalName() != null)
						? SecurityContext.getUser().getUserPrincipalName()
						: "SYSTEM";
		VendorCommRequestEntity vendorCommRequestEntity = new VendorCommRequestEntity();
		if (noOfVendorGstins == 0) {
			status = ReportStatusConstants.NO_DATA_FOUND;
		}

		try {
			vendorCommRequestEntity.setNoOfRecipientGstins(noOfRecipientsGstin);
			vendorCommRequestEntity.setNoOfVendorGstins(noOfVendorGstins);
			vendorCommRequestEntity.setNoOfReportTypes(noOfReportTypes);
			vendorCommRequestEntity.setReportTypes(reportTypes);
			vendorCommRequestEntity.setFromTaxPeriod(fromTaxPeriod);
			vendorCommRequestEntity.setToTaxPeriod(toTaxPeriod);
			vendorCommRequestEntity.setStatus(status);
			vendorCommRequestEntity.setReconType(reconType);
			vendorCommRequestEntity.setCreatedOn(LocalDateTime.now());
			vendorCommRequestEntity.setCreatedBy(userName);
			vendorCommRequestEntity.setUpdatedOn(LocalDateTime.now());
			vendorCommRequestEntity.setUpdatedBy(userName);
			vendorCommRequestRepository.save(vendorCommRequestEntity);
			return vendorCommRequestEntity.getRequestId();
		} catch (Exception e) {
			LOGGER.error("Exception while Persisting Vendor Comm Request ", e);
			throw new AppException(e);
		}
	}

	@Override
	public void createEntryVendorReqRecipientGstin(Long requestId,
			String recipientGstin) {
		VendorReqRecipientGstinEntity vendorReqRecipientGstinEntity = new VendorReqRecipientGstinEntity();
		try {
			vendorReqRecipientGstinEntity.setRequestId(requestId);
			vendorReqRecipientGstinEntity.setRecipientGstin(recipientGstin);
			vendorReqRecipientGstinRepository
					.save(vendorReqRecipientGstinEntity);
		}

		catch (Exception e) {
			LOGGER.error("Exception while Persisting VendorReqRecipientGstin ",
					e);
			throw new AppException(e);

		}

	}

	@Override
	public void createEntryVendorReqVendorGstin(Long requestId,
			String vendorGstin) {
		String userName = (SecurityContext.getUser() != null
				&& SecurityContext.getUser().getUserPrincipalName() != null)
						? SecurityContext.getUser().getUserPrincipalName()
						: "SYSTEM";
		VendorReqVendorGstinEntity vendorReqVendorGstinEntity = new VendorReqVendorGstinEntity();
		try {
			vendorReqVendorGstinEntity.setRequestId(requestId);
			vendorReqVendorGstinEntity.setVendorGstin(vendorGstin);
			vendorReqVendorGstinEntity.setCreatedOn(LocalDateTime.now());
			vendorReqVendorGstinEntity.setCreatedBy(userName);
			vendorReqVendorGstinEntity.setUpdatedOn(LocalDateTime.now());
			vendorReqVendorGstinEntity.setUpdatedBy(userName);
			vendorReqVendorGstinEntity.setEmailStatus("DRAFTED");
			vendorReqVendorGstinEntity.setReportStatus("SUBMITTED");
			vendorReqVendorGstinRepository.save(vendorReqVendorGstinEntity);
		} catch (Exception e) {
			LOGGER.error("Exception while Persisting VendorReqVendorGstin ", e);
			throw new AppException(e);

		}

	}

	private List<String> convertToValidCombinations(
			List<Object[]> distinctCombination) {

		List<String> validComb = new ArrayList<>();

		distinctCombination.forEach(o -> {
			String vtin = (String) o[0];
			String rtin = (String) o[1];
			validComb.add(String.format("%s-%s", vtin, rtin));
		});

		return validComb;
	}

	private Pair<List<String>, List<String>> findValidCombForRecipientGstins(
			List<Object[]> distinctCombination,
			List<String> recipientGstinsList) {
		List<String> validRtins = new ArrayList<>();
		List<String> validVtins = new ArrayList<>();

		distinctCombination.forEach(o -> {
			String vtin = (String) o[0];
			String rtin = (String) o[1];
			if (recipientGstinsList.contains(rtin)) {
				validRtins.add(rtin);
				validVtins.add(vtin);
			}
		});

		return new Pair<>(validRtins, validVtins);
	}

	@Override
	public Pair<List<String>, List<String>> getDistinctCombination(
			Integer derivedFromTaxPeriod, Integer derivedToTaxPeriod,
			List<String> reportTypeList, List<String> recipientGstinsList,
			List<String> vendorGstinsList, boolean isApOpted,
			String reconType) {
		List<String> validRtins = new ArrayList<>();
		List<String> validVtins = new ArrayList<>();
		Pair<List<String>, List<String>> validGstins = null;
		List<Object[]> distinctCombination = null;
		try {

			if (B2PR.equalsIgnoreCase(reconType)) {
				distinctCombination = vendorCommDAO.getDistinctCombFromLink2BPR(
						derivedFromTaxPeriod, derivedToTaxPeriod,
						reportTypeList);

			} else {
				distinctCombination = vendorCommDAO.getDistinctCombFromLink2APR(
						derivedFromTaxPeriod, derivedToTaxPeriod,
						reportTypeList, isApOpted);
			}
			if (CollectionUtils.isEmpty(vendorGstinsList)) {

				validGstins = findValidCombForRecipientGstins(
						distinctCombination, recipientGstinsList);

			} else {
				List<String> validCombList = convertToValidCombinations(
						distinctCombination);

				recipientGstinsList.forEach(rtin -> {
					vendorGstinsList.forEach(vtin -> {
						String tinsCombination = vtin + "-" + rtin;
						if (validCombList.contains(tinsCombination)) {
							validRtins.add(rtin);
							validVtins.add(vtin);

						}

					});
				});
				validGstins = new Pair<>(validRtins, validVtins);
			}
		} catch (Exception e) {
			LOGGER.error("error", e);
			throw new AppException(e);
		}
		return validGstins;

	}

	@Override
	public String generateVendorReportUploadAsync(Long requestId,
			boolean isAPOpted, String reconType) {

		JsonObject jsonParams = new JsonObject();

		jsonParams.addProperty("id", requestId);
	

		if (B2PR.equalsIgnoreCase(reconType)) {
			asyncJobsService.createJob(TenantContext.getTenantId(),
					JobConstants.VENDOR_COMM_REPORT_2BPR, jsonParams.toString(),
					"SYSTEM", 1L, null, null);

		} else {
			jsonParams.addProperty("isAPOpted", isAPOpted);
			asyncJobsService.createJob(TenantContext.getTenantId(),
					JobConstants.VENDOR_COMM_REPORT, jsonParams.toString(),
					"SYSTEM", 1L, null, null);

		}
		LOGGER.debug(
				"Vendor comm report upload Job has been submitted for requestId ",
				requestId);

		return "Request has been submitted";
	}
}
