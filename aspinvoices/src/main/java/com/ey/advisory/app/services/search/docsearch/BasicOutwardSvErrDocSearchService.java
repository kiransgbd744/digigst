package com.ey.advisory.app.services.search.docsearch;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.Anx1OutWardErrHeader;
import com.ey.advisory.app.data.entities.client.OutwardTransDocError;
import com.ey.advisory.app.data.repositories.client.
											  Anx1OutWardErrHeaderRepository;
import com.ey.advisory.app.data.repositories.client.DocErrorRepository;
import com.ey.advisory.app.docs.dto.DocErrorDto;
import com.ey.advisory.app.docs.dto.OutwardSvErrDocDto;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.core.dto.DocSearchReqDto;
import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;
import com.ey.advisory.core.search.SearchResult;
import com.ey.advisory.core.search.SearchService;

/**
 * This service class is responsible for getting Structurally validated Error
 * Documents which are uploaded from file
 * 
 * @author Mohana.Dasari
 *
 */
@Service("BasicOutwardSvErrDocSearchService")
public class BasicOutwardSvErrDocSearchService implements SearchService{
	
	private static final Logger LOGGER = LoggerFactory
			.getLogger(BasicOutwardSvErrDocSearchService.class);
	
	@Autowired
	@Qualifier("anx1OutWardErrHeaderRepository")
	private Anx1OutWardErrHeaderRepository outwardTransDocSvErrRepository;
	
	@Autowired
	@Qualifier("DocErrorRepository")
	private DocErrorRepository docErrorRepository;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public <R> SearchResult<R> find(SearchCriteria criteria,
			PageRequest pageReq, Class<? extends R> retType) {
		// Load the list of Structurally Validated Error objects using the
		// search criteria.
		DocSearchReqDto searchParams = (DocSearchReqDto) criteria;
		
		Page<Anx1OutWardErrHeader> page = outwardTransDocSvErrRepository
				.findDocsBySearchCriteria(searchParams,
						org.springframework.data.domain.PageRequest.of(
								pageReq.getPageNo(), pageReq.getPageSize()));
		
		List<Anx1OutWardErrHeader> docs = page.getContent();
		long totalElements = page.getTotalElements();
		int totalCount = new Long(totalElements).intValue();
		if (LOGGER.isDebugEnabled()) {
			int noOfDocs = (docs != null ? docs.size(): 0);
			LOGGER.debug(String.format(String.format("Obtained a page of "
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
		if (docs == null || docs.isEmpty())
			return new SearchResult<>(null, pageReq, totalCount);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Mapping the Anx1OutWardErrHeader "
					+ "objects to DocumentDto Objects...");
		}
		// Use model mapper to convert the list of
		// Anx1OutWardErrHeader(OutwardSVError) objects to
		// the list of OutwardSvErrDocDto objects. Since OutwardSvErrDocDto
		// is a subclass of Anx1OutWardErrHeader object,
		// the mapper can easily map all properties from an
		// Anx1OutWardErrHeader object to a OutwardSvErrDocDto
		// object.
		ModelMapper modelMapper = new ModelMapper();
		List<OutwardSvErrDocDto> retDocs = docs.stream()
				.map(doc -> modelMapper.map(doc, OutwardSvErrDocDto.class))
				.collect(Collectors.toCollection(ArrayList::new));
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format(
					"Mapped the Anx1OutWardErrHeader "
							+ "objects to OutwardSvErrDocDto Objects. "
							+ "About to load the error list for all the "
							+ "documents obtained. No: of docs "
							+ "for which errors will be loaded = %d..",
					docs.size()));
		}
		
		// Get the list of ids for the documents and use it to find the
		// associated list of OutwardTrans Doc SV error objects.
		List<Long> ids = docs.stream().map(doc -> doc.getId())
				.collect(Collectors.toCollection(ArrayList::new));
		
		List<OutwardTransDocError> errors = docErrorRepository
				.findByDocHeaderIdsAndValType(ids,
						GSTConstants.STRUCTURAL_VALIDATIONS);
		
		if(LOGGER.isDebugEnabled()){
			int noOfErrors = (errors != null && !errors.isEmpty())
					? errors.size() : 0;
			LOGGER.debug(String.format("Loaded all the errors for %d Ids. "
					+ "Total No: of errors obtained = %d. ",					 
					docs.size(), noOfErrors));		
		}
		
		// Return if the OutwardSvErrDocDto List if there are no errors.
		if (errors == null || errors.isEmpty()){
			if(LOGGER.isDebugEnabled()){
				int noOfErrors = (errors != null && !errors.isEmpty())
						? errors.size() : 0;
				LOGGER.debug(
						String.format("Returning Docs as there are no errors",
								docs.size(), noOfErrors));
			}
			return new SearchResult(retDocs, pageReq, totalCount);
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format(
					"About to segregate the errors based on Doc Ids..."));
		}
		
		// Segregate the errors according to the inward Sv err document ids.
		Map<Long, List<OutwardTransDocError>> errMap = errors.stream()
				.collect(Collectors.groupingBy(e -> e.getDocHeaderId()));
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format("Seggregated the documents based "
					+ "on Ids. No: of Docs that have errors associated = %d. "
					+ "About to convert OutwardSvErrDocDto objects to "
					+ "DocErrorDto objects and attach them to corresponding "
					+ "OutwardSvErrDocDto objects...", 
					errMap.size()));							
		}
		
		// Populate the InwardSvErrDocDto objects with respective errors.
		retDocs.stream().forEach(doc -> {
			List<OutwardTransDocError> docSvErrors = errMap.get(doc.getId());
			if (docSvErrors != null && !docSvErrors.isEmpty()) {
				List<DocErrorDto> docSvErrList = docSvErrors.stream()
						.map(e -> convertToDocErrorDto(e))
						.collect(Collectors.toCollection(ArrayList::new));
				doc.setErrors(docSvErrList);
			}
		});
				
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format(
					"Created the final List of OutwardSvErrDocDto "
							+ "objects. Returning the list of size: %d!!",
					retDocs.size()));
		}
				
		return new SearchResult(retDocs, pageReq, totalCount);
	}
	
	/**
	 * Convert an input OutwardTransDocError object to DocErrorDto object.
	 * 
	 * @param error
	 * @return
	 */
	private DocErrorDto convertToDocErrorDto(OutwardTransDocError error) {
		DocErrorDto dto = new DocErrorDto();
		dto.setIndex(error.getItemIndex());
		dto.setErrorCode(error.getErrorCode());
		dto.setErrorDesc(error.getErrorDesc());
		dto.setErrorFields(error.getErrorField());
		dto.setErrorType(error.getErrorType());
		return dto;
	}

	@Override
	public <R> Stream<R> find(SearchCriteria criteria,
			Class<? extends R> retType) {
		// TODO Auto-generated method stub
		return null;
	}

}
