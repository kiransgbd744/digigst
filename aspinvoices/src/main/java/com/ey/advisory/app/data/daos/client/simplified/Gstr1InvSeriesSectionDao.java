package com.ey.advisory.app.data.daos.client.simplified;

import java.util.List;

import com.ey.advisory.app.docs.dto.Gstr1VerticalDocSeriesRespDto;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;

/**
 * 
 * @author Mahesh.Golla
 *
 */
public interface Gstr1InvSeriesSectionDao {

	List<Gstr1VerticalDocSeriesRespDto> gstinViewSection(
			Annexure1SummaryReqDto req);

}
