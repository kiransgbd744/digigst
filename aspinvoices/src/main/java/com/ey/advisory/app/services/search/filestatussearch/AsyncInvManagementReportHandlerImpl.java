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
import com.ey.advisory.app.services.docs.einvoice.EinvoiceBasicDocSearchDataSecParams;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.ReportStatusConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.core.dto.EInvoiceDocSearchReqDto;
import com.google.common.base.Strings;

/**
 * @author Laxmi.Salukuti
 *
 */

@Component("AsyncInvManagementReportHandlerImpl")
public class AsyncInvManagementReportHandlerImpl
		implements AsyncInvManagementReportHandler {

	@Autowired
	@Qualifier("EinvoiceBasicDocSearchDataSecParams")
	private EinvoiceBasicDocSearchDataSecParams basicDocSearchDataSecParams;

	@Autowired
	FileStatusRepository fileStatusRepo;

	@Override
	public void setDataToEntity(FileStatusDownloadReportEntity entity,
			EInvoiceDocSearchReqDto reqDto) {

		User user = SecurityContext.getUser();
		String userName = user.getUserPrincipalName();

		entity.setCreatedBy(userName);
		entity.setCreatedDate(
				EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
		entity.setReportStatus(ReportStatusConstants.INITIATED);

		EInvoiceDocSearchReqDto setDataSecurity = basicDocSearchDataSecParams
				.setDataSecuritySearchParams(reqDto);

		entity.setEntityId(setDataSecurity.getEntityId().get(0));
		entity.setReportCateg("InvoiceManagement");
		entity.setDataType("Outward");
		
		if (!Strings.isNullOrEmpty(reqDto.getProcessingStatus())) {
			if ("P".equalsIgnoreCase(reqDto.getProcessingStatus())) {
				entity.setReportType("ProcessedRecords");
			} else if ("E".equalsIgnoreCase(reqDto.getProcessingStatus())) {
				entity.setReportType("ErrorRecords");
			} else if ("I".equalsIgnoreCase(reqDto.getProcessingStatus())) {
				entity.setReportType("ProcessedInfoData");
			}
		} else {
			entity.setReportType("data");
		}

		entity.setDocDateFrom(reqDto.getDocFromDate());
		entity.setDocDateTo(reqDto.getDocToDate());
		entity.setTaxPeriodFrom(reqDto.getReturnFrom());
		if (reqDto.getReturnFrom() != null) {
			Long derivedRetPeriodFrom = Long.valueOf(
					GenUtil.convertTaxPeriodToInt(reqDto.getReturnFrom()));
			entity.setDerivedRetPeriodFrom(derivedRetPeriodFrom);
		}
		entity.setTaxPeriodTo(reqDto.getReturnTo());
		if (reqDto.getReturnTo() != null) {
			Long derivedRetPeriodTo = Long.valueOf(
					GenUtil.convertTaxPeriodToInt(reqDto.getReturnTo()));
			entity.setDerivedRetPeriodFromTo(derivedRetPeriodTo);
		}
		if (reqDto.getReceivFromDate() != null) {
			entity.setRequestFromDate(
					reqDto.getReceivFromDate().atStartOfDay());
		}
		if (reqDto.getReceivToDate() != null) {
			entity.setRequestToDate(reqDto.getReceivToDate().atStartOfDay());
		}
		entity.setDocDateFrom(reqDto.getDocFromDate());
		entity.setDocDateTo(reqDto.getDocToDate());
		entity.setDocType(convertToQueryFormat(reqDto.getDocTypes()));
		List<String> docNo = reqDto.getDocNums();
		docNo.replaceAll(String::toUpperCase);
		entity.setDocNum(convertToQueryFormatDocNum(docNo));
		/*List<String> accVoucherNo = reqDto.getAccVoucherNum();
		accVoucherNo.replaceAll(String::toUpperCase);
		entity.setAccVoucherNo(convertToQueryFormatDocNum(accVoucherNo));*/
		
		entity.setIrnNum(reqDto.getIrnNum());
		entity.setCounterPartyGstin(reqDto.getCounterPartyGstins());
		entity.setProcessingStatus(reqDto.getProcessingStatus());
		entity.setFileId(reqDto.getFileId());
		entity.setDataOriginTypeCode(reqDto.getDataOriginTypeCode());
		if (reqDto.getEwbNo() != null) {
			entity.setEWbGenerated(reqDto.getEwbNo().toString());
		}
		entity.setRefId(reqDto.getGstnRefId());
		entity.setShowGstnData(reqDto.isShowGstnData());
		entity.setGstReturnsStatus(
				convertToQueryFormat(reqDto.getGstReturnsStatus()));

		List<Long> ewbStatus = reqDto.getEwbStatus();
		entity.setEwbStatus(convertToNumList(ewbStatus));

		List<Long> einvStatus = reqDto.getEinvStatus();
		entity.setInvStatus(convertToNumList(einvStatus));

		List<Long> errorPoint = reqDto.getEwbErrorPoint();
		entity.setEwbErrorPoint(convertToNumList(errorPoint));

		entity.setSubSupplyType(
				convertToQueryFormat(reqDto.getSubSupplyType()));
		entity.setSupplyType(convertToQueryFormat(reqDto.getSupplyType()));
		entity.setTransType(reqDto.getTransType());
		entity.setPostingDate(reqDto.getPostingDate());
		entity.setTransporterId(reqDto.getTransporterID());
		entity.setEwbValidUpto(reqDto.getEwbValidUpto());
		Map<String, List<String>> dataSec = setDataSecurity.getDataSecAttrs();
		entity.setGstins(GenUtil.convertStringToClob(
				convertToQueryFormat(dataSec.get(OnboardingConstant.GSTIN))));
		entity.setDisChannel(
				convertToQueryFormat(dataSec.get(OnboardingConstant.DC)));
		entity.setDivision(
				convertToQueryFormat(dataSec.get(OnboardingConstant.DIVISION)));
		entity.setLocation(
				convertToQueryFormat(dataSec.get(OnboardingConstant.LOCATION)));
		entity.setPlantCode(
				convertToQueryFormat(dataSec.get(OnboardingConstant.PLANT)));
		entity.setProfitCenter(
				convertToQueryFormat(dataSec.get(OnboardingConstant.PC)));
		entity.setPurchaseOrg(
				convertToQueryFormat(dataSec.get(OnboardingConstant.PO)));
		entity.setSalesOrg(
				convertToQueryFormat(dataSec.get(OnboardingConstant.SO)));
		entity.setSubdivision(
				convertToQueryFormat(dataSec.get(OnboardingConstant.DIVISION)));
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
	
	private String convertToQueryFormatDocNum(List<String> list)
	{
		
		String queryData = null;
		if(list == null || list.isEmpty())
			return null;
		
		if(list.size()==1)
		return list.get(0);
		
		queryData = "'"+list.get(0);
		for (int i = 1; i < list.size(); i++) {
			queryData += "," + list.get(i);
		}
		queryData+="'";
		
		return queryData;
	}

	private String convertToNumList(List<Long> list) {

		Long queryData = null;

		if (list == null || list.isEmpty())
			return null;

		queryData = list.get(0);
		String quryDataString = queryData.toString();
		for (int i = 1; i < list.size(); i++) {
			quryDataString += "," + list.get(i);
		}
		return quryDataString;
	}

	private Long getDerivedRetPeriod(String taxPeriod) {
		return Long.valueOf(
				taxPeriod.substring(2).concat(taxPeriod.substring(0, 2)));
	}
}