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

import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.data.entities.client.OutwardTransDocError;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.data.repositories.client.DocErrorRepository;
import com.ey.advisory.app.data.repositories.client.DocRepository;
import com.ey.advisory.app.docs.dto.DocErrorDto;
import com.ey.advisory.app.docs.dto.DocumentDto;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.core.dto.GstnDocSearchReqDto;
import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;
import com.ey.advisory.core.search.SearchResult;
import com.ey.advisory.core.search.SearchService;

@Service("GstnBasicDocSearchService")
public class GstnBasicDocSearchService implements SearchService{
	
	private static final Logger LOGGER = LoggerFactory
			.getLogger(GstnBasicDocSearchService.class);
	
	@Autowired
	private DocRepository docRepository;
	
	@Autowired
	private DocErrorRepository docErrorRepository;
	
	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstinInfoRepository;

	@Override
	public <R> SearchResult<R> find(SearchCriteria criteria,
			PageRequest pageReq, Class<? extends R> retType) {
		// TODO Auto-generated method stub
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format("Initiated Document Search using "
					+ "the parameters: %s", criteria));
		}
		
		GstnDocSearchReqDto searchParams = (GstnDocSearchReqDto) criteria;
		
		List<String> gstins = new ArrayList<>();
		if (null != searchParams.getEntityId()
				&& !searchParams.getEntityId().isEmpty()) {
			if (searchParams.getGstins().isEmpty()) {
				gstins = gstinInfoRepository
						.findByEntityId(searchParams.getEntityId());
				searchParams.setGstins(gstins);
			}
		}else{//GSTIN search should always be based on given/selected Entity Ids
			searchParams.setGstins(gstins);
		}
		
		Page<OutwardTransDocument> page = docRepository
				.findGstnDocsBySearchCriteria(searchParams,
						org.springframework.data.domain.PageRequest.of(
								pageReq.getPageNo(), pageReq.getPageSize()));
		List<OutwardTransDocument> docs = page.getContent();
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
		
		if (docs == null || docs.isEmpty())
			return new SearchResult<>(null, pageReq, totalCount);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Mapping the OutwardTransDocument "
					+ "objects to DocumentDto Objects...");							
		}
		// Use model mapper to convert the list of OutwardTransDoc objects to
		// the list of DocumentDto objects. Since DocumentDto is a subclass of
		// OutwardTransDoc object, the mapper can easily map all properties
		// from an OutwardTransDoc object to a DocumentDto object.
		ModelMapper modelMapper = new ModelMapper();
		List<DocumentDto> retDocs = docs.stream()
				.map(doc -> modelMapper.map(doc, DocumentDto.class))
				.collect(Collectors.toCollection(ArrayList::new));

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format("Mapped the OutwardTransDocument "
					+ "objects to DocumentDto Objects. About to load the "
					+ "error list for all the documents obtained. No: of docs "
					+ "for which errors will be loaded = %d..", docs.size()));							
		}
		
		// Get the list of ids for the documents and use it to find the 
		// associated list of OutwardTransDoc error objects.
		List<Long> ids = docs.stream()
				.map(doc -> doc.getId())
				.collect(Collectors.toCollection(ArrayList::new));
		List<OutwardTransDocError> errors = 
				docErrorRepository
				.findByDocHeaderIdsAndValType(ids,
						GSTConstants.STRUCTURAL_VALIDATIONS);
		
		if (LOGGER.isDebugEnabled()) {
			int noOfErrors = (errors != null) ? errors.size() : 0;
			LOGGER.debug(String.format("Loaded all the errors for %d Ids. "
					+ "Total No: of errors obtained = %d. "
					+ "About to segregate the errors based on Doc Ids...", 
					docs.size(), noOfErrors));							
		}
		
		// Return if the DocumentDto List if there are no errors.
		if (errors == null || errors.isEmpty()) 
				return new SearchResult(retDocs,pageReq,totalCount);
		
		// Segregate the errors according to the input document ids.
		Map<Long, List<OutwardTransDocError>> errMap = 
				errors.stream()
				.collect(Collectors.groupingBy(e -> e.getDocHeaderId()));
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format("Seggregated the documents based "
					+ "on Ids. No: of Docs that have errors associated = %d. "
					+ "About to convert OtwardTransDocError objects to "
					+ "DocErrorDto objects and attach them to corresponding "
					+ "DocumentDto objects...", 
					errMap.size()));							
		}
		
		// Populate the DocumentDto objects with respective errors.
		retDocs.stream().forEach(doc -> {
			List<OutwardTransDocError> docErrors = errMap.get(doc.getId());
			if (docErrors != null && !docErrors.isEmpty()) {
				List<DocErrorDto> list =docErrors.stream().map(
						o -> convertToDocErrorDto(o)).collect(
								Collectors.toCollection(ArrayList::new)); 
				doc.setErrors(list);
			}
		});

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format("Created the final List of DocumentDto "
					+ "objects. Returning the list of size: %d!!", 
					retDocs.size()));							
		}	
		return new SearchResult(retDocs,pageReq,totalCount);
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
