package com.ey.advisory.admin.services.onboarding;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.javatuples.Pair;

import com.ey.advisory.core.dto.MasterItemReqDto;
import com.ey.advisory.core.dto.MasterItemRespDto;
import com.ey.advisory.core.dto.Messages;
/**
 * 
 * @author Umesha.M
 *
 */
public interface MasterItemService {
	
	//Get all active from Master Item Details 
	public Map<Long, Map<String, List<Pair<Integer, BigDecimal>>>> 
	getAllMasterItems();

	//Get All Item Service from Master table
	public List<MasterItemRespDto> getMasterItem(final Long entityId);
	
	// Update All details to master table 
	public Messages updateMasterItem(final List<MasterItemReqDto> dtos);
	
	public void deleteMasterItem(final List<MasterItemReqDto> dtos);
}
