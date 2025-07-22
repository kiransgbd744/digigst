package com.ey.advisory.app.services.bifurcation.returns;

import java.util.ArrayList;
import java.util.List;

import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.common.GSTConstants;
import com.google.common.collect.ImmutableList;

import com.google.common.base.Strings;

/**
 * @author Arun KA
 *
 */
public class Gstr3bOutwardBifurcator {

	String gstin = null;
	String taxPeriod = null;

	public void bifurcate() {
		List<OutwardTransDocument> outwardList = new ArrayList<>();
		boolean bifurcate = false;

		for (OutwardTransDocument outward : outwardList) {
			
			bifurcate = checkforTable3_2_2(outward);
			if (bifurcate)
				continue;

			bifurcate = checkforTable3_2_3(outward);
			if (bifurcate)
				continue;

			bifurcate = checkforTable3_1A(outward);
			if (bifurcate)
				continue;

			bifurcate = checkforTable3_1B(outward);
			if (bifurcate)
				continue;

			bifurcate = checkforTable3_1C(outward);
			if (bifurcate)
				continue;

			bifurcate = checkforTable3_1E(outward);
			if (bifurcate)
				continue;
			
			bifurcate = checkforTable3_2_1(outward);
			if (bifurcate)
				continue;

		}
	}

	private boolean checkforTable3_1A(OutwardTransDocument outward) {

		String tableType = outward.getTableType();

		final List<String> supplyTypeList = ImmutableList.of("SEZWP", "SEZWOP",
				"EXPT", "EXPWT");

		String supplyType = outward.getSupplyType();
		boolean isBifurcate = false;

		if (Strings.isNullOrEmpty(tableType))
			return false;

		if (tableType.equalsIgnoreCase(GSTConstants.GSTR1_4A)
				|| tableType.equalsIgnoreCase(GSTConstants.GSTR1_4B)
				|| tableType.equalsIgnoreCase(GSTConstants.GSTR1_4C)
				|| tableType.equalsIgnoreCase(GSTConstants.GSTR1_5A)
				|| tableType.equalsIgnoreCase(GSTConstants.GSTR1_5B)
				|| tableType.equalsIgnoreCase(GSTConstants.GSTR1_6C)
				|| tableType.equalsIgnoreCase(GSTConstants.GSTR1_7A1)
				|| tableType.equalsIgnoreCase(GSTConstants.GSTR1_7B1)
				|| (tableType.equalsIgnoreCase(GSTConstants.GSTR1_9B)
						&& !supplyTypeList.contains(supplyType))) {

			// bifurcate to 3.1a
			isBifurcate = true;
		}

		return isBifurcate;

	}

	private boolean checkforTable3_1B(OutwardTransDocument outward) {
		
		String tableType = outward.getTableType();
		String supplyType = outward.getSupplyType();
		boolean isBifurcate = false;
		
		final List<String> supplyTypeList = ImmutableList
				.of("SEZWP", "SEZWOP", "EXPT", "EXPWT");
		
		if (Strings.isNullOrEmpty(tableType))
			return false;
		
		if (tableType.equalsIgnoreCase(GSTConstants.GSTR1_6A)
				|| tableType.equalsIgnoreCase(GSTConstants.GSTR1_6B)
				|| (tableType.equalsIgnoreCase(GSTConstants.GSTR1_9B) 
						&& supplyTypeList.contains(supplyType))){
			
			//bifurcate to 3.1b
			isBifurcate = true;
			
		}
		return isBifurcate;
	}

	private boolean checkforTable3_1C(OutwardTransDocument outward) {
		
		String tableType = outward.getTableType();
		String supplyType = outward.getSupplyType();
		boolean isBifurcate = false;
		
		final List<String> supplyTypeList = ImmutableList
				.of("NIL", "EXT");
		
		if (Strings.isNullOrEmpty(tableType) || !supplyTypeList.contains(supplyType))
			return false;
		
		else if (tableType.equalsIgnoreCase(GSTConstants.GSTR1_8A)
				|| tableType.equalsIgnoreCase(GSTConstants.GSTR1_8B)
				|| tableType.equalsIgnoreCase(GSTConstants.GSTR1_8C)
				|| tableType.equalsIgnoreCase(GSTConstants.GSTR1_8D)){
			
			//bifurcate to 3.1c
			isBifurcate = true;
		}
		return isBifurcate;
	}

	private boolean checkforTable3_1E(OutwardTransDocument outward) {
		
		String tableType = outward.getTableType();
		String supplyType = outward.getSupplyType();
		boolean isBifurcate = false;
		
		final List<String> supplyTypeList = ImmutableList
				.of("NON", "SCH3");
		
		if (Strings.isNullOrEmpty(tableType) || !supplyTypeList.contains(supplyType))
			return false;
		
		else if (tableType.equalsIgnoreCase(GSTConstants.GSTR1_8A)
				|| tableType.equalsIgnoreCase(GSTConstants.GSTR1_8B)
				|| tableType.equalsIgnoreCase(GSTConstants.GSTR1_8C)
				|| tableType.equalsIgnoreCase(GSTConstants.GSTR1_8D)){
			
			//bifurcate to 3.1e
			isBifurcate = true;
		}
		return isBifurcate;
	}
	
	private boolean checkforTable3_2_1(OutwardTransDocument outward) {
		
		String tableType = outward.getTableType();
		String supplyType = outward.getSupplyType();
		String section = outward.getGstnBifurcation();
		boolean isBifurcate = false;
		
		final List<String> supplyTypeList = ImmutableList
				.of("EXPT", "EXPWT");
		
		if (Strings.isNullOrEmpty(tableType))
			return false;
		
		else if (tableType.equalsIgnoreCase(GSTConstants.GSTR1_5A)
				|| tableType.equalsIgnoreCase(GSTConstants.GSTR1_5B)
				|| tableType.equalsIgnoreCase(GSTConstants.GSTR1_7B1)
				|| (tableType.equalsIgnoreCase(GSTConstants.GSTR1_9B) 
						&& "CDNUR".equalsIgnoreCase(section) 
						&& !supplyTypeList.contains(supplyType))){
			
			//bifurcate to 3.2(1)
			isBifurcate = true;
		}
		return isBifurcate;
	}


	private boolean checkforTable3_2_2(OutwardTransDocument outward) {
		
		String tableType = outward.getTableType();
		String sgstin = outward.getSgstin();
		String pos = outward.getPos();
		String cgstin = outward.getCgstin();
		String custType = outward.getCustOrSuppType();
		boolean isBifurcate = false;
		
		if(Strings.isNullOrEmpty(sgstin) || Strings.isNullOrEmpty(pos)
				|| Strings.isNullOrEmpty(custType) || Strings.isNullOrEmpty(tableType)
				|| Strings.isNullOrEmpty(cgstin)){
			return false;
		}
		
		boolean isSame = sgstin.substring(0, 2).equalsIgnoreCase(pos);
				
		if (isSame || !custType.equalsIgnoreCase("C"))
			return false;
		
		if (tableType.equalsIgnoreCase(GSTConstants.GSTR1_4A)
				|| tableType.equalsIgnoreCase(GSTConstants.GSTR1_4B)
				|| tableType.equalsIgnoreCase(GSTConstants.GSTR1_4C)
				|| tableType.equalsIgnoreCase(GSTConstants.GSTR1_9B)){
			
			//bifurcat 3.2(2)
			isBifurcate = true;
		}
		
		return isBifurcate;
		
	}

	private boolean checkforTable3_2_3(OutwardTransDocument outward) {
		
		String tableType = outward.getTableType();
		String sgstin = outward.getSgstin();
		String pos = outward.getPos();
		String cgstin = outward.getCgstin();
		String custType = outward.getCustOrSuppType();
		boolean isBifurcate = false;
		
		if(Strings.isNullOrEmpty(sgstin) || Strings.isNullOrEmpty(pos)
				|| Strings.isNullOrEmpty(custType) || Strings.isNullOrEmpty(tableType)
				|| Strings.isNullOrEmpty(cgstin)){
			return false;
		}
		
		boolean isSame = sgstin.substring(0, 2).equalsIgnoreCase(pos);
				
		if (isSame || !custType.equalsIgnoreCase("U"))
			return false;
		
		if (tableType.equalsIgnoreCase(GSTConstants.GSTR1_4A)
				|| tableType.equalsIgnoreCase(GSTConstants.GSTR1_4B)
				|| tableType.equalsIgnoreCase(GSTConstants.GSTR1_4C)
				|| tableType.equalsIgnoreCase(GSTConstants.GSTR1_9B)){
			
			//bifurcat 3.2(3)
			isBifurcate = true;
		}
		
		return isBifurcate;
	}

}
