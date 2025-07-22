/**
 * 
 */
package com.ey.advisory.app.services.search.docsearch;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.repositories.client.gstr2.InwardTransDocRepository;
import com.ey.advisory.app.docs.dto.InwardInvoiceFilterListingReqDto;
import com.ey.advisory.app.docs.dto.InwardInvoiceFilterListingResponseDto;
import com.ey.advisory.app.docs.dto.InwardListingReqDto;
import com.ey.advisory.app.docs.dto.InwardListingResponseDto;
import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;
import com.ey.advisory.core.search.SearchResult;

/**
 * @author Laxmi.Salukuti
 *
 */
@Service("BasicInwardListingService")
public class BasicInwardListingService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(BasicInwardDocSearchService.class);

	@Autowired
	private InwardTransDocRepository inwardDocRepository;

	@Autowired
	@Qualifier("BasicInwardListingDataSecParams")
	private BasicInwardListingDataSecParams basicInwardListingDataSecParams;

	@Autowired
	@Qualifier("InwardListingManagementDaoImpl")
	private InwardListingManagementDaoImpl inwardListingManagementDaoImpl;

	@Autowired
	@Qualifier("InvoiceMngtInwardTotalCountService")
	private InvoiceMngtInwardTotalCountService invoiceMngtInwardTotalCountService;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <R> SearchResult<R> findList(SearchCriteria criteria,
			PageRequest pageReq, Class<? extends R> retType) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format(
					"Initiated Document Search using " + "the parameters: %s",
					criteria));
		}
		// Load the list of InwardTransDoc objects using the search
		// criteria.
		InwardListingReqDto searchParams = (InwardListingReqDto) criteria;
	//	InwardInvoiceFilterListingReqDto searchParams = (InwardInvoiceFilterListingReqDto) criteria;

		/**
		 * Start - Set Data Security Attributes
		 */
		searchParams = basicInwardListingDataSecParams
				.setDataSecuritySearchParams(searchParams);
		/**
		 * End - Set Data Security Attributes
		 */

		List<InwardListingResponseDto> resp = inwardListingManagementDaoImpl
				.getInwardListing(searchParams, pageReq);

		Integer totalCount = invoiceMngtInwardTotalCountService
				.getInvMngmtInwardCount(searchParams);

		return new SearchResult(resp, pageReq, totalCount);

	}
}