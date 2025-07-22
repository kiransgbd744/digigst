
package com.ey.advisory.einv.dto;

import java.io.Serializable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class ReferenceDetails implements Serializable
{	
	 private static final long serialVersionUID = 3486622398993353040L;

	 /**
	 * Remarks/Note
	 *
	 */
	 @SerializedName("InvRm")
	 @Expose
	 public String invRm;
	 @SerializedName("DocPerdDtls")
	 @Expose
	 public DocPerdDtls docPerdDtls;
	 @SerializedName("PrecDocDtls")
	 @Expose
	 public PrecDocument precDocDtls;
	 @SerializedName("ContrDtls")
	 @Expose
	 public Contract contrDtls;
    
	public static boolean isEmpty(ReferenceDetails refDtls) {
		ReferenceDetails refDetails = new ReferenceDetails();
		return refDetails.hashCode() == refDtls.hashCode();
	}
   

}
