/**
 * 
 */
package com.ey.advisory.einv.dto;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Khalid1.Khan
 *
 */
@Data
@EqualsAndHashCode
public class RefDtls implements Serializable {

	private static final long serialVersionUID = 1L;
	/**
	 * Remarks/Note
	 *
	 */
	@SerializedName("InvRm")
	@Expose
	private String invRm;
	@SerializedName("PrecDocDtls")
	@Expose
	private List<PrecDocument> precDocDtls;
	
	@SerializedName("DocPerdDtls")
	@Expose
	private DocPerdDtls docPerdDtls;
	@SerializedName("ContrDtls")
	@Expose
	private List<Contract> contrDtls;
	
	public static boolean isEmpty(RefDtls refDtls) {
		RefDtls refDetails = new RefDtls();
		return refDetails.hashCode() == refDtls.hashCode();
	}

}