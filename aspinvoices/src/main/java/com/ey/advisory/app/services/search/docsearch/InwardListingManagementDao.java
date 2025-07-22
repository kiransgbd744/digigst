/**
 * 
 */
package com.ey.advisory.app.services.search.docsearch;

import java.util.List;

import com.ey.advisory.app.docs.dto.InwardInvoiceFilterListingReqDto;
import com.ey.advisory.app.docs.dto.InwardInvoiceFilterListingResponseDto;
import com.ey.advisory.app.docs.dto.InwardListingReqDto;
import com.ey.advisory.app.docs.dto.InwardListingResponseDto;
import com.ey.advisory.core.search.PageRequest;

/**
 * @author Laxmi.Salukuti
 *
 */
public interface InwardListingManagementDao {

	List<InwardListingResponseDto> getInwardListing(InwardListingReqDto request,
			PageRequest pageReq);

}
