/**
 * 
 */
package com.ey.advisory.app.services.ewb;

import com.ey.advisory.app.data.business.dto.OutwardTransDocument;
import com.ey.advisory.ewb.dto.AddMultiVehicleDetailsReqDto;
import com.ey.advisory.ewb.dto.AddMultiVehicleDetailsRespDto;
import com.ey.advisory.ewb.dto.CancelEwbRequestListDto;
import com.ey.advisory.ewb.dto.CancelEwbResponseDto;
import com.ey.advisory.ewb.dto.ChangeMultiVehicleDetaiilsReqDto;
import com.ey.advisory.ewb.dto.ChangeMultiVehicleDetailsRespDto;
import com.ey.advisory.ewb.dto.ConsolidateEWBReqDto;
import com.ey.advisory.ewb.dto.ConsolidateEWBResponseDto;
import com.ey.advisory.ewb.dto.EwbResponseDto;
import com.ey.advisory.ewb.dto.ExtendEWBReqDto;
import com.ey.advisory.ewb.dto.ExtendEWBResponseDto;
import com.ey.advisory.ewb.dto.GetEwbResponseDto;
import com.ey.advisory.ewb.dto.InitiateMultiVehicleReqDto;
import com.ey.advisory.ewb.dto.InitiateMultiVehicleRespDto;
import com.ey.advisory.ewb.dto.RejectEwbRequestparamDto;
import com.ey.advisory.ewb.dto.RejectEwbResponseDto;
import com.ey.advisory.ewb.dto.UpdateEWBTransporterReqDto;
import com.ey.advisory.ewb.dto.UpdateEWBTransporterRespDto;
import com.ey.advisory.ewb.dto.UpdatePartBEwbRequestDto;
import com.ey.advisory.ewb.dto.UpdatePartBEwbResponseDto;

/**
 * @author Khalid1.Khan
 *
 */
public interface EwbBusinessService {

	EwbResponseDto generateEwb(OutwardTransDocument req);

	UpdatePartBEwbResponseDto updateEwbPartB(UpdatePartBEwbRequestDto req,
			String gstin);

	CancelEwbResponseDto cancelEwb(CancelEwbRequestListDto reqList);

	RejectEwbResponseDto rejectEwb(RejectEwbRequestparamDto rejectreq);

	ConsolidateEWBResponseDto consolidateEWB(ConsolidateEWBReqDto req,
			String gstin);

	ExtendEWBResponseDto extendEWB(ExtendEWBReqDto req, String gstin);

	GetEwbResponseDto getEWB(String ewbNo, String gstin);

	UpdateEWBTransporterRespDto updateTransporter(
			UpdateEWBTransporterReqDto dto, String gstin);

	AddMultiVehicleDetailsRespDto addMultiVehicles(
			AddMultiVehicleDetailsReqDto dto, String gstin);

	InitiateMultiVehicleRespDto initiateMultiVehicles(
			InitiateMultiVehicleReqDto dto, String gstin);

	ChangeMultiVehicleDetailsRespDto changeMultiVehicles(
			ChangeMultiVehicleDetaiilsReqDto dto, String gstin);

	GetEwbResponseDto getEwbByConsigner(String gstin, String docType,
			String docNo);

}