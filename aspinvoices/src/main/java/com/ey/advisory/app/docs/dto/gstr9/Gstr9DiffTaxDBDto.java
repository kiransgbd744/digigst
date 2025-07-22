/**
 * 
 */
package com.ey.advisory.app.docs.dto.gstr9;

import java.math.BigDecimal;

import lombok.Data;

/**
 * @author vishal.verma
 *
 */

@Data
public class Gstr9DiffTaxDBDto {
	
 private BigDecimal igstPayable = BigDecimal.ZERO;
 
 private BigDecimal cgstPayable = BigDecimal.ZERO;
	
 private BigDecimal sgstPayable = BigDecimal.ZERO;
 
 private BigDecimal cessPayable = BigDecimal.ZERO;
 
 private BigDecimal interestPayable = BigDecimal.ZERO;

 private BigDecimal igstPaid = BigDecimal.ZERO;
 
 private BigDecimal cgstPaid = BigDecimal.ZERO;
	
 private BigDecimal sgstPaid = BigDecimal.ZERO;
 
 private BigDecimal cessPaid = BigDecimal.ZERO;
 
 private BigDecimal interestPaid = BigDecimal.ZERO;

}
