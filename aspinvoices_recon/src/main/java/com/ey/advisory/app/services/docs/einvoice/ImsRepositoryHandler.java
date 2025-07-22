package com.ey.advisory.app.services.docs.einvoice;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.GetAnx1BatchEntity;
import com.ey.advisory.app.data.repositories.client.GetAnx1BatchRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.ImsGstnCountRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.ImsInvoiceStagingB2BARepository;
import com.ey.advisory.app.data.repositories.client.asprecon.ImsInvoiceStagingB2BRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.ImsInvoiceStagingCNARepository;
import com.ey.advisory.app.data.repositories.client.asprecon.ImsInvoiceStagingCNRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.ImsInvoiceStagingDNARepository;
import com.ey.advisory.app.data.repositories.client.asprecon.ImsInvoiceStagingDNRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.ImsInvoiceStagingECOMARepository;
import com.ey.advisory.app.data.repositories.client.asprecon.ImsInvoiceStagingECOMRepository;
import com.ey.advisory.app.ims.handlers.GetImsCountDtlsDto;
import com.ey.advisory.app.ims.handlers.GetImsCountGoodsTypeDtlsDto;
import com.ey.advisory.app.ims.handlers.GetImsCountSectionDtlsDto;
import com.ey.advisory.app.ims.handlers.GetImsInvoicesDtlsDto;
import com.ey.advisory.app.ims.handlers.GetImsInvoicesSectionDtlsDto;
import com.ey.advisory.app.ims.handlers.ImsInvoicesProcCallService;
import com.ey.advisory.app.inward.einvoice.GetIrnAdditionalSuppDocsEntity;
import com.ey.advisory.app.inward.einvoice.GetIrnB2bItemEntity;
import com.ey.advisory.app.inward.einvoice.GetIrnContractDetailEntity;
import com.ey.advisory.app.inward.einvoice.GetIrnDtlsRespDto;
import com.ey.advisory.app.inward.einvoice.GetIrnItemAttributeListEntity;
import com.ey.advisory.app.inward.einvoice.GetIrnPreceedingDocDetailEntity;
import com.ey.advisory.app.inward.einvoice.ImsDataDto;
import com.ey.advisory.app.inward.einvoice.InwardGetIrnDetailsDataParserService;
import com.ey.advisory.app.service.ims.ImsGstnCountEntity;
import com.ey.advisory.app.service.ims.ImsInvoiceStagingB2BAEntity;
import com.ey.advisory.app.service.ims.ImsInvoiceStagingB2BEntity;
import com.ey.advisory.app.service.ims.ImsInvoiceStagingCNAEntity;
import com.ey.advisory.app.service.ims.ImsInvoiceStagingCNEntity;
import com.ey.advisory.app.service.ims.ImsInvoiceStagingDNAEntity;
import com.ey.advisory.app.service.ims.ImsInvoiceStagingDNEntity;
import com.ey.advisory.app.service.ims.ImsInvoiceStagingECOMAEntity;
import com.ey.advisory.app.service.ims.ImsInvoiceStagingECOMEntity;
import com.ey.advisory.app.util.Anx1GetBatchUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;
import com.ey.advisory.einv.dto.AddlDocument;
import com.ey.advisory.einv.dto.Attribute;
import com.ey.advisory.einv.dto.Contract;
import com.ey.advisory.einv.dto.EinvoiceRequestDto;
import com.ey.advisory.einv.dto.ItemDto;
import com.ey.advisory.einv.dto.PrecDocument;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("ImsRepositoryHandler")
public class ImsRepositoryHandler {

	@Autowired
	private ImsProcessUtil imsProcessUtil;

	@Autowired
	@Qualifier("getAnx1BatchRepository")
	private GetAnx1BatchRepository batchRepo;

	@Autowired
	@Qualifier("anx1GetBatchUtil")
	private Anx1GetBatchUtil batchUtil;

	@Autowired
	private ImsInvoiceStagingB2BARepository imsInvoiceStagingB2BARepository;

	@Autowired
	private ImsInvoiceStagingB2BRepository imsInvoiceStagingB2BRepository;

	@Autowired
	private ImsInvoiceStagingCNRepository imsInvoiceStagingCNRepository;

	@Autowired
	private ImsInvoiceStagingDNARepository imsInvoiceStagingDNARepository;

	@Autowired
	private ImsInvoiceStagingECOMRepository imsInvoiceStagingEcomRepository;

	@Autowired
	private ImsInvoiceStagingCNARepository imsInvoiceStagingCNARepository;

	@Autowired
	private ImsInvoiceStagingDNRepository imsInvoiceStagingDNRepository;

	@Autowired
	private ImsInvoiceStagingECOMARepository imsInvoiceStagingEcomARepository;

	@Autowired
	@Qualifier("InwardGetIrnDetailsDataParserServiceImpl")
	private InwardGetIrnDetailsDataParserService convertHdrLineService;
	
	@Autowired
	private ImsGstnCountRepository imsGstnCountRepository;

	@Autowired
	@Qualifier("ImsInvoicesProcCallServiceImpl")
	private ImsInvoicesProcCallService imsProcCallInvoiceParser;
	
	private static final List<String> GETIMS_SUPPLY_TYPES = ImmutableList.of(
			APIConstants.IMS_TYPE_B2B, APIConstants.IMS_TYPE_B2BA,
			APIConstants.IMS_TYPE_CN, APIConstants.IMS_TYPE_CNA,
			APIConstants.IMS_TYPE_DN, APIConstants.IMS_TYPE_DNA,
			APIConstants.IMS_TYPE_ECOM, APIConstants.IMS_TYPE_ECOMA);

	private final DateTimeFormatter saveformatter = DateTimeFormatter
			.ofPattern("dd-MM-yyyy");
	
	private final DateTimeFormatter parseFormatter = DateTimeFormatter
			.ofPattern("yyyy-MM-dd");
	
	DateTimeFormatter displayFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

	private static final String DOC_KEY_JOINER = "|";

	public Integer processImsWorkSheetData(Object[][] objList,
			int columnCount, String rgstin,
			Gstr1GetInvoicesReqDto dto) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"processInwardEinvoiceWorkSheetData list size [{}] started",
					objList.length);
		}
		List<ImsDataDto> imsDataList = imsProcessUtil
				.convertImsWorkSheetDataToList(objList, columnCount,
						rgstin);
		
		
		String groupCode = TenantContext.getTenantId();
		
		Gstr1GetInvoicesReqDto batchDto = createBatchAndSave(groupCode, dto);
		
		GetImsCountGoodsTypeDtlsDto reqDto = new GetImsCountGoodsTypeDtlsDto();
				
		GetImsCountDtlsDto countDtlsDto = new GetImsCountDtlsDto();
		
		Map<String, GetImsCountSectionDtlsDto> sectionMap = new HashMap<>();
		
		 if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("imsDataList {}",
						imsDataList);
			}  
		for (ImsDataDto imsData : imsDataList) {
            String tableType = imsData.getTableType();
            String action = imsData.getAction();
            
            if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("tableType {} action {}",
						tableType,action);
			} 
            // Initialize section if it doesn't exist
            sectionMap.putIfAbsent(tableType, new GetImsCountSectionDtlsDto());
            GetImsCountSectionDtlsDto sectionDto = sectionMap.get(tableType);

            // Populate counts based on action
            switch (action) {
                case "N":
                    sectionDto.setNoAction(incrementCount(sectionDto.getNoAction()));
                    break;
                case "R":
                    sectionDto.setReject(incrementCount(sectionDto.getReject()));
                    break;
                case "P":
                    sectionDto.setPending(incrementCount(sectionDto.getPending()));
                    break;
                case "A":
                    sectionDto.setAccept(incrementCount(sectionDto.getAccept()));
                    break;
                default:
                    break;
            }

            // Update total count for each section
            sectionDto.setTotal(incrementCount(sectionDto.getTotal()));
        }
		
		 	countDtlsDto.setB2bcn(sectionMap.get("CN"));
	        countDtlsDto.setB2bdn(sectionMap.get("DN"));
	        countDtlsDto.setB2ba(sectionMap.get("B2BA"));
	        countDtlsDto.setB2b(sectionMap.get("B2B"));
	        countDtlsDto.setEcom(sectionMap.get("ECOM"));
	        countDtlsDto.setEcoma(sectionMap.get("ECOMA"));
	        countDtlsDto.setB2bdna(sectionMap.get("DNA"));
	        countDtlsDto.setB2bcna(sectionMap.get("CNA"));
	        countDtlsDto.setIsd(sectionMap.get("ISD"));
	        countDtlsDto.setIsda(sectionMap.get("ISDA"));
	        countDtlsDto.setIsdcn(sectionMap.get("ISDCN"));
	        countDtlsDto.setIsdcna(sectionMap.get("ISDCNA"));
	        countDtlsDto.setImpg(sectionMap.get("IMPG"));
	        countDtlsDto.setImpga(sectionMap.get("IMPGA"));
	        countDtlsDto.setImpgsez(sectionMap.get("IMPGSEZ"));
	        countDtlsDto.setImpgseza(sectionMap.get("IMPGSEZA"));
	        
	        reqDto.setAllOther(countDtlsDto);
	        
	        if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("sectionMap {}",
						sectionMap);
			}  
	        
	        if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("countDtlsDto {}",
						countDtlsDto);
			}  
		List<ImsGstnCountEntity> countEntity = parseImsCountsData(dto,
				reqDto, APIConstants.IMS_COUNT_TYPE_ALL_OTH, batchDto.getBatchId(), dto.getGroupcode());
		
		   if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("countEntity {}",
						countEntity);
			}  
		
		imsGstnCountRepository.setDeleteTrueByGstinAndType(dto.getGstin(),APIConstants.IMS_COUNT_TYPE_ALL_OTH);
		
		imsGstnCountRepository.saveAll(countEntity);
		//-------------------------------------------------------
		
		Map<String, List<ImsDataDto>> groupedByTableType = imsDataList.stream()
		        .collect(Collectors.groupingBy(ImsDataDto::getTableType));
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("groupedData list size {}",
					groupedByTableType);
		}
		
		Map<String, Long> batchIdMap = new HashMap<>();
				
		// Initialize GetImsInvoicesDtlsDto to hold all sections
		GetImsInvoicesDtlsDto imsInvoicesDtlsDto = new GetImsInvoicesDtlsDto();

		// Iterate over each tableType in groupedByTableType
		for (Map.Entry<String, List<ImsDataDto>> entry : groupedByTableType.entrySet()) {
		    String tableType = entry.getKey();
		    List<ImsDataDto> imsDataForTableType = entry.getValue();

		    // Create or retrieve batch ID for the current tableType
		    Gstr1GetInvoicesReqDto batchDtoInvoice = new Gstr1GetInvoicesReqDto();
		    batchDtoInvoice.setType(tableType);
		    if (batchIdMap.containsKey(tableType)) {
		        batchDtoInvoice.setBatchId(batchIdMap.get(tableType));
		    } else {
		        batchDtoInvoice = createBatchAndSaveInvoice(groupCode, dto, tableType);
		        batchIdMap.put(tableType, batchDtoInvoice.getBatchId());
		    }

		    // List to hold section DTOs for the current table type
		    List<GetImsInvoicesSectionDtlsDto> invoiceSection = new ArrayList<>();

		    // Process each ImsDataDto in the current tableType group
		    for (ImsDataDto imsData : imsDataForTableType) {
		        GetImsInvoicesSectionDtlsDto sectionDto = new GetImsInvoicesSectionDtlsDto();

		        // Map fields from ImsDataDto to GetImsInvoicesSectionDtlsDto
		        sectionDto.setSupplierGstin(imsData.getSupplierGstin());
		        sectionDto.setOriginalInvoiceNumber(imsData.getOriginalDocumentNumber());
		        sectionDto.setOriginalInvoiceDate(
		        	    Optional.ofNullable(imsData.getOriginalDocumentDate())
		        	            .map(LocalDate::toString)
		        	            .orElse("")
		        	);
		        sectionDto.setReturnPeriod(imsData.getGstr1FilingPeriod());
		        sectionDto.setInvoiceNumber(imsData.getDocumentNumber());
		        sectionDto.setInvoiceType(imsData.getDocumentType());
		        sectionDto.setAction(imsData.getAction());
		        sectionDto.setSourceForm(imsData.getFormType());
		        sectionDto.setIsPendingActionBlocked(imsData.getPendingActionBlocked());
		        sectionDto.setInvoiceDate(
		        	    Optional.ofNullable(imsData.getDocumentDate())
		        	            .map(LocalDate::toString)
		        	            .orElse("")
		        	);
		        sectionDto.setInvoiceValue(imsData.getInvoiceValue());
		        sectionDto.setPos(imsData.getPos());
		        sectionDto.setTaxableValue(imsData.getTaxableValue());
		        sectionDto.setIgstAmount(imsData.getIgst());
		        sectionDto.setCgstAmount(imsData.getCgst());
		        sectionDto.setSgstAmount(imsData.getSgst());
		        sectionDto.setCess(imsData.getCess());
		        sectionDto.setSrcfilstatus(imsData.getGstr1FilingStatus());
		        sectionDto.setHash(imsData.getChecksum());

		        invoiceSection.add(sectionDto);
		    }
		    if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("invoiceSection {}",
						invoiceSection);
			}
		    // Add invoice section to the appropriate list in GetImsInvoicesDtlsDto based on tableType
		    switch (tableType.toLowerCase()) {
		        case "b2b":
		            imsInvoicesDtlsDto.setB2b(invoiceSection);
		            break;
		        case "b2ba":
		            imsInvoicesDtlsDto.setB2ba(invoiceSection);
		            break;
		        case "dn":
		            imsInvoicesDtlsDto.setB2bdn(invoiceSection);
		            break;
		        case "dna":
		            imsInvoicesDtlsDto.setB2bdna(invoiceSection);
		            break;
		        case "cn":
		            imsInvoicesDtlsDto.setB2bcn(invoiceSection);
		            break;
		        case "cna":
		            imsInvoicesDtlsDto.setB2bcna(invoiceSection);
		            break;
		        case "ecom":
		            imsInvoicesDtlsDto.setEcom(invoiceSection);
		            break;
		        case "ecoma":
		            imsInvoicesDtlsDto.setEcoma(invoiceSection);
		            break;
		        default:
		            // Handle unexpected table types if necessary
		            break;
		    }
		}

		Set<String> uniqueTableTypes = imsDataList.stream()
                .map(ImsDataDto::getTableType)
                .filter(tableType -> tableType != null)
                .collect(Collectors.toSet());
		
		 if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("uniqueTableTypes {}",
						uniqueTableTypes);
			}
		 
		 
		 if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("imsInvoicesDtlsDto {}",
						imsInvoicesDtlsDto);
			}
		for (String type : uniqueTableTypes) {
			switch (type.toUpperCase()) {
			case "B2B":
				List<ImsInvoiceStagingB2BEntity> b2bEntity = parseImsInvoicesData(
						dto, imsInvoicesDtlsDto.b2b, type, batchIdMap.get(type),
						dto.getGroupcode(),
						ImsInvoiceStagingB2BEntity.class);

				LOGGER.debug(" b2bEntity {} ", b2bEntity.toArray());
				imsInvoiceStagingB2BRepository.saveAll(b2bEntity);
				dto.setType(type);
				imsProcCallInvoiceParser.procCall(dto, batchIdMap.get(type));
				batchRepo.updateStatusById(batchIdMap.get(type), "SUCCESS");
				break;

			case "B2BA":
				List<ImsInvoiceStagingB2BAEntity> b2baEntity = parseImsInvoicesData(
						dto, imsInvoicesDtlsDto.b2ba, type, batchIdMap.get(type),
						dto.getGroupcode(),
						ImsInvoiceStagingB2BAEntity.class);
				imsInvoiceStagingB2BARepository.saveAll(b2baEntity);
				dto.setType(type);
				imsProcCallInvoiceParser.procCall(dto, batchIdMap.get(type));
				batchRepo.updateStatusById(batchIdMap.get(type), "SUCCESS");
				break;
			case "CN":
				List<ImsInvoiceStagingCNEntity> cnEntity = parseImsInvoicesData(
						dto, imsInvoicesDtlsDto.b2bcn, type, batchIdMap.get(type),
						dto.getGroupcode(),
						ImsInvoiceStagingCNEntity.class);
				imsInvoiceStagingCNRepository.saveAll(cnEntity);
				dto.setType(type);
				imsProcCallInvoiceParser.procCall(dto, batchIdMap.get(type));
				batchRepo.updateStatusById(batchIdMap.get(type), "SUCCESS");
				break;
			case "CNA":
				List<ImsInvoiceStagingCNAEntity> cnaEntity = parseImsInvoicesData(
						dto, imsInvoicesDtlsDto.b2bcna, type, batchIdMap.get(type),
						dto.getGroupcode(),
						ImsInvoiceStagingCNAEntity.class);
				imsInvoiceStagingCNARepository.saveAll(cnaEntity);
				dto.setType(type);
				imsProcCallInvoiceParser.procCall(dto, batchIdMap.get(type));
				batchRepo.updateStatusById(batchIdMap.get(type), "SUCCESS");
				break;
			case "DN":
				List<ImsInvoiceStagingDNEntity> dnEntity = parseImsInvoicesData(
						dto, imsInvoicesDtlsDto.b2bdn, type, batchIdMap.get(type),
						dto.getGroupcode(),
						ImsInvoiceStagingDNEntity.class);
				imsInvoiceStagingDNRepository.saveAll(dnEntity);
				dto.setType(type);
				imsProcCallInvoiceParser.procCall(dto, batchIdMap.get(type));
				batchRepo.updateStatusById(batchIdMap.get(type), "SUCCESS");
				break;
			case "DNA":
				List<ImsInvoiceStagingDNAEntity> dnaEntity = parseImsInvoicesData(
						dto, imsInvoicesDtlsDto.b2bdna,type, batchIdMap.get(type),
						dto.getGroupcode(),
						ImsInvoiceStagingDNAEntity.class);
				imsInvoiceStagingDNARepository.saveAll(dnaEntity);
				dto.setType(type);
				imsProcCallInvoiceParser.procCall(dto, batchIdMap.get(type));
				batchRepo.updateStatusById(batchIdMap.get(type), "SUCCESS");
				break;
			case "ECOM":
				List<ImsInvoiceStagingECOMEntity> ecomEntity = parseImsInvoicesData(
						dto, imsInvoicesDtlsDto.ecom, type, batchIdMap.get(type),
						dto.getGroupcode(),
						ImsInvoiceStagingECOMEntity.class);
				imsInvoiceStagingEcomRepository.saveAll(ecomEntity);
				dto.setType(type);
				imsProcCallInvoiceParser.procCall(dto, batchIdMap.get(type));
				batchRepo.updateStatusById(batchIdMap.get(type), "SUCCESS");
				break;
			case "ECOMA":
				List<ImsInvoiceStagingECOMAEntity> ecomaEntity = parseImsInvoicesData(
						dto, imsInvoicesDtlsDto.ecoma, type, batchIdMap.get(type),
						dto.getGroupcode(),
						ImsInvoiceStagingECOMAEntity.class);
				imsInvoiceStagingEcomARepository.saveAll(ecomaEntity);
				dto.setType(type);
				imsProcCallInvoiceParser.procCall(dto, batchIdMap.get(type));
				batchRepo.updateStatusById(batchIdMap.get(type), "SUCCESS");
				break;
			}

		 }
		

		return (imsInvoicesDtlsDto.b2b != null ? imsInvoicesDtlsDto.b2b.size() : 0)
			       + (imsInvoicesDtlsDto.b2ba != null ? imsInvoicesDtlsDto.b2ba.size() : 0)
			       + (imsInvoicesDtlsDto.b2bcn != null ? imsInvoicesDtlsDto.b2bcn.size() : 0)
			       + (imsInvoicesDtlsDto.b2bdn != null ? imsInvoicesDtlsDto.b2bdn.size() : 0)
			       + (imsInvoicesDtlsDto.b2bcna != null ? imsInvoicesDtlsDto.b2bcna.size() : 0)
			       + (imsInvoicesDtlsDto.b2bdna != null ? imsInvoicesDtlsDto.b2bdna.size() : 0)
			       + (imsInvoicesDtlsDto.ecom != null ? imsInvoicesDtlsDto.ecom.size() : 0)
			       + (imsInvoicesDtlsDto.ecoma != null ? imsInvoicesDtlsDto.ecoma.size() : 0);

	}
	
	private String createInvKey(String suppGstin, String recipientGstin,
			String docNum, String docDate, String docType, String tableType) {

		StringBuilder docKey = new StringBuilder();

		if (!isPresent(docType) || !isPresent(docNum) || !isPresent(suppGstin)
				|| !isPresent(recipientGstin) || !isPresent(docDate)) {
			return null;
		}

		docKey.append(suppGstin).append(DOC_KEY_JOINER).append(recipientGstin)
				.append(DOC_KEY_JOINER).append(removeQuotes(docNum))
				.append(DOC_KEY_JOINER).append(removeQuotes(docDate))
				.append(DOC_KEY_JOINER).append(docType).append(DOC_KEY_JOINER)
				.append(tableType);

		return docKey.toString();
	}

	private Object removeQuotes(String docNum) {
		if (docNum == null) {
			return null;
		}
		return docNum.replace("\"", "");
	}

	private boolean isPresent(String docNum) {
		return docNum != null && !docNum.trim().isEmpty();
	}

	// R - INV B2b, Ecom
	// R - RNV - b2ba, ecoma
	// R - CR - CN
	// R - RCR - CNA
	// r - DR- DN
	// R - RDR- DNA

	private String deriveLinkingKey(LocalDate date, String cgstin,
			String sgstin, String docType, String documentNumber) {
		String finYear = GenUtil.getFinYear(date);

		return new StringJoiner(DOC_KEY_JOINER).add(finYear).add(cgstin)
				.add(sgstin).add(docType).add(documentNumber).toString();
	}
	private <T> List<T> parseImsInvoicesData(Gstr1GetInvoicesReqDto dto,
			List<GetImsInvoicesSectionDtlsDto> sectionDtlDto, String type,
			Long batchId, String groupcode, Class<T> entityClass) {
		List<T> entityList = new ArrayList<>();
		sectionDtlDto.forEach(sectionDto -> {
			try {
				T entity = entityClass.newInstance();

				entity.getClass().getMethod("setSupplierGstin", String.class)
						.invoke(entity, sectionDto.getSupplierGstin() != null
								? sectionDto.getSupplierGstin() : null);

				entity.getClass().getMethod("setRecipientGstin", String.class)
						.invoke(entity,
								dto.getGstin() != null ? dto.getGstin() : null);

				entity.getClass().getMethod("setReturnPeriod", String.class)
						.invoke(entity, sectionDto.getReturnPeriod() != null
								? sectionDto.getReturnPeriod() : null);

				entity.getClass().getMethod("setDerivedRetPeriod", Long.class)
						.invoke(entity,
								Long.valueOf(sectionDto.getReturnPeriod()
										.substring(2, 6)
										+ sectionDto
												.getReturnPeriod().substring(0,
														2)));

				entity.getClass().getMethod("setInvoiceNumber", String.class)
						.invoke(entity, sectionDto.getInvoiceNumber() != null
								? sectionDto.getInvoiceNumber() : null);

				// the invoice type coming from GSTN
				entity.getClass().getMethod("setGstnInvType", String.class)
						.invoke(entity, sectionDto.getInvoiceType() != null
								? sectionDto.getInvoiceType() : null);

				if (type.equalsIgnoreCase("B2B")
						|| type.equalsIgnoreCase("ECOM"))
					sectionDto.setInvoiceType("INV");
				else if (type.equalsIgnoreCase("B2BA")
						|| type.equalsIgnoreCase("ECOMA"))
					sectionDto.setInvoiceType("RNV");
				else if (type.equalsIgnoreCase("CN"))
					sectionDto.setInvoiceType("CR");
				else if (type.equalsIgnoreCase("CNA"))
					sectionDto.setInvoiceType("RCR");
				else if (type.equalsIgnoreCase("DN"))
					sectionDto.setInvoiceType("DR");
				else if (type.equalsIgnoreCase("DNA"))
					sectionDto.setInvoiceType("RDR");

				entity.getClass().getMethod("setInvoiceType", String.class)
						.invoke(entity, sectionDto.getInvoiceType() != null
								? sectionDto.getInvoiceType() : null);

				entity.getClass().getMethod("setAction", String.class)
						.invoke(entity, sectionDto.getAction() != null
								? sectionDto.getAction() : null);
				entity.getClass().getMethod("setFilingStatus", String.class)
						.invoke(entity, sectionDto.getSrcfilstatus() != null
								? sectionDto.getSrcfilstatus() : null);

				if (sectionDto.getIsPendingActionAllowed() != null
						&& sectionDto.getIsPendingActionBlocked() == null) {

					String isPendBlocked = ("N"
							.equals(sectionDto.getIsPendingActionAllowed())
									? "Y" : "N");

					entity.getClass().getMethod("setIsPendingActionBlocked",
							String.class).invoke(entity, isPendBlocked);
				} else {
					entity.getClass()
							.getMethod("setIsPendingActionBlocked",
									String.class)
							.invoke(entity,
									sectionDto
											.getIsPendingActionBlocked() != null
													? sectionDto
															.getIsPendingActionBlocked()
													: null);
				}

				entity.getClass().getMethod("setFormType", String.class)
						.invoke(entity, sectionDto.getSourceForm() != null
								? sectionDto.getSourceForm() : null);
				Date finalDate = null;
				if(sectionDto.getInvoiceDate() != null){
					SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
					SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
					 
					Date parsedDate = inputFormat.parse(sectionDto.getInvoiceDate());

					String formattedDate = outputFormat.format(parsedDate);

					finalDate = outputFormat.parse(formattedDate);

					entity.getClass().getMethod("setInvoiceDate", Date.class).invoke(entity, finalDate);
				}
				 
//				entity.getClass().getMethod("setInvoiceDate", Date.class)
//						.invoke(entity, sectionDto.getInvoiceDate() != null
//								? new SimpleDateFormat("dd-MM-yyyy").parse(
//										sectionDto.getInvoiceDate())
//								: null);
				entity.getClass().getMethod("setInvoiceValue", BigDecimal.class)
						.invoke(entity,
								sectionDto.getInvoiceValue() != null
										? sectionDto.getInvoiceValue()
										: BigDecimal.ZERO);

				entity.getClass().getMethod("setTaxableValue", BigDecimal.class)
						.invoke(entity,
								sectionDto.getTaxableValue() != null
										? sectionDto.getTaxableValue()
										: BigDecimal.ZERO);
				// need for igst, sgst, cgst, cess
				entity.getClass().getMethod("setIgstAmt", BigDecimal.class)
						.invoke(entity, sectionDto.getIgstAmount() != null
								? sectionDto.getIgstAmount() : BigDecimal.ZERO);
				entity.getClass().getMethod("setCgstAmt", BigDecimal.class)
						.invoke(entity, sectionDto.getCgstAmount() != null
								? sectionDto.getCgstAmount() : BigDecimal.ZERO);
				entity.getClass().getMethod("setSgstAmt", BigDecimal.class)
						.invoke(entity, sectionDto.getSgstAmount() != null
								? sectionDto.getSgstAmount() : BigDecimal.ZERO);
				entity.getClass().getMethod("setCessAmt", BigDecimal.class)
						.invoke(entity, sectionDto.getCess() != null
								? sectionDto.getCess() : BigDecimal.ZERO);

				entity.getClass().getMethod("setPos", String.class)
						.invoke(entity, sectionDto.getPos() != null
								? sectionDto.getPos() : null);
				entity.getClass().getMethod("setTaxableValue", BigDecimal.class)
						.invoke(entity,
								sectionDto.getTaxableValue() != null
										? sectionDto.getTaxableValue()
										: BigDecimal.ZERO);

				entity.getClass().getMethod("setChksum", String.class)
						.invoke(entity, sectionDto.getHash() != null
								? sectionDto.getHash() : null);
				// need for isdelete, batchid, created on, created by
				entity.getClass().getMethod("setBatchId", Long.class)
						.invoke(entity, batchId);
				entity.getClass().getMethod("setIsDelete", Boolean.class)
						.invoke(entity, false);
				entity.getClass().getMethod("setCreatedBy", String.class)
						.invoke(entity, groupcode);
				entity.getClass().getMethod("setCreatedOn", LocalDateTime.class)
						.invoke(entity, LocalDateTime.now());

				LocalDate invDate = null;
				String formattedDate = null;
				LocalDate finalDatev = null;
				if (!Strings.isNullOrEmpty(sectionDto.getInvoiceDate())) {
					invDate = LocalDate.parse(sectionDto.getInvoiceDate(),
							parseFormatter);
					formattedDate = invDate.format(displayFormatter);
					finalDatev = LocalDate.parse(formattedDate, displayFormatter);
				}
				
				entity.getClass().getMethod("setDocKey", String.class).invoke(
						entity,
						createInvKey(sectionDto.getSupplierGstin(),
								dto.getGstin(), sectionDto.getInvoiceNumber(),
								formattedDate,
								sectionDto.getInvoiceType(), type));

				entity.getClass().getMethod("setLnkingDocKey", String.class)
						.invoke(entity,
								deriveLinkingKey(finalDatev, dto.getGstin(),
										sectionDto.getSupplierGstin(),
										sectionDto.getInvoiceType(),
										sectionDto.getInvoiceNumber()));

				// need for ORG_INV_NUM, ORG_INV_date

				if (type.equalsIgnoreCase("B2BA")
						|| type.equalsIgnoreCase("CNA")
						|| type.equalsIgnoreCase("DNA")
						|| type.equalsIgnoreCase("ECOMA")) {
					entity.getClass().getMethod("setOrgInvNum", String.class)
							.invoke(entity,
									sectionDto
											.getOriginalInvoiceNumber() != null
													? sectionDto
															.getOriginalInvoiceNumber()
													: null);
					entity.getClass().getMethod("setOrgInvDate", Date.class)
							.invoke(entity,
									sectionDto.getOriginalInvoiceDate() != null
											? new SimpleDateFormat("dd-mm-yyyy")
													.parse(sectionDto
															.getOriginalInvoiceDate())
											: null);
				}

				entityList.add(entity);
			} catch (InstantiationException | IllegalAccessException
					| NoSuchMethodException | InvocationTargetException
					| IllegalArgumentException | SecurityException
					| ParseException e) {
				LOGGER.error("Error while creating instance of entity class {}",
						entityClass.getName(), e);
				throw new AppException(e);
			}
		});
		return entityList;
	}


	private Gstr1GetInvoicesReqDto createBatchAndSave(String groupCode,
			Gstr1GetInvoicesReqDto dto) {
	
		TenantContext.setTenantId(groupCode);
		// InActiveting Previous Batch Records
		batchRepo.softlyDelete(APIConstants.IMS_COUNT_TYPE_ALL_OTH,
				APIConstants.IMS_COUNT_UPL, dto.getGstin(),
				"000000");
		dto.setApiSection("IMS_COUNT");
		GetAnx1BatchEntity batch = batchUtil.makeBatchGstr1(dto, APIConstants.IMS_COUNT_TYPE_ALL_OTH,
				APIConstants.IMS_COUNT_UPL);
		// Save new Batch
		batch = batchRepo.save(batch);
		dto.setBatchId(batch.getId());
		
		return dto;

	}
	
	private Gstr1GetInvoicesReqDto createBatchAndSaveInvoice(String groupCode,
			Gstr1GetInvoicesReqDto dto, String tableType) {
		TenantContext.setTenantId(groupCode);
		// InActiveting Previous Batch Records
		batchRepo.softlyDelete(tableType.toUpperCase(),
				APIConstants.IMS_INVOICE_UPL, dto.getGstin(),
				"000000");
		dto.setApiSection("IMS_INVOICE");
		GetAnx1BatchEntity batch = batchUtil.makeBatchGstr1(dto, tableType.toUpperCase(),
				APIConstants.IMS_INVOICE_UPL);
		// Save new Batch
		batch = batchRepo.save(batch);
		dto.setBatchId(batch.getId());
		
		return dto;

	}

	public static Map<String, String> createIrnMap(List<String> listIrn) {
        Map<String, String> irnMap = new HashMap<>();

        for (String irnStatus : listIrn) {
            String[] parts = irnStatus.split("-");
            if (parts.length == 2) {
                String irn = parts[0];
                String irnSts = parts[1];
                irnMap.put(irn, irnSts);
            }
        }

        return irnMap;
    }
	
	private List<ImsGstnCountEntity> parseImsCountsData(
			Gstr1GetInvoicesReqDto dto,
			GetImsCountGoodsTypeDtlsDto sectionDtlDto, String type,
			Long batchId, String groupcode) {

		List<ImsGstnCountEntity> gstinCountList = new ArrayList<>();

			List<ImsGstnCountEntity> entity = createEntityFromDto(
					sectionDtlDto.getAllOther(), type, batchId, groupcode, dto);
			gstinCountList.addAll(entity);
		
			 if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("gstinCountList {}",
							gstinCountList);
				} 
		return gstinCountList;
	}

	private static List<ImsGstnCountEntity> createEntityFromDto(
			GetImsCountDtlsDto countDtls, String type, Long batchId,
			String groupcode, Gstr1GetInvoicesReqDto dto) {

		List<ImsGstnCountEntity> entitylist = new ArrayList<>();
		 if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("countDtls {}",
						countDtls);
			}
		if (countDtls.getB2bcn() != null) {
			ImsGstnCountEntity entity = new ImsGstnCountEntity();
			mapSectionToEntity("CN",countDtls.getB2bcn(), entity, type, batchId,
					groupcode, dto);

			if(entity.getGstin()!=null)
			entitylist.add(entity);
		}

		//need for all the countdtls variables
		if (countDtls.getB2bdn() != null) {
			ImsGstnCountEntity entity = new ImsGstnCountEntity();
			mapSectionToEntity("DN",countDtls.getB2bdn(), entity, type, batchId,
					groupcode, dto);

			if(entity.getGstin()!=null)
			entitylist.add(entity);
		}
		if (countDtls.getB2ba() != null) {
			ImsGstnCountEntity entity = new ImsGstnCountEntity();
			mapSectionToEntity("B2BA",countDtls.getB2ba(), entity, type, batchId,
					groupcode, dto);

			if(entity.getGstin()!=null)
			entitylist.add(entity);
		}
		if (countDtls.getB2b() != null) {
			ImsGstnCountEntity entity = new ImsGstnCountEntity();
			mapSectionToEntity("B2B",countDtls.getB2b(), entity, type, batchId,
					groupcode, dto);

			if(entity.getGstin()!=null)
			entitylist.add(entity);
		}
		if (countDtls.getEcom() != null) {
			ImsGstnCountEntity entity = new ImsGstnCountEntity();
			mapSectionToEntity("ECOM",countDtls.getEcom(), entity, type, batchId,
					groupcode, dto);

			if(entity.getGstin()!=null)
			entitylist.add(entity);
		}
		if (countDtls.getEcoma() != null) {
			ImsGstnCountEntity entity = new ImsGstnCountEntity();
			mapSectionToEntity("ECOMA",countDtls.getEcoma(), entity, type, batchId,
					groupcode, dto);

			if(entity.getGstin()!=null)
			entitylist.add(entity);
		}
		if (countDtls.getB2bdna() != null) {
			ImsGstnCountEntity entity = new ImsGstnCountEntity();
			mapSectionToEntity("DNA",countDtls.getB2bdna(), entity, type, batchId,
					groupcode, dto);
			if(entity.getGstin()!=null)
			entitylist.add(entity);
		}
		if (countDtls.getB2bcna() != null) {
			ImsGstnCountEntity entity = new ImsGstnCountEntity();
			mapSectionToEntity("CNA",countDtls.getB2bcna(), entity, type, batchId,
					groupcode, dto);

			if(entity.getGstin()!=null)
			entitylist.add(entity);
		}
		if (countDtls.getIsd() != null) {
			ImsGstnCountEntity entity = new ImsGstnCountEntity();
			mapSectionToEntity("ISD",countDtls.getIsd(), entity, type, batchId,
					groupcode, dto);

			if(entity.getGstin()!=null)
			entitylist.add(entity);
		}
		if (countDtls.getIsda() != null) {
			ImsGstnCountEntity entity = new ImsGstnCountEntity();
			mapSectionToEntity("ISDA",countDtls.getIsda(), entity, type, batchId,
					groupcode, dto);

			if(entity.getGstin()!=null)
			entitylist.add(entity);
		}
		if (countDtls.getIsdcn() != null) {
			ImsGstnCountEntity entity = new ImsGstnCountEntity();
			mapSectionToEntity("ISDCN",countDtls.getIsdcn(), entity, type, batchId,
					groupcode, dto);

			if(entity.getGstin()!=null)
			entitylist.add(entity);
		}
		if (countDtls.getIsdcna() != null) {
			ImsGstnCountEntity entity = new ImsGstnCountEntity();
			mapSectionToEntity("ISDCNA",countDtls.getIsdcna(), entity, type, batchId,
					groupcode, dto);

			if(entity.getGstin()!=null)
			entitylist.add(entity);
		}
		if (countDtls.getImpg() != null) {
			ImsGstnCountEntity entity = new ImsGstnCountEntity();
			mapSectionToEntity("IMPG",countDtls.getImpg(), entity, type, batchId,
					groupcode, dto);

			if(entity.getGstin()!=null)
			entitylist.add(entity);
		}
		if (countDtls.getImpga() != null) {
			ImsGstnCountEntity entity = new ImsGstnCountEntity();
			mapSectionToEntity("IMPGA",countDtls.getImpga(), entity, type, batchId,
					groupcode, dto);

			if(entity.getGstin()!=null)
			entitylist.add(entity);
		}
		if (countDtls.getImpgsez() != null) {
			ImsGstnCountEntity entity = new ImsGstnCountEntity();
			mapSectionToEntity("IMPGSEZ",countDtls.getImpgsez(), entity, type, batchId,
					groupcode, dto);

			if(entity.getGstin()!=null)
			entitylist.add(entity);
		}
		if (countDtls.getImpgseza() != null) {
			ImsGstnCountEntity entity = new ImsGstnCountEntity();
			mapSectionToEntity("IMPGSEZA",countDtls.getImpgseza(), entity, type, batchId,
					groupcode, dto);
			if(entity.getGstin()!=null)
			entitylist.add(entity);
		}


		return entitylist;
	}

	private static  void mapSectionToEntity(String sectionName, GetImsCountSectionDtlsDto section,
			ImsGstnCountEntity entity, String type, Long batchId,
			String groupcode, Gstr1GetInvoicesReqDto dto) {
		// need to check null of all the attributes at first place with or
		// condition and then set the values
		if(section.getAccept()!=null || section.getNoAction()!=null || section.getPending()!=null || section.getReject()!=null) {
			entity.setGstin(dto.getGstin());
			entity.setGoodsType(type);
			entity.setSection(sectionName);
			entity.setIsDelete(false);
			entity.setBatchId(batchId);
			entity.setCreatedBy(groupcode);
			entity.setCreatedOn(LocalDateTime.now());

			entity.setGstnNoAction(section.getNoAction() != null
					? Integer.parseInt(section.getNoAction()) : 0);
			// complete all other gstn attributes
			entity.setGstnAccepted(section.getAccept() != null
					? Integer.parseInt(section.getAccept()) : 0);
			entity.setGstnPending(section.getPending() != null
					? Integer.parseInt(section.getPending()) : 0);
			entity.setGstnRejected(section.getReject() != null
					? Integer.parseInt(section.getReject()) : 0);
			//entity set total as the sum of all the above attributes
			entity.setGstnTotal(entity.getGstnNoAction() + entity.getGstnAccepted()
					+ entity.getGstnPending() + entity.getGstnRejected());
			entity.setGstin(dto.getGstin());
		}else
		{
			return;
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("entity {}",
					entity);
		}
		
	}
	
	 private String incrementCount(String currentCount) {
	        int count = (currentCount == null) ? 0 : Integer.parseInt(currentCount);
	        return String.valueOf(count + 1);
	    }

}
