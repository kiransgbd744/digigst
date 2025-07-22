package com.ey.advisory.app.data.daos.ewb;

import java.util.List;

import com.ey.advisory.ewb.app.api.APIResponse;
import com.ey.advisory.ewb.dto.AddMultiVehicleDetailsDtoReq;
import com.ey.advisory.ewb.dto.ChangeMultiVehicleRequestDto;
import com.ey.advisory.ewb.dto.EwbMultiVehicleDetailsRespDto;
import com.ey.advisory.ewb.dto.EwbMultiVehicleGetVehicleRespDto;
import com.ey.advisory.ewb.dto.EwbMultiVehicleListGroupResp;
import com.ey.advisory.ewb.dto.EwbMultiVehicleListVehicleResp;
import com.ey.advisory.ewb.dto.InitiateMultiVehicleRequestDto;

public interface EwbMultiVehicleDao {

	public void saveInitiateMultivehicleResp(String jsonResp,
			InitiateMultiVehicleRequestDto req);

	public APIResponse initiateMultiVehicle(String reqBody, String gstin);

	public String saveAddMultivehicleRes(String jsonResp,
			AddMultiVehicleDetailsDtoReq req);

	public APIResponse addMultiVehicle(AddMultiVehicleDetailsDtoReq req,
			String gstin);

	public List<EwbMultiVehicleListGroupResp> getListOfGroup(Long eWbno);

	public List<EwbMultiVehicleListVehicleResp> getListOfVehicleNumber(
			Long eWbno, String groupNo);

	public APIResponse changeMultiVehicle(ChangeMultiVehicleRequestDto req,
			String gstin);

	public String changeMultiVehicle(String jsonResp,
			ChangeMultiVehicleRequestDto req);

	public List<EwbMultiVehicleDetailsRespDto> getMultiVehicleDetails(
			Long eWbno, String docNo, String supplierGstin);

	public EwbMultiVehicleGetVehicleRespDto getVehiclesforGroup(Long ewbNo,
			String groupNo);

}
