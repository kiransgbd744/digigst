package com.ey.advisory.app.service.ims.supplier;

import java.util.List;

import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;

public interface  SupplierImsGetCallRestrictionService {
	
	public List<Gstr1GetInvoicesReqDto> processGetCall(Gstr1GetInvoicesReqDto reqDtos);

}
