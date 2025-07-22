package com.ey.advisory.app.services.reports;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.app.docs.dto.simplified.TDSDownlaodRequestDto;

/**
 * @author Sasidhar
 *
 * 
 */

@Component("TdsTotalReportHandler")
public class TdsTotalReportHandler {
    @Autowired
    @Qualifier("TdsTotalReportsServiceImpl")
    private TdsTotalReportsServiceImpl tdsTotalReportsServiceImpl;

    public Workbook downloadTdsTotalReport(TDSDownlaodRequestDto criteria) {

        return tdsTotalReportsServiceImpl.downloadReports(criteria, null);

    }

}
