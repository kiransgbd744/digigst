package com.ey.advisory.app.services.dashboard.fiori;

import java.time.LocalDate;
import java.util.List;

import com.ey.advisory.gstr2.userdetails.GstinDto;

/**
 * @author Hema G M
 *
 */
public interface EinvoiceFioriDashboardService {

	List<GstinDto> getAllSupplierGstins(List<Long> entityId);

	EinvHeaderDetailsDto getEinvHeaderDetails(List<String> supplierGstins,
			LocalDate fromSummDate, LocalDate toSummDate);

	EinvIrnDto getAvgIrnGenPerMonth(List<String> supplierGstins,
			LocalDate fromSummDate, LocalDate toSummDate);

	EInvoiceSummaryDto getEinvSummaryData(List<String> supplierGstins,
			LocalDate fromSummDate, LocalDate toSummDate, Long entityId);

	List<EInvoiceDistributionDto> getEinvDistributionData(
			List<String> supplierGstins, LocalDate fromSummDate,
			LocalDate toSummDate);

	List<EinvGenerationDto1> getEinvGenTredForGenAndTotal(
			List<String> supplierGstins, LocalDate fromSummDate,
			LocalDate toSummDate);

	List<EinvGenerationDto2> getEinvGenTredForCanDupAndErr(
			List<String> supplierGstins, LocalDate fromSummDate,
			LocalDate toSummDate);

	List<EinvoiceStatusDto> getEinvStatusTable(List<String> supplierGstins,
			LocalDate fromSummDate, LocalDate toSummDate);

	List<EinvErrorDetailsDto> getEinvErrorDetails(List<String> supplierGstins,
			LocalDate fromSummDate, LocalDate toSummDate);
}
