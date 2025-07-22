package com.ey.advisory.app.services.savetogstn.jobs.gstr1;

import com.ey.advisory.core.dto.Gstr1SaveToGstnReqDto;

/**
 * 
 * @author Hemasundar.J
 *
 */
public interface SupplierGstinExtractorFromCriteria {

	public String buildSgstinRetPerdsQuery(Gstr1SaveToGstnReqDto dto);

	public String buildGStr7SgstinRetPerdsQuery(Gstr1SaveToGstnReqDto dto);

	public String buildItc04SgstinRetPerdsQuery(Gstr1SaveToGstnReqDto dto);
	
	public String buildGstr1ASgstinRetPerdsQuery(Gstr1SaveToGstnReqDto dto);

	
	
}
