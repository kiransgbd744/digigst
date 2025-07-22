package com.ey.advisory.app.services.docs;

import java.util.List;

import com.aspose.cells.Workbook;

public interface PermissibleITC10PerctDwnldService {

	Workbook getPermissibleReport(List<String> gstnsList, String toTaxPeriod,
			String fromTaxPeriod, List<String> docType, String reconType,Long entityId);
}
