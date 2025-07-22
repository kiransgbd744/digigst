package com.ey.advisory.app.services.aspreports;

public interface MasterAspReportsService {

	public MasterAspReportsResponseDto getMasterAspReports() throws Exception;

	MasterAspReportsDetailsResponseDto fetchDetailsReports(
			MasterAspReportsDetailsRequestDto masterDetailsReportsRequestDto);
}
