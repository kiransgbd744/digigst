/**
 * 
 */
package com.ey.advisory.app.services.search.filestatussearch;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.admin.data.repositories.client.FileStatusRepository;
import com.ey.advisory.app.data.entities.client.FileStatusDownloadReportEntity;
import com.ey.advisory.app.docs.dto.FileStatusReportDto;
import com.ey.advisory.app.search.reports.BasicCommonSecParamAsyncReports;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.ReportStatusConstants;
import com.ey.advisory.common.ReportTypeConstants;
import com.ey.advisory.common.SecurityContext;
import com.google.common.base.Strings;

/**
 * @author Sujith.Nanga
 *
 * 
 */

@Component("AsyncGstr2ReportHandlerImpl")
public class AsyncGstr2ReportHandlerImpl implements AsyncReportHandler {

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

		if ("Consolidated_DigiGST_Error_Report"
				.equalsIgnoreCase(reqDto.getType())) {
			entity.setReportType("Consolidated DigiGST Error");
		} else if ("Processed_Records_Recon_Tagging"
				.equalsIgnoreCase(reqDto.getType())) {
			entity.setReportType("Processed Records (Recon Tagging)");
			entity.setReconType(reqDto.getReconType());
		}
		entity.setCreatedBy(userName);
		entity.setCreatedDate(LocalDateTime.now());
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

			/*
			 * entity.setDocDateFrom(EYDateUtil.toUTCDateTimeFromLocal(
			 * setDataSecurity.getDocDateFrom().atStartOfDay()));
			 * entity.setDocDateTo(EYDateUtil.toUTCDateTimeFromLocal(
			 * setDataSecurity.getDocDateTo().atStartOfDay()));
			 */

			entity.setDocDateFrom(reqDto.getDocDateFrom());
			entity.setDocDateTo(reqDto.getDocDateTo());

			if (!Strings.isNullOrEmpty(reqDto.getReturnFrom())) {
				entity.setTaxPeriodFrom(reqDto.getReturnFrom());
				Long derivedRetPeriodFrom = Long.valueOf(
						GenUtil.convertTaxPeriodToInt(reqDto.getReturnFrom()));
				entity.setDerivedRetPeriodFrom(derivedRetPeriodFrom);
			}
			if (!Strings.isNullOrEmpty(reqDto.getReturnTo())) {
				entity.setTaxPeriodTo(reqDto.getReturnTo());
				Long derivedRetPeriodTo = Long.valueOf(
						GenUtil.convertTaxPeriodToInt(reqDto.getReturnTo()));
				entity.setDerivedRetPeriodFromTo(derivedRetPeriodTo);
			}
			List<String> tableType = reqDto.getTableType();
			List<String> docType = reqDto.getDocType();
			List<String> doclist = new ArrayList<>();
			List<String> docCategory = reqDto.getDocCategory();
			List<String> tablelist = new ArrayList<>();

			List<String> reconReportsType = reqDto.getReconReportType();
			List<String> reconReportTypelist = new ArrayList<>();

			if (!CollectionUtils.isEmpty(reconReportsType)) {
				for (String reconReportType : reconReportsType) {
					reconReportTypelist.add("'" + reconReportType + "'");
				}

				entity.setReconReportType(reconReportTypelist.stream()
						.collect(Collectors.joining(",")));
			}

			if (!CollectionUtils.isEmpty(tableType)) {
				for (String table : tableType) {
					tablelist.add("'" + table + "'");
				}

				entity.setTableType(
						tablelist.stream().collect(Collectors.joining(",")));
			}

			if (!CollectionUtils.isEmpty(docType)) {
				for (String doc : docType) {
					doclist.add("'" + doc + "'");
				}

				entity.setDocType(
						doclist.stream().collect(Collectors.joining(",")));
			}

			/*
			 * if(!CollectionUtils.isEmpty(docType)){
			 * entity.setDocType(docType.stream().collect(Collectors.joining(","
			 * ))); }
			 */
			if (!CollectionUtils.isEmpty(docCategory)) {
				entity.setDocCategory(
						docCategory.stream().collect(Collectors.joining(",")));
			}
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
}
