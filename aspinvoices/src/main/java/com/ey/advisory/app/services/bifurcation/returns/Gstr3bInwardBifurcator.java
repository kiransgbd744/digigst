package com.ey.advisory.app.services.bifurcation.returns;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.ey.advisory.app.data.entities.client.BifurcationConstants;
import com.ey.advisory.app.data.entities.client.InwardTransDocument;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

/**
 * @author Arun KA
 *
 */
public class Gstr3bInwardBifurcator {
	
	String gstin = null;
	String taxPeriod = null;

	public void bifurcate() {
		List<InwardTransDocument> inwardList = new ArrayList<>();
		boolean bifurcate = false;

		for (InwardTransDocument inward : inwardList) {
			
			bifurcate = checkforTable3_1D(inward);
			if (bifurcate)
				continue;

			bifurcate = checkforTable4A1(inward);
			if (bifurcate)
				continue;

			bifurcate = checkforTable4A2(inward);
			if (bifurcate)
				continue;

			bifurcate = checkforTable4A3(inward);
			if (bifurcate)
				continue;

			bifurcate = checkforTable4A4(inward);
			if (bifurcate)
				continue;

			bifurcate = checkforTable4A5(inward);
			if (bifurcate)
				continue;
			
			bifurcate = checkforTable4B1(inward);
			if (bifurcate)
				continue;
			
			bifurcate = checkforTable4B2(inward);
			if (bifurcate)
				continue;

			bifurcate = checkforTable4D1(inward);
			if (bifurcate)
				continue;

			bifurcate = checkforTable4D2(inward);
			if (bifurcate)
				continue;

			bifurcate = checkforTable5A1(inward);
			if (bifurcate)
				continue;

			bifurcate = checkforTable5A2(inward);
			if (bifurcate)
				continue;

			bifurcate = checkforTable5B1(inward);
			if (bifurcate)
				continue;
			
			bifurcate = checkforTable5B2(inward);
			if (bifurcate)
				continue;

		}
	}

	private boolean checkforTable3_1D(InwardTransDocument inward) {
		
		String tableType = inward.getTableType();
		String revFlag = inward.getReverseCharge();
		boolean isBifurcate = false;
		
		if(Strings.isNullOrEmpty(tableType) || Strings.isNullOrEmpty(revFlag) 
				|| !revFlag.equalsIgnoreCase("Y"))
		return false;
		
		else if(tableType.equalsIgnoreCase(BifurcationConstants.SECTION_1) 
				|| tableType.equalsIgnoreCase(BifurcationConstants.SECTION_3)
				|| tableType.equalsIgnoreCase(BifurcationConstants.SECTION_10)
				|| tableType.equalsIgnoreCase(BifurcationConstants.SECTION_12)){
			//bifurcate 3.1D
			isBifurcate = true;
		}
		return isBifurcate;
	}

	private boolean checkforTable4A1(InwardTransDocument inward) {
		
		String tableType = inward.getTableType();
		String supplyType = inward.getSupplyType();
		boolean isBifurcate = false;
		String portCode = inward.getPortCode();
		String billOfEntry = inward.getBillOfEntryNo();
		LocalDate billOfEntryDate = inward.getBillOfEntryDate();
		boolean isBillOfEntryDetailsAvailabe = portCode != null
				&& billOfEntry != null && billOfEntryDate != null;
		
		if(Strings.isNullOrEmpty(tableType))
			return false;
		
		else if (tableType.equalsIgnoreCase(BifurcationConstants.SECTION_10) && 
				("IMPG".equalsIgnoreCase(supplyType) || 
						("SEZG".equalsIgnoreCase(supplyType) 
								&& isBillOfEntryDetailsAvailabe))){
			
			//bifurcate 4A1
			isBifurcate = true;
			
		}
		
		return isBifurcate;
	}

	private boolean checkforTable4A2(InwardTransDocument inward) {
		
		String tableType = inward.getTableType();
		String supplyType = inward.getSupplyType();
		boolean isBifurcate = false;
		
		if(Strings.isNullOrEmpty(tableType) || !"IMPS".equalsIgnoreCase(supplyType))
			return false;
		
		else if (tableType.equalsIgnoreCase(BifurcationConstants.SECTION_10)){
			
			//bifurcate 4A2
			isBifurcate = true;
		}
		return isBifurcate;
	}

	private boolean checkforTable4A3(InwardTransDocument inward) {
		
		String tableType = inward.getTableType();
		String revCharge = inward.getReverseCharge();
		boolean isBifurcate = false;
		
		if(Strings.isNullOrEmpty(tableType) || !"Y".equalsIgnoreCase(revCharge))
			return false;
		
		else if(tableType.equalsIgnoreCase(BifurcationConstants.SECTION_1) 
				|| tableType.equalsIgnoreCase(BifurcationConstants.SECTION_3)
				|| tableType.equalsIgnoreCase(BifurcationConstants.SECTION_12)){
			
			//bifurcate 4A3
			isBifurcate = true;
		}
		return isBifurcate;
		
	}

	private boolean checkforTable4A4(InwardTransDocument inward) {
		
		String tableType = inward.getTableType();
		boolean isBifurcate = false;
		
		if(Strings.isNullOrEmpty(tableType))
			return false;
		
		else if(tableType.equalsIgnoreCase(BifurcationConstants.SECTION_5)){
			
			//bifurcate 4A4
			isBifurcate = true;
		}
		return isBifurcate;
	}

	private boolean checkforTable4A5(InwardTransDocument inward) {
		
		String tableType = inward.getTableType();
		String revCharge = inward.getReverseCharge();
		boolean isBifurcate = false;
		
		if(Strings.isNullOrEmpty(tableType) || !"N".equalsIgnoreCase(revCharge))
			return false;
		
		else if(tableType.equalsIgnoreCase(BifurcationConstants.SECTION_1) 
				|| tableType.equalsIgnoreCase(BifurcationConstants.SECTION_3)){
			
			//bifurcate 4A5
			isBifurcate = true;
		}
		return isBifurcate;
		
	}

	private boolean checkforTable4B1(InwardTransDocument inward) {
		// TODO Auto-generated method stub
		return false;
	}

	private boolean checkforTable4B2(InwardTransDocument inward) {
		// TODO Auto-generated method stub
		return false;
	}

	private boolean checkforTable4D1(InwardTransDocument inward) {
		// TODO Auto-generated method stub
		return false;
	}

	private boolean checkforTable4D2(InwardTransDocument inward) {
		// TODO Auto-generated method stub
		return false;
	}

	private boolean checkforTable5A1(InwardTransDocument inward) {
		// TODO Auto-generated method stub
		return false;
	}

	private boolean checkforTable5A2(InwardTransDocument inward) {
		// TODO Auto-generated method stub
		return false;
	}

	private boolean checkforTable5B1(InwardTransDocument inward) {
		// TODO Auto-generated method stub
		return false;
	}

	private boolean checkforTable5B2(InwardTransDocument inward) {
		// TODO Auto-generated method stub
		return false;
	}

}
