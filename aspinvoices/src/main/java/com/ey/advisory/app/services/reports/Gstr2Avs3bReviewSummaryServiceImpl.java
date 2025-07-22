package com.ey.advisory.app.services.reports;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import com.aspose.cells.FileFormatType;
import com.aspose.cells.LoadOptions;
import com.aspose.cells.MemorySetting;
import com.aspose.cells.Workbook;
import com.ey.advisory.app.data.repositories.client.EntityInfoRepository;
import com.ey.advisory.app.data.services.anx1.Gstr2AVsGstr3bDownloadReviewSummaryFetchService;
import com.ey.advisory.app.docs.dto.Gstr2AVss3bReviewSummaryScreenRespDto;
import com.ey.advisory.app.docs.dto.Gstr2AVssGstr3bReviewSummaryRespDto;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.core.dto.Gstr1VsGstr3bProcessSummaryReqDto;

import lombok.extern.slf4j.Slf4j;

/**
 * @author siva K
 * 
 */

@Service("Gstr2Avs3bReviewSummaryServiceImpl")
@Slf4j
public class Gstr2Avs3bReviewSummaryServiceImpl {
	@Autowired
	CommonUtility commonUtility;
	@Autowired
	@Qualifier("Gstr2AVsGstr3bDownloadReviewSummaryFetchService")
	Gstr2AVsGstr3bDownloadReviewSummaryFetchService gstr2AVsGstr3bDownloadReviewSummaryFetchService;
	@Autowired
	@Qualifier("entityInfoRepository")
	EntityInfoRepository entityInfoRepository;
	public static final BigDecimal convert = new BigDecimal("0.0");
	public static final BigDecimal convert1 = new BigDecimal("0.00");
	public Workbook findReviewSummaryData(
			Gstr1VsGstr3bProcessSummaryReqDto criteria) {
		String entityName = entityInfoRepository.findEntityNameByEntityId(
				criteria.getEntityId().stream().findFirst().get());
		Workbook workbook = new Workbook();
		List<Gstr2AVssGstr3bReviewSummaryRespDto> response = gstr2AVsGstr3bDownloadReviewSummaryFetchService
				.loadDataByCriteria(criteria);
		List<Gstr2AVss3bReviewSummaryScreenRespDto> responseFromViewProcess = convertProcessRecordsToScreenDtos(
				response);
		workbook = createWorkbookWithExcelTemplate("ReportTemplates",
				"GSTR2AvsGSTR3B_GSTIN_FromTaxPeriod_ToTaxPeriod_TimeofGeneration.xlsx");
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("gstr2Avs3b Review Summary data response" + response);
		}
		if (responseFromViewProcess != null
				&& responseFromViewProcess.size() > 0) {
			try {
				Gstr2Avss3bReviewSummaryServiceUtil.downloadGstrVs3bReport(
						workbook, responseFromViewProcess, criteria,
						entityName);
			} catch (Exception e) {
				LOGGER.error(e.getMessage());
			}
		}

		return workbook;

	}

	private List<Gstr2AVss3bReviewSummaryScreenRespDto> convertProcessRecordsToScreenDtos(
			List<Gstr2AVssGstr3bReviewSummaryRespDto> response) {
		List<Gstr2AVss3bReviewSummaryScreenRespDto> dtos = new ArrayList<Gstr2AVss3bReviewSummaryScreenRespDto>();
		response.stream().forEach(dto -> {
			Gstr2AVss3bReviewSummaryScreenRespDto screenDto = new Gstr2AVss3bReviewSummaryScreenRespDto();
			screenDto.setGstin(dto.getGstin());
			screenDto.setSupplies(String.valueOf(dto.getSupplies()));
			screenDto.setFormula(String.valueOf(dto.getFormula()));
			BigDecimal igst = dto.getIgst();
			BigDecimal cgst = dto.getCgst();
			BigDecimal sgst = dto.getSgst();
			BigDecimal cess = dto.getCess();
			if(convert.compareTo(igst)==0 || convert1.compareTo(igst)==0){
				igst=BigDecimal.ZERO;	
			}
			if(convert.compareTo(sgst)==0 || convert1.compareTo(sgst)==0){
				sgst=BigDecimal.ZERO;	
			}
			if(convert.compareTo(cgst)==0 || convert1.compareTo(cgst)==0){
				cgst=BigDecimal.ZERO;	
			}
			if(convert.compareTo(cess)==0 || convert1.compareTo(cess)==0){
				cess=BigDecimal.ZERO;	
			}
			screenDto.setIgst(String.valueOf(igst));
			screenDto.setCgst(String.valueOf(cgst));
			screenDto.setSgst(String.valueOf(sgst));
			screenDto.setCess(String.valueOf(cess));
			screenDto.setTaxPeriod(dto.getTaxPeriod());
			
			dtos.add(screenDto);
		});
		return dtos;
	}

	private Workbook createWorkbookWithExcelTemplate(String folderName,
			String fileName) {
		Workbook workbook = null;
		try {
			ClassLoader classLoader = getClass().getClassLoader();
			URL template_Dir = classLoader.getResource(folderName + "/");
			String templatePath = template_Dir.getPath() + fileName;
			LoadOptions options = new LoadOptions(FileFormatType.XLSX);
			CommonUtility.setAsposeLicense();
			workbook = new Workbook(templatePath, options);
			workbook.getSettings()
					.setMemorySetting(MemorySetting.MEMORY_PREFERENCE);
		} catch (Exception ex) {
			LOGGER.error("Exception in creating workbook : ", ex);
		}
		return workbook;
	}
}
