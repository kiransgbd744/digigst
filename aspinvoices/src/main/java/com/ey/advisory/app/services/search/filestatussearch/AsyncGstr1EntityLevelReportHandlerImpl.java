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
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.core.api.APIConstants;

/**
 * @author Balakrishna.S
 *
 */
@Component("AsyncGstr1EntityLevelReportHandlerImpl")
public class AsyncGstr1EntityLevelReportHandlerImpl
		implements AsyncReportHandler {

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
		if(reqDto.getReportCateg()!=null && APIConstants.GSTR1A.equalsIgnoreCase(reqDto.getReportCateg()))
		{	
		entity.setReportCateg("GSTR1A");
		}else
		{
			entity.setReportCateg("GSTR1");
			
		}
		entity.setDataType(reqDto.getDataType());

		// String reportCateg = reqDto.getReportCateg();

		/*
		 * if(ReportTypeConstants.PROCESSED_SUMMARY.equalsIgnoreCase(reportCateg
		 * )||
		 * ReportTypeConstants.REVIEW_SUMMARY.equalsIgnoreCase(reportCateg)){
		 */

		FileStatusReportDto setDataSecurity = basicCommonSecParamAsyncReports
				.setDataSecuritySearchParams(reqDto);

		entity.setEntityId(setDataSecurity.getEntityId().get(0));
		// entity.setDocDateFrom(reqDto.getDocDateFrom());
		// entity.setDocDateTo(reqDto.getDocDateTo());
		entity.setTaxPeriod(reqDto.getTaxPeriod());

		Long derivedRetPeriod = Long.valueOf(GenUtil
				.convertTaxPeriodToInt(reqDto.getTaxPeriod()).toString());
		entity.setDerivedRetPeriod(derivedRetPeriod);

		// List<String> tableType = reqDto.getTableType();
		/*
		 * List<String> cdnrTableType = new ArrayList<>(); for (String table :
		 * tableType) { if (table.equalsIgnoreCase("CDNUR")) {
		 * cdnrTableType.add("CDNUR-EXPORTS"); cdnrTableType.add("CDNUR-B2CL");
		 * } } tableType.addAll(cdnrTableType);
		 */
		/*
		 * List<String> docType = reqDto.getDocType(); List<String>
		 * einvGenerated = reqDto .getEInvGenerated(); List<String> ewbGenerated
		 * = reqDto .getEWbGenerated(); List<String> doclist = new
		 * ArrayList<>(); List<String> tablelist = new ArrayList<>();
		 * List<String> einvlist = new ArrayList<>(); List<String> ewblist = new
		 * ArrayList<>();
		 * 
		 * if (!CollectionUtils.isEmpty(tableType)) { for (String table :
		 * tableType) { tablelist.add("'" + table + "'"); }
		 * 
		 * entity.setTableType(
		 * tablelist.stream().collect(Collectors.joining(","))); }
		 * 
		 * if (!CollectionUtils.isEmpty(docType)) { for (String doc : docType) {
		 * doclist.add("'" + doc + "'"); }
		 * 
		 * entity.setDocType(
		 * doclist.stream().collect(Collectors.joining(","))); }
		 * 
		 * if (!CollectionUtils.isEmpty(einvGenerated)) { for (String einv :
		 * einvGenerated) { einvlist.add(einv); }
		 * 
		 * entity.setEInvGenerated(
		 * einvlist.stream().collect(Collectors.joining(","))); }
		 * 
		 * if (!CollectionUtils.isEmpty(ewbGenerated)) { for (String ewb :
		 * ewbGenerated) { ewblist.add(ewb ); }
		 * 
		 * entity.setEWbGenerated(
		 * ewblist.stream().collect(Collectors.joining(","))); }
		 */
		Map<String, List<String>> dataSec = setDataSecurity.getDataSecAttrs();
		entity.setGstins(GenUtil.convertStringToClob(
				convertToQueryFormat(dataSec.get(OnboardingConstant.GSTIN))));
		/*
		 * entity.setDisChannel(
		 * convertToQueryFormat(dataSec.get(OnboardingConstant.DC)));
		 * entity.setDivision(
		 * convertToQueryFormat(dataSec.get(OnboardingConstant.DIVISION)));
		 * entity.setLocation(
		 * convertToQueryFormat(dataSec.get(OnboardingConstant.LOCATION)));
		 * entity.setPlantCode(
		 * convertToQueryFormat(dataSec.get(OnboardingConstant.PLANT)));
		 * entity.setProfitCenter(
		 * convertToQueryFormat(dataSec.get(OnboardingConstant.PC)));
		 * entity.setPurchaseOrg(
		 * convertToQueryFormat(dataSec.get(OnboardingConstant.PO)));
		 * entity.setSalesOrg(
		 * convertToQueryFormat(dataSec.get(OnboardingConstant.SO)));
		 * entity.setSubdivision(
		 * convertToQueryFormat(dataSec.get(OnboardingConstant.DIVISION)));
		 * entity.setUsrAcs1(
		 * convertToQueryFormat(dataSec.get(OnboardingConstant.UD1)));
		 * entity.setUsrAcs2(
		 * convertToQueryFormat(dataSec.get(OnboardingConstant.UD2)));
		 * entity.setUsrAcs3(
		 * convertToQueryFormat(dataSec.get(OnboardingConstant.UD3)));
		 * entity.setUsrAcs4(
		 * convertToQueryFormat(dataSec.get(OnboardingConstant.UD4)));
		 * entity.setUsrAcs5(
		 * convertToQueryFormat(dataSec.get(OnboardingConstant.UD5)));
		 * entity.setUsrAcs6(
		 * convertToQueryFormat(dataSec.get(OnboardingConstant.UD6)));
		 */ }

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
