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

import com.ey.advisory.app.data.entities.client.Anx2InwardErrorHeaderEntity;
import com.ey.advisory.app.data.entities.client.InwardTransDocError;
import com.ey.advisory.app.data.repositories.client.Anx2InwardErrHeaderRepository;
import com.ey.advisory.app.data.repositories.client.gstr2.InwardTransDocErrRepository;
import com.ey.advisory.app.docs.dto.DocErrorDto;
import com.ey.advisory.app.docs.dto.InwardSvErrDocDto;
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
@Service("BasicInwardSvErrDocSearchService")
public class BasicInwardSvErrDocSearchService implements SearchService{
	
	private static final Logger LOGGER = LoggerFactory
			.getLogger(BasicInwardSvErrDocSearchService.class);
	
	
	@Autowired
	@Qualifier("Anx2InwardErrHeaderRepository")
	private Anx2InwardErrHeaderRepository inwardTransDocSvErrRepository;
	
	@Autowired
	private InwardTransDocErrRepository inwardDocErrorRepository;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public <R> SearchResult<R> find(SearchCriteria criteria,
			PageRequest pageReq, Class<? extends R> retType) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Initiated Inward Structurally "
					+ "Validated Error Document Search");
		}
		// Load the list of Structurally Validated Error objects using the
		// search criteria.
		DocSearchReqDto searchParams = (DocSearchReqDto) criteria;
		
		Page<Anx2InwardErrorHeaderEntity> page = inwardTransDocSvErrRepository
				.findDocsBySearchCriteria(searchParams,
						org.springframework.data.domain.PageRequest.of(
								pageReq.getPageNo(), pageReq.getPageSize()));
		
		List<Anx2InwardErrorHeaderEntity> docs = page.getContent();
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
			LOGGER.debug("Mapping the Anx2InwardErrorHeaderEntity "
					+ "objects to InwardSvErrDocDto Objects...");							
		}
		
		// Use model mapper to convert the list of
		// Anx2InwardErrorHeaderEntity(InwardSVError) objects to
		// the list of InwardSvErrDocDto objects. Since InwardSvErrDocDto 
		// is a subclass of Anx2InwardErrorHeaderEntity object, 
		// the mapper can easily map all properties from an 
		// Anx2InwardErrorHeaderEntity object to a InwardSvErrDocDto
		// object.
		ModelMapper modelMapper = new ModelMapper();
		List<InwardSvErrDocDto> retDocs = docs.stream()
				.map(doc -> modelMapper.map(doc, InwardSvErrDocDto.class))
				.collect(Collectors.toCollection(ArrayList::new));
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format(
					"Mapped the Anx2InwardErrorHeaderEntity "
							+ "objects to InwardSvErrDocDto Objects. "
							+ "About to load the error list for all the "
							+ "documents obtained. No: of docs "
							+ "for which errors will be loaded = %d..",
					docs.size()));
		}
		// Get the list of ids for the documents and use it to find the
		// associated list of InwardTrans Doc SV error objects.
		List<Long> ids = docs.stream().map(doc -> doc.getId())
				.collect(Collectors.toCollection(ArrayList::new));
		
		List<InwardTransDocError> errors = inwardDocErrorRepository
				.findSvErrByDocHeaderIds(ids,
						GSTConstants.STRUCTURAL_VALIDATIONS);
		if(LOGGER.isDebugEnabled()){
			int noOfErrors = (errors != null && !errors.isEmpty())
					? errors.size() : 0;
			LOGGER.debug(String.format("Loaded all the errors for %d Ids. "
					+ "Total No: of errors obtained = %d. ",					 
					docs.size(), noOfErrors));		
		}
		// Return if the InwardSvErrDocDto List if there are no errors.
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
		Map<Long, List<InwardTransDocError>> errMap = errors.stream()
				.collect(Collectors.groupingBy(e -> e.getDocHeaderId()));
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format("Seggregated the documents based "
					+ "on Ids. No: of Docs that have errors associated = %d. "
					+ "About to convert InwardSvErrDocDto objects to "
					+ "DocErrorDto objects and attach them to corresponding "
					+ "InwardSvErrDocDto objects...", 
					errMap.size()));							
		}
		
		// Populate the InwardSvErrDocDto objects with respective errors.
		retDocs.stream().forEach(doc -> {
			List<InwardTransDocError> docSvErrors = errMap.get(doc.getId());
			if(docSvErrors != null && !docSvErrors.isEmpty()){
				List<DocErrorDto> docSvErrList = docSvErrors.stream()
						.map(e -> convertToDocErrorDto(e))
						.collect(Collectors.toCollection(ArrayList::new));
				doc.setErrors(docSvErrList);
			}
		});
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format(
					"Created the final List of InwardSvErrDocDto "
							+ "objects. Returning the list of size: %d!!",
					retDocs.size()));
		}
		
		return new SearchResult(retDocs,pageReq,totalCount);
	}
	
	/**
	 * Convert an input InwardTransDocError object to DocErrorDto object.
	 * 
	 * @param error
	 * @return
	 */
	private DocErrorDto convertToDocErrorDto(InwardTransDocError error) {
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
