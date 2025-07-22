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

/**
 * 
 * @author Anand3.M
 *
 */
@Component("AsyncGstr1EInvReportHandlerImpl")
public class AsyncGstr1EInvReportHandlerImpl implements AsyncReportHandler {

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

		if (ReportTypeConstants.GET_GSTR1_EINVOICE
				.equalsIgnoreCase(reportCateg)) {

			FileStatusReportDto setDataSecurity = basicCommonSecParamAsyncReports
					.setDataSecuritySearchParams(reqDto);

			entity.setEntityId(setDataSecurity.getEntityId().get(0));
			// entity.setTaxPeriod(reqDto.getTaxPeriod());
			entity.setDocDateFrom(reqDto.getDocDateFrom());
			entity.setDocDateTo(reqDto.getDocDateTo());
			List<String> tableType = reqDto.getTableType();
			List<String> tablelist = new ArrayList<>();
			if (!CollectionUtils.isEmpty(tableType)) {
				for (String table : tableType) {
					tablelist.add("'" + table + "'");
				}

				entity.setTableType(
						tablelist.stream().collect(Collectors.joining(",")));
			}

			Long derivedRetPeriodFrom = Long.valueOf(
					GenUtil.convertTaxPeriodToInt(reqDto.getReturnFrom()));
			entity.setDerivedRetPeriodFrom(derivedRetPeriodFrom);
			Long derivedRetPeriodTo = Long.valueOf(
					GenUtil.convertTaxPeriodToInt(reqDto.getReturnTo()));
			entity.setDerivedRetPeriodFromTo(derivedRetPeriodTo);

			Map<String, List<String>> dataSec = setDataSecurity
					.getDataSecAttrs();
			entity.setGstins(GenUtil.convertStringToClob(convertToQueryFormat(
					dataSec.get(OnboardingConstant.GSTIN))));
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
