
package com.ey.advisory.einv.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class PayeeDetails implements Serializable
{
	
	 private static final long serialVersionUID = -2667251528844172099L;

	 /**
	 * Payee Name
	 *
	 */
	 @SerializedName("Nm")
	 @Expose
	 public String nm;
	 /**
	 * Bank account number of payee
	 *
	 */
	 @SerializedName("AccDet")
	 @Expose
	 public String accDet;
	 /**
	 * Mode of Payment: Cash, Credit, Direct Transfer
	 *
	 */
	 @SerializedName("Mode")
	 @Expose
	 public String mode;
	 /**
	 * Branch or IFSC code
	 *
	 */
	 @SerializedName("FinInsBr")
	 @Expose
	 public String finInsBr;
	 /**
	 * Terms of Payment
	 *
	 */
	 @SerializedName("PayTerm")
	 @Expose
	 public String payTerm;
	 /**
	 * Payment Instruction
	 *
	 */
	 @SerializedName("PayInstr")
	 @Expose
	 public String payInstr;
	 /**
	 * Credit Transfer
	 *
	 */
	 @SerializedName("CrTrn")
	 @Expose
	 public String crTrn;
	 /**
	 * Direct Debit
	 *
	 */
	 @SerializedName("DirDr")
	 @Expose
	 public String dirDr;
	 /**
	 * Credit Days
	 *
	 */
	 @SerializedName("CrDay")
	 @Expose
	 public Integer crDay;
	 /**
	 * The sum of amount which have been paid in advance.
	 *
	 */
	 @SerializedName("PaidAmt")
	 @Expose
	 public BigDecimal paidAmt;
	 /**
	 * Outstanding amount that is required to be paid.
	 *
	 */
	 @SerializedName("PaymtDue")
	 @Expose
	 public BigDecimal paymtDue;
    
    public static boolean isEmpty (PayeeDetails pDeatails) {
    	PayeeDetails payeeDtls = new PayeeDetails();
    	return pDeatails.hashCode() == payeeDtls.hashCode();
    }

}
