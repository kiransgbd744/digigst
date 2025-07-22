/**
 * 
 */
package com.ey.advisory.app.gstr3b;

import java.util.List;

import com.ey.advisory.common.AppException;
import com.ey.advisory.core.api.APIResponse;

/**
 * @author Khalid1.Khan
 *
 */
public interface Gstr3BGstinDashboardService {

	Gstr3bDashboardDto getgstr3bgstnDashBoardList(String taxPeriod,
			String gstin, Long entityId) throws AppException;

	List<Gstr3BInterStateSuppliesDto> getInterStateSupplies(String taxPeriod,
			String gstin);
	
	List<Gstr3BGstinAspUserInputDto> getPastLiabDtls(String taxPeriod,
			String gstin);

	List<Gstr3BExcemptNilNonGstnDto> getExcemptNilNonGstIS(String taxPeriod,
			String gstin);
	
	/**
	 * This method is calling GSTN API.
	 * 
	 * @param taxPeriod
	 * @param gstin
	 * @return APIResponse
	 */
	public APIResponse getGSTR3BSummary(String taxPeriod, String gstin);
	
	
	/**
	 * This method is taking APIResponse as request and 
	 * returning List of dto.
	 * 
	 * @param apiResponse
	 * @return  List<Gstr3BGstinsDto>
	 */
	public List<Gstr3BGstinsDto> getGstrDtoList(APIResponse
			apiResponse);

	List<Gstr3bTaxPaymentDto> getTaxPayemntList(APIResponse apiResponse);

	List<Gstr3BGstinAspUserInputDto> getUserInterStateSupplies(
			String taxPeriod, String gstin);

}
