/**
 * 
 */
package com.ey.advisory.app.services.einvoice.reports;

import java.util.List;

import com.ey.advisory.app.docs.dto.einvoice.EInvMangmntScreenDownloadReqDto;
import com.ey.advisory.app.docs.dto.einvoice.EInvMangmntScreenDownloadResponseDto;

/**
 * @author Laxmi.Salukuti
 *
 */
public interface EInvManagmntScreenDownloadDao {

	List<EInvMangmntScreenDownloadResponseDto> getEInvMngmtScreendwnld(
			EInvMangmntScreenDownloadReqDto request);

}
