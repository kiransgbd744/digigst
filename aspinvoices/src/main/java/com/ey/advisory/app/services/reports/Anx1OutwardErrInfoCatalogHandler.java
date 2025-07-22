/**
 * 
 */
package com.ey.advisory.app.services.reports;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.app.docs.dto.Anx1CatalogErrInfoReportsReqDto;

/**
 * @author Laxmi.Salukuti
 *
 */
@Component("Anx1OutwardErrInfoCatalogHandler")
public class Anx1OutwardErrInfoCatalogHandler {

	@Autowired
	@Qualifier("Anx1OutwardErrInfoCatalogServiceImpl")
	private Anx1OutwardErrInfoCatalogService anx1OutwardErrInfoCatalogService;

	public Workbook downloadErrInfoCataloag(
			Anx1CatalogErrInfoReportsReqDto criteria) {

		return anx1OutwardErrInfoCatalogService.findErrInfoCatalog(criteria,
				null);

	}

}
