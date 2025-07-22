
package com.ey.advisory.ewb.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.ey.advisory.common.EWBLocalDateTimeAdapter;
import com.ey.advisory.common.GetEwbLocalDateAdapter;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class VehiclListDetail implements Serializable
{

    @SerializedName("updMode")
    @Expose
    @XmlElement(name = "upd-mode")
    private String updMode;
    @SerializedName("vehicleNo")
    @Expose
    @XmlElement(name = "veh-no")
    private String vehicleNo;
    @SerializedName("fromPlace")
    @Expose
    @XmlElement(name = "from-place")
    private String fromPlace;
    @SerializedName("fromState")
    @Expose
    @XmlElement(name = "from-state")
    private String fromState;
    @SerializedName("tripshtNo")
    @Expose
    @XmlElement(name = "trip-sht-no")
    private String tripshtNo;
    @SerializedName("userGSTINTransin")
    @Expose
    @XmlElement(name = "usr-gstin-tran")
    private String userGSTINTransin;
    @SerializedName("enteredDate")
    @Expose
    @XmlElement(name = "entred-date")
	@XmlJavaTypeAdapter(value = EWBLocalDateTimeAdapter.class)
    private LocalDateTime enteredDate;
    @SerializedName("transMode")
    @Expose
    @XmlElement(name = "trans-mode")
    private String transMode;
    @SerializedName("transDocNo")
    @Expose
    @XmlElement(name = "trns-doc-no")
    private String transDocNo;
    @SerializedName("transDocDate")
    @Expose
    @XmlElement(name = "trns-doc-date")
	@XmlJavaTypeAdapter(value = GetEwbLocalDateAdapter.class)
    private LocalDate transDocDate;
    @SerializedName("groupNo")
    @Expose
    @XmlElement(name = "grp-no")
    private String groupNo;
    @SerializedName("vehicleType")
    @Expose
    @XmlElement(name = "veh-type")
    private String vehicleType;
    private static final long serialVersionUID = 4154284943049225897L;

}
