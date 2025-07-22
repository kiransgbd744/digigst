/**
* 
 */
package com.ey.advisory.app.services.validation.eInvoice;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GenUtil.trimAndConvToUpperCase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.caches.SubSupplyTypeCache;
import com.ey.advisory.app.caches.ehcache.Ehcachegstin;
import com.ey.advisory.app.data.entities.client.OutwardTransDocLineItem;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.google.common.collect.ImmutableList;

/**
 * @author Laxmi.Salukuti
 *
 */
@Component("EwbStatusValidation")
public class EwbStatusValidation {

	@Autowired
	@Qualifier("DefaultSubSupplyTypeCache")
	private SubSupplyTypeCache stateCache;
	
	@Autowired
	@Qualifier("Ehcachegstin")
	private Ehcachegstin ehcachegstin;
	
	private EwbStatusValidation() {

	}

	private static final List<String> EWB_DOCTYPE = ImmutableList
			.of(GSTConstants.INV, GSTConstants.BOS);

	
	private static final List<String> EWB1_DOCTYPE = ImmutableList
			.of(GSTConstants.INV, GSTConstants.BOS, GSTConstants.DLC);

	private static final List<String> EWB2_DOCTYPE = ImmutableList
			.of(GSTConstants.DLC, GSTConstants.OTH);

	private static final List<String> EWB3_DOCTYPE = ImmutableList.of(
			GSTConstants.INV, GSTConstants.BOS, GSTConstants.DLC,
			GSTConstants.BOE);
	private static final List<String> SUB_SUPPLY_TYPE = ImmutableList
			.of(GSTConstants.TAX, GSTConstants.EXP);
	private static final List<String> SUB_SUPPLY_TYPE1 = ImmutableList
			.of(GSTConstants.UNK, GSTConstants.OTH);
	private static final List<String> SUB_SUPPLY_TYPE2 = ImmutableList
			.of(GSTConstants.FOU, GSTConstants.EXB, GSTConstants.LNS);
	private static final List<String> SUB_SUPPLY_TYPE3 = ImmutableList.of(
			GSTConstants.JWR, GSTConstants.SR, GSTConstants.EXB,
			GSTConstants.FOU);

	public static boolean validate(OutwardTransDocument document) {
		if (document.getTransactionType() == null
				|| document.getTransactionType().isEmpty())
			return false;
		if (document.getSubSupplyType() == null
				|| document.getSubSupplyType().isEmpty())
			return false;
		if (document.getDocType() == null || document.getDocType().isEmpty())
			return false;

	/*	List<String> hsnList = new ArrayList<>();
		List<OutwardTransDocLineItem> items = document.getLineItems();
		for (OutwardTransDocLineItem item : items) {
			if (item.getHsnSac() != null && !item.getHsnSac().isEmpty()
					&& item.getHsnSac().length() > 1) {
				hsnList.add(item.getHsnSac().substring(0, 2));
			}
		}
			*///if (!hsnList.contains(GSTConstants.SERVICES_CODE)) {
				if (document.getTransactionType()
						.equalsIgnoreCase(GSTConstants.O)) {
					if ((EWB_DOCTYPE.contains(
							trimAndConvToUpperCase(document.getDocType()))
							&& SUB_SUPPLY_TYPE
									.contains(document.getSubSupplyType()))
							|| (GSTConstants.DLC
									.equalsIgnoreCase(document.getDocType())
									&& GSTConstants.JWK.equalsIgnoreCase(
											document.getSubSupplyType()))
							|| (EWB1_DOCTYPE
									.contains(trimAndConvToUpperCase(
											document.getDocType()))
									&& GSTConstants.SKD.equalsIgnoreCase(
											document.getSubSupplyType()))
							|| (EWB2_DOCTYPE
									.contains(trimAndConvToUpperCase(
											document.getDocType()))
									&& SUB_SUPPLY_TYPE1
											.contains(trimAndConvToUpperCase(
													document.getSubSupplyType())))
							|| (GSTConstants.DLC
									.equalsIgnoreCase(document.getDocType())
									&& SUB_SUPPLY_TYPE2.contains(
											trimAndConvToUpperCase(document
													.getSubSupplyType())))) {
						return true;
					}
				}
				if (document.getTransactionType()
						.equalsIgnoreCase(GSTConstants.I)) {
					if ((EWB_DOCTYPE.contains(
							trimAndConvToUpperCase(document.getDocType()))
							&& GSTConstants.TAX.equalsIgnoreCase(
									document.getSubSupplyType()))
							|| (GSTConstants.BOE
									.equalsIgnoreCase(document.getDocType())
									&& GSTConstants.IMP.equalsIgnoreCase(
											document.getSubSupplyType()))
							|| (EWB3_DOCTYPE
									.contains(trimAndConvToUpperCase(
											document.getDocType()))
									&& GSTConstants.SKD.equalsIgnoreCase(
											document.getSubSupplyType()))
							|| (GSTConstants.DLC
									.equalsIgnoreCase(document.getDocType())
									&& SUB_SUPPLY_TYPE3
											.contains(trimAndConvToUpperCase(
													document.getSubSupplyType())))
							|| (EWB2_DOCTYPE
									.contains(trimAndConvToUpperCase(
											document.getDocType()))
									&& GSTConstants.OTH.equalsIgnoreCase(
											document.getSubSupplyType()))) {
						return true;

					}
				}
			//}
		
		return false;
	}
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();

		if (document.getSubSupplyType() != null
				&& !document.getSubSupplyType().isEmpty()) {
			String subSupplyType = document.getSubSupplyType();

			stateCache = StaticContextHolder.getBean(
					"DefaultSubSupplyTypeCache", SubSupplyTypeCache.class);
			int n = stateCache.findSubSupplyType(trimAndConvToUpperCase(subSupplyType));

			if (n <= 0) {

				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.SUB_SUPPLY_TYPE);
				TransDocProcessingResultLoc location 
				           = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER10119",
						"Invalid Sub Supply Type", location));
			}
		}
		return errors;
	}
	
	public static boolean HsnEwbcheck(OutwardTransDocument document) {
		
		//List<String> hsnList = new ArrayList<>();
		List<OutwardTransDocLineItem> items = document.getLineItems();
		for (OutwardTransDocLineItem item : items) {
			if (item.getHsnSac() != null && !item.getHsnSac().isEmpty()
					&& item.getHsnSac().length() > 1) {
				//hsnList.add(item.getHsnSac().substring(0, 2));
				if(!GSTConstants.SERVICES_CODE.equalsIgnoreCase(item.getHsnSac().substring(0, 2))){
					return true;
				}
			}
		}
			
		
		return false;
	}
	
	
	
	

}
