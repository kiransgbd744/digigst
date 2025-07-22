package com.ey.advisory.app.data.services.drc01c;

import java.util.List;

import com.ey.advisory.app.data.services.drc.DrcCommRespDto;
import com.ey.advisory.app.data.services.drc.DrcGetReminderFrequencyRespDto;

public interface Drc01EmailCommService {

	public List<DrcCommRespDto> getDRC1CommDetails(
			List<String> gstinList, DrcGetReminderFrequencyRespDto reqDto);

}