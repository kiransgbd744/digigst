package com.ey.advisory.app.services.reports;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
import com.ey.advisory.admin.data.entities.client.EntityInfoEntity;
import com.ey.advisory.admin.data.repositories.client.EntityInfoDetailsRepository;
import com.ey.advisory.app.data.services.anx1.Gstr1VsGstr3bReviewSummaryFetchService;
import com.ey.advisory.app.docs.dto.Gstr1Vs3bReviewSummaryFinalScreenRespDto;
import com.ey.advisory.app.docs.dto.Gstr1VsGstr3bReviewSummaryItemsRespDto;
import com.ey.advisory.app.docs.dto.Gstr1VsGstr3bReviewSummaryRespDto;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.core.dto.Gstr1VsGstr3bProcessSummaryReqDto;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * @author Sasidhar
 *
 * 
 */

@Service("Gstr1vs3bReviewSummaryScreenDownloadServiceImpl")
public class Gstr1vs3bReviewSummaryScreenDownloadServiceImpl {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(Gstr2avs3bReviewSummaryDownloadServiceImpl.class);

    @Autowired
    CommonUtility commonUtility;

    @Autowired
    @Qualifier("Gstr1VsGstr3bReviewSummaryFetchService")
    Gstr1VsGstr3bReviewSummaryFetchService gstr1VsGstr3bReviewSummaryService;

    @Autowired
    @Qualifier("entityInfoDetailsRepository")
    EntityInfoDetailsRepository repo;

    public Workbook findGstr1vs3bRevSummTablesReports(
            Gstr1VsGstr3bProcessSummaryReqDto criteria) throws Exception {
        Workbook workbook = new Workbook();
        int startRow = 4;
        int startcolumn = 0;
        boolean isHeaderRequired = false;
        List<Gstr1VsGstr3bReviewSummaryRespDto> response = gstr1VsGstr3bReviewSummaryService
                .response(criteria);
        List<Gstr1Vs3bReviewSummaryFinalScreenRespDto> responseFromViewProcess = convertProcessSummaryRecordsToScreenDtos(
                response);
        workbook = createWorkbookWithExcelTemplate("ReportTemplates",
                "Gstr1vs3b_ReviewSummary_Screen_Download.xlsx");

        LOGGER.debug("Gstr1vs3b_ReviewSummary_Screen_Download "
                + responseFromViewProcess);

        if (responseFromViewProcess != null
                && responseFromViewProcess.size() > 0) {
            String[] invoiceHeaders = commonUtility
                    .getProp("gstr1vs3b.review.screen.report.headers")
                    .split(",");

            Cells errorDumpCells = workbook.getWorksheets().get(0).getCells();

            List<Long> entityId = criteria.getEntityId();
            EntityInfoEntity findEntityByEntityId = new EntityInfoEntity();
            for (Long entity : entityId) {
                findEntityByEntityId = repo.findEntityByEntityId(entity);
            }
            errorDumpCells.get("A1")
                    .setValue("Gstr1vs3b_ReviewSummary_Screen_Download");
            if (findEntityByEntityId != null) {
                errorDumpCells.get("A2").setValue(
                        "Entity Name- " + findEntityByEntityId.getEntityName());
            }

            LocalDateTime now = EYDateUtil
                    .toUTCDateTimeFromLocal(LocalDateTime.now());

            LocalDateTime istDateTimeFromUTC = EYDateUtil
                    .toISTDateTimeFromUTC(now);
            DateTimeFormatter FOMATTER = DateTimeFormatter
                    .ofPattern("dd-MM-yyyy");

            DateTimeFormatter FOMATTER1 = DateTimeFormatter
                    .ofPattern("HH:mm:ss");

            String date = FOMATTER.format(istDateTimeFromUTC);
            String time = FOMATTER1.format(istDateTimeFromUTC);

            errorDumpCells.get("C2").setValue("Date-" + date);
            errorDumpCells.get("D2").setValue("Time-" + time);

            String fromTaxPeriod = criteria.getTaxPeriodFrom();
            LocalDate startDate = LocalDate.of(
                    Integer.parseInt(fromTaxPeriod.substring(2)),
                    Integer.parseInt(fromTaxPeriod.substring(0, 2)), 01);
            String taxPeriod = startDate.getMonth().getDisplayName(
                    TextStyle.SHORT, Locale.US) + " " + startDate.getYear();

            errorDumpCells.get("E2").setValue("TaxPeriod-" + taxPeriod);
            String gstins = criteria.getDataSecAttrs().get("GSTIN").get(0);
            errorDumpCells.get("B2").setValue("GSTIN-" + gstins);
            errorDumpCells.importCustomObjects(responseFromViewProcess,
                    invoiceHeaders, isHeaderRequired, startRow, startcolumn,
                    responseFromViewProcess.size(), true, "yyyy-mm-dd", false);
        }

        return workbook;

    }

    private List<Gstr1Vs3bReviewSummaryFinalScreenRespDto> convertProcessSummaryRecordsToScreenDtos(
            List<Gstr1VsGstr3bReviewSummaryRespDto> response) {
        List<Gstr1Vs3bReviewSummaryFinalScreenRespDto> dtos = Lists
                .newLinkedList();
        response.stream().forEach(dto -> {
            Gstr1Vs3bReviewSummaryFinalScreenRespDto screenDto = new Gstr1Vs3bReviewSummaryFinalScreenRespDto();
            screenDto.setSupplies(String.valueOf(dto.getSupplies()));
            screenDto.setFormula(String.valueOf(dto.getFormula()));
            screenDto.setTaxableValue(String.valueOf(dto.getTaxableValue()));
            screenDto.setIgst(String.valueOf(dto.getIgst()));
            screenDto.setCgst(String.valueOf(dto.getCgst()));
            screenDto.setSgst(String.valueOf(dto.getSgst()));
            screenDto.setCess(String.valueOf(dto.getCess()));
            dtos.add(screenDto);

            List<Gstr1VsGstr3bReviewSummaryItemsRespDto> items = dto.getItems();
            if (CollectionUtils.isNotEmpty(items)) {
                items.forEach(item -> {
                    Gstr1Vs3bReviewSummaryFinalScreenRespDto screenDto1 = new Gstr1Vs3bReviewSummaryFinalScreenRespDto();
                    screenDto1.setSupplies(
                            natureSuppliesMap().get(item.getSupplies()));
                    screenDto1.setFormula(String.valueOf(item.getFormula()));
                    screenDto1.setTaxableValue(
                            String.valueOf(item.getTaxableValue()));
                    screenDto1.setIgst(String.valueOf(item.getIgst()));
                    screenDto1.setCgst(String.valueOf(item.getCgst()));
                    screenDto1.setSgst(String.valueOf(item.getSgst()));
                    screenDto1.setCess(String.valueOf(item.getCess()));
                    dtos.add(screenDto1);
                });
            }
        });
        return dtos;
    }

    private Map<String, String> natureSuppliesMap() {
        Map<String, String> natureSuppliesMap = Maps.newHashMap();
        natureSuppliesMap.put("A", "Table 3.1 (a) & 3.1 (b) as per GSTR - 3B");
        natureSuppliesMap.put("A1",
                "Table 3.1(a) Outward Taxable Supplies (other than zero rated, nil rated and exempted)");
        natureSuppliesMap.put("A2",
                "Table 3.1(b) Outward taxable supplies (zero rated)");
        natureSuppliesMap.put("B", "Details of supplies reported in GSTR - 1");
        natureSuppliesMap.put("B1",
                "Supplies made to registered person on forward charge (B2B - 4A, 6B, 6C)");
        natureSuppliesMap.put("B2",
                "Supplies made to registered person on reverse charge  (B2B - 4B)");
        natureSuppliesMap.put("B3",
                "Supplies made to unregistered person (B2CL - 5)");
        natureSuppliesMap.put("B4",
                "Supplies made to other unregistered person (B2CS - 7)");
        natureSuppliesMap.put("B5", "Exports (6A)");
        natureSuppliesMap.put("B6",
                "CR/DR issued against supplies reported in B1 & B2 (CDNR - 9B)");
        natureSuppliesMap.put("B7",
                "CR/DR issued against supplies reported in B3 & B5 (CDNUR - 9B)");
        natureSuppliesMap.put("B8-B9",
                "Advance Received Less Advance Adjusted (11)");
        natureSuppliesMap.put("B10",
                "Amendment of supplies reported in B1 & B2 of previous tax periods (B2BA - 9A)");
        natureSuppliesMap.put("B11",
                "Amendment of supplies reported in B3 of previous tax periods (B2CLA - 9A)");
        natureSuppliesMap.put("B12",
                "Amendment of supplies reported in B4 of previous tax periods (B2CSA - 10)");
        natureSuppliesMap.put("B13",
                "Amendment of supplies reported in B5 of previous tax periods (EXPA - 9A)");
        natureSuppliesMap.put("B14",
                "Amendment of supplies reported in B6 of previous tax periods (CDNRA - 9C)");
        natureSuppliesMap.put("B15",
                "Amendment of supplies reported in B7 of previous tax periods (CDNURA - 9C)");
        natureSuppliesMap.put("B16-B17",
                "Amendment of advance received reported in B8 of previous "
                        + "tax periods Less Amendment of advance adjusted reported in"
                        + " B9 of previous tax periods (11)");
        natureSuppliesMap.put("C",
                "Difference of Table 3.1 (a) & 3.1 (b) and GSTR-1");
        natureSuppliesMap.put("D",
                "Table 3.1 (c) Other outward supplies (Nil rated, exempted) - GSTR - 3B");
        natureSuppliesMap.put("E",
                "Table 8 - Details of NIL Rated & Exempt supplies reported in GSTR - 1");
        natureSuppliesMap.put("F", "Difference of Table 3.1 (c) and GSTR-1");
        natureSuppliesMap.put("G", "Difference excluding NON-GST Supplies");
        natureSuppliesMap.put("H",
                "Table 3.1 (e) Non-GST outward supplies - GSTR - 3B");
        natureSuppliesMap.put("I",
                "Table 8 - Details of NON-GST Supplies reported in GSTR - 1");
        natureSuppliesMap.put("J", "Difference of Table 3.1 (e) and GSTR-1");
        natureSuppliesMap.put("K", "Net Difference of GSTR - 1 and GSTR - 3B");
        return natureSuppliesMap;
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
