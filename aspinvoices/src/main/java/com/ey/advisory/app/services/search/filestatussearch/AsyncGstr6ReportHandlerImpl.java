/**
 * 
 */
package com.ey.advisory.app.services.search.filestatussearch;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.admin.data.repositories.client.FileStatusRepository;
import com.ey.advisory.app.data.entities.client.FileStatusDownloadReportEntity;
import com.ey.advisory.app.docs.dto.FileStatusReportDto;
import com.ey.advisory.app.search.reports.BasicCommonSecParamAsyncReports;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.ReportStatusConstants;
import com.ey.advisory.common.ReportTypeConstants;
import com.ey.advisory.common.SecurityContext;

/**
 * @author Sujith.Nanga
 *
 * 
 */

@Component("AsyncGstr6ReportHandlerImpl")
public class AsyncGstr6ReportHandlerImpl implements AsyncReportHandler {

	@Autowired
	@Qualifier("BasicCommonSecParamAsyncReports")
	BasicCommonSecParamAsyncReports basicCommonSecParamAsyncReports;

	@Autowired
	FileStatusRepository fileStatusRepo;

	@Override
	public void setDataToEntity(FileStatusDownloadReportEntity entity,
			FileStatusReportDto reqDto) {

		User user = SecurityContext.getUser();
		String userName = user.getUserPrincipalName();

		/* entity.setStatus(reqDto.getStatus()); */
		entity.setReportType(reqDto.getType());
		entity.setCreatedBy(userName);
		entity.setCreatedDate(
				EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
		entity.setReportStatus(ReportStatusConstants.INITIATED);
		entity.setReportCateg(reqDto.getReportCateg());
		entity.setDataType(reqDto.getDataType());

		String reportCateg = reqDto.getReportCateg();

		if (ReportTypeConstants.PROCESSED_SUMMARY.equalsIgnoreCase(reportCateg)
				|| ReportTypeConstants.REVIEW_SUMMARY
						.equalsIgnoreCase(reportCateg)) {

			FileStatusReportDto setDataSecurity = basicCommonSecParamAsyncReports
					.setDataSecuritySearchParams(reqDto);

			entity.setEntityId(setDataSecurity.getEntityId().get(0));
			entity.setTaxPeriod(reqDto.getTaxPeriod());

			Long derivedRetPeriod = Long.valueOf(GenUtil
					.convertTaxPeriodToInt(reqDto.getTaxPeriod()).toString());
			entity.setDerivedRetPeriod(derivedRetPeriod);

			Map<String, List<String>> dataSec = setDataSecurity
					.getDataSecAttrs();
			entity.setGstins(GenUtil.convertStringToClob(convertToQueryFormat(
					dataSec.get(OnboardingConstant.GSTIN))));
			entity.setDisChannel(
					convertToQueryFormat(dataSec.get(OnboardingConstant.DC)));
			entity.setDivision(convertToQueryFormat(
					dataSec.get(OnboardingConstant.DIVISION)));
			entity.setLocation(convertToQueryFormat(
					dataSec.get(OnboardingConstant.LOCATION)));
			entity.setPlantCode(convertToQueryFormat(
					dataSec.get(OnboardingConstant.PLANT)));
			entity.setProfitCenter(
					convertToQueryFormat(dataSec.get(OnboardingConstant.PC)));
			entity.setPurchaseOrg(
					convertToQueryFormat(dataSec.get(OnboardingConstant.PO)));
			entity.setSalesOrg(
					convertToQueryFormat(dataSec.get(OnboardingConstant.SO)));
			entity.setSubdivision(convertToQueryFormat(
					dataSec.get(OnboardingConstant.DIVISION)));
			entity.setUsrAcs1(
					convertToQueryFormat(dataSec.get(OnboardingConstant.UD1)));
			entity.setUsrAcs2(
					convertToQueryFormat(dataSec.get(OnboardingConstant.UD2)));
			entity.setUsrAcs3(
					convertToQueryFormat(dataSec.get(OnboardingConstant.UD3)));
			entity.setUsrAcs4(
					convertToQueryFormat(dataSec.get(OnboardingConstant.UD4)));
			entity.setUsrAcs5(
					convertToQueryFormat(dataSec.get(OnboardingConstant.UD5)));
			entity.setUsrAcs6(
					convertToQueryFormat(dataSec.get(OnboardingConstant.UD6)));
		}

	}

	private String convertToQueryFormat(List<String> list) {

		String queryData = null;

		if (list == null || list.isEmpty())
			return null;

		queryData = "'" + list.get(0) + "'";
		for (int i = 1; i < list.size(); i++) {
			queryData += "," + "'" + list.get(i) + "'";
		}

		return queryData;

	}

	private Long getDerivedRetPeriod(String taxPeriod) {
		return Long.valueOf(
				taxPeriod.substring(2).concat(taxPeriod.substring(0, 2)));
	}

}
