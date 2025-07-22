package com.ey.advisory.app.services.strcutvalidation.purchase;

import static com.ey.advisory.common.GSTConstants.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.javatuples.Pair;
import org.javatuples.Quartet;
import org.javatuples.Triplet;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

@Component("InwardDocOtherStructuralValidations")
public class InwardDocOtherStructuralValidations {

	
	private static final List<Quartet<Integer, String, String,String>> LIST = new ImmutableList.Builder()
			.add(new Quartet<Integer, String, String,String>(8,"ER130","Single document cannot have multiple return period.",RETURN_PREIOD))
			.add(new Quartet<Integer, String, String,String>(13,"E131","Single document cannot have multiple docment dates.",DOC_DATE))
			.add(new Quartet<Integer, String, String,String>(14,"ER139","Single document cannot have multiple Original document number.",ORIGINAL_DOC_NO))
			.add(new Quartet<Integer, String, String,String>(15,"ER140","Single document cannot have multiple Original document Date.",ORIGINAL_DOC_DATE))
			.add(new Quartet<Integer, String, String,String>(16,"ER132","Single document cannot have multiple flags.",PRE_GST))
			.add(new Quartet<Integer, String, String,String>(19,"ER133","Single document cannot have Multiple  OrgSgstin.",OrgSgstin))
			.add(new Quartet<Integer, String, String,String>(22,"ER062","Single document cannot have multiple pos.",POS))
			.add(new Quartet<Integer, String, String,String>(23,"ER134","Single document cannot have multiple portCodes.",PORT_CODE))
			.add(new Quartet<Integer, String, String,String>(46,"ERXXX","Single document cannot have multiple ReverseChargeFlags.",ReverseCharge))
			
			.build();
	
	
	
	private static final List<String> ORGDOCNUM_REQUIRING_IMPORTS = 
			ImmutableList.of(DLC,RDLC);
	private static final List<String> ORGDOCNUM_REQUIRING_IMPORTS1 = 
			ImmutableList.of(RNV,CR,DR,RCR,RDR,RFV);
	private static final List<String> ORGDOCNUM_REQUIRING_IMPORTS2 = 
			ImmutableList.of(CR,DR,RCR,RDR);

	public List<ProcessingResult> validate(List<Object[]> rows) {
		
		List<ProcessingResult> results = new ArrayList<>();
		
		for (Quartet<Integer, String, String,String> quartet : LIST) {
			ProcessingResult result = validateColForEqValues1(rows, quartet);
			if (result != null) {
				results.add(result);
			}
		}
		return results;
	}
	
	private ProcessingResult validateColForEqValues1(
			List<Object[]> listobj,  Quartet<Integer, 
			String, String,String> triplet){
	
	
	List<String> errorLocations = new ArrayList<>();
	Object prev = null;
	boolean isFirst = true;
	
	int colNo = triplet.getValue0();
	
	for (Object[] objarr : listobj) {
		Object curVal = objarr[colNo];	
		
		Object doctype = objarr[10];
		String supplyType = objarr[11].toString();
		if(doctype!=null && supplyType!=null){
		if((colNo==8 && !ORGDOCNUM_REQUIRING_IMPORTS.contains(doctype)) 
		|| (colNo==13 && !ORGDOCNUM_REQUIRING_IMPORTS.contains(doctype))
		|| (colNo==14 && ORGDOCNUM_REQUIRING_IMPORTS1.contains(doctype))
		||(colNo==15 && ORGDOCNUM_REQUIRING_IMPORTS1.contains(doctype))
		||(colNo==16 && ORGDOCNUM_REQUIRING_IMPORTS2.contains(doctype))
		||(colNo==19 && ORGDOCNUM_REQUIRING_IMPORTS1.contains(doctype))
		||(colNo==23 && IMPG.equalsIgnoreCase(supplyType))
		|| (colNo==22) ||(colNo==46)
		){
		if (isFirst) {
			prev = curVal;
			isFirst = false;
		} else {
			if(curVal==null){
				if(prev!=null){
					errorLocations.add(triplet.getValue3());
					TransDocProcessingResultLoc location =
							new TransDocProcessingResultLoc(null, 
									errorLocations.toArray());
					return new ProcessingResult(APP_VALIDATION,
							triplet.getValue1(),
							triplet.getValue2(),
							location);	
				}
			}
			if(curVal!=null){
			if (!curVal.equals(prev)){
				errorLocations.add(triplet.getValue3());
				TransDocProcessingResultLoc location =
						new TransDocProcessingResultLoc(null, 
								errorLocations.toArray());
				return new ProcessingResult(APP_VALIDATION,
						triplet.getValue1(),
						triplet.getValue2(),
						location);	
			}
			}
		}
		}
		}
	};
	
	
	return null;		
}
}

