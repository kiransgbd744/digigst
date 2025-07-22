package com.ey.advisory.app.services.daos.gstr6;

import java.util.List;

import org.javatuples.Pair;

import com.ey.advisory.app.data.entities.client.Gstr6ComputeCredDistDataDto;
import com.ey.advisory.app.docs.dto.gstr6.Gstr6CalculateTurnOverRequestDto;
import com.ey.advisory.app.docs.dto.gstr6.Gstr6ComputeGstr1SummaryRequestDto;
import com.ey.advisory.app.docs.dto.gstr6.Gstr6ComputeGstr1SummaryResponseDto;
import com.ey.advisory.app.docs.dto.gstr6.Gstr6ComputeTimeStampResponseDto;
import com.ey.advisory.core.dto.Gstr2InitiateReconReqDto;

/**
 * 
 * @author SriBhavya
 *
 */
public interface Gstr6CalculateTurnOverGstnService {

	/*
	 * String getGstr6CalTurnOverGstnData(Gstr6CalculateTurnOverRequestDto
	 * criteria, List<String> taxperoidList);
	 */

	public List<String> getListOfTaxperoids(Gstr6CalculateTurnOverRequestDto criteria);

	public List<Pair<String, String>> getListOfCombinationPairs(List<String> gstins, List<String> taxperoidList);

	public void getGstr6CalTurnOverGstnData(List<Pair<String, String>> listOfPairs, String groupCode,
			List<String> gstins, Gstr6CalculateTurnOverRequestDto criteria, List<String> isdGstins);

	public void getGstr6CalTurnOverDigiGstData(Gstr6CalculateTurnOverRequestDto criteria);

	//public void getGstr6ComputeCreditDistributionData(Gstr6CalculateTurnOverRequestDto criteria);

	public void getGstr6ComputeTurnOverUserInputData(Gstr6CalculateTurnOverRequestDto criteria);

	public Gstr6ComputeTimeStampResponseDto getGstr6ComputeTimeStamp(Gstr6CalculateTurnOverRequestDto criteria);

	public List<Gstr6ComputeGstr1SummaryResponseDto> getGstr1SummaryStatus(Gstr6ComputeGstr1SummaryRequestDto criteria);

	public List<String> getRegGstins(Long entityId);

	public void getGstr6CalTurnOverDigiGstProcessedData(Gstr6CalculateTurnOverRequestDto criteria, Long entityId);
	
	public String gstr6ComputeCredDistData(Gstr6CalculateTurnOverRequestDto criteria);

	public String getGstr6ComputeCreditDistributionData(
			Gstr6CalculateTurnOverRequestDto criteria, Long requestId);
	
	List<Gstr6ComputeCredDistDataDto> getGstr6reqIdWiseScreenData(
			Gstr2InitiateReconReqDto reqDto, String userName);
	
	public void updateGstnStatus(Gstr6CalculateTurnOverRequestDto dto, String staus);
	
	public String checkStatus(List<String> list);

}
