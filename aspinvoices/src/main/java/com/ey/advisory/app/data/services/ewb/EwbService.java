/**
 * 
 */
package com.ey.advisory.app.data.services.ewb;

import java.util.List;

import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.ewb.dto.AddMultiVehicleDetailsDtoReq;
import com.ey.advisory.ewb.dto.AddMultiVehicleDetailsRespDto;
import com.ey.advisory.ewb.dto.CancelEwbReqDto;
import com.ey.advisory.ewb.dto.CancelEwbResponseDto;
import com.ey.advisory.ewb.dto.ChangeMultiVehicleDetailsRespDto;
import com.ey.advisory.ewb.dto.ChangeMultiVehicleRequestDto;
import com.ey.advisory.ewb.dto.ConsolidateEWBReqDto;
import com.ey.advisory.ewb.dto.ConsolidateEWBResponseDto;
import com.ey.advisory.ewb.dto.EwbMultiVehicleDetailsRespDto;
import com.ey.advisory.ewb.dto.EwbMultiVehicleGetFromAndToDto;
import com.ey.advisory.ewb.dto.EwbMultiVehicleGetVehicleRespDto;
import com.ey.advisory.ewb.dto.EwbMultiVehicleListGroupResp;
import com.ey.advisory.ewb.dto.EwbMultiVehicleListVehicleResp;
import com.ey.advisory.ewb.dto.EwbResponseDto;
import com.ey.advisory.ewb.dto.ExtendEWBReqDto;
import com.ey.advisory.ewb.dto.ExtendEWBResponseDto;
import com.ey.advisory.ewb.dto.GetEwayBillGeneratedByConsignerDto;
import com.ey.advisory.ewb.dto.GetEwbByDateResponseDto;
import com.ey.advisory.ewb.dto.GetEwbResponseDto;
import com.ey.advisory.ewb.dto.GetGSTINResponseDto;
import com.ey.advisory.ewb.dto.GetHsnDetailsResponseDto;
import com.ey.advisory.ewb.dto.InitiateMultiVehicleRequestDto;
import com.ey.advisory.ewb.dto.InitiateMultiVehicleRespDto;
import com.ey.advisory.ewb.dto.RejectEwbReqDto;
import com.ey.advisory.ewb.dto.RejectEwbResponseDto;
import com.ey.advisory.ewb.dto.UpdateEWBTransporterReqDto;
import com.ey.advisory.ewb.dto.UpdateEWBTransporterRespDto;
import com.ey.advisory.ewb.dto.UpdatePartBEwbRequestDto;
import com.ey.advisory.ewb.dto.UpdatePartBEwbResponseDto;

/**
 * @author Khalid1.Khan
 *
 */
public interface EwbService {

	EwbResponseDto generateEwb(OutwardTransDocument req, boolean updateDb,
			boolean erpPush);

	UpdatePartBEwbResponseDto updateEwbPartB(UpdatePartBEwbRequestDto req,
			String gstin, boolean updateDb, boolean erpPush);

	CancelEwbResponseDto cancelEwb(CancelEwbReqDto req, boolean updateDb,
			boolean erpPush);

	ConsolidateEWBResponseDto consolidateEWB(ConsolidateEWBReqDto req,
			String gstin, boolean updateDb, boolean erpPush);

	ExtendEWBResponseDto extendEWB(ExtendEWBReqDto req, String gstin,
			boolean updateDb, boolean erpPush);

	GetEwbResponseDto getEWB(String ewbNo, String gstin, boolean updateDb,
			boolean erpPush);

	UpdateEWBTransporterRespDto updateTransporter(
			UpdateEWBTransporterReqDto dto, String gstin, boolean updateDb,
			boolean erpPush);

	InitiateMultiVehicleRespDto initiateMultiVehicles(
			InitiateMultiVehicleRequestDto dto, String gstin);

	AddMultiVehicleDetailsRespDto addMultiVehicles(
			AddMultiVehicleDetailsDtoReq dto, String gstin);

	ChangeMultiVehicleDetailsRespDto changeMultiVehicles(
			ChangeMultiVehicleRequestDto dto, String gstin);

	List<EwbMultiVehicleListGroupResp> getGroupNumber(Long eWbno);

	List<EwbMultiVehicleListVehicleResp> listVehicleNumber(Long eWbno,
			String groupNo);

	RejectEwbResponseDto rejectEwb(RejectEwbReqDto req, boolean updateDb,
			boolean erpPush);

	GetHsnDetailsResponseDto getHsnDetails(String hsnCode, String gstin,
			boolean updateDb, boolean erpPush);

	List<GetEwbByDateResponseDto> getEWBByDate(String ewbNo, String gstin,
			boolean updateDb, boolean erpPush);

	GetGSTINResponseDto getGSTINDetails(String gstin, String getGstin);

	GetEwayBillGeneratedByConsignerDto getEwayBillGeneratedByConsigner(
			String docType, String docNo, String gstin);

	List<EwbMultiVehicleDetailsRespDto> getMultiVehicleDetails(Long eWbno,
			String docNo, String supplierGstin);

	EwbMultiVehicleGetVehicleRespDto getVehiclesforGroup(Long eWbno,
			String groupNo);

	EwbMultiVehicleGetFromAndToDto getEwbFromAndToData(Long ewbNo);

}