package com.ey.advisory.app.data.services.savetogstn.gstr8;

import java.util.List;

import com.ey.advisory.app.docs.dto.gstr8.Gstr8SaveStatusDto;

public interface Gstr8SaveStatus {

	public List<Gstr8SaveStatusDto> getSaveStatus(String gstin, String fy);

}
