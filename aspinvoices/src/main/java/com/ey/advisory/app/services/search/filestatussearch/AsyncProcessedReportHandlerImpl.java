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
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.ReportStatusConstants;
import com.ey.advisory.common.ReportTypeConstants;
import com.ey.advisory.common.SecurityContext;

@Component("AsyncProcessedReportHandlerImpl")
public class AsyncProcessedReportHandlerImpl implements AsyncReportHandler {

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
		
		/*String gstin = null;
		List<String> gstinList = null;
		if (!reqDto.getDataSecAttrs().isEmpty()) {
			for (String key : reqDto.getDataSecAttrs().keySet()) {

				if (key.equalsIgnoreCase(OnboardingConstant.GSTIN)) {
					gstin = key;
					if (!reqDto.getDataSecAttrs().get(OnboardingConstant.GSTIN).isEmpty()
							&& reqDto.getDataSecAttrs().get(OnboardingConstant.GSTIN)
									.size() > 0) {
						gstinList = reqDto.getDataSecAttrs().get(OnboardingConstant.GSTIN);
					}
				}
			}
		}
		entity.setGstins(GenUtil.convertStringToClob(
				convertToQueryFormat(gstinList)));*/
		/*if(reqDto.getEntityId() != null && !reqDto.getEntityId().isEmpty()){
			entity.setEntityId(reqDto.getEntityId().get(0));
		}
		entity.setTaxPeriod(reqDto.getTaxPeriod());*/
		if (ReportTypeConstants.PROCESSED_SUMMARY.equalsIgnoreCase(reportCateg)
				|| ReportTypeConstants.REVIEW_SUMMARY
						.equalsIgnoreCase(reportCateg)
				|| ReportTypeConstants.STOCK_TRANSFER
						.equalsIgnoreCase(reportCateg)
				|| ReportTypeConstants.SHIPPING_BILL
						.equalsIgnoreCase(reqDto.getType()) 
				|| ReportTypeConstants.RATE_LEVEL_REPORT
								.equalsIgnoreCase(reqDto.getType())
				|| ReportTypeConstants.HSN_SUMMARY
								.equalsIgnoreCase(reqDto.getType())				) {

			FileStatusReportDto setDataSecurity = basicCommonSecParamAsyncReports
					.setDataSecuritySearchParams(reqDto);

			entity.setEntityId(setDataSecurity.getEntityId().get(0));
			entity.setDocDateFrom(reqDto.getDocDateFrom());
			entity.setDocDateTo(reqDto.getDocDateTo());
			entity.setTaxPeriod(reqDto.getTaxPeriod());

			Long derivedRetPeriod = Long.valueOf(GenUtil
					.convertTaxPeriodToInt(reqDto.getTaxPeriod()).toString());
			entity.setDerivedRetPeriod(derivedRetPeriod);

			List<String> tableType = reqDto.getTableType();
			/*
			 * List<String> cdnrTableType = new ArrayList<>(); for (String table
			 * : tableType) { if (table.equalsIgnoreCase("CDNUR")) {
			 * cdnrTableType.add("CDNUR-EXPORTS");
			 * cdnrTableType.add("CDNUR-B2CL"); } }
			 * tableType.addAll(cdnrTableType);
			 */
			List<String> docType = reqDto.getDocType();
			List<String> einvGenerated = reqDto.getEInvGenerated();
			List<String> ewbGenerated = reqDto.getEWbGenerated();
			List<String> doclist = new ArrayList<>();
			List<String> tablelist = new ArrayList<>();
			List<String> einvlist = new ArrayList<>();
			List<String> ewblist = new ArrayList<>();

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

			if (!CollectionUtils.isEmpty(einvGenerated)) {
				for (String einv : einvGenerated) {
					einvlist.add(einv);
				}

				entity.setEInvGenerated(
						einvlist.stream().collect(Collectors.joining(",")));
			}

			if (!CollectionUtils.isEmpty(ewbGenerated)) {
				for (String ewb : ewbGenerated) {
					ewblist.add(ewb);
				}

				entity.setEWbGenerated(
						ewblist.stream().collect(Collectors.joining(",")));
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

	private Long getDerivedRetPeriod(String taxPeriod) {
		return Long.valueOf(
				taxPeriod.substring(2).concat(taxPeriod.substring(0, 2)));
	}

}
