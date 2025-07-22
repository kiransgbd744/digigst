package com.ey.advisory.app.sac;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.client.SacGroupConfigRepository;
import com.ey.advisory.common.multitenancy.TenantContext;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */

@Slf4j
@Component("SacDashboardURLService")
public class SacDashboardURLService {

	@Autowired
	@Qualifier("SacGroupConfigRepository")
	private SacGroupConfigRepository repo;
	
	//GSTR-1 Outward Report -- USP_SAC_GSTR1_OUTWARD_RPT
	//GSTR-1 Inward Report -- TBL_SAC_GSTR1_INWARD

	public String getSacUrl(String reportName, Integer entityId) {
		
		String url = null;
		String groupCode = TenantContext.getTenantId();
		
		try {
			
		SacGroupConfigEntity obj = repo.findUrlByConfigValueAndGroupCode(
				reportName,groupCode,entityId);
		
		url = obj.getUrl();
		
		} catch(Exception ex){
			String msg = String.format("Error Occured while Executing "
					+ "stor proc:: Group code %s ", groupCode);
			LOGGER.error(msg,ex);
		}
		return url;
	}

}
