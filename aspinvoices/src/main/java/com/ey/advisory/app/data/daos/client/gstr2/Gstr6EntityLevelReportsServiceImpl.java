package com.ey.advisory.app.data.daos.client.gstr2;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
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
import com.aspose.cells.Worksheet;
import com.beust.jcommander.internal.Lists;
import com.beust.jcommander.internal.Maps;
import com.ey.advisory.admin.data.repositories.client.EntityInfoDetailsRepository;
import com.ey.advisory.app.data.repositories.client.EntityInfoRepository;
import com.ey.advisory.app.data.views.client.GSTR6EntityLevelScreenSummaryDto;
import com.ey.advisory.app.data.views.client.GSTR6EntityLevelScreenSummaryDtoNew;
import com.ey.advisory.app.data.views.client.GSTR6EntityLevelScreenSummaryFinalDto;
import com.ey.advisory.app.data.views.client.GSTR6EntityLevelSummaryDto;
import com.ey.advisory.app.docs.dto.gstr6a.Gstr6ReviewSummaryResponseItemDtoNew;
import com.ey.advisory.app.services.daos.gstr6a.Gstr6ReviewSummaryDaoImpl;
import com.ey.advisory.app.services.jobs.gstr6.Gstr6EntityLevelSummReportsDaoImpl;
import com.ey.advisory.app.services.reports.Gstr6EntityLevelSummaryServiceUtil;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;
import com.ey.advisory.core.dto.Gstr6SummaryRequestDto;
import com.ey.advisory.core.search.PageRequest;

@Service("Gstr6EntityLevelReportsServiceImpl")
public class Gstr6EntityLevelReportsServiceImpl {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr6EntityLevelReportsServiceImpl.class);

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	@Qualifier("Gstr6EntityLevelSummReportsDaoImpl")
	private Gstr6EntityLevelSummReportsDaoImpl gstr6EntityLevelReportsDaoImpl;

	@Autowired
	@Qualifier("entityInfoDetailsRepository")
	EntityInfoDetailsRepository repo;

	@Autowired
	@Qualifier("entityInfoRepository")
	EntityInfoRepository entityInfoRepository;

	@Autowired
	@Qualifier("Gstr6ReviewSummaryDaoImpl")
	private Gstr6ReviewSummaryDaoImpl gstr6RevSumDaoImpl;

	private static final String GSTR6_COLUMNS = "gstr6.download.report.header";
	private int counter = 0;

	public Workbook findEntitySummary(Gstr6SummaryRequestDto setDataSecurity,
			PageRequest pageReq) {
		String entityName = entityInfoRepository.findEntityNameByEntityId(
				setDataSecurity.getEntityId().stream().findFirst().get());
		Gstr6SummaryRequestDto request = (Gstr6SummaryRequestDto) setDataSecurity;
		Workbook workbook = new Workbook();

		List<GSTR6EntityLevelSummaryDto> responseFromView = new ArrayList<>();
		responseFromView = gstr6EntityLevelReportsDaoImpl
				.getEntityLevelSummReports(request);

		List<GSTR6EntityLevelScreenSummaryDto> responseFromViewProcess = new ArrayList<>();
		if (responseFromView.size() > 0 && responseFromView != null) {
			responseFromViewProcess = convertProcessRecordsToScreenDtos(
					responseFromView);
		}
		workbook = createWorkbookWithExcelTemplate("ReportTemplates",
				"GSTR-6_Entity_Level_Summary.xlsx");
		if (responseFromViewProcess != null
				&& responseFromViewProcess.size() > 0) {
			try {
				Gstr6EntityLevelSummaryServiceUtil
						.downloadGstr6EntityLevelReport(workbook,
								responseFromViewProcess, request, entityName);
			} catch (Exception e) {
				LOGGER.error(e.getMessage());
			}
		}
		return workbook;

	}

	private List<GSTR6EntityLevelScreenSummaryDto> convertProcessRecordsToScreenDtos(
			List<GSTR6EntityLevelSummaryDto> response) {
		List<GSTR6EntityLevelScreenSummaryDto> dtos = Lists.newLinkedList();

		Map<String, String> docTypes = Maps.newLinkedHashMap();
		docTypes.put("B2B", "B2B (Section 3)");
		docTypes.put("CDN", "CDN (Section 6B)");
		docTypes.put("B2BA", "B2BA (Section 6A)");
		docTypes.put("CDNA", "CDNA (Section 6C)");
		docTypes.put("Eligible/Ineligible ITC",
				"Eligible / Ineligible ITC (Section 4)");
		docTypes.put("DISTRIBUTION_INVOICE",
				"Distribution - Invoices (Section 5)");
		docTypes.put("DISTRIBUTION_CREDIT",
				"Distribution - Credit Notes (Section 8)");
		docTypes.put("REDISTRIBUTION_INVOICE",
				"Redistribution - Invoices (Section 9)");
		docTypes.put("REDISTRIBUTION_CREDIT",
				"Redistribution - Credit Notes (Section 9)");

		Map<String, List<GSTR6EntityLevelSummaryDto>> groupByGstins = response
				.stream().collect(Collectors
						.groupingBy(GSTR6EntityLevelSummaryDto::getGSTIN));
		groupByGstins.keySet().forEach(gstin -> {
			List<GSTR6EntityLevelSummaryDto> groupByDocTypes = groupByGstins
					.get(gstin);
			docTypes.keySet().forEach(docType -> {
				List<GSTR6EntityLevelSummaryDto> taxDocTypes = groupByDocTypes
						.stream()
						.filter(dto -> dto.getTaxDocType().equals(docType))
						.collect(Collectors.toList());
				List<GSTR6EntityLevelSummaryDto> updatedDocTypes = updateMissingDocTypes(
						taxDocTypes, docType, gstin, docTypes);
				updatedDocTypes.forEach(dto -> {
					GSTR6EntityLevelScreenSummaryDto screenDto = new GSTR6EntityLevelScreenSummaryDto();
					screenDto.setGSTIN(dto.getGSTIN());
					/*
					 * if (dto.getTableDescription()
					 * .equals(dto.getTableDescription())) { screenDto
					 * .setTableDescription(dto.getTableDescription()); } else
					 * if (dto.getTableDescription().equals("Eligible")) {
					 * screenDto.setTableDescription(" Eligible"); } else if
					 * (dto.getTableDescription().equals("Ineligible")) {
					 * screenDto.setTableDescription(" Ineligible"); }
					 */
					screenDto.setTableDescription(dto.getTableDescription());
					screenDto.setCount(String.valueOf(dto.getCount()));
					screenDto.setInvoiceValue(
							String.valueOf(dto.getInvoiceValue()));
					screenDto.setTaxableValue(
							String.valueOf(dto.getTaxableValue()));
					screenDto.setTotalTax(String.valueOf(dto.getTotalTax()));
					screenDto.setIGST(String.valueOf(dto.getIGST()));
					screenDto.setCGST(String.valueOf(dto.getCGST()));
					screenDto.setSGST(String.valueOf(dto.getSGST()));
					screenDto.setCess(String.valueOf(dto.getCess()));
					dtos.add(screenDto);
				});
			});
		});

		dtos.forEach(dto -> {
			String description = dto.getTableDescription();
			if (docTypes.containsKey(description)) {
				dto.setTableDescription(docTypes.get(description));
			}
		});

		return dtos;
	}

	private List<GSTR6EntityLevelSummaryDto> updateMissingDocTypes(
			List<GSTR6EntityLevelSummaryDto> taxDocTypes, String docType,
			String gstin, Map<String, String> docTypesMap) {
		List<GSTR6EntityLevelSummaryDto> updatedocTypes = Lists.newLinkedList();
		List<String> doctypes = Lists.newLinkedList();
		doctypes.add(docType);
		doctypes.add("Eligible");
		doctypes.add("Ineligible");

		if (CollectionUtils.isEmpty(taxDocTypes)) {
			doctypes.forEach(type -> {
				String tableDesc = docTypesMap.get(type) != null
						? docTypesMap.get(type)
						: type.equals("Eligible") ? type
								: type.equals("Ineligible") ? type : type;
				if (docTypesMap.containsKey(type)) {
					tableDesc = docTypesMap.get(type);
				}
				updatedocTypes.add(new GSTR6EntityLevelSummaryDto(gstin, "",
						docType, tableDesc, 0, BigDecimal.valueOf(0.00),
						BigDecimal.valueOf(0.00), BigDecimal.valueOf(0.00),
						BigDecimal.valueOf(0.00), BigDecimal.valueOf(0.00),
						BigDecimal.valueOf(0.00), BigDecimal.valueOf(0.00)));
			});
		} else {
			doctypes.forEach(type -> {
				List<GSTR6EntityLevelSummaryDto> dbTaxDocTypes = taxDocTypes
						.stream()
						.filter(dto -> dto.getTableDescription().equals(type))
						.collect(Collectors.toList());

				if (CollectionUtils.isNotEmpty(dbTaxDocTypes)) {
					GSTR6EntityLevelSummaryDto dbDto = dbTaxDocTypes.stream()
							.findFirst().get();
					dbDto.setTaxDocType(docType);
					updatedocTypes.add(dbDto);
				} else {
					String tableDesc = docTypesMap.get(type) != null
							? docTypesMap.get(type)
							: type.equals("Eligible") ? type
									: type.equals("Ineligible") ? type : type;
					if (docTypesMap.containsKey(type)) {
						tableDesc = docTypesMap.get(type);
					}
					updatedocTypes.add(new GSTR6EntityLevelSummaryDto(gstin, "",
							docType, tableDesc, 0, BigDecimal.valueOf(0.00),
							BigDecimal.valueOf(0.00), BigDecimal.valueOf(0.00),
							BigDecimal.valueOf(0.00), BigDecimal.valueOf(0.00),
							BigDecimal.valueOf(0.00),
							BigDecimal.valueOf(0.00)));
				}
			});
		}
		return updatedocTypes;
	}

	private List<Gstr6ReviewSummaryResponseItemDtoNew> updateMissedDocTypes(
			List<Gstr6ReviewSummaryResponseItemDtoNew> taxDocTypes,
			String docType, String gstin, Map<String, String> docTypesMap) {
		List<Gstr6ReviewSummaryResponseItemDtoNew> updatedocTypes = Lists
				.newLinkedList();
		List<String> doctypes = Lists.newLinkedList();
		doctypes.add(docType);
		doctypes.add(" - Eligible");
		doctypes.add(" - Ineligible");

		if (CollectionUtils.isEmpty(taxDocTypes)) {
			doctypes.forEach(type -> {
				String tableDesc = docTypesMap.get(type) != null
						? docTypesMap.get(type)
						: type.equals(" - Eligible") ? type
								: type.equals(" - Ineligible") ? type : type;
				if (docTypesMap.containsKey(type)) {
					tableDesc = docTypesMap.get(type);
				}
				String eliIndicator = "";
				updatedocTypes.add(new Gstr6ReviewSummaryResponseItemDtoNew(
						gstin, "", docType, eliIndicator, tableDesc,
						BigInteger.valueOf(0), BigDecimal.valueOf(0.00),
						BigDecimal.valueOf(0.00), BigDecimal.valueOf(0.00),
						BigDecimal.valueOf(0.00), BigDecimal.valueOf(0.00),
						BigDecimal.valueOf(0.00), BigDecimal.valueOf(0.00),
						BigInteger.valueOf(0), BigDecimal.valueOf(0.00),
						BigDecimal.valueOf(0.00), BigDecimal.valueOf(0.00),
						BigDecimal.valueOf(0.00), BigDecimal.valueOf(0.00),
						BigDecimal.valueOf(0.00), BigDecimal.valueOf(0.00),
						BigInteger.valueOf(0), BigDecimal.valueOf(0.00),
						BigDecimal.valueOf(0.00), BigDecimal.valueOf(0.00),
						BigDecimal.valueOf(0.00), BigDecimal.valueOf(0.00),
						BigDecimal.valueOf(0.00), BigDecimal.valueOf(0.00)));
			});
		} else {
			doctypes.forEach(type -> {
				List<Gstr6ReviewSummaryResponseItemDtoNew> dbTaxDocTypes = taxDocTypes
						.stream()
						.filter(dto -> dto.getTableDescription().equals(type))
						.collect(Collectors.toList());

				if (CollectionUtils.isNotEmpty(dbTaxDocTypes)) {
					Gstr6ReviewSummaryResponseItemDtoNew dbDto = dbTaxDocTypes
							.stream().findFirst().get();
					dbDto.setDocType(docType);
					updatedocTypes.add(dbDto);
				} else {
					String tableDesc = docTypesMap.get(type) != null
							? docTypesMap.get(type)
							: type.equals(" - Eligible") ? type
									: type.equals(" - Ineligible") ? type
											: type;
					if (docTypesMap.containsKey(type)) {
						tableDesc = docTypesMap.get(type);
					}
					String eliIndicator = "";
					updatedocTypes.add(new Gstr6ReviewSummaryResponseItemDtoNew(
							gstin, "", docType, eliIndicator, tableDesc,
							BigInteger.valueOf(0), BigDecimal.valueOf(0.00),
							BigDecimal.valueOf(0.00), BigDecimal.valueOf(0.00),
							BigDecimal.valueOf(0.00), BigDecimal.valueOf(0.00),
							BigDecimal.valueOf(0.00), BigDecimal.valueOf(0.00),
							BigInteger.valueOf(0), BigDecimal.valueOf(0.00),
							BigDecimal.valueOf(0.00), BigDecimal.valueOf(0.00),
							BigDecimal.valueOf(0.00), BigDecimal.valueOf(0.00),
							BigDecimal.valueOf(0.00), BigDecimal.valueOf(0.00),
							BigInteger.valueOf(0), BigDecimal.valueOf(0.00),
							BigDecimal.valueOf(0.00), BigDecimal.valueOf(0.00),
							BigDecimal.valueOf(0.00), BigDecimal.valueOf(0.00),
							BigDecimal.valueOf(0.00),
							BigDecimal.valueOf(0.00)));
				}
			});
		}
		return updatedocTypes;
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

	public Workbook findGstr6EntitySummary(Gstr6SummaryRequestDto reqDto,
			PageRequest pageReq) throws Exception {
		String entityName = entityInfoRepository.findEntityNameByEntityId(
				reqDto.getEntityId().stream().findFirst().get());
		Gstr6SummaryRequestDto request = (Gstr6SummaryRequestDto) reqDto;
		Workbook workbook = new Workbook();

		Annexure1SummaryReqDto reqDto1 = new Annexure1SummaryReqDto();
		reqDto1.setDataSecAttrs(reqDto.getDataSecAttrs());
		reqDto1.setEntityId(reqDto.getEntityId());
		reqDto1.setTaxPeriod(reqDto.getTaxPeriod());

		List<Gstr6ReviewSummaryResponseItemDtoNew> respItemDtos = getGstr6InwardRespItem(
				reqDto1);
		if (respItemDtos.isEmpty()) {
			LOGGER.error("No Data Found for GSTR 6 Summary Report");
			return null;
		}
		List<GSTR6EntityLevelScreenSummaryDtoNew> responseFromViewProcess = new ArrayList<>();

		responseFromViewProcess = convertToScreenDtos(respItemDtos);

		List<GSTR6EntityLevelScreenSummaryFinalDto> dtoListFinal = responseFromViewProcess
				.stream().map(o -> convertToDtoFinal(o))
				.collect(Collectors.toCollection(ArrayList::new));
		
		Map<String, List<GSTR6EntityLevelScreenSummaryFinalDto>> respMap = dtoListFinal
				.stream().collect(Collectors.groupingBy(
						GSTR6EntityLevelScreenSummaryFinalDto::getGstin));

		String fromTaxPeriod = request.getTaxPeriod();
		workbook = commonUtility.createWorkbookWithExcelTemplate(
				"ReportTemplates", "GSTR-6_Entity_Level_Summary.xlsx");

		LocalDate localDate = LocalDate.of(
				Integer.parseInt(fromTaxPeriod.substring(2)),
				Integer.parseInt(fromTaxPeriod.substring(0, 2)), 1);
		Worksheet worksheet = workbook.getWorksheets().get(0);
		worksheet.autoFitColumns();
		worksheet.autoFitRows();
		Cells cells = worksheet.getCells();
		cells.setStandardWidth(20.5f);
		LocalDateTime now = EYDateUtil
				.toUTCDateTimeFromLocal(LocalDateTime.now());

		LocalDateTime istDateTimeFromUTC = EYDateUtil.toISTDateTimeFromUTC(now);
		DateTimeFormatter FOMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

		DateTimeFormatter FOMATTER1 = DateTimeFormatter.ofPattern("HH:mm:ss");

		String date = FOMATTER.format(istDateTimeFromUTC);
		String time = FOMATTER1.format(istDateTimeFromUTC);
		String[][] dataArray = new String[70 + (respItemDtos.size() * 2)][(10)
				* 10];
		dataArray[0][1] = "ENTITY LEVEL GSTR-6 SUMMARY";
		dataArray[1][1] = "Entity Name - " + entityName + " | Date - " + date
				+ " | Time - " + time + " | Tax Period - "
				+ localDate.getMonth() + " " + localDate.getYear();

		Cells reportCell0 = workbook.getWorksheets().get(0).getCells();

		String[] gstr6Columns = commonUtility.getProp(GSTR6_COLUMNS).split(",");

		// Adding headers
		dataArray[3][3] = "DigiGST";
		dataArray[3][11] = "GSTN";
		dataArray[3][19] = "Difference";
		AtomicInteger row = new AtomicInteger();
		AtomicInteger column = new AtomicInteger();
		row.set(4);
		column.set(1);

		dataArray[row.get()][column.get()] = "GSTIN";
		dataArray[row.get()][column.incrementAndGet()] = "Table Description";
		dataArray[row.get()][column.incrementAndGet()] = "Count";
		dataArray[row.get()][column.incrementAndGet()] = "Invoice Value";
		dataArray[row.get()][column.incrementAndGet()] = "Taxable Value";
		dataArray[row.get()][column.incrementAndGet()] = "Total Tax";
		dataArray[row.get()][column.incrementAndGet()] = "IGST";
		dataArray[row.get()][column.incrementAndGet()] = "CGST";
		dataArray[row.get()][column.incrementAndGet()] = "SGST";
		dataArray[row.get()][column.incrementAndGet()] = "Cess";
		dataArray[row.get()][column.incrementAndGet()] = "Count";
		dataArray[row.get()][column.incrementAndGet()] = "Invoice Value";
		dataArray[row.get()][column.incrementAndGet()] = "Taxable Value";
		dataArray[row.get()][column.incrementAndGet()] = "Total Tax";
		dataArray[row.get()][column.incrementAndGet()] = "IGST";
		dataArray[row.get()][column.incrementAndGet()] = "CGST";
		dataArray[row.get()][column.incrementAndGet()] = "SGST";
		dataArray[row.get()][column.incrementAndGet()] = "Cess";
		dataArray[row.get()][column.incrementAndGet()] = "Count";
		dataArray[row.get()][column.incrementAndGet()] = "Invoice Value";
		dataArray[row.get()][column.incrementAndGet()] = "Taxable Value";
		dataArray[row.get()][column.incrementAndGet()] = "Total Tax";
		dataArray[row.get()][column.incrementAndGet()] = "IGST";
		dataArray[row.get()][column.incrementAndGet()] = "CGST";
		dataArray[row.get()][column.incrementAndGet()] = "SGST";
		dataArray[row.get()][column.incrementAndGet()] = "Cess";
		worksheet.autoFitColumns();
		worksheet.autoFitRows();
		cells.importArray(dataArray, 0, 0);
		int startRow = 5;
		for (Map.Entry map : respMap.entrySet()) {

			List<GSTR6EntityLevelScreenSummaryFinalDto> sortList = respMap
					.get(map.getKey());
			if (sortList != null && !sortList.isEmpty()) {

				reportCell0.importCustomObjects(sortList, gstr6Columns, false,
						startRow, 1, sortList.size(), true, "yyyy-mm-dd",
						false);

				startRow = startRow + sortList.size() + 1;

			}
		}

		return workbook;
	}

	private GSTR6EntityLevelScreenSummaryFinalDto convertToDtoFinal(
			GSTR6EntityLevelScreenSummaryDtoNew o) {
		GSTR6EntityLevelScreenSummaryFinalDto dto = new GSTR6EntityLevelScreenSummaryFinalDto();

		dto.setGstin(o.getGstin());
		dto.setDocType(o.getDocType());
		dto.setTableDescription(o.getTableDescription());

		if (("B2B".equalsIgnoreCase(o.getDocType())
				&& "B2B (Section 3)".equalsIgnoreCase(o.getTableDescription()))
				|| ("CDN".equalsIgnoreCase(o.getDocType()) && "CDN (Section 6B)"
						.equalsIgnoreCase(o.getTableDescription()))
				|| ("B2BA".equalsIgnoreCase(o.getDocType())
						&& "B2BA (Section 6A)"
								.equalsIgnoreCase(o.getTableDescription()))
				|| ("CDNA".equalsIgnoreCase(o.getDocType())
						&& "CDNA (Section 6C)"
								.equalsIgnoreCase(o.getTableDescription()))) {

			if ((o.getAspCess().equals(o.getGstnCess()))
					&& (o.getAspCgst().equals(o.getGstnCgst()))
					&& (o.getAspCount().equals(o.getGstnCount()))
					&& (o.getAspIgst().equals(o.getGstnIgst()))
					&& (o.getAspInvValue().equals(o.getGstnInvValue()))
					&& (o.getAspSgst().equals(o.getGstnSgst()))
					&& (o.getAspTaxbValue().equals(o.getGstnTaxbValue()))
					&& (o.getAspTotTax().equals(o.getGstnTotTax()))) {
				counter = 2;
			}
		}

		dto.setAspCount(o.getAspCount().toString());
		dto.setAspInvValue(appendDecimalDigit(o.getAspInvValue()));
		dto.setAspTaxbValue(appendDecimalDigit(o.getAspTaxbValue()));
		dto.setAspTotTax(appendDecimalDigit(o.getAspTotTax()));
		dto.setAspIgst(appendDecimalDigit(o.getAspIgst()));
		dto.setAspCgst(appendDecimalDigit(o.getAspCgst()));
		dto.setAspSgst(appendDecimalDigit(o.getAspSgst()));
		dto.setAspCess(appendDecimalDigit(o.getAspCess()));
		
		
		dto.setGstnCount(o.getGstnCount().toString());
		dto.setGstnTotTax(appendDecimalDigit(o.getGstnTotTax()));
		dto.setGstnIgst(appendDecimalDigit(o.getGstnIgst()));
		dto.setGstnCgst(appendDecimalDigit(o.getGstnCgst()));
		dto.setGstnSgst(appendDecimalDigit(o.getGstnSgst()));
		dto.setGstnCess(appendDecimalDigit(o.getGstnCess()));
		dto.setGstnInvValue(appendDecimalDigit(o.getGstnInvValue()));
		dto.setGstnTaxbValue(appendDecimalDigit(o.getGstnTaxbValue()));
		
		dto.setDiffCount(o.getDiffCount().toString());
		dto.setDiffTotTax(appendDecimalDigit(o.getDiffTotTax()));
		dto.setDiffIgst(appendDecimalDigit(o.getDiffIgst()));
		dto.setDiffCgst(appendDecimalDigit(o.getDiffCgst()));
		dto.setDiffSgst(appendDecimalDigit(o.getDiffSgst()));
		dto.setDiffCess(appendDecimalDigit(o.getDiffCess()));
		dto.setDiffInvValue(appendDecimalDigit(o.getDiffInvValue()));
		dto.setDiffTaxbValue(appendDecimalDigit(o.getDiffTaxbValue()));
		
		if ("ITC_CROSS".equalsIgnoreCase(o.getDocType())
				&& "Eligible / Ineligible ITC (Section 4)"
						.equalsIgnoreCase(o.getTableDescription())) {
			dto.setDiffCount("NA");
			dto.setGstnInvValue("NA");
			dto.setGstnTaxbValue("NA");
			dto.setDiffInvValue("NA");
			dto.setDiffTaxbValue("NA");
		} else if (counter > 0) {
			if (("B2B".equalsIgnoreCase(o.getDocType())
					|| "CDN".equalsIgnoreCase(o.getDocType())
					|| "B2BA".equalsIgnoreCase(o.getDocType())
					|| "CDNA".equalsIgnoreCase(o.getDocType()))
					&& (" - Eligible".equalsIgnoreCase(o.getTableDescription())
							|| " - Ineligible"
									.equalsIgnoreCase(o.getTableDescription())))

			{
				counter--;
				dto.setDiffCount(BigInteger.ZERO.toString());
				dto.setDiffTotTax(appendDecimalDigit(BigDecimal.ZERO));
				dto.setDiffIgst(appendDecimalDigit(BigDecimal.ZERO));
				dto.setDiffCgst(appendDecimalDigit(BigDecimal.ZERO));
				dto.setDiffSgst(appendDecimalDigit(BigDecimal.ZERO));
				dto.setDiffCess(appendDecimalDigit(BigDecimal.ZERO));
				dto.setDiffInvValue(appendDecimalDigit(BigDecimal.ZERO));
				dto.setDiffTaxbValue(appendDecimalDigit(BigDecimal.ZERO));
			}
		}
		

		return dto;
	}

	private List<GSTR6EntityLevelScreenSummaryDtoNew> convertToScreenDtos(
			List<Gstr6ReviewSummaryResponseItemDtoNew> respItemDtos) {

		List<GSTR6EntityLevelScreenSummaryDtoNew> dtos = new ArrayList<>();

		Map<String, String> docTypes = Maps.newLinkedHashMap();
		docTypes.put("B2B", "B2B (Section 3)");
		docTypes.put("CDN", "CDN (Section 6B)");
		docTypes.put("B2BA", "B2BA (Section 6A)");
		docTypes.put("CDNA", "CDNA (Section 6C)");
		docTypes.put("ITC_CROSS", "Eligible / Ineligible ITC (Section 4)");
		docTypes.put("D_INV", "Distribution - Invoices (Section 5)");
		docTypes.put("D_CR", "Distribution - Credit Notes (Section 8)");
		docTypes.put("RD_INC", "Redistribution - Invoices (Section 9)");
		docTypes.put("RD_CR", "Redistribution - Credit Notes (Section 9)");

		Map<String, List<Gstr6ReviewSummaryResponseItemDtoNew>> groupByGstins = respItemDtos
				.stream().collect(Collectors.groupingBy(
						Gstr6ReviewSummaryResponseItemDtoNew::getGstin));
		groupByGstins.keySet().forEach(gstin -> {
			List<Gstr6ReviewSummaryResponseItemDtoNew> groupByDocTypes = groupByGstins
					.get(gstin);
			docTypes.keySet().forEach(docType -> {
				List<Gstr6ReviewSummaryResponseItemDtoNew> taxDocTypes = groupByDocTypes
						.stream()
						.filter(dto -> dto.getDocType().equals(docType))
						.collect(Collectors.toList());
				taxDocTypes.forEach(dto -> {
					if (dto.getEligInd().equalsIgnoreCase("TOTAL")) {
						dto.setTableDescription(dto.getDocType());
					} else if (dto.getEligInd().equalsIgnoreCase("IS_ELG")) {
						dto.setTableDescription(" - Eligible");
					} else if (dto.getEligInd().equalsIgnoreCase("NOT_ELG")) {
						dto.setTableDescription(" - Ineligible");
					}
				});
				List<Gstr6ReviewSummaryResponseItemDtoNew> updatedDocTypes = updateMissedDocTypes(
						taxDocTypes, docType, gstin, docTypes);

				if (updatedDocTypes != null && !updatedDocTypes.isEmpty()) {

					Map<String, List<Gstr6ReviewSummaryResponseItemDtoNew>> mapGstr6InwardRespItem = getGstr6InwardRespItem(
							updatedDocTypes);
					getGstr6InwardDINVSection(mapGstr6InwardRespItem,
							updatedDocTypes);

					getGstr6InwardDCRSection(mapGstr6InwardRespItem,
							updatedDocTypes);

				}

				updatedDocTypes.forEach(dto -> {
					GSTR6EntityLevelScreenSummaryDtoNew screenDto = new GSTR6EntityLevelScreenSummaryDtoNew();

					screenDto.setGstin(dto.getGstin());
					screenDto.setDocType(dto.getDocType());
					screenDto.setTableDescription(dto.getTableDescription());
					if (dto.getDocType().equalsIgnoreCase("ITC_CROSS")
							&& dto.getTableDescription()
									.equalsIgnoreCase(" - Eligible")) {
						screenDto.setAspCount(BigInteger.ZERO);
					} else if (dto.getDocType().equalsIgnoreCase("ITC_CROSS")
							&& dto.getTableDescription()
									.equalsIgnoreCase(" - InEligible")) {
						screenDto.setAspCount(BigInteger.ZERO);
					} else {
						screenDto.setAspCount(dto.getAspCount());
					}
					screenDto.setAspInvValue(dto.getAspInvValue());
					screenDto.setAspTaxbValue(dto.getAspTaxbValue());
					screenDto.setAspTotTax(dto.getAspTotTax());
					screenDto.setAspIgst(dto.getAspIgst());
					screenDto.setAspCgst(dto.getAspCgst());
					screenDto.setAspSgst(dto.getAspSgst());
					screenDto.setAspCess(dto.getAspCess());

					if (dto.getDocType().equalsIgnoreCase("ITC_CROSS")
							&& dto.getTableDescription()
									.equalsIgnoreCase(" - Eligible")) {
						screenDto.setGstnCount(BigInteger.ZERO);
					} else if (dto.getDocType().equalsIgnoreCase("ITC_CROSS")
							&& dto.getTableDescription()
									.equalsIgnoreCase(" - InEligible")) {
						screenDto.setGstnCount(BigInteger.ZERO);
					} else {
						screenDto.setGstnCount(dto.getAspCount());
					}
					screenDto.setGstnInvValue(dto.getGstnInvValue());
					screenDto.setGstnTaxbValue(dto.getGstnTaxbValue());
					screenDto.setGstnTotTax(dto.getGstnTotTax());
					screenDto.setGstnIgst(dto.getGstnIgst());
					screenDto.setGstnCgst(dto.getGstnCgst());
					screenDto.setGstnSgst(dto.getGstnSgst());
					screenDto.setGstnCess(dto.getGstnCess());

					if (dto.getDocType().equalsIgnoreCase("ITC_CROSS")
							&& dto.getTableDescription()
									.equalsIgnoreCase(" - Eligible")) {
						screenDto.setDiffCount(BigInteger.ZERO);
					} else if (dto.getDocType().equalsIgnoreCase("ITC_CROSS")
							&& dto.getTableDescription()
									.equalsIgnoreCase(" - InEligible")) {
						screenDto.setDiffCount(BigInteger.ZERO);
					} else {
						screenDto.setDiffCount(dto.getDiffCount());
					}
					screenDto.setDiffInvValue(dto.getDiffInvValue());
					screenDto.setDiffTaxbValue(dto.getDiffTaxbValue());
					screenDto.setDiffTotTax(dto.getDiffTotTax());
					screenDto.setDiffIgst(dto.getDiffIgst());
					screenDto.setDiffCgst(dto.getDiffCgst());
					screenDto.setDiffSgst(dto.getDiffSgst());
					screenDto.setDiffCess(dto.getDiffCess());

					dtos.add(screenDto);
				});
			});
		});

		dtos.forEach(dto -> {
			String description = dto.getTableDescription() == null
					? dto.getDocType() : dto.getTableDescription();
			if (docTypes.containsKey(description)) {
				dto.setTableDescription(docTypes.get(description));
			}
		});

		return dtos;
	}

	private void getGstr6InwardDCRSection(
			Map<String, List<Gstr6ReviewSummaryResponseItemDtoNew>> mapGstr6InwardRespItems,
			List<Gstr6ReviewSummaryResponseItemDtoNew> updatedDocTypes) {
		List<Gstr6ReviewSummaryResponseItemDtoNew> respDInvDtos = mapGstr6InwardRespItems
				.get("D_CR");
		if (respDInvDtos != null && !respDInvDtos.isEmpty()) {

			Map<String, List<Gstr6ReviewSummaryResponseItemDtoNew>> eligebleItem = getGstr6InwardRespEligibleItem(
					respDInvDtos);
			List<Gstr6ReviewSummaryResponseItemDtoNew> listEligibleRevSum = eligebleItem
					.get("IS_ELG");

			String gstin = null;
			for (Gstr6ReviewSummaryResponseItemDtoNew dt : updatedDocTypes) {
				gstin = dt.getGstin();
			}
			// Eligible for Review Summary screen
			BigInteger aspCount = BigInteger.ZERO;
			BigDecimal aspInvValue = BigDecimal.ZERO;
			BigDecimal aspTaxbValue = BigDecimal.ZERO;
			BigDecimal aspTotTax = BigDecimal.ZERO;
			BigDecimal aspIgst = BigDecimal.ZERO;
			BigDecimal aspCgst = BigDecimal.ZERO;
			BigDecimal aspSgst = BigDecimal.ZERO;
			BigDecimal aspCess = BigDecimal.ZERO;

			BigInteger gstnCount = BigInteger.ZERO;
			BigDecimal gstnInvValue = BigDecimal.ZERO;
			BigDecimal gstnTaxbValue = BigDecimal.ZERO;
			BigDecimal gstnTotTax = BigDecimal.ZERO;
			BigDecimal gstnIgst = BigDecimal.ZERO;
			BigDecimal gstnCgst = BigDecimal.ZERO;
			BigDecimal gstnSgst = BigDecimal.ZERO;
			BigDecimal gstnCess = BigDecimal.ZERO;

			BigInteger diffCount = BigInteger.ZERO;
			BigDecimal diffInvValue = BigDecimal.ZERO;
			BigDecimal diffTaxbValue = BigDecimal.ZERO;
			BigDecimal diffTotTax = BigDecimal.ZERO;
			BigDecimal diffIgst = BigDecimal.ZERO;
			BigDecimal diffCgst = BigDecimal.ZERO;
			BigDecimal diffSgst = BigDecimal.ZERO;
			BigDecimal diffCess = BigDecimal.ZERO;

			if (listEligibleRevSum != null && !listEligibleRevSum.isEmpty()) {

				for (Gstr6ReviewSummaryResponseItemDtoNew respDto : listEligibleRevSum) {
					aspCount = aspCount.add(respDto.getAspCount() != null
							? respDto.getAspCount() : BigInteger.ZERO);
					aspInvValue = aspInvValue
							.add(respDto.getAspInvValue() != null
									? respDto.getAspInvValue()
									: BigDecimal.ZERO);
					aspTaxbValue = aspTaxbValue
							.add(respDto.getAspTaxbValue() != null
									? respDto.getAspTaxbValue()
									: BigDecimal.ZERO);
					aspTotTax = aspTotTax.add(respDto.getAspTotTax() != null
							? respDto.getAspTotTax() : BigDecimal.ZERO);
					aspIgst = aspIgst.add(respDto.getAspIgst() != null
							? respDto.getAspIgst() : BigDecimal.ZERO);
					aspSgst = aspSgst.add(respDto.getAspSgst() != null
							? respDto.getAspSgst() : BigDecimal.ZERO);
					aspCgst = aspCgst.add(respDto.getAspCgst() != null
							? respDto.getAspCgst() : BigDecimal.ZERO);
					aspCess = aspCess.add(respDto.getAspCess() != null
							? respDto.getAspCess() : BigDecimal.ZERO);

					gstnCount = gstnCount.add(respDto.getGstnCount() != null
							? respDto.getGstnCount() : BigInteger.ZERO);
					gstnInvValue = gstnInvValue
							.add(respDto.getGstnInvValue() != null
									? respDto.getGstnInvValue()
									: BigDecimal.ZERO);

					gstnTaxbValue = gstnTaxbValue
							.add(respDto.getGstnTaxbValue() != null
									? respDto.getGstnTaxbValue()
									: BigDecimal.ZERO);
					gstnTotTax = gstnTotTax.add(respDto.getGstnTotTax() != null
							? respDto.getGstnTotTax() : BigDecimal.ZERO);
					gstnIgst = gstnIgst.add(respDto.getGstnIgst() != null
							? respDto.getGstnIgst() : BigDecimal.ZERO);
					gstnSgst = gstnSgst.add(respDto.getGstnSgst() != null
							? respDto.getGstnSgst() : BigDecimal.ZERO);
					gstnCgst = gstnCgst.add(respDto.getGstnCgst() != null
							? respDto.getGstnCgst() : BigDecimal.ZERO);
					gstnCess = gstnCess.add(respDto.getGstnCess() != null
							? respDto.getGstnCess() : BigDecimal.ZERO);

					diffCount = diffCount.add(respDto.getDiffCount() != null
							? respDto.getDiffCount() : BigInteger.ZERO);
					diffInvValue = diffInvValue
							.add(respDto.getDiffInvValue() != null
									? respDto.getDiffInvValue()
									: BigDecimal.ZERO);

					diffTaxbValue = diffTaxbValue
							.add(respDto.getDiffTaxbValue() != null
									? respDto.getDiffTaxbValue()
									: BigDecimal.ZERO);
					diffTotTax = diffTotTax.add(respDto.getDiffTotTax() != null
							? respDto.getDiffTotTax() : BigDecimal.ZERO);
					diffIgst = diffIgst.add(respDto.getDiffIgst() != null
							? respDto.getDiffIgst() : BigDecimal.ZERO);
					diffSgst = diffSgst.add(respDto.getDiffSgst() != null
							? respDto.getDiffSgst() : BigDecimal.ZERO);
					diffCgst = diffCgst.add(respDto.getDiffCgst() != null
							? respDto.getDiffCgst() : BigDecimal.ZERO);
					diffCess = diffCess.add(respDto.getDiffCess() != null
							? respDto.getDiffCess() : BigDecimal.ZERO);
					respDto.setDocType(" - Eligible");
				}
			} else {
				Gstr6ReviewSummaryResponseItemDtoNew respItemEliDto = new Gstr6ReviewSummaryResponseItemDtoNew();
				respItemEliDto.setDocType(" - Eligible");
			}
			List<Gstr6ReviewSummaryResponseItemDtoNew> listInEligibleRevSum = eligebleItem
					.get("NOT_ELG");
			if (listInEligibleRevSum != null
					&& !listInEligibleRevSum.isEmpty()) {

				for (Gstr6ReviewSummaryResponseItemDtoNew respDto : listInEligibleRevSum) {
					aspCount = aspCount.add(respDto.getAspCount() != null
							? respDto.getAspCount() : BigInteger.ZERO);
					aspInvValue = aspInvValue
							.add(respDto.getAspInvValue() != null
									? respDto.getAspInvValue()
									: BigDecimal.ZERO);
					aspTaxbValue = aspTaxbValue
							.add(respDto.getAspTaxbValue() != null
									? respDto.getAspTaxbValue()
									: BigDecimal.ZERO);
					aspTotTax = aspTotTax.add(respDto.getAspTotTax() != null
							? respDto.getAspTotTax() : BigDecimal.ZERO);
					aspIgst = aspIgst.add(respDto.getAspIgst() != null
							? respDto.getAspIgst() : BigDecimal.ZERO);
					aspSgst = aspSgst.add(respDto.getAspSgst() != null
							? respDto.getAspSgst() : BigDecimal.ZERO);
					aspCgst = aspCgst.add(respDto.getAspCgst() != null
							? respDto.getAspCgst() : BigDecimal.ZERO);
					aspCess = aspCess.add(respDto.getAspCess() != null
							? respDto.getAspCess() : BigDecimal.ZERO);

					gstnCount = gstnCount.add(respDto.getGstnCount() != null
							? respDto.getGstnCount() : BigInteger.ZERO);
					gstnInvValue = gstnInvValue
							.add(respDto.getGstnInvValue() != null
									? respDto.getGstnInvValue()
									: BigDecimal.ZERO);

					gstnTaxbValue = gstnTaxbValue
							.add(respDto.getGstnTaxbValue() != null
									? respDto.getGstnTaxbValue()
									: BigDecimal.ZERO);
					gstnTotTax = gstnTotTax.add(respDto.getGstnTotTax() != null
							? respDto.getGstnTotTax() : BigDecimal.ZERO);
					gstnIgst = gstnIgst.add(respDto.getGstnIgst() != null
							? respDto.getGstnIgst() : BigDecimal.ZERO);
					gstnSgst = gstnSgst.add(respDto.getGstnSgst() != null
							? respDto.getGstnSgst() : BigDecimal.ZERO);
					gstnCgst = gstnCgst.add(respDto.getGstnCgst() != null
							? respDto.getGstnCgst() : BigDecimal.ZERO);
					gstnCess = gstnCess.add(respDto.getGstnCess() != null
							? respDto.getGstnCess() : BigDecimal.ZERO);

					diffCount = diffCount.add(respDto.getDiffCount() != null
							? respDto.getDiffCount() : BigInteger.ZERO);
					diffInvValue = diffInvValue
							.add(respDto.getDiffInvValue() != null
									? respDto.getDiffInvValue()
									: BigDecimal.ZERO);

					diffTaxbValue = diffTaxbValue
							.add(respDto.getDiffTaxbValue() != null
									? respDto.getDiffTaxbValue()
									: BigDecimal.ZERO);
					diffTotTax = diffTotTax.add(respDto.getDiffTotTax() != null
							? respDto.getDiffTotTax() : BigDecimal.ZERO);
					diffIgst = diffIgst.add(respDto.getDiffIgst() != null
							? respDto.getDiffIgst() : BigDecimal.ZERO);
					diffSgst = diffSgst.add(respDto.getDiffSgst() != null
							? respDto.getDiffSgst() : BigDecimal.ZERO);
					diffCgst = diffCgst.add(respDto.getDiffCgst() != null
							? respDto.getDiffCgst() : BigDecimal.ZERO);
					diffCess = diffCess.add(respDto.getDiffCess() != null
							? respDto.getDiffCess() : BigDecimal.ZERO);
					respDto.setDocType(" - Ineligible");
				}
			} else {
				Gstr6ReviewSummaryResponseItemDtoNew respItemInEliDto = new Gstr6ReviewSummaryResponseItemDtoNew();
				respItemInEliDto.setDocType(" - Ineligible");
			}

			for (Gstr6ReviewSummaryResponseItemDtoNew dt : updatedDocTypes) {

				if (dt.getTableDescription().equalsIgnoreCase(
						"Distribution - Credit Notes (Section 8)")) {
					dt.setGstin(gstin);
					dt.setDocType("D_CR");
					dt.setTableDescription(
							"Distribution - Credit Notes (Section 8)");
					dt.setEligInd("TOTAL");
					dt.setAspCount(aspCount);
					dt.setAspInvValue(aspInvValue);
					dt.setAspTaxbValue(aspTaxbValue);
					dt.setAspTotTax(aspTotTax);
					dt.setAspIgst(aspIgst);
					dt.setAspCgst(aspCgst);
					dt.setAspSgst(aspSgst);
					dt.setAspCess(aspCess);

					dt.setGstnCount(gstnCount);
					dt.setGstnInvValue(gstnInvValue);
					dt.setGstnTaxbValue(gstnTaxbValue);
					dt.setGstnTotTax(gstnTotTax);
					dt.setGstnIgst(gstnIgst);
					dt.setGstnCgst(gstnCgst);
					dt.setGstnSgst(gstnSgst);
					dt.setGstnCess(gstnCess);

					dt.setDiffCount(diffCount);
					dt.setDiffInvValue(diffInvValue);
					dt.setDiffTaxbValue(diffTaxbValue);
					dt.setDiffTotTax(diffTotTax);
					dt.setDiffIgst(diffIgst);
					dt.setDiffCgst(diffCgst);
					dt.setDiffSgst(diffSgst);
					dt.setDiffCess(diffCess);
				}
			}
		}

	}

	private void getGstr6InwardDINVSection(
			Map<String, List<Gstr6ReviewSummaryResponseItemDtoNew>> mapGstr6InwardRespItems,
			List<Gstr6ReviewSummaryResponseItemDtoNew> updatedDocTypes) {
		List<Gstr6ReviewSummaryResponseItemDtoNew> respDInvDtos = mapGstr6InwardRespItems
				.get("D_INV");
		if (respDInvDtos != null && !respDInvDtos.isEmpty()) {

			Map<String, List<Gstr6ReviewSummaryResponseItemDtoNew>> eligebleItem = getGstr6InwardRespEligibleItem(
					respDInvDtos);
			List<Gstr6ReviewSummaryResponseItemDtoNew> listEligibleRevSum = eligebleItem
					.get("IS_ELG");

			String gstin = null;
			for (Gstr6ReviewSummaryResponseItemDtoNew dt : updatedDocTypes) {
				gstin = dt.getGstin();
			}
			// Eligible for Review Summary screen
			BigInteger aspCount = BigInteger.ZERO;
			BigDecimal aspInvValue = BigDecimal.ZERO;
			BigDecimal aspTaxbValue = BigDecimal.ZERO;
			BigDecimal aspTotTax = BigDecimal.ZERO;
			BigDecimal aspIgst = BigDecimal.ZERO;
			BigDecimal aspCgst = BigDecimal.ZERO;
			BigDecimal aspSgst = BigDecimal.ZERO;
			BigDecimal aspCess = BigDecimal.ZERO;

			BigInteger gstnCount = BigInteger.ZERO;
			BigDecimal gstnInvValue = BigDecimal.ZERO;
			BigDecimal gstnTaxbValue = BigDecimal.ZERO;
			BigDecimal gstnTotTax = BigDecimal.ZERO;
			BigDecimal gstnIgst = BigDecimal.ZERO;
			BigDecimal gstnCgst = BigDecimal.ZERO;
			BigDecimal gstnSgst = BigDecimal.ZERO;
			BigDecimal gstnCess = BigDecimal.ZERO;

			BigInteger diffCount = BigInteger.ZERO;
			BigDecimal diffInvValue = BigDecimal.ZERO;
			BigDecimal diffTaxbValue = BigDecimal.ZERO;
			BigDecimal diffTotTax = BigDecimal.ZERO;
			BigDecimal diffIgst = BigDecimal.ZERO;
			BigDecimal diffCgst = BigDecimal.ZERO;
			BigDecimal diffSgst = BigDecimal.ZERO;
			BigDecimal diffCess = BigDecimal.ZERO;

			if (listEligibleRevSum != null && !listEligibleRevSum.isEmpty()) {

				for (Gstr6ReviewSummaryResponseItemDtoNew respDto : listEligibleRevSum) {
					aspCount = aspCount.add(respDto.getAspCount() != null
							? respDto.getAspCount() : BigInteger.ZERO);
					aspInvValue = aspInvValue
							.add(respDto.getAspInvValue() != null
									? respDto.getAspInvValue()
									: BigDecimal.ZERO);
					aspTaxbValue = aspTaxbValue
							.add(respDto.getAspTaxbValue() != null
									? respDto.getAspTaxbValue()
									: BigDecimal.ZERO);
					aspTotTax = aspTotTax.add(respDto.getAspTotTax() != null
							? respDto.getAspTotTax() : BigDecimal.ZERO);
					aspIgst = aspIgst.add(respDto.getAspIgst() != null
							? respDto.getAspIgst() : BigDecimal.ZERO);
					aspSgst = aspSgst.add(respDto.getAspSgst() != null
							? respDto.getAspSgst() : BigDecimal.ZERO);
					aspCgst = aspCgst.add(respDto.getAspCgst() != null
							? respDto.getAspCgst() : BigDecimal.ZERO);
					aspCess = aspCess.add(respDto.getAspCess() != null
							? respDto.getAspCess() : BigDecimal.ZERO);

					gstnCount = gstnCount.add(respDto.getGstnCount() != null
							? respDto.getGstnCount() : BigInteger.ZERO);
					gstnInvValue = gstnInvValue
							.add(respDto.getGstnInvValue() != null
									? respDto.getGstnInvValue()
									: BigDecimal.ZERO);

					gstnTaxbValue = gstnTaxbValue
							.add(respDto.getGstnTaxbValue() != null
									? respDto.getGstnTaxbValue()
									: BigDecimal.ZERO);
					gstnTotTax = gstnTotTax.add(respDto.getGstnTotTax() != null
							? respDto.getGstnTotTax() : BigDecimal.ZERO);
					gstnIgst = gstnIgst.add(respDto.getGstnIgst() != null
							? respDto.getGstnIgst() : BigDecimal.ZERO);
					gstnSgst = gstnSgst.add(respDto.getGstnSgst() != null
							? respDto.getGstnSgst() : BigDecimal.ZERO);
					gstnCgst = gstnCgst.add(respDto.getGstnCgst() != null
							? respDto.getGstnCgst() : BigDecimal.ZERO);
					gstnCess = gstnCess.add(respDto.getGstnCess() != null
							? respDto.getGstnCess() : BigDecimal.ZERO);

					diffCount = diffCount.add(respDto.getDiffCount() != null
							? respDto.getDiffCount() : BigInteger.ZERO);
					diffInvValue = diffInvValue
							.add(respDto.getDiffInvValue() != null
									? respDto.getDiffInvValue()
									: BigDecimal.ZERO);

					diffTaxbValue = diffTaxbValue
							.add(respDto.getDiffTaxbValue() != null
									? respDto.getDiffTaxbValue()
									: BigDecimal.ZERO);
					diffTotTax = diffTotTax.add(respDto.getDiffTotTax() != null
							? respDto.getDiffTotTax() : BigDecimal.ZERO);
					diffIgst = diffIgst.add(respDto.getDiffIgst() != null
							? respDto.getDiffIgst() : BigDecimal.ZERO);
					diffSgst = diffSgst.add(respDto.getDiffSgst() != null
							? respDto.getDiffSgst() : BigDecimal.ZERO);
					diffCgst = diffCgst.add(respDto.getDiffCgst() != null
							? respDto.getDiffCgst() : BigDecimal.ZERO);
					diffCess = diffCess.add(respDto.getDiffCess() != null
							? respDto.getDiffCess() : BigDecimal.ZERO);
					respDto.setDocType(" - Eligible");
				}
			} else {
				Gstr6ReviewSummaryResponseItemDtoNew respItemEliDto = new Gstr6ReviewSummaryResponseItemDtoNew();
				respItemEliDto.setDocType(" - Eligible");
			}
			List<Gstr6ReviewSummaryResponseItemDtoNew> listInEligibleRevSum = eligebleItem
					.get("NOT_ELG");
			if (listInEligibleRevSum != null
					&& !listInEligibleRevSum.isEmpty()) {

				for (Gstr6ReviewSummaryResponseItemDtoNew respDto : listInEligibleRevSum) {
					aspCount = aspCount.add(respDto.getAspCount() != null
							? respDto.getAspCount() : BigInteger.ZERO);
					aspInvValue = aspInvValue
							.add(respDto.getAspInvValue() != null
									? respDto.getAspInvValue()
									: BigDecimal.ZERO);
					aspTaxbValue = aspTaxbValue
							.add(respDto.getAspTaxbValue() != null
									? respDto.getAspTaxbValue()
									: BigDecimal.ZERO);
					aspTotTax = aspTotTax.add(respDto.getAspTotTax() != null
							? respDto.getAspTotTax() : BigDecimal.ZERO);
					aspIgst = aspIgst.add(respDto.getAspIgst() != null
							? respDto.getAspIgst() : BigDecimal.ZERO);
					aspSgst = aspSgst.add(respDto.getAspSgst() != null
							? respDto.getAspSgst() : BigDecimal.ZERO);
					aspCgst = aspCgst.add(respDto.getAspCgst() != null
							? respDto.getAspCgst() : BigDecimal.ZERO);
					aspCess = aspCess.add(respDto.getAspCess() != null
							? respDto.getAspCess() : BigDecimal.ZERO);

					gstnCount = gstnCount.add(respDto.getGstnCount() != null
							? respDto.getGstnCount() : BigInteger.ZERO);
					gstnInvValue = gstnInvValue
							.add(respDto.getGstnInvValue() != null
									? respDto.getGstnInvValue()
									: BigDecimal.ZERO);

					gstnTaxbValue = gstnTaxbValue
							.add(respDto.getGstnTaxbValue() != null
									? respDto.getGstnTaxbValue()
									: BigDecimal.ZERO);
					gstnTotTax = gstnTotTax.add(respDto.getGstnTotTax() != null
							? respDto.getGstnTotTax() : BigDecimal.ZERO);
					gstnIgst = gstnIgst.add(respDto.getGstnIgst() != null
							? respDto.getGstnIgst() : BigDecimal.ZERO);
					gstnSgst = gstnSgst.add(respDto.getGstnSgst() != null
							? respDto.getGstnSgst() : BigDecimal.ZERO);
					gstnCgst = gstnCgst.add(respDto.getGstnCgst() != null
							? respDto.getGstnCgst() : BigDecimal.ZERO);
					gstnCess = gstnCess.add(respDto.getGstnCess() != null
							? respDto.getGstnCess() : BigDecimal.ZERO);

					diffCount = diffCount.add(respDto.getDiffCount() != null
							? respDto.getDiffCount() : BigInteger.ZERO);
					diffInvValue = diffInvValue
							.add(respDto.getDiffInvValue() != null
									? respDto.getDiffInvValue()
									: BigDecimal.ZERO);

					diffTaxbValue = diffTaxbValue
							.add(respDto.getDiffTaxbValue() != null
									? respDto.getDiffTaxbValue()
									: BigDecimal.ZERO);
					diffTotTax = diffTotTax.add(respDto.getDiffTotTax() != null
							? respDto.getDiffTotTax() : BigDecimal.ZERO);
					diffIgst = diffIgst.add(respDto.getDiffIgst() != null
							? respDto.getDiffIgst() : BigDecimal.ZERO);
					diffSgst = diffSgst.add(respDto.getDiffSgst() != null
							? respDto.getDiffSgst() : BigDecimal.ZERO);
					diffCgst = diffCgst.add(respDto.getDiffCgst() != null
							? respDto.getDiffCgst() : BigDecimal.ZERO);
					diffCess = diffCess.add(respDto.getDiffCess() != null
							? respDto.getDiffCess() : BigDecimal.ZERO);
					respDto.setDocType(" - Ineligible");
				}
			} else {
				Gstr6ReviewSummaryResponseItemDtoNew respItemInEliDto = new Gstr6ReviewSummaryResponseItemDtoNew();
				respItemInEliDto.setDocType(" - Ineligible");
			}

			for (Gstr6ReviewSummaryResponseItemDtoNew dt : updatedDocTypes) {

				if (dt.getTableDescription().equalsIgnoreCase(
						"Distribution - Invoices (Section 5)")) {
					dt.setGstin(gstin);
					dt.setDocType("D_INV");
					dt.setTableDescription(
							"Distribution - Invoices (Section 5)");
					dt.setEligInd("TOTAL");
					dt.setAspCount(aspCount);
					dt.setAspInvValue(aspInvValue);
					dt.setAspTaxbValue(aspTaxbValue);
					dt.setAspTotTax(aspTotTax);
					dt.setAspIgst(aspIgst);
					dt.setAspCgst(aspCgst);
					dt.setAspSgst(aspSgst);
					dt.setAspCess(aspCess);

					dt.setGstnCount(gstnCount);
					dt.setGstnInvValue(gstnInvValue);
					dt.setGstnTaxbValue(gstnTaxbValue);
					dt.setGstnTotTax(gstnTotTax);
					dt.setGstnIgst(gstnIgst);
					dt.setGstnCgst(gstnCgst);
					dt.setGstnSgst(gstnSgst);
					dt.setGstnCess(gstnCess);

					dt.setDiffCount(diffCount);
					dt.setDiffInvValue(diffInvValue);
					dt.setDiffTaxbValue(diffTaxbValue);
					dt.setDiffTotTax(diffTotTax);
					dt.setDiffIgst(diffIgst);
					dt.setDiffCgst(diffCgst);
					dt.setDiffSgst(diffSgst);
					dt.setDiffCess(diffCess);
				}
			}
		}

	}

	private Map<String, List<Gstr6ReviewSummaryResponseItemDtoNew>> getGstr6InwardRespEligibleItem(
			List<Gstr6ReviewSummaryResponseItemDtoNew> respDInvDtos) {
		Map<String, List<Gstr6ReviewSummaryResponseItemDtoNew>> mapRevSumResItemDtos = new HashMap<>();
		respDInvDtos.forEach(respItemDto -> {
			StringBuilder key = new StringBuilder();
			key.append(respItemDto.getEligInd());
			String docKey = key.toString();
			if (mapRevSumResItemDtos.containsKey(docKey)) {
				List<Gstr6ReviewSummaryResponseItemDtoNew> revSumItemDtos = mapRevSumResItemDtos
						.get(docKey);
				revSumItemDtos.add(respItemDto);
				mapRevSumResItemDtos.put(docKey, revSumItemDtos);
			} else {
				List<Gstr6ReviewSummaryResponseItemDtoNew> revSumItemDtos = new ArrayList<>();
				revSumItemDtos.add(respItemDto);
				mapRevSumResItemDtos.put(docKey, revSumItemDtos);
			}
		});
		return mapRevSumResItemDtos;
	}

	private Map<String, List<Gstr6ReviewSummaryResponseItemDtoNew>> getGstr6InwardRespItem(
			List<Gstr6ReviewSummaryResponseItemDtoNew> updatedDocTypes) {
		Map<String, List<Gstr6ReviewSummaryResponseItemDtoNew>> mapRevSumResItemDtos = new HashMap<>();
		updatedDocTypes.forEach(respItemDto -> {
			StringBuilder key = new StringBuilder();
			key.append(respItemDto.getDocType());
			String docKey = key.toString();
			if (mapRevSumResItemDtos.containsKey(docKey)) {
				List<Gstr6ReviewSummaryResponseItemDtoNew> revSumItemDtos = mapRevSumResItemDtos
						.get(docKey);
				revSumItemDtos.add(respItemDto);
				mapRevSumResItemDtos.put(docKey, revSumItemDtos);
			} else {
				List<Gstr6ReviewSummaryResponseItemDtoNew> revSumItemDtos = new ArrayList<>();
				revSumItemDtos.add(respItemDto);
				mapRevSumResItemDtos.put(docKey, revSumItemDtos);
			}
		});
		return mapRevSumResItemDtos;
	}

	public List<Gstr6ReviewSummaryResponseItemDtoNew> getGstr6InwardRespItem(
			final Annexure1SummaryReqDto reqDto) {

		List<Object[]> inwardSumDataObjs = gstr6RevSumDaoImpl
				.getSummaryDetails(reqDto);
		List<Gstr6ReviewSummaryResponseItemDtoNew> respItemDtos = new ArrayList<>();
		if (inwardSumDataObjs != null && !inwardSumDataObjs.isEmpty()) {

			inwardSumDataObjs.forEach(inwardSumDataObj -> {
				Gstr6ReviewSummaryResponseItemDtoNew respItemDto = new Gstr6ReviewSummaryResponseItemDtoNew();
				respItemDto.setAspCount(inwardSumDataObj[0] != null
						? new BigInteger(String.valueOf(inwardSumDataObj[0]))
						: BigInteger.ZERO);
				respItemDto.setAspInvValue(inwardSumDataObj[1] != null
						? new BigDecimal(String.valueOf(inwardSumDataObj[1]))
						: BigDecimal.ZERO);
				respItemDto.setAspTaxbValue(inwardSumDataObj[2] != null
						? new BigDecimal(String.valueOf(inwardSumDataObj[2]))
						: BigDecimal.ZERO);
				respItemDto.setAspTotTax(inwardSumDataObj[3] != null
						? new BigDecimal(String.valueOf(inwardSumDataObj[3]))
						: BigDecimal.ZERO);
				respItemDto.setAspIgst(inwardSumDataObj[4] != null
						? new BigDecimal(String.valueOf(inwardSumDataObj[4]))
						: BigDecimal.ZERO);
				respItemDto.setAspCgst(inwardSumDataObj[5] != null
						? new BigDecimal(String.valueOf(inwardSumDataObj[5]))
						: BigDecimal.ZERO);
				respItemDto.setAspSgst(inwardSumDataObj[6] != null
						? new BigDecimal(String.valueOf(inwardSumDataObj[6]))
						: BigDecimal.ZERO);
				respItemDto.setAspCess(inwardSumDataObj[7] != null
						? new BigDecimal(String.valueOf(inwardSumDataObj[7]))
						: BigDecimal.ZERO);
				respItemDto.setEligInd(inwardSumDataObj[8] != null
						? String.valueOf(inwardSumDataObj[8]) : null);
				respItemDto.setDocType(inwardSumDataObj[9] != null
						? String.valueOf(inwardSumDataObj[9]) : null);
				respItemDto.setGstin(inwardSumDataObj[10] != null
						? String.valueOf(inwardSumDataObj[10]) : null);
				respItemDto.setGstnCount(inwardSumDataObj[12] != null
						? new BigInteger(String.valueOf(inwardSumDataObj[12]))
						: BigInteger.ZERO);
				respItemDto.setGstnInvValue(inwardSumDataObj[13] != null
						? new BigDecimal(String.valueOf(inwardSumDataObj[13]))
						: BigDecimal.ZERO);
				respItemDto.setGstnTaxbValue(inwardSumDataObj[14] != null
						? new BigDecimal(String.valueOf(inwardSumDataObj[14]))
						: BigDecimal.ZERO);
				respItemDto.setGstnTotTax(inwardSumDataObj[15] != null
						? new BigDecimal(String.valueOf(inwardSumDataObj[15]))
						: BigDecimal.ZERO);
				respItemDto.setGstnIgst(inwardSumDataObj[16] != null
						? new BigDecimal(String.valueOf(inwardSumDataObj[16]))
						: BigDecimal.ZERO);
				respItemDto.setGstnCgst(inwardSumDataObj[17] != null
						? new BigDecimal(String.valueOf(inwardSumDataObj[17]))
						: BigDecimal.ZERO);
				respItemDto.setGstnSgst(inwardSumDataObj[18] != null
						? new BigDecimal(String.valueOf(inwardSumDataObj[18]))
						: BigDecimal.ZERO);
				respItemDto.setGstnCess(inwardSumDataObj[19] != null
						? new BigDecimal(String.valueOf(inwardSumDataObj[19]))
						: BigDecimal.ZERO);
				respItemDto.setDiffCount(respItemDto.getAspCount()
						.subtract(respItemDto.getGstnCount()));
				respItemDto.setDiffInvValue(respItemDto.getAspInvValue()
						.subtract(respItemDto.getGstnInvValue()));
				respItemDto.setDiffTaxbValue(respItemDto.getAspTaxbValue()
						.subtract(respItemDto.getGstnTaxbValue()));
				respItemDto.setDiffTotTax(respItemDto.getAspTotTax()
						.subtract(respItemDto.getGstnTotTax()));
				respItemDto.setDiffIgst(respItemDto.getAspIgst()
						.subtract(respItemDto.getGstnIgst()));
				respItemDto.setDiffCgst(respItemDto.getAspCgst()
						.subtract(respItemDto.getGstnCgst()));
				respItemDto.setDiffSgst(respItemDto.getAspSgst()
						.subtract(respItemDto.getGstnSgst()));
				respItemDto.setDiffCess(respItemDto.getAspCess()
						.subtract(respItemDto.getGstnCess()));
				respItemDtos.add(respItemDto);
			});
		}
		return respItemDtos;
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
