package com.ey.advisory.app.docs.services.gstr6;

import java.util.List;

import com.ey.advisory.app.data.statecode.dto.Gstr6DistributionStateCodeDetailsDto;

/**
 * 
 * @author Dibyakanta.S
 *
 */

public interface Gstr6DistributedStateCodeSummaryDao {

	public List<Gstr6DistributionStateCodeDetailsDto> findGetState();

}
