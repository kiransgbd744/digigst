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
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Khalid1.Khan
 *
 */

@ToString
@Getter
@Setter
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ConsolidateEWBResponseDto implements Serializable
{

@SerializedName("cEwbNo")
@Expose
@XmlElement(name = "c-ewb-no")
private String cEwbNo;

@SerializedName("cEwbDate")
@XmlJavaTypeAdapter(value = EWBLocalDateTimeAdapter.class)
@Expose
@XmlElement(name = "c-ewb-date")
private LocalDateTime cEWBDate;


@SerializedName("errorCode")
@Expose
@XmlElement(name = "error-code")
private String errorCode;

@SerializedName("errorMessage")
@Expose
@XmlElement(name = "error-message")
private String errorDesc;
private final static long serialVersionUID = 2686215335004551050L;
}

