/**
 * 
 */
package com.ey.advisory.einv.dto;

import java.time.LocalDate;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Khalid.Khan
 *
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class DocPerdDtls {
	
	/**
     * Invoice Period Start Date
     * (Required)
     * 
     */
    @SerializedName("InvStDt")
    @Expose
    public LocalDate invStDt;
    /**
     * Invoice Period End Date
     * (Required)
     * 
     */
    @SerializedName("InvEndDt")
    @Expose
    public LocalDate invEndDt;
    
    public static boolean isEmpty(DocPerdDtls docPerdDtls) {
    	DocPerdDtls perdDetails = new DocPerdDtls();
		return perdDetails.hashCode() == docPerdDtls.hashCode();
	}

}
