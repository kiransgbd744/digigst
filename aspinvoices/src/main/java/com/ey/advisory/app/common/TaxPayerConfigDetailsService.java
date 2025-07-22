package com.ey.advisory.app.common;

import java.util.List;

import com.ey.advisory.app.docs.dto.TaxPayerConfigDto;

public interface TaxPayerConfigDetailsService {

	public List<TaxPayerConfigDto> getTaxPayerConfigDetails(boolean einvApplicable);

}
