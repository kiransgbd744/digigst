/**
 * 
 */
package com.ey.advisory.app.data.services.ewb;

import java.util.List;

import org.javatuples.Pair;

import com.ey.advisory.ewb.dto.CancelEwbReqDto;
import com.ey.advisory.ewb.dto.CancelEwbRequestListDto;
import com.ey.advisory.ewb.dto.ConsolidateEWBReqDto;
import com.ey.advisory.ewb.dto.ExtendEWBReqDto;
import com.ey.advisory.ewb.dto.GetEWBReqDto;
import com.ey.advisory.ewb.dto.GetPartBDetailsDto;
import com.ey.advisory.ewb.dto.UpdateEWBTransporterReqDto;
import com.ey.advisory.ewb.dto.UpdatePartBEwbRequestDto;

/**
 * @author Khalid1.Khan
 *
 */
public interface EwbServiceUi {


	String updatePartBEwb(List<UpdatePartBEwbRequestDto> req);

	String updateTransporter(List<UpdateEWBTransporterReqDto> req);

	String extendEwb(List<ExtendEWBReqDto> req);

	String consolidateEwb(List<ConsolidateEWBReqDto> req);

	String getEwb(List<GetEWBReqDto> req);

	List<GetPartBDetailsDto> getPartBDetailsByEwbNo(List<String> ewbNos);

	// New Method

	public String generateEwbSync(Long id);

	public String generateEwbAsync(List<Long> docIds);

	public Pair<String, String> cancelEwbSync(CancelEwbReqDto cancelEwb);

	public String cancelEwbAsync(CancelEwbRequestListDto cancelEwb);

	public String updatePartEwbASync(List<UpdatePartBEwbRequestDto> reqList);

	public Pair<String, String> updatePartEwbSync(
			UpdatePartBEwbRequestDto updatePartEwb);


	public String extendEwbAsync(List<ExtendEWBReqDto> reqList);

	public Pair<String, String> extendEwbSync(ExtendEWBReqDto extendEwbReq);

	public Pair<String, String> updateTransporterSync(
			UpdateEWBTransporterReqDto updateTransporterReq);

	public String updateTransporterASync(
			List<UpdateEWBTransporterReqDto> reqList);
	
	public Pair<String, String> consolidateEwbsync(
			ConsolidateEWBReqDto consolidateEwbReq);
	
	public String consolidateEwbAsync(List<ConsolidateEWBReqDto> reqList);
	
	public String rejectEwbSync(Long id);


}
