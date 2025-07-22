package com.ey.advisory.admin.services.onboarding;

import java.util.List;

import com.ey.advisory.core.dto.MasterCustomerReqDto;
import com.ey.advisory.core.dto.MasterCustomerRespDto;
import com.ey.advisory.core.dto.Messages;
/**
 * 
 * @author Umesha.M
 *
 */
public interface MasterCustomerService {
	
	/**
	 * 
	 * @param entityId
	 * @return
	 */
	public List<String> getAllMasterCustomerGstins();
	
	/**
	 * This method used to get all Master Customer data by Entity Id
	 * @return
	 */
	public List<MasterCustomerRespDto> getMasterCustomer(final Long entityId);
	
	/**
	 * This method used to Update All Given Master Customer Values
	 * @param dtos
	 */
	public Messages updateMasterCustomer(List<MasterCustomerReqDto> dtos);
	
	public void deleteMasterCustomer(List<MasterCustomerReqDto> dtos);
}
