package com.ey.advisory.app.services.vendorcomm;

import java.util.List;

import org.javatuples.Pair;

public interface VendorCommunicationService {

	Long createEntryVendorCommRequest(Long noOfRecipientsGstin,
			Long noOfVendorGstins, Long noOfReportTypes, String reportTypes,
			String fromTaxPeriod, String toTaxPeriod, String reconType);

	void createEntryVendorReqRecipientGstin(Long requestId,
			String recipientGstin);

	void createEntryVendorReqVendorGstin(Long requestId, String recipientGstin);

	public Pair<List<String>, List<String>> getDistinctCombination(
			Integer derivedFromTaxPeriod, Integer derivedToTaxPeriod,
			List<String> reportTypeList, List<String> recipientGstinsList,
			List<String> vendorGstinsList, boolean isApOpted, String reconType);

	String generateVendorReportUploadAsync(Long requestId, boolean isApOpted,
			String reconType);
}
