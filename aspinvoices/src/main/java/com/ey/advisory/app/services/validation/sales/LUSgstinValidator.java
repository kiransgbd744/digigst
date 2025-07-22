package com.ey.advisory.app.services.validation.sales;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.app.caches.StateCache;
import com.ey.advisory.app.caches.ehcache.Ehcachegstin;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

/**
 * 
 * @author Siva.Nandam
 *
 */

@Component("LUSgstinValidator")
public class LUSgstinValidator
		implements DocRulesValidator<OutwardTransDocument> {

	@Autowired
	@Qualifier("Ehcachegstin")
	private Ehcachegstin ehcachegstin;

	@Autowired
	@Qualifier("DefaultStateCache")
	private StateCache stateCache;
	
	private static final List<String> REGYPE_IMPORTS = ImmutableList
			.of(GSTConstants.SEZD, GSTConstants.SEZU, GSTConstants.REGULAR);
	
	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {
		String groupCode = TenantContext.getTenantId();
		List<ProcessingResult> errors = new ArrayList<>();
		
		if (GSTConstants.I.equalsIgnoreCase(document.getTransactionType())) {
			String sgstin=document.getSgstin();
			if (GSTConstants.URP.equalsIgnoreCase(sgstin))
				return errors;
			if (!Strings.isNullOrEmpty(sgstin)) {
				
				String regex = "^[0-9][0-9][A-Za-z][A-Za-z][A-Za-z]"
						+ "[A-Za-z][A-Za-z][0-9][0-9][0-9][0-9][A-Za-z][A-Za-z0-9]"
						+ "[A-Za-z0-9][A-Za-z0-9]$";
				
				String regex1 = "^[0-9][0-9][A-Za-z][A-Za-z][A-Za-z]"
						+ "[A-Za-z][0-9][0-9][0-9][0-9][0-9][A-Za-z][A-Za-z0-9]"
						+ "[A-Za-z0-9][A-Za-z0-9]$";
					Pattern pattern = Pattern.compile(regex);
				
					Pattern pattern1 = Pattern.compile(regex1);
					
					  Matcher matcher = pattern.matcher(sgstin.trim());
					  Matcher matcher1 = pattern1.matcher(sgstin.trim());
				if (matcher.matches() || matcher1.matches()) {
					
				}else{
					Set<String> errorLocations = new HashSet<>();
					errorLocations.add(GSTConstants.SGSTIN);
					TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
							null, errorLocations.toArray());
					
					errors.add(new ProcessingResult(APP_VALIDATION, "ER0024",
							"Invalid Supplier GSTIN.",
							location));
					return errors;
				}
				stateCache = StaticContextHolder.getBean("DefaultStateCache",
						StateCache.class);

				String statecode = document.getSgstin().substring(0, 2);
				int n = stateCache.findStateCode(statecode);
				if (n <= 0) {
					Set<String> errorLocations = new HashSet<>();
					errorLocations.add(GSTConstants.SGSTIN);
					TransDocProcessingResultLoc location 
					             = new TransDocProcessingResultLoc(
							null, errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION, "ER0024",
							"Invalid Supplier GSTIN.", location));
					return errors;
				}
			}
			

		} else {
			 if(Strings.isNullOrEmpty(document.getSgstin())){
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.SGSTIN);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER0025",
						"Supplier GSTIN cannot be left balnk.", location));
			}
				if (GSTConstants.URP.equalsIgnoreCase(document.getSgstin())){
					Set<String> errorLocations = new HashSet<>();
					errorLocations.add(GSTConstants.SGSTIN);
					TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
							null, errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION, "ER0026",
							"Supplier GSTIN is not as per On-Boarding data",
							location));
					return errors;
				}
				ehcachegstin = StaticContextHolder.getBean("Ehcachegstin",
						Ehcachegstin.class);

				GSTNDetailEntity gstin = ehcachegstin.getGstinInfo(groupCode,
						document.getSgstin());
				if (gstin == null) {

					Set<String> errorLocations = new HashSet<>();
					errorLocations.add(GSTConstants.SGSTIN);
					TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
							null, errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION, "ER0026",
							"Supplier GSTIN is not as per On-Boarding data",
							location));
					return errors;
				}
				if (!REGYPE_IMPORTS.contains(gstin.getRegistrationType())) {
					Set<String> errorLocations = new HashSet<>();
					errorLocations.add(GSTConstants.SGSTIN);
					TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
							null, errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION, "ER15031",
							"Registration Type of Supplier GSTIN is other "
							+ "than Regular / SEZU / SEZD",
							location));
				}
			
		}
		return errors;
	}
}
