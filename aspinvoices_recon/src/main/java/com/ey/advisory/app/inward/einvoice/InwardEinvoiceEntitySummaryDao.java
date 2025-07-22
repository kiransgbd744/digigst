package com.ey.advisory.app.inward.einvoice;

import java.util.List;
import java.util.Map;

/**
 * 
 * @author vishal.verma
 *
 */

public interface InwardEinvoiceEntitySummaryDao {
	public List<InwardEinvoiceEntitySummaryResponseDto> findTableData(
			InwardEinvoiceEntitySummaryReqDto criteria,
			Map<String, String> regMap, Map<String, String> stateNames,
			Map<String, String> authTokenStatus);

	public List<InwardEinvoiceDetailedInfoResponseDto> findTableDetailedData(
			InwardEinvoiceEntitySummaryReqDto criteria,
			Map<String, String> regMap, Map<String, String> stateNames,
			Map<String, String> authTokenStatus, Map<String, String> emailMap);

	public List<InwardEinvoiceStatusScreenResponseDto> findStatusData(
			InwardEinvoiceEntitySummaryReqDto criteria,
			Map<String, String> regMap, Map<String, String> stateNames,
			Map<String, String> authTokenStatus);

}
