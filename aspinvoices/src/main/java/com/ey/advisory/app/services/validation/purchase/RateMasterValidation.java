package com.ey.advisory.app.services.validation.purchase;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ey.advisory.admin.data.entities.client.EntityConfigPrmtEntity;
import com.ey.advisory.app.data.entities.client.InwardTransDocLineItem;
import com.ey.advisory.app.data.entities.client.InwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.app.services.validation.sales.ProductMasterRateAndHsnValUtil;
import com.ey.advisory.app.util.OnboardingQuestionValidationsUtil;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.ProcessingResultType;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;

/**
 * @author Siva.Nandam
 *
 */
public class RateMasterValidation
		implements DocRulesValidator<InwardTransDocument> {

	@Autowired
	@Qualifier("OnboardingQuestionValidationsUtil")
	private OnboardingQuestionValidationsUtil util;
	
	public static final BigDecimal TWO = new BigDecimal("2");

	@Override
	public List<ProcessingResult> validate(InwardTransDocument document,
			ProcessingContext context) {

		List<InwardTransDocLineItem> items = document.getLineItems();

		List<ProcessingResult> errors = new ArrayList<>();

		if (document.getCgstin() == null || document.getCgstin().isEmpty())
			return errors;

		String paramkryId = GSTConstants.I9;
		util = StaticContextHolder.getBean("OnboardingQuestionValidationsUtil",
				OnboardingQuestionValidationsUtil.class);
		Map<Long, List<EntityConfigPrmtEntity>> entityConfigParamMap = document
				.getEntityConfigParamMap();
		String paramtrvalue = util.valid(entityConfigParamMap, paramkryId,
				document.getEntityId());
		
		String paramkryId10 = GSTConstants.I10;
		String paramtrvalue10 = util.valid(entityConfigParamMap, paramkryId10,
				document.getEntityId());
		if (paramtrvalue == null || paramtrvalue.isEmpty())
			return errors;

		if (paramtrvalue.equalsIgnoreCase(GSTConstants.B) 
				) {
			IntStream.range(0, items.size()).forEach(idx -> {
				InwardTransDocLineItem item = items.get(idx);
				BigDecimal igstRate = item.getIgstRate();
				BigDecimal cgstRate = item.getCgstRate();
				BigDecimal sgstRate = item.getSgstRate();
				if (igstRate == null) {
					igstRate = BigDecimal.ZERO;
				}

				if (cgstRate == null) {
					cgstRate = BigDecimal.ZERO;
				}

				if (sgstRate == null) {
					sgstRate = BigDecimal.ZERO;
				}
				BigDecimal cgstRatemul = cgstRate.multiply(TWO);
				BigDecimal sgstRatemul = sgstRate.multiply(TWO);
				

				Map<String, List<Pair<Integer, BigDecimal>>> 
                masterItemMap = document.getMasterItemMap();
				if(masterItemMap==null){
					if (paramtrvalue10 != null && paramtrvalue10
							.equalsIgnoreCase(GSTConstants.A)) {
							if(igstRate.compareTo(BigDecimal.ZERO) > 0){
						Set<String> errorLocations = new HashSet<>();
						errorLocations.add(GSTConstants.IGST_RATE);
						
						TransDocProcessingResultLoc location 
						= new TransDocProcessingResultLoc(
								idx, errorLocations.toArray());
						errors.add(new ProcessingResult(APP_VALIDATION,
								"ER1134",
								"IGST Rate not as per Product "
										+ "Master provided at the time "
										+ "of On Boarding",
								location));
					}
							if(sgstRate.compareTo(BigDecimal.ZERO) > 0){
							Set<String> errorLocations = new HashSet<>();
							errorLocations.add(GSTConstants.SGST_RATE);
							
							TransDocProcessingResultLoc location 
							= new TransDocProcessingResultLoc(
									idx, errorLocations.toArray());
							errors.add(new ProcessingResult(APP_VALIDATION,
									"ER1136",
									"SGST Rate not as per Product "
											+ "Master provided at the time "
											+ "of On Boarding",
									location));
						}
							if(cgstRate.compareTo(BigDecimal.ZERO) > 0){
							Set<String> errorLocations = new HashSet<>();
							errorLocations.add(GSTConstants.CGST_RATE);
							
							TransDocProcessingResultLoc location 
							= new TransDocProcessingResultLoc(
									idx, errorLocations.toArray());
							errors.add(new ProcessingResult(APP_VALIDATION,
									"ER1135",
									"CGST Rate not as per Product "
											+ "Master provided at the time "
											+ "of On Boarding",
									location));
						}
					}
					else if (paramtrvalue10 != null && paramtrvalue10
							.equalsIgnoreCase(GSTConstants.B)) {
						if(igstRate.compareTo(BigDecimal.ZERO) > 0){
							Set<String> errorLocations = new HashSet<>();
							errorLocations.add(GSTConstants.IGST_RATE);
							
							TransDocProcessingResultLoc location 
							= new TransDocProcessingResultLoc(
									idx, errorLocations.toArray());
							errors.add(new ProcessingResult(APP_VALIDATION,
									ProcessingResultType.INFO,"IN1016",
									"IGST Rate not as per Product "
											+ "Master provided at the time "
											+ "of On Boarding",
									location));
					}
						if(cgstRate.compareTo(BigDecimal.ZERO) > 0){
							Set<String> errorLocations = new HashSet<>();
							errorLocations.add(GSTConstants.CGST_RATE);
							
							TransDocProcessingResultLoc location 
							= new TransDocProcessingResultLoc(
									idx, errorLocations.toArray());
							errors.add(new ProcessingResult(APP_VALIDATION,
									ProcessingResultType.INFO,"IN1017",
									"CGST Rate not as per Product "
											+ "Master provided at the time "
											+ "of On Boarding",
									location));
					}
						if(sgstRate.compareTo(BigDecimal.ZERO) > 0){
							Set<String> errorLocations = new HashSet<>();
							errorLocations.add(GSTConstants.SGST_RATE);
							
							TransDocProcessingResultLoc location 
							= new TransDocProcessingResultLoc(
									idx, errorLocations.toArray());
							errors.add(new ProcessingResult(APP_VALIDATION,
									ProcessingResultType.INFO,"IN1018",
									"sGST Rate not as per Product "
											+ "Master provided at the time "
											+ "of On Boarding",
									location));
					}
					}
				}
				if(masterItemMap!=null){
				Boolean igstvalid=	ProductMasterRateAndHsnValUtil
				          .isValidRate(masterItemMap,igstRate,document.getSgstin());
				Boolean sgstvalid=	ProductMasterRateAndHsnValUtil
				          .isValidRate(masterItemMap,sgstRatemul,document.getSgstin());
				Boolean cgstvalid=	ProductMasterRateAndHsnValUtil
				          .isValidRate(masterItemMap,cgstRatemul,document.getSgstin());
				
					if (paramtrvalue10 != null && paramtrvalue10
							.equalsIgnoreCase(GSTConstants.A)) {
						
						if (igstRate.compareTo(BigDecimal.ZERO) > 0) {
						if (!igstvalid) {
							Set<String> errorLocations = new HashSet<>();
							errorLocations.add(GSTConstants.IGST_RATE);

							TransDocProcessingResultLoc location 
							= new TransDocProcessingResultLoc(
									idx, errorLocations.toArray());
							errors.add(new ProcessingResult(APP_VALIDATION,
									"ER1134",
									"IGST Rate not as per Product "
											+ "Master provided at the time "
											+ "of On Boarding",
									location));
						}
					}
						if (cgstRate.compareTo(BigDecimal.ZERO) > 0) {
							if (!cgstvalid) {
								Set<String> errorLocations = new HashSet<>();

								errorLocations.add(GSTConstants.CGST_RATE);

								TransDocProcessingResultLoc location 
								     = new TransDocProcessingResultLoc(
										idx, errorLocations.toArray());
								errors.add(new ProcessingResult(APP_VALIDATION,
										"ER1135",
										"CGST Rate not as per Product "
												+ "Master provided at the "
												+ "time of On Boarding",
										location));
							}
						}
						if (sgstRate.compareTo(BigDecimal.ZERO) > 0) {
							if (!sgstvalid) {
								Set<String> errorLocations = new HashSet<>();
								errorLocations.add(GSTConstants.SGST_RATE);
								TransDocProcessingResultLoc location 
								  = new TransDocProcessingResultLoc(
										idx, errorLocations.toArray());
								errors.add(new ProcessingResult(APP_VALIDATION,
										"ER1136",
										"SGST Rate not as per Product "
												+ "Master provided at the "
												+ "time of On Boarding",
										location));
							}
						}
					}
					else if (paramtrvalue10 != null && paramtrvalue10
							.equalsIgnoreCase(GSTConstants.B)) {
						if (igstRate.compareTo(BigDecimal.ZERO) > 0) {
							if (!igstvalid) {
								Set<String> errorLocations = new HashSet<>();
								errorLocations.add(GSTConstants.IGST_RATE);

								TransDocProcessingResultLoc location 
								       = new TransDocProcessingResultLoc(
										idx, errorLocations.toArray());
								errors.add(new ProcessingResult(APP_VALIDATION,
										ProcessingResultType.INFO, "IN1016",
										"IGST Rate not as per Product "
												+ "Master provided at the time "
												+ "of On Boarding",
										location));
							}
						}
						if (cgstRate.compareTo(BigDecimal.ZERO) > 0) {
							if (!cgstvalid && BigDecimal.ZERO
									.compareTo(cgstRate) > 0) {
								Set<String> errorLocations = new HashSet<>();

								errorLocations.add(GSTConstants.CGST_RATE);

								TransDocProcessingResultLoc location 
								 = new TransDocProcessingResultLoc(
										idx, errorLocations.toArray());
								errors.add(new ProcessingResult(APP_VALIDATION,
										ProcessingResultType.INFO, "IN1017",
										"CGST Rate not as per Product "
												+ "Master provided at the time "
												+ "of On Boarding",
										location));
							}
						}
						if (sgstRate.compareTo(BigDecimal.ZERO) > 0) {
							if (!sgstvalid) {
								Set<String> errorLocations = new HashSet<>();
								errorLocations.add(GSTConstants.SGST_RATE);
								TransDocProcessingResultLoc location 
								     = new TransDocProcessingResultLoc(
										idx, errorLocations.toArray());
								errors.add(new ProcessingResult(APP_VALIDATION,
										ProcessingResultType.INFO, "IN1018",
										"SGST Rate not as per Product Master"
												+ " provided at the time of On Boarding",
										location));
							}
						}
					}
				
				}
			});
		}

		return errors;

	}

	
}
