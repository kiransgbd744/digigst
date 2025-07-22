
package com.ey.advisory.app.inward.einvoice;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.asprecon.GetIrnListEntity;
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
import com.ey.advisory.app.data.repositories.client.asprecon.GetIrnLineItemSezwopRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.GetIrnListingRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.GetIrnPreceedingDocDtlsRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;
import com.ey.advisory.einv.common.JwtParserUtility;
import com.ey.advisory.einv.dto.EinvoiceRequestDto;
import com.ey.advisory.einv.dto.ItemDto;
import com.google.gson.Gson;
import com.google.gson.JsonParser;

import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Sakshi.jain
 * 
 *
 */
@Slf4j
@Transactional(value = "clientTransactionManager")
@Service("InwardGetIrnDetailsDataParserImpl")
public class InwardGetIrnDetailsDataParserImpl
        implements InwardGetIrnDetailsDataParser {

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
	private GetIrnHeaderDexpRepository getIrnDexpHeaderRepo;

		@Autowired
	private GetIrnHeaderExpwpRepository getIrnExpwpHeaderRepo;

	@Autowired
	private GetIrnHeaderExwopRepository getIrnExpWopHeaderRepo;

	@Autowired
	private GetIrnPreceedingDocDtlsRepository getIrnPreDocRepo;
	
	@Autowired
	private GetIrnContractDtlsRepository getIrnCntrRepo;

	@Autowired
	private GetIrnAddSuppDocDtlsRepository getIrnSuppDocRepo;

	@Autowired
	private GetIrnItmAttributeDocDtlsRepository getIrnItmAttrDocRepo;
	
	@Autowired
	GetIrnListingRepository getIrnListingRepository;

	
	@Autowired
	@Qualifier("InwardGetIrnDetailsDataParserServiceImpl")
	private InwardGetIrnDetailsDataParserService convertHdrLineService;

	private final DateTimeFormatter formatter = DateTimeFormatter
	        .ofPattern("yyyy-MM-dd HH:mm:ss");

	@Override
	public String parseHeaderAndLineIrnDtls(String signedPayload,
	        Gstr1GetInvoicesReqDto dto, GetIrnDtlsRespDto irnDtlDto,
	        String groupCode, String signedQrCode) {
		LocalDateTime now = EYDateUtil
		        .toUTCDateTimeFromLocal(LocalDateTime.now());

		Claims claims = JwtParserUtility
		        .getJwtBodyWithoutSignature(signedPayload);
		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();

		String claimResp = gson.toJson(claims);

		String requestObject = JsonParser.parseString(claimResp)
		        .getAsJsonObject().get("data").getAsString();
		
		String issNme = JsonParser.parseString(claimResp).getAsJsonObject().has("iss")?
				JsonParser.parseString(claimResp).getAsJsonObject().get("iss").getAsString():null;

		/*
		 * JsonObject reqObj = JsonParser.parseString(signedPayload)
		 * .getAsJsonObject().get("data").getAsJsonObject();
		 * 
		 */
		EinvoiceRequestDto respDto = gson.fromJson(requestObject,
		        EinvoiceRequestDto.class);

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
				GetIrnListEntity irnListEntity = mapToGetIrnListEntity(hdrEntity);
				updateIrnDetails(irnListEntity,hdrEntity.getSupplyType());
				
				hdrEntity.setInvCessAdvaloremAmt(invoiceCessAdvaloremAmount);
				hdrEntity.setInvCessSpecificAmt(invoiceCessSpecificAmount);
				hdrEntity.setInvStateCessAdvaloremAmt(
				        invoiceStateCessAdvaloremAmount);
				hdrEntity.setInvStateCessSpecificAmt(
				        invoiceStateCessSpecificAmount);
				hdrEntity.setSignedQR(GenUtil.convertStringToClob(signedQrCode));
				hdrEntity.setIssNme(issNme);
				hdrEntity2 = getIrnb2bHeaderRepo.save(hdrEntity);
				lineList = convertHdrLineService.convertToLineandNestedList(
				        respDto, dto, irnDtlDto, hdrEntity2.getId(),
				        GetIrnB2bItemEntity.class, nestedList,contrctList,addSuppDocList,irnAtributeList);
				//getIrnb2bLineRepo.saveAll(lineList);
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
				GetIrnListEntity irnListEntity = mapToGetIrnListEntity(hdrEntity);
				updateIrnDetails(irnListEntity, hdrEntity.getSupplyType());
			
				hdrEntity.setInvCessAdvaloremAmt(invoiceCessAdvaloremAmount);
				hdrEntity.setInvCessSpecificAmt(invoiceCessSpecificAmount);
				hdrEntity.setInvStateCessAdvaloremAmt(
				        invoiceStateCessAdvaloremAmount);
				hdrEntity.setInvStateCessSpecificAmt(
				        invoiceStateCessSpecificAmount);
				hdrEntity.setSignedQR(GenUtil.convertStringToClob(signedQrCode));
				hdrEntity.setIssNme(issNme);
				GetIrnSezWopHeaderEntity hdrEntity2 = new GetIrnSezWopHeaderEntity();

				hdrEntity2 = getIrnSezwopHeaderRepo.save(hdrEntity);
				lineList = convertHdrLineService.convertToLineandNestedList(
				        respDto, dto, irnDtlDto, hdrEntity2.getId(),
				        GetIrnSezWopItemEntity.class, nestedList,contrctList,addSuppDocList,irnAtributeList);
				//getIrnSezwopLineRepo.saveAll(lineList);
				
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
				GetIrnListEntity irnListEntity = mapToGetIrnListEntity(hdrEntity);
				updateIrnDetails(irnListEntity,hdrEntity.getSupplyType());
			
				hdrEntity.setInvCessAdvaloremAmt(invoiceCessAdvaloremAmount);
				hdrEntity.setInvCessSpecificAmt(invoiceCessSpecificAmount);
				hdrEntity.setInvStateCessAdvaloremAmt(
				        invoiceStateCessAdvaloremAmount);
				hdrEntity.setInvStateCessSpecificAmt(
				        invoiceStateCessSpecificAmount);
				hdrEntity.setSignedQR(GenUtil.convertStringToClob(signedQrCode));
				hdrEntity.setIssNme(issNme);
				GetIrnSezWpHeaderEntity hdrEntity2 = new GetIrnSezWpHeaderEntity();

				hdrEntity2 = getIrnSezwpHeaderRepo.save(hdrEntity);
				lineList = convertHdrLineService.convertToLineandNestedList(
				        respDto, dto, irnDtlDto, hdrEntity2.getId(),
				        GetIrnSezWpItemEntity.class, nestedList,contrctList,addSuppDocList,irnAtributeList);
				//getIrnSezwpLineRepo.saveAll(lineList);

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
			        .equalsIgnoreCase(dto.getType()) || APIConstants.INV_TYPE_DXP
			        .equalsIgnoreCase(dto.getType())) {
				GetIrnDexpHeaderEntity hdrEntity = new GetIrnDexpHeaderEntity();
				List<GetIrnDexpItemEntity> lineList = new ArrayList<>();
				hdrEntity = convertHdrLineService.convertToHeaderList(respDto,
				        dto, irnDtlDto, GetIrnDexpHeaderEntity.class);
				GetIrnListEntity irnListEntity = mapToGetIrnListEntity(hdrEntity);
				updateIrnDetails(irnListEntity,hdrEntity.getSupplyType());
			
				hdrEntity.setInvCessAdvaloremAmt(invoiceCessAdvaloremAmount);
				hdrEntity.setInvCessSpecificAmt(invoiceCessSpecificAmount);
				hdrEntity.setInvStateCessAdvaloremAmt(
				        invoiceStateCessAdvaloremAmount);
				hdrEntity.setInvStateCessSpecificAmt(
				        invoiceStateCessSpecificAmount);
				hdrEntity.setSignedQR(GenUtil.convertStringToClob(signedQrCode));
				hdrEntity.setIssNme(issNme);
				GetIrnDexpHeaderEntity hdrEntity2 = new GetIrnDexpHeaderEntity();

				hdrEntity2 = getIrnDexpHeaderRepo.save(hdrEntity);
				lineList = convertHdrLineService.convertToLineandNestedList(
				        respDto, dto, irnDtlDto, hdrEntity2.getId(),
				        GetIrnDexpItemEntity.class, nestedList,contrctList,addSuppDocList,irnAtributeList);
				//getIrnDexpLineRepo.saveAll(lineList);
			
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
				GetIrnListEntity irnListEntity = mapToGetIrnListEntity(hdrEntity);
				updateIrnDetails(irnListEntity,hdrEntity.getSupplyType());
			
				hdrEntity.setInvCessAdvaloremAmt(invoiceCessAdvaloremAmount);
				hdrEntity.setInvCessSpecificAmt(invoiceCessSpecificAmount);
				hdrEntity.setInvStateCessAdvaloremAmt(
				        invoiceStateCessAdvaloremAmount);
				hdrEntity.setInvStateCessSpecificAmt(
				        invoiceStateCessSpecificAmount);
				hdrEntity.setSignedQR(GenUtil.convertStringToClob(signedQrCode));
				hdrEntity.setIssNme(issNme);
				GetIrnExpWpHeaderEntity hdrEntity2 = new GetIrnExpWpHeaderEntity();

				hdrEntity2 = getIrnExpwpHeaderRepo.save(hdrEntity);
				lineList = convertHdrLineService.convertToLineandNestedList(
				        respDto, dto, irnDtlDto, hdrEntity2.getId(),
				        GetIrnExpWpItemEntity.class, nestedList,contrctList,addSuppDocList,irnAtributeList);
				//getIrnExpWpLineRepo.saveAll(lineList);
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
				GetIrnListEntity irnListEntity = mapToGetIrnListEntity(hdrEntity);
				updateIrnDetails(irnListEntity,hdrEntity.getSupplyType());
			
				GetIrnExpWopHeaderEntity hdrEntity2 = new GetIrnExpWopHeaderEntity();

				hdrEntity.setInvCessAdvaloremAmt(invoiceCessAdvaloremAmount);
				hdrEntity.setInvCessSpecificAmt(invoiceCessSpecificAmount);
				hdrEntity.setInvStateCessAdvaloremAmt(
				        invoiceStateCessAdvaloremAmount);
				hdrEntity.setInvStateCessSpecificAmt(
				        invoiceStateCessSpecificAmount);
				hdrEntity.setSignedQR(GenUtil.convertStringToClob(signedQrCode));
				hdrEntity2 = getIrnExpWopHeaderRepo.save(hdrEntity);
				hdrEntity.setIssNme(issNme);
				lineList = convertHdrLineService.convertToLineandNestedList(
				        respDto, dto, irnDtlDto, hdrEntity2.getId(),
				        GetIrnExpWopItemEntity.class, nestedList,contrctList,addSuppDocList,irnAtributeList);
				//getIrnExpWopLineRepo.saveAll(lineList);
		
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
	private GetIrnListEntity mapToGetIrnListEntity(Object sourceEntity) {
	    GetIrnListEntity irnListEntity = new GetIrnListEntity();
	    
	    try {
	        Class<?> sourceClass = sourceEntity.getClass();

	        // Using reflection to get and set fields dynamically
	        irnListEntity.setSuppGstin(getFieldValue(sourceEntity, sourceClass, "supplierGSTIN"));
	        irnListEntity.setDocNum(getFieldValue(sourceEntity, sourceClass, "docNum"));
	        irnListEntity.setDocDate(getFieldValue(sourceEntity, sourceClass, "docDate"));
	        irnListEntity.setDocTyp(getFieldValue(sourceEntity, sourceClass, "docType"));
	        irnListEntity.setCusGstin(getFieldValue(sourceEntity, sourceClass, "customerGSTIN"));
	        irnListEntity.setTotInvAmt(getFieldValue(sourceEntity, sourceClass, "invValue"));
	        irnListEntity.setIrn(getFieldValue(sourceEntity, sourceClass, "irn"));
	        irnListEntity.setIrnSts(getFieldValue(sourceEntity, sourceClass, "irnStatus"));
	        
	    } catch (Exception e) {
	    	LOGGER.error("Error in InwardGetIrnParseImpl"+e);
	        e.printStackTrace();  // Handle exception appropriately
	    }
	    
	    return irnListEntity;
	}

	@SuppressWarnings("unchecked")
	private <T> T getFieldValue(Object source, Class<?> sourceClass, String fieldName) {
	    try {
	        Field field = sourceClass.getDeclaredField(fieldName);
	        field.setAccessible(true); // Allow access to private fields
	        return (T) field.get(source);
	    } catch (NoSuchFieldException | IllegalAccessException e) {
	        // Field does not exist or cannot be accessed, return null or handle it
	        return null;
	    }
	}
	public void updateIrnDetails(GetIrnListEntity irnListEntity, String supplyType) {
		getIrnListingRepository.updateIrnDetails(
	        irnListEntity.getDocNum(),
	        irnListEntity.getDocTyp(),
	        irnListEntity.getSuppGstin(),
	        irnListEntity.getDocDate(),
	        irnListEntity.getTotInvAmt(),
	        irnListEntity.getIrn(),
	        irnListEntity.getIrnSts(),
	        supplyType
	    );
	}
}
