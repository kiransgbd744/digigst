package com.ey.advisory.app.data.statecode.service;

import java.util.List;

import com.ey.advisory.app.data.statecode.dto.Gstr6DistributionStateCodeDetailsDto;

public interface Gstr6DistStateCodeDetailsService {
	public List<Gstr6DistributionStateCodeDetailsDto> findStates();
}
