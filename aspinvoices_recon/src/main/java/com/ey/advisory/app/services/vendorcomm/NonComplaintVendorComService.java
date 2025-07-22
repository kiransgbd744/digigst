package com.ey.advisory.app.services.vendorcomm;

import java.util.List;
import java.util.Set;

import org.javatuples.Pair;

import com.ey.advisory.app.data.entities.client.asprecon.NonCompVendorRequestEntity;
import com.ey.advisory.app.data.entities.client.asprecon.NonCompVendorVGstinEntity;
import com.ey.advisory.app.itcmatching.vendorupload.VendorGstinDto;
import com.ey.advisory.app.vendorcomm.dto.GstinDto;
import com.ey.advisory.app.vendorcomm.dto.NonCompVendorRequestDto;
import com.ey.advisory.app.vendorcomm.dto.VendorEmailCommDto;

/**
 * @author Saif.S
 *
 */
public interface NonComplaintVendorComService {

	List<GstinDto> getListOfVendorPans(Long entityId);

	List<GstinDto> getListOfNonComplaintComVendorGstin(List<String> vendorPans,
			Long entityId);

	List<VendorGstinDto> getVendorNameForGstin(List<String> vendorGstInList,
			Long entityId);

	List<String> getListOfVendorCode(List<String> vendorNameList,
			List<String> vendorGstInList, Long entityId);

	void createEntryNonCompVendorReqVGstin(Long requestId, String vendorGstin,
			Set<String> returnType);

	String generateNonCompVendorReportUploadAsync(Long requestId);

	Long createEntryNonComplaintVendorComReq(Long noOfVendorGstins,
			String financialYear, Long entityId);

	List<NonCompVendorRequestEntity> getNonCompVendorCommDataByUserName(
			String userName);

	List<NonCompVendorRequestDto> getNonCompVendorCommResponse(
			List<NonCompVendorRequestEntity> vendorComReqList);

	List<NonCompVendorVGstinEntity> getNonCompVendorReqVgstinData(
			Long requestId);

	Pair<List<VendorEmailCommDto>, Integer> getNonCompEmailCommunicationDetails(
			Long requestId, Long entityId, int pageSize, int pageNum);

	String createReqPayloadForEmail(String jsonString);
}
