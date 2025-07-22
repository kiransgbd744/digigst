package com.ey.advisory.app.services.search.docsearch;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.data.repositories.client.DocErrorRepository;
import com.ey.advisory.app.data.repositories.client.DocRepository;
import com.ey.advisory.app.data.repositories.client.EwbRepository;
import com.ey.advisory.app.docs.dto.EInvoiceDocumentDto;
import com.ey.advisory.app.services.docs.einvoice.EinvoiceBasicDocSearchDataSecParams;
import com.ey.advisory.core.dto.EInvoiceDocSearchReqDto;
import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;
import com.ey.advisory.core.search.SearchResult;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;

/**
 * 
 * @author Umesha.M
 *
 */
@Service("BasicEInvoiceDocSearchService")
public class BasicEInvoiceDocSearchService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(BasicEInvoiceDocSearchService.class);
	@Autowired
	private DocRepository docRepository;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstinInfoRepository;

	@Autowired
	@Qualifier("EinvoiceBasicDocSearchDataSecParams")
	private EinvoiceBasicDocSearchDataSecParams basicDocSearchDataSecParams;

	@Autowired
	@Qualifier("EinvoiceMngtTotalCountService")
	private EinvoiceMngtTotalCountService einvoiceMngtTotalCountService;

	@Autowired
	private DocErrorRepository docErrorRepository;

	@Autowired
	@Qualifier("EwbRepository")
	private EwbRepository ewbRepository;

	@Autowired
	@Qualifier("EinvoiceManagementDaoImpl")
	private EinvoiceManagementDaoImpl einvoiceManagementDaoImpl;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	GSTNAuthTokenService gSTNAuthTokenService;

	/**
	 * 
	 * @param criteria
	 * @param pageReq
	 * @param retType
	 * @return
	 * 
	 * 		This method regarding Invoice Management Screen
	 */
	public <R> SearchResult<R> find(SearchCriteria criteria,
			PageRequest pageReq, Class<? extends R> retType) {
		// Load the list of OutwardTransDoc objects using the search
		// criteria.
		EInvoiceDocSearchReqDto searchParams = (EInvoiceDocSearchReqDto) criteria;

		/**
		 * Start - Set Data Security Attributes
		 */
		searchParams = basicDocSearchDataSecParams
				.setDataSecuritySearchParams(searchParams);
		/**
		 * End - Set Data Security Attributes
		 */
		Page<OutwardTransDocument> page = docRepository
				.findDocsByEinvoiceSearchCriteria(searchParams,
						org.springframework.data.domain.PageRequest.of(
								pageReq.getPageNo(), pageReq.getPageSize()));
		List<OutwardTransDocument> docs = page.getContent();
		long totalElements = page.getTotalElements();
		int totalCount = new Long(totalElements).intValue();
		if (LOGGER.isDebugEnabled()) {
			int noOfDocs = (docs != null ? docs.size() : 0);
			LOGGER.debug(String.format(String.format(
					"Obtained a page of "
							+ "search results. No: of results returned = %d",
					noOfDocs)));
			if (noOfDocs == 0) {
				LOGGER.debug("No docs found for the specified search criteria. "
						+ "Terminating the search process and returning");
			}
		}
		// If there are no results, then return an emtpy serach result. The
		// SearchResult constructor takes care of initializing it with an
		// empty list.
		if (docs.isEmpty())
			return new SearchResult<>(null, pageReq, totalCount);

		List<EInvoiceDocumentDto> dtos = new ArrayList<>();
		docs.forEach(doc -> {
			EInvoiceDocumentDto dto = new EInvoiceDocumentDto();

			dtos.add(dto);
		});
		return new SearchResult(dtos, pageReq, totalCount);
	}

	public <R> SearchResult<R> findNew(SearchCriteria criteria,
			PageRequest pageReq, Class<? extends R> retType) {

		EInvoiceDocSearchReqDto searchParams = (EInvoiceDocSearchReqDto) criteria;

		searchParams = basicDocSearchDataSecParams
				.setDataSecuritySearchParams(searchParams);

		List<EInvoiceDocumentDto> resp = einvoiceManagementDaoImpl
				.getEInvMngmtListing(searchParams, pageReq);

		Integer totalCount = einvoiceMngtTotalCountService
				.getEInvMngmtCount(searchParams);

		return new SearchResult(resp, pageReq, totalCount);
	}

}
