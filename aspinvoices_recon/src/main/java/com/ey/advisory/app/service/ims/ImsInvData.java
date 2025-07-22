/**
 * 
 */
package com.ey.advisory.app.service.ims;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author vishal.verma
 *
 */

@Data
public class ImsInvData {
	
	@Expose
    @SerializedName("b2b")
    private List<ImsB2bInvoice> b2b;

    @Expose
    @SerializedName("b2ba")
    private List<ImsB2baInvoice> b2ba;

    @Expose
    @SerializedName("b2bdn")
    private List<ImsDnInvoice> dn;

    @Expose
    @SerializedName("b2bdna")
    private List<ImsDnaInvoice> dna;

    @Expose
    @SerializedName("b2bcn")
    private List<ImsCnInvoice> cn;

    @Expose
    @SerializedName("b2bcna")
    private List<ImsCnaInvoice> cna;

    @Expose
    @SerializedName("ecom")
    private List<ImsEcomInvoice> ecom;

    @Expose
    @SerializedName("ecoma")
    private List<ImsEcomaInvoice> ecoma;



}
