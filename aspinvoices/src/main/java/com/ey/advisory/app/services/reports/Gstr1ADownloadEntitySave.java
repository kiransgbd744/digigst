package com.ey.advisory.app.services.reports;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.app.data.entities.client.FileStatusDownloadReportEntity;
import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.app.downloadreports.DownloadReportsConstant;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.ReportStatusConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("Gstr1ADownloadEntitySave")
public class Gstr1ADownloadEntitySave {

	@Autowired
	FileStatusDownloadReportRepository fileStatusDownloadReportRepo;
	
	public Long saveDownloadEntity(String jsonString, Gstr1ReviwSummReportsReqDto reqDto) {

		FileStatusDownloadReportEntity entity = new FileStatusDownloadReportEntity();

		if (LOGGER.isDebugEnabled()) {
			String msg = "Setting request data to entity to SaveorPersist";
			LOGGER.debug(msg);
		}

		setDataToEntity(entity, reqDto);

		FileStatusDownloadReportEntity entityObj = fileStatusDownloadReportRepo
				.save(entity);

		Long id = entityObj.getId();
		
		return id;

	}

	private void setDataToEntity(FileStatusDownloadReportEntity entity,
			Gstr1ReviwSummReportsReqDto reqDto) {
		
		String GSTIN = null;
		Map<String, List<String>> dataSecAttrs = reqDto.getDataSecAttrs();
		List<String> gstinList = null;
		
		if (!dataSecAttrs.isEmpty()) {
			for (String key : dataSecAttrs.keySet()) {

				if (key.equalsIgnoreCase(OnboardingConstant.GSTIN)) {
					GSTIN = key;
					if (!dataSecAttrs.get(OnboardingConstant.GSTIN).isEmpty()
							&& dataSecAttrs.get(OnboardingConstant.GSTIN).size() > 0) {
						gstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
					}
				}
			}
		}

		User user = SecurityContext.getUser();
		String userName = user.getUserPrincipalName();

		if (reqDto.getTaxDocType() != null && reqDto.getTaxDocType()
				.equalsIgnoreCase(DownloadReportsConstant.EXPORTA)) {
			entity.setDocType("EXPORTA");
		}else{
			entity.setDocType(reqDto.getTaxDocType());
		}
		
		entity.setEntityId(reqDto.getEntityId().get(0));
		entity.setReportType(reqDto.getReportType());
		entity.setCreatedBy(userName);
		entity.setCreatedDate(LocalDateTime.now());
		entity.setReportStatus(ReportStatusConstants.INITIATED);
		entity.setReportCateg("ReviewSummary");

		entity.setDataType(reqDto.getDataType());
		//entity.setDataType("Outward_1A");

		entity.setTaxPeriod(reqDto.getTaxperiod());

		Long derivedRetPeriod = Long.valueOf(GenUtil
				.convertTaxPeriodToInt(reqDto.getTaxperiod()).toString());
		entity.setDerivedRetPeriod(derivedRetPeriod);
		//entity.setGstins((Clob) reqDto.getDataSecAttrs().get("GSTIN"));
		
		if (GSTIN != null && !GSTIN.isEmpty()) {
			if (gstinList != null && !gstinList.isEmpty() && gstinList.size() > 0) {
				
				entity.setGstins(GenUtil.convertStringToClob(
						String.join(",", gstinList)));

				
			}
		}


	}
	
}
