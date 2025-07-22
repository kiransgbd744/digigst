/**
 * 
 */
package com.ey.advisory.app.services.search.docsearch;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.app.data.entities.client.EwbEntity;
import com.ey.advisory.app.data.entities.client.OutwardTransDocError;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.data.repositories.client.DocErrorRepository;
import com.ey.advisory.app.data.repositories.client.DocRepository;
import com.ey.advisory.app.data.repositories.client.EwbRepository;
import com.ey.advisory.app.docs.dto.DocErrorDto;
import com.ey.advisory.app.docs.dto.DocumentDto;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.core.dto.DocSearchReqDto;
import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;
import com.ey.advisory.core.search.SearchResult;
import com.ey.advisory.core.search.SearchService;
import com.ey.advisory.ewb.client.repositories.EwbLifecycleRepository;
import com.ey.advisory.ewb.common.EyEwbCommonUtil;
import com.ey.advisory.repositories.client.Gstr1BatchRepository;
import com.google.common.base.Strings;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Laxmi.Salukuti
 *
 */
@Service("EWBFormScreenDocSearchService")
@Slf4j
public class EWBFormScreenDocSearchService implements SearchService {

	@Autowired
	private DocRepository docRepository;

	@Autowired
	private DocErrorRepository docErrorRepository;

	@Autowired
	private EwbRepository ewbRepository;

	@Autowired
	private Gstr1FileStatusRepository gstr1FileStatusRepository;

	@Autowired
	private EwbLifecycleRepository ewbLifecycleRepository;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstinInfoRepository;

	@Autowired
	@Qualifier("BasicDocSearchDataSecParams")
	private BasicDocSearchDataSecParams basicDocSearchDataSecParams;

	@Autowired
	@Qualifier("batchSaveStatusRepository")
	private Gstr1BatchRepository gstr1BatchRepository;

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

				LocalDateTime ewbDate = doc.geteWayBillDate();
				if (ewbDate != null) {
					LocalDateTime convertEwbDate = EYDateUtil
							.toISTDateTimeFromUTC(ewbDate);
					doc.seteWayBillDate(convertEwbDate);
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
						doc.setFromPlace(ewbObj.getFromPlace());
						doc.setReasonCode(ewbObj.getReasonCode());
						LocalDateTime ewbDate1 = ewbObj.getEwbDate();
						if (ewbDate1 != null) {
							LocalDateTime convertEwbDate = EYDateUtil
									.toISTDateTimeFromUTC(ewbDate1);
							doc.seteWayBillDate(convertEwbDate);
						}
						LocalDateTime validUpto = ewbObj.getValidUpto();
						if (validUpto != null) {
							LocalDateTime convertvalidUpto = EYDateUtil
									.toISTDateTimeFromUTC(validUpto);
							doc.setValidUpto(convertvalidUpto);
						}
					}
					List<Object> partBUpdatedTime = ewbLifecycleRepository
							.findEwbByEwbNumAndEwbFun(ewbNum);
					if (partBUpdatedTime != null
							|| !partBUpdatedTime.isEmpty()) {
						LocalDateTime partBTimeStamp = (LocalDateTime) partBUpdatedTime
								.get(0);
						LocalDateTime partBTime = EYDateUtil
								.toISTDateTimeFromUTC(partBTimeStamp);
						doc.setEwbPartBUpdated(partBTime);
					}
				}
				Long fileId = doc.getAcceptanceId();
				if (fileId != null) {
					String fileName = gstr1FileStatusRepository
							.getFileName(fileId);
					doc.setFileName(fileName);
				}
				Long batchId = doc.getGstnBatchId();
				if (batchId != null) {
					String refId = gstr1BatchRepository.getRefId(batchId);
					doc.setRefId(refId);
				}
				String toPlace = EyEwbCommonUtil.getToPlace(
						doc.getShipToLocation(), doc.getCustOrSuppAddress4(),
						doc.getDocCategory());
				doc.setToPlace(toPlace);

				LocalDateTime irnDate = doc.getIrnDate();
				if (irnDate != null) {
					LocalDateTime convertIrnDate = EYDateUtil
							.toISTDateTimeFromUTC(irnDate);
					doc.setIrnDate(convertIrnDate);
				}
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
			LocalDateTime ewbDate = doc.geteWayBillDate();
			if (ewbDate != null) {
				LocalDateTime convertEwbDate = EYDateUtil
						.toISTDateTimeFromUTC(ewbDate);
				doc.seteWayBillDate(convertEwbDate);
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
					doc.setFromPlace(ewbObj.getFromPlace());
					doc.setReasonCode(ewbObj.getReasonCode());
					LocalDateTime validUpto = ewbObj.getValidUpto();
					LocalDateTime ewbDate1 = ewbObj.getEwbDate();
					if (ewbDate1 != null) {
						LocalDateTime convertEwbDate = EYDateUtil
								.toISTDateTimeFromUTC(ewbDate1);
						doc.seteWayBillDate(convertEwbDate);
					}
					if (validUpto != null) {
						LocalDateTime convertvalidUpto = EYDateUtil
								.toISTDateTimeFromUTC(validUpto);
						doc.setValidUpto(convertvalidUpto);
					}
				}
				List<Object> partBUpdatedTime = ewbLifecycleRepository
						.findEwbByEwbNumAndEwbFun(ewbNum);
				if (partBUpdatedTime != null || !partBUpdatedTime.isEmpty()) {
					LocalDateTime partBTimeStamp = (LocalDateTime) partBUpdatedTime
							.get(0);
					LocalDateTime partBTime = EYDateUtil
							.toISTDateTimeFromUTC(partBTimeStamp);
					doc.setEwbPartBUpdated(partBTime);
				}
			}
			Long fileId = doc.getAcceptanceId();
			if (fileId != null) {
				String fileName = gstr1FileStatusRepository.getFileName(fileId);
				doc.setFileName(fileName);
			}
			Long batchId = doc.getGstnBatchId();
			if (batchId != null) {
				String refId = gstr1BatchRepository.getRefId(batchId);
				doc.setRefId(refId);
			}
			String toPlace = EyEwbCommonUtil.getToPlace(doc.getShipToLocation(),
					doc.getCustOrSuppAddress4(), doc.getDocCategory());
			doc.setToPlace(toPlace);
			LocalDateTime irnDate = doc.getIrnDate();
			if (irnDate != null) {
				LocalDateTime convertIrnDate = EYDateUtil
						.toISTDateTimeFromUTC(irnDate);
				doc.setIrnDate(convertIrnDate);
			}
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
