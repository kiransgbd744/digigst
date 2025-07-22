package com.ey.advisory.app.gstr2.recon.summary;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Cell;
import com.aspose.cells.Cells;
import com.aspose.cells.SaveFormat;
import com.aspose.cells.Style;
import com.aspose.cells.StyleFlag;
import com.aspose.cells.Workbook;
import com.aspose.cells.Worksheet;
import com.ey.advisory.admin.data.entities.client.EntityInfoEntity;
import com.ey.advisory.app.asprecon.gstr2.initiaterecon.Gstr2InitiateReconLineStringItemDto;
import com.ey.advisory.app.data.entities.client.asprecon.Gstr2ReconConfigEntity;
import com.ey.advisory.app.data.repositories.client.EntityInfoRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2ReconConfigRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2ReconGstinRepository;
import com.ey.advisory.app.getr2.ap.recon.summary.Gstr2APAndNonAPReconSummaryDao;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.config.ConfigConstants;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Rajesh N K
 *
 */
@Slf4j
@Component("Gstr2ReconSummaryDownloadServiceImpl")
public class Gstr2ReconSummaryDownloadServiceImpl
		implements Gstr2ReconSummaryDownloadService {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("Gstr2ReconConfigRepository")
	private Gstr2ReconConfigRepository reconConfigRepo;

	@Autowired
	@Qualifier("Gstr2ReconGstinRepository")
	private Gstr2ReconGstinRepository gstinDetails;

	@Autowired
	@Qualifier("entityInfoRepository")
	EntityInfoRepository entityInfoRepo;

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	@Qualifier("Gstr2APAndNonAPReconSummaryDaoImpl")
	Gstr2APAndNonAPReconSummaryDao gstr2ReconDao;

	@Override
	public Workbook getReconSummaryDownload(Long configId, List<String> gstins,
			String toTaxPeriod, String fromTaxPeriod, String reconType,
			Long entityId, String toTaxPeriod_A2, String fromTaxPeriod_A2,
			String criteria) {

		Workbook workbook = null;
		String fromTaxPeriod2a = fromTaxPeriod_A2;
		String toTaxPeriod2a = toTaxPeriod_A2;
		String criteria1 = criteria;

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					" Inside Gstr2ReconSummaryServiceImpl.getReconSummary()"
							+ "method{} configId %d : ",
					configId);
		}

		String fromTaxPeriod2A = null;
		String toTaxPeriod2A = null;
		String fromTaxPeriodPR = null;
		String toTaxPeriodPR = null;

		if (configId != null && configId != 0) {

			Gstr2ReconConfigEntity findByConfigId = reconConfigRepo
					.findByConfigId(configId);

			fromTaxPeriod2A = findByConfigId.getFromTaxPeriod2A() != null
					? findByConfigId.getFromTaxPeriod2A().toString()
					: findByConfigId.getFromDocDate().toString();

			toTaxPeriod2A = findByConfigId.getToTaxPeriod2A() != null
					? findByConfigId.getToTaxPeriod2A().toString()
					: findByConfigId.getToDocDate().toString();

			fromTaxPeriodPR = findByConfigId.getFromTaxPeriodPR() != null
					? findByConfigId.getFromTaxPeriodPR().toString()
					: findByConfigId.getFromDocDate().toString();

			toTaxPeriodPR = findByConfigId.getToTaxPeriodPR() != null
					? findByConfigId.getToTaxPeriodPR().toString()
					: findByConfigId.getToDocDate().toString();

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						" findByConfigId() method{} configId %d,  "
								+ "fromTaxPeriod2A : %s, toTaxPeriod2A : %s ,  "
								+ "fromTaxPeriodPR : %s, toTaxPeriodPR : %s",
						configId, fromTaxPeriod2A, toTaxPeriod2A,
						fromTaxPeriodPR,
						toTaxPeriodPR);
			}

		}

		List<Gstr2ReconSummaryDto> response = gstr2ReconDao
				.findReconSummary(configId, gstins,
						GenUtil.convertTaxPeriodToInt(toTaxPeriod),
						GenUtil.convertTaxPeriodToInt(fromTaxPeriod),
						reconType,
						GenUtil.convertTaxPeriodToInt(toTaxPeriod_A2),
						GenUtil.convertTaxPeriodToInt(fromTaxPeriod_A2),
						criteria);
		
		if(!reconType.equalsIgnoreCase("EINVPR"))
		{
		response.remove(0);
		response.remove(2);
		response.remove(10);
		response.remove(12);
		response.remove(13);
		response.remove(17);
		}
		else
		{
			response.remove(0);
			response.remove(2);
			response.remove(10);
			response.remove(12);
			response.remove(13);
			
		}
		response.forEach(e -> {
			if (e.getPerticulasName().equalsIgnoreCase("Exact Match")
					|| e.getPerticulasName()
							.equalsIgnoreCase("Match With Tolerance")) {
				e.setPerticulas("Match");
			} else if (e.getPerticulasName().equalsIgnoreCase("Value Mismatch")
					|| e.getPerticulasName().equalsIgnoreCase("POS Mismatch")
					|| e.getPerticulasName()
							.equalsIgnoreCase("Doc Date Mismatch")
					|| e.getPerticulasName()
							.equalsIgnoreCase("Doc Type Mismatch")
					|| e.getPerticulasName()
							.equalsIgnoreCase("Doc No Mismatch I")
					|| e.getPerticulasName().equalsIgnoreCase("Multi-Mismatch")
					|| e.getPerticulasName()
							.equalsIgnoreCase("Doc No Mismatch II")
					|| e.getPerticulasName()
							.equalsIgnoreCase("Doc No & Doc Date Mismatch")) {
				e.setPerticulas("Mismatch");
			} else if (e.getPerticulasName().equalsIgnoreCase("Potential-I")
					|| e.getPerticulasName().equalsIgnoreCase("Potential-II")) {
				e.setPerticulas("Potential");
			} else if (e.getPerticulasName()
					.equalsIgnoreCase("Logical Match")) {
				e.setPerticulas("Logical");
			} else if (e.getPerticulasName().equalsIgnoreCase("Addition in PR")
					|| e.getPerticulasName()
							.equalsIgnoreCase("Addition in 2A")
					|| e.getPerticulasName()
							.equalsIgnoreCase("Addition in 2B") || e.getPerticulasName()
							.equalsIgnoreCase("Addition in Inward E-Inv")) {
				e.setPerticulas("Additional Entries");
			} else if (e.getPerticulasName().equalsIgnoreCase("Mismatch")
					|| e.getPerticulasName().equalsIgnoreCase("Match")
					|| e.getPerticulasName()
							.equalsIgnoreCase("Addition in PR(Imports)")
					|| e.getPerticulasName()
							.equalsIgnoreCase("Addition in 2B(Imports)")
					|| e.getPerticulasName()
							.equalsIgnoreCase("Addition in 2A(Imports)")) {
				e.setPerticulas("Imports/SEZG Matching");
			}

		});

		String yearF2A = fromTaxPeriod2A != null
				? fromTaxPeriod2A.substring(0, 4) : "";
		String monthF2A = fromTaxPeriod2A != null
				? fromTaxPeriod2A.substring(4, 6) : "";
		String fromTaxP2A = monthF2A + yearF2A;

		String yearT2A = toTaxPeriod2A != null ? toTaxPeriod2A.substring(0, 4)
				: "";
		String monthT2A = toTaxPeriod2A != null ? toTaxPeriod2A.substring(4, 6)
				: "";
		String toTaxP2A = monthT2A + yearT2A;

		String yearFPR = fromTaxPeriodPR != null
				? fromTaxPeriodPR.substring(0, 4) : "";
		String monthFPR = fromTaxPeriodPR != null
				? fromTaxPeriodPR.substring(4, 6) : "";
		String fromTaxPPR = monthFPR + yearFPR;

		String yearTPR = toTaxPeriodPR != null ? toTaxPeriodPR.substring(0, 4)
				: "";
		String monthTPR = toTaxPeriodPR != null ? toTaxPeriodPR.substring(4, 6)
				: "";
		String toTaxPPR = monthTPR + yearTPR;

		Optional<EntityInfoEntity> optional = entityInfoRepo.findById(entityId);
		EntityInfoEntity entity = optional.get();
		String entityName = entity.getEntityName();
		String gstinList = String.join(",", gstins);
		workbook = writeToExcel(response, entityName, gstinList, fromTaxP2A,
				toTaxP2A, fromTaxPPR, toTaxPPR, reconType, configId,
				toTaxPeriod, fromTaxPeriod, toTaxPeriod2a, fromTaxPeriod2a,
				criteria);

		return workbook;
	}

	private Workbook writeToExcel(List<Gstr2ReconSummaryDto> response,
			String entityName, String gstins, String fromTaxPeriod2A,
			String toTaxPeriod2A, String fromTaxPeriodPR,
			String toTaxPeriodPR, String reconType, Long configId,
			String toTaxPeriod, String fromTaxPeriod, String toTaxPeriod2a,
			String fromTaxPeriod2a, String criteria) {

		Workbook workbook = null;
		int startRow = 9;
		int startcolumn = 0;
		boolean isHeaderRequired = false;

		if (response != null && !response.isEmpty()) {

			List<Gstr2InitiateReconLineStringItemDto> finalgstr2SummaryRecon = response
					.stream()
					.map(o -> convertDtoToStringDto(o))
					.collect(Collectors.toList());

			String[] invoiceHeaders = commonUtility
					.getProp("gstr2a.recon.summary.report.header").split(",");

			if (reconType != null && reconType.equalsIgnoreCase("2BPR")) {
				workbook = commonUtility.createWorkbookWithExcelTemplate(
						"ReportTemplates", "Data for Recon_Summary_2BPR.xlsx");
			}else if (reconType != null && reconType.equalsIgnoreCase("EINVPR")) {
				workbook = commonUtility.createWorkbookWithExcelTemplate(
						"ReportTemplates", "Data for Recon_Summary_EINVPR.xlsx");
			} else {
				if (configId > 0) {
					workbook = commonUtility.createWorkbookWithExcelTemplate(
							"ReportTemplates",
							"Data for Non AP Recon_Summary.xlsx");
				} else {
					workbook = commonUtility.createWorkbookWithExcelTemplate(
							"ReportTemplates",
							"Data for AP Recon_Summary.xlsx");
				}
			}
			if (LOGGER.isDebugEnabled()) {
				String msg = "GenerateErrorReportImpl.writeToExcel "
						+ "workbook created writing data to the workbook";
				LOGGER.debug(msg);
			}

			Cells reportCells = workbook.getWorksheets().get(0).getCells();

			Worksheet sheet = workbook.getWorksheets().get(0);

			String gstinsList = String.join(",", gstins);

			Cell cell = sheet.getCells().get("B1");
			Cell cell1 = sheet.getCells().get("B2");
			Cell cell2 = sheet.getCells().get("B3");
			Cell cell3 = sheet.getCells().get("B4");
			Cell cell4 = sheet.getCells().get("B5");
			Cell cell5 = sheet.getCells().get("B6");

			cell.setValue(entityName);
			cell1.setValue(gstinsList);

			if (configId > 0) {
				cell2.setValue(fromTaxPeriodPR);
				cell3.setValue(toTaxPeriodPR);
				cell4.setValue(fromTaxPeriod2A);
				cell5.setValue(toTaxPeriod2A);
			} else {
				if ("2Ataxperiod".equalsIgnoreCase(criteria) || "2Btaxperiod".equalsIgnoreCase(criteria)) {
					cell4.setValue(fromTaxPeriod2a);
					cell5.setValue(toTaxPeriod2a);
				} else if ("PRtaxperiod".equalsIgnoreCase(criteria)) {
					cell2.setValue(fromTaxPeriod);
					cell3.setValue(toTaxPeriod);
				} 
				else {
					cell2.setValue(fromTaxPeriod);
					cell3.setValue(toTaxPeriod);
					cell4.setValue(fromTaxPeriod2a);
					cell5.setValue(toTaxPeriod2a);

				}

			}

			reportCells.importCustomObjects(finalgstr2SummaryRecon,
					invoiceHeaders,
					isHeaderRequired, startRow, startcolumn,
					finalgstr2SummaryRecon.size(),
					true, "yyyy-mm-dd", false);
			
			if(!reconType.equalsIgnoreCase("EINVPR"))
			{
			Style rowStyle = sheet.getCells().getRows().get(28).getStyle();
			rowStyle.getFont().setBold(true);

			StyleFlag rowStyleFlag = new StyleFlag();
			rowStyleFlag.setFontBold(true);
			sheet.getCells().getRows().get(28).applyStyle(rowStyle,
					rowStyleFlag);

			Style rowStyle1 = sheet.getCells().getRows().get(31).getStyle();
			rowStyle1.getFont().setBold(true);

			StyleFlag rowStyleFlag1 = new StyleFlag();
			rowStyleFlag1.setFontBold(true);
			sheet.getCells().getRows().get(31).applyStyle(rowStyle1,
					rowStyleFlag1);
			}
			
			else
			{
				Style rowStyle = sheet.getCells().getRows().get(24).getStyle();
				rowStyle.getFont().setBold(true);

				StyleFlag rowStyleFlag = new StyleFlag();
				rowStyleFlag.setFontBold(true);
				sheet.getCells().getRows().get(24).applyStyle(rowStyle,
						rowStyleFlag);

				Style rowStyle1 = sheet.getCells().getRows().get(25).getStyle();
				rowStyle1.getFont().setBold(true);

				StyleFlag rowStyleFlag1 = new StyleFlag();
				rowStyleFlag1.setFontBold(true);
				sheet.getCells().getRows().get(25).applyStyle(rowStyle1,
						rowStyleFlag1);
				
			}
			try {
				if (LOGGER.isDebugEnabled()) {
					String msg = "GenerateErrorReportImpl.writeToExcel "
							+ "saving workbook";
					LOGGER.debug(msg);
				}
				workbook.save(ConfigConstants.DATA_FOR_RECON_SUMMARY,
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

	private Gstr2InitiateReconLineStringItemDto convertDtoToStringDto(
			Gstr2ReconSummaryDto o) {
		Gstr2InitiateReconLineStringItemDto dto = new Gstr2InitiateReconLineStringItemDto();

		dto.setPerticulas(o.getPerticulas());
		dto.setPerticulasName(o.getPerticulasName());
		dto.setPrCount(o.getPrCount().toString());
		dto.setPrPercenatge(appendDecimalDigit(o.getPrPercenatge()));

		dto.setPrTaxableValue(appendDecimalDigit(o.getPrTaxableValue()));
		dto.setPrTotalTax(appendDecimalDigit(o.getPrTotalTax()));

		dto.setPrIgst(appendDecimalDigit(o.getPrIgst()));
		dto.setPrCgst(appendDecimalDigit(o.getPrCgst()));
		dto.setPrSgst(appendDecimalDigit(o.getPrSgst()));
		dto.setPrCess(appendDecimalDigit(o.getPrCess()));

		dto.setA2Count(o.getA2Count().toString());
		dto.setA2Percenatge(appendDecimalDigit(o.getA2Percenatge()));
		dto.setA2TaxableValue(appendDecimalDigit(o.getA2TaxableValue()));
		dto.setA2TotalTax(appendDecimalDigit(o.getA2TotalTax()));

		dto.setA2Igst(appendDecimalDigit(o.getA2Igst()));
		dto.setA2Cgst(appendDecimalDigit(o.getA2Cgst()));
		dto.setA2Sgst(appendDecimalDigit(o.getA2Sgst()));
		dto.setA2Cess(appendDecimalDigit(o.getA2Cess()));

		dto.setLevel(o.getLevel());
		dto.setOrderPosition(o.getOrderPosition());
		return dto;
	}

	private String appendDecimalDigit(BigDecimal b) {

		String val = b.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString();

		String[] s = val.split("\\.");
		if (s.length == 2) {
			if (s[1].length() == 1)
				return "'" + (s[0] + "." + s[1] + "0");
			else {
				return "'" + val;
			}
		} else
			return "'" + (val + ".00");

	}

}
