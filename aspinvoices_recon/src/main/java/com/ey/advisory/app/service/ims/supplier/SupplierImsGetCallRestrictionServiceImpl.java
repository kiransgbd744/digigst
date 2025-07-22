package com.ey.advisory.app.service.ims.supplier;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.GetAnx1BatchEntity;
import com.ey.advisory.app.data.entities.client.GstrReturnStatusEntity;
import com.ey.advisory.app.data.repositories.client.GetAnx1BatchRepository;
import com.ey.advisory.app.data.repositories.client.GstrReturnStatusRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;

import lombok.extern.slf4j.Slf4j;

@Component("SupplierImsGetCallRestrictionServiceImpl")
@Slf4j
public class SupplierImsGetCallRestrictionServiceImpl 
       implements SupplierImsGetCallRestrictionService{
	
	@Autowired
	@Qualifier("gstrReturnStatusRepository")
	private GstrReturnStatusRepository returnstatusRepo;
	
	@Autowired
	@Qualifier("getAnx1BatchRepository")
	private GetAnx1BatchRepository batchRepo;
	
	public static List<String> sectionList = new ArrayList<>(
			Arrays.asList("B2B", "B2BA", "CDNR", "CDNRA", "ECOM", "ECOMA"));

	public static List<String> returnTypeList = new ArrayList<>(
			Arrays.asList("GSTR1", "GSTR1A"));
	
	public List<Gstr1GetInvoicesReqDto> processGetCall(Gstr1GetInvoicesReqDto reqDto) {
		
		try{
	    List<Gstr1GetInvoicesReqDto> filteredReqList = new ArrayList<>();

	        String gstin = reqDto.getGstin();
	        String taxPeriod = reqDto.getReturnPeriod(); 
	        
	        Gstr1GetInvoicesReqDto dto = new Gstr1GetInvoicesReqDto();
		    List<String> eligibleReturnTypeList = new ArrayList<>();

	   
	    List <GetAnx1BatchEntity> batchEntities = batchRepo
	    		.findImsStatusBasedOnType(
	        gstin, taxPeriod, sectionList);
	    
	    if(batchEntities == null || batchEntities.isEmpty()){
	    	//Gstr1GetInvoicesReqDto dto = new Gstr1GetInvoicesReqDto();
            setDtoValues(filteredReqList, gstin, taxPeriod, dto);
         
        	if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Inside batchEntities where its value is null {} ",
						filteredReqList);
			}
		
            return filteredReqList;
       
	    }
	    // Fetching only FILED statuses
	    GstrReturnStatusEntity gstr1Status = returnstatusRepo
	    		.getGstr1StatuswithTaxPeriod(gstin, taxPeriod);
	    GstrReturnStatusEntity gstr3bStatus = returnstatusRepo
	    		.getGstr3bStatuswithTaxPeriod(gstin, taxPeriod);

	    boolean is3bFiled = (gstr3bStatus != null)
	           ? "FILED".equalsIgnoreCase(gstr3bStatus.getStatus()): false;
	    
	   
	    boolean isGstr1Filed = (gstr1Status != null)
		           ? "FILED".equalsIgnoreCase(gstr1Status.getStatus()): false;
	    
	    	
	    if(!is3bFiled){
	    	eligibleReturnTypeList.add("GSTR1A");

	            setEligibleDto(filteredReqList, gstin, taxPeriod, dto,
						eligibleReturnTypeList);
	            
	            if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Inside !is3bFiled {} ",
							filteredReqList);
				}

	    	
	    	
	    } if(!isGstr1Filed){
	    	// Gstr1GetInvoicesReqDto dto = new Gstr1GetInvoicesReqDto();
	    	eligibleReturnTypeList.add("GSTR1");

	            setEligibleDto(filteredReqList, gstin, taxPeriod, dto,
						eligibleReturnTypeList);
	            
	            if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Inside !isGstr1Filed {} ",
							filteredReqList);
				}
	            
	    }
	    if(!is3bFiled && !isGstr1Filed){

        return filteredReqList;
        
	    }
	    
	    Optional<LocalDateTime> maxCreatedOnOpt = batchEntities.stream()
	    	    .filter(Objects::nonNull)
	    	    .map(GetAnx1BatchEntity::getCreatedOn)
	    	    .filter(Objects::nonNull)
	    	    .max(Comparator.naturalOrder());
	    
	    LocalDateTime maxCreatedOnBatch = maxCreatedOnOpt.orElse(null);
	    
	    
	    LocalDateTime gstr1CreatedOn = gstr1Status!=null ? gstr1Status.getCreatedOn():null;
	    LocalDateTime gstr3bCreatedOn = gstr3bStatus!=null ? gstr3bStatus.getCreatedOn(): null;
	    
	   boolean restrictFlag1 =false;
	   boolean restrictFlag3b =false;

	    if(isGstr1Filed && gstr1CreatedOn != null 
	            && (gstr1CreatedOn.isAfter(maxCreatedOnBatch))){
	    	restrictFlag1 =true;
	    	eligibleReturnTypeList.add("GSTR1");

	    	
	    	setEligibleDto(filteredReqList, gstin, taxPeriod, dto,
					eligibleReturnTypeList);
	    	
	    	 if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Inside gstr1CreatedOn.isAfter(maxCreatedOnBatch) {} ",
							filteredReqList);
				}
	    	
	    	
	    } if(is3bFiled && gstr3bCreatedOn != null 
	            && (gstr3bCreatedOn.isAfter(maxCreatedOnBatch))){
	    	
	    	restrictFlag3b =true;
	    	
	    	eligibleReturnTypeList.add("GSTR1A");

	    	
            setEligibleDto(filteredReqList, gstin, taxPeriod, dto,
					eligibleReturnTypeList);
            
            if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Inside gstr3bCreatedOn.isAfter(maxCreatedOnBatch) {} ",
						filteredReqList);
			}
	    	
	    	
	    }
	    if(restrictFlag1 && restrictFlag3b){
	    	
	    	return filteredReqList;
	    }
	    
	    //restriction required
	    
	     if(is3bFiled && (maxCreatedOnBatch.isAfter(gstr3bCreatedOn))){
	    	
    	    List<String> validSections = new ArrayList<>();

	    	
	    	 List<GetAnx1BatchEntity> filteredBatchEntities = batchEntities.stream()
	                    .filter(entity -> "GSTR1A".equalsIgnoreCase(entity.getImsReturnType()) &&
	                                      !"SUCCESS_WITH_NO_DATA".equalsIgnoreCase(entity.getStatus()))
	                    .collect(Collectors.toList());

	            // If there are any valid entries, process them
	            if (!filteredBatchEntities.isEmpty()) {
	                validSections = filteredBatchEntities.stream()
	                        .map(GetAnx1BatchEntity::getType)
	                        .collect(Collectors.toList());
	            }
	            eligibleReturnTypeList.add("GSTR1A");

		    	
	            setGstr1aValidDto(filteredReqList, gstin, taxPeriod, dto,
						eligibleReturnTypeList, validSections);
	    	
	            if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Inside maxCreatedOnBatch.isAfter(gstr3bCreatedOn) {} ",
							filteredReqList);
				}

	    	
	    }  if(isGstr1Filed && (maxCreatedOnBatch.isAfter(gstr1CreatedOn))){
	    	
	    	 List<String> validSections = new ArrayList<>();
	            eligibleReturnTypeList.add("GSTR1");

		    	
	    	 List<GetAnx1BatchEntity> filteredBatchEntities = batchEntities.stream()
	                    .filter(entity -> "GSTR1".equalsIgnoreCase(entity.getImsReturnType()) &&
	                                      !"SUCCESS_WITH_NO_DATA".equalsIgnoreCase(entity.getStatus()))
	                    .collect(Collectors.toList());

	            // If there are any valid entries, process them
	            if (!filteredBatchEntities.isEmpty()) {
	                validSections = filteredBatchEntities.stream()
	                        .map(GetAnx1BatchEntity::getType)
	                        .collect(Collectors.toList());
	            }
	            
	            setGstr1ValidDto(filteredReqList, gstin, taxPeriod, dto,
						eligibleReturnTypeList, validSections);
	            
	            if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Inside maxCreatedOnBatch.isAfter(gstr1CreatedOn) {} ",
							filteredReqList);
				}
	    	
	    }

	    return filteredReqList;
	    
	} catch (Exception ex) {
		String msg = "Unexpected error while restricting getcalls in "
				+ "SupplierImsGetCallRestrictionServiceImpl ";
		LOGGER.error(msg, ex);
		throw new AppException(ex);
	}
}

	/**
	 * @param filteredReqList
	 * @param gstin
	 * @param taxPeriod
	 * @param dto
	 * @param eligibleReturnTypeList
	 * @param validSections
	 */
	private void setGstr1aValidDto(List<Gstr1GetInvoicesReqDto> filteredReqList,
			String gstin, String taxPeriod, Gstr1GetInvoicesReqDto dto,
			List<String> eligibleReturnTypeList, List<String> validSections) {
		dto.setGstin(gstin);
		dto.setReturnPeriod(taxPeriod);
		dto.setIsFailed(false);
		dto.setGstr1aSections(validSections);
		dto.setImsReturnTypeList(eligibleReturnTypeList);
		filteredReqList.add(dto);
	}
	private void setGstr1ValidDto(List<Gstr1GetInvoicesReqDto> filteredReqList,
			String gstin, String taxPeriod, Gstr1GetInvoicesReqDto dto,
			List<String> eligibleReturnTypeList, List<String> validSections) {
		dto.setGstin(gstin);
		dto.setReturnPeriod(taxPeriod);
		dto.setIsFailed(false);
		dto.setGstr1Sections(validSections);
		dto.setImsReturnTypeList(eligibleReturnTypeList);
		filteredReqList.add(dto);
	}

	/**
	 * @param filteredReqList
	 * @param gstin
	 * @param taxPeriod
	 * @param dto
	 * @param eligibleReturnTypeList
	 */
	private void setEligibleDto(List<Gstr1GetInvoicesReqDto> filteredReqList,
			String gstin, String taxPeriod, Gstr1GetInvoicesReqDto dto,
			List<String> eligibleReturnTypeList) {
		dto.setGstin(gstin);
		dto.setReturnPeriod(taxPeriod);
		dto.setIsFailed(false);
		dto.setGstr1Sections(sectionList);
		dto.setGstr1aSections(sectionList);

		dto.setImsReturnTypeList(eligibleReturnTypeList);
		filteredReqList.add(dto);
	}

	/**
	 * @param filteredReqList
	 * @param gstin
	 * @param taxPeriod
	 * @param dto
	 */
	private void setDtoValues(List<Gstr1GetInvoicesReqDto> filteredReqList,
			String gstin, String taxPeriod, Gstr1GetInvoicesReqDto dto) {
		dto.setGstin(gstin);
		dto.setReturnPeriod(taxPeriod);
		dto.setIsFailed(false);
		dto.setGstr1Sections(sectionList);
		dto.setGstr1aSections(sectionList);

		dto.setImsReturnTypeList(returnTypeList);
		filteredReqList.add(dto);
	}
 
}
