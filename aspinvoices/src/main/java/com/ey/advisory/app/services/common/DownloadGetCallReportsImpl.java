/**
 * 
 */
package com.ey.advisory.app.services.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.javatuples.Triplet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.dashboard.mergefiles.APICallDashboardReportMerger;
import com.ey.advisory.app.dashboard.mergefiles.FileMergeInput;
import com.ey.advisory.app.data.entities.client.FileStatusDownloadReportEntity;
import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.GetCallDto;
import com.ey.advisory.core.dto.InitiateGetCallDto;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Arun.KA
 *
 */

@Service("DownloadGetCallReportsImpl")
@Slf4j
public class DownloadGetCallReportsImpl implements DownloadGetCallReports {

	@Autowired
	FileStatusDownloadReportRepository fileStatusDownloadReportRepo;

	@Autowired
	DownloadGstr8AServiceImpl downloadGstr8AServiceImpl;

	@Autowired
	@Qualifier("DefaultAPICallDashboardReportMerger")
	private APICallDashboardReportMerger defaultReportMerger;

	@Override
	public void generateReport(Long id) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Begin file merging processor");
		}

		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();

		Optional<FileStatusDownloadReportEntity> optEntity = fileStatusDownloadReportRepo
				.findById(id);

		FileStatusDownloadReportEntity entity = optEntity.get();

		String reqObj = entity.getReqPayload();

		GetCallDto dto = gson.fromJson(reqObj, GetCallDto.class);
		String returnType = dto.getReturnType();
		List<Triplet<String, String, String>> combinations = new ArrayList<>();
		List<FileMergeInput> fileMergeInputList = new ArrayList<>();
		if ("Table-8A".equalsIgnoreCase(returnType)) {
			String fy = dto.getFy();
			String[] fyArr = fy.split("-");
			String taxPeriodFromFy = "0320" + fyArr[1];
			for (InitiateGetCallDto dt : dto.getGstinTaxPeiordList()) {
				FileMergeInput obj = new FileMergeInput(dt.getGstin(),
						taxPeriodFromFy, new ArrayList<>());
				fileMergeInputList.add(obj);
			}
			downloadGstr8AServiceImpl.mergeReport(id, fileMergeInputList, fy);
		} else if ("Gstr9".equalsIgnoreCase(returnType)) {
			String fy = dto.getFy();
			String taxPeriodFromFy = GenUtil.getFinancialPeriodFromFY(fy);
			for (InitiateGetCallDto dt : dto.getGstinTaxPeiordList()) {
				combinations.add(new Triplet<>(dt.getGstin(), taxPeriodFromFy,
						returnType));
			}
			defaultReportMerger.mergeReport(combinations, id);
		} else {
			for (InitiateGetCallDto dt : dto.getGstinTaxPeiordList()) {
				List<String> taxPeriodList = dt.getTaxPeriodList();
				for (String taxPeriod : taxPeriodList) {
					combinations.add(new Triplet<>(dt.getGstin(), taxPeriod,
							returnType));
				}
			}
			defaultReportMerger.mergeReport(combinations, id);
		}

	}

}
