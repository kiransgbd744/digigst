/**
 * 
 */
package com.ey.advisory.app.service.report.gstr7;

import static com.ey.advisory.common.FormatValidationUtil.isPresent;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Cell;
import com.aspose.cells.Cells;
import com.aspose.cells.Font;
import com.aspose.cells.SaveFormat;
import com.aspose.cells.Style;
import com.aspose.cells.Workbook;
import com.aspose.cells.Worksheet;
import com.ey.advisory.admin.data.entities.client.EntityInfoEntity;
import com.ey.advisory.app.data.repositories.client.EntityInfoRepository;
import com.ey.advisory.app.data.services.anx1.Gstr7ReviewSummaryFetchService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.config.ConfigConstants;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */

@Slf4j
@Component("Gstr7EntityLevelReportDownloadServiceImpl")
public class Gstr7EntityLevelReportDownloadServiceImpl implements Gstr7EntityLevelReportDownloadService {

	@Autowired
	@Qualifier("Gstr7ReviewSummaryFetchService")
	Gstr7ReviewSummaryFetchService gstr7ReviewSummaryFetchService;
	
	@Autowired
	@Qualifier("entityInfoRepository")
	EntityInfoRepository entityInfoRepo;
	
	@Autowired
	CommonUtility commonUtility;
	
	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public Workbook getGstr7ReportData(Long entityId, List<String> gstins, String taxPeriod) {

		Workbook workbook = new Workbook();

		List<Gstr7EntitySmryDto> response = getReviewSummary(gstins, taxPeriod);
		
		Optional<EntityInfoEntity> optional = entityInfoRepo
				.findById(entityId);
		EntityInfoEntity entity = optional.get();
		String entityName = entity.getEntityName();

		workbook = writeToExcel(response, entityName, taxPeriod);
		

		return workbook;
	}
		
	
	
	private List<Gstr7EntitySmryDto> getReviewSummary(List<String> gstins, String taxPeriod) {
		
		String gstinList = String.join(",", gstins);

		StoredProcedureQuery storedProc = entityManager
				.createStoredProcedureQuery("USP_GSTR7_ENTITY_LEVEL_SMRY");
		
		storedProc.registerStoredProcedureParameter("P_GSTIN_LIST", String.class, ParameterMode.IN);
		storedProc.setParameter("P_GSTIN_LIST", gstinList);

		storedProc.registerStoredProcedureParameter("P_TAX_PERIOD", String.class, ParameterMode.IN);
		storedProc.setParameter("P_TAX_PERIOD", taxPeriod);

		@SuppressWarnings("unchecked")
		List<Object[]> resultList = storedProc.getResultList();
		
		List<Gstr7EntitySmryDto> respList = resultList.stream()
				.map(o -> convertDto(o))
				.collect(Collectors.toCollection(ArrayList::new));
		
		
		
		if (org.apache.commons.collections.CollectionUtils
				.isNotEmpty(respList)) {
			respList.forEach(dto -> {
				dto.setDiffTotalAmount(dto.getAspTotalAmount()
						.subtract(dto.getGstnTotalAmount()));
				dto.setDiffIgst(dto.getAspIgst().subtract(dto.getGstnIgst()));
				dto.setDiffCgst(dto.getAspCgst().subtract(dto.getGstnCgst()));
				dto.setDiffSgst(dto.getAspSgst().subtract(dto.getGstnSgst()));
				dto.setDiffCount(dto.getAspCount() - (dto.getGstnCount()));
			});
		}
		
		List<String> dataGstinList = new ArrayList<>();
		respList.forEach(dto -> dataGstinList.add(dto.getSection()+dto.getGstin()));
		
		for(String gstin : gstins){
		if (!dataGstinList.contains("Table-3"+gstin)) {
			Gstr7EntitySmryDto dto1 = new Gstr7EntitySmryDto();
			dto1.setSection("Table-3");
			dto1.setSectionDesc("Original details");
			dto1.setAspTotalAmount(BigDecimal.ZERO);
			dto1.setAspCount(0);
			dto1.setAspIgst(BigDecimal.ZERO);
			dto1.setAspCgst(BigDecimal.ZERO);
			dto1.setAspSgst(BigDecimal.ZERO);
			dto1.setGstnCount(0);
			dto1.setGstnTotalAmount(BigDecimal.ZERO);
			dto1.setGstnIgst(BigDecimal.ZERO);
			dto1.setGstnSgst(BigDecimal.ZERO);
			dto1.setGstnCgst(BigDecimal.ZERO);
			dto1.setDiffCount(0);
			dto1.setDiffTotalAmount(BigDecimal.ZERO);
			dto1.setDiffIgst(BigDecimal.ZERO);
			dto1.setDiffSgst(BigDecimal.ZERO);
			dto1.setDiffCgst(BigDecimal.ZERO);
			dto1.setGstin(gstin);

			respList.add(dto1);
		}
		if (!dataGstinList.contains("Table-4"+gstin)) {
			Gstr7EntitySmryDto dto1 = new Gstr7EntitySmryDto();
			dto1.setSection("Table-4");
			dto1.setSectionDesc("Amendment details");
			dto1.setAspTotalAmount(BigDecimal.ZERO);
			dto1.setAspCount(0);
			dto1.setAspIgst(BigDecimal.ZERO);
			dto1.setAspCgst(BigDecimal.ZERO);
			dto1.setAspSgst(BigDecimal.ZERO);
			dto1.setGstnCount(0);
			dto1.setGstnTotalAmount(BigDecimal.ZERO);
			dto1.setGstnIgst(BigDecimal.ZERO);
			dto1.setGstnSgst(BigDecimal.ZERO);
			dto1.setGstnCgst(BigDecimal.ZERO);
			dto1.setDiffCount(0);
			dto1.setDiffTotalAmount(BigDecimal.ZERO);
			dto1.setDiffIgst(BigDecimal.ZERO);
			dto1.setDiffSgst(BigDecimal.ZERO);
			dto1.setDiffCgst(BigDecimal.ZERO);
			dto1.setGstin(gstin);


			respList.add(dto1);
		}
		}		
		
		
		return respList;
	}



	private Workbook writeToExcel(
			List<Gstr7EntitySmryDto> responseList, String entityName,
			String taxPeriod) {
		Workbook workbook = null;
		int startRow = 6;
		int startcolumn = 1;
		boolean isHeaderRequired = false;
		if (LOGGER.isDebugEnabled()) {
			String msg = "Begin Gstr7EntityLevelReportDownloadServiceImpl.writeToExcel "
					+ "errorList Size = " + responseList.size();
			LOGGER.debug(msg);
		}

		if (responseList != null && !responseList.isEmpty()) {

			String[] invoiceHeaders = commonUtility
					.getProp("gstr7.entity.level.summary.report.header").split(",");

			workbook = commonUtility.createWorkbookWithExcelTemplate(
					"ReportTemplates", "GSTR7_Entity level summary.xlsx");
			if (LOGGER.isDebugEnabled()) {
				String msg = "Gstr7EntityLevelReportDownloadServiceImpl.writeToExcel "
						+ "workbook created writing data to the workbook";
				LOGGER.debug(msg);
			}

			Cells reportCells = workbook.getWorksheets().get(0).getCells();

			Worksheet sheet = workbook.getWorksheets().get(0);

			Cell cell = sheet.getCells().get("B3");
			
			
			LocalDateTime istDateTimeFromUTC = EYDateUtil
					.toISTDateTimeFromUTC(LocalDateTime.now());
			
			String[] dateTime = istDateTimeFromUTC.toString().split("T");

			cell.setValue("ENTITY NAME- " + entityName + "| Date - "
					+ dateTime[0] + " | Time - " + dateTime[1].substring(0, 8)
					+ "| Tax Period - " + taxPeriod);
			Style style = workbook.createStyle();
			Font font = style.getFont();
			font.setBold(true);
			font.setSize(12);
			
			List<Gstr7EntitySmryStringDto> finalRespList = responseList.stream().map(o -> convertToStringDto(o))
			.collect(Collectors.toCollection(ArrayList::new));
			
			
			Map<String, List<Gstr7EntitySmryStringDto>> respMap = finalRespList.stream()
					.collect(Collectors.groupingBy(o -> o.getGstin()));
			
			for(Map.Entry map : respMap.entrySet()){
				
				startRow = 6;
				List<Gstr7EntitySmryStringDto> sortList = respMap
						.get(map.getKey()).stream()
						.sorted((o1, o2) -> o1.getSection()
								.compareTo(o2.getSection()))
						.collect(Collectors.toList());

				reportCells.importCustomObjects(sortList, invoiceHeaders,
						isHeaderRequired, startRow, startcolumn,
						sortList.size(), true, "yyyy-mm-dd", false);

				startRow = startRow + sortList.size() + 2;

			}

			try {
				if (LOGGER.isDebugEnabled()) {
					String msg = "Gstr7EntityLevelReportDownloadServiceImpl.writeToExcel "
							+ "saving workbook";
					LOGGER.debug(msg);
				}
				workbook.save(ConfigConstants.GSTR7_SUMMARY_REPORT,
						SaveFormat.XLSX);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Workbook has been generated successfully with the"
									+ " error response list in the directory : %s",
							workbook.getAbsolutePath());
				}
			} catch (Exception e) {
				String msg = String.format(
						"Exception occured while "
								+ "saving excel sheet into folder, %s ",
						e.getMessage());
				LOGGER.error(msg);
				throw new AppException(e.getMessage(), e);
			}

		} else {
			throw new AppException("No records found, cannot generate report");
		}
		return workbook;
	}
	
	private Gstr7EntitySmryStringDto convertToStringDto(Gstr7EntitySmryDto o) {
		Gstr7EntitySmryStringDto dto = new Gstr7EntitySmryStringDto();
		
		
		dto.setSection(o.getSection());
		dto.setSectionDesc(o.getSectionDesc());
		dto.setGstin(o.getGstin());
		
		dto.setAspCount(o.getAspCount());
		dto.setAspTotalAmount(appendDecimalDigit(o.getAspTotalAmount()));
		dto.setAspIgst(appendDecimalDigit(o.getAspIgst()));
		dto.setAspCgst(appendDecimalDigit(o.getAspCgst()));
		dto.setAspSgst(appendDecimalDigit(o.getAspSgst()));
		
		dto.setGstnCount(o.getGstnCount());
		dto.setGstnTotalAmount(appendDecimalDigit(o.getGstnTotalAmount()));
		dto.setGstnIgst(appendDecimalDigit(o.getGstnIgst()));
		dto.setGstnCgst(appendDecimalDigit(o.getGstnCgst()));
		dto.setGstnSgst(appendDecimalDigit(o.getGstnSgst()));
		
		dto.setDiffCount(o.getDiffCount());
		dto.setDiffTotalAmount(appendDecimalDigit(o.getDiffTotalAmount()));
		dto.setDiffIgst(appendDecimalDigit(o.getDiffIgst()));
		dto.setDiffCgst(appendDecimalDigit(o.getDiffCgst()));
		dto.setDiffSgst(appendDecimalDigit(o.getDiffSgst()));
		
		return dto;
	}



	private Gstr7EntitySmryDto convertDto(Object[] data) {

		Gstr7EntitySmryDto dto = new Gstr7EntitySmryDto();

		dto.setSection(String.valueOf(data[0]));
		
		if(dto.getSection().equalsIgnoreCase("Table-3")){
			dto.setSectionDesc("Original details");
		}else{
			dto.setSectionDesc("Amendment details");
		}

		dto.setGstin(data[1] != null ? data[1].toString() : "");

		Integer count = (GenUtil.getBigInteger(data[2])).intValue();
		dto.setAspCount(count);
		dto.setAspTotalAmount((BigDecimal) data[3]);
		dto.setAspIgst((BigDecimal) data[4]);
		dto.setAspCgst((BigDecimal) data[5]);
		dto.setAspSgst((BigDecimal) data[6]);
		dto.setGstnCount((Integer) data[7]);
		dto.setGstnTotalAmount((BigDecimal) data[8]);
		dto.setGstnIgst((BigDecimal) data[9]);
		dto.setGstnCgst((BigDecimal) data[10]);
		dto.setGstnSgst((BigDecimal) data[11]);

		return dto;

	}
	
	private String appendDecimalDigit(BigDecimal value) {

		try {
			if (isPresent(value)) {
					String val = value.setScale(2, BigDecimal.ROUND_HALF_UP)
							.toPlainString();

					String[] s = val.split("\\.");
					if (s.length == 2) {
						if (s[1].length() == 1) {
							val =  (s[0] + "." + s[1] + "0");
							
							return val;
						} else {
							
							return val;
						}
					} else {
						val = (val + ".00");
						
						return val;
					}
				
			}
		} catch (Exception e) {
			LOGGER.error("Gstr7EntityLevelReportDownloadServiceImpl"
					+ " AppendDecimalDigit method {}",
					value);
			return value != null ? value.toString() : null;
		}
		LOGGER.error("Gstr7EntityLevelReportDownloadServiceImpl "
				+ "AppendDecimalDigit method val : {} before final return ",
				value.toString());
		return value.toString();
	}
}
