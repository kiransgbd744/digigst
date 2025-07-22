/**
 * 
 */
package com.ey.advisory.admin.services.onboarding;

import java.util.List;

import com.ey.advisory.core.dto.ErpScenarioItmDetailsDto;

/**
 * @author Laxmi.Salukuti
 *
 */
public interface ErpSPIntegrationDao {

	List<ErpScenarioItmDetailsDto> getErpSPItems(Long entityId);

	List<ErpScenarioItmDetailsDto> getErpEventsSP();
}
