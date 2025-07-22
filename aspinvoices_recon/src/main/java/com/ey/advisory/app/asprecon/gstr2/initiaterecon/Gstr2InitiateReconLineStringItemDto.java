/**
 * 
 */
package com.ey.advisory.app.asprecon.gstr2.initiaterecon;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Sakshi.jain
 *
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Gstr2InitiateReconLineStringItemDto {
	
	private String perticulas;

	private String perticulasName;

	private String prCount;

	private String prPercenatge;

	private String prTaxableValue;

	private String prTotalTax;
	
	private String prIgst;
	
	private String prCgst;
	
	private String prSgst;
	
	private String prCess;

	private String a2Count;

	private String a2Percenatge;

	private String a2TaxableValue;

	private String a2TotalTax;
	
	private String a2Igst;
	
	private String a2Cgst;
	
	private String a2Sgst;
	
	private String a2Cess;

	private String level;

	private String orderPosition;

	
}
