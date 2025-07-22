
package com.ey.advisory.einv.dto;

import java.io.Serializable;
import java.time.LocalDate;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class DocumentDetails implements Serializable
{
	
	  private static final  long serialVersionUID = -7444275667892684903L;
    /**
     * Document Type: INV-INVOICE, CRN-CREDIT NOTE, DBN-DEBIT NOTE
     * (Required)
     * 
     */
    @SerializedName("Typ")
    @Expose
    private String typ;
    /**
     * Document Number
     * (Required)
     * 
     */
    @SerializedName("No")
    @Expose
    private String no;
    /**
     * Document Date
     * (Required)
     * 
     */
    @SerializedName("Dt")
    @Expose
    private LocalDate dt;
    
    public static boolean isEmpty(DocumentDetails docDetails) {
    	DocumentDetails dcDetails = new DocumentDetails();
    		return dcDetails.hashCode() == docDetails.hashCode();
    }


}
