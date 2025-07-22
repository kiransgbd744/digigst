package com.ey.advisory.admin.services.onboarding;

import java.util.List;
import java.util.Map;

import org.javatuples.Pair;

import com.ey.advisory.core.dto.OrganizationDataResDto;
import com.ey.advisory.core.dto.OrganizationReqDto;
import com.ey.advisory.core.dto.OrganizationResDto;

/**
 * @author Umesha.M
 *
 */
public interface OrganizationService {

    /**
     * @param reqJson
     * @return
     */
    public List<OrganizationResDto> getOrganization(
	    final OrganizationReqDto organizationReqDto);

    /**
     * @param reqJson
     * @return
     */
    public void updateOrganization(
	    final List<OrganizationReqDto> organizationReqDtos);

    public List<OrganizationDataResDto> getOrganizationData(
	    final OrganizationReqDto organizationReqDto);

    public String addOrganizationData(
	    final List<OrganizationReqDto> organizationReqDtos);

    public String deleteOrganizationData(
	    final List<OrganizationReqDto> organizationReqDtos);
    
    public Map<Long, List<Pair<String, String>>> getAllEntityAtValMap();
    
	public Map<EntityAtConfigKey, Map<Long, String>> getEntityAtConfMap(
			String transDocType);
}
