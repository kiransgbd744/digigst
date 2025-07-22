package com.ey.advisory.app.services.search.filestatussearch;

import java.util.List;

import com.ey.advisory.app.docs.dto.FileStatusReqDto;
import com.ey.advisory.app.docs.dto.FileStatusResponseDto;

public interface MasterFileStatusService {

	public List<FileStatusResponseDto> getMasterFileStatus(
	        final FileStatusReqDto reqDto);
}
