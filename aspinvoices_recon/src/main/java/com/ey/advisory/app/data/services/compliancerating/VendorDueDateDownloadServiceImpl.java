package com.ey.advisory.app.data.services.compliancerating;

import static com.ey.advisory.common.FormatValidationUtil.isPresent;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.aspose.cells.Cells;
import com.aspose.cells.SaveFormat;
import com.aspose.cells.Workbook;
import com.ey.advisory.app.data.entities.client.asprecon.VendorDueDateEntity;
import com.ey.advisory.app.data.repositories.client.asprecon.VendorDueDateRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.core.config.ConfigConstants;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Jithendra.B
 *
 */
@Slf4j
@Component("VendorDueDateDownloadServiceImpl")
public class VendorDueDateDownloadServiceImpl
		implements VendorDueDateDownloadService {

	@Autowired
	private VendorDueDateRepository repo;

	@Autowired
	private CommonUtility commonUtility;

	private static final DateTimeFormatter format = DateTimeFormatter
			.ofPattern("dd-MM-yyyy");

	@Override
	public Workbook getVendorDueDateData(Long entityId) {
		try {
			List<VendorDueDateEntity> entities = repo
					.findByEntityIdAndIsDeleteFalse(entityId);

			if (entities.isEmpty()) {
				return null;
			}

			List<VendorDueDateDTO> dtoList = entities.stream()
					.map(o -> convertEntityToDto(o))
					.collect(Collectors.toList());

			Workbook workbook = null;
			int startRow = 1;
			int startcolumn = 0;
			boolean isHeaderRequired = false;

			if (!dtoList.isEmpty()) {

				String[] headers = commonUtility
						.getProp("vendor.due.date.report.header").split(",");

				workbook = commonUtility.createWorkbookWithExcelTemplate(
						"ReportTemplates", "Vendors_Due_Date.xlsx");

				Cells reportCells = workbook.getWorksheets().get(0).getCells();

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("No. of rows to be written int XLSX are {}",
							dtoList.size());
				}
				reportCells.importCustomObjects(dtoList, headers,
						isHeaderRequired, startRow, startcolumn, dtoList.size(),
						true, "yyyy-mm-dd", false);

				workbook.save(ConfigConstants.VENDOR_DUE_DATE, SaveFormat.XLSX);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Workbook has been generated successfully with the"
									+ " error response list in the directory : %s",
							workbook.getAbsolutePath());
				}

			}
			return workbook;
		} catch (Exception e) {
			LOGGER.error("Error in getVendorDueDateData", e);
			throw new AppException(e);
		}
	}

	private VendorDueDateDTO convertEntityToDto(VendorDueDateEntity e) {

		VendorDueDateDTO dto = new VendorDueDateDTO();

		dto.setTaxPeriod(convertToStringAndConcat(e.getTaxPeriod(), true));
		dto.setReturnType(convertToStringAndConcat(e.getReturnType(), false));
		dto.setDueDate(
				e.getDueDate() != null ? e.getDueDate().format(format) : null);
		dto.setVendorStateCode(
				convertToStringAndConcat(e.getVendorStateCode(), false));
		dto.setReturnFilingFrequency(e.getReturnFilingFrequency());
		return dto;
	}

	private String convertToStringAndConcat(Object obj, boolean isConcat) {

		if (!isPresent(obj)) {
			return null;
		}
		if (isConcat) {
			return "'" + obj.toString().trim();
		}
		return obj.toString().trim();
	}

}
