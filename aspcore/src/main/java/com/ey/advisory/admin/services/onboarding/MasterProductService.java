package com.ey.advisory.admin.services.onboarding;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.javatuples.Pair;

import com.ey.advisory.core.dto.MasterProductReqDto;
import com.ey.advisory.core.dto.MasterProductRespDto;
import com.ey.advisory.core.dto.Messages;
/**
 * 
 * @author Umesha.M
 *
 */
public interface MasterProductService {
	
	//Get all active from Master Product Details 
	public Map<Long, Map<String, List<Pair<Integer, BigDecimal>>>> 
	getAllMasterProducts();

	//Get All Item Service from Master Product table
	public List<MasterProductRespDto> getMasterProduct(final Long entityId);
	
	// Update All details to master table 
	public Messages updateMasterProduct(final List<MasterProductReqDto> dtos);
	
	public void deleteMasterProduct(List<MasterProductReqDto> dtos);
	
	
}
