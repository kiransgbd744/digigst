package com.ey.advisory.app.inward.einvoice;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.GetAnx1BatchEntity;
import com.ey.advisory.app.data.entities.client.asprecon.GetIrnListEntity;
import com.ey.advisory.app.data.repositories.client.GetAnx1BatchRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.GetIrnAddSuppDocDtlsRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.GetIrnContractDtlsRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.GetIrnHeaderB2BRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.GetIrnHeaderDexpRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.GetIrnHeaderExpwpRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.GetIrnHeaderExwopRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.GetIrnHeaderSezwopRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.GetIrnHeaderSezwpRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.GetIrnItmAttributeDocDtlsRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.GetIrnLineItemB2BRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.GetIrnLineItemDexpRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.GetIrnLineItemExpWopRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.GetIrnLineItemExpWpRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.GetIrnLineItemSezwopRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.GetIrnLineItemSezwpRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.GetIrnListingRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.GetIrnPreceedingDocDtlsRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.InwardEInvoiceERPRequestRepository;
import com.ey.advisory.app.services.docs.einvoice.InwardEinvoiceProcessUtil;
import com.ey.advisory.app.util.Anx1GetBatchUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;
import com.ey.advisory.einv.dto.AddlDocument;
import com.ey.advisory.einv.dto.Attribute;
import com.ey.advisory.einv.dto.BatchDetails;
import com.ey.advisory.einv.dto.BuyerDetails;
import com.ey.advisory.einv.dto.Contract;
import com.ey.advisory.einv.dto.DispatchDetails;
import com.ey.advisory.einv.dto.DocPerdDtls;
import com.ey.advisory.einv.dto.DocumentDetails;
import com.ey.advisory.einv.dto.EinvEwbDetails;
import com.ey.advisory.einv.dto.EinvoiceRequestDto;
import com.ey.advisory.einv.dto.ExportDetails;
import com.ey.advisory.einv.dto.ItemDto;
import com.ey.advisory.einv.dto.PayeeDetails;
import com.ey.advisory.einv.dto.PrecDocument;
import com.ey.advisory.einv.dto.RefDtls;
import com.ey.advisory.einv.dto.SellerDetails;
import com.ey.advisory.einv.dto.ShippingDetails;
import com.ey.advisory.einv.dto.TransactionDetails;
import com.ey.advisory.einv.dto.ValueDetails;
import com.google.common.base.Strings;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("InwardEinvoiceRepositoryHandler")
public class InwardEinvoiceRepositoryHandler {

	@Autowired
	private InwardEinvoiceProcessUtil inwardEinvoiceProcessUtil;

	@Autowired
	@Qualifier("getAnx1BatchRepository")
	private GetAnx1BatchRepository batchRepo;

	@Autowired
	@Qualifier("anx1GetBatchUtil")
	private Anx1GetBatchUtil batchUtil;

	@Autowired
	private GetIrnListingRepository getIrnListingRepo;

	@Autowired
	private GetIrnHeaderB2BRepository getIrnb2bHeaderRepo;

	@Autowired
	private GetIrnLineItemB2BRepository getIrnb2bLineRepo;

	@Autowired
	private GetIrnHeaderSezwopRepository getIrnSezwopHeaderRepo;

	@Autowired
	private GetIrnLineItemSezwopRepository getIrnSezwopLineRepo;

	@Autowired
	private GetIrnHeaderSezwpRepository getIrnSezwpHeaderRepo;

	@Autowired
	private GetIrnLineItemSezwpRepository getIrnSezwpLineRepo;

	@Autowired
	private GetIrnHeaderDexpRepository getIrnDexpHeaderRepo;

	@Autowired
	private GetIrnLineItemDexpRepository getIrnDexpLineRepo;

	@Autowired
	private GetIrnHeaderExpwpRepository getIrnExpwpHeaderRepo;

	@Autowired
	private GetIrnLineItemExpWpRepository getIrnExpWpLineRepo;

	@Autowired
	private GetIrnHeaderExwopRepository getIrnExpWopHeaderRepo;

	@Autowired
	private GetIrnLineItemExpWopRepository getIrnExpWopLineRepo;

	@Autowired
	private GetIrnPreceedingDocDtlsRepository getIrnPreDocRepo;

	@Autowired
	private GetIrnContractDtlsRepository getIrnCntrRepo;

	@Autowired
	private GetIrnAddSuppDocDtlsRepository getIrnSuppDocRepo;

	@Autowired
	private GetIrnItmAttributeDocDtlsRepository getIrnItmAttrDocRepo;
	
	@Autowired
	private InwardEInvoiceERPRequestRepository erpRepo;

	@Autowired
	@Qualifier("InwardGetIrnDetailsDataParserServiceImpl")
	private InwardGetIrnDetailsDataParserService convertHdrLineService;

	private final DateTimeFormatter formatter = DateTimeFormatter
			.ofPattern("yyyy-MM-dd HH:mm:ss");

	private final DateTimeFormatter formatter1 = DateTimeFormatter
			.ofPattern("dd/MM/yyyy");
	
	private final DateTimeFormatter formatter2 = DateTimeFormatter
			.ofPattern("yyyy-MM-dd");
	
	private final DateTimeFormatter formatter3 = DateTimeFormatter
			.ofPattern("dd/MM/yyyy HH:mm:ss");

	public Integer processInwardEinvoiceWorkSheetData(Object[][] objList,
			int columnCount, String cgstin, String taxPeriod,
			Gstr1GetInvoicesReqDto dto) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"processInwardEinvoiceWorkSheetData list size [{}] started",
					objList.length);
		}
		List<InwardEinvoiceDataDto> inwardEinvoiceDataList = inwardEinvoiceProcessUtil
				.convertInwardEinvoiceWorkSheetDataToList(objList, columnCount,
						cgstin, taxPeriod);
		String groupCode = TenantContext.getTenantId();
		Map<String, Map<String, Map<String, List<InwardEinvoiceDataDto>>>> groupedData = inwardEinvoiceDataList
				.stream()
				.collect(Collectors.groupingBy(
						InwardEinvoiceDataDto::getCustomerGstin,
						Collectors.groupingBy(
								InwardEinvoiceDataDto::getIrnGenerationPeriod,
								Collectors.groupingBy(
										InwardEinvoiceDataDto::getSupplyType))));

		GetIrnListDtlsDto irnListDtlsDto = new GetIrnListDtlsDto();
		List<GetIrnCtinListDtlsDto> irnList = new ArrayList<>();
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("groupedData list size {}",
					groupedData);
		}
		
		Map<String, Long> batchIdMap = new HashMap<>();
//		groupedData.forEach((customerGstin, returnPeriodMap) -> returnPeriodMap
//				.forEach((returnPeriod, supplyTypeMap) -> supplyTypeMap
//						.forEach((supplyType, dtoList) -> {
//							Gstr1GetInvoicesReqDto reqDto = new Gstr1GetInvoicesReqDto();
//							reqDto.setGstin(customerGstin);
//							reqDto.setReturnPeriod(returnPeriod);
//							reqDto.setType(supplyType);
//
//							irnListDtlsDto.setRequestDate(
//									LocalDateTime.now().toString());
//							GetIrnCtinListDtlsDto ctinListDtlsDto = new GetIrnCtinListDtlsDto();
//							
//							Map<String, List<InwardEinvoiceDataDto>> groupedBySupplierGstin = dtoList
//									.stream()
//									.collect(Collectors.groupingBy(
//											InwardEinvoiceDataDto::getSupplierGstin));
//							
//							groupedBySupplierGstin.forEach((supplierGstin, invoices) -> {
//					            
//					            ctinListDtlsDto.setSuppGstin(supplierGstin);
//					            List<GetIrnDtlsRespDto> irnDtlsRespDtos = invoices
//										.stream().map(dtoIrn -> {
//											GetIrnDtlsRespDto irnDtlsRespDto = new GetIrnDtlsRespDto();
//											irnDtlsRespDto.setDocNum(
//													dtoIrn.getDocumentNumber());
//											LocalDate documentDate = dtoIrn.getDocumentDate();
//											String documentDateString = documentDate.format(formatter1);
//											irnDtlsRespDto.setDocDt(documentDateString);
//											irnDtlsRespDto.setDocTyp(
//													dtoIrn.getDocumentType());
//											irnDtlsRespDto.setSuppTyp(
//													dtoIrn.getSupplyType());
//											irnDtlsRespDto.setTotInvAmt(dtoIrn.getItemAssessableAmount());
//											irnDtlsRespDto
//													.setIrn(dtoIrn.getIrnNumber());
//											if (dtoIrn.getIrnStatus() != null) {
//											    if (dtoIrn.getIrnStatus().equals("Active")) {
//											    	irnDtlsRespDto.setIrnSts("ACT");
//											    } else if (dtoIrn.getIrnStatus().equals("Cancelled")) {
//											    	irnDtlsRespDto.setIrnSts("CNL");
//											    } else {
//											    	irnDtlsRespDto.setIrnSts(
//															dtoIrn.getIrnStatus());
//											    }
//											}
//											irnDtlsRespDto.setAckNo(dtoIrn
//													.getAcknowledgmentNumber());
//											LocalDateTime irnDate = dtoIrn.getIrnDate();
//											String formattedDate = irnDate.format(formatter);
//											irnDtlsRespDto.setAckDt(formattedDate);
//											irnDtlsRespDto.setEwbNo(
//													dtoIrn.getEWayBillNumber());
//											String ewayBilldate = dtoIrn.getEWayBillDate();
//											if(ewayBilldate != null){
//												LocalDate date = null;
//												if (ewayBilldate.contains("-")) {
//								                    date = LocalDate.parse(ewayBilldate, formatter2);
//								                } else if (ewayBilldate.contains("/")) {
//								                    date = LocalDate.parse(ewayBilldate, formatter1);
//								                }
//												irnDtlsRespDto.setEwbDt(date.format(formatter1));
//											}
//											
//											if(dtoIrn.getIrnCancellationDate() != null)
//											{
//												LocalDateTime irnCancellationDate = dtoIrn.getIrnCancellationDate();
//												String formattedDate1 = irnCancellationDate.format(formatter);
//												irnDtlsRespDto.setCnlDt(formattedDate1);
//											}
//											
//											if(dtoIrn.getValidUpto() != null)
//											{
//												LocalDateTime validUpto = dtoIrn.getValidUpto();
//												String formattedDate2 = validUpto.format(formatter);
//												irnDtlsRespDto.setEwbVald(
//														formattedDate2);
//											}
//											
//											irnDtlsRespDto.setRmrks(
//													dtoIrn.getInvoiceRemarks());
//											irnDtlsRespDto.setCnlRsn(
//													dtoIrn.getCancellationReason());
//											irnDtlsRespDto.setCnlRem(dtoIrn
//													.getCancellationRemarks());
//											return irnDtlsRespDto;
//										}).collect(Collectors.toList());
//					            
//					            ctinListDtlsDto.setIrnDtl(irnDtlsRespDtos);
//					        });
//							
//							irnList.add(ctinListDtlsDto);
//							irnListDtlsDto.setIrnList(irnList);
//							if (batchIdMap.containsKey(supplyType)) {
//				                reqDto.setBatchId(batchIdMap.get(supplyType));
//				            } else {
//				                reqDto = createBatchAndSave(groupCode, reqDto);
//				                batchIdMap.put(supplyType, reqDto.getBatchId());
//				            }
//							getIrnDetails(reqDto, groupCode, irnListDtlsDto,
//									inwardEinvoiceDataList, dtoList);
//
//						})));
		groupedData.forEach((customerGstin, returnPeriodMap) -> 
	    returnPeriodMap.forEach((returnPeriod, supplyTypeMap) -> 
	        supplyTypeMap.forEach((supplyType, dtoList) -> {
	            // Use a single-element array to hold reqDto
	            final Gstr1GetInvoicesReqDto[] reqDtoHolder = {new Gstr1GetInvoicesReqDto()};
	            reqDtoHolder[0].setGstin(customerGstin);
	            reqDtoHolder[0].setReturnPeriod(returnPeriod);
	            reqDtoHolder[0].setType(supplyType);

	            irnListDtlsDto.setRequestDate(LocalDateTime.now().toString());

	            // Group by Supplier GSTIN
	            Map<String, List<InwardEinvoiceDataDto>> groupedBySupplierGstin = dtoList
	                    .stream()
	                    .collect(Collectors.groupingBy(InwardEinvoiceDataDto::getSupplierGstin));

	            // Iterate over each group by Supplier GSTIN
	            groupedBySupplierGstin.forEach((supplierGstin, invoices) -> {
	                // Create a new GetIrnCtinListDtlsDto for each supplier GSTIN
	                GetIrnCtinListDtlsDto ctinListDtlsDto = new GetIrnCtinListDtlsDto();
	                ctinListDtlsDto.setSuppGstin(supplierGstin);

	                // Process each invoice in the list
	                List<GetIrnDtlsRespDto> irnDtlsRespDtos = invoices.stream().map(dtoIrn -> {
	                    GetIrnDtlsRespDto irnDtlsRespDto = new GetIrnDtlsRespDto();
	                    irnDtlsRespDto.setDocNum(dtoIrn.getDocumentNumber());
	                    LocalDate documentDate = dtoIrn.getDocumentDate();
	                    String documentDateString = documentDate.format(formatter1);
	                    irnDtlsRespDto.setDocDt(documentDateString);
	                    irnDtlsRespDto.setDocTyp(dtoIrn.getDocumentType());
	                    irnDtlsRespDto.setSuppTyp(dtoIrn.getSupplyType());
	                    irnDtlsRespDto.setTotInvAmt(dtoIrn.getItemAssessableAmount());
	                    irnDtlsRespDto.setIrn(dtoIrn.getIrnNumber());
	                    if (dtoIrn.getIrnStatus() != null) {
	                        if (dtoIrn.getIrnStatus().equals("Active")) {
	                            irnDtlsRespDto.setIrnSts("ACT");
	                        } else if (dtoIrn.getIrnStatus().equals("Cancelled")) {
	                            irnDtlsRespDto.setIrnSts("CNL");
	                        } else {
	                            irnDtlsRespDto.setIrnSts(dtoIrn.getIrnStatus());
	                        }
	                    }
	                    irnDtlsRespDto.setAckNo(dtoIrn.getAcknowledgmentNumber());
	                    LocalDateTime irnDate = dtoIrn.getIrnDate();
	                    String formattedDate = irnDate.format(formatter);
	                    irnDtlsRespDto.setAckDt(formattedDate);
	                    irnDtlsRespDto.setEwbNo(dtoIrn.getEWayBillNumber());
	                    LocalDateTime ewayBilldate = dtoIrn.getEWayBillDate();
	                    if (ewayBilldate != null) {
	                    	String ewayBillFormattedDate = ewayBilldate.format(formatter);
	                        irnDtlsRespDto.setEwbDt(ewayBillFormattedDate);
	                    }
	                    if (dtoIrn.getIrnCancellationDate() != null) {
	                        LocalDateTime irnCancellationDate = dtoIrn.getIrnCancellationDate();
	                        String formattedDate1 = irnCancellationDate.format(formatter);
	                        irnDtlsRespDto.setCnlDt(formattedDate1);
	                    }
	                    if (dtoIrn.getValidUpto() != null) {
	                        LocalDateTime validUpto = dtoIrn.getValidUpto();
	                        String formattedDate2 = validUpto.format(formatter);
	                        irnDtlsRespDto.setEwbVald(formattedDate2);
	                    }
	                    irnDtlsRespDto.setRmrks(dtoIrn.getInvoiceRemarks());
	                    irnDtlsRespDto.setCnlRsn(dtoIrn.getCancellationReason());
	                    irnDtlsRespDto.setCnlRem(dtoIrn.getCancellationRemarks());
	                    return irnDtlsRespDto;
	                }).collect(Collectors.toList());

	                // Set the IRN details list in the DTO
	                ctinListDtlsDto.setIrnDtl(irnDtlsRespDtos);

	                // Add the current CTIN details DTO to the list
	                irnList.add(ctinListDtlsDto);

	                // Update irnListDtlsDto with the current irnList
	                irnListDtlsDto.setIrnList(irnList);

	                // Ensure getIrnDetails is called for each supplier GSTIN
	                if (batchIdMap.containsKey(supplyType)) {
	                    reqDtoHolder[0].setBatchId(batchIdMap.get(supplyType));
	                } else {
	                    reqDtoHolder[0] = createBatchAndSave(groupCode, reqDtoHolder[0]);
	                    batchIdMap.put(supplyType, reqDtoHolder[0].getBatchId());
	                }

	                // Call getIrnDetails for each supplier GSTIN group
	                getIrnDetails(reqDtoHolder[0], groupCode, irnListDtlsDto, inwardEinvoiceDataList, invoices);
	            });
	        })
	    )
	);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("processInwardEinvoiceWorkSheetData list size [{}]",
					inwardEinvoiceDataList.size());
		}

		return inwardEinvoiceDataList.size();
	}

	private Gstr1GetInvoicesReqDto createBatchAndSave(String groupCode,
			Gstr1GetInvoicesReqDto dto) {
		if (APIConstants.INV_TYPE_DEXP.equalsIgnoreCase(dto.getType())) {
			dto.setType(APIConstants.INV_TYPE_DXP);
		}
		TenantContext.setTenantId(groupCode);
		// InActiveting Previous Batch Records
		batchRepo.softlyDelete(dto.getType().toUpperCase(),
				APIConstants.GET_IRN_LIST, dto.getGstin(),
				dto.getReturnPeriod());
		GetAnx1BatchEntity batch = batchUtil.makeBatchGstr1(dto, dto.getType(),
				APIConstants.GET_IRN_LIST);
		// Save new Batch
		batch = batchRepo.save(batch);
		dto.setBatchId(batch.getId());
		
		InwardEInvoiceERPRequestEntity erpEntity = new InwardEInvoiceERPRequestEntity();
		
		erpEntity.setBatchId(dto.getBatchId());
		erpEntity.setSupplyType(dto.getType());
		erpEntity.setGstin(dto.getGstin());
		erpEntity.setTaxPeriod(dto.getReturnPeriod());
		erpEntity.setStatus("INITIATED");
		erpEntity.setCreatedOn(LocalDateTime.now());
		
		erpRepo.save(erpEntity);
		
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
	private void getIrnDetails(Gstr1GetInvoicesReqDto dto, String groupCode,
			GetIrnListDtlsDto reqDto,
			List<InwardEinvoiceDataDto> inwardEinvoiceDataList,
			List<InwardEinvoiceDataDto> dtoList) {

		if (LOGGER.isDebugEnabled()) {
		    LOGGER.debug("reqDto content: {}", reqDto);
		}
		if (LOGGER.isDebugEnabled()) {
		    LOGGER.debug("dto content: {}", dto);
		}
		if (LOGGER.isDebugEnabled()) {
		    LOGGER.debug("dtoList content: {}", dtoList.size());
		}
		if (LOGGER.isDebugEnabled()) {
		    LOGGER.debug("dtoList content: {}", inwardEinvoiceDataList.size());
		}
		List<String> listIrn = parseIrnListData(dto, groupCode, reqDto);
		if (LOGGER.isDebugEnabled()) {
		    LOGGER.debug("listIrn content: {}", listIrn);
		}
		 Map<String, String> irnMap = createIrnMap(listIrn);
		 if (LOGGER.isDebugEnabled()) {
			    LOGGER.debug("irnMap content: {}", irnMap);
			}

		 String response = "";
		try {
			if (listIrn != null && groupCode != null) {
				
				 for (Map.Entry<String, String> entry : irnMap.entrySet()) {
					 String irn = entry.getKey();
			         String irnSts = entry.getValue();
			         dto.setIrn(irn);
			         dto.setIrnSts(irnSts);
			         if (LOGGER.isDebugEnabled()) {
			        	    LOGGER.debug("Gstr1GetInvoicesReqDto content: {}", dto);
			        	}
			         if (LOGGER.isDebugEnabled()) {
			             LOGGER.debug("irn: {}, irnSts: {}", irn, irnSts);
			         }
			         getIrnListingRepo.updateGetDtlSts(irn, "Initiated",
			        		 irnSts);
			         if (LOGGER.isDebugEnabled()) {
			        	    LOGGER.debug("dtoList content: {}", dtoList);
			        	}
			         List<InwardEinvoiceDataDto> filteredData = dtoList.stream()
			                    .filter(data -> irn.equals(data.getIrnNumber()) && irnSts.equals(data.getIrnStatus()))
			                    .collect(Collectors.toList());
			         
			         if (LOGGER.isDebugEnabled()) {
			        	    LOGGER.debug("filteredData content: {}", filteredData);
			        	}
			         Map<String, GetIrnDtlsRespDto> result = new HashMap<>();
			         String issName = "";
			         for (InwardEinvoiceDataDto dtoIrn : filteredData) {
			        	 GetIrnDtlsRespDto irnDtlsRespDto = new GetIrnDtlsRespDto();
			        	 issName = dtoIrn.getSourceIrp();
							irnDtlsRespDto.setDocNum(
									dtoIrn.getDocumentNumber());
							LocalDate documentDate = dtoIrn.getDocumentDate();
							String documentDateString = documentDate.format(formatter1);
							irnDtlsRespDto.setDocDt(documentDateString);
							irnDtlsRespDto.setDocTyp(
									dtoIrn.getDocumentType());
							irnDtlsRespDto.setSuppTyp(
									dtoIrn.getSupplyType());
							irnDtlsRespDto.setTotInvAmt(dtoIrn.getItemAssessableAmount());
							irnDtlsRespDto
									.setIrn(dtoIrn.getIrnNumber());
							if (dtoIrn.getIrnStatus() != null) {
							    if (dtoIrn.getIrnStatus().equals("Active")) {
							    	irnDtlsRespDto.setIrnSts("ACT");
							    } else if (dtoIrn.getIrnStatus().equals("Cancelled")) {
							    	irnDtlsRespDto.setIrnSts("CNL");
							    } else {
							    	irnDtlsRespDto.setIrnSts(
											dtoIrn.getIrnStatus());
							    }
							}
							irnDtlsRespDto.setAckNo(dtoIrn
									.getAcknowledgmentNumber());
							LocalDateTime irnDate = dtoIrn.getIrnDate();
							String formattedDate = irnDate.format(formatter);
							irnDtlsRespDto.setAckDt(formattedDate);
							irnDtlsRespDto.setEwbNo(
									dtoIrn.getEWayBillNumber());
							LocalDateTime ewayBilldate = dtoIrn.getEWayBillDate();
			                    if (ewayBilldate != null) {
			                    	String ewayBillFormattedDate = ewayBilldate.format(formatter);
			                        irnDtlsRespDto.setEwbDt(ewayBillFormattedDate);
			                    }
							if(dtoIrn.getIrnCancellationDate() != null)
							{
								LocalDateTime irnCancellationDate = dtoIrn.getIrnCancellationDate();
								String formattedDate1 = irnCancellationDate.format(formatter);
								irnDtlsRespDto.setCnlDt(formattedDate1);
							}
							
							if(dtoIrn.getValidUpto() != null)
							{
								LocalDateTime validUpto = dtoIrn.getValidUpto();
								String formattedDate2 = validUpto.format(formatter);
								irnDtlsRespDto.setEwbVald(
										formattedDate2);
							}
							irnDtlsRespDto.setRmrks(
									dtoIrn.getInvoiceRemarks());
							irnDtlsRespDto.setCnlRsn(
									dtoIrn.getCancellationReason());
							irnDtlsRespDto.setCnlRem(dtoIrn
									.getCancellationRemarks());
							result.put(irnDtlsRespDto.getIrn(), irnDtlsRespDto);
			         }
			         if (LOGGER.isDebugEnabled()) {
			        	    LOGGER.debug("result content: {}", result);
			        	}
			         List<EinvoiceRequestDto> respDto = generateEinvoiceData(filteredData);
			         if (LOGGER.isDebugEnabled()) {
			        	    LOGGER.debug("respDto content: {}", respDto);
			        	}
			         
			         Map<String, EinvoiceRequestDto> irnToEinvoiceRequestDtoMap = respDto.stream()
		                        .collect(Collectors.toMap(EinvoiceRequestDto::getIrn, einvoiceDto -> einvoiceDto, (existing, replacement) -> replacement));


			         if (LOGGER.isDebugEnabled()) {
						    LOGGER.debug("irnToEinvoiceRequestDtoMap content: {}", irnToEinvoiceRequestDtoMap);
						}
		             EinvoiceRequestDto einvoiceDto = irnToEinvoiceRequestDtoMap.get(irn);
		             if (LOGGER.isDebugEnabled()) {
		        		    LOGGER.debug("einvoiceDto content: {}", einvoiceDto);
		        		}
			         GetIrnDtlsRespDto irnDtlDto = result.get(irn);
		        	 if (LOGGER.isDebugEnabled()) {
		        		    LOGGER.debug("irnDtlDto content: {}", irnDtlDto);
		        		}
		        	
		        	 response = parseHeaderAndLineIrnDtls(dto, irnDtlDto,
								groupCode, einvoiceDto, issName);
		        	 
		        	 if (LOGGER.isDebugEnabled()) {
		        		    LOGGER.debug("response: {}", response);
		        		}
//			         for(EinvoiceRequestDto einvoiceDto : respDto){			        	 
//			        	 
//			         }
			        
			         if ("SUCCESS".equalsIgnoreCase(response)) {
							 getIrnListingRepo.updateGetDtlSts(irn, "SUCCESS",
							 irnSts);
							 batchRepo.updateStatusById(dto.getBatchId(), "SUCCESS");
						 }
						
						 if ("CNL".equalsIgnoreCase(irnSts) || "Cancelled".equalsIgnoreCase(irnSts)) {
							 getIrnListingRepo.updateIsDeleteFlag(irn);
						 }		
				 }
				
			}
				
				 
				
				// getIrnListingRepo.updateGetDtlSts(dto.getIrn(), "FAILED",
				// dto.getIrnSts());

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Get IRN Details Gstn Processed with args {} ",
							dto);
				}
			} catch (Exception ex) {
			String msg = "GetIrnDtlProcessor got interrupted. "
					+ "Jobs might not be completed. Marking as 'Failed'";

			LOGGER.error(msg);
			getIrnListingRepo.updateGetDtlSts(dto.getIrn(), "FAILED",
					dto.getIrnSts());
			throw new AppException(msg, ex);
		}
	}

	private List<EinvoiceRequestDto> generateEinvoiceData(
			List<InwardEinvoiceDataDto> dtoList) {
		Map<String, List<InwardEinvoiceDataDto>> groupedByIrn = dtoList.stream()
				.collect(Collectors
						.groupingBy(InwardEinvoiceDataDto::getIrnNumber));
		List<EinvoiceRequestDto> einvoiceRequestDtoList = new ArrayList<>();
		
		groupedByIrn.forEach((irn, irnDtoList) -> {
			irnDtoList.forEach(irnDto -> {
				EinvoiceRequestDto einvoiceRequestDto = new EinvoiceRequestDto();
				// Set IRN and other common fields
				einvoiceRequestDto.setIrn(irn);
				einvoiceRequestDto.setVersion("1.1"); // Assuming version is
														// constant
				einvoiceRequestDto.setTaxSch(irnDto.getTaxScheme());

				// Populate transaction details
				TransactionDetails transactionDetails = new TransactionDetails();
				transactionDetails.setTaxSch(irnDto.getTaxScheme());
				transactionDetails.setSupTyp(irnDto.getSupplyType());
				transactionDetails.setRegRev(irnDto.getReverseChargeFlag());
				transactionDetails.setEcmGstin(irnDto.getEcomGstin());
				transactionDetails.setIgstOnIntra(irnDto.getSection7OfIgstFlag());
				einvoiceRequestDto.setTranDtls(transactionDetails);

				// Populate document details
				DocumentDetails documentDetails = new DocumentDetails();
				documentDetails.setTyp(irnDto.getDocumentType());
				documentDetails.setNo(irnDto.getDocumentNumber());
				documentDetails
						.setDt(irnDto.getDocumentDate());
				einvoiceRequestDto.setDocDtls(documentDetails);

				// Populate seller details
				SellerDetails sellerDetails = new SellerDetails();
				sellerDetails.setGstin(irnDto.getSupplierGstin());
				sellerDetails.setTrdNm(irnDto.getSupplierTradeName());
				sellerDetails.setLglNm(irnDto.getSupplierLegalName());
				sellerDetails.setAddr1(irnDto.getSupplierAddress1());
				sellerDetails.setAddr2(irnDto.getSupplierAddress2());
				sellerDetails.setLoc(irnDto.getSupplierLocation());
				sellerDetails
						.setPin(Integer.valueOf(irnDto.getSupplierPincode()));
				sellerDetails.setState(irnDto.getSupplierStateCode());
				sellerDetails.setPh(irnDto.getSupplierPhone());
				sellerDetails.setEm(irnDto.getSupplierEmail());
				einvoiceRequestDto.setSellerDtls(sellerDetails);

				// Populate buyer details
				BuyerDetails buyerDetails = new BuyerDetails();
				buyerDetails.setGstin(irnDto.getCustomerGstin());
				buyerDetails.setTrdNm(irnDto.getCustomerTradeName());
				buyerDetails.setLglNm(irnDto.getCustomerLegalName());
				buyerDetails.setAddr1(irnDto.getCustomerAddress1());
				buyerDetails.setAddr2(irnDto.getCustomerAddress2());
				buyerDetails.setLoc(irnDto.getCustomerLocation());
				buyerDetails.setPos(irnDto.getBillingPos());
				buyerDetails
						.setPin(Integer.valueOf(irnDto.getCustomerPincode()));
				buyerDetails.setState(irnDto.getCustomerStateCode());
				buyerDetails.setPh(irnDto.getCustomerPhone());
				buyerDetails.setEm(irnDto.getCustomerEmail());
				einvoiceRequestDto.setBuyerDtls(buyerDetails);

				// Populate dispatch details
				DispatchDetails dispatchDetails = new DispatchDetails();
				dispatchDetails.setNm(irnDto.getDispatcherTradeName());
				dispatchDetails.setAddr1(irnDto.getDispatcherAddress1());
				dispatchDetails.setAddr2(irnDto.getDispatcherAddress2());
				dispatchDetails.setLoc(irnDto.getDispatcherLocation());
				dispatchDetails.setPin(irnDto.getDispatcherPincode());
				dispatchDetails.setStcd(irnDto.getDispatcherStateCode());
				einvoiceRequestDto.setDispDtls(dispatchDetails);

				// Populate shipping details
				ShippingDetails shippingDetails = new ShippingDetails();
				shippingDetails.setGstin(irnDto.getShipToGstin());
				shippingDetails.setTrdNm(irnDto.getShipToTradeName());
				shippingDetails.setLglNm(irnDto.getShipToLegalName());
				shippingDetails.setAddr1(irnDto.getShipToAddress1());
				shippingDetails.setAddr2(irnDto.getShipToAddress2());
				shippingDetails.setLoc(irnDto.getShipToLocation());
				shippingDetails
						.setPin(irnDto.getShipToPincode());
				shippingDetails.setStcd(irnDto.getShipToStateCode());
				einvoiceRequestDto.setShipDtls(shippingDetails);

				// Populate item details
				List<ItemDto> itemList = irnDtoList.stream().map(dtoItem -> {
					ItemDto itemDto = new ItemDto();
					itemDto.setSlNo(dtoItem.getItemSerialNumber());
					itemDto.setPrdSlNo(dtoItem.getProductSerialNumber());
					itemDto.setIsServc(dtoItem.getIsService());
					itemDto.setHsnCd(dtoItem.getHsn());
				    itemDto.setQty(dtoItem.getQuantity());
					itemDto.setFreeQty(dtoItem.getFreeQuantity());
					itemDto.setUnitPrice(dtoItem.getUnitPrice());
					 itemDto.setTotAmt(dtoItem.getItemAmount());
					 itemDto.setDiscount(dtoItem.getItemDiscount());
					 itemDto.setPreTaxVal(dtoItem.getPreTaxAmount());
					 itemDto.setAssAmt(dtoItem.getItemAssessableAmount());
					 itemDto.setGstRt(dtoItem.getIgstRate());
					 itemDto.setIgstAmt(dtoItem.getIgstAmount());
					 //itemDto.setCesRt(dtoItem.getCgstRate());
					 itemDto.setCgstAmt(dtoItem.getCgstAmount());
					 itemDto.setCesRt(dtoItem.getCessAdValoremRate());
					 itemDto.setStateCesRt(dtoItem.getSgstRate());
					 itemDto.setSgstAmt(dtoItem.getSgstAmount());
					 itemDto.setCesRt(dtoItem.getCessAdValoremRate());
					 itemDto.setCesAmt(dtoItem.getCessAdValoremAmount());
					 itemDto.setCesNonAdvlAmt(dtoItem.getCessSpecificAmount());
					 itemDto.setStateCesRt(dtoItem.getStateCessAdValoremRate());
					 itemDto.setStateCesAmt(dtoItem.getStateCessAdValoremAmount());
					 itemDto.setStateCesNonAdvlAmt(dtoItem.getStateCessSpecificAmount());
					 itemDto.setOthChrg(dtoItem.getItemOtherCharges());
					 itemDto.setBarcde(dtoItem.getBarcode());
					 itemDto.setTotItemVal(dtoItem.getTotalItemAmount());
					 BatchDetails bchDtls = new BatchDetails();
					 bchDtls.setNm(dtoItem.getBatchName());
					 bchDtls.setExpDt(dtoItem.getBatchExpiryDate());
					 bchDtls.setWrDt(dtoItem.getWarrantyDate());
					 itemDto.setBchDtls(bchDtls);
					 itemDto.setPrdDesc(dtoItem.getProductDescription());
					 itemDto.setUnit(dtoItem.getUqc());
					 itemDto.setOrdLineRef(dtoItem.getOrderLineReference());
					 itemDto.setOrgCntry(dtoItem.getOriginCountry());
					 Attribute attDtls = new Attribute();
					 attDtls.setNm(dtoItem.getAttributeName());
					 attDtls.setVal(dtoItem.getAttributeValue());
					 if (LOGGER.isDebugEnabled()) {
		        		    LOGGER.debug("attDtls: {}", attDtls);
		        		}
					 List<Attribute> attribDtls = new ArrayList<>();
					 attribDtls.add(attDtls);
					 itemDto.setAttribDtls(attribDtls);
					 if (LOGGER.isDebugEnabled()) {
		        		    LOGGER.debug("attribDtls List: {}", attribDtls);
		        		}
					return itemDto;
				}).collect(Collectors.toList());
				 if (LOGGER.isDebugEnabled()) {
	        		    LOGGER.debug("itemList: {}", itemList);
	        		}
				einvoiceRequestDto.setItemList(itemList);

				// Populate value details
				ValueDetails valueDetails = new ValueDetails();
				 valueDetails.setAssVal(irnDto.getInvoiceAssessableAmount());
				 valueDetails.setCgstVal(irnDto.getInvoiceCgstAmount());
				 valueDetails.setSgstVal(irnDto.getInvoiceSgstAmount());
				 valueDetails.setIgstVal(irnDto.getInvoiceIgstAmount());
				 valueDetails.setCesVal(irnDto.getInvoiceCessAdValoremAmount());
				 //valueDetails.setCessNonAdVal(irnDto.getInvoiceCessSpecificAmount());
				 valueDetails.setStCesVal(irnDto.getInvoiceStateCessAdValoremAmount());
				 //valueDetails.setStCesNonAdVal(irnDto.getInvoiceStateCessSpecificAmount());
				 valueDetails.setDiscount(irnDto.getInvoiceDiscount());
				 valueDetails.setOthChrg(irnDto.getInvoiceOtherCharges());
				 valueDetails.setRndOffAmt(irnDto.getRoundOff());
				 valueDetails.setTotInvVal(irnDto.getInvoiceValue());
				 valueDetails.setTotInvValFc(irnDto.getInvoiceValueFc());
				einvoiceRequestDto.setValDtls(valueDetails);

				// Populate payee details
				PayeeDetails payeeDetails = new PayeeDetails();
				payeeDetails.setNm(irnDto.getPayeeName());
				payeeDetails.setAccDet(irnDto.getAccountDetail());
				payeeDetails.setMode(irnDto.getModeOfPayment());
				payeeDetails.setFinInsBr(irnDto.getBranchOrIfscCode());
				payeeDetails.setPayTerm(irnDto.getPaymentTerms());
				payeeDetails.setPayInstr(irnDto.getPaymentInstruction());
				payeeDetails.setCrTrn(irnDto.getCreditTransfer());
				 payeeDetails.setDirDr(irnDto.getDirectDebit());
				 payeeDetails.setCrDay(irnDto.getCreditDays());
				 payeeDetails.setPaidAmt(irnDto.getPaidAmount());
				 payeeDetails.setPaymtDue(irnDto.getBalanceAmount());
				einvoiceRequestDto.setPayDtls(payeeDetails);

//				// Populate reference details
				RefDtls refDtls = new RefDtls();
				 refDtls.setInvRm(irnDto.getInvoiceRemarks());
				 DocPerdDtls docPerdDtls = new DocPerdDtls();
				 docPerdDtls.setInvStDt(irnDto.getInvoicePeriodStartDate());
				 docPerdDtls.setInvEndDt(irnDto.getInvoicePeriodEndDate());
				 refDtls.setDocPerdDtls(docPerdDtls);
//				 List<PrecDocument> precDocumentList= new ArrayList<>();
//				 PrecDocument precDocument= new PrecDocument();
//				 precDocument.setInvNo(irnDto.getPrecedingInvoiceNumber());
//				 precDocument.setInvDt(irnDto.getPrecedingInvoiceDate());
//				 precDocument.setOthRefNo(irnDto.getOtherReference());
//				 precDocumentList.add(precDocument);
//				 refDtls.setPrecDocDtls(precDocumentList);
//				 
//				 //contract details
//				 List<Contract> contractList= new ArrayList<>();
//				 Contract contract = new Contract();
//				 contract.setRecAdvRefr(irnDto.getReceiptAdviceReference());
//				 contract.setRecAdvDt(irnDto.getReceiptAdviceDate());
//				 contract.setTendRef(irnDto.getTenderReference());
//				 contract.setContrRef(irnDto.getContractReference());
//				 contract.setExtRef(irnDto.getExternalReference());
//				 contract.setProjRef(irnDto.getProjectReference());
//				 contract.setPORefr(irnDto.getCustomerPoReferenceNumber());
//				 contract.setPORefDt(irnDto.getCustomerPoReferenceDate());
//				 contractList.add(contract);
//				 refDtls.setContrDtls(contractList);
//				einvoiceRequestDto.setRefDtls(refDtls);
//				
//				
//				// additional details
//				 List<AddlDocument> addlDocumentList= new ArrayList<>();
//				 AddlDocument addlDetails = new AddlDocument();
//				 addlDetails.setDocs(irnDto.getSupportingDocument());
//				 addlDetails.setInfo(irnDto.getAdditionalInformation());
//				 addlDetails.setUrl(irnDto.getSupportingDocUrl());
//				 addlDocumentList.add(addlDetails);
//				 einvoiceRequestDto.setAddlDocDtls(addlDocumentList);
				
				 List<PrecDocument> precDocumentList = irnDtoList.stream().map(irnDto1 -> {
			            PrecDocument precDocument = new PrecDocument();
			            precDocument.setInvNo(irnDto1.getPrecedingInvoiceNumber());
			            precDocument.setInvDt(irnDto1.getPrecedingInvoiceDate());
			            precDocument.setOthRefNo(irnDto1.getOtherReference());
			            return precDocument;
			        }).collect(Collectors.toList());
			        refDtls.setPrecDocDtls(precDocumentList);

			        // Populate contract details
			        List<Contract> contractList = irnDtoList.stream().map(irnDto2 -> {
			            Contract contract = new Contract();
			            contract.setRecAdvRefr(irnDto2.getReceiptAdviceReference());
			            contract.setRecAdvDt(irnDto2.getReceiptAdviceDate());
			            contract.setTendRef(irnDto2.getTenderReference());
			            contract.setContrRef(irnDto2.getContractReference());
			            contract.setExtRef(irnDto2.getExternalReference());
			            contract.setProjRef(irnDto2.getProjectReference());
			            contract.setPORefr(irnDto2.getCustomerPoReferenceNumber());
			            contract.setPORefDt(irnDto2.getCustomerPoReferenceDate());
			            return contract;
			        }).collect(Collectors.toList());
			        refDtls.setContrDtls(contractList);
			        einvoiceRequestDto.setRefDtls(refDtls);

			        // Populate additional details
			        List<AddlDocument> addlDocumentList = irnDtoList.stream().map(irnDto3 -> {
			            AddlDocument addlDetails = new AddlDocument();
			            addlDetails.setDocs(irnDto3.getSupportingDocument());
			            addlDetails.setInfo(irnDto3.getAdditionalInformation());
			            addlDetails.setUrl(irnDto3.getSupportingDocUrl());
			            return addlDetails;
			        }).collect(Collectors.toList());
			        einvoiceRequestDto.setAddlDocDtls(addlDocumentList);

				
				// Populate export details
				ExportDetails exportDetails = new ExportDetails();
				 exportDetails.setExpDuty(irnDto.getExportDuty());
				 exportDetails.setForCur(irnDto.getCurrencyCode());
				 exportDetails.setCntCode(irnDto.getCountryCode());
				 exportDetails.setPort(irnDto.getPortCode());
				 exportDetails.setRefClm(irnDto.getClaimRefundFlag());
				 exportDetails.setShipBDt(irnDto.getShippingBillDate());
				 exportDetails.setShipBNo(irnDto.getShippingBillNumber());
				einvoiceRequestDto.setExpDtls(exportDetails);

				// Populate EWB details
				EinvEwbDetails ewbDetails = new EinvEwbDetails();
				ewbDetails.setTransId(irnDto.getTransporterId());
				ewbDetails.setTransName(irnDto.getTransporterName());
				ewbDetails.setTransMode(irnDto.getTransportMode());
				 ewbDetails.setTransDocNo(irnDto.getTransportDocNo());
				 ewbDetails.setTransDocDt(irnDto.getTransportDocDate());
				 ewbDetails.setVehNo(irnDto.getVehicleNo());
				ewbDetails.setVehType(irnDto.getVehicleType());
				ewbDetails.setDistance(irnDto.getDistance());
				einvoiceRequestDto.setEwbDetails(ewbDetails);

				// Set acknowledgment details
				einvoiceRequestDto
						.setAckNum(irnDto.getAcknowledgmentNumber().toString());
				LocalDateTime irnDate = irnDto.getIrnDate();
				String formattedDate = irnDate.format(formatter);
				einvoiceRequestDto.setAckDt(formattedDate);
				einvoiceRequestDtoList.add(einvoiceRequestDto);
			});
		});
		
		 if (LOGGER.isDebugEnabled()) {
 		    LOGGER.debug("einvoiceRequestDtoList: {}", einvoiceRequestDtoList);
 		}
		return einvoiceRequestDtoList;
	}

	public List<String> parseIrnListData(Gstr1GetInvoicesReqDto dto,
	        String groupcode, GetIrnListDtlsDto reqDto) {
	    try {
	        Map<String, GetIrnListEntity> irnListRespSts = new HashMap<>();

	        List<GetIrnListEntity> irnListEntity = getIrnListingRepo
	                .findByCusGstinAndMonYear(dto.getGstin(), dto.getReturnPeriod());

	        irnListRespSts = irnListEntity.stream()
	                .collect(Collectors.toMap(
	                        o -> o.getIrn() + "-" + o.getIrnSts(),
	                        Function.identity(),
	                        (existing, replacement) -> existing));

	        if (LOGGER.isDebugEnabled()) {
	            LOGGER.debug(
	                    "GETIRN inside InwardGetIrnListDataParserImpl for batch id {}, irnListRespSts {}",
	                    dto.getBatchId(), irnListRespSts);
	        }

	        List<String> incremtlIrnList = new ArrayList<>();
	        List<GetIrnListEntity> irnDtls = new ArrayList<>();

	        List<Long> updateIds = new ArrayList<>();
	        List<Long> updateBatchIds = new ArrayList<>();

	        Set<String> processedIrns = new HashSet<>();

	        for (GetIrnCtinListDtlsDto irnListDto : reqDto.getIrnList()) {
	            String cusGstin = dto.getGstin();
	            String suppGstin = irnListDto.getSuppGstin();
	            String month = dto.getReturnPeriod();
	            for (GetIrnDtlsRespDto irnDtlDto : irnListDto.getIrnDtl()) {
	                String irnKey = irnDtlDto.getIrn() + "-" + irnDtlDto.getIrnSts();
	                if (processedIrns.add(irnKey)) {
	                    convertToListingEntity(irnDtlDto, suppGstin, cusGstin,
	                            irnDtls, dto.getBatchId(), updateIds,
	                            irnListRespSts, month, incremtlIrnList,
	                            updateBatchIds, groupcode);
	                }
	            }
	        }

	        getIrnListingRepo.saveAll(irnDtls);

	        if (!updateBatchIds.isEmpty()) {
	            getIrnListingRepo.updateBatchId(updateBatchIds, dto.getBatchId());
	        }

	        return incremtlIrnList;

	    } catch (Exception ex) {
	        String msg = "Exception occurred in InwardGetIrnListDataParserImpl";
	        LOGGER.error(ex.getLocalizedMessage());
	        throw new AppException(ex, msg);
	    }
	}

	public void convertToListingEntity(GetIrnDtlsRespDto irnDtlDto,
			String suppGstin, String cusGstin, List<GetIrnListEntity> irnDtls,
			Long batchId, List<Long> updateIds,
			Map<String, GetIrnListEntity> irnListRespSts, String month,
			List<String> incremtlIrnList, List<Long> updateBatchIds,
			String groupCode) {
		String irnSts = irnDtlDto.getIrn() + "-" + irnDtlDto.getIrnSts();
		if (irnListRespSts.containsKey(irnSts)) {

			if ("Failed".equalsIgnoreCase(
					irnListRespSts.get(irnSts).getGetIrnDetSts())
					|| "NOT_INITIATED".equalsIgnoreCase(
							irnListRespSts.get(irnSts).getGetIrnDetSts())) {

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"GETIRN inside  InwardGetIrnListDataParserImpl failed irn {} ",
							irnDtlDto.getIrn());
				}

				incremtlIrnList
						.add(irnDtlDto.getIrn() + "-" + irnDtlDto.getIrnSts());
				updateBatchIds.add(irnListRespSts.get(irnSts).getId());
			}

		} else {

			entityList(irnDtlDto, suppGstin, cusGstin, irnDtls, batchId, month,
					incremtlIrnList, groupCode);
		}

	}

	public void entityList(GetIrnDtlsRespDto irnDtlDto, String suppGstin,
			String cusGstin, List<GetIrnListEntity> irnDtls, Long batchId,
			String month, List<String> incremtlIrnList, String groupCode) {

		// saving incremental irns
		incremtlIrnList.add(irnDtlDto.getIrn() + "-" + irnDtlDto.getIrnSts());

		GetIrnListEntity dto = new GetIrnListEntity();
		dto.setCusGstin(cusGstin);
		dto.setSuppGstin(suppGstin);
		dto.setDocNum(
				irnDtlDto.getDocNum() != null ? irnDtlDto.getDocNum() : null);
		dto.setDocDate(!Strings.isNullOrEmpty(irnDtlDto.getDocDt()) ? LocalDate
				.parse(irnDtlDto.getDocDt(), formatter1).atStartOfDay() : null);
		String docType = irnDtlDto.getDocTyp();
		if ("CRN".equalsIgnoreCase(docType)) {
			docType = "CR";
		} else if ("DBN".equalsIgnoreCase(docType)) {
			docType = "DR";
		}
		dto.setDocTyp(docType != null ? docType : null);

		String suppTyp = irnDtlDto.getSuppTyp();
		if ("DEXP".equalsIgnoreCase(suppTyp)) {
			suppTyp = "DXP";
		}
		dto.setSuppType(suppTyp != null ? suppTyp : null);
		dto.setTotInvAmt(irnDtlDto.getTotInvAmt() != null
				? irnDtlDto.getTotInvAmt() : null);
		dto.setIrn(irnDtlDto.getIrn() != null ? irnDtlDto.getIrn() : null);
		if (irnDtlDto.getIrnSts() != null) {
		    if (irnDtlDto.getIrnSts().equalsIgnoreCase("Active")) {
		        dto.setIrnSts("ACT");
		    } else if (irnDtlDto.getIrnSts().equalsIgnoreCase("Cancelled")) {
		        dto.setIrnSts("CNL");
		    } else {
		        dto.setIrnSts(irnDtlDto.getIrnSts());
		    }
		} else {
		    dto.setIrnSts(null);
		}
		dto.setAckNo(
				irnDtlDto.getAckNo() != null ? irnDtlDto.getAckNo() : null);
		dto.setAckDate(!Strings.isNullOrEmpty(irnDtlDto.getAckDt())
				? LocalDateTime.parse(irnDtlDto.getAckDt(), formatter) : null);
		dto.setEwbNum(irnDtlDto.getEwbNo() != null
				? String.valueOf(irnDtlDto.getEwbNo()) : null);
		LocalDate date = null;
		dto.setEwbDate(!Strings.isNullOrEmpty(irnDtlDto.getEwbDt())
				? LocalDateTime.parse(irnDtlDto.getEwbDt(), formatter) : null);
		
		dto.setCanDate(!Strings.isNullOrEmpty(irnDtlDto.getCnlDt())
				? LocalDateTime.parse(irnDtlDto.getCnlDt(), formatter) : null);
		dto.setBatchId(batchId);
		dto.setMonYear(month);
		dto.setGetIrnDetSts("NOT_INITIATED");
		dto.setDerivdMonYear(month.substring(2, 6) + month.substring(0, 2));
		dto.setCreatedBy(groupCode);
		dto.setCreatedOn(LocalDateTime.now());
		dto.setGetIrnDetIniOn(LocalDateTime.now());
		irnDtls.add(dto);

	}

	public String parseHeaderAndLineIrnDtls(Gstr1GetInvoicesReqDto dto,
			GetIrnDtlsRespDto irnDtlDto, String groupCode,
			EinvoiceRequestDto respDto, String issName) {
		LocalDateTime now = EYDateUtil
				.toUTCDateTimeFromLocal(LocalDateTime.now());

		BigDecimal arr[] = new BigDecimal[] { BigDecimal.ZERO, BigDecimal.ZERO,
				BigDecimal.ZERO, BigDecimal.ZERO };

		arr = calculateCessValue(respDto, arr);

		BigDecimal invoiceCessAdvaloremAmount = arr[0];
		BigDecimal invoiceCessSpecificAmount = arr[1];
		BigDecimal invoiceStateCessAdvaloremAmount = arr[2];
		BigDecimal invoiceStateCessSpecificAmount = arr[3];

		List<GetIrnPreceedingDocDetailEntity> nestedList = new ArrayList<>();

		List<GetIrnContractDetailEntity> contrctList = new ArrayList<>();

		List<GetIrnAdditionalSuppDocsEntity> addSuppDocList = new ArrayList<>();

		List<GetIrnItemAttributeListEntity> irnAtributeList = new ArrayList<>();

		try {
			if (APIConstants.INV_TYPE_B2B.equalsIgnoreCase(dto.getType())) {
				GetIrnB2bHeaderEntity hdrEntity = new GetIrnB2bHeaderEntity();
				List<GetIrnB2bItemEntity> lineList = new ArrayList<>();
				GetIrnB2bHeaderEntity hdrEntity2 = new GetIrnB2bHeaderEntity();

				hdrEntity = convertHdrLineService.convertToHeaderList(respDto,
						dto, irnDtlDto, GetIrnB2bHeaderEntity.class);

				hdrEntity.setInvCessAdvaloremAmt(invoiceCessAdvaloremAmount);
				hdrEntity.setInvCessSpecificAmt(invoiceCessSpecificAmount);
				hdrEntity.setInvStateCessAdvaloremAmt(
						invoiceStateCessAdvaloremAmount);
				hdrEntity.setInvStateCessSpecificAmt(
						invoiceStateCessSpecificAmount);
				// hdrEntity
				// .setSignedQR(GenUtil.convertStringToClob(signedQrCode));
				hdrEntity.setIssNme(issName);
				hdrEntity2 = getIrnb2bHeaderRepo.save(hdrEntity);

				lineList = convertHdrLineService.convertToLineandNestedList(
						respDto, dto, irnDtlDto, hdrEntity2.getId(),
						GetIrnB2bItemEntity.class, nestedList, contrctList,
						addSuppDocList, irnAtributeList);
				getIrnb2bLineRepo.saveAll(lineList);
				if (!nestedList.isEmpty()) {
					getIrnPreDocRepo.saveAll(nestedList);
				}
				if (!contrctList.isEmpty()) {
					getIrnCntrRepo.saveAll(contrctList);

				}
				if (!addSuppDocList.isEmpty()) {
					getIrnSuppDocRepo.saveAll(addSuppDocList);
				}
				if (!irnAtributeList.isEmpty()) {
					getIrnItmAttrDocRepo.saveAll(irnAtributeList);
				}

			} else if (APIConstants.INV_TYPE_SEZWOP
					.equalsIgnoreCase(dto.getType())) {
				GetIrnSezWopHeaderEntity hdrEntity = new GetIrnSezWopHeaderEntity();
				List<GetIrnSezWopItemEntity> lineList = new ArrayList<>();
				hdrEntity = convertHdrLineService.convertToHeaderList(respDto,
						dto, irnDtlDto, GetIrnSezWopHeaderEntity.class);

				hdrEntity.setInvCessAdvaloremAmt(invoiceCessAdvaloremAmount);
				hdrEntity.setInvCessSpecificAmt(invoiceCessSpecificAmount);
				hdrEntity.setInvStateCessAdvaloremAmt(
						invoiceStateCessAdvaloremAmount);
				hdrEntity.setInvStateCessSpecificAmt(
						invoiceStateCessSpecificAmount);
				// hdrEntity
				// .setSignedQR(GenUtil.convertStringToClob(signedQrCode));
				hdrEntity.setIssNme(issName);
				GetIrnSezWopHeaderEntity hdrEntity2 = new GetIrnSezWopHeaderEntity();

				hdrEntity2 = getIrnSezwopHeaderRepo.save(hdrEntity);
				lineList = convertHdrLineService.convertToLineandNestedList(
						respDto, dto, irnDtlDto, hdrEntity2.getId(),
						GetIrnSezWopItemEntity.class, nestedList, contrctList,
						addSuppDocList, irnAtributeList);
				getIrnSezwopLineRepo.saveAll(lineList);

				if (!nestedList.isEmpty()) {
					getIrnPreDocRepo.saveAll(nestedList);
				}
				if (!contrctList.isEmpty()) {
					getIrnCntrRepo.saveAll(contrctList);

				}
				if (!addSuppDocList.isEmpty()) {
					getIrnSuppDocRepo.saveAll(addSuppDocList);
				}
				if (!irnAtributeList.isEmpty()) {
					getIrnItmAttrDocRepo.saveAll(irnAtributeList);
				}
			} else if (APIConstants.INV_TYPE_SEZWP
					.equalsIgnoreCase(dto.getType())) {
				GetIrnSezWpHeaderEntity hdrEntity = new GetIrnSezWpHeaderEntity();
				List<GetIrnSezWpItemEntity> lineList = new ArrayList<>();
				hdrEntity = convertHdrLineService.convertToHeaderList(respDto,
						dto, irnDtlDto, GetIrnSezWpHeaderEntity.class);

				hdrEntity.setInvCessAdvaloremAmt(invoiceCessAdvaloremAmount);
				hdrEntity.setInvCessSpecificAmt(invoiceCessSpecificAmount);
				hdrEntity.setInvStateCessAdvaloremAmt(
						invoiceStateCessAdvaloremAmount);
				hdrEntity.setInvStateCessSpecificAmt(
						invoiceStateCessSpecificAmount);
				// hdrEntity
				// .setSignedQR(GenUtil.convertStringToClob(signedQrCode));
				hdrEntity.setIssNme(issName);
				GetIrnSezWpHeaderEntity hdrEntity2 = new GetIrnSezWpHeaderEntity();

				hdrEntity2 = getIrnSezwpHeaderRepo.save(hdrEntity);
				lineList = convertHdrLineService.convertToLineandNestedList(
						respDto, dto, irnDtlDto, hdrEntity2.getId(),
						GetIrnSezWpItemEntity.class, nestedList, contrctList,
						addSuppDocList, irnAtributeList);
				getIrnSezwpLineRepo.saveAll(lineList);

				if (!nestedList.isEmpty()) {
					getIrnPreDocRepo.saveAll(nestedList);
				}
				if (!contrctList.isEmpty()) {
					getIrnCntrRepo.saveAll(contrctList);

				}
				if (!addSuppDocList.isEmpty()) {
					getIrnSuppDocRepo.saveAll(addSuppDocList);
				}
				if (!irnAtributeList.isEmpty()) {
					getIrnItmAttrDocRepo.saveAll(irnAtributeList);
				}
			} else if (APIConstants.INV_TYPE_DEXP
					.equalsIgnoreCase(dto.getType())
					|| APIConstants.INV_TYPE_DXP
							.equalsIgnoreCase(dto.getType())) {
				GetIrnDexpHeaderEntity hdrEntity = new GetIrnDexpHeaderEntity();
				List<GetIrnDexpItemEntity> lineList = new ArrayList<>();
				hdrEntity = convertHdrLineService.convertToHeaderList(respDto,
						dto, irnDtlDto, GetIrnDexpHeaderEntity.class);

				hdrEntity.setInvCessAdvaloremAmt(invoiceCessAdvaloremAmount);
				hdrEntity.setInvCessSpecificAmt(invoiceCessSpecificAmount);
				hdrEntity.setInvStateCessAdvaloremAmt(
						invoiceStateCessAdvaloremAmount);
				hdrEntity.setInvStateCessSpecificAmt(
						invoiceStateCessSpecificAmount);
				// hdrEntity
				// .setSignedQR(GenUtil.convertStringToClob(signedQrCode));
				hdrEntity.setIssNme(issName);
				GetIrnDexpHeaderEntity hdrEntity2 = new GetIrnDexpHeaderEntity();

				hdrEntity2 = getIrnDexpHeaderRepo.save(hdrEntity);
				lineList = convertHdrLineService.convertToLineandNestedList(
						respDto, dto, irnDtlDto, hdrEntity2.getId(),
						GetIrnDexpItemEntity.class, nestedList, contrctList,
						addSuppDocList, irnAtributeList);
				getIrnDexpLineRepo.saveAll(lineList);

				if (!nestedList.isEmpty()) {
					getIrnPreDocRepo.saveAll(nestedList);
				}
				if (!contrctList.isEmpty()) {
					getIrnCntrRepo.saveAll(contrctList);

				}
				if (!addSuppDocList.isEmpty()) {
					getIrnSuppDocRepo.saveAll(addSuppDocList);
				}
				if (!irnAtributeList.isEmpty()) {
					getIrnItmAttrDocRepo.saveAll(irnAtributeList);
				}
			} else if (APIConstants.INV_TYPE_EXPWP
					.equalsIgnoreCase(dto.getType())) {
				GetIrnExpWpHeaderEntity hdrEntity = new GetIrnExpWpHeaderEntity();
				List<GetIrnExpWpItemEntity> lineList = new ArrayList<>();
				hdrEntity = convertHdrLineService.convertToHeaderList(respDto,
						dto, irnDtlDto, GetIrnExpWpHeaderEntity.class);

				hdrEntity.setInvCessAdvaloremAmt(invoiceCessAdvaloremAmount);
				hdrEntity.setInvCessSpecificAmt(invoiceCessSpecificAmount);
				hdrEntity.setInvStateCessAdvaloremAmt(
						invoiceStateCessAdvaloremAmount);
				hdrEntity.setInvStateCessSpecificAmt(
						invoiceStateCessSpecificAmount);
				// hdrEntity
				// .setSignedQR(GenUtil.convertStringToClob(signedQrCode));
				hdrEntity.setIssNme(issName);
				GetIrnExpWpHeaderEntity hdrEntity2 = new GetIrnExpWpHeaderEntity();

				hdrEntity2 = getIrnExpwpHeaderRepo.save(hdrEntity);
				lineList = convertHdrLineService.convertToLineandNestedList(
						respDto, dto, irnDtlDto, hdrEntity2.getId(),
						GetIrnExpWpItemEntity.class, nestedList, contrctList,
						addSuppDocList, irnAtributeList);
				getIrnExpWpLineRepo.saveAll(lineList);
				if (!nestedList.isEmpty()) {
					getIrnPreDocRepo.saveAll(nestedList);
				}
				if (!contrctList.isEmpty()) {
					getIrnCntrRepo.saveAll(contrctList);

				}
				if (!addSuppDocList.isEmpty()) {
					getIrnSuppDocRepo.saveAll(addSuppDocList);
				}
				if (!irnAtributeList.isEmpty()) {
					getIrnItmAttrDocRepo.saveAll(irnAtributeList);
				}
			} else if (APIConstants.INV_TYPE_EXPWOP
					.equalsIgnoreCase(dto.getType())) {
				GetIrnExpWopHeaderEntity hdrEntity = new GetIrnExpWopHeaderEntity();
				List<GetIrnExpWopItemEntity> lineList = new ArrayList<>();
				hdrEntity = convertHdrLineService.convertToHeaderList(respDto,
						dto, irnDtlDto, GetIrnExpWopHeaderEntity.class);
				GetIrnExpWopHeaderEntity hdrEntity2 = new GetIrnExpWopHeaderEntity();

				hdrEntity.setInvCessAdvaloremAmt(invoiceCessAdvaloremAmount);
				hdrEntity.setInvCessSpecificAmt(invoiceCessSpecificAmount);
				hdrEntity.setInvStateCessAdvaloremAmt(
						invoiceStateCessAdvaloremAmount);
				hdrEntity.setInvStateCessSpecificAmt(
						invoiceStateCessSpecificAmount);
				// hdrEntity
				// .setSignedQR(GenUtil.convertStringToClob(signedQrCode));
				hdrEntity2 = getIrnExpWopHeaderRepo.save(hdrEntity);
				hdrEntity.setIssNme(issName);
				lineList = convertHdrLineService.convertToLineandNestedList(
						respDto, dto, irnDtlDto, hdrEntity2.getId(),
						GetIrnExpWopItemEntity.class, nestedList, contrctList,
						addSuppDocList, irnAtributeList);
				getIrnExpWopLineRepo.saveAll(lineList);

				if (!nestedList.isEmpty()) {
					getIrnPreDocRepo.saveAll(nestedList);
				}
				if (!contrctList.isEmpty()) {
					getIrnCntrRepo.saveAll(contrctList);

				}
				if (!addSuppDocList.isEmpty()) {
					getIrnSuppDocRepo.saveAll(addSuppDocList);
				}
				if (!irnAtributeList.isEmpty()) {
					getIrnItmAttrDocRepo.saveAll(irnAtributeList);
				}

			}

		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException
				| SecurityException | InstantiationException e) {

			LOGGER.error("Failed while parsing details of irn " + e);
			throw new AppException(e);

		} catch (Exception ex) {
			LOGGER.error("Failed while parsing the get irn Details " + ex);
			throw new AppException(ex);
		}

		return "SUCCESS";
	}

	private BigDecimal[] calculateCessValue(EinvoiceRequestDto respDto,
			BigDecimal[] arr1)

	{
		if (respDto.getItemList() != null) {
			for (ItemDto item : respDto.getItemList()) {
				arr1[0] = arr1[0].add(item.getCesAmt() != null
						? item.getCesAmt() : BigDecimal.ZERO);
				arr1[1] = arr1[1].add(item.getCesNonAdvlAmt() != null
						? item.getCesNonAdvlAmt() : BigDecimal.ZERO);
				arr1[2] = arr1[2].add(item.getStateCesAmt() != null
						? item.getStateCesAmt() : BigDecimal.ZERO);
				arr1[3] = arr1[3].add(item.getStateCesNonAdvlAmt() != null
						? item.getStateCesNonAdvlAmt() : BigDecimal.ZERO);
			}
		}

		return arr1;
	}

	public static void main(String[] args) {

		List<GetIrnPreceedingDocDetailEntity> nestedList = new ArrayList<>();

		List<GetIrnContractDetailEntity> contrctList = new ArrayList<>();

		List<GetIrnAdditionalSuppDocsEntity> addSuppDocList = new ArrayList<>();

		List<GetIrnItemAttributeListEntity> irnAtributeList = new ArrayList<>();

		Gson gson = new Gson();

		String requestObject = "{\"Version\": \"1.1\",\"TranDtls\": {\"TaxSch\": \"GST\",\"SupTyp\": \"B2B\",\"RegRev\": \"Y\",\"EcmGstin\": null,\"IgstOnIntra\": \"N\"},\"DocDtls\": {\"Typ\": \"INV\",\"No\": \"DOC/001\"},\"SellerDtls\": {\"Gstin\": \"37ARZPT4384Q1MT\",\"LglNm\": \"NIC company pvt ltd\",\"TrdNm\": \"NIC Industries\",\"Addr1\": \"5th block, kuvempu layout\",\"Addr2\": \"kuvempu layout\",\"Loc\": \"GANDHINAGAR\",\"Pin\": 518001,\"Stcd\": \"37\",\"Ph\": \"9000000000\",\"Em\": \"abc@gmail.com\"},\"BuyerDtls\": {\"Gstin\": \"29AWGPV7107B1Z1\",\"LglNm\": \"XYZ company pvt ltd\",\"TrdNm\": \"XYZ Industries\",\"Pos\": \"12\",\"Addr1\": \"7th block, kuvempu layout\",\"Addr2\": \"kuvempu layout\",\"Loc\": \"GANDHINAGAR\",\"Pin\": 562160,\"Stcd\": \"29\",\"Ph\": \"91111111111\",\"Em\": \"xyz@yahoo.com\"},\"DispDtls\": {\"Nm\": \"ABC company pvt ltd\",\"Addr1\": \"7th block, kuvempu layout\",\"Addr2\": \"kuvempu layout\",\"Loc\": \"Banagalore\",\"Pin\": 562160,\"Stcd\": \"29\"},\"ShipDtls\": {\"Gstin\": \"29AWGPV7107B1Z1\",\"LglNm\": \"CBE company pvt ltd\",\"TrdNm\": \"kuvempu layout\",\"Addr1\": \"7th block, kuvempu layout\",\"Addr2\": \"kuvempu layout\",\"Loc\": \"Banagalore\",\"Pin\": 562160,\"Stcd\": \"29\"},\"ItemList\": [{\"SlNo\": \"1\",\"PrdDesc\": \"Rice\",\"IsServc\": \"N\",\"HsnCd\": \"1001\",\"Barcde\": \"123456\",\"Qty\": 100.345,\"FreeQty\": 10,\"Unit\": \"BAG\",\"UnitPrice\": 99.545,\"TotAmt\": 9988.84,\"Discount\": 10,\"PreTaxVal\": 1,\"AssAmt\": 9978.84,\"GstRt\": 12.0,\"IgstAmt\": 1197.46,\"CgstAmt\": 0,\"SgstAmt\": 0,\"CesRt\": 5,\"CesAmt\": 498.94,\"CesNonAdvlAmt\": 10,\"StateCesRt\": 12,\"StateCesAmt\": 1197.46,\"StateCesNonAdvlAmt\": 5,\"OthChrg\": 10,\"TotItemVal\": 12897.7,\"OrdLineRef\": \"3256\",\"OrgCntry\": \"AG\",\"PrdSlNo\": \"12345\",\"BchDtls\": {\"Nm\": \"123456\"},\"AttribDtls\": [{\"Nm\": \"Rice\",\"Val\": \"10000\"}]}],\"ValDtls\": {\"AssVal\": 9978.84,\"CgstVal\": 0,\"SgstVal\": 0,\"IgstVal\": 1197.46,\"CesVal\": 508.94,\"StCesVal\": 1202.46,\"Discount\": 10,\"OthChrg\": 20,\"RndOffAmt\": 0.3,\"TotInvVal\": 12908,\"TotInvValFc\": 12897.7},\"PayDtls\": {\"Nm\": \"ABCDE\",\"AccDet\": \"5697389713210\",\"Mode\": \"Cash\",\"FinInsBr\": \"SBIN11000\",\"PayTerm\": \"100\",\"PayInstr\": \"Gift\",\"CrTrn\": \"test\",\"DirDr\": \"test\",\"CrDay\": 100,\"PaidAmt\": 10000,\"PaymtDue\": 5000},\"RefDtls\": {\"InvRm\": \"TEST\",\"DocPerdDtls\": {},\"PrecDocDtls\": [{\"InvNo\": \"DOC/002\",\"OthRefNo\": \"123456\"}],\"ContrDtls\": [{\"RecAdvRefr\": \"Doc/003\",\"TendRefr\": \"Abc001\",\"ContrRefr\": \"Co123\",\"ExtRefr\": \"Yo456\",\"ProjRefr\": \"Doc-456\",\"PORefr\": \"Doc-789\"}]},\"AddlDocDtls\": [{\"Url\": \"https://einv-apisandbox.nic.in\",\"Docs\": \"Test Doc\",\"Info\": \"Document Test\"}],\"ExpDtls\": {\"ShipBNo\": \"A-248\",\"Port\": \"INABG1\",\"RefClm\": \"N\",\"ForCur\": \"AED\",\"CntCode\": \"AE\",\"ExpDuty\": null},\"EwbDtls\": {\"TransId\": \"12AWGPV7107B1Z1\",\"TransName\": \"XYZ EXPORTS\",\"Distance\": 100,\"TransDocNo\": \"DOC01\",\"VehNo\": \"ka123456\",\"VehType\": \"R\",\"TransMode\": \"1\"}}";
		EinvoiceRequestDto respDto = gson.fromJson(requestObject,
				EinvoiceRequestDto.class);
		Gstr1GetInvoicesReqDto dto = new Gstr1GetInvoicesReqDto();

		GetIrnDtlsRespDto irnDtlDto = new GetIrnDtlsRespDto();

		try {
			List<GetIrnB2bItemEntity> lineList = convertToLineandNestedList(
					respDto, dto, irnDtlDto, 1L, GetIrnB2bItemEntity.class,
					nestedList, contrctList, addSuppDocList, irnAtributeList);
		} catch (Exception e) {
		}
	}

	public static <T> List<T> convertToLineandNestedList(
			EinvoiceRequestDto respDto, Gstr1GetInvoicesReqDto dto,
			GetIrnDtlsRespDto irnDtlDto, Long hdrEntityId, Class<T> itemEntity,
			List<GetIrnPreceedingDocDetailEntity> nestedList,
			List<GetIrnContractDetailEntity> contrctList,
			List<GetIrnAdditionalSuppDocsEntity> additonalSuppDocsList,
			List<GetIrnItemAttributeListEntity> itmAttList)
			throws IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException,
			InstantiationException {

		List<T> itemList = new ArrayList<>();

		// To Set the ITEM LIST

		for (ItemDto item : respDto.getItemList()) {
			T itmEntity = itemEntity.newInstance();
			itmEntity.getClass().getMethod("setHeaderId", Long.class)
					.invoke(itmEntity, hdrEntityId);
			itmEntity.getClass().getMethod("setItemSerialNumber", String.class)
					.invoke(itmEntity,
							item.getSlNo() != null ? item.getSlNo() : null);
			itmEntity.getClass()
					.getMethod("setProductSerialNumber", String.class)
					.invoke(itmEntity, item.getPrdSlNo() != null
							? item.getPrdSlNo() : null);
			itmEntity.getClass()
					.getMethod("setProductDescription", String.class)
					.invoke(itmEntity, item.getPrdDesc() != null
							? item.getPrdDesc() : null);
			itmEntity.getClass().getMethod("setIsService", String.class).invoke(
					itmEntity,
					item.getIsServc() != null ? item.getIsServc() : null);

			itmEntity.getClass()
					.getMethod("setOrderLineReference", String.class)
					.invoke(itmEntity, item.getOrdLineRef() != null
							? item.getOrdLineRef() : null);
			itmEntity.getClass().getMethod("setOriginCountry", String.class)
					.invoke(itmEntity, item.getOrgCntry() != null
							? item.getOrgCntry() : null);
			if (item.getBchDtls() != null) {
				itmEntity.getClass().getMethod("setBatchName", String.class)
						.invoke(itmEntity, item.getBchDtls().getNm() != null
								? item.getBchDtls().getNm() : null);

				itmEntity.getClass()
						.getMethod("setBatchExpiryDate", LocalDateTime.class)
						.invoke(itmEntity,
								item.getBchDtls().getExpDt() != null ? item
										.getBchDtls().getExpDt().atStartOfDay()
										: null);

				itmEntity.getClass()
						.getMethod("setWarrantyDate", LocalDateTime.class)
						.invoke(itmEntity,
								item.getBchDtls().getWrDt() != null ? item
										.getBchDtls().getWrDt().atStartOfDay()
										: null);
			}
			itmEntity.getClass().getMethod("setHsn", String.class).invoke(
					itmEntity,
					item.getHsnCd() != null ? item.getHsnCd() : null);
			itmEntity.getClass().getMethod("setBarcode", String.class).invoke(
					itmEntity,
					item.getBarcde() != null ? item.getBarcde() : null);

			itmEntity.getClass().getMethod("setQuantity", BigDecimal.class)
					.invoke(itmEntity, item.getQty() != null ? item.getQty()
							: BigDecimal.ZERO);

			itmEntity.getClass().getMethod("setFreeQuantity", BigDecimal.class)
					.invoke(itmEntity, item.getFreeQty() != null
							? item.getFreeQty() : BigDecimal.ZERO);

			itmEntity.getClass().getMethod("setUnitPrice", BigDecimal.class)
					.invoke(itmEntity, item.getUnitPrice() != null
							? item.getUnitPrice() : null);

			itmEntity.getClass().getMethod("setPreTaxAmount", BigDecimal.class)
					.invoke(itmEntity, item.getPreTaxVal() != null
							? item.getPreTaxVal() : null);

			itmEntity.getClass().getMethod("setItemAmount", BigDecimal.class)
					.invoke(itmEntity,
							item.getTotAmt() != null ? item.getTotAmt() : null);
			itmEntity.getClass().getMethod("setItemDiscount", BigDecimal.class)
					.invoke(itmEntity, item.getDiscount() != null
							? item.getDiscount() : null);
			itmEntity.getClass()
					.getMethod("setItemAssessableAmt", BigDecimal.class)
					.invoke(itmEntity,
							item.getAssAmt() != null ? item.getAssAmt() : null);
			itmEntity.getClass().getMethod("setIgstRate", BigDecimal.class)
					.invoke(itmEntity,
							item.getGstRt() != null ? item.getGstRt() : null);
			itmEntity.getClass().getMethod("setIgstAmount", BigDecimal.class)
					.invoke(itmEntity, item.getIgstAmt() != null
							? item.getIgstAmt() : null);
			itmEntity.getClass().getMethod("setCgstAmount", BigDecimal.class)
					.invoke(itmEntity, item.getCgstAmt() != null
							? item.getCgstAmt() : null);
			itmEntity.getClass().getMethod("setSgstAmount", BigDecimal.class)
					.invoke(itmEntity, item.getSgstAmt() != null
							? item.getSgstAmt() : null);
			itmEntity.getClass()
					.getMethod("setCessAdvaloremRate", BigDecimal.class)
					.invoke(itmEntity,
							item.getCesRt() != null ? item.getCesRt() : null);
			itmEntity.getClass()
					.getMethod("setCessAdvaloremAmount", BigDecimal.class)
					.invoke(itmEntity,
							item.getCesAmt() != null ? item.getCesAmt() : null);
			itmEntity.getClass()
					.getMethod("setCessSpecificAmount", BigDecimal.class)
					.invoke(itmEntity, item.getCesNonAdvlAmt() != null
							? item.getCesNonAdvlAmt() : null);
			itmEntity.getClass()
					.getMethod("setStateCessAdvaloremRate", BigDecimal.class)
					.invoke(itmEntity, item.getStateCesRt() != null
							? item.getStateCesRt() : null);
			itmEntity.getClass()
					.getMethod("setStateCessAdvaloremAmount", BigDecimal.class)
					.invoke(itmEntity, item.getStateCesAmt() != null
							? item.getStateCesAmt() : null);
			itmEntity.getClass()
					.getMethod("setStateCessSpecificAmount", BigDecimal.class)
					.invoke(itmEntity, item.getStateCesNonAdvlAmt() != null
							? item.getStateCesNonAdvlAmt() : null);
			itmEntity.getClass()
					.getMethod("setItemOtherCharges", BigDecimal.class)
					.invoke(itmEntity, item.getOthChrg() != null
							? item.getOthChrg() : null);
			itmEntity.getClass()
					.getMethod("setTotalItemAmount", BigDecimal.class)
					.invoke(itmEntity, item.getTotItemVal() != null
							? item.getTotItemVal() : null);
			itmEntity.getClass().getMethod("setUnit", String.class).invoke(
					itmEntity, item.getUnit() != null ? item.getUnit() : null);

			itmEntity.getClass().getMethod("setCreatedOn", LocalDateTime.class)
					.invoke(itmEntity, LocalDateTime.now());
			itmEntity.getClass().getMethod("setCreatedBy", String.class)
					.invoke(itmEntity, "SYSTEM");

			Long itmId = Long.valueOf(itmEntity.getClass().getMethod("getId")
					.invoke(itmEntity).toString());

			if (item.getAttribDtls() != null) {
				itmAttList.addAll(convertItmAttItem(hdrEntityId, itmId, respDto,
						item.getAttribDtls(), dto));
			}
			// taling the id
			// making attribute values persistance
			//

			itemList.add(itmEntity);

		}
		// headerList.add(entity);

		// Preceeding List

		if (respDto.getRefDtls() != null) {
			if (respDto.getRefDtls().getPrecDocDtls() != null) {
				for (PrecDocument nestedItem : respDto.getRefDtls()
						.getPrecDocDtls()) {

					GetIrnPreceedingDocDetailEntity preDocEntity = new GetIrnPreceedingDocDetailEntity();
					preDocEntity.setHeaderId(hdrEntityId);
					preDocEntity.setPreInvNum(nestedItem.getInvNo() != null
							? nestedItem.getInvNo() : null);
					preDocEntity
							.setInvReference(nestedItem.getOthRefNo() != null
									? nestedItem.getOthRefNo() : null);
					preDocEntity.setPreInvDate(nestedItem.getInvDt() != null
							? nestedItem.getInvDt().atStartOfDay() : null);
					preDocEntity.setSupplierGstin(
							respDto.getBuyerDtls().getGstin());
					preDocEntity.setBatchId(dto.getBatchId());

					if (respDto.getDocDtls() != null) {
						preDocEntity
								.setDocNum(respDto.getDocDtls().getNo() != null
										? respDto.getDocDtls().getNo() : null);
						preDocEntity.setDocDate(
								respDto.getDocDtls().getDt() != null ? respDto
										.getDocDtls().getDt().atStartOfDay()
										: null);
						preDocEntity.setDocType(
								respDto.getDocDtls().getTyp() != null
										? respDto.getDocDtls().getTyp() : null);
					}
					preDocEntity.setReturnPeriod(dto.getReturnPeriod());
					preDocEntity.setSupplyType(dto.getType());
					preDocEntity.setCreatedBy("SYSTEM");
					preDocEntity.setCreatedOn(LocalDateTime.now());
					preDocEntity.setDelete(false);

					nestedList.add(preDocEntity);

				}
			}

			if (respDto.getRefDtls().getContrDtls() != null) {

				for (Contract contrctItem : respDto.getRefDtls()
						.getContrDtls()) {

					GetIrnContractDetailEntity contrctEntity = new GetIrnContractDetailEntity();
					contrctEntity.setHeaderId(hdrEntityId);
					contrctEntity.setReceiptAdviceReference(
							contrctItem.getRecAdvRefr() != null
									? contrctItem.getRecAdvRefr() : null);
					contrctEntity.setReceiptAdviceDate(
							contrctItem.getRecAdvDt() != null
									? contrctItem.getRecAdvDt().atStartOfDay()
									: null);
					contrctEntity.setTenderReference(
							contrctItem.getTendRefr() != null
									? contrctItem.getTendRefr() : null);

					contrctEntity.setContractReference(
							contrctItem.getContrRefr() != null
									? contrctItem.getContrRefr() : null);

					contrctEntity.setExternalReference(
							contrctItem.getExtRefr() != null
									? contrctItem.getExtRefr() : null);

					contrctEntity.setProjectReference(
							contrctItem.getProjRefr() != null
									? contrctItem.getProjRefr() : null);

					contrctEntity
							.setCustPoRefNum(contrctItem.getPORefr() != null
									? contrctItem.getPORefr() : null);

					contrctEntity
							.setCustPoRefDate(contrctItem.getPORefDt() != null
									? contrctItem.getPORefDt().atStartOfDay()
									: null);

					contrctEntity.setSupplierGstin(
							respDto.getBuyerDtls().getGstin());

					contrctEntity.setBatchId(dto.getBatchId());

					if (respDto.getDocDtls() != null) {
						contrctEntity
								.setDocNum(respDto.getDocDtls().getNo() != null
										? respDto.getDocDtls().getNo() : null);
						contrctEntity.setDocDate(
								respDto.getDocDtls().getDt() != null ? respDto
										.getDocDtls().getDt().atStartOfDay()
										: null);
						contrctEntity.setDocType(
								respDto.getDocDtls().getTyp() != null
										? respDto.getDocDtls().getTyp() : null);
					}
					contrctEntity.setReturnPeriod(dto.getReturnPeriod());
					contrctEntity.setSupplyType(dto.getType());
					contrctEntity.setCreatedBy("SYSTEM");
					contrctEntity.setCreatedOn(LocalDateTime.now());
					contrctList.add(contrctEntity);

				}

			}
			if (respDto.getAddlDocDtls() != null) {
				for (AddlDocument adlDocEntity : respDto.getAddlDocDtls()) {

					GetIrnAdditionalSuppDocsEntity addlDocEntity = new GetIrnAdditionalSuppDocsEntity();
					addlDocEntity.setHeaderId(hdrEntityId);
					addlDocEntity.setSuppDocUrl(adlDocEntity.getUrl() != null
							? adlDocEntity.getUrl() : null);
					addlDocEntity
							.setSuppDocBase64(adlDocEntity.getDocs() != null
									? adlDocEntity.getDocs() : null);
					addlDocEntity.setAddInfo(adlDocEntity.getInfo() != null
							? adlDocEntity.getInfo() : null);

					addlDocEntity.setSupplierGstin(
							respDto.getBuyerDtls().getGstin());

					addlDocEntity.setBatchId(dto.getBatchId());

					if (respDto.getDocDtls() != null) {
						addlDocEntity
								.setDocNum(respDto.getDocDtls().getNo() != null
										? respDto.getDocDtls().getNo() : null);
						addlDocEntity.setDocDate(
								respDto.getDocDtls().getDt() != null ? respDto
										.getDocDtls().getDt().atStartOfDay()
										: null);
						addlDocEntity.setDocType(
								respDto.getDocDtls().getTyp() != null
										? respDto.getDocDtls().getTyp() : null);
					}
					addlDocEntity.setReturnPeriod(dto.getReturnPeriod());
					addlDocEntity.setSupplyType(dto.getType());
					addlDocEntity.setCreatedBy("SYSTEM");
					addlDocEntity.setCreatedOn(LocalDateTime.now());
					additonalSuppDocsList.add(addlDocEntity);

				}
			}
		}
		return itemList;
	}

	private static List<GetIrnItemAttributeListEntity> convertItmAttItem(
			Long hdrEntityId, Long itmId, EinvoiceRequestDto respDto,
			List<Attribute> attItm, Gstr1GetInvoicesReqDto dto) {

		List<GetIrnItemAttributeListEntity> itemAttList = new ArrayList<>();

		for (Attribute attItmEntity : attItm) {
			GetIrnItemAttributeListEntity itmAttDto = new GetIrnItemAttributeListEntity();
			itmAttDto.setHeaderId(hdrEntityId);
			itmAttDto.setItmId(itmId);

			itmAttDto.setSupplierGstin(respDto.getBuyerDtls().getGstin());

			itmAttDto.setBatchId(dto.getBatchId());

			itmAttDto.setAttrName(
					attItmEntity.getNm() != null ? attItmEntity.getNm() : null);

			itmAttDto.setAttrValue(attItmEntity.getVal() != null
					? attItmEntity.getVal() : null);

			if (respDto.getDocDtls() != null) {
				itmAttDto.setDocNum(respDto.getDocDtls().getNo() != null
						? respDto.getDocDtls().getNo() : null);
				itmAttDto.setDocDate(respDto.getDocDtls().getDt() != null
						? respDto.getDocDtls().getDt().atStartOfDay() : null);
				itmAttDto.setDocType(respDto.getDocDtls().getTyp() != null
						? respDto.getDocDtls().getTyp() : null);
			}
			itmAttDto.setReturnPeriod(dto.getReturnPeriod());
			itmAttDto.setSupplyType(dto.getType());
			itmAttDto.setCreatedBy("SYSTEM");
			itmAttDto.setCreatedOn(LocalDateTime.now());
			itemAttList.add(itmAttDto);
		}

		return itemAttList;
	}

}
