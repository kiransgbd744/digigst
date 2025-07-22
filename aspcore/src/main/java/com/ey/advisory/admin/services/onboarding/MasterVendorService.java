package com.ey.advisory.admin.services.onboarding;

import java.util.List;

import com.ey.advisory.core.dto.MasterVendorReqDto;
import com.ey.advisory.core.dto.MasterVendorRespDto;
import com.ey.advisory.core.dto.Messages;
/**
 * 
 * @author Umesha.M
 *
 */
public interface MasterVendorService {
	
	public List<String> getAllMasterVendorGstins();

	public List<MasterVendorRespDto> getMasterVendor(final Long entityId);
	
	public Messages updateMasterVendor(final List<MasterVendorReqDto> dtos);
	
	public void deleteMasterVendor(List<MasterVendorReqDto> dtos);
}
