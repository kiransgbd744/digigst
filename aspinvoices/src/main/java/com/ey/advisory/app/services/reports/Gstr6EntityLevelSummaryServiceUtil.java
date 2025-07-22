package com.ey.advisory.app.services.reports;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.aspose.cells.Cells;
import com.aspose.cells.Workbook;
import com.aspose.cells.Worksheet;
import com.ey.advisory.app.data.views.client.GSTR6EntityLevelScreenSummaryDto;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.core.dto.Gstr6SummaryRequestDto;

public class Gstr6EntityLevelSummaryServiceUtil {

    public static void downloadGstr6EntityLevelReport(Workbook workbook,
            List<GSTR6EntityLevelScreenSummaryDto> responseFromViewProcess,
            Gstr6SummaryRequestDto request, String entityName)
            throws Exception {
        String fromTaxPeriod = request.getTaxPeriod();
        LocalDate localDate = LocalDate.of(
                Integer.parseInt(fromTaxPeriod.substring(2)),
                Integer.parseInt(fromTaxPeriod.substring(0, 2)), 1);

        Worksheet worksheet = workbook.getWorksheets().get(0);
        worksheet.autoFitColumns();
        worksheet.autoFitRows();
        Cells cells = worksheet.getCells();
        worksheet.getCells().setStandardWidth(20.5f);
        LocalDateTime now = EYDateUtil
                .toUTCDateTimeFromLocal(LocalDateTime.now());

        LocalDateTime istDateTimeFromUTC = EYDateUtil.toISTDateTimeFromUTC(now);
        DateTimeFormatter FOMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        DateTimeFormatter FOMATTER1 = DateTimeFormatter.ofPattern("HH:mm:ss");

        String date = FOMATTER.format(istDateTimeFromUTC);
        String time = FOMATTER1.format(istDateTimeFromUTC);
        String[][] dataArray = new String[70
                + (responseFromViewProcess.size() * 2)][(10) * 10];
        dataArray[0][1] = "ENTITY LEVEL GSTR-6 SUMMARY";
        dataArray[1][1] = "Entity Name - " + entityName + ".| Date -" + date
                + " | Time -" + time + "| Tax Period - " + localDate.getMonth()
                + " " + localDate.getYear();

        // Adding headers
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
        worksheet.autoFitColumns();
        worksheet.autoFitRows();

        responseFromViewProcess.forEach(dto -> {
            row.incrementAndGet();
            column.set(1);
            dataArray[row.get()][column.get()] = dto.getGSTIN();
            dataArray[row.get()][column.incrementAndGet()] = dto
                    .getTableDescription();
            dataArray[row.get()][column.incrementAndGet()] = dto.getCount();
            dataArray[row.get()][column.incrementAndGet()] = dto
                    .getInvoiceValue();
            dataArray[row.get()][column.incrementAndGet()] = dto
                    .getTaxableValue();
            dataArray[row.get()][column.incrementAndGet()] = dto.getTotalTax();
            dataArray[row.get()][column.incrementAndGet()] = dto.getIGST();
            dataArray[row.get()][column.incrementAndGet()] = dto.getCGST();
            dataArray[row.get()][column.incrementAndGet()] = dto.getSGST();
            dataArray[row.get()][column.incrementAndGet()] = dto.getCess();

        });

        cells.importArray(dataArray, 0, 0);
    }

}
