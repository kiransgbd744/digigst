/**
 * 
 */
package com.ey.advisory.ewb.dto;
import java.io.Serializable;
import java.time.LocalDateTime;

import com.ey.advisory.common.EWBLocalDateTimeAdapter;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import lombok.Data;
import lombok.ToString;

/**
 * @author Khalid1.Khan
 *
 */
@Data
@ToString
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ExtendEWBResponseDto implements Serializable
{

@SerializedName("ewayBillNo")
@Expose
@XmlElement(name = "ewb-no")
private String ewayBillNo;
@SerializedName("updatedDate")
@XmlJavaTypeAdapter(value = EWBLocalDateTimeAdapter.class)
@Expose
@XmlElement(name = "updated-date")
private LocalDateTime updatedDate;

@SerializedName("validUpto")
@XmlJavaTypeAdapter(value = EWBLocalDateTimeAdapter.class)
@Expose
@XmlElement(name = "valid-upto")
private LocalDateTime validUpto;

@SerializedName("errorCode")
@Expose
@XmlElement(name = "error-code")
private String errorCode;

@SerializedName("errorMessage")
@Expose
@XmlElement(name = "error-msg")
private String errorDesc;

@SerializedName("nicDistance")
@Expose
@XmlElement(name = "nic-dist")
private Integer nicDistance;
private final static long serialVersionUID = -6452744964841446061L;

}
