package com.ey.advisory.app.services.validation.sales;

import com.ey.advisory.app.data.entities.client.OutwardTransDocument;

public class LookUpUtil {

	public static boolean checkIfBothDocsHaveSameCgstin(
			OutwardTransDocument doc1, OutwardTransDocument doc2) {
		
		String cgstin1 = (doc1.getCgstin() != null) ? doc1.getCgstin() : "";
		String cgstin2 = (doc2.getCgstin() != null) ? doc2.getCgstin() : "";
		
		return cgstin1.equals(cgstin2);
	}

	
	public static boolean checkIfBothDocsHaveSameInterIntra(
			OutwardTransDocument doc1, OutwardTransDocument doc2) {
		
		String pos1 = (doc1.getPos() != null) ? doc1.getPos().trim() : "";
		String pos2 = (doc2.getPos() != null) ? doc2.getPos().trim() : "";
		
		String sgstin1st2Chars1 = (doc1.getSgstin() != null) ?
				doc1.getSgstin().trim().substring(0, 2) : "";
				
		String sgstin1st2Chars2 = (doc2.getSgstin() != null) ?
				doc2.getSgstin().trim().substring(0, 2) : "";

		boolean isIntra1 = pos1.equals(sgstin1st2Chars1);
		boolean isIntra2 = pos2.equals(sgstin1st2Chars2);
		
		return (isIntra1 == isIntra2);

	}
	
	public static boolean checkIfBothDocsHaveSameRcmFlag(
			OutwardTransDocument doc1, OutwardTransDocument doc2) {
		
String pos1 = (doc1.getReverseCharge() != null) ? doc1.getPos().trim() : "N";
String pos2 = (doc2.getReverseCharge() != null) ? doc2.getPos().trim() : "N";
		
		return pos1.equals(pos2);

	}
}
