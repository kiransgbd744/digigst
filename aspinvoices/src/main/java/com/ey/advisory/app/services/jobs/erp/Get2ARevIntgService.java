package com.ey.advisory.app.services.jobs.erp;

import java.util.List;

import com.ey.advisory.app.docs.dto.erp.Get2ARevIntgItemDto;

public interface Get2ARevIntgService {

	public List<Get2ARevIntgItemDto> get2ARevIntg(String gstin, int chunkId);
}
