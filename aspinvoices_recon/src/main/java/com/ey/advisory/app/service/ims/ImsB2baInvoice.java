/**
 * 
 */
package com.ey.advisory.app.service.ims;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author vishal.verma
 *
 */
@Data
public class ImsB2baInvoice {
	
	@Expose(serialize = false, deserialize = false)
	@SerializedName("psdDocId")
    private Long psdDocId;
	
	@Expose
    @SerializedName("stin")
    private String stin;

    @Expose
    @SerializedName("inum")
    private String inum;

    @Expose
    @SerializedName("inv_typ")
    private String inv_typ;

    @Expose
    @SerializedName("action")
    private String action;

    @Expose
    @SerializedName("srcform")
    private String srcform;

    @Expose
    @SerializedName("rtnprd")
    private String rtnprd;

    @Expose
    @SerializedName("idt")
    private String idt;

    @Expose
    @SerializedName("val")
    private BigDecimal val;

    @Expose
    @SerializedName("txval")
    private BigDecimal txval;

    @Expose
    @SerializedName("iamt")
    private BigDecimal iamt;

    @Expose
    @SerializedName("camt")
    private BigDecimal camt;

    @Expose
    @SerializedName("samt")
    private BigDecimal samt;

    @Expose
    @SerializedName("cess")
    private BigDecimal cess;

    @Expose
    @SerializedName("pos")
    private String pos;

    @Expose
    @SerializedName("prev_status")
    private String prev_status;

    //extra
    @Expose
    @SerializedName("oinum")
    private String oinum;

    @Expose
    @SerializedName("oidt")
    private String oidt;
    
    @Expose
    @SerializedName("remarks")
    private String remarks;
    
    @Expose
    @SerializedName("itcRedReq")
    private String itcRedReq;
    
    @Expose
    @SerializedName("declIgst")
    private BigDecimal declIgst;
    
    @Expose
    @SerializedName("declCgst")
    private BigDecimal declCgst;
    
    @Expose
    @SerializedName("declSgst")
    private BigDecimal declSgst;
    
    @Expose
    @SerializedName("declCess")
    private BigDecimal declCess;


    // Getter and Setter for psdDocId
    public Long getPsdDocId() {
        return psdDocId;
    }

    public void setPsdDocId(Long psdDocId) {
        this.psdDocId = psdDocId;
    }

}
