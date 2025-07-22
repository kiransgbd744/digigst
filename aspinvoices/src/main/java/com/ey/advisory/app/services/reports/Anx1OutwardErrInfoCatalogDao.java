/**
 * 
 */
package com.ey.advisory.app.services.reports;

import java.util.List;

import com.ey.advisory.app.data.views.client.Anx1CatalogErrInfoResponseDto;
import com.ey.advisory.app.docs.dto.Anx1CatalogErrInfoReportsReqDto;

/**
 * @author Laxmi.Salukuti
 *
 */
public interface Anx1OutwardErrInfoCatalogDao {

	List<Anx1CatalogErrInfoResponseDto> getErrInfoCatalog(
			Anx1CatalogErrInfoReportsReqDto request);

}
