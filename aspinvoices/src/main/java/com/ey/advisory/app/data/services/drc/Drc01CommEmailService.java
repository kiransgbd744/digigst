package com.ey.advisory.app.data.services.drc;

import java.util.List;

import com.ey.advisory.app.common.GstrEmailDetailsDto;
import com.ey.advisory.app.data.entities.drc.TblDrc01AutoGetCallDetails;

public interface Drc01CommEmailService {

	String persistAndSendDrc01Email(List<GstrEmailDetailsDto> reqDtos,
			TblDrc01AutoGetCallDetails entity, int reminderNumber);
}
