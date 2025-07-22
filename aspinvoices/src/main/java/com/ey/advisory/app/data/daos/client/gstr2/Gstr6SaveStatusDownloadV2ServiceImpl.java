package com.ey.advisory.app.data.daos.client.gstr2;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
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
import com.ey.advisory.app.data.views.client.Gstr6DistrubutionStatusReportsRespDto;
import com.ey.advisory.app.data.views.client.Gstr6SaveStatusReportsRespDto;
import com.ey.advisory.app.docs.dto.gstr1.Gstr6SaveStatusDownloadReqDto;
import com.ey.advisory.app.services.jobs.gstr6.Gstr6DistrubutiionsStatusDownloadV2DaoImpl;
import com.ey.advisory.app.services.jobs.gstr6.Gstr6SaveStatusDownloadV2DaoImpl;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.EYDateUtil;

@Service("Gstr6SaveStatusDownloadV2ServiceImpl")
public class Gstr6SaveStatusDownloadV2ServiceImpl {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(Gstr6SaveStatusDownloadV2ServiceImpl.class);

    @Autowired
    CommonUtility commonUtility;

    @Autowired
    @Qualifier("Gstr6SaveStatusDownloadV2DaoImpl")
    private Gstr6SaveStatusDownloadV2DaoImpl gstr6SaveStatusDownloadDaoImpl;

    @Autowired
    @Qualifier("Gstr6DistrubutiionsStatusDownloadV2DaoImpl")
    private Gstr6DistrubutiionsStatusDownloadV2DaoImpl gstr6DistrubutiionsStatusDownloadDaoImpl;

    @Autowired
    @Qualifier("entityInfoDetailsRepository")
    EntityInfoDetailsRepository repo;

    public Workbook fetchSaveStatus(
            List<Gstr6SaveStatusDownloadReqDto> reqDtos) {
        Workbook workbook = new Workbook();
        int startRow = 4;
        int startcolumn = 0;
        boolean isHeaderRequired = false;

        Gstr6SaveStatusDownloadReqDto summaryRespDto = new Gstr6SaveStatusDownloadReqDto();

        reqDtos.forEach(viewB2ba -> {

            summaryRespDto.setEntityId(viewB2ba.getEntityId());
            summaryRespDto.setGstin(viewB2ba.getGstin());
            summaryRespDto.setTaxPeriod(viewB2ba.getTaxPeriod());

        });
        List<Gstr6SaveStatusReportsRespDto> responseFromView = new ArrayList<>();
        responseFromView = gstr6SaveStatusDownloadDaoImpl
                .fetchGst6SaveSections(reqDtos);
        List<Gstr6DistrubutionStatusReportsRespDto> responseFromdist = new ArrayList<>();
        responseFromdist = gstr6DistrubutiionsStatusDownloadDaoImpl
                .fetchDistributionDataByReq(reqDtos);

        workbook = createWorkbookWithExcelTemplate("ReportTemplates",
                "GSTR-6_Saved_Submitted_Records.xlsx");

        if (responseFromView != null && responseFromView.size() > 0) {
            String[] invoiceHeaders = commonUtility
                    .getProp("gstr6.saved.submit.v.report.headers").split(",");

            Cells errorDumpCells = workbook.getWorksheets().get(0).getCells();

            Long entityId = summaryRespDto.getEntityId();
            EntityInfoEntity findEntityByEntityId = new EntityInfoEntity();
            findEntityByEntityId = repo.findEntityByEntityId(entityId);
            errorDumpCells.get("A1").setValue(" GSTR-6 Processed Summary ");
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

            String fromTaxPeriod = summaryRespDto.getTaxPeriod();
            LocalDate startDate = LocalDate.of(
                    Integer.parseInt(fromTaxPeriod.substring(2)),
                    Integer.parseInt(fromTaxPeriod.substring(0, 2)), 01);
            String taxPeriod = startDate.getMonth().getDisplayName(
                    TextStyle.SHORT, Locale.US) + " " + startDate.getYear();

            errorDumpCells.get("E2").setValue("TaxPeriod-" + taxPeriod);

            String gstins = summaryRespDto.getGstin();
            errorDumpCells.get("B2").setValue("GSTIN-" + gstins);

            errorDumpCells.importCustomObjects(responseFromView, invoiceHeaders,
                    isHeaderRequired, startRow, startcolumn,
                    responseFromView.size(), true, "yyyy-mm-dd", false);
        }

        if (responseFromdist != null && responseFromdist.size() > 0) {
            String[] invoiceHeaders = commonUtility
                    .getProp("gstr6.distribute.submit.report.headers")
                    .split(",");

            Cells errorDumpCells = workbook.getWorksheets().get(1).getCells();

            Long entityId = summaryRespDto.getEntityId();
            EntityInfoEntity findEntityByEntityId = new EntityInfoEntity();
            findEntityByEntityId = repo.findEntityByEntityId(entityId);
            errorDumpCells.get("A1").setValue(" GSTR-6 Processed Summary ");
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

            String fromTaxPeriod = summaryRespDto.getTaxPeriod();
            LocalDate startDate = LocalDate.of(
                    Integer.parseInt(fromTaxPeriod.substring(2)),
                    Integer.parseInt(fromTaxPeriod.substring(0, 2)), 01);
            String taxPeriod = startDate.getMonth().getDisplayName(
                    TextStyle.SHORT, Locale.US) + " " + startDate.getYear();

            errorDumpCells.get("E2").setValue("TaxPeriod-" + taxPeriod);

            String gstins = summaryRespDto.getGstin();
            errorDumpCells.get("B2").setValue("GSTIN-" + gstins);

            errorDumpCells.importCustomObjects(responseFromdist, invoiceHeaders,
                    isHeaderRequired, startRow, startcolumn,
                    responseFromdist.size(), true, "yyyy-mm-dd", false);
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

}
