package com.ey.advisory.app.services.search.docsearch;

import java.math.BigDecimal;
import java.math.BigInteger;
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
import com.ey.advisory.app.data.entities.client.InwardTransDocError;
import com.ey.advisory.app.data.entities.client.InwardTransDocLineItem;
import com.ey.advisory.app.data.entities.client.InwardTransDocument;
import com.ey.advisory.app.data.repositories.client.gstr2.InwardTransDocErrRepository;
import com.ey.advisory.app.data.repositories.client.gstr2.InwardTransDocRepository;
import com.ey.advisory.app.docs.dto.DocErrorDto;
import com.ey.advisory.app.docs.dto.gstr2.InwardDocumentDto;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.core.dto.DocSearchReqDto;
import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;
import com.ey.advisory.core.search.SearchResult;
import com.ey.advisory.core.search.SearchService;

/**
 * 
 * @author Mohana.Dasari
 *
 */
@Service("BasicInwardDocSearchService")
public class BasicInwardDocSearchService implements SearchService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(BasicInwardDocSearchService.class);

	@Autowired
	private InwardTransDocRepository inwardDocRepository;

	@Autowired
	private InwardTransDocErrRepository InwardDocErrRepository;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstinInfoRepository;

	@Autowired
	@Qualifier("BasicInwardDocSearchDataSecParams")
	private BasicInwardDocSearchDataSecParams docSearchDataSecParams;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public <R> SearchResult<R> find(SearchCriteria criteria,
			PageRequest pageReq, Class<? extends R> retType) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format(
					"Initiated Document Search using " + "the parameters: %s",
					criteria));
		}
		// Load the list of InwardTransDoc objects using the search
		// criteria.
		DocSearchReqDto searchParams = (DocSearchReqDto) criteria;

		/**
		 * Start - Set Data Security Attributes
		 */
		searchParams = docSearchDataSecParams
				.setDataSecuritySearchParams(searchParams);
		/**
		 * End - Set Data Security Attributes
		 */

		Page<InwardTransDocument> page = inwardDocRepository
				.findDocsBySearchCriteria(searchParams,
						org.springframework.data.domain.PageRequest.of(
								pageReq.getPageNo(), pageReq.getPageSize()));
		List<InwardTransDocument> docs = page.getContent();
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
			LOGGER.debug("Mapping the InwardTransDocument "
					+ "objects to DocumentDto Objects...");
		}
		// Use model mapper to convert the list of OutwardTransDoc objects to
		// the list of DocumentDto objects. Since DocumentDto is a subclass of
		// OutwardTransDoc object, the mapper can easily map all properties
		// from an OutwardTransDoc object to a DocumentDto object.
		ModelMapper modelMapper = new ModelMapper();
		List<InwardDocumentDto> retDocs = docs.stream()
				.map(doc -> modelMapper.map(doc, InwardDocumentDto.class))
				.collect(Collectors.toCollection(ArrayList::new));

		retDocs.stream().forEach(doc -> {
			String docType = doc.getDocType();
			if (docType != null && (docType.equalsIgnoreCase("CR")
					|| docType.equalsIgnoreCase("C")
					|| docType.equalsIgnoreCase("RCR")
					|| docType.equalsIgnoreCase("ADJ"))) {
				for (InwardTransDocLineItem item : doc.getLineItems()) {

					if (item.getAdjustedCessAmtAdvalorem() != null) {
						item.setAdjustedCessAmtAdvalorem(CheckForNegativeValue(
								item.getAdjustedCessAmtAdvalorem()));
					}
					if (item.getAdjustedCessAmtSpecific() != null) {
						item.setAdjustedCessAmtSpecific(CheckForNegativeValue(
								item.getAdjustedCessAmtSpecific()));
					}
					if (item.getAdjustedCgstAmt() != null) {
						item.setAdjustedCgstAmt(CheckForNegativeValue(
								item.getAdjustedCgstAmt()));
					}
					if (item.getAdjustedIgstAmt() != null) {
						item.setAdjustedIgstAmt(CheckForNegativeValue(
								item.getAdjustedIgstAmt()));
					}
					if (item.getAdjustedSgstAmt() != null) {
						item.setAdjustedSgstAmt(CheckForNegativeValue(
								item.getAdjustedSgstAmt()));
					}
					if (item.getAdjustedStateCessAmt() != null) {
						item.setAdjustedStateCessAmt(CheckForNegativeValue(
								item.getAdjustedStateCessAmt()));
					}
					if (item.getAdjustedTaxableValue() != null) {
						item.setAdjustedTaxableValue(CheckForNegativeValue(
								item.getAdjustedTaxableValue()));
					}
					if (item.getAvailableCess() != null) {
						item.setAvailableCess(
								CheckForNegativeValue(item.getAvailableCess()));
					}
					if (item.getAvailableCgst() != null) {
						item.setAvailableCgst(
								CheckForNegativeValue(item.getAvailableCgst()));
					}
					if (item.getAvailableIgst() != null) {
						item.setAvailableIgst(
								CheckForNegativeValue(item.getAvailableIgst()));
					}
					if (item.getAvailableSgst() != null) {
						item.setAvailableSgst(
								CheckForNegativeValue(item.getAvailableSgst()));
					}
					if (item.getAvailableTaxPayable() != null) {
						item.setAvailableTaxPayable(CheckForNegativeValue(
								item.getAvailableTaxPayable()));
					}
					// if(item.getCessAmount() != null){
					// item.setCessAmount(CheckForNegativeValue(item.getCessAmount()));
					// }
					if (item.getCessAmountAdvalorem() != null) {
						item.setCessAmountAdvalorem(CheckForNegativeValue(
								item.getCessAmountAdvalorem()));
					}
					if (item.getCessAmountSpecific() != null) {
						item.setCessAmountSpecific(CheckForNegativeValue(
								item.getCessAmountSpecific()));
					}
					if (item.getCgstAmount() != null) {
						item.setCgstAmount(
								CheckForNegativeValue(item.getCgstAmount()));
					}
					if (item.getCustomDuty() != null) {
						item.setCustomDuty(
								CheckForNegativeValue(item.getCustomDuty()));
					}
					if (item.getEligibleCess() != null) {
						item.setEligibleCess(
								CheckForNegativeValue(item.getEligibleCess()));
					}
					if (item.getEligibleCgst() != null) {
						item.setEligibleCgst(
								CheckForNegativeValue(item.getEligibleCgst()));
					}
					if (item.getEligibleCess() != null) {
						item.setEligibleCess(
								CheckForNegativeValue(item.getEligibleCess()));
					}
					if (item.getEligibleCgst() != null) {
						item.setEligibleCgst(
								CheckForNegativeValue(item.getEligibleCgst()));
					}
					if (item.getEligibleIgst() != null) {
						item.setEligibleIgst(
								CheckForNegativeValue(item.getEligibleIgst()));
					}
					if (item.getEligibleSgst() != null) {
						item.setEligibleSgst(
								CheckForNegativeValue(item.getEligibleSgst()));
					}
					if (item.getEligibleTaxPayable() != null) {
						item.setEligibleTaxPayable(CheckForNegativeValue(
								item.getEligibleTaxPayable()));
					}
					if (item.getIgstAmount() != null) {
						item.setIgstAmount(
								CheckForNegativeValue(item.getIgstAmount()));
					}
					if (item.getInEligibleCess() != null) {
						item.setInEligibleCess(CheckForNegativeValue(
								item.getInEligibleCess()));
					}
					if (item.getInEligibleCgst() != null) {
						item.setInEligibleCgst(CheckForNegativeValue(
								item.getInEligibleCgst()));
					}
					if (item.getInEligibleIgst() != null) {
						item.setInEligibleIgst(CheckForNegativeValue(
								item.getInEligibleIgst()));
					}
					if (item.getInEligibleSgst() != null) {
						item.setInEligibleSgst(CheckForNegativeValue(
								item.getInEligibleSgst()));
					}
					if (item.getInEligibleTaxPayable() != null) {
						item.setInEligibleTaxPayable(CheckForNegativeValue(
								item.getInEligibleTaxPayable()));
					}
					if (item.getInvoiceAssessableAmount() != null) {
						item.setInvoiceAssessableAmount(CheckForNegativeValue(
								item.getInvoiceAssessableAmount()));
					}
					if (item.getInvoiceCessAdvaloremAmount() != null) {
						item.setInvoiceCessAdvaloremAmount(
								CheckForNegativeValue(
										item.getInvoiceCessAdvaloremAmount()));
					}
					if (item.getInvoiceCessSpecificAmount() != null) {
						item.setInvoiceCessSpecificAmount(CheckForNegativeValue(
								item.getInvoiceCessSpecificAmount()));
					}
					if (item.getInvoiceCgstAmount() != null) {
						item.setInvoiceCgstAmount(CheckForNegativeValue(
								item.getInvoiceCgstAmount()));
					}
					if (item.getInvoiceIgstAmount() != null) {
						item.setInvoiceIgstAmount(CheckForNegativeValue(
								item.getInvoiceIgstAmount()));
					}
					if (item.getInvoiceOtherCharges() != null) {
						item.setInvoiceOtherCharges(CheckForNegativeValue(
								item.getInvoiceOtherCharges()));
					}
					if (item.getInvoiceSgstAmount() != null) {
						item.setInvoiceSgstAmount(CheckForNegativeValue(
								item.getInvoiceSgstAmount()));
					}
					if (item.getInvoiceStateCessAmount() != null) {
						item.setInvoiceStateCessAmount(CheckForNegativeValue(
								item.getInvoiceStateCessAmount()));
					}
					if (item.getInvoiceValueFc() != null) {
						item.setInvoiceValueFc(CheckForNegativeValue(
								item.getInvoiceValueFc()));
					}
					if (item.getInvStateCessSpecificAmt() != null) {
						item.setInvStateCessSpecificAmt(CheckForNegativeValue(
								item.getInvStateCessSpecificAmt()));
					}
					if (item.getItemAmount() != null) {
						item.setItemAmount(
								CheckForNegativeValue(item.getItemAmount()));
					}
					if (item.getItemDiscount() != null) {
						item.setItemDiscount(
								CheckForNegativeValue(item.getItemDiscount()));
					}
					if (item.getLineItemAmt() != null) {
						item.setLineItemAmt(
								CheckForNegativeValue(item.getLineItemAmt()));
					}
					if (item.getMemoValCgst() != null) {
						item.setMemoValCgst(
								CheckForNegativeValue(item.getMemoValCgst()));
					}
					if (item.getMemoValIgst() != null) {
						item.setMemoValIgst(
								CheckForNegativeValue(item.getMemoValIgst()));
					}
					if (item.getMemoValSgst() != null) {
						item.setMemoValSgst(
								CheckForNegativeValue(item.getMemoValSgst()));
					}
					if (item.getPrecCessAmt() != null) {
						item.setPrecCessAmt(
								CheckForNegativeValue(item.getPrecCessAmt()));
					}
					if (item.getPrecIgstAmt() != null) {
						item.setPrecIgstAmt(
								CheckForNegativeValue(item.getPrecIgstAmt()));
					}
					if (item.getPrecCgstAmt() != null) {
						item.setPrecCgstAmt(
								CheckForNegativeValue(item.getPrecCgstAmt()));
					}
					if (item.getPrecSgstAmt() != null) {
						item.setPrecSgstAmt(
								CheckForNegativeValue(item.getPrecSgstAmt()));
					}
					if (item.getPrecInvoiceValue() != null) {
						item.setPrecInvoiceValue(CheckForNegativeValue(
								item.getPrecInvoiceValue()));
					}
					if (item.getOtherValues() != null) {
						item.setOtherValues(
								CheckForNegativeValue(item.getOtherValues()));
					}
					if (item.getPrecCessAmt() != null) {
						item.setPrecCessAmt(
								CheckForNegativeValue(item.getPrecCessAmt()));
					}
					if (item.getPrecCgstAmt() != null) {
						item.setPrecCgstAmt(
								CheckForNegativeValue(item.getPrecCgstAmt()));
					}
					if (item.getPrecIgstAmt() != null) {
						item.setPrecIgstAmt(
								CheckForNegativeValue(item.getPrecIgstAmt()));
					}
					if (item.getPrecInvoiceValue() != null) {
						item.setPrecInvoiceValue(CheckForNegativeValue(
								item.getPrecInvoiceValue()));
					}
					if (item.getPrecSgstAmt() != null) {
						item.setPrecSgstAmt(
								CheckForNegativeValue(item.getPrecSgstAmt()));
					}
					if (item.getPrecTaxableValue() != null) {
						item.setPrecTaxableValue(CheckForNegativeValue(
								item.getPrecTaxableValue()));
					}
					if (item.getPrecTotalTax() != null) {
						item.setPrecTotalTax(
								CheckForNegativeValue(item.getPrecTotalTax()));
					}
					if (item.getPreTaxAmount() != null) {
						item.setPreTaxAmount(
								CheckForNegativeValue(item.getPreTaxAmount()));
					}
					if (item.getSgstAmount() != null) {
						item.setSgstAmount(
								CheckForNegativeValue(item.getSgstAmount()));
					}
					if (item.getStateCessAmount() != null) {
						item.setStateCessAmount(CheckForNegativeValue(
								item.getStateCessAmount()));
					}
					if (item.getStateCessSpecificAmt() != null) {
						item.setStateCessSpecificAmt(CheckForNegativeValue(
								item.getStateCessSpecificAmt()));
					}
					if (item.getTaxableValue() != null) {
						item.setTaxableValue(
								CheckForNegativeValue(item.getTaxableValue()));
					}
					if (item.getTaxPayable() != null) {
						item.setTaxPayable(
								CheckForNegativeValue(item.getTaxPayable()));
					}
					if (item.getTcsAmount() != null) {
						item.setTcsAmount(
								CheckForNegativeValue(item.getTcsAmount()));
					}
					if (item.getTcsAmountIncomeTax() != null) {
						item.setTcsAmountIncomeTax(CheckForNegativeValue(
								item.getTcsAmountIncomeTax()));
					}
					if (item.getTcsCgstAmount() != null) {
						item.setTcsCgstAmount(
								CheckForNegativeValue(item.getTcsCgstAmount()));
					}
					if (item.getTcsIgstAmount() != null) {
						item.setTcsIgstAmount(
								CheckForNegativeValue(item.getTcsIgstAmount()));
					}
					if (item.getTcsSgstAmount() != null) {
						item.setTcsSgstAmount(
								CheckForNegativeValue(item.getTcsSgstAmount()));
					}
					if (item.getTdsCgstAmount() != null) {
						item.setTdsCgstAmount(
								CheckForNegativeValue(item.getTdsCgstAmount()));
					}
					if (item.getTdsIgstAmount() != null) {
						item.setTdsIgstAmount(
								CheckForNegativeValue(item.getTdsIgstAmount()));
					}
					if (item.getTdsSgstAmount() != null) {
						item.setTdsSgstAmount(
								CheckForNegativeValue(item.getTdsSgstAmount()));
					}
					if (item.getTotalItemAmount() != null) {
						item.setTotalItemAmount(CheckForNegativeValue(
								item.getTotalItemAmount()));
					}
					if (item.getTotalAmt() != null) {
						item.setTotalAmt(
								CheckForNegativeValue(item.getTotalAmt()));
					}
					if (item.getCifValue() != null) {
						item.setCifValue(
								CheckForNegativeValue(item.getCifValue()));
					}
					if (item.getPurchaseVoucherNum() != null) {
						item.setPurchaseVoucherNum(item.getPurchaseVoucherNum());
					}
					if (item.getContractValue() != null) {
						item.setContractValue(CheckForNegativeValue(item.getContractValue()));
					}
				}
			}
		});

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format(
					"Mapped the InwardTransDocument "
							+ "objects to DocumentDto Objects. About to load the "
							+ "error list for all the documents obtained. No: of docs "
							+ "for which errors will be loaded = %d..",
					docs.size()));
		}

		// Get the list of ids for the documents and use it to find the
		// associated list of InwardTransDoc error objects.
		List<Long> ids = docs.stream().map(doc -> doc.getId())
				.collect(Collectors.toCollection(ArrayList::new));
		List<InwardTransDocError> errors = InwardDocErrRepository
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
		if (errors == null || errors.isEmpty())
			return new SearchResult(retDocs, pageReq, totalCount);

		// Segregate the errors according to the input document ids.
		Map<Long, List<InwardTransDocError>> errMap = errors.stream()
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
			List<InwardTransDocError> docErrors = errMap.get(doc.getId());
			if (docErrors != null && !docErrors.isEmpty()) {
				List<DocErrorDto> list = docErrors.stream()
						.map(o -> convertToDocErrorDto(o))
						.collect(Collectors.toCollection(ArrayList::new));
				doc.setErrors(list);
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
		return null;
	}

	private BigDecimal CheckForNegativeValue(Object value) {

		if (value != null) {
			if (value instanceof BigDecimal) {
				return new BigDecimal((value != null
						? ((((BigDecimal) value).compareTo(BigDecimal.ZERO) > 0)
								? "-" + value.toString() : value.toString())
						: null));
			}
		}
		return null;
	}
	
	private String ChangeForNegativeValue(Object value) {
		if (value != null) {
			if (value instanceof BigDecimal) {
				return (value != null
						? ((((BigDecimal) value).compareTo(BigDecimal.ZERO) > 0)
								? "-" + value.toString() : value.toString())
						: null);
			} else if (value instanceof Integer) {
				return (value != null ? (((Integer) value > 0)
						? "-" + value.toString() : value.toString()) : null);
			} else if (value instanceof Long) {
				return (value != null ? (((Long) value > 0)
						? "-" + value.toString() : value.toString()) : null);
			} else if (value instanceof BigInteger) {
				return (value != null
						? ((((BigInteger) value).compareTo(BigInteger.ZERO) > 0)
								? "-" + value.toString() : value.toString())
						: null);
			}
			return "-" + value.toString();
		}
		return null;
	}
}
