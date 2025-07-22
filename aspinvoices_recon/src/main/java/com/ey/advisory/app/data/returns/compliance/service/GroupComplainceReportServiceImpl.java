/**
 * 
 */
package com.ey.advisory.app.data.returns.compliance.service;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.map.HashedMap;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.aspose.cells.Cells;
import com.aspose.cells.FileFormatType;
import com.aspose.cells.LoadOptions;
import com.aspose.cells.MemorySetting;
import com.aspose.cells.Workbook;
import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.admin.data.repositories.master.StatecodeRepository;
import com.ey.advisory.admin.services.onboarding.DataSecurityService;
import com.ey.advisory.app.data.repositories.client.EntityInfoRepository;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.ProcessedRecordsCommonSecParam;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.core.dto.Gstr1ProcessedRecordsReqDto;
import com.ey.advisory.core.dto.Gstr2aProcessedDataRecordsReqDto;
import com.ey.advisory.core.dto.ReturnStusFilingDto;
import com.ey.advisory.core.dto.ReturnStusFilingDto.ReturnPeriodDto;
import com.ey.advisory.core.search.PageRequest;

/**
 * @author Shashikant.Shukla
 *
 */

@Service("GroupComplainceReportServiceImpl")
public class GroupComplainceReportServiceImpl
		implements GroupComplainceReportService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(GroupComplainceReportServiceImpl.class);

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	@Qualifier("CompienceHistoryServiceImpl")
	private CompienceHistoryServiceImpl complienceSummery;

	@Autowired
	@Qualifier("StatecodeRepositoryMaster")
	private StatecodeRepository statecodeRepository;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstinInfoRepository;

	@Autowired
	@Qualifier("dataSecurityService")
	private DataSecurityService dataSecurityService;
	

	@Autowired
	@Qualifier("entityInfoRepository")
	private EntityInfoRepository entityInfoRepository;

	@Autowired
	@Qualifier("ProcessedRecordsCommonSecParam")
	private ProcessedRecordsCommonSecParam processedRecordsCommonSecParam;

	public Workbook findComplaince(
			GroupComplianceHistoryDataRecordsReqDto criteria,
			PageRequest pageReq) {

		Workbook workbook = new Workbook();
		int startRow = 1;
		int startcolumn = 0;
		boolean isHeaderRequired = false;

		List<ReturnComplianceDto> responseFromView = dataresultset(criteria);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Complaince Report response" + responseFromView);
		}
		if (responseFromView != null && responseFromView.size() > 0) {
			String[] invoiceHeaders = commonUtility
					.getProp("grpcomp.report.headers").split(",");
			workbook = createWorkbookWithExcelTemplate("ReportTemplates",
					"GroupComplianceReport.xlsx");
			Cells errorDumpCells = workbook.getWorksheets().get(0).getCells();
			errorDumpCells.importCustomObjects(responseFromView, invoiceHeaders,
					isHeaderRequired, startRow, startcolumn,
					responseFromView.size(), true, "yyyy-mm-dd", false);
		}
		return workbook;
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

	public List<ReturnComplianceDto> dataresultset(
			GroupComplianceHistoryDataRecordsReqDto groupComplianceHistoryDataRecordsReqDto) {
		List<ReturnComplianceDto> responseFromView = new ArrayList<>();

		List<Gstr2aProcessedDataRecordsReqDto> groupComplianceProcessed = new ArrayList<>();
		List<Long> entityIdList = new ArrayList<>();
		if (!groupComplianceHistoryDataRecordsReqDto.getDataSecAttrs()
				.get("entityIds").isEmpty()) {
			entityIdList = groupComplianceHistoryDataRecordsReqDto
					.getDataSecAttrs().get("entityIds");
		} else {
			User user = SecurityContext.getUser();
			String userName = user.getUserPrincipalName();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Anx2GetDataSecurityController User Name: {}",
						userName);
			}
			Map<Long, Map<String, List<Pair<Long, String>>>> attributeMap = user
					.getAttributeMap();
			entityIdList = new ArrayList<>(attributeMap.keySet());

		}
		for (Long entityId : entityIdList) {
			Gstr2aProcessedDataRecordsReqDto req = new Gstr2aProcessedDataRecordsReqDto();
			req.setEntity(entityId.toString());
			req.setFinancialYear(
					groupComplianceHistoryDataRecordsReqDto.getFinancialYear());
			req.setReturnType(
					groupComplianceHistoryDataRecordsReqDto.getReturnType());
			Map<String, List<String>> dataSecAttrs = new HashedMap<>();
			List<String> gstinList = getGstins(new ArrayList<>(), entityId,
					groupComplianceHistoryDataRecordsReqDto.getReturnType(),
					dataSecAttrs);
			dataSecAttrs.put("GSTIN", gstinList);
			req.setDataSecAttrs(dataSecAttrs);
			groupComplianceProcessed.add(req);
		}

		List<ReturnStusFilingDto> statusdto = new ArrayList<>();

		for (Gstr2aProcessedDataRecordsReqDto complianceHistoryDataRecordsReqDto : groupComplianceProcessed) {
			if (!complianceHistoryDataRecordsReqDto.getDataSecAttrs()
					.get("GSTIN").isEmpty()) {
				List<ReturnStusFilingDto> dto = complienceSummery
						.findcomplienceSummeryRecords(
								complianceHistoryDataRecordsReqDto);
				for (ReturnStusFilingDto filingDto : dto) {
					filingDto.setEntityId(Long.valueOf(
							complianceHistoryDataRecordsReqDto.getEntity()));
				}
				statusdto.addAll(dto);
			}
		}

		statusdto.stream().forEach(dto -> {
			String gstin = dto.getGstin();
			String entityName = entityInfoRepository.findEntityNameByEntityId(
					Long.valueOf(dto.getEntityId()));
			String stateCode = gstin.substring(0, 2);
			String stateName = statecodeRepository
					.findStateNameByCode(stateCode);
			String registrationType = gstinInfoRepository
					.findByGstinAndIsDeleteFalse(gstin).getRegistrationType();

			List<ReturnPeriodDto> returnperiods = dto.getReturnperiods();

			returnperiods.stream().forEach(dto1 -> {

				ReturnComplianceDto obj = new ReturnComplianceDto();

				obj.setEntityName(entityName);
				obj.setgStin(gstin);
				String datetime = dto1.getTime();
				if (datetime != null) {
					LOGGER.debug("Datetime {}",datetime);
					String[] datetime1 = datetime.split(" ");
					String date = datetime1[0];
					obj.setFillingSubDate(date);
				}
				obj.setRegistrationType(registrationType);
				obj.setReturnType(groupComplianceHistoryDataRecordsReqDto
						.getReturnType());
				obj.setStateName(stateName);
				if(groupComplianceHistoryDataRecordsReqDto
						.getReturnType().equalsIgnoreCase("ITC04")){
					obj.setTaxPeriod(itc04TaxPeriod(dto1.getMonth()));
				} else{
					obj.setTaxPeriod(dto1.getMonth());
				}
				obj.setArnNo(dto1.getArnNo());
				obj.setStatus(dto1.getStatus());

				responseFromView.add(obj);

			});

		});
		return responseFromView;
	}

	public String itc04TaxPeriod(String month){
		String taxPeriod = "";
		if(month.startsWith("13")){
			taxPeriod = month.replace("13", "Q1");
		} else if(month.startsWith("14")){
			taxPeriod = month.replace("14", "Q2");
		} else if(month.startsWith("15")){
			taxPeriod = month.replace("15", "Q3");
		} else if(month.startsWith("16")){
			taxPeriod = month.replace("16", "Q4");
		} else if(month.startsWith("17")){
			taxPeriod = month.replace("17", "H1");
		} else if(month.startsWith("18")){
			taxPeriod = month.replace("18", "H2");
		} else{
			taxPeriod = month;
		}
			
		return taxPeriod;	
	}
	private List<String> getGstins(List<String> gstinList, Long entityId,
			String returnType, Map<String, List<String>> dataSecAttrs) {
		if (!gstinList.isEmpty())
			return gstinList;
		if ("GSTR6".equalsIgnoreCase(returnType)) {
			Gstr1ProcessedRecordsReqDto dto = new Gstr1ProcessedRecordsReqDto();
			dto.setDataSecAttrs(dataSecAttrs);
			dto.setEntityId(Arrays.asList(entityId));
			Gstr1ProcessedRecordsReqDto reqDto = processedRecordsCommonSecParam
					.setGstr6DataSecuritySearchParams(dto);
			return reqDto.getDataSecAttrs().get(OnboardingConstant.GSTIN);
		}
		if ("GSTR7".equalsIgnoreCase(returnType)) {
			Gstr1ProcessedRecordsReqDto dto = new Gstr1ProcessedRecordsReqDto();
			dto.setDataSecAttrs(dataSecAttrs);
			dto.setEntityId(Arrays.asList(entityId));
			Gstr1ProcessedRecordsReqDto reqDto = processedRecordsCommonSecParam
					.setGstr7DataSecuritySearchParams(dto);
			return reqDto.getDataSecAttrs().get(OnboardingConstant.GSTIN);
		}
		// gstr1,gstr3b,gstr9,itc04
		Gstr1ProcessedRecordsReqDto dto = new Gstr1ProcessedRecordsReqDto();
		dto.setDataSecAttrs(dataSecAttrs);
		dto.setEntityId(Arrays.asList(entityId));
		Gstr1ProcessedRecordsReqDto reqDto = processedRecordsCommonSecParam
				.setGstr1DataSecuritySearchParams(dto);

		return reqDto.getDataSecAttrs().get(OnboardingConstant.GSTIN);

	}
}