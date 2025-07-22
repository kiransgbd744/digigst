/**
 * 
 */
package com.ey.advisory.ewb.dto;

/**
 * @author Khalid1.Khan
 *
 */
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

import com.ey.advisory.common.EwbLocalDateAdapter;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import lombok.Data;

@Data
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ConsolidateEWBReqDto implements Serializable
{
	
@XmlElement(name = "id-token")
private String idToken;
	
@SerializedName("gstin")
@Expose
private String gstin;

@Expose
@SerializedName("docHeaderId")
@XmlTransient
private String docHeaderId;

/**
* From Place
* (Required)
*
*/
@SerializedName("fromPlace")
@Expose
@XmlElement(name = "from-place")
private String fromPlace;
/**
* From State
* (Required)
*
*/
@SerializedName("fromState")
@Expose
@XmlElement(name = "from-state")
private String fromState;
/**
* Vehicle Number
*
*/
@SerializedName("vehicleNo")
@Expose
@XmlElement(name = "veh-no")
private String vehicleNo;
/**
* Transport Mode (Road-1,Rail-2,Air-3,Ship-4)
* (Required)
*
*/
@SerializedName("transMode")
@Expose
@XmlElement(name = "trans-mode")
private String transMode;
/**
* Transport Document Number
*
*/
@SerializedName("transDocNo")
@Expose
@XmlElement(name = "trans-doc-no")
private String transDocNo;
/**
* Transport Document Date
*
*/
@SerializedName("transDocDate")
@XmlJavaTypeAdapter(value = EwbLocalDateAdapter.class)
@Expose
@XmlElement(name = "trans-doc-date")
private LocalDate transDocDate;
/**
*
* (Required)
*
*/

@SerializedName("id")
@Expose
@XmlElement(name = "id")
private Long id;

@SerializedName("fileId")
@Expose
@XmlElement(name = "fileId")
private Long fileId;

@SerializedName("serialNo")
@Expose
@XmlElement(name = "serialNo")
private Long serialNo;

@SerializedName("tripSheetEwbBills")
@Expose
@XmlElement(name = "trp-sht-ewbs")
private List<TripSheetEwbBills> tripSheetEwbBills = null;
private final static long serialVersionUID = 6215389552252190499L;

}
