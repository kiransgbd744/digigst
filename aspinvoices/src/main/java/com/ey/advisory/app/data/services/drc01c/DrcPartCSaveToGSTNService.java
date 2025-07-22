package com.ey.advisory.app.data.services.drc01c;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.drc01c.TblDrc01cDetails;
import com.ey.advisory.app.data.entities.drc01c.TblDrc01cSaveStatus;
import com.ey.advisory.app.data.entities.drc01c.TblUserDrc01cReasonDetails;
import com.ey.advisory.app.data.repositories.client.drc01c.TblDrc01cChallanDetailsRepo;
import com.ey.advisory.app.data.repositories.client.drc01c.TblDrc01cDetailsRepo;
import com.ey.advisory.app.data.repositories.client.drc01c.TblDrc01cSaveStatusRepo;
import com.ey.advisory.app.data.repositories.client.drc01c.TblUserDrc01cReasonDetailsRepo;
import com.ey.advisory.app.data.services.drc.DrcSaveToGstnDto;
import com.ey.advisory.app.data.services.drc.ReasonDto;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIExecutor;
import com.ey.advisory.core.api.APIIdentifiers;
import com.ey.advisory.core.api.APIParam;
import com.ey.advisory.core.api.APIParams;
import com.ey.advisory.core.api.APIProviderEnum;
import com.ey.advisory.core.api.APIResponse;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */
@Slf4j
@Component("DrcPartCSaveToGSTNService")
public class DrcPartCSaveToGSTNService {

	@Autowired
	@Qualifier("TblDrc01cSaveStatusRepo")
	private TblDrc01cSaveStatusRepo saveStatusRepo;

	@Autowired
	@Qualifier("DefaultGSTNAPIExecutor")
	private APIExecutor apiExecutor;

	@Autowired
	@Qualifier("TblDrc01cDetailsRepo")
	private TblDrc01cDetailsRepo drcDetailsRepo;
	
	@Autowired
	@Qualifier("TblDrc01cChallanDetailsRepo")
	private TblDrc01cChallanDetailsRepo drcChalanDetailsRepo;
	
	@Autowired
	@Qualifier("TblUserDrc01cReasonDetailsRepo")
	private TblUserDrc01cReasonDetailsRepo userDrcReasonDetailsRepo;
	
	public String saveDRCToGstn(String gstin, String taxPeriod,
			String groupCode) {
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Inside DrcPartCSaveToGSTNService"
					+ ".saveDRCToGstn() method %s gstin, %s taxPeriod,"
					+ " %s groupCode {}", gstin, taxPeriod, groupCode);
		}

		Gson gson = GsonUtil.gsonInstanceWithExpose();
		try {

			TblDrc01cDetails drcDetialsEntity = drcDetailsRepo
					.findByGstinAndTaxPeriodAndIsActiveTrue(gstin, taxPeriod);
			
			String refId = drcDetialsEntity.getRefId();
			
			List<String> drcArnNoList = drcChalanDetailsRepo
					.findByRefIdAndIsActiveTrue(refId);
			
			 List<TblUserDrc01cReasonDetails> resonEntityList = 
					 userDrcReasonDetailsRepo
					 .findByGstinAndTaxPeriodAndIsActiveTrue(
					 gstin, taxPeriod);
					
			List<ReasonDto> resonList = new ArrayList<>();
			
			for (TblUserDrc01cReasonDetails resons : resonEntityList) {
				ReasonDto resondto = new ReasonDto();
				resondto.setRsn(resons.getExplanation());
				resondto.setRsncd(resons.getReasonCode());
				resonList.add(resondto);
			}
			
			DrcSaveToGstnDto jsonDto = new DrcSaveToGstnDto();	
			
			jsonDto.setFormType(APIIdentifiers.DRC01C_Form_Type);
			jsonDto.setGstin(gstin);
			jsonDto.setReturnPeriod(taxPeriod);
			jsonDto.setDeletedDrc03Arn(new ArrayList<String>());// empty 
			jsonDto.setDrc03Arn(drcArnNoList);
			jsonDto.setReasons(resonList);
			jsonDto.setReferenceId(refId);
			

			String reqJson = gson.toJson(jsonDto);

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"DRC01C Save Request Payload for GSTIN '%s', "
								+ "TaxPeriod '%s' is '%s'",
						gstin, taxPeriod, reqJson);
				LOGGER.debug(msg);
			}

			if (reqJson == null || reqJson.isEmpty()) {

				String msg = String.format(
						"No Data to DRC01C Save for GSTIN '%s', TaxPeriod '%s' ",
						gstin, taxPeriod);
				LOGGER.error(msg);
				throw new AppException(msg);
			}

			APIParam param1 = new APIParam("gstin", gstin);
			APIParam param2 = new APIParam("ret_period", taxPeriod);

			APIParams params = new APIParams(groupCode, APIProviderEnum.GSTN,
					APIIdentifiers.DRC01C_SAVE, param1, param2);
			APIResponse apiResp = apiExecutor.execute(params, reqJson);

			Long id = createSaveStatusEntry(taxPeriod, gstin,
					null, APIConstants.SAVE_INITIATED, null, groupCode, reqJson,
					apiResp.getResponse());

			if (apiResp.isSuccess()) {

				String status = apiResp.getResponse();
				LOGGER.debug("save status {} ", status);
				
				saveStatusRepo.updateStatus(APIConstants.SAVED, id);
				
				return "DRC01C Save Is Successful";
			} else {

				saveStatusRepo.updateStatus(APIConstants.SAVE_INITIATION_FAILED,
						id, apiResp.getErrors().toString());

				return "DRC01C Save Is Failed with "
						+ apiResp.getErrors().get(0).getErrorDesc();
			}

		} catch (Exception ex) {
			String msg = "Error while saving to gstn";
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}

	}
	
	private Long createSaveStatusEntry(String taxPeriod,
			String gstin, String refId, String status, String filePath,
			String groupCode, String request, String response) {

		TblDrc01cSaveStatus entity = new TblDrc01cSaveStatus();

		entity.setTaxPeriod(taxPeriod);
		entity.setGstin(gstin);
		entity.setStatus(status);
		entity.setRefId(refId);
		//entity.setErrorCount(0);
		entity.setFilePath(filePath);
		entity.setCreatedOn(LocalDateTime.now());
		entity.setUpdatedOn(LocalDateTime.now());
		entity.setIsActive(true);
		entity.setSaveRequestPayload(request);
		entity.setSaveResponsePayload(response);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Before saveStatusRepo entity {} :", entity);
		}
		
		//updating previous get call data.
		saveStatusRepo.updateActiveFlag(taxPeriod, gstin);

		entity = saveStatusRepo.save(entity);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Success saveStatusRepo entity {} :", entity);
		}
		return entity.getId();
	}
	
}
