/**
 * 
 */
package com.ey.advisory.app.services.doc.gstr1a;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.BifurcationConstants;
import com.ey.advisory.app.data.entities.client.OutwardTransDocLineItem;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AOutwardTransDocLineItem;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AOutwardTransDocument;
import com.ey.advisory.common.GSTConstants;

/**
 * This class is responsible for Calculating Memo Values for IGST, CGST, SGST
 * and CESS
 * 
 * @author Shashikant.Shukla
 *
 */
@Component("Gstr1AEInvoiceOutwardDocMemoValCalculation")
public class Gstr1AEInvoiceOutwardDocMemoValCalculation {

	/**
	 * 
	 * @param document
	 * @param item
	 */
	public void calculateMemoValues(Gstr1AOutwardTransDocument document,
			Gstr1AOutwardTransDocLineItem item) {
		BigDecimal docRet1IgstImpct = document.getMemoValIgst();
		BigDecimal docRet1CgstImpct = document.getMemoValCgst();
		BigDecimal docRet1SgstImpct = document.getMemoValSgst();
		BigDecimal itmRet1IgstImpct = BigDecimal.ZERO;
		BigDecimal itmRet1CgstImpct = BigDecimal.ZERO;
		BigDecimal itmRet1SgstImpct = BigDecimal.ZERO;
		BigDecimal itmIgstMemoValRounded = BigDecimal.ZERO;
		BigDecimal itmSgstMemoValRounded = BigDecimal.ZERO;
		BigDecimal itmCgstMemoValRounded = BigDecimal.ZERO;
		if (docRet1IgstImpct == null) {
			docRet1IgstImpct = BigDecimal.ZERO;
		}
		if (docRet1CgstImpct == null) {
			docRet1CgstImpct = BigDecimal.ZERO;
		}
		if (docRet1SgstImpct == null) {
			docRet1SgstImpct = BigDecimal.ZERO;
		}
		BigDecimal docIgstMemoVal = document.getMemoValIgst();
		BigDecimal docCgstMemoVal = document.getMemoValCgst();
		BigDecimal docSgstMemoVal = document.getMemoValSgst();
		if (docIgstMemoVal == null) {
			docIgstMemoVal = BigDecimal.ZERO;
		}
		if (docCgstMemoVal == null) {
			docCgstMemoVal = BigDecimal.ZERO;
		}
		if (docSgstMemoVal == null) {
			docSgstMemoVal = BigDecimal.ZERO;
		}
		String tableSection = document.getTableTypeNew();
		// Start - Calculate Memo IGST, CGST, SGST Values
		// Memo Values to be calculated only for section B2B (3B), EXPT (3C &
		// 3D), SEZ (3E), SEZ (3F), Deemed Exports (3G)
		// for Doc type = INV / RNV
		if ((BifurcationConstants.SECTION_3B.equalsIgnoreCase(tableSection)
				|| BifurcationConstants.SECTION_3C
						.equalsIgnoreCase(tableSection)
				|| BifurcationConstants.SECTION_3D
						.equalsIgnoreCase(tableSection)
				|| BifurcationConstants.SECTION_3E
						.equalsIgnoreCase(tableSection)
				|| BifurcationConstants.SECTION_3F
						.equalsIgnoreCase(tableSection)
				|| BifurcationConstants.SECTION_3G
						.equalsIgnoreCase(tableSection))
				&& (GSTConstants.INV.equalsIgnoreCase(document.getDocType())
						|| GSTConstants.RNV
								.equalsIgnoreCase(document.getDocType()))) {
			BigDecimal hundred = new BigDecimal(GSTConstants.BIGDECIMAL_100);
			BigDecimal igstPercentageFactor = BigDecimal.ZERO;
			BigDecimal cgstPercentageFactor = BigDecimal.ZERO;
			BigDecimal sgstPercentageFactor = BigDecimal.ZERO;
			if (item.getIgstRate() != null
					&& !item.getIgstRate().equals(BigDecimal.ZERO)) {
				igstPercentageFactor = item.getIgstRate().divide(hundred);
			}
			if (item.getCgstRate() != null
					&& !item.getCgstRate().equals(BigDecimal.ZERO)) {
				cgstPercentageFactor = item.getCgstRate().divide(hundred);
			}
			if (item.getSgstRate() != null
					&& !item.getSgstRate().equals(BigDecimal.ZERO)) {
				sgstPercentageFactor = item.getSgstRate().divide(hundred);
			}
			if (GSTConstants.DIFF_PERCENT_L65
					.equalsIgnoreCase(document.getDiffPercent())) {
				if (item.getTaxableValue() != null) {
					BigDecimal itmIgstMemoVal = item.getTaxableValue()
							.multiply(igstPercentageFactor)
							.multiply(new BigDecimal(
									GSTConstants.DIFF_PERCENT_L65_VAL));
					itmIgstMemoValRounded = itmIgstMemoVal.setScale(2,
							BigDecimal.ROUND_HALF_EVEN);
					BigDecimal itmCgstMemoVal = item.getTaxableValue()
							.multiply(cgstPercentageFactor)
							.multiply(new BigDecimal(
									GSTConstants.DIFF_PERCENT_L65_VAL));
					itmCgstMemoValRounded = itmCgstMemoVal.setScale(2,
							BigDecimal.ROUND_HALF_EVEN);
					BigDecimal itmSgstMemoVal = item.getTaxableValue()
							.multiply(sgstPercentageFactor)
							.multiply(new BigDecimal(
									GSTConstants.DIFF_PERCENT_L65_VAL));
					itmSgstMemoValRounded = itmSgstMemoVal.setScale(2,
							BigDecimal.ROUND_HALF_EVEN);
				}
			} else {
				if (item.getTaxableValue() != null) {
					BigDecimal itmIgstMemoVal = item.getTaxableValue()
							.multiply(igstPercentageFactor);
					itmIgstMemoValRounded = itmIgstMemoVal.setScale(2,
							BigDecimal.ROUND_HALF_EVEN);
					BigDecimal itmCgstMemoVal = item.getTaxableValue()
							.multiply(cgstPercentageFactor);
					itmCgstMemoValRounded = itmCgstMemoVal.setScale(2,
							BigDecimal.ROUND_HALF_EVEN);
					BigDecimal itmSgstMemoVal = item.getTaxableValue()
							.multiply(sgstPercentageFactor);
					itmSgstMemoValRounded = itmSgstMemoVal.setScale(2,
							BigDecimal.ROUND_HALF_EVEN);
				}
			}
			item.setMemoValIgst(itmIgstMemoValRounded.setScale(2,
					BigDecimal.ROUND_HALF_EVEN));
			docIgstMemoVal = docIgstMemoVal.add(itmIgstMemoValRounded);
			document.setMemoValIgst(
					docIgstMemoVal.setScale(2, BigDecimal.ROUND_HALF_EVEN));
			item.setMemoValCgst(itmCgstMemoValRounded.setScale(2,
					BigDecimal.ROUND_HALF_EVEN));
			docCgstMemoVal = docCgstMemoVal.add(itmCgstMemoValRounded);
			document.setMemoValCgst(
					docCgstMemoVal.setScale(2, BigDecimal.ROUND_HALF_EVEN));
			item.setMemoValSgst(itmSgstMemoValRounded.setScale(2,
					BigDecimal.ROUND_HALF_EVEN));
			docSgstMemoVal = docSgstMemoVal.add(itmSgstMemoValRounded);
			document.setMemoValSgst(
					docSgstMemoVal.setScale(2, BigDecimal.ROUND_HALF_EVEN));
			// Calculate Item RET-1 Impact
			BigDecimal itemIgstAmt = item.getIgstAmount();
			BigDecimal itemMemoValIgst = item.getMemoValIgst();
			BigDecimal itemCgstAmt = item.getCgstAmount();
			BigDecimal itemMemoValCgst = item.getMemoValCgst();
			BigDecimal itemSgstAmt = item.getSgstAmount();
			BigDecimal itemMemoValSgst = item.getMemoValSgst();
			if (itemIgstAmt == null) {
				itemIgstAmt = BigDecimal.ZERO;
			}
			if (itemMemoValIgst == null) {
				itemMemoValIgst = BigDecimal.ZERO;
			}
			if (itemCgstAmt == null) {
				itemCgstAmt = BigDecimal.ZERO;
			}
			if (itemMemoValCgst == null) {
				itemMemoValCgst = BigDecimal.ZERO;
			}
			if (itemSgstAmt == null) {
				itemSgstAmt = BigDecimal.ZERO;
			}
			if (itemMemoValSgst == null) {
				itemMemoValSgst = BigDecimal.ZERO;
			}
			itmRet1IgstImpct = itemIgstAmt.subtract(itemMemoValIgst);
			itmRet1CgstImpct = itemCgstAmt.subtract(itemMemoValCgst);
			itmRet1SgstImpct = itemSgstAmt.subtract(itemMemoValSgst);
		} else {// For remaining sections / Doc Types, values to be shown as
				// is
			if (item.getIgstAmount() != null) {
				itmIgstMemoValRounded = item.getIgstAmount().setScale(2,
						BigDecimal.ROUND_HALF_EVEN);
			}
			item.setMemoValIgst(itmIgstMemoValRounded);
			docIgstMemoVal = docIgstMemoVal.add(itmIgstMemoValRounded);
			if (item.getCgstAmount() != null) {
				itmCgstMemoValRounded = item.getCgstAmount().setScale(2,
						BigDecimal.ROUND_HALF_EVEN);
			}
			item.setMemoValCgst(itmCgstMemoValRounded);
			docCgstMemoVal = docCgstMemoVal.add(itmCgstMemoValRounded);
			if (item.getSgstAmount() != null) {
				itmSgstMemoValRounded = item.getSgstAmount().setScale(2,
						BigDecimal.ROUND_HALF_EVEN);
			}
			item.setMemoValSgst(itmSgstMemoValRounded);
			docSgstMemoVal = docSgstMemoVal.add(itmSgstMemoValRounded);
			document.setMemoValIgst(
					docIgstMemoVal.setScale(2, BigDecimal.ROUND_HALF_EVEN));
			document.setMemoValCgst(
					docCgstMemoVal.setScale(2, BigDecimal.ROUND_HALF_EVEN));
			document.setMemoValSgst(
					docSgstMemoVal.setScale(2, BigDecimal.ROUND_HALF_EVEN));
		}
		// Set Item RET-1 Impact Calculation
		// Only Positive difference would be considered for RET-1 computation
		// That means, Only positive difference values will be persisted in DB,
		// in case of negative difference, '0' will be persisted.
		BigDecimal itmRet1IgstImpact = itmRet1IgstImpct.setScale(2,
				BigDecimal.ROUND_HALF_EVEN);
		if (itmRet1IgstImpact.compareTo(BigDecimal.ZERO) > 0) {
			item.setRet1IgstImpact(itmRet1IgstImpact);
		} else {
			item.setRet1IgstImpact(BigDecimal.ZERO);
		}
		BigDecimal itmRet1CgstImpact = itmRet1CgstImpct.setScale(2,
				BigDecimal.ROUND_HALF_EVEN);
		if (itmRet1CgstImpact.compareTo(BigDecimal.ZERO) > 0) {
			item.setRet1CgstImpact(itmRet1CgstImpact);
		} else {
			item.setRet1CgstImpact(BigDecimal.ZERO);
		}
		BigDecimal itmRet1SgstImpact = itmRet1SgstImpct.setScale(2,
				BigDecimal.ROUND_HALF_EVEN);
		if (itmRet1SgstImpact.compareTo(BigDecimal.ZERO) > 0) {
			item.setRet1SgstImpact(itmRet1SgstImpact);
		} else {
			item.setRet1SgstImpact(BigDecimal.ZERO);
		}
		BigDecimal docIgstAmt = document.getIgstAmount();
		BigDecimal docCgstAmt = document.getCgstAmount();
		BigDecimal docSgstAmt = document.getSgstAmount();
		// Calculate and Set Document RET-1 Impact
		if (docIgstAmt == null) {
			docIgstAmt = BigDecimal.ZERO;
		}
		if (docCgstAmt == null) {
			docCgstAmt = BigDecimal.ZERO;
		}
		if (docSgstAmt == null) {
			docSgstAmt = BigDecimal.ZERO;
		}
		BigDecimal docRet1IgstImpact = (docIgstAmt
				.subtract(document.getMemoValIgst())).setScale(2,
						BigDecimal.ROUND_HALF_EVEN);
		BigDecimal docRet1CgstImpact = (docCgstAmt
				.subtract(document.getMemoValCgst())).setScale(2,
						BigDecimal.ROUND_HALF_EVEN);
		BigDecimal docRet1SgstImpact = (docSgstAmt
				.subtract(document.getMemoValSgst())).setScale(2,
						BigDecimal.ROUND_HALF_EVEN);
		if (docRet1IgstImpct.compareTo(BigDecimal.ZERO) > 0) {
			document.setRet1IgstImpact(docRet1IgstImpact);
		} else {
			document.setRet1IgstImpact(BigDecimal.ZERO);
		}
		if (docRet1CgstImpct.compareTo(BigDecimal.ZERO) > 0) {
			document.setRet1CgstImpact(docRet1CgstImpact);
		} else {
			document.setRet1CgstImpact(BigDecimal.ZERO);
		}
		if (docRet1SgstImpact.compareTo(BigDecimal.ZERO) > 0) {
			document.setRet1SgstImpact(docRet1SgstImpact);
		} else {
			document.setRet1SgstImpact(BigDecimal.ZERO);
		}
		// End - Calculate Memo IGST, CGST, SGST Values

		// Start - CESS Memo Val - set CESS Val to CESS Memo Val
		BigDecimal docCessMemoVal = document.getMemoValCess();
		BigDecimal itemcessAmtAdvalorem = item.getCessAmountAdvalorem();
		BigDecimal itemCessAmtSpecific = item.getCessAmountSpecific();
		if (docCessMemoVal == null) {
			docCessMemoVal = BigDecimal.ZERO;
		}
		if (itemcessAmtAdvalorem == null) {
			itemcessAmtAdvalorem = BigDecimal.ZERO;
		}
		if (itemCessAmtSpecific == null) {
			itemCessAmtSpecific = BigDecimal.ZERO;
		}
		BigDecimal itmCessAmt = itemcessAmtAdvalorem.add(itemCessAmtSpecific);
		BigDecimal itmCessAmtRounded = itmCessAmt.setScale(2,
				BigDecimal.ROUND_HALF_EVEN);
		item.setMemoValCess(itmCessAmtRounded);
		docCessMemoVal = docCessMemoVal.add(itmCessAmtRounded);
		document.setMemoValCess(
				docCessMemoVal.setScale(2, BigDecimal.ROUND_HALF_EVEN));
		// End - CESS Memo Val - set CESS Val to CESS Memo Val
	}
}
