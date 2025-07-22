package com.ey.advisory.app.data.daos.client.simplified;

import java.util.List;

import com.ey.advisory.app.docs.dto.simplified.Ret1AspDetailRespDto;
import com.ey.advisory.app.docs.dto.simplified.Ret1SummaryReqDto;

/**
 * 
 * @author Balakrishna.S
 *
 */
public interface Ret1AspDetailSectionDao {

	public abstract List<Ret1AspDetailRespDto> loadBasicSummarySection(
			Ret1SummaryReqDto req);
	
}
