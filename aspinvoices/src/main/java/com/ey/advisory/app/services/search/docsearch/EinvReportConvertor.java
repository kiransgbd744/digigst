/**
 * 
 */
package com.ey.advisory.app.services.search.docsearch;

import java.util.List;

import com.ey.advisory.app.docs.dto.einvoice.EwbEinvDownloadRequestDto;
import com.ey.advisory.core.dto.EinvEwbDto;

/**
 * @author Sujith.Nanga
 *
 * 
 */
public interface EinvReportConvertor {
	
	List<EinvEwbDto> getEInvMngmtListing(EwbEinvDownloadRequestDto request);

}
