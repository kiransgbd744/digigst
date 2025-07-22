package com.ey.advisory.app.services.search.docsearch;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
import com.ey.advisory.app.data.entities.client.EwbEntity;
import com.ey.advisory.app.data.entities.client.GstrReturnStatusEntity;
import com.ey.advisory.app.data.entities.client.OutwardTransDocError;
import com.ey.advisory.app.data.entities.client.OutwardTransDocLineItem;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.data.repositories.client.DocErrorRepository;
import com.ey.advisory.app.data.repositories.client.DocRepository;
import com.ey.advisory.app.data.repositories.client.EwbRepository;
import com.ey.advisory.app.docs.dto.DocErrorDto;
import com.ey.advisory.app.docs.dto.DocumentDto;
import com.ey.advisory.app.util.EhcacheGstinTaxperiod;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.dto.DocSearchReqDto;
import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;
import com.ey.advisory.core.search.SearchResult;
import com.ey.advisory.core.search.SearchService;
import com.ey.advisory.ewb.common.EyEwbCommonUtil;
import com.google.common.base.Strings;

@Service("BasicDocSearchService")
public class BasicDocSearchService implements SearchService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(BasicDocSearchService.class);

	@Autowired
	private DocRepository docRepository;

	@Autowired
	private DocErrorRepository docErrorRepository;

	@Autowired
	private EwbRepository ewbRepository;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstinInfoRepository;

	@Autowired
	@Qualifier("BasicDocSearchDataSecParams")
	private BasicDocSearchDataSecParams basicDocSearchDataSecParams;

	@Autowired
	@Qualifier("EhcacheGstinTaxperiod")
	private EhcacheGstinTaxperiod ehcachegstinTaxPeriod;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public <R> SearchResult<R> find(SearchCriteria criteria,
			PageRequest pageReq, Class<? extends R> retType) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format(
					"Initiated Document Search using " + "the parameters: %s",
					criteria));
		}
		// Load the list of OutwardTransDoc objects using the search
		// criteria.
		DocSearchReqDto searchParams = (DocSearchReqDto) criteria;

		/**
		 * Start - Set Data Security Attributes
		 */
		searchParams = basicDocSearchDataSecParams
				.setDataSecuritySearchParams(searchParams);
		/**
		 * End - Set Data Security Attributes
		 */
		Page<OutwardTransDocument> page = docRepository
				.findDocsBySearchCriteria(searchParams,
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
		if (docs == null || docs.isEmpty())
			return new SearchResult<>(null, pageReq, totalCount);

		for (OutwardTransDocument outTrnsDoc : docs) {
			
			if (!Strings.isNullOrEmpty(outTrnsDoc.getTransportMode())) {
				outTrnsDoc.setTransportMode(outTrnsDoc.getTransportMode()
						.equalsIgnoreCase("SHIP/ROAD CUM SHIP") ? "SHIP"
								: outTrnsDoc.getTransportMode());
			}
			for (OutwardTransDocLineItem lineItem : outTrnsDoc.getLineItems()) {
				LOGGER.debug("Line Item {} : ", lineItem);
				if (lineItem.getItemAmount() == null
						|| lineItem.getItemAmount().compareTo(BigDecimal.ZERO) == 0) {
					LOGGER.debug("Setting Line item amount in Item Amount");
					lineItem.setItemAmount(lineItem.getLineItemAmt());
				}
			}
		}
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
			LOGGER.debug(String.format(
					"Mapped the OutwardTransDocument "
							+ "objects to DocumentDto Objects. About to load the "
							+ "error list for all the documents obtained. No: of docs "
							+ "for which errors will be loaded = %d..",
					docs.size()));
		}

		// Get the list of ids for the documents and use it to find the
		// associated list of OutwardTransDoc error objects.
		List<Long> ids = docs.stream().map(doc -> doc.getId())
				.collect(Collectors.toCollection(ArrayList::new));
		List<OutwardTransDocError> errors = docErrorRepository
				.findByDocHeaderIdsForBV(ids);

		if (LOGGER.isDebugEnabled()) {
			int noOfErrors = (errors != null) ? errors.size() : 0;
			LOGGER.debug(String.format(
					"Loaded all the errors for %d Ids. "
							+ "Total No: of errors obtained = %d. "
							+ "About to segregate the errors based on Doc Ids...",
					docs.size(), noOfErrors));
		}

		// Return if the DocumentDto List if there are no errors.
		if (errors == null || errors.isEmpty()) {
			retDocs.stream().forEach(doc -> {

				Long ewbNum = doc.getEwbNoresp();
				if (ewbNum != null) {
					String ewbNo = ewbNum.toString();
					EwbEntity ewbObj = ewbRepository.findEwbByEwbNum(ewbNo);
					if (ewbObj != null) {
						String transMode = ewbObj.getTransMode();
						if (!Strings.isNullOrEmpty(transMode)) {
							String transModeDesc = EyEwbCommonUtil
									.getTransModeDesc(transMode);
							doc.setTransportMode(transModeDesc);
						}
						doc.setVehicleType(ewbObj.getVehicleType());
						doc.setVehicleNo(ewbObj.getVehicleNum());
						doc.setTransportDocNo(ewbObj.getTransDocNum());
						doc.setTransportDocDate(ewbObj.getTransDocDate());
						doc.setTransporterID(ewbObj.getTransporterId());
						doc.setComputedDistance(ewbObj.getAspDistance());
						doc.setRemainingDistance(ewbObj.getRemDistance());
					}
				}
				/*
				 * LocalDateTime ewbDate = doc.geteWayBillDate(); if (ewbDate !=
				 * null) { LocalDateTime convertEwbDate = EYDateUtil
				 * .toISTDateTimeFromUTC(ewbDate);
				 * doc.seteWayBillDate(convertEwbDate); } LocalDateTime irnDate
				 * = doc.getIrnDate(); if (irnDate != null) { LocalDateTime
				 * convertIrnDate = EYDateUtil .toISTDateTimeFromUTC(irnDate);
				 * doc.setIrnDate(convertIrnDate); }
				 */
				LocalDateTime hciReceivedOn = doc.getHciReceivedOn();
				if (hciReceivedOn != null) {
					LocalDateTime convertHciReceivedOn = EYDateUtil
							.toISTDateTimeFromUTC(hciReceivedOn);
					doc.setHciReceivedOn(convertHciReceivedOn);
				}
				LocalDateTime javaReqReceivedOn = doc.getReqReceivedOn();
				if (javaReqReceivedOn != null) {
					LocalDateTime convertJavaReqReceivedOn = EYDateUtil
							.toISTDateTimeFromUTC(javaReqReceivedOn);
					doc.setReqReceivedOn(convertJavaReqReceivedOn);
				}
				LocalDateTime javaBeforeSavingOn = doc.getBeforeSavingOn();
				if (javaBeforeSavingOn != null) {
					LocalDateTime convertJavaBeforeSavingOn = EYDateUtil
							.toISTDateTimeFromUTC(javaBeforeSavingOn);
					doc.setBeforeSavingOn(convertJavaBeforeSavingOn);
				}
				if (doc.getTaxperiod() != null && doc.getSgstin() != null) {
					ehcachegstinTaxPeriod = StaticContextHolder.getBean(
							"EhcacheGstinTaxperiod", EhcacheGstinTaxperiod.class);
					GstrReturnStatusEntity entity1 = ehcachegstinTaxPeriod
							.isGstinFiled(doc.getSgstin(), doc.getTaxperiod(),
									"GSTR1", "FILED", TenantContext.getTenantId());
					GstrReturnStatusEntity entity2 = ehcachegstinTaxPeriod
							.isGstinFiled(doc.getSgstin(), doc.getTaxperiod(),
									"GSTR3B", "FILED", TenantContext.getTenantId());
					if(entity1 != null){
						doc.setGstr1FilingStatus(true);
						doc.setGstr1FilingDate(entity1.getFilingDate());
					} else {
						doc.setGstr1FilingStatus(false);
					}
					if(entity2 != null){
						doc.setGstr3BFilingStatus(true);
						doc.setGst3BFilingDate(entity2.getFilingDate());
					} else {
						doc.setGstr3BFilingStatus(false);
					}
					
				}
			});
			return new SearchResult(retDocs, pageReq, totalCount);
		}

		// Segregate the errors according to the input document ids.
		Map<Long, List<OutwardTransDocError>> errMap = errors.stream()
				.collect(Collectors.groupingBy(e -> e.getDocHeaderId()));

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format("Seggregated the documents based "
					+ "on Ids. No: of Docs that have errors associated = %d. "
					+ "About to convert OtwardTransDocError objects to "
					+ "DocErrorDto objects and attach them to corresponding "
					+ "DocumentDto objects...", errMap.size()));
		}

		// Populate the DocumentDto objects with respective errors.
		retDocs.stream().forEach(doc -> {
			List<OutwardTransDocError> docErrors = errMap.get(doc.getId());
			if (docErrors != null && !docErrors.isEmpty()) {
				List<DocErrorDto> list = docErrors.stream()
						.map(o -> convertToDocErrorDto(o))
						.collect(Collectors.toCollection(ArrayList::new));
				doc.setErrors(list);
			}
			Long ewbNum = doc.getEwbNoresp();
			if (ewbNum != null) {
				String ewbNo = ewbNum.toString();
				EwbEntity ewbObj = ewbRepository.findEwbByEwbNum(ewbNo);
				if (ewbObj != null) {
					String transMode = ewbObj.getTransMode();
					if (!Strings.isNullOrEmpty(transMode)) {
						String transModeDesc = EyEwbCommonUtil
								.getTransModeDesc(transMode);
						doc.setTransportMode(transModeDesc);
					}
					doc.setVehicleType(ewbObj.getVehicleType());
					doc.setVehicleNo(ewbObj.getVehicleNum());
					doc.setTransportDocNo(ewbObj.getTransDocNum());
					doc.setTransportDocDate(ewbObj.getTransDocDate());
					doc.setTransporterID(ewbObj.getTransporterId());
					doc.setComputedDistance(ewbObj.getAspDistance());
					doc.setRemainingDistance(ewbObj.getRemDistance());
				}
			}
			/*
			 * LocalDateTime ewbDate = doc.geteWayBillDate(); if (ewbDate !=
			 * null) { LocalDateTime convertEwbDate = EYDateUtil
			 * .toISTDateTimeFromUTC(ewbDate);
			 * doc.seteWayBillDate(convertEwbDate); } LocalDateTime irnDate =
			 * doc.getIrnDate(); if (irnDate != null) { LocalDateTime
			 * convertIrnDate = EYDateUtil .toISTDateTimeFromUTC(irnDate);
			 * doc.setIrnDate(convertIrnDate); }
			 */
			LocalDateTime hciReceivedOn = doc.getHciReceivedOn();
			if (hciReceivedOn != null) {
				LocalDateTime convertHciReceivedOn = EYDateUtil
						.toISTDateTimeFromUTC(hciReceivedOn);
				doc.setHciReceivedOn(convertHciReceivedOn);
			}
			LocalDateTime javaReqReceivedOn = doc.getReqReceivedOn();
			if (javaReqReceivedOn != null) {
				LocalDateTime convertJavaReqReceivedOn = EYDateUtil
						.toISTDateTimeFromUTC(javaReqReceivedOn);
				doc.setReqReceivedOn(convertJavaReqReceivedOn);
			}
			LocalDateTime javaBeforeSavingOn = doc.getBeforeSavingOn();
			if (javaBeforeSavingOn != null) {
				LocalDateTime convertJavaBeforeSavingOn = EYDateUtil
						.toISTDateTimeFromUTC(javaBeforeSavingOn);
				doc.setBeforeSavingOn(convertJavaBeforeSavingOn);
			}

			if (doc.getTaxperiod() != null && doc.getSgstin() != null) {
				ehcachegstinTaxPeriod = StaticContextHolder.getBean(
						"EhcacheGstinTaxperiod", EhcacheGstinTaxperiod.class);
				GstrReturnStatusEntity entity1 = ehcachegstinTaxPeriod
						.isGstinFiled(doc.getSgstin(), doc.getTaxperiod(),
								"GSTR1", "FILED", TenantContext.getTenantId());
				GstrReturnStatusEntity entity2 = ehcachegstinTaxPeriod
						.isGstinFiled(doc.getSgstin(), doc.getTaxperiod(),
								"GSTR3B", "FILED", TenantContext.getTenantId());
				if(entity1 != null){
					doc.setGstr1FilingStatus(true);
					doc.setGstr1FilingDate(entity1.getFilingDate());
				} else {
					doc.setGstr1FilingStatus(false);
				}
				if(entity2 != null){
					doc.setGstr3BFilingStatus(true);
					doc.setGst3BFilingDate(entity2.getFilingDate());
				} else {
					doc.setGstr3BFilingStatus(false);
				}
				
			}
		});

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format(
					"Created the final List of DocumentDto "
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
		return null;
	}

}
