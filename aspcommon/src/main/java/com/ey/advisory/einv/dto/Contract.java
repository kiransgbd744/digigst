/**
 * 
 */
package com.ey.advisory.einv.dto;

import java.io.Serializable;
import java.time.LocalDate;

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
public class Contract implements Serializable{
	
	private static final long serialVersionUID = 1L;
/**
* Receipt Advice No.
*
*/
@SerializedName("RecAdvRefr")
@Expose
private String recAdvRefr;
/**
* Date of receipt advice
*
*/
@SerializedName("RecAdvDt")
@Expose
private LocalDate recAdvDt;
/**
* Lot/Batch Reference No.
*
*/
@SerializedName("Tendrefr")
@Expose
private String tendRefr;
/**
* Contract Reference Number
*
*/
@SerializedName("Contrrefr")
@Expose
private String contrRefr;
/**
* Any other reference
*
*/
@SerializedName("Extrefr")
@Expose
private String extRefr;
/**
* Project Reference Number
*
*/
@SerializedName("Projrefr")
@Expose
private String projRefr;
/**
* Vendor PO Reference Number
*
*/
@SerializedName("Porefr")
@Expose
private String pORefr;
/**
* Vendor PO Reference date
*
*/
@SerializedName("PorefDt")
@Expose
private LocalDate pORefDt;



//new serialised name for irn dtl


@SerializedName("TendRefr")
@Expose
private String tendRef;
/**
* Contract Reference Number
*
*/
@SerializedName("ContrRefr")
@Expose
private String contrRef;
/**
* Any other reference
*
*/
@SerializedName("ExtRefr")
@Expose
private String extRef;
/**
* Project Reference Number
*
*/
@SerializedName("ProjRefr")
@Expose
private String projRef;
/**
* Vendor PO Reference Number
*
*/
@SerializedName("PORefr")
@Expose
private String poRefr;
/**
* Vendor PO Reference date
*
*/
@SerializedName("PORefDt")
@Expose
private LocalDate poRefDt;

public static boolean isEmpty(Contract attrDetails) {
	Contract attrDtls = new Contract();
	return attrDetails.hashCode() == attrDtls.hashCode();
}

}
