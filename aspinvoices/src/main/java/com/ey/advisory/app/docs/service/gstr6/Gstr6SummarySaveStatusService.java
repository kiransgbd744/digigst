package com.ey.advisory.app.docs.service.gstr6;

import java.util.List;

import com.ey.advisory.app.docs.dto.gstr1.Gstr1SummarySaveStatusRespDto;
import com.ey.advisory.core.dto.Gstr1SummarySaveStatusReqDto;

public interface Gstr6SummarySaveStatusService {
	public List<Gstr1SummarySaveStatusRespDto> findByCriteria(
			Gstr1SummarySaveStatusReqDto criteria);
}
