package com.ey.advisory.app.services.feedback;

import java.io.File;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface FeedbackSurveyService {

	public List<FDSurveyGetRespDto> fetchSurveyRecords(String userName);

	public String saveSurveyRecords(FDSurveyRespDto respDto, MultipartFile file,
			File tempDir);

	public List<FDSurveyRespDto> getAllUserFeedbackSvyDtls(String groupCode);

}