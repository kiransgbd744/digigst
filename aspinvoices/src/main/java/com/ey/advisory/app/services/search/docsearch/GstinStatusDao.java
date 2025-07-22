/**
 * 
 */
package com.ey.advisory.app.services.search.docsearch;

import java.util.List;

import com.ey.advisory.app.docs.dto.GstinStatusDocumentDto;
import com.ey.advisory.app.docs.dto.einvoice.GstinStatusDocSearchReqDto;
import com.ey.advisory.core.search.PageRequest;

/**
 * @author Shashikant.Shukla
 *
 */
public interface GstinStatusDao {

	public List<GstinStatusDocumentDto> getGstinStatusListing(
			GstinStatusDocSearchReqDto criteria, PageRequest pageReq);

}
