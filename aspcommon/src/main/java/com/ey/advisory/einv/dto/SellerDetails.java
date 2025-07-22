
package com.ey.advisory.einv.dto;

import java.io.Serializable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class SellerDetails implements Serializable
{
	
	 private static final  long serialVersionUID = -3226442168682295911L;
    /**
     * GSTIN
     * (Required)
     * 
     */
    @SerializedName("Gstin")
    @Expose
    private String gstin;
    /**
     * Tradename
     * (Required)
     * 
     */
    @SerializedName("TrdNm")
    @Expose
    private String trdNm;
    
    /**
     * LegalName
     * (Required)
     * 
     */
    @SerializedName("LglNm")
    @Expose
    private String lglNm;

    /**
     * Building no.
     * 
     */
    @SerializedName("Addr1")
    @Expose
    private String addr1;
    /**
     * Building name
     * 
     */
    @SerializedName("Addr2")
    @Expose
    private String addr2;
 
    /**
     * Location
     * (Required)
     * 
     */
    @SerializedName("Loc")
    @Expose
    private String loc;
      /**
     * Pincode
     * (Required)
     * 
     */
    @SerializedName("Pin")
    @Expose
    private Integer pin;
    /**
     * State name
     * (Required)
     * 
     */
    @SerializedName("Stcd")
    @Expose
    private String state;
    /**
     * Phone or Mobile No.
     * 
     */
    @SerializedName("Ph")
    @Expose
    private String ph;
    /**
     * Email-Id
     * 
     */
    @SerializedName("Em")
    @Expose
    private String em;

    
    public static boolean isEmpty(SellerDetails seller){
    	SellerDetails seller1 = new  SellerDetails();
    	return seller.hashCode() == seller1.hashCode();
    }
}
