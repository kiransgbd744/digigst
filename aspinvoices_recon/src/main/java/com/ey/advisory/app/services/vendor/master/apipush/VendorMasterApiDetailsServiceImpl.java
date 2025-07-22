/**
 * 
 */
package com.ey.advisory.app.services.vendor.master.apipush;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.repositories.client.asprecon.VendorApiUploadStatusRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.VendorMasterApiPayloadRepository;
import com.ey.advisory.app.itcmatching.vendorupload.ItcMatchingVendorDataFileUpld;
import com.ey.advisory.app.itcmatching.vendorupload.VendorAPIPushListDto;
import com.ey.advisory.app.itcmatching.vendorupload.VendorApiUploadStatusEntity;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.async.JobStatusConstants;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import lombok.extern.slf4j.Slf4j;

@Service("VendorMasterApiDetailsServiceImpl")
@Slf4j
public class VendorMasterApiDetailsServiceImpl {

	@Autowired
	@Qualifier("VendorMasterApiPayloadRepository")
	private VendorMasterApiPayloadRepository payloadRepo;

	@Autowired
	private AsyncJobsService asyncJobsService;

	@Autowired
	@Qualifier("VendorApiUploadStatusRepository")
	VendorApiUploadStatusRepository apiStatusRepository;
	
	@Autowired
	@Qualifier("ItcMatchingVendorDataFileUpld")
	private ItcMatchingVendorDataFileUpld vendorDataFileUpload;

	public void fetchVendorMasterApiDetails(String payloadId, String sourceId,
			String groupCode) {

		try {
			VendorMasterApiPayloadEntity payloadEntity = payloadRepo
					.getVendorMaterApiPayload(payloadId);
			String jsonString = payloadEntity.getReqPlayload();

			Gson gson = GsonUtil.newSAPGsonInstance();
			VendorMasterApiReqDtoList dtoList = gson.fromJson(jsonString,
					VendorMasterApiReqDtoList.class);

			List<Object[]> req = new ArrayList<>();
			for (VendorAPIPushListDto dto : dtoList.getReqDtoList()) {
				Object[] obj = new Object[28];
				convertDtoToObj(obj, dto);
				req.add(obj);
			}
			logRequestPayload(jsonString, true,payloadId);
			vendorDataFileUpload.validation(req, null, null, payloadId);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Posting reverse integration job for payloadId : {} ",
						payloadId);
			}

			String jobCategory = APIConstants.VENDOR_MASTER_API_PAYLOAD_METADATA_REV_INTG;
			JsonObject jobParams = new JsonObject();
			jobParams.addProperty("payloadId", payloadId);
			jobParams.addProperty("sourceId", sourceId);
			jobParams.addProperty("scenarioName",
					APIConstants.VENDOR_MASTER_API_PAYLOAD_METADATA_REV_INTG);

			asyncJobsService.createJob(groupCode, jobCategory,
					jobParams.toString(), "SYSTEM", JobConstants.PRIORITY,
					JobConstants.PARENT_JOB_ID,
					JobConstants.SCHEDULE_AFTER_IN_MINS);
		} catch (Exception ex) {
			LOGGER.error(" Error occured while fetching the vendor details {} ",
					ex);
			throw new AppException(ex);
		}
	}

	private static void convertDtoToObj(Object[] obj,
			VendorAPIPushListDto reqDto) {
		obj[0] = reqDto.getRecipientPAN();
		obj[1] = reqDto.getVendorPAN();
		obj[2] = reqDto.getVendorGstin();
		obj[3] = reqDto.getSupplierCode();
		obj[4] = reqDto.getVendorName();
		obj[5] = reqDto.getVendPrimEmailId();
		obj[6] = reqDto.getVendorContactNumber();
		obj[7] = reqDto.getVendorEmailId1();
		obj[8] = reqDto.getVendorEmailId2();
		obj[9] = reqDto.getVendorEmailId3();
		obj[10] = reqDto.getVendorEmailId4();
		obj[11] = reqDto.getRecipientEmailId1();
		obj[12] = reqDto.getRecipientEmailId2();
		obj[13] = reqDto.getRecipientEmailId3();
		obj[14] = reqDto.getRecipientEmailId4();
		obj[15] = reqDto.getRecipientEmailId5();
		obj[16] = reqDto.getVendorType();
		obj[17] = reqDto.getHsn();
		obj[18] = reqDto.getVendorRiskCategory();
		obj[19] = reqDto.getVendorPaymentTerms();
		obj[20] = reqDto.getVendorRemarks();
		obj[21] = reqDto.getApprovalStatus();
		obj[22] = reqDto.getExcludeVendorRemarks();
		obj[23] = reqDto.getIsVendorCom();
		obj[24] = reqDto.getIsExcludeVendor();
		obj[25] = reqDto.getIsNonComplaintCom();
		obj[26] = reqDto.getIsCreditEligibility();
		obj[27] = reqDto.getIsDelete();
	}
	
	private VendorApiUploadStatusEntity logRequestPayload(String jsonString,
			boolean value,String payloadId) {
		VendorApiUploadStatusEntity apiStatus = new VendorApiUploadStatusEntity();

		apiStatus.setRefId(payloadId);
		apiStatus.setCreatedBy("ERP");
		apiStatus.setCreatedOn(LocalDateTime.now());
		apiStatus.setReqPayload(
				GenUtil.convertStringToClob(jsonString.toString()));
		if (value) {
			apiStatus.setApiStatus(JobStatusConstants.UPLOADED);
		} else {
			apiStatus.setApiStatus("FAILED");
		}
		return apiStatusRepository.save(apiStatus);

	}
}
