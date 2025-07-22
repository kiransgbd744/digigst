package com.ey.advisory.app.services.validation.sales;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GenUtil.trimAndConvToUpperCase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.EntityConfigPrmtEntity;
import com.ey.advisory.app.caches.UomCache;
import com.ey.advisory.app.data.entities.client.OutwardTransDocLineItem;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.app.util.OnboardingQuestionValidationsUtil;
import com.ey.advisory.app.util.OnboardingConstant.CONFIG_PARAM_OUTWARD_ANS_KEY_ID;
import com.ey.advisory.app.util.OnboardingConstant.CONFIG_PARAM_OUTWARD_QUE_KEY_ID;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.ProcessingResultType;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.google.common.base.Strings;

@Component("UqcValidator")
public class UqcValidator implements DocRulesValidator<OutwardTransDocument> {
	/**
	 * This class is responsible to validate UQC should be as per the defined
	 * masters attached. In case UQC is other than defined values, then
	 * information message will be diplayed. In case UQCis not as per defined
	 * masters then at the time of JSON creation for Saving GSTR 1, OTH-others
	 * will be reported by default.
	 * 
	 * 
	 * Bala krishna
	 * 
	 * BR_OUTWARD_73
	 */

	@Autowired
	@Qualifier("DefaultUomCache")
	private UomCache uomCache;

	@Qualifier("OnboardingQuestionValidationsUtil")
	private OnboardingQuestionValidationsUtil util;

	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {

		List<ProcessingResult> errors = new ArrayList<>();

		List<OutwardTransDocLineItem> items = document.getLineItems();
		String dataOriginTypeCode = document.getDataOriginTypeCode();
		
		IntStream.range(0, items.size()).forEach(idx -> {
			OutwardTransDocLineItem item = items.get(idx);
			
			 if(item.getUom()!=null){
	            	item.setUom(item.getUom().toUpperCase());
	            }

			String uom = item.getUom();
			String hsn = item.getHsnSac();
			
			if(hsn != null && !hsn.isEmpty()){
				 if(GSTConstants.NA.equalsIgnoreCase(uom) 
          			 && !hsn.startsWith("99")){
           		Set<String> errorLocations = new HashSet<>();
						errorLocations.add(GSTConstants.UQC);
						TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
								idx, errorLocations.toArray());
						errors.add(new ProcessingResult(APP_VALIDATION,
								ProcessingResultType.ERROR, "ER10052",
								"Invalid Unit Of Measurement", location));
           	} if(GSTConstants.NA.equalsIgnoreCase(item.getItemUqcUser()) 
         			 && !hsn.startsWith("99")){
          		Set<String> errorLocations = new HashSet<>();
						errorLocations.add(GSTConstants.UQC);
						TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
								idx, errorLocations.toArray());
						errors.add(new ProcessingResult(APP_VALIDATION,
								ProcessingResultType.ERROR, "ER10052",
								"Invalid Unit Of Measurement", location));
          	}
			}
			if (uom != null && !uom.isEmpty()) {
				uomCache = StaticContextHolder.getBean("DefaultUomCache",
						UomCache.class);
				int m = uomCache.finduom(trimAndConvToUpperCase(uom));
				int n = uomCache.finduomDesc(trimAndConvToUpperCase(uom));
				int o = uomCache.finduomMergeDesc(trimAndConvToUpperCase(uom));

				if (m == 1 || n == 1 || o == 1) {
					if (n == 1) {
						item.setUom(uomCache.uQcDescAndCodemap()
								.get(trimAndConvToUpperCase(uom)));
					}
					if (o == 1) {

						item.setUom(uomCache.uQcDesc()
								.get(trimAndConvToUpperCase(uom)));
					}
				} else {

					String paramkryId = CONFIG_PARAM_OUTWARD_QUE_KEY_ID.O19
							.name();
					util = StaticContextHolder.getBean(
							"OnboardingQuestionValidationsUtil",
							OnboardingQuestionValidationsUtil.class);
					Map<Long, List<EntityConfigPrmtEntity>> entityConfigParamMap = document
							.getEntityConfigParamMap();
					String paramtrvalue = util.valid(entityConfigParamMap,
							paramkryId, document.getEntityId());
					if (CONFIG_PARAM_OUTWARD_ANS_KEY_ID.B.name()
							.equalsIgnoreCase(paramtrvalue)) {
						item.setUom(GSTConstants.OTH);
                     if(invoicemanagementUqc(dataOriginTypeCode)){
                    	 if(GSTConstants.NA.equalsIgnoreCase(item.getItemUqcUser())){
                     		Set<String> errorLocations = new HashSet<>();
     						errorLocations.add(GSTConstants.UQC);
     						TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
     								idx, errorLocations.toArray());
     						errors.add(new ProcessingResult(APP_VALIDATION,
     								ProcessingResultType.INFO, "IN0507",
     								"Invalid Unit Of Measurement", location));
                     	}
						}else{
						Set<String> errorLocations = new HashSet<>();
						errorLocations.add(GSTConstants.UQC);
						TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
								idx, errorLocations.toArray());
						errors.add(new ProcessingResult(APP_VALIDATION,
								ProcessingResultType.INFO, "IN0507",
								"Invalid Unit Of Measurement", location));
						}
					} else {
						 if(invoicemanagementUqc(dataOriginTypeCode)){
	                    	if(GSTConstants.NA.equalsIgnoreCase(item.getItemUqcUser())){
	                     		Set<String> errorLocations = new HashSet<>();
	     						errorLocations.add(GSTConstants.UQC);
	     						TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
	     								idx, errorLocations.toArray());
	     						errors.add(new ProcessingResult(APP_VALIDATION,
	     								ProcessingResultType.INFO, "IN0507",
	     								"Invalid Unit Of Measurement", location));
	                     	}
							}else{
						Set<String> errorLocations = new HashSet<>();
						errorLocations.add(GSTConstants.UQC);
						TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
								idx, errorLocations.toArray());
						errors.add(new ProcessingResult(APP_VALIDATION,
								ProcessingResultType.ERROR, "ER10052",
								"Invalid Unit Of Measurement", location));
							}
					}
				}
			} else {
				String paramkryId = CONFIG_PARAM_OUTWARD_QUE_KEY_ID.O19.name();
				util = StaticContextHolder.getBean(
						"OnboardingQuestionValidationsUtil",
						OnboardingQuestionValidationsUtil.class);
				Map<Long, List<EntityConfigPrmtEntity>> entityConfigParamMap = document
						.getEntityConfigParamMap();
				String paramtrvalue = util.valid(entityConfigParamMap,
						paramkryId, document.getEntityId());
				if (CONFIG_PARAM_OUTWARD_ANS_KEY_ID.B.name()
						.equalsIgnoreCase(paramtrvalue)) {
					item.setUom(GSTConstants.OTH);

				} else {
					Set<String> errorLocations = new HashSet<>();
					errorLocations.add(GSTConstants.UQC);
					TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
							idx, errorLocations.toArray());
					errors.add(
							new ProcessingResult(APP_VALIDATION,
									ProcessingResultType.ERROR, "ER15211",
									"Unit Of Measurement is mandatory as per "
											+ "On-Boarding  parameter.",
									location));
				}
			}

		});
		return errors;
	}
private boolean invoicemanagementUqc(String origionType){
	if(Strings.isNullOrEmpty(origionType)) return false;
	if(origionType.toUpperCase().contains(GSTConstants.IM)) return true;
	return false;
	
}
public static void main(String[] args) {
	String hsn = "892043";
	
	String itemUqc ="NA";
	
	if(hsn != null && !hsn.isEmpty()){
		 if(GSTConstants.NA.equalsIgnoreCase(itemUqc) 
  			 && !hsn.startsWith("99")){
   		System.out.println("ER10052 : Invalid Unit Of Measurement");
   	}/*else{
   		System.out.println("Valid Unit Of Measurement");
	
   	}*/
	}
}
}
