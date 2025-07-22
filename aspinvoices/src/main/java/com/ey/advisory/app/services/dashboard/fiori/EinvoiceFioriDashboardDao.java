package com.ey.advisory.app.services.dashboard.fiori;

import java.time.LocalDate;
import java.util.List;

import com.ey.advisory.common.AppException;

/**
 * @author Hema G M
 *
 */
public interface EinvoiceFioriDashboardDao {

	List<String> getSupGstinList(List<String> supplierGstins)
			throws AppException;

	List<Object[]> getHeaderDataDetails(List<String> supplierGstins,
			LocalDate fromSummDate, LocalDate toSummDate);

	List<Object[]> getAvgIrnGen(List<String> supplierGstins,
			LocalDate fromSummDate, LocalDate toSummDate);

	List<Object[]> getEinvoiceSumm(List<String> supplierGstins,
			LocalDate fromSummDate, LocalDate toSummDate, Long entityId);

	List<Object[]> getEinvDistribution(List<String> supplierGstins,
			LocalDate fromSummDate, LocalDate toSummDate);

	List<Object[]> getErrorDetails(List<String> supplierGstins,
			LocalDate fromSummDate, LocalDate toSummDate);

	List<Object[]> getEinvGenTredForGenAndTotal(List<String> supplierGstins,
			LocalDate fromSummDate, LocalDate toSummDate);

	List<Object[]> getEinvGenTredForCanDupAndErr(List<String> supplierGstins,
			LocalDate fromSummDate, LocalDate toSummDate);

	List<Object[]> getEinvStatusTable(List<String> supplierGstins,
			LocalDate fromSummDate, LocalDate toSummDate);
}
