package com.ey.advisory.admin.services.onboarding;

import java.util.List;

import com.ey.advisory.core.dto.ELEntitlementDto;
import com.ey.advisory.core.dto.ElEntitlementHistoryReqDto;
import com.ey.advisory.core.dto.ElEntitlementReqDto;
import com.ey.advisory.core.dto.Messages;

/**
 * @author Umesha.M
 *
 */
public interface ELEntitlementService {

    /**
     * Get Call from EL Entitlement table
     * 
     * @param jsonReq
     * @return
     */
    public List<ELEntitlementDto> getLatestElEntDetails(
	    final ElEntitlementReqDto dto);

    public List<ELEntitlementDto> getEleEntitlementHistory(
	    final ElEntitlementHistoryReqDto dto);

    /**
     * Update or save EL Entitlement Table
     * 
     * @param jsonReq
     * @return
     */
    public Messages updateLatestElEntDetails(
	    final List<ElEntitlementReqDto> elEntitlementReqDtos);
}
