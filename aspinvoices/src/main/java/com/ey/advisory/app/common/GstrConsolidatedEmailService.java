package com.ey.advisory.app.common;

import java.util.List;
import java.util.Map;

import org.javatuples.Pair;
import org.javatuples.Quintet;

import com.ey.advisory.admin.data.entities.client.Gstr2bAutoCommEntity;
import com.ey.advisory.app.data.entities.client.RecipientMasterUploadEntity;

public interface GstrConsolidatedEmailService {

	Quintet<List<String>, List<GstinWiseEmailDto>, String, Boolean, String> getGstinsData(
			List<RecipientMasterUploadEntity> uploadEntites,
			List<String> activeGstins);

	void persistAndSendEmail(List<GstrEmailDetailsDto> reqDtos,
			boolean isToSave);

	Map<String, List<RecipientMasterUploadEntity>> groupByPrimayEmail(
			List<RecipientMasterUploadEntity> uploadEntites);

	Pair<List<String>, List<GstinWiseEmailDto>> getGstinsAndSectionWiseData(
			List<RecipientMasterUploadEntity> uploadEntites,
			List<Gstr2bAutoCommEntity> getCallRecords, String taxPeriod);
}
