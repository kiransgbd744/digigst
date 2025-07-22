package com.ey.advisory.app.services.reports;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

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
import com.ey.advisory.app.data.services.anx1.Gstr2aVsGstr3bReviewSummaryFetchService;
import com.ey.advisory.app.docs.dto.Gstr2aVsGstr3bReviewSummaryRespDto;
import com.ey.advisory.app.docs.dto.Gstr2aVsGstr3bReviewSummaryScreenRespDto;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.core.dto.Gstr1VsGstr3bProcessSummaryReqDto;
import com.google.common.collect.Lists;

/**
 * @author Sasidhar
 *
 * 
 */

@Service("Gstr2avs3bReviewSummaryDownloadServiceImpl")
public class Gstr2avs3bReviewSummaryDownloadServiceImpl {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(Gstr2avs3bReviewSummaryDownloadServiceImpl.class);

    @Autowired
    CommonUtility commonUtility;

    @Autowired
    @Qualifier("Gstr2aVsGstr3bReviewSummaryFetchService")
    Gstr2aVsGstr3bReviewSummaryFetchService gstr2aVsGstr3bReviewSummaryFetchService;

    @Autowired
    @Qualifier("entityInfoDetailsRepository")
    EntityInfoDetailsRepository repo;

    public Workbook findGstr2avs3bRevSummTablesReports(
            Gstr1VsGstr3bProcessSummaryReqDto criteria) throws Exception {
        Workbook workbook = new Workbook();
        int startRow = 4;
        int startcolumn = 0;
        boolean isHeaderRequired = false;
        List<Gstr2aVsGstr3bReviewSummaryRespDto> response = gstr2aVsGstr3bReviewSummaryFetchService
                .gstr2aVsGstr3bReviewSummaryRecords(criteria);
        List<Gstr2aVsGstr3bReviewSummaryScreenRespDto> responseFromViewProcess = convertProcessSummaryRecordsToScreenDtos(
                response);
        workbook = createWorkbookWithExcelTemplate("ReportTemplates",
                "Gstr2avs3b_ReviewSummary_Screen_Download.xlsx");

        LOGGER.debug("Gstr2avs3b_ReviewSummary_Screen_Download "
                + responseFromViewProcess);

        if (responseFromViewProcess != null
                && responseFromViewProcess.size() > 0) {
            String[] invoiceHeaders = commonUtility
                    .getProp("gstr2avs3b.review.screen.report.headers")
                    .split(",");

            Cells errorDumpCells = workbook.getWorksheets().get(0).getCells();

            List<Long> entityId = criteria.getEntityId();
            EntityInfoEntity findEntityByEntityId = new EntityInfoEntity();
            for (Long entity : entityId) {
                findEntityByEntityId = repo.findEntityByEntityId(entity);
            }
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

            errorDumpCells.get("B3").setValue("From TaxPeriod-" + taxPeriod);
            
            String toTaxPeriod = criteria.getTaxPeriodTo();
            LocalDate endDate = LocalDate.of(
                    Integer.parseInt(toTaxPeriod.substring(2)),
                    Integer.parseInt(toTaxPeriod.substring(0, 2)), 01);
            String endTaxPeriod = endDate.getMonth().getDisplayName(
                    TextStyle.SHORT, Locale.US) + " " + endDate.getYear();
            errorDumpCells.get("C3").setValue("To TaxPeriod-" + endTaxPeriod);
            
            String gstins = criteria.getDataSecAttrs().get("GSTIN").get(0);
            errorDumpCells.get("B2").setValue("GSTIN-" + gstins);
            errorDumpCells.importCustomObjects(responseFromViewProcess,
                    invoiceHeaders, isHeaderRequired, startRow, startcolumn,
                    responseFromViewProcess.size(), true, "yyyy-mm-dd", false);
        }

        return workbook;

    }

    private List<Gstr2aVsGstr3bReviewSummaryScreenRespDto> convertProcessSummaryRecordsToScreenDtos(
            List<Gstr2aVsGstr3bReviewSummaryRespDto> response) {
        List<Gstr2aVsGstr3bReviewSummaryScreenRespDto> dtos = Lists
                .newLinkedList();
        response.stream().forEach(dto -> {
            Gstr2aVsGstr3bReviewSummaryScreenRespDto screenDto = new Gstr2aVsGstr3bReviewSummaryScreenRespDto();
            screenDto.setDescription(dto.getDescription());
            screenDto.setCalFeild(dto.getCalFeild());
            screenDto.setIgst(dto.getIgst());
            screenDto.setSgst(dto.getSgst());
            screenDto.setCgst(dto.getCgst());
            screenDto.setCess(dto.getCess());
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
